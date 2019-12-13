package com.ca.umg.business.validation;

import static org.hamcrest.core.Is.is;
import static org.joda.time.DateTime.parse;
import static org.joda.time.format.DateTimeFormat.forPattern;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.syndicatedata.entity.SyndicateData;
import com.ca.umg.business.syndicatedata.info.SyndicateDataColumnInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataContainerInfo;
import com.ca.umg.business.tenant.entity.SystemKey;

public class CreateSyndicateDataValidatorTest {

    private CreateSyndicateDataValidator classUnderTest;

    private SyndicateDataContainerInfo bean;

    private List<ValidationError> errors;

    private List<String> keyValues = Arrays
            .asList("STRING,VARCHAR,DECIMAL,DOUBLE,BIT,INTEGER, INT, SMALLINT, TINYINT, MEDIUMINT, BIGINT,TIMESTAMP,TIME,YEAR,DATETIME,DATE"
                    .split(BusinessConstants.CHAR_COMMA));

    @Before
    public void setup() {
        classUnderTest = new CreateSyndicateDataValidator();
        bean = new SyndicateDataContainerInfo();
        errors = new ArrayList<ValidationError>();
    }

    @Test
    public void testColumnSizeCannotBeEmpty() {
        ArrayList<SyndicateDataColumnInfo> metaData = new ArrayList<SyndicateDataColumnInfo>();
        SyndicateDataColumnInfo column = new SyndicateDataColumnInfo();
        column.setColumnType("dummy");
        metaData.add(column);
        bean.setMetaData(metaData);

        classUnderTest.validateColumnDefinition(bean, errors, keyValues);

        assertThat(errors.size(), is(1));
        assertThat(errors.get(0).getMessage(), is("Column size cannot be empty"));
    }

    @Test
    public void testDuplicateColumnNames() {
        ArrayList<SyndicateDataColumnInfo> metaData = new ArrayList<SyndicateDataColumnInfo>();
        SyndicateDataColumnInfo column1 = new SyndicateDataColumnInfo();
        column1.setColumnSize(22);
        column1.setColumnType("someName");
        column1.setDisplayName("someName");
        metaData.add(column1);
        SyndicateDataColumnInfo column2 = new SyndicateDataColumnInfo();
        column2.setColumnSize(22);
        column2.setDisplayName("someName");
        column2.setColumnType("someName");
        metaData.add(column2);
        bean.setMetaData(metaData);

        classUnderTest.validateColumnDefinition(bean, errors, keyValues);

        assertThat(errors.size(), is(1));
        assertThat(errors.get(0).getMessage(), is("Cannot have duplicate column names"));
    }

    @Test
    public void testSameContainerName() throws BusinessException {
        List<SyndicateData> allSyndicateDatas = new ArrayList<>();
        SyndicateData data1 = new SyndicateData();
        data1.setContainerName("sameName");
        allSyndicateDatas.add(data1);
        bean.setContainerName("sameName");
        bean.setValidFrom(parse("22-Mar-2015 11:21", forPattern("dd-MMM-yyyy hh:mm")).getMillis());
        bean.setValidTo(parse("22-Mar-2016 11:25", forPattern("dd-MMM-yyyy hh:mm")).getMillis());
        SystemKey systemKey = new SystemKey();
        systemKey
                .setType("STRING,VARCHAR,DECIMAL,DOUBLE,BIT,INTEGER, INT, SMALLINT, TINYINT, MEDIUMINT, BIGINT,TIMESTAMP,TIME,YEAR,DATETIME,DATE");
        errors = classUnderTest.validateForCreate(bean, allSyndicateDatas, systemKey);

        assertThat(errors.get(0).getMessage(), is("Syndicate data with the same container name already present."));
    }

    @Test
    public void testColumnNameValidity() {

        ArrayList<SyndicateDataColumnInfo> metaData = new ArrayList<SyndicateDataColumnInfo>();
        SyndicateDataColumnInfo column1 = new SyndicateDataColumnInfo();
        column1.setColumnSize(22);
        column1.setColumnType("DATE");
        column1.setDisplayName("DOUBLE");
        metaData.add(column1);
        bean.setMetaData(metaData);

        classUnderTest.validateColumnDefinition(bean, errors, keyValues);

        assertThat(errors.size(), is(1));
        assertThat(errors.get(0).getMessage(), is("InValid Column Name '" + column1.getDisplayName() + "' defined"));

    }
}
