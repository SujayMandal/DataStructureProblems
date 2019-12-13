package com.ca.framework.core.logging.appender;

import org.testng.annotations.Test;

public class TenantAwareTest extends TenantAwareContextLoaderTest {
    @Test(groups = { "TenantAwareBase" }, enabled = false)
    public void authenticateTest() {
        TenantAwareDBConnection.authenticate(getMongo().getDB(getDbName()), getUsername(), getPassword());
    }

    @Test(groups = { "TenantAwareBase" }, enabled = false)
    public void authenticateFailTest() {
        try {
            TenantAwareDBConnection.authenticate(getMongo().getDB(getDbName()), getUsername(), "log");
        } catch (Exception e) {
            // Assert.fail("failed password authentication test");
        }
    }

    @Test(groups = { "TenantAwareBase" }, enabled = false)
    public void insertObject() {
        TenantAwareDbObject dbo = new TenantAwareDbObject();
        dbo.set("company", "timkintimkin");
        getDbConnection().insertObject(dbo);
    }
}
