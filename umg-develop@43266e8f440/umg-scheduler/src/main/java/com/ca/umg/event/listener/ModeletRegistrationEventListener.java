/**
 * 
 */
package com.ca.umg.event.listener;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.pool.util.ModeletRegistrationEvent;
import com.ca.umg.file.processor.FileRequestProcessor;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

/**
 * Listens to modelet registration events and delegates the event data to request processor.
 * 
 * @author kamathan
 *
 */
@Named
public class ModeletRegistrationEventListener implements MessageListener<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModeletRegistrationEventListener.class);

    @Inject
    private CacheRegistry cacheRegistry;

    @Inject
    private FileRequestProcessor fileRequestProcessor;

    private ITopic<Object> modeletRegistrationTopic;
    
    @Value("${enable.modelet.event.reg.listener}")
    private Boolean modeletRegEventEnabled;

    @PostConstruct
    public void init() {
        modeletRegistrationTopic = cacheRegistry.getTopic(FrameworkConstant.MODELET_REG_LISTENER_EVENT);
        modeletRegistrationTopic.addMessageListener(this);
    }

    @Override
    public void onMessage(Message<Object> message) {
        if (modeletRegEventEnabled) {
            LOGGER.info("Received modelet registration event notification.");
            ModeletRegistrationEvent modeletRegistrationEvent = (ModeletRegistrationEvent) message.getMessageObject();
            if (modeletRegistrationEvent != null) {
                fileRequestProcessor.processFileByModel(modeletRegistrationEvent.getTenantCode(),
                        modeletRegistrationEvent.getModelName(), modeletRegistrationEvent.getMajorVersion());
            }
        }
    }

}
