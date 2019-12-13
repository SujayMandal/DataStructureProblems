package com.ca.umg.business.migration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.util.CheckSumUtil;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.umg.business.migration.delegate.VersionMigrationDelegate;
import com.ca.umg.business.migration.info.VersionDetail;
import com.ca.umg.business.migration.info.VersionImportInfo;
import com.ca.umg.business.migration.info.VersionMigrationWrapper;
import com.ca.umg.business.util.ZipUtil;
import com.ca.umg.business.version.info.VersionInfo;

@Named("legacyMigrationAdapter")
@Scope(value=BeanDefinition.SCOPE_PROTOTYPE)
public class MigrationAdapterImpl_V1_1 implements MigrationAdapter {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MigrationAdapterImpl_V1_1.class);
    
    private byte[] zipArray;
    
    private VersionImportInfo versionImportInfo;
    
    @Inject
    private VersionMigrationDelegate versionMigrationDelegate;

    @Override
    public VersionImportInfo extractVersionPackage() throws SystemException, BusinessException {
        VersionMigrationWrapper versionMigrationWrapper = null;
        ZipInputStream zipInputStream = null;
        try {
            ZipEntry entry = null;
            String calculatedCheckSum = null;
            String readCheckSum = null;
            String zipCheckSumAlgo = null;
            zipInputStream = new ZipInputStream(new ByteArrayInputStream(zipArray));
            while ((entry = zipInputStream.getNextEntry()) != null) {// NOPMD
                LOGGER.info("Found file {} with size {}.", entry.getName(), entry.getSize());
                // check for the zip file
                if (entry.getName().toLowerCase(Locale.ENGLISH).endsWith(".zip")) {
                    byte[] zipArray = ZipUtil.prepareZipData(zipInputStream, entry.getName() + "_" + new Random().nextLong());
                    LOGGER.debug("Zip entry {}", entry.getName());
                    ZipInputStream tempZis = new ZipInputStream(new ByteArrayInputStream(zipArray));
                    tempZis.getNextEntry();
                    ObjectInputStream objectInputStream = new ObjectInputStream(tempZis);
                    versionMigrationWrapper = (VersionMigrationWrapper) objectInputStream.readObject();
                    zipCheckSumAlgo = versionMigrationWrapper.getZipChecksumAlgo();
                    calculatedCheckSum = CheckSumUtil.getCheckSumValue(zipArray, zipCheckSumAlgo);
                } else if (entry.getName().toLowerCase().contains("checksum")) {
                    List<String> checksumList = IOUtils.readLines(zipInputStream);
                    if (!checksumList.isEmpty()) {
                        readCheckSum = checksumList.get(0);
                    }
                }
            }
            versionImportInfo = new VersionImportInfo();
            versionImportInfo.setChecksum(calculatedCheckSum);
            versionImportInfo.setReadChecksum(readCheckSum);
            versionImportInfo.setVersionMigrationWrapper(versionMigrationWrapper);
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.error("An error occurred while extracting the zip file:: ERROR :: ", e.getMessage());
        } finally {
            if (zipInputStream != null) {
                try {
                    zipInputStream.closeEntry();
                    zipInputStream.close();
                } catch (IOException e) {
                    LOGGER.error("An error occurred whileclosing the zip input stream. {}", e.getMessage());
                }
            }
        }
        return versionImportInfo;
    }

    @Override
    public KeyValuePair<VersionInfo, KeyValuePair<List<String>, List<String>>> importVersion(VersionDetail versionDetail) throws SystemException, BusinessException {
        return versionMigrationDelegate.importVersion(versionDetail, versionImportInfo);        
    }

    @Override
    public void setZipArray(byte[] zipArray) {
        this.zipArray = zipArray;        
    }
}
