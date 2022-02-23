package springcloud

import javaposse.jobdsl.dsl.DslFactory

import org.springframework.jenkins.cloud.ci.SleuthBenchmarksBuildMaker
import org.springframework.jenkins.cloud.e2e.BreweryEndToEndBuildMaker
import org.springframework.jenkins.cloud.e2e.EndToEndBuildMaker
import org.springframework.jenkins.cloud.e2e.JdkBreweryEndToEndBuildMaker
import org.springframework.jenkins.cloud.e2e.NetflixEndToEndBuildMaker
import org.springframework.jenkins.cloud.e2e.SleuthEndToEndBuildMaker
import org.springframework.jenkins.cloud.e2e.SpringCloudContractSamplesEndToEndBuilder
import org.springframework.jenkins.cloud.e2e.SpringCloudSamplesEndToEndBuildMaker

DslFactory dsl = this

// SLEUTH
new SleuthBenchmarksBuildMaker(dsl).buildSleuth()
new SpringCloudSamplesEndToEndBuildMaker(dsl).with {
	buildWithMavenTests("spring-cloud-sleuth-samples", mainBranch(), oncePerDay())
	buildWithMavenTests("sleuth-issues", mainBranch(), oncePerDay())
	buildWithMavenTests("sleuth-documentation-apps", mainBranch(), oncePerDay())
}
new SleuthEndToEndBuildMaker(dsl).with {
	buildSleuth(oncePerDay())
}

// CONTRACT
new SpringCloudContractSamplesEndToEndBuilder().with {
	it.withJdk(jdk11())
}.buildAll(dsl)
new SpringCloudContractSamplesEndToEndBuilder().with {
	it.withJdk(jdk17())
}.buildAll(dsl)

new SpringCloudSamplesEndToEndBuildMaker(dsl).with {
	buildWithMavenTests("the-legacy-app", mainBranch(), oncePerDay())
	buildWithMavenTests("sc-contract-car-rental", mainBranch(), oncePerDay())
}

new NetflixEndToEndBuildMaker(dsl).with {
	build(oncePerDay())
}
new JdkBreweryEndToEndBuildMaker(dsl).with { withJdk(jdk11()).build() }
new JdkBreweryEndToEndBuildMaker(dsl).with { withJdk(jdk17()).build() }

// new LatestJdkBreweryEndToEndBuildMaker(dsl).build()
["2020.0", "2021.0", "2022.0"].each {
	new BreweryEndToEndBuildMaker(dsl).build(it)
}

new EndToEndBuildMaker(dsl, "spring-cloud-samples").with {
	buildWithoutTests("eureka-release-train-interop", oncePerDay())
}
