package org.springframework.jenkins.cloud.release

import groovy.transform.CompileStatic

/**
 * @author Marcin Grzejszczak
 */
@CompileStatic
class ReleaserOptionsBuilder {
	String projectName
	boolean dryRun
	boolean updateSagan
	boolean updateDocumentationRepos
	boolean updateSpringProjects
	boolean updateReleaseTrainWiki
	boolean runUpdatedSamples
	boolean updateAllTestSamples
	boolean updateReleaseTrainDocs
	boolean updateSpringGuides
	boolean updateStartSpringIo
	boolean updateGithubMilestones
	boolean postReleaseOnly
	String releaseTrainProjectName
	String releaserConfigUrl
	String releaserBranch = "main"
	String releaserConfigBranch
	List<String> releaseTrainDependencyNames
	String gitOrgUrl
	String releaseTrainBomUrl
	String releaseThisTrainBom
	String projectsToSkip

	ReleaserOptionsBuilder projectName(String projectName) {
		this.projectName = projectName
		return this
	}

	ReleaserOptionsBuilder dryRun(boolean dryRun) {
		this.dryRun = dryRun
		return this
	}

	ReleaserOptionsBuilder updateSagan(boolean updateSagan) {
		this.updateSagan = updateSagan
		return this
	}

	ReleaserOptionsBuilder updateDocumentationRepos(boolean updateDocumentationRepos) {
		this.updateDocumentationRepos = updateDocumentationRepos
		return this
	}

	ReleaserOptionsBuilder updateSpringProjects(boolean updateSpringProjects) {
		this.updateSpringProjects = updateSpringProjects
		return this
	}

	ReleaserOptionsBuilder updateReleaseTrainWiki(boolean updateReleaseTrainWiki) {
		this.updateReleaseTrainWiki = updateReleaseTrainWiki
		return this
	}

	ReleaserOptionsBuilder runUpdatedSamples(boolean runUpdatedSamples) {
		this.runUpdatedSamples = runUpdatedSamples
		return this
	}

	ReleaserOptionsBuilder updateAllTestSamples(boolean updateAllTestSamples) {
		this.updateAllTestSamples = updateAllTestSamples
		return this
	}

	ReleaserOptionsBuilder updateReleaseTrainDocs(boolean updateReleaseTrainDocs) {
		this.updateReleaseTrainDocs = updateReleaseTrainDocs
		return this
	}

	ReleaserOptionsBuilder updateSpringGuides(boolean updateSpringGuides) {
		this.updateSpringGuides = updateSpringGuides
		return this
	}

	ReleaserOptionsBuilder updateStartSpringIo(boolean updateStartSpringIo) {
		this.updateStartSpringIo = updateStartSpringIo
		return this
	}

	ReleaserOptionsBuilder updateGithubMilestones(boolean updateGithubMilestones) {
		this.updateGithubMilestones = updateGithubMilestones
		return this
	}

	ReleaserOptionsBuilder postReleaseOnly(boolean postReleaseOnly) {
		this.postReleaseOnly = postReleaseOnly
		return this
	}

	ReleaserOptionsBuilder releaseTrainProjectName(String releaseTrainProjectName) {
		this.releaseTrainProjectName = releaseTrainProjectName
		return this
	}

	ReleaserOptionsBuilder releaserConfigUrl(String releaserConfigUrl) {
		this.releaserConfigUrl = releaserConfigUrl
		return this
	}

	ReleaserOptionsBuilder releaserBranch(String releaserBranch) {
		this.releaserBranch = releaserBranch
		return this
	}

	ReleaserOptionsBuilder releaserConfigBranch(String releaserConfigBranch) {
		this.releaserConfigBranch = releaserConfigBranch
		return this
	}

	ReleaserOptionsBuilder releaseTrainDependencyNames(List<String> releaseTrainDependencyNames) {
		this.releaseTrainDependencyNames = releaseTrainDependencyNames
		return this
	}

	ReleaserOptionsBuilder gitOrgUrl(String gitOrgUrl) {
		this.gitOrgUrl = gitOrgUrl
		return this
	}

	ReleaserOptionsBuilder releaseTrainBomUrl(String releaseTrainBomUrl) {
		this.releaseTrainBomUrl = releaseTrainBomUrl
		return this
	}

	ReleaserOptionsBuilder releaseThisTrainBom(String releaseThisTrainBom) {
		this.releaseThisTrainBom = releaseThisTrainBom
		return this
	}

	ReleaserOptionsBuilder projectsToSkip(String projectsToSkip) {
		this.projectsToSkip = projectsToSkip
		return this
	}

	ReleaserOptions build() {
		return new ReleaserOptions(projectName,
				dryRun,
				updateSagan,
				updateDocumentationRepos,
				updateSpringProjects,
				updateReleaseTrainWiki,
				runUpdatedSamples,
				updateAllTestSamples,
				updateReleaseTrainDocs,
				updateSpringGuides,
				updateStartSpringIo,
				updateGithubMilestones,
				postReleaseOnly,
				releaseTrainProjectName,
				releaserConfigUrl,
				releaserConfigBranch,
				releaserBranch,
				releaseTrainDependencyNames,
				releaseTrainBomUrl,
				releaseThisTrainBom,
				projectsToSkip,
				gitOrgUrl)
	}
}
