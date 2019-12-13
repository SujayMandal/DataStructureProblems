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
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

public class BatchFlowDeploymentListener implements MessageListener<Object> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BatchFlowDeploymentListener.class);
    private static final Executor BATCH_MESSAGEEXECUTER = Executors.newSingleThreadExecutor();
    public static final String BATCH_DEPLOYMENT_TOPIC = "batch-deployment";
    private CacheRegistry cacheRegistry;
    private DeploymentBO deploymnetBO;
    
    private ITopic<Object> topic;
        
    public void init(){
        topic = cacheRegistry.getTopic(BATCH_DEPLOYMENT_TOPIC);
        topic.addMessageListener(this);
    }

    @Override
    public void onMessage(final Message<Object> message) {
        final FlowPublishEvent event = (FlowPublishEvent)message.getMessageObject();
        BATCH_MESSAGEEXECUTER.execute(new Runnable() {

            @Override
            public void run() {
                if(message.getPublishingMember().localMember()){
                    return;
                }
                RequestContext requestContext = null;
                try{
                    String tenant = event.getTenantCode();
                    Properties properties = new Properties();
                    properties.put(RequestContext.TENANT_CODE, tenant);
                    requestContext = new RequestContext(properties);
                    if(event.getEvent().equalsIgnoreCase(FlowPublishEvent.DEPLOY)){
                        deploymnetBO.deployBatch();
                    } else if(event.getEvent().equalsIgnoreCase(FlowPublishEvent.UNDEPLOY)){
                        deploymnetBO.undeployBatch();
                    }
                } catch (SystemException | BusinessException e) {
                    LOGGER.error("Batch Deployment failed", e);
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

    public DeploymentBO getDeploymnetBO() {
        return deploymnetBO;
    }

    public void setDeploymnetBO(DeploymentBO deploymnetBO) {
        this.deploymnetBO = deploymnetBO;
    }
}
