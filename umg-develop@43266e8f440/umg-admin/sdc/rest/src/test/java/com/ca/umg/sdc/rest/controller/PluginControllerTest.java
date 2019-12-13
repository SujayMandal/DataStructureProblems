/**
 * 
 */
package com.ca.umg.sdc.rest.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.ca.umg.business.model.delegate.MediateModelLibraryDelegate;
import com.ca.umg.business.modelexecenvs.ModelExecEnvironmentProvider;
import com.ca.umg.business.plugin.delegate.PluginDelegate;
import com.ca.umg.sdc.rest.utils.RestResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * @author raddibas
 *
 */

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class PluginControllerTest {

	 @Autowired
	 private WebApplicationContext wac;

	 private MockMvc mockMvc;
	 
	 @Inject
	 PluginController pluginController;
	 
	 @Inject
	 PluginDelegate pluginDelegate;
	 
	 @Inject
	 MediateModelLibraryDelegate mediateModelLibraryDelegate;
	 
	 @Inject
	 ModelExecEnvironmentProvider modelExecEnvironmentProvider;
	 
	 private static final Gson gson = new GsonBuilder().create();
	    
	 @Before
	    public void setup() {
	        MockitoAnnotations.initMocks(this);
	        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	        this.mockMvc = MockMvcBuilders.standaloneSetup(pluginController).build();
	    }
	 
	 @Test
	 public void testGetPluginsForTenant () {
		 try {
			when(pluginDelegate.getPluginsMappedForTenant()).thenReturn(createPluginsMappedForTenant());
			MvcResult mvcResult = mockMvc.perform(post("/plugin/getPlugins")).andDo(print()).andReturn();
			MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertNotNull(mockResponse);
            String responseBody = mockResponse.getContentAsString();
            RestResponse<Map<String, Boolean>> restResponse = gson.fromJson(responseBody,
                    new TypeToken<RestResponse<Map<String, Boolean>>>() {
                    }.getType());
            assertEquals(2, restResponse.getResponse().size());
            assertEquals("Done", restResponse.getMessage());
            assertEquals(true, restResponse.getResponse().get("EXCEL"));
            assertEquals(false, restResponse.getResponse().get("PDF"));
		} catch (Exception exp) {
			 fail(exp.getMessage());
		}
		 
		 
	 }
	 
	 private Map<String, Boolean> createPluginsMappedForTenant () {
		 
		 Map<String, Boolean> pluginsMapForTenant = new HashMap<>();
		 pluginsMapForTenant.put("EXCEL", true);
		 pluginsMapForTenant.put("PDF", false);
		 
		 return pluginsMapForTenant;
		 
	 }
}
