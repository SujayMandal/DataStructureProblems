package com.ca.umg.rt.web.rest.controller;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.rt.batching.data.BatchFileStatusInput;
import com.ca.umg.rt.batching.data.BatchFileStatusResponse;
import com.ca.umg.rt.batching.delegate.BatchFileStatusDelegate;
import com.ca.umg.rt.core.deployment.constants.RuntimeConstants;
import com.ca.umg.rt.exception.codes.RuntimeExceptionCode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import junit.framework.Assert;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class BatchFileStatusControllerTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BatchFileStatusControllerTest.class);

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Inject
    private BatchFileStatusController batchFileStatusController;

    @Inject
    private BatchFileStatusDelegate batchFileStatusDelegate;

    private static final Gson gson = new GsonBuilder().create();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        this.mockMvc = MockMvcBuilders.standaloneSetup(batchFileStatusController).build();

    }

    @Test
    public void testGetBatchFileStatusYettoPicked() {
        try {
            when(batchFileStatusDelegate.getBatchFileStatus(Mockito.any(BatchFileStatusInput.class))).thenReturn(
                    new ArrayList<Map<String,Object>>());
            MvcResult mvcResult = mockMvc.perform(get("/api/batchfile//status/{fileName}/{extn}", "correct23", "xls"))
                    .andDo(print()).andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertNotNull(mockResponse);
            String responseBody = mockResponse.getContentAsString();
            BatchFileStatusResponse restResponse = gson.fromJson(responseBody, new TypeToken<BatchFileStatusResponse>() {
            }.getType());
            assertNotNull(restResponse);
            verify(batchFileStatusDelegate, times(1)).getBatchFileStatus(Mockito.any(BatchFileStatusInput.class));
        } catch (SystemException | BusinessException e) {
        	LOGGER.error("Exception: ", e);
        } catch (Exception e) {
        	LOGGER.error("Exception: ", e);
        }

    }

    @Test
    public void testGetBatchFileStatusProcessed() {
        try {
            when(batchFileStatusDelegate.getBatchFileStatus(Mockito.any(BatchFileStatusInput.class))).thenReturn(
                    getBatchStatusInfo());
            MvcResult mvcResult = mockMvc.perform(get("/api/batchfile//status/{fileName}/{extn}", "correct23", "xls"))
                    .andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertNotNull(mockResponse);
            String responseBody = mockResponse.getContentAsString();
            BatchFileStatusResponse restResponse = gson.fromJson(responseBody, new TypeToken<BatchFileStatusResponse>() {
            }.getType());
            assertNotNull(restResponse);
            verify(batchFileStatusDelegate, times(1)).getBatchFileStatus(Mockito.any(BatchFileStatusInput.class));
        } catch (SystemException | BusinessException e) {
        	LOGGER.error("Exception: ", e);
        } catch (Exception e) {
        	LOGGER.error("Exception: ", e);
        }

    }

    @Test
    public void testGetBatchFileStatusWithDateandYettoPicked() {
        try {
            when(batchFileStatusDelegate.getBatchFileStatus(Mockito.any(BatchFileStatusInput.class))).thenReturn(
                    new ArrayList<Map<String,Object>>());
            MvcResult mvcResult = mockMvc
                    .perform(get("/api/batchfile//status/{fileName}/{extn}/{date}", "correct23", "xls", "22-Jun-2010"))
                    .andDo(print()).andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertNotNull(mockResponse);
            String responseBody = mockResponse.getContentAsString();
            BatchFileStatusResponse restResponse = gson.fromJson(responseBody, new TypeToken<BatchFileStatusResponse>() {
            }.getType());
            assertNotNull(restResponse);
            verify(batchFileStatusDelegate, times(1)).getBatchFileStatus(Mockito.any(BatchFileStatusInput.class));
        } catch (SystemException | BusinessException e) {
        	LOGGER.error("Exception: ", e);
        } catch (Exception e) {
        	LOGGER.error("Exception: ", e);
        }

    }

    @Test
    public void testGetBatchFileStatusWithDate() {
        try {
            when(batchFileStatusDelegate.getBatchFileStatus(Mockito.any(BatchFileStatusInput.class))).thenReturn(
                    getBatchStatusInfo());
            MvcResult mvcResult = mockMvc
                    .perform(get("/api/batchfile//status/{fileName}/{extn}/{date}", "correct23", "xls", "22-Jun-2010"))
                    .andDo(print()).andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertNotNull(mockResponse);
            String responseBody = mockResponse.getContentAsString();
            BatchFileStatusResponse restResponse = gson.fromJson(responseBody, new TypeToken<BatchFileStatusResponse>() {
            }.getType());
            assertNotNull(restResponse);
            verify(batchFileStatusDelegate, times(1)).getBatchFileStatus(Mockito.any(BatchFileStatusInput.class));
        } catch (SystemException | BusinessException e) {
        	LOGGER.error("Exception: ", e);
        } catch (Exception e) {
        	LOGGER.error("Exception: ", e);
        }

    }

    @Test
    public void testGetBatchFileStatusWithException() {
        try {
            BusinessException businessException = new BusinessException(RuntimeExceptionCode.RSE000509, new Object[] {});
            when(batchFileStatusDelegate.getBatchFileStatus(Mockito.any(BatchFileStatusInput.class)))
                    .thenThrow(businessException);

            MvcResult mvcResult = mockMvc
                    .perform(get("/api/batchfile//status/{fileName}/{extn}/{date}", "correct23", "xls", "22-Jun2010"))
                    .andDo(print()).andReturn();

            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertNotNull(mockResponse);
            String responseBody = mockResponse.getContentAsString();
            BatchFileStatusResponse restResponse = gson.fromJson(responseBody, new TypeToken<BatchFileStatusResponse>() {
            }.getType());
            assertNotNull(restResponse);
            Assert.assertEquals("ERROR", restResponse.getHeader().get(RuntimeConstants.BATCH_API_STATUS));
            verify(batchFileStatusDelegate, times(1)).getBatchFileStatus(Mockito.any(BatchFileStatusInput.class));

        } catch (SystemException | BusinessException e) {
            assertNotNull(e.getLocalizedMessage());
            LOGGER.error("Exception: ", e);
        } catch (Exception e) {
        	LOGGER.error("Exception: ", e);
        }

    }

    @Test
    public void testGetBatchFileStatusWithExceptionWithoutDate() {
        try {
            BusinessException businessException = new BusinessException(RuntimeExceptionCode.RSE000509, new Object[] {});
            when(batchFileStatusDelegate.getBatchFileStatus(Mockito.any(BatchFileStatusInput.class)))
                    .thenThrow(businessException);

            MvcResult mvcResult = mockMvc.perform(get("/api/batchfile//status/{fileName}/{extn}", "correct23", "xls"))
                    .andDo(print()).andReturn();

            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertNotNull(mockResponse);
            String responseBody = mockResponse.getContentAsString();
            BatchFileStatusResponse restResponse = gson.fromJson(responseBody, new TypeToken<BatchFileStatusResponse>() {
            }.getType());
            assertNotNull(restResponse);
            Assert.assertEquals("ERROR", restResponse.getHeader().get(RuntimeConstants.BATCH_API_STATUS));
            verify(batchFileStatusDelegate, times(1)).getBatchFileStatus(Mockito.any(BatchFileStatusInput.class));

        } catch (SystemException | BusinessException e) {
            assertNotNull(e.getLocalizedMessage());
            LOGGER.error("Exception: ", e);
        } catch (Exception e) {
        	LOGGER.error("Exception: ", e);
        }

    }

    private List<Map<String,Object>> getBatchStatusInfo() {
        List<Map<String,Object>> batchStatusInfos = new ArrayList<Map<String,Object>>();
        for (int i = 0; i < 10; i++) {
            Map<String, Object> batchStatusInfo = new LinkedHashMap<String, Object>();
            batchStatusInfo.put("Batch status", "IN_PROGRESS");
        }

        return batchStatusInfos;

    }

}
