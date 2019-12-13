/**
 * 
 */
package com.ca.umg.event.listener;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.event.TenantBulkPollingEvent;
import com.ca.umg.file.UmgFilePoller;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

/**
 * Listens to bulk pooling events when bulk is enabled for a tenant.
 * 
 * @author mandasuj
 *
 */
@Named
public class TenantBulkPollingEventListener implements MessageListener<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantBulkPollingEventListener.class);

    @Inject
    private CacheRegistry cacheRegistry;

    @Inject
    private UmgFilePoller umgFilePoller;

    private ITopic<Object> tenantBulkPollingEnableTopic;

    private ITopic<Object> tenantBulkPollingDisableTopic;

    @PostConstruct
    public void init() {
        tenantBulkPollingEnableTopic = cacheRegistry.getTopic(TenantBulkPollingEvent.TENANT_BULK_POLLING_ENABLE_EVENT);
        tenantBulkPollingEnableTopic.addMessageListener(this);

        tenantBulkPollingDisableTopic = cacheRegistry.getTopic(TenantBulkPollingEvent.TENANT_BULK_POLLING_DISABLE_EVENT);
        tenantBulkPollingDisableTopic.addMessageListener(this);
    }

    @Override
    public void onMessage(Message<Object> message) {
        LOGGER.info("Receveid event to add/remove new tenant bulk/input folder path into folder monitor list.");
        TenantBulkPollingEvent event = (TenantBulkPollingEvent) message.getMessageObject();
        switch (StringUtils.upperCase(event.getEvent())) {
        case TenantBulkPollingEvent.TENANT_BULK_POLLING_ENABLE_EVENT:
            try {
                umgFilePoller.enableTenantBulkPoll(event.getTenantCode());
            } catch (SystemException e) {
                LOGGER.error(e.getLocalizedMessage(), e);
            }
            break;
        case TenantBulkPollingEvent.TENANT_BULK_POLLING_DISABLE_EVENT:
            try {
                umgFilePoller.disableTenantBulkPoll(event.getTenantCode());
            } catch (SystemException e) {
                LOGGER.error(e.getLocalizedMessage(), e);
            }
            break;
        default:
            break;
        }
    }

}
