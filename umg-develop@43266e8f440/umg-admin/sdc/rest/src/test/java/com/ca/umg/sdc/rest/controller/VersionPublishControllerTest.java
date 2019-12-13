package com.ca.umg.sdc.rest.controller;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.ca.framework.core.bo.ModelType;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.business.common.info.SearchOptions;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.execution.delegate.ModelExecutionEnvironmentDelegate;
import com.ca.umg.business.model.delegate.MediateModelLibraryDelegate;
import com.ca.umg.business.transaction.delegate.TransactionDelegate;
import com.ca.umg.business.version.command.master.CommandMaster;
import com.ca.umg.business.version.delegate.VersionDelegate;
import com.ca.umg.business.version.info.VersionInfo;
import com.ca.umg.sdc.rest.utils.RestResponse;
import com.ca.umg.sdc.rest.utils.TestUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)


// TODO fix ignored test cases
public class VersionPublishControllerTest {

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;
    private static final String TID_NAME = "tidName";
    private static final String TXN_ID = "1";

    @Inject
    private VersionPublishController versionPublishController;

    @Inject
    private VersionDelegate mockVersionDelegate;

    @Inject
    private TransactionDelegate mockTransactionDelegate;

    @Inject
    private ModelExecutionEnvironmentDelegate mockModelExecEnvironmentDelegate;

    @Inject
    private MediateModelLibraryDelegate mockMediateModelLibraryDelegate;

    @Inject
    @Named("versionCommandMaster")
    private CommandMaster mockCommandMaster;

    private static final Gson gson = new GsonBuilder().create();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        this.mockMvc = MockMvcBuilders.standaloneSetup(versionPublishController).build();

    }

    @Test
    public void testGetVersionNames() throws Exception {
        when(mockVersionDelegate.getAllVersionNames()).thenReturn(getVersionNames());
        MvcResult mvcResult = mockMvc
                .perform(get("/publishVersion/listVersionNames").contentType(MediaType.APPLICATION_JSON_VALUE)).andDo(print())
                .andReturn();
        MockHttpServletResponse mockResponse = mvcResult.getResponse();
        assertNotNull(mockResponse);
        String responseBody = mockResponse.getContentAsString();
        RestResponse<List<String>> restResponse = gson.fromJson(responseBody, new TypeToken<RestResponse<List<String>>>() {
        }.getType());
        assertNotNull(restResponse);
        verify(mockVersionDelegate, times(1)).getAllVersionNames();
    }

    @Test
    public void testGetVersionNamesFail() throws Exception {

        when(mockVersionDelegate.getAllVersionNames())
                .thenThrow(new BusinessException(BusinessExceptionCodes.BSE000068, new Object[] {}));
        MvcResult mvcResult = mockMvc
                .perform(get("/publishVersion/listVersionNames").contentType(MediaType.APPLICATION_JSON_VALUE)).andDo(print())
                .andReturn();

        MockHttpServletResponse mockResponse = mvcResult.getResponse();
        assertNotNull(mockResponse);
        String responseBody = mockResponse.getContentAsString();
        RestResponse<List<String>> restResponse = gson.fromJson(responseBody, new TypeToken<RestResponse<List<String>>>() {
        }.getType());
        assertNotNull(restResponse.getErrorCode());

    }

    @Test
    public void testGetVersionDescription() throws Exception {

        when(mockVersionDelegate.getVersionDescription("version1")).thenReturn("version1 Description");
        MvcResult mvcResult = mockMvc.perform(
                get("/publishVersion/getVersionDescription/{name}", "version1").contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print()).andReturn();
        MockHttpServletResponse mockResponse = mvcResult.getResponse();
        assertNotNull(mockResponse);
        String responseBody = mockResponse.getContentAsString();
        RestResponse<String> restResponse = gson.fromJson(responseBody, new TypeToken<RestResponse<String>>() {
        }.getType());
        assertNotNull(restResponse);
        verify(mockVersionDelegate, times(1)).getVersionDescription("version1");

    }

    @Test
    public void testGetVersionDescriptionFail() throws Exception {

        when(mockVersionDelegate.getVersionDescription("version1"))
                .thenThrow(new BusinessException(BusinessExceptionCodes.BSE000068, new Object[] {}));
        MvcResult mvcResult = mockMvc.perform(
                get("/publishVersion/getVersionDescription/{name}", "version1").contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print()).andReturn();

        MockHttpServletResponse mockResponse = mvcResult.getResponse();
        assertNotNull(mockResponse);
        String responseBody = mockResponse.getContentAsString();
        RestResponse<String> restResponse = gson.fromJson(responseBody, new TypeToken<RestResponse<String>>() {
        }.getType());
        assertNotNull(restResponse.getErrorCode());

    }

    @Test
    public void testGetMajorVersions() throws Exception {
        List<Integer> majorVersions = new ArrayList<>();
        majorVersions.add(1);
        majorVersions.add(2);
        when(mockVersionDelegate.getMajorVersions("version1")).thenReturn(majorVersions);
        MvcResult mvcResult = mockMvc
                .perform(get("/publishVersion/getMajorVersions/{name}", "version1").contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print()).andReturn();
        MockHttpServletResponse mockResponse = mvcResult.getResponse();
        assertNotNull(mockResponse);
        String responseBody = mockResponse.getContentAsString();
        RestResponse<List<String>> restResponse = gson.fromJson(responseBody, new TypeToken<RestResponse<List<String>>>() {
        }.getType());
        assertNotNull(restResponse);
        verify(mockVersionDelegate, times(1)).getMajorVersions("version1");
    }

    @Test
    public void testGetMajorVersionsFail() throws Exception {

        when(mockVersionDelegate.getMajorVersions("version1"))
                .thenThrow(new BusinessException(BusinessExceptionCodes.BSE000068, new Object[] {}));
        MvcResult mvcResult = mockMvc
                .perform(get("/publishVersion/getMajorVersions/{name}", "version1").contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print()).andReturn();

        MockHttpServletResponse mockResponse = mvcResult.getResponse();
        assertNotNull(mockResponse);
        String responseBody = mockResponse.getContentAsString();
        RestResponse<List<String>> restResponse = gson.fromJson(responseBody, new TypeToken<RestResponse<List<String>>>() {
        }.getType());
        assertNotNull(restResponse.getErrorCode());

    }

    @Test
    public void testGetExistingRecords() throws Exception {
        List<VersionInfo> majorVersions = new ArrayList<>();
        majorVersions.add(new VersionInfo());
        majorVersions.add(new VersionInfo());
        when(mockVersionDelegate.getVersionDetails("R-3.2.1", ModelType.ONLINE, Boolean.FALSE)).thenReturn(majorVersions);
        MvcResult mvcResult = mockMvc.perform(get("/publishVersion/listLibraryDetails/{executionLanguage:.+}/{modelType}", "R-3.2.1" , "Online")
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andDo(print()).andReturn();
        MockHttpServletResponse mockResponse = mvcResult.getResponse();
        assertNotNull(mockResponse);
        String responseBody = mockResponse.getContentAsString();
        RestResponse<List<VersionInfo>> restResponse = gson.fromJson(responseBody,
                new TypeToken<RestResponse<List<VersionInfo>>>() {
                }.getType());
        assertNotNull(restResponse);
        verify(mockVersionDelegate, times(1)).getVersionDetails("R-3.2.1", ModelType.ONLINE, Boolean.FALSE);
    }

    @Test
    public void testGetExistingRecordsFail() throws Exception {

        when(mockVersionDelegate.getVersionDetails("R-3.2.1", ModelType.ONLINE, Boolean.FALSE))
                .thenThrow(new BusinessException(BusinessExceptionCodes.BSE000068, new Object[] {}));
        MvcResult mvcResult = mockMvc.perform(get("/publishVersion/listLibraryDetails/{executionLanguage:.+}/{modelType}", "R-3.2.1" , "Online")
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andDo(print()).andReturn();

        MockHttpServletResponse mockResponse = mvcResult.getResponse();
        assertNotNull(mockResponse);
        String responseBody = mockResponse.getContentAsString();
        RestResponse<List<String>> restResponse = gson.fromJson(responseBody, new TypeToken<RestResponse<List<String>>>() {
        }.getType());
        assertNotNull(restResponse.getErrorCode());

    }

    
    @Ignore
    public void testGetVersion() throws Exception {
        VersionInfo versionInfo = new VersionInfo();
        versionInfo.setId("1");

        when(mockVersionDelegate.getVersionById("1")).thenReturn(versionInfo);
        MvcResult mvcResult = mockMvc
                .perform(get("/publishVersion/getModelApi/{versionId}", "1").contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print()).andReturn();
        MockHttpServletResponse mockResponse = mvcResult.getResponse();
        assertNotNull(mockResponse);
        String responseBody = mockResponse.getContentAsString();
        RestResponse<VersionInfo> restResponse = gson.fromJson(responseBody, new TypeToken<RestResponse<VersionInfo>>() {
        }.getType());
        assertNotNull(restResponse);
        verify(mockVersionDelegate, times(1)).getVersionById("1");

    }

    @Test
    public void testGetVersionFail() throws Exception {
        when(mockVersionDelegate.getVersionById("1"))
                .thenThrow(new BusinessException(BusinessExceptionCodes.BSE000068, new Object[] {}));
        MvcResult mvcResult = mockMvc
                .perform(get("/publishVersion/getModelApi/{versionId}", "1").contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print()).andReturn();

        MockHttpServletResponse mockResponse = mvcResult.getResponse();
        assertNotNull(mockResponse);
        String responseBody = mockResponse.getContentAsString();
        RestResponse<String> restResponse = gson.fromJson(responseBody, new TypeToken<RestResponse<String>>() {
        }.getType());
        assertNotNull(restResponse.getErrorCode());

    }

    @Test
    public void testSearchLibrary() throws Exception {
        List<VersionInfo> majorVersions = new ArrayList<>();
        majorVersions.add(new VersionInfo());
        majorVersions.add(new VersionInfo());
        String language = "R";
        String rVersion = "R-3.2.1";
        SearchOptions serachOptions = new SearchOptions();
        when(mockVersionDelegate.searchLibrary(serachOptions, language)).thenReturn(majorVersions);
        MvcResult mvcResult = mockMvc
                .perform(post("/publishVersion/searchLibrary/{executionLanguage:.+}", rVersion)
                        .contentType(MediaType.APPLICATION_JSON_VALUE).content(TestUtil.convertObjectToJsonBytes(serachOptions)))
                .andDo(print()).andReturn();
        MockHttpServletResponse mockResponse = mvcResult.getResponse();
        assertNotNull(mockResponse);
        String responseBody = mockResponse.getContentAsString();
        RestResponse<List<VersionInfo>> restResponse = gson.fromJson(responseBody,
                new TypeToken<RestResponse<List<VersionInfo>>>() {
                }.getType());
        assertNotNull(restResponse);

    }

    @Test
    public void testSearchIoDefn() throws Exception {
        List<VersionInfo> majorVersions = new ArrayList<>();
        majorVersions.add(new VersionInfo());
        majorVersions.add(new VersionInfo());
        String language = "R";
        String rVersion = "R-3.2.1";
        SearchOptions serachOptions = new SearchOptions();
        when(mockVersionDelegate.searchLibrary(serachOptions, language)).thenReturn(majorVersions);
        MvcResult mvcResult = mockMvc
                .perform(post("/publishVersion/searchIoDefn/{executionLanguage:.+}/{modelType}", rVersion , "Online")
                        .contentType(MediaType.APPLICATION_JSON_VALUE).content(TestUtil.convertObjectToJsonBytes(serachOptions)))
                .andDo(print()).andReturn();
        MockHttpServletResponse mockResponse = mvcResult.getResponse();
        assertNotNull(mockResponse);
        String responseBody = mockResponse.getContentAsString();
        RestResponse<List<VersionInfo>> restResponse = gson.fromJson(responseBody,
                new TypeToken<RestResponse<List<VersionInfo>>>() {
                }.getType());
        assertNotNull(restResponse);

    }

    @Test
    public void testSearchNewLibrary() throws Exception {

        Properties properties = new Properties();
        properties.put(RequestContext.TENANT_CODE, "localhost");
        new RequestContext(properties);
        List<VersionInfo> majorVersions = new ArrayList<>();
        majorVersions.add(new VersionInfo());
        majorVersions.add(new VersionInfo());
        String language = "R";
        String rVersion = "R-3.2.1";
        SearchOptions serachOptions = new SearchOptions();
        when(mockVersionDelegate.searchLibrary(serachOptions, language)).thenReturn(majorVersions);
        MvcResult mvcResult = mockMvc
                .perform(post("/publishVersion/searchNewLibrary/{executionLanguage:.+}", rVersion)
                        .contentType(MediaType.APPLICATION_JSON_VALUE).content(TestUtil.convertObjectToJsonBytes(serachOptions)))
                .andDo(print()).andReturn();
        MockHttpServletResponse mockResponse = mvcResult.getResponse();
        assertNotNull(mockResponse);
        String responseBody = mockResponse.getContentAsString();
        RestResponse<List<VersionInfo>> restResponse = gson.fromJson(responseBody,
                new TypeToken<RestResponse<List<VersionInfo>>>() {
                }.getType());
        assertNotNull(restResponse);

    }

    /*
     * @Test public void testSearchNewLibraryFail() throws Exception { Properties properties = new Properties();
     * properties.put(RequestContext.TENANT_CODE, "localhost"); new RequestContext(properties); List<VersionInfo> majorVersions =
     * new ArrayList<>(); majorVersions.add(new VersionInfo()); majorVersions.add(new VersionInfo()); String language = "R";
     * String rVersion = "R-3.2.1"; SearchOptions serachOptions = new SearchOptions();
     * when(mockVersionDelegate.searchLibrary(Mockito.any(SearchOptions.class), language)) .thenThrow(new
     * BusinessException(BusinessExceptionCodes.BSE000068, new Object[] {})); MvcResult mvcResult = mockMvc
     * .perform(post("/publishVersion/searchNewLibrary/{executionLanguage:.+}", rVersion)
     * .contentType(MediaType.APPLICATION_JSON_VALUE).content(Mockito.any(byte[].class))) .andDo(print()).andReturn();
     * MockHttpServletResponse mockResponse = mvcResult.getResponse(); assertNotNull(mockResponse); String responseBody =
     * mockResponse.getContentAsString(); RestResponse<List<VersionInfo>> restResponse = gson.fromJson(responseBody, new
     * TypeToken<RestResponse<List<VersionInfo>>>() { }.getType()); assertNotNull(restResponse);
     * 
     * }
     */
   /* @Test
    public void testSearchLibraryFail() throws Exception {

        SearchOptions serachOptions = new SearchOptions();
        String language = "R";
        String rVersion = "R-3.2.1";

        MvcResult mvcResult = mockMvc
                .perform(post("/publishVersion/searchLibrary/{executionLanguage:.+}", rVersion)
                        .contentType(MediaType.APPLICATION_JSON_VALUE).content(TestUtil.convertObjectToJsonBytes(serachOptions)))
                .andDo(print()).andReturn();

        MockHttpServletResponse mockResponse = mvcResult.getResponse();
        assertNotNull(mockResponse);
        String responseBody = mockResponse.getContentAsString();
        RestResponse<List<String>> restResponse = gson.fromJson(responseBody, new TypeToken<RestResponse<List<String>>>() {
        }.getType());
        assertNotNull(restResponse.getResponse().size(), 0);

    }
*/


    private List<String> getVersionNames() {
        List<String> versionNames = new ArrayList();
        versionNames.add("version1");
        versionNames.add("version2");
        versionNames.add("version3");

        return versionNames;
    }

}
