package org.springframework.jenkins.cloud.release

import javaposse.jobdsl.dsl.DslFactory

import org.springframework.jenkins.cloud.common.Project
import org.springframework.jenkins.cloud.common.ReleaseTrain

/**
 * @author Marcin Grzejszczak
 * @author Spencer Gibb
 */
class SpringCloudProjectReleaseMaker extends SpringCloudReleaseMaker {

	private final ReleaseTrain train
	private final Project project
	private String branch

	SpringCloudProjectReleaseMaker(DslFactory dsl, ReleaseTrain train, Project project) {
		super(dsl, project.org)
		this.train = train
		this.project = project
		branch = train.projectsWithBranch[project]
	}

	@Override
	protected String projectName(String project) {
		return "${project}-${train.codename}-${branch}-releaser"
	}

	void release(ReleaserOptions options) {
		options.updateSagan = false
		super.release(project.repo, train.jdkBaseline(), branch, train.releaseBranch(), options)
	}

	@Override
	protected String branchToCheck() {
		return branch
	}

}
