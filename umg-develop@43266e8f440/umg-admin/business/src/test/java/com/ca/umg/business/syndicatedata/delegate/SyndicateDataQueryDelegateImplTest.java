package com.ca.umg.business.syndicatedata.delegate;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import com.ca.framework.core.custom.mapper.UMGConfigurableMapper;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.mapping.bo.MappingBO;
import com.ca.umg.business.mapping.delegate.MappingDelegate;
import com.ca.umg.business.mapping.entity.Mapping;
import com.ca.umg.business.mapping.info.MappingInfo;
import com.ca.umg.business.model.info.ModelInfo;
import com.ca.umg.business.syndicatedata.bo.SyndicateDataQueryBO;
import com.ca.umg.business.syndicatedata.entity.SyndicateDataQuery;
import com.ca.umg.business.syndicatedata.entity.SyndicateDataQueryInput;
import com.ca.umg.business.syndicatedata.entity.SyndicateDataQueryOutput;
import com.ca.umg.business.syndicatedata.info.SyndicateDataQueryInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataQueryObjectInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataQueryParameterInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataQueryResponseInfo;

import ma.glasnost.orika.impl.ConfigurableMapper;

public class SyndicateDataQueryDelegateImplTest {

    @Mock
    private SyndicateDataQueryBO mockSyndicateDataQueryBO;

    @InjectMocks
    private SyndicateDataQueryDelegateImpl classUnderTest;

    @Mock
    MappingDelegate mappingDelegate;
    
    @Mock
    private MappingBO mappingBO;

    @Spy
    ConfigurableMapper mapper = new UMGConfigurableMapper();

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testListAll() throws BusinessException, SystemException {
        ArrayList<SyndicateDataQuery> queryObjects = new ArrayList<SyndicateDataQuery>();
        SyndicateDataQuery object1 = new SyndicateDataQuery();
        object1.setDescription("some description");
        queryObjects.add(object1);
        when(mockSyndicateDataQueryBO.listAll()).thenReturn(queryObjects);

        assertThat(classUnderTest.listAll().size(), is(1));
        assertThat(classUnderTest.listAll().get(0), notNullValue());
        assertThat(classUnderTest.listAll().get(0).getDescription(), is("some description"));
    }

    @Test
    public void testListAllIfBOReturnsNullObject() throws BusinessException, SystemException {
        when(mockSyndicateDataQueryBO.listAll()).thenReturn(null);

        assertThat(classUnderTest.listAll(), notNullValue());
    }

    @Test
    public void testListAllIfBOReturnsEmptyObject() throws BusinessException, SystemException {
        when(mockSyndicateDataQueryBO.listAll()).thenReturn(new ArrayList<SyndicateDataQuery>());

        assertThat(classUnderTest.listAll(), notNullValue());
    }

    @Test
    public void updateSyndicateDataQueryTest() throws SystemException, BusinessException {
        SyndicateDataQueryInfo synDataQryInfo = this.createSyndicateDataQueryInfo();
        SyndicateDataQuery synDataQuery = new SyndicateDataQuery();
        Mapping mapping = this.createMapping();

        when(mappingBO.findByName(synDataQryInfo.getMapping().getName())).thenReturn(mapping);
        when(mockSyndicateDataQueryBO.update(synDataQuery)).thenReturn(synDataQuery);

        classUnderTest.updateSyndicateDataQuery(synDataQryInfo);
    }
    
    @Test
    public void listByMappingNameTest() throws BusinessException, SystemException{
     
        List<SyndicateDataQuery> syndicateDataQueryList = this.createSyndicateDataQueryList();
        when(mockSyndicateDataQueryBO.listByMappingName("model")).thenReturn(syndicateDataQueryList);

        List<SyndicateDataQueryInfo> syndicateDataQueryInfoResult = classUnderTest.listByMappingName("model");
        Assert.assertNotNull(syndicateDataQueryInfoResult);
        
        syndicateDataQueryInfoResult = classUnderTest.listByMappingName("model1");
        assertThat(syndicateDataQueryInfoResult.size(), is(0));
    }

    @Test
    public void fetchQueryTestDataTest() throws BusinessException, SystemException{
        List<Map<String, Object>> queryResponse = new ArrayList<>();
        SyndicateDataQuery queryObject = new SyndicateDataQuery();
        Map<String, Object> systemParams = new HashMap<>();
        Map<String, Boolean> references = new HashMap<>();
        List<String> tidParamNames = new ArrayList<>();
        SyndicateDataQueryInfo synDataQryInfo = createSyndicateDataQueryInfo();
        
        when(mockSyndicateDataQueryBO.fetchTestData(queryObject)).thenReturn(queryResponse);
        when(mockSyndicateDataQueryBO.systemParameters(queryObject)).thenReturn(systemParams);
        when(mockSyndicateDataQueryBO.getExecutableQuery(queryObject)).thenReturn("select * from mydataTable");
        when(mappingDelegate.isReferenced(tidParamNames, "newTid", "mappingType")).thenReturn(references);
        
        SyndicateDataQueryResponseInfo info = classUnderTest.fetchQueryTestData(synDataQryInfo);
        Assert.assertNotNull(info);
        
    }

    private List<SyndicateDataQuery> createSyndicateDataQueryList() {
        List<SyndicateDataQuery> syndicateDataQueryList = new ArrayList<>();
        SyndicateDataQuery syndicateDataQuery = new SyndicateDataQuery();
        syndicateDataQuery.setCreatedDate(new DateTime()); 
        syndicateDataQuery.setDataType("integer");
        syndicateDataQuery.setDescription("");
        syndicateDataQuery.setExecSequence(1);
        syndicateDataQuery.setInputParameters(new HashSet<SyndicateDataQueryInput>());
        syndicateDataQuery.setMapping(createMapping());
        syndicateDataQuery.setMappingType("single");
        syndicateDataQuery.setName("query1");
        syndicateDataQuery.setOutputParameters(new HashSet<SyndicateDataQueryOutput>());
        syndicateDataQuery.setQueryObject(null);
        syndicateDataQuery.setRowType("rowType");
        syndicateDataQuery.setTenantId("123");
        syndicateDataQuery.setLastModifiedDate(new DateTime());
        
        syndicateDataQueryList.add(syndicateDataQuery);
        return syndicateDataQueryList;
    }

    private Mapping createMapping() {
        Mapping mapping = new Mapping();
        mapping.setDescription("mappingdescription");
        mapping.setModel(null);
        mapping.setModelIO(null);
        mapping.setName("model");
        mapping.setStatus("active");
        mapping.setTenantId("123456789");
        return mapping;
    }

    private SyndicateDataQueryInfo createSyndicateDataQueryInfo() {
        MappingInfo mappingInfo = new MappingInfo();
        mappingInfo.setActive(true);
        mappingInfo.setDescription("mappingdescription");
        mappingInfo.setMappingData("md");
        mappingInfo.setModel(new ModelInfo());
        mappingInfo.setModelName("mymodel");
        mappingInfo.setName("model");
        mappingInfo.setStatus("active");
        mappingInfo.setUmgName("umgName");
        mappingInfo.setVersion(1);

        SyndicateDataQueryInfo syndicateDataQueryInfo = new SyndicateDataQueryInfo();
        syndicateDataQueryInfo.setDataType("integer");
        syndicateDataQueryInfo.setDescription("query desc");
        syndicateDataQueryInfo.setExecSequence(0);
        syndicateDataQueryInfo.setInputParameters(new ArrayList<SyndicateDataQueryParameterInfo>());
        syndicateDataQueryInfo.setMandatory(false);
        syndicateDataQueryInfo.setMapping(mappingInfo);
        syndicateDataQueryInfo.setMappingType("single");
        syndicateDataQueryInfo.setName("queryInfo");
        syndicateDataQueryInfo.setOutputParameters(new ArrayList<SyndicateDataQueryParameterInfo>());
        syndicateDataQueryInfo.setQueryLaunchInfo(null);
        SyndicateDataQueryObjectInfo queryObject = new SyndicateDataQueryObjectInfo();
        queryObject.setSelectString("");
        queryObject.setFromString("");
        queryObject.setOrderByString("");
        queryObject.setWhereClause("");
        queryObject.setExecutableQuery("");
        syndicateDataQueryInfo.setQueryObject(queryObject);
        syndicateDataQueryInfo.setRowType("");
        return syndicateDataQueryInfo;
    }
}