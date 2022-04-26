package org.springframework.jenkins.cloud.common
/**
 *
 * @author Marcin Grzejszczak
 */
class TapPublisher {

	private final Node rootNode
	private final def propertiesNode
	private final def tap

	TapPublisher(Node rootNode) {
		this.rootNode = rootNode
		this.propertiesNode = rootNode / 'publishers'
		this.tap = propertiesNode / 'org.tap4j.plugin.TapPublisher'
		testResults()
		failIfNoResults()
		failedTestsMarkBuildAsFailure()
		outputTapToConsole()
		enableSubtests()
		discardOldReports()
		todoIsFailure()
		includeCommentDiagnostics()
		validateNumberOfTests()
		planRequired()
		verbose()
		showOnlyFailures()
		stripSingleParents()
		flattenTapResult()
		skipIfBuildNotOk()
	}

	void testResults(String testResults = "**/*.tap") {
		(tap / 'testResults').setValue(testResults)
	}

	void failIfNoResults(boolean value = true) {
		(tap / 'failIfNoResults').setValue(value)
	}

	void failedTestsMarkBuildAsFailure(boolean value = true) {
		(tap / 'failedTestsMarkBuildAsFailure').setValue(value)
	}

	void outputTapToConsole(boolean value = false) {
		(tap / 'outputTapToConsole').setValue(value)
	}

	void enableSubtests(boolean value = false) {
		(tap / 'enableSubtests').setValue(value)
	}

	void discardOldReports(boolean value = false) {
		(tap / 'discardOldReports').setValue(value)
	}

	void todoIsFailure(boolean value = false) {
		(tap / 'todoIsFailure').setValue(value)
	}

	void includeCommentDiagnostics(boolean value = false) {
		(tap / 'includeCommentDiagnostics').setValue(value)
	}

	void validateNumberOfTests(boolean value = false) {
		(tap / 'validateNumberOfTests').setValue(value)
	}

	void planRequired(boolean value = true) {
		(tap / 'planRequired').setValue(value)
	}

	void verbose(boolean value = true) {
		(tap / 'verbose').setValue(value)
	}

	void showOnlyFailures(boolean value = false) {
		(tap / 'showOnlyFailures').setValue(value)
	}

	void stripSingleParents(boolean value = false) {
		(tap / 'stripSingleParents').setValue(value)
	}

	void flattenTapResult(boolean value = false) {
		(tap / 'flattenTapResult').setValue(value)
	}

	void skipIfBuildNotOk(boolean value = false) {
		(tap / 'skipIfBuildNotOk').setValue(value)
	}

	static TapPublisher cloudTap(Node rootNode, @DelegatesTo(TapPublisher) Closure closure = Closure.IDENTITY) {
		TapPublisher publisher = new TapPublisher(rootNode)
		closure.delegate = publisher
		closure.call()
		return publisher
	}
}
