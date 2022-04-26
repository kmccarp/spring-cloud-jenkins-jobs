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
class MutationBuildMaker implements JdkConfig, TestPublisher, SonarTrait, Cron {

	private final DslFactory dsl

	private final String organization

	String rootView;

	String jdkVersion = jdk8()

	MutationBuildMaker(DslFactory dsl) {
		this.dsl = dsl
		this.organization = "spring-cloud"
		this.rootView = "Spring%20Cloud"
	}

	MutationBuildMaker(DslFactory dsl, String organization) {
		this.dsl = dsl
		this.organization = organization
	}

	MutationBuildMaker jdk(String jdkVersion) {
		this.jdkVersion = jdkVersion
		return this
	}

	MutationBuildMaker rootView(String rootView) {
		this.rootView = rootView
		return this
	}

	void build(String projectName) {
		build(projectName, everySunday())
	}

	void build(String projectName, String cronExpr) {
		dsl.job("$projectName-qa-mutation") {
			triggers {
				cron cronExpr
			}
			environmentVariables {
				env("CI", "jenkins")
			}
			scm {
				git {
					remote {
						url "https://github.com/$organization/$projectName"
						branch "main"
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
				// PitestPublisher.cloudMutation(it as Node)
			}
		}
	}

	Closure defaultSteps() {
		return buildStep {
			shell("./mvnw clean verify -Pmutation,spring org.pitest:pitest-maven:report-aggregate-module -U || ${postAction()}")
			shell('''\
ROOT_VIEW=''' + this.rootView + '''
CURRENT_VIEW="QA"
INDEX_HTML="target/pit-reports/index.html"
echo "You can click here to see the PIT report [${JENKINS_URL}/view/${ROOT_VIEW}/view/${CURRENT_VIEW}/job/${JOB_NAME}/ws/${INDEX_HTML}]"
''')
		}
	}

	protected Closure buildStep(@DelegatesTo(StepContext) Closure buildSteps) {
		return buildSteps
	}

	protected String postAction() {
		return 'exit 1'
	}
}
