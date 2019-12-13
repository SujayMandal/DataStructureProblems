/*
 * FlowDao.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.core.flow.dao;

import java.util.List;
import java.util.Map;

import com.ca.umg.rt.core.flow.entity.Tenant;
import com.ca.umg.rt.core.flow.entity.Version;
import com.ca.umg.rt.core.flow.entity.VersionMapping;
import com.ca.umg.rt.core.flow.entity.VersionModelLibrary;
import com.ca.umg.rt.core.flow.entity.VersionQuery;

/**
 * DOCUMENT ME!
 * **/
public interface FlowDAO
{
	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 **/
	public List<Tenant> getAllTenants();

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 **/
	public List<Version> getAllVersions();

	/**
	 * Get all mapping details for all version.
	 *
	 * @return
	 **/
	public Map<Version, VersionMapping> getAllVersionMapping();

	/**
	 * Get all query details for all version.
	 *
	 * @return
	 **/
	public Map<Version, List<VersionQuery>> getAllVersionQuery();

	/**
	 * Get all model library details for all version.
	 *
	 * @return
	 **/
	public Map<Version, VersionModelLibrary> getAllVersionModelLibrary();
	
	/**
	 * Fetches all tenants with batch enabled.
	 * 
	 * @return
	 */
	List<Tenant> getAllBatchEnabledTenants();
	
	Map<String, String> loadWrapperDetail(String wrapperType);
	
	List<String> getAllEnabledWrappers();

}
