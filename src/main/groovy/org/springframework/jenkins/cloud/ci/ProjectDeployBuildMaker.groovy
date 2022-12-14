package org.springframework.jenkins.cloud.ci

import javaposse.jobdsl.dsl.DslFactory

import org.springframework.jenkins.cloud.common.BuildContext
import org.springframework.jenkins.cloud.common.CloudCron
import org.springframework.jenkins.cloud.common.Project
import org.springframework.jenkins.cloud.common.ReleaseTrain
import org.springframework.jenkins.cloud.common.SpringCloudJobs
import org.springframework.jenkins.cloud.common.SpringCloudNotification
import org.springframework.jenkins.common.job.JdkConfig
import org.springframework.jenkins.common.job.Maven
import org.springframework.jenkins.common.job.TestPublisher

/**
 * @author Marcin Grzejszczak
 */
class ProjectDeployBuildMaker implements JdkConfig, TestPublisher, CloudCron,
		SpringCloudJobs, Maven {
	private final DslFactory dsl
	private final ReleaseTrain train
	private final Project project
	public final BuildContext buildContext = new BuildContext()
	Closure<Node> slack = { Node node -> SpringCloudNotification.cloudSlack(node) }


	ProjectDeployBuildMaker(DslFactory dsl, ReleaseTrain train, Project project) {
		this.dsl = dsl
		this.train = train
		this.project = project
		buildContext.branch = train.projectsWithBranch[project]
	}

	void deploy() {
		Project.verify(project, buildContext)
		String jobName = "${project.getName()}-${train.codename}-${buildContext.branch}-${buildContext.jdk}-ci"

		dsl.job(jobName) {
			triggers {
				cron cronValue
				if (onGithubPush) {
					githubPush()
				}
			}
			parameters {
				stringParam(branchVarName(), buildContext.branch ?: mainBranch(), 'Which branch should be built')
			}
			jdk buildContext.jdk
			label(project.labelExpression(buildContext))
			scm {
				git {
					remote {
						url "https://github.com/${project.org}/${project.repo}"
					}
					branch "\$${branchVarName()}"
					extensions {
						wipeOutWorkspace()
						localBranch("**")
					}
				}
			}
			wrappers {
				timestamps()
				colorizeOutput()
				maskPasswords()
				credentialsBinding {
					usernamePassword(dockerhubUserNameEnvVar(),
							dockerhubPasswordEnvVar(),
							dockerhubCredentialId())
					usernamePassword(githubRepoUserNameEnvVar(),
							githubRepoPasswordEnvVar(),
							githubUserCredentialId())
					usernamePassword(buildUserNameEnvVar(),
							buildPasswordEnvVar(),
							buildCredentialId())
					string(githubToken(), githubTokenCredId())
					if (project.buildSystem == Project.BuildSystem.GRADLE
							|| project.buildSystem == Project.BuildSystem.BOTH) {
						string(gradlePublishKeyEnvVar(), gradlePublishKeySecretId())
						string(gradlePublishSecretEnvVar(), gradlePublishSecretSecretId())
					}
				}
				environmentVariables {
					env('BRANCH', buildContext.branch)
				}
				timeout {
					noActivity(600)
					failBuild()
					writeDescription('Build failed due to timeout after {0} minutes of inactivity')
				}
				configFiles {
					file(mavenSettingsId()) {
						targetLocation('${HOME}/.m2/settings.xml')
					}
				}
			}
			steps {
				shell(loginToDocker())
				shell(removeMavenInstallation())
				shell(stopRunningDocker())
				maven {
					mavenInstallation(maven33())
					goals('--version')
				}
				buildCommand().each {
					shell(it)
				}
			}
			configure {
				slack.call(it as Node)
			}
			if (project.publishTests(buildContext) || project.notificationEmails) {
				publishers {
					archiveJunit mavenJUnitResults()
					if (project.buildSystem == Project.BuildSystem.GRADLE
							|| project.buildSystem == Project.BuildSystem.BOTH) {
						archiveJunit gradleJUnitResults()
					}
					if (project.notificationEmails) {
						mailer(project.notificationEmails, true, true)
					}
				}
			}
		}
	}

	List<String> buildCommand() {
		List<String> customBuildCommand = project.customBuildCommand(buildContext)
		if (customBuildCommand) {
			return customBuildCommand
		}
		return this.buildContext.upload ? [cleanDeployWithDocs()] : [cleanInstallWithoutDocs()]
	}

}
