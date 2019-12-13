/*
 * SyndicateVersionDataDAOTest.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics 
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.business.syndicatedata.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.syndicatedata.daohelper.SyndicateDataQueryHelper;
import com.ca.umg.business.syndicatedata.entity.SyndicateDataQuery;
import com.ca.umg.business.syndicatedata.entity.SyndicateDataQueryInput;
import com.ca.umg.business.syndicatedata.entity.SyndicateDataQueryObject;
import com.ca.umg.business.syndicatedata.entity.SyndicateDataQueryOutput;
import com.ca.umg.business.syndicatedata.info.SyndicateDataColumnInfo;

/**
 * 
 * @author Venkat R.
 * 
 */
@ContextHierarchy({ @ContextConfiguration("classpath:root-db-context.xml"), @ContextConfiguration })
@RunWith(SpringJUnit4ClassRunner.class)
public class SyndicateVersionDataDAOTest {

    @Inject
    private SyndicateVersionDataDAO syndicateVersionDataDAO;

    @Inject
    private SyndicateDataQueryHelper sQueryHelper;

    /**
     * Test to check the column info of the created table.
     */
    @Test
    public void createTableTest() {
        syndicateVersionDataDAO
                .executeQuery("CREATE TABLE USER(USER_ID INTEGER NOT NULL,LOGIN_ID VARCHAR(128) NOT NULL,USER_NAME VARCHAR(254) DEFAULT ' ' NOT NULL,SAL DECIMAL(6,2),CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,UPDATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,LAST_ACCESS_DATE TIMESTAMP,CONSTRAINT IXUQ_LOGIN_ID5 UNIQUE(LOGIN_ID))");
        List<SyndicateDataColumnInfo> syndicateDataColumnInfos = null;
        try {
            syndicateDataColumnInfos = syndicateVersionDataDAO.getTableColumnInfo("USER");
        } catch (SystemException e) {
            Assert.fail();
        }
        Assert.assertNotNull(syndicateDataColumnInfos);
        Assert.assertTrue(CollectionUtils.isNotEmpty(syndicateDataColumnInfos));
        syndicateVersionDataDAO.executeQuery("DROP TABLE USER");
    }

    /**
     * Test to check the indexes created on table.
     */
    @Test
    public void createIndexTest() {
        syndicateVersionDataDAO
                .executeQuery("CREATE TABLE USER(USER_ID INTEGER NOT NULL,LOGIN_ID VARCHAR(128) NOT NULL,USER_NAME VARCHAR(254) DEFAULT ' ' NOT NULL,CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,UPDATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,LAST_ACCESS_DATE TIMESTAMP,CONSTRAINT IXUQ_LOGIN_ID6 UNIQUE(LOGIN_ID))");
        Map<String, List<String>> tableKeys = null;
        try {
            tableKeys = syndicateVersionDataDAO.getTableKeys("USER");
        } catch (SystemException e) {
            Assert.fail();
        }
        Assert.assertNotNull(tableKeys);
        Assert.assertTrue(MapUtils.isNotEmpty(tableKeys));
        syndicateVersionDataDAO.executeQuery("DROP TABLE USER");
    }

    @Test
    public void fetchSyndDataQueryOutputsTest() {

        syndicateVersionDataDAO
                .executeQuery("CREATE TABLE USER(SYNDICATEDVERID BIGINT, USER_ID INTEGER NOT NULL,LOGIN_ID VARCHAR(128) NOT NULL,"
                        + "USER_NAME VARCHAR(254) DEFAULT ' ' NOT NULL, active TINYINT, sal DOUBLE,dob DATE,"
                        + "CREATE_DATE DATE DEFAULT CURRENT_TIMESTAMP NOT NULL,UPDATE_DATE DATE DEFAULT CURRENT_TIMESTAMP NOT NULL,LAST_ACCESS_DATE DATE,"
                        + "CONSTRAINT IXUQ_LOGIN_ID7 UNIQUE(LOGIN_ID))");
        String insertValues = "INSERT INTO USER(SYNDICATEDVERID,USER_ID,LOGIN_ID,USER_NAME,sal,dob,CREATE_DATE,UPDATE_DATE,LAST_ACCESS_DATE) "
                + "VALUES(1,1,'mandavak','mandavak',56.23,'1984-01-31','2012-01-31','2013-01-31','2014-01-31')";
        syndicateVersionDataDAO.executeQuery(insertValues);
        syndicateVersionDataDAO
                .executeQuery("INSERT INTO SYNDICATED_DATA VALUES ('01012b5a-1320-11e4-bba0-b2227cce2b54', 'USER', 'USER', 1, 'VER_USER', 'USER_DES', 'USER', "
                        + "1311464000, 1756191174,'SYSTEM', 1406199094, null, null)");

        SyndicateDataQuery synDataQuery = new SyndicateDataQuery();
        synDataQuery.setName("TestName");
        synDataQuery.setDescription("TestDesc");
        SyndicateDataQueryObject queryObject = new SyndicateDataQueryObject();
        queryObject.setSelectString("USER_ID AS USERID, LOGIN_ID AS LOGINID,active AS STATUS, sal AS SALARY, dob as DOB");
        queryObject.setFromString("USER USR");
        queryObject.setWhereClause("USR.USER_ID = #USERID#");
        synDataQuery.setQueryObject(queryObject);
        Set<SyndicateDataQueryInput> inputParameters = new HashSet<SyndicateDataQueryInput>();
        SyndicateDataQueryInput inputParam = new SyndicateDataQueryInput();
        inputParam.setName("USERID");
        inputParam.setDataType("INTEGER");
        inputParam.setSampleValue("1");
        inputParameters.add(inputParam);

        inputParam = new SyndicateDataQueryInput();
        inputParam.setName("TESTDATE");
        inputParam.setDataType("DATE");
        inputParam.setSampleValue("JUL-30-2011");
        inputParameters.add(inputParam);

        synDataQuery.setInputParameters(inputParameters);

        SyndicateDataQuery syndicateDataQuery = new SyndicateDataQuery();
        Set<SyndicateDataQueryOutput> outputParameters = new HashSet<>();
        SyndicateDataQueryOutput syndicateDataQueryOutput = new SyndicateDataQueryOutput();
        syndicateDataQueryOutput.setDataType("INTEGER");
        syndicateDataQueryOutput.setName("parameter1");
        syndicateDataQueryOutput.setQuery(syndicateDataQuery);
        syndicateDataQueryOutput.setSequence(1);
        syndicateDataQueryOutput.setTenantId("ocwin");
        outputParameters.add(syndicateDataQueryOutput);

        syndicateDataQueryOutput = new SyndicateDataQueryOutput();
        syndicateDataQueryOutput.setDataType("BOOLEAN");
        syndicateDataQueryOutput.setName("parameter2");
        syndicateDataQueryOutput.setQuery(syndicateDataQuery);
        syndicateDataQueryOutput.setSequence(1);
        syndicateDataQueryOutput.setTenantId("ocwin");
        outputParameters.add(syndicateDataQueryOutput);

        syndicateDataQueryOutput = new SyndicateDataQueryOutput();
        syndicateDataQueryOutput.setDataType("DATE");
        syndicateDataQueryOutput.setName("parameter3");
        syndicateDataQueryOutput.setQuery(syndicateDataQuery);
        syndicateDataQueryOutput.setSequence(1);
        syndicateDataQueryOutput.setTenantId("ocwin");
        outputParameters.add(syndicateDataQueryOutput);

        syndicateDataQueryOutput = new SyndicateDataQueryOutput();
        syndicateDataQueryOutput.setDataType("DOUBLE");
        syndicateDataQueryOutput.setName("parameter4");
        syndicateDataQueryOutput.setQuery(syndicateDataQuery);
        syndicateDataQueryOutput.setSequence(1);
        syndicateDataQueryOutput.setTenantId("ocwin");
        outputParameters.add(syndicateDataQueryOutput);

        syndicateDataQueryOutput = new SyndicateDataQueryOutput();
        syndicateDataQueryOutput.setDataType("DECIMAL");
        syndicateDataQueryOutput.setName("parameter5");
        syndicateDataQueryOutput.setQuery(syndicateDataQuery);
        syndicateDataQueryOutput.setSequence(1);
        syndicateDataQueryOutput.setTenantId("ocwin");
        outputParameters.add(syndicateDataQueryOutput);
        synDataQuery.setOutputParameters(outputParameters);

        String executableQuery = getExecutableQuery(synDataQuery);
        synDataQuery.getQueryObject().setExecutableQuery(executableQuery);
        List<Map<String, Object>> queryOutput = null;
        try {
            MapSqlParameterSource mSource = sQueryHelper.inputParameters(synDataQuery);
            queryOutput = syndicateVersionDataDAO.fetchSyndDataQueryOutputs(synDataQuery, mSource);
        } catch (SystemException e) {
            e.printStackTrace();
            Assert.fail();
        } finally {
            syndicateVersionDataDAO.executeQuery("DROP TABLE USER");
        }
        Assert.assertNotNull(queryOutput);
    }

    private String getExecutableQuery(SyndicateDataQuery synDataQuery) {
        Map<String, String> aliases = sQueryHelper.fetchTableAliases(synDataQuery.getQueryObject().getFromString());
        String filterQuery = sQueryHelper.createFilterQuery(aliases, SyndicateDataQueryHelper.DB_TYPE.HSQL);
        return sQueryHelper.generateExecutableQuery(synDataQuery, filterQuery, SyndicateDataQueryHelper.DB_TYPE.HSQL);
    }

}
