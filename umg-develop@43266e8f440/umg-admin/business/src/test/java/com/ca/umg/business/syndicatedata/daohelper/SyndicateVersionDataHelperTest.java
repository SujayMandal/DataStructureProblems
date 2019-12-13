package com.ca.umg.business.syndicatedata.daohelper;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.multipart.MultipartFile;

import com.ca.umg.business.syndicatedata.entity.SyndicateData;
import com.ca.umg.business.syndicatedata.info.SyndicateDataColumnInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataContainerInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataKeyColumnInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataKeyInfo;

import junit.framework.Assert;

@ContextHierarchy({ @ContextConfiguration("classpath:root-db-context.xml"), @ContextConfiguration })
@RunWith(SpringJUnit4ClassRunner.class)

@Ignore
// TODO fix ignored test cases
public class SyndicateVersionDataHelperTest {

    private SyndicateVersionDataHelper syndicateVersionDataHelper = new SyndicateVersionDataHelper();

    @Test
    public void createTableQueryTest() {

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

        SyndicateDataColumnInfo dataColumnInfo4 = new SyndicateDataColumnInfo();
        dataColumnInfo4.setField("COL4");
        dataColumnInfo4.setDisplayName("COL4");
        dataColumnInfo4.setDescription("col4 desc");
        dataColumnInfo4.setColumnSize(5);
        dataColumnInfo4.setPrecision(2);
        dataColumnInfo4.setColumnType("DATE");
        dataColumnInfo4.setIndex(2);
        dataColumnInfo4.setMandatory(false);
        syndicateDataColumnInfos.add(dataColumnInfo4);

        SyndicateDataColumnInfo dataColumnInfo5 = new SyndicateDataColumnInfo();
        dataColumnInfo5.setField("COL5");
        dataColumnInfo5.setDisplayName("COL5");
        dataColumnInfo5.setDescription("col5 desc");
        dataColumnInfo5.setColumnSize(5);
        dataColumnInfo5.setPrecision(2);
        dataColumnInfo5.setColumnType("DATETIME");
        dataColumnInfo5.setIndex(2);
        dataColumnInfo5.setMandatory(false);
        syndicateDataColumnInfos.add(dataColumnInfo5);

        SyndicateDataColumnInfo dataColumnInfo6 = new SyndicateDataColumnInfo();
        dataColumnInfo6.setField("COL6");
        dataColumnInfo6.setDisplayName("COL6");
        dataColumnInfo6.setDescription("col6 desc");
        dataColumnInfo6.setColumnSize(5);
        dataColumnInfo6.setPrecision(2);
        dataColumnInfo6.setColumnType("INTEGER");
        dataColumnInfo6.setIndex(2);
        dataColumnInfo6.setMandatory(false);
        syndicateDataColumnInfos.add(dataColumnInfo6);

        SyndicateDataColumnInfo dataColumnInfo7 = new SyndicateDataColumnInfo();
        dataColumnInfo7.setField("COL7");
        dataColumnInfo7.setDisplayName("COL7");
        dataColumnInfo7.setDescription("col7 desc");
        dataColumnInfo7.setColumnSize(5);
        dataColumnInfo7.setPrecision(2);
        dataColumnInfo7.setColumnType("BOOLEAN");
        dataColumnInfo7.setIndex(2);
        dataColumnInfo7.setMandatory(false);
        syndicateDataColumnInfos.add(dataColumnInfo7);
        String createTable = syndicateVersionDataHelper.createTableQuery("test", syndicateDataColumnInfos);

        Assert.assertNotNull(createTable);
        assertThat(
                createTable,
                is("CREATE TABLE TEST(SYNDICATEDVERID INTEGER, COL1 VARCHAR(5) COMMENT 'col1 desc',COL2 INTEGER COMMENT 'col2 desc',COL3 DECIMAL(7,2) COMMENT 'col3 desc',COL4 DATE COMMENT 'col4 desc',COL5 DATETIME COMMENT 'col5 desc',COL6 INTEGER COMMENT 'col6 desc',COL7 TINYINT(1) COMMENT 'col7 desc' );"));
    }

    @Test
    public void createIndexesQueryTest() {
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

        String keyCreation = syndicateVersionDataHelper.createIndexesQuery("test", sDataKeyInfos);
        Assert.assertNotNull(keyCreation);

        assertThat(keyCreation, is("CREATE INDEX IND1 ON test(COL1 );"));
    }

    @Test
    public void insertDataStatementsTest() {
        SyndicateDataContainerInfo containerInfo = this.createSyndicateDataContainerInfo();
        SyndicateData syndicateData = this.getSyndicateData(containerInfo);

        String[] insertStatement = syndicateVersionDataHelper.insertDataStatements(containerInfo, syndicateData);
        Assert.assertNotNull(insertStatement);

        assertThat(
                insertStatement[0],
                is("INSERT INTO SYND_DATA_CONT_1(SYNDICATEDVERID,COL1,COL2,COL3,COL4,COL5,COL6,COL7) VALUES(1,'VAL1',2,52.2,STR_TO_DATE('OCT-02-2018 00:00 GMT+0530','%d-%b-%Y'),STR_TO_DATE('OCT-02-2018 15:00 GMT+0530','%b-%d-%Y %H:%i:%s'),6,1 );"));
        assertThat(insertStatement[1], is("INSERT INTO SYND_DATA_CONT_1(SYNDICATEDVERID,COL1,COL2) VALUES(1,'VAL2',4 );"));
        assertThat(insertStatement[2], is("INSERT INTO SYND_DATA_CONT_1(SYNDICATEDVERID,COL1,COL2) VALUES(1,'VAL3',5 );"));
    }

    @Test
    public void dropIndexesQueriesTest() {
        String tableName = "testDropKey";
        Set<String> keySet = new HashSet<>();
        keySet.add("key1");
        keySet.add("key2");
        keySet.add("key3");

        List<String> keyListString = syndicateVersionDataHelper.dropIndexesQueries(tableName, keySet);

        Assert.assertNotNull(keyListString);
        assertThat(keyListString.get(0), is("DROP INDEX testDropKey_key3 ON testDropKey;"));
        assertThat(keyListString.get(1), is("DROP INDEX testDropKey_key2 ON testDropKey;"));
        assertThat(keyListString.get(2), is("DROP INDEX testDropKey_key1 ON testDropKey;"));
    }

    private SyndicateData getSyndicateData(SyndicateDataContainerInfo containerInfo) {
        SyndicateData syndicateData = new SyndicateData();
        syndicateData.setContainerName(containerInfo.getContainerName());
        syndicateData.setCreatedBy(containerInfo.getCreatedBy());
        syndicateData.setCreatedDate(containerInfo.getCreatedDate());
        syndicateData.setDescription(containerInfo.getDescription());
        syndicateData.setId(containerInfo.getId());
        syndicateData.setLastModifiedBy(containerInfo.getLastModifiedBy());
        syndicateData.setLastModifiedDate(containerInfo.getLastModifiedDate());
        syndicateData.setTableName("SYND_DATA_CONT_1");
        syndicateData.setValidFrom(containerInfo.getValidFrom());
        syndicateData.setValidTo(containerInfo.getValidTo());
        syndicateData.setVersionDescription(containerInfo.getVersionDescription());
        syndicateData.setVersionId(containerInfo.getVersionId());
        syndicateData.setVersionName(containerInfo.getVersionName());
        return syndicateData;
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
        containerInfo.setValidFrom(new DateTime().getMillis() + 2592000L);
        containerInfo.setValidFromString("OCT-01-2014 00:00 GMT+0530");
        containerInfo.setValidTo(new DateTime().getMillis() + 5184000L);
        containerInfo.setValidToString("OCT-01-2015 23:59 GMT+0530");
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

        SyndicateDataColumnInfo dataColumnInfo4 = new SyndicateDataColumnInfo();
        dataColumnInfo4.setField("COL4");
        dataColumnInfo4.setDisplayName("COL4");
        dataColumnInfo4.setDescription("col4 desc");
        dataColumnInfo4.setColumnSize(5);
        dataColumnInfo4.setPrecision(2);
        dataColumnInfo4.setColumnType("DATE");
        dataColumnInfo4.setIndex(2);
        dataColumnInfo4.setMandatory(false);
        syndicateDataColumnInfos.add(dataColumnInfo4);

        SyndicateDataColumnInfo dataColumnInfo5 = new SyndicateDataColumnInfo();
        dataColumnInfo5.setField("COL5");
        dataColumnInfo5.setDisplayName("COL5");
        dataColumnInfo5.setDescription("col5 desc");
        dataColumnInfo5.setColumnSize(5);
        dataColumnInfo5.setPrecision(2);
        dataColumnInfo5.setColumnType("DATETIME");
        dataColumnInfo5.setIndex(2);
        dataColumnInfo5.setMandatory(false);
        syndicateDataColumnInfos.add(dataColumnInfo5);

        SyndicateDataColumnInfo dataColumnInfo6 = new SyndicateDataColumnInfo();
        dataColumnInfo6.setField("COL6");
        dataColumnInfo6.setDisplayName("COL6");
        dataColumnInfo6.setDescription("col6 desc");
        dataColumnInfo6.setColumnSize(5);
        dataColumnInfo6.setPrecision(2);
        dataColumnInfo6.setColumnType("INTEGER");
        dataColumnInfo6.setIndex(2);
        dataColumnInfo6.setMandatory(false);
        syndicateDataColumnInfos.add(dataColumnInfo6);

        SyndicateDataColumnInfo dataColumnInfo7 = new SyndicateDataColumnInfo();
        dataColumnInfo7.setField("COL7");
        dataColumnInfo7.setDisplayName("COL7");
        dataColumnInfo7.setDescription("col7 desc");
        dataColumnInfo7.setColumnSize(5);
        dataColumnInfo7.setPrecision(2);
        dataColumnInfo7.setColumnType("BOOLEAN");
        dataColumnInfo7.setIndex(2);
        dataColumnInfo7.setMandatory(false);
        syndicateDataColumnInfos.add(dataColumnInfo7);

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
        rowData1.put("COL4", "OCT-02-2015 00:00 GMT+0530");
        rowData1.put("COL5", "OCT-02-2015 12:00 GMT+0530");
        rowData1.put("COL6", "4");
        rowData1.put("COL7", "true");
        data.add(rowData1);
        Map<String, String> rowData2 = new LinkedHashMap<String, String>();
        rowData2.put("COL1", "VAL2");
        rowData2.put("COL2", "4");
        rowData1.put("COL3", "6.52");
        rowData1.put("COL4", "OCT-02-2016 00:00 GMT+0530");
        rowData1.put("COL5", "OCT-02-2016 18:00 GMT+0530");
        rowData1.put("COL6", "5");
        rowData1.put("COL7", "false");
        data.add(rowData2);
        Map<String, String> rowData3 = new LinkedHashMap<String, String>();
        rowData3.put("COL1", "VAL3");
        rowData3.put("COL2", "5");
        rowData1.put("COL3", "52.2");
        rowData1.put("COL4", "OCT-02-2018 00:00 GMT+0530");
        rowData1.put("COL5", "OCT-02-2018 15:00 GMT+0530");
        rowData1.put("COL6", "6");
        rowData1.put("COL7", "true");
        data.add(rowData3);
        containerInfo.setSyndicateVersionData(data);

        return containerInfo;
    }
}