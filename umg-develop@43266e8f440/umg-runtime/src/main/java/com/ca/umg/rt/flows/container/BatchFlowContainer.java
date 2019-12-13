/*
 * RuntimeFlowContainer.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.flows.container;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.web.HttpRequestHandler;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.logging.appender.AppenderConstants;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.umg.rt.core.deployment.constants.RuntimeConstants;
import com.ca.umg.rt.core.flow.entity.Tenant;
import com.ca.umg.rt.core.runtime.info.FlowDetailInfo;
import com.ca.umg.rt.endpoint.http.ModelRequest;
import com.ca.umg.rt.exception.codes.RuntimeExceptionCode;
import com.ca.umg.rt.flows.generator.FlowGenerator;
import com.ca.umg.rt.flows.version.BatchVersionInfo;
import com.ca.umg.rt.flows.version.VersionInfo;
import com.ca.umg.rt.repository.IntegrationFlow;
import com.ca.umg.rt.repository.IntegrationRepository;
import com.ca.umg.rt.support.RuntimeXmlApplicationContext;

/**
 * Implementation of {@link Container}. Holds all {@link ApplicationContext} instances for each flow for {@link Tenant}.
 **/
public class BatchFlowContainer implements Container, ApplicationContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(BatchFlowContainer.class);
    private ApplicationContext parent;
    private ApplicationContext root;
    private IntegrationRepository integrationRepository;
    private CacheRegistry cacheRegistry;
    private final Map<BatchVersionInfo, RuntimeContextWrapper> flows = new ConcurrentHashMap<BatchVersionInfo, RuntimeContextWrapper>();
    private final Map<String, ApplicationContext> wrapperMap = new ConcurrentHashMap<String, ApplicationContext>();
    private final Map<BatchVersionInfo, IntegrationFlow> flowMap = new ConcurrentHashMap<BatchVersionInfo, IntegrationFlow>();

    private String name;
    private ContainerStatus status = ContainerStatus.STOPPED;
    private long timeTaken;
    @Inject
    private SystemParameterProvider systemParameterProvider;

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     **/
    public Map<Object, Object> getCache() {
        return cacheRegistry.getMap(this.getName());
    }

    /**
     * Starts the {@link Container} instance.
     * 
     * @throws SystemException
     *             {@link SystemException}
     * @throws BusinessException
     *             {@link BusinessException}
     **/
    @Override
    public void start() throws SystemException, BusinessException {
        LOGGER.debug("Starting containder {}", this.name);

        long startTime = System.currentTimeMillis();
        this.root = loadRootContext();

        final IntegrationFlow batchFlow = integrationRepository.loadBatchIntegrationFlow();
        RequestContext.getRequestContext().setAdminAware(true);
        final List<String> wrappers = integrationRepository.getAllEnabledWrappers();
        RequestContext.getRequestContext().setAdminAware(false);

        if (batchFlow.getResource() != null) {
            java.util.concurrent.ExecutorService service = java.util.concurrent.Executors.newCachedThreadPool();
            service.submit(new Runnable() {

                @Override
                public void run() {
                    DeploymentStatus deploymentStatus = null;
                    Properties properties = new Properties();
                    properties.put(RequestContext.TENANT_CODE, name);
                    RequestContext reqeustContext = new RequestContext(properties);
                    MDC.put(AppenderConstants.MDC_TENANT_CODE, name);
                    try {
                        LOGGER.debug("Deploying batch flow in container {}", name);
                        deploymentStatus = deployflow(batchFlow,null , false);
                        LOGGER.debug("Deployed batch flow in container {}", name);
                        if(deploymentStatus.isSuccess() && CollectionUtils.isNotEmpty(wrappers)){
                            initiateWrapperDeployment(wrappers);
                        }
                    } catch (SystemException | BusinessException e) {
                        LOGGER.error("Error while deploying batch flow", e);
                    } finally {
                        reqeustContext.destroy();
                        MDC.remove(AppenderConstants.MDC_TENANT_CODE);
                    }
                }
            });

            service.shutdown();
            /*
             * try { service.shutdown(); service.awaitTermination(240, TimeUnit.SECONDS); } catch (InterruptedException e) {
             * logger.error("pool shutdown",e); }
             */
        }
        long endTime = System.currentTimeMillis();
        LOGGER.debug("Started the container");
        this.timeTaken = endTime - startTime;
        LOGGER.info("Time taken to start the containder is {}", this.timeTaken);
    }
    
    /**
     * Stops container by closing spring {@link ApplicationContext} object for each tenant.
     * 
     * @throws SystemException
     *             DOCUMENT ME!
     * @throws BusinessException
     **/
    @Override
    public void stop() throws SystemException, BusinessException {
        long startTime = System.currentTimeMillis();
        LOGGER.debug("Stopping runtime flow containder - STOP wrappers and then STOP batch");
        this.stopWrappers();
        for (RuntimeContextWrapper contextWrapper : this.flows.values()) {
            ConfigurableApplicationContext context = (ConfigurableApplicationContext) contextWrapper.getApplicationContext();
            LOGGER.debug("Stopping batch application context {}", context.getId());
            context.stop();
            context.close();
            LOGGER.debug("Stopped batch application context {}", context.getId());
        }

        LOGGER.debug("Stopped runtime batch flow container");

        long endTime = System.currentTimeMillis();
        LOGGER.info("Time taken to stop the batch flow container is {}", endTime - startTime);
        ConfigurableApplicationContext context = (ConfigurableApplicationContext) this.root;
        context.stop();
        context.close();
        this.status = ContainerStatus.STOPPED;
    }
    
    private void initiateWrapperDeployment(List<String> wrappers) {
        for (String wrapperType : wrappers) {
            try {
                deployWrapper(StringUtils.lowerCase(wrapperType, Locale.getDefault()));
            } catch (SystemException | BusinessException exp) {
                LOGGER.error(String.format("Failed to deploy wrapper %s at container start :: Exception Message :: %s",
                        wrapperType, exp.getMessage()));
            }
        }
    }
    
    private void stopWrappers(){
        if(MapUtils.isNotEmpty(wrapperMap)){
            for (Entry<String, ApplicationContext> wrapperContextEntry : wrapperMap.entrySet()) {
                LOGGER.debug("Stopping batch wrapper application context for {}", wrapperContextEntry.getKey());
                ConfigurableApplicationContext context = (ConfigurableApplicationContext) wrapperContextEntry.getValue();
                context.stop();
                context.close();
                LOGGER.debug("Stopped batch wrapper application context for {}", wrapperContextEntry.getKey());
            }
            LOGGER.debug("Stopped All batch wrapper application context.");
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     * 
     * @throws SystemException
     *             DOCUMENT ME!
     **/
    @Override
    public boolean refresh() throws SystemException {
        for (BatchVersionInfo batchVersionInfo : this.flows.keySet()) {
            ApplicationContext context = this.flows.get(batchVersionInfo).getApplicationContext();
            ((ConfigurableApplicationContext) context).refresh();
        }

        return true;
    }

    /**
     * Deploy a runtime integration flow container. Used for runtime hot deployment.
     * 
     * @param flow
     *            {@link IntegrationFlow}
     * 
     * @return deployment status.
     * 
     * @throws SystemException
     *             DOCUMENT ME!
     * @throws BusinessException
     **/
    @Override
    public DeploymentStatus deployflow(IntegrationFlow flow,ApplicationContext rootContext , boolean isTest) throws SystemException, BusinessException {
        LOGGER.debug("Deploying flow {}", flow.getFlowName());
        BatchVersionInfo versionInfo = new BatchVersionInfo(name);

        if (this.flows.containsKey(versionInfo) && this.flowMap.containsKey(versionInfo)) {
            DeploymentStatus status = new DeploymentStatus();
            status.setSuccess(true);
            return status;
        }
        ApplicationContext context = null;
        RuntimeContextWrapper wrapper = new RuntimeContextWrapper();
        try {
            Properties properties = new Properties();
            // TODO set any properties.
            context = createApplicationContext(flow, properties,rootContext);

            if (context == null) {
                throw new SystemException(RuntimeExceptionCode.RSE000100, new Object[] {});
            }

            wrapper.setFlowStatus(FlowStatus.RUNNING);
            wrapper.setIntegrationFlow(flow);
            wrapper.setApplicationContext(context);
            this.flows.put(versionInfo, wrapper);
            this.flowMap.put(versionInfo, flow);
            if(!isTest){
            this.getCache().put(versionInfo, IOUtils.toString(flow.getResource().getInputStream()));
            }
        } catch (Exception e)// NOPMD
        {
            LOGGER.error(e.getMessage(), e);
            // Ignore and continue, Details error logging is done here
            // to be displayed on admin console. And a facility need to be
            // given to correct the problem and redeploys the flow.
            wrapper.setFlowStatus(FlowStatus.ERROR);
            wrapper.setErrorMessage(e.getMessage());
            if (context != null) {
                ConfigurableApplicationContext configurableContext = (ConfigurableApplicationContext) context;
                configurableContext.stop();
                configurableContext.close();
            }
        }
        finally{
        	try {
				flow.getResource().getInputStream().close();
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
        }
        DeploymentStatus status = new DeploymentStatus();
        if (wrapper.getFlowStatus().equals(FlowStatus.ERROR)) {
            status.setSuccess(false);
            status.setMessage(wrapper.getErrorMessage());
        } else {
            status.setSuccess(true);
        }

        return status;
    }

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
    @Override
    public DeploymentStatus unDeployflow(IntegrationFlow flow , boolean isTest) throws SystemException, BusinessException {
        BatchVersionInfo versionInfo = new BatchVersionInfo(flow.getFlowName());
        RuntimeContextWrapper wrapper = this.flows.get(versionInfo);
        DeploymentStatus status = new DeploymentStatus();
        if (wrapper == null) {
            status.setSuccess(false);
            status.setMessage(String.format("Could not find deployed batch version for %s", versionInfo));
            return status;
        }
        ApplicationContext context = wrapper.getApplicationContext();
        if (context != null) {
            ConfigurableApplicationContext configurableContext = (ConfigurableApplicationContext) context;
            configurableContext.stop();
            configurableContext.close();
        }

        // wrapper.setFlowStatus(FlowStatus.STOPPED);
        this.flows.remove(versionInfo);
        this.flowMap.remove(versionInfo);
        if(!isTest){
        this.getCache().remove(versionInfo);
        }
        status.setSuccess(true);
        status.setMessage(String.format("successfully undeployed batch version %s", versionInfo));
        return status;
    }

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
    @Override
    public boolean refreshFlow(IntegrationFlow flow) throws SystemException {
        VersionInfo versionInfo = new VersionInfo(flow.getFlowName(), flow.getFlowMetadata().getMajorVersion(), flow
                .getFlowMetadata().getMinorVersion());
        ApplicationContext context = this.flows.get(versionInfo).getApplicationContext();

        if (context == null) {
            return false;
        }

        ((ConfigurableApplicationContext) context).refresh();
        return true;
    }

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
    @Override
    public ApplicationContext getContext(VersionInfo versionInfo) throws SystemException {
        return this.flows.get(versionInfo).getApplicationContext();
    }

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
    @Override
    public ApplicationContext getContext(String applicationId) throws SystemException {
        for (RuntimeContextWrapper contextWrapper : this.flows.values()) {
            ApplicationContext context = contextWrapper.getApplicationContext();

            if (context.getId().equals(applicationId)) {
                return context;
            }
        }

        return null;
    }

    /**
     * Return the HandlerAdapter for this handler object.
     * 
     * @param request
     *            the handler object to find an adapter for
     * 
     * @return DOCUMENT ME!
     * 
     * @throws SystemException
     *             if no HandlerAdapter can be found for the handler. This is a fatal error.
     * @throws BusinessException
     **/
    @Override
    public HttpRequestHandler getHandler(HttpServletRequest request, ModelRequest modelRequest) throws SystemException, BusinessException {
        return null;
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
        this.parent = applicationContext;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     **/
    public IntegrationRepository getIntegrationRepository() {
        return integrationRepository;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param integrationRepository
     *            DOCUMENT ME!
     **/
    public void setIntegrationRepository(IntegrationRepository integrationRepository) {
        this.integrationRepository = integrationRepository;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     * 
     * @throws SystemException
     *             DOCUMENT ME!
     * @throws BusinessException
     **/
    @Override
    public ApplicationContext loadRootContext() throws SystemException, BusinessException {
        Map<String, String> parentBatchData = new HashMap<String, String>();
        parentBatchData.put(RuntimeConstants.BATCH_THREAD_POOL_SIZE,
                systemParameterProvider.getParameter(RuntimeConstants.BATCH_THREAD_POOL_SIZE));
        String parentBatchFlowXml = FlowGenerator.generateParentBatchFlow(parentBatchData);
        Resource rootContextResource = new ByteArrayResource(parentBatchFlowXml.getBytes(),
                "Resource loaded from batch deployment Flow");

        ConfigurableApplicationContext context = new RuntimeXmlApplicationContext(rootContextResource);
        ConfigurableEnvironment environment = new StandardEnvironment();
        context.setEnvironment(environment);
        // context.setId(mapping.getName()+"--"+mapping.getVersion()+mapping.getModel().getName()+"--"
        // + mapping.getModel().getVersion());
        context.setParent(parent);
        context.refresh();
        context.registerShutdownHook();
        return context;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param integrationFlow
     *            DOCUMENT ME!
     * @param properties
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     **/
    private ApplicationContext createApplicationContext(IntegrationFlow integrationFlow, Properties properties,ApplicationContext rootContext) {
        ConfigurableApplicationContext context = new RuntimeXmlApplicationContext(integrationFlow.getResource());

        ConfigurableEnvironment environment = new StandardEnvironment();
        final PropertiesPropertySource propertySoruce = new PropertiesPropertySource("umgProp", properties);
        environment.getPropertySources().addLast(propertySoruce);
        context.setEnvironment(environment);
        context.setId(this.name + "--" + integrationFlow.getFlowName());
        ApplicationContext appContext =root != null ? root : rootContext;
        context.setParent(appContext);
        context.refresh();
        context.registerShutdownHook();
        return context;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param versionInfo
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     **/
    public FlowDetailInfo getFlowDetail(VersionInfo versionInfo) {
        return new FlowDetailInfo();
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     **/
    public CacheRegistry getCacheRegistry() {
        return cacheRegistry;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param cacheRegistry
     *            DOCUMENT ME!
     **/
    public void setCacheRegistry(CacheRegistry cacheRegistry) {
        this.cacheRegistry = cacheRegistry;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return the name
     **/
    @Override
    public String getName() {
        return name;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param name
     *            the name to set
     **/
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     **/
    @Override
    public int getCount() {
        return this.flowMap.size();
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     **/
    @Override
    public Collection<IntegrationFlow> getFlowDetails() {
        return this.flowMap.values();
    }

    /**
     * DOCUMENT ME!
     * 
     * @param status
     *            DOCUMENT ME!
     **/
    @Override
    public void setStatus(ContainerStatus status) {
        this.status = status;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     **/
    @Override
    public ContainerStatus getStatus() {
        return status;
    }

    /**
     * @return the timeTaken
     */
    public long getTimeTaken() {
        return timeTaken;
    }

    /**
     * @param timeTaken
     *            the timeTaken to set
     */
    public void setTimeTaken(long timeTaken) {
        this.timeTaken = timeTaken;
    }

    @Override
    public TestGateway getTestHandler(ModelRequest modelRequest) {
        return null;
    }

    @Override
    public DeploymentStatus deployWrapper(String wrapperType) throws SystemException, BusinessException {
        WrapperDeployer wrapperDeployer = null;
        DeploymentStatus deploymentStatus = new DeploymentStatus();
        BatchVersionInfo versionInfo = new BatchVersionInfo(getName());
        ApplicationContext wrapperContext = null;
        String wrapper = StringUtils.isNotBlank(wrapperType) ? StringUtils.upperCase(wrapperType, Locale.getDefault()) : wrapperType;
        if (flows.containsKey(versionInfo) && StringUtils.isNotBlank(wrapper)) {
            if (!wrapperMap.containsKey(wrapper)) {
                wrapperDeployer = new CommonWrapperDeployer();
                wrapperDeployer.setIntegrationRepository(integrationRepository);
                wrapperDeployer.setName(getName());
                wrapperDeployer.setParentContext(flows.get(versionInfo).getApplicationContext());
                wrapperContext = wrapperDeployer.deployWrapper(wrapperType);
                wrapperMap.put(wrapper, wrapperContext);
                deploymentStatus.setMessage(String.format("Batch wrapper deployed successfully %s - %s", getName(), wrapperType));
            }else{
                deploymentStatus.setMessage(String.format("Batch wrapper already deployed successfully %s - %s", getName(), wrapperType));
            }
            deploymentStatus.setSuccess(true);
            LOGGER.error(String.format("Batch wrapper deployed successfully %s - %s", getName(), wrapperType));
        } else {
            deploymentStatus.setSuccess(false);
            deploymentStatus.setMessage(String.format("No batch deployed for tenant %s. Cannot deploy wrapper %s", getName(),
                    wrapperType));
            LOGGER.error(String.format("No batch deployed for tenant %s. Cannot deploy wrapper %s", getName(), wrapperType));
        }
        return deploymentStatus;
    }

    @Override
    public DeploymentStatus unDeployWrapper(String wrapperType) throws SystemException, BusinessException {
        DeploymentStatus status = new DeploymentStatus();
        ApplicationContext wrapperContext = null;
        String wrapper = StringUtils.isNotBlank(wrapperType) ? StringUtils.upperCase(wrapperType, Locale.getDefault()) : wrapperType;
        if (StringUtils.isNotBlank(wrapper) && MapUtils.isNotEmpty(wrapperMap) && wrapperMap.containsKey(wrapper)) {
            wrapperContext = wrapperMap.get(wrapper);
            if (wrapperContext == null) {
                status.setSuccess(false);
                status.setMessage(String.format("Could not find deployed wrapper version for %s for tenant %s", wrapperType,
                        getName()));
                return status;
            } else {
                ConfigurableApplicationContext configurableContext = (ConfigurableApplicationContext) wrapperContext;
                configurableContext.stop();
                configurableContext.close();
            }
        }
        this.wrapperMap.remove(StringUtils.upperCase(wrapper));
        status.setSuccess(true);
        status.setMessage(String.format("successfully undeployed batch wrapper %s for tenant %s", wrapperType, getName()));
        return status;
    }
}
