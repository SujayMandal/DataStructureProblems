/**
 * 
 */
package com.fa.dp.business.command.prepare;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import com.fa.dp.business.command.Command;
import com.fa.dp.business.command.CommandPreparator;
import com.fa.dp.business.command.processor.CommandAggregator;
import com.fa.dp.core.exception.SystemException;

import com.fa.dp.core.util.RAClientConstants;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author mandasuj
 *
 */
@Named
public class DefaultCommandPreparator implements CommandPreparator, ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCommandPreparator.class);

    @Inject
    private CommandAggregator commandAggregator;

    private Map<String, Class<? extends Command>> commandsMap;

    private ApplicationContext rootContext;

    /* (non-Javadoc)
     * @see com.ca.umg.business.version.command.CommandPreparator#prepareCommand(com.ca.umg.business.version.command.error.ErrorController, java.lang.String)
     */
    @Override
    public Command prepareCommand(String commandName) throws SystemException {
        Command command = null;
        if (MapUtils.isNotEmpty(commandsMap)) {
            command = rootContext.getBean(commandsMap.get(StringUtils.lowerCase(commandName)));
        } else {
            SystemException.newSystemException(RAClientConstants.CHAR_EMPTY,
                    new Object[] { "No commands available! Please consider refreshing the context" });
        }
        return command;
    }

    @PostConstruct
    public void gatherCommands() {
        try {
            commandsMap = commandAggregator.getCommands();
        } catch (SystemException e) {
            LOGGER.error("Commands could not be aggregated!!", e);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        LOGGER.debug("Adding application context to DefaultCommandPreparator");
        rootContext = applicationContext;
        LOGGER.debug("Added application context to DefaultCommandPreparator");
    }
}
