package org.springframework.jenkins.cloud.common

import spock.lang.Specification

import org.springframework.jenkins.common.job.JdkConfig

class ReleaseTrainsSpec extends Specification implements JdkConfig {

	def 'define all release trains'() {
		expect:
		ReleaseTrains.ALL_BY_CODENAME.keySet().containsAll(["Tools", "Experimental", "Leyton", "Kilburn", "Jubilee", "Ilford", "Hoxton"])
		ReleaseTrains.ALL_BY_CODENAME.size() == 7
	}

	def 'define a train'() {
		expect:
		ReleaseTrains.LEYTON.version == "2023.0"
		ReleaseTrains.LEYTON.codename == "Leyton"
		ReleaseTrains.LEYTON.bootVersions.containsAll(["3.2.x"])
		ReleaseTrains.LEYTON.jdks.containsAll([jdk17()])
		ReleaseTrains.LEYTON.projectsWithBranch.size() == 18
		ReleaseTrains.KILBURN.version == "2022.0"
		ReleaseTrains.KILBURN.codename == "Kilburn"
		ReleaseTrains.KILBURN.bootVersions.containsAll(["3.0.x"])
		ReleaseTrains.KILBURN.jdks.containsAll([jdk17(), jdk20()])
		ReleaseTrains.KILBURN.projectsWithBranch.size() == 19
		ReleaseTrains.JUBILEE.version == "2021.0"
		ReleaseTrains.JUBILEE.codename == "Jubilee"
		ReleaseTrains.JUBILEE.bootVersions.containsAll(["2.6.x", "2.7.x"])
		ReleaseTrains.JUBILEE.jdks.containsAll([jdk8(), jdk11(), jdk17()])
		ReleaseTrains.JUBILEE.projectsWithBranch.size() == 22
	}

}
