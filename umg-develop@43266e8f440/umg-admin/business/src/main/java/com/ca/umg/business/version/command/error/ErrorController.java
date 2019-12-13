/**
 * 
 */
package com.ca.umg.business.version.command.error;

import java.util.List;

import com.ca.framework.core.exception.SystemException;

/**
 * @author chandrsa
 *
 */
public interface ErrorController {

    boolean canContinueExecution() throws SystemException;
    
    void setExecutionBreak(boolean execBreak) throws SystemException;
    
    void setVersionIdentifier(String id) throws SystemException;
    
    String getVersionIdentifier() throws SystemException;
    
    void addError(Error error) throws SystemException;
    
    void setErrors(List<Error> errors) throws SystemException;
    
    List<Error> getErrors() throws SystemException;
    
    void setRollbackFlag(boolean rollback) throws SystemException;
    
    boolean isRollback() throws SystemException;
    
}
