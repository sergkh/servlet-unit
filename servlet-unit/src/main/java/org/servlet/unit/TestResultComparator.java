package org.servlet.unit;

import org.servlet.unit.format.ServletsTestCase;

public interface TestResultComparator {

	public String getType();
	
	public void compareResponse(ServletsTestCase test, String expected, String actual);

}