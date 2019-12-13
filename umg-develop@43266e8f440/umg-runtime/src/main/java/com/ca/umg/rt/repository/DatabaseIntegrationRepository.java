/*
 * DatabaseIntegrationRepository.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.repository;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.rt.core.flow.bo.FlowBO;
import com.ca.umg.rt.core.flow.entity.Tenant;

/**
 * Repository implementation to load flow xml for each {@link Tenant} from database.
 **/
public class DatabaseIntegrationRepository implements IntegrationRepository
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseIntegrationRepository.class);
	@Inject
	private FlowBO        flowBO;

	/**
	 * Load flow xml for each tenant in the system.
	 *
	 * @return {@link List} of {@link IntegrationFlow}
	 *
	 * @throws SystemException {@link SystemException}
	 * @throws BusinessException {@link BusinessException}
	 **/
	@Override
	public List<IntegrationFlow> loadIntegrationFlow()
	  throws SystemException,
	           BusinessException
	{
		LOGGER.debug("Loading runtime integration flows from database.");
		return flowBO.getAllIntegrationFlows();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws SystemException DOCUMENT ME!
	 * @throws BusinessException DOCUMENT ME!
	 **/
	@Override
	public List<Tenant> loadTenants()
	  throws SystemException,
	           BusinessException
	{
	    LOGGER.debug("Loading tenants from the database");
		return flowBO.getAllTenants();
	}

    /* (non-Javadoc)
     * @see com.ca.umg.rt.repository.IntegrationRepository#loadBatchIntegrationFlow()
     */
    @Override
    public IntegrationFlow loadBatchIntegrationFlow() throws SystemException, BusinessException {
        LOGGER.debug("Loading runtime batch integration flows for tenant.");
        return flowBO.loadBatchIntegrationFlow();
    }

    /* (non-Javadoc)
     * @see com.ca.umg.rt.repository.IntegrationRepository#loadBatchEnabledTenants()
     */
    @Override
    public List<Tenant> loadBatchEnabledTenants() throws SystemException, BusinessException {
        return flowBO.loadBatchEnabledTenants();
    }

    @Override
    public IntegrationFlow loadWrapperDetail(String wrapperType) throws SystemException, BusinessException {
        return flowBO.loadWrapperDetail(wrapperType);
    }

    @Override
    public List<String> getAllEnabledWrappers() throws SystemException, BusinessException {
        return flowBO.getAllEnabledWrappers();
    }
}
