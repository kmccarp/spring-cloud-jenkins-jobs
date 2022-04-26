package org.springframework.jenkins.cloud.compatibility

import groovy.transform.CompileStatic
import javaposse.jobdsl.dsl.helpers.step.StepContext

import org.springframework.jenkins.cloud.common.AllCloudConstants
import org.springframework.jenkins.cloud.common.SpringCloudJobs
import org.springframework.jenkins.common.job.Maven

/**
 * @author Marcin Grzejszczak
 */
@CompileStatic
abstract class CompatibilityTasks implements Maven, SpringCloudJobs {

	protected static final String DEFAULT_BOOT_MINOR_VERSION = ""
	protected static final String SPRING_BOOT_VERSION_VAR = 'SPRING_BOOT_VERSION'
	protected static final String SPRING_BOOT_MINOR = AllCloudConstants.BOOT_MINOR_FOR_API_COMPATIBILITY
	protected static final String SPRING_VERSION_VAR = 'SPRING_VERSION'
	protected static final String SPRING_CLOUD_BUILD_BRANCH = 'SPRING_CLOUD_BUILD_BRANCH'
	protected static final String DEFAULT_BUILD_BRANCH = AllCloudConstants.SPRING_CLOUD_BUILD_BRANCH_FOR_COMPATIBILITY_BUILD

	Closure defaultStepsForBoot(String bootVersion = SPRING_BOOT_MINOR) {
		return buildStep {
			shell compileProductionForBoot(bootVersion)
		}
	}

	protected String printDepsForBoot() {
		return """
					echo -e "Printing the list of dependencies"
					./mvnw dependency:tree -Dspring-boot.version=\$${SPRING_BOOT_VERSION_VAR}
			"""
	}

	Closure defaultStepsWithTestsForBoot(String bootVersion = SPRING_BOOT_MINOR) {
		return buildStep {
			shell runTestsForBoot(bootVersion)
		}
	}

	protected String compileProductionForBoot(String bootVersion) {
		return """#!/bin/bash -x
					set -o errexit
					${fetchLatestBootSnapshotVersion(bootVersion)}
					${bumpBoot(bootVersion)}
					echo -e "Checking if prod code compiles against latest boot"
					${buildCommand()}
					${printDepsForBoot()}
"""
	}

	protected String buildCommand() {
		return "./mvnw clean package -U -fae -Dspring-boot.version=\$${SPRING_BOOT_VERSION_VAR} -DskipTests"
	}

	protected String runTestsForBoot(String bootVersion) {
		return """#!/bin/bash -x
					set -o errexit
					${fetchLatestBootSnapshotVersion(bootVersion)}
					${bumpBoot(bootVersion)}
					echo -e "Checking if the project can be built with Boot version [\$${SPRING_BOOT_VERSION_VAR}]"
					./mvnw clean install -U -fae
					${printDepsForBoot()}
					"""
	}

	protected String bumpBoot(String bootVersion = SPRING_BOOT_MINOR) {
		return """
		echo "Removing stored spring-cloud-release-tools"
		rm -rf ~/.m2/repository/org/springframework/cloud/internal
		echo -e "Will:
		1)Download releaser
		2)Clone SC-Build
		3)Use releaser to bump boot for SC-Build
		4)Install new SC-Build locally
		5)Build the project"
		rm -rf target
		mkdir -p target
		export MAVEN_PATH=${mavenBin()}
		${fetchLatestBootVersionAsFunction()}
		export ${SPRING_BOOT_VERSION_VAR}="\${${SPRING_BOOT_VERSION_VAR}:-}"
		[[ -z "\$${SPRING_BOOT_VERSION_VAR}" ]] && ${SPRING_BOOT_VERSION_VAR}="\$( bootVersion "${bootVersion}" )"
		echo "Boot version [\$${SPRING_BOOT_VERSION_VAR}]" 
		pushd target
			\${MAVEN_PATH}/mvn dependency:get -DremoteRepositories=https://repo.spring.io/libs-snapshot-local -Dartifact=org.springframework.cloud.internal:spring-cloud:2.0.0-SNAPSHOT -Dtransitive=false
			\${MAVEN_PATH}/mvn dependency:copy -Dartifact=org.springframework.cloud.internal:spring-cloud:2.0.0-SNAPSHOT -Dproject.basedir=../
			mv dependency/*.jar dependency/spring-cloud-2.0.0-SNAPSHOT.jar
			echo "Cloning Spring Cloud Build"
			git clone https://github.com/spring-cloud/spring-cloud-build.git
			pushd spring-cloud-build
				${SPRING_CLOUD_BUILD_BRANCH}="\${${SPRING_CLOUD_BUILD_BRANCH}:-main}"
				git checkout "\$${SPRING_CLOUD_BUILD_BRANCH}"
				echo -e "Updating SC-Build's Boot version [\$${SPRING_BOOT_VERSION_VAR}]"
				java -jar ../dependency/spring-cloud-2.0.0-SNAPSHOT.jar --releaser.git.fetch-versions-from-git=false --"releaser.fixed-versions[spring-boot]=\$${SPRING_BOOT_VERSION_VAR}" --"releaser.fixed-versions[spring-boot-dependencies]=\$${SPRING_BOOT_VERSION_VAR}" --releaser.git.oauth-token="token" -u -i=false
				trap "echo 'Clearing spring-cloud-build jars' && rm -rf ~/.m2/repository/org/springframework/cloud/spring-cloud-build/" EXIT
				./mvnw clean install -fae -U
			popd
		popd
"""
	}

	/**
	 * Dirty hack cause Jenkins is not inserting Maven to path...
	 * Requires using Maven3 installation before calling
	 */
	String mavenBin() {
		return "/opt/jenkins/data/tools/hudson.tasks.Maven_MavenInstallation/maven33/apache-maven-3.3.9/bin/"
	}

	Closure defaultStepsForSpring() {
		return buildStep {
			shell compileProductionForSpring()
		}
	}

	protected String printDepsForSpring() {
		return """
					echo -e "Printing the list of dependencies"
					./mvnw dependency:tree -U -Dspring.version=\$${SPRING_VERSION_VAR}
			"""
	}

	Closure defaultStepsWithTestsForSpring() {
		return buildStep {
			shell runTestsForSpring()
		}
	}

	protected String compileProductionForSpring() {
		return """
					echo -e "Checking if prod code compiles against latest spring"
					./mvnw clean compile -U -fae -Dspring.version=\$${SPRING_VERSION_VAR}
					${printDepsForSpring()}
"""
	}

	protected String runTestsForSpring() {
		return """
					echo -e "Checking if prod code compiles against latest spring"
					./mvnw clean install -U -fae -Dspring.version=\$${SPRING_VERSION_VAR}
					${printDepsForSpring()}
"""
	}

	private Closure buildStep(@DelegatesTo(StepContext) Closure buildSteps) {
		return buildSteps
	}

}
