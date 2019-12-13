package com.ca.umg.rt.cache.listener;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.rmodel.info.SupportPackage;
import com.ca.framework.core.rmodel.info.VersionExecInfo;
import com.ca.framework.event.StaticDataRefreshEvent;
import com.ca.framework.event.util.EventOperations;
import com.ca.umg.rt.util.container.StaticDataContainer;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

public class StaticDataContainerListener implements MessageListener<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StaticDataContainerListener.class);

    private CacheRegistry cacheRegistry;

    private ITopic<Object> tenantRefreshTopic;

    private ITopic<Object> versionExcEnvTopic;

    private ITopic<Object> refreshSupportPkgTpoic;

    private ITopic<Object> refreshModelPkgNameTopic;

    private ITopic<Object> refreshOnlyTenantTopic;

    private StaticDataContainer staticDataContainer;

    public static final String STATIC_DATA_REFRESH = "STATIC_DATA_REFRESH";

    public void init() {
        tenantRefreshTopic = cacheRegistry.getTopic(STATIC_DATA_REFRESH);
        tenantRefreshTopic.addMessageListener(this);

        versionExcEnvTopic = cacheRegistry.getTopic(StaticDataRefreshEvent.REFRESH_VERSION_EXC_ENV_MAP);
        versionExcEnvTopic.addMessageListener(this);

        refreshModelPkgNameTopic = cacheRegistry.getTopic(StaticDataRefreshEvent.REFRESH_PACKAGE_NAMES);
        refreshModelPkgNameTopic.addMessageListener(this);

        refreshSupportPkgTpoic = cacheRegistry.getTopic(StaticDataRefreshEvent.REFRESH_SUPPORT_PACKAGES_EVENT);
        refreshSupportPkgTpoic.addMessageListener(this);

        refreshOnlyTenantTopic = cacheRegistry.getTopic(StaticDataRefreshEvent.REFRESH_ONLY_TENANT_EVENT);
        refreshOnlyTenantTopic.addMessageListener(this);
    }

    @Override
    public void onMessage(Message<Object> message) {
        LOGGER.info("Receveid event to update the cache.");
        StaticDataRefreshEvent event = (StaticDataRefreshEvent) message.getMessageObject();
        switch (StringUtils.upperCase(event.getEvent())) {
        case StaticDataRefreshEvent.REFRESH_SUPPORT_PACKAGES_EVENT:
            refreshSupportPackageMap(event);
            break;
        case StaticDataRefreshEvent.REFRESH_PACKAGE_NAMES:
            refreshSupportPackageNamesMap(event);
            break;
        case StaticDataRefreshEvent.REFRESH_VERSION_EXC_ENV_MAP:
            refreshVersionExecEnvMap(event);
            break;
        case StaticDataRefreshEvent.REFRESH_ONLY_TENANT_EVENT:
            staticDataContainer.loadTenant();
            break;
        default:
            break;
        }
    }

    private void refreshSupportPackageMap(StaticDataRefreshEvent<List<SupportPackage>> event) {
        switch (EventOperations.valueOf(StringUtils.upperCase(event.getOperation()))) {
        case ADD:
            staticDataContainer.addSupportPackage(event.getTenantCode(), event.getVersionKey(), event.getData());
            break;
        case REMOVE:
            staticDataContainer.removeSupportPackage(event.getTenantCode(), event.getVersionKey());
            break;
        case UPDATE:
        default:
            break;
        }
    }

    private void refreshSupportPackageNamesMap(StaticDataRefreshEvent<String> event) {
        switch (EventOperations.valueOf(StringUtils.upperCase(event.getOperation()))) {
        case ADD:
            staticDataContainer.addPackageName(event.getTenantCode(), event.getVersionKey(), event.getData());
            break;
        case REMOVE:
            staticDataContainer.removePackageName(event.getTenantCode(), event.getVersionKey());
            break;
        case UPDATE:
        default:
            break;
        }
    }

    private void refreshVersionExecEnvMap(StaticDataRefreshEvent<VersionExecInfo> event) {
        switch (EventOperations.valueOf(StringUtils.upperCase(event.getOperation()))) {
        case ADD:
            staticDataContainer.addVersionEnvironmentMapping(event.getTenantCode(), event.getVersionKey(), event.getData());
            break;
        case REMOVE:
            staticDataContainer.removeVersionEnvironmentMapping(event.getTenantCode(), event.getVersionKey());
            break;
        case UPDATE:
        default:
            break;
        }
    }

    public CacheRegistry getCacheRegistry() {
        return cacheRegistry;
    }

    public void setCacheRegistry(CacheRegistry cacheRegistry) {
        this.cacheRegistry = cacheRegistry;
    }

    public ITopic<Object> getTenantRefreshTopic() {
        return tenantRefreshTopic;
    }

    public void setTopic(ITopic<Object> topic) {
        this.tenantRefreshTopic = topic;
    }

    public StaticDataContainer getStaticDataContainer() {
        return staticDataContainer;
    }

    public void setStaticDataContainer(StaticDataContainer staticDataContainer) {
        this.staticDataContainer = staticDataContainer;
    }

    public ITopic<Object> getVersionExcEnvTopic() {
        return versionExcEnvTopic;
    }

    public ITopic<Object> getRefreshSupportPkgTpoic() {
        return refreshSupportPkgTpoic;
    }

    public ITopic<Object> getRefreshModelPkgNameTopic() {
        return refreshModelPkgNameTopic;
    }

    public ITopic<Object> getRefreshOnlyTenantTopic() {
        return refreshOnlyTenantTopic;
    }

    public void setRefreshOnlyTenantTopic(ITopic<Object> refreshOnlyTenantTopic) {
        this.refreshOnlyTenantTopic = refreshOnlyTenantTopic;
    }

}