package org.springframework.jenkins.cloud.common

/**
 * @author Marcin Grzejszczak
 */
interface CustomJob extends CloudCron {
	void deploy()

	void deploy(String branch)

	String compileOnlyCommand()

	String projectName()

	boolean checkTests()

	void jdkBuild(String jdkVersion)
}
