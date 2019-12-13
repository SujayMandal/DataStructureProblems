/**

 * 
 */
package com.ca.umg.business.mapping.helper;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.mapping.entity.Mapping;
import com.ca.umg.business.mapping.entity.MappingInput;
import com.ca.umg.business.mapping.entity.MappingOutput;
import com.ca.umg.business.mapping.info.TidIoDefinition;
import com.ca.umg.business.mid.extraction.info.DatatypeInfo;
import com.ca.umg.business.mid.extraction.info.MappingViewInfo;
import com.ca.umg.business.mid.extraction.info.MappingViews;
import com.ca.umg.business.mid.extraction.info.MidIOInfo;
import com.ca.umg.business.mid.extraction.info.MidParamInfo;
import com.ca.umg.business.mid.extraction.info.PartialMapping;
import com.ca.umg.business.mid.extraction.info.TidIOInfo;
import com.ca.umg.business.mid.extraction.info.TidParamInfo;
import com.ca.umg.business.mid.extraction.info.TidSqlInfo;
import com.ca.umg.business.tenant.entity.Address;
import com.ca.umg.business.util.XmlAttribute.DataType;

/**
 * @author elumalas
 * 
 */

public class MappingHelperTest {
    private MappingHelper mappingHelper;

    private Mapping mapping;

    private TidIOInfo tidIOInfo;

    private TidParamInfo tidParamInfo;

    private List<TidParamInfo> tidParamInfoList;

    private MappingViews mappingViews;

    private MappingViewInfo mappingViewInfo;

    private List<MappingViewInfo> mappingViewInfoList;

    private MidIOInfo midIOInfo;

    private List<MidParamInfo> midParamInfoList;

    private TidSqlInfo tidSqlInfo;

    private List<TidSqlInfo> tidSqlInfoList;

    private TidIoDefinition tidIoDefinition;

    private Map<String, Object> inputMap = null;
    private Map<String, Object> complexMap = null;
    private TidIoDefinition tidIoDefinition1 = null;
    @Mock
    private MappingHelper mockMappingHelper;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mappingHelper = new MappingHelper();
        dummyMethodToCreateLists();
        prepareComplexAndInputMap();
    }

    private void dummyMethodToCreateLists() {
        this.mapping = new Mapping();
        this.mapping.setId("test101");
        this.mapping.setDescription("desc1");
        this.mapping.setName("name1");
        this.mapping.setTenantId("test1");
        this.tidParamInfo = new TidParamInfo();
        this.tidParamInfo.setDataFormat("test1");
        this.tidParamInfo.setDataTypeStr("dataType1");
        this.tidParamInfo.setDescription("desc1");
        this.tidParamInfo.setExpressionId("exp1");
        this.tidParamInfo.setExprsnOutput(true);
        this.tidParamInfo.setFlatenedName("test2");
        this.tidParamInfo.setMandatory(true);
        this.tidParamInfo.setMapped(false);
        this.tidParamInfo.setName("name");
        DatatypeInfo datatype = new DatatypeInfo();
        datatype.setType("String");
        this.tidParamInfo.setDatatype(datatype);
        this.tidIOInfo = new TidIOInfo();
        this.tidParamInfoList = new ArrayList<>();
        this.tidParamInfoList.add(tidParamInfo);
        this.tidIOInfo.setTidInput(tidParamInfoList);
        this.mappingViews = new MappingViews();
        this.mappingViewInfo = new MappingViewInfo();
        this.mappingViewInfo.setMappedTo("tidName1");
        this.mappingViewInfo.setMappingParam("midName1");
        this.mappingViewInfoList = new ArrayList<>();
        this.mappingViewInfoList.add(mappingViewInfo);
        this.mappingViews.setInputMappingViews(mappingViewInfoList);
        this.mappingViews.setOutputMappingViews(mappingViewInfoList);
        this.midIOInfo = new MidIOInfo();
        this.midParamInfoList = new ArrayList<>();
        this.midParamInfoList.add(new MidParamInfo());
        this.midParamInfoList.add(new MidParamInfo());
        this.midIOInfo.setMidInput(this.midParamInfoList);
        this.midIOInfo.setMidOutput(this.midParamInfoList);
        this.tidSqlInfo = new TidSqlInfo();
        this.tidSqlInfo.setInputParams(tidParamInfoList);
        this.tidSqlInfo.setOutputParams(tidParamInfoList);
        this.tidSqlInfo.setSqlId("sqlId1");
        this.tidSqlInfo.setSqlName("sqlName1");
        this.tidSqlInfoList = new ArrayList<>();
        this.tidSqlInfoList.add(tidSqlInfo);
        this.tidIoDefinition = new TidIoDefinition();
        this.tidIoDefinition.setArrayType(true);
        this.tidIoDefinition.setError(false);
        this.tidIoDefinition.setMandatory(true);
        this.tidIoDefinition.setName("name1");
        Map<String, Object> test2 = new HashMap<>();
        test2.put("type", DataType.DOUBLE);
        this.tidIoDefinition.setDatatype(test2);
    }

    @Test
    public void testPrepareInputMapping() throws SystemException {
        MappingInput mappingInput = this.mappingHelper.prepareInputMapping(this.mapping, this.tidIOInfo, this.mappingViews);
        Assert.assertNotNull(mappingInput);
        Assert.assertNotNull(mappingInput.getMappingData());
        Assert.assertNotNull(mappingInput.getTenantInterfaceDefn());
        assertThat(mappingInput.getMapping().getName(), is("name1"));
    }

    @Test
    public void testPrepareOutputMapping() throws SystemException {
        MappingOutput mappingOutput = this.mappingHelper.prepareOutputMapping(this.mapping, this.tidIOInfo, this.mappingViews);
        Assert.assertNotNull(mappingOutput);
        Assert.assertNotNull(mappingOutput.getMappingData());
        Assert.assertNotNull(mappingOutput.getTenantInterfaceDefn());
        assertThat(mappingOutput.getMapping().getName(), is("name1"));
    }

    @Test
    public void testGetTidInfoInitial() throws SystemException {
        PartialMapping<TidParamInfo> tidParamInfo = this.mappingHelper.getTidInfoInitial(this.midIOInfo, "testType1");
        List<TidParamInfo> tidParamInfoList = tidParamInfo.getPartials();
        assertThat(tidParamInfoList.isEmpty(), is(false));
        for (TidParamInfo tidParamInfo2 : tidParamInfoList) {
            Assert.assertNotNull(tidParamInfo2);
        }
    }

    @Test
    public void testGetSQLOutputsSave() throws SystemException {
        TidIOInfo tidIOInfos = this.mappingHelper.getSQLOutputsSave(tidSqlInfoList, this.tidIOInfo);
        Assert.assertNotNull(tidIOInfos);
    }

    @Test
    public void testGetExistingId() throws SystemException {
        Address address = new Address();
        address.setId("test1");
        final String str = this.mappingHelper.getExistingId(address);
        Assert.assertNotNull(str);
        assertThat(str, is("test1"));
    }

    @Test
    public void testGetFlattenedInputMapping() throws BusinessException, SystemException {
        Map<String, TidParamInfo> tidParamInfo = this.mappingHelper.getFlattenedInputMapping(this.tidIOInfo);
        Assert.assertNotNull(tidParamInfo);
        assertThat(tidParamInfo.isEmpty(), is(false));
    }

    @Test
    public void testGetFlattenedOutputMapping() throws BusinessException, SystemException {
        Map<String, MidParamInfo> midParamInfo = this.mappingHelper.getFlattenedOutputMapping(this.midIOInfo);
        Assert.assertNotNull(midParamInfo);
        assertThat(midParamInfo.isEmpty(), is(false));
    }

    @Test
    public void testGetTidChildMap() throws SystemException {
        TidParamInfo paramInfo = new TidParamInfo();
        paramInfo.setChildren(tidParamInfoList);
        paramInfo.setDataTypeStr("dataType1");
        paramInfo.setDescription("desc1");
        paramInfo.setMandatory(true);
        Map<String, TidParamInfo> paramInfoMap = this.mappingHelper.getTidChildMap(paramInfo);
        Assert.assertNotNull(paramInfoMap);
        assertThat(paramInfoMap.size() > 0, is(true));
    }

    @Test
    public void testGetMidChildMap() throws SystemException {
        MidParamInfo paramInfo = new MidParamInfo();
        paramInfo.setChildren(midParamInfoList);
        paramInfo.setDataTypeStr("dataType1");
        paramInfo.setDescription("desc1");
        paramInfo.setMandatory(true);
        Map<String, MidParamInfo> paramInfoMap = this.mappingHelper.getMidChildMap(paramInfo);
        Assert.assertNotNull(paramInfoMap);
        assertThat(paramInfoMap.size() > 0, is(true));
    }

    @Test
    public void testCreateMappingDetailMap() throws SystemException {
        Map<String, String> paramInfoMap = this.mappingHelper.createMappingDetailMap(this.mappingViewInfoList);
        Assert.assertNotNull(paramInfoMap);
        assertThat(paramInfoMap.size() > 0, is(true));
        assertThat(paramInfoMap.get("MIDNAME1"), is("tidName1"));
    }

    @Test
    public void testClearModifiedByAndDate() throws SystemException {
        this.mappingHelper.clearModifiedByAndDate(mapping);
        Assert.assertNotNull(mapping.getLastModifiedDate());
        Assert.assertNull(mapping.getLastModifiedBy());
    }

    @Test
    public void testBuildSqlMappedInputs() throws SystemException {
        Map<String, String> paramInfoMap = this.mappingHelper.buildSqlMappedInputs(tidSqlInfoList);
        Assert.assertNotNull(paramInfoMap);
        assertThat(paramInfoMap.size() > 0, is(true));
        assertThat(paramInfoMap.get("test2"), is("sqlName1"));
    }

    @Test
    public void testGetIoDefinitions() throws SystemException {
        seTidParamInfoList(tidParamInfoList);
        TidParamInfo tidParamInfo3 = new TidParamInfo();
        tidParamInfo3.setFlatenedName("test3");
        tidParamInfo3.setMandatory(true);
        tidParamInfo3.setName("name1");
        DatatypeInfo datatype3 = new DatatypeInfo();
        datatype3.setType("Integer");
        tidParamInfo3.setDatatype(datatype3);
        TidParamInfo tidParamInfo4 = new TidParamInfo();
        tidParamInfo4.setFlatenedName("test3");
        tidParamInfo4.setMandatory(true);
        tidParamInfo4.setName("name1");
        DatatypeInfo datatype4 = new DatatypeInfo();
        datatype4.setType("Date");
        tidParamInfo4.setDatatype(datatype4);
        TidParamInfo tidParamInfo5 = new TidParamInfo();
        tidParamInfo5.setFlatenedName("test3");
        tidParamInfo5.setMandatory(true);
        tidParamInfo5.setName("name1");
        DatatypeInfo datatype5 = new DatatypeInfo();
        datatype5.setType("DateTime");
        tidParamInfo5.setDatatype(datatype5);
        this.tidParamInfoList.add(tidParamInfo3);
        this.tidParamInfoList.add(tidParamInfo4);
        this.tidParamInfoList.add(tidParamInfo5);
        List<TidIoDefinition> definitions = new ArrayList<>();
        TidIoDefinition tidIoDefinition1 = new TidIoDefinition();
        tidIoDefinition1.setArrayType(false);
        tidIoDefinition1.setDescription("desc1");
        tidIoDefinition1.setMandatory(true);
        tidIoDefinition1.setName("name1");
        tidIoDefinition1.setValidationMethod("valid1");
        definitions.add(tidIoDefinition);
        definitions.add(tidIoDefinition1);
        this.mappingHelper.getIoDefinitions(this.tidParamInfoList, definitions, false);
    }

    public void prepareComplexAndInputMap() {
        inputMap = new HashMap<>();
        complexMap = new HashMap<>();
        List<TidIoDefinition> definitions = new ArrayList<>();
        tidIoDefinition1 = new TidIoDefinition();
        tidIoDefinition1.setArrayType(false);
        tidIoDefinition1.setDescription("desc1");
        tidIoDefinition1.setMandatory(true);
        tidIoDefinition1.setName("state/city");
        tidIoDefinition1.setValidationMethod("valid1");
        Map<String, Object> datatype = new HashMap<>();
        datatype.put("type", "String");
        tidIoDefinition1.setDatatype(datatype);
        tidIoDefinition1.setValue("test");
        definitions.add(tidIoDefinition);
        definitions.add(tidIoDefinition1);
        for (TidIoDefinition tidIoDefinition : definitions) {
            if (StringUtils.contains(tidIoDefinition.getName(), "/")) {
                complexMap.put(tidIoDefinition.getName(), tidIoDefinition.getValue());
            } else {
                inputMap.put(tidIoDefinition.getName(), tidIoDefinition.getValue());
            }
        }
    }

    @Test
    public void testCreateObjectStructure() {
        mappingHelper.createObjectStructure(complexMap, inputMap);
    }

    @Test
    public void testSetElementValue() {
        try {
            mappingHelper.setElementValue(tidIoDefinition1);
        } catch (SystemException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetIoDefinitionsFromTxnDashboard() throws SystemException, BusinessException {
        List<String> defaultValuesList = new ArrayList<String>();
        List<String> paramNamesList = new ArrayList<String>();

        seTidParamInfoList(tidParamInfoList);

        TidParamInfo tidParamInfo3 = new TidParamInfo();
        tidParamInfo3.setFlatenedName("test3");
        tidParamInfo3.setMandatory(true);
        tidParamInfo3.setName("name1");
        DatatypeInfo datatype3 = new DatatypeInfo();
        datatype3.setType("Integer");
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("defaultValue", "123");
        datatype3.setProperties(properties);
        tidParamInfo3.setDatatype(datatype3);

        TidParamInfo tidParamInfo4 = new TidParamInfo();
        tidParamInfo4.setFlatenedName("test3");
        tidParamInfo4.setMandatory(true);
        tidParamInfo4.setName("name1");
        tidParamInfo4.setValue("10/17/2014");
        DatatypeInfo datatype4 = new DatatypeInfo();
        datatype4.setType("Date");
        tidParamInfo4.setDatatype(datatype4);
        TidParamInfo tidParamInfo5 = new TidParamInfo();
        tidParamInfo5.setFlatenedName("test3");
        tidParamInfo5.setMandatory(true);
        tidParamInfo5.setName("name1");
        tidParamInfo5.setValue("1111");
        DatatypeInfo datatype5 = new DatatypeInfo();
        datatype5.setType("DateTime");
        tidParamInfo5.setDatatype(datatype5);

        TidParamInfo tidParamInfo6 = new TidParamInfo();
        tidParamInfo6.setFlatenedName("serviceData");
        tidParamInfo6.setMandatory(true);
        tidParamInfo6.setName("serviceData");
        DatatypeInfo datatype6 = new DatatypeInfo();
        datatype6.setType("object");

        tidParamInfo6.setDatatype(datatype6);

        List<TidParamInfo> tidParamInfoList = new ArrayList<TidParamInfo>();
        tidParamInfoList.add(tidParamInfo3);

        tidParamInfo6.setChildren(tidParamInfoList);

        this.tidParamInfoList.add(tidParamInfo3);
        this.tidParamInfoList.add(tidParamInfo4);
        this.tidParamInfoList.add(tidParamInfo5);
        this.tidParamInfoList.add(tidParamInfo6);

        List<TidIoDefinition> definitions = new ArrayList<>();
        TidIoDefinition tidIoDefinition1 = new TidIoDefinition();
        tidIoDefinition1.setArrayType(false);
        tidIoDefinition1.setDescription("desc1");
        tidIoDefinition1.setMandatory(true);
        tidIoDefinition1.setName("name1");
        tidIoDefinition1.setValidationMethod("valid1");
        definitions.add(tidIoDefinition);
        definitions.add(tidIoDefinition1);
        this.mappingHelper
                .getIoDefinitionsFromTxnDashboard(this.tidParamInfoList, definitions, defaultValuesList, paramNamesList);
        Assert.assertTrue(paramNamesList.size() > 0);
    }

    private List<TidParamInfo> seTidParamInfoList(List<TidParamInfo> tidParamInfoList) {
        TidParamInfo tidParamInfo1 = new TidParamInfo();
        tidParamInfo1.setFlatenedName("test3");
        tidParamInfo1.setMandatory(true);
        tidParamInfo1.setName("name1");
        DatatypeInfo datatype1 = new DatatypeInfo();
        datatype1.setType("Double");
        tidParamInfo1.setDatatype(datatype1);
        TidParamInfo tidParamInfo2 = new TidParamInfo();
        tidParamInfo2.setFlatenedName("test3");
        tidParamInfo2.setMandatory(true);
        tidParamInfo2.setName("name1");
        DatatypeInfo datatype2 = new DatatypeInfo();
        datatype2.setType("Boolean");
        tidParamInfo2.setDatatype(datatype2);
        tidParamInfoList.add(tidParamInfo1);
        tidParamInfoList.add(tidParamInfo2);
        return tidParamInfoList;
    }

    @Test(expected = BusinessException.class)
    public void testGetIoDefinitionsFromTxnDashboardWithExcep() throws SystemException, BusinessException {
        List<String> defaultValuesList = new ArrayList<String>();
        List<String> paramNamesList = new ArrayList<String>();

        TidParamInfo tidParamInfo1 = new TidParamInfo();
        tidParamInfo1.setFlatenedName("test3");
        tidParamInfo1.setMandatory(true);
        tidParamInfo1.setName("name1");
        tidParamInfo1.setValue("1111");
        DatatypeInfo datatype1 = new DatatypeInfo();
        datatype1.setType("date");
        tidParamInfo1.setDatatype(datatype1);

        TidParamInfo tidParamInfo2 = new TidParamInfo();
        tidParamInfo2.setFlatenedName("serviceData");
        tidParamInfo2.setMandatory(true);
        tidParamInfo2.setName("serviceData");
        DatatypeInfo datatype2 = new DatatypeInfo();
        datatype2.setType("object");

        tidParamInfo2.setDatatype(datatype2);

        List<TidParamInfo> tidParamInfoList = new ArrayList<TidParamInfo>();
        tidParamInfoList.add(tidParamInfo1);

        tidParamInfo2.setChildren(tidParamInfoList);

        this.tidParamInfoList.add(tidParamInfo1);
        this.tidParamInfoList.add(tidParamInfo2);

        List<TidIoDefinition> definitions = new ArrayList<>();
        this.mappingHelper
                .getIoDefinitionsFromTxnDashboard(this.tidParamInfoList, definitions, defaultValuesList, paramNamesList);

    }
}
