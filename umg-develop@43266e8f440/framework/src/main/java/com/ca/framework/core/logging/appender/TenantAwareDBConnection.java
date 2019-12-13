/**
 * 
 */
package com.ca.framework.core.logging.appender;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.nosql.appender.NoSqlConnection;
import org.apache.logging.log4j.nosql.appender.NoSqlObject;
import org.apache.logging.log4j.status.StatusLogger;
import org.bson.BSON;
import org.bson.Transformer;
import org.jasypt.util.text.BasicTextEncryptor;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

/**
 * @author kamathan
 * 
 */
public class TenantAwareDBConnection implements NoSqlConnection<BasicDBObject, TenantAwareDbObject> {

    private static final Logger LOGGER = StatusLogger.getLogger();

    static {
        BSON.addEncodingHook(Level.class, new Transformer() {
            public Object transform(Object o) {
                if (o instanceof Level) {
                    return ((Level) o).name();
                }
                return o;
            }
        });
    }

    private final DBCollection collection;
    private final Mongo mongo;
    private final WriteConcern writeConcern;

    private final DB database;

    public TenantAwareDBConnection(DB database, WriteConcern writeConcern, String collectionName) {
        this.mongo = database.getMongo();
        this.collection = database.getCollection(collectionName);
        this.writeConcern = writeConcern;
        this.database = database;
    }

    public TenantAwareDbObject createObject() {
        return new TenantAwareDbObject();
    }

    public TenantAwareDbObject[] createList(int length) {
        return new TenantAwareDbObject[length];
    }

    public void insertObject(NoSqlObject<BasicDBObject> object) {
        try {
            //String tenantCode = MDC.get(AppenderConstants.MDC_TENANT_CODE);
            
            TenantAwareDbObject logMessage = (TenantAwareDbObject)object;
            String tenantCode = logMessage.getTenant();
            DBCollection dbCollection = null;
            WriteResult result = null;
            if (StringUtils.isNotBlank(tenantCode)) {
                tenantCode = tenantCode + AppenderConstants.LOG_COLLECTION_SUFFIX;
                dbCollection = this.database.getCollection(tenantCode);
                result = dbCollection.insert((DBObject) object.unwrap(), this.writeConcern);
            } else {
                result = this.collection.insert(object.unwrap(), this.writeConcern);
            }
            /*if (Strings.isNotEmpty(result.getError())) {
                throw new AppenderLoggingException("Failed to write log event to MongoDB due to error: " + result.getError()
                        + ".");
            }*/
        } catch (MongoException e) {
            throw new AppenderLoggingException("Failed to write log event to MongoDB due to error: " + e.getMessage(), e);
        }
    }

    public void close() {
        // there's no need to call this.mongo.close() since that literally
        // closes the connection
        // MongoDBClient uses internal connection pooling
        // for more details, see LOG4J2-591
    }

    public boolean isClosed() {
        //return !this.mongo.getConnector().isOpen();
    	return Boolean.TRUE;
    }

    static void authenticate(DB database, String username, String password) {
        try {
            BasicTextEncryptor basicTextEncryptor = new BasicTextEncryptor();
            basicTextEncryptor.setPassword(AppenderConstants.PSSWDKY);
            String decryptedPwd = basicTextEncryptor.decrypt(password);

          /*  if (!database.authenticate(username, decryptedPwd.toCharArray())) {
                LOGGER.error("Failed to authenticate against MongoDB server. Unknown error.");
            }*/
        } catch (MongoException e) {
            LOGGER.error("Failed to authenticate against MongoDB: " + e.getMessage(), e);
        } catch (IllegalStateException e) {
            LOGGER.error("Factory-supplied MongoDB database connection already authenticated with differentcredentials but lost connection.");
        }
    }

    static {
        BSON.addEncodingHook(Level.class, new Transformer() {
            public Object transform(Object o) {
                if (o instanceof Level) {
                    return ((Level) o).name();
                }
                return o;
            }
        });
    }

}
