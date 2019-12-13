/**
 * 
 */
package com.ca.umg.business.version.command.error.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import org.springframework.context.annotation.Scope;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.version.command.error.Error;
import com.ca.umg.business.version.command.error.ErrorController;

/**
 * @author chandrsa
 *
 */
@Named
@Scope(value = BusinessConstants.SCOPE_PROTOTYPE)
public class ErrorControllerImpl implements ErrorController {
    
    private boolean continueExecution = Boolean.TRUE;
    private final List<Error> errors = new ArrayList<Error>();
    private String versionId;
    private boolean rollbackInitiated = Boolean.FALSE;
    

    /* (non-Javadoc)
     * @see com.ca.umg.business.version.command.error.ErrorController#canContinueExecution()
     */
    @Override
    public boolean canContinueExecution() throws SystemException {
        return continueExecution;
    }

    /* (non-Javadoc)
     * @see com.ca.umg.business.version.command.error.ErrorController#setExecutionBreak(boolean)
     */
    @Override
    public void setExecutionBreak(boolean execBreak) throws SystemException {
        this.continueExecution = !execBreak; // NOPMD
    }

    /* (non-Javadoc)
     * @see com.ca.umg.business.version.command.error.ErrorController#setVersionIdentifier(java.lang.String)
     */
    @Override
    public void setVersionIdentifier(String id) throws SystemException {
        this.versionId = id;

    }

    /* (non-Javadoc)
     * @see com.ca.umg.business.version.command.error.ErrorController#getVersionIdentifier()
     */
    @Override
    public String getVersionIdentifier() throws SystemException {
        return this.versionId;
    }

    /* (non-Javadoc)
     * @see com.ca.umg.business.version.command.error.ErrorController#addError(com.ca.umg.business.version.command.error.Error)
     */
    @Override
    public void addError(Error error) throws SystemException {
        this.errors.add(error);
    }

    /* (non-Javadoc)
     * @see com.ca.umg.business.version.command.error.ErrorController#setErrors(java.util.List)
     */
    @Override
    public void setErrors(List<Error> errors) throws SystemException {
        this.errors.addAll(errors);
    }

    /* (non-Javadoc)
     * @see com.ca.umg.business.version.command.error.ErrorController#getErrors()
     */
    @Override
    public List<Error> getErrors() throws SystemException {
        return errors;
    }

    /* (non-Javadoc)
     * @see com.ca.umg.business.version.command.error.ErrorController#setRollbackFlag(boolean)
     */
    @Override
    public void setRollbackFlag(boolean rollback) throws SystemException {
       this.rollbackInitiated = rollback;
    }

    /* (non-Javadoc)
     * @see com.ca.umg.business.version.command.error.ErrorController#isRollback()
     */
    @Override
    public boolean isRollback() throws SystemException {
        return rollbackInitiated;
    }
}