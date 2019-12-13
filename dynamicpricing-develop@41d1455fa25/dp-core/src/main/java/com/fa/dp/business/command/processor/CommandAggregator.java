/**
 * 
 */
package com.fa.dp.business.command.processor;

import java.util.Map;

import com.fa.dp.business.command.Command;
import com.fa.dp.core.exception.SystemException;

/**
 * @author mandasuj
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
    public Map<String, Class<? extends Command>> getCommands() throws SystemException;
}
