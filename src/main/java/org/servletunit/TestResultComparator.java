package org.servletunit;

import org.servletunit.format.ServletsTestCase;

public interface TestResultComparator {

	public String getType();
	
	public void compareResponse(ServletsTestCase test, String expected, String actual);

}