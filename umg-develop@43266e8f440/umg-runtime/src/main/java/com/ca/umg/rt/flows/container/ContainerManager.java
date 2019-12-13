/*
 * ContainerManager.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.flows.container;

import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpRequest;
import org.springframework.web.HttpRequestHandler;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.rt.endpoint.http.ModelRequest;
import com.ca.umg.rt.repository.IntegrationFlow;

/**
 * DOCUMENT ME!
 * **/
public interface ContainerManager {
    /**
     * Starts the container
     * 
     * @throws SystemException
     *             SystemException SystemException} , if any system error occur during the startup. In this case whole container
     *             will be down.
     * @throws BusinessException
     **/
    public void start() throws SystemException, BusinessException;

    /**
     * Stops the container
     * 
     * @throws SystemException
     *             DOCUMENT ME!
     * @throws BusinessException
     *             DOCUMENT ME!
     **/
    public void stop() throws SystemException, BusinessException;;

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
     * @throws BusinessException
     *             DOCUMENT ME!
     **/
    public DeploymentStatus deployflow(IntegrationFlow flow , boolean isTest) throws SystemException, BusinessException;

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
     * @throws BusinessException
     *             DOCUMENT ME!
     **/
    public DeploymentStatus unDeployflow(IntegrationFlow flow , boolean isTest) throws SystemException, BusinessException;

    /**
     * Find appropriate request handler suited for the given request.
     * 
     * @param request
     *            Incoming {@link HttpRequest} from client application.
     * 
     * @return {@link HttpRequestHandler} matching request.
     * 
     * @throws SystemException
     *             DOCUMENT ME!
     * @throws BusinessException
     *             DOCUMENT ME!
     **/
    public HttpRequestHandler getHandler(HttpServletRequest request, ModelRequest modelRequest) throws SystemException, BusinessException;

    public int getContainerSize();

    public int getModelSize();

    public Map<String, Collection<IntegrationFlow>> getFlowDetails();

    TestGateway getTestHandler(ModelRequest modelRequest) throws SystemException, BusinessException;

    DeploymentStatus deployWrapper(String wrapperType) throws SystemException, BusinessException;

    DeploymentStatus unDeployWrapper(String wrapperType) throws SystemException, BusinessException;
}
