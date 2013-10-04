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

/**
 * @author Vladimir.Khruschak
 */
package org.servlet.unit.util;

import org.servlet.unit.format.ServletsTestCase;


public interface TestCase {
	public String replaceVars(String requestData);
	public ServletsTestCase getTest();
}
