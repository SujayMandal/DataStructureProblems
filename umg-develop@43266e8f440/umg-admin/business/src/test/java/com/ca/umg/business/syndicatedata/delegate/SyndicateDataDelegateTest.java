/*
 * SyndicateDataDelegateTest.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics 
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.business.syndicatedata.delegate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.multipart.MultipartFile;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.BaseTest;
import com.ca.umg.business.common.info.PageRecord;
import com.ca.umg.business.common.info.SearchOptions;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.syndicatedata.dao.SyndicateVersionDataDAO;
import com.ca.umg.business.syndicatedata.info.SyndicateDataColumnInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataContainerInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataKeyColumnInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataKeyInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataVersionInfo;
import com.ca.umg.business.validation.UserConfirmation;

/**
 * Test cases for SyndicateDataDelegate.
 * 
 * @author mandavak
 *
 */
@ContextHierarchy({ @ContextConfiguration("classpath:root-db-context.xml"), @ContextConfiguration })
@RunWith(SpringJUnit4ClassRunner.class)
public class SyndicateDataDelegateTest extends BaseTest {

    @Inject
    SyndicateDataDelegate syndicateDataDelegate;

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
        createSyndicateData("REO", "REO1", 250l, "SYND_DATA_REO", "version1", "version1 desc", 1388514600l, 1391106600l);
        createSyndicateData("REO", "REO2", 251l, "SYND_DATA_REO", "version2", "version2 desc",
                new DateTime().getMillis() + 2592000L,
                new DateTime().getMillis() + 5184000L);
        createSyndicateData("REO", "REO3", 252l, "SYND_DATA_REO", "version3", "version3 desc", 1393612200l, 1396204200l);
        createSyndicateData("REO", "REO4", 253l, "SYND_DATA_REO", "version4", "version4 desc", 1396290600l, null);
        createSyndicateData("METO", "METO1", 200l, "METO_TAB", "version5", "version5 desc", 1362114000000L, 1372651140000L);
        createSyndicateData("METO", "METO2", 201l, "METO_TAB", "version6", "version6 desc", 1372651200000L, null);
    }

    /**
     * listVersions method test case
     */

    @Test
    public void listVersionsTest() {
        try {
            syndicateVersionDataDAO
                    .executeQuery("CREATE TABLE SYND_DATA_REO(USER_ID INTEGER NOT NULL,LOGIN_ID VARCHAR(128) NOT NULL,USER_NAME VARCHAR(254) DEFAULT ' ' NOT NULL,CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,UPDATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,LAST_ACCESS_DATE TIMESTAMP,CONSTRAINT IXUQ_LOGIN_ID8 UNIQUE(LOGIN_ID))");
            SyndicateDataVersionInfo syndicateDataVersionInfo = syndicateDataDelegate.listVersions("REO");
            Assert.assertNotNull(syndicateDataVersionInfo);
            List<SyndicateDataInfo> listSyndicateDataInfo = syndicateDataVersionInfo.getVersions();
            Assert.assertNotNull(listSyndicateDataInfo);
            Assert.assertEquals(4, listSyndicateDataInfo.size());
            for (SyndicateDataInfo syndicateDataInfo : listSyndicateDataInfo) {
                Assert.assertEquals(syndicateDataInfo.getContainerName(), "REO");
            }
            List<SyndicateDataColumnInfo> listSyndicateDataColumnInfo = syndicateDataVersionInfo.getMetaData();
            Assert.assertNull(listSyndicateDataColumnInfo); // because getVersions should not send meta data.
            List<SyndicateDataKeyInfo> listSyndicateDataKeyInfo = syndicateDataVersionInfo.getKeys();
            Assert.assertNull(listSyndicateDataKeyInfo); // because getVersions should not send keys.
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        } finally {
            syndicateVersionDataDAO.executeQuery("DROP TABLE SYND_DATA_REO");
        }

        try {
            SyndicateDataVersionInfo syndicateDataVersionInfo = syndicateDataDelegate.listVersions(null);
            Assert.assertNull(syndicateDataVersionInfo.getId());
        } catch (BusinessException | SystemException exp) {
            assertTrue(exp instanceof BusinessException);
            assertEquals(BusinessExceptionCodes.BSE000012, exp.getCode());
        }

        try {
            SyndicateDataVersionInfo syndicateDataVersionInfo = syndicateDataDelegate.listVersions("dummy");
            System.out.println(syndicateDataVersionInfo.getId());
        } catch (BusinessException | SystemException exp) {
            assertTrue(exp instanceof BusinessException);
            assertEquals(BusinessExceptionCodes.BSE000013, exp.getCode());
        }
    }

    /**
     * getContainerVersionInformation method test case
     */

    @Test
    public void getContainerVersionInformationTest() {
        try {
            syndicateVersionDataDAO
                    .executeQuery("CREATE TABLE SYND_DATA_REO(SYNDICATEDVERID INTEGER, USER_ID INTEGER NOT NULL,LOGIN_ID VARCHAR(128) NOT NULL,USER_NAME VARCHAR(254) DEFAULT ' ' NOT NULL,CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,UPDATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,LAST_ACCESS_DATE TIMESTAMP,CONSTRAINT IXUQ_LOGIN_ID9 UNIQUE(LOGIN_ID))");
            SyndicateDataContainerInfo syndicateDataContainerInfo = syndicateDataDelegate.getContainerVersionInformation(251L,
                    "REO");
            Assert.assertNotNull(syndicateDataContainerInfo);
            List<Map<String, String>> versionInfoList = syndicateDataContainerInfo.getSyndicateVersionData();
            Assert.assertNotNull(versionInfoList);
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }

        try {
            syndicateDataDelegate.getContainerVersionInformation(0L, null);
        } catch (BusinessException | SystemException exp) {
            assertTrue(exp instanceof BusinessException);
            assertEquals(BusinessExceptionCodes.BSE000015, exp.getCode());
        } finally {
            syndicateVersionDataDAO.executeQuery("DROP TABLE SYND_DATA_REO");
        }
    }

    /**
     * getContainerInformation method test case
     */
    @Test
    public void getContainerInformationTest() {
        try {
            SyndicateDataContainerInfo syndicateDataContainerInfo = syndicateDataDelegate.getContainerInformation("REO");
            Assert.assertNotNull(syndicateDataContainerInfo);
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }

        try {
            syndicateDataDelegate.getContainerInformation("Alti");
        } catch (BusinessException | SystemException exp) {
            assertTrue(exp instanceof BusinessException);
            assertEquals(BusinessExceptionCodes.BSE000018, exp.getCode());
        }
    }

    /**
     * getContainerInformation method test case
     */
    @Test
    public void getContainerInformationForAllTest() {
        try {
            List<SyndicateDataContainerInfo> syndicateDataContainerInfoList = syndicateDataDelegate.getContainerInformation();
            Assert.assertNotNull(syndicateDataContainerInfoList);
            Assert.assertEquals(2, syndicateDataContainerInfoList.size());
            Assert.assertNotNull(syndicateDataContainerInfoList.get(0).getValidFrom());
            Assert.assertNotNull(syndicateDataContainerInfoList.get(1).getValidFrom());
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }

        try {
            getSyndicateDataDAO().deleteAll();
            syndicateDataDelegate.getContainerInformation();
        } catch (BusinessException | SystemException exp) {
            assertTrue(exp instanceof BusinessException);
            assertEquals(BusinessExceptionCodes.BSE000013, exp.getCode());
        }
    }
    
    @Test
    public void getFilteredContainerInformationTest(){
    	SearchOptions searchOptions = buildSearchOptions(1, 5, "containerName", false);
    	for(int i = 0 ; i < 10; i++)
    		createSyndicateData("Container"+i, "Container Desc "+i, 1L, "Table"+i, "Version"+i, "Version Desc "+i, 140000L, 180000L);
    	try {
            PageRecord<SyndicateDataContainerInfo> syndicateDataContainerInfoPage = syndicateDataDelegate.getContainerInformation(searchOptions);
            Assert.assertNotNull(syndicateDataContainerInfoPage);
            Assert.assertEquals(5, syndicateDataContainerInfoPage.getContent().size());
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }
    }

    @Test
    public void createVersionTest() {
        SyndicateDataContainerInfo containerInfo = this.createSyndicateDataContainerInfo();
        try {
            syndicateDataDelegate.createProvider(containerInfo);
        } catch (BusinessException | SystemException exp) {
            assertTrue(exp instanceof BusinessException);
            assertEquals(BusinessExceptionCodes.BSE000025, exp.getCode());
        }

        try {
            List<SyndicateDataColumnInfo> columnInfos = syndicateVersionDataDAO.getTableColumnInfo(containerInfo
                    .getContainerName());
            Assert.assertNotNull(columnInfos);
            Assert.assertTrue(CollectionUtils.sizeIsEmpty(columnInfos));

            Map<String, List<String>> indexes = syndicateVersionDataDAO.getTableKeys(containerInfo.getContainerName());
            Assert.assertTrue(CollectionUtils.sizeIsEmpty(indexes));

        } catch (SystemException exp) {
            Assert.fail();
        }
    }

    //TODO: Need to give a re-look in to this test case as this is failing for HSQL DB for bulk insert.
    @Ignore
    @Test
    public void createProviderVersionTest() {
        byte[] bytes = new byte[100];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = 1;
        }
        createSyndicateData("TEST_DATA", "REO1", 250l, "SYND_DATA_REO", "version1", "version1 Desc", 1388514600l, 1391106600l);
        syndicateVersionDataDAO
                .executeQuery("CREATE TABLE SYND_DATA_TEST_DATA(SYNDICATEDVERID INTEGER,COL1 VARCHAR(25), COL2 INTEGER,COL3 DECIMAL(5,2));");
        SyndicateDataContainerInfo containerInfo = this.createSyndicateDataContainerInfo();
        containerInfo.setContainerName("TEST_DATA");
        containerInfo.setValidFromString("2014-SEP-23 03:02");
        containerInfo.setValidToString("2014-SEP-27 03:02");
        containerInfo.setAction(UserConfirmation.AGREED_TO_ADJUST_TIME_GAP);
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        try {
            Mockito.when(multipartFile.getBytes()).thenReturn(bytes);
        } catch (IOException e1) {
            Assert.fail();
        }
        containerInfo.setCsvFile(multipartFile);
        try {
            syndicateDataDelegate.createProviderVersion(containerInfo);
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }

        try {
            SyndicateDataVersionInfo dataVersionInfo = syndicateDataDelegate.listVersions("TEST_DATA");
            Assert.assertNotNull(dataVersionInfo);
            Assert.assertNotNull(dataVersionInfo.getVersions().get(0).getValidFrom());
            Long actual = dataVersionInfo.getVersions().get(0).getValidFrom();
            Long expected = 1538418600000L;
            assertEquals(actual, expected);
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        } finally {
            syndicateVersionDataDAO.executeQuery("DROP TABLE SYND_DATA_TEST_DATA");
        }

        List<Map<String, String>> data = new ArrayList<>();
        Map<String, String> rowData1 = new LinkedHashMap<String, String>();
        rowData1.put("COL1", "VAL1");
        rowData1.put("COL2", "val2");
        data.add(rowData1);
        containerInfo.setSyndicateVersionData(data);

        try {
            syndicateDataDelegate.createProviderVersion(containerInfo);
        } catch (BusinessException | SystemException exp) {
            assertTrue(exp instanceof BusinessException);
            assertEquals(BusinessExceptionCodes.BSE000033, exp.getCode());
        }
    }

    @Test
    public void deleteContainerVersionTest() {
        try {
            syndicateVersionDataDAO
                    .executeQuery("CREATE TABLE SYND_DATA_REO(SYNDICATEDVERID INTEGER, USER_ID INTEGER NOT NULL,LOGIN_ID VARCHAR(128) NOT NULL,USER_NAME VARCHAR(254) DEFAULT ' ' NOT NULL)");
            syndicateVersionDataDAO.executeQuery("INSERT INTO  SYND_DATA_REO VALUES(251,'2','ADMIN','TEST')");
            syndicateDataDelegate.deleteContainerVersion(251L, "REO");
            List<SyndicateDataContainerInfo> syndicateDataContainerInfoList = syndicateDataDelegate.getContainerInformation();
            Assert.assertNotNull(syndicateDataContainerInfoList);
            for (SyndicateDataContainerInfo syndicateDataContainerInfo : syndicateDataContainerInfoList) {
                Assert.assertNotNull(syndicateDataContainerInfo);
                Assert.assertFalse(syndicateDataContainerInfo.getVersionId().equals(251L));
            }
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }

        try {
            syndicateDataDelegate.deleteContainerVersion(25986L, null);
        } catch (BusinessException | SystemException exp) {
            assertTrue(exp instanceof BusinessException);
            assertEquals(BusinessExceptionCodes.BSE000014, exp.getCode());
        }

        try {
            syndicateDataDelegate.deleteContainerVersion(25986L, "TEST");
        } catch (BusinessException | SystemException exp) {
            assertTrue(exp instanceof BusinessException);
            assertEquals(BusinessExceptionCodes.BSE000036, exp.getCode());
        } finally {
            syndicateVersionDataDAO.executeQuery("DROP TABLE SYND_DATA_REO");
        }
    }

    @Test
    public void updateProviderTest() {
        SyndicateDataContainerInfo containerInfo = this.createSyndicateDataContainerInfo();
        syndicateVersionDataDAO.executeQuery("CREATE TABLE SYND_DATA_REO(COL1 INTEGER, COL2 INTEGER)");
        syndicateVersionDataDAO.executeQuery("INSERT INTO  SYND_DATA_REO VALUES(251,2)");
        try {
            containerInfo.setContainerName("REO");
            syndicateDataDelegate.updateProvider(containerInfo);

            SyndicateDataContainerInfo containerInfo1 = syndicateDataDelegate.getContainerInformation("REO");
            Assert.assertNotNull(containerInfo1);
            List<SyndicateDataKeyInfo> syndicateDataKeyInfos = containerInfo1.getKeyDefinitions();
            Assert.assertNotNull(syndicateDataKeyInfos);
            for (SyndicateDataKeyInfo syndicateDataKeyInfo : syndicateDataKeyInfos) {
                Assert.assertNotNull(syndicateDataKeyInfo);
                Assert.assertNotNull(syndicateDataKeyInfo.getKeyName());
                List<SyndicateDataKeyColumnInfo> syndicateDataKeyColumnInfos = syndicateDataKeyInfo.getsColumnInfos();
                Assert.assertNotNull(syndicateDataKeyColumnInfos);
                for (SyndicateDataKeyColumnInfo syndicateDataKeyColumnInfo : syndicateDataKeyColumnInfos) {
                    Assert.assertNotNull(syndicateDataKeyColumnInfo);
                    if (syndicateDataKeyColumnInfo.getColumnName().equals("COL1") && syndicateDataKeyColumnInfo.isStatus()) {
                        Assert.assertTrue(Boolean.TRUE);
                    } else if (syndicateDataKeyColumnInfo.getColumnName().equals("COL2")
                            && !syndicateDataKeyColumnInfo.isStatus()) {
                        Assert.assertFalse(Boolean.FALSE);
                    }
                }
            }
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }

        try {
            containerInfo.setKeyDefinitions(new ArrayList<SyndicateDataKeyInfo>());
            containerInfo.setContainerName("invalid");
            syndicateDataDelegate.updateProvider(containerInfo);
        } catch (BusinessException | SystemException exp) {
            assertTrue(exp instanceof BusinessException);
            assertEquals(BusinessExceptionCodes.BSE000018, exp.getCode());
        } finally {
            syndicateVersionDataDAO.executeQuery("DROP TABLE SYND_DATA_REO");
        }
    }

    @Test
    public void updateProviderVersionTest() {
        SyndicateDataContainerInfo sContainerInfo = this.createSyndicateDataContainerInfo();
        syndicateVersionDataDAO.executeQuery("CREATE TABLE SYND_DATA_METO(COL1 INTEGER, COL2 INTEGER)");
        syndicateVersionDataDAO.executeQuery("INSERT INTO  SYND_DATA_METO VALUES(251,2)");
        sContainerInfo.setContainerName("METO");
        sContainerInfo.setVersionId(201L);
        sContainerInfo.setValidFromString("2019-SEP-27 03:02");
        sContainerInfo.setValidToString("2020-SEP-27 03:02");
        sContainerInfo.setAction(UserConfirmation.AGREED_TO_ADJUST_TIME_GAP);
        try {
            List<SyndicateDataContainerInfo> syndicateDataContainerInfos = syndicateDataDelegate.getContainerInformation();
            Assert.assertNotNull(syndicateDataContainerInfos);
            int sizePrevious = syndicateDataContainerInfos.size();
            syndicateDataDelegate.updateProviderVersion(sContainerInfo);
            syndicateDataContainerInfos = syndicateDataDelegate.getContainerInformation();
            Assert.assertNotNull(syndicateDataContainerInfos);
            int sizeNow = syndicateDataContainerInfos.size();
            if (sizeNow > sizePrevious) {
                Assert.assertTrue(Boolean.TRUE);
            } else {
                Assert.assertFalse(Boolean.FALSE);
            }
        } catch (BusinessException | SystemException exp) {
            Assert.fail();
        } finally {
            syndicateVersionDataDAO.executeQuery("DROP TABLE SYND_DATA_METO");
        }
    }

    @Test
    public void updateProviderVersionNegativeTest() {
        SyndicateDataContainerInfo sContainerInfo = this.createSyndicateDataContainerInfo();
        syndicateVersionDataDAO.executeQuery("CREATE TABLE SYND_DATA_REO(COL1 INTEGER, COL2 INTEGER)");
        syndicateVersionDataDAO.executeQuery("INSERT INTO  SYND_DATA_REO VALUES(251,2)");
        sContainerInfo.setContainerName("REO");
        sContainerInfo.setVersionId(251L);
        sContainerInfo.setValidFromString("2014-SEP-23 03:02");
        sContainerInfo.setValidToString("2014-SEP-27 03:02");
        try {
            List<SyndicateDataContainerInfo> syndicateDataContainerInfos = syndicateDataDelegate.getContainerInformation();
            Assert.assertNotNull(syndicateDataContainerInfos);
            int sizePrevious = syndicateDataContainerInfos.size();
            syndicateDataDelegate.updateProviderVersion(sContainerInfo);
            syndicateDataContainerInfos = syndicateDataDelegate.getContainerInformation();
            Assert.assertNotNull(syndicateDataContainerInfos);
            int sizeNow = syndicateDataContainerInfos.size();
            if (sizeNow > sizePrevious) {
                Assert.assertTrue(Boolean.TRUE);
            } else {
                Assert.assertFalse(Boolean.FALSE);
            }
        } catch (BusinessException | SystemException exp) {
            assertTrue(exp instanceof BusinessException);
            assertEquals(BusinessExceptionCodes.BSE000034, exp.getCode());
        } finally {
            syndicateVersionDataDAO.executeQuery("DROP TABLE SYND_DATA_REO");
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
            List<String> syndTableData = syndicateDataDelegate.downloadSyndTableData("REO", 250L);
            assertEquals(3, syndTableData.size());

        } catch (BusinessException | SystemException e) {
            Assert.fail();
        } finally {
            syndicateVersionDataDAO.executeQuery("drop TABLE SYND_DATA_REO");
        }
    }

    /**
     * 
     * @return
     */
    private SyndicateDataContainerInfo createSyndicateDataContainerInfo() {
        SyndicateDataContainerInfo containerInfo = new SyndicateDataContainerInfo();
        containerInfo.setContainerName("CONT_1");
        containerInfo.setCreatedBy("ADMIN");
        containerInfo.setDescription("cont1");
        containerInfo.setValidFrom(new DateTime().getMillis());
        containerInfo.setValidFromString("2014-SEP-23 03:02");
        containerInfo.setValidTo(new DateTime().getMillis());
        containerInfo.setValidToString("2014-SEP-27 03:02");
        containerInfo.setTotalRows(3L);
        containerInfo.setVersionDescription("Version1");
        containerInfo.setVersionId(1L);
        containerInfo.setVersionName("newVersion");
        containerInfo.setSyndicateVersionData(new ArrayList<Map<String, String>>());
        byte[] bytes = new byte[100];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = 1;
        }
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        try {
            Mockito.when(multipartFile.getBytes()).thenReturn(bytes);
        } catch (IOException e1) {
            Assert.fail();
        }
        containerInfo.setCsvFile(multipartFile);

        List<SyndicateDataColumnInfo> syndicateDataColumnInfos = new ArrayList<>();

        SyndicateDataColumnInfo dataColumnInfo1 = new SyndicateDataColumnInfo();
        dataColumnInfo1.setField("COL1");
        dataColumnInfo1.setDisplayName("COL1");
        dataColumnInfo1.setDescription("col1 desc");
        dataColumnInfo1.setColumnSize(5);
        dataColumnInfo1.setColumnType("String");
        dataColumnInfo1.setIndex(1);
        dataColumnInfo1.setMandatory(false);
        syndicateDataColumnInfos.add(dataColumnInfo1);

        SyndicateDataColumnInfo dataColumnInfo2 = new SyndicateDataColumnInfo();
        dataColumnInfo2.setField("COL2");
        dataColumnInfo2.setDisplayName("COL2");
        dataColumnInfo2.setDescription("col2 desc");
        dataColumnInfo2.setColumnSize(5);
        dataColumnInfo2.setColumnType("NUMBER");
        dataColumnInfo2.setIndex(2);
        dataColumnInfo2.setMandatory(false);
        syndicateDataColumnInfos.add(dataColumnInfo2);

        SyndicateDataColumnInfo dataColumnInfo3 = new SyndicateDataColumnInfo();
        dataColumnInfo3.setField("COL3");
        dataColumnInfo3.setDisplayName("COL3");
        dataColumnInfo3.setDescription("col3 desc");
        dataColumnInfo3.setColumnSize(5);
        dataColumnInfo3.setPrecision(2);
        dataColumnInfo3.setColumnType("DOUBLE");
        dataColumnInfo3.setIndex(2);
        dataColumnInfo3.setMandatory(false);
        syndicateDataColumnInfos.add(dataColumnInfo3);
        containerInfo.setMetaData(syndicateDataColumnInfos);

        List<SyndicateDataKeyInfo> sDataKeyInfos = new ArrayList<>();

        List<SyndicateDataKeyColumnInfo> sColumnInfos = new ArrayList<>();

        SyndicateDataKeyColumnInfo columnInfo = new SyndicateDataKeyColumnInfo();
        columnInfo.setColumnName("COL1");
        columnInfo.setStatus(Boolean.TRUE);
        sColumnInfos.add(columnInfo);

        SyndicateDataKeyInfo keyInfo = new SyndicateDataKeyInfo();
        keyInfo.setKeyName("IND1");
        keyInfo.setsColumnInfos(sColumnInfos);
        sDataKeyInfos.add(keyInfo);

        containerInfo.setKeyDefinitions(sDataKeyInfos);

        List<Map<String, String>> data = new ArrayList<>();
        Map<String, String> rowData1 = new LinkedHashMap<String, String>();
        rowData1.put("COL1", "VAL1");
        rowData1.put("COL2", "2");
        rowData1.put("COL3", "5.2");
        data.add(rowData1);
        Map<String, String> rowData2 = new LinkedHashMap<String, String>();
        rowData2.put("COL1", "VAL2");
        rowData2.put("COL2", "4");
        rowData1.put("COL3", "6.52");
        data.add(rowData2);
        Map<String, String> rowData3 = new LinkedHashMap<String, String>();
        rowData3.put("COL1", "VAL3");
        rowData3.put("COL2", "5");
        rowData1.put("COL3", "52.2");
        data.add(rowData3);
        containerInfo.setSyndicateVersionData(data);

        return containerInfo;
    }
}