package com.ca.umg.business.validation;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.syndicatedata.info.SyndicateDataContainerInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataKeyColumnInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataKeyInfo;
import com.ca.umg.business.tenant.entity.SystemKey;

public class UpdateSyndicateDataValidatorTest {

    SystemKey systemKey = null;

    @Before
    public void setUp() {
        systemKey = new SystemKey();
        systemKey.setKey(BusinessConstants.COLUMN_IDENTIFIERS);
        systemKey
                .setType("STRING,VARCHAR,DECIMAL,DOUBLE,BIT,INTEGER, INT, SMALLINT, TINYINT, MEDIUMINT, BIGINT,TIMESTAMP,TIME,YEAR,DATETIME,DATE");
    }

    @Test
    public void testIfNameIsEmpty() {
        UpdateSyndicateDataValidator classUnderTest = new UpdateSyndicateDataValidator();
        SyndicateDataContainerInfo bean = new SyndicateDataContainerInfo();
        bean.setDescription("some description");

        List<ValidationError> errors = classUnderTest.validateForUpdate(bean, systemKey);

        assertThat(errors.size(), is(1));
        assertThat(errors.get(0).getMessage(), is("Container name cannot be empty"));
    }

    @Test
    public void testIfDescriptionIsEmpty() {
        UpdateSyndicateDataValidator classUnderTest = new UpdateSyndicateDataValidator();
        SyndicateDataContainerInfo bean = new SyndicateDataContainerInfo();
        bean.setContainerName("someName");

        List<ValidationError> errors = classUnderTest.validateForUpdate(bean, systemKey);

        assertThat(errors.size(), is(1));
        assertThat(errors.get(0).getMessage(), is("Container description cannot be empty"));
    }

    @Test
    public void testIfDescriptionIsMoreThanLimit() {
        UpdateSyndicateDataValidator classUnderTest = new UpdateSyndicateDataValidator();
        SyndicateDataContainerInfo bean = new SyndicateDataContainerInfo();
        bean.setContainerName("someName");
        bean.setDescription(StringUtils.repeat("s", 250));

        List<ValidationError> errors = classUnderTest.validateForUpdate(bean, systemKey);

        assertThat(errors.size(), is(1));
        assertThat(errors.get(0).getMessage(), is("Container description can be maximum 200 characters"));
    }

    @Test
    public void testIfKeyNameIsEmpty() {
        UpdateSyndicateDataValidator classUnderTest = new UpdateSyndicateDataValidator();
        SyndicateDataContainerInfo bean = new SyndicateDataContainerInfo();
        bean.setContainerName("someName");
        bean.setDescription("some description");
        List<SyndicateDataKeyInfo> keyDefinitions = new ArrayList<SyndicateDataKeyInfo>();
        SyndicateDataKeyInfo key = new SyndicateDataKeyInfo();
        List<SyndicateDataKeyColumnInfo> columnInfo1 = new ArrayList<>();
        SyndicateDataKeyColumnInfo column1 = new SyndicateDataKeyColumnInfo();
        column1.setColumnName("column1");
        columnInfo1.add(column1);
        key.setsColumnInfos(columnInfo1);
        keyDefinitions.add(key);
        bean.setKeyDefinitions(keyDefinitions);

        List<ValidationError> errors = classUnderTest.validateForUpdate(bean, systemKey);

        assertThat(errors.size(), is(2));
        assertThat(errors.get(0).getMessage(), is("Key name cannot be empty"));
    }

    @Test
    public void testIfKeyNameContainsSpaces() {
        UpdateSyndicateDataValidator classUnderTest = new UpdateSyndicateDataValidator();
        SyndicateDataContainerInfo bean = new SyndicateDataContainerInfo();
        bean.setContainerName("someName");
        bean.setDescription("some description");
        List<SyndicateDataKeyInfo> keyDefinitions = new ArrayList<SyndicateDataKeyInfo>();
        SyndicateDataKeyInfo key = new SyndicateDataKeyInfo();
        key.setKeyName("with space");
        List<SyndicateDataKeyColumnInfo> columnInfo1 = new ArrayList<>();
        SyndicateDataKeyColumnInfo column1 = new SyndicateDataKeyColumnInfo();
        column1.setColumnName("column1");
        columnInfo1.add(column1);
        key.setsColumnInfos(columnInfo1);
        keyDefinitions.add(key);
        bean.setKeyDefinitions(keyDefinitions);

        List<ValidationError> errors = classUnderTest.validateForUpdate(bean, systemKey);

        assertThat(errors.size(), is(2));
        assertThat(errors.get(0).getMessage(), is("Key name cannot contain special charaters and spaces"));
    }

    @Test
    public void testIfKeyNameContainsSpecialCharacters() {
        UpdateSyndicateDataValidator classUnderTest = new UpdateSyndicateDataValidator();
        SyndicateDataContainerInfo bean = new SyndicateDataContainerInfo();
        bean.setContainerName("someName");
        bean.setDescription("some description");
        List<SyndicateDataKeyInfo> keyDefinitions = new ArrayList<SyndicateDataKeyInfo>();
        SyndicateDataKeyInfo key = new SyndicateDataKeyInfo();
        key.setKeyName("withSpecialChar$");
        List<SyndicateDataKeyColumnInfo> columnInfo1 = new ArrayList<>();
        SyndicateDataKeyColumnInfo column1 = new SyndicateDataKeyColumnInfo();
        column1.setColumnName("column1");
        columnInfo1.add(column1);
        key.setsColumnInfos(columnInfo1);
        keyDefinitions.add(key);
        bean.setKeyDefinitions(keyDefinitions);

        List<ValidationError> errors = classUnderTest.validateForUpdate(bean, systemKey);

        assertThat(errors.size(), is(2));
        assertThat(errors.get(0).getMessage(), is("Key name cannot contain special charaters and spaces"));
    }

    @Test
    public void testIfKeyNameContainingUnderscoreShouldBeAllowed() {
        UpdateSyndicateDataValidator classUnderTest = new UpdateSyndicateDataValidator();
        SyndicateDataContainerInfo bean = new SyndicateDataContainerInfo();
        bean.setContainerName("someName");
        bean.setDescription("some description");
        List<SyndicateDataKeyInfo> keyDefinitions = new ArrayList<SyndicateDataKeyInfo>();
        SyndicateDataKeyInfo key = new SyndicateDataKeyInfo();
        key.setKeyName("With_Special_Char");
        List<SyndicateDataKeyColumnInfo> columnInfo1 = new ArrayList<>();
        SyndicateDataKeyColumnInfo column1 = new SyndicateDataKeyColumnInfo();
        column1.setColumnName("column1");
        column1.setStatus(true);
        columnInfo1.add(column1);
        key.setsColumnInfos(columnInfo1);
        keyDefinitions.add(key);
        bean.setKeyDefinitions(keyDefinitions);

        List<ValidationError> errors = classUnderTest.validateForUpdate(bean, systemKey);

        assertThat(errors.size(), is(0));
    }

    @Test
    public void testIfColumnAssociatedWithKeyIsNotEmpty() {
        UpdateSyndicateDataValidator classUnderTest = new UpdateSyndicateDataValidator();
        SyndicateDataContainerInfo bean = new SyndicateDataContainerInfo();
        bean.setContainerName("someName");
        bean.setDescription("some description");
        List<SyndicateDataKeyInfo> keyDefinitions = new ArrayList<SyndicateDataKeyInfo>();
        SyndicateDataKeyInfo key1 = new SyndicateDataKeyInfo();
        key1.setKeyName("key");
        key1.setsColumnInfos(null);
        keyDefinitions.add(key1);
        bean.setKeyDefinitions(keyDefinitions);

        List<ValidationError> errors = classUnderTest.validateForUpdate(bean, systemKey);

        assertThat(errors.size(), is(2));
        assertThat(errors.get(0).getMessage(), is("Columns associated with keys cannot be empty"));
    }

    @Test
    public void testNoDuplicateKeys() {
        UpdateSyndicateDataValidator classUnderTest = new UpdateSyndicateDataValidator();
        SyndicateDataContainerInfo bean = new SyndicateDataContainerInfo();
        bean.setContainerName("someName");
        bean.setDescription("some description");
        List<SyndicateDataKeyInfo> keyDefinitions = new ArrayList<SyndicateDataKeyInfo>();
        SyndicateDataKeyInfo key1 = new SyndicateDataKeyInfo();
        key1.setKeyName("key");
        List<SyndicateDataKeyColumnInfo> columnInfo1 = new ArrayList<>();
        SyndicateDataKeyColumnInfo column1 = new SyndicateDataKeyColumnInfo();
        column1.setColumnName("column1");
        columnInfo1.add(column1);
        key1.setsColumnInfos(columnInfo1);
        keyDefinitions.add(key1);
        SyndicateDataKeyInfo key2 = new SyndicateDataKeyInfo();
        key2.setKeyName("key");
        List<SyndicateDataKeyColumnInfo> columnInfo2 = new ArrayList<>();
        SyndicateDataKeyColumnInfo column2 = new SyndicateDataKeyColumnInfo();
        column2.setColumnName("column2");
        columnInfo2.add(column2);
        key2.setsColumnInfos(columnInfo2);
        keyDefinitions.add(key2);
        bean.setKeyDefinitions(keyDefinitions);

        List<ValidationError> errors = classUnderTest.validateForUpdate(bean, systemKey);

        assertThat(errors.size(), is(2));
        assertThat(errors.get(1).getMessage(), is("Cannot have duplicate keys : key"));
    }

    @Test
    public void testNoSameColumnForDiifferentKeys() {
        UpdateSyndicateDataValidator classUnderTest = new UpdateSyndicateDataValidator();
        SyndicateDataContainerInfo bean = new SyndicateDataContainerInfo();
        bean.setContainerName("someName");
        bean.setDescription("some description");
        List<SyndicateDataKeyInfo> keyDefinitions = new ArrayList<SyndicateDataKeyInfo>();
        SyndicateDataKeyInfo key1 = new SyndicateDataKeyInfo();
        key1.setKeyName("key");
        List<SyndicateDataKeyColumnInfo> columnInfo1 = new ArrayList<>();
        SyndicateDataKeyColumnInfo column1 = new SyndicateDataKeyColumnInfo();
        column1.setColumnName("column1");
        column1.setStatus(true);
        columnInfo1.add(column1);
        key1.setsColumnInfos(columnInfo1);
        keyDefinitions.add(key1);
        SyndicateDataKeyInfo key2 = new SyndicateDataKeyInfo();
        key2.setKeyName("key1");
        List<SyndicateDataKeyColumnInfo> columnInfo2 = new ArrayList<>();
        SyndicateDataKeyColumnInfo column2 = new SyndicateDataKeyColumnInfo();
        column2.setColumnName("column1");
        column2.setStatus(true);
        columnInfo2.add(column2);
        key2.setsColumnInfos(columnInfo2);
        keyDefinitions.add(key2);
        bean.setKeyDefinitions(keyDefinitions);

        List<ValidationError> errors = classUnderTest.validateForUpdate(bean, systemKey);

        assertThat(errors.size(), is(1));
        assertThat(errors.get(0).getMessage(), is("Cannot have same columns for different keys : key1"));
    }
}
