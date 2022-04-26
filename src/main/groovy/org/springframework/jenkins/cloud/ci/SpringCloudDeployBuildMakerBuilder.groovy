package org.springframework.jenkins.cloud.ci

import groovy.transform.CompileStatic
import javaposse.jobdsl.dsl.DslFactory

import org.springframework.jenkins.cloud.common.CloudCron
import org.springframework.jenkins.cloud.common.SpringCloudJobs
import org.springframework.jenkins.common.job.JdkConfig
import org.springframework.jenkins.common.job.Maven
import org.springframework.jenkins.common.job.TestPublisher

/**
 * @author Marcin Grzejszczak
 */
@CompileStatic
class SpringCloudDeployBuildMakerBuilder implements JdkConfig, TestPublisher, CloudCron,
		SpringCloudJobs, Maven {
	protected final DslFactory dsl
	String organization
	String prefix
	String jobName
	String jdkVersion
	boolean upload = true
	Closure<Node> slack

	SpringCloudDeployBuildMakerBuilder(DslFactory dsl) {
		this.dsl = dsl
	}

	SpringCloudDeployBuildMakerBuilder organization(String organization) {
		this.organization = organization
		return this
	}

	SpringCloudDeployBuildMakerBuilder prefix(String prefix) {
		this.prefix = prefix
		return this
	}

	SpringCloudDeployBuildMakerBuilder jobName(String jobName) {
		this.jobName = jobName
		return this
	}

	SpringCloudDeployBuildMakerBuilder jdkVersion(String jdkVersion) {
		this.jdkVersion = jdkVersion
		return this
	}

	SpringCloudDeployBuildMakerBuilder upload(boolean upload) {
		this.upload = upload
		return this
	}

	SpringCloudDeployBuildMakerBuilder cron(String cron) {
		this.cronValue = cron
		return this
	}

	SpringCloudDeployBuildMakerBuilder onGithubPush(boolean onGithubPush) {
		this.onGithubPush = onGithubPush
		return this
	}

	SpringCloudDeployBuildMakerBuilder slack(Closure<Node> slack) {
		this.slack = slack
		return this
	}

	SpringCloudDeployBuildMaker build() {
		def maker = new SpringCloudDeployBuildMaker(this.dsl, this.organization, this.prefix)
		if (this.jdkVersion) {
			maker.jdkVersion = this.jdkVersion
		}
		if (this.slack) {
			maker.slack = this.slack
		}
		if (this.jobName) {
			maker.jobName = jobName
		}
		maker.upload = this.upload
		maker.cronValue = this.cronValue
		maker.onGithubPush = this.onGithubPush
		return maker
	}
}
