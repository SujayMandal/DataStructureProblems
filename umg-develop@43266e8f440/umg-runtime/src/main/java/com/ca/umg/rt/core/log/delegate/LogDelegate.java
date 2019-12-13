/*
 * LogDelegate.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics 
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.core.log.delegate;

import java.util.List;

import com.ca.umg.rt.core.log.info.LogInfo;

/**
 * DOCUMENT ME!
 * **/
public interface LogDelegate
{
	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 **/
	public List<LogInfo> getLoggers();
}
