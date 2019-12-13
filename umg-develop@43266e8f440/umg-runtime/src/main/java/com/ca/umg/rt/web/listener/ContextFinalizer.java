package com.ca.umg.rt.web.listener;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Set;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.mongodb.DB;
import com.mysql.jdbc.AbandonedConnectionCleanupThread;

public class ContextFinalizer implements ServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContextFinalizer.class);

    public void contextInitialized(ServletContextEvent sce) {
        // EMPTY_METHOD
    }

    public void contextDestroyed(ServletContextEvent sce) {
        try {
            ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(sce.getServletContext());
            MongoTemplate mongoTemplate = ctx.getBean(MongoTemplate.class);

            if (mongoTemplate != null) {
                DB mongoDb = mongoTemplate.getDb();
                if (mongoDb != null && mongoDb.getMongo() != null) {
                    LOGGER.info("Closing connections to mongo db " + mongoDb.getName());
                    mongoDb.getMongo().close();
                }
            }

            Enumeration<Driver> drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                Driver driver = drivers.nextElement();
                LOGGER.info("Deregistering the driver " + driver);
                DriverManager.deregisterDriver(driver);
            }

            AbandonedConnectionCleanupThread.shutdown();
        } catch (InterruptedException e) {
            LOGGER.warn("Severe problem cleaning up AbandonedConnectionCleanupThread : " + e.getMessage());
        } catch (SQLException e) {
            LOGGER.warn("Severe problem deregistering driver : " + e.getMessage());
        }
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
        for (Thread t : threadArray) {
            if (t.getName().contains("com.google.common.base.internal.Finalizer")) {
                synchronized (t) {
                    t.stop();
                }
            }
        }
    }
}