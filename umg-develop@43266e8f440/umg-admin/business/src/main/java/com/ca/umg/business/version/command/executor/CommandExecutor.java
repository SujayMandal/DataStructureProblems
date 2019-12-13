/**
 * 
 */
package com.ca.umg.business.version.command.executor;

import java.util.List;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.version.command.Command;
import com.ca.umg.business.version.command.error.ErrorController;
import com.ca.umg.business.version.command.info.CommandReportInfo;

/**
 * @author chandrsa
 *
 */
public interface CommandExecutor {

    /**
     * Executes the commands submitted to it in the sequence. The first command in the list is executed first and so on. The
     * execution would stop if any exception is registered.
     * 
     * @param commands
     *            , the list of sequenced commands.
     * @param errorController
     *            , the error controller that all commands have an access to.
     * @param data
     *            , the data on which the commands will act upon.
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    CommandReportInfo execute(ErrorController errorController, List<Command> commands, Object data) throws BusinessException,
            SystemException;
}
