/**
 * 
 */
package com.ca.umg.business.version.command.reporting.impl;

import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.version.command.error.ErrorController;
import com.ca.umg.business.version.command.info.CommandReportInfo;
import com.ca.umg.business.version.command.reporting.ReportCommand;
import com.ca.umg.business.version.info.VersionInfo;

/**
 * @author chandrsa
 *
 */
@Named("versionReport")
public class VersionReport implements ReportCommand {
    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.version.command.reporting.ReportCommand#generateReport(com.ca.umg.business.version.command.info.
     * CommandReportInfo, java.lang.Object)
     */
    @Override
    public void generateReport(CommandReportInfo commandReportInfo, ErrorController errorController, Object data)
            throws BusinessException, SystemException {
        // VersionInfo versionInfo = null;
    	VersionInfo dataInfo = (VersionInfo) data;
    	if (CollectionUtils.isEmpty(errorController.getErrors())) {
            // versionInfo = new VersionInfo();
            commandReportInfo.getVersionInfo().setId(dataInfo.getId());
            commandReportInfo.getVersionInfo().setMajorVersion(dataInfo.getMajorVersion());
            commandReportInfo.getVersionInfo().setMinorVersion(dataInfo.getMinorVersion());
            commandReportInfo.getVersionInfo().setStatus(dataInfo.getStatus());
            // commandReportInfo.setVersionInfo(versionInfo);
    		commandReportInfo.setSuccess(Boolean.TRUE);
    		commandReportInfo.setVersionId(dataInfo.getId());
    		commandReportInfo.setTransactionId(dataInfo.getUmgTransactionId());
        } else {
    		commandReportInfo.setErrors(errorController.getErrors());    		
    		commandReportInfo.setSuccess(Boolean.FALSE);
    		commandReportInfo.setVersionId(null);
    		commandReportInfo.setTransactionId(dataInfo.getUmgTransactionId());
    	}
    }
}
