package reactor

import javaposse.jobdsl.dsl.DslFactory

import org.springframework.jenkins.cloud.release.SpringCloudReleaserOptions
import org.springframework.jenkins.reactor.ReactorMetaReleaseMaker
import org.springframework.jenkins.reactor.ReactorMetaReleaseRepoPurger
import org.springframework.jenkins.reactor.ReactorReleaseMaker

DslFactory dsl = this

// RELEASER
["reactor-core", "reactor-addons", "reactor-pool", "reactor-netty", "reactor-kafka", "reactor-kotlin-extensions"].each {
//	new SpringCloudReleaseMainMaker(dsl).release(it, SpringCloudReleaserOptions.reactorMain())
	new ReactorReleaseMaker(dsl).release(it, SpringCloudReleaserOptions.reactor())
}

new ReactorMetaReleaseMaker(dsl).release("reactor-meta-releaser", SpringCloudReleaserOptions.reactor())
new ReactorMetaReleaseRepoPurger(dsl).build()
