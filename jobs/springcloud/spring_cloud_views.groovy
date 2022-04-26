package springcloud

import javaposse.jobdsl.dsl.DslFactory

import org.springframework.jenkins.cloud.common.ReleaseTrains

DslFactory dsl = this

dsl.listView('Seeds') {
	jobs {
		regex('.*-seed')
	}
	columns defaultColumns()
}

dsl.nestedView('Spring Cloud') {
	views {
		def views = delegate
		listView('Boot.NEXT') {
			jobs {
				regex('spring-cloud.*-compatibility-check')
			}
			columns defaultColumns()
		}
		listView('CI') {
			jobs {
				regex('spring-cloud.*-ci')
			}
			columns defaultColumns()
		}
		// ALL Release Train CI Views
		ReleaseTrains.allActive().each { train ->
			views.listView("CI.${train.codename}") {
				jobs {
					regex("spring-cloud.*-${train.codename}-.*-ci")
				}
				columns defaultColumns()
			}
		}
		// ALL Release Train Releaser Views
		ReleaseTrains.allActive().each { train ->
			views.listView("Releaser.${train.codename}") {
				jobs {
					regex("spring-cloud.*-${train.codename}-.*-releaser")
				}
				columns defaultColumns()
			}
		}
		listView('E2E') {
			jobs {
				regex('spring-cloud.*-e2e')
			}
			columns defaultColumns()
		}
		listView('Releaser') {
			jobs {
				regex('spring-cloud.*-releaser')
			}
			columns defaultColumns()
		}
		listView('Sonar') {
			jobs {
				regex('spring-cloud.*-sonar')
			}
			columns defaultColumns()
		}
		nestedView('CD-pipelines') {
			def nested = delegate
			['github-analytics', 'github-webhook', 'app-monolith'].each {
				String artifactName = it
				String projectName = "${artifactName}-pipeline"
				nested.views {
					deliveryPipelineView(projectName) {
						allowPipelineStart()
						pipelineInstances(5)
						showAggregatedPipeline(false)
						columns(1)
						updateInterval(5)
						enableManualTriggers()
						showAvatars()
						showChangeLog()
						pipelines {
							component("Deploy ${artifactName} to production", "${projectName}-build")
						}
						allowRebuild()
						showDescription()
						showPromotions()
						showTotalBuildTime()
						configure {
							(it / 'showTestResults').setValue(true)
							(it / 'pagingEnabled').setValue(true)
						}
					}
				}
			}
		}
		listView('All Cloud') {
			jobs {
				regex('spring-cloud.*')
			}
			columns defaultColumns()
		}
	}
}

private Closure defaultColumns() {
	return {
		status()
		name()
		lastSuccess()
		lastFailure()
		lastBuildConsole()
		buildButton()
	}
}
