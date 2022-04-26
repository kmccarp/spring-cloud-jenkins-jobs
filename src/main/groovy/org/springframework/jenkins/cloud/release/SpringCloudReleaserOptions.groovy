package org.springframework.jenkins.cloud.release

import groovy.transform.CompileStatic

import org.springframework.jenkins.cloud.common.AllCloudConstants

@CompileStatic
class SpringCloudReleaserOptions {

	static ReleaserOptions springCloud() {
		return springCloudBuilder()
				.build()
	}

	private static ReleaserOptionsBuilder springCloudBuilder() {
		return ReleaserOptions.builder()
				.projectName("spring-cloud")
				.releaseThisTrainBom("spring-cloud-dependencies/pom.xml")
				.releaseTrainBomUrl("https://github.com/spring-cloud/spring-cloud-release")
				.releaserConfigUrl("https://raw.githubusercontent.com/spring-cloud/spring-cloud-release")
				.releaserBranch("main")
				.releaserConfigBranch("jenkins-releaser-config")
				.projectsToSkip(AllCloudConstants.DEFAULT_RELEASER_SKIPPED_PROJECTS)
				.releaseTrainProjectName("spring-cloud-release")
				.releaseTrainDependencyNames(["spring-cloud", "spring-cloud-dependencies", "spring-cloud-starter", "spring-cloud-starter-build"])
				.gitOrgUrl("https://github.com/spring-cloud")
				.runUpdatedSamples(true)
				.updateAllTestSamples(true)
				.updateDocumentationRepos(true)
				.updateReleaseTrainDocs(true)
				.updateReleaseTrainWiki(true)
				.updateSpringGuides(true)
				.updateGithubMilestones(true)
				.updateStartSpringIo(true)
				.updateSpringProjects(true)
				.updateSagan(true)
				.postReleaseOnly(false)
				.dryRun(false)
	}

	static ReleaserOptions springCloudSnapshot() {
		return springCloudBuilder()
				.updateSagan(false)
				.build()
	}

	static ReleaserOptions springCloudStream() {
		return ReleaserOptions.builder()
				.projectName("spring-cloud-stream")
				.releaseThisTrainBom("spring-cloud-stream-dependencies/pom.xml")
				.releaseTrainBomUrl("https://github.com/spring-cloud/spring-cloud-stream-starters")
				.releaserConfigUrl("https://raw.githubusercontent.com/spring-cloud/spring-cloud-stream-starters")
				.releaserBranch("main")
				.releaserConfigBranch("jenkins-releaser-config")
				.projectsToSkip(AllCloudConstants.DEFAULT_STREAM_RELEASER_SKIPPED_PROJECTS)
				.releaseTrainProjectName("spring-cloud-stream-starters")
				.releaseTrainDependencyNames(["spring-cloud-stream-dependencies"])
				.gitOrgUrl("https://github.com/spring-cloud")
				.runUpdatedSamples(false)
				.updateAllTestSamples(false)
				.updateDocumentationRepos(false)
				.updateReleaseTrainDocs(false)
				.updateReleaseTrainWiki(false)
				.updateSpringGuides(false)
				.updateGithubMilestones(false)
				.updateStartSpringIo(false)
				.updateSpringProjects(false)
				.updateSagan(true)
				.dryRun(false)
				.postReleaseOnly(false)
				.build()
	}

	static ReleaserOptions springProjectsExperimental() {
		return springCloudBuilder()
				.gitOrgUrl("https://github.com/spring-projects-experimental/")
				.runUpdatedSamples(false)
				.updateAllTestSamples(false)
				.updateDocumentationRepos(false)
				.updateReleaseTrainDocs(false)
				.updateReleaseTrainWiki(false)
				.updateSpringGuides(false)
				.updateGithubMilestones(true)
				.updateStartSpringIo(false)
				.updateSpringProjects(false)
				.updateSagan(true)
				.dryRun(false)
				.postReleaseOnly(false)
				.build()
	}

	static ReleaserOptions reactor() {
		return reactorBuilder().build()
	}

	private static ReleaserOptionsBuilder reactorBuilder() {
		return ReleaserOptions.builder()
				.projectName("reactor")
				.releaseTrainBomUrl("https://github.com/reactor/reactor")
				.releaserConfigUrl("https://raw.githubusercontent.com/reactor/reactor")
				.releaserBranch("main")
				.releaserConfigBranch("releases")
				.releaseTrainProjectName("reactor")
				.releaseTrainDependencyNames(["reactor"])
				.gitOrgUrl("https://github.com/reactor")
				.runUpdatedSamples(false)
				.updateAllTestSamples(false)
				.updateDocumentationRepos(false)
				.updateReleaseTrainDocs(false)
				.updateReleaseTrainWiki(false)
				.updateSpringGuides(false)
				.updateGithubMilestones(false)
				.updateStartSpringIo(false)
				.updateSpringProjects(false)
				.updateSagan(false)
				.dryRun(false)
				.postReleaseOnly(false)
	}

	static ReleaserOptions reactorMain() {
		return reactorBuilder()
				.updateSagan(false)
				.build()
	}
}
