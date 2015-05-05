package org.servletunit.comparators;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.servletunit.TestScriptEngine;
import org.servletunit.TestResultComparator;
import org.servletunit.format.ServletsTestCase;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.fail;

/**
 * Comparator for two Json objects.
 * 
 * @author Sergey.Khruschak
 * @author Yaroslav.Derman
 *
 */
@Component("json")
public class JsonComparator implements TestResultComparator {

	public static final String TYPE = "json";
	private ObjectMapper mapper = new ObjectMapper();

	public JsonComparator() {
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public void compareResponse(ServletsTestCase test, String expected,
			String actual, TestScriptEngine engine) {

		Map<String, Object> expNode = null;
		try {
			expNode = mapper.readValue(
					new ByteArrayInputStream(expected.getBytes()),
					new TypeReference<Map<String, Object>>() {
					});
		} catch (Exception e) {
			e.printStackTrace();
			fail("Can't read JSON data: " + expected);
		}

		Map<String, Object> actualNode = null;
		try {
			actualNode = mapper.readValue(
					new ByteArrayInputStream(actual.getBytes()),
					new TypeReference<Map<String, Object>>() {
					});

			if (actualNode == null)
				throw new NullPointerException("Response is null");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Can't read JSON data: " + actual + "\t Test:" + test);
		}

		Map<String, Object> sortedExpNode = toSortedMap(expNode);
		Map<String, Object> sortedActualNode = toSortedMap(actualNode);

		try {
			jsonCompare(sortedExpNode, sortedActualNode, "root", engine);
		} catch (Exception ex) {
			ex.printStackTrace();
			Assert.assertEquals("Json strings aren't same:" + test.toString(),
					expected, actual);
		}

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	// only fields has to be sorted
	private Map<String, Object> toSortedMap(Map<String, Object> expNode) {
		LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
		List<String> keys = new ArrayList<String>(expNode.keySet());

		Collections.sort(keys);

		for (String key : keys) {
			Object val = expNode.get(key);

			if (val instanceof List) {
				List<Object> list = new ArrayList<Object>(((List) val).size());
				for (Object o : (List) val) {
					if (o instanceof Map) {
						o = toSortedMap((Map<String, Object>) o);
					}
					list.add(o);
				}

				val = list;
			}

			if (val instanceof Map) {
				val = toSortedMap((Map<String, Object>) val);
			}

			map.put(key, val);
		}

		return map;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void jsonCompare(Object exp, Object actual,
			String context, TestScriptEngine engine) {
		if (exp instanceof Map) {
			Map<String, Object> expMap = (Map<String, Object>) exp;

			if (expMap.size() != ((Map) actual).size())
				throw new RuntimeException("Maps aren't equal:" + exp + " <> "
						+ actual + ", on: " + context);

			for (Entry<String, Object> e : expMap.entrySet()) {

				if (!((Map) actual).containsKey(e.getKey())) {
					throw new RuntimeException(
							"Actual value doesn't contains key: " + e.getKey());
				}

				jsonCompare(e.getValue(), ((Map) actual).get(e.getKey()),
						context + "." + e.getKey(), engine);
			}

		} else if (exp instanceof List) {
			List expList = (List) exp;

			if (expList.size() != ((List) actual).size())
				throw new RuntimeException("Lists aren't equal:" + exp + " <> "
						+ actual + ", on: " + context);

			for (int i = 0; i < expList.size(); i++) {
				jsonCompare(expList.get(i), ((List) actual).get(i),
						context, engine);
			}

		} else {
			if (!checkVar(String.valueOf(exp), String.valueOf(actual), engine) && !"{*}".equals(exp)) {
				throw new RuntimeException("Fields aren't equal:" + exp
						+ " <> " + actual + ", on: " + context);
			}
		}
	}

	public static boolean checkVar(String exp, String actual, TestScriptEngine engine) {

		String result = String.valueOf(engine.eval(exp, actual));

		return Boolean.valueOf(result) || result.equals(actual);

	}
}
