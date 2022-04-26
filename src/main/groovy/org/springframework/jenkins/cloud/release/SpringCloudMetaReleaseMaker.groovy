package org.springframework.jenkins.cloud.release

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.helpers.wrapper.CredentialsBindingContext
import javaposse.jobdsl.dsl.jobs.FreeStyleJob

import org.springframework.jenkins.cloud.common.Releaser
import org.springframework.jenkins.cloud.common.SpringCloudJobs
import org.springframework.jenkins.common.job.JdkConfig
import org.springframework.jenkins.common.job.TestPublisher

/**
 * @author Marcin Grzejszczak
 */
class SpringCloudMetaReleaseMaker implements JdkConfig, TestPublisher,
		SpringCloudJobs, Releaser {
	protected static final String RELEASE_VERSION_PARAM = "RELEASE_VERSION"
	protected static final String RELEASER_BRANCH_PARAM = "RELEASER_BRANCH"
	protected static final String RELEASER_CONFIG_URL_PARAM = "RELEASER_CONFIG_URL"
	protected static final String RELEASER_CONFIG_BRANCH_PARAM = "RELEASER_CONFIG_BRANCH"
	protected static final String START_FROM_PARAM = "START_FROM"
	protected static final String TASK_NAMES_PARAM = "TASK_NAMES"
	protected static final String DRY_RUN_PARAM = "DRY_RUN"
	protected static final String RELEASER_POM_THIS_TRAIN_BOM = 'RELEASER_POM_THIS_TRAIN'
	protected static final String RELEASER_SAGAN_UPDATE_VAR = 'RELEASER_SAGAN_UPDATE'
	protected static final String RELEASER_GIT_UPDATE_DOCUMENTATION_REPOS_VAR = 'RELEASER_GIT_UPDATE_DOCUMENTATION_REPOS'
	protected static final String RELEASER_GIT_UPDATE_SPRING_PROJECTS_VAR = 'RELEASER_GIT_UPDATE_SPRING_PROJECTS'
	protected static final String RELEASER_GIT_UPDATE_RELEASE_TRAIN_WIKI_VAR = 'RELEASER_GIT_UPDATE_RELEASE_TRAIN_WIKI'
	protected static final String RELEASER_GIT_RUN_UPDATED_SAMPLES_VAR = 'RELEASER_GIT_RUN_UPDATED_SAMPLES'
	protected static final String RELEASER_GIT_UPDATE_ALL_TEST_SAMPLES_VAR = 'RELEASER_GIT_UPDATE_ALL_TEST_SAMPLES'
	protected static final String RELEASER_GIT_UPDATE_RELEASE_TRAIN_DOCS_VAR = 'RELEASER_GIT_UPDATE_RELEASE_TRAIN_DOCS'
	protected static final String RELEASER_GIT_UPDATE_SPRING_GUIDES_VAR = 'RELEASER_GIT_UPDATE_SPRING_GUIDES'
	protected static final String RELEASER_GIT_UPDATE_START_SPRING_IO_VAR = 'RELEASER_GIT_UPDATE_START_SPRING_IO'
	protected static final String RELEASER_GIT_UPDATE_GITHUB_MILESTONES_VAR = 'RELEASER_GIT_UPDATE_GITHUB_MILESTONES'
	protected static final String RELEASER_RELEASE_TRAIN_PROJECT_NAME_VAR = 'RELEASER_META_RELEASE_RELEASE_TRAIN_PROJECT_NAME'
	protected static final String RELEASER_META_RELEASE_GIT_ORG_URL_VAR = "RELEASER_META_RELEASE_GIT_ORG_URL"
	protected static final String RELEASER_RELEASE_TRAIN_DEPENDENCY_NAMES_VAR = 'RELEASER_META_RELEASE_RELEASE_TRAIN_DEPENDENCY_NAMES'
	protected static final String RELEASER_GIT_RELEASE_TRAIN_BOM_URL_VAR = 'RELEASER_GIT_RELEASE_TRAIN_BOM'
	protected static final String RELEASER_PROJECTS_TO_SKIP_VAR = 'RELEASER_PROJECTS_TO_SKIP'
	protected static final String RELEASER_POST_RELEASE_ONLY_VAR = 'RELEASER_POST_RELEASE_ONLY'

	private final DslFactory dsl

	SpringCloudMetaReleaseMaker(DslFactory dsl) {
		this.dsl = dsl
	}

	void release(String jobName, ReleaserOptions options = new ReleaserOptions()) {
		release(jobName, jdk8(), options)
	}

	void release(String jobName, String jdkVersion, ReleaserOptions options = new ReleaserOptions()) {
		dsl.job(jobName) {
			additionalConfiguration(delegate as FreeStyleJob)
			parameters {
				stringParam(RELEASE_VERSION_PARAM, "", "Name of the release (e.g. Hoxton.RELEASE). Will correspond to the properties file (e.g. hoxton_release.properties)")
				stringParam(RELEASER_BRANCH_PARAM, options.releaserBranch, "Branch for the releaser code")
				stringParam(RELEASER_CONFIG_URL_PARAM, options.releaserConfigUrl, "Root of the URL where the RAW version of the configuration file is present")
				stringParam(RELEASER_CONFIG_BRANCH_PARAM, options.releaserConfigBranch, "Branch, where the RAW version of the configuration file is present")
				stringParam(RELEASER_META_RELEASE_GIT_ORG_URL_VAR, options.gitOrgUrl, 'URL of the organization from which projects can be cloned')
				stringParam(START_FROM_PARAM, "", "Project name from which you'd like to start the meta-release process. E.g. spring-cloud-sleuth")
				stringParam(TASK_NAMES_PARAM, "", "Comma separated list of project names. E.g. spring-cloud-sleuth,spring-cloud-contract")
				booleanParam(DRY_RUN_PARAM, options.dryRun, 'If true then will run meta-release in a dry run mode')
				booleanParam(RELEASER_SAGAN_UPDATE_VAR, options.updateSagan, 'If true then will update documentation repository with the current URL')
				booleanParam(RELEASER_GIT_UPDATE_DOCUMENTATION_REPOS_VAR, options.updateDocumentationRepos, 'If true then will update documentation repository with the current URL')
				booleanParam(RELEASER_GIT_UPDATE_SPRING_PROJECTS_VAR, options.updateSpringProjects, 'If true then will update Project Sagan with the current release train values')
				booleanParam(RELEASER_GIT_UPDATE_RELEASE_TRAIN_WIKI_VAR, options.updateReleaseTrainWiki, 'If true then will update the release train wiki page with the current release train values')
				booleanParam(RELEASER_GIT_RUN_UPDATED_SAMPLES_VAR, options.runUpdatedSamples, 'If true then will update samples and run the the build')
				booleanParam(RELEASER_GIT_UPDATE_ALL_TEST_SAMPLES_VAR, options.updateAllTestSamples, ' If true then will update samples with bumped snapshots after release')
				booleanParam(RELEASER_GIT_UPDATE_RELEASE_TRAIN_DOCS_VAR, options.updateReleaseTrainDocs, ' If true then will update the release train documentation project and run the generation')
				booleanParam(RELEASER_GIT_UPDATE_SPRING_GUIDES_VAR, options.updateSpringGuides, ' If true then will update the release train documentation project and run the generation')
				booleanParam(RELEASER_GIT_UPDATE_START_SPRING_IO_VAR, options.updateStartSpringIo, ' If true then will update start.spring.io')
				booleanParam(RELEASER_GIT_UPDATE_GITHUB_MILESTONES_VAR, options.updateGithubMilestones, ' If true then will update milestones on Github for each project')
				booleanParam(RELEASER_POST_RELEASE_ONLY_VAR, false, 'If set to true will run only post release tasks')
				stringParam(RELEASER_RELEASE_TRAIN_PROJECT_NAME_VAR, options.releaseTrainProjectName, 'Name of the project that represents the BOM of the release train')
				stringParam(RELEASER_RELEASE_TRAIN_DEPENDENCY_NAMES_VAR, options.releaseTrainDependencyNames.join(","), 'All the names of dependencies that should be updated with the release train project version')
				stringParam(RELEASER_GIT_RELEASE_TRAIN_BOM_URL_VAR, options.releaseTrainBomUrl, 'Subfolder of the pom that contains the versions for the release train')
				stringParam(RELEASER_POM_THIS_TRAIN_BOM, options.releaseThisTrainBom, 'URL to a project containing a BOM. Defaults to Spring Cloud Release Git repository')
				stringParam(RELEASER_PROJECTS_TO_SKIP_VAR, options.projectsToSkip, 'Names of projects to skip deployment for meta-release')
			}
			jdk jdkVersion
			scm {
				git {
					remote {
						url "https://github.com/spring-cloud/spring-cloud-release-tools"
						branch('${' + RELEASER_BRANCH_PARAM + '}')
					}
					extensions {
						wipeOutWorkspace()
						localBranch("**")
					}
				}
			}
			label(releaserLabel())
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
					file(gpgSecRing(), "spring-signing-secring.gpg")
					file(gpgPubRing(), "spring-signing-pubring.gpg")
					string(gpgPassphrase(), "spring-gpg-passphrase")
					string(githubToken(), githubTokenCredId())
					string(gradlePublishKeyEnvVar(), gradlePublishKeySecretId())
					string(gradlePublishSecretEnvVar(), gradlePublishSecretSecretId())
					usernamePassword(sonatypeUser(), sonatypePassword(),
							"oss-s01-token")
					additionalCredentials(delegate as CredentialsBindingContext)
				}
				timeout {
					noActivity(600)
					failBuild()
					writeDescription('Build failed due to timeout after {0} minutes of inactivity')
				}
			}
			steps {
				shell(loginToDocker())
				// build the releaser
				shell("""#!/bin/bash
				set -o errexit
				if [[ "\${$RELEASE_VERSION_PARAM}" == "" || "\${$RELEASE_VERSION_PARAM}" == "\"\"" ]]; then
					echo "\n\n\nYOU MUST PASS THE VERSION OF THE META-RELEASE!!!\n\n\n"
				fi
				echo "\n\n\nRUNNING THE [\${$RELEASE_VERSION_PARAM}] META-RELEASE!!!\n\n\n" 
				${setupGitCredentials()}
				${fetchConfigurationFile("config")}
				echo "Checking out the branch [\$${RELEASER_BRANCH_PARAM}]"
				git checkout "\$${RELEASER_BRANCH_PARAM}"
				mkdir -p target
				ROOT_VIEW="Spring%20Cloud"
				CURRENT_VIEW="Releaser"
				LOGS_FILE=".git/releaser.log"
				echo "Building the releaser, please wait... If the build fails after this then it means that the releaser failed to get built. Then please check the build's workspace under [.git/releaser.log] for logs. You can click here to see it [\${JENKINS_URL}/view/\${ROOT_VIEW}/view/\${CURRENT_VIEW}/job/\${JOB_NAME}/ws/\${LOGS_FILE}]"
				./mvnw clean install -DskipTests -am -pl :${options.projectName} > "\${LOGS_FILE}"
				set +x
				export SPRING_CLOUD_STATIC_REPO_DESTINATION="\$( dirname "\$(mktemp)" )/"
				SPRING_CLOUD_RELEASE_REPO="https://github.com/spring-cloud/spring-cloud-release.git"
				SYSTEM_PROPS="-Dgpg.secretKeyring="\$${gpgSecRing()}" -Dgpg.publicKeyring="\$${gpgPubRing()}" -Dgpg.passphrase="\$${gpgPassphrase()}" -DSONATYPE_USER="\$${sonatypeUser()}" -DSONATYPE_PASSWORD="\$${sonatypePassword()}""
				if [[ \${$START_FROM_PARAM} != "" ]]; then
					START_FROM_OPTS="--start-from '\${$START_FROM_PARAM}'"
				fi
				if [[ \${$TASK_NAMES_PARAM} != "" ]]; then
					TASK_NAMES_OPTS="--task-names '\${$TASK_NAMES_PARAM}'"
				fi
				${additionalEnvVars()}
				echo "Start from opts [\${START_FROM_OPTS}], task names [\${TASK_NAMES_OPTS}]"
				echo "Run the meta-releaser!"
				java -Dreleaser.git.username="\$${githubRepoUserNameEnvVar()}" \\
						-Dreleaser.git.password="\$${githubRepoPasswordEnvVar()}" \\
						-jar projects/${options.projectName}/target/${options.projectName}*SNAPSHOT.jar ${releaserOptions()} || exit 1
				${cleanGitCredentials()}
				""")
			}
			configure {
				slackNotification(it as Node)
			}
			publishers {
				archiveArtifacts {
					allowEmpty()
					pattern(".git/*.log")
					pattern("target/*.txt")
					pattern("target/*.md")
					pattern("target/*.adoc")
				}
				textFinder(".*BUILD UNSTABLE.*", "**/build_status,build_status", false, false, true)
			}
		}
	}

	protected String additionalEnvVars() {
		return ""
	}

	protected void additionalConfiguration(FreeStyleJob job) {

	}

	protected void additionalCredentials(CredentialsBindingContext context) {

	}

	protected String releaserOptions() {
		return """\
--releaser.post-release-tasks-only=\${$RELEASER_POST_RELEASE_ONLY_VAR}
--releaser.meta-release.release-train-project-name=\${$RELEASER_RELEASE_TRAIN_PROJECT_NAME_VAR}
--releaser.meta-release.release-train-dependency-names="\${$RELEASER_RELEASE_TRAIN_DEPENDENCY_NAMES_VAR}"
--releaser.meta-release.projects-to-skip="\${$RELEASER_PROJECTS_TO_SKIP_VAR}"
--releaser.git.release-train-bom-url=\${$RELEASER_GIT_RELEASE_TRAIN_BOM_URL_VAR}
--releaser.pom.this-train-bom=\${$RELEASER_POM_THIS_TRAIN_BOM}
--releaser.maven.wait-time-in-minutes=180
--releaser.maven.system-properties="\${SYSTEM_PROPS}"
--interactive=false
--meta-release=true
--dry-run=\${$DRY_RUN_PARAM}
--releaser.sagan.update-sagan=\${$RELEASER_SAGAN_UPDATE_VAR}
--releaser.git.update-documentation-repo=\${$RELEASER_GIT_UPDATE_DOCUMENTATION_REPOS_VAR}
--releaser.git.update-spring-project=\${$RELEASER_GIT_UPDATE_SPRING_PROJECTS_VAR}
--releaser.git.update-release-train-wiki=\${$RELEASER_GIT_UPDATE_RELEASE_TRAIN_WIKI_VAR}
--releaser.git.run-updated-samples=\${$RELEASER_GIT_RUN_UPDATED_SAMPLES_VAR}
--releaser.git.update-all-test-samples=\${$RELEASER_GIT_UPDATE_ALL_TEST_SAMPLES_VAR}
--releaser.git.update-release-train-docs=\${$RELEASER_GIT_UPDATE_RELEASE_TRAIN_DOCS_VAR}
--releaser.git.update-spring-guides=\${$RELEASER_GIT_UPDATE_SPRING_GUIDES_VAR}
--full-release \${START_FROM_OPTS} \${TASK_NAMES_OPTS}""".split("\n").join(" ")
	}

}
