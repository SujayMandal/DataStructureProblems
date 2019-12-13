/**
 * 
 */
package com.ca.umg.business.version.command.master.impl;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.version.command.Command;
import com.ca.umg.business.version.command.CommandPreparator;
import com.ca.umg.business.version.command.bo.CommandBO;
import com.ca.umg.business.version.command.error.Error;
import com.ca.umg.business.version.command.error.ErrorController;
import com.ca.umg.business.version.command.executor.CommandExecutor;
import com.ca.umg.business.version.command.info.CommandProcess;
import com.ca.umg.business.version.command.info.CommandReportInfo;
import com.ca.umg.business.version.command.master.CommandMaster;
import com.ca.umg.business.version.command.reporting.ReportCommand;
import com.ca.umg.business.version.info.VersionInfo;

/**
 * @author chandrsa
 *
 */
@Named("versionCommandMaster")
public class VersionCommandMaster implements CommandMaster, ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(VersionCommandMaster.class);

    @Inject
    private CommandPreparator commandPreparator;

    @Inject
    private CommandExecutor commandExecutor;

    private ApplicationContext rootContext;

    @Inject
    private CommandBO commandBO;
    
    @Inject
    @Named("versionReport")
    private ReportCommand versionReport;

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.version.command.master.CommandMaster#createVersion(com.ca.umg.business.version.info.VersionInfo)
     */
    @Override
    public CommandReportInfo createVersion(VersionInfo versionInfo) throws BusinessException, SystemException {
        return createExecuteFlows(versionInfo, CommandProcess.CREATE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.version.command.master.CommandMaster#updateVersion(com.ca.umg.business.version.info.VersionInfo)
     */
    @Override
    public CommandReportInfo updateVersion(VersionInfo versionInfo) throws BusinessException, SystemException {
        return createExecuteFlows(versionInfo, CommandProcess.EDIT);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.version.command.master.CommandMaster#rollbackVersion(java.lang.String)
     */
    @Override
    public CommandReportInfo rollbackVersion(String versionId) throws BusinessException, SystemException {
        LOGGER.debug("NOT IMPLEMENTED!!");
        return null;
    }

    private CommandReportInfo createExecuteFlows(VersionInfo versionInfo, CommandProcess process) throws BusinessException,
            SystemException {
        LOGGER.debug(String.format("Version %s process started", process.getCommmandProcess()));
        CommandReportInfo commandReportInfo = null;
        List<String> commandSequence = null;
        List<Command> execCommands = null;
        ErrorController errorController = null;
        Command command = null;
        if (versionInfo != null) {
            errorController = rootContext.getBean(ErrorController.class);
            errorController.setVersionIdentifier(getVersionIdentifier(versionInfo));
            commandSequence = commandBO.getAllCommandsByExecutionSequenceForProcess(process.getCommmandProcess());
            if (CollectionUtils.isNotEmpty(commandSequence)) {
                execCommands = new LinkedList<Command>();
                for (String commandName : commandSequence) {
                    command = commandPreparator.prepareCommand(errorController, commandName);
                    if (command.isCreated()) {
                        execCommands.add(command);
                    } else {
                        SystemException.newSystemException("", new Object[] { commandName });
                        // TODO put the code "Could not create {0} command"
                    }
                }
                commandReportInfo = commandExecutor.execute(errorController, execCommands, versionInfo);
                if (!commandReportInfo.isSuccess()) {
                    LOGGER.debug("System initiated Rollback.");
                    commandReportInfo.setRollback(Boolean.TRUE);
                    errorController.setRollbackFlag(Boolean.TRUE);
                    errorController.setExecutionBreak(Boolean.FALSE);
                    commandReportInfo = commandExecutor.execute(errorController,
                            (LinkedList<Command>) commandReportInfo.getExecutedSteps(), versionInfo);
                }
            } else {
                Error error = new Error("No Commands Configured! Please contact system admin.", "Started to create version", "");
                commandReportInfo = new CommandReportInfo();
                commandReportInfo.addError(error);
            }
        } else {
            Error error = new Error("Version information is empty", "Started to create version", "");// TODO find better ways to
            commandReportInfo = new CommandReportInfo();
            commandReportInfo.addError(error);
        }
        versionReport.generateReport(commandReportInfo, errorController, versionInfo);
        LOGGER.debug(String.format("Version %s process completed", process.getCommmandProcess()));
        return commandReportInfo;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        LOGGER.debug("Adding application context to VersionCommandMaster");
        rootContext = applicationContext;
        LOGGER.debug("Added application context to VersionCommandMaster");
    }
    
    private String getVersionIdentifier(VersionInfo info) {
        return new StringBuffer(info.getName()).append(BusinessConstants.HYPHEN).append(info.getMajorVersion())
                .append(BusinessConstants.DOT).append(info.getMinorVersion()).toString();
    }
}