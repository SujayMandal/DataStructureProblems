/**
 * 
 */
package com.ca.umg.business.tenant.bo;

import static com.ca.umg.notification.NotificationConstants.FROM_ADDRESS;
import static com.ca.umg.notification.NotificationConstants.NEW_TENANT_ADDED_EVENT_TEMPLATE;
import static com.ca.umg.notification.NotificationConstants.NOTIFICATION_EVENT_TABLENAME;
import static com.ca.umg.notification.NotificationConstants.NOTIFICATION_MAIL_TYPE;
import static com.ca.umg.notification.NotificationConstants.NOTIFICATION_TEMPLATE_TABLENAME;
import static com.ca.umg.notification.NotificationConstants.NOTIFICATION_TYPE_TABLENAME;
import static com.ca.umg.notification.NotificationConstants.RUNTIME_FAILURE_MAIL_TEMPLATE;
import static com.ca.umg.notification.model.NotificationEventNameEnum.NEW_TENANT_ADDED;
import static com.ca.umg.notification.model.NotificationEventNameEnum.RUNTIME_TRANSACTION_FAILURE;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.info.tenant.FileUtil;
import com.ca.framework.core.info.tenant.TenantConfigInfo;
import com.ca.framework.core.info.tenant.TenantInfo;
import com.ca.framework.core.info.tenant.TenantUtil;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.framework.core.util.UmgFileProxy;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.mappingnotification.dao.MappingNotificationDao;
import com.ca.umg.business.mappingnotification.dao.NotificationDataDao;
import com.ca.umg.business.mappingnotification.entity.MappingNotificationEntity;
import com.ca.umg.business.tenant.dao.SystemKeyDAO;
import com.ca.umg.business.tenant.dao.TenantConfigDAO;
import com.ca.umg.business.tenant.dao.TenantDAO;
import com.ca.umg.business.tenant.entity.Address;
import com.ca.umg.business.tenant.entity.SystemKey;
import com.ca.umg.business.tenant.entity.Tenant;
import com.ca.umg.business.tenant.entity.TenantConfig;
import com.google.common.collect.Lists;

/**
 * @author kamathan
 * 
 */
@Named
@SuppressWarnings({"PMD.TooManyMethods"})
public class TenantBOImpl implements TenantBO {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantBOImpl.class.getName());

    @Inject
    private TenantDAO tenantDAO;

    @Inject
    private SystemKeyDAO systemKeyDAO;

    @Inject
    private TenantConfigDAO tenantConfigDAO;

    @Inject
    private UmgFileProxy umgFileProxy;

    @Inject
    private SystemParameterProvider systemParameterProvider;

    @Inject
    private CacheRegistry cacheRegistry;

    @Inject
    private MappingNotificationDao mappingNotificationDao;

    @Inject
    private NotificationDataDao notificationDao;

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.tenant.bo.TenantBO#listAllTenants()
     */
    @Override
    public List<Tenant> listAll() throws BusinessException, SystemException {
        return tenantDAO.findAll();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.tenant.bo.TenantBO#saveTenant(com.ca.umg.business .tenant.entity.Tenant)
     */
    @Override
    @Transactional
    public Tenant save(Tenant tenant) throws SystemException, BusinessException {
        Set<TenantConfig> tenantConfigs = tenant.getTenantConfigs();
        Set<Address> addresses = tenant.getAddresses();
        SystemKey systemKey = null;
        if (CollectionUtils.isNotEmpty(tenantConfigs)) {
            LOGGER.info("Found {} configuration for tenant {}.", tenantConfigs.size(), tenant.getId());
            for (TenantConfig tenantConfig : tenantConfigs) {
                tenantConfig.setTenant(tenant);
                systemKey = systemKeyDAO.findByKeyAndType(tenantConfig.getSystemKey().getKey(), tenantConfig.getSystemKey()
                        .getType());
                if (systemKey == null) {
                    throw SystemException.newSystemException(BusinessExceptionCodes.BSE000002, new Object[] { tenantConfig
                            .getSystemKey().getKey() });
                }
                tenantConfig.setSystemKey(systemKey);
            }
            if (CollectionUtils.isNotEmpty(addresses)) {
                for (Address address : addresses) {
                    address.setTenant(tenant);
                }
            }
        }
        
        Tenant te = tenantDAO.save(tenant);
        if (te != null) {
            createNotificationMappings(te);
        }
        
        return te;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.tenant.bo.TenantBO#deleteTenant(com.ca.umg.business .tenant.entity.Tenant)
     */
    @Override
    public void delete(Tenant tenant) throws SystemException {
        Set<TenantConfig> tenantConfigs = tenant.getTenantConfigs();
        Set<Address> addresses = tenant.getAddresses();
        SystemKey systemKey = null;
        if (CollectionUtils.isNotEmpty(tenantConfigs)) {
            LOGGER.info("Found {} configuration for tenant {}.", tenantConfigs.size(), tenant.getId());
            for (TenantConfig tenantConfig : tenantConfigs) {
                tenantConfig.setTenant(tenant);
                systemKey = systemKeyDAO.findByKeyAndType(tenantConfig.getSystemKey().getKey(), tenantConfig.getSystemKey()
                        .getType());
                if (systemKey == null) {
                    throw SystemException.newSystemException(BusinessExceptionCodes.BSE000002, new Object[] { tenantConfig
                            .getSystemKey().getKey() });
                }
                tenantConfig.setSystemKey(systemKey);
            }
            if (CollectionUtils.isNotEmpty(addresses)) {
                for (Address address : addresses) {
                    address.setTenant(tenant);
                }
            }
        }
        tenantDAO.delete(tenant);
        deleteNotificationMappings(tenant);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.tenant.bo.TenantBO#getTenantConfigDetails(java.lang .String, java.lang.String, java.lang.String)
     */
    @Override
    public TenantConfig getTenantConfigDetails(String code, String key, String type) throws BusinessException, SystemException {
        return tenantConfigDAO.findByTenantCodeAndSystemKeyKeyAndSystemKeyType(code, key, type);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.tenant.bo.TenantBO#getTenant(java.lang.String)
     */
    @Override
    public Tenant getTenant(String code) throws SystemException {
        return tenantDAO.findByCode(code);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.tenant.bo.TenantBO#update(com.ca.umg.business.tenant .entity.Tenant,
     * com.ca.umg.business.tenant.entity.Tenant)
     */
    @Override
    public Tenant update(Tenant newTenant, Tenant existingTenant) throws SystemException, BusinessException {
        updateTenant(newTenant, existingTenant);
        updateTenantConfig(newTenant, existingTenant);
        LOGGER.info("Updating the tenant :" + existingTenant.getCode());
        Tenant updatedTenant = tenantDAO.save(existingTenant);
        LOGGER.info("Tenant : " + existingTenant.getCode() + " updated successfully");
        tenantFoldersCreation(newTenant);
        return updatedTenant;
    }

    /**
     * this method used to overridden the existing tenant with the updated tenant details
     *
     * @param newTenant
     * @param existingTenant
     * @throws SystemException
     */
    private void updateTenant(Tenant newTenant, Tenant existingTenant) throws SystemException {
        existingTenant.setDescription(newTenant.getDescription());
        existingTenant.setName(newTenant.getName());
        if (newTenant.getAddresses() != null && newTenant.getAddresses().size() > BusinessConstants.NUMBER_ZERO) {
            List<Address> addressList = new ArrayList<Address>(newTenant.getAddresses());
            Address address = addressList.get(0);
            if (existingTenant.getAddresses() != null && existingTenant.getAddresses().size() > BusinessConstants.NUMBER_ZERO) {
                List<Address> existingAddresses = new ArrayList<Address>(existingTenant.getAddresses());
                Address existingAddres = existingAddresses.get(0);
                updateAddress(address, existingAddres);
            } else {
                for (Address newAddress : newTenant.getAddresses()) {
                    newAddress.setTenant(existingTenant);
                }
                existingTenant.setAddresses(newTenant.getAddresses());

            }
        }
        updateTenantType(newTenant, existingTenant);
    }

    /**
     * This method used to overrriden existing tenant address with the updated tenant address
     *
     * @param address
     * @param existingAddres
     */
    private void updateAddress(Address address, Address existingAddres) {
        existingAddres.setAddress1(address.getAddress1());
        existingAddres.setAddress2(address.getAddress2());
        existingAddres.setCity(address.getCity());
        existingAddres.setState(address.getState());
        existingAddres.setZip(address.getZip());
        existingAddres.setCountry(address.getCountry());

    }

    /**
     * This method used to overridden the existing tenant with the updated tenant type
     *
     * @param newTenant
     * @param existingTenant
     * @throws SystemException
     */
    private void updateTenantType(Tenant newTenant, Tenant existingTenant) throws SystemException {
        if (StringUtils.equals(BusinessConstants.TENANTTYPE_BOTH, existingTenant.getTenantType())
                && !StringUtils.equals(BusinessConstants.TENANTTYPE_BOTH, newTenant.getTenantType())) {
            throw SystemException
                    .newSystemException(BusinessExceptionCodes.BSE000102, new Object[] { newTenant.getTenantType() });
        } else {
            existingTenant.setTenantType(newTenant.getTenantType());

        }
    }

    /**
     * This method used to validate the system keys of tenant configs
     *
     * @param tenantConfigs
     * @param tenantConnfigInfo
     * @throws SystemException
     */
    private void validateSystemKeys(Set<TenantConfig> tenantConfigs, TenantConfigInfo tenantConnfigInfo, String sysKey)
            throws SystemException {
        if (CollectionUtils.isNotEmpty(tenantConfigs)) {
            for (TenantConfig tenantConfig : tenantConfigs) {
                SystemKey systemKey = systemKeyDAO.findByKeyAndType(tenantConfig.getSystemKey().getKey(), tenantConfig
                        .getSystemKey().getType());
                if (systemKey == null) {
                    throw SystemException.newSystemException(BusinessExceptionCodes.BSE000002, new Object[] { tenantConfig
                            .getSystemKey().getKey() });
                }
                if (StringUtils.equals(sysKey, systemKey.getKey()) && Boolean.valueOf(tenantConfig.getValue())) {
                    tenantConnfigInfo.setValue("true");
                }
                tenantConfig.setSystemKey(systemKey);
            }

        }
    }

    /**
     * This method used to update the existing tenant configs with the updated tenant config values
     *
     * @param newTenant
     * @param existingTenant
     */
    private void updateTenantConfig(Tenant newTenant, Tenant existingTenant) {
        MultiKeyMap existingConfigMap = new MultiKeyMap();
        Set<TenantConfig> existingConfigs = existingTenant.getTenantConfigs();

        for (TenantConfig tenantConfig : existingConfigs) {
            SystemKey systemKey = tenantConfig.getSystemKey();
            existingConfigMap.put(systemKey.getKey(), systemKey.getType(), tenantConfig);
        }
        if (newTenant.getTenantConfigs().size() > BusinessConstants.NUMBER_ZERO) {
            for (TenantConfig tenantConfig : newTenant.getTenantConfigs()) {
                SystemKey systemKey = tenantConfig.getSystemKey();
                TenantConfig existingConfig = (TenantConfig) existingConfigMap.get(systemKey.getKey(), systemKey.getType());
                if (existingConfig != null) {
                    existingConfig.setTenant(existingTenant);
                    existingConfig.setValue(tenantConfig.getValue());
                } else {
                    tenantConfig.setTenant(existingTenant);
                    existingConfigs.add(tenantConfig);
                }

            }
        }
    }

    /**
     * This method used to create the batch folder if it is not created when batch is enabled for the tenant
     *
     * @throws BusinessException
     * @throws SystemException
     */
    private void batchFoldersCreation(Tenant tenant) throws BusinessException, SystemException {
        String tenantSanBase = new StringBuffer(umgFileProxy.getSanPath(systemParameterProvider
                .getParameter(SystemConstants.SAN_BASE))).append(File.separatorChar).append(tenant.getCode()).toString();
        StringBuilder batchFileFolder = new StringBuilder(tenantSanBase).append(File.separator)
                .append(BusinessConstants.BATCH_FILE);
        StringBuilder inputFolderPath = new StringBuilder(batchFileFolder).append(File.separator)
                .append(BusinessConstants.INPUT_FOLDER);
        StringBuilder outputFolderPath = new StringBuilder(batchFileFolder).append(File.separator).append(
                BusinessConstants.OUTPUT_FOLDER);
        StringBuilder inProgressFolderPath = new StringBuilder(batchFileFolder).append(File.separator).append(
                BusinessConstants.INPROGRESS_FOLDER);
        StringBuilder archieveFolderPath = new StringBuilder(batchFileFolder).append(File.separator).append(
                BusinessConstants.ARCHIEVE_FOLDER);
        StringBuilder testFolderPath = new StringBuilder(batchFileFolder).append(File.separator).append(
                BusinessConstants.BATCH_TEST);
        StringBuilder testArchieveFolderPath = new StringBuilder(batchFileFolder).append(File.separator)
                .append(BusinessConstants.BATCH_TEST).append(File.separator).append(BusinessConstants.ARCHIEVE_FOLDER);
        StringBuilder testOutputFolderPath = new StringBuilder(batchFileFolder).append(File.separator)
                .append(BusinessConstants.BATCH_TEST).append(File.separator).append(BusinessConstants.OUTPUT_FOLDER);

        if (!Files.exists(Paths.get(batchFileFolder.toString()), LinkOption.NOFOLLOW_LINKS)) {
            createFolder(batchFileFolder);
            createFolder(inputFolderPath);
            createFolder(outputFolderPath);
            createFolder(inProgressFolderPath);
            createFolder(archieveFolderPath);
            createFolder(testFolderPath);
            createFolder(testArchieveFolderPath);
            createFolder(testOutputFolderPath);
        }
    }

    /**
     * This method used to create the bulk folder if it is not created when bulk is enabled for the tenant
     *
     * @throws BusinessException
     * @throws SystemException
     */
    private void bulkFoldersCreation(Tenant tenant) throws BusinessException, SystemException {
        String tenantSanBase = new StringBuilder(umgFileProxy.getSanPath(systemParameterProvider
                .getParameter(SystemConstants.SAN_BASE))).append(File.separatorChar).append(tenant.getCode()).toString();
        StringBuilder bulkFileFolder = new StringBuilder(tenantSanBase).append(File.separator).append(BusinessConstants.BULK_FILE);
        StringBuilder inputBulkFileFolderPath = new StringBuilder(bulkFileFolder).append(File.separator).append(
                BusinessConstants.INPUT_FOLDER);
        StringBuilder outputBulkFileFolderPath = new StringBuilder(bulkFileFolder).append(File.separator).append(
                BusinessConstants.OUTPUT_FOLDER);
        StringBuilder inProgressBulkFileFolderPath = new StringBuilder(bulkFileFolder).append(File.separator).append(
                BusinessConstants.INPROGRESS_FOLDER);
        StringBuilder archieveBulkFileFolderPath = new StringBuilder(bulkFileFolder).append(File.separator).append(
                BusinessConstants.ARCHIEVE_FOLDER);
        if (!Files.exists(Paths.get(bulkFileFolder.toString()), LinkOption.NOFOLLOW_LINKS)) { // Backward compatibility check
            createFolder(bulkFileFolder);
            createFolder(inputBulkFileFolderPath);
            createFolder(outputBulkFileFolderPath);
            createFolder(inProgressBulkFileFolderPath);
            createFolder(archieveBulkFileFolderPath);
        }

        StringBuilder bulkHTTPFolder = new StringBuilder(tenantSanBase).append(File.separator).append(BusinessConstants.BULK_HTTP);
        StringBuilder inputBulkHTTPFolderPath = new StringBuilder(bulkHTTPFolder).append(File.separator).append(
                BusinessConstants.INPUT_FOLDER);
        StringBuilder outputBulkHTTPFolderPath = new StringBuilder(bulkHTTPFolder).append(File.separator).append(
                BusinessConstants.OUTPUT_FOLDER);
        StringBuilder inprogressBulkHTTPFolderPath = new StringBuilder(bulkHTTPFolder).append(File.separator).append(
                BusinessConstants.INPROGRESS_FOLDER);
        StringBuilder archiveBulkHTTPFolderPath = new StringBuilder(bulkHTTPFolder).append(File.separator).append(
                BusinessConstants.ARCHIEVE_FOLDER);
        if (!Files.exists(Paths.get(bulkHTTPFolder.toString()), LinkOption.NOFOLLOW_LINKS)) {
            createFolder(bulkHTTPFolder);
            createFolder(inputBulkHTTPFolderPath);
            createFolder(outputBulkHTTPFolderPath);
            createFolder(inprogressBulkHTTPFolderPath);
            createFolder(archiveBulkHTTPFolderPath);
        }
    }

    /**
     * This method used to create the tenant folder if it is not created
     *
     * @throws BusinessException
     * @throws SystemException
     */
    private void tenantFolderCreation(Tenant tenant) throws BusinessException, SystemException {
        String sanBase = new StringBuffer(umgFileProxy.getSanPath(systemParameterProvider.getParameter(SystemConstants.SAN_BASE)))
                .toString();
        StringBuilder tenantFolder = new StringBuilder(sanBase).append(File.separator).append(tenant.getCode());
        if (!Files.exists(Paths.get(tenantFolder.toString()), LinkOption.NOFOLLOW_LINKS)) {
            createFolder(tenantFolder);
        }
    }

    /**
     * This method used to create the folders for new tenant
     * 
     * @throws BusinessException
     * @throws SystemException
     */
    @Override
    public void tenantFoldersCreation(Tenant tenant) throws BusinessException, SystemException {
        tenantFolderCreation(tenant);
        Set<TenantConfig> tenantConfigs = tenant.getTenantConfigs();
        TenantConfigInfo batchTenantConfig = new TenantConfigInfo();
        batchTenantConfig.setValue("false");
        validateSystemKeys(tenantConfigs, batchTenantConfig, BusinessConstants.BATCH_ENABLED);
        if (Boolean.valueOf(batchTenantConfig.getValue())) {
            batchFoldersCreation(tenant);
        }
        TenantConfigInfo bulkTenantConfig = new TenantConfigInfo();
        bulkTenantConfig.setValue("false");
        validateSystemKeys(tenantConfigs, bulkTenantConfig, BusinessConstants.BULK_ENABLED);
        if (Boolean.valueOf(bulkTenantConfig.getValue())) {
            bulkFoldersCreation(tenant);
        }
        TenantUtil.initiateBulk(tenant.getCode(), cacheRegistry, Boolean.valueOf(bulkTenantConfig.getValue()));
    }

    /**
     * This method used to delete the folder for tenant in case of roll-back during on-boarding
     *
     * @throws BusinessException
     * @throws SystemException
     */
    @Override
    public void newTenantFoldersDeletion(Tenant tenant) throws SystemException {
        String sanBase = new StringBuffer(umgFileProxy.getSanPath(systemParameterProvider.getParameter(SystemConstants.SAN_BASE)))
                .toString();
        StringBuffer tenantFolder = new StringBuffer(sanBase).append(File.separator).append(tenant.getCode());
        if (Files.exists(Paths.get(tenantFolder.toString()), LinkOption.NOFOLLOW_LINKS)) {
            boolean isFolderDeleted = FileUtil.delete(tenantFolder.toString(), true);
            if (!isFolderDeleted) {
                SystemException.newSystemException(BusinessExceptionCodes.BSE000108, new Object[] { tenantFolder });
            } else {
                LOGGER.info(tenant.getCode() + " folder deleted successfully");
            }
        } else {
            LOGGER.info(tenant.getCode() + " folder not created before rollback");
        }
    }

    private void createFolder(StringBuilder folderPath) throws SystemException {
        boolean isFolderCreated = new File(folderPath.toString()).mkdir();
        if (!isFolderCreated) {
            SystemException.newSystemException(BusinessExceptionCodes.BSE000108, new Object[] { folderPath });
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.tenant.bo.TenantBO#getSystemKeys()
     */
    @Override
    public List<SystemKey> getSystemKeys() throws SystemException {
        return systemKeyDAO.findAll();

    }

    @Override
    public List<String> getListOfTenantCodes() {
        Map<String, TenantInfo> tenantInfo = cacheRegistry.getMap(FrameworkConstant.TENANT_MAP);
        return Lists.newArrayList(tenantInfo.keySet());
    }

    @Override
    public long getTenantCountByNameOrCode(String name, String code) throws SystemException {
        return tenantDAO.countByNameIgnoreCaseOrCodeIgnoreCase(name, code);
    }

    private void createNotificationMappings(final Tenant te) throws SystemException, BusinessException {
        final String toAddress = notificationDao.getSuperAdminToAddresses();
        createTenantAddedMapping(te, toAddress);
        createRuntimeFailureMapping(te, toAddress);
    }
    
    private void createTenantAddedMapping(final Tenant te, final String toAddress) throws BusinessException, SystemException {
        final MappingNotificationEntity entity = new MappingNotificationEntity();
        entity.setFromAddress(systemParameterProvider.getParameter(FROM_ADDRESS));
        entity.setTenantId(te.getCode());
        String templateID = notificationDao.findId(NOTIFICATION_TEMPLATE_TABLENAME, NEW_TENANT_ADDED_EVENT_TEMPLATE);
        String eventID = notificationDao.findId(NOTIFICATION_EVENT_TABLENAME, NEW_TENANT_ADDED.getName());
        String typeID = notificationDao.findTypeId(NOTIFICATION_TYPE_TABLENAME, NOTIFICATION_MAIL_TYPE);
        entity.setNotificationTemplateId(templateID);
        entity.setNotifiacytionTypeId(typeID);
        entity.setNotificationEventId(eventID);
        entity.setName(NEW_TENANT_ADDED.getName());
        entity.setToAddress(toAddress);
        mappingNotificationDao.save(entity);
    }

    private void createRuntimeFailureMapping(final Tenant te, final String toAddress) throws BusinessException, SystemException {
        final MappingNotificationEntity entity = new MappingNotificationEntity();
        entity.setFromAddress(systemParameterProvider.getParameter(FROM_ADDRESS));
        entity.setTenantId(te.getCode());
        String templateId = notificationDao.findId(NOTIFICATION_TEMPLATE_TABLENAME, RUNTIME_FAILURE_MAIL_TEMPLATE);
        String eventId = notificationDao.findId(NOTIFICATION_EVENT_TABLENAME, RUNTIME_TRANSACTION_FAILURE.getName());
        String typeID = notificationDao.findTypeId(NOTIFICATION_TYPE_TABLENAME, NOTIFICATION_MAIL_TYPE);
        entity.setNotificationTemplateId(templateId);
        entity.setNotifiacytionTypeId(typeID);
        entity.setNotificationEventId(eventId);
        entity.setName(RUNTIME_TRANSACTION_FAILURE.getName());
        entity.setToAddress(toAddress);
        mappingNotificationDao.save(entity);
    }
    
    private void deleteNotificationMappings(final Tenant te) throws SystemException {
        deleteTenantAddedMapping(te);
        deleteRuntimeFailureMapping(te);
    }
    
    private void deleteTenantAddedMapping(final Tenant te) throws SystemException {
        final MappingNotificationEntity entity = new MappingNotificationEntity();
        entity.setTenantId(te.getCode());
        String templateID = notificationDao.findId(NOTIFICATION_TEMPLATE_TABLENAME, NEW_TENANT_ADDED_EVENT_TEMPLATE);
        String eventID = notificationDao.findId(NOTIFICATION_EVENT_TABLENAME, NEW_TENANT_ADDED.getName());
        String typeID = notificationDao.findTypeId(NOTIFICATION_TYPE_TABLENAME, NOTIFICATION_MAIL_TYPE);
        entity.setNotificationTemplateId(templateID);
        entity.setNotifiacytionTypeId(typeID);
        entity.setNotificationEventId(eventID);
        entity.setName(NEW_TENANT_ADDED.getName());
        mappingNotificationDao.delete(entity);
    }

    private void deleteRuntimeFailureMapping(final Tenant te) throws SystemException {
        final MappingNotificationEntity entity = new MappingNotificationEntity();
        entity.setTenantId(te.getCode());
        String templateId = notificationDao.findId(NOTIFICATION_TEMPLATE_TABLENAME, RUNTIME_FAILURE_MAIL_TEMPLATE);
        String eventId = notificationDao.findId(NOTIFICATION_EVENT_TABLENAME, RUNTIME_TRANSACTION_FAILURE.getName());
        String typeID = notificationDao.findTypeId(NOTIFICATION_TYPE_TABLENAME, NOTIFICATION_MAIL_TYPE);
        entity.setNotificationTemplateId(templateId);
        entity.setNotifiacytionTypeId(typeID);
        entity.setNotificationEventId(eventId);
        entity.setName(RUNTIME_TRANSACTION_FAILURE.getName());
        mappingNotificationDao.delete(entity);
    }
}