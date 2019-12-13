/**
 * 
 */
package com.ca.umg.business.tenant.delegate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;

import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.delegate.AbstractDelegate;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.info.tenant.TenantConfigInfo;
import com.ca.framework.core.info.tenant.TenantInfo;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.framework.core.util.UmgFileProxy;
import com.ca.umg.business.accessprivilege.bo.RolesPrivilegesBO;
import com.ca.umg.business.batching.info.BatchFileInfo;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.integration.runtime.RuntimeIntegrationClient;
import com.ca.umg.business.model.info.ModelArtifact;
import com.ca.umg.business.tenant.bo.TenantBO;
import com.ca.umg.business.tenant.configurator.TenantConfigurator;
import com.ca.umg.business.tenant.entity.AuthToken;
import com.ca.umg.business.tenant.entity.SystemKey;
import com.ca.umg.business.tenant.entity.Tenant;
import com.ca.umg.business.tenant.entity.TenantConfig;
import com.ca.umg.business.transaction.mongo.dao.MongoTransactionDAOImpl;
import com.ca.umg.business.util.AdminUtil;
import com.ca.umg.business.version.data.VersionDataContainer;

/**
 * @author kamathan
 * @version 1.0
 */
@Named
public class TenantDelegateImpl extends AbstractDelegate implements TenantDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantDelegateImpl.class.getName());

    @Inject
    private TenantBO tenantBO;

    @Inject
    private TenantConfigurator tenantConfigurator;

    @Inject
    private SystemParameterProvider systemParameterProvider;

    @Inject
    private RuntimeIntegrationClient runtimeIntegrationClient;

    @Inject
    private UmgFileProxy umgFileProxy;

    @Inject
    private VersionDataContainer versionDataContainer;

    @Inject
    private MongoTransactionDAOImpl mongoTransactionDAOImpl;

    @Inject
    private RolesPrivilegesBO rolesPrivilegesBO;

    @Inject
    private AuthTokenDelegate authTokenDelegate;
    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.tenant.delegate.TenantDelegate#listAllTenants()
     */
    @SuppressWarnings("unchecked")
    @Override
    @PreAuthorize("hasRole(T(com.ca.umg.business.constants.BusinessConstants).ROLE_SUPER_ADMIN)")
    public List<TenantInfo> listAll() throws BusinessException, SystemException {
        List<TenantInfo> tenantInfos = null;
        List<Tenant> tenants = tenantBO.listAll();
        if (CollectionUtils.isNotEmpty(tenants)) {
            LOGGER.debug("Found {} tenants in the system.", tenants.size());
            tenantInfos = convertToList(tenants, TenantInfo.class);
        }
        return tenantInfos;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.tenant.delegate.TenantDelegate#saveTenant(com.ca. umg.business.tenant.info.TenantInfo)
     */
    @Override
    public TenantInfo create(TenantInfo tenantInfo, boolean isNewSchema) throws SystemException {
        LOGGER.info("Received {} for saving.", tenantInfo);
        Tenant tenant = convert(tenantInfo, Tenant.class);
        try {
            // save tenant information
            Set<AuthToken> authTokens = new HashSet<AuthToken>();
            authTokens.add(authTokenDelegate.create(tenant));

            tenant.setAuthTokens(authTokens);
            tenant = tenantBO.save(tenant);
            LOGGER.info("Tenant {} saved with id {}.", tenantInfo.getName(), tenantInfo.getId());

            // check if tenant needs seperate schema
            /*
             * if (isNewSchema) {
             * 
             * LOGGER.info("Initiated provisioning new schema for tenant {}.", tenantInfo.getName()); // provision new schema to
             * tenant and initialise tenantConfigurator.provisionSchema(tenant);
             * LOGGER.info("Completed schema cretion for tenant {}.", tenantInfo.getName());
             * 
             * 
             * } else {
             */
                // initialize tenant datasource
                tenantConfigurator.initializeTenantDatasource(tenant);
                versionDataContainer.buildTenantContainer(tenant);
                mongoTransactionDAOImpl.createMongoCollection(tenant.getCode());
                tenantBO.tenantFoldersCreation(tenant);
            // }
            LOGGER.info("Started creating default roles for tenant {}", tenant.getCode());
            rolesPrivilegesBO.createDefaultRolesAndPrivileges(tenant.getCode());
            LOGGER.info("Created default roles for tenant {}", tenant.getCode());

        } catch (SystemException ex) {// NOPMD
            revertTenantOnboarding(tenant);
            throw ex;
        } catch (Exception ex) {// NOPMD
            revertTenantOnboarding(tenant);
            SystemException.newSystemException(BusinessExceptionCodes.BSE000900, new Object[] { ex.getMessage() });
        }
        return convert(tenant, TenantInfo.class);

    }

    private void revertTenantOnboarding(Tenant tenant) throws SystemException  {
        rolesPrivilegesBO.deleteDefaultRolesAndPrivileges(tenant.getCode());
        tenantBO.delete(tenant);
        LOGGER.info("Rollback - new tenant deleted from mysql db");
        versionDataContainer.rollbackTenantContainer(tenant);
        mongoTransactionDAOImpl.dropMongoCollection(tenant.getCode() + FrameworkConstant.DOCUMENTS);
        mongoTransactionDAOImpl.dropMongoCollection(tenant.getCode() + FrameworkConstant.UNDERSCORE+FrameworkConstant.TENANTINPUT+FrameworkConstant.DOCUMENTS);
        mongoTransactionDAOImpl.dropMongoCollection(tenant.getCode() + FrameworkConstant.UNDERSCORE+FrameworkConstant.TENANTOUTPUT+FrameworkConstant.DOCUMENTS);
        mongoTransactionDAOImpl.dropMongoCollection(tenant.getCode() + FrameworkConstant.UNDERSCORE+FrameworkConstant.MODELINPUT+FrameworkConstant.DOCUMENTS);
        mongoTransactionDAOImpl.dropMongoCollection(tenant.getCode() + FrameworkConstant.UNDERSCORE+FrameworkConstant.MODELOUTPUT+FrameworkConstant.DOCUMENTS);
        tenantBO.newTenantFoldersDeletion(tenant);

        LOGGER.info("Tenant creation failure");
    }


    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.tenant.delegate.TenantDelegate#getTenantConfig(java .lang.String, java.lang.String,
     * java.lang.String)
     */
    @Override
    public TenantConfig getTenantConfig(String code, String key, String type) throws BusinessException, SystemException {
        return tenantBO.getTenantConfigDetails(code, key, type);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.tenant.delegate.TenantDelegate#getTenant(java.lang .String)
     */
    @Override
    public TenantInfo getTenant(String code) throws BusinessException, SystemException {
        Tenant tenant = tenantBO.getTenant(code);
        return convert(tenant, TenantInfo.class);
    }


    /*
     * e (non-Javadoc)
     * 
     * @see com.ca.umg.business.tenant.delegate.TenantDelegate#getTenantWithAllSystemKeys (java.lang.String)
     */
    @Override
    public TenantInfo getTenantWithAllSystemKeys() throws BusinessException, SystemException {
        Tenant tenant = tenantBO.getTenant(RequestContext.getRequestContext().getTenantCode());
        List<SystemKey> systemKeys = tenantBO.getSystemKeys();

        MultiKeyMap existingConfigMap = new MultiKeyMap();
        Set<TenantConfig> existingConfigs = tenant.getTenantConfigs();
        for (TenantConfig tenantConfig : existingConfigs) {
            SystemKey systemKey = tenantConfig.getSystemKey();
            existingConfigMap.put(systemKey.getKey(), systemKey.getType(), tenantConfig);
        }
        for (SystemKey systemKey : systemKeys) {
            if ((TenantConfig) existingConfigMap.get(systemKey.getKey(), systemKey.getType()) == null) {
                TenantConfig tenantConnfig = new TenantConfig();
                tenantConnfig.setSystemKey(systemKey);
                existingConfigs.add(tenantConnfig);
            }

        }
        Set<TenantConfig> configsWithoutSchemaSysKey = new HashSet<TenantConfig>();
        for (TenantConfig tenantConfig : existingConfigs) {
            if (!(StringUtils.equals(SystemConstants.SYSTEM_KEY_DB_SCHEMA, tenantConfig.getSystemKey().getKey())
                    && StringUtils.equals(SystemConstants.SYSTEM_KEY_TYPE_DATABASE, tenantConfig.getSystemKey().getType()))) {
                configsWithoutSchemaSysKey.add(tenantConfig);

            }
        }
        // Nullifying unused variables and setting tenantconfigs with out schema systemkey and database systemtype
        existingConfigs = null;// NOPMD
        existingConfigMap = null;// NOPMD
        tenant.setTenantConfigs(configsWithoutSchemaSysKey);

        TenantInfo tenantInfo = convert(tenant, TenantInfo.class);
        Set<TenantConfigInfo> tenantConfigsSet = new TreeSet<TenantConfigInfo>(new TenantConfigComparator());
        tenantConfigsSet.addAll(tenantInfo.getTenantConfigs());
        tenantInfo.setTenantConfigs(tenantConfigsSet);

        return tenantInfo;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.tenant.delegate.TenantDelegate#getAllSystemKeys (java.lang.String)
     */
    @Override
    @PreAuthorize("hasRole(T(com.ca.umg.business.constants.BusinessConstants).ROLE_SUPER_ADMIN)")
    public List<SystemKey> getAllSystemKeys() throws BusinessException, SystemException {
        return tenantBO.getSystemKeys();
    }

    @Override
    public TenantInfo getTenantWithAllSystemKeys(String code) throws BusinessException, SystemException {
        Tenant tenant = tenantBO.getTenant(code);
        List<SystemKey> systemKeys = tenantBO.getSystemKeys();

        MultiKeyMap existingConfigMap = new MultiKeyMap();
        Set<TenantConfig> existingConfigs = tenant.getTenantConfigs();
        for (TenantConfig tenantConfig : existingConfigs) {
            SystemKey systemKey = tenantConfig.getSystemKey();
            existingConfigMap.put(systemKey.getKey(), systemKey.getType(), tenantConfig);
        }
        for (SystemKey systemKey : systemKeys) {
            if ((TenantConfig) existingConfigMap.get(systemKey.getKey(), systemKey.getType()) == null) {
                TenantConfig tenantConnfig = new TenantConfig();
                tenantConnfig.setSystemKey(systemKey);
                existingConfigs.add(tenantConnfig);
            }

        }
        Set<TenantConfig> configsWithoutSchemaSysKey = new HashSet<TenantConfig>();
        for (TenantConfig tenantConfig : existingConfigs) {
            if (!(StringUtils.equals(SystemConstants.SYSTEM_KEY_DB_SCHEMA, tenantConfig.getSystemKey().getKey())
                    && StringUtils.equals(SystemConstants.SYSTEM_KEY_TYPE_DATABASE, tenantConfig.getSystemKey().getType()))) {
                configsWithoutSchemaSysKey.add(tenantConfig);

            }
        }
        // Nullifying unused variables and setting tenantconfigs with out schema systemkey and database systemtype
        existingConfigs = null;// NOPMD
        existingConfigMap = null;// NOPMD
        tenant.setTenantConfigs(configsWithoutSchemaSysKey);

        TenantInfo tenantInfo = convert(tenant, TenantInfo.class);
        Set<TenantConfigInfo> tenantConfigsSet = new TreeSet<TenantConfigInfo>(new TenantConfigComparator());
        tenantConfigsSet.addAll(tenantInfo.getTenantConfigs());
        tenantInfo.setTenantConfigs(tenantConfigsSet);

        try {

            tenantInfo.setActiveAuthToken(authTokenDelegate.getActiveAuthCode(tenantInfo.getId()));
            tenantInfo.setActiveUntil(AdminUtil
                    .getDateFormatMillisForEst(authTokenDelegate.getActiveAuthToken(tenantInfo.getId()).getActiveUntil(), null));
        } catch (SystemException ex) {// NOPMD
            LOGGER.error("There is no active auth token");

        }

        return tenantInfo;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.tenant.delegate.TenantDelegate#update(com.ca.umg. business.tenant.info.TenantInfo,
     * com.ca.umg.business.tenant.entity.Tenant)
     */
    @Override
    public TenantInfo update(TenantInfo tenantInfo) throws SystemException, BusinessException {
        LOGGER.info("Received {} for updating.", tenantInfo);
        Tenant newTenant = convert(tenantInfo, Tenant.class);
        // save tenant information
        Tenant existingtenant = tenantBO.getTenant(tenantInfo.getCode());
        Tenant tenant = tenantBO.update(newTenant, existingtenant);
        TenantInfo newTenantInfo = convert(tenant, TenantInfo.class);
        Set<TenantConfigInfo> tenantConfigsSet = new TreeSet<TenantConfigInfo>(new TenantConfigComparator());
        tenantConfigsSet.addAll(newTenantInfo.getTenantConfigs());
        newTenantInfo.setTenantConfigs(tenantConfigsSet);
        LOGGER.info("Tenant {} has been updated successfully.", newTenant.getName(), tenantInfo.getId());
        versionDataContainer.buildTenantContainer(tenant);
        return newTenantInfo;
    }

    @Override
    public void fileUpload(BatchFileInfo batchFileInfo) throws BusinessException, SystemException {
        StringBuffer filePathBfr = new StringBuffer(AdminUtil
                .getSanBasePath(umgFileProxy.getSanPath(systemParameterProvider.getParameter(SystemConstants.SAN_BASE))));
        filePathBfr.append(File.separatorChar).append(BusinessConstants.BATCH_FILE).append(File.separatorChar)
        .append(BusinessConstants.INPUT_FOLDER).append(File.separatorChar)
        .append(FilenameUtils.getBaseName(batchFileInfo.getFileName())).append(BusinessConstants.UNDERSCORE)
        .append(BusinessConstants.UI_PRIFIX).append(BusinessConstants.UNDERSCORE).append(DateTime.now().getMillis())
        .append(BusinessConstants.DOT).append(FilenameUtils.getExtension(batchFileInfo.getFileName()));
        File file = new File(filePathBfr.toString());
        writeFileToDirectory(file, batchFileInfo.getFileInputStream());
    }

    /**
     * Writes the given {@link ModelArtifact} to the system defined san location.
     * 
     * @param file
     * @param modelArtifact
     * @throws SystemException
     */
    @Override
    public void writeFileToDirectory(File file, InputStream is) throws SystemException {
        OutputStream outputStream = null;
        byte[] byteInStream = null;
        // create directory for upload if it does not exist
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            LOGGER.info("Saving Batch File {} to directory {}", file.getName(), file.getParentFile());
            outputStream = new FileOutputStream(file);
            byteInStream = new byte[is.available()];
            is.read(byteInStream);
            outputStream.write(byteInStream);
            is.close();
            LOGGER.info("Successfully saved Batch File {} to directory {}", file.getName(), file.getParentFile());
        } catch (IOException e) {
            throw new SystemException(BusinessExceptionCodes.BSE000005, new Object[] {}, e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    LOGGER.error("Exception occured closing Output Stream", e);
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    LOGGER.error("Exception occured closing Input Stream", e);
                }
            }
        }

    }

    @Override
    public void deployBatch(String code) throws BusinessException, SystemException {

        String authKey = authTokenDelegate.getActiveAuthCode(
                getTenant(code).getId());
        String tenantBaseUrl = getSystemKeyValueForTenant(SystemConstants.SYSTEM_KEY_TENANT_URL,
                SystemConstants.SYSTEM_KEY_TYPE_TENANT, code);

        /*boolean isFTPEnabled = Boolean
                .valueOf(getSystemKeyValueForTenant(SystemConstants.SYSTEM_KEY_FTP,
                        SystemConstants.SYSTEM_KEY_TENANT_WRAPPER_TYPE, code));*/
        String deployUrl = systemParameterProvider.getParameter(BusinessConstants.BATCH_DEPLOY_URL);

/*        if (!isFTPEnabled) {
*/            LOGGER.info("Starting Batch Deployment ...");
            runtimeIntegrationClient.deployBatch(tenantBaseUrl, deployUrl, authKey, code);
        /*}*/

        /*if (isFTPEnabled) {
            String ftpDeployUrl = systemParameterProvider.getParameter(BusinessConstants.FTP_DEPLOY_URL);
            LOGGER.info("Starting Batch Deployment ...");
            runtimeIntegrationClient.deployBatch(tenantBaseUrl, deployUrl, authKey, code);
            LOGGER.info("Batch Deployment Finished Successfully ...");
            LOGGER.info("Starting FTP Deployment ...");
            runtimeIntegrationClient.deployBatch(tenantBaseUrl, ftpDeployUrl, authKey, code);
            LOGGER.info("FTP Deployment Finished Successfully ...");
        }*/

    }

    @Override
    public void undeployBatch(String code) throws BusinessException, SystemException {

        String authKey = authTokenDelegate.getActiveAuthCode(
                getTenant(code).getId());
        String tenantBaseUrl = getSystemKeyValueForTenant(SystemConstants.SYSTEM_KEY_TENANT_URL,
                SystemConstants.SYSTEM_KEY_TYPE_TENANT, code);

       /* boolean isFTPEnabled = Boolean
.valueOf(getSystemKeyValueForTenant(SystemConstants.SYSTEM_KEY_FTP,
                SystemConstants.SYSTEM_KEY_TENANT_WRAPPER_TYPE, code));*/
        String undeployUrl = systemParameterProvider.getParameter(BusinessConstants.BATCH_UNDEPLOY_URL);

       /* if (!isFTPEnabled) {*/
            LOGGER.info("Starting Batch Undeployment ...");
            runtimeIntegrationClient.unDeployBatch(tenantBaseUrl, undeployUrl, authKey, code);
       /* }*/

       /* if (isFTPEnabled) {
            String ftpUndeployUrl = systemParameterProvider.getParameter(BusinessConstants.FTP_UNDEPLOY_URL);
            LOGGER.info("Starting FTP Undeployment ...");
            runtimeIntegrationClient.unDeployBatch(tenantBaseUrl, ftpUndeployUrl, authKey, code);
            LOGGER.info("FTP Undeployment Finished Successfully ...");
            LOGGER.info("Starting Batch Undeployment ...");
            runtimeIntegrationClient.unDeployBatch(tenantBaseUrl, undeployUrl, authKey, code);
            LOGGER.info("Batch Undeployment Finished Successfully ...");
        }*/

    }

    @Override
    public String getSystemKeyValue(String key, String type) throws BusinessException, SystemException {
        String sysKeyValue = null;
        String tenantCode = RequestContext.getRequestContext().getTenantCode();
        LOGGER.info("Fetching value for system key {} and tenant {}.", key, tenantCode);
        TenantConfig tenantConfig = tenantBO.getTenantConfigDetails(tenantCode, key, type);
        if (tenantConfig != null) {
            sysKeyValue = tenantConfig.getValue();
        } else {
            SystemException.newSystemException(BusinessExceptionCodes.BSE000109, new Object[] { key, tenantCode });
        }
        return sysKeyValue;
    }

    @Override
    public String getSystemKeyValueForTenant(String key, String type, String code) throws BusinessException, SystemException {
        String sysKeyValue = null;
        String tenantCode = code;
        LOGGER.info("Fetching value for system key {} and tenant {}.", key, tenantCode);
        TenantConfig tenantConfig = tenantBO.getTenantConfigDetails(tenantCode, key, type);
        if (tenantConfig != null) {
            sysKeyValue = tenantConfig.getValue();
        } else {
            SystemException.newSystemException(BusinessExceptionCodes.BSE000109, new Object[] { key, tenantCode });
        }
        return sysKeyValue;
    }

    @Override
    @PreAuthorize("hasRole(T(com.ca.umg.business.constants.BusinessConstants).ROLE_SUPER_ADMIN)")
    public long getTenantCountByNameOrCode(String name, String code) throws SystemException {
        return tenantBO.getTenantCountByNameOrCode(name, code);
    }
}


class TenantConfigComparator implements Comparator<TenantConfigInfo> {
    @Override
    public int compare(TenantConfigInfo tenantConfigInfo1, TenantConfigInfo tenantConfigInfo2) {
        int size = tenantConfigInfo1.getSystemKey().getType().compareTo(tenantConfigInfo2.getSystemKey().getType());
        if (size == BusinessConstants.NUMBER_ZERO) {
            size = tenantConfigInfo1.getSystemKey().getKey().compareTo(tenantConfigInfo2.getSystemKey().getKey());
        }
        return size;
    }
}