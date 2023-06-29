package org.springframework.jenkins.cloud.release

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.jobs.FreeStyleJob

import org.springframework.jenkins.cloud.common.Project
import org.springframework.jenkins.cloud.common.ReleaseTrain
import org.springframework.jenkins.cloud.common.ReleaseTrains

/**
 * @author Marcin Grzejszczak
 * @author Spencer Gibb
 */
class SpringCloudReleaseSnapshotMaker extends SpringCloudReleaseMaker {

	private final ReleaseTrain train
	private final Project project
	private String branch

	SpringCloudReleaseSnapshotMaker(DslFactory dsl, ReleaseTrain train, Project project) {
		super(dsl, project.org)
		this.train = train
		this.project = project
		branch = train.projectsWithBranch[project]
	}

	@Override
	protected String projectName(String project) {
		return "${project}-${train.codename}-${branch}-snapshot-releaser"
	}

	void release(ReleaserOptions options) {
		options.updateSagan = false
		super.release(project.repo, ReleaseTrains.jdks.jdk17(), branch, train.releaseBranch(), options)
	}

	@Override
	protected String branchToCheck() {
		return branch
	}

	@Override
	protected void additionalConfiguration(FreeStyleJob job) {
		job.triggers {
			cron oncePerDay()
		}
	}

	@Override
	protected String scriptPreconditions() {
		return '''\
echo "Running version check"
VERSION=$( sed '\\!<parent!,\\!</parent!d' `pwd`/pom.xml | grep '<version' | head -1 | sed -e 's/.*<version>//' -e 's!</version>.*$!!' )
echo "The found version is [${VERSION}]"

if ! echo $VERSION | egrep -q 'SNAPSHOT'; then
    echo "Version is NOT SNAPSHOT, will not do anything. Something is wrong!"
    exit 1
else
	echo "Version is a SNAPSHOT one, will continue with the build"
fi
'''
	}

	@Override
	protected void configureLabels(FreeStyleJob job) {

	}
}
