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
import com.ca.umg.business.mid.extraction.info.TidIOInfo;
import com.ca.umg.business.mid.extraction.info.TidParamInfo;
import com.ca.umg.business.mid.extraction.info.TidSqlInfo;
import com.ca.umg.business.validation.ValidationError;

public class InputMappingValidatorTest extends AbstractMappingValidatorTest {

    private DatatypeInfo stringDataType;
    private DatatypeInfo integerDataType;

    private MidParamInfo midParamInfoA;
    private MidParamInfo midParamInfoB;
    private MidParamInfo midParamInfoC;
    private MidParamInfo midParamInfoD;
    private MidParamInfo midParamInfoE;

    private TidParamInfo tidParamInfoA;
    private TidParamInfo tidParamInfoB;
    private TidParamInfo tidParamInfoC;
    private TidParamInfo tidParamInfoD;
    private TidParamInfo tidParamInfoE;
    private TidParamInfo tidParamInfoF;
    private TidParamInfo tidParamInfoG;
    private TidParamInfo tidParamInfoH;

    private TidSqlInfo sqOne;
    private TidSqlInfo sqTwo;
    private TidSqlInfo sqThree;

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
        midIOInfo = new MidIOInfo();
        midIOInfo.setMidInput(new ArrayList<MidParamInfo>());
        tidIOInfo = new TidIOInfo();
        tidIOInfo.setTidInput(new ArrayList<TidParamInfo>());
        tidIOInfo.setTidSystemInput(new ArrayList<TidParamInfo>());
        tidIOInfo.setSqlInfos(new ArrayList<TidSqlInfo>());

        prepareMidParams();
        prepareTidParams();
        prepareSqlParam();

    }

    private void prepareSqlParam() {
        List<TidParamInfo> queryInputList = new ArrayList<>();
        queryInputList.add(tidParamInfoA);
        queryInputList.add(tidParamInfoB);

        List<TidParamInfo> queryOutputList = new ArrayList<>();
        queryOutputList.add(tidParamInfoC);
        queryOutputList.add(tidParamInfoD);

        sqOne = this.prepareTidSqlInfo("1", "SQ1", queryInputList, queryOutputList);

        tidIOInfo.getSqlInfos().add(sqOne);

        queryInputList = new ArrayList<>();
        queryInputList.add(tidParamInfoA);
        queryInputList.add(tidParamInfoE);

        queryOutputList = new ArrayList<>();
        queryOutputList.add(tidParamInfoF);

        sqTwo = this.prepareTidSqlInfo("2", "SQ2", queryInputList, queryOutputList);
        tidIOInfo.getSqlInfos().add(sqTwo);

        queryInputList = new ArrayList<>();
        queryInputList.add(tidParamInfoF);

        queryOutputList = new ArrayList<>();
        queryOutputList.add(tidParamInfoG);

        sqThree = this.prepareTidSqlInfo("3", "SQ3", queryInputList, queryOutputList);
        tidIOInfo.getSqlInfos().add(sqThree);
    }

    private void prepareMidParams() {
        midParamInfoA = this.prepareMidParamInfo(false, false, 1, "A", stringDataType);
        midIOInfo.getMidInput().add(midParamInfoA);
        midParamInfoB = this.prepareMidParamInfo(true, true, 2, "B", stringDataType);
        midIOInfo.getMidInput().add(midParamInfoB);
        midParamInfoC = this.prepareMidParamInfo(true, true, 3, "C", stringDataType);
        midIOInfo.getMidInput().add(midParamInfoC);

        midParamInfoD = this.prepareMidParamInfo(false, true, 4, "D", stringDataType);
        midIOInfo.getMidInput().add(midParamInfoD);
        midParamInfoE = this.prepareMidParamInfo(true, true, 5, "E", integerDataType);
        midIOInfo.getMidInput().add(midParamInfoE);
    }

    private void prepareTidParams() {
        tidParamInfoA = this.prepareTidParamInfo(true, 1, "A", stringDataType);
        tidIOInfo.getTidInput().add(tidParamInfoA);
        tidParamInfoB = this.prepareTidParamInfo(false, 2, "B", stringDataType);
        tidIOInfo.getTidInput().add(tidParamInfoB);
        tidParamInfoE = this.prepareTidParamInfo(true, 5, "E", stringDataType);
        tidIOInfo.getTidInput().add(tidParamInfoE);

        // Query Output Tid Parameters
        tidParamInfoC = this.prepareTidParamInfo(false, 3, "C", stringDataType);
        tidIOInfo.getTidSystemInput().add(tidParamInfoC);
        tidParamInfoD = this.prepareTidParamInfo(false, 4, "D", integerDataType);
        tidIOInfo.getTidSystemInput().add(tidParamInfoD);

        tidParamInfoF = this.prepareTidParamInfo(false, 6, "F", integerDataType);
        tidIOInfo.getTidSystemInput().add(tidParamInfoF);
        tidParamInfoG = this.prepareTidParamInfo(false, 7, "G", stringDataType);
        tidIOInfo.getTidSystemInput().add(tidParamInfoG);
        tidParamInfoH = this.prepareTidParamInfo(true, 8, "H", integerDataType);
        tidIOInfo.getTidInput().add(tidParamInfoH);
    }

    @Test
    public void testTidMidMapping() {
        List<MidMapping> midInputMappings = new ArrayList<>();
        Map<String,List<String>> mappingToDelete = new HashMap<>();

        MidMapping midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoA.getFlatenedName());

        List<String> tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoA.getFlatenedName());
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);
        List<ValidationError> errors = mappingValidator.validateMidMapping(midIOInfo, tidIOInfo, midInputMappings, validatorList,mappingToDelete);
        Assert.assertEquals(4, errors.size());
    }

    @Test
    public void testOrphanMidMapping() {
        List<MidMapping> midInputMappings = new ArrayList<>();
        Map<String,List<String>> mappingToDelete = new HashMap<>();
        
        MidMapping midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoA.getFlatenedName());

        List<String> tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoA.getFlatenedName());
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);

        midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoB.getFlatenedName());
        tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoA.getFlatenedName());
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);

        midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoC.getFlatenedName());
        tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoE.getFlatenedName());
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);

        midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoD.getFlatenedName());
        tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoC.getFlatenedName());
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);

        midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoE.getFlatenedName());
        tidInputs = new ArrayList<>();
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);

        List<ValidationError> errors = mappingValidator.validateMidMapping(midIOInfo, tidIOInfo, midInputMappings, validatorList,mappingToDelete);
        Assert.assertEquals(1, errors.size());
    }

    @Test
    public void testDatatypeMidMapping() {
        List<MidMapping> midInputMappings = new ArrayList<>();
        Map<String,List<String>> mappingToDelete = new HashMap<>();
        
        MidMapping midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoA.getFlatenedName());

        List<String> tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoA.getFlatenedName());
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);

        midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoB.getFlatenedName());
        tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoA.getFlatenedName());
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);

        midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoC.getFlatenedName());
        tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoE.getFlatenedName());
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);

        midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoD.getFlatenedName());
        tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoC.getFlatenedName());
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);

        midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoE.getFlatenedName());
        tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoG.getFlatenedName());
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);

        List<ValidationError> errors = mappingValidator.validateMidMapping(midIOInfo, tidIOInfo, midInputMappings, validatorList,mappingToDelete);
        Assert.assertEquals(1, errors.size());
    }

    @Test
    public void testTwoTidToMidMapping() {
        List<MidMapping> midInputMappings = new ArrayList<>();
        Map<String,List<String>> mappingToDelete = new HashMap<>();

        MidMapping midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoA.getFlatenedName());

        List<String> tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoA.getFlatenedName());
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);

        midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoB.getFlatenedName());
        tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoA.getFlatenedName());
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);

        midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoC.getFlatenedName());
        tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoE.getFlatenedName());
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);

        midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoD.getFlatenedName());
        tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoC.getFlatenedName());
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);

        midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoE.getFlatenedName());
        tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoF.getFlatenedName());
        tidInputs.add(tidParamInfoD.getFlatenedName());
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);

        List<ValidationError> errors = mappingValidator.validateMidMapping(midIOInfo, tidIOInfo, midInputMappings, validatorList,mappingToDelete);
        Assert.assertEquals(1, errors.size());
    }

    @Test
    public void testNoTidToOptionalMidMapping() {
        List<MidMapping> midInputMappings = new ArrayList<>();
        Map<String,List<String>> mappingToDelete = new HashMap<>();

        MidMapping midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoA.getFlatenedName());

        List<String> tidInputs = new ArrayList<>();
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);

        midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoB.getFlatenedName());
        tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoA.getFlatenedName());
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);

        midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoC.getFlatenedName());
        tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoE.getFlatenedName());
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);

        midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoD.getFlatenedName());
        tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoC.getFlatenedName());
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);

        midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoE.getFlatenedName());
        tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoF.getFlatenedName());
        tidInputs.add(tidParamInfoH.getFlatenedName());
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);

        List<ValidationError> errors = mappingValidator.validateMidMapping(midIOInfo, tidIOInfo, midInputMappings, validatorList, mappingToDelete);
        Assert.assertEquals(0, errors.size());
    }

    @Test
    public void testMoreThanTwoTidToMidMapping() {
        List<MidMapping> midInputMappings = new ArrayList<>();
        Map<String,List<String>> mappingToDelete = new HashMap<>();

        MidMapping midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoA.getFlatenedName());

        List<String> tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoA.getFlatenedName());
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);

        midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoB.getFlatenedName());
        tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoA.getFlatenedName());
        tidInputs.add(tidParamInfoB.getFlatenedName());
        tidInputs.add(tidParamInfoE.getFlatenedName());
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);

        midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoC.getFlatenedName());
        tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoE.getFlatenedName());
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);

        midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoD.getFlatenedName());
        tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoC.getFlatenedName());
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);

        midMapping = new MidMapping();
        midMapping.setMappedTo(midParamInfoE.getFlatenedName());
        tidInputs = new ArrayList<>();
        tidInputs.add(tidParamInfoF.getFlatenedName());
        tidInputs.add(tidParamInfoD.getFlatenedName());
        midMapping.setInputs(tidInputs);
        midInputMappings.add(midMapping);

        List<ValidationError> errors = mappingValidator.validateMidMapping(midIOInfo, tidIOInfo, midInputMappings, validatorList,mappingToDelete);
        Assert.assertEquals(2, errors.size());
    }
}
