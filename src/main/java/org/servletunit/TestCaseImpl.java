package org.servletunit;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.servletunit.ReplaceFunction;
import org.servletunit.format.ServletsTestCase;

/**
 * @author Sergey.Khruschak
 */
public class TestCaseImpl implements TestCase {
	
	private List<ReplaceFunction> replacers;
	private ServletsTestCase test;
	
	public TestCaseImpl(List<ReplaceFunction> replacers, ServletsTestCase test) {
		this.replacers = replacers;
		this.test = test;
	}

	/**
	 * @param replacers the replacers to set
	 */
	public void setReplacers(List<ReplaceFunction> replacers) {
		this.replacers = replacers;
	}

	/**
	 * @param test the test to set
	 */
	public void setTest(ServletsTestCase test) {
		this.test = test;
	}

	/**
	 * @return the test
	 */
	@Override
	public ServletsTestCase getTest() {
		return test;
	}

	/* (non-Javadoc)
	 * @see ua.globalmoney.gmws.util.RequestDataReplacer#replaceVars(java.lang.String, ua.globalmoney.common.web.format.ServletsTestCase)
	 */
	@Override
	public String replaceVars(String requestData) {
		if(requestData == null || requestData.isEmpty()) return requestData;
		
		try {
			// process all replacement functions
			for(ReplaceFunction rf : replacers) {
				Pattern pattern = rf.getPattern();
				Matcher m = pattern.matcher(requestData);
				StringBuffer sb = new StringBuffer();
	
				while(m.find()) {
					m.appendReplacement(sb, rf.replace(m, this));
				}
	
				m.appendTail(sb);
				requestData = sb.toString();
			}
		} catch (Exception e) {
			throw new RuntimeException("Variables replacement failed: " + requestData + ", test: " + test, e);
		}
		
		return requestData;
	}
}