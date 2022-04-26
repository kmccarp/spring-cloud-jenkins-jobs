package org.springframework.jenkins.cloud.util

import java.nio.file.Paths

import groovy.xml.XmlUtil
import org.custommonkey.xmlunit.DetailedDiff
import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.ElementNameAndAttributeQualifier
import org.custommonkey.xmlunit.XMLUnit
import org.junit.Before
import org.junit.ComparisonFailure

trait XmlComparator {

	private XMLUnit xmlUnit

	@Before
	void init() {
		xmlUnit = new XMLUnit()
		xmlUnit.ignoreWhitespace = true
		xmlUnit.normalizeWhitespace = true
	}

	void compareXmls(File expected, File actual) {
		//default parameter initializers are not allowed in traits
		compareXmls(expected, actual, false)
	}

	void compareXmls(File expected, File actual, boolean displayActualXmlInCaseOfError) {
		String nodeXml = XmlUtil.serialize(actual.text).stripIndent().stripMargin()
		if (!expected.isFile()) {
			if (System.getProperty('outputMissingXml') == 'true') {
				File missingXml = expected
				missingXml.parentFile.mkdirs()
				missingXml.text = nodeXml
			}
			throw new RuntimeException("Reference xml file [${expected.path}] not found")
		}
		String referenceXml = XmlUtil.serialize(expected.text).stripIndent().stripMargin()
		compareXmls(expected, referenceXml, nodeXml, displayActualXmlInCaseOfError)
	}

	void compareXmls(File expectedFile, String expected, String actual, boolean displayActualXmlInCaseOfError) {
		Diff diff = xmlUnit.compareXML(expected, actual)
		diff.overrideElementQualifier(new ElementNameAndAttributeQualifier())
		if (!diff.identical()) {
			DetailedDiff detailedDiff = new DetailedDiff(diff)
			//TODO: How to get line from diff? Find by node in XML file?
			if (displayActualXmlInCaseOfError) {
				println("Actual XML:\n $actual")
			}
			if (System.getProperty("outputActualXml") == 'true') {
				new File(expectedFile.parentFile, createActualOutputFileName(expectedFile)).text = actual
			}
			throw new XmlsAreNotSimilar(expectedFile.path, detailedDiff.allDifferences, expected, actual)
		}
	}

	private String createActualOutputFileName(File compared) {
		return compared.name.replace("xml", "ACTUAL.xml")
	}

	static class XmlsAreNotSimilar extends ComparisonFailure {
		XmlsAreNotSimilar(String packageFileName, List diffs, String expected, String actual) {
			super("For file ${formatPackageFileNameToHaveClickableLinkInIdea(packageFileName)} the following differences where found [$diffs].",
					expected, actual)
		}

		private static String formatPackageFileNameToHaveClickableLinkInIdea(String packageFileName) {
			//.(foo.ext:1) is a regex recognizable by Idea
			//In addition as there usually is "at" word in the exception message later on it is required to add extra "at" before a file name
			return "at .(${Paths.get(packageFileName).fileName}:1) "
		}
	}
}
