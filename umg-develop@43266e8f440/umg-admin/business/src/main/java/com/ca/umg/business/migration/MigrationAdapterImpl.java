package com.ca.umg.business.migration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.util.StringUtils;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.rmodel.info.VersionExecInfo;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.framework.event.StaticDataRefreshEvent;
import com.ca.framework.event.util.EventOperations;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.encryption.EncryptionProvider;
import com.ca.umg.business.encryption.info.EncryptionDataInfo;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.migration.delegate.VersionMigrationDelegate;
import com.ca.umg.business.migration.info.VersionDetail;
import com.ca.umg.business.migration.info.VersionImportInfo;
import com.ca.umg.business.migration.info.VersionMigrationWrapper;
import com.ca.umg.business.migration.info.ZipContentsInfo;
import com.ca.umg.business.util.EncryptionUtil;
import com.ca.umg.business.util.ZipUtil;
import com.ca.umg.business.version.data.VersionDataContainer;
import com.ca.umg.business.version.delegate.VersionDelegate;
import com.ca.umg.business.version.event.VersionRefreshEvent;
import com.ca.umg.business.version.info.VersionInfo;
import com.ca.umg.business.version.listener.VersionRefreshEventListener;

@Named("newMigrationAdapter")
@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
public class MigrationAdapterImpl implements MigrationAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(MigrationAdapterImpl.class);

    private byte[] zipArray;

    private VersionImportInfo versionImportInfo;

    @Inject
    private VersionMigrationDelegate versionMigrationDelegate;

    @Inject
    protected EncryptionProvider encryptionProvider;

    @Inject
    private CacheRegistry cacheRegistry;

    @Inject
    private VersionDataContainer versionDataContainer;

    @Inject
    private VersionDelegate versionDelegate;

    @Override
    public VersionImportInfo extractVersionPackage() throws SystemException, BusinessException {
        try {
            ZipContentsInfo zipContentsInfo = getZipContents();
            if (zipContentsInfo.getInternalZipArray() == null || StringUtils.isEmpty(zipContentsInfo.getEncryptedKey())) {
                BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000088,
                        new String[] { "Corrupted Zip Imported" });
            }
            EncryptionDataInfo encryptionDataInfo = new EncryptionDataInfo();
            decryptImportedData(encryptionDataInfo, zipContentsInfo);
            VersionMigrationWrapper versionMigrationWrapper = getVersionMigrationWrapper(encryptionDataInfo);
            buildVersionImportInfo(versionMigrationWrapper);
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.error("An error occurred while extracting the zip file:: ERROR :: ", e.getMessage());
        }
        return versionImportInfo;
    }

    private void buildVersionImportInfo(VersionMigrationWrapper versionMigrationWrapper) {
        versionImportInfo = new VersionImportInfo();
        versionImportInfo.setChecksum("Dummy");
        versionImportInfo.setReadChecksum("Dummy");
        versionImportInfo.setVersionMigrationWrapper(versionMigrationWrapper);
    }

    private ZipContentsInfo getZipContents() throws SystemException, IOException {
        ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(zipArray));
        ZipContentsInfo zipContentsInfo = new ZipContentsInfo();
        try {
            ZipEntry entry = null;
            while ((entry = zipInputStream.getNextEntry()) != null) {// NOPMD
                LOGGER.info("Found file {} with size {}.", entry.getName(), entry.getSize());
                // check for the zip file
                if (entry.getName().toLowerCase(Locale.ENGLISH).endsWith(".zip")) {
                    zipContentsInfo.setInternalZipArray(
                            ZipUtil.prepareZipData(zipInputStream, entry.getName() + "_" + new Random().nextLong()));
                } else if (entry.getName().toLowerCase(Locale.ENGLISH).endsWith(".key")) {
                    zipContentsInfo.setEncryptedKey(
                            ZipUtil.prepareZipData(zipInputStream, entry.getName() + "_" + new Random().nextLong()));
                }
            }
        } finally {
            IOUtils.closeQuietly(zipInputStream);
        }
        return zipContentsInfo;
    }

    private VersionMigrationWrapper getVersionMigrationWrapper(EncryptionDataInfo encryptionDataInfo)
            throws IOException, ClassNotFoundException, SystemException {
        ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(encryptionDataInfo.getDecryptedData()));
        return ZipUtil.readWrapperZipFile(zis);
    }

    private void decryptImportedData(EncryptionDataInfo encryptionDataInfo, ZipContentsInfo zipContentsInfo)
            throws BusinessException, SystemException {
        encryptionDataInfo.setEncryptedData(zipContentsInfo.getInternalZipArray());
        encryptionDataInfo.setEncryptedKey(EncryptionUtil.applyKeyDecryption(zipContentsInfo.getEncryptedKey()));
        encryptionProvider.decrypt(encryptionDataInfo);
    }

    @Override
    public KeyValuePair<VersionInfo, KeyValuePair<List<String>, List<String>>> importVersion(VersionDetail versionDetail)
            throws SystemException, BusinessException {
        KeyValuePair<VersionInfo, KeyValuePair<List<String>, List<String>>> versionMigrateionDetail = versionMigrationDelegate
                .importVersion(versionDetail, versionImportInfo);

        // update cache
        if (versionMigrateionDetail != null && versionMigrateionDetail.getValue() != null
                && CollectionUtils.isEmpty(versionMigrateionDetail.getValue().getValue())) {
            updateCache(versionMigrateionDetail.getKey());
        }

        return versionMigrateionDetail;
    }

    @SuppressWarnings("unchecked")
    private void updateCache(VersionInfo versionInfo) throws SystemException {

        updateVersionInfo(versionInfo);

        String versionKey = org.apache.commons.lang3.StringUtils.join(versionInfo.getName(), BusinessConstants.CHAR_HYPHEN,
                versionInfo.getMajorVersion(), BusinessConstants.CHAR_HYPHEN, versionInfo.getMinorVersion());

        // update version execution environment map
        updateVersionExecEnvmap(versionInfo, versionKey);

    }

    private void updateVersionExecEnvmap(VersionInfo versionInfo, String versionKey) throws SystemException {
    	VersionExecInfo versionExecInfo = null;

    	versionExecInfo = versionDelegate.getVersionExecutionEnvInfo(versionInfo.getName(), versionInfo.getMajorVersion(),
                versionInfo.getMinorVersion());
    	
        LOGGER.debug("request to update the map : {} for key {}", StaticDataRefreshEvent.REFRESH_VERSION_EXC_ENV_MAP, versionKey);
        StaticDataRefreshEvent<VersionExecInfo> versionExcEnvRefreshEvent = buildEvent(
                StaticDataRefreshEvent.REFRESH_VERSION_EXC_ENV_MAP, EventOperations.ADD.getOperation(), versionKey,
                versionExecInfo);

        LOGGER.debug("Event to be raised is : " + versionExcEnvRefreshEvent);

        cacheRegistry.getTopic(StaticDataRefreshEvent.REFRESH_VERSION_EXC_ENV_MAP).publish(versionExcEnvRefreshEvent);
    }

    private <T> StaticDataRefreshEvent<T> buildEvent(String eventName, String operation, String versionKey, T t) {
        StaticDataRefreshEvent<T> event = new StaticDataRefreshEvent<T>();
        event.setEvent(eventName);
        event.setOperation(operation);
        event.setData(t);
        event.setTenantCode(RequestContext.getRequestContext().getTenantCode());
        event.setVersionKey(versionKey);
        return event;
    }

    private void updateVersionInfo(VersionInfo versionInfo) throws SystemException {

        VersionRefreshEvent<VersionInfo> event = new VersionRefreshEvent<VersionInfo>();
        event.setEvent(VersionRefreshEvent.REFRESH_VERSION);
        event.setOperation(EventOperations.ADD.getOperation());
        event.setData(versionInfo);
        event.setTenantCode(RequestContext.getRequestContext().getTenantCode());

        versionDataContainer.addVersionToContainer(RequestContext.getRequestContext().getTenantCode(), versionInfo.getName(),
                versionInfo.getDescription());
        cacheRegistry.getTopic(VersionRefreshEventListener.VERSION_REFRESH).publish(event);
        versionDataContainer.addModelNameToContainer(versionInfo.getName(), versionInfo.getMajorVersion(),
                versionInfo.getMinorVersion());
    }

    @Override
    public void setZipArray(byte[] zipArray) {
        this.zipArray = zipArray;
    }
}
