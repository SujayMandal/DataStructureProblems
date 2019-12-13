/**
 * 
 */
package com.fa.dp.business.command.bo;

import java.util.List;

import com.fa.dp.business.command.entity.Command;
import com.fa.dp.core.exception.SystemException;

/**
 * @author mandasuj
 *
 */
public interface CommandBO {

    /**
     * Returns all the commands defined in the system in their execution order for the given process.
     * 
     * @param process
     * @return
     * @throws SystemException
     */
    public List<String> getAllCommandsByExecutionSequenceForProcess(String process) throws SystemException;

}
