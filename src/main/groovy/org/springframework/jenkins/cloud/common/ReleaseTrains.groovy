package org.springframework.jenkins.cloud.common


import groovy.transform.CompileStatic

import static org.springframework.jenkins.cloud.common.Projects.BUILD
import static org.springframework.jenkins.cloud.common.Projects.BUS
import static org.springframework.jenkins.cloud.common.Projects.CIRCUITBREAKER
import static org.springframework.jenkins.cloud.common.Projects.CLI
import static org.springframework.jenkins.cloud.common.Projects.CLOUDFOUNDRY
import static org.springframework.jenkins.cloud.common.Projects.COMMONS
import static org.springframework.jenkins.cloud.common.Projects.CONFIG
import static org.springframework.jenkins.cloud.common.Projects.CONSUL
import static org.springframework.jenkins.cloud.common.Projects.CONTRACT
import static org.springframework.jenkins.cloud.common.Projects.CORE_TESTS
import static org.springframework.jenkins.cloud.common.Projects.FUNCTION
import static org.springframework.jenkins.cloud.common.Projects.GATEWAY
import static org.springframework.jenkins.cloud.common.Projects.KUBERNETES
import static org.springframework.jenkins.cloud.common.Projects.NETFLIX
import static org.springframework.jenkins.cloud.common.Projects.OPENFEIGN
import static org.springframework.jenkins.cloud.common.Projects.RELEASE
import static org.springframework.jenkins.cloud.common.Projects.RELEASE_TOOLS
import static org.springframework.jenkins.cloud.common.Projects.SLEUTH
import static org.springframework.jenkins.cloud.common.Projects.SLEUTH_OTEL
import static org.springframework.jenkins.cloud.common.Projects.SQUARE
import static org.springframework.jenkins.cloud.common.Projects.STREAM
import static org.springframework.jenkins.cloud.common.Projects.TASK
import static org.springframework.jenkins.cloud.common.Projects.TRAIN_DOCS
import static org.springframework.jenkins.cloud.common.Projects.VAULT
import static org.springframework.jenkins.cloud.common.Projects.ZOOKEEPER

/**
 * TBD
 *
 * @author Spencer Gibb
 */
@CompileStatic
class ReleaseTrains {

	static CloudJdkConfig jdks = new CloudJdkConfig();

	public static final ReleaseTrain KILBURN = new ReleaseTrain(
			version: "2022.0",
			codename: "Kilburn",
			jdks: [jdks.jdk17()],
			bootVersions: ["3.0.x"],
			projectsWithBranch: [
					(BUILD)         : "main",
					(BUS)           : "main",
					(CIRCUITBREAKER): "main",
					(COMMONS)       : "main",
					(CONFIG)        : "main",
					(CONSUL)        : "main",
					(CONTRACT)      : "main",
					(CORE_TESTS)    : "main",
					(GATEWAY)       : "main",
					(FUNCTION)      : "main",
					(KUBERNETES)    : "main",
					(NETFLIX)       : "main",
					(OPENFEIGN)     : "main",
					(RELEASE)       : "main",
					(STREAM)        : "main",
					(TRAIN_DOCS)    : "main",
					(TASK)          : "main",
					(VAULT)         : "main",
					(ZOOKEEPER)     : "main",
			]
	)
	public static final ReleaseTrain JUBILEE = new ReleaseTrain(
			version: "2021.0",
			codename: "Jubilee",
			bootVersions: ["2.6.x", "2.7.x"],
			jdks: [jdks.jdk8(), jdks.jdk11(), jdks.jdk17()],
			projectsWithBranch: [
					(BUILD)         : "3.1.x",
					(BUS)           : "3.1.x",
					(CIRCUITBREAKER): "2.1.x",
					(CLI)           : "3.1.x",
					(CLOUDFOUNDRY)  : "3.1.x",
					(COMMONS)       : "3.1.x",
					(CONFIG)        : "3.1.x",
					(CONSUL)        : "3.1.x",
					(CONTRACT)      : "3.1.x",
					(CORE_TESTS)    : "2021.0.x",
					(FUNCTION)      : "3.2.x",
					(GATEWAY)       : "3.1.x",
					(KUBERNETES)    : "2.1.x",
					(NETFLIX)       : "3.1.x",
					(OPENFEIGN)     : "3.1.x",
					(RELEASE)       : "2021.0.x",
					(TRAIN_DOCS)    : "2021.0.x",
					(SLEUTH)        : "3.1.x",
					(STREAM)        : "3.2.x",
					(TASK)          : "2.4.x",
					(VAULT)         : "3.1.x",
					(ZOOKEEPER)     : "3.1.x",
			]
	)
	public static final ReleaseTrain ILFORD = new ReleaseTrain(
			version: "2020.0",
			codename: "Ilford",
			bootVersions: ["2.4.x", "2.5.x"],
			jdks: [jdks.jdk8(), jdks.jdk11(), jdks.jdk17()],
			projectsWithBranch: [
					(BUILD)         : "3.0.x",
					(BUS)           : "3.0.x",
					(CIRCUITBREAKER): "2.0.x",
					(CLI)           : "3.0.x",
					(CLOUDFOUNDRY)  : "3.0.x",
					(COMMONS)       : "3.0.x",
					(CONFIG)        : "3.0.x",
					(CONSUL)        : "3.0.x",
					(CONTRACT)      : "3.0.x", // jdk17 not supported
					(CORE_TESTS)    : "2020.0.x",
					(FUNCTION)      : "3.1.x",
					(GATEWAY)       : "3.0.x",
					(KUBERNETES)    : "2.0.x",
					(NETFLIX)       : "3.0.x",
					(OPENFEIGN)     : "3.0.x",
					(RELEASE)       : "2020.0.x",
					(TRAIN_DOCS)    : "2020.0.x",
					(SLEUTH)        : "3.0.x",
					(TASK)          : "2.3.x",
					(VAULT)         : "3.0.x",
					(ZOOKEEPER)     : "3.0.x",
			]
	)
	public static final ReleaseTrain HOXTON = new ReleaseTrain(
			version: "Hoxton",
			codename: "Hoxton",
			active: false,
			jdks: [jdks.jdk8()],
			bootVersions: ["2.3.x"],
			projectsWithBranch: [
					(BUILD)         : "2.3.x",
					(BUS)           : "2.2.x",
					(CIRCUITBREAKER): "1.0.x",
					(CLI)           : "2.2.x",
					(CLOUDFOUNDRY)  : "2.2.x",
					(COMMONS)       : "2.2.x",
					(CONFIG)        : "2.2.x",
					(CONSUL)        : "2.2.x",
					(CONTRACT)      : "2.2.x",
					(CORE_TESTS)    : "Hoxton",
					(GATEWAY)       : "2.2.x",
					(KUBERNETES)    : "1.1.x",
					(NETFLIX)       : "2.2.x",
					(OPENFEIGN)     : "2.2.x",
					(RELEASE)       : "Hoxton",
					(TRAIN_DOCS)    : "Hoxton",
					(SLEUTH)        : "2.2.x",
					(VAULT)         : "2.2.x",
					(ZOOKEEPER)     : "2.2.x",
			]
	)
	public static final ReleaseTrain EXPERIMENTAL = new ReleaseTrain(
			version: "Experimental",
			codename: "Experimental",
			metaRelease: false,
			jdks: [jdks.jdk8()],
			bootVersions: ["2.6.x"],
			projectsWithBranch: [
					(SQUARE)     : "main",
					(SLEUTH_OTEL): "main",
			]
	)
	public static final ReleaseTrain TOOLS = new ReleaseTrain(
			version: "Tools",
			codename: "Tools",
			metaRelease: false,
			jdks: [jdks.jdk8(), jdks.jdk17()],
			bootVersions: ["2.6.x"],
			projectsWithBranch: [
					(RELEASE_TOOLS): "main",
			]
	)

	public static final List<ReleaseTrain> ALL = [TOOLS, EXPERIMENTAL, HOXTON, ILFORD, JUBILEE, KILBURN]

	public static final Map<String, ReleaseTrain> ALL_BY_CODENAME = ALL.collectEntries { [it.codename, it] }

	static List<ReleaseTrain> allActive() {
		ALL.findAll { it.active }
	}

}
