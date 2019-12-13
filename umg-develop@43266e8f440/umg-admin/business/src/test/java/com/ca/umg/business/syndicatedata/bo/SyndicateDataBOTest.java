/*
 * SyndicateDataBOTest.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics 
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.business.syndicatedata.bo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.BaseTest;
import com.ca.umg.business.common.info.SearchOptions;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.syndicatedata.dao.SyndicateVersionDataDAO;
import com.ca.umg.business.syndicatedata.entity.SyndicateData;
import com.ca.umg.business.syndicatedata.info.SyndicateDataColumnInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataKeyColumnInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataKeyInfo;
import com.ca.umg.business.syndicatedata.util.DataTypes;

import junit.framework.Assert;

/**
 * 
 * SyndicateDataBO test cases
 * 
 * @author mandavak
 *
 */

@ContextHierarchy({ @ContextConfiguration("classpath:root-db-context.xml"), @ContextConfiguration })
@RunWith(SpringJUnit4ClassRunner.class)
public class SyndicateDataBOTest extends BaseTest {

    @Inject
    SyndicateDataBO syndicateDataBO;

    @Inject
    SyndicateVersionDataDAO syndicateVersionDataDAO;

    /**
     * Destroy method to call after test case execution.
     */
    @After
    public void destroy() {
        getSyndicateDataDAO().deleteAllInBatch();
    }

    /**
     * Set up data before creating db.
     */
    @Before
    public void setup() {
        createSyndicateData("REO", "REO1", 250L, "SYND_DATA_REO", "version1", "version1 desc", 1388514600l, 1391106600l);
        createSyndicateData("REO", "REO2", 251L, "SYND_DATA_REO", "version2", "version2 desc", 1391193000l, 1393525800l);
        createSyndicateData("REO", "REO3", 252L, "SYND_DATA_REO", "version3", "version3 desc", 1393612200l, 1396204200l);
        createSyndicateData("REO", "REO4", 253L, "SYND_DATA_REO", "version4", "version4 desc", 1396290600l, null);
    }

    /**
     * findSyndicateContainerVersions method test case
     */
    @Test
    public void findSyndicateContainerVersionsTest() {
        List<SyndicateData> syndicateDataList = null;
        try {
            syndicateDataList = syndicateDataBO.findSyndicateContainerVersions("REO");
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }
        Assert.assertNotNull(syndicateDataList);
        Assert.assertEquals(syndicateDataList.size(), 4);
        SyndicateData syndicateData = syndicateDataList.get(0);
        Assert.assertNotNull(syndicateData);
        Assert.assertEquals(syndicateData.getVersionId(), new Long(253L));
        try {
            syndicateDataList = syndicateDataBO.findSyndicateContainerVersions("");
        } catch (BusinessException | SystemException exp) {
            assertTrue(exp instanceof BusinessException);
            assertEquals(BusinessExceptionCodes.BSE000012, exp.getCode());
        }
    }

    /**
     * findSyndicateDataContainer method test case
     */
    @Test
    public void findSyndicateDataContainerTest() {
        SyndicateData syndicateData = null;
        try {
            syndicateData = syndicateDataBO.findSyndicateDataContainer("REO");
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }
        Assert.assertNotNull(syndicateData);
        Assert.assertEquals(syndicateData.getVersionId(), new Long(250L));

        try {
            syndicateData = syndicateDataBO.findSyndicateDataContainer("NotExist");
        } catch (BusinessException | SystemException exp) {
            assertTrue(exp instanceof BusinessException);
            assertEquals(BusinessExceptionCodes.BSE000018, exp.getCode());
        }
        try {
            syndicateData = syndicateDataBO.findSyndicateDataContainer(null);
        } catch (BusinessException | SystemException exp) {
            assertTrue(exp instanceof BusinessException);
            assertEquals(BusinessExceptionCodes.BSE000012, exp.getCode());
        }
    }

    /**
     * findSyndicateDataByVersionId method test case
     */
    @Test
    public void findSyndicateDataByVersionIdTest() {
        SyndicateData syndicateData = null;
        try {
            syndicateData = syndicateDataBO.findSyndicateDataByVersionId(250L, "REO");
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }
        Assert.assertNotNull(syndicateData);
        Assert.assertEquals(syndicateData.getValidFrom(), new Long(1388514600));
        Assert.assertFalse(syndicateData.getValidTo().equals(new Long("1393525800")));

        try {
            syndicateData = syndicateDataBO.findSyndicateDataByVersionId(0L, null);
        } catch (BusinessException | SystemException exp) {
            assertTrue(exp instanceof BusinessException);
            assertEquals(BusinessExceptionCodes.BSE000014, exp.getCode());
        }
    }

    /**
     * getTableColumnInfo method test case
     */
    @Test
    public void getContainersTest() {
        List<SyndicateData> syndicateDatas = null;
        try {
            syndicateDatas = syndicateDataBO.getContainers();
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }
        Assert.assertNotNull(syndicateDatas);
        Assert.assertEquals(syndicateDatas.size(), 1);

        SyndicateData syndicateData = syndicateDatas.get(0);
        Assert.assertNotNull(syndicateData);
        Assert.assertEquals(syndicateData.getContainerName(), "REO");
        Assert.assertFalse(syndicateData.getVersionId().equals(new Long(253L)));

        try {
            getSyndicateDataDAO().deleteAll();
            syndicateDatas = syndicateDataBO.getContainers();
        } catch (BusinessException | SystemException exp) {
            assertTrue(exp instanceof BusinessException);
            assertEquals(BusinessExceptionCodes.BSE000018, exp.getCode());
        }
    }
    
    @Test
    public void getFilteredContainersTest() {
    	SearchOptions searchOptions = buildSearchOptions(1, 5, "containerName", false);
    	for(int i = 0 ; i < 10; i++)
    		createSyndicateData("Container"+i, "Container Desc "+i, 1L, "Table"+i, "Version"+i, "Version Desc "+i, 140000L, 180000L);
    	try {
    		Page<SyndicateData> page = syndicateDataBO.getContainers(searchOptions);
    		Assert.assertNotNull(page);
    		Assert.assertEquals(page.getContent().size(), 5);
    		Assert.assertEquals(page.getTotalElements(), 14);
    		Assert.assertEquals(page.getTotalPages(), 3);
    	 } catch (BusinessException | SystemException e) {
             Assert.fail();
         }
    }

    /**
     * getTableColumnInfo method test case
     */
    @Test
    public void getTableKeysTest() {
        syndicateVersionDataDAO
                .executeQuery("CREATE TABLE USER(USER_ID INTEGER NOT NULL,LOGIN_ID VARCHAR(128) NOT NULL,USER_NAME VARCHAR(254) DEFAULT ' ' NOT NULL,CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,UPDATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,LAST_ACCESS_DATE TIMESTAMP,CONSTRAINT IXUQ_LOGIN_ID1 UNIQUE(LOGIN_ID))");
        Map<String, List<String>> tableKeys = null;
        try {
            tableKeys = syndicateDataBO.getTableKeys("USER");
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }
        Assert.assertNotNull(tableKeys);
        Assert.assertTrue(MapUtils.isNotEmpty(tableKeys));
        syndicateVersionDataDAO.executeQuery("DROP TABLE USER");

        try {
            getSyndicateDataDAO().deleteAll();
            tableKeys = syndicateDataBO.getTableKeys("");
        } catch (BusinessException | SystemException exp) {
            assertTrue(exp instanceof BusinessException);
            assertEquals(BusinessExceptionCodes.BSE000019, exp.getCode());
        }
    }

    /**
     * getTableColumnInfo method test case
     */
    @Test
    public void getTableColumnInfoTest() {

        syndicateVersionDataDAO
                .executeQuery("CREATE TABLE USER(USER_ID INTEGER NOT NULL,LOGIN_ID VARCHAR(128) NOT NULL,USER_NAME VARCHAR(254) DEFAULT ' ' NOT NULL,CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,UPDATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,LAST_ACCESS_DATE TIMESTAMP,CONSTRAINT IXUQ_LOGIN_ID2 UNIQUE(LOGIN_ID))");
        List<SyndicateDataColumnInfo> syndicateDataColumnInfos = null;
        try {
            syndicateDataColumnInfos = syndicateDataBO.getTableColumnInfo("USER");
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }
        Assert.assertNotNull(syndicateDataColumnInfos);
        Assert.assertTrue(CollectionUtils.isNotEmpty(syndicateDataColumnInfos));
        syndicateVersionDataDAO.executeQuery("DROP TABLE USER");
        try {
            getSyndicateDataDAO().deleteAll();
            syndicateDataColumnInfos = syndicateDataBO.getTableColumnInfo("");
        } catch (BusinessException | SystemException exp) {
            assertTrue(exp instanceof BusinessException);
            assertEquals(BusinessExceptionCodes.BSE000019, exp.getCode());
        }
    }

    @Test
    public void createSyndicateDataKeyDefsTest() {
        syndicateVersionDataDAO.executeQuery("CREATE TABLE SYND_DATA_CONT(COL1 INTEGER, COL2 INTEGER);");
        List<SyndicateDataKeyInfo> dataKeyInfos = new ArrayList<>();
        SyndicateDataKeyInfo dataKeyInfo = new SyndicateDataKeyInfo();
        dataKeyInfo.setKeyName("KEY1");
        List<SyndicateDataKeyColumnInfo> columnInfos = new ArrayList<>();
        SyndicateDataKeyColumnInfo columnInfo = new SyndicateDataKeyColumnInfo();
        columnInfo.setColumnName("COL1");
        columnInfo.setStatus(true);
        columnInfos.add(columnInfo);
        columnInfo = new SyndicateDataKeyColumnInfo();
        columnInfo.setColumnName("COL2");
        columnInfo.setStatus(true);
        columnInfos.add(columnInfo);
        dataKeyInfo.setsColumnInfos(columnInfos);
        dataKeyInfos.add(dataKeyInfo);

        try {
            syndicateDataBO.createSyndicateDataKeyDefs("SYND_DATA_CONT", dataKeyInfos);
            System.out.println(syndicateVersionDataDAO.getTableKeys("SYND_DATA_CONT"));
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }
        try {
            syndicateDataBO.createSyndicateDataKeyDefs("SYND_DATA_CONT", null);
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }
    }

    /*
     * @Test public void deleteSyndicateDatakeyDefsTest(){
     * syndicateVersionDataDAO.executeQuery("CREATE TABLE TEMP_KEYS (COL1 INTEGER, COL2 INTEGER);");
     * syndicateVersionDataDAO.executeQuery("CREATE UNIQUE INDEX 'testIndex' ON TEMP_KEYS (COL1);"); try { Map<String,
     * List<String>> keysMap = syndicateDataBO.getTableKeys("testIndex"); syndicateDataBO.deleteSyndicateDatakeyDefs("TEMP_KEYS",
     * keysMap.keySet()); System.out.println(syndicateVersionDataDAO.getTableKeys("TEMP_KEYS")); } catch (BusinessException |
     * SystemException e) { Assert.fail(); } }
     */

    @Test
    public void dataTypesTest() {
        DataTypes dataType = DataTypes.valueOf("INTEGER");
        Assert.assertNotNull(dataType);
        dataType = DataTypes.valueOf("STRING");
        Assert.assertNotNull(dataType);
        dataType = DataTypes.valueOf("DOUBLE");
        Assert.assertNotNull(dataType);
        dataType = DataTypes.valueOf("DATE");
        Assert.assertNotNull(dataType);
    }

    @Test
    public void createSyndicateDataTableTest() {
        List<SyndicateDataColumnInfo> syndicateDataColumnInfos = new ArrayList<>();

        SyndicateDataColumnInfo dataColumnInfo1 = new SyndicateDataColumnInfo();
        dataColumnInfo1.setField("COL1");
        dataColumnInfo1.setDisplayName("COL1");
        dataColumnInfo1.setDescription("");
        dataColumnInfo1.setColumnSize(5);
        dataColumnInfo1.setColumnType("String");
        dataColumnInfo1.setIndex(1);
        dataColumnInfo1.setMandatory(false);
        syndicateDataColumnInfos.add(dataColumnInfo1);

        SyndicateDataColumnInfo dataColumnInfo2 = new SyndicateDataColumnInfo();
        dataColumnInfo2.setField("COL2");
        dataColumnInfo2.setDisplayName("COL2");
        dataColumnInfo2.setDescription("");
        dataColumnInfo2.setColumnSize(5);
        dataColumnInfo2.setColumnType("NUMBER");
        dataColumnInfo2.setIndex(2);
        dataColumnInfo2.setMandatory(false);
        syndicateDataColumnInfos.add(dataColumnInfo2);

        SyndicateDataColumnInfo dataColumnInfo3 = new SyndicateDataColumnInfo();
        dataColumnInfo3.setField("COL3");
        dataColumnInfo3.setDisplayName("COL3");
        dataColumnInfo3.setDescription("");
        dataColumnInfo3.setColumnSize(5);
        dataColumnInfo3.setPrecision(2);
        dataColumnInfo3.setColumnType("DOUBLE");
        dataColumnInfo3.setIndex(2);
        dataColumnInfo3.setMandatory(false);
        syndicateDataColumnInfos.add(dataColumnInfo3);

        syndicateDataBO.createSyndicateDataTable("TEST_CONTAINER", syndicateDataColumnInfos);
    }

    @Test
    public void dropSyndicateDataContainerTest() {
        syndicateVersionDataDAO
                .executeQuery("CREATE TABLE SYND_DATA_USER(USER_ID INTEGER NOT NULL,LOGIN_ID VARCHAR(128) NOT NULL,USER_NAME VARCHAR(254) DEFAULT ' ' NOT NULL,CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,UPDATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,LAST_ACCESS_DATE TIMESTAMP,CONSTRAINT IXUQ_LOGIN_ID3 UNIQUE(LOGIN_ID))");
        try {
            syndicateDataBO.dropSyndicateDataContainer("USER");
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }
    }

    @Test
    public void testGetSyndicateTableData() {
        syndicateVersionDataDAO
                .executeQuery("CREATE TABLE SYND_DATA_REO(SYNDICATEDVERID BIGINT,USER_ID INTEGER NOT NULL,LOGIN_ID VARCHAR(128) NOT NULL,USER_NAME VARCHAR(254) DEFAULT ' ' NOT NULL,CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,UPDATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,LAST_ACCESS_DATE TIMESTAMP,CONSTRAINT IXUQ_LOGIN_ID4 UNIQUE(LOGIN_ID))");
        syndicateVersionDataDAO
                .executeQuery("INSERT INTO SYND_DATA_REO(SYNDICATEDVERID,USER_ID,LOGIN_ID,USER_NAME) VALUES(250,'1','xyz','abc')");
        syndicateVersionDataDAO
                .executeQuery("INSERT INTO SYND_DATA_REO(SYNDICATEDVERID,USER_ID,LOGIN_ID,USER_NAME) VALUES(250,'2','xyz,abc','abcd')");
        try {
            List<String> str = syndicateDataBO.getSyndicateTableData("REO", 250L);
            Assert.assertEquals(str.size(), 3);

        } catch (BusinessException | SystemException e) {
            Assert.fail();
        } finally {
            syndicateVersionDataDAO.executeQuery("drop TABLE SYND_DATA_REO");
        }
    }
    
    @Test
    public void testGetSyndicateContainerDefinition() {
    	// INTEGER - not used as hsql doesn't have int but integer. This datatype is not included for this test as it fails during datatype resolution in SyndicateDataUtil.getUIDataType() which returns null for INTEGER.
    	syndicateVersionDataDAO
        .executeQuery("CREATE TABLE SYND_DATA_REO(SYNDICATEDVERID BIGINT, LOGIN_ID VARCHAR(128) NOT NULL,USER_NAME VARCHAR(254) DEFAULT ' ' NOT NULL, USER_SALARY DECIMAL(10,2) NULL, CONSTRAINT IXUQ_LOGIN_ID4 UNIQUE(LOGIN_ID))");
    	try {
			String definitionStr = syndicateDataBO.getSyndicateContainerDefinition("REO");
			String expectedCSV = "LOGIN_ID,USER_NAME,USER_SALARY\n\"null\",\"null\",\"null\"\nSTRING,STRING,DOUBLE\n128,254,8|2\nN,N,Y";
			// This was done as the index name was generated dynamically from HSQL.
			Assert.assertTrue(definitionStr.contains(expectedCSV));
			Assert.assertTrue(definitionStr.contains("IXUQ_LOGIN_ID4"));
		} catch (BusinessException | SystemException e) {
			Assert.fail();
		} finally {
            syndicateVersionDataDAO.executeQuery("drop TABLE SYND_DATA_REO");
        }
    }
}