package org.springframework.jenkins.cloud.compatibility

import javaposse.jobdsl.dsl.DslFactory

import org.springframework.jenkins.cloud.common.HashicorpTrait
import org.springframework.jenkins.cloud.common.SpringCloudNotification

/**
 * @author Marcin Grzejszczak
 */
class VaultCompatibilityBuildMaker extends CompatibilityBuildMaker implements HashicorpTrait {

	VaultCompatibilityBuildMaker(DslFactory dsl) {
		super(dsl)
	}

	VaultCompatibilityBuildMaker(DslFactory dsl, String suffix) {
		super(dsl, suffix)
	}

	VaultCompatibilityBuildMaker(DslFactory dsl, String suffix, String organization) {
		super(dsl, suffix, organization)
	}

	protected void buildWithTests(String projectName, String repoName, String branchName, String cronExpr, boolean checkTests,
								  boolean parametrizedBoot = true) {
		String prefixedProjectName = prefixJob(projectName)
		dsl.job("${prefixedProjectName}-${suffix}") {
			concurrentBuild()
			if (parametrizedBoot) {
				parameters {
					stringParam(SPRING_BOOT_VERSION_VAR, DEFAULT_BOOT_MINOR_VERSION, 'Which version of Spring Boot should be used for the build')
					stringParam(SPRING_CLOUD_BUILD_BRANCH, DEFAULT_BUILD_BRANCH, 'Which branch of Spring Cloud Build should be checked out')
				}
			}
			triggers {
				if (cronExpr) {
					cron cronExpr
				}
			}
			jdk jdk8()
			scm {
				git {
					remote {
						url "https://github.com/${organization}/${repoName}"
						branch branchName
					}
					extensions {
						wipeOutWorkspace()
						localBranch("**")
					}
				}
			}
			wrappers {
				credentialsBinding {
					usernamePassword(dockerhubUserNameEnvVar(),
							dockerhubPasswordEnvVar(),
							dockerhubCredentialId())
					usernamePassword(buildUserNameEnvVar(),
							buildPasswordEnvVar(),
							buildCredentialId())
				}
			}
			steps {
				shell(loginToDocker())
				maven {
					mavenInstallation(maven33())
					goals('--version')
				}
				// #!/bin/bash must be first in the line without tabs
				shell("""${antiPermgenAndJava7TlsHack(branchName)}
						${preVaultShell()}
						trap "{ ${postVaultShell()} }" EXIT
						${checkTests ? runTestsForBoot() : compileProductionForBoot()}
					""")
			}
			configure {
				SpringCloudNotification.cloudSlack(it as Node)
			}
			if (checkTests) {
				publishers {
					archiveJunit mavenJUnitResults()
				}
			}
		}
	}

	protected String antiPermgenAndJava7TlsHack(String branchName) {
		println "branchName = ${branchName}"
		if (branchName == "main") {
			return '#!/bin/bash -x\nexport MAVEN_OPTS="-Xms256M -Xmx1024M -Dsun.rmi.dgc.client.gcInterval=3600000 -Dsun.rmi.dgc.server.gcInterval=3600000 -Dhttps.protocols=TLSv1.2"'
		}
		return '#!/bin/bash -x\nexport MAVEN_OPTS="-Xms256M -Xmx1024M -Dsun.rmi.dgc.client.gcInterval=3600000 -Dsun.rmi.dgc.server.gcInterval=3600000 -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=4096M -Dhttps.protocols=TLSv1.2"'
	}

}
