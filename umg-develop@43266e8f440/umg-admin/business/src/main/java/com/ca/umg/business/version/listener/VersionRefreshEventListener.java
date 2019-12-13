/**
 * 
 */
package com.ca.umg.business.version.listener;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.event.util.EventOperations;
import com.ca.umg.business.version.data.VersionDataContainer;
import com.ca.umg.business.version.event.VersionRefreshEvent;
import com.ca.umg.business.version.info.VersionInfo;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

/**
 * @author kamathan
 *
 */
@Named
public class VersionRefreshEventListener implements MessageListener<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(VersionRefreshEventListener.class);

    @Inject
    private CacheRegistry cacheRegistry;

    @Inject
    private VersionDataContainer versionDataContainer;

    private ITopic<Object> topic;

    public static final String VERSION_REFRESH = "VERSION_REFRESH";

    @PostConstruct
    public void init() {
        topic = cacheRegistry.getTopic(VERSION_REFRESH);
        topic.addMessageListener(this);
    }

    @Override
    public void onMessage(Message<Object> message) {
        LOGGER.info("Received event from {}.", message.getPublishingMember().getSocketAddress().getHostName());
        VersionRefreshEvent<VersionInfo> event = (VersionRefreshEvent<VersionInfo>) message.getMessageObject();
        VersionInfo versionInfo = event.getData();
        switch (EventOperations.valueOf(event.getOperation())) {
        case ADD:
            versionDataContainer.addVersionToContainer(event.getTenantCode(), versionInfo.getName(),
                    versionInfo.getDescription());
            break;
        case REMOVE:
            versionDataContainer.removeVersionFromContainer(event.getTenantCode(), versionInfo.getName());
            break;
        default:
            break;
        }
    }

    public ITopic<Object> getTopic() {
        return topic;
    }

    public void setTopic(ITopic<Object> topic) {
        this.topic = topic;
    }

}
