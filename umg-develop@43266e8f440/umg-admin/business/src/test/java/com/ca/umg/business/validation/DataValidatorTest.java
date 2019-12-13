package com.ca.umg.business.validation;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.ca.umg.business.syndicatedata.info.SyndicateDataColumnInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataContainerInfo;

public class DataValidatorTest {

    private DataValidator classUnderTest;

    private SyndicateDataContainerInfo bean;

    private List<ValidationError> errors;

    @Before
    public void setUp() {
        classUnderTest = new DataValidator();
        bean = new SyndicateDataContainerInfo();
        errors = new ArrayList<ValidationError>();
    }

    @Test
    public void testZeroRowCount() {
        classUnderTest.validateData(bean, errors);

        assertThat(errors.size(), is(1));
        assertThat(errors.get(0).getField(), is("rowCount"));
        assertThat(errors.get(0).getMessage(), is("Row count cannot be empty"));
    }

    @Test
    public void testRowCountDoesNotMatchNoOfRowsOfData() {
        bean.setTotalRows(40l);
        bean.setSyndicateVersionData(new ArrayList<Map<String, String>>());
        classUnderTest.validateData(bean, errors);

        assertThat(errors.size(), is(1));
        assertThat(errors.get(0).getField(), is("rowCount"));
        assertThat(errors.get(0).getMessage(), is("Row count mentioned does not match the no of rows of data"));
    }

    @Test
    public void testDataMandatoryColumnContainingEmptyValues() {
        bean.setTotalRows(1L);
        ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
        Map<String, String> row = new LinkedHashMap<>();
        row.put("column1", "");
        data.add(row);
        bean.setSyndicateVersionData(data);

        List<SyndicateDataColumnInfo> metaData = new ArrayList<>();
        SyndicateDataColumnInfo columnInfo1 = new SyndicateDataColumnInfo();
        columnInfo1.setMandatory(true);
        columnInfo1.setColumnType("String");
        metaData.add(columnInfo1);
        bean.setMetaData(metaData);

        classUnderTest.validateData(bean, errors);

        assertThat(errors.size(), is(1));
        assertThat(errors.get(0).getField(), is("data"));
        assertThat(errors.get(0).getMessage(), is("Data in cell[1][1] : Mandatory column cannot contain empty fields"));
    }

    @Test
    public void testDataMandatoryColumnContainingEmptyValuesForNumber() {
        bean.setTotalRows(1L);
        ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
        Map<String, String> row = new LinkedHashMap<>();
        row.put("column1", "");
        data.add(row);
        bean.setSyndicateVersionData(data);

        List<SyndicateDataColumnInfo> metaData = new ArrayList<>();
        SyndicateDataColumnInfo columnInfo1 = new SyndicateDataColumnInfo();
        columnInfo1.setMandatory(true);
        columnInfo1.setColumnType("Number");
        metaData.add(columnInfo1);
        bean.setMetaData(metaData);

        classUnderTest.validateData(bean, errors);

        assertThat(errors.size(), is(1));
        assertThat(errors.get(0).getField(), is("data"));
        assertThat(errors.get(0).getMessage(), is("Data in cell[1][1] : Mandatory column cannot contain empty fields"));
    }

    /**
     * To check only double data validation.
     */
    @Test
    public void testDataValueSizeGreaterThanColumnSizeDefined() {
        bean.setTotalRows(1L);
        ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
        Map<String, String> row = new LinkedHashMap<>();
        row.put("column1", "more than two size");
        data.add(row);
        bean.setSyndicateVersionData(data);

        List<SyndicateDataColumnInfo> metaData = new ArrayList<>();
        SyndicateDataColumnInfo columnInfo1 = new SyndicateDataColumnInfo();
        columnInfo1.setColumnSize(2);
        columnInfo1.setColumnType("String");
        metaData.add(columnInfo1);
        bean.setMetaData(metaData);

        classUnderTest.validateData(bean, errors);

        assertThat(errors.size(), is(1));
        assertThat(errors.get(0).getField(), is("data"));
        assertThat(errors.get(0).getMessage(), is("Data in cell[1][1] : Data size is greater than column size defined"));
    }

    @Test
    public void testDataValueDefinedAsNumberValueIsNotANumber() {
        bean.setTotalRows(1L);
        ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
        Map<String, String> row = new LinkedHashMap<>();
        row.put("column1", "more than two size");
        data.add(row);
        bean.setSyndicateVersionData(data);

        List<SyndicateDataColumnInfo> metaData = new ArrayList<>();
        SyndicateDataColumnInfo columnInfo1 = new SyndicateDataColumnInfo();
        columnInfo1.setColumnSize(100);
        columnInfo1.setColumnType("NUMBER");
        metaData.add(columnInfo1);
        bean.setMetaData(metaData);

        classUnderTest.validateData(bean, errors);

        assertThat(errors.size(), is(1));
        assertThat(errors.get(0).getField(), is("data"));
        assertThat(errors.get(0).getMessage(), is("Data in cell[1][1] : Cell does not comply to the column type"));
    }

    @Test
    public void decimalDataValidation() {
        bean.setTotalRows(1L);
        ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
        Map<String, String> row = new LinkedHashMap<>();
        row.put("column1", "53.2");
        data.add(row);
        bean.setSyndicateVersionData(data);

        List<SyndicateDataColumnInfo> metaData = new ArrayList<>();
        SyndicateDataColumnInfo columnInfo1 = new SyndicateDataColumnInfo();
        columnInfo1.setColumnSize(5);
        columnInfo1.setPrecision(2);
        columnInfo1.setColumnType("DOUBLE");
        metaData.add(columnInfo1);
        bean.setMetaData(metaData);

        classUnderTest.validateData(bean, errors);

        assertThat(errors.size(), is(0));

        data = new ArrayList<Map<String, String>>();
        row = new LinkedHashMap<>();
        row.put("column1", "53K.2");
        data.add(row);
        bean.setSyndicateVersionData(data);

        errors = new ArrayList<ValidationError>();
        classUnderTest.validateData(bean, errors);

        assertThat(errors.get(0).getMessage(), is("Data in cell[1][1] : Character present between data."));
        assertThat(errors.get(1).getMessage(), is("Data in cell[1][1] : Invalid Data."));

        data = new ArrayList<Map<String, String>>();
        row = new LinkedHashMap<>();
        row.put("column1", "536.256");
        data.add(row);
        bean.setSyndicateVersionData(data);

        errors = new ArrayList<ValidationError>();
        classUnderTest.validateData(bean, errors);

        assertThat(errors.get(0).getMessage(),
                is("Data in cell[1][1] : Size of either decimal value or precision value invalid."));
        assertThat(errors.get(1).getMessage(), is("Data in cell[1][1] : Invalid Data."));
    }
}
