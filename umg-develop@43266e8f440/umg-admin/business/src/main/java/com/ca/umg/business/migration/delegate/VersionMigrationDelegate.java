package com.ca.umg.business.migration.delegate;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.umg.business.migration.audit.info.MigrationAuditInfo;
import com.ca.umg.business.migration.info.VersionDetail;
import com.ca.umg.business.migration.info.VersionImportInfo;
import com.ca.umg.business.migration.info.VersionMigrationWrapper;
import com.ca.umg.business.version.info.VersionInfo;

public interface VersionMigrationDelegate {

    VersionMigrationWrapper exportVersion(String versionId, MigrationAuditInfo migrationAuditInfo) throws BusinessException,
            SystemException;

    /**
     * 
     * @param versionMigrationWrapper
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    KeyValuePair<VersionInfo, KeyValuePair<List<String>, List<String>>> importVersion(VersionDetail versionDetail,
            VersionImportInfo versionImportInfo) throws BusinessException, SystemException;

    MigrationAuditInfo logVersionExport(String versionId) throws BusinessException, SystemException;

    MigrationAuditInfo logVersionImport(String versionId, MigrationAuditInfo mai) throws BusinessException, SystemException;

    void markExportAsFailed(String migrationId);

    void markImportAsFailed() throws BusinessException, SystemException;

    public String getImportFilePath(final String importFileName) throws SystemException;
    
    public String getImportFileName(final MultipartFile importFile);

    public void storeImportFileIntoSan(final String importFilePath, final MultipartFile importFile) throws BusinessException, SystemException;
    
    public void deleteImportFilefromSan(final String filename);
    
    public MigrationAuditInfo logVersionImport(MigrationAuditInfo mai, final String improtFileName) throws BusinessException, SystemException;
    
    public void markImportAsFailed(final String migrationId) throws BusinessException, SystemException;
}
