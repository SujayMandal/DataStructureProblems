package com.ca.framework.core.tenant;

import java.sql.SQLException;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.db.persistance.TenantRoutingDataSource;
import com.ca.framework.event.TenantDataRefreshEvent;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

public class TenantDataContainerListener implements MessageListener<Object> {

    @Inject
    private TenantRoutingDataSource tenantRoutingDataSource;

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantDataContainerListener.class);

    private CacheRegistry cacheRegistry;

    private ITopic<Object> tenantConfigRefreshTopic;

    public void init() {
        tenantConfigRefreshTopic = cacheRegistry.getTopic(TenantDataRefreshEvent.REFRESH_TENANT_CONFIG_EVENT);
        tenantConfigRefreshTopic.addMessageListener(this);
    }

    @Override
    public void onMessage(Message<Object> message) {
        LOGGER.info("Receveid event to update the cache.");
        TenantDataRefreshEvent configEvent = (TenantDataRefreshEvent) message.getMessageObject();
        try {
            tenantRoutingDataSource.createDataSourceForTenantCode(configEvent.getTenantCode());
        } catch (SQLException e) {
            // throw new FatalBeanException("Error creating the TenantRoutingDataSource - This is fatal", e);
            LOGGER.error("Exception occured creating datasource.");
        }
    }

    public CacheRegistry getCacheRegistry() {
        return cacheRegistry;
    }

    public void setCacheRegistry(CacheRegistry cacheRegistry) {
        this.cacheRegistry = cacheRegistry;
    }

    public ITopic<Object> getTenantConfigRefreshTopic() {
        return tenantConfigRefreshTopic;
    }

    public void setTenantConfigRefreshTopic(ITopic<Object> tenantConfigRefreshTopic) {
        this.tenantConfigRefreshTopic = tenantConfigRefreshTopic;
    }

}
