/**
 * 
 */
package com.ca.umg.business.version.command;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.version.command.error.ErrorController;

/**
 * @author chandrsa
 *
 */
public interface CommandPreparator {

    /**
     * This method would prepare the command bean for execution using the command name. The beans are of type prototype. Be sure
     * to mark the command implementations are prototypes.
     * 
     * @param errorController
     *            , the error holder passed to each command.
     * @param commandName
     *            , the name of the command to be created.
     * 
     * @return {@link Command} prepared and ready for use.
     * @throws BusinessException
     * @throws SystemException
     */
    Command prepareCommand(ErrorController errorController, String commandName) throws BusinessException, SystemException;

}
