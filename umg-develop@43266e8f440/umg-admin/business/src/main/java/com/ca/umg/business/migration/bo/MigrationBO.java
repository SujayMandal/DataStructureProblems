/**
 * 
 */
package com.ca.umg.business.migration.bo;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.migration.audit.entity.MigrationAudit;

/**
 * @author nigampra
 * 
 */
public interface MigrationBO {

    /**
     * Create Migration Log for Import/Export of UMG Version
     * 
     * @param migrationAudit
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    MigrationAudit createMigrationAudit(MigrationAudit migrationAudit) throws BusinessException, SystemException;

    /**
     * Update Export Status to Failed
     * 
     * @param migrationId
     */
    void markExportAsFailed(String migrationId);
    
    public MigrationAudit updateMigrationAudit(MigrationAudit migrationAudit) throws BusinessException, SystemException;
}
