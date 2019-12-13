/**
 * 
 */
package com.fa.dp.business.command.processor;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import com.fa.dp.business.command.Command;
import com.fa.dp.core.exception.SystemException;

/**
 * @author mandasuj
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
    public Map<String, Class<? extends Command>> getCommands() throws SystemException {
        return util.scanForCommands();
    }

}
