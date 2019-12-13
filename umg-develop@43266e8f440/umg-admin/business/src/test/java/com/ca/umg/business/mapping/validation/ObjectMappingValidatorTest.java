package com.ca.umg.business.mapping.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ca.umg.business.mapping.validation.core.MappingValidatorConstants;
import com.ca.umg.business.mid.extraction.info.DatatypeInfo;
import com.ca.umg.business.mid.extraction.info.MidIOInfo;
import com.ca.umg.business.mid.extraction.info.MidMapping;
import com.ca.umg.business.mid.extraction.info.MidParamInfo;
import com.ca.umg.business.mid.extraction.info.TidExpressionInfo;
import com.ca.umg.business.mid.extraction.info.TidIOInfo;
import com.ca.umg.business.mid.extraction.info.TidParamInfo;
import com.ca.umg.business.mid.extraction.info.TidSqlInfo;
import com.ca.umg.business.validation.ValidationError;

public class ObjectMappingValidatorTest extends AbstractMappingValidatorTest {
    private DatatypeInfo stringDataType;
    private DatatypeInfo integerDataType;
    private DatatypeInfo objectDataType;

    private MidParamInfo midParamInfoA;
    private MidParamInfo midParamInfoB;
    private MidParamInfo midParamInfoC;
    private MidParamInfo midParamInfoD;
    private MidParamInfo midParamInfoE;
    private MidParamInfo midParamInfoF;
    private MidParamInfo midParamInfoG;

    private TidParamInfo tidParamInfoA;
    private TidParamInfo tidParamInfoB;
    private TidParamInfo tidParamInfoC;
    private TidParamInfo tidParamInfoD;
    private TidParamInfo tidParamInfoE;
    private TidParamInfo tidParamInfoF;
    private TidParamInfo tidParamInfoG;
    private TidParamInfo tidParamInfoH;
    private TidParamInfo tidParamInfoI;
    private TidParamInfo tidParamInfoJ;

    private TidSqlInfo sqOne;
    private TidExpressionInfo expressionInfo;

    private MidIOInfo midIOInfo;
    private TidIOInfo tidIOInfo;
    private static List<String> validatorList;
    static {
        validatorList = new ArrayList<>();
        validatorList.add(MappingValidatorConstants.NULL_VALIDATOR);
        validatorList.add(MappingValidatorConstants.DATA_TYPE_VALIDATOR);
        validatorList.add(MappingValidatorConstants.INPUT_MAPPING_VALIDATOR);
    }

    @Before
    public void prepareMidMappings() {

        stringDataType = this.prepareDatatypeInfo(Datatype.STRING);
        integerDataType = this.prepareDatatypeInfo(Datatype.INTEGER);
        objectDataType = this.prepareDatatypeInfo(Datatype.OBJECT);

        midIOInfo = new MidIOInfo();
        midIOInfo.setMidInput(new ArrayList<MidParamInfo>());
        tidIOInfo = new TidIOInfo();
        tidIOInfo.setTidInput(new ArrayList<TidParamInfo>());
        tidIOInfo.setTidSystemInput(new ArrayList<TidParamInfo>());
        tidIOInfo.setSqlInfos(new ArrayList<TidSqlInfo>());
        tidIOInfo.setExpressionInfos(new ArrayList<TidExpressionInfo>());
        prepareMidParams();
        prepareTidParams();
        prepareSqlParam();
        prepareExpParam();
    }

    private void prepareMidParams() {
        midParamInfoA = this.prepareMidParamInfo(false, false, 1, "A", objectDataType);
        midIOInfo.getMidInput().add(midParamInfoA);
        midParamInfoB = this.prepareMidParamInfo(true, true, 2, "B", stringDataType);
        midParamInfoC = this.prepareMidParamInfo(true, true, 3, "C", objectDataType);
        midParamInfoA.setChildren(new ArrayList<MidParamInfo>());
        midParamInfoA.getChildren().add(midParamInfoB);
        midParamInfoA.getChildren().add(midParamInfoC);

        midParamInfoD = this.prepareMidParamInfo(false, true, 4, "D", integerDataType);
        midParamInfoC.setChildren(new ArrayList<MidParamInfo>());
        midParamInfoC.getChildren().add(midParamInfoD);

        midParamInfoE = this.prepareMidParamInfo(true, true, 5, "E", objectDataType);
        midIOInfo.getMidInput().add(midParamInfoE);

        midParamInfoF = this.prepareMidParamInfo(true, true, 5, "F", integerDataType);
        midParamInfoE.setChildren(new ArrayList<MidParamInfo>());
        midParamInfoE.getChildren().add(midParamInfoF);

        midParamInfoG = this.prepareMidParamInfo(false, true, 5, "G", stringDataType);
        midIOInfo.getMidInput().add(midParamInfoG);
    }

    private void prepareTidParams() {
        tidParamInfoA = this.prepareTidParamInfo(true, 1, "A", objectDataType);
        tidIOInfo.getTidInput().add(tidParamInfoA);
        tidParamInfoB = this.prepareTidParamInfo(true, 2, "B", stringDataType);
        tidParamInfoC = this.prepareTidParamInfo(false, 3, "C", objectDataType);

        tidParamInfoA.setChildren(new ArrayList<TidParamInfo>());
        tidParamInfoA.getChildren().add(tidParamInfoB);
        tidParamInfoA.getChildren().add(tidParamInfoC);

        tidParamInfoD = this.prepareTidParamInfo(false, 4, "D", integerDataType);
        tidParamInfoC.setChildren(new ArrayList<TidParamInfo>());
        tidParamInfoC.getChildren().add(tidParamInfoD);

        tidParamInfoE = this.prepareTidParamInfo(true, 5, "E", stringDataType);
        tidIOInfo.getTidInput().add(tidParamInfoE);
        tidParamInfoF = this.prepareTidParamInfo(false, 6, "F", stringDataType);
        tidParamInfoE.setChildren(new ArrayList<TidParamInfo>());
        tidParamInfoE.getChildren().add(tidParamInfoF);

        tidParamInfoG = this.prepareTidParamInfo(true, 7, "G", integerDataType);
        tidIOInfo.getTidInput().add(tidParamInfoG);

        tidParamInfoH = this.prepareTidParamInfo(true, 8, "H", stringDataType);
        tidIOInfo.getTidSystemInput().add(tidParamInfoH);

        tidParamInfoI = this.prepareTidParamInfo(false, 9, "I", stringDataType);
        tidIOInfo.getTidInput().add(tidParamInfoI);
        
        tidParamInfoJ = this.prepareTidParamInfo(true, 10, "J", stringDataType);
        tidIOInfo.getTidSystemInput().add(tidParamInfoJ);
    }

    private void prepareSqlParam() {

        List<TidParamInfo> queryInputList = new ArrayList<>();
        queryInputList.add(tidParamInfoI);
        // SQ1 output tidParamInfoC and tidParamInfoD List<TidParamInfo>
        List<TidParamInfo> queryOutputList = new ArrayList<>();
        queryOutputList.add(tidParamInfoH);

        sqOne = this.prepareTidSqlInfo("1", "SQ1", queryInputList, queryOutputList);
        tidIOInfo.getSqlInfos().add(sqOne);
    }

    private void prepareExpParam() {

        List<TidParamInfo> expInputList = new ArrayList<>();
        expInputList.add(tidParamInfoI);
        // SQ1 output tidParamInfoC and tidParamInfoD List<TidParamInfo>
        List<TidParamInfo> expOutputList = new ArrayList<>();
        expOutputList.add(tidParamInfoJ);

        expressionInfo = this.prepareTidExpInfo("1", "exp1", expInputList, expOutputList);
        tidIOInfo.getExpressionInfos().add(expressionInfo);
    }

    @Test
    public void testObjectMapping() {
        List<MidMapping> midInputMappings = new ArrayList<>();
        Map<String,List<String>> mappingToDelete = new HashMap<>();

        MidMapping midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoA.getFlatenedName());

        List<String> tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoA.getFlatenedName());
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);

        midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoF.getFlatenedName());

        tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoG.getFlatenedName());
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);

        midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoG.getFlatenedName());

        tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoF.getFlatenedName());
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);
        List<ValidationError> errors = mappingValidator.validateMidMapping(midIOInfo, tidIOInfo, midInputMappings, validatorList,mappingToDelete);
        Assert.assertEquals(0, errors.size());
    }

    @Test
    public void testObjectMappingWithPrimitiveTidMapping() {
        List<MidMapping> midInputMappings = new ArrayList<>();
        Map<String,List<String>> mappingToDelete = new HashMap<>();

        MidMapping midMapping;
        List<String> tidInputs;

        midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoA.getFlatenedName());

        tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoA.getFlatenedName());
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);

        midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoB.getFlatenedName());

        tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoF.getFlatenedName());
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);

        midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoF.getFlatenedName());

        tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoG.getFlatenedName());
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);

        midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoG.getFlatenedName());

        tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoF.getFlatenedName());
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);
        List<ValidationError> errors = mappingValidator.validateMidMapping(midIOInfo, tidIOInfo, midInputMappings, validatorList,mappingToDelete);
        Assert.assertEquals(0, errors.size());
    }

    @Test
    public void testObjectMappingWithSystemTidMapping() {
        List<MidMapping> midInputMappings = new ArrayList<>();
        Map<String,List<String>> mappingToDelete = new HashMap<>();

        MidMapping midMapping;
        List<String> tidInputs;

        midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoA.getFlatenedName());

        tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoA.getFlatenedName());
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);

        midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoB.getFlatenedName());

        tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoH.getFlatenedName());
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);

        midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoF.getFlatenedName());

        tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoG.getFlatenedName());
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);

        midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoG.getFlatenedName());

        tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoF.getFlatenedName());
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);
        List<ValidationError> errors = mappingValidator.validateMidMapping(midIOInfo, tidIOInfo, midInputMappings, validatorList,mappingToDelete);
        Assert.assertEquals(1, errors.size());
    }

    @Test
    public void testObjectErrorMappingWithDifferentObjectStructure() {
        List<MidMapping> midInputMappings = new ArrayList<>();
        Map<String,List<String>> mappingToDelete = new HashMap<>();

        MidMapping midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoA.getFlatenedName());

        List<String> tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoE.getFlatenedName());
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);

        midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoE.getFlatenedName());

        tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoA.getFlatenedName());
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);

        midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoG.getFlatenedName());

        tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoG.getFlatenedName());
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);
        List<ValidationError> errors = mappingValidator.validateMidMapping(midIOInfo, tidIOInfo, midInputMappings, validatorList,mappingToDelete);
        Assert.assertEquals(2, errors.size());
    }

    @Test
    public void testObjectErrorMappingWithoutObjectMapping() {
        List<MidMapping> midInputMappings = new ArrayList<>();
        Map<String,List<String>> mappingToDelete = new HashMap<>();

        MidMapping midMapping;
        List<String> tidInputs;

        midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoF.getFlatenedName());

        tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoG.getFlatenedName());
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);

        midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoG.getFlatenedName());

        tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoF.getFlatenedName());
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);
        List<ValidationError> errors = mappingValidator.validateMidMapping(midIOInfo, tidIOInfo, midInputMappings, validatorList,mappingToDelete);
        Assert.assertEquals(1, errors.size());
    }

    @Test
    public void testObjectErrorMappingWithOneOptionalTidToMandatoryObjectMid() {
        List<MidMapping> midInputMappings = new ArrayList<>();
        Map<String,List<String>> mappingToDelete = new HashMap<>();

        MidMapping midMapping;
        List<String> tidInputs;

        midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoB.getFlatenedName());

        tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoF.getFlatenedName());
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);

        midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoF.getFlatenedName());

        tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoG.getFlatenedName());
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);

        midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoG.getFlatenedName());

        tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoF.getFlatenedName());
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);
        List<ValidationError> errors = mappingValidator.validateMidMapping(midIOInfo, tidIOInfo, midInputMappings, validatorList,mappingToDelete);
        Assert.assertEquals(1, errors.size());
    }
}
