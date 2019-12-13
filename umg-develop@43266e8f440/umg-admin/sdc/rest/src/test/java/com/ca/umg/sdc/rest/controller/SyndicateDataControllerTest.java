/*
 * SyndicateDataControllerTest.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.sdc.rest.controller;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
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
import com.ca.umg.business.common.info.SearchOptions;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.syndicatedata.delegate.SyndicateDataDelegate;
import com.ca.umg.business.syndicatedata.info.SyndicateDataContainerInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataVersionInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateFileDataInfo;
import com.ca.umg.sdc.rest.utils.CSVUtil;
import com.ca.umg.sdc.rest.utils.TestUtil;

import junit.framework.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class SyndicateDataControllerTest extends BaseTest {

    @Inject
    private SyndicateDataDelegate syndicateDataDelegate;

    @Inject
    private SyndicateDataController controller;

    @Autowired
    private WebApplicationContext ctx;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = webAppContextSetup(ctx).build();
        mockMvc = standaloneSetup(controller).build();
        buildSyndicateDataColumnInfoList();
        buildSyndicateDataInfoList();
    }

    private Resource dataFile = new ClassPathResource("testdata/MSA_HPI_factor.csv");
    private Resource duplciate_dataFile = new ClassPathResource("testdata/MSA_HPI_factor_duplicatecolumn.csv");

    @Test
    public void testCsvWithDuplicateColumns() throws IOException {
        try
        {
            CSVUtil.readAllRecords(duplciate_dataFile.getInputStream());
        } catch (SystemException | BusinessException e) {
            Assert.assertEquals(BusinessExceptionCodes.BSE000128, e.getCode());
        }
       
    }

    @Test
    public void testReadObjectsFromCsv() throws IOException, SystemException, BusinessException {
        // SyndicateDataController classUnderTest = new SyndicateDataController();
        SyndicateFileDataInfo syndicateFileDataInfo = CSVUtil.readAllRecords(dataFile.getInputStream());
        Assert.assertNotNull(syndicateFileDataInfo);
        List<Map<String, String>> data = syndicateFileDataInfo.getData();
        assertThat(data.get(0).get("MSA_CODE_VC_PK"), is("Akron"));
        assertThat(data.get(0).get("HPI_DATE_DT_NN"), is("6/1/2017 0:00"));
        assertThat(data.get(0).get("HPI_FCTR_NM_NN"), is("175.7628617"));
        assertThat(data.get(0).get("CRTD_BY_VC_NN"), is("SYSTEM"));
        // TODO : date conversion
        assertThat(data.get(0).get("CRTD_DATE_DT_NN"), is("9/19/2013 15:36"));
        assertThat(data.get(0).get("MODI_BY_VC"), is(""));
        assertThat(data.get(0).get("MODI_DATE_DT"), is(""));

        assertThat(data.get(3).get("MSA_CODE_VC_PK"), is("Akron"));
        // TODO : date conversion
        assertThat(data.get(3).get("HPI_DATE_DT_NN"), is("9/1/2017 0:00"));
        assertThat(data.get(3).get("HPI_FCTR_NM_NN"), is("180.3800857"));
        assertThat(data.get(3).get("CRTD_BY_VC_NN"), is("SYSTEM"));
        assertThat(data.get(3).get("CRTD_DATE_DT_NN"), is("9/19/2013 15:36"));
        assertThat(data.get(3).get("MODI_BY_VC"), is(""));
        assertThat(data.get(3).get("MODI_DATE_DT"), is(""));
    }

    @Test
    public void testFindSyndicateData() throws Exception {
        Map<String, String> map1 = new HashMap<>();
        Map<String, String> map2 = new HashMap<>();
        List<Map<String, String>> syndicateVersionData = Arrays.asList(map1, map2);
        SyndicateDataContainerInfo sdci = buildSyndicateDataContainerInfo("Test Container", 1L, "Ver1",
                "August 20, 2014, 14:14:00", syndicateVersionData);
        when(syndicateDataDelegate.getContainerVersionInformation(1L, "Test Container")).thenReturn(sdci);
        this.mockMvc
                .perform(
                        get("/syndicateData/version/{versionid}/{containerName}", 1L, "Test Container").accept(
                                MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is(Boolean.FALSE))).andExpect(jsonPath("$.errorCode", nullValue()))
                .andExpect(jsonPath("$.message", is("Done"))).andExpect(jsonPath("$.response", notNullValue()));
    }

    @Test
    public void testFindSyndicateDataWE() throws Exception {
        when(syndicateDataDelegate.getContainerVersionInformation(1L, "Test Container")).thenThrow(
                new SystemException(BusinessExceptionCodes.BSE000001, new Object[] {}));
        this.mockMvc
                .perform(
                        get("/syndicateData/version/{versionid}/{containerName}", 1L, "Test Container").accept(
                                MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is(Boolean.TRUE))).andExpect(jsonPath("$.errorCode", is("BSE000001")))
                .andExpect(jsonPath("$.message", notNullValue())).andExpect(jsonPath("$.response", nullValue()));
    }

    @Test
    public void testFindAllSyndicateData() throws Exception {

        SyndicateDataVersionInfo sdvi = buildSyndicateDataVersionInfo();
        when(syndicateDataDelegate.listVersions("Test Container")).thenReturn(sdvi);
        this.mockMvc
                .perform(
                        get("/syndicateData/version/listAll/{containerName}", "Test Container")
                                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is(Boolean.FALSE))).andExpect(jsonPath("$.errorCode", nullValue()))
                .andExpect(jsonPath("$.message", is("Done"))).andExpect(jsonPath("$.response", notNullValue()));

    }

    @Test
    public void testFindAllSyndicateDataWE() throws Exception {
        when(syndicateDataDelegate.listVersions("Test Container")).thenThrow(
                new SystemException(BusinessExceptionCodes.BSE000001, new Object[] {}));
        this.mockMvc
                .perform(
                        get("/syndicateData/version/listAll/{containerName}", "Test Container")
                                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is(Boolean.TRUE))).andExpect(jsonPath("$.errorCode", is("BSE000001")))
                .andExpect(jsonPath("$.message", notNullValue())).andExpect(jsonPath("$.response", nullValue()));

    }

    @Test
    public void testListContainers() throws Exception {
        Map<String, String> map1 = new HashMap<>();
        Map<String, String> map2 = new HashMap<>();
        List<Map<String, String>> syndicateVersionData = Arrays.asList(map1, map2);
        SyndicateDataContainerInfo sdci = buildSyndicateDataContainerInfo("REO Container", 2L, "REO Version1",
                "August 20, 2014, 14:14:00", syndicateVersionData);

        List<SyndicateDataContainerInfo> sdcis = Arrays.asList(sdci);
        when(syndicateDataDelegate.getContainerInformation()).thenReturn(sdcis);
        this.mockMvc.perform(get("/syndicateData/container/listAll").accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk()).andExpect(jsonPath("$.error", is(Boolean.FALSE)))
                .andExpect(jsonPath("$.errorCode", nullValue())).andExpect(jsonPath("$.message", is("Done")))
                .andExpect(jsonPath("$.response", notNullValue()))
                .andExpect(jsonPath("$.response[0].containerName", is("REO Container")));

    }
    
    @Test
    public void testListFilteredContainer() throws Exception {
    	SearchOptions searchOptions = buildSearchOptions(0, 5, "containerName", false);
        this.mockMvc.perform(post("/syndicateData/container/listFilteredContainer").contentType(TestUtil.APPLICATION_JSON_UTF8).content(
                TestUtil.convertObjectToJsonBytes(searchOptions))).andDo(print())
                .andExpect(status().isOk()).andExpect(jsonPath("$.error", is(Boolean.FALSE)))
                .andExpect(jsonPath("$.errorCode", nullValue())).andExpect(jsonPath("$.message", is("Done")))
                .andExpect(jsonPath("$.response", nullValue()));
    }

    @Test
    public void testListContainersWE() throws Exception {
        when(syndicateDataDelegate.getContainerInformation()).thenThrow(
                new SystemException(BusinessExceptionCodes.BSE000001, new Object[] {}));
        this.mockMvc.perform(get("/syndicateData/container/listAll", "Test Container").accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.error", is(Boolean.TRUE)))
                .andExpect(jsonPath("$.errorCode", is("BSE000001"))).andExpect(jsonPath("$.message", notNullValue()))
                .andExpect(jsonPath("$.response", nullValue()));

    }

    @Test
    public void testFindContainer() throws Exception {
        buildSyndicateDataColumnInfoList();
        Map<String, String> map1 = new HashMap<>();
        Map<String, String> map2 = new HashMap<>();
        List<Map<String, String>> syndicateVersionData = Arrays.asList(map1, map2);

        SyndicateDataContainerInfo sdci = buildSyndicateDataContainerInfo("REO Container", 2L, "REO Version1",
                "August 20, 2014, 14:14:00", syndicateVersionData);

        when(syndicateDataDelegate.getContainerInformation("REO Container")).thenReturn(sdci);
        this.mockMvc.perform(get("/syndicateData/container/{containerName}", "REO Container").accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.error", is(Boolean.FALSE)))
                .andExpect(jsonPath("$.errorCode", nullValue())).andExpect(jsonPath("$.message", is("Done")))
                .andExpect(jsonPath("$.response", notNullValue()))
                .andExpect(jsonPath("$.response.containerName", is("REO Container")));
    }

    @Test
    public void testFindContainerWE() throws Exception {
        when(syndicateDataDelegate.getContainerInformation("REO Container")).thenThrow(
                new SystemException(BusinessExceptionCodes.BSE000001, new Object[] {}));
        this.mockMvc.perform(get("/syndicateData/container/{containerName}", "REO Container").accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.error", is(Boolean.TRUE)))
                .andExpect(jsonPath("$.errorCode", is("BSE000001"))).andExpect(jsonPath("$.message", notNullValue()))
                .andExpect(jsonPath("$.response", nullValue()));

    }

    @Test
    public void testDeleteContainerVersion() throws Exception {
        this.mockMvc
                .perform(
                        delete("/syndicateData/{containerName}/{versionId}", "REO Container", 2L).accept(
                                MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is(Boolean.FALSE))).andExpect(jsonPath("$.errorCode", nullValue()))
                .andExpect(jsonPath("$.message", is("Done"))).andExpect(jsonPath("$.response", notNullValue()))
                .andExpect(jsonPath("$.response", is("Success, container version successfully deleted")));
    }

    @Test
    public void testUpdateProvider() throws Exception {

        buildSyndicateDataColumnInfoList();
        Map<String, String> map1 = new HashMap<>();
        Map<String, String> map2 = new HashMap<>();
        List<Map<String, String>> syndicateVersionData = Arrays.asList(map1, map2);
        SyndicateDataContainerInfo sdci = buildSyndicateDataContainerInfo("REO Container", 2L, "REO Version1",
                "August 20, 2014, 14:14:00", syndicateVersionData);

        this.mockMvc
                .perform(
                        put("/syndicateData/update/container").contentType(TestUtil.APPLICATION_JSON_UTF8).content(
                                TestUtil.convertObjectToJsonBytes(sdci))).andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is(Boolean.FALSE))).andExpect(jsonPath("$.errorCode", nullValue()))
                .andExpect(jsonPath("$.message", is("Done"))).andExpect(jsonPath("$.response", nullValue()));

    }

    @Test
    public void testUpdateProviderVersion() throws Exception {

        buildSyndicateDataColumnInfoList();
        Map<String, String> map1 = new HashMap<>();
        Map<String, String> map2 = new HashMap<>();
        List<Map<String, String>> syndicateVersionData = Arrays.asList(map1, map2);
        SyndicateDataContainerInfo sdci = buildSyndicateDataContainerInfo("REO Container", 2L, "REO Version1",
                "August 20, 2014, 14:14:00", syndicateVersionData);

        this.mockMvc
                .perform(
                        put("/syndicateData/update/version").contentType(TestUtil.APPLICATION_JSON_UTF8).content(
                                TestUtil.convertObjectToJsonBytes(sdci))).andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is(Boolean.FALSE))).andExpect(jsonPath("$.errorCode", nullValue()))
                .andExpect(jsonPath("$.message", is("Done"))).andExpect(jsonPath("$.response", nullValue()));

    }

    @Test
    public void testDownloadSyndData() throws Exception {
        Mockito.when(syndicateDataDelegate.downloadSyndTableData("REO", 250L)).thenReturn(getSyndData());
        this.mockMvc.perform(
                get("/syndicateData/version/template/{containerName:.+}/{versionId:.+}/{versionName:.+}", "REO", 250L, "test"))
                .andExpect(status().isOk());
        MvcResult mvcResult = this.mockMvc
                .perform(
                        get("/syndicateData/version/template/{containerName:.+}/{versionId:.+}/{versionName:.+}", "REO", 250L,
                                "test")).andDo(print()).andReturn();

        MockHttpServletResponse mockResponse = mvcResult.getResponse();
        assertThat(mockResponse, notNullValue());


    }


    private List<String> getSyndData() {
        List<String> syndDataList = new ArrayList<String>();
        syndDataList.add("MSA_CODE_VC_PK,HPI_DATE_DT_NN,HPI_FCTR_NM_NN");
        syndDataList.add("Battle Creek,2008-11-01,159.118");
        syndDataList.add("Iowa City,2008-11-01,156.945");
        return syndDataList;
    }
}
