package org.springframework.jenkins.cloud.e2e

import org.springframework.jenkins.cloud.common.AllCloudJobs
import org.springframework.jenkins.common.job.JdkConfig
import javaposse.jobdsl.dsl.DslFactory
import org.springframework.jenkins.cloud.common.SpringCloudJobs
import org.springframework.jenkins.cloud.common.SpringCloudNotification
import org.springframework.jenkins.common.job.Cron
import org.springframework.jenkins.common.job.TestPublisher

/**
 * @author Marcin Grzejszczak
 */
class SpringCloudSamplesTestsBuildMaker implements TestPublisher,
		JdkConfig, BreweryDefaults, Cron, SpringCloudJobs {

	private final DslFactory dsl
	private final String organization

	SpringCloudSamplesTestsBuildMaker(DslFactory dsl) {
		this.dsl = dsl
		this.organization = "spring-cloud-samples"
	}

	SpringCloudSamplesTestsBuildMaker(DslFactory dsl, String organization) {
		this.dsl = dsl
		this.organization = organization
	}

	void buildForIlford() {
		build(AllCloudJobs.RELEASE_TRAIN_TO_BOOT_VERSION_MINOR.entrySet().first().key, "tests", everySixHours(), masterBranch())
	}

	void buildForIlfordWithJdk(String jdk) {
		build(AllCloudJobs.RELEASE_TRAIN_TO_BOOT_VERSION_MINOR.entrySet().first().key, "tests-${jdk}", everySixHours(), masterBranch(), jdk)
	}

	private void build(String cloudTrainVersion, String projectName, String cronExpr = everySixHours(),
					   String branchName = masterBranch(), String jdkVersion = jdk8()) {
		String organization = this.organization
		dsl.job("${prefixJob(projectName)}-${branchName}-e2e") {
			triggers {
				cron cronExpr
			}
			jdk jdkVersion
			wrappers {
				timestamps()
				colorizeOutput()
				timeout {
					noActivity(defaultInactivity())
					failBuild()
					writeDescription('Build failed due to timeout after {0} minutes of inactivity')
				}
			}
			scm {
				git {
					remote {
						url "https://github.com/${organization}/tests"
						branch branchName
					}
					extensions {
						wipeOutWorkspace()
						localBranch("**")
					}
				}
			}
			steps {
				String bootMinor = AllCloudJobs.bootForReleaseTrain(cloudTrainVersion)
				shell("""#!/bin/bash
						set -o errexit
						set -o errtrace
						set -o nounset
						set -o pipefail
						
						echo "Current java version"
						java -version
						${fetchLatestBootVersion(bootMinor)}
						${fetchLatestCloudVersion(cloudTrainVersion)}

						echo "Running the build with cloud train [\$${currentCloudVersionVar()}] and Boot version [\$${currentBootVersionVar()}]"
						./scripts/runAcceptanceTests.sh
					""")
			}
			configure {
				SpringCloudNotification.cloudSlack(it as Node)
			}
			publishers {
				archiveJunit mavenJUnitResults()
			}
		}
	}

}
