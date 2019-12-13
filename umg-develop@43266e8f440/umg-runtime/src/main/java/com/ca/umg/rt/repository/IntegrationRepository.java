/*
 * IntegrationRepository.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.repository;

import java.util.List;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.rt.core.flow.entity.Tenant;

/**
 * DOCUMENT ME!
 * **/
public interface IntegrationRepository
{
	/**
	 * Loads {@link IntegrationFlow} from repository implementation.
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws SystemException DOCUMENT ME!
	 * @throws BusinessException DOCUMENT ME!
	 **/
	public List<IntegrationFlow> loadIntegrationFlow()
	  throws SystemException,
	           BusinessException;

	/**
	 * Load {@link Tenant} from repository implementation.
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws SystemException DOCUMENT ME!
	 * @throws BusinessException DOCUMENT ME!
	 **/
	public List<Tenant> loadTenants()
	  throws SystemException,
	           BusinessException;
	
	/**
     * Load {@link Tenant} that have batching enabled from repository implementation.
     *
     * @return DOCUMENT ME!
     *
     * @throws SystemException DOCUMENT ME!
     * @throws BusinessException DOCUMENT ME!
     **/
    List<Tenant> loadBatchEnabledTenants()
      throws SystemException,
               BusinessException;
	
    /**
     * @return
     * @throws SystemException
     * @throws BusinessException
     */
    IntegrationFlow loadBatchIntegrationFlow() throws SystemException, BusinessException;
    
    IntegrationFlow loadWrapperDetail(String wrapperType) throws SystemException, BusinessException;
    
    List<String> getAllEnabledWrappers() throws SystemException, BusinessException;
}
