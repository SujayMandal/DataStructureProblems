/**
 * 
 */
package com.ca.umg.business.version.command;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.version.command.error.ErrorController;

/**
 * @author kamathan
 *
 */
public interface Command {

    /**
     * This method executes the process for creating the artifact.
     * 
     * @param data
     *            has all information embedded that the UI layer has provided.
     * 
     * @throws BusinessException
     *             when there are validation error
     * @throws SystemException
     *             when system cannot complete the requested step.
     */
    void execute(Object data) throws BusinessException, SystemException;

    /**
     * This method would rollback the creation step : {@link #execute(VersionInfo)}
     * 
     * @param data
     *            has all information embedded populated by the command master.
     * @throws BusinessException
     *             when the rollback faces business issues.
     * @throws SystemException
     *             when system cannot process the request.
     */
    void rollback(Object data) throws BusinessException, SystemException;

    /**
     * This method is to check if the command has been prepared correctly.
     * 
     * @return <code>true</code> if created successfully.
     * @throws BusinessException
     * @throws SystemException
     */
    boolean isCreated() throws BusinessException, SystemException;

    /**
     * This method would set the error controller assigned for the execution flow in command.
     * 
     * @param errorController
     * @throws BusinessException
     * @throws SystemException
     */
    void setErrorController(ErrorController errorController) throws BusinessException, SystemException;

    /**
     * This method would return the execution status of the command.
     * 
     * @return
     */
    boolean isExecuted() throws BusinessException, SystemException;
}
