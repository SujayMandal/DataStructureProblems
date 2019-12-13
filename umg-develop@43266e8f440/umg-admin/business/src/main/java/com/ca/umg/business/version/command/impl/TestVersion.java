/**
 * 
 */
package com.ca.umg.business.version.command.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.info.tenant.TenantInfo;
import com.ca.framework.core.publishing.status.constants.PublishingStatus;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.util.ConversionUtil;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.mapping.info.TestBedOutputInfo;
import com.ca.umg.business.tenant.delegate.TenantDelegate;
import com.ca.umg.business.tenant.entity.TenantConfig;
import com.ca.umg.business.version.command.annotation.CommandDescription;
import com.ca.umg.business.version.command.base.AbstractCommand;
import com.ca.umg.business.version.command.error.Error;
import com.ca.umg.business.version.delegate.VersionDelegate;
import com.ca.umg.business.version.info.VersionInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author kamathan
 *
 */
@Named
@Scope(BusinessConstants.SCOPE_PROTOTYPE)
@CommandDescription(name = "testVersion")
public class TestVersion extends AbstractCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestVersion.class);

    @Inject
    private VersionDelegate versionDelegate;

    @Inject
    private TenantDelegate tenantDelegate;

    @Inject
    private CacheRegistry cacheRegistry;

    private static final String TEST_VERSION = "testVersion";

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.version.command.Command#execute(java.lang.Object)
     */
    @Override
    public void execute(Object data) throws BusinessException, SystemException {
        List<Error> errors = new ArrayList<Error>();
        VersionInfo versionInfo;
        boolean execBreak = Boolean.FALSE;
        if (checkData(errors, data, TEST_VERSION, VersionInfo.class)) {
            Map<String, Object> header = null;
            Map<String, Object> convertedMap = null;
            LOGGER.debug("Data validated for GenerateTestInput");
            versionInfo = (VersionInfo) data;
            try {
                if (StringUtils.isNotBlank(versionInfo.getSampleTestInput())) {
                    setExecuted(Boolean.TRUE);
                    String tenantUrl = getTenantBaseUrl(tenantDelegate);
                    Map<String, TenantInfo> tenantMap = cacheRegistry.getMap(FrameworkConstant.TENANT_MAP);
                    TenantInfo tenantInfo = tenantMap.get(RequestContext.getRequestContext().getTenantCode());
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) mapper.readValue(
                            versionInfo.getSampleTestInput(), Map.class);
                    Map<String, Object> headermap = map.get("header");
                    // Map<String, Object> headermap = (Map<String, Object>)mapper.readValue(headerValue.toString(), Map.class);
                    headermap.put("clientID", versionInfo.getClientID());
                    TestBedOutputInfo testBedOutputInfo = versionDelegate.versionTest(mapper.writeValueAsString(map), tenantUrl,
                            tenantInfo.getActiveAuthToken(), versionInfo.getId());

                    convertedMap = ConversionUtil.convertJson(testBedOutputInfo.getOutputJson(), Map.class);
                    if (convertedMap !=  null) {
                        header = (Map<String, Object>) convertedMap.get("header");
                    }
                    if (header  != null) {
                        versionInfo.setUmgTransactionId((String) header.get("umgTransactionId"));
                    }
                    
                    versionInfo.setTestBedOutputInfo(testBedOutputInfo);
                    if (testBedOutputInfo.isError()) {
                        execBreak = Boolean.TRUE;
                        errors.add(new Error(testBedOutputInfo.getErrorMessage(), TEST_VERSION, testBedOutputInfo.getErrorCode()));
                    }
                }
            } catch (BusinessException | SystemException e) {
                errors.add(new Error(e.getLocalizedMessage(), TEST_VERSION, e.getCode()));
                execBreak = Boolean.TRUE;
            } catch (IOException e) {
                errors.add(new Error(e.getLocalizedMessage(), TEST_VERSION, e.getMessage()));
                execBreak = Boolean.TRUE;
            } finally {
                sendStatusMessage(errors, data, PublishingStatus.EXECUTING_TEST_TRANSACTION.getStatus());
            }

        } else {
            execBreak = Boolean.TRUE;
        }
        getErrorController().setErrors(errors);
        getErrorController().setExecutionBreak(execBreak);
    }

    private String getTenantBaseUrl(TenantDelegate tenantDelegate) throws BusinessException, SystemException {
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



    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.version.command.Command#rollback(java.lang.Object)
     */
    @Override
    public void rollback(Object data) throws BusinessException, SystemException {
        LOGGER.info("Rollback testVersion called.");

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.version.command.Command#isCreated()
     */
    @Override
    public boolean isCreated() throws BusinessException, SystemException {
        return true;
    }

}
