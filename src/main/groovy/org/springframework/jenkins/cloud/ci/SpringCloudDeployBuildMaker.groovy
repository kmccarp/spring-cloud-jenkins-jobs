package org.springframework.jenkins.cloud.ci

import javaposse.jobdsl.dsl.DslFactory

import org.springframework.jenkins.cloud.common.AllCloudJobs
import org.springframework.jenkins.cloud.common.CloudCron
import org.springframework.jenkins.cloud.common.SpringCloudJobs
import org.springframework.jenkins.cloud.common.SpringCloudNotification
import org.springframework.jenkins.common.job.JdkConfig
import org.springframework.jenkins.common.job.Maven
import org.springframework.jenkins.common.job.Slack
import org.springframework.jenkins.common.job.TestPublisher

/**
 * @author Marcin Grzejszczak
 */
class SpringCloudDeployBuildMaker implements JdkConfig, TestPublisher, CloudCron,
		SpringCloudJobs, Maven {
	private final DslFactory dsl
	final String organization
	final String prefix
	boolean upload = true
	String jdkVersion = jdk17()

	Closure<Node> slack = { Node node -> SpringCloudNotification.cloudSlack(node) }

	SpringCloudDeployBuildMaker(DslFactory dsl) {
		this(dsl, 'spring-cloud', '')
	}

	SpringCloudDeployBuildMaker(DslFactory dsl, String organization) {
		this(dsl, organization, '')
	}

	SpringCloudDeployBuildMaker(DslFactory dsl, String organization, String prefix) {
		this.dsl = dsl
		this.organization = organization ?: 'spring-cloud'
		this.prefix = prefix ?: ''
	}

	void deploy(String project, boolean checkTests = true) {
		deploy(project, mainBranch(), checkTests)
	}

	private String prefix(String project) {
		if (this.prefix) {
			// spring-observability prefix & repo name
			if (project == this.prefix) {
				return ""
			}
			return this.prefix.endsWith("-") ? this.prefix : this.prefix + "-"
		}
		return project.startsWith("spring-cloud-") ? "" : "spring-cloud-"
	}

	void deploy(String project, String branchToBuild, boolean checkTests = true) {
		String projectNameWithBranch = branchToBuild ? "$branchToBuild-" : ''
		String prefixedName = prefixedName(project)
		dsl.job("${prefixedName}-${projectNameWithBranch}ci") {
			triggers {
				cron cronValue
				if (onGithubPush) {
					githubPush()
				}
			}
			parameters {
				stringParam(branchVarName(), branchToBuild ?: mainBranch(), 'Which branch should be built')
			}
			jdk branchToBuild != mainBranch() ? jdk8() : jdkVersion
			scm {
				git {
					remote {
						url "https://github.com/${organization}/${project}"
					}
					branch "\$${branchVarName()}"
					extensions {
						wipeOutWorkspace()
						localBranch("**")
					}
				}
			}
			wrappers {
				timestamps()
				colorizeOutput()
				maskPasswords()
				credentialsBinding {
					usernamePassword(dockerhubUserNameEnvVar(),
							dockerhubPasswordEnvVar(),
							dockerhubCredentialId())
					usernamePassword(githubRepoUserNameEnvVar(),
							githubRepoPasswordEnvVar(),
							githubUserCredentialId())
					string(githubToken(), githubTokenCredId())
				}
				timeout {
					noActivity(600)
					failBuild()
					writeDescription('Build failed due to timeout after {0} minutes of inactivity')
				}
			}
			steps {
				shell(loginToDocker())
				shell(removeMavenInstallation())
				shell(stopRunningDocker())
				maven {
					mavenInstallation(maven33())
					goals('--version')
				}
				shell(buildCommand())
			}
			configure {
				slack.call(it as Node)
			}
			if (checkTests) {
				publishers {
					archiveJunit mavenJUnitResults()
				}
			}
		}
	}

	String prefixedName(String project) {
		return prefix(project) + project
	}

	String buildCommand() {
		return this.upload ? cleanDeployWithDocs() : cleanInstallWithoutDocs()
	}

	void deployWithoutTests(String project) {
		deploy(project, false)
	}
}
