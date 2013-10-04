/*
 * Copyright (c) 2011 by Global Money Ukraine.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Global Money Ukraine.
 *  
 * Created on 7.05.2013, 15:31:20
 * Author Vladimir Khruschak
 */
package org.servlet.unit.format;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

/**
 * Contains custom test functions for serialized object.
 * 
 * @author Vladimir.Khruschak
 */
public class TestsFunction {

	@Attribute(name = "name", required = true)
	private String name;

	@ElementList(inline = true, entry = "return", required = true, type = String.class)
	private List<String> returnValues;

	public TestsFunction() {
	}

	/**
	 * @return the returnValues
	 */
	public List<String> getReturnValues() {
		return returnValues;
	}

	/**
	 * @param returnValues
	 *            the returnValues to set
	 */
	public void setReturnValues(List<String> returnValues) {
		this.returnValues = returnValues;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("TestsFunction [name=").append(name).append("]");

		return builder.toString();
	}
}