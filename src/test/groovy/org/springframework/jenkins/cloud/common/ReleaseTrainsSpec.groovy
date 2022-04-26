package org.springframework.jenkins.cloud.common

import spock.lang.Specification

import org.springframework.jenkins.common.job.JdkConfig

class ReleaseTrainsSpec extends Specification implements JdkConfig {

	def 'define all release trains'() {
		expect:
		ReleaseTrains.ALL_BY_CODENAME.keySet().containsAll(["Tools", "Experimental", "Kilburn", "Jubilee", "Ilford", "Hoxton"])
		ReleaseTrains.ALL_BY_CODENAME.size() == 6
	}

	def 'define a train'() {
		expect:
		ReleaseTrains.KILBURN.version == "2022.0"
		ReleaseTrains.KILBURN.codename == "Kilburn"
		ReleaseTrains.KILBURN.bootVersions.containsAll(["3.0.x"])
		ReleaseTrains.KILBURN.jdks.containsAll([jdk17()])
		ReleaseTrains.KILBURN.projectsWithBranch.size() == 19
		ReleaseTrains.JUBILEE.version == "2021.0"
		ReleaseTrains.JUBILEE.codename == "Jubilee"
		ReleaseTrains.JUBILEE.bootVersions.containsAll(["2.6.x", "2.7.x"])
		ReleaseTrains.JUBILEE.jdks.containsAll([jdk8(), jdk11(), jdk17()])
		ReleaseTrains.JUBILEE.projectsWithBranch.size() == 22
	}

}
