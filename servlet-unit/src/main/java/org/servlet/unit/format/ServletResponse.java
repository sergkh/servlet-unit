/*
 * Copyright (c) 2011 by Global Money Ukraine.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Global Money Ukraine.
 *  
 * Created on Aug 2, 2012, 5:56:50 PM
 * Author Sergey Khruschak
 */
package org.servlet.unit.format;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Text;

/**
 * Servlet response description object
 * @author Sergey.Khruschak
 */
public class ServletResponse {

	@Text(data = true, required = false)
	private String text;
	
	@Attribute(name = "code", required = false)
	private int code = 200;
	
	@Attribute(name="type", required = false)
	private String type = "text";

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Code: " + code + ", Body(" + type + "): " + text;
	}
}
