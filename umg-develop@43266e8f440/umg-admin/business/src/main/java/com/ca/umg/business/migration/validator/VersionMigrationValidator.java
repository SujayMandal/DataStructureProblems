/**
 * 
 */
package com.ca.umg.business.migration.validator;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;

/**
 * @author kamathan
 *
 */
public interface VersionMigrationValidator {

    boolean validatePackageChecksum() throws BusinessException, SystemException;

    boolean validateSyndicateTables() throws BusinessException, SystemException;

}
