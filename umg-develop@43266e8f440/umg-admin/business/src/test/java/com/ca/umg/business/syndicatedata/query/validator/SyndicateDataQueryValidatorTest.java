package com.ca.umg.business.syndicatedata.query.validator;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.mid.extraction.info.DatatypeInfo;
import com.ca.umg.business.syndicatedata.entity.SyndicateDataQuery;
import com.ca.umg.business.syndicatedata.entity.SyndicateDataQueryInput;
import com.ca.umg.business.syndicatedata.entity.SyndicateDataQueryOutput;
import com.ca.umg.business.syndicatedata.util.QueryResultTypes;

public class SyndicateDataQueryValidatorTest {

    SyndicateDataQueryValidator syndicateDataQueryValidator = new SyndicateDataQueryValidator();

    @Test
    public void validateInputParametersTest() {
        SyndicateDataQuery synDataQryInfo = new SyndicateDataQuery();
        Set<SyndicateDataQueryInput> inputParameters = populateInputParams();
        synDataQryInfo.setInputParameters(inputParameters);
        try {
            syndicateDataQueryValidator.validateInputParameters(synDataQryInfo);
        } catch (BusinessException e) {
            fail();
        }
        assertTrue(true);
    }

    @Test
    public void validateInputParametersThrowingDateTimeTypeExceptionTest() {
        SyndicateDataQuery synDataQryInfo = new SyndicateDataQuery();
        Set<SyndicateDataQueryInput> inputParameters = new HashSet<SyndicateDataQueryInput>();
        SyndicateDataQueryInput syndicateDataQueryInput = getDateTimeTypeInputParam();
        syndicateDataQueryInput.setSampleValue("xyz");
        inputParameters.add(syndicateDataQueryInput);
        synDataQryInfo.setInputParameters(inputParameters);
        try {
            syndicateDataQueryValidator.validateInputParameters(synDataQryInfo);
        } catch (BusinessException bse) {
            assertThat(bse.getCode(), is(BusinessExceptionCodes.BSE000080));
        }
    }

    @Test
    public void validateInputParametersThrowingDateTypeExceptionTest() {
        SyndicateDataQuery synDataQryInfo = new SyndicateDataQuery();
        Set<SyndicateDataQueryInput> inputParameters = new HashSet<SyndicateDataQueryInput>();
        SyndicateDataQueryInput syndicateDataQueryInput = getDateTypeInputParam();
        syndicateDataQueryInput.setSampleValue("xyz");
        inputParameters.add(syndicateDataQueryInput);
        synDataQryInfo.setInputParameters(inputParameters);
        try {
            syndicateDataQueryValidator.validateInputParameters(synDataQryInfo);
        } catch (BusinessException bse) {
            assertThat(bse.getCode(), is(BusinessExceptionCodes.BSE000080));
        }
    }

    @Test
    public void validateInputParametersThrowingBooleanTypeExceptionTest() {
        SyndicateDataQuery synDataQryInfo = new SyndicateDataQuery();
        Set<SyndicateDataQueryInput> inputParameters = new HashSet<SyndicateDataQueryInput>();
        SyndicateDataQueryInput syndicateDataQueryInput = getBooleanTypeInputParam();
        syndicateDataQueryInput.setSampleValue("xyz");
        inputParameters.add(syndicateDataQueryInput);
        synDataQryInfo.setInputParameters(inputParameters);
        try {
            syndicateDataQueryValidator.validateInputParameters(synDataQryInfo);
        } catch (BusinessException bse) {
            assertThat(bse.getCode(), is(BusinessExceptionCodes.BSE000080));
        }
    }

    @Test
    public void validateInputParametersThrowingDoubleTypeExceptionTest() {
        SyndicateDataQuery synDataQryInfo = new SyndicateDataQuery();
        Set<SyndicateDataQueryInput> inputParameters = new HashSet<SyndicateDataQueryInput>();
        SyndicateDataQueryInput syndicateDataQueryInput = getDoubleTypeInputParam();
        syndicateDataQueryInput.setSampleValue("xyz");
        inputParameters.add(syndicateDataQueryInput);
        synDataQryInfo.setInputParameters(inputParameters);
        try {
            syndicateDataQueryValidator.validateInputParameters(synDataQryInfo);
        } catch (BusinessException bse) {
            assertThat(bse.getCode(), is(BusinessExceptionCodes.BSE000080));
        }
    }

    @Test
    public void validateInputParametersThrowingIntegerTypeExceptionTest() {
        SyndicateDataQuery synDataQryInfo = new SyndicateDataQuery();
        Set<SyndicateDataQueryInput> inputParameters = new HashSet<SyndicateDataQueryInput>();
        SyndicateDataQueryInput syndicateDataQueryInput = getIntegerTypeInputParam();
        syndicateDataQueryInput.setSampleValue("12-45");
        inputParameters.add(syndicateDataQueryInput);
        synDataQryInfo.setInputParameters(inputParameters);
        try {
            syndicateDataQueryValidator.validateInputParameters(synDataQryInfo);
        } catch (BusinessException bse) {
            assertThat(bse.getCode(), is(BusinessExceptionCodes.BSE000080));
        }
    }

    @Test
    public void validateReferencesTestThrowingError() {
        Map<String, Boolean> references = new HashMap<String, Boolean>();
        references.put("1", true);
        references.put("2", false);
        try {
            syndicateDataQueryValidator.validateReferences(references);
        } catch (BusinessException bse) {
            assertThat(bse.getCode(), is(BusinessExceptionCodes.BSE000056));
        }
    }

    @Test
    public void validateReferencesTest() {
        Map<String, Boolean> references = new HashMap<String, Boolean>();
        references.put("1", false);
        references.put("2", false);
        try {
            syndicateDataQueryValidator.validateReferences(references);
        } catch (BusinessException bse) {
            fail();
        }
        assertTrue(true);
    }

    @Test
    public void validateQReturnTypesTestForSingleRow_SingleData() {
        SyndicateDataQuery synDataQryInfo = new SyndicateDataQuery();
        synDataQryInfo.setRowType(QueryResultTypes.SINGLEROW.getDatatype());
        synDataQryInfo.setDataType(DatatypeInfo.Datatype.STRING.getDatatype());
        Set<SyndicateDataQueryOutput> outputParameters = new HashSet<SyndicateDataQueryOutput>();
        outputParameters.add(getStringTypeSyndicateDataQueryOutput());
        synDataQryInfo.setOutputParameters(outputParameters);
        try {
            syndicateDataQueryValidator.validateQReturnTypes(synDataQryInfo);
        } catch (BusinessException bse) {
            assertThat(bse.getCode(), is(BusinessExceptionCodes.BSE000074));
        }
    }

    @Test
    public void validateQReturnTypesTestForSingleRow_MultipleData() {
        SyndicateDataQuery synDataQryInfo = new SyndicateDataQuery();
        synDataQryInfo.setRowType(QueryResultTypes.SINGLEROW.getDatatype());
        synDataQryInfo.setDataType(DatatypeInfo.Datatype.STRING.getDatatype());
        Set<SyndicateDataQueryOutput> outputParameters = new HashSet<SyndicateDataQueryOutput>();
        outputParameters.add(getStringTypeSyndicateDataQueryOutput());
        outputParameters.add(getIntegerTypeSyndicateDataQueryOutput());
        synDataQryInfo.setOutputParameters(outputParameters);
        try {
            syndicateDataQueryValidator.validateQReturnTypes(synDataQryInfo);
        } catch (BusinessException bse) {
            assertThat(bse.getCode(), is(BusinessExceptionCodes.BSE000074));
        }
    }

    @Test
    public void validateQReturnTypesTestForMultipleRow() {
        SyndicateDataQuery synDataQryInfo = new SyndicateDataQuery();
        synDataQryInfo.setRowType(QueryResultTypes.MULTIPLEROW.getDatatype());
        synDataQryInfo.setDataType(DatatypeInfo.Datatype.STRING.getDatatype());
        Set<SyndicateDataQueryOutput> outputParameters = new HashSet<SyndicateDataQueryOutput>();
        outputParameters.add(getStringTypeSyndicateDataQueryOutput());
        synDataQryInfo.setOutputParameters(outputParameters);
        try {
            syndicateDataQueryValidator.validateQReturnTypes(synDataQryInfo);
        } catch (BusinessException bse) {
            assertThat(bse.getCode(), is(BusinessExceptionCodes.BSE000074));
        }
    }

    @Test
    public void validateQReturnTypesTestForMultipleRowWithPrimitiveType() {
        SyndicateDataQuery synDataQryInfo = new SyndicateDataQuery();
        synDataQryInfo.setRowType(QueryResultTypes.MULTIPLEROW.getDatatype());
        synDataQryInfo.setDataType(QueryResultTypes.PRIMITIVE.getDatatype());
        Set<SyndicateDataQueryOutput> outputParameters = new HashSet<SyndicateDataQueryOutput>();
        outputParameters.add(getStringTypeSyndicateDataQueryOutput());
        synDataQryInfo.setOutputParameters(outputParameters);
        try {
            syndicateDataQueryValidator.validateQReturnTypes(synDataQryInfo);
        } catch (BusinessException bse) {
            assertThat(bse.getCode(), is(BusinessExceptionCodes.BSE000074));
        }
    }

    @Test
    public void validateQReturnTypesTestForSingleRowWithPrimitiveType() {
        SyndicateDataQuery synDataQryInfo = new SyndicateDataQuery();
        synDataQryInfo.setRowType(QueryResultTypes.SINGLEROW.getDatatype());
        synDataQryInfo.setDataType(QueryResultTypes.PRIMITIVE.getDatatype());
        Set<SyndicateDataQueryOutput> outputParameters = new HashSet<SyndicateDataQueryOutput>();
        outputParameters.add(getStringTypeSyndicateDataQueryOutput());
        synDataQryInfo.setOutputParameters(outputParameters);
        try {
            syndicateDataQueryValidator.validateQReturnTypes(synDataQryInfo);
        } catch (BusinessException bse) {
            assertThat(bse.getCode(), is(BusinessExceptionCodes.BSE000074));
        }
    }

    private Set<SyndicateDataQueryInput> populateInputParams() {
        Set<SyndicateDataQueryInput> inputParameters = new HashSet<SyndicateDataQueryInput>();
        inputParameters.add(getBooleanTypeInputParam());
        inputParameters.add(getDateTimeTypeInputParam());
        inputParameters.add(getDateTypeInputParam());
        inputParameters.add(getDoubleTypeInputParam());
        inputParameters.add(getIntegerTypeInputParam());
        return inputParameters;
    }

    private SyndicateDataQueryInput getQueryInput(String name, String sampleValue, String dataType, String dataTypeFormat) {
        SyndicateDataQueryInput syndDataQryIp = new SyndicateDataQueryInput();
        syndDataQryIp.setName(name);
        syndDataQryIp.setSampleValue(sampleValue);
        syndDataQryIp.setDataType(dataType);
        syndDataQryIp.setDataTypeFormat(dataTypeFormat);
        return syndDataQryIp;
    }

    private SyndicateDataQueryOutput getQueryOutput(String name, String dataType) {
        SyndicateDataQueryOutput syndicateDataQueryOutput = new SyndicateDataQueryOutput();
        syndicateDataQueryOutput.setName(name);
        syndicateDataQueryOutput.setDataType(dataType);
        return syndicateDataQueryOutput;
    }

    private SyndicateDataQueryOutput getStringTypeSyndicateDataQueryOutput() {
        return getQueryOutput("StringType", DatatypeInfo.Datatype.STRING.getDatatype());
    }

    private SyndicateDataQueryOutput getIntegerTypeSyndicateDataQueryOutput() {
        return getQueryOutput("IntegerType", DatatypeInfo.Datatype.INTEGER.getDatatype());
    }

    private SyndicateDataQueryInput getDoubleTypeInputParam() {
        return getQueryInput("DoubleType", "2.0", DatatypeInfo.Datatype.DOUBLE.getDatatype(), "");
    }

    private SyndicateDataQueryInput getIntegerTypeInputParam() {
        return getQueryInput("IntegerType", "2", DatatypeInfo.Datatype.INTEGER.getDatatype(), "");
    }

    private SyndicateDataQueryInput getDateTypeInputParam() {
        return getQueryInput("DateType", "23-JAN-1980", DatatypeInfo.Datatype.DATE.getDatatype(), "DD-MMM-YYYY");
    }

    private SyndicateDataQueryInput getDateTimeTypeInputParam() {
        return getQueryInput("DateTimeType", "JAN-23-1980 15:23", DatatypeInfo.Datatype.DATETIME.getDatatype(), "MMM-DD-YYYY HH:MM");
    }

    private SyndicateDataQueryInput getBooleanTypeInputParam() {
        return getQueryInput("BooleanType", "true", DatatypeInfo.Datatype.BOOLEAN.getDatatype(),"");
    }
}
