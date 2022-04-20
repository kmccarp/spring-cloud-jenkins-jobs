package org.springframework.jenkins.cloud.e2e

import groovy.transform.CompileStatic
import javaposse.jobdsl.dsl.DslFactory

import org.springframework.jenkins.cloud.common.AllCloudJobs

/**
 * @author Marcin Grzejszczak
 */
@CompileStatic
class BreweryEndToEndBuildMaker extends EndToEndBuildMaker {
	private final String repoName = 'brewery'

	private static final Map<String, String> RELEASE_TRAIN_TO_PROJECT_BRANCH = [
			"2021.0"   : "2021.0.x",
			"2020.0"   : "2020.0.x",
	]

	BreweryEndToEndBuildMaker(DslFactory dsl) {
		super(dsl, 'spring-cloud-samples')
	}

	void build(String releaseTrainName) {
		this.build(releaseTrainName, releaseTrainName)
	}

	void build(String prefix, String releaseTrainName) {
		buildWithSwitches(prefix, releaseTrainName, defaultSwitches(releaseTrainName))
	}

	protected void buildWithSwitches(String prefix, String releaseTrainName, String defaultSwitches) {
		String branch = branchName(releaseTrainName)
		setBranchName(branch)
		super.build("$prefix-sleuth", repoName(), "runAcceptanceTests.sh -t SLEUTH $defaultSwitches", oncePerDay())
		super.build("$prefix-eureka", repoName(), "runAcceptanceTests.sh -t EUREKA $defaultSwitches", oncePerDay())
		super.build("$prefix-consul", repoName(), "runAcceptanceTests.sh -t CONSUL $defaultSwitches", oncePerDay())
		super.build("$prefix-zookeeper", repoName(), "runAcceptanceTests.sh -t ZOOKEEPER $defaultSwitches", oncePerDay())
		if (!prefix.toLowerCase().startsWith("hoxton")) {
			super.build("$prefix-wavefront", repoName(), "runAcceptanceTests.sh -t WAVEFRONT $defaultSwitches", oncePerDay())
		}
	}

	protected String repoName() {
		return this.repoName
	}

	protected static String branchName(String releaseTrainName) {
		String[] split = releaseTrainName.toLowerCase().split("\\.")
		String splitReleaseTrain = split.length == 3 ? split[0] + "." + split[1] : releaseTrainName.toLowerCase()
		return RELEASE_TRAIN_TO_PROJECT_BRANCH.getOrDefault(splitReleaseTrain, "main")
	}

	protected String defaultSwitches(String releaseTrainName) {
		String[] split = releaseTrainName.toLowerCase().split("\\.")
		// 2020.0.0 -> 2020.0
		println "Release train name [${releaseTrainName}]"
		String splitReleaseTrain = split.length == 3 ? split[0] + "." + split[1] : releaseTrainName.toLowerCase()
		String boot = AllCloudJobs.RELEASE_TRAIN_TO_BOOT_VERSION_MINOR.get(splitReleaseTrain)
		if (boot == null) {
			throw new IllegalStateException("No Boot version found for train with release name [${releaseTrainName}]")
		}
		String releaseTrain = releaseTrainName.capitalize()
		String bootVersion = boot.split("\\.").length == 3 ? boot : "\$( bootVersion \"${boot}\" )"
		String additionalSwitches = "--killattheend -v \"\$( springCloudVersion \"${releaseTrain}\" )\" --branch ${branchName} -r -b \"${bootVersion}\""
		println "Found additional switches [${additionalSwitches}]"
		return additionalSwitches
	}
}
