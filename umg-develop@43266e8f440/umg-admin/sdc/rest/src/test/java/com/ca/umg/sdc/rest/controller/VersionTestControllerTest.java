package com.ca.umg.sdc.rest.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;

import ma.glasnost.orika.impl.ConfigurableMapper;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.custom.mapper.UMGConfigurableMapper;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.mapping.delegate.MappingDelegate;
import com.ca.umg.business.mapping.info.TestBedOutputInfo;
import com.ca.umg.business.mapping.info.TidIoDefinition;
import com.ca.umg.business.mapping.info.VersionTestContainer;
import com.ca.umg.business.tenant.delegate.TenantDelegate;
import com.ca.umg.business.tenant.entity.TenantConfig;
import com.ca.umg.business.version.delegate.VersionDelegate;
import com.ca.umg.business.version.info.VersionAPIContainer;
import com.ca.umg.business.versiontest.delegate.VersionTestDelegate;
import com.ca.umg.sdc.rest.utils.RestResponse;
import com.ca.umg.sdc.rest.utils.TestUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class VersionTestControllerTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(VersionTestControllerTest.class);
	
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;
    private static final String TID_NAME = "tidName";
    private static final String TXN_ID = "1";
    @Inject
    private MappingDelegate mockMappingDelegate;
    @Inject
    private VersionTestController versionTestController;
    @Inject
    private TenantDelegate mockTenantDelegate;
    @Inject
    private VersionDelegate mockVersionDelegate;
    @Inject
    private VersionTestDelegate mockVersionTestDelegate;
    @Inject
    @Qualifier("cacheRegistry")
    private CacheRegistry cacheRegistry;
    @Spy
    ConfigurableMapper mapper = new UMGConfigurableMapper();
    private static final Gson gson = new GsonBuilder().create();
    private VersionTestContainer versionTestContainer;
    private List<TidIoDefinition> tidIoDefinitions;
    private TidIoDefinition tidIoDefinition;
    private TestBedOutputInfo testBedOutputInfo;
    private TenantConfig tenantConfig;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        this.mockMvc = MockMvcBuilders.standaloneSetup(versionTestController).build();
        createTidIoDefinitions();
        createVersionTestContainer();
        createTestBedOutputInfo();
        createTenantConfig();
    }

    public void createVersionTestContainer() {
        versionTestContainer = new VersionTestContainer();
        versionTestContainer.setAsOnDate("25/04/2014");
        versionTestContainer.setMajorVersion(1);
        versionTestContainer.setMinorVersion(0);
        versionTestContainer.setModelName("test");
        versionTestContainer.setTidIoDefinitions(tidIoDefinitions);
        versionTestContainer.setTidName("test");
        versionTestContainer.setVersionId("1.0");
    }

    public void createTidIoDefinitions() {
        tidIoDefinitions = new ArrayList<>();
        tidIoDefinition = new TidIoDefinition();
        tidIoDefinition.setArrayType(false);
        Map<String, Object> dataType = new HashMap<String, Object>();
        dataType.put("String", new Object());
        tidIoDefinition.setDatatype(dataType);
        tidIoDefinition.setDescription("takes only String");
        tidIoDefinition.setHtmlElement("text");
        tidIoDefinition.setValidationMethod("validate_string");
        tidIoDefinition.setName("city");
        tidIoDefinition.setValue("bangalore");
        tidIoDefinition.setMandatory(false);
    }

    public void createTestBedOutputInfo() {
        testBedOutputInfo = new TestBedOutputInfo();
        testBedOutputInfo.setError(false);
        testBedOutputInfo.setStatus("success");
        testBedOutputInfo.setTimeTaken(1000);
        testBedOutputInfo.setOutputJson("{key:value}");
    }

    public void createTenantConfig() {
        tenantConfig = new TenantConfig();
        tenantConfig.setValue("tenant");
    }

    @Test
    public void testLoadTestVersion() {
        String tidName = "test";
        try {
            when(mockMappingDelegate.getTidIoDefinitions(TID_NAME, true)).thenReturn(new ArrayList<TidIoDefinition>());
            MvcResult mvcResult = mockMvc
                    .perform(
                            get("/versiontest/loadTestVersion/{tidName}", tidName).contentType(MediaType.APPLICATION_JSON).param(
                                    "tidName", tidName)).andDo(print()).andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertNotNull(mockResponse);
            String responseBody = mockResponse.getContentAsString();
            RestResponse<List<String>> restResponse = gson.fromJson(responseBody,
                    new TypeToken<RestResponse<VersionTestContainer>>() {
                    }.getType());
            assertNotNull(restResponse);
            //verify(mockMappingDelegate, times(1)).getTidIoDefinitions(tidName, true);
        } catch (SystemException | BusinessException e) {
            e.printStackTrace();
        } catch (Exception e) {
        	LOGGER.error("Exception: ", e);
        }

    }

    @Test
    public void testFailLoadTestVersion() {
        String tidName = "test";
        try {
            BusinessException thrown = new BusinessException(BusinessExceptionCodes.BSE000046,
                    new Object[] { "Load test version fail" });
            boolean isTestbed = Mockito.anyBoolean();
            doThrow(thrown).when(mockMappingDelegate).getTidIoDefinitions(any(String.class), isTestbed);
            MvcResult mvcResult = mockMvc
                    .perform(
                            get("/versiontest/loadTestVersion/{tidName}", tidName).contentType(MediaType.APPLICATION_JSON).param(
                                    "tidName", tidName)).andDo(print()).andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertNotNull(mockResponse);
            String responseBody = mockResponse.getContentAsString();
            RestResponse<VersionTestContainer> restResponse = gson.fromJson(responseBody,
                    new TypeToken<RestResponse<VersionTestContainer>>() {
                    }.getType());
            assertNotNull(restResponse);
            verify(mockMappingDelegate, times(1)).getTidIoDefinitions(tidName, isTestbed);
            assertEquals(BusinessExceptionCodes.BSE000046, restResponse.getErrorCode());
        } catch (SystemException | BusinessException e) {
            e.printStackTrace();
        } catch (Exception e) {
        	LOGGER.error("Exception: ", e);
        }

    }

    @Test
    public void testMarkAsTested() {
        String tidName = "test";
        MvcResult mvcResult;
        try {
            mvcResult = mockMvc
                    .perform(
post("/versiontest/markastested/{tidName}", tidName)
                    .contentType(MediaType.APPLICATION_JSON).param("tidName",
                                    tidName)).andDo(print()).andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertNotNull(mockResponse);
        } catch (Exception e) {
        	LOGGER.error("Exception: ", e);
        }
    }

    @Ignore
    public void testExecuteVersion() {
        MvcResult mvcResult;
        String tenantUrl = "tenant";
        String runtimeJson = "{key:value}";
        try {
            when(mockMappingDelegate.createRuntimeInputJson(tidIoDefinitions, "test", 1, 0, "25/04/2014", Boolean.FALSE,Boolean.TRUE,Boolean.TRUE,Boolean.TRUE))
                    .thenReturn(runtimeJson);
            Properties properties = new Properties();
            properties.setProperty("TENANT_CODE", "TENANT_CODE");
            RequestContext rc = new RequestContext(properties);
            when(
                    mockTenantDelegate.getTenantConfig(rc.getTenantCode(), SystemConstants.SYSTEM_KEY_TENANT_URL,
                            SystemConstants.SYSTEM_KEY_TYPE_TENANT)).thenReturn(tenantConfig);
            when(mockVersionDelegate.versionTest(runtimeJson, tenantUrl, null, "1.0")).thenReturn(testBedOutputInfo);
            mvcResult = mockMvc
                    .perform(
post("/versiontest/executeVersion")
.contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(
                                    TestUtil.convertObjectToJsonBytes(versionTestContainer))).andDo(print()).andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertNotNull(mockResponse);
            String responseBody = mockResponse.getContentAsString();
            RestResponse<TestBedOutputInfo> restResponse = gson.fromJson(responseBody,
                    new TypeToken<RestResponse<TestBedOutputInfo>>() {
                    }.getType());
            assertNotNull(restResponse);
            verify(mockMappingDelegate, times(1)).createRuntimeInputJson(tidIoDefinitions, "test", 1, 0, "25/04/2014", Boolean.FALSE,Boolean.TRUE,Boolean.TRUE,Boolean.TRUE);
            verify(mockTenantDelegate, times(1)).getTenantConfig("TENANT_CODE", "URL", "TENANT");
            verify(mockVersionDelegate, times(1)).versionTest(runtimeJson, tenantUrl, null, "1.0");
        } catch (Exception e) {
        	LOGGER.error("Exception: ", e);
        }
    }

    @Ignore
    public void testFailExecuteVersion() {
        MvcResult mvcResult;
        String tenantUrl = "tenantUrl";
        String runtimeJson = "{key:value}";
        try {
            BusinessException thrown = new BusinessException(BusinessExceptionCodes.BSE000046,
                    new Object[] { "Execcute version fail" });
            doThrow(thrown).when(mockMappingDelegate).createRuntimeInputJson(tidIoDefinitions, "test", 1, 0, "25/04/2014", Boolean.FALSE,Boolean.TRUE,Boolean.TRUE,Boolean.TRUE);
            when(mockTenantDelegate.getTenantConfig("tenant", "tenant", "tenant")).thenReturn(tenantConfig);
            when(mockVersionDelegate.versionTest(runtimeJson, tenantUrl, null, "1.0")).thenReturn(testBedOutputInfo);
            mvcResult = mockMvc
                    .perform(
                            post("/versiontest/executeVersion").contentType(TestUtil.APPLICATION_JSON_UTF8).content(
                                    TestUtil.convertObjectToJsonBytes(versionTestContainer))).andDo(print()).andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertNotNull(mockResponse);
            String responseBody = mockResponse.getContentAsString();
            RestResponse<TestBedOutputInfo> restResponse = gson.fromJson(responseBody,
                    new TypeToken<RestResponse<TestBedOutputInfo>>() {
                    }.getType());
            assertNotNull(restResponse);
            verify(mockMappingDelegate, times(1)).createRuntimeInputJson(tidIoDefinitions, "test", 1, 0, "25/04/2014", Boolean.FALSE, Boolean.TRUE,Boolean.TRUE,Boolean.TRUE);
            assertEquals(BusinessExceptionCodes.BSE000046, restResponse.getErrorCode());
        } catch (Exception e) {
        	LOGGER.error("Exception: ", e);
        }
    }

    @Test
    public void testPopulateTenantInputDatatoTestBed() {

        try {
            when(mockVersionTestDelegate.getVersionTestContainer(TXN_ID)).thenReturn(versionTestContainer);
            MvcResult mvcResult = mockMvc
                    .perform(
                            get("/versiontest/loadTestBed/{txnId}", TXN_ID).contentType(MediaType.APPLICATION_JSON).param(
                                    "txnId", TXN_ID)).andDo(print()).andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertNotNull(mockResponse);
            String responseBody = mockResponse.getContentAsString();
            RestResponse<List<String>> restResponse = gson.fromJson(responseBody,
                    new TypeToken<RestResponse<VersionTestContainer>>() {
                    }.getType());
            assertNotNull(restResponse);
            verify(mockVersionTestDelegate, times(1)).getVersionTestContainer(TXN_ID);
        } catch (SystemException | BusinessException e) {
            e.printStackTrace();
        } catch (Exception e) {
        	LOGGER.error("Exception: ", e);
        }

    }

    @Test
    public void testFailPopulateTenantInputDatatoTestBed() {

        try {
            BusinessException thrown = new BusinessException(BusinessExceptionCodes.BSE000087,
                    new Object[] { "Version Not found" });
            doThrow(thrown).when(mockVersionTestDelegate.getVersionTestContainer(TXN_ID));
            MvcResult mvcResult = mockMvc
                    .perform(
                            get("/versiontest/loadTestBed/{txnId}", TXN_ID).contentType(MediaType.APPLICATION_JSON).param(
                                    "txnId", TXN_ID)).andDo(print()).andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertNotNull(mockResponse);
            String responseBody = mockResponse.getContentAsString();
            RestResponse<VersionTestContainer> restResponse = gson.fromJson(responseBody,
                    new TypeToken<RestResponse<VersionTestContainer>>() {
                    }.getType());
            assertNotNull(restResponse);
            verify(mockVersionTestDelegate, times(1)).getVersionTestContainer(TXN_ID);
            assertEquals(BusinessExceptionCodes.BSE000087, restResponse.getErrorCode());
        } catch (BusinessException e) {
            e.printStackTrace();
        } catch (Exception e) {
        	LOGGER.error("Exception: ", e);
        }
    }

    @Test
    public void testDownloadVersionAPI() {

        try {
            when(mockVersionTestDelegate.getVersionAPI(TXN_ID)).thenReturn(getVersionAPIContainer());
            MvcResult mvcResult = mockMvc
.perform(get("/versiontest/downloadAPI/{versionId}", TXN_ID)
                    .contentType(MediaType.APPLICATION_JSON_VALUE).param(
                                    "txnId", TXN_ID)).andDo(print()).andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertNotNull(mockResponse);
            String responseBody = mockResponse.getContentAsString();
            RestResponse<List<String>> restResponse = gson.fromJson(responseBody,
                    new TypeToken<RestResponse<VersionTestContainer>>() {
                    }.getType());
            assertNotNull(restResponse);
            verify(mockVersionTestDelegate, times(1)).getVersionTestContainer(TXN_ID);
        } catch (SystemException | BusinessException e) {
            e.printStackTrace();
        } catch (Exception e) {
        	LOGGER.error("Exception: ", e);
        }

    }

    private VersionAPIContainer getVersionAPIContainer() {

        VersionAPIContainer versionAPIContainer = new VersionAPIContainer();
        versionAPIContainer.setTenantInputSchemaName("testVersionInput");
        versionAPIContainer.setTenantOutputSchemaName("testVersionOutput");
        versionAPIContainer.setTenantInputSchema("testVersionInput".getBytes());
        versionAPIContainer.setTenantOutputSchema("testVersionOutput".getBytes());
        versionAPIContainer.setSampleTenantInputJson("testVersionSampleJson".getBytes());

        return versionAPIContainer;

    }

}
