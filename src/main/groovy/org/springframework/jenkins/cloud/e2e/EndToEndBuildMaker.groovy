package org.springframework.jenkins.cloud.e2e

import javaposse.jobdsl.dsl.DslFactory

import org.springframework.jenkins.cloud.common.SpringCloudJobs
import org.springframework.jenkins.cloud.common.SpringCloudNotification
import org.springframework.jenkins.common.job.Cron
import org.springframework.jenkins.common.job.JdkConfig
import org.springframework.jenkins.common.job.Label
import org.springframework.jenkins.common.job.Slack
import org.springframework.jenkins.common.job.TestPublisher

/**
 * @author Marcin Grzejszczak
 */
class EndToEndBuildMaker implements TestPublisher,
		JdkConfig, BreweryDefaults, Label, Cron, SpringCloudJobs {

	private static final int MAX_EC2_EXECUTORS = 1

	private final DslFactory dsl
	private final String organization

	String branchName = mainBranch()

	EndToEndBuildMaker(DslFactory dsl) {
		this.dsl = dsl
		this.organization = "spring-cloud"
	}

	EndToEndBuildMaker(DslFactory dsl, String organization) {
		this.dsl = dsl
		this.organization = organization
	}

	void build(String projectName, String cronExpr) {
		build(projectName, "scripts/runAcceptanceTests.sh", cronExpr)
	}

	void buildWithoutTests(String projectName, String cronExpr) {
		build(projectName, "scripts/runAcceptanceTests.sh", cronExpr, false)
	}

	void build(String projectName, String scriptName, String cronExpr, boolean withTests = true) {
		build(projectName, projectName, scriptName, cronExpr, withTests)
	}

	void buildWithGradleAndMavenTests(String projectName, String scriptName, String cronExpr) {
		build(projectName, projectName, scriptName, cronExpr, true, '', true)
	}

	protected void build(String projectName, String repoName, String scriptName, String cronExpr, boolean withTests = true, String postBuildScripts = "", boolean mavenTests = false) {
		String organization = this.organization
		dsl.job("${prefixJob(projectName)}-e2e") {
			triggers {
				cron cronExpr
			}
			jdk jdkVersion()
			if (jdkVersion() != jdk8()) {
				label(ubuntu18_04())
			}
			wrappers {
				timestamps()
				colorizeOutput()
				environmentVariables([
						TERM         : 'dumb',
						RETRIES      : 70,
						WAVEFRONT_URI: "https://demo.wavefront.com"
				])
				timeout {
					noActivity(defaultInactivity())
					failBuild()
					writeDescription('Build failed due to timeout after {0} minutes of inactivity')
				}
				maskPasswords()
				credentialsBinding {
					usernamePassword(dockerhubUserNameEnvVar(),
							dockerhubPasswordEnvVar(),
							dockerhubCredentialId())
					string("WAVEFRONT_API_TOKEN", "brewery-wavefront-token")
				}
			}
			scm {
				git {
					remote {
						url "https://github.com/${organization}/$repoName"
						branch branchName
					}
					extensions {
						wipeOutWorkspace()
						localBranch("**")
					}
				}
			}
			weight(MAX_EC2_EXECUTORS)
			steps {
				shell(loginToDocker())
				shell(stopRunningDocker())
				shell(killAllApps())
				shell("""#!/bin/bash
					echo "Cleaning up .m2"
					rm -rf ~/.m2/repository/org/springframework/cloud/
					echo "Clearing Gradle caches" 
					rm -rf ~/.gradle/caches/modules-2/files-2.1/ 
				""")
				shell("""#!/bin/bash
						${fetchLatestBootVersionAsFunction()}
			
						${fetchLatestCloudVersionAsFunction()}
						
						./${scriptName}
					""")
				if (postBuildScripts) {
					shell("""#!/bin/bash
						./${postBuildScripts}
					""")
				}
			}
			configure {
				customConfiguration(projectName, it as Node)
			}
			publishers {
				if (withTests) {
					archiveArtifacts {
						pattern acceptanceTestReports()
						allowEmpty()
					}
					archiveArtifacts {
						pattern acceptanceTestSpockReports()
						allowEmpty()
					}
				}
				if (mavenTests) {
					archiveJunit mavenJUnitResults()
				} else {
					archiveJunit gradleJUnitResults()
				}
			}
		}
	}

	protected String jdkVersion() {
		return jdk17()
	}

	protected void customConfiguration(String projectName, Node node) {
		Slack slack = SpringCloudNotification.cloudSlack(node)
		if (projectName.contains("stream")) {
			slack.room([SpringCloudNotification.CLOUD_ROOM, SpringCloudNotification.STREAM_ROOM].join(","))
		}
	}
}
