/*
 * Copyright (c) 2011 by Global Money Ukraine.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Global Money Ukraine.
 *  
 * Created on 16.09.2011, 9:36:09
 * Author Sergey Khruschak
 */
package org.servlet.unit.format;

import java.util.List;

import org.simpleframework.xml.ElementList;

/**
 * Default settings for servlets test.
 * 
 * @author Sergey.Khruschak
 */
public class ServletTestDefaults {

	@ElementList(inline = true, entry = "test", required = false)
	private List<ServletsTestCase> settings;

	public ServletTestDefaults() {
	}

	public List<ServletsTestCase> getSettings() {
		return settings;
	}

	public void setSettings(List<ServletsTestCase> settings) {
		this.settings = settings;
	}
}