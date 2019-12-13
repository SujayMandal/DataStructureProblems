/*
 * IntegrationFlow.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.repository;

import java.io.Serializable;

import org.springframework.core.io.Resource;

import com.ca.umg.rt.flows.generator.FlowMetaData;

/**
 * Metadata information for an integration flow.
 *
 * @author devasiaa
 **/
public class IntegrationFlow
	implements Serializable
{
	private static final long serialVersionUID = 6453885152219777737L;
	private String            flowName;
	private String            description;
	private Resource          resource;
	private FlowMetaData      flowMetadata;

	/**
	 * Name of this integration flow
	 *
	 * @return the flowName
	 **/
	public String getFlowName()
	{
		return flowName;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param flowName DOCUMENT ME!
	 **/
	public void setFlowName(String flowName)
	{
		this.flowName                          = flowName;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return the description
	 **/
	public String getDescription()
	{
		return description;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param description the description to set
	 **/
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * Resource used to build the integration flow.
	 *
	 * @return
	 **/
	public Resource getResource()
	{
		return resource;
	}

	/**
	 * Sets the {@link Resource} used for the integration flow.
	 *
	 * @param resource
	 **/
	public void setResource(Resource resource)
	{
		this.resource = resource;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 **/
	public FlowMetaData getFlowMetadata()
	{
		return flowMetadata;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param flowMetadata DOCUMENT ME!
	 **/
	public void setFlowMetadata(FlowMetaData flowMetadata)
	{
		this.flowMetadata = flowMetadata;
	}
}
