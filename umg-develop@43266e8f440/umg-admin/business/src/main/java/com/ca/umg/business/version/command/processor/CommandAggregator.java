/**
 * 
 */
package com.ca.umg.business.version.command.processor;

import java.util.Map;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.version.command.Command;

/**
 * @author kamathan
 *
 */
public interface CommandAggregator {

    /**
     * Returns all the command beans present in the application context.
     * 
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    public Map<String, Class<? extends Command>> getCommands() throws BusinessException, SystemException;
}
