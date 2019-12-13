/*
 * DeploymentDelegate.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics 
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.core.deployment.delegate;

import java.util.Map;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.rt.core.deployment.info.DeploymentDescriptor;
import com.ca.umg.rt.core.deployment.info.DeploymentStatusInfo;
import com.ca.umg.rt.core.deployment.info.TestStatusInfo;
import com.ca.umg.rt.endpoint.http.ModelRequest;

/**
 * DOCUMENT ME!
 * **/
public interface DeploymentDelegate {
    /**
     * Deploy new UMG version into contianer.
     * 
     * @param deploymentDescriptor
     *            DOCUMENT ME!
     * 
     * @throws SystemException
     *             DOCUMENT ME!
     * @throws BusinessException
     *             DOCUMENT ME!
     **/
    DeploymentStatusInfo deploy(DeploymentDescriptor deploymentDescriptor) throws SystemException, BusinessException;

    /**
     * Un deploy a UMG version from containder.
     * 
     * @param deploymentDescriptor
     *            DOCUMENT ME!
     * 
     * @throws SystemException
     *             DOCUMENT ME!
     * @throws BusinessException
     *             DOCUMENT ME!
     **/
    DeploymentStatusInfo undeploy(DeploymentDescriptor deploymentDescriptor) throws SystemException, BusinessException;

    TestStatusInfo executeTestFlow(ModelRequest modelRequest, Map<String, Object> requestBody) throws SystemException,
            BusinessException;

    /**
     * The method would deploy the batch execution flow for the tenant. Requires no input from tenant as the configurations will
     * be read from tenant-configuration table.
     * 
     * @return {@link DeploymentStatusInfo} with the status of deployment.
     * @throws SystemException
     * @throws BusinessException
     */
    DeploymentStatusInfo deployBatch() throws SystemException, BusinessException;

    /**
     * The method would un-deploy the batch execution flow for the tenant. Requires no input from tenant as the configurations will
     * be read from tenant-configuration table.
     * 
     * @return {@link DeploymentStatusInfo} with the status of un-deployment.
     * @throws SystemException
     * @throws BusinessException
     */
    DeploymentStatusInfo undeployBatch() throws SystemException, BusinessException;
    
    DeploymentStatusInfo deployBatchWrapper(String wrapperType) throws SystemException, BusinessException;
    
    DeploymentStatusInfo undeployBatchWrapper(String wrapperType) throws SystemException, BusinessException;
}
