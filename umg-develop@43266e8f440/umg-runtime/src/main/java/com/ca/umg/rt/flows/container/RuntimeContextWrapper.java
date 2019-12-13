/*
 * RuntimeContextWrapper.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.flows.container;

import java.io.Serializable;

import org.springframework.context.ApplicationContext;

import com.ca.umg.rt.repository.IntegrationFlow;

/**
 * 
 **/
public class RuntimeContextWrapper
	implements Serializable
{
	private static final long  serialVersionUID   = -8645216491091306494L;
	private IntegrationFlow    integrationFlow;
	private FlowStatus         flowStatus;
	private String             errorMessage;
	private ApplicationContext applicationContext;

	/**
	 * DOCUMENT ME!
	 *
	 * @return the integrationFlow
	 **/
	public IntegrationFlow getIntegrationFlow()
	{
		return integrationFlow;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param integrationFlow the integrationFlow to set
	 **/
	public void setIntegrationFlow(IntegrationFlow integrationFlow)
	{
		this.integrationFlow                      = integrationFlow;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return the flowStatus
	 **/
	public FlowStatus getFlowStatus()
	{
		return flowStatus;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param flowStatus the flowStatus to set
	 **/
	public void setFlowStatus(FlowStatus flowStatus)
	{
		this.flowStatus = flowStatus;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return the applicationContext
	 **/
	public ApplicationContext getApplicationContext()
	{
		return applicationContext;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param applicationContext the applicationContext to set
	 **/
	public void setApplicationContext(ApplicationContext applicationContext)
	{
		this.applicationContext = applicationContext;
	}

	/**
	 * 
	DOCUMENT ME!
	 *
	 * @return the errorMessage
	 **/
	public String getErrorMessage()
	{
		return errorMessage;
	}

	/**
	 * 
	DOCUMENT ME!
	 *
	 * @param errorMessage the errorMessage to set
	 **/
	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}
}
