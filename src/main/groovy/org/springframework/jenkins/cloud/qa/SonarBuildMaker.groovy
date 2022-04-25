package org.springframework.jenkins.cloud.qa

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.helpers.step.StepContext
import org.springframework.jenkins.cloud.common.SpringCloudNotification
import org.springframework.jenkins.common.job.Cron
import org.springframework.jenkins.common.job.JdkConfig
import org.springframework.jenkins.common.job.SonarTrait
import org.springframework.jenkins.common.job.TestPublisher

/**
 * @author Marcin Grzejszczak
 */
class SonarBuildMaker implements JdkConfig, TestPublisher, SonarTrait, Cron {

	private final DslFactory dsl

	String branchName = "main"

	String org = "spring-cloud"

	String jdkVersion = jdk17()

	SonarBuildMaker(DslFactory dsl) {
		this.dsl = dsl
	}

	void buildSonar(String projectName) {
		buildSonar(projectName, oncePerDay())
	}

	void buildSonar(String projectName, String cronExpr) {
		dsl.job("$projectName-sonar") {
			triggers {
				cron cronExpr
			}
			environmentVariables {
				env("CI", "jenkins")
			}
			scm {
				git {
					remote {
						url "https://github.com/${org}/${projectName}"
						branch branchName
					}
					extensions {
						wipeOutWorkspace()
						localBranch("**")
					}
				}
			}
			jdk jdkVersion
			steps defaultSteps()
			publishers {
				archiveArtifacts mavenJUnitResults()
			}
			configure {
				SpringCloudNotification.cloudSlack(it as Node)
				appendSonar(it as Node)
			}
		}
	}

	Closure defaultSteps() {
		return buildStep {
			shell('./mvnw clean org.jacoco:jacoco-maven-plugin:prepare-agent install -Psonar,spring -U')
			shell("""\
				echo "Running sonar please wait..."
				set +x
				./mvnw \$SONAR_MAVEN_GOAL -Psonar,spring -Dsonar.host.url=\$SONAR_HOST_URL -Dsonar.login=\$SONAR_AUTH_TOKEN || ${postAction()}
				set -x
				""")
		}
	}

	protected String postAction() {
		return 'echo "Tests failed"'
	}

	protected Closure buildStep(@DelegatesTo(StepContext) Closure buildSteps) {
		return buildSteps
	}
}
