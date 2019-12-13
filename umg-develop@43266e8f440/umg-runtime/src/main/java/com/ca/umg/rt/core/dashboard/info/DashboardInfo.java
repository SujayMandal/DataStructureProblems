/*
 * DashboardInfo.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics 
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.core.dashboard.info;

/**
 * 
 * **/
public class DashboardInfo
{
	private int tenantCount;
	private int modelCount;
	private int modeletCount;

	/**
	 * 
	DOCUMENT ME!
	 *
	 * @return the tenantCount
	 **/
	public int getTenantCount()
	{
		return tenantCount;
	}

	/**
	 * 
	DOCUMENT ME!
	 *
	 * @param tenantCount the tenantCount to set
	 **/
	public void setTenantCount(int tenantCount)
	{
		this.tenantCount = tenantCount;
	}

	/**
	 * 
	DOCUMENT ME!
	 *
	 * @return the modelCount
	 **/
	public int getModelCount()
	{
		return modelCount;
	}

	/**
	 * 
	DOCUMENT ME!
	 *
	 * @param modelCount the modelCount to set
	 **/
	public void setModelCount(int modelCount)
	{
		this.modelCount = modelCount;
	}

    public int getModeletCount() {
        return modeletCount;
    }

    public void setModeletCount(int modeletCount) {
        this.modeletCount = modeletCount;
    }
}
