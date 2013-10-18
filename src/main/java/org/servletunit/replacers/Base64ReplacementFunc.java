package org.servletunit.replacers;

import static org.junit.Assert.fail;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.servletunit.ReplaceFunction;
import org.servletunit.util.Base64;
import org.servletunit.TestCase;
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
