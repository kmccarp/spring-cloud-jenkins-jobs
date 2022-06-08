package org.springframework.jenkins.cloud.common

import groovy.transform.CompileStatic

import org.springframework.jenkins.common.job.JdkConfig

@CompileStatic
class KubernetesProject extends Project implements JdkConfig, SpringCloudJobs {
	@Override
	List<String> customBuildCommand(BuildContext context) {
		if(context.branch == "2.0.x" || context.branch == "2.1.x") {
			List<String> cmd = [
					"./mvnw clean install -Pspring -B -U",
					"""#!/bin/bash
	cd spring-cloud-kubernetes-integration-tests
    ./run.sh
""",
					context.upload ? "./mvnw deploy -Pdocs,deploy,spring -B -U -DskipTests=true" : "./mvnw install -Pdeploy,spring -B -U -DskipTests=true",
					"""./mvnw dockerfile:push -pl :spring-cloud-kubernetes-configuration-watcher -Pdockerpush""",
			]
			if (context.branch != "2.0.x") {
				cmd.add "./mvnw dockerfile:push -pl :spring-cloud-kubernetes-configserver -Pdockerpush"
				cmd.add "./mvnw dockerfile:push -pl :spring-cloud-kubernetes-discoveryserver -Pdockerpush"
			}
			return cmd
		}
		else {
			List<String> cmd = [
					"./mvnw clean install -Pspring -B -U",
					context.upload ? "./mvnw deploy -Pdocs,deploy,spring -B -U -DskipTests=true" : "./mvnw install -Pdeploy,spring -B -U -DskipTests=true",
					"""./mvnw dockerfile:push -pl :spring-cloud-kubernetes-configuration-watcher -Pdockerpush""",
					"./mvnw dockerfile:push -pl :spring-cloud-kubernetes-configserver -Pdockerpush",
					"./mvnw dockerfile:push -pl :spring-cloud-kubernetes-discoveryserver -Pdockerpush"
			]
			return cmd
		}
	}

}
