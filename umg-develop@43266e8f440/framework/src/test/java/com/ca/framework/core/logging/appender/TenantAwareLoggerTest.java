/**
 * 
 */
package com.ca.framework.core.logging.appender;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * test case check if the logs are written to mongo database correctly.
 * 
 * @author kamathan
 * 
 */
public class TenantAwareLoggerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantAwareLoggerTest.class);

    @Test
    public void testLoggingToDefaultCollection() {
        LOGGER.debug("Test logging to default collection.");
        // wait for async thread to complete logging
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testLoggingToTenantCollection() {
        MDC.put(AppenderConstants.MDC_TENANT_CODE, "ocwen");
        LOGGER.debug("Test logging to ocwen tenant collection");

        // wait for async thread to complete logging
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testWithConcurrentLogs() {
        MyThreadLoggingClient[] loggingClient = new MyThreadLoggingClient[3];
        int i = 1;
        for (MyThreadLoggingClient client : loggingClient) {
            client = new MyThreadLoggingClient(LOGGER, "Tenant" + i);
            ++i;
            new Thread(client).start();
        }

        try {
            // wait for async thread to complete logging
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
    }

}

class MyThreadLoggingClient implements Runnable {

    private Logger logger = null;
    private String tenantId;

    MyThreadLoggingClient(Logger logger, String tenantId) {
        this.logger = logger;
        this.tenantId = tenantId;
    }

    @Override
    public void run() {
        MDC.put(AppenderConstants.MDC_TENANT_CODE, tenantId);
        logger.info("Testing ......" + tenantId);

    }

}