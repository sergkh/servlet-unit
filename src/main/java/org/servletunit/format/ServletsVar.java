package org.servletunit.format;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.servletunit.TestCase;

/**
 * @author Vladimir.Khruschak
 */
public class ServletsVar {

	private static final String SINGLE_PARAM_PATTERN = "('[^']*'|[^\\s,']+)";

	private List<String> values;
	private boolean function;

	public ServletsVar(List<String> values) {
		this.values = values;
		this.function = true;
	}

	public ServletsVar(String value) {
		List<String> values = new ArrayList<String>();
		values.add(value);

		this.values = values;
		this.function = false;
	}

	/**
	 * Returns variable or function value.
	 */
	public String evaluate(String paramsStr, TestCase replacer) {
		if (this.function) {
			List<String> results = this.values;

			for (String result : results) {

				if (!isNullOrEmpty(paramsStr)) {
					Pattern validationPattern = Pattern.compile("^(("
							+ SINGLE_PARAM_PATTERN + "\\s*,\\s*)*"
							+ SINGLE_PARAM_PATTERN + ")$");
					Matcher validationMatcher = validationPattern
							.matcher(paramsStr);

					if (!validationMatcher.find()) {
						throw new RuntimeException(
								"Function parameters syntax error: '"
										+ paramsStr + "'");
					}

					Pattern paramPattern = Pattern
							.compile(SINGLE_PARAM_PATTERN);
					Matcher paramMatcher = paramPattern.matcher(paramsStr);

					int paramIndex = 0;
					while (paramMatcher.find()) {
						paramIndex++;

						String param = paramsStr.substring(
								paramMatcher.start(), paramMatcher.end());

						if (param.charAt(0) == '\'')
							param = param.substring(1, param.length() - 1);

						result = result.replace("${" + paramIndex + "}", param);
					}
				}

				String value = replacer.replaceVars(result);

				if (!isNullOrEmpty(value)) {
					return value;
				}
			}

			throw new RuntimeException("No return value in function.");
		} else {
			return this.values.size() == 0 ? null : this.values.get(0);
		}
	}

	private boolean isNullOrEmpty(String string) {
		return (string == null || string.isEmpty());
	}
}