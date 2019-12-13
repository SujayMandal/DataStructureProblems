package com.ca.umg.rt.cache.listener;

import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.rt.cache.event.FlowPublishEvent;
import com.ca.umg.rt.core.deployment.bo.DeploymentBO;
import com.ca.umg.rt.core.deployment.info.DeploymentDescriptor;
import com.ca.umg.rt.flows.container.ContainerManager;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

public class FlowPublishListener implements MessageListener<Object> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FlowPublishListener.class);
    private static final Executor MESSAGEEXECUTER = Executors.newSingleThreadExecutor();
    public static final String FLOW_PUBLISH_TOPIC = "flow-publish";
    private CacheRegistry cacheRegistry;
    private ContainerManager containerManager;
    private DeploymentBO deploymnetBO;
    
    private ITopic<Object> topic;
        
    public void init(){
        topic = cacheRegistry.getTopic(FLOW_PUBLISH_TOPIC);
        topic.addMessageListener(this);
    }

    @Override
    public void onMessage(final Message<Object> message) {
        final FlowPublishEvent event = (FlowPublishEvent)message.getMessageObject();
        MESSAGEEXECUTER.execute(new Runnable() {

            @Override
            public void run() {
                if(message.getPublishingMember().localMember()){
                    return;
                }
                RequestContext requestContext = null;
                try{
                    String tenant = event.getTenantCode();
                    DeploymentDescriptor descriptor = event.getDeploymentDescriptor();
                    Properties properties = new Properties();
                    properties.put(RequestContext.TENANT_CODE, tenant);
                    requestContext = new RequestContext(properties);
                    if(event.getEvent().equalsIgnoreCase(FlowPublishEvent.DEPLOY)){
                        deploymnetBO.deploy(descriptor);
                    } else if(event.getEvent().equalsIgnoreCase(FlowPublishEvent.UNDEPLOY)){
                        deploymnetBO.undeploy(descriptor , false);
                    }
                } catch (SystemException | BusinessException e) {
                    LOGGER.error("Deployment failed", e);
                }finally{
                    if(requestContext!=null){
                        requestContext.destroy();
                    }
                }
            }
        });
    }

    public CacheRegistry getCacheRegistry() {
        return cacheRegistry;
    }

    public void setCacheRegistry(CacheRegistry cacheRegistry) {
        this.cacheRegistry = cacheRegistry;
    }

    public ITopic<Object> getTopic() {
        return topic;
    }

    public ContainerManager getContainerManager() {
        return containerManager;
    }

    public void setContainerManager(ContainerManager containerManager) {
        this.containerManager = containerManager;
    }

    public DeploymentBO getDeploymnetBO() {
        return deploymnetBO;
    }

    public void setDeploymnetBO(DeploymentBO deploymnetBO) {
        this.deploymnetBO = deploymnetBO;
    }
}
