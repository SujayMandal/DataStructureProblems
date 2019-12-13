package com.ca.umg.sdc.rest.controller;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import com.ca.framework.core.db.persistance.CAAbstractRoutingDataSource;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.umg.business.common.info.PageRecord;
import com.ca.umg.business.common.info.ResponseWrapper;
import com.ca.umg.business.common.info.SearchOptions;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.mapping.info.MappingInfo;
import com.ca.umg.business.mappingnotification.delegate.MappingNotificationDelegate;
import com.ca.umg.business.transaction.info.TransactionWrapper;
import com.ca.umg.business.version.delegate.VersionDelegate;
import com.ca.umg.business.version.info.CreateVersionInfo;
import com.ca.umg.business.version.info.VersionHierarchyInfo;
import com.ca.umg.business.version.info.VersionInfo;
import com.ca.umg.business.version.info.VersionMetricRequestInfo;
import com.ca.umg.business.version.info.VersionSummaryInfo;
import com.ca.umg.notification.notify.NotificationTriggerDelegate;
import com.ca.umg.sdc.rest.utils.RestResponse;
import com.ca.umg.sdc.rest.utils.TestUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
@ContextHierarchy({ @ContextConfiguration("classpath:root-db-context.xml"), @ContextConfiguration })
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@JsonIgnoreProperties
public class VersionControllerTest {

    private Gson gson = new GsonBuilder().create();
	
    @Inject
    private VersionDelegate mockVersionDelegate;

    @Inject
    private VersionController controller;

    @Autowired
    private WebApplicationContext ctx;

    private MockMvc mockMvc;

    private List<VersionHierarchyInfo> versionHierarchyInfoList;

    private List<VersionInfo> versionInfoList;

    private List<MappingInfo> mappingInfoList;

    private List<String> tidMappingList;
    private VersionInfo versionInfo1;

    private CreateVersionInfo createVersionInfo;

    private PageRecord<String> record;

    private PageRecord<VersionInfo> recordVersion;

    private VersionSummaryInfo versionSummary1;

    @Before
    public void setUp() {
    	initMocks(this);
        mockMvc = webAppContextSetup(ctx).build();
        mockMvc = standaloneSetup(controller).build();
        buildDummyLists();
    }

    @Test
    public void getAllLibraryNames() throws Exception {
        List<String> libraryNames = Arrays.asList("library1", "library2", "library3", "library4");
        when(mockVersionDelegate.getAllLibraryNames()).thenReturn(libraryNames);

        RestResponse<List<String>> restResponse = performGetRequest("/version/listAllLibraryNames");

        assertThat(restResponse.getResponse().size(), is(4));
        assertThat(restResponse.getResponse().get(0), is("library1"));
        assertThat(restResponse.isError(), is(false));
        assertThat(restResponse.getErrorCode(), nullValue());
        assertThat(restResponse.getMessage(), is("Done"));
    }

    @Test
    public void getAllLibraryNamesWhenThereIsAnException() throws Exception {
        when(mockVersionDelegate.getAllLibraryNames()).thenThrow(
                new SystemException(BusinessExceptionCodes.BSE000001, new Object[] {}));

        RestResponse<List<String>> restResponse = performGetRequest("/version/listAllLibraryNames");

        assertThat(restResponse.isError(), is(true));
        assertThat(restResponse.getErrorCode(), is("BSE000001"));
    }

    @Test
    public void getAllLibraryRecords() throws Exception {
        List<String> libraryRecordNames = Arrays.asList("libraryRecord1", "libraryRecord2", "libraryRecord3");
        when(mockVersionDelegate.getAllLibraryRecords("library1")).thenReturn(libraryRecordNames);

        RestResponse<List<String>> restResponse = performGetRequest("/version/listAllLibraryRecords/library1");

        assertThat(restResponse.getResponse().size(), is(3));
        assertThat(restResponse.getResponse().get(0), is("libraryRecord1"));
        assertThat(restResponse.isError(), is(false));
        assertThat(restResponse.getErrorCode(), nullValue());
        assertThat(restResponse.getMessage(), is("Done"));
    }
    
    @Test
    public void getAllLibraryRecordsWhenThereIsAnException() throws Exception {
        when(mockVersionDelegate.getAllLibraryRecords("library1")).thenThrow(
                new SystemException(BusinessExceptionCodes.BSE000001, new Object[] {}));

        RestResponse<List<String>> restResponse = performGetRequest("/version/listAllLibraryRecords/library1");

        assertThat(restResponse.isError(), is(true));
        assertThat(restResponse.getErrorCode(), is("BSE000001"));
    }

    @Test
    public void getAllModelNames() throws Exception {
        List<String> modelNames = Arrays.asList("model1", "model2", "model3", "model4", "model5");
        when(mockVersionDelegate.getAllModelNames()).thenReturn(modelNames);

        RestResponse<List<String>> restResponse = performGetRequest("/version/listAllModelNames");

        assertThat(restResponse.getResponse().size(), is(5));
        assertThat(restResponse.getResponse().get(0), is("model1"));
        assertThat(restResponse.isError(), is(false));
        assertThat(restResponse.getErrorCode(), nullValue());
        assertThat(restResponse.getMessage(), is("Done"));
    }

    @Test
    public void getAllModelNamesWhenThereIsAnException() throws Exception {
        when(mockVersionDelegate.getAllModelNames()).thenThrow(
                new SystemException(BusinessExceptionCodes.BSE000001, new Object[] {}));

        RestResponse<List<String>> restResponse = performGetRequest("/version/listAllModelNames");

        assertThat(restResponse.isError(), is(true));
        assertThat(restResponse.getErrorCode(), is("BSE000001"));
    }
    
    @Test
    public void getAllTidVersionNames() throws Exception {
        List<String> mappingNames = Arrays.asList("mapping1", "mapping2", "mapping3", "mapping4");
        when(mockVersionDelegate.getAllTidVersionNames("model1")).thenReturn(mappingNames);

        RestResponse<List<String>> restResponse = performGetRequest("/version/listAllTidVersionNames/model1");

        assertThat(restResponse.getResponse().size(), is(4));
        assertThat(restResponse.getResponse().get(0), is("mapping1"));
        assertThat(restResponse.isError(), is(false));
        assertThat(restResponse.getErrorCode(), nullValue());
        assertThat(restResponse.getMessage(), is("Done"));
    }
    
    @Test
    public void getAllTidVersionNamesWhenThereIsAnException() throws Exception {
        when(mockVersionDelegate.getAllTidVersionNames("model1")).thenThrow(
                new SystemException(BusinessExceptionCodes.BSE000001, new Object[] {}));

        RestResponse<List<String>> restResponse = performGetRequest("/version/listAllTidVersionNames/model1");

        assertThat(restResponse.isError(), is(true));
        assertThat(restResponse.getErrorCode(), is("BSE000001"));
    }
    

    @Test
    public void getTenantModelNames() throws Exception {
        List<String> tenantModelNames = Arrays.asList("tmn1", "tmn2", "tmn3", "tmn4");
        when(mockVersionDelegate.getAllTenantModelNames()).thenReturn(tenantModelNames);

        RestResponse<List<String>> restResponse = performGetRequest("/version/listAllTenantModelNames");

        assertThat(restResponse.getResponse().size(), is(4));
        assertThat(restResponse.getResponse().get(0), is("tmn1"));
        assertThat(restResponse.isError(), is(false));
        assertThat(restResponse.getErrorCode(), nullValue());
        assertThat(restResponse.getMessage(), is("Done"));
    }

    @Test
    public void getTenantModelNamesWhenThereIsAnException() throws Exception {
        when(mockVersionDelegate.getAllTenantModelNames()).thenThrow(
                new SystemException(BusinessExceptionCodes.BSE000001, new Object[] {}));

        RestResponse<List<String>> restResponse = performGetRequest("/version/listAllTenantModelNames");

        assertThat(restResponse.isError(), is(true));
        assertThat(restResponse.getErrorCode(), is("BSE000001"));
    }
    
    @Test
    public void getVersionSummary() throws Exception {
        VersionSummaryInfo versionSummary = new VersionSummaryInfo();
        versionSummary.setDescription("description");
        versionSummary.setMajorVersions(Arrays.asList(1, 2, 3));
        when(mockVersionDelegate.getVersionSummary("tmn1")).thenReturn(versionSummary);

        MvcResult mvcResult = mockMvc.perform(get("/version/versionSummary/tmn1").contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andReturn();
        MockHttpServletResponse mockResponse = mvcResult.getResponse();
        RestResponse<VersionSummaryInfo> restResponse = gson.fromJson(mockResponse.getContentAsString(),
                new TypeToken<RestResponse<VersionSummaryInfo>>() {
                }.getType());

        assertThat(restResponse.getResponse().getDescription(), is("description"));
        assertThat(restResponse.getResponse().getMajorVersions().size(), is(3));
        assertThat(restResponse.getResponse().getMajorVersions().get(0), is(1));
        assertThat(restResponse.getResponse().getMajorVersions().get(1), is(2));
        assertThat(restResponse.getResponse().getMajorVersions().get(2), is(3));
        assertThat(restResponse.isError(), is(false));
        assertThat(restResponse.getErrorCode(), nullValue());
        assertThat(restResponse.getMessage(), is("Done"));
    }

    @Test
    public void getVersionSummaryWhenThereIsAnException() throws Exception {
        when(mockVersionDelegate.getVersionSummary("tmn1")).thenThrow(
                new SystemException(BusinessExceptionCodes.BSE000001, new Object[] {}));

        RestResponse<List<String>> restResponse = performGetRequest("/version/versionSummary/tmn1");

        assertThat(restResponse.isError(), is(true));
        assertThat(restResponse.getErrorCode(), is("BSE000001"));
    }
    
    private RestResponse<List<String>> performGetRequest(String url) throws Exception, UnsupportedEncodingException {
        MvcResult mvcResult = mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON)).andDo(print()).andReturn();
        MockHttpServletResponse mockResponse = mvcResult.getResponse();
        RestResponse<List<String>> restResponse = gson.fromJson(mockResponse.getContentAsString(),
                new TypeToken<RestResponse<List<String>>>() {
                }.getType());
        return restResponse;
    }

    private void buildDummyLists() {
        versionHierarchyInfoList = new ArrayList<>();
        versionInfoList = new ArrayList<>();
        mappingInfoList = new ArrayList<>();
        tidMappingList = Arrays.asList("tmn1", "tmn2", "tmn3", "tmn4");
        VersionHierarchyInfo versionHierarchyInfo1 = new VersionHierarchyInfo();
        versionHierarchyInfo1.setName("VersionHierarchy1");
        VersionHierarchyInfo versionHierarchyInfo2 = new VersionHierarchyInfo();
        versionHierarchyInfo2.setName("VersionHierarchy2");
        versionHierarchyInfoList.add(versionHierarchyInfo1);
        versionHierarchyInfoList.add(versionHierarchyInfo2);
        versionInfo1 = new VersionInfo();
        VersionInfo versionInfo2 = new VersionInfo();
        versionInfo1.setId("test1");
        versionInfo1.setDescription("testing1");
        versionInfo2.setId("test2");
        versionInfo2.setDescription("testing2");
        versionInfoList.add(versionInfo1);
        versionInfoList.add(versionInfo2);
        MappingInfo mappingInfo1 = new MappingInfo();
        mappingInfo1.setId("test11");
        mappingInfo1.setName("test1Mapping");
        mappingInfo1.setModelName("model1");
        MappingInfo mappingInfo2 = new MappingInfo();
        mappingInfo2.setId("test12");
        mappingInfo2.setName("test2Mapping");
        mappingInfo2.setModelName("model2");
        mappingInfoList.add(mappingInfo1);
        mappingInfoList.add(mappingInfo2);
        createVersionInfo = new CreateVersionInfo();
        createVersionInfo.setCreatedBy("12-02-2014");
        createVersionInfo.setId("id1");
        createVersionInfo.setLastModifiedBy("System");
        createVersionInfo.setLibDescValue("libDesc1");
        createVersionInfo.setLibraryName("lib1");
        createVersionInfo.setLibraryRecord("record1");
        createVersionInfo.setMajorVersion(10);
        createVersionInfo.setModelName("model1");
        createVersionInfo.setTenantModelDescription("tenantModelDesc1");
        createVersionInfo.setTenantModelName("tenantModelName1");
        createVersionInfo.setTidDescription("tidDesc1");
        createVersionInfo.setTidName("tidName1");
        createVersionInfo.setVersionDescription("versionDesc1");
        createVersionInfo.setVersionType("versionType1");
        this.record = new PageRecord<>();
        record.setFirstPage(true);
        record.setLastPage(false);
        record.setNumber(1);
        record.setSize(5);
        record.setTotalPages(10);
        this.recordVersion = new PageRecord<>();
        recordVersion.setContent(versionInfoList);
        recordVersion.setFirstPage(true);
        recordVersion.setSize(2);
        recordVersion.setTotalPages(5);
        versionSummary1 = new VersionSummaryInfo();
        versionSummary1.setDescription("description");
        versionSummary1.setMajorVersions(Arrays.asList(1, 2, 3));

    }

    @Test
    public void testGetAllVersions() {
        try {
            when(mockVersionDelegate.getAllVersions()).thenReturn(versionHierarchyInfoList);
            MvcResult mvcResult = mockMvc.perform(get("/version/listAll").contentType(MediaType.APPLICATION_JSON)).andDo(print())
                    .andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertNotNull(mockResponse);
            String responseBody = mockResponse.getContentAsString();
            RestResponse<List<VersionHierarchyInfo>> restResponse = gson.fromJson(responseBody,
                    new TypeToken<RestResponse<List<VersionHierarchyInfo>>>() {
                    }.getType());
            assertThat(restResponse.isError(), is(false));
            assertThat(restResponse.getErrorCode(), nullValue());
            assertThat(restResponse.getMessage(), is("Done"));
            assertEquals(2, restResponse.getResponse().size());
            verify(mockVersionDelegate, times(1)).getAllVersions();
        } catch (Exception exp) {
            fail(exp.getMessage());
        }
    }

    @Test
    public void testGetAllVersionsWE() throws Exception {
        when(mockVersionDelegate.getAllVersions()).thenThrow(
                new SystemException(BusinessExceptionCodes.BSE000001, new Object[] {}));
        this.mockMvc.perform(get("/version/listAll", "REO Container").accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.error", is(Boolean.TRUE)))
                .andExpect(jsonPath("$.errorCode", is("BSE000001"))).andExpect(jsonPath("$.response", nullValue()));

    }

    
    @Test
    public void testGetlistAllLibraryRecNameDescs() {
        String libraryName = "test1Mapping";
        try {
            when(mockVersionDelegate.listAllLibraryRecNameDescs(libraryName)).thenReturn(mappingInfoList);
            MvcResult mvcResult = mockMvc
                    .perform(
                            get("/version/listAllLibraryRecNameDescs/{libraryName}", libraryName).contentType(
                                    MediaType.APPLICATION_JSON).param("libraryName", libraryName)).andDo(print()).andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            String responseBody = mockResponse.getContentAsString();
            RestResponse<List<MappingInfo>> restResponse = gson.fromJson(responseBody,
                    new TypeToken<RestResponse<List<MappingInfo>>>() {
                    }.getType());
            assertNotNull(restResponse);
            assertThat(restResponse.isError(), is(false));
            assertThat(restResponse.getErrorCode(), nullValue());
            assertThat(restResponse.getMessage(), is("Done"));
            verify(mockVersionDelegate, times(1)).listAllLibraryRecNameDescs(libraryName);
        } catch (BusinessException | SystemException e) {
            fail(e.getMessage());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetlistAllLibraryRecNameDescsWE() throws Exception {
        when(mockVersionDelegate.listAllLibraryRecNameDescs("test1Mapping")).thenThrow(
                new SystemException(BusinessExceptionCodes.BSE000001, new Object[] {}));
        this.mockMvc.perform(get("/version/listAllLibraryRecNameDescs/{libraryName}","test1Mapping").accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.error", is(Boolean.TRUE)))
                .andExpect(jsonPath("$.errorCode", is("BSE000001"))).andExpect(jsonPath("$.response", nullValue()));

    }

    @Test
    public void testGetTidMappings() {
        String modelName = "model1";
        try {
            when(mockVersionDelegate.getTidMappings(modelName)).thenReturn(mappingInfoList);
            MvcResult mvcResult = mockMvc
                    .perform(
                            get("/version/listAllTidVersions/{modelName}", modelName).contentType(MediaType.APPLICATION_JSON)
                                    .param("modelName", modelName)).andDo(print()).andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            String responseBody = mockResponse.getContentAsString();
            RestResponse<List<MappingInfo>> restResponse = gson.fromJson(responseBody,
                    new TypeToken<RestResponse<List<MappingInfo>>>() {
                    }.getType());
            assertNotNull(restResponse);
            assertThat(restResponse.isError(), is(false));
            assertThat(restResponse.getErrorCode(), nullValue());
            assertThat(restResponse.getMessage(), is("Done"));
            verify(mockVersionDelegate, times(1)).getTidMappings(modelName);
        } catch (BusinessException | SystemException e) {
            fail(e.getMessage());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetTidMappingsWE() throws Exception {
        when(mockVersionDelegate.getTidMappings("model1")).thenThrow(
                new SystemException(BusinessExceptionCodes.BSE000001, new Object[] {}));
        this.mockMvc.perform(get("/version/listAllTidVersions/{modelName}","model1").accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.error", is(Boolean.TRUE)))
                .andExpect(jsonPath("$.errorCode", is("BSE000001"))).andExpect(jsonPath("$.response", nullValue()));

    }
    
    @Test
    public void testCreate() {
        try {

            this.mockMvc
                    .perform(
                            post("/version/create").contentType(TestUtil.APPLICATION_JSON_UTF8).content(
                                    TestUtil.convertObjectToJsonBytes(this.createVersionInfo))).andExpect(status().isOk())
                    .andExpect(jsonPath("$.error", is(Boolean.FALSE))).andExpect(jsonPath("$.errorCode", nullValue()))
                    .andExpect(jsonPath("$.message", is("Done"))).andExpect(jsonPath("$.response", nullValue()));

        } catch (Exception exp) {
            fail(exp.getMessage());
        }
    }

    @Test
    public void testUpdate() {
        try {
            this.mockMvc
                    .perform(
                            post("/version/update").contentType(TestUtil.APPLICATION_JSON_UTF8).content(
                                    TestUtil.convertObjectToJsonBytes(this.createVersionInfo))).andExpect(status().isOk())
                    .andExpect(jsonPath("$.error", is(Boolean.FALSE))).andExpect(jsonPath("$.errorCode", nullValue()))
                    .andExpect(jsonPath("$.message", is("Done"))).andExpect(jsonPath("$.response", nullValue()));
        } catch (Exception exp) {
            fail(exp.getMessage());
        }
    }

    @Test
    public void test1GetTidMappingStatus() {
        try {
            KeyValuePair<Boolean, List<String>> result = new KeyValuePair<Boolean, List<String>>(true, tidMappingList);
            when(mockVersionDelegate.getTidMappingStatus("tidName1")).thenReturn(result);
            this.mockMvc
                    .perform(
                            post("/version/getTidMappingStatus").contentType(TestUtil.APPLICATION_JSON_UTF8).param("tidName",
                                    "tidName1")).andExpect(status().isOk()).andExpect(jsonPath("$.error", is(Boolean.FALSE)))
                    .andExpect(jsonPath("$.errorCode", nullValue()))
                    .andExpect(jsonPath("$.message", is("Umg Versions retrieved successfully")));
        } catch (Exception exp) {
            fail(exp.getMessage());
        }
    }

    @Test
    public void test2GetTidMappingStatus() {
        try {
            KeyValuePair<Boolean, List<String>> result = new KeyValuePair<Boolean, List<String>>(false, tidMappingList);
            when(mockVersionDelegate.getTidMappingStatus("tidName1")).thenReturn(result);
            this.mockMvc
                    .perform(
                            post("/version/getTidMappingStatus").contentType(TestUtil.APPLICATION_JSON_UTF8).param("tidName",
                                    "tidName1")).andExpect(status().isOk()).andExpect(jsonPath("$.error", is(Boolean.FALSE)))
                    .andExpect(jsonPath("$.errorCode", nullValue()))
                    .andExpect(jsonPath("$.message", is("No Umg Versions retrieved")));
        } catch (Exception exp) {
            fail(exp.getMessage());
        }
    }


    @Test
    public void testGetTidMappingStatusWE() throws Exception {
        when(mockVersionDelegate.getTidMappingStatus("tidName1")).thenThrow(
                new SystemException(BusinessExceptionCodes.BSE000001, new Object[] {}));
        this.mockMvc.perform(post("/version/getTidMappingStatus").accept(MediaType.APPLICATION_JSON).param("tidName","tidName1"))
                .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.error", is(Boolean.TRUE)))
                .andExpect(jsonPath("$.errorCode", is("BSE000001"))).andExpect(jsonPath("$.response", nullValue()));

    }
    
    @Test
    public void testGetPagedModelLIbraries() {
        try {
            this.mockMvc
                    .perform(
                            post("/version/listVersionedLibraries").contentType(TestUtil.APPLICATION_JSON_UTF8).content(
                                    TestUtil.convertObjectToJsonBytes(this.versionInfo1))).andExpect(status().isOk())
                    .andExpect(jsonPath("$.error", is(Boolean.FALSE))).andExpect(jsonPath("$.errorCode", nullValue()));
        } catch (Exception exp) {
            fail(exp.getMessage());
        }
    }
    
    @Test
    public void testGetModelsForLibraries() {
        try {
            when(mockVersionDelegate.getAllModelsForLibrary("Lib1", versionInfo1)).thenReturn(this.record);
            this.mockMvc
                    .perform(
                            post("/version/listVersionedModels/{libraryName}", "Lib1")
                                    .contentType(TestUtil.APPLICATION_JSON_UTF8).param("LIBRARY_NAME", "Lib1")
                                    .content(TestUtil.convertObjectToJsonBytes(this.versionInfo1))).andExpect(status().isOk())
                    .andExpect(jsonPath("$.error", is(Boolean.FALSE))).andExpect(jsonPath("$.errorCode", nullValue()));
        } catch (Exception exp) {
            fail(exp.getMessage());
        }
    }

    @Test
    public void testGetAllVersion() {
        try {
            when(mockVersionDelegate.getAllVersions("Lib1", "Model1", versionInfo1)).thenReturn(this.recordVersion);
            this.mockMvc
                    .perform(
                            post("/version/listAllVersions/{libraryName}/{modelName}", "Lib1", "Model1")
                                    .contentType(TestUtil.APPLICATION_JSON_UTF8).param("libraryName", "Lib1")
                                    .param("modelName", "Model1").content(TestUtil.convertObjectToJsonBytes(this.versionInfo1)))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.error", is(Boolean.FALSE)))
                    .andExpect(jsonPath("$.errorCode", nullValue()));
        } catch (Exception exp) {
            fail(exp.getMessage());
        }
    }
  
    @Test
    public void testGetNotDeletedVersions() {
        try {
            when(mockVersionDelegate.getNotDeletedVersions("tidName1")).thenReturn(tidMappingList);
            this.mockMvc
                    .perform(
                            post("/version/getNotDeletedVersions").contentType(TestUtil.APPLICATION_JSON_UTF8).param("tidName",
                                    "tidName1")).andExpect(status().isOk()).andExpect(jsonPath("$.error", is(Boolean.FALSE)))
                    .andExpect(jsonPath("$.errorCode", nullValue()))
                    .andExpect(jsonPath("$.message", is("Umg Versions retrieved successfully")));
        } catch (Exception exp) {
            fail(exp.getMessage());
        }
    }
    
    @Test
    public void testGetNotDeletedVersionsWE() throws Exception {
        when(mockVersionDelegate.getNotDeletedVersions("tidName1")).thenThrow(
                new SystemException(BusinessExceptionCodes.BSE000001, new Object[] {}));
        this.mockMvc.perform(post("/version/getNotDeletedVersions").accept(MediaType.APPLICATION_JSON).param("tidName", "tidName1"))
                .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.error", is(Boolean.TRUE)))
                .andExpect(jsonPath("$.errorCode", is("BSE000001"))).andExpect(jsonPath("$.response", nullValue()));

    }
    

    @Test
    public void testGetVersionsForModelLibrary() {
        try {
            when(mockVersionDelegate.getUmgVersionsOnModelLibraryId("id1")).thenReturn(tidMappingList);
            this.mockMvc
                    .perform(
                            post("/version/getVersionsForModelLibrary/{id}", "id1").contentType(TestUtil.APPLICATION_JSON_UTF8)
                                    .param("id", "id1")).andExpect(status().isOk())
                    .andExpect(jsonPath("$.error", is(Boolean.FALSE))).andExpect(jsonPath("$.errorCode", nullValue()))
                    .andExpect(jsonPath("$.message", is("Versions for Model Libraries Fetched")));
        } catch (Exception exp) {
            fail(exp.getMessage());
        }
    }

    @Test
    public void testGetVersionsForModelLibraryWE() throws Exception {
        when(mockVersionDelegate.getUmgVersionsOnModelLibraryId("id1")).thenThrow(
                new SystemException(BusinessExceptionCodes.BSE000001, new Object[] {}));
        this.mockMvc.perform(post("/version/getVersionsForModelLibrary/{id}", "id1").accept(MediaType.APPLICATION_JSON).param("id", "id1"))
                .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.error", is(Boolean.TRUE)))
                .andExpect(jsonPath("$.errorCode", is("BSE000001"))).andExpect(jsonPath("$.response", nullValue()));

    }
    
    
    @Test
    public void testGetAllTenantModelNames() {
        try {
            when(mockVersionDelegate.getAllTenantModelNames()).thenReturn(tidMappingList);
            this.mockMvc.perform(get("/version/listAllTenantModelNames").contentType(TestUtil.APPLICATION_JSON_UTF8))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.error", is(Boolean.FALSE)))
                    .andExpect(jsonPath("$.errorCode", nullValue())).andExpect(jsonPath("$.message", is("Done")));
        } catch (Exception exp) {
            fail(exp.getMessage());
        }
    }
    
    @Test
    public void testGetAllTenantModelNamesWE() throws Exception {
        when(mockVersionDelegate.getAllTenantModelNames()).thenThrow(
                new SystemException(BusinessExceptionCodes.BSE000001, new Object[] {}));
        this.mockMvc.perform(get("/version/listAllTenantModelNames").accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.error", is(Boolean.TRUE)))
                .andExpect(jsonPath("$.errorCode", is("BSE000001"))).andExpect(jsonPath("$.response", nullValue()));

    }

    @Test
    public void testGetVersionSummary() {
        try {
            when(mockVersionDelegate.getVersionSummary("tenantModelName1")).thenReturn(this.versionSummary1);
            this.mockMvc
                    .perform(
                            get("/version/versionSummary/{tenantModelName}", "tenantModelName1").contentType(
                                    TestUtil.APPLICATION_JSON_UTF8).param("tenantModelName", "tenantModelName1"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.error", is(Boolean.FALSE)))
                    .andExpect(jsonPath("$.errorCode", nullValue())).andExpect(jsonPath("$.message", is("Done")));
        } catch (Exception exp) {
            fail(exp.getMessage());
        }
    }

    @Test
    public void testGetVersionSummaryWE() throws Exception {
        when(mockVersionDelegate.getVersionSummary("tenantModelName1")).thenThrow(
                new SystemException(BusinessExceptionCodes.BSE000001, new Object[] {}));
        this.mockMvc.perform(get("/version/versionSummary/{tenantModelName}", "tenantModelName1").accept(MediaType.APPLICATION_JSON).param("tenantModelName", "tenantModelName1"))
                .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.error", is(Boolean.TRUE)))
                .andExpect(jsonPath("$.errorCode", is("BSE000001"))).andExpect(jsonPath("$.response", nullValue()));

    }
    
    @Test
    public void testGetModelNamesForLibraryNameAndCharsInNameOrDescription() {
        try {
            when(mockVersionDelegate.getModelNamesForLibraryNameAndCharsInNameOrDescription("Lib1", "str1", true)).thenReturn(
                    this.tidMappingList);
            this.mockMvc
                    .perform(
                            get("/version/listAllModelNames/{libraryName}/{isDescending}/{searchString}", "Lib1", true, "str1")
                                    .contentType(TestUtil.APPLICATION_JSON_UTF8)).andExpect(status().isOk())
                    .andExpect(jsonPath("$.error", is(Boolean.FALSE))).andExpect(jsonPath("$.errorCode", nullValue()))
                    .andExpect(jsonPath("$.message", is("Done")));
        } catch (Exception exp) {
            fail(exp.getMessage());
        }
    }
    
    @Test
    public void testGetModelNamesForLibraryNameAndCharsInNameOrDescriptionWE() throws Exception {
        when(mockVersionDelegate.getModelNamesForLibraryNameAndCharsInNameOrDescription("Lib1", "str1", true)).thenThrow(
                new SystemException(BusinessExceptionCodes.BSE000001, new Object[] {}));
        this.mockMvc.perform(get("/version/listAllModelNames/{libraryName}/{isDescending}/{searchString}", "Lib1", true, "str1")
        		.accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.error", is(Boolean.TRUE)))
                .andExpect(jsonPath("$.errorCode", is("BSE000001"))).andExpect(jsonPath("$.response", nullValue()));

    }

    @Test
    public void test1GetModelNamesForLibraryNameAndCharsInNameOrDescription() {
        try {
            when(mockVersionDelegate.getModelNamesForLibraryNameAndCharsInNameOrDescription("Lib1", "", true)).thenReturn(
                    this.tidMappingList);
            this.mockMvc
                    .perform(
                            get("/version/listAllModelNames/{libraryName}/{isDescending}", "Lib1", true)
                                    .contentType(TestUtil.APPLICATION_JSON_UTF8)).andExpect(status().isOk())
                    .andExpect(jsonPath("$.error", is(Boolean.FALSE))).andExpect(jsonPath("$.errorCode", nullValue()))
                    .andExpect(jsonPath("$.message", is("Done")));
        } catch (Exception exp) {
            fail(exp.getMessage());
        }
    }
    
    @Test
    public void test1GetModelNamesForLibraryNameAndCharsInNameOrDescriptionWE() throws Exception {
        when(mockVersionDelegate.getModelNamesForLibraryNameAndCharsInNameOrDescription("Lib1", "", true)).thenThrow(
                new SystemException(BusinessExceptionCodes.BSE000001, new Object[] {}));
        this.mockMvc.perform(get("/version/listAllModelNames/{libraryName}/{isDescending}", "Lib1", true)
        		.accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.error", is(Boolean.TRUE)))
                .andExpect(jsonPath("$.errorCode", is("BSE000001"))).andExpect(jsonPath("$.response", nullValue()));

    }
    
    @Test
    public void test1GetVersionStatus() {
        try {
            Boolean result = Boolean.TRUE;
            when(mockVersionDelegate.getVersionStatus("tidName5")).thenReturn(result);
            this.mockMvc
                    .perform(
                            post("/version/getVersionStatus").contentType(TestUtil.APPLICATION_JSON_UTF8).param("tidName",
                                    "tidName5")).andExpect(status().isOk()).andExpect(jsonPath("$.error", is(Boolean.FALSE)))
                    .andExpect(jsonPath("$.errorCode", nullValue()))
                    .andExpect(jsonPath("$.message", is("Umg Versions retrieved successfully")));
        } catch (Exception exp) {
            fail(exp.getMessage());
        }
    }
    
    @Test
    public void test2GetVersionStatus() {
        try {
            Boolean result = Boolean.FALSE;
            when(mockVersionDelegate.getVersionStatus("tidName6")).thenReturn(result);
            this.mockMvc
                    .perform(
                            post("/version/getVersionStatus").contentType(TestUtil.APPLICATION_JSON_UTF8).param("tidName",
                                    "tidName6")).andExpect(status().isOk()).andExpect(jsonPath("$.error", is(Boolean.FALSE)))
                    .andExpect(jsonPath("$.errorCode", nullValue()))
                    .andExpect(jsonPath("$.message", is("No Umg Versions retrieved")));
        } catch (Exception exp) {
            fail(exp.getMessage());
        }
    }


    @Test
    public void testGetVersionStatusWE() throws Exception {
        when(mockVersionDelegate.getVersionStatus("tidName7")).thenThrow(
                new SystemException(BusinessExceptionCodes.BSE000001, new Object[] {}));
        this.mockMvc.perform(post("/version/getVersionStatus").accept(MediaType.APPLICATION_JSON).param("tidName","tidName7"))
                .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.error", is(Boolean.TRUE)))
                .andExpect(jsonPath("$.errorCode", is("BSE000001"))).andExpect(jsonPath("$.response", nullValue()));

    }
    
    @Test
    public void testDeleteVersion() {
        String id = "1";
        try {
            doNothing().when(mockVersionDelegate).delete(any(String.class));
            MvcResult mvcResult = mockMvc
                    .perform(
                    		delete("/version/deleteVersion/{id}", id).contentType(MediaType.APPLICATION_JSON).param("id", id))
                    .andDo(print()).andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertEquals("application/json;charset=UTF-8", mockResponse.getContentType());
            String responseBody = mockResponse.getContentAsString();
            RestResponse<String> restResponse = gson.fromJson(responseBody, new TypeToken<RestResponse<String>>() {
            }.getType());
            assertNotNull(mockResponse);
            assertEquals("Done", restResponse.getMessage());
            //verify(mockVersionDelegate, times(1)).delete(id);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    
    
    
    @Test
    public void testFindAllVersionName() throws Exception {
    	ResponseWrapper<List<String>> response=new ResponseWrapper<List<String>>();
    	SearchOptions searchOptions=new SearchOptions();
		searchOptions.setPage(0);
		searchOptions.setPageSize(5);
		searchOptions.setSearchText("TEST");
		
		response.setPagingInfo(searchOptions);
		List<String> versionList=new ArrayList<String>();
		versionList.add("TNT Model 01");
		versionList.add("TNT Model 02");
		response.setResponse(versionList);

        when(mockVersionDelegate.findAllVersionName(Mockito.any(SearchOptions.class))).thenReturn(response);
        MvcResult mvcResult = mockMvc.perform(post("/version/listAllVersionName").contentType(MediaType.APPLICATION_JSON)
        		.content(gson.toJson(searchOptions))).andDo(print()).andReturn();
        
        MockHttpServletResponse mockResponse = mvcResult.getResponse();
        RestResponse<TransactionWrapper> restResponse = gson.fromJson(mockResponse.getContentAsString(),
                new TypeToken<RestResponse<ResponseWrapper<List<String>>>>() { }.getType());
        assertThat(mockResponse, notNullValue());
        assertThat(mockResponse.getContentType(), is("application/json"));
        assertThat(restResponse.isError(), is(false));
        assertThat(restResponse.getErrorCode(), nullValue());
    }
    
    
    @Test
    public void testFindAllversionByVersionName() throws Exception {
    	String versionName="verions 01";
    	
        PageRecord<VersionInfo> pageRecord = new PageRecord<VersionInfo>();
    	VersionInfo verInfo=new VersionInfo();
    	List<VersionInfo> verInfoList=new ArrayList<VersionInfo>();
        verInfo.setName("verions 01");

    	verInfoList.add(verInfo);
    	pageRecord.setContent(verInfoList);
    	
    	VersionInfo pageInfo=new VersionInfo();
    	pageInfo.setPage(0);
        pageInfo.setPageSize(5);

        SearchOptions searchOptions = new SearchOptions();
        searchOptions.setPage(0);
        searchOptions.setPageSize(5);
        searchOptions.setSearchText("TEST");
		
        when(mockVersionDelegate.findAllversionByVersionName(Mockito.any(String.class), Mockito.any(SearchOptions.class))).thenReturn(pageRecord);
        MvcResult mvcResult = mockMvc
                .perform(
                        post("/version/findAllVersion/{versionName:.+}", versionName).contentType(MediaType.APPLICATION_JSON)
                                .content(gson.toJson(pageInfo)).param("versionName", versionName)
                                .content(gson.toJson(searchOptions))).andDo(print()).andReturn();
        
        MockHttpServletResponse mockResponse = mvcResult.getResponse();
        RestResponse<PageRecord<VersionInfo>> restResponse = gson.fromJson(mockResponse.getContentAsString(),
                new TypeToken<RestResponse<PageRecord<VersionInfo>>>() { }.getType());
        assertThat(mockResponse, notNullValue());
        assertThat(mockResponse.getContentType(), is("application/json"));
        assertThat(restResponse.isError(), is(false));
        assertThat(restResponse.getErrorCode(), nullValue());
    }
    
    @Test
    public void testGetVersionMetrics() throws Exception {
    	VersionMetricRequestInfo req = new VersionMetricRequestInfo();
    	req.setVersionName("TestVersion");
    	req.setMajorVersion(1);
    	req.setMinorVersion(0);
    	Map response = new HashMap();
    	response.put("testBed", "sample");
    	response.put("api", "sample");
    	when(mockVersionDelegate.getVersionMetrics(Mockito.any(VersionMetricRequestInfo.class))).thenReturn(response);
    	MvcResult mvcResult = mockMvc.perform(post("/version/versionMetrics").contentType(TestUtil.APPLICATION_JSON_UTF8).
    			content(TestUtil.convertObjectToJsonBytes(req))).andDo(print()).andReturn();
    	MockHttpServletResponse mockResponse = mvcResult.getResponse();
    	assertThat(mockResponse, notNullValue());
        assertThat(mockResponse.getContentType(), is("application/json"));
        RestResponse<Map> restResponse = gson.fromJson(mockResponse.getContentAsString(), new TypeToken<RestResponse<Map>>(){}.getType());
        Map responseMap = restResponse.getResponse();
        assertTrue(responseMap.equals(response));
    }

}
