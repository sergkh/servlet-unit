package org.servletunit.replacers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.servletunit.ReplaceFunction;
import org.servletunit.TestCase;
import org.springframework.stereotype.Component;

/**
 * Replaces occurrences of ${varName} to varName value in all functions.
 * 
 * @author Sergey.Khruschak
 */
@Component
public class VariablesInsertFunc implements ReplaceFunction {

	private int order = 100;

	@Override
	public int getOrder() {
		return order;
	}

	@Override
	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	// like: "${test1}" or "${test2(1, 'dde')}" or "${test3()}"
	public Pattern getPattern() {
		return Pattern.compile(
				"\\$\\{([\\p{Alnum}\\.]*?)(\\(([^//)]*)\\))?\\}",
				Pattern.MULTILINE);
	}

	@Override
	public String replace(Matcher matcher, TestCase replacer) {
		String varName = matcher.group(1);

		if (!replacer.getTest().getVariables().containsKey(varName))
			throw new IllegalStateException("No variable named: '" + varName
					+ "' exists in context");

		return replacer.getTest().getVariables().get(varName)
				.evaluate(matcher.group(3), replacer);
	}
}