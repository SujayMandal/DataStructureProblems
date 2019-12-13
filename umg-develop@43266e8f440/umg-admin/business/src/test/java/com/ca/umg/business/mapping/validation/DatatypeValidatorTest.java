package com.ca.umg.business.mapping.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ca.umg.business.mapping.validation.core.AbstractValidator;
import com.ca.umg.business.mapping.validation.core.MappingValidatorConstants;
import com.ca.umg.business.mapping.validation.core.MappingValidatorContainer;
import com.ca.umg.business.mid.extraction.info.DatatypeInfo;
import com.ca.umg.business.mid.extraction.info.MappingTypes;
import com.ca.umg.business.mid.extraction.info.MidIOInfo;
import com.ca.umg.business.mid.extraction.info.MidMapping;
import com.ca.umg.business.mid.extraction.info.MidParamInfo;
import com.ca.umg.business.mid.extraction.info.TidIOInfo;
import com.ca.umg.business.mid.extraction.info.TidParamInfo;
import com.ca.umg.business.validation.ValidationError;

public class DatatypeValidatorTest extends AbstractMappingValidatorTest {

    private DatatypeInfo stringDataType;
    private DatatypeInfo integerDataType;
    private DatatypeInfo objectDataType;

    private MidParamInfo midParamInfoA;
    private MidParamInfo midParamInfoC;
    private MidParamInfo midParamInfoD;

    private TidParamInfo tidParamInfoA;
    private TidParamInfo tidParamInfoB;
    private TidParamInfo tidParamInfoC;
    private TidParamInfo tidParamInfoD;

    private MidIOInfo midIOInfo;
    private TidIOInfo tidIOInfo;

    @Before
    public void prepareMidMappings() {

        stringDataType = this.prepareDatatypeInfo(Datatype.STRING);
        integerDataType = this.prepareDatatypeInfo(Datatype.INTEGER);
        objectDataType = this.prepareDatatypeInfo(Datatype.OBJECT);

        midParamInfoC = this.prepareMidParamInfo(false, false, 3, "C", objectDataType);
        midParamInfoD = this.prepareMidParamInfo(false, true, 4, "D", integerDataType);
        midParamInfoC.setChildren(new ArrayList<MidParamInfo>());
        midParamInfoC.getChildren().add(midParamInfoD);

        midParamInfoA = this.prepareMidParamInfo(false, false, 1, "A", stringDataType);

        tidParamInfoC = this.prepareTidParamInfo(false, 3, "C", objectDataType);
        tidParamInfoD = this.prepareTidParamInfo(false, 4, "D", integerDataType);
        tidParamInfoC.setChildren(new ArrayList<TidParamInfo>());
        tidParamInfoC.getChildren().add(tidParamInfoD);
        tidParamInfoA = this.prepareTidParamInfo(false, 1, "A", stringDataType);

        tidParamInfoB = this.prepareTidParamInfo(false, 1, "B", integerDataType);

        midIOInfo = new MidIOInfo();
        midIOInfo.setMidInput(new ArrayList<MidParamInfo>());
        midIOInfo.getMidInput().add(midParamInfoA);
        midIOInfo.getMidInput().add(midParamInfoC);

        tidIOInfo = new TidIOInfo();
        tidIOInfo.setTidInput(new ArrayList<TidParamInfo>());
        tidIOInfo.getTidInput().add(tidParamInfoA);
        tidIOInfo.getTidInput().add(tidParamInfoB);
        tidIOInfo.getTidInput().add(tidParamInfoC);

    }

    @Test
    public void testDataTypeMappings() {
        List<ValidationError> errors = new ArrayList<>();
        Map<String,List<String>> mappingToDelete = new HashMap<>();
        AbstractValidator validator = validatorFactory.getValidator(MappingValidatorConstants.DATA_TYPE_VALIDATOR);

        List<MidMapping> midInputMappings = new ArrayList<>();

        MidMapping midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoA.getApiName());

        MidMapping midMappingC = new MidMapping();
        midMappingC.setMappedTo(midParamInfoC.getApiName());

        List<String> tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoA.getApiName());
        midMapping.setInputs(tidInputs);

        List<String> tidInputsC = new ArrayList<>();
        tidInputsC.add(tidParamInfoC.getApiName());
        midMappingC.setInputs(tidInputsC);

        midInputMappings.add(midMapping);
        midInputMappings.add(midMappingC);

        MappingValidatorContainer mappingValidatorContainer = mappingValidator.prepareValidatorContainer(midIOInfo, tidIOInfo,
                midInputMappings, errors);

        for (MidMapping midMappingElement : midInputMappings) {
            mappingValidatorContainer.setMidMapping(midMappingElement);
            Assert.assertTrue(validator.validate(mappingValidatorContainer, errors, mappingToDelete));
        }
        Assert.assertEquals(0, errors.size());

    }

    @Test
    public void testDifferentDataTypeMappings() {
        List<ValidationError> errors = new ArrayList<>();
        Map<String,List<String>> mappingToDelete = new HashMap<>();
        AbstractValidator validator = validatorFactory.getValidator(MappingValidatorConstants.DATA_TYPE_VALIDATOR);

        List<MidMapping> midInputMappings = new ArrayList<>();

        MidMapping midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoA.getApiName());

        List<String> tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoA.getApiName());
        tidInputs.add(tidParamInfoB.getApiName());
        midMapping.setInputs(tidInputs);

        midInputMappings.add(midMapping);

        MappingValidatorContainer mappingValidatorContainer = mappingValidator.prepareValidatorContainer(midIOInfo, tidIOInfo,
                midInputMappings, errors);

        for (MidMapping midMappingElement : midInputMappings) {
            mappingValidatorContainer.setMidMapping(midMappingElement);
            Assert.assertFalse(validator.validate(mappingValidatorContainer, errors,mappingToDelete));
        }
        Assert.assertEquals(1, errors.size());
        
    }
    
    //@Ignore
    @Test
    public void testInfoObjects() {

        //testing for MidMapping
        
        MidMapping midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoA.getApiName());

        List<String> tidInputs = new ArrayList<>();
        midMapping.setInputs(tidInputs);
        
        MappingTypes mappingTypes= midMapping.getMappingType();
        assertNotNull(mappingTypes);
        assertEquals(MappingTypes.NONE, mappingTypes);
        
        tidInputs.add(tidParamInfoA.getApiName());
        tidInputs.add(tidParamInfoB.getApiName());
        midMapping.setInputs(tidInputs);
        
        mappingTypes= midMapping.getMappingType();
        assertNotNull(mappingTypes);
        assertEquals(MappingTypes.OPTIONAL, mappingTypes);
        
        tidInputs.add(tidParamInfoC.getApiName());
        midMapping.setInputs(tidInputs);
        
        mappingTypes= midMapping.getMappingType();
        assertNotNull(mappingTypes);
        assertEquals(MappingTypes.INVALID, mappingTypes);
        
        //testing for MidParamInfo
        
        MidParamInfo midParamInfoE = this.prepareMidParamInfo(false, false, 1, "A", stringDataType);
        MidParamInfo midParamInfoF = this.prepareMidParamInfo(false, false, 3, "C", objectDataType);
        assertTrue(midParamInfoE.equals(midParamInfoA));
        assertTrue(midParamInfoE.equals(midParamInfoE));
        assertFalse(midParamInfoE.equals(midParamInfoC));
        assertFalse(midParamInfoF.equals(midParamInfoC));
        midParamInfoF.setChildren(new ArrayList<MidParamInfo>());
        assertFalse(midParamInfoC.equals(midParamInfoF));
        midParamInfoF.getChildren().add(midParamInfoE); 
        //assertFalse(midParamInfoC.equals(midParamInfoF));
        midParamInfoF.hashCode();
        midParamInfoF.setValue("test");
        assertEquals("test",midParamInfoF.getValue());
        
        //testing TidIOInfo
        
        MidIOInfo midIOInfo1 = new MidIOInfo();
        midIOInfo1.setMidInput(new ArrayList<MidParamInfo>());
        midIOInfo1.getMidInput().add(midParamInfoA);
        midIOInfo1.getMidInput().add(midParamInfoC);
        midIOInfo1.setMidOutput(new ArrayList<MidParamInfo>());
        midIOInfo1.getMidOutput().add(midParamInfoD);

        TidIOInfo tidIOInfo1 = new TidIOInfo();
        tidIOInfo1.setTidInput(new ArrayList<TidParamInfo>());
        tidIOInfo1.getTidInput().add(tidParamInfoA);
        tidIOInfo1.getTidInput().add(tidParamInfoB);
        tidIOInfo1.getTidInput().add(tidParamInfoC);
        
        TidIOInfo result = tidIOInfo1.copy(midIOInfo1);
        assertEquals(2,result.getTidInput().size());
        assertEquals(1,result.getTidOutput().size());
        
        TidIOInfo tidIOInfo2 = tidIOInfo1.copy(tidIOInfo1);
        assertNotNull(tidIOInfo2);
        
        //testing paraminfo
        
        TidParamInfo tidParamInfoTest = prepareTidParamInfo();
        assertNotNull(tidParamInfoTest);
        assertEquals("1",tidParamInfoTest.getExpressionId());
        assertEquals("sqlId",tidParamInfoTest.getSqlId());
        assertEquals(Boolean.FALSE,tidParamInfoTest.isExprsnOutput());
        assertEquals(Boolean.FALSE,tidParamInfoTest.isSqlOutput());
        assertEquals("value",tidParamInfoTest.getValue());
        assertEquals(Boolean.FALSE,tidParamInfoTest.isMapped());
        assertEquals("dataFormat",tidParamInfoTest.getDataFormat());
        assertEquals(1,tidParamInfoTest.getSize());
        assertEquals("name",tidParamInfoTest.getText());
        assertEquals(1,tidParamInfoTest.getPrecision());
        assertEquals(Boolean.TRUE,tidParamInfoTest.isUserSelected());
        
        assertTrue(tidParamInfoTest.equals(tidParamInfoTest));
        assertFalse(tidParamInfoTest.equals(null));
        assertFalse(tidParamInfoTest.equals(tidParamInfoA));
        tidParamInfoTest.setDatatype(stringDataType);
        assertFalse(tidParamInfoTest.equals(tidParamInfoA));
        
        tidParamInfoTest.setMandatory(Boolean.FALSE);
        tidParamInfoTest.setName(null);
        assertFalse(tidParamInfoTest.equals(tidParamInfoA));
        
        tidParamInfoTest.setName("name");
        assertFalse(tidParamInfoTest.equals(tidParamInfoA));
        
        tidParamInfoTest.setName("A");
        tidParamInfoTest.setSyndicate(Boolean.TRUE);
        assertFalse(tidParamInfoTest.equals(tidParamInfoA));
        
        tidParamInfoTest.setDatatype(null);
        tidParamInfoTest.setMandatory(Boolean.TRUE);
        tidParamInfoTest.setName(null);
        tidParamInfoTest.hashCode();
    }

}
