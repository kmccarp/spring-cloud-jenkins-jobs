package org.springframework.jenkins.cloud.e2e

import javaposse.jobdsl.dsl.DslFactory

import org.springframework.jenkins.cloud.common.SpringCloudJobs
import org.springframework.jenkins.common.job.Cron
import org.springframework.jenkins.common.job.JdkConfig
import org.springframework.jenkins.common.job.Label
import org.springframework.jenkins.common.job.TestPublisher

class SpringCloudSamplesEndToEndBuilder<T extends SpringCloudSamplesEndToEndBuilder> implements TestPublisher,
		JdkConfig, BreweryDefaults, Label, Cron, SpringCloudJobs {
	String projectName
	String organization = "spring-cloud-samples"
	String repoName
	String scriptName = "scripts/runAcceptanceTests.sh"
	String cronExpr
	String branchName = mainBranch()
	String label = ""
	String postBuildScripts = ""
	boolean wipeOutWorkspace = true
	String jdk = jdk17()
	boolean mavenTests = false
	boolean gradleTests = false
	boolean withNodeJs = false
	Map<String, String> envs = [:]

	T withOrganization(String organization) {
		this.organization = organization
		return this
	}

	T withProjectName(String projectName) {
		this.projectName = projectName
		return this
	}

	T withProjectAndRepoName(String projectName) {
		this.projectName = projectName
		this.repoName = projectName
		return this
	}

	T withJdk(String jdk) {
		this.jdk = jdk
		return this
	}

	T withWipeOutWorkspace(boolean wipeOutWorkspace = true) {
		this.wipeOutWorkspace = wipeOutWorkspace
		return this
	}

	T withRepoName(String repoName) {
		this.repoName = repoName
		return this
	}

	T withScriptName(String scriptName) {
		this.scriptName = scriptName
		return this
	}

	T withCronExpr(String cronExpr) {
		this.cronExpr = cronExpr
		return this
	}

	T withBranchName(String branchName) {
		this.branchName = branchName
		return this
	}

	T withEnvs(Map<String, String> envs) {
		this.envs = envs
		return this
	}

	T withPostBuildScripts(String postBuildScripts) {
		this.postBuildScripts = postBuildScripts
		return this
	}

	T withMavenTests(boolean mavenTests) {
		this.mavenTests = mavenTests
		return this
	}

	T withGradleTests(boolean gradleTests) {
		this.gradleTests = gradleTests
		return this
	}

	T withNodeJs(boolean withNodeJs) {
		this.withNodeJs = withNodeJs
		return this
	}

	T withLabel(String label) {
		this.label = label
		return this
	}

	SpringCloudSamplesEndToEndBuildMaker build(DslFactory dsl) {
		def maker = new SpringCloudSamplesEndToEndBuildMaker(dsl, this.organization)
		maker.jdkVersion = this.jdk
		maker.additionalEnvs = this.envs
		return maker
				.build(this.projectName, this.repoName, this.scriptName, this.cronExpr,
						this.branchName, this.postBuildScripts, this.mavenTests, this.gradleTests,
						this.label, this.wipeOutWorkspace, this.withNodeJs)
	}
}
