/**
 *
 */
package com.ca.umg.business.execution.bo;

import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.delegate.AbstractDelegate;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.info.tenant.TenantInfo;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.modelet.ModeletClientInfo;
import com.ca.pool.ModeletPoolingResponse;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.execution.dao.ModelExecutionEnvironmentDAO;
import com.ca.umg.business.execution.entity.ModelExecutionEnvironment;
import com.ca.umg.business.integration.runtime.RuntimeIntegrationClient;
import com.ca.umg.business.model.info.ModelExecutionEnvironmentInfo;
import com.ca.umg.business.tenant.bo.TenantBO;
import com.ca.umg.business.tenant.delegate.AuthTokenDelegate;
import com.ca.umg.business.tenant.entity.Tenant;
import com.ca.umg.business.tenant.entity.TenantConfig;
import com.ca.umg.business.util.AdminUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;


/**
 * @author nigampra
 *
 */
@Service
public class ModelExecutionEnvironmentBOImpl extends AbstractDelegate implements ModelExecutionEnvironmentBO {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelExecutionEnvironmentBOImpl.class);

    @Inject
    private ModelExecutionEnvironmentDAO executionEnvDAO;

    @Inject
    private AuthTokenDelegate authTokendelegate;

    @Inject
    private RuntimeIntegrationClient runtimeIntegrationClient;

    @Inject
    private TenantBO tenantBO;

    /*
     * (non-Javadoc)
     *
     * @see com.ca.umg.business.execution.bo.ModelExecutionEnvironmentBO#getModelExecutionEnvironment(java.lang.String,
     * java.lang.String)
     */
    @Override
    public ModelExecutionEnvironment getModelExecutionEnvironment(String executionEnvironment, String environmentVersion)
            throws BusinessException, SystemException {
        return executionEnvDAO.findByExecutionEnvironmentAndEnvironmentVersion(executionEnvironment, environmentVersion);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ca.umg.business.execution.bo.ModelExecutionEnvironmentBO#getAllRModelExecutionEnvironment()
     */
    @Override
    public List<ModelExecutionEnvironment> getAllRModelExecutionEnvironment() throws BusinessException, SystemException {
        return executionEnvDAO.findByExecutionEnvironment(BusinessConstants.R_LANG);
    }

    @Override
    public ModelExecutionEnvironment getModelExecutionEnvByName(String name) {
        return executionEnvDAO.findByName(name);
    }

    @Override
    public String getActiveRVersion(String executionEnvironment) {
        List<ModelExecutionEnvironment> activeVersions = executionEnvDAO.findByExecutionEnvironmentAndActive(executionEnvironment, 'T');
        return activeVersions.get(0).getEnvironmentVersion();
    }

    @Override
    public ModelExecutionEnvironmentInfo getModelExecutionEnvById(String id) throws SystemException {
        ModelExecutionEnvironmentInfo executionEnvironmentInfo = null;
        ModelExecutionEnvironment executionEnvironment = null;
        boolean actualAdminAware = AdminUtil.getActualAdminAware();
        AdminUtil.setAdminAwareTrue();
        try {
            executionEnvironment = executionEnvDAO.findById(id);
        } catch (DataAccessException e) {
            LOGGER.error("Problem in retrieving modelet execution environment. {}", e);
            SystemException.newSystemException(BusinessExceptionCodes.BSE001014, new Object[]{});
        } finally {
            AdminUtil.setActualAdminAware(actualAdminAware);
        }
        if (executionEnvironment != null) {
            executionEnvironmentInfo = convert(executionEnvironment, ModelExecutionEnvironmentInfo.class);
        }
        return executionEnvironmentInfo;
    }

    @Override
    public List<ModelExecutionEnvironmentInfo> getActiveModelExecutionEnvList() throws SystemException {
        List<ModelExecutionEnvironment> executionEnvironments = null;
        List<ModelExecutionEnvironmentInfo> executionEnvironmentInfos = null;
        boolean actualAdminAware = AdminUtil.getActualAdminAware();
        AdminUtil.setAdminAwareTrue();
        try {
            executionEnvironments = executionEnvDAO.findByActive('T');
        } catch (DataAccessException e) {
            LOGGER.error("Problem in retrieving modelet execution environment. {}", e);
            SystemException.newSystemException(BusinessExceptionCodes.BSE001022, new Object[]{});
        } finally {
            AdminUtil.setActualAdminAware(actualAdminAware);
        }
        if (executionEnvironments != null) {
            executionEnvironmentInfos = convertToList(executionEnvironments, ModelExecutionEnvironmentInfo.class);
        }
        return executionEnvironmentInfos;
    }

    @Override
    public void restartModelets(List<ModeletClientInfo> modeletClientInfoList) throws SystemException, BusinessException {
        RequestContext requestContext = RequestContext.getRequestContext();
        requestContext.setAdminAware(true);
        final String authKey = authTokendelegate.getActiveAuthCode(getTenant(RequestContext.getRequestContext().getTenantCode()).getId());
        final String tenantBaseUrl = getSystemKeyValue(SystemConstants.SYSTEM_KEY_TENANT_URL, SystemConstants.SYSTEM_KEY_TYPE_TENANT);
        requestContext.setAdminAware(false);
        ModeletPoolingResponse modeletPoolingResponse = runtimeIntegrationClient.restartModelets(tenantBaseUrl, authKey, modeletClientInfoList);
        LOGGER.info("Restart modelet(s) status :" + modeletPoolingResponse.getStatus());
    }

    @Override
    public String downloadModeletLogs(ModeletClientInfo modeletClientInfo) throws SystemException, BusinessException {
        RequestContext requestContext = RequestContext.getRequestContext();
        requestContext.setAdminAware(true);
        final String authKey = authTokendelegate.getActiveAuthCode(getTenant(RequestContext.getRequestContext().getTenantCode()).getId());
        final String tenantBaseUrl = getSystemKeyValue(SystemConstants.SYSTEM_KEY_TENANT_URL, SystemConstants.SYSTEM_KEY_TYPE_TENANT);
        requestContext.setAdminAware(false);
        ModeletPoolingResponse modeletPoolingResponse = runtimeIntegrationClient.downloadModeletLogs(tenantBaseUrl, authKey, modeletClientInfo);
        return modeletPoolingResponse.getStatus();
    }

    private TenantInfo getTenant(final String code) throws BusinessException, SystemException {
        Tenant tenant = tenantBO.getTenant(code);
        return convert(tenant, TenantInfo.class);
    }

    private String getSystemKeyValue(final String key, final String type) throws BusinessException, SystemException {
        String sysKeyValue = null;
        String tenantCode = RequestContext.getRequestContext().getTenantCode();
        LOGGER.info("Fetching value for system key {} and tenant {}.", key, tenantCode);
        TenantConfig tenantConfig = tenantBO.getTenantConfigDetails(tenantCode, key, type);
        if (tenantConfig != null) {
            sysKeyValue = tenantConfig.getValue();
        } else {
            SystemException.newSystemException(BusinessExceptionCodes.BSE000109, new Object[]{key, tenantCode});
        }
        return sysKeyValue;
    }
}
