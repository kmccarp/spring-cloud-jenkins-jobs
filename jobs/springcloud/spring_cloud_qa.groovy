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

Projects.SLEUTH.with {
	SonarBuildMaker sonar = new SonarBuildMaker(dsl)
	sonar.branchName = "3.1.x"
	sonar.buildSonar(it.repo)
}
Projects.SLEUTH_OTEL.with {
	SonarBuildMaker sonar = new SonarBuildMaker(dsl)
	sonar.org = "spring-projects-experimental"
	sonar.buildSonar(it.repo)
}
// new ConsulMutationBuildMaker(dsl).build()
new KubernetesSonarBuildMaker(dsl).buildSonar()
// new MutationBuildMaker(dsl).build("spring-cloud-contract")
