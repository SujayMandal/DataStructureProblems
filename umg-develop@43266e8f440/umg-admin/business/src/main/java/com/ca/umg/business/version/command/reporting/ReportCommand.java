/**
 * 
 */
package com.ca.umg.business.version.command.reporting;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.version.command.error.ErrorController;
import com.ca.umg.business.version.command.info.CommandReportInfo;

/**
 * @author chandrsa
 *
 */
public interface ReportCommand {

    /**
     * @param commandReportInfo
     * @param errorController
     * @param data
     * @throws BusinessException
     * @throws SystemException
     */
    void generateReport(CommandReportInfo commandReportInfo,ErrorController errorController, Object data) throws BusinessException, SystemException;
}
