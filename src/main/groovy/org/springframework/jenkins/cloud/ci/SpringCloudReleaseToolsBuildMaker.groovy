package org.springframework.jenkins.cloud.ci

import javaposse.jobdsl.dsl.DslFactory

import org.springframework.jenkins.cloud.common.SpringCloudJobs
import org.springframework.jenkins.cloud.common.SpringCloudNotification
import org.springframework.jenkins.common.job.Cron
import org.springframework.jenkins.common.job.JdkConfig
import org.springframework.jenkins.common.job.Maven
import org.springframework.jenkins.common.job.TestPublisher

/**
 * @author Marcin Grzejszczak
 */
class SpringCloudReleaseToolsBuildMaker implements JdkConfig, TestPublisher, Cron,
		SpringCloudJobs, Maven {
	private final DslFactory dsl
	final String organization
	final String project = "spring-cloud-release-tools"

	SpringCloudReleaseToolsBuildMaker(DslFactory dsl) {
		this.dsl = dsl
		this.organization = 'spring-cloud'
	}

	void deploy(boolean checkTests = true) {
		deploy(mainBranch(), checkTests)
	}

	void deploy(String branchToBuild, boolean checkTests = true) {
		String projectNameWithBranch = branchToBuild ? "$branchToBuild-" : ''
		dsl.job("$project-${projectNameWithBranch}ci") {
			triggers {
				githubPush()
			}
			parameters {
				stringParam(branchVarName(), branchToBuild ?: mainBranch(), 'Which branch should be built')
			}
			jdk jdk17()
			scm {
				git {
					remote {
						url "https://github.com/${organization}/${project}"
						branch "\$${branchVarName()}"
					}
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
					usernamePassword(githubRepoUserNameEnvVar(),
							githubRepoPasswordEnvVar(),
							githubUserCredentialId())
					usernamePassword(buildUserNameEnvVar(),
							buildPasswordEnvVar(),
							buildCredentialId())
					string(githubToken(), githubTokenCredId())
				}
				timeout {
					noActivity(600)
					failBuild()
					writeDescription('Build failed due to timeout after {0} minutes of inactivity')
				}
			}
			steps {
				shell(removeMavenInstallation())
				shell(stopRunningDocker())
				maven {
					mavenInstallation(maven33())
					goals('--version')
				}
				shell(cleanDeployWithDocs())
			}
			configure {
				SpringCloudNotification.cloudSlack(it as Node)
			}
			if (checkTests) {
				publishers {
					archiveJunit mavenJUnitResults()
				}
			}
		}
	}
}
