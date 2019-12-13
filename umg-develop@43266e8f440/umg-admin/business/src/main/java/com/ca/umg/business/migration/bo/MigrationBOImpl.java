/**
 * 
 */
package com.ca.umg.business.migration.bo;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;

import com.ca.framework.core.bo.AbstractBusinessObject;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.migration.audit.entity.MigrationAudit;
import com.ca.umg.business.migration.dao.MigrationAuditDAO;

/**
 * @author nigampra
 * 
 */
@Named
public class MigrationBOImpl extends AbstractBusinessObject implements MigrationBO {

    private static final long serialVersionUID = 622664502666804314L;

    private static final Logger LOGGER = LoggerFactory.getLogger(MigrationBOImpl.class);

    @Inject
    private MigrationAuditDAO migrationAuditDAO;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ca.umg.business.migration.bo.MigrationBO#createMigrationAudit(com.ca.umg.business.migration.audit.entity.MigrationAudit
     * )
     */
    @Override
    public MigrationAudit createMigrationAudit(MigrationAudit migrationAudit) throws BusinessException, SystemException {
        LOGGER.info("Creating Migration Log");
        return migrationAuditDAO.saveAndFlush(migrationAudit);
    }

    @Override
    public void markExportAsFailed(String migrationId) {
        try {
            MigrationAudit migrationAudit = migrationAuditDAO.findOne(migrationId);
            if (migrationAudit != null) {
                migrationAudit.setStatus(BusinessConstants.MIGRATION_STATUS_FAILED);
                migrationAudit = migrationAuditDAO.saveAndFlush(migrationAudit);
            } else {
                LOGGER.error("There is no migration for {} id.", migrationId);
            }
        } catch (DataAccessException e) {
            LOGGER.error("Unable to update UMG Migration", e);
        }

    }
    
    @Override
    public MigrationAudit updateMigrationAudit(MigrationAudit migrationAudit) throws BusinessException, SystemException {
    	MigrationAudit existingMigrationAudit = null;
    	try {
            existingMigrationAudit = migrationAuditDAO.findOne(migrationAudit.getId());
            if (existingMigrationAudit != null) {
            	existingMigrationAudit.setType(BusinessConstants.MIGRATION_TYPE_IMPORT);
            	existingMigrationAudit.setStatus(BusinessConstants.MIGRATION_STATUS_SUCCESSFUL);
            	existingMigrationAudit = migrationAuditDAO.saveAndFlush(migrationAudit);
            	
            	LOGGER.info("Updated Migration Audit Successfully");
            } else {
                LOGGER.error("There is no migration for {} id.", migrationAudit.getId());
            }
        } catch (DataAccessException e) {
            LOGGER.error("Unable to update UMG Migration", e);
        }
        
        return existingMigrationAudit;
    }
}