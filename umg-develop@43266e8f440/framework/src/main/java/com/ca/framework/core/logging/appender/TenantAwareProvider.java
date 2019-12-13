/**
 * 
 */
package com.ca.framework.core.logging.appender;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.util.Loader;
import org.apache.logging.log4j.nosql.appender.NoSqlProvider;
import org.apache.logging.log4j.status.StatusLogger;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;

/**
 * @author kamathan
 * 
 */
@Plugin(name = "TenantAwareDb", category = "Core", printObject = true)
public final class TenantAwareProvider implements NoSqlProvider<TenantAwareDBConnection> {

    private static final Logger LOGGER = StatusLogger.getLogger();
    private final String collectionName;
    private final DB database;
    private final String description;
    private final WriteConcern writeConcern;

    private TenantAwareProvider(DB database, WriteConcern writeConcern, String collectionName, String description) {
        this.database = database;
        this.writeConcern = writeConcern;
        this.collectionName = collectionName;
        this.description = "mongoDb{ " + description + " }";
    }

    public TenantAwareDBConnection getConnection() {
        return new TenantAwareDBConnection(this.database, this.writeConcern, this.collectionName);
    }

    public String toString() {
        return this.description;
    }

    @PluginFactory
    public static TenantAwareProvider createNoSQLProvider(@PluginAttribute("collectionName") String collectionName,
            @PluginAttribute("writeConcernConstant") String writeConcernConstant,
            @PluginAttribute("writeConcernConstantClass") String writeConcernConstantClassName,
            @PluginAttribute("databaseName") String databaseName, @PluginAttribute("server") String server,
            @PluginAttribute("port") String port, @PluginAttribute("username") String username,
            @PluginAttribute("password") String password, @PluginAttribute("factoryClassName") String factoryClassName,
            @PluginAttribute("factoryMethodName") String factoryMethodName) {
        DB database;
        String description;
        if (factoryClassName != null && factoryClassName.length() > 0 && factoryMethodName != null
                && factoryMethodName.length() > 0) {
            try {
                final Class<?> factoryClass = Loader.loadClass(factoryClassName);
                final Method method = factoryClass.getMethod(factoryMethodName);
                final Object object = method.invoke(null);

                if (object instanceof DB) {
                    database = (DB) object;
                } else if (object instanceof MongoClient) {
                    if (databaseName != null && databaseName.length() > 0) {
                        database = ((MongoClient) object).getDB(databaseName);
                    } else {
                        LOGGER.error(
                                "The factory method [{}.{}()] returned a MongoClient so the database name is " + "required.",
                                factoryClassName, factoryMethodName);
                        return null;
                    }
                } else if (object == null) {
                    LOGGER.error("The factory method [{}.{}()] returned null.", factoryClassName, factoryMethodName);
                    return null;
                } else {
                    LOGGER.error("The factory method [{}.{}()] returned an unsupported type [{}].", factoryClassName,
                            factoryMethodName, object.getClass().getName());
                    return null;
                }

                description = "database=" + database.getName();
                final List<ServerAddress> addresses = database.getMongo().getAllAddress();
                if (addresses.size() == 1) {
                    description += ", server=" + addresses.get(0).getHost() + ", port=" + addresses.get(0).getPort();
                } else {
                    description += ", servers=[";
                    for (final ServerAddress address : addresses) {
                        description += " { " + address.getHost() + ", " + address.getPort() + " } ";
                    }
                    description += "]";
                }
            } catch (final ClassNotFoundException e) {
                LOGGER.error("The factory class [{}] could not be loaded.", factoryClassName, e);
                return null;
            } catch (final NoSuchMethodException e) {
                LOGGER.error("The factory class [{}] does not have a no-arg method named [{}].", factoryClassName,
                        factoryMethodName, e);
                return null;
            } catch (final Exception e) {
                LOGGER.error("The factory method [{}.{}()] could not be invoked.", factoryClassName, factoryMethodName, e);
                return null;
            }
        } else if (databaseName != null && databaseName.length() > 0) {
            description = "database=" + databaseName;
            try {
                if (server != null && server.length() > 0) {
                    final int portInt = AbstractAppender.parseInt(port, 0);
                    description += ", server=" + server;
                    String[] servers = server.split(",");
                    if (portInt > 0) {
                        if(servers.length > 1) {
                            List<ServerAddress> serverAddress = new ArrayList<ServerAddress>(servers.length);
                            for(String instance: servers){
                                String[] serverPort = instance.split(":");
                                if(serverPort.length > 1 ) {
                                    serverAddress.add(new ServerAddress(serverPort[0],AbstractAppender.parseInt(serverPort[1], 0)));
                                } else {
                                    serverAddress.add(new ServerAddress(serverPort[0], portInt));
                                }
                            }
                            database = new MongoClient(serverAddress).getDB(databaseName);
                        } else {
                            description += ", port=" + portInt;
                            database = new MongoClient(server, portInt).getDB(databaseName);
                        }                        
                    } else {
                        if(servers.length > 1) {
                            List<ServerAddress> serverAddress = new ArrayList<ServerAddress>(servers.length);
                            for(String instance: servers){
                                String[] serverPort = instance.split(":");
                                if(serverPort.length > 1 ) {
                                    serverAddress.add(new ServerAddress(serverPort[0],AbstractAppender.parseInt(serverPort[1], 0)));
                                } else {
                                    serverAddress.add(new ServerAddress(serverPort[0]));
                                }
                            }
                            database = new MongoClient(serverAddress).getDB(databaseName);
                        } else {
                            database = new MongoClient(server).getDB(databaseName);
                        }
                    }
                } else {
                    database = new MongoClient().getDB(databaseName);
                }
            } catch (final Exception e) {
                LOGGER.error("Failed to obtain a database instance from the MongoClient at server [{}] and " + "port [{}].",
                        server, port);
                return null;
            }
        } else {
            LOGGER.error("No factory method was provided so the database name is required.");
            return null;
        }

        description = authenticate(username, password, database, description);

        WriteConcern writeConcern;
        if (writeConcernConstant != null && writeConcernConstant.length() > 0) {
            if (writeConcernConstantClassName != null && writeConcernConstantClassName.length() > 0) {
                try {
                    final Class<?> writeConcernConstantClass = Loader.loadClass(writeConcernConstantClassName);
                    final Field field = writeConcernConstantClass.getField(writeConcernConstant);
                    writeConcern = (WriteConcern) field.get(null);
                } catch (final Exception e) {
                    LOGGER.error("Write concern constant [{}.{}] not found, using default.", writeConcernConstantClassName,
                            writeConcernConstant);
                    writeConcern = WriteConcern.ACKNOWLEDGED;
                }
            } else {
                writeConcern = WriteConcern.valueOf(writeConcernConstant);
                if (writeConcern == null) {
                    LOGGER.warn("Write concern constant [{}] not found, using default.", writeConcernConstant);
                    writeConcern = WriteConcern.ACKNOWLEDGED;
                }
            }
        } else {
            writeConcern = WriteConcern.ACKNOWLEDGED;
        }
        return new TenantAwareProvider(database, writeConcern, collectionName, description);
    }

    private static String authenticate(String username, String password, DB database, String description) {
        String descriptionToReturn = description;
        /*if (!database.isAuthenticated()) {
            if (username != null && username.length() > 0 && password != null && password.length() > 0) {
                descriptionToReturn += ", username=" + username + ", passwordHash="
                        + NameUtil.md5(password + MongoDbProvider.class.getName());
                TenantAwareDBConnection.authenticate(database, username, password);
            } else {
                LOGGER.error("The database is not already authenticated so you must supply a username and password "
                        + "for the MongoDB provider.");
                return null;
            }
        }*/
        return descriptionToReturn;
    }
}
