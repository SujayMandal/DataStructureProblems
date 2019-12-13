/**
 * 
 */
package com.ca.umg.business.version.command.executor.impl;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.version.command.Command;
import com.ca.umg.business.version.command.error.ErrorController;
import com.ca.umg.business.version.command.executor.CommandExecutor;
import com.ca.umg.business.version.command.impl.GenerateModelReport;
import com.ca.umg.business.version.command.info.CommandReportInfo;
import com.ca.umg.business.version.info.VersionInfo;

/**
 * @author chandrsa
 *
 */
@Named
public class DefaultCommandExecutor implements CommandExecutor {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ca.umg.business.version.command.executor.CommandExecutor#execute(com.ca.umg.business.version.command.error.ErrorController
     * , java.util.List)
     */
    @Override
    public CommandReportInfo execute(ErrorController errorController, List<Command> commands, Object data)
            throws BusinessException, SystemException {
        CommandReportInfo commandReportInfo = new CommandReportInfo();
        Deque<Command> executedCommand = null;
        boolean completed = true;
        boolean rollback;
        if (CollectionUtils.isNotEmpty(commands)) {
            rollback = errorController.isRollback();
            executedCommand = new LinkedList<Command>();
            for (Command command : commands) {
                if (errorController.canContinueExecution()) {
                    if (!rollback) {
                        command.execute(data);
                    } else {
                        command.rollback(data);
                    }
                    if (command.isExecuted()) {
                        executedCommand.addFirst(command);
                    }

                } else {
                    completed = false;
                    break;
                }
            }
            commandReportInfo.setVersionId(errorController.getVersionIdentifier());
        }

        // TODO SAURABH Add details from the error controller.

        if (completed && errorController.canContinueExecution()) {
            commandReportInfo.setSuccess(true);
            VersionInfo info = (VersionInfo) data;
            commandReportInfo.setVersionInfo(info);
        } else {
            commandReportInfo.setSuccess(false);
        }
        commandReportInfo.setExecutedSteps(executedCommand);
        setReportInfo(commands, commandReportInfo);
        
        return commandReportInfo;
    }
    
    private void setReportInfo(final List<Command> commands, final CommandReportInfo commandReportInfo) throws SystemException, BusinessException {
        for (Command command : commands) {
            if (command instanceof GenerateModelReport && command.isExecuted()) {
            	commandReportInfo.setReportInfo(((GenerateModelReport) command).getReportInfo());
            	break;
            }
        }
    }
}
