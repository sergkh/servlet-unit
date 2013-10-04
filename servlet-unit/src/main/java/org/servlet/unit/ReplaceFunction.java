/*
 * Copyright (c) 2011 by Global Money Ukraine.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Global Money Ukraine.
 *  
 * Created on Dec 19, 2011, 3:05:27 PM
 * Author Sergey Khruschak
 */
package org.servlet.unit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.servlet.unit.util.TestCase;

/**
 * 
 * @author Sergey.Khruschak
 */
public interface ReplaceFunction {
	
	public void setOrder(int order);

	public int getOrder();

	public Pattern getPattern();

	public String replace(Matcher matcher, TestCase replacer);
}