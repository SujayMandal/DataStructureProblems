package com.ca.umg.sdc.rest.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
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

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.custom.mapper.UMGConfigurableMapper;
import com.ca.framework.core.info.tenant.AddressInfo;
import com.ca.framework.core.info.tenant.TenantInfo;
import com.ca.umg.business.integration.info.RuntimeResponse;
import com.ca.umg.business.integration.runtime.RuntimeIntegrationClient;
import com.ca.umg.business.tenant.delegate.TenantDelegate;
import com.ca.umg.business.tenant.entity.Address;
import com.ca.umg.business.tenant.entity.Tenant;
import com.ca.umg.sdc.rest.utils.RestResponse;
import com.ca.umg.sdc.rest.utils.TestUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import ma.glasnost.orika.impl.ConfigurableMapper;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)


// TODO fix ignored test cases
public class TenantControllerTest extends BaseTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Inject
    private TenantController tenantController;

    @Inject
    private TenantDelegate tenantDelegate;
    
    @Inject
    private RuntimeIntegrationClient runtimeIntegrationClient;
    
    @Mock
    private RuntimeResponse runtimeResponse;
    
    @Autowired
    private CacheRegistry cacheRegistry;
    
    @Spy
    ConfigurableMapper mapper = new UMGConfigurableMapper();

    private VersionControllerHelper versionControllerHelper = null;


    private static final Gson gson = new GsonBuilder().create();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        this.mockMvc = MockMvcBuilders.standaloneSetup(tenantController).build();

    }

    @Test
    public final void testListAll() {
        try {
            when(tenantDelegate.listAll()).thenReturn(createTenantInfos());
            MvcResult mvcResult = mockMvc.perform(get("/tenant/listAll")).andDo(print()).andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertNotNull(mockResponse);
            assertEquals("application/json;charset=UTF-8", mockResponse.getContentType());
            String responseBody = mockResponse.getContentAsString();
            RestResponse<List<TenantInfo>> restResponse = gson.fromJson(responseBody,
                    new TypeToken<RestResponse<List<TenantInfo>>>() {
                    }.getType());
            assertEquals(1, restResponse.getResponse().size());
            assertEquals("Done", restResponse.getMessage());

            verify(tenantDelegate, times(1)).listAll();
        } catch (Exception exp) {
            fail(exp.getMessage());
        }
    }

    @Test
    public final void testCreate() {
        try {
            TenantInfo tenantInfo = createTenantInfo();
            when(tenantDelegate.create(Mockito.any(TenantInfo.class), Mockito.anyBoolean())).thenReturn(tenantInfo);

            MvcResult mvcResult = mockMvc
                    .perform(post("/tenant/create/").contentType(TestUtil.APPLICATION_JSON_UTF8).content(gson.toJson(tenantInfo)))
                    .andDo(print()).andReturn();

            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertNotNull(mockResponse);           
            String responseBody = mockResponse.getContentAsString();
            RestResponse<TenantInfo> restResponse = gson.fromJson(responseBody,
                    new TypeToken<RestResponse<TenantInfo>>() {
                    }.getType());

            assertNotNull(restResponse);

        } catch (Exception exp) {
            fail(exp.getMessage());
        }
    }

    @Test
    public final void testGetTenantDetails() {
        try {
            when(tenantDelegate.getTenantWithAllSystemKeys()).thenReturn(createTenantInfo());

            MvcResult mvcResult = mockMvc.perform(get("/tenant/tenantDetails")).andDo(print()).andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertNotNull(mockResponse);
            assertEquals("application/json;charset=UTF-8", mockResponse.getContentType());
            String responseBody = mockResponse.getContentAsString();
            RestResponse<TenantInfo> restResponse = gson.fromJson(responseBody, new TypeToken<RestResponse<TenantInfo>>() {
            }.getType());
            assertEquals("localhost", restResponse.getResponse().getCode());
            verify(tenantDelegate, times(1)).getTenantWithAllSystemKeys();
        } catch (Exception exp) {
            fail(exp.getMessage());
        }
    }

    @Test
    public final void testUpdate() {
        try {
            Tenant existingTenant = createTenant();
            TenantInfo tenantInfo = createTenantInfo();
            System.out.println(tenantInfo.hashCode());
            when(tenantDelegate.getTenant("localhost")).thenReturn(tenantInfo);
            when(tenantDelegate.update(Mockito.any(TenantInfo.class))).thenReturn(tenantInfo);

            MvcResult mvcResult = mockMvc
                    .perform(post("/tenant/update/").contentType(MediaType.APPLICATION_JSON).content(gson.toJson(tenantInfo)))
                    .andDo(print()).andReturn();

            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertNotNull(mockResponse);
            assertEquals("application/json", mockResponse.getContentType());
            String responseBody = mockResponse.getContentAsString();
            RestResponse<TenantInfo> restResponse = gson.fromJson(responseBody, new TypeToken<RestResponse<TenantInfo>>() {
            }.getType());
            assertEquals("Tenant localhost updated successfully.", restResponse.getMessage());
            assertNotNull(restResponse.getResponse());

        } catch (Exception exp) {
            fail(exp.getMessage());
        }
    }


    @Test
    @Ignore
    public final void testBatchDeploy() {
        try {
            // versionControllerHelper = PowerMockito.mock(VersionControllerHelper.class);
            when(versionControllerHelper.getTenantBaseUrl(tenantDelegate)).thenReturn("http://localhost:8081");
            when(runtimeIntegrationClient.deployBatch("http://localhost:8081", "/tenant/batchDeploy", null, null))
                    .thenReturn(runtimeResponse);
            MvcResult mvcResult = mockMvc.perform(get("/tenant/batchDeploy")).andDo(print()).andReturn();
            assertNotNull(mvcResult);

        } catch (Exception exp) {
            fail(exp.getMessage());
        }
    }

    private List<TenantInfo> createTenantInfos() {
        List<TenantInfo> tenantInfoList = new ArrayList<TenantInfo>();
        TenantInfo tenantInfo = createTenantInfo();

        tenantInfoList.add(tenantInfo);

        return tenantInfoList;

    }

    private TenantInfo createTenantInfo() {
        TenantInfo tenantInfo = new TenantInfo();
        tenantInfo.setCode("localhost");
        Set<AddressInfo> addressInfos = new HashSet<AddressInfo>();
        AddressInfo addressInfo = new AddressInfo();
        addressInfo.setAddress1("address1");
        addressInfo.setAddress2("address2");
        addressInfo.setCity("city");
        addressInfo.setState("state");
        tenantInfo.setAddresses(addressInfos);
        return tenantInfo;

    }

    private Tenant createTenant() {
        Tenant tenant = new Tenant();
        tenant.setCode("localhost");

        Set<Address> addresses = new HashSet<Address>();
        Address address = new Address();
        address.setAddress1("address1");
        address.setAddress2("address2");
        address.setCity("city");
        address.setState("state");
        tenant.setAddresses(addresses);
        return tenant;

    }


}
