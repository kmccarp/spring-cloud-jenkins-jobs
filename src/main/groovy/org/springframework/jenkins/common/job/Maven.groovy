package org.springframework.jenkins.common.job

import groovy.transform.CompileStatic

/**
 * Defaults for Maven builds
 *
 * @author Marcin Grzejszczak
 */
@CompileStatic
trait Maven {

	String maven35() {
		return "maven35"
	}

	String maven33() {
		return "maven33"
	}

	String maven32() {
		return "maven32"
	}

	String maven31() {
		return "maven31"
	}

	String maven30() {
		return "maven30"
	}
}
