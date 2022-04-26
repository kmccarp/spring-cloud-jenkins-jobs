package org.springframework.jenkins.cloud.release

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.helpers.wrapper.CredentialsBindingContext
import javaposse.jobdsl.dsl.jobs.FreeStyleJob

import org.springframework.jenkins.cloud.common.Releaser
import org.springframework.jenkins.cloud.common.SpringCloudJobs
import org.springframework.jenkins.common.job.Cron
import org.springframework.jenkins.common.job.JdkConfig
import org.springframework.jenkins.common.job.TestPublisher

/**
 * @author Marcin Grzejszczak
 */
class SpringCloudReleaseMaker implements JdkConfig, TestPublisher,
		SpringCloudJobs, Cron, Releaser {
	protected static final String RELEASE_VERSION_PARAM = "RELEASE_VERSION"
	protected static final String RELEASER_CONFIG_URL_PARAM = "RELEASER_CONFIG_URL"
	protected static final String RELEASER_BRANCH_PARAM = "RELEASER_BRANCH"
	protected static final String RELEASER_CONFIG_BRANCH_PARAM = "RELEASER_CONFIG_BRANCH"
	protected static final String RELEASER_POM_BRANCH_VAR = "RELEASER_POM_BRANCH"
	protected static final String DRY_RUN_PARAM = "DRY_RUN"
	protected static final String RELEASER_META_RELEASE_GIT_ORG_URL_VAR = "RELEASER_META_RELEASE_GIT_ORG_URL"
	protected static final String RELEASER_ADDITIONAL_PROPS_VAR = "RELEASER_ADDITIONAL_PROPS"
	protected static final String RELEASER_SAGAN_UPDATE_VAR = 'RELEASER_SAGAN_UPDATE'
	protected static final String RELEASER_RELEASE_TRAIN_PROJECT_NAME_VAR = 'RELEASER_META_RELEASE_RELEASE_TRAIN_PROJECT_NAME'
	protected static final String RELEASER_GIT_RELEASE_TRAIN_BOM_URL_VAR = 'RELEASER_GIT_RELEASE_TRAIN_BOM'
	protected static final String RELEASER_POM_THIS_TRAIN_BOM_VAR = 'RELEASER_POM_THIS_TRAIN'
	protected static final String RELEASER_POST_RELEASE_ONLY_VAR = 'RELEASER_POST_RELEASE_ONLY'

	private final DslFactory dsl
	final String organization

	SpringCloudReleaseMaker(DslFactory dsl) {
		this.dsl = dsl
		this.organization = 'spring-cloud'
	}

	SpringCloudReleaseMaker(DslFactory dsl, String organization) {
		this.dsl = dsl
		this.organization = organization
	}

	void release(String project, ReleaserOptions options = new ReleaserOptions()) {
		release(project, jdk8(), mainBranch(), mainBranch(), options)
	}

	void release(String project, String jdkVersion, String projectBranch, String releaserBranch, ReleaserOptions options = new ReleaserOptions()) {
		dsl.job(projectName(project)) {
			additionalConfiguration(delegate as FreeStyleJob)
			parameters {
				stringParam(branchVarName(), projectBranch, "Your project's branch")
				stringParam(RELEASE_VERSION_PARAM, "", "Name of the release (e.g. Hoxton.RELEASE). Will correspond to the properties file (e.g. hoxton_release.properties) in the branch with releaser properties")
				stringParam(RELEASER_BRANCH_PARAM, options.releaserBranch, "Branch for the releaser code")
				stringParam(RELEASER_CONFIG_URL_PARAM, options.releaserConfigUrl, "Root of the URL where the RAW version of the configuration file is present")
				stringParam(RELEASER_CONFIG_BRANCH_PARAM, options.releaserConfigBranch, "Branch, where the RAW version of the configuration file is present")
				stringParam(RELEASER_POM_BRANCH_VAR, releaserBranch, "Spring Cloud Release branch. If [${RELEASE_VERSION_PARAM}] was passed, then this will be ignored")
				stringParam(RELEASER_ADDITIONAL_PROPS_VAR, '', 'Additional system properties')
				stringParam(RELEASER_RELEASE_TRAIN_PROJECT_NAME_VAR, options.releaseTrainProjectName, 'Name of the project that represents the BOM of the release train')
				stringParam(RELEASER_GIT_RELEASE_TRAIN_BOM_URL_VAR, options.releaseTrainBomUrl, 'Subfolder of the pom that contains the versions for the release train')
				stringParam(RELEASER_META_RELEASE_GIT_ORG_URL_VAR, options.gitOrgUrl, 'URL of the organization from which projects can be cloned')
				stringParam(RELEASER_POM_THIS_TRAIN_BOM_VAR, options.releaseThisTrainBom, 'URL to a project containing a BOM. Defaults to Spring Cloud Release Git repository')
				booleanParam(RELEASER_SAGAN_UPDATE_VAR, options.updateSagan, 'If true then will update documentation repository with the current URL')
				booleanParam(RELEASER_POST_RELEASE_ONLY_VAR, options.postReleaseOnly, 'If set to true will run only post release tasks')
				booleanParam(DRY_RUN_PARAM, options.dryRun, 'If true then will run meta-release in a dry run mode')
			}
			jdk jdkVersion
			scm {
				git {
					remote {
						url "https://github.com/${organization}/${project}"
						branch "\$${branchVarName()}"
						credentials(githubUserCredentialId())
					}
					extensions {
						wipeOutWorkspace()
						localBranch("**")
					}
				}
			}
			configureLabels(delegate as FreeStyleJob)
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
				${scriptPreconditions()}
				${buildReleaserForSingleProject(options)}
				releaserJarLocation="\${tmpDir}/projects/${options.projectName}/target/"
				additionalParams=""
				if [[ \$${RELEASE_VERSION_PARAM} != "" ]]; then
					echo "Found the release version parameter. Will use the properties file to set the versions"
					${fetchConfigurationFile("\${releaserJarLocation}")}
					versions="--\$( sed '{:q;N;s/\\n/ --/g;t q}' \${releaserJarLocation}/application.properties )"
					additionalParams="--releaser.git.fetch-versions-from-git=false \${versions}"
				fi
				echo "Run the releaser against the project"
				echo "Checking out branch"
				git checkout ${branchToCheck()}
				echo "Releasing the project"
				${setupGitCredentials()}
				${additionalEnvVars()}
				set +x
				SPRING_CLOUD_RELEASE_REPO="https://github.com/spring-cloud/spring-cloud-release.git"
				SYSTEM_PROPS="-Dgpg.secretKeyring="\$${gpgSecRing()}" -Dgpg.publicKeyring="\$${gpgPubRing()}" -Dgpg.passphrase="\$${gpgPassphrase()}" -DSONATYPE_USER="\$${sonatypeUser()}" -DSONATYPE_PASSWORD="\$${sonatypePassword()}""
				java \${${RELEASER_ADDITIONAL_PROPS_VAR}} -Dreleaser.git.username="\$${githubRepoUserNameEnvVar()}" -Dreleaser.git.password="\$${githubRepoPasswordEnvVar()}" -jar \${releaserJarLocation}/${options.projectName}*SNAPSHOT.jar ${releaserOptions()} \$additionalParams || exit 1
				${cleanGitCredentials()}
				""")
			}
			configure {
				slackNotification(it as Node)
			}
			publishers {
				archiveJunit(mavenJUnitResults()) {
					allowEmptyResults()
				}
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

	protected void configureLabels(FreeStyleJob job) {
		job.label(releaserLabel())
	}

	protected String branchToCheck() {
		return '$' + branchVarName()
	}

	protected String projectName(String project) {
		return "$project-releaser"
	}

	protected String scriptPreconditions() {
		return ""
	}

	protected String releaserOptions() {
		return """\
--releaser.post-release-tasks-only=\${$RELEASER_POST_RELEASE_ONLY_VAR}
--releaser.meta-release.release-train-project-name=\${$RELEASER_RELEASE_TRAIN_PROJECT_NAME_VAR}
--releaser.git.release-train-bom-url=\${$RELEASER_GIT_RELEASE_TRAIN_BOM_URL_VAR}
--releaser.pom.this-train-bom=\${$RELEASER_POM_THIS_TRAIN_BOM_VAR}
--releaser.pom.branch=\${$RELEASER_POM_BRANCH_VAR}
--releaser.maven.wait-time-in-minutes=180
--releaser.maven.system-properties="\${SYSTEM_PROPS}"
--full-release
--dry-run=\${$DRY_RUN_PARAM}
--releaser.sagan.update-sagan=\${$RELEASER_SAGAN_UPDATE_VAR}
--interactive=false""".split("\n").join(" ")
	}
}
