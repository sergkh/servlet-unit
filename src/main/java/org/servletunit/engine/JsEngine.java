package org.servletunit.engine;

import org.servletunit.TestScriptEngine;
import org.servletunit.format.ServletTestsSet;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.fail;

/**
 * Created by yaroslav on 06/04/15.
 *
 * @author Sergey.Khruschak
 * @author Yaroslav.Derman
 *
 */
public class JsEngine implements TestScriptEngine {

    private ApplicationContext springContext;

    public ScriptEngine scriptEngine;

    private static final Pattern JS_PATTERN = Pattern.compile("\\#(.*?)\\#", Pattern.MULTILINE);

    @Autowired
    public JsEngine(ApplicationContext appContext) {
        this.springContext = appContext;
    }

    @Override
    public void init(ServletTestsSet test) {
        scriptEngine = new ScriptEngineManager().getEngineByName("JavaScript");
        scriptEngine.put("__ctx", this);

        if (test.getTestScripts() != null) {
            for (String function: test.getTestScripts()) {
                try {
                    scriptEngine.eval(injectContext(function.trim()));
                } catch (ScriptException e) {
                    e.printStackTrace();
                    fail("Error execute script " + function);
                }
            }
        }
    }

    @Override
    public void clearContext() {
        scriptEngine = new ScriptEngineManager().getEngineByName("JavaScript");
        scriptEngine.put("__ctx", this);
    }

    /**
     *
     * @param requestData - java script body =walletUuid or var a = getEmailCode(uuid); a
     * @param actualData - value that must be put into bindings
     * @return
     */
    /*{
				"transactionId":"@{paymentId}",
				"source":"{*}",
				"destination":"{*}",
				"amount":100000,
				"balance":300000,
				"timestamp":"{*}",
				"status" : 0
			}
	*/
    public Object eval(String requestData, String actualData) {

        //System.out.println("**********" + requestData);

        if (!isNullOrEmpty(requestData)) {

            //System.out.println("**********" + requestData);

            Matcher m = Pattern.compile("\\#(.*?)\\#",
                    Pattern.MULTILINE).matcher(requestData);

            StringBuffer sb = new StringBuffer();

            while (m.find()) {

                //String script = scriptCreation(m.group(1));
                //System.out.println("************ Replace Group 1 " + m.group(1));
                //System.out.println("************ Replace script " + script);

                String body2 = null;

                if (m.group(1).startsWith("=") && actualData != null) {
                    body2 = String.format("%s = arg; true;", m.group(1).replace("=", ""));
                } else {
                    body2 = m.group(1);
                }

                //return getEmailCode(walletUuid)
                String body = scriptCreation(body2);
                String function = "function __exec(arg){" + injectContext(body) + "}";

                //System.out.println("/0/0/0/0/0/0/0/" + function + ", arg " + actualData);

                try {
                    scriptEngine.eval(function);
                    Object result = ((Invocable) scriptEngine).invokeFunction("__exec", actualData);
                    //System.out.println("/0/0/0/0/0/0/0/" + result);
                    m.appendReplacement(sb, String.valueOf(result));
                } catch (ScriptException e) {
                    e.printStackTrace();
                }  catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }

            m.appendTail(sb);
            requestData = sb.toString();
        } else {
            requestData = "";
        }

        return requestData;

        /*try {
            // evaluate JavaScript code from String

            if (request.startsWith("=") && arg != null) {
                request = String.format("%s = arg; true;", request.replace("=", ""));
            }

            //return getEmailCode(walletUuid)
            String body = scriptCreation(request.trim());
            String function = "function __exec(arg){ " + injectContext(body) + "}";

            System.out.println("/0/0/0/0/0/0/0/" + function + ", arg " + arg);

            scriptEngine.eval(function);

            Object result = ((Invocable) scriptEngine).invokeFunction("__exec", arg);

            System.out.println("Invoke result " + result);

            return result;
        } catch (ScriptException se) {
            se.printStackTrace();
            fail("Error execute script: " + request);
        } catch (NoSuchMethodException ne) {
            ne.printStackTrace();
            fail("No such method found: " + ne.getMessage());
        }

        return null;*/
    }

    private String injectContext(String body) {
        StringBuffer sb = new StringBuffer();
        Pattern pattern = Pattern.compile("([A-Za-z]+)\\.");
        Matcher m = pattern.matcher(body);

        while (m.find()) {
            String obj = m.group(1);
            m.appendReplacement(sb, replaceBeanName(obj) + '.');
        }

        m.appendTail(sb);

        return sb.toString();
    }

    private String replaceBeanName(String name) {

        if (springContext.containsBean(name)) {
            return "__ctx.byName('" + name + "')";
        }  else {
            // not a spring bean, just keep it as it was
            return name;
        }
    }

    public Object byName(String s) {
        try {
            return springContext.getBean(s);
        } catch (NoSuchBeanDefinitionException notFoudEx) {
            return null;
        }
    }

    public String scriptCreation(String requestData) {

        Matcher m = Pattern.compile("\\;([ \\n\\w\\.\\(\\)\\'\\:\\@\\!\\+]+);?$")
                .matcher(requestData.trim());

        StringBuffer sb = new StringBuffer();

        //System.out.println("+++++++++++++ Script Creation " + requestData);

        if (m.find()) {
            m.appendReplacement(sb, String.valueOf("; return " + m.group(1).trim() + ";"));
            //System.out.println("Find" + sb.toString());
        } else {
            sb.append(" return " + requestData.trim() + ";");
            //System.out.println("No Find" + sb.toString());
        }

        //m.appendTail(sb);
        //System.out.println("--------Result script " + sb.toString());

        return sb.toString();
    }

    public boolean isNullOrEmpty(String string) {
        return (string == null || string.equals(""));
    }

    public ScriptEngine getScriptEngine() {
        return scriptEngine;
    }

    public void setScriptEngine(ScriptEngine scriptEngine) {
        this.scriptEngine = scriptEngine;
    }
}
