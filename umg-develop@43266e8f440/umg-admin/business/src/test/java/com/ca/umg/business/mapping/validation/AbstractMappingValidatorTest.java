package com.ca.umg.business.mapping.validation;

import java.util.List;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ca.umg.business.mapping.validation.core.ValidationUtil;
import com.ca.umg.business.mapping.validation.core.ValidatorFactory;
import com.ca.umg.business.mid.extraction.info.DatatypeInfo;
import com.ca.umg.business.mid.extraction.info.MidMapping;
import com.ca.umg.business.mid.extraction.info.MidParamInfo;
import com.ca.umg.business.mid.extraction.info.TidExpressionInfo;
import com.ca.umg.business.mid.extraction.info.TidParamInfo;
import com.ca.umg.business.mid.extraction.info.TidSqlInfo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ValidatorConfig.class })
public abstract class AbstractMappingValidatorTest {
    @Autowired
    protected MappingValidator mappingValidator;
    @Autowired
    protected ValidationUtil validationUtil;
    @Autowired
    protected ValidatorFactory validatorFactory;

    protected MidParamInfo prepareMidParamInfo(boolean mandatory, boolean syndicate, int sequence, String name,
            DatatypeInfo datatypeInfo) {
        MidParamInfo midParamInfo = new MidParamInfo();
        midParamInfo.setMandatory(mandatory);
        midParamInfo.setSyndicate(syndicate);
        midParamInfo.setSequence(sequence);
        midParamInfo.setName(name);
        midParamInfo.setFlatenedName(name);
        midParamInfo.setDatatype(datatypeInfo);
        return midParamInfo;
    }

    protected TidParamInfo prepareTidParamInfo(boolean mandatory, int sequence, String name, DatatypeInfo datatypeInfo) {
        TidParamInfo tidParamInfo = new TidParamInfo();
        tidParamInfo.setMandatory(mandatory);
        tidParamInfo.setSyndicate(false);
        tidParamInfo.setSequence(sequence);
        tidParamInfo.setName(name);
        tidParamInfo.setFlatenedName(name);
        tidParamInfo.setDatatype(datatypeInfo);
        return tidParamInfo;
    }

    protected DatatypeInfo prepareDatatypeInfo(Datatype type) {
        DatatypeInfo datatypeInfo = new DatatypeInfo();
        datatypeInfo.setType(type.getDatatype());
        return datatypeInfo;
    }

    protected TidSqlInfo prepareTidSqlInfo(String sqlId, String sqlName, List<TidParamInfo> inputTidParamInfos,
            List<TidParamInfo> outputTidParamInfos) {
        TidSqlInfo tidSqlInfo = new TidSqlInfo();
        tidSqlInfo.setSqlId(sqlId);
        tidSqlInfo.setSqlName(sqlName);
        tidSqlInfo.setInputParams(inputTidParamInfos);
        tidSqlInfo.setOutputParams(outputTidParamInfos);

        for (TidParamInfo tidParamInfo : outputTidParamInfos) {
            tidParamInfo.setSqlOutput(true);
            tidParamInfo.setSqlId(sqlId);
        }
        return tidSqlInfo;
    }

    protected enum Datatype {
        STRING("STRING"), DOUBLE("DOUBLE"), OBJECT("OBJECT"), INTEGER("INTEGER"), NUMERIC("NUMERIC"), BOOLEAN("BOOLEAN"), DATE(
                "DATE");

        private String datatype;

        private Datatype(String datatype) {
            this.datatype = datatype;
        }

        public String getDatatype() {
            return datatype;
        }
    }

    protected MidMapping prepareMidMapping(String midName, List<String> tidNames) {
        MidMapping midMapping = new MidMapping();
        midMapping.setMappedTo(midName);
        midMapping.setInputs(tidNames);
        return midMapping;
    }

    protected TidExpressionInfo prepareTidExpInfo(String expId, String expName, List<TidParamInfo> inputTidParamInfos,
            List<TidParamInfo> outputTidParamInfos) {
        TidExpressionInfo tidExpInfo = new TidExpressionInfo();
        tidExpInfo.setExpressionName(expId);
        tidExpInfo.setExpressionText("add");
        tidExpInfo.setInputParams(inputTidParamInfos);
        tidExpInfo.setOutputParams(outputTidParamInfos);

        for (TidParamInfo tidParamInfo : outputTidParamInfos) {
            tidParamInfo.setExprsnOutput(true);
            tidParamInfo.setExpressionId(expId);
        }
        return tidExpInfo;
    }

    protected TidParamInfo prepareTidParamInfo() {
        TidParamInfo tidParamInfo = new TidParamInfo();
        tidParamInfo.setMandatory(Boolean.TRUE);
        tidParamInfo.setName("name");
        tidParamInfo.setDataFormat("dataFormat");
        tidParamInfo.setExpressionId("1");
        tidParamInfo.setExprsnOutput(Boolean.FALSE);
        tidParamInfo.setSqlOutput(Boolean.FALSE);
        tidParamInfo.setMapped(Boolean.FALSE);
        tidParamInfo.setPrecision(1);
        tidParamInfo.setSize(1);
        tidParamInfo.setSqlId("sqlId");
        tidParamInfo.setText("text");
        tidParamInfo.setUserSelected(Boolean.TRUE);
        tidParamInfo.setValue("value");
        return tidParamInfo;
    }
}
