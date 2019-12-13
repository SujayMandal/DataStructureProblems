package com.ca.umg.business.version.command.executor.impl;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.publishing.status.constants.PublishingStatus;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

@Named
public class ModeletExecEventListener implements MessageListener<Object> {

@Inject
private CacheRegistry cacheRegistry;

private ITopic<Object> modeletTopic;
private ITopic<Object> libTopic;
private ITopic<Object> modelTopic;
	    
	    
	    @PostConstruct
	    public void init() {
        modeletTopic = cacheRegistry.getTopic(PublishingStatus.MODELET_FOUND);
	    	modeletTopic.addMessageListener(this);
        libTopic = cacheRegistry.getTopic(PublishingStatus.LOAD_LIB);
	    	libTopic.addMessageListener(this);
        modelTopic = cacheRegistry.getTopic(PublishingStatus.LOAD_MODEL);
	    	modelTopic.addMessageListener(this);
	    }
	public CacheRegistry getCacheRegistry() {
			return cacheRegistry;
		}
		public void setCacheRegistry(CacheRegistry cacheRegistry) {
			this.cacheRegistry = cacheRegistry;
		}
		public ITopic<Object> getModeletTopic() {
			return modeletTopic;
		}
		public void setModeletTopic(ITopic<Object> modeletTopic) {
			this.modeletTopic = modeletTopic;
		}
		public ITopic<Object> getLibTopic() {
			return libTopic;
		}
		public void setLibTopic(ITopic<Object> libTopic) {
			this.libTopic = libTopic;
		}

    public ITopic<Object> getModelTopic() {
        return modelTopic;
    }

    public void setModelTopic(ITopic<Object> modelTopic) {
        this.modelTopic = modelTopic;
    }

    @Override
	public void onMessage(Message<Object> message) {
		 String event = (String) message.getMessageObject();
		 String clientID = event.substring(0, event.lastIndexOf('@'));
        String eventName = event.substring(event.lastIndexOf('@') + 1, event.length());
		 WSServerExecution ex = new WSServerExecution();
        switch (StringUtils.upperCase(eventName)) {
        case PublishingStatus.MODELET_FOUND:
            ex.sendStatusMessage(PublishingStatus.OBTAINING_MODELET.getStatus(), clientID);
            break;
        case PublishingStatus.LOAD_LIB:
            ex.sendStatusMessage(PublishingStatus.LOADING_LIBRARIES.getStatus(), clientID);
            break;
        case PublishingStatus.LOAD_MODEL:
            ex.sendStatusMessage(PublishingStatus.LOADING_MODEL_PACKAGE.getStatus(), clientID);
            break;
        default:
            break;

        }
	}
}
