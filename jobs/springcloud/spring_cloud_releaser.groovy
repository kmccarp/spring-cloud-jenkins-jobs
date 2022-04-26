package springcloud

import javaposse.jobdsl.dsl.DslFactory

import org.springframework.jenkins.cloud.common.ReleaseTrains
import org.springframework.jenkins.cloud.release.ReleaserOptions
import org.springframework.jenkins.cloud.release.SpringCloudMetaReleaseMaker
import org.springframework.jenkins.cloud.release.SpringCloudMetaReleaseRepoPurger
import org.springframework.jenkins.cloud.release.SpringCloudProjectReleaseMaker
import org.springframework.jenkins.cloud.release.SpringCloudReleaseMaker
import org.springframework.jenkins.cloud.release.SpringCloudReleaseSnapshotMaker
import org.springframework.jenkins.cloud.release.SpringCloudReleaserOptions

import static org.springframework.jenkins.cloud.common.AllCloudJobs.ALL_STREAM_JOBS_FOR_RELEASER

DslFactory dsl = this

// RELEASER
ReleaseTrains.allActive().each { train ->
	// meta releaser per train, for jdk configuration
	if (train.metaRelease) {
		new SpringCloudMetaReleaseMaker(dsl)
				.release("spring-cloud-${train.codename}-meta-releaser", train.jdkBaseline(),
						SpringCloudReleaserOptions.springCloud())
	}

	train.projects().each { project ->
		// snapshot release, daily, no sagan

		ReleaserOptions snapshotOptions = SpringCloudReleaserOptions.springCloudSnapshot()
		// release, no schedule
		ReleaserOptions releaseOptions = SpringCloudReleaserOptions.springCloud()

		// TODO: train behavior?
		if (train.codename == "Experimental") {
			snapshotOptions = SpringCloudReleaserOptions.springProjectsExperimental()
			releaseOptions = SpringCloudReleaserOptions.springProjectsExperimental()
		}

		if (project.releaseTypeSnapshots()) {
			new SpringCloudReleaseSnapshotMaker(dsl, train, project).release(snapshotOptions)
		}
		if (project.releaseTypeRelease()) {
			new SpringCloudProjectReleaseMaker(dsl, train, project).release(releaseOptions)
		}
	}
}
new SpringCloudMetaReleaseMaker(dsl)
		.release("spring-cloud-stream-meta-releaser", SpringCloudReleaserOptions.springCloudStream())
new SpringCloudMetaReleaseRepoPurger(dsl).build()

// TODO: move this
// Compatibility builds
//new ManualBootCompatibilityBuildMaker(dsl).build()

ALL_STREAM_JOBS_FOR_RELEASER.each {
	new SpringCloudReleaseMaker(dsl).release(it, SpringCloudReleaserOptions.springCloudStream())
}
