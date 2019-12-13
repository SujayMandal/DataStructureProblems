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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

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
import org.springframework.core.io.Resource;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.logging.appender.AppenderConstants;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.pool.TransactionMode;
import com.ca.umg.rt.core.flow.entity.Tenant;
import com.ca.umg.rt.core.runtime.info.FlowDetailInfo;
import com.ca.umg.rt.endpoint.http.Header;
import com.ca.umg.rt.endpoint.http.ModelRequest;
import com.ca.umg.rt.exception.codes.RuntimeExceptionCode;
import com.ca.umg.rt.flows.version.VersionInfo;
import com.ca.umg.rt.flows.version.VersionMap;
import com.ca.umg.rt.repository.IntegrationFlow;
import com.ca.umg.rt.repository.IntegrationRepository;
import com.ca.umg.rt.support.RuntimeXmlApplicationContext;
import com.ca.umg.rt.util.PublishedVersionContainer;

/**
 * Implementation of {@link Container}. Holds all {@link ApplicationContext} instances for each flow for {@link Tenant}.
 **/
public class FlowContainer implements Container, ApplicationContextAware {
	private static final Logger LOGGER = LoggerFactory.getLogger(FlowContainer.class);
	private static final String INTEGRATION_ROOT_CONTEXT = "classpath:parent-integration-context.xml";
	private ApplicationContext parent;
	private ApplicationContext root;
	private IntegrationRepository integrationRepository;
	private CacheRegistry cacheRegistry;
	private final VersionMap versionMap = new VersionMap();
	private final VersionMap versionMapForTest = new VersionMap();
	private final Map<VersionInfo, RuntimeContextWrapper> flows = new ConcurrentHashMap<VersionInfo, RuntimeContextWrapper>();
	private final Map<VersionInfo, RuntimeContextWrapper> flowsTest = new ConcurrentHashMap<VersionInfo, RuntimeContextWrapper>();
	private final Map<VersionInfo, IntegrationFlow> flowMap = new ConcurrentHashMap<VersionInfo, IntegrationFlow>();

	private String name;
	private ContainerStatus status = ContainerStatus.STOPPED;
	private long timeTaken;

	@Inject
	private PublishedVersionContainer publishedVersionContainer;

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

		List<IntegrationFlow> flowList = integrationRepository.loadIntegrationFlow();

		if (!flowList.isEmpty()) {
			java.util.concurrent.ExecutorService service = java.util.concurrent.Executors.newCachedThreadPool();

			for (final IntegrationFlow flow : flowList)// NOPMD
			{
				service.submit(new Runnable() {

					@Override
					public void run() {
						Properties properties = new Properties();
						properties.put(RequestContext.TENANT_CODE, name);
						RequestContext reqeustContext = new RequestContext(properties);
						MDC.put(AppenderConstants.MDC_TENANT_CODE, name);
						LOGGER.debug("Testing MDC");
						try {
							LOGGER.debug("Deploying flow in container {}", name);
							deployflow(flow,null , false);
							LOGGER.debug("Finished  deploying flow in container {}", name);
						} catch (SystemException | BusinessException e) {
							LOGGER.error("Error while deploying flow", e);
						} finally {
							reqeustContext.destroy();
							MDC.remove(AppenderConstants.MDC_TENANT_CODE);
						}
					}
				});
			}
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
	 **/
	@Override
	public void stop() throws SystemException {
		long startTime = System.currentTimeMillis();
		LOGGER.debug("Stopping runtime flow containder");

		for (RuntimeContextWrapper contextWrapper : this.flows.values()) {
			if (!contextWrapper.getFlowStatus().equals(FlowStatus.ERROR)) {
				ConfigurableApplicationContext context = (ConfigurableApplicationContext) contextWrapper.getApplicationContext();
				LOGGER.debug("Stopping application context {}", context.getId());
				context.stop();
				context.close();
				LOGGER.debug("Stopped application context {}", context.getId());
			}
		}

		LOGGER.debug("Stopped runtime flow containder");

		long endTime = System.currentTimeMillis();
		LOGGER.info("Time taken to stop the container is {}", endTime - startTime);
		ConfigurableApplicationContext context = (ConfigurableApplicationContext) this.root;
		context.stop();
		context.close();
		this.status = ContainerStatus.STOPPED;
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
		for (VersionInfo versionInfo : this.flows.keySet()) {
			ApplicationContext context = this.flows.get(versionInfo).getApplicationContext();
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
		VersionInfo versionInfo = new VersionInfo(flow.getFlowName(), flow.getFlowMetadata().getMajorVersion(), flow
				.getFlowMetadata().getMinorVersion(), flow.getFlowMetadata().getModelType());
		versionInfo.setStatus(flow.getFlowMetadata().getStatus());
		if (this.flows.containsKey(versionInfo) && this.flowMap.containsKey(versionInfo)) {
			DeploymentStatus status = new DeploymentStatus();
			status.setSuccess(true);
			return status;
		}
		ApplicationContext context = null;
		ApplicationContext contextTest = null;
		RuntimeContextWrapper wrapper = new RuntimeContextWrapper();
		try {
			Properties properties = new Properties();
			properties.put(EnvironmentVariables.AUDIT_LOGGER_NAME, this.name + "--" + versionInfo.getVersionString());
			properties.put(EnvironmentVariables.JMX_EXPORT_DOMAIN, this.name + "--" + versionInfo.getVersionString());
			properties.put(EnvironmentVariables.FLOW_CONTAINER_NAME, this.name);
			properties.put(EnvironmentVariables.MODEL_NAME, flow.getFlowName());
			properties.put(EnvironmentVariables.MODEL_LIBRARY_NAME, flow.getFlowMetadata().getModelLibrary().getName());
			properties
			.put(EnvironmentVariables.MODEL_LIBRARY_VERSION_NAME, flow.getFlowMetadata().getModelLibrary().getUmgName());
			properties.put(EnvironmentVariables.MAJOR_VERSION, flow.getFlowMetadata().getMajorVersion());
			properties.put(EnvironmentVariables.MINOR_VERSION, flow.getFlowMetadata().getMinorVersion());
			properties.put(EnvironmentVariables.MODEL_CHECKSUM, flow.getFlowMetadata().getModelLibrary().getChecksum());
			properties.put(EnvironmentVariables.VERSION_STRING, versionInfo.getVersionString());
			properties.put(EnvironmentVariables.TENANT_CODE, this.name);
			properties.put(EnvironmentVariables.IS_TEST, 0);
			properties.put(EnvironmentVariables.ME2_URL, systemParameterProvider.getParameter(EnvironmentVariables.ME2_URL));

			if(! isTest ){
				context = createApplicationContext(flow, properties);

				if (context == null) {
					throw new SystemException(RuntimeExceptionCode.RSE000100, new Object[] {});
				}
			} else {
				contextTest = createApplicationContext(flow, properties);

				if (contextTest == null) {
					throw new SystemException(RuntimeExceptionCode.RSE000100, new Object[] {});
				}
			}
			wrapper.setFlowStatus(FlowStatus.RUNNING);
		} catch (Exception e)// NOPMD
		{
			LOGGER.error(e.getMessage(), e);
			// Ignore and continue, Details error logging is done here
			// to be displayed on admin console. And a facility need to be
			// given to correct the problem and redeploys the flow.
			wrapper.setFlowStatus(FlowStatus.ERROR);
			wrapper.setErrorMessage(e.getMessage());
		}

		
		wrapper.setIntegrationFlow(flow);
		if(isTest){
			wrapper.setApplicationContext(contextTest);
			this.flowsTest.put(versionInfo, wrapper);
			this.versionMapForTest.add(versionInfo);
		} else {
			this.versionMap.add(versionInfo);
			wrapper.setApplicationContext(context);
			this.flows.put(versionInfo, wrapper);
			this.flowMap.put(versionInfo, flow);
		}
		
		if(! isTest){
			this.getCache().put(versionInfo, flow.getFlowMetadata());
		}
		LOGGER.error(String.format("DEPLOYMENT IN CACHE for tenant :: %s :: Version :: %s :: Interface definition Present ::   >> %s ",
				MDC.get(AppenderConstants.MDC_TENANT_CODE),
				versionInfo.getVersionString(),
				flow.getFlowMetadata().getMappingMetaData().getTenantInputDefinition() != null ? "TRUE" : "FALSE"));
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
		VersionInfo versionInfo = new VersionInfo(flow.getFlowName(), flow.getFlowMetadata().getMajorVersion(), flow
				.getFlowMetadata().getMinorVersion());
		RuntimeContextWrapper wrapper = null;
		if(isTest){
			wrapper = this.flowsTest.get(versionInfo);
		} else {
			wrapper = this.flows.get(versionInfo);
		}
		DeploymentStatus status = new DeploymentStatus();
		if (wrapper == null) {
			status.setSuccess(false);
			status.setMessage(String.format("Could not find deployed version for %s", versionInfo));
					return status;
		}
		ApplicationContext context = wrapper.getApplicationContext();
		if (context != null) {
			ConfigurableApplicationContext configurableContext = (ConfigurableApplicationContext) context;
			configurableContext.stop();
			configurableContext.close();
		}

		// wrapper.setFlowStatus(FlowStatus.STOPPED);

		
		
		if(!isTest){
			this.flows.remove(versionInfo);
			this.getCache().remove(versionInfo);
			this.versionMap.remove(versionInfo);
			this.flowMap.remove(versionInfo);
		} else {
			this.versionMapForTest.remove(versionInfo);
			this.flowsTest.remove(versionInfo);
		}
		status.setSuccess(true);
		status.setMessage(String.format("successfully undeployed %s", versionInfo));
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
		HandlerExecutionChain mappedHandler;

		mappedHandler = this.getHandlerMapping(request, modelRequest);

		if ((mappedHandler == null) || (mappedHandler.getHandler() == null)) {
			throw new SystemException(RuntimeExceptionCode.RVE000205, new Object[] { mappedHandler });
		}

		return (HttpRequestHandler) mappedHandler.getHandler();
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
	 **/
	@Override
	public ApplicationContext loadRootContext() throws SystemException {
		Resource rootContextResource = this.parent.getResource(INTEGRATION_ROOT_CONTEXT);

		if (rootContextResource == null) {
			throw new SystemException(RuntimeExceptionCode.RSE000002, new Object[] { INTEGRATION_ROOT_CONTEXT });
		}

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
	private ApplicationContext createApplicationContext(IntegrationFlow integrationFlow, Properties properties) {
		ConfigurableApplicationContext context = new RuntimeXmlApplicationContext(integrationFlow.getResource());

		ConfigurableEnvironment environment = new StandardEnvironment();
		final PropertiesPropertySource propertySoruce = new PropertiesPropertySource("umgProp", properties);
		environment.getPropertySources().addLast(propertySoruce);
		context.setEnvironment(environment);
		context.setId(this.name + "--" + integrationFlow.getFlowName());
		context.setParent(root);
		context.refresh();
		context.registerShutdownHook();
		return context;
	}

	/**
	 * Return the HandlerExecutionChain for this request.
	 * <p>
	 * Tries all handler mappings in order.
	 * </p>
	 *
	 * @param request
	 *            current HTTP request
	 *
	 * @return the HandlerExecutionChain, or {@code null} if no handler could be found
	 * @throws SystemException
	 * @throws BusinessException
	 *
	 * @throws Exception
	 *             DOCUMENT ME!
	 **/
	private HandlerExecutionChain getHandlerMapping(HttpServletRequest request, ModelRequest modelRequest) throws SystemException, BusinessException {
		/*String body = null;
        try {
            body = IOUtils.toString(request.getReader());
        } catch (IOException ex) {
            throw new SystemException(RuntimeExceptionCode.RVE000201, new Object[] { ex.getMessage() }, ex);
        }

        ModelRequest modelRequest = RequestValidator.validateRequest(body);*/

		VersionInfo versionInfo = versionMap.get(modelRequest.getHeader().getModelName(), modelRequest.getHeader()
				.getMajorVersion(), modelRequest.getHeader().getMinorVersion(), false,modelRequest.getHeader().getTransactionMode());
		if (versionInfo == null) {
			Boolean isPublishedVersion = publishedVersionContainer.checkRequestedVersionIsPublished(modelRequest);
			if (isPublishedVersion) {
				LOGGER.error("Error while isPublishedVersion versionInfo check :: FlowContainer : getHandlerMapping method");
				throw new SystemException(RuntimeExceptionCode.RSE000831, new Object[] {});
			}
			LOGGER.error("Error while versionInfo check :: FlowContainer : getHandlerMapping method ");
			throw new SystemException(RuntimeExceptionCode.RVE000203, new Object[] { modelRequest.getHeader().getModelName(),
					modelRequest.getHeader().getMajorVersion(), modelRequest.getHeader().getMinorVersion() });
		}

		validateModelIsBulk (modelRequest, versionInfo);

		if (this.flows == null) {
			LOGGER.warn("There are no model deployed in container for reqeust {}", modelRequest);
			throw new SystemException(RuntimeExceptionCode.RVE000205, new Object[] { modelRequest.getHeader().getModelName() });
		}

		ApplicationContext context = this.flows.get(versionInfo).getApplicationContext();

		if (context == null) {
			LOGGER.error("Error while context check :: FlowContainer : getHandlerMapping method");
			throw new SystemException(RuntimeExceptionCode.RVE000203, new Object[] {});
		}

		HandlerMapping handlerMapping = context.getBean(HandlerMapping.class);

		if (handlerMapping == null) {
			LOGGER.error("Error while handlerMapping check:: FlowContainer : getHandlerMapping method");
			throw new SystemException(RuntimeExceptionCode.RVE000203, new Object[] {});
		}

		try {
			return handlerMapping.getHandler(request);
		} catch (Exception ex) {// NOPMD
			LOGGER.error("Error in Exception Block  :: FlowContainer : getHandlerMapping method " + ex);
			throw new SystemException(RuntimeExceptionCode.RVE000203, new Object[] {}, ex);
		}
	}

	/**
	 * checks if the model is bulk or not
	 * @param modelRequest
	 * @param versionInfo
	 * @throws SystemException
	 */
	private void validateModelIsBulk (ModelRequest modelRequest, VersionInfo versionInfo) throws SystemException {
		Header header = modelRequest.getHeader();
		if ((StringUtils.isNotBlank(header.getFileName()) && versionInfo != null &&
				!StringUtils.equals(versionInfo.getModelType(), TransactionMode.BULK.getMode())) || (versionInfo != null && StringUtils.equals(modelRequest.getHeader().getTransactionMode(), TransactionMode.BULK.getMode()) && StringUtils.equals(versionInfo.getModelType(), TransactionMode.ONLINE.getMode()))) {
			throw new SystemException(RuntimeExceptionCode.RVE000221, new Object[] {
					header.getModelName(),
					header.getMajorVersion(), header.getMinorVersion()});
		}
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
		ApplicationContext context = flows.get(versionInfo).getApplicationContext();
		context.getBeanDefinitionNames();
		context.getBeanDefinitionCount();
		((RuntimeXmlApplicationContext) context).getConfigResources();
		((ConfigurableApplicationContext) context).getApplicationName();
		((ConfigurableApplicationContext) context).getStartupDate();
		((ConfigurableApplicationContext) context).getDisplayName();
		((ConfigurableApplicationContext) context).getEnvironment().getSystemEnvironment();
		((ConfigurableApplicationContext) context).getEnvironment().getSystemProperties();
		((ConfigurableApplicationContext) context).getEnvironment().getActiveProfiles();

		FlowDetailInfo flowDetailInfo = new FlowDetailInfo();

		return flowDetailInfo;
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
		VersionInfo versionInfo  = null;
		ApplicationContext context = null;
		if(!modelRequest.getHeader().getVersionCreationTest() ) {
			versionInfo = versionMap.get(modelRequest.getHeader().getModelName(), modelRequest.getHeader()
					.getMajorVersion(), modelRequest.getHeader().getMinorVersion(), true,modelRequest.getHeader().getTransactionMode());
			if(versionInfo == null){  
				versionInfo = versionMapForTest.get(modelRequest.getHeader().getModelName(), modelRequest.getHeader()
						.getMajorVersion(), modelRequest.getHeader().getMinorVersion(), true,modelRequest.getHeader().getTransactionMode());
				if (versionInfo == null) {
					return null;
				}
				context = this.flowsTest.get(versionInfo).getApplicationContext();
			} else {
				context = this.flows.get(versionInfo).getApplicationContext();
			}
		} else {
			versionInfo = versionMapForTest.get(modelRequest.getHeader().getModelName(), modelRequest.getHeader()
					.getMajorVersion(), modelRequest.getHeader().getMinorVersion(), true,modelRequest.getHeader().getTransactionMode());
			context = this.flowsTest.get(versionInfo).getApplicationContext();
		}
		if (versionInfo == null) {
			return null;
		}


		if (context == null) {
			return null;
		}

		return context.getBean(TestGateway.class);
	}

	@Override
	public DeploymentStatus deployWrapper(String wrapperType) {
		return new DeploymentStatus();
	}

	@Override
	public DeploymentStatus unDeployWrapper(String wrapperType) {
		return new DeploymentStatus();
	}
}
