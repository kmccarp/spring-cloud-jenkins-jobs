job('spring-cloud-meta-seed') {
	triggers {
		githubPush()
	}
	jdk("jdk8")
	scm {
		git {
			remote {
				github('spring-cloud/spring-cloud-jenkins-jobs')
			}
			branch("main")
		}
	}
	steps {
		shell("./mvnw clean install -B")
		dsl {
			external('projects/*.groovy')
			removeAction('DISABLE')
			removeViewAction('DELETE')
			ignoreExisting(false)
			additionalClasspath([
					'src/main/groovy', 'src/main/resources', 'build/lib/*.jar'
			].join("\n"))
		}
	}
}
