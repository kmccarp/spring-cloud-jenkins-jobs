package org.springframework.jenkins.cloud.common

import groovy.transform.CompileStatic

import org.springframework.jenkins.common.job.JdkConfig

@CompileStatic
class TrainDocsProject extends Project implements JdkConfig, SpringCloudJobs {
	@Override
	List<String> customBuildCommand(BuildContext context) {
		return context.upload ? ["./mvnw clean deploy -Pdocs,deploy,train-docs,spring -B -pl train-docs"] : ["./mvnw clean install -Pdocs,deploy,train-docs,spring -B -pl train-docs"]
	}

}
