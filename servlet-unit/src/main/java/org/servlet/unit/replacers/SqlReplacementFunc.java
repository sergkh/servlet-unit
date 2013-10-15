package org.servlet.unit.replacers;

/*
 * Copyright (c) 2011 by Global Money Ukraine.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Global Money Ukraine.
 *  
 * Created on Dec 19, 2011, 3:12:32 PM
 * Author Sergey Khruschak
 */

import static org.junit.Assert.fail;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.servlet.unit.ReplaceFunction;
import org.servlet.unit.util.TestCase;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 
 * @author Sergey.Khruschak
 */
public class SqlReplacementFunc implements ReplaceFunction {

	private int order = 200;

	private JdbcTemplate template;

	public SqlReplacementFunc() {
	}

	public SqlReplacementFunc(ApplicationContext appContext) {
		try {
			template = appContext.getBean(JdbcTemplate.class);
		} catch (NoSuchBeanDefinitionException nbe) {
			template = null;
		}
	}

	@Override
	public int getOrder() {
		return order;
	}

	@Override
	public void setOrder(int order) {
		this.order = order;
	}

	public Pattern getPattern() {
		return Pattern.compile("sql\\{(.*?)\\}(\\[([0-9]*)\\])?",
				Pattern.MULTILINE);
	}

	@Override
	public String replace(Matcher matcher, TestCase replacer) {

		// if no manager exists - just remove matched string
		if (template == null)
			return "";

		final String sql = matcher.group(1);

		try {
			Object obj = template.queryForObject(sql, String.class);
			return obj.toString();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error retrieving entity from database for query: " + sql
					+ "\n" + replacer.getTest().toString());
		}

		return "";
	}
}