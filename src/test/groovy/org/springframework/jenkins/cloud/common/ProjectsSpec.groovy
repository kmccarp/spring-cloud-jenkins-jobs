package org.springframework.jenkins.cloud.common

import spock.lang.Specification

class ProjectsSpec extends Specification {

	def 'define all projects'() {
		expect:
		Projects.ALL_BY_NAME.keySet().containsAll(["spring-cloud-build", "spring-cloud-commons"])
		Projects.ALL_BY_NAME.size() == 24
	}

	def 'define a project'() {
		expect:
		Projects.BUILD.repo == "spring-cloud-build"
		Projects.BUILD.org == "spring-cloud"
		Projects.BUILD.buildSystem == Project.BuildSystem.MAVEN
		!Projects.BUILD.hasTests
		Projects.BUILD.checkBootCompatibility
		Projects.TRAIN_DOCS.repo == "spring-cloud-release"
		Projects.TRAIN_DOCS.name == "spring-cloud-release-train-docs"
		Projects.TRAIN_DOCS.getName() == "spring-cloud-release-train-docs"
		!Projects.TRAIN_DOCS.checkJdkCompatibility
		!Projects.BUS.notificationEmails
		Projects.STREAM.notificationEmails
		Projects.STREAM.notificationEmails.contains("@vmware.com")
	}

}
