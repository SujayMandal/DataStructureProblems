/**
 * 
 */
package com.ca.umg.business.version.command.processor;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.version.command.Command;

/**
 * @author kamathan
 *
 */
@Named
public class CommandAggregatorImpl implements CommandAggregator {

    @Inject
    private CommandAggregatorUtil util;

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.version.command.processor.CommandAggregator#getCommands()
     */
    @Override
    public Map<String, Class<? extends Command>> getCommands() throws BusinessException, SystemException {
        return util.scanForCommands();
    }

}
