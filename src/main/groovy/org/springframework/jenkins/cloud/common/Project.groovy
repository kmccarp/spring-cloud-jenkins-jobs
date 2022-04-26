package org.springframework.jenkins.cloud.common

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@CompileStatic
@EqualsAndHashCode
@ToString
class Project {

	enum BuildSystem {
		MAVEN, GRADLE, BOTH
	}

	enum ReleaseType {
		SNAPSHOT, RELEASE, BOTH, NONE
	}

	// repo spring-cloud-build
	String repo;

	// name, defaults to repo
	String name

	// org default to spring-cloud, allow spring-projects-experimental
	String org = "spring-cloud"

	// boolean has tests?
	boolean hasTests = true

	// enum build system, maven, gradle, both
	BuildSystem buildSystem = BuildSystem.MAVEN

	ReleaseType releaseType = ReleaseType.BOTH

	// included in boot compatibility?
	boolean checkJdkCompatibility = true

	// included in boot compatibility?
	boolean checkBootCompatibility = true

	String getName() {
		return name ?: repo
	}

	boolean releaseTypeSnapshots() {
		releaseType == ReleaseType.SNAPSHOT || releaseType == ReleaseType.BOTH
	}

	boolean releaseTypeRelease() {
		releaseType == ReleaseType.RELEASE || releaseType == ReleaseType.BOTH
	}

	// custom build stuff (branch param, jdk param)
	// see CustomJob
	List<String> customBuildCommand(BuildContext context) {
		return null;
	}

	String labelExpression(BuildContext context) {
		return null;
	}

	boolean publishTests(BuildContext context) {
		return hasTests
	}

	static void verify(Project project, BuildContext buildContext) {
		assert project.org?.trim(): "Project org is required"
		assert project.repo?.trim(): "Project repo is required"
		assert project.buildSystem: "Project buildSystgem is required"
		assert buildContext.jdk?.trim(): "Project jdk is required"
		assert buildContext.branch?.trim(): "Project branch is required"
	}

	/**
	 * Provides information to project methods unkown ahead of time.
	 */

}
