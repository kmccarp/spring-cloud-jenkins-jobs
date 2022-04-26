package org.springframework.jenkins.cloud.common


import groovy.transform.CompileStatic

import org.springframework.jenkins.cloud.common.Project.ReleaseType

/**
 * TBD
 *
 * @author Spencer Gibb
 */
@CompileStatic
class Projects {

	public static final Project BUILD = new Project(
			repo: "spring-cloud-build",
			hasTests: false
	)

	public static final Project BUS = new Project(
			repo: "spring-cloud-bus"
	)

	public static final Project CIRCUITBREAKER = new Project(
			repo: "spring-cloud-circuitbreaker"
	)

	public static final Project CLI = new Project(
			repo: "spring-cloud-cli",
			checkBootCompatibility: false
	)

	public static final Project CLOUDFOUNDRY = new Project(
			repo: "spring-cloud-cloudfoundry"
	)

	public static final Project COMMONS = new Project(
			repo: "spring-cloud-commons"
	)

	public static final Project CONFIG = new Project(
			repo: "spring-cloud-config"
	)

	public static final Project CONSUL = new Project(
			repo: "spring-cloud-consul"
	)

	public static final Project CONTRACT = new ContractProject(
			repo: "spring-cloud-contract",
			buildSystem: Project.BuildSystem.BOTH
	)

	public static final Project CORE_TESTS = new Project(
			repo: "spring-cloud-core-tests",
			releaseType: ReleaseType.NONE
	)

	public static final Project FUNCTION = new Project(
			repo: "spring-cloud-function"
	)

	public static final Project GATEWAY = new Project(
			repo: "spring-cloud-gateway"
	)

	public static final Project KUBERNETES = new KubernetesProject(
			repo: "spring-cloud-kubernetes"
	)

	public static final Project NETFLIX = new Project(
			repo: "spring-cloud-netflix"
	)

	public static final Project OPENFEIGN = new Project(
			repo: "spring-cloud-openfeign"
	)

	public static final Project RELEASE = new Project(
			repo: "spring-cloud-release",
			hasTests: false
	)

	public static final Project SLEUTH = new Project(
			repo: "spring-cloud-sleuth"
	)

	public static final Project STREAM = new Project(
			repo: "spring-cloud-stream"
	)

	public static final Project TASK = new Project(
			repo: "spring-cloud-task"
	)

	public static final Project TRAIN_DOCS = new TrainDocsProject(
			repo: "spring-cloud-release",
			name: "spring-cloud-release-train-docs",
			checkJdkCompatibility: false
	)

	public static final VaultProject VAULT = new VaultProject(
			repo: "spring-cloud-vault",
			hasTests: false
	)

	public static final Project ZOOKEEPER = new Project(
			repo: "spring-cloud-zookeeper"
	)

	public static final Project SQUARE = new Project(
			repo: "spring-cloud-square",
			org: "spring-projects-experimental",
			releaseType: ReleaseType.RELEASE
	)

	public static final Project SLEUTH_OTEL = new Project(
			repo: "spring-cloud-sleuth-otel",
			org: "spring-projects-experimental",
			releaseType: ReleaseType.RELEASE
	)

	public static final Project RELEASE_TOOLS = new Project(
			repo: "spring-cloud-release-tools",
			releaseType: ReleaseType.NONE
	)

	public static final List<Project> ALL = [BUILD, BUS, CIRCUITBREAKER, CLI, CLOUDFOUNDRY, COMMONS, CONFIG,
											 CONSUL, CONTRACT, CORE_TESTS, FUNCTION, GATEWAY, KUBERNETES, NETFLIX, OPENFEIGN,
											 RELEASE, TRAIN_DOCS, SLEUTH, STREAM, TASK, VAULT, ZOOKEEPER, SQUARE,
											 SLEUTH_OTEL, RELEASE_TOOLS]

	public static final Map<String, Project> ALL_BY_NAME = ALL.collectEntries { [it.repo, it]}

}
