/**
 * 
 */
package com.ca.umg.business.version.command.prepare;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.version.command.Command;
import com.ca.umg.business.version.command.CommandPreparator;
import com.ca.umg.business.version.command.error.ErrorController;
import com.ca.umg.business.version.command.processor.CommandAggregator;

/**
 * @author chandrsa
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
    public Command prepareCommand(ErrorController errorController, String commandName) throws BusinessException, SystemException {
        Command command = null;
        if (MapUtils.isNotEmpty(commandsMap)) {
            command = rootContext.getBean(commandsMap.get(StringUtils.lowerCase(commandName)));
            command.setErrorController(errorController);
        } else {
            SystemException.newSystemException("",
                    new Object[] { "No commands available! Please consider refreshing the context" });
        }
        return command;
    }

    @PostConstruct
    public void gatherCommands() {
        try {
            commandsMap = commandAggregator.getCommands();
        } catch (BusinessException | SystemException e) {
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
