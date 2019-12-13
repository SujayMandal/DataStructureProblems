package com.ca.framework.core.logging.appender;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;

import com.mongodb.Mongo;
import com.mongodb.WriteConcern;

@ContextConfiguration("classpath:com/ca/framework/core/appender/TenantAwareTest-context.xml")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class TenantAwareContextLoaderTest extends AbstractTestNGSpringContextTests {
    @Inject
    private Mongo mongo;

    @Value("${mongo.databaseName}")
    private String dbName;

    @Value("${mongo.collectionName}")
    private String collectionName;

    @Value("${mongo.username}")
    private String username;

    @Value("${mongo.password}")
    private String password;

    private TenantAwareDBConnection dbConnection = null;

    @BeforeMethod(groups = "TenantAwareBase")
    public void tenantAwareDBConnectionTest() {
        this.dbConnection = new TenantAwareDBConnection(getMongo().getDB(getDbName()), new WriteConcern(true),
                getCollectionName());
        Assert.assertNotNull(dbConnection);
    }


    public Mongo getMongo() {
        return mongo;
    }

    public String getDbName() {
        return dbName;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public TenantAwareDBConnection getDbConnection() {
        return dbConnection;
    }
}
