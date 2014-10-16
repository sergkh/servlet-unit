package org.servletunit;

/*
 * Copyright (c) 2011 by Global Money Ukraine.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Global Money Ukraine.
 *  
 * Created on 3 July. 2011, 01:06:07
 * Author Vladimir Khruschak
 */

import org.junit.Before;
import org.junit.Test;
import org.servletunit.format.*;
import org.servletunit.format.TestsBundle.Alias;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.inject.Inject;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


/**
 * Test for requests, all data got from xml file.
 * 
 * @author Vladimir Khruschak
 * @author Sergey Khruschak
 * @author Yaroslav Derman
 */
public abstract class AbstractServletTest {

	private String bundleXml;

    private ApplicationContext appContext;

    //public org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

    public List<Alias> aliases = Collections.emptyList();
	public List<ReplaceFunction> replacers = new ArrayList<ReplaceFunction>();
	public Map<String, TestResultComparator> comparators = new HashMap<String, TestResultComparator>();

	@Before
	public void initTest() throws Exception {
		initContext();

        replacers.addAll(getAppContext().getBeansOfType(ReplaceFunction.class).values());
	
		Collections.sort(replacers, new Comparator<ReplaceFunction>() {
            @Override
            public int compare(ReplaceFunction o1, ReplaceFunction o2) {
                return Integer.compare(o1.getOrder(), o2.getOrder());
            }
        });

        comparators.putAll(getAppContext().getBeansOfType(TestResultComparator.class));

	}


	protected abstract void initContext() throws Exception;

	@Test
	public void testAllServlets() throws Exception {
		List<ServletTestsSet> tests = readTestsSet(getBundleXml());

		// skip empty tests
		if (tests == null)
			return;

		for (ServletTestsSet set : tests) {
			Map<String, ServletsVar> varsContext = new HashMap<String, ServletsVar>();

			List<TestsFunction> functions = set.getTestFunctions();

			if (functions != null) {
				for (TestsFunction function : functions) {
					List<String> values = new ArrayList<String>();

					for (String value : function.getReturnValues()) {
						if (isNullOrEmpty(value)) {
							fail("Wrong return values");							
						} else {
							values.add(value);
						}
					}

					varsContext.put(function.getName(), new ServletsVar(values));
				}
			}

			for (ServletsTestCase test : set.getTestCases()) {
				parseParams(test);
				loadDefaults(test, set.getDefaults());
				test.setVariables(varsContext);
				testServlet(test);
			}
		}
	}

	private void testServlet(ServletsTestCase test) throws Exception {
		System.out.println("--- [ Executing " + test.getMethod() + " on " + test.getUrl() +
                            ", from file: " + test.getFilename() + " ] ---");

		HttpServletRequest hsRequest = createRequestStub(test);
		MockHttpServletResponse hsResponse = createResponseStub();
		HttpServlet defaultServlet = createChain(test);
		// actually does the servlet call
		long timeBefore, timeAfter, timeRequest = 0;
		try {
			timeBefore = System.currentTimeMillis();
			defaultServlet.service(hsRequest, hsResponse);
			timeAfter = System.currentTimeMillis();
			timeRequest = timeAfter - timeBefore;

			if ((test.getTimeLimit() != 0)
					&& (timeRequest > test.getTimeLimit())) {

				fail("Request execution failed. Runtime was out : expected "
						+ test.getTimeLimit() + " ms, but it was "
						+ timeRequest + " ms. " + test.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();

			fail("Servlet execution failed with exception: " + e.getMessage()
					+ " " + test.toString());
		}

		System.out.println("--- [ Request time is " + timeRequest + " miliseconds" + " of " + test.getMethod() + " on " +
                    test.getUrl() + ", from file: " + test.getFilename() + " ] --");

		TestCase replacer = new TestCaseImpl(replacers, test);

		final String response = replacer.replaceVars(test.getResponseBody());

		assertEquals("Servlet response status doesn't match: " + test,
				test.getResponseCode(), hsResponse.getStatus());

		if (test.getResponseCode() == 200) {
			compareResponse(test, response, hsResponse.getContentAsString());
		}

		/* validateDatabase(test.getDatabaseValidations(), test); */
	}

	private void loadDefaults(ServletsTestCase test,
			ServletTestDefaults defaults) {
		if (defaults != null) {

			for (ServletsTestCase defValues : defaults.getSettings()) {
				if (defValues.getClassName().equals(test.getClassName())) {
					test.setHeaders(mergeMaps(defValues.getHeaders(),
							test.getHeaders()));
					test.setPropertiesMap(mergeMaps(
							defValues.getPropertiesMap(),
							test.getPropertiesMap()));

					if (test.getMethod() == null
							&& defValues.getMethod() != null) {
						test.setMethod(defValues.getMethod());
					}

					if (test.getClassName() == null
							&& defValues.getClassName() != null) {
						test.setClassName(defValues.getClassName());
					}

					if (test.getRequest() == null
							&& defValues.getRequest() != null) {
						test.setRequest(defValues.getRequest());
					}

					if (test.getResponse() == null
							&& defValues.getResponse() != null) {
						test.setResponse(defValues.getResponse());
					}

					if (test.getFiltersClasses() == null
							&& defValues.getFiltersClasses() != null) {
						test.setFiltersClasses(defValues.getFiltersClasses());
					}

					if (test.getParameters() == null
							&& defValues.getParameters() != null) {
						test.setParameters(defValues.getParameters());
					}

					if (defValues.getDatabaseValidations() != null) {
						if (test.getDatabaseValidations() == null) {
							test.setDatabaseValidations(defValues
									.getDatabaseValidations());
						}

						for (ServletTestDbVerify verifier : defValues
								.getDatabaseValidations()) {
							test.getDatabaseValidations().add(verifier);
						}
					}
				}
			}
		}

		if (test.getMethod() == null) {
			test.setMethod("POST");
		}

	}

	/**
	 * Merges two properties maps into one. The values are put into out map only
	 * if map doesn't contain such value itself.
	 * 
	 * @param in
	 *            map to merge into.
	 * @param out
	 *            map to merge to.
	 * @return out map reference with merged values or new map if old map was
	 *         null and were created or null if map is null and shouldn't be
	 *         created.
	 */
	private Map<String, String> mergeMaps(Map<String, String> in,
			Map<String, String> out) {
		if (in == null)
			return out;
		if (out == null)
			return in;

		for (Entry<String, String> e : in.entrySet()) {
			if (!out.containsKey(e.getKey())) {
				out.put(e.getKey(), e.getValue());
			}
		}

		return out;
	}

	private MockHttpServletResponse createResponseStub() throws IOException {
		return new MockHttpServletResponse();
	}

	/**
	 * Creates request stub.
	 * 
	 * @param test
	 *            servlet test instance.
	 * 
	 * @return mock servlet request instance.
	 * @throws IOException
	 */
	private HttpServletRequest createRequestStub(ServletsTestCase test)
			throws IOException {
		TestCase replacer = new TestCaseImpl(replacers, test);

		MockHttpServletRequest hsRequest = new MockHttpServletRequest(
				test.getMethod(), replacer.replaceVars(test.getPathInfo()));

		Map<String, String> headers = test.getHeaders();

		if (headers != null) {
			for (Entry<String, String> header : headers.entrySet()) {
				hsRequest.addHeader(header.getKey(),
						replacer.replaceVars(header.getValue()));
			}
		}

		final String requestBody = replacer.replaceVars(test.getRequest() != null ? test.getRequest() : "");

		hsRequest.setContent(requestBody.getBytes());
		hsRequest.setPathInfo(replacer.replaceVars(test.getPathInfo()));

        hsRequest.setRemoteAddr("127.0.0.1");
        hsRequest.setRemoteHost("localhost");
        hsRequest.setRemotePort(16794);

		// very dummy implementation, fix it if u'll find some bugs
		if (test.getParameters() != null && !test.getParameters().isEmpty()) {
			String params = test.getParameters();
			if (params.startsWith("?"))
				params = params.substring(1);

			for (String param : params.split("&")) {
				String[] parVal = param.split("=");
				hsRequest.addParameter(parVal[0], parVal[1].split(","));
			}
		}

		return hsRequest;
	}

	private void compareResponse(ServletsTestCase test, String expected,
			String actual) {
		String respType = test.getResponse().getType();

		if (!comparators.containsKey(respType)) {
			fail("Unsupported response type: " + respType);
		}

		comparators.get(respType).compareResponse(test, expected, actual);
	}	

	/**
	 * Reads test descriptions from specified file.
	 * 
	 * @param fileName
	 * @return
	 */
	private List<ServletTestsSet> readTestsSet(String fileName) {
		TestsBundle bundle;

		List<ServletTestsSet> tests = new ArrayList<ServletTestsSet>();

		// Load tests set from xml in classpath
		try {
			Serializer serializer = new Persister();
			InputStream is = AbstractServletTest.class
					.getResourceAsStream(fileName);
			bundle = serializer.read(TestsBundle.class, is);
			is.close();

			if (bundle.getAliases() != null)
				aliases = bundle.getAliases();

			for (String testName : bundle.getTests()) {
				InputStream bundleStream = AbstractServletTest.class
						.getResourceAsStream(testName);
				ServletTestsSet set = serializer.read(ServletTestsSet.class,
						bundleStream);

				for (ServletsTestCase c : set.getTestCases()) {
					c.setFilename(testName);
				}

				tests.add(set);
				bundleStream.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
			fail("Test error: can not read test configuration!");
		}

		return tests;
	}

	/**
	 * Creates servlet object instance from it's description.
	 * 
	 * @param objDescr
	 *            description object.
	 * @return object instance
	 */
	private HttpServlet createChain(ServletsTestCase objDescr) {
		try {
			Class<?> clazz = Class.forName(objDescr.getClassName());
			BeanWrapper wrapper = new BeanWrapperImpl(clazz.newInstance());

			Map<String, String> props = objDescr.getPropertiesMap();

			initializeFields(clazz, wrapper.getWrappedInstance(), props);

			if (props != null) {
				for (String prop : props.keySet()) {
					String value = props.get(prop);
					if (value.startsWith("ref:")) {
						wrapper.setPropertyValue(
								prop,
								getAppContext().getBean(
										value.substring(4, value.length())));
					} else {
						wrapper.setPropertyValue(prop, value);
					}
				}
			}

			HttpServlet servlet = (HttpServlet) wrapper.getWrappedInstance();

			// MockServletContext context = new MockServletContext();
			// context.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
			// appContext);
			//
			// servlet.init(new MockServletConfig(context));

			return servlet;
		} catch (InstantiationException e) {
			e.printStackTrace();
			fail("Test error: wrong class name specified in test. Message: "
					+ e.getMessage() + "/nClass: " + objDescr.getClassName());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			fail("Test error: wrong class name specified in test. Message: "
					+ e.getMessage() + "/nClass: " + objDescr.getClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			fail("Test error: wrong class name specified in test. Message: "
					+ e.getMessage() + "/nClass: " + objDescr.getClassName());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test error! Message: " + e.getMessage() + "/nClass: "
					+ objDescr.getClassName());
		}

		return null;
	}

	/**
	 * Initialize autowired fields including those that are in super classes,
	 * skips fields that are declared manually in properties.
	 * 
	 * @param clazz
	 * @param o
	 * @param props
	 * @throws IllegalAccessException
	 */
	private void initializeFields(Class<?> clazz, Object o,
			Map<String, String> props) throws IllegalAccessException {
		Class<?> curClazz = clazz;

		while (curClazz != null) {
			Field[] fields = curClazz.getDeclaredFields();
			// autowire all services
			for (Field f : fields) {

				if (props != null && props.containsKey(f.getName()))
					continue; // will be set manually

				// autowire automatically
				if (f.isAnnotationPresent(Autowired.class)
						|| f.isAnnotationPresent(Inject.class)) {
					boolean oldState = f.isAccessible();
					f.setAccessible(true);
					f.set(o, getAppContext().getBean(f.getType()));
					f.setAccessible(oldState);
				}
			}

			curClazz = curClazz.getSuperclass();
		}
	}

	/**
	 * Builds servlet name, params and pathInfo from URL in case it is set.
	 * 
	 * @param test
	 *            test case.
	 */
	private void parseParams(ServletsTestCase test) {
		if (!test.hasUrl()) {
			if (test.getClassName() == null)
				throw new IllegalArgumentException(
						"Class name nor URL are set in test: " + test);
			return;
		}

		String path = null, params = null;
		Alias a = null;

		// match URL
		for (Alias alias : aliases) {
			Matcher m = alias.getCompiledPattern().matcher(test.getUrl());

			if (m.matches()) {
				a = alias;
				path = m.group(1);
				params = m.group(2);
				break;
			}
		}

		if (a == null) {
			throw new IllegalArgumentException("No alias foundfor url: "
					+ test.getUrl());
		}

		test.setClassName(a.getClassName());

		if (path != null && !path.isEmpty()) {
			if (!path.startsWith("/"))
				path = "/" + path;
			test.setPathInfo(path);
		}

		if (params != null && !params.isEmpty()) {
			if (params.startsWith("?"))
				params = params.substring(1);
			test.setParameters(params);
		}
	}	

	public ApplicationContext getAppContext() {
		return appContext;
	}

	public void setAppContext(ApplicationContext appContext) {
		this.appContext = appContext;
	}

	public String getBundleXml() {
		return bundleXml;
	}

	public void setBundleXml(String bundleXml) {
		this.bundleXml = bundleXml;
	}
	
	public boolean isNullOrEmpty(String string) {
		return (string == null || string.equals(""));
	}
}
