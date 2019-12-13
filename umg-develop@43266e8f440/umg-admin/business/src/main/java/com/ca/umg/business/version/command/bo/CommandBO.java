/**
 * 
 */
package com.ca.umg.business.version.command.bo;

import java.util.List;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;

/**
 * @author kamathan
 *
 */
public interface CommandBO {

    /**
     * Returns all the commands defined in the system in their execution order for the given process.
     * 
     * @param process
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    public List<String> getAllCommandsByExecutionSequenceForProcess(String process) throws BusinessException, SystemException;

}
