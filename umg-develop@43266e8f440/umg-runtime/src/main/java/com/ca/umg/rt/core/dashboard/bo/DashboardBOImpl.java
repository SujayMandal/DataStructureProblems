/*
 * DashboardBOImpl.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.core.dashboard.bo;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.umg.rt.core.dashboard.info.DashboardInfo;
import com.ca.umg.rt.flows.container.ContainerManager;

/**
 * 
 **/
@Component
public class DashboardBOImpl
	implements DashboardBO
{
	@Inject
	@Qualifier("flowContainerManager")
	private ContainerManager containerManager;
	
	@Inject
	private CacheRegistry cacheRegistry;

	/**
	 * DOCUMENT ME!
	 **/
	@Override
	public DashboardInfo getContainerStatistics()
	{
		DashboardInfo dashboardInfo = new DashboardInfo();
		dashboardInfo.setModelCount(containerManager.getModelSize());
		dashboardInfo.setTenantCount(containerManager.getContainerSize());
		dashboardInfo.setModeletCount(cacheRegistry.getDistributedQueue().size());
		return dashboardInfo;
	}
}
