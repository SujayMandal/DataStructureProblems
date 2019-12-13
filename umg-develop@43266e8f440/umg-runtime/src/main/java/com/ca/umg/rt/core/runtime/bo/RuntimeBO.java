/*
 * RuntimeBO.java
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

import com.ca.umg.rt.repository.IntegrationFlow;

/**
 * DOCUMENT ME!
 * **/
public interface RuntimeBO
{
	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 **/
	Map<String, Collection<IntegrationFlow>> getFlowList();
}
