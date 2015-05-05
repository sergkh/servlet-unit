package org.servletunit;

import org.servletunit.format.ServletTestsSet;

/**
 * Created by yaroslav on 06/04/15.
 *
 * @author Yaroslav.Derman - yaroslav.derman@gmail.com
 */
public interface TestScriptEngine {

    public void init(ServletTestsSet test);

    public Object eval(String requestData, String actualData);

    public void clearContext();

}
