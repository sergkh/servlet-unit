/*
 * Copyright (c) 2011 by Global Money Ukraine.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Global Money Ukraine.
 *  
 * Created on 14.05.2013, 11:56:18
 * Author Vladimir Khruschak
 */
package org.servlet.unit.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.servlet.unit.ReplaceFunction;
import org.servlet.unit.format.ServletsTestCase;

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