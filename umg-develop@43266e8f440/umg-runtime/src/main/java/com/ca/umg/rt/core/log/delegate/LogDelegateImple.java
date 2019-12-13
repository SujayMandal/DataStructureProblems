/*
 * LogDelegateImple.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics 
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.core.log.delegate;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ca.umg.rt.core.log.info.LogInfo;

/**
 * 
 * **/
@Component
public class LogDelegateImple
	implements LogDelegate
{
	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 **/
	@Override
	public List<LogInfo> getLoggers()
	{
		return null;
	}
}
