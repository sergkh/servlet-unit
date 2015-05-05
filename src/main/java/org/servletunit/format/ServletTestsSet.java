package org.servletunit.format;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

/**
 * Contains test data for serialized object.
 * 
 * @author Vladimir Khruschak
 * @author Sergey Khruschak
 * @author Yaroslav Derman
 */
@Root
@Namespace(reference = "http://www.w3.org/2001/XMLSchema-instance")
public class ServletTestsSet {

	@Attribute(name = "noNamespaceSchemaLocation", required = false)
	private String noNamespaceSchemaLocation;

	@ElementList(inline = true, entry = "script", required = false)
	private List<String> testScripts = new ArrayList<String>();

	@ElementList(inline = true, entry = "test", required = false)
	private List<ServletsTestCase> testCases = new ArrayList<ServletsTestCase>();

	@Element(name = "defaults", required = false)
	private ServletTestDefaults defaults;

	public ServletTestsSet() {
	}

	public List<ServletsTestCase> getTestCases() {
		return testCases;
	}

	public void setTestCases(List<ServletsTestCase> testCases) {
		this.testCases = testCases;
	}

	/**
	 *
	 * @return the scripts
	 */
	public List<String> getTestScripts() {
		return testScripts;
	}

	/**
	 *
	 * @param testScripts
	 */
	public void setTestScripts(List<String> testScripts) {
		this.testScripts = testScripts;
	}

	/**
	 * @return the defaults
	 */
	public ServletTestDefaults getDefaults() {
		return defaults;
	}

	/**
	 * @param defaults
	 *            the defaults to set
	 */
	public void setDefaults(ServletTestDefaults defaults) {
		this.defaults = defaults;
	}
}