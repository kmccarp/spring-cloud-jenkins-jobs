package org.springframework.jenkins.cloud.common

import spock.lang.Specification
import spock.lang.Unroll

class AllCloudJobsSpec extends Specification {

	@Unroll
	def 'get correct boot version [#boot] for cloud release train [#cloud]'() {
		expect:
		AllCloudJobs.bootForReleaseTrain(cloud).startsWith(boot)

		where:
		cloud               || boot
		null                || "3.0"
		"null"              || "3.0"
		"2022.0.0"          || "3.0"
		"2022.0.0-SNAPSHOT" || "3.0"
		"2021.0.0"          || "2.6"
		"2021.0.0-SNAPSHOT" || "2.6"
		"2020.0.0"          || "2.5"
		"2020.0.0-SNAPSHOT" || "2.5"
		"Hoxton.RELEASE"    || "2.3"
		"Greenwich.SR1"     || "2.1"
	}

}
