package com.ca.umg.business.migration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.prepost.PreAuthorize;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.util.ZipUtil;

@Named
public class MigrationAdapterFactory {
    
private static final Logger LOGGER = LoggerFactory.getLogger(MigrationAdapterFactory.class);
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @PreAuthorize("hasRole(@accessPrivilege.getModelAdd())")
    public MigrationAdapter getMigrationAdapter(InputStream importedZipStream, String name) throws SystemException, IOException {
        byte[] zipByteArray = generateZipArray(importedZipStream, name);
        MigrationAdapter rtMigrationAdapter = null;
        if (getVersion(zipByteArray).equals("1.1")) {
            rtMigrationAdapter = applicationContext.getBean(MigrationAdapterImpl_V1_1.class);
        } else {
            rtMigrationAdapter = applicationContext.getBean(MigrationAdapterImpl.class);
        }
        rtMigrationAdapter.setZipArray(zipByteArray);
        return rtMigrationAdapter;
    }
    
    private byte[] generateZipArray(InputStream importedZipStream, String zipName) throws SystemException, IOException {
        return ZipUtil.prepareZipData(importedZipStream, zipName + "_" + new Random().nextLong());
    }
    
    private String getVersion(byte[] zipByteArray) throws SystemException, IOException {
        ZipInputStream newZipIs = new ZipInputStream(new ByteArrayInputStream(zipByteArray));
        ZipEntry entry = null;
        String version = null;
        try {
            while ((entry = newZipIs.getNextEntry()) != null) {// NOPMD
                LOGGER.info("Found file {} with size {}.", entry.getName(), entry.getSize());
                if (entry.getName().toLowerCase().contains("versioninfo") || entry.getName().toLowerCase().contains(".key")) {
                    version = "1.2";
                    break;
                }
            }
        } catch (IOException e) {
            SystemException.newSystemException("", new Object[1]);
        }
        if (version == null) {
            version = "1.1";
        }
        return version;
    }
}
