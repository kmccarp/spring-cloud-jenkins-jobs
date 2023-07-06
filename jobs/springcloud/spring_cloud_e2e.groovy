package springcloud

import javaposse.jobdsl.dsl.DslFactory

import org.springframework.jenkins.cloud.ci.SleuthBenchmarksBuildMaker
import org.springframework.jenkins.cloud.e2e.BreweryEndToEndBuildMaker
import org.springframework.jenkins.cloud.e2e.EndToEndBuildMaker
import org.springframework.jenkins.cloud.e2e.NetflixEndToEndBuildMaker
import org.springframework.jenkins.cloud.e2e.SleuthEndToEndBuildMaker
import org.springframework.jenkins.cloud.e2e.SpringCloudContractSamplesEndToEndBuilder
import org.springframework.jenkins.cloud.e2e.SpringCloudSamplesEndToEndBuildMaker

DslFactory dsl = this

// SLEUTH
new SleuthBenchmarksBuildMaker(dsl).buildSleuth()
new SleuthEndToEndBuildMaker(dsl).with {
	buildSleuth(oncePerDay())
}

// CONTRACT
new SpringCloudContractSamplesEndToEndBuilder().buildAll(dsl)

// Bring back once there's more priority on contract
//new SpringCloudSamplesEndToEndBuildMaker(dsl).with {
//	buildWithMavenTests("the-legacy-app", mainBranch(), oncePerDay())
//	buildWithMavenTests("sc-contract-car-rental", mainBranch(), oncePerDay())
//}

new NetflixEndToEndBuildMaker(dsl).with {
	build(oncePerDay())
}

// new LatestJdkBreweryEndToEndBuildMaker(dsl).build()
["2020.0", "2021.0", "2022.0"].each {
	new BreweryEndToEndBuildMaker(dsl).build(it)
}

new EndToEndBuildMaker(dsl, "spring-cloud-samples").with {
	buildWithoutTests("eureka-release-train-interop", oncePerDay())
}
