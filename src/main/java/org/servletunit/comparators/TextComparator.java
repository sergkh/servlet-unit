package org.servletunit.comparators;

import org.junit.Assert;
import org.servletunit.TestResultComparator;
import org.servletunit.format.ServletsTestCase;
import org.springframework.stereotype.Component;

/**
 * Comparator for two Text objects.
 * 
 * @author Sergey.Khruschak
 */
@Component("text")
public class TextComparator implements TestResultComparator {

	public static final String TYPE = "text";

	public TextComparator() {
	}
	
	@Override
	public String getType() {
		return TYPE;
	}
	
	@Override
	public void compareResponse(ServletsTestCase test, String expected, String actual) {
		expected = expected.replaceAll("\\s", ""); 
		actual = actual.replaceAll("\\s", "");
		
		try {
			while (expected.contains("{*}")) {
				int beginIndex = expected.indexOf("{*}");
				expected = expected.replaceFirst("\\{\\*\\}", "*");
				int endIndex = beginIndex;
	
				int minIndex = actual.length();
				final String stopSymbols = "\"<,}'";
				
				for(char ch : stopSymbols.toCharArray()) {
					int idx = actual.indexOf("" + ch, beginIndex);
					if(idx != -1 && minIndex > idx) minIndex = idx;
				}
				
				endIndex = minIndex;
				
				if(endIndex == -1) endIndex = actual.length();
				
				actual = actual.substring(0, beginIndex) + "*" + actual.substring(endIndex, actual.length());
			}
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			Assert.assertEquals("Servlet response doesn't match for " + test.toString(), expected, actual);
		}
	}
}
