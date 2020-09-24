package org.springframework.jenkins.cloud.common

import org.springframework.jenkins.common.job.BuildAndDeploy
import org.springframework.jenkins.common.job.JdkConfig
import org.springframework.jenkins.common.job.Label

/**
 * @author Marcin Grzejszczak
 */
trait SpringCloudJobs implements BuildAndDeploy, JdkConfig, Label {

	public static final String CURRENT_CLOUD_VERSION_VAR = "CURRENT_CLOUD_VERSION"
	public static final String CURRENT_BOOT_VERSION_VAR = "CURRENT_BOOT_VERSION"

	@Override
	String projectSuffix() {
		return 'spring-cloud'
	}

	String releaserLabel() {
		return "releaser"
	}

	String setupGitCredentials() {
		return """
					set +x
					git config user.name "${githubUserName()}"
					git config user.email "${githubEmail()}"
					git config credential.helper "store --file=/tmp/gitcredentials"
					echo "https://\$${githubRepoUserNameEnvVar()}:\$${githubRepoPasswordEnvVar()}@github.com" > /tmp/gitcredentials
					export SPRING_CLOUD_STATIC_REPO="https://\$${githubRepoUserNameEnvVar()}:\$${githubRepoPasswordEnvVar()}@github.com/spring-cloud/spring-cloud-static.git"
					trap "{ rm -f /tmp/gitcredentials; }" EXIT
					set -x
				"""
	}

	String removeMavenInstallation() {
		return "rm -rf /opt/jenkins/data/tools/hudson.tasks.Maven_MavenInstallation/maven33/"
	}

	String cleanDeployWithDocs() {
		return "./mvnw clean deploy -Pdocs,deploy,spring -B -U"
	}

	String cleanInstallWithoutDocs() {
		return "./mvnw clean install -Pdeploy,spring -B -U"
	}

	String stopRunningDocker() {
		return """#!/bin/bash
if [ -n "\$(type -t timeout)" ]; then timeout 10s docker ps -a -q | xargs -n 1 -P 8 -I {} docker stop {} || echo "Failed to stop docker... Hopefully you know what you're doing"; fi
if [ -n "\$(type gtimeout)" ]; then gtimeout 10s docker ps -a -q | xargs -n 1 -P 8 -I {} docker stop {} || echo "Failed to stop docker... Hopefully you know what you're doing"; fi
"""
	}

	/**
	 * Dirty hack cause Jenkins is not inserting Maven to path...
	 * Requires using Maven3 installation before calling
	 */
	String mavenBin() {
		return "/opt/jenkins/data/tools/hudson.tasks.Maven_MavenInstallation/maven33/apache-maven-3.3.9/bin/"
	}

	String cleanGitCredentials() {
		return "rm -rf /tmp/gitcredentials"
	}

	String cleanInstall() {
		return "./mvnw clean install -U -Pintegration,spring -Djavadoc.failOnError=false -Djavadoc.failOnWarnings=false"
	}

	String buildDocs() {
		return '''./mvnw clean install -P docs,spring -q -U -DskipTests=true -Dmaven.test.redirectTestOutputToFile=true'''
	}

	String deployDocsWithoutSkippingTests() {
		return '''./mvnw clean deploy -nsu -P docs,integration,spring -U $MVN_LOCAL_OPTS -Dmaven.test.redirectTestOutputToFile=true -Dsurefire.runOrder=random'''
	}

	String fetchLatestCloudVersion(String springCloudMinor) {
		return """
		echo -e "Getting latest version of Spring Cloud"
		# Uncomment this to get latest version at all (not necessarily for the minor)
		#${CURRENT_CLOUD_VERSION_VAR}="\$( curl https://repo.spring.io/libs-snapshot-local/org/springframework/cloud/spring-cloud-starter-build/maven-metadata.xml | sed -ne '/<latest>/s#\\s*<[^>]*>\\s*##gp')"
		$CURRENT_CLOUD_VERSION_VAR="\${$CURRENT_CLOUD_VERSION_VAR:-}"
		[[ -z "\$${CURRENT_CLOUD_VERSION_VAR}" ]] && ${CURRENT_CLOUD_VERSION_VAR}="\$( curl https://repo.spring.io/libs-snapshot-local/org/springframework/cloud/spring-cloud-starter-build/maven-metadata.xml | grep "<version>${springCloudMinor}." | grep "SNAPSHOT" | tail -1 | sed -ne '/<version>/s#\\s*<[^>]*>\\s*##gp')"
		echo -e "Latest version of cloud for minor [${springCloudMinor}] is [\$${CURRENT_CLOUD_VERSION_VAR}]"
"""
	}

	String fetchLatestBootVersion(String bootMinor) {
		return """
		echo -e "Getting latest version of Spring Cloud"
		# Uncomment this to get latest version at all (not necessarily for the minor)
		#${CURRENT_BOOT_VERSION_VAR}="\$( curl https://repo.spring.io/libs-snapshot-local/org/springframework/boot/spring-boot-starter/maven-metadata.xml | sed -ne '/<latest>/s#\\s*<[^>]*>\\s*##gp')"
		$CURRENT_BOOT_VERSION_VAR="\${$CURRENT_BOOT_VERSION_VAR:-}"
		[[ -z "\$${CURRENT_BOOT_VERSION_VAR}" ]] && ${CURRENT_BOOT_VERSION_VAR}="\$( curlhttps://repo.spring.io/libs-snapshot-local/org/springframework/boot/spring-boot-starter/maven-metadata.xml | grep "<version>${bootMinor}." | grep "SNAPSHOT" | tail -1 | sed -ne '/<version>/s#\\s*<[^>]*>\\s*##gp')"
		echo -e "Latest version of boot minor [${bootMinor}] is [\$${CURRENT_BOOT_VERSION_VAR}]"
"""
	}

	String fetchLatestCloudVersionAsFunction() {
		return """
		function springCloudVersion {
			local cloudVersion="\${1}"
			curl --silent https://repo.spring.io/libs-snapshot-local/org/springframework/cloud/spring-cloud-starter-build/maven-metadata.xml | grep "<version>\${cloudVersion}." | grep "SNAPSHOT" | tail -1 | sed -ne '/<version>/s#\\s*<[^>]*>\\s*##gp' | xargs
		}
"""
	}

	String fetchLatestBootVersionAsFunction() {
		return """
		function bootVersion {
			local bootVersion="\${1}"
			curl --silent ttps://repo.spring.io/libs-snapshot-local/org/springframework/boot/spring-boot-starter/maven-metadata.xml | grep "<version>\${bootVersion}." | grep "SNAPSHOT" | tail -1 | sed -ne '/<version>/s#\\s*<[^>]*>\\s*##gp' | xargs
		}
"""
	}

	String currentCloudVersionVar() {
		return CURRENT_CLOUD_VERSION_VAR
	}

	String currentBootVersionVar() {
		return CURRENT_BOOT_VERSION_VAR
	}

	String repoUserNameEnvVar() {
		return 'REPO_USERNAME'
	}

	String repoPasswordEnvVar() {
		return 'REPO_PASSWORD'
	}

	String githubRepoUserNameEnvVar() {
		return 'GITHUB_REPO_USERNAME'
	}

	String githubRepoPasswordEnvVar() {
		return 'GITHUB_REPO_PASSWORD'
	}

	String gradlePublishKeyEnvVar() {
		return 'GRADLE_PUBLISH_KEY'
	}

	String gradlePublishSecretEnvVar() {
		return 'GRADLE_PUBLISH_SECRET'
	}

	String gradlePublishKeySecretId() {
		return 'gradle-publish-key'
	}

	String gradlePublishSecretSecretId() {
		return 'gradle-publish-secret'
	}

	String repoSpringIoUserCredentialId() {
		return '02bd1690-b54f-4c9f-819d-a77cb7a9822c'
	}

	String githubUserCredentialId() {
		return '3a20bcaa-d8ad-48e3-901d-9fbc941376ee'
	}
	
	String dockerhubUserNameEnvVar() {
		return 'DOCKER_HUB_USERNAME'
	}

	String dockerhubPasswordEnvVar() {
		return 'DOCKER_HUB_PASSWORD'
	}

	String dockerhubCredentialId() {
		return 'hub.docker.com-springbuildmaster'
	}

	String githubUserName() {
		return 'spring-buildmaster'
	}

	String githubEmail() {
		return 'buildmaster@springframework.org'
	}

	String gpgSecRing() {
		return 'FOO_SEC'
	}

	String gpgPubRing() {
		return 'FOO_PUB'
	}

	String gpgPassphrase() {
		return 'FOO_PASSPHRASE'
	}

	String sonatypeUser() {
		return 'SONATYPE_USER'
	}

	String sonatypePassword() {
		return 'SONATYPE_PASSWORD'
	}

	String githubToken() {
		return 'RELEASER_GIT_OAUTH_TOKEN'
	}

	String githubTokenCredId() {
		return '7b3ebbea-7001-479b-8578-b8c464dab973'
	}

	void slackNotification(Node node) {
		SpringCloudNotification.cloudSlack(node) {
			notifyFailure()
			notifySuccess()
			notifyUnstable()
		}
	}

	String windows() {
		return "windows"
	}
}