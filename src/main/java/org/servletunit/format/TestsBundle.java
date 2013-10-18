package org.servletunit.format;

import java.util.List;
import java.util.regex.Pattern;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

/**
 * Represents tests bundle.
 * 
 * @author Sergey.Khruschak
 */
@Root
@Namespace(reference = "http://www.w3.org/2001/XMLSchema-instance")
public class TestsBundle {

	@Attribute(name = "noNamespaceSchemaLocation", required = false)
	private String noNamespaceSchemaLocation;

	@ElementList(name = "aliases", entry = "alias", type = Alias.class, required = false)
	private List<Alias> aliases;

	@ElementListUnion({ @ElementList(entry = "test", type = String.class, inline = true),
	// @ElementList(entry="action", type=String.class, inline=true)
	})
	private List<String> tests;

	public TestsBundle() {
	}

	/**
	 * @return the tests
	 */
	public List<String> getTests() {
		return tests;
	}

	/**
	 * @param tests
	 *            the tests to set
	 */
	public void setTests(List<String> tests) {
		this.tests = tests;
	}

	public List<Alias> getAliases() {
		return aliases;
	}

	public void setAliases(List<Alias> aliases) {
		this.aliases = aliases;
	}

	public static class Alias {
		@Attribute(name = "pattern", required = true)
		private String pattern;

		@Attribute(name = "servlet", required = true)
		private String className;

		private transient Pattern urlPattern;

		public String getPattern() {
			return pattern;
		}

		public void setPattern(String pattern) {
			this.pattern = pattern;
		}

		public String getClassName() {
			return className;
		}

		public void setClassName(String className) {
			this.className = className;
		}

		public Pattern getCompiledPattern() {
			if (urlPattern != null)
				return urlPattern;

			if (pattern.endsWith("*")) {
				urlPattern = Pattern.compile(pattern.replace("*",
						"([^\\?]*)?(\\?.+)?"));
			} else {
				urlPattern = Pattern.compile(pattern + "()(\\?.+)?");
			}

			return urlPattern;
		}
	}	
}