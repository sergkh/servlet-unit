package org.servlet.unit.replacers;

/*
 * Copyright (c) 2011 by Global Money Ukraine.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Global Money Ukraine.
 *  
 * Created on Dec 19, 2011, 3:12:32 PM
 * Author Sergey Khruschak
 */
import static org.junit.Assert.fail;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.servlet.unit.ReplaceFunction;
import org.servlet.unit.util.TestCase;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;


/**
 * 
 * @author Sergey.Khruschak
 */
@Component
public class JpqlReplacementFunc implements ReplaceFunction {

	private EntityManager entityManager;

    private int order = 200;
    
    public JpqlReplacementFunc() {
	}

	public JpqlReplacementFunc(ApplicationContext appContext) {
    	 try {
             EntityManagerFactory em = appContext.getBean(EntityManagerFactory.class);
             entityManager = em.createEntityManager();
         } catch (NoSuchBeanDefinitionException nbe) {
             entityManager = null;
         }
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public void setOrder(int order) {
        this.order = order;
    }

    public Pattern getPattern() {
        return Pattern.compile("jpql\\{(.*?)\\}(\\[([0-9]*)\\])?", Pattern.MULTILINE);
    }
	
	@Override
	public String replace(Matcher matcher, TestCase replacer) {

        // if no manager exists - just remove matched string
        if(entityManager == null) return "";

		int index = (matcher.group(2) != null) ?  Integer.parseInt(matcher.group(2)) : 0;
		
		final String sql = matcher.group(1);
		
		try {
			Object obj = entityManager.createQuery(sql).setFirstResult(index).setMaxResults(1).getSingleResult();
			return obj.toString();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error retrieving entity from database for query: " + sql + "\n" + replacer.getTest().toString());
		}
		
		return null;
	}
}