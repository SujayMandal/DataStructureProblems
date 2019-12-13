/**
 * 
 */
package com.ca.umg.business.version.command.master;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.version.command.info.CommandReportInfo;
import com.ca.umg.business.version.info.VersionInfo;

/**
 * The master flow controller. Creates appropriate execution flows collating commands and hands over the responsibility of
 * execution to the command executor.
 * 
 * @author chandrsa
 *
 */
public interface CommandMaster {

    /**
     * This method would take the responsibility of version creation.
     * 
     * @param versionInfo
     *            has all the information required to create version
     * @return {@link CommandReportInfo} having the status and report for the activity.
     * 
     * @throws BusinessException
     * @throws SystemExceptio
     */
    CommandReportInfo createVersion(VersionInfo versionInfo) throws BusinessException, SystemException;

    /**
     * This method would find the version needed to be updated based upon the version name and prepare the system for edit flow
     * execution.
     * 
     * @param versionInfo
     *            has all the information required to create version
     * @return {@link CommandReportInfo} having the status and report for the activity.
     * 
     * @throws BusinessException
     * @throws SystemExceptio
     */
    CommandReportInfo updateVersion(VersionInfo versionInfo) throws BusinessException, SystemException;

    /**
     * This method would prepare the system for rollback of the version. Reads for the steps created and calls the rollback in the
     * reverse order.
     * 
     * @param versionId
     *            , the identifier for the version. Using this identifier the system would pull out all details required for
     *            rollback
     * @return {@link CommandReportInfo} having the status and report for the activity.
     * 
     * @throws BusinessException
     * @throws SystemException
     */
    CommandReportInfo rollbackVersion(String versionId) throws BusinessException, SystemException;
}
