package com.ca.umg.business.syndicatedata.dao;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ca.umg.business.mapping.dao.AbstractMappingTest;
import com.ca.umg.business.mapping.entity.Mapping;
import com.ca.umg.business.model.entity.Model;
import com.ca.umg.business.syndicatedata.entity.SyndicateDataQuery;
import com.ca.umg.business.syndicatedata.entity.SyndicateDataQueryInput;
import com.ca.umg.business.syndicatedata.entity.SyndicateDataQueryObject;
import com.ca.umg.business.syndicatedata.entity.SyndicateDataQueryOutput;

@ContextHierarchy({ @ContextConfiguration("classpath:root-db-context.xml"), @ContextConfiguration })
@RunWith(SpringJUnit4ClassRunner.class)
public class SyndicateDataQueryDAOTest extends AbstractMappingTest {

    @Before
    public void setup() {
        getLocalhostTenantContext();
    }

    @Test
    public void saveTest() {
        String tenantId = getLocalhostTenantContext().getTenantCode();
        Model model = createModel("MOD1", "desc", "docname", "ioDefinitionName", "type", "ioDefinition");
        Mapping mapping = createMapping("M1", model, tenantId, "MOD_DESC", "MAPPING_IO");
        createSyndicateDataQuery("name", "Input", tenantId, mapping);

        List<SyndicateDataQuery> sList = getSyndicateDataQueryDAO().findAll();

        Assert.assertNotNull(sList);
        Assert.assertTrue(CollectionUtils.isNotEmpty(sList));

        SyndicateDataQuery dataQueryDB = sList.get(0);
        Assert.assertNotNull(dataQueryDB.getInputParameters());
        Assert.assertTrue(CollectionUtils.isNotEmpty(dataQueryDB.getInputParameters()));

        Assert.assertNotNull(dataQueryDB.getOutputParameters());
        Assert.assertTrue(CollectionUtils.isNotEmpty(dataQueryDB.getOutputParameters()));
    }

    @Test
    public void getQueriesByMappingName() {
        String tenantId = getLocalhostTenantContext().getTenantCode();
        Model model1 = createModel("model1", "desc", "docname", "ioDefinitionName", "type", "ioDefinition");
        Mapping mapping1 = createMapping("mapping1", model1, tenantId, "MOD_DESC", "MAPPING_IO");
        createSyndicateDataQuery("query1", "Input", tenantId, mapping1);

        Model model2 = createModel("model2", "desc", "docname", "ioDefinitionName", "type", "ioDefinition");
        Mapping mapping2 = createMapping("mapping2", model2, tenantId, "MOD_DESC", "MAPPING_IO");
        createSyndicateDataQuery("query2", "Input", tenantId, mapping2);

        List<SyndicateDataQuery> queries = getSyndicateDataQueryDAO().findByMappingNameOrderByExecSequenceAsc("mapping2");

        assertThat(queries.size(), is(1));
        assertThat(queries.get(0).getName(), is("query2"));
    }

    @Test
    public void getQueriesByMappingNameAndType() {
        String tenantId = getLocalhostTenantContext().getTenantCode();
        Model model = createModel("model21", "desc", "docname", "ioDefinitionName", "type", "ioDefinition");
        Mapping mapping = createMapping("mapping11", model, tenantId, "MOD_DESC", "MAPPING_IO");
        createSyndicateDataQuery("query11", "Input", tenantId, mapping);
        createSyndicateDataQuery("query12", "Output", tenantId, mapping);

        List<SyndicateDataQuery> queries = getSyndicateDataQueryDAO().findByMappingNameAndMappingTypeOrderByExecSequenceAsc(
                "mapping11", "Output");

        assertThat(queries.size(), is(1));
        assertThat(queries.get(0).getName(), is("query12"));
    }

    @Test
    public void testCreateSyndicateDataQueryItem() {
        SyndicateDataQuery syndDataQuery = new SyndicateDataQuery();
        String tenantId = getLocalhostTenantContext().getTenantCode();
        Model model = createModel("MOD2", "desc", "docname", "ioDefinitionName", "type", "ioDefinition");
        Mapping mapping = createMapping("M2", model, tenantId, "MOD_DESC", "MAPPING_IO");
        syndDataQuery.setName("TestQuery");
        syndDataQuery.setDescription("TestDescription");
        syndDataQuery.setRowType("SINGLE");
        syndDataQuery.setDataType("ARRAY");
        syndDataQuery.setMappingType("Input");
        syndDataQuery.setExecSequence(1);
        syndDataQuery.setMapping(mapping);
        SyndicateDataQueryObject syndDataQueryObject = createQueryObject("Select A,B,C", "FROM ABC", "WHERE C ='ABC'",
                "ORDER BY A ASC", "Select A,B,C FROM ABC WHERE C ='ABC' ORDER BY A ASC");
        Set<SyndicateDataQueryInput> inputParameters = new HashSet<SyndicateDataQueryInput>();
        SyndicateDataQueryInput inputParams = createInputParams("C", "STRING", "ABC", syndDataQuery);
        inputParameters.add(inputParams);
        Set<SyndicateDataQueryOutput> outputParameters = new HashSet<SyndicateDataQueryOutput>();
        SyndicateDataQueryOutput outputParams = createOutputParams("A", "STRING", syndDataQuery, 1);
        outputParameters.add(outputParams);
        outputParams = createOutputParams("B", "STRING", syndDataQuery, 2);
        outputParameters.add(outputParams);
        outputParams = createOutputParams("C", "STRING", syndDataQuery, 3);
        outputParameters.add(outputParams);
        syndDataQuery.setQueryObject(syndDataQueryObject);
        syndDataQuery.setInputParameters(inputParameters);
        syndDataQuery.setOutputParameters(outputParameters);
        SyndicateDataQuery savedValue = getSyndicateDataQueryDAO().save(syndDataQuery);
        Assert.assertNotNull(savedValue);
    }

    private SyndicateDataQuery createSyndicateDataQuery(String name, String mappingType, String tenantId, Mapping mapping) {
        SyndicateDataQueryObject dataQueryObject = new SyndicateDataQueryObject();
        SyndicateDataQuery dataQuery = new SyndicateDataQuery();
        dataQuery.setMapping(mapping);
        dataQuery.setMappingType(mappingType);
        Set<SyndicateDataQueryInput> inputs = new HashSet<>();
        Set<SyndicateDataQueryOutput> outputs = new HashSet<>();
        dataQuery.setQueryObject(dataQueryObject);

        dataQuery.setDescription("description");
        dataQuery.setExecSequence(1);
        dataQueryObject.setFromString("from");
        dataQuery.setName(name);
        dataQueryObject.setSelectString("select");
        dataQuery.setTenantId(tenantId);
        dataQueryObject.setExecutableQuery("exec");
        dataQuery.setRowType("SINGLE");
        dataQuery.setDataType("ARRAY");
        dataQuery.setInputParameters(inputs);
        dataQuery.setOutputParameters(outputs);

        SyndicateDataQueryInput input = new SyndicateDataQueryInput();
        input.setName("col1");
        input.setSampleValue("val1");
        input.setDataType("type");
        input.setQuery(dataQuery);
        dataQuery.getInputParameters().add(input);

        input = new SyndicateDataQueryInput();
        input.setName("col2");
        input.setSampleValue("val2");
        input.setDataType("type");
        input.setQuery(dataQuery);
        dataQuery.getInputParameters().add(input);

        SyndicateDataQueryOutput output = new SyndicateDataQueryOutput();
        output.setName("opt1");
        output.setQuery(dataQuery);
        output.setDataType("data");
        output.setSequence(1);
        dataQuery.getOutputParameters().add(output);

        output = new SyndicateDataQueryOutput();
        output.setName("opt2");
        output.setQuery(dataQuery);
        output.setSequence(2);
        output.setDataType("data");
        dataQuery.getOutputParameters().add(output);
        getSyndicateDataQueryDAO().save(dataQuery);
        return dataQuery;
    }

    private SyndicateDataQueryOutput createOutputParams(String name, String dataType, SyndicateDataQuery syndDataQuery,
            Integer sequence) {
        SyndicateDataQueryOutput outputParams = new SyndicateDataQueryOutput();
        outputParams.setName(name);
        outputParams.setDataType(dataType);
        outputParams.setQuery(syndDataQuery);
        outputParams.setSequence(sequence);
        return outputParams;
    }

    private SyndicateDataQueryInput createInputParams(String name, String dataType, String sampleValue,
            SyndicateDataQuery syndDataQuery) {
        SyndicateDataQueryInput inputParams = new SyndicateDataQueryInput();
        inputParams.setName(name);
        inputParams.setDataType(dataType);
        inputParams.setSampleValue(sampleValue);
        inputParams.setQuery(syndDataQuery);
        return inputParams;
    }

    private SyndicateDataQueryObject createQueryObject(String selectStr, String fromStr, String whrClause, String orderByStr,
            String execQuery) {
        SyndicateDataQueryObject syndDataQueryObject = new SyndicateDataQueryObject();
        syndDataQueryObject.setSelectString(selectStr);
        syndDataQueryObject.setFromString(fromStr);
        syndDataQueryObject.setWhereClause(whrClause);
        syndDataQueryObject.setOrderByString(orderByStr);
        syndDataQueryObject.setExecutableQuery(execQuery);
        return syndDataQueryObject;
    }

}
