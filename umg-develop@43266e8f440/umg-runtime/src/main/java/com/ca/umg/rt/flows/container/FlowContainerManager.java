/*
 * FlowContainerManager.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.flows.container;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.HttpRequestHandler;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.logging.appender.AppenderConstants;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.rt.core.flow.entity.Tenant;
import com.ca.umg.rt.endpoint.http.ModelRequest;
import com.ca.umg.rt.exception.codes.RuntimeExceptionCode;
import com.ca.umg.rt.repository.IntegrationFlow;
import com.ca.umg.rt.repository.IntegrationRepository;
import com.ca.umg.rt.util.PublishedVersionContainer;
import com.codahale.metrics.annotation.Metered;

/**
 * 
 **/
public class FlowContainerManager implements ContainerManager, ApplicationContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(FlowContainerManager.class);
    private ApplicationContext context;
    private final Map<String, Container> containerMap = new ConcurrentHashMap<String, Container>();
    private boolean running;
    private IntegrationRepository integrationRepository;
    private PublishedVersionContainer publishedVersionContainer;

    public PublishedVersionContainer getPublishedVersionContainer() {
        return publishedVersionContainer;
    }

    public void setPublishedVersionContainer(PublishedVersionContainer publishedVersionContainer) {
        this.publishedVersionContainer = publishedVersionContainer;
    }

    /**
     * DOCUMENT ME!
     *
     * @param applicationContext
     *            DOCUMENT ME!
     *
     * @throws BeansException
     *             DOCUMENT ME!
     **/
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        LOGGER.debug("Adding parent appliction cotnext as {}", applicationContext.getId());
        this.context = applicationContext;
        LOGGER.debug("Added  parent appliction cotnext as {}", this.context.getId());
    }

    /**
     * DOCUMENT ME!
     **/
    @Override
    public void start() throws SystemException, BusinessException {
        LOGGER.debug("Start initiated for FlowContainerManager");
        if (this.running) {
            LOGGER.warn("FlowContainderManager is already running");
            return;
        }
        long start = System.currentTimeMillis();
        List<Tenant> tenantList = null;
        try {
            tenantList = integrationRepository.loadTenants();
        } catch (SystemException | BusinessException tenantException) {
            LOGGER.error("Failed to retrieve tenant information from repository", tenantException);
        }
        String mdcTenantCode = (String) MDC.get(AppenderConstants.MDC_TENANT_CODE);
        RequestContext previousContext = RequestContext.getRequestContext();
        if(tenantList != null){
        try {
            java.util.concurrent.ExecutorService service = java.util.concurrent.Executors.newCachedThreadPool();
            for (final Tenant tenant : tenantList) {// NOPMD
                service.submit(new Runnable() {

                    @Override
                    public void run() {
                        createContainer(tenant.getCode(), true);
                    }
                });
            }
            service.shutdown();
            /*
             * try { service.shutdown(); //service.awaitTermination(60, TimeUnit.SECONDS); } catch (InterruptedException e) {
             * logger.error("pool shutdown",e); }
             */
        } finally {
            /* set old thread local back */
            if (mdcTenantCode != null) {
                MDC.put(AppenderConstants.MDC_TENANT_CODE, mdcTenantCode);
            }
            if (previousContext != null) {
                Properties properties = new Properties();
                properties.put(RequestContext.TENANT_CODE, previousContext.getTenantCode());
                new RequestContext(properties);
            }
        }
        long end = System.currentTimeMillis();
        LOGGER.debug("Start completed for FlowContainerManager. Time taken to start is {} milliseconds", (end - start));
        this.running = true;
        }
    }

    /**
     * DOCUMENT ME!
     **/
    @Override
    public void stop() throws SystemException, BusinessException {
        LOGGER.debug("Stop initiated for FlowContainerManager");

        long start = System.currentTimeMillis();

        for (String key : this.containerMap.keySet()) {
            try {
                this.containerMap.get(key).stop();
            } catch (Exception e) { // NOPMD
                LOGGER.error("Failed to stop container {}", key, e);
            }
        }

        long end = System.currentTimeMillis();
        LOGGER.debug("Stop completed for FlowContainerManager. Time taken to stop is {} milliseconds", (end - start));
        this.running = false;
    }

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
     *             DOCUMENT ME!
     *
     * @see
     **/
    @Override
    public HttpRequestHandler getHandler(HttpServletRequest request, ModelRequest modelRequest)
            throws SystemException, BusinessException {
        RequestContext requestContext = RequestContext.getRequestContext();
        if (requestContext == null || StringUtils.isEmpty(requestContext.getTenantCode())) {
            LOGGER.error("Error while getting the request context in :: FlowContainerManager : getHandler method");
            throw new SystemException(RuntimeExceptionCode.RVE000203, new Object[] {});
        }
        String tenantCode = requestContext.getTenantCode();
        Container container = getContainer(tenantCode);

        if (container == null) {
            Boolean isPublishedVersion = publishedVersionContainer.checkRequestedVersionIsPublished(modelRequest);
            if (isPublishedVersion) {
                LOGGER.error("The version {} may not be published." + modelRequest.getHeader() != null
                        ? modelRequest.getHeader().getModelName() : null);
                throw new SystemException(RuntimeExceptionCode.RSE000831, new Object[] {});
            }
            LOGGER.error("Error occurred while identifying the flow container for the request.");
            throw new SystemException(RuntimeExceptionCode.RVE000203, new Object[] {});
        }

        return container.getHandler(request, modelRequest);
    }

    /**
     * DOCUMENT ME!
     *
     * @return the repository
     **/
    public IntegrationRepository getIntegrationRepository() {
        return integrationRepository;
    }

    /**
     * DOCUMENT ME!
     *
     * @param repository
     *            the repository to set
     **/
    public void setIntegrationRepository(IntegrationRepository repository) {
        this.integrationRepository = repository;
    }

    @Override
    @Metered(name = "deploy")
    public DeploymentStatus deployflow(IntegrationFlow flow , boolean isTest) throws SystemException, BusinessException {
        RequestContext requestContext = RequestContext.getRequestContext();
        if (requestContext == null || StringUtils.isEmpty(requestContext.getTenantCode())) {
            throw new SystemException(RuntimeExceptionCode.RVE000205, new Object[] {});
        }

        String tenantCode = requestContext.getTenantCode();
        Container container = getContainer(tenantCode);

        if (container == null) {
            throw new SystemException(RuntimeExceptionCode.RVE000205, new Object[] {});
        }

        return container.deployflow(flow,null , isTest);
    }

    @Override
    public DeploymentStatus unDeployflow(IntegrationFlow flow ,boolean isTest) throws SystemException, BusinessException {
        RequestContext requestContext = RequestContext.getRequestContext();
        if (requestContext == null || StringUtils.isEmpty(requestContext.getTenantCode())) {
            throw new SystemException(RuntimeExceptionCode.RVE000205, new Object[] {});
        }

        String tenantCode = requestContext.getTenantCode();
        Container container = getContainer(tenantCode);

        if (container == null) {
            throw new SystemException(RuntimeExceptionCode.RVE000205, new Object[] {});
        }

        return container.unDeployflow(flow , isTest);
    }

    @Override
    public int getContainerSize() {
        return this.containerMap.size();
    }

    @Override
    public int getModelSize() {
        int modelSize = 0;
        for (String key : containerMap.keySet()) {
            modelSize = modelSize + containerMap.get(key).getCount();
        }
        return modelSize;
    }

    @Override
    public Map<String, Collection<IntegrationFlow>> getFlowDetails() {
        Map<String, Collection<IntegrationFlow>> flowMap = new HashMap<String, Collection<IntegrationFlow>>(
                this.getContainerSize());
        for (String key : containerMap.keySet()) {
            flowMap.put(key, containerMap.get(key).getFlowDetails());
        }
        return flowMap;
    }

    @Override
    public TestGateway getTestHandler(ModelRequest modelRequest) throws SystemException, BusinessException {
        RequestContext requestContext = RequestContext.getRequestContext();
        if (requestContext == null || StringUtils.isEmpty(requestContext.getTenantCode())) {
            throw new SystemException(RuntimeExceptionCode.RVE000205, new Object[] {});
        }
        String tenantCode = requestContext.getTenantCode();
        Container container = getContainer(tenantCode);

        if (container == null) {
            throw new SystemException(RuntimeExceptionCode.RVE000205, new Object[] {});
        }

        return container.getTestHandler(modelRequest);
    }

    @Override
    public DeploymentStatus deployWrapper(String wrapperType) throws SystemException, BusinessException {
        return new DeploymentStatus();
    }

    @Override
    public DeploymentStatus unDeployWrapper(String wrapperType) throws SystemException, BusinessException {
        return new DeploymentStatus();
    }

    private Container getContainer(final String tenantCode) {
        Container container = containerMap.get(tenantCode);
        if (container == null) {
            createContainer(tenantCode, false);
            container = containerMap.get(tenantCode);
        }

        return container;
    }

    private void createContainer(final String tenantCode, final boolean destroy) {
        Properties properties = new Properties();
        properties.put(RequestContext.TENANT_CODE, tenantCode);
        RequestContext reqeustContext = new RequestContext(properties);
        MDC.put(AppenderConstants.MDC_TENANT_CODE, tenantCode);
        LOGGER.debug("Testing MDC");
        try {
            Container container = context.getBean(FlowContainer.class);
            container.setIntegrationRepository(integrationRepository);
            ((ApplicationContextAware) container).setApplicationContext(context);
            try {
                container.setName(tenantCode);
                container.start();
                container.setStatus(ContainerStatus.RUNNING);
            } catch (Exception excp) { // NOPMD
                LOGGER.error("Failed to start container for tenant {}", tenantCode, excp);
                container.setStatus(ContainerStatus.ERROR);
            }
           /* if(!isTest){
            containerMap.put(tenantCode, container);
            }*/
            containerMap.put(tenantCode, container);
        } finally {
            if (destroy) {
                reqeustContext.destroy();
            }
            MDC.remove(AppenderConstants.MDC_TENANT_CODE);
        }

    }
}
