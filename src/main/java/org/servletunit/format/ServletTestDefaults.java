package org.servletunit.format;

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