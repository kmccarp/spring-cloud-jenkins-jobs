package org.springframework.jenkins.cloud.common

import groovy.transform.CompileStatic

import org.springframework.jenkins.common.job.JdkConfig

@CompileStatic
class ContractProject extends Project implements JdkConfig, SpringCloudJobs {
	private static final Map<String, String> JDKS = [
			"jdk8"     : "8.0.272.hs-adpt",
			"jdk11"    : "11.0.9.hs-adpt",
			"openjdk11": "11.0.9.hs-adpt",
			"openjdk14": "14.0.2.hs-adpt",
			"openjdk15": "15.0.1.hs-adpt",
			"openjdk16": "16.0.1.hs-adpt",
			"openjdk17": "17.0.1-zulu"
	]

	@Override
	List<String> customBuildCommand(BuildContext context) {
		boolean jdkIs17 = context.jdk == jdk17()

		List<String> cmd = []
		cmd.add("""#!/bin/bash
	rm -rf ~/.m2/repository/org/springframework/boot/spring-boot-loader-tools/
""" as String)
		cmd.add("""#!/bin/bash -x
echo "Removes old installed stubs and deploys all projects (except for docs)"
rm -rf ~/.m2/repository/com/example && rm -rf ~/.m2/repository/org/springframework/cloud/contract/verifier/stubs/ && ./mvnw clean deploy -nsu -P integration,spring -U \$MVN_LOCAL_OPTS -Dmaven.test.redirectTestOutputToFile=true -Dsurefire.runOrder=random
""" as String)
		cmd.add("""#!/bin/bash -x
					echo "Building Spring Cloud Contract docs"
					./scripts/generateDocs.sh
					${
			if (context.upload) {
				"./mvnw deploy -Pdocs,spring -pl docs -Dsdkman-java-installation.version=${JDKS.get(context.jdk) ?: JDKS.get(jdk17())} ${!jdkIs17 ? '-Djavadoc.failOnError=false -Djavadoc.failOnWarnings=false' : ''}"
			} else {
				"./mvnw clean install -U -Pintegration,spring -Dsdkman-java-installation.version=${JDKS.get(context.jdk) ?: JDKS.get(jdk17())} ${!jdkIs17 ? '-Djavadoc.failOnError=false -Djavadoc.failOnWarnings=false' : ''}"
			}
		}
        """ as String)
		return cmd
	}

	@Override
	boolean publishTests(BuildContext context) {
		return hasTests && context.jdk != jdk17()
	}
}
