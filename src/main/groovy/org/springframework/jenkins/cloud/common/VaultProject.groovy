package org.springframework.jenkins.cloud.common

import groovy.transform.CompileStatic

import org.springframework.jenkins.common.job.JdkConfig

@CompileStatic
class VaultProject extends Project implements HashicorpTrait, JdkConfig, SpringCloudJobs {
	@Override
	List<String> customBuildCommand(BuildContext context) {
		List<String> cmd = []
		cmd.add("""\
        ${antiPermgenAndJava7TlsHack(context.jdk)}
        ${preVaultShell()}
        trap "{ ${postVaultShell()} }" EXIT
        ${context.upload ? cleanDeployWithDocs() : cleanInstallWithoutDocs()}
        """ as String)
		return cmd
	}

	@Override
	String labelExpression(BuildContext context) {
		if (context.jdk == jdk8() || context.jdk == jdk11()) {
			return "linux&&jdk8"
		} else {
			return "linux&&jdk17"
		}
	}

	protected String antiPermgenAndJava7TlsHack(String jdkVersion) {
		if (jdkVersion == jdk8() || jdkVersion == jdk11()) {
			return '#!/bin/bash -x\nexport MAVEN_OPTS="-Xms256M -Xmx1024M -Dsun.rmi.dgc.client.gcInterval=3600000 -Dsun.rmi.dgc.server.gcInterval=3600000 -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=4096M -Dhttps.protocols=TLSv1.2"'
		}
		return '#!/bin/bash -x\nexport MAVEN_OPTS="-Xms256M -Xmx1024M -Dsun.rmi.dgc.client.gcInterval=3600000 -Dsun.rmi.dgc.server.gcInterval=3600000 -Dhttps.protocols=TLSv1.2"'
	}
}
