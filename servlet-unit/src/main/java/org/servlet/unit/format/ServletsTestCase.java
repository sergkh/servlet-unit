/*
 * Copyright (c) 2011 by Global Money Ukraine.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Global Money Ukraine.
 *  
 * Created on 03.06.2011, 14:52:17
 * Author Vladimir Khruschak
 */
package org.servlet.unit.format;

import java.util.List;
import java.util.Map;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;

/**
 * Contains test data for serialized object.
 * 
 * @author Vladimir Khruschak
 * @author Sergey Khruschak
 */
public class ServletsTestCase {

	@Attribute(name = "className", required = false)
	private String className;

	@Attribute(name = "method", required = false)
	private String method;

	@Attribute(name = "path-info", required = false)
	private String pathInfo;

	@Attribute(name = "url", required = false)
	private String url;

	@Attribute(name = "params", required = false)
	private String parameters;

	@Attribute(name = "timeLimit", required = false)
	private int timeLimit;

	@Attribute(name = "filters", required = false)
	private String filtersClasses;

	@ElementMap(entry = "property", key = "key", attribute = true, inline = true, required = false)
	private Map<String, String> propertiesMap;

	@ElementMap(entry = "header", key = "name", attribute = true, inline = true, required = false)
	private Map<String, String> headers;

	@Element(data = true, name = "request", required = false)
	private String request;

	@Element(data = true, name = "response", required = false)
	private ServletResponse response;

	@ElementList(inline = true, entry = "check-db", required = false, type = ServletTestDbVerify.class)
	private List<ServletTestDbVerify> databaseValidations;

	private transient Map<String, ServletsVar> variables;

	private transient String filename;

	public ServletsTestCase() {
	}

	/**
	 * @return the headers
	 */
	public Map<String, String> getHeaders() {
		return headers;
	}

	/**
	 * @param headers
	 *            the headers to set
	 */
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	/**
	 * Sets HTTP method to use in request.
	 * 
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * Sets HTTP method to use in request.
	 * 
	 * @param method
	 *            the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * The extra path information follows the servlet path but precedes the
	 * query string and will start with a "/" character.
	 * 
	 * @return the pathInfo
	 */
	public String getPathInfo() {
		return pathInfo;
	}

	/**
	 * Sets the extra path information follows the servlet path but precedes the
	 * query string and will start with a "/" character.
	 * 
	 * @param pathInfo
	 *            the pathInfo to set
	 */
	public void setPathInfo(String pathInfo) {
		this.pathInfo = pathInfo;
	}

	/**
	 * Returns URL of this request, alternative to servlet name usage. Also can
	 * be used aide with {@link #pathInfo} - in this case they will be simply
	 * concatenated.
	 * 
	 * @return request URL.
	 */
	public String getUrl() {
		return url;
	}

	public boolean hasUrl() {
		return url != null;
	}

	/**
	 * Sets URL of this request, alternative to servlet name usage. Also can be
	 * used aide with {@link #pathInfo} - in this case they will be simply
	 * concatenated.
	 * 
	 * @param url
	 *            request URL.
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Returns servlet filter class names to apply.
	 * 
	 * @return servlet filter class names to apply.
	 */
	public String getFiltersClasses() {
		return filtersClasses;
	}

	/**
	 * Sets servlet filter class names to apply before exit.
	 * 
	 * @param filterClass
	 *            servlet filters to apply.
	 */
	public void setFiltersClasses(String filterClass) {
		this.filtersClasses = filterClass;
	}

	/**
	 * @return the databaseCheck
	 */
	public List<ServletTestDbVerify> getDatabaseValidations() {
		return databaseValidations;
	}

	/**
	 * @param databaseCheck
	 *            the databaseCheck to set
	 */
	public void setDatabaseValidations(List<ServletTestDbVerify> databaseCheck) {
		this.databaseValidations = databaseCheck;
	}

	/**
	 * @return the response
	 */
	public String getResponseBody() {
		return response.getText();
	}

	/**
	 * @param response
	 *            the response to set
	 */
	public void setResponse(ServletResponse response) {
		this.response = response;
	}

	public ServletResponse getResponse() {
		return response;
	}

	public int getResponseCode() {
		return response.getCode();
	}

	/**
	 * @return the request
	 */
	public String getRequest() {
		return request;
	}

	/**
	 * @param request
	 *            the request to set
	 */
	public void setRequest(String request) {
		this.request = request;
	}

	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @param className
	 *            the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * @return the propertiesMap
	 */
	public Map<String, String> getPropertiesMap() {
		return propertiesMap;
	}

	/**
	 * @param propertiesMap
	 *            the propertiesMap to set
	 */
	public void setPropertiesMap(Map<String, String> propertiesMap) {
		this.propertiesMap = propertiesMap;
	}

	/**
	 * Returns variables map.
	 * 
	 * @return the variables
	 */
	public Map<String, ServletsVar> getVariables() {
		return variables;
	}

	/**
	 * Sets variables map.
	 * 
	 * @param variables
	 *            the variables to set
	 */
	public void setVariables(Map<String, ServletsVar> variables) {
		this.variables = variables;
	}

	/**
	 * Returns request parameters.
	 * 
	 * @return the parameters
	 */
	public String getParameters() {
		return parameters;
	}

	/**
	 * Sets request parameters.
	 * 
	 * @param parameters
	 *            the parameters to set
	 */
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * Returns limits of request's runtime
	 * 
	 * @return the timeLimit
	 */
	public int getTimeLimit() {
		return timeLimit;
	}

	/**
	 * Sets limits of request's runtime
	 * 
	 * @param timeLimit
	 */
	public void setTimeLimit(int timeLimit) {
		this.timeLimit = timeLimit;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ServletsTestCase [");
		if (filename != null)
			builder.append("filename=").append(filename).append(", ");
		if (url != null)
			builder.append("url=").append(url).append(", ");
		if (request != null)
			builder.append("request=").append(request).append(", ");
		if (response != null)
			builder.append("response=").append(response);
		builder.append("]");
		return builder.toString();
	}
}