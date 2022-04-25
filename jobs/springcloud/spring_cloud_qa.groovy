package springcloud

import javaposse.jobdsl.dsl.DslFactory
import org.springframework.jenkins.cloud.common.Projects
import org.springframework.jenkins.cloud.qa.KubernetesSonarBuildMaker
import org.springframework.jenkins.cloud.qa.SonarBuildMaker

DslFactory dsl = this

// QA
(Projects.ALL - Projects.SLEUTH - Projects.SLEUTH_OTEL).findAll { it.hasTests }.each { project ->
	new SonarBuildMaker(dsl).buildSonar(project.repo)
}

new SonarBuildMaker(dsl).with {
	branchName = "3.1.x"
	jdkVersion = jdk8()
	buildSonar(Projects.SLEUTH.repo)
}

new SonarBuildMaker(dsl).with {
	org = "spring-projects-experimental"
	jdkVersion = jdk8()
	buildSonar(Projects.SLEUTH_OTEL.repo)
}

// new ConsulMutationBuildMaker(dsl).build()
new KubernetesSonarBuildMaker(dsl).buildSonar()
// new MutationBuildMaker(dsl).build("spring-cloud-contract")
