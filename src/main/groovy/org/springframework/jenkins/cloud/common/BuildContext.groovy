package org.springframework.jenkins.cloud.common

import groovy.transform.CompileStatic

/**
 * Parameter for any behavior methods on Project
 */
@CompileStatic
class BuildContext {
	boolean upload = true
	String branch
	String jdk
}
