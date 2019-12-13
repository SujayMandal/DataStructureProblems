package com.ca.umg.business.syndicatedata.bo;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.data.domain.Sort;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.mapping.entity.Mapping;
import com.ca.umg.business.syndicatedata.dao.SyndicateDataQueryDAO;
import com.ca.umg.business.syndicatedata.dao.SyndicateDataQueryInputDAO;
import com.ca.umg.business.syndicatedata.dao.SyndicateDataQueryOutputDAO;
import com.ca.umg.business.syndicatedata.daohelper.SyndicateDataQueryHelper;
import com.ca.umg.business.syndicatedata.entity.SyndicateDataQuery;
import com.ca.umg.business.syndicatedata.entity.SyndicateDataQueryInput;
import com.ca.umg.business.syndicatedata.entity.SyndicateDataQueryObject;
import com.ca.umg.business.syndicatedata.entity.SyndicateDataQueryOutput;

public class SyndicateDataQueryBOImplTest {

    @Mock
    private SyndicateDataQueryDAO mockSyndicateDataQueryDAO;

    @InjectMocks
    private SyndicateDataQueryBOImpl classUnderTest = new SyndicateDataQueryBOImpl();

    @Mock
    private SyndicateDataQueryHelper sQueryHelper;
    
    @Mock
    private SyndicateDataQueryInputDAO dataQueryInputDAO;
    
    @Mock
    private SyndicateDataQueryOutputDAO dataQueryOutputDAO;
    
    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testListAllIfThereIsExceptionAcessingData() throws BusinessException {
        when(mockSyndicateDataQueryDAO.findAll(Mockito.any(Sort.class))).thenThrow(
                new InvalidDataAccessResourceUsageException("could not extract ResultSet"));
        try {
            classUnderTest.listAll();
            fail("SystemException should be thrown");
        } catch (SystemException e) {
            assertThat(e.getCode(), is("BSE000037"));
        }
    }
    
    @Test
    public void testListAll()  {
        List<SyndicateDataQuery> listSyndicateDataQuery = new ArrayList<>();
        when(mockSyndicateDataQueryDAO.findAll(Mockito.any(Sort.class))).thenReturn(listSyndicateDataQuery);
        try {
            List<SyndicateDataQuery> resultSyndicateDataQuery = classUnderTest.listAll();
            Assert.assertNotNull(resultSyndicateDataQuery);
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }
    }

    @Test
    public void testIfEmptyQueryNameIsPassed() throws BusinessException, SystemException {
        List<SyndicateDataQuery> queries = new ArrayList<>();
        SyndicateDataQuery query1 = new SyndicateDataQuery();
        queries.add(query1);
        try {
            classUnderTest.updateQueryExecutionSequence(queries);
            fail("BusinessException should be thrown");
        } catch (BusinessException e) {
            assertThat(e.getCode(), is("BSE000043"));
        }
    }

    @Test
    public void testIfSequenceIsNegativeNumber() throws BusinessException, SystemException {
        List<SyndicateDataQuery> queries = new ArrayList<>();
        SyndicateDataQuery query1 = new SyndicateDataQuery();
        query1.setName("query1");
        query1.setExecSequence(-2);
        queries.add(query1);
        try {
            classUnderTest.updateQueryExecutionSequence(queries);
            fail("BusinessException should be thrown");
        } catch (BusinessException e) {
            assertThat(e.getCode(), is("BSE000044"));
        }
    }

    @Test
    public void testIfSequenceIsZero() throws BusinessException, SystemException {
        List<SyndicateDataQuery> queries = new ArrayList<>();
        SyndicateDataQuery query1 = new SyndicateDataQuery();
        query1.setName("query1");
        query1.setExecSequence(0);
        queries.add(query1);
        try {
            classUnderTest.updateQueryExecutionSequence(queries);
            fail("BusinessException should be thrown");
        } catch (BusinessException e) {
            assertThat(e.getCode(), is("BSE000044"));
        }
    }

    @Test
    public void testIfSequenceIsOne() throws BusinessException, SystemException {
        List<SyndicateDataQuery> queries = new ArrayList<>();
        SyndicateDataQuery query1 = new SyndicateDataQuery();
        query1.setName("query1");
        query1.setExecSequence(1);
        queries.add(query1);
        when(mockSyndicateDataQueryDAO.findByName("query1")).thenReturn(query1);

        classUnderTest.updateQueryExecutionSequence(queries);

        verify(mockSyndicateDataQueryDAO).save(query1);
    }

    @Test
    public void testOnePositiveSequence() throws BusinessException, SystemException {
        List<SyndicateDataQuery> queries = new ArrayList<>();
        SyndicateDataQuery query1 = new SyndicateDataQuery();
        query1.setName("query1");
        query1.setExecSequence(2);
        queries.add(query1);
        when(mockSyndicateDataQueryDAO.findByName("query1")).thenReturn(query1);

        try {
            classUnderTest.updateQueryExecutionSequence(queries);
            fail("BusinessException should be thrown");
        } catch (BusinessException e) {
            assertThat(e.getCode(), is("BSE000042"));
        }
    }

    @Test
    public void testTwoPositiveSequences() throws BusinessException, SystemException {
        List<SyndicateDataQuery> queries = new ArrayList<>();
        SyndicateDataQuery query1 = new SyndicateDataQuery();
        query1.setName("query1");
        query1.setExecSequence(1);
        queries.add(query1);
        SyndicateDataQuery query2 = new SyndicateDataQuery();
        query2.setName("query2");
        query2.setExecSequence(2);
        queries.add(query2);
        when(mockSyndicateDataQueryDAO.findByName("query1")).thenReturn(query1);
        when(mockSyndicateDataQueryDAO.findByName("query2")).thenReturn(query2);

        classUnderTest.updateQueryExecutionSequence(queries);

        verify(mockSyndicateDataQueryDAO).save(query1);
        verify(mockSyndicateDataQueryDAO).save(query2);
    }

    @Test
    public void testSequencesWithGaps() throws BusinessException, SystemException {
        List<SyndicateDataQuery> queries = new ArrayList<>();
        SyndicateDataQuery query1 = new SyndicateDataQuery();
        query1.setName("query1");
        query1.setExecSequence(1);
        queries.add(query1);
        SyndicateDataQuery query2 = new SyndicateDataQuery();
        query2.setName("query2");
        query2.setExecSequence(3);
        queries.add(query2);
        when(mockSyndicateDataQueryDAO.findByName("query1")).thenReturn(query1);
        when(mockSyndicateDataQueryDAO.findByName("query2")).thenReturn(query2);

        try {
            classUnderTest.updateQueryExecutionSequence(queries);
            fail("BusinessException should be thrown");
        } catch (BusinessException e) {
            assertThat(e.getCode(), is("BSE000042"));
        }
    }

    @Test
    public void testDuplicateSequences() throws BusinessException, SystemException {
        List<SyndicateDataQuery> queries = new ArrayList<>();
        SyndicateDataQuery query1 = new SyndicateDataQuery();
        query1.setName("query1");
        query1.setExecSequence(1);
        queries.add(query1);
        SyndicateDataQuery query2 = new SyndicateDataQuery();
        query2.setName("query2");
        query2.setExecSequence(1);
        queries.add(query2);
        when(mockSyndicateDataQueryDAO.findByName("query1")).thenReturn(query1);
        when(mockSyndicateDataQueryDAO.findByName("query2")).thenReturn(query2);

        try {
            classUnderTest.updateQueryExecutionSequence(queries);
            fail("BusinessException should be thrown");
        } catch (BusinessException e) {
            assertThat(e.getCode(), is("BSE000041"));
        }
    }

    @Test
    public void listByMappingNameHappyPath() throws BusinessException, SystemException {

        classUnderTest.listByMappingName("someMappingName");

        verify(mockSyndicateDataQueryDAO).findByMappingNameOrderByExecSequenceAsc("someMappingName");
    }

    @Test
    public void listByMappingNameWhenThereIsADataAccessException() throws BusinessException, SystemException {
        when(mockSyndicateDataQueryDAO.findByMappingNameOrderByExecSequenceAsc("someMappingName")).thenThrow(
                new InvalidDataAccessResourceUsageException("could not extract ResultSet"));
        try {
            classUnderTest.listByMappingName("someMappingName");
            fail("SystemException should be thrown");
        } catch (SystemException e) {
            assertThat(e.getCode(), is("BSE000050"));
        }
    }

    @Test
    public void listByMappingNameAndTypeHappyPath() throws BusinessException, SystemException {

        classUnderTest.listByMappingNameAndType("someMappingName", "someMappingType");

        verify(mockSyndicateDataQueryDAO).findByMappingNameAndMappingTypeOrderByExecSequenceAsc("someMappingName",
                "someMappingType");
    }

    @Test
    public void listByMappingNameAndTypeWhenThereIsADataAccessException() throws BusinessException, SystemException {
        when(
                mockSyndicateDataQueryDAO.findByMappingNameAndMappingTypeOrderByExecSequenceAsc("someMappingName",
                        "someMappingType")).thenThrow(new InvalidDataAccessResourceUsageException("could not extract ResultSet"));
        try {
            classUnderTest.listByMappingNameAndType("someMappingName", "someMappingType");
            fail("SystemException should be thrown");
        } catch (SystemException e) {
            assertThat(e.getCode(), is("BSE000051"));
        }
    }

    private SyndicateDataQuery getSyndicateDataQuery() {
        String testDate = new SimpleDateFormat("MMM-dd-YYYY").format(new Date());
        Set<SyndicateDataQueryInput> dataQueryInputs = new HashSet<>();
        SyndicateDataQueryInput input = new SyndicateDataQueryInput();
        input.setName("TESTDATE");
        input.setDataType("STRING");
        input.setSampleValue(testDate);
        dataQueryInputs.add(input);
        Mapping mapping = new Mapping();
        mapping.setId("2365-9658743-56321-77845");
        SyndicateDataQuery query = new SyndicateDataQuery();
        query.setName("myqueryUpdate");
        query.setMapping(mapping);
        query.setInputParameters(dataQueryInputs);
        SyndicateDataQueryObject queryObject = new SyndicateDataQueryObject();
        query.setQueryObject(queryObject);
        query.setRowType("MULTIPLEROW");
        query.setDataType("ARRAY");
        queryObject.setSelectString("TAB1.COLUMN1 AS \"COL1ALIAS\", TAB2.COLUMN2 AS \"COL2ALIAS\"");
        queryObject.setFromString("SYND_DATA_TABLE TAB1, SYND_DATA_TABLE TAB2");
        queryObject.setWhereClause("TAB1.COLUMN1 = #A1# AND TAB1.COLUMN3 = #COL3#");

        input = new SyndicateDataQueryInput();
        input.setName("A1");
        input.setDataType("STRING");
        input.setSampleValue("A1");
        dataQueryInputs.add(input);

        input = new SyndicateDataQueryInput();
        input.setName("COL3");
        input.setDataType("DATE");
        input.setSampleValue("JUL-10-2014");
        dataQueryInputs.add(input);
        
        Set<SyndicateDataQueryOutput> outputParameters = new HashSet<>();
        SyndicateDataQueryOutput syndicateDataQueryOutput = new SyndicateDataQueryOutput();
        syndicateDataQueryOutput.setDataType("INTEGER");
        syndicateDataQueryOutput.setName("out1");
        syndicateDataQueryOutput.setQuery(query);
        syndicateDataQueryOutput.setSequence(1);
        syndicateDataQueryOutput.setTenantId("value");
        outputParameters.add(syndicateDataQueryOutput);
        query.setOutputParameters(outputParameters);
        
        return query;
    }
    
    @Test
    public void updateTest(){
        SyndicateDataQuery query = this.getSyndicateDataQuery();
        Map<String, String> mapValue = new HashMap<>();
        try {
            when(mockSyndicateDataQueryDAO.findByNameAndMappingId(query.getName(), query.getMapping().getId())).thenReturn(query);
            when(mockSyndicateDataQueryDAO.save(query)).thenReturn(query);
            when(sQueryHelper.fetchTableAliases("")).thenReturn(mapValue);
            when(dataQueryInputDAO.getOne("")).thenReturn(null);
            when(dataQueryOutputDAO.getOne("")).thenReturn(null);
            
            SyndicateDataQuery resultQuery = classUnderTest.update(query);
            Assert.assertNotNull(resultQuery);
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }
    }

    @SuppressWarnings("unchecked")
    @Ignore
    public void deletedOutputParametersTest(){
        SyndicateDataQuery query = this.getSyndicateDataQuery();
        SyndicateDataQuery query1 = this.getSyndicateDataQuery();
        for(SyndicateDataQueryOutput dataQueryOutput: query1.getOutputParameters()){
            dataQueryOutput.setName(dataQueryOutput.getName()+"_1");
        }
        when(mockSyndicateDataQueryDAO.findByNameAndMappingId(query.getName(), query.getMapping().getId())).thenReturn(query1);
        
        try {
            Map<String, Object> queryParams = classUnderTest.systemParameters(query);
            List<String> resultParameters = (List<String>) queryParams.get(BusinessConstants.DELETED_PARAMS);
            Assert.assertNotNull(resultParameters);
            for (String resultParameter : resultParameters) {
                Assert.assertNotNull(resultParameter);
                assertThat(resultParameter, is("myqueryUpdate/out1_1"));
            }
        } catch (BusinessException | SystemException e) {
           Assert.fail();
        }
    }
}
