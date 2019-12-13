/*
 * DeploymentDelegateImpl.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics 
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.core.deployment.delegate;

import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.rt.cache.event.FlowPublishEvent;
import com.ca.umg.rt.cache.listener.BatchFlowDeploymentListener;
import com.ca.umg.rt.cache.listener.BatchWrapperDeploymentListener;
import com.ca.umg.rt.cache.listener.FlowPublishListener;
import com.ca.umg.rt.core.deployment.bo.DeploymentBO;
import com.ca.umg.rt.core.deployment.info.DeploymentDescriptor;
import com.ca.umg.rt.core.deployment.info.DeploymentStatusInfo;
import com.ca.umg.rt.core.deployment.info.TestStatusInfo;
import com.ca.umg.rt.endpoint.http.ModelRequest;

/**
 * 
 * **/
@Component
public class DeploymentDelegateImpl
	implements DeploymentDelegate
{
	@Inject
	private DeploymentBO deploymentBO;
	
	 @Inject
	 private CacheRegistry cacheRegistry;

	/**
	 * DOCUMENT ME!
	 *
	 * @param deploymentDescriptor DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws SystemException DOCUMENT ME!
	 * @throws BusinessException DOCUMENT ME!
	 **/
	@Override
	public DeploymentStatusInfo deploy(DeploymentDescriptor deploymentDescriptor)
	  throws SystemException,
	           BusinessException
	{
	    try{
	        return deploymentBO.deploy(deploymentDescriptor);
	    }finally {
	        FlowPublishEvent event = new FlowPublishEvent();
            event.setEvent(FlowPublishEvent.DEPLOY);
            event.setDeploymentDescriptor(deploymentDescriptor);
            event.setTenantCode(RequestContext.getRequestContext().getTenantCode());
            cacheRegistry.getTopic(FlowPublishListener.FLOW_PUBLISH_TOPIC).publish(event);
	    }
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param deploymentDescriptor DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws SystemException DOCUMENT ME!
	 * @throws BusinessException DOCUMENT ME!
	 **/
	@Override
	public DeploymentStatusInfo undeploy(DeploymentDescriptor deploymentDescriptor)
	  throws SystemException,
	           BusinessException
	{
	    try{
	        return deploymentBO.undeploy(deploymentDescriptor , false);
	    } finally{
	        FlowPublishEvent event = new FlowPublishEvent();
            event.setEvent(FlowPublishEvent.UNDEPLOY);
            event.setDeploymentDescriptor(deploymentDescriptor);
            event.setTenantCode(RequestContext.getRequestContext().getTenantCode());
            cacheRegistry.getTopic(FlowPublishListener.FLOW_PUBLISH_TOPIC).publish(event);
	    }
	}

    @Override
    public TestStatusInfo executeTestFlow(ModelRequest modelRequest, Map<String, Object> requestBody) throws SystemException,
            BusinessException {
        return deploymentBO.executeTestFlow(modelRequest, requestBody);
    }

    /* (non-Javadoc)
     * @see com.ca.umg.rt.core.deployment.delegate.DeploymentDelegate#deployBatch()
     */
    @Override
    public DeploymentStatusInfo deployBatch() throws SystemException, BusinessException {
        try{
            return deploymentBO.deployBatch();
        }finally {
            FlowPublishEvent event = new FlowPublishEvent();
            event.setEvent(FlowPublishEvent.DEPLOY);
            event.setTenantCode(RequestContext.getRequestContext().getTenantCode());
            cacheRegistry.getTopic(BatchFlowDeploymentListener.BATCH_DEPLOYMENT_TOPIC).publish(event);
        }
    }

    /* (non-Javadoc)
     * @see com.ca.umg.rt.core.deployment.delegate.DeploymentDelegate#undeployBatch()
     */
    @Override
    public DeploymentStatusInfo undeployBatch() throws SystemException, BusinessException {
        try{
            return deploymentBO.undeployBatch();
        }finally {
            FlowPublishEvent event = new FlowPublishEvent();
            event.setEvent(FlowPublishEvent.UNDEPLOY);
            event.setTenantCode(RequestContext.getRequestContext().getTenantCode());
            cacheRegistry.getTopic(BatchFlowDeploymentListener.BATCH_DEPLOYMENT_TOPIC).publish(event);
        }
    }

    @Override
    public DeploymentStatusInfo deployBatchWrapper(String wrapperType) throws SystemException, BusinessException {
        try{
            return deploymentBO.deployWrapper(wrapperType);
        }finally {
            FlowPublishEvent event = new FlowPublishEvent();
            event.setEvent(FlowPublishEvent.DEPLOY);
            event.setTenantCode(RequestContext.getRequestContext().getTenantCode());
            DeploymentDescriptor deploymentDescriptor = new DeploymentDescriptor();
            deploymentDescriptor.setName(wrapperType);
            event.setDeploymentDescriptor(deploymentDescriptor);
            cacheRegistry.getTopic(BatchWrapperDeploymentListener.BATCH_WRAPPER_DEPLOYMENT_TOPIC).publish(event);
        }
    }

    @Override
    public DeploymentStatusInfo undeployBatchWrapper(String wrapperType) throws SystemException, BusinessException {
        try{
            return deploymentBO.unDeployWrapper(wrapperType);
        }finally {
            FlowPublishEvent event = new FlowPublishEvent();
            event.setEvent(FlowPublishEvent.UNDEPLOY);
            event.setTenantCode(RequestContext.getRequestContext().getTenantCode());
            DeploymentDescriptor deploymentDescriptor = new DeploymentDescriptor();
            deploymentDescriptor.setName(wrapperType);
            event.setDeploymentDescriptor(deploymentDescriptor);
            cacheRegistry.getTopic(BatchWrapperDeploymentListener.BATCH_WRAPPER_DEPLOYMENT_TOPIC).publish(event);
        }
    }
}
