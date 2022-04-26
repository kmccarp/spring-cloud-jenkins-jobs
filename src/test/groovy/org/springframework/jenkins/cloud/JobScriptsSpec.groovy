package org.springframework.jenkins.cloud

import groovy.io.FileType
import javaposse.jobdsl.dsl.DslScriptLoader
import javaposse.jobdsl.dsl.GeneratedItems
import javaposse.jobdsl.dsl.MemoryJobManagement
import javaposse.jobdsl.dsl.ScriptRequest
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

import org.springframework.jenkins.cloud.util.XmlComparator

import static org.assertj.core.api.BDDAssertions.then

/**
 * Tests that all dsl scripts in the jobs directory will compile.
 */
@Ignore
class JobScriptsSpec extends Specification implements XmlComparator {

	File expectedJobs = new File(JobScriptsSpec.getResource("/generated-jenkins-files").toURI())

	@Unroll
	def 'test script #file.name'() {
		given:

		MemoryJobManagement jm = new MemoryJobManagement()
		DslScriptLoader loader = new DslScriptLoader(jm)

		when:
		GeneratedItems scripts = loader.runScripts([new ScriptRequest(file.text)])
		String scriptName = file.getName().replace(".groovy", "")
		File generatedFolderParent = writeItems(scriptName, jm)
		then:
		noExceptionThrown()
		and:
		List<String> generatedJobNames = scripts.getJobs().collect { it.getJobName() }
		List<String> generatedViewNames = scripts.getViews().collect { it.getName() }
		File fromScript = new File(this.expectedJobs, scriptName)
		File[] expectedJobs = new File(fromScript, "jobs").listFiles() ?: []
		File[] expectedViews = new File(fromScript, "views").listFiles() ?: []
		then: "We have to generate as many jobs and views as we had previously stored"
		then(expectedJobs.collect { it.name.replace(".xml", "") }).containsExactlyInAnyOrder((String[]) generatedJobNames.toArray())
		then(expectedViews.collect { it.name.replace(".xml", "") }).containsExactlyInAnyOrder((String[]) generatedViewNames.toArray())
		and:
		assertXmls(expectedJobs, new File(generatedFolderParent, "jobs"))
		and:
		assertXmls(expectedViews, new File(generatedFolderParent, "views"))
		where:
		file << jobFiles
	}

	void assertXmls(File[] expectedFiles, File folder) {
		expectedFiles.each { File expected ->
			compareXmls(expected, new File(folder, expected.name))
		}
	}

	def setupSpec() {
		new File("target/generated-jenkins-files/").delete()
	}

	static List<File> getJobFiles() {
		List<File> files = []
		new File('jobs').eachFileRecurse(FileType.FILES) {
			files << it
		}
		return files
	}

	/**
	 * Write the config.xml for each generated job and view to the build dir.
	 */
	private File writeItems(String fileName, MemoryJobManagement jm) {
		File parent = new File("target/generated-jenkins-files/${fileName}/jobs")
		parent.mkdirs()
		jm.getSavedConfigs().each { String name, String text ->
			new File(parent, name + ".xml").text = text
		}
		parent = new File("target/generated-jenkins-files/${fileName}/views")
		parent.mkdirs()
		jm.getSavedViews().each { String name, String text ->
			new File(parent, name + ".xml").text = text
		}
		return parent.getParentFile()
	}

}

