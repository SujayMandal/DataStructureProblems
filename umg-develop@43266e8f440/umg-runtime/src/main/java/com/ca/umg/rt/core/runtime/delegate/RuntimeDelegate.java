/*
 * RuntimeDelegate.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics 
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.core.runtime.delegate;

import java.util.List;
import java.util.Map;

import com.ca.umg.rt.core.runtime.info.RuntimeFlowInfo;

/**
 * DOCUMENT ME!
 * **/
public interface RuntimeDelegate
{
	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 **/
	public Map<String, List<RuntimeFlowInfo>> getFlowList();
}
