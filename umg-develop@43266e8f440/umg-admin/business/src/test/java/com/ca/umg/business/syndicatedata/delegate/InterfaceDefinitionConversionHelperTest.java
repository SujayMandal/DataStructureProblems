package com.ca.umg.business.syndicatedata.delegate;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.mid.extraction.info.TidSqlInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataQueryInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataQueryParameterInfo;

public class InterfaceDefinitionConversionHelperTest {

    private InterfaceDefinitionConversionHelper classUnderTest;

    @Before
    public void setUp() {
        classUnderTest = new InterfaceDefinitionConversionHelper();
    }

    @Test
    public void getInterfaceDefinitionSqlInfos() throws BusinessException, SystemException {

        List<TidSqlInfo> sqlInfos = classUnderTest.convertToInterfaceDefinitionInfos(createQueries());

        assertThat(sqlInfos.size(), is(2));
        TidSqlInfo info1 = sqlInfos.get(0);
        TidSqlInfo info2 = sqlInfos.get(1);
        assertThat(info1.getSqlName(), is("query1"));
        assertThat(info1.getInputParams().get(0).getApiName(), is("input1"));
        assertThat(info1.getInputParams().get(0).getFlatenedName(), is("input1"));
        assertThat(info1.getInputParams().get(0).isSyndicate(), is(true));
        assertThat(info1.getInputParams().get(0).getDatatype().getType(), is("STRING"));
        assertThat(info1.getOutputParams().get(0).getApiName(), is("output1"));
        assertThat(info1.getOutputParams().get(0).getFlatenedName(), is("query1/output1"));
        // assertThat(info1.getOutputParams().get(0).isSyndicate(), is(true));
        assertThat(info1.getOutputParams().get(0).getDatatype().getType(), is("STRING"));
        assertThat(info2.getSqlName(), is("query2"));
    }

    private List<SyndicateDataQueryInfo> createQueries() {
        List<SyndicateDataQueryInfo> queries = new ArrayList<>();
        SyndicateDataQueryInfo query1 = new SyndicateDataQueryInfo();
        query1.setName("query1");
        List<SyndicateDataQueryParameterInfo> inputParameters = new ArrayList<SyndicateDataQueryParameterInfo>();
        SyndicateDataQueryParameterInfo inputParam = new SyndicateDataQueryParameterInfo();
        inputParam.setName("input1");
        inputParam.setDataType("STRING");
        inputParameters.add(inputParam);
        query1.setInputParameters(inputParameters);
        List<SyndicateDataQueryParameterInfo> outputParameters = new ArrayList<SyndicateDataQueryParameterInfo>();
        SyndicateDataQueryParameterInfo outputParam = new SyndicateDataQueryParameterInfo();
        outputParam.setName("output1");
        outputParam.setDataType("STRING");
        outputParameters.add(outputParam);
        query1.setOutputParameters(outputParameters);
        queries.add(query1);
        SyndicateDataQueryInfo query2 = new SyndicateDataQueryInfo();
        query2.setName("query2");
        queries.add(query2);
        return queries;
    }

}
