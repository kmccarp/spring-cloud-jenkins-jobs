package org.springframework.jenkins.cloud.compatibility

import javaposse.jobdsl.dsl.DslFactory

import org.springframework.jenkins.cloud.common.BuildContext
import org.springframework.jenkins.cloud.common.Project
import org.springframework.jenkins.cloud.common.Projects
import org.springframework.jenkins.cloud.common.ReleaseTrain
import org.springframework.jenkins.cloud.common.SpringCloudNotification
import org.springframework.jenkins.common.job.Cron
import org.springframework.jenkins.common.job.JdkConfig
import org.springframework.jenkins.common.job.TestPublisher

/**
 * @author Marcin Grzejszczak
 */
class ProjectBootCompatibilityBuildMaker extends CompatibilityTasks implements JdkConfig, Cron, TestPublisher {

	private final DslFactory dsl
	private final ReleaseTrain train
	private final Project project
	public final BuildContext buildContext = new BuildContext()
	String cronExpr = oncePerDay()
	String suffix = CompatibilityBuildMaker.COMPATIBILITY_BUILD_DEFAULT_SUFFIX

	ProjectBootCompatibilityBuildMaker(DslFactory dsl, ReleaseTrain train, Project project) {
		this.dsl = dsl
		this.train = train
		this.project = project
		buildContext.branch = train.projectsWithBranch[project]
	}

	void build(String bootVersion) {
		Project.verify(project, buildContext)
		String jobName = "${project.getName()}-${train.codename}-${buildContext.branch}-${buildContext.jdk}-boot${bootVersion}-${suffix}"

		bootVersion = bootVersion.replace(".x", "")

		dsl.job(jobName) {
			concurrentBuild()
			parameters {
				stringParam(SPRING_BOOT_VERSION_VAR, "", 'Which version of Spring Boot should be used for the build')
				stringParam(SPRING_CLOUD_BUILD_BRANCH, train.projectsWithBranch[Projects.BUILD], 'Which branch of Spring Cloud Build should be checked out')
			}
			triggers {
				if (cronExpr) {
					cron cronExpr
				}
			}
			jdk buildContext.jdk
			scm {
				git {
					remote {
						url "https://github.com/${project.org}/${project.repo}"
						branch buildContext.branch
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
			}
			steps project.hasTests ? defaultStepsWithTestsForBoot(bootVersion) : defaultStepsForBoot(bootVersion)
			configure {
				SpringCloudNotification.cloudSlack(it as Node)
			}
			if (project.hasTests) {
				publishers {
					archiveJunit mavenJUnitResults()
				}
			}
		}
	}

}
