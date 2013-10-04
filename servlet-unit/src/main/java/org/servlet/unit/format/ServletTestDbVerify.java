/*
 * Copyright (c) 2011 by Global Money Ukraine.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Global Money Ukraine.
 *  
 * Created on 5 Jan. 2011, 21:36:53
 * Author Vladimir Khruschak
 */
package org.servlet.unit.format;

import java.util.Map;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementMap;

/**
 * 
 * @author Vladimir Khruschak
 */
public class ServletTestDbVerify {

	@Attribute(name = "query")
	private String query;

	@ElementMap(entry = "property", key = "key", attribute = true, inline = true, required = false)
	private Map<String, String> propertiesMap;

	public ServletTestDbVerify() {
	}

	/**
	 * @return the query
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * @param query
	 *            the query to set
	 */
	public void setQuery(String query) {
		this.query = query;
	}

	/**
	 * @return the propertiesMap
	 */
	public Map<String, String> getPropertiesMap() {
		return propertiesMap;
	}

	/**
	 * @param propertiesMap
	 *            the propertiesMap to set
	 */
	public void setPropertiesMap(Map<String, String> propertiesMap) {
		this.propertiesMap = propertiesMap;
	}

}
