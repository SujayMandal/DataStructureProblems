/*
 * FlowBO.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.core.flow.bo;

import java.util.List;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.rt.core.flow.entity.Tenant;
import com.ca.umg.rt.core.flow.entity.Version;
import com.ca.umg.rt.repository.IntegrationFlow;

/**
 * DOCUMENT ME!
 * **/
public interface FlowBO
{
	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 **/
	public List<Tenant> getAllTenants();
	
	public List<Version> getAllVersionsForTenant ();

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 * @throws BusinessException 
	 * @throws SystemException 
	 **/
	public List<IntegrationFlow> getAllIntegrationFlows() throws SystemException, BusinessException;
	
	/**
	 * Loads the batch execution integration flow for a tenant.
	 * @return
	 * @throws SystemException
	 * @throws BusinessException
	 */
	IntegrationFlow loadBatchIntegrationFlow() throws SystemException, BusinessException;
	
	List<Tenant> loadBatchEnabledTenants() throws SystemException, BusinessException;
	
	IntegrationFlow loadWrapperDetail(String wrapperType) throws SystemException, BusinessException;
	
	List<String> getAllEnabledWrappers() throws SystemException, BusinessException;
}
