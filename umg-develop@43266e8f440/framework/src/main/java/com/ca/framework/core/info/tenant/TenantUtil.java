package com.ca.framework.core.info.tenant;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.event.TenantBulkPollingEvent;

public class TenantUtil {

    public static void initiateBulk(String tenantCode, CacheRegistry cacheRegistry, Boolean isBulkENabled) {
        TenantBulkPollingEvent bulkPoolingEvent = new TenantBulkPollingEvent();
        bulkPoolingEvent.setTenantCode(tenantCode);
        if (isBulkENabled) {
            bulkPoolingEvent.setEvent(TenantBulkPollingEvent.TENANT_BULK_POLLING_ENABLE_EVENT);
            cacheRegistry.getTopic(TenantBulkPollingEvent.TENANT_BULK_POLLING_ENABLE_EVENT).publish(bulkPoolingEvent);
        } else {
            bulkPoolingEvent.setEvent(TenantBulkPollingEvent.TENANT_BULK_POLLING_DISABLE_EVENT);
            cacheRegistry.getTopic(TenantBulkPollingEvent.TENANT_BULK_POLLING_DISABLE_EVENT).publish(bulkPoolingEvent);
        }

    }

    public static boolean isTenantConfigEnabled(final TenantInfo tenantInfo, final String systemKey,
            final CacheRegistry cacheRegistry)
            throws SystemException, BusinessException {
        boolean enabled = false;
        TenantInfo existtenantInfo = (TenantInfo) cacheRegistry.getMap(FrameworkConstant.TENANT_MAP).get(tenantInfo.getCode());
         
        if (existtenantInfo != null && Boolean.valueOf(existtenantInfo.getTenantConfigsMap().get(systemKey))) {
            enabled = true;
        }
        return enabled;
    }
	

}
