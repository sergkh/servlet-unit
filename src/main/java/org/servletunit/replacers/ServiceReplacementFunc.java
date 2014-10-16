package org.servletunit.replacers;

import org.servletunit.ReplaceFunction;
import org.servletunit.TestCase;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.activation.UnsupportedDataTypeException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.fail;

/**
 * Created by yaroslav on 10/6/14.
 */
@Component
public class ServiceReplacementFunc implements ReplaceFunction {

    private int order = 150;

    @Autowired
    private ApplicationContext appContext;

    private Object serviceEntity;

    private static final String   EMPTY_STRING = "";
    private static final String   PACKAGE = "ua.globalmoney.gmws.service.";

    public org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

    public ServiceReplacementFunc() {
    }

    /*public ServiceReplacementFunc(ApplicationContext appContext) {
        this.appContext = appContext;
    }*/

    @Override
    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public Pattern getPattern() {

        return Pattern.compile("\\#\\{(\\w+)\\.(\\w+)\\(([\\w\\.,\\s]*?)\\)\\}");
    }

    @Override
    public String replace(Matcher matcher, TestCase replacer) {

        String result = null;

        String serviceName = matcher.group(1);
        serviceName = serviceName.substring(0, 1).toUpperCase() + serviceName.substring(1);

        String methodName = matcher.group(2);

        List<String> variableList = new ArrayList<String>();

        String[] inputValues = matcher.group(3).split(",");

        for (int i=0; i < inputValues.length; i++){
            String value = inputValues[i].trim();
            if (value != null && !EMPTY_STRING.equals(value)) {
                variableList.add(value);
            }
        }

        try {

            serviceEntity = appContext.getBean(Class.forName(PACKAGE + serviceName));

            List<Object> transformVariables = new ArrayList<Object>();

            Method[] allMethods = serviceEntity.getClass().getMethods();

            for(Method singleMethod: allMethods){
                Type[] allMethodTypes = singleMethod.getGenericParameterTypes();

                if (singleMethod.getName().equals(methodName)){

                    if (allMethodTypes.length == variableList.size()){

                        for (int i=0; i < variableList.size(); i++){
                            Type singleType = allMethodTypes[i];

                            if (singleType.equals(Integer.class)){
                                transformVariables.add(Integer.valueOf(variableList.get(i)));
                            } else if (singleType.equals(Long.class)){
                                transformVariables.add(Long.valueOf(variableList.get(i)));
                            } if (singleType.equals(String.class)){
                                transformVariables.add(String.valueOf(variableList.get(i)));
                            } else throw new UnsupportedDataTypeException("Unsupported data type for " + singleType);
                        }
                        result = singleMethod.invoke(serviceEntity, transformVariables.toArray()).toString();
                    } else {
                        fail("Different variable count for method <" + serviceName + "." +
                                singleMethod.getName() + "> required:" +
                                allMethodTypes.length + ", actual:" + variableList.size());
                        throw new IllegalArgumentException("Do not match argument counts for method " + methodName);
                    }
                }
            }

        } catch (NoSuchBeanDefinitionException x) {
            fail("Failed create bean definition" + x.getBeanName());

        } catch (ClassNotFoundException x) {
            fail("Class " + x.getMessage() + " not found");
        } catch (InvocationTargetException x){
            fail("Can not invoke method for " + serviceEntity.toString() + ", " + x.getMessage());
        } catch (ReflectiveOperationException x) {
            fail(x.toString());
        } catch (UnsupportedDataTypeException x){
            fail(x.getMessage());
        }

        return result;
    }
}

