package org.springframework.jenkins.cloud.e2e

import javaposse.jobdsl.dsl.DslFactory

import org.springframework.jenkins.cloud.common.SpringCloudJobs
import org.springframework.jenkins.cloud.common.SpringCloudNotification
import org.springframework.jenkins.common.job.Cron
import org.springframework.jenkins.common.job.JdkConfig
import org.springframework.jenkins.common.job.Label
import org.springframework.jenkins.common.job.TestPublisher

/**
 * @author Marcin Grzejszczak
 */
class SpringCloudSamplesEndToEndBuildMaker implements TestPublisher,
		JdkConfig, BreweryDefaults, Label, Cron, SpringCloudJobs {

	private final DslFactory dsl
	private final String organization
	String jdkVersion = jdk17()
	Map<String, String> additionalEnvs = [:]

	SpringCloudSamplesEndToEndBuildMaker(DslFactory dsl) {
		this.dsl = dsl
		this.organization = "spring-cloud-samples"
	}

	SpringCloudSamplesEndToEndBuildMaker(DslFactory dsl, String organization) {
		this.dsl = dsl
		this.organization = organization
	}

	void build(String projectName, String cronExpr) {
		build(projectName, projectName, "scripts/runAcceptanceTests.sh", cronExpr)
	}

	void buildWithGradleTests(String projectName, String branch, String cronExpr) {
		build(projectName, projectName, "scripts/runAcceptanceTests.sh", cronExpr, branch, '', false, true)
	}

	void buildWithMavenTests(String projectName, String branch, String cronExpr) {
		build(projectName, projectName, "scripts/runAcceptanceTests.sh", cronExpr, branch, '', true, false)
	}

	void buildWithoutTests(String projectName, String branch, String cronExpr) {
		build(projectName, projectName, "scripts/runAcceptanceTests.sh", cronExpr, branch, "", false, false)
	}

	void buildWithoutTestsForNewUbuntu(String projectName, String branch, String cronExpr) {
		build(projectName, projectName, "scripts/runAcceptanceTests.sh", cronExpr, branch, "", false, false, ubuntu18_04())
	}

	void buildWithoutTests(String projectName, String cronExpr) {
		build(projectName, projectName, "scripts/runAcceptanceTests.sh", cronExpr, mainBranch(), "", false, false)
	}

	void buildWithGradleAndMavenTests(String projectName, String cronExpr, String branch = mainBranch()) {
		build(projectName, projectName, "scripts/runAcceptanceTests.sh", cronExpr, branch, '', true, true)
	}

	protected String jdkPart() {
		return this.jdkVersion == jdk8() ? "" : "${this.jdkVersion}-"
	}

	protected void build(String projectName, String repoName, String scriptName, String cronExpr, String branchName = mainBranch(),
						 String postBuildScripts = "", boolean mavenTests = false,
						 boolean gradleTests = false, String newLabel = "", boolean isWipeOutWorkspace = true, boolean withNodeJs = false) {
		String organization = this.organization
		dsl.job("${prefixJob(projectName)}-${jdkPart()}${branchName}-e2e") {
			triggers {
				cron cronExpr
			}
			jdk jdkVersion
			label(newLabel)
			environmentVariables(envVars())
			wrappers {
				timestamps()
				colorizeOutput()
				timeout {
					noActivity(defaultInactivity())
					failBuild()
					writeDescription('Build failed due to timeout after {0} minutes of inactivity')
				}
				if (withNodeJs) {
					nodejs("9.11.1 Latest Stable")
				}
				credentialsBinding {
					usernamePassword(dockerhubUserNameEnvVar(),
							dockerhubPasswordEnvVar(),
							dockerhubCredentialId())
				}
			}
			scm {
				git {
					remote {
						url "https://github.com/${organization}/$repoName"
						branch branchName
						credentials(githubUserCredentialId())
					}
					extensions {
						if (isWipeOutWorkspace) {
							wipeOutWorkspace()
						}
						localBranch("**")
					}
				}
			}
			steps {
				shell(loginToDocker())
				shell("""#!/bin/bash
						echo "Cleaning up .m2"
						rm -rf ~/.m2/repository/org/springframework/cloud/
						echo "Clearing up the gradle cache"
						rm -rf ~/.gradle/caches/modules-2/files-2.1/
						./${scriptName}
					""")
				if (postBuildScripts) {
					shell("""#!/bin/bash
						./${postBuildScripts}
					""")
				}
			}
			configure {
				SpringCloudNotification.cloudSlack(it as Node)
			}
			publishers {
				if (gradleTests) {
					archiveJunit gradleJUnitResults()
				}
				if (mavenTests) {
					archiveJunit '**/*-reports/*.xml'
				}
			}
		}
	}

	protected Map<String, String> envVars() {
		def envVars = [
				KILL_AT_THE_END: 'yes',
				TERM           : 'dumb',
				RETRIES        : '70'
		]
		envVars.putAll(this.additionalEnvs)
		return envVars
	}
}


