package com.ca.umg.sdc.rest.controller;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.syndicatedata.delegate.SyndicateDataQueryDelegate;
import com.ca.umg.business.syndicatedata.info.SyndicateDataQueryInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataQueryResponseInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateQueryWrapper;
import com.ca.umg.sdc.rest.utils.RestResponse;
import com.ca.umg.sdc.rest.utils.TestUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class SyndicateDataQueriesControllerTest {
    private Gson gson = new GsonBuilder().create();

    @Inject
    private SyndicateDataQueryDelegate mockSyndicateDataQueryDelegate;

    @Inject
    private SyndicateDataQueriesController controller;

    @Autowired
    private WebApplicationContext ctx;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = webAppContextSetup(ctx).build();
        mockMvc = standaloneSetup(controller).build();
    }

    @Test
    public void testCreateSyndicateDataQuery() throws Exception {
        SyndicateDataQueryInfo sampleQuery = new SyndicateDataQueryInfo();
        sampleQuery.setName("TestSampleQuery");
        when(mockSyndicateDataQueryDelegate.createSyndicateDataQuery(sampleQuery)).thenReturn(sampleQuery);
        MvcResult mvcResult = mockMvc
                .perform(
                        post("/syndicateDataQueries/save").contentType(MediaType.APPLICATION_JSON).content(
                                gson.toJson(sampleQuery))).andDo(print()).andReturn();
        MockHttpServletResponse mockResponse = mvcResult.getResponse();
        RestResponse<String> restResponse = gson.fromJson(mockResponse.getContentAsString(),
                new TypeToken<RestResponse<String>>() {
                }.getType());
        assertThat(mockResponse, notNullValue());
        assertThat(restResponse.isError(), is(false));
        assertThat(restResponse.getErrorCode(), nullValue());
        assertThat(restResponse.getResponse(), is("Query Created Successfully"));
    }

    @Test
    public void testCreateSyndicateDataQueryRaisingException() throws Exception {
        SyndicateDataQueryInfo sampleQuery = new SyndicateDataQueryInfo();
        sampleQuery.setName("TestSampleQuery");
        BusinessException createExcp = new BusinessException(BusinessExceptionCodes.BSE000039, new Object[] {
                BusinessConstants.SYNDICATE_DATA_QUERY, "TestSampleQuery" });
        when(mockSyndicateDataQueryDelegate.createSyndicateDataQuery(Mockito.any(SyndicateDataQueryInfo.class))).thenThrow(
                createExcp);
        MvcResult mvcResult = mockMvc
                .perform(
                        post("/syndicateDataQueries/save").contentType(MediaType.APPLICATION_JSON).content(
                                gson.toJson(sampleQuery))).andDo(print()).andReturn();
        MockHttpServletResponse mockResponse = mvcResult.getResponse();
        RestResponse<String> restResponse = gson.fromJson(mockResponse.getContentAsString(),
                new TypeToken<RestResponse<String>>() {
                }.getType());
        assertThat(mockResponse, notNullValue());
        assertThat(restResponse.isError(), is(true));
        assertThat(restResponse.getErrorCode(), is("BSE000039"));
        assertThat(restResponse.getMessage(), is("A SyndicateDataQuery with the name TestSampleQuery already exists"));
    }

    @Test
    public void testFindAllSyndicateDataQueries() throws Exception {
        List<SyndicateDataQueryInfo> allQueries = buildSampleQueryObjects();
        when(mockSyndicateDataQueryDelegate.listAll()).thenReturn(allQueries);

        MvcResult mvcResult = mockMvc.perform(get("/syndicateDataQueries/listAll").contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andReturn();
        MockHttpServletResponse mockResponse = mvcResult.getResponse();
        RestResponse<List<SyndicateDataQueryInfo>> restResponse = gson.fromJson(mockResponse.getContentAsString(),
                new TypeToken<RestResponse<List<SyndicateDataQueryInfo>>>() {
                }.getType());

        assertThat(mockResponse, notNullValue());
        assertThat(mockResponse.getContentType(), is("application/json"));
        assertThat(restResponse.getResponse().size(), is(2));
        assertThat(restResponse.isError(), is(false));
        assertThat(restResponse.getErrorCode(), nullValue());
        assertThat(restResponse.getMessage(), is("Syndicate data queries successfully fetched"));
    }

    @Test
    public void testFindAllSyndicateDataQueriesWhenThereIsAException() throws Exception {
        when(mockSyndicateDataQueryDelegate.listAll()).thenThrow(
                new SystemException(BusinessExceptionCodes.BSE000037, new Object[] {}));

        MvcResult mvcResult = mockMvc.perform(get("/syndicateDataQueries/listAll").contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andReturn();
        MockHttpServletResponse mockResponse = mvcResult.getResponse();
        RestResponse<List<SyndicateDataQueryInfo>> restResponse = gson.fromJson(mockResponse.getContentAsString(),
                new TypeToken<RestResponse<List<SyndicateDataQueryInfo>>>() {
                }.getType());

        assertThat(mockResponse, notNullValue());
        assertThat(mockResponse.getContentType(), is("application/json"));
        assertThat(restResponse.isError(), is(true));
        assertThat(restResponse.getErrorCode(), is("BSE000037"));
        assertThat(restResponse.getMessage(), is("An error occurred while fetching syndicate data queries."));
    }
    
    @Test
    public void getQueriesByMappingName() throws Exception {
        List<SyndicateDataQueryInfo> allQueries = buildSampleQueryObjects();
        when(mockSyndicateDataQueryDelegate.listByMappingName("someMappingName")).thenReturn(allQueries);

        MvcResult mvcResult = mockMvc.perform(get("/syndicateDataQueries/list/someMappingName").contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andReturn();
        MockHttpServletResponse mockResponse = mvcResult.getResponse();
        RestResponse<SyndicateQueryWrapper> restResponse = gson.fromJson(mockResponse.getContentAsString(),
                new TypeToken<RestResponse<SyndicateQueryWrapper>>() {
                }.getType());

        assertThat(mockResponse, notNullValue());
        assertThat(mockResponse.getContentType(), is("application/json"));
        assertThat(restResponse.getResponse().getAllQueries().size(), is(2));
        assertThat(restResponse.isError(), is(false));
        assertThat(restResponse.getErrorCode(), nullValue());
        assertThat(restResponse.getMessage(), is("Syndicate data queries successfully fetched"));
    }

    @Test
    public void getQueriesByMappingNameWhenThereIsAException() throws Exception {
        when(mockSyndicateDataQueryDelegate.listByMappingName("someMappingName")).thenThrow(
                new SystemException(BusinessExceptionCodes.BSE000050, new Object[] { "someMappingName" }));

        MvcResult mvcResult = mockMvc
                .perform(get("/syndicateDataQueries/list/someMappingName").contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andReturn();
        MockHttpServletResponse mockResponse = mvcResult.getResponse();
        RestResponse<List<SyndicateDataQueryInfo>> restResponse = gson.fromJson(mockResponse.getContentAsString(),
                new TypeToken<RestResponse<List<SyndicateDataQueryInfo>>>() {
                }.getType());

        assertThat(mockResponse, notNullValue());
        assertThat(mockResponse.getContentType(), is("application/json"));
        assertThat(restResponse.isError(), is(true));
        assertThat(restResponse.getErrorCode(), is("BSE000050"));
        assertThat(restResponse.getMessage(),
                is("An error occurred while fetching syndicate data queries, mapping name : someMappingName."));
    }

    @Test
    public void testvalidateExecSyndicateDataQuery() throws Exception {
        SyndicateDataQueryInfo sampleQuery = new SyndicateDataQueryInfo();
        sampleQuery.setName("TestSampleQuery");
        SyndicateDataQueryResponseInfo responseInfo = new SyndicateDataQueryResponseInfo();
        responseInfo.setExecutedQuery("TestQuery");

        when(mockSyndicateDataQueryDelegate.fetchQueryTestData(sampleQuery)).thenReturn(responseInfo);

        MvcResult mvcResult = mockMvc
                .perform(
                        post("/syndicateDataQueries/testQuery").contentType(MediaType.APPLICATION_JSON).content(
                                gson.toJson(sampleQuery))).andDo(print()).andReturn();
        MockHttpServletResponse mockResponse = mvcResult.getResponse();
        RestResponse<SyndicateDataQueryResponseInfo> restResponse = gson.fromJson(mockResponse.getContentAsString(),
                new TypeToken<RestResponse<SyndicateDataQueryResponseInfo>>() {
                }.getType());

        assertThat(mockResponse, notNullValue());
        assertThat(mockResponse.getContentType(), is("application/json"));
        assertThat(restResponse.isError(), is(false));
        assertThat(restResponse.getErrorCode(), nullValue());
    }

    @Test
    public void testValidateExecSyndicateDataQueryWE() throws Exception {
        SyndicateDataQueryInfo sampleQuery = new SyndicateDataQueryInfo();
        sampleQuery.setName("TestSampleQuery");
        BusinessException createExcp = new BusinessException(BusinessExceptionCodes.BSE000039, new Object[] {
                BusinessConstants.SYNDICATE_DATA_QUERY, "TestSampleQuery" });
        when(mockSyndicateDataQueryDelegate.fetchQueryTestData(Mockito.any(SyndicateDataQueryInfo.class))).thenThrow(createExcp);
        this.mockMvc
                .perform(
                        post("/syndicateDataQueries/testQuery").contentType(TestUtil.APPLICATION_JSON_UTF8).content(
                                TestUtil.convertObjectToJsonBytes(sampleQuery))).andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is(Boolean.TRUE))).andExpect(jsonPath("$.errorCode", is("BSE000039")))
                .andExpect(jsonPath("$.message", is("A SyndicateDataQuery with the name TestSampleQuery already exists")))
                .andExpect(jsonPath("$.response", nullValue()));
    }

    @Test
    public void testUpdateExecutionSequence() throws Exception {
        SyndicateDataQueryInfo sampleQuery = new SyndicateDataQueryInfo();
        sampleQuery.setName("TestSampleQuery");
        List<SyndicateDataQueryInfo> queries = Arrays.asList(sampleQuery);
        this.mockMvc
                .perform(
                        post("/syndicateDataQueries/updateSequence").contentType(TestUtil.APPLICATION_JSON_UTF8).content(
                                TestUtil.convertObjectToJsonBytes(queries))).andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is(Boolean.FALSE))).andExpect(jsonPath("$.errorCode", nullValue()))
                .andExpect(jsonPath("$.message", is("Syndicate data query execution sequence updated successfully.")))
                .andExpect(jsonPath("$.response", is("Syndicate data query execution sequence updated successfully.")));
    }

    @Test
    public void testUpdateSyndicateDataQueries() throws Exception {
        SyndicateDataQueryInfo sampleQuery = new SyndicateDataQueryInfo();
        sampleQuery.setName("TestSampleQuery");
        this.mockMvc
                .perform(
                        post("/syndicateDataQueries/update").contentType(TestUtil.APPLICATION_JSON_UTF8).content(
                                TestUtil.convertObjectToJsonBytes(sampleQuery))).andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is(Boolean.FALSE))).andExpect(jsonPath("$.errorCode", nullValue()))
                .andExpect(jsonPath("$.message", is("Done"))).andExpect(jsonPath("$.response", is("Query Created Successfully")));
    }

    private List<SyndicateDataQueryInfo> buildSampleQueryObjects() {
        List<SyndicateDataQueryInfo> allQueries = new ArrayList<SyndicateDataQueryInfo>();
        SyndicateDataQueryInfo query1 = new SyndicateDataQueryInfo();
        query1.setName("query1");
        allQueries.add(query1);
        SyndicateDataQueryInfo query2 = new SyndicateDataQueryInfo();
        query2.setName("query2");
        allQueries.add(query2);
        return allQueries;
    }

}
