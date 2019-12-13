/*
 * RuntimeXmlApplicationContext.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics 
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.support;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.core.io.Resource;

/**
 * 
 * **/
public class RuntimeXmlApplicationContext
	extends AbstractXmlApplicationContext
{
	private Resource[] configResources;

	/**
	 * Creates a new RuntimeXmlApplicationContext object.
	 **/
	public RuntimeXmlApplicationContext()
	{
		super();
	}

	/**
	 * Creates a new RuntimeXmlApplicationContext object.
	 *
	 * @param parent DOCUMENT ME!
	 **/
	public RuntimeXmlApplicationContext(ApplicationContext parent)
	{
		super(parent);
	}

	/**
	 * Creates a new RuntimeXmlApplicationContext object.
	 *
	 **/
	public RuntimeXmlApplicationContext(Resource... resources)
	{
		super();
		this.configResources = resources;
	}

	@Override
	public Resource[] getConfigResources()
	{
		return this.configResources;
	}
}
