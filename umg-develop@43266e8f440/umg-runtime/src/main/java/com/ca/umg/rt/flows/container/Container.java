/*
 * Container.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.flows.container;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationContext;
import org.springframework.web.HttpRequestHandler;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.rt.core.runtime.info.FlowDetailInfo;
import com.ca.umg.rt.endpoint.http.ModelRequest;
import com.ca.umg.rt.flows.version.VersionInfo;
import com.ca.umg.rt.repository.IntegrationFlow;
import com.ca.umg.rt.repository.IntegrationRepository;

/**
 * DOCUMENT ME!
 * **/
interface Container {
    /**
     * DOCUMENT ME!
     * 
     * @param name
     *            DOCUMENT ME!
     **/
    void setName(String name);

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     **/
    String getName();

    /**
     * Starts the container
     * 
     * @throws SystemException
     *             SystemException SystemException} , if any system error occur during the startup. In this case whole container
     *             will be down.
     * @throws BusinessException
     **/
    void start() throws SystemException, BusinessException;

    /**
     * Stops the container
     * 
     * @throws SystemException
     *             DOCUMENT ME!
     **/
    void stop() throws SystemException, BusinessException;

    /**
     * refresh the container
     * 
     * @return DOCUMENT ME!
     * 
     * @throws SystemException
     *             DOCUMENT ME!
     **/
    boolean refresh() throws SystemException;

    /**
     * Deploy new {@link IntegrationFlow} in the container.
     * 
     * @param flow
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     * 
     * @throws SystemException
     *             DOCUMENT ME!
     * @throws BusinessException
     **/
    DeploymentStatus deployflow(IntegrationFlow flow,ApplicationContext rootContext , boolean isTest) throws SystemException, BusinessException;

    /**
     * Stops, destroy and remove the {@link IntegrationFlow} from the container.
     * 
     * @param flow
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     * 
     * @throws SystemException
     *             DOCUMENT ME!
     * @throws BusinessException
     **/
    DeploymentStatus unDeployflow(IntegrationFlow flow,boolean isTest) throws SystemException, BusinessException;

    /**
     * DOCUMENT ME!
     * 
     * @param flow
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     * 
     * @throws SystemException
     *             DOCUMENT ME!
     **/
    boolean refreshFlow(IntegrationFlow flow) throws SystemException;

    /**
     * DOCUMENT ME!
     * 
     * @param versionInfo
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     * 
     * @throws SystemException
     *             DOCUMENT ME!
     **/
    ApplicationContext getContext(VersionInfo versionInfo) throws SystemException;

    /**
     * DOCUMENT ME!
     * 
     * @param applicationId
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     * 
     * @throws SystemException
     *             DOCUMENT ME!
     **/
    ApplicationContext getContext(String applicationId) throws SystemException;

    /**
     * DOCUMENT ME!
     * 
     * @param request
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     * 
     * @throws SystemException
     *             DOCUMENT ME!
     * @throws BusinessException
     **/
    HttpRequestHandler getHandler(HttpServletRequest request, ModelRequest modelRequest) throws SystemException, BusinessException;

    /**
     * DOCUMENT ME!
     * 
     * @param integrationRepository
     *            DOCUMENT ME!
     **/
    void setIntegrationRepository(IntegrationRepository integrationRepository);

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     **/
    int getCount();

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     **/
    Collection<IntegrationFlow> getFlowDetails();

    /**
     * DOCUMENT ME!
     * 
     * @param versionInfo
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     **/
    FlowDetailInfo getFlowDetail(VersionInfo versionInfo);

    /**
     * DOCUMENT ME!
     * 
     * @param status
     *            DOCUMENT ME!
     **/
    void setStatus(ContainerStatus status);

    /**
     * DOCUMENT ME!
     *
     **/
    ContainerStatus getStatus();

    TestGateway getTestHandler(ModelRequest modelRequest);

    /**
     * DOCUMENT ME!
     *
     **/
    ApplicationContext loadRootContext() throws SystemException, BusinessException;
    
    DeploymentStatus deployWrapper(String wrapperType) throws SystemException, BusinessException;
    
    DeploymentStatus unDeployWrapper(String wrapperType) throws SystemException, BusinessException;

}
