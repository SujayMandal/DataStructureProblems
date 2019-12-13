/*
 * DashboardDelegateImpl.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.core.dashboard.delegate;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.ca.umg.rt.core.dashboard.bo.DashboardBO;
import com.ca.umg.rt.core.dashboard.info.DashboardInfo;

/**
 * 
 **/
@Component
public class DashboardDelegateImpl
	implements DashboardDelegate
{
	@Inject
	private DashboardBO dashboardBO;

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 **/
	@Override
	public DashboardInfo getStatistics()
	{
		return dashboardBO.getContainerStatistics();
	}
}
