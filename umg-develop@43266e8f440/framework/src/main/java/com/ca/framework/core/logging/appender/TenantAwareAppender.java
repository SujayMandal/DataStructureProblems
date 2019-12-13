package com.ca.framework.core.logging.appender;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.db.AbstractDatabaseAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.util.Booleans;
import org.apache.logging.log4j.nosql.appender.NoSqlDatabaseManager;
import org.apache.logging.log4j.nosql.appender.NoSqlProvider;

@Plugin(name = "TenantAwareAppender", category = "Core", elementType = "appender", printObject = true)
public final class TenantAwareAppender extends AbstractDatabaseAppender<NoSqlDatabaseManager<?>> {

    private final String description;

    private TenantAwareAppender(String name, Filter filter, boolean ignoreExceptions, NoSqlDatabaseManager<?> manager) {
        super(name, filter, ignoreExceptions, manager);
        this.description = getName() + "{ manager=" + getManager() + " }";

    }

    public String toString() {
        return this.description;
    }

    @PluginFactory
    public static TenantAwareAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginAttribute("ignoreExceptions") String ignore, 
            @PluginElement("Filter") Filter filter,
            @PluginAttribute("bufferSize") String bufferSize, 
            @PluginElement("NoSqlProvider") NoSqlProvider<?> provider) {
        if (provider == null) {
            LOGGER.error("NoSQL provider not specified for appender [{}].", name);
            return null;
        }

        final int bufferSizeInt = AbstractAppender.parseInt(bufferSize, 0);
        final boolean ignoreExceptions = Booleans.parseBoolean(ignore, true);

        final String managerName = "noSqlManager{ description=" + name + ", bufferSize=" + bufferSizeInt + ", provider="
                + provider + " }";

        final NoSqlDatabaseManager<?> manager = NoSqlDatabaseManager
                .getNoSqlDatabaseManager(managerName, bufferSizeInt, provider);
        if (manager == null) {
            return null;
        }

        return new TenantAwareAppender(name, filter, ignoreExceptions, manager);
    }
}
