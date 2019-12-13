/**
 * 
 */
package com.fa.dp.business.command.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Named;

import com.fa.dp.business.command.Command;
import com.fa.dp.business.command.annotation.CommandDescription;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

/**
 * This utility class scans all beans in existing context and finds beans those are annotated with {@link CommandDescription} and
 * prepares the {@link Map} of command name and bena name.
 * 
 * @author mandasuj
 *
 */
@Named
public class CommandAggregatorUtil {

    @Inject
    private ApplicationContext applicationContext;

    /**
     * Scan all beans in existing context and finds beans those are annotated with {@link CommandDescription} and cache these
     * {@link Command} beans in a map.
     * 
     * @return
     */
    public Map<String, Class<? extends Command>> scanForCommands() {
        Map<String, Class<? extends Command>> commands = new HashMap<String, Class<? extends Command>>();

        Class<? extends Object> clazz = null;
        // retrieve all classes annotated with given annotation type
        final Map<String, Object> classes = applicationContext.getBeansWithAnnotation(CommandDescription.class);
        // verify each class is assignable to IssService
        if (MapUtils.isNotEmpty(classes)) {
            for (Entry<String, Object> entry : classes.entrySet()) {
                // verify it is assignable to IssService
                if (entry.getValue() instanceof Command) {
                    // find service name by using Command meta data
                    clazz = entry.getValue().getClass();
                    CommandDescription annotation = clazz.getAnnotation(CommandDescription.class);
                    // populate map with name (lower case) and service bean
                    commands.put(StringUtils.lowerCase(StringUtils.trimToEmpty(annotation.name())),
                            (Class<? extends Command>) entry.getValue().getClass());
                }
            }
        }
        return commands;
    }
}
