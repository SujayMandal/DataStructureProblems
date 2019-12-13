package com.ca.umg.business.migration;

import java.util.List;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.umg.business.migration.info.VersionDetail;
import com.ca.umg.business.migration.info.VersionImportInfo;
import com.ca.umg.business.version.info.VersionInfo;

public interface MigrationAdapter {

    VersionImportInfo extractVersionPackage() throws SystemException, BusinessException;
    
    void setZipArray(byte[] zipArray);
    
    KeyValuePair<VersionInfo, KeyValuePair<List<String>, List<String>>> importVersion(VersionDetail versionDetail) throws SystemException, BusinessException;

}
