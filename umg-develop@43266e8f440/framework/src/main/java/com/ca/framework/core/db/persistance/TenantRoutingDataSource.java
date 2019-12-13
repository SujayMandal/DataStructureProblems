/*
 * TenantRoutingDataSource.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.framework.core.db.persistance;

import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
//import java.util.UUID;
//import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;

import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.requestcontext.RequestContext;
//import com.jolbox.bonecp.BoneCPDataSource;
import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * 
 **/
public class TenantRoutingDataSource extends CAAbstractRoutingDataSource {
    private Logger logger = LoggerFactory.getLogger(TenantRoutingDataSource.class);

    private static final int MIN_CONNECTIONS = 1;

    private static final int MAX_CONNECTIONS = 3;

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     **/
    @Override
    protected Object determineCurrentLookupKey() {
        RequestContext requestContext = RequestContext.getRequestContext();

        if (requestContext == null) {
            return null;
        }

        if (requestContext.isSyndicateAware()) {
            return null;
        }
        if (requestContext.isAdminAware()) {
            return null;
        }

        return requestContext.getTenantCode();
    }

    public void createDataSourceForTenantCode(String tenantCode) throws SQLException {
        createDataSourceForTenant(tenantCode);
    }

    public void createDataSourceForTenant() throws SQLException {
        createDataSourceForTenant(null);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws SQLException
     *             DOCUMENT ME!
     **/
    @SuppressWarnings("PMD.CloseResource")
    private void createDataSourceForTenant(String tenantCode) throws SQLException {
        logger.info("Creating new datasource connection for tenant {}.", tenantCode);

        if (MapUtils.isNotEmpty(getTargetDataSources()) && getTargetDataSources().get(tenantCode) != null) {
            return;
        }

        HashMap<Object, Object> targetDataSources = new HashMap<Object, Object>();
        DataSource ds = getDefaultTargetDataSource();
        Connection con = null;

        HashMap<String, Map<String, String>> tenantMap = new HashMap<String, Map<String, String>>();
        PreparedStatement prstTenantConfig = null;
        PreparedStatement prstTenant = null;
        ResultSet rsTenant = null;
        ResultSet rsTenantConfig = null;
        Map<String, String> tenantConfigMap = null;
        String tenantId = null;
        DataSource tenantDataSource = null;
        try {
            String allTenantCodesQuery = "SELECT T.CODE AS Code FROM TENANT T ORDER BY Code";
            String tenantCodeQuery = "SELECT T.CODE AS Code FROM TENANT T WHERE T.CODE = '" + tenantCode + "'";
            con = ds.getConnection();
            logger.info("Creating prepared statement.");
            if (tenantCode == null) {
                prstTenant = con.prepareStatement(allTenantCodesQuery);
            } else {
                prstTenant = con.prepareStatement(tenantCodeQuery);
            }
            logger.info("Executing prepared statement.");
            rsTenant = prstTenant.executeQuery();
            while (rsTenant.next()) {
                tenantId = rsTenant.getString("Code");
                tenantConfigMap = getConfigForTenant(tenantId);
                logger.debug("tenantConfigMap: " + tenantConfigMap.toString());
                tenantMap.put(tenantId, tenantConfigMap);
            }
            logger.info("Prepared statement executed.");

            release(con, prstTenant, rsTenant);
            Iterator<String> it = tenantMap.keySet().iterator();

            while (it.hasNext()) {
                String tenantKey = it.next();
                tenantConfigMap = tenantMap.get(tenantKey);
                String driver = tenantConfigMap.get(SystemConstants.SYSTEM_KEY_DB_DRIVER);
                String url = tenantConfigMap.get(SystemConstants.SYSTEM_KEY_DB_URL);
                String schema = tenantConfigMap.get(SystemConstants.SYSTEM_KEY_DB_SCHEMA);
                String user = tenantConfigMap.get(SystemConstants.SYSTEM_KEY_DB_USER);
                String password = tenantConfigMap.get(SystemConstants.SYSTEM_KEY_DB_PASSWORD);
                long connectionTimeout = StringUtils
                        .isNotBlank(tenantConfigMap.get(SystemConstants.SYSTEM_KEY_CONNECTION_TIMEOUT))
                                ? Long.parseLong(tenantConfigMap.get(SystemConstants.SYSTEM_KEY_CONNECTION_TIMEOUT)) : 0;
                long maxConnectionAge = StringUtils.isNotBlank(tenantConfigMap.get(SystemConstants.SYSTEM_KEY_MAX_CONNECTION_AGE))
                        ? Long.parseLong(tenantConfigMap.get(SystemConstants.SYSTEM_KEY_MAX_CONNECTION_AGE)) : 0;
                final int maxIdleTime = StringUtils.isNotBlank(tenantConfigMap.get(SystemConstants.SYSTEM_KEY_MAX_IDLE_TIME))
                        ? Integer.parseInt(tenantConfigMap.get(SystemConstants.SYSTEM_KEY_MAX_IDLE_TIME)) : 0;
                boolean defaultAutoCommit = Boolean
                        .parseBoolean(tenantConfigMap.get(SystemConstants.SYSTEM_KEY_DEFAULT_AUTO_COMMIT));

                final int minPoolSize = StringUtils.isNotBlank(tenantConfigMap.get(SystemConstants.MIN_POOL_SIZE))
                        ? Integer.parseInt(tenantConfigMap.get(SystemConstants.MIN_POOL_SIZE)) : MIN_CONNECTIONS;

                final int maxPoolSize = StringUtils.isNotBlank(tenantConfigMap.get(SystemConstants.MAX_POOL_SIZE))
                        ? Integer.parseInt(tenantConfigMap.get(SystemConstants.MAX_POOL_SIZE)) : MAX_CONNECTIONS;

                tenantDataSource = createDataSource(driver, url, user, password, maxConnectionAge, connectionTimeout,
                        defaultAutoCommit, maxIdleTime, minPoolSize, maxPoolSize);
                targetDataSources.put(tenantKey, tenantDataSource);
            }
            if (getTargetDataSources() == null) {
                setTargetDataSources(targetDataSources);
            } else {
                getTargetDataSources().putAll(targetDataSources);
            }

            for (Map.Entry<Object, Object> entry : targetDataSources.entrySet()) {
                logger.info((String) entry.getKey());
                ComboPooledDataSource dsx = (ComboPooledDataSource) entry.getValue();
                logger.info(String.valueOf(dsx.getMaxConnectionAge()));
            }

            super.afterPropertiesSet();
            // afterPropertiesSet();
        } catch (Exception e) { // NOPMD
            logger.error("Exception occured creating datasource", e);
        } finally { 
            release(con, prstTenant, rsTenant);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param con
     *            DOCUMENT ME!
     * @param stmt
     *            DOCUMENT ME!
     * @param rs
     *            DOCUMENT ME!
     *
     * @throws SQLException
     *             DOCUMENT ME!
     **/
    public void release(Connection con, Statement stmt, ResultSet rs)  {
        try {
            if (rs != null && !rs.isClosed()) {
                logger.info("Relesing Result set");
                rs.close();
            }

            if (stmt != null && !stmt.isClosed()) {
                logger.info("Relesing Statement");
                stmt.close();
            }

            if (con != null && !con.isClosed()) {
                logger.info("Relesing Connction");
                con.close();
            }
        } catch (SQLException e) {
            logger.error("Error in creating multi tenant data sources", e); 
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param driver
     *            DOCUMENT ME!
     * @param dbURL
     *            DOCUMENT ME!
     * @param userName
     *            DOCUMENT ME!
     * @param pwd
     *            DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     **/
    private DataSource createDataSource(String driver, String dbURL, String userName, String pwd, long maxConnectionAge,
            long connectionTimeout, boolean defaultAutoCommit, final int maxIdleTime, final int minPoolSize,
            final int maxPoolSize) {
        /*
         * BoneCPDataSource boneCPDataSource = null; logger.info("Received request to create datasource connection {}, {}, {}",
         * driver, dbURL, userName); if ((driver != null) && (dbURL != null) && (userName != null) && (pwd != null)) {
         * logger.info("Creating connection for datasorce driver {}, {}, {}.", driver, dbURL, userName); boneCPDataSource = new
         * BoneCPDataSource(); boneCPDataSource.setDriverClass(driver); boneCPDataSource.setJdbcUrl(dbURL);
         * boneCPDataSource.setUsername(userName); boneCPDataSource.setPassword(pwd);
         * boneCPDataSource.setMaxConnectionAgeInSeconds(maxConnectionAge);
         * boneCPDataSource.setConnectionTimeout(connectionTimeout, TimeUnit.MILLISECONDS);
         * boneCPDataSource.setConnectionTestStatement("SELECT 1"); boneCPDataSource.setDefaultAutoCommit(defaultAutoCommit);
         * boneCPDataSource.setPoolName(UUID.randomUUID().toString()); }
         */

        ComboPooledDataSource dataSource = null;
        try {
            logger.info("Received request to create datasource connection {}, {}, {}, {}, {}, {}, {}, {}, {}", driver, dbURL,
                    userName, maxConnectionAge, connectionTimeout, defaultAutoCommit, maxIdleTime, minPoolSize, maxPoolSize);
            if ((driver != null) && (dbURL != null) && (userName != null) && (pwd != null)) {
                logger.info("Creating connection for datasorce driver {}, {}, {}.", driver, dbURL, userName);
                dataSource = new ComboPooledDataSource();
                try {
                    dataSource.setDriverClass(driver);
                } catch (PropertyVetoException e) {
                    throw new FatalBeanException(String.format("Error occurred while loading database driver "), e);
                }
                dataSource.setJdbcUrl(dbURL);
                dataSource.setUser(userName);
                dataSource.setPassword(pwd);
                dataSource.setMaxConnectionAge((int) maxConnectionAge);
                dataSource.setCheckoutTimeout((int) connectionTimeout);
                dataSource.setPreferredTestQuery("SELECT 1");
                dataSource.setAutoCommitOnClose(defaultAutoCommit);
                dataSource.setMinPoolSize(minPoolSize);
                dataSource.setMaxPoolSize(maxPoolSize);
                // boneCPDataSource.setPoolName(UUID.randomUUID().toString());
                dataSource.setMaxIdleTime(maxIdleTime);
            }
        } catch (Exception e) { // NOPMD
            logger.error("Exception occured while creating datasource", e);
        }
        logger.info("Datasource created");
        return dataSource;
    }

    /**
     * DOCUMENT ME!
     *
     * @param bean
     *            DOCUMENT ME!
     * @param beanName
     *            DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws BeansException
     *             DOCUMENT ME!
     * @throws FatalBeanException
     *             DOCUMENT ME!
     **/
    @Override
    public void afterPropertiesSet() {
        setFullyInitialized(true);
        try {
            createDataSourceForTenant();
            super.afterPropertiesSet();
        } catch (SQLException e) {
            throw new FatalBeanException("Error creating the TenantRoutingDataSource - This is fatal", e);
        }
    }

    
    public void provisionNewSchemaToTenant(String tenantCode) throws BeansException {
    	logger.info("Yet to be implemented !");   
    }
    /*@SuppressWarnings("PMD.CloseResource")
    public void provisionNewSchemaToTenant(String tenantCode) throws BeansException {
        logger.info("Creating new schema for tenant {}.", tenantCode);
        Map<String, String> configMap = null;
        PreparedStatement prstTenantConfig = null;
        DataSource dataSource = null;
        Connection connection = null;
        String schema = null;
        try {
            configMap = getConfigForTenant(tenantCode);
            schema = configMap.get(SystemConstants.SYSTEM_KEY_DB_SCHEMA);

            if (StringUtils.isNotBlank(schema)) {
                dataSource = getDefaultTargetDataSource();
                connection = dataSource.getConnection();

                // create new schema
                prstTenantConfig = connection.prepareStatement("create schema " + schema);
                prstTenantConfig.executeUpdate();

                // initialize tenant datasource
                DataSource tenantDataSource = initializeDatasourceForTenant(tenantCode);
                connection = tenantDataSource.getConnection();

                // load ddl files to create tables in newly created schema
                URL url = this.getClass().getClassLoader().getResource("sql/umg_tenant.sql");
                Reader reader = new BufferedReader(new FileReader(url.getFile()));
                ScriptRunner scriptRunner = new ScriptRunner(connection);
                scriptRunner.runScript(reader);
 
                logger.info("Created new schema {} for tenant {}.", schema, tenantCode);
            } else {
                throw new FatalBeanException(
                        String.format("Schema details not provided for provisioning new database to tenant", schema, tenantCode));
            }
        } catch (SQLException sqlexc) { 
            throw new FatalBeanException(
                    String.format("Error occurred while provisiong new database %s to tenant %s.", schema, tenantCode), sqlexc);
        } catch (FileNotFoundException e) { 
            throw new FatalBeanException(String.format("Error occurred while loading sql files for tenant %s.", tenantCode), e);
        }
        finally {
        	 release(connection, prstTenantConfig, null);
        }
    }*/

    /**
     * 
     * @param tenantCode
     * @param driver
     * @param dbURL
     * @param userName
     * @param pwd
     * @return
     * @throws SQLException
     */
    public DataSource initializeDatasourceForTenant(String tenantCode) throws BeansException {
        logger.info("Creating new datasource connection for tenant {}.", tenantCode);
        DataSource dataSource = null;
        Connection connection = null;
        String originalTenantCode = RequestContext.getRequestContext().getTenantCode();
        try {
            Map<String, String> configMap = getConfigForTenant(tenantCode);
            String url = configMap.get(SystemConstants.SYSTEM_KEY_DB_URL);
            String driver = configMap.get(SystemConstants.SYSTEM_KEY_DB_DRIVER);
            String user = configMap.get(SystemConstants.SYSTEM_KEY_DB_USER);
            String schema = configMap.get(SystemConstants.SYSTEM_KEY_DB_SCHEMA);
            String password = configMap.get(SystemConstants.SYSTEM_KEY_DB_PASSWORD);
            long connectionTimeout = StringUtils.isNotBlank(configMap.get(SystemConstants.SYSTEM_KEY_CONNECTION_TIMEOUT))
                    ? Long.parseLong(configMap.get(SystemConstants.SYSTEM_KEY_CONNECTION_TIMEOUT)) : 0;
            long maxConnectionAge = StringUtils.isNotBlank(configMap.get(SystemConstants.SYSTEM_KEY_MAX_CONNECTION_AGE))
                    ? Long.parseLong(configMap.get(SystemConstants.SYSTEM_KEY_MAX_CONNECTION_AGE)) : 0;
            boolean defaultAutoCommit = Boolean.parseBoolean(configMap.get(SystemConstants.SYSTEM_KEY_DEFAULT_AUTO_COMMIT));
            final int maxIdleTime = StringUtils.isNotBlank(configMap.get(SystemConstants.SYSTEM_KEY_MAX_IDLE_TIME))
                    ? Integer.parseInt(configMap.get(SystemConstants.SYSTEM_KEY_MAX_IDLE_TIME)) : 0;

            final int minPoolSize = StringUtils.isNotBlank(configMap.get(SystemConstants.MIN_POOL_SIZE))
                    ? Integer.parseInt(configMap.get(SystemConstants.MIN_POOL_SIZE)) : MIN_CONNECTIONS;

            final int maxPoolSize = StringUtils.isNotBlank(configMap.get(SystemConstants.MAX_POOL_SIZE))
                    ? Integer.parseInt(configMap.get(SystemConstants.MAX_POOL_SIZE)) : MAX_CONNECTIONS;

            dataSource = createDataSource(driver, url, user, password, maxConnectionAge, connectionTimeout, defaultAutoCommit,
                    maxIdleTime, minPoolSize, maxPoolSize);

            getTargetDataSources().put(tenantCode, dataSource);
            super.afterPropertiesSet();

            RequestContext.getRequestContext().setTenantCode(tenantCode);

            connection = dataSource.getConnection();
            connection.createStatement().execute("Select 1");

        } catch (SQLException sqlException) {
            throw new FatalBeanException(String.format("Error occurred while initializing datasource for tenant %s.", tenantCode),
                    sqlException);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error("");
                }
            }

            RequestContext.getRequestContext().setTenantCode(originalTenantCode);

        }
        logger.info("Created new datasource connection for tenant {}.", tenantCode);
        return dataSource;
    }

    @SuppressWarnings("PMD.CloseResource")
    private Map<String, String> getConfigForTenant(String tenantCode) throws SQLException {
        logger.info("Fetching tenant configuration for tenant {}.", tenantCode); 
        Map<String, String> configMap = new HashMap<String, String>();
        PreparedStatement prstTenantConfig = null;
        ResultSet rsTenantConfig = null;
        Connection con=null;
        try {
        DataSource dataSource = getDefaultTargetDataSource();
        con = dataSource.getConnection();

        prstTenantConfig = con.prepareStatement(
                "SELECT T.CODE AS CODE,SK.SYSTEM_KEY AS KEYY ,SK.KEY_TYPE AS TYPE ,TC.CONFIG_VALUE AS VALUE FROM TENANT T, TENANT_CONFIG TC, SYSTEM_KEY SK WHERE T.ID = TC.TENANT_ID AND TC.SYSTEM_KEY_ID = SK.ID AND T.CODE = ? AND SK.KEY_TYPE='DATABASE'");

        prstTenantConfig.setString(1, tenantCode);

        rsTenantConfig = prstTenantConfig.executeQuery();

        while (rsTenantConfig.next()) {
            String key = rsTenantConfig.getString("KEYY");
            String value = rsTenantConfig.getString("VALUE");
            configMap.put(key, value);
        } 
        logger.info("Found {} configurations for tenant.", configMap == null ? 0 : configMap.size(), tenantCode);
        }catch(SQLException e) {
        	throw e;
        }
        finally {
        	  release(con, prstTenantConfig, rsTenantConfig);
        }
        return configMap;
    }

    /**
     * Retrieves the datasource for current tenant.
     * 
     * @return
     */
    public DataSource getTenantDataSource() {
        return determineTargetDataSource();
    }
}
