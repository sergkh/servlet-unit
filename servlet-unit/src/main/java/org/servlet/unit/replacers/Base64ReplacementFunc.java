/*
 * Copyright (c) 2011 by Global Money Ukraine.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Global Money Ukraine.
 *  
 * Created on Dec 19, 2011, 3:13:09 PM
 * Author Sergey Khruschak
 */
package org.servlet.unit.replacers;

import static org.junit.Assert.fail;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.servlet.unit.ReplaceFunction;
import org.servlet.unit.util.Base64;
import org.servlet.unit.util.TestCase;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Sergey.Khruschak
 */
@Component
public class Base64ReplacementFunc implements ReplaceFunction {

	private int order = 1000;

	@Override
	public int getOrder() {
		return order;
	}

	@Override
	public void setOrder(int order) {
		this.order = order;
	}

	public Pattern getPattern() {
		return Pattern.compile("Base64\\{(.*?)\\}", Pattern.MULTILINE);
	}

	@Override
	public String replace(Matcher matcher, TestCase replacer) {
		try {
			return Base64.encodeToString(matcher.group(1).getBytes(), false);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error while Base64 encoding string: " + matcher.group(1));
		}

		return null;
	}
}
