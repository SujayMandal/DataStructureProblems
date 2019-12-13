package com.ca.umg.sdc.rest.controller;

import java.util.Map;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.info.tenant.TenantInfo;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.mapping.info.MappingInfo;
import com.ca.umg.business.model.info.ModelLibraryInfo;
import com.ca.umg.business.tenant.delegate.TenantDelegate;
import com.ca.umg.business.tenant.entity.TenantConfig;
import com.ca.umg.business.version.info.CreateVersionInfo;
import com.ca.umg.business.version.info.VersionInfo;

public final class VersionControllerHelper {

    private VersionControllerHelper() {
    }

    public static VersionInfo mapToVersionInfo(CreateVersionInfo input) throws BusinessException {
        MappingInfo mapping = new MappingInfo();
        ModelLibraryInfo modelLibrary = new ModelLibraryInfo();
        VersionInfo version = new VersionInfo();
        version.setName(input.getTenantModelName());
        version.setDescription(input.getTenantModelDescription());
        setMajorVersion(input, version);
        version.setVersionDescription(input.getVersionDescription());
        mapping.setName(input.getTidName());
        version.setMapping(mapping);
        modelLibrary.setUmgName(input.getLibraryRecord());
        version.setModelLibrary(modelLibrary);
        return version;
    }

    public static String getTenantBaseUrl(TenantDelegate tenantDelegate) throws BusinessException, SystemException {
        String tenantBaseUrl = null;
        TenantConfig tenantConfig = tenantDelegate.getTenantConfig(RequestContext.getRequestContext().getTenantCode(),
                SystemConstants.SYSTEM_KEY_TENANT_URL, SystemConstants.SYSTEM_KEY_TYPE_TENANT);
        if (tenantConfig != null) {
            tenantBaseUrl = tenantConfig.getValue();
        } else {
            SystemException.newSystemException(BusinessExceptionCodes.BSE000069, new Object[] { RequestContext
                    .getRequestContext().getTenantCode() });
        }
        return tenantBaseUrl;
    }

    private static void setMajorVersion(CreateVersionInfo input, VersionInfo version) throws BusinessException {
        if (isCreateMinorVersion(input)) {
            if (isMajorEmpty(input)) {
                throw new BusinessException(BusinessExceptionCodes.BSE000073, new Object[] {});
            } else {
                version.setMajorVersion(input.getMajorVersion());
            }
        }
    }

    private static boolean isMajorEmpty(CreateVersionInfo input) {
        return input.getMajorVersion() == null || input.getMajorVersion() == 0;
    }

    private static boolean isCreateMinorVersion(CreateVersionInfo input) {
        return input.getVersionType().equalsIgnoreCase("MINOR");
    }

    public static String getTenantAuthToken(CacheRegistry cacheRegistry)
            throws BusinessException, SystemException {
        Map<String, TenantInfo> tenantMap = cacheRegistry.getMap(FrameworkConstant.TENANT_MAP);
        TenantInfo tenantInfo = tenantMap.get(RequestContext.getRequestContext().getTenantCode());
        return tenantInfo.getActiveAuthToken();
	}

}
