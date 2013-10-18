/**
 * @author Vladimir.Khruschak
 */
package org.servletunit;

import org.servletunit.format.ServletsTestCase;


public interface TestCase {
	public String replaceVars(String requestData);
	public ServletsTestCase getTest();
}
