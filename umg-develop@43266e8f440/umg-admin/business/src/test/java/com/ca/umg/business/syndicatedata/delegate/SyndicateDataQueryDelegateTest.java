package com.ca.umg.business.syndicatedata.delegate;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.mapping.dao.AbstractMappingTest;
import com.ca.umg.business.mapping.entity.Mapping;
import com.ca.umg.business.mapping.info.MappingInfo;
import com.ca.umg.business.mapping.info.QueryLaunchInfo;
import com.ca.umg.business.mid.extraction.info.MidParamInfo;
import com.ca.umg.business.mid.extraction.info.TidParamInfo;
import com.ca.umg.business.model.entity.Model;
import com.ca.umg.business.syndicatedata.dao.SyndicateDataQueryDAO;
import com.ca.umg.business.syndicatedata.info.SyndicateDataQueryInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataQueryObjectInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataQueryParameterInfo;

@ContextHierarchy({ @ContextConfiguration("classpath:root-db-context.xml"), @ContextConfiguration })
@RunWith(SpringJUnit4ClassRunner.class)
public class SyndicateDataQueryDelegateTest extends AbstractMappingTest {

    @Inject
    private SyndicateDataQueryDelegate syndicateDataQueryDelegate;
    
    @Mock
    private SyndicateDataQueryDAO syndicateDataqueryDAO;

    @Before
    public void setup() {
        getLocalhostTenantContext();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateSyndicateDataQuery() {
        SyndicateDataQueryInfo queryInfo = new SyndicateDataQueryInfo();
        String tenantId = getLocalhostTenantContext().getTenantCode();
        Model model = createModel("MOD2", "desc", "docname", "ioDefinitionName", "type", "ioDefinition");
        Mapping mappingEntity = createMapping("M2", model, tenantId, "MOD_DESC", "MAPPING_IO");
        MappingInfo mapping = new MappingInfo();
        mapping.setName("M2");
        mapping.setId(mappingEntity.getId());
        queryInfo.setName("TestQueryName");
        queryInfo.setDescription("TestDesc");
        queryInfo.setExecSequence(1);
        queryInfo.setRowType("SINGLE");
        queryInfo.setDataType("ARRAY");
        queryInfo.setMapping(mapping);
        queryInfo.setMappingType("Input");

        SyndicateDataQueryObjectInfo queryObj = createQueryObject("Select A,B,C", "FROM ABC", "WHERE C ='ABC'", "ORDER BY A ASC",
                "Select A,B,C FROM ABC WHERE C ='ABC' AND B='DEF' ORDER BY A ASC");

        List<SyndicateDataQueryParameterInfo> inputParamList = new ArrayList<SyndicateDataQueryParameterInfo>();
        List<SyndicateDataQueryParameterInfo> outputParamList = new ArrayList<SyndicateDataQueryParameterInfo>();
        SyndicateDataQueryParameterInfo inputParam = createInputParams("C", "STRING", "ABC");
        inputParamList.add(inputParam);
        inputParam = createInputParams("B", "STRING", "ABC");
        inputParamList.add(inputParam);
        SyndicateDataQueryParameterInfo outputParam = createOutputParams("A", "STRING");
        outputParamList.add(outputParam);
        outputParam = createOutputParams("B", "STRING");
        outputParamList.add(outputParam);
        outputParam = createOutputParams("C", "STRING");
        outputParamList.add(outputParam);

        queryInfo.setQueryObject(queryObj);
        queryInfo.setInputParameters(inputParamList);
        queryInfo.setOutputParameters(outputParamList);
        SyndicateDataQueryInfo synDataQueryInfo = null;
        try {
            synDataQueryInfo = syndicateDataQueryDelegate.createSyndicateDataQuery(queryInfo);
        } catch (BusinessException | SystemException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(synDataQueryInfo);
        
        
        queryInfo = this.updateQueryInfo(queryInfo);
        when(syndicateDataqueryDAO.findByNameAndMappingId(queryInfo.getName(), queryInfo.getMapping().getId())).thenReturn(null);
        try {
            synDataQueryInfo = syndicateDataQueryDelegate.createSyndicateDataQuery(queryInfo);
        } catch (BusinessException | SystemException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(synDataQueryInfo);
    }

    private SyndicateDataQueryInfo updateQueryInfo(SyndicateDataQueryInfo queryInfo) {
        Map<String,MidParamInfo> midOutput = new HashMap<>();
        Map<String,TidParamInfo> tidInput = new HashMap<>();
        TidParamInfo tidParamInfo = new TidParamInfo();
        tidParamInfo.setMandatory(true);
        tidInput.put("B", tidParamInfo);
        tidParamInfo = new TidParamInfo();
        tidParamInfo.setMandatory(false);
        tidInput.put("C", tidParamInfo);
        QueryLaunchInfo queryLaunchInfo = new QueryLaunchInfo();
        queryLaunchInfo.setMidOutput(midOutput);
        queryLaunchInfo.setTidInput(tidInput);
        queryLaunchInfo.setTidName("tidMapping1");
        queryLaunchInfo.setType("tid1");
        queryInfo.setQueryLaunchInfo(queryLaunchInfo);
        queryInfo.setName("TestQueryName1");
        return queryInfo;
    }

    private SyndicateDataQueryParameterInfo createOutputParams(String name, String dataType) {
        SyndicateDataQueryParameterInfo outputParams = new SyndicateDataQueryParameterInfo();
        outputParams.setName(name);
        outputParams.setDataType(dataType);
        return outputParams;
    }

    private SyndicateDataQueryParameterInfo createInputParams(String name, String dataType, String sampleValue) {
        SyndicateDataQueryParameterInfo inputParams = new SyndicateDataQueryParameterInfo();
        inputParams.setName(name);
        inputParams.setDataType(dataType);
        inputParams.setSampleValue(sampleValue);
        return inputParams;
    }

    private SyndicateDataQueryObjectInfo createQueryObject(String selectStr, String fromStr, String whrClause, String orderByStr,
            String execQuery) {
        SyndicateDataQueryObjectInfo queryObjInfo = new SyndicateDataQueryObjectInfo();
        queryObjInfo.setSelectString(selectStr);
        queryObjInfo.setFromString(fromStr);
        queryObjInfo.setWhereClause(whrClause);
        queryObjInfo.setOrderByString(orderByStr);
        queryObjInfo.setExecutableQuery(execQuery);
        return queryObjInfo;
    }

}
