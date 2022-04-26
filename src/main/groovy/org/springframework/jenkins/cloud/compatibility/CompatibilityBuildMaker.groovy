package org.springframework.jenkins.cloud.compatibility

import javaposse.jobdsl.dsl.DslFactory

import org.springframework.jenkins.cloud.common.SpringCloudJobs
import org.springframework.jenkins.common.job.Cron
import org.springframework.jenkins.common.job.JdkConfig
import org.springframework.jenkins.common.job.Maven
import org.springframework.jenkins.common.job.TestPublisher

/**
 * @author Marcin Grzejszczak
 */
abstract class CompatibilityBuildMaker extends CompatibilityTasks implements TestPublisher,
		JdkConfig, SpringCloudJobs, Cron, Maven {
	public static final String COMPATIBILITY_BUILD_DEFAULT_SUFFIX = 'compatibility-check'

	protected final DslFactory dsl
	protected final String organization
	protected final String suffix

	CompatibilityBuildMaker(DslFactory dsl) {
		this.dsl = dsl
		this.suffix = COMPATIBILITY_BUILD_DEFAULT_SUFFIX
		this.organization = 'spring-cloud'
	}

	CompatibilityBuildMaker(DslFactory dsl, String suffix) {
		this.dsl = dsl
		this.suffix = suffix
		this.organization = 'spring-cloud'
	}

	CompatibilityBuildMaker(DslFactory dsl, String suffix, String organization) {
		this.dsl = dsl
		this.suffix = suffix
		this.organization = organization
	}

	void build(String projectName, String cronExpr = '', boolean parametrizedVersions = true) {
		buildWithTests(projectName, projectName, mainBranch(), cronExpr, true, parametrizedVersions)
	}

	void build(String projectName, String repoName, String branch, String cronExpr, boolean parametrizedVersions = true) {
		buildWithTests(projectName, repoName, branch, cronExpr, true)
	}

	void buildWithoutTests(String projectName, String repoName, String branch, String cronExpr) {
		buildWithTests(projectName, repoName, branch, cronExpr, false)
	}

	abstract protected void buildWithTests(String projectName, String repoName, String branchName, String cronExpr, boolean checkTests, boolean parametrizedVersions = true)

	void buildWithoutTests(String projectName, String cronExpr = '', boolean parametrizedVersions = true) {
		buildWithTests(projectName, projectName, mainBranch(), cronExpr, false, parametrizedVersions)
	}

}
