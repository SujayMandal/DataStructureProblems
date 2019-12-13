/*
 * RuntimeBOImpl.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.core.runtime.bo;

import java.util.Collection;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ca.umg.rt.flows.container.ContainerManager;
import com.ca.umg.rt.repository.IntegrationFlow;

/**
 * 
 **/
@Component
public class RuntimeBOImpl
	implements RuntimeBO
{
	@Inject
	@Qualifier("flowContainerManager")
	private ContainerManager containerManager;

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 **/
	@Override
	public Map<String, Collection<IntegrationFlow>> getFlowList()
	{
	    return containerManager.getFlowDetails();
	}

}
