package com.ca.umg.sdc.rest.controller;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.mapping.delegate.MappingDelegate;
import com.ca.umg.business.mapping.info.MappingDescriptor;
import com.ca.umg.business.mapping.info.MappingHierarchyInfo;
import com.ca.umg.business.mapping.info.MappingInfo;
import com.ca.umg.business.mapping.info.MappingStatus;
import com.ca.umg.business.mapping.info.MappingsCopyInfo;
import com.ca.umg.business.mapping.info.QueryLaunchInfo;
import com.ca.umg.business.mid.extraction.info.MappingViews;
import com.ca.umg.business.mid.extraction.info.MidIOInfo;
import com.ca.umg.business.mid.extraction.info.MidParamInfo;
import com.ca.umg.business.mid.extraction.info.TidIOInfo;
import com.ca.umg.business.mid.extraction.info.TidParamInfo;
import com.ca.umg.business.model.info.ModelInfo;
import com.ca.umg.business.validation.ValidationError;
import com.ca.umg.sdc.rest.utils.RestResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class MappingControllerTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MappingControllerTest.class);

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Inject
    private MappingController mappingController;

    @Inject
    private MappingDelegate mappingDelegate;

    private static final Gson gson = new GsonBuilder().create();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        this.mockMvc = MockMvcBuilders.standaloneSetup(mappingController).build();
    }

    @Test
    public final void testFindAllLibraries() {
        try {
            when(mappingDelegate.getMappingHierarchyInfos()).thenReturn(createMappingHierarchyInfo());
            MvcResult mvcResult = mockMvc.perform(post("/mapping/listAll")).andDo(print()).andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertNotNull(mockResponse);
            assertEquals("application/json;charset=UTF-8", mockResponse.getContentType());
            String responseBody = mockResponse.getContentAsString();
            RestResponse<List<MappingHierarchyInfo>> restResponse = gson.fromJson(responseBody,
                    new TypeToken<RestResponse<List<MappingHierarchyInfo>>>() {
                    }.getType());
            assertEquals(1, restResponse.getResponse().size());
            assertEquals("Done", restResponse.getMessage());
            assertMappingInfoValues(restResponse.getResponse().get(0).getMappingInfos().get(0));
            verify(mappingDelegate, times(1)).getMappingHierarchyInfos();
        } catch (Exception exp) {
            fail(exp.getMessage());
        }
    }

    @Test
    public final void testFindAllLibrariesForException() throws Exception {
        BusinessException businessExcp = new BusinessException(BusinessExceptionCodes.BSE000049, new Object[] {});
        when(mappingDelegate.getMappingHierarchyInfos()).thenThrow(businessExcp);
        MvcResult mvcResult = mockMvc.perform(post("/mapping/listAll")).andDo(print()).andReturn();
        MockHttpServletResponse mockResponse = mvcResult.getResponse();
        assertNotNull(mockResponse);
        assertEquals("application/json;charset=UTF-8", mockResponse.getContentType());
        String responseBody = mockResponse.getContentAsString();
        RestResponse<List<MappingHierarchyInfo>> restResponse = gson.fromJson(responseBody,
                new TypeToken<RestResponse<List<MappingHierarchyInfo>>>() {
                }.getType());
        assertTrue(restResponse.isError());
        assertEquals(restResponse.getErrorCode(), BusinessExceptionCodes.BSE000049);
    }

    @Test
    public final void testExtractTidMidForNullTid() {
        try {
            MappingDescriptor mappingDescriptor = getMappingDescriptorForTest();
            when(mappingDelegate.generateMapping("DerivedTestModel")).thenReturn(mappingDescriptor);
            MvcResult mvcResult = mockMvc
                    .perform(
                            post("/mapping/getMappingsForModel").param("derivedModelName", "DerivedTestModel")
                                    .param("derivedTidName", "derivedTidName")
                                    .param("tidName", "null").param("description", "description").contentType(MediaType.APPLICATION_JSON)
                                    .content(gson.toJson(mappingDescriptor))).andDo(print()).andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertNotNull(mockResponse);
            assertEquals("application/json;charset=UTF-8", mockResponse.getContentType());
            String responseBody = mockResponse.getContentAsString();
            RestResponse<MappingDescriptor> restResponse = gson.fromJson(responseBody,
                    new TypeToken<RestResponse<MappingDescriptor>>() {
                    }.getType());
            assertMappingDescriptorValues(restResponse.getResponse());
            assertEquals("Generated mapping successfully for model DerivedTestModel.", restResponse.getMessage());
            verify(mappingDelegate, times(1)).generateMapping("DerivedTestModel");
        } catch (Exception exp) {
            fail(exp.getMessage());
        }
    }

    @Test
    public final void testExtractTidMidForTidName() {
        try {
            MappingDescriptor mappingDescriptor = getMappingDescriptorForTest();
            when(mappingDelegate.generateMapping("DerivedTestModel","derivedTid", "UserTid", "")).thenReturn(mappingDescriptor);
            MvcResult mvcResult = mockMvc
                    .perform(
                            post("/mapping/getMappingsForModel").param("derivedModelName", "DerivedTestModel")
                                    .param("derivedTidName", "derivedTid")
                                    .param("tidName", "UserTid").contentType(MediaType.APPLICATION_JSON)
                                    .content(gson.toJson(mappingDescriptor))).andDo(print()).andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertNotNull(mockResponse);
            assertEquals("application/json;charset=UTF-8", mockResponse.getContentType());
            String responseBody = mockResponse.getContentAsString();
            RestResponse<MappingDescriptor> restResponse = gson.fromJson(responseBody,
                    new TypeToken<RestResponse<MappingDescriptor>>() {
                    }.getType());
            assertMappingDescriptorValues(restResponse.getResponse());
            assertEquals("Generated mapping successfully for model DerivedTestModel.", restResponse.getMessage());
            verify(mappingDelegate, times(1)).generateMapping("DerivedTestModel", "derivedTid", "UserTid", "");
        } catch (Exception exp) {
            fail(exp.getMessage());
        }
    }

    @Test
    public final void testExtractTidMidForException() {
        try {
            BusinessException businessExcp = new BusinessException(BusinessExceptionCodes.BSE000049, new Object[] {});
            MappingDescriptor mappingDescriptor = getMappingDescriptorForTest();
            when(mappingDelegate.generateMapping("DerivedTestModel")).thenThrow(businessExcp);
            MvcResult mvcResult = mockMvc
                    .perform(
                            post("/mapping/getMappingsForModel").param("derivedModelName", "DerivedTestModel")
                                    .param("derivedTidName", "null")
                                    .param("tidName", "null").param("description", "description").contentType(MediaType.APPLICATION_JSON)
                                    .content(gson.toJson(mappingDescriptor))).andDo(print()).andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertNotNull(mockResponse);
            assertEquals("application/json;charset=UTF-8", mockResponse.getContentType());
            String responseBody = mockResponse.getContentAsString();
            RestResponse<MappingDescriptor> restResponse = gson.fromJson(responseBody,
                    new TypeToken<RestResponse<MappingDescriptor>>() {
                    }.getType());
            assertTrue(restResponse.isError());
            assertEquals(restResponse.getErrorCode(), BusinessExceptionCodes.BSE000049);
            verify(mappingDelegate, times(1)).generateMapping("DerivedTestModel");
        } catch (Exception exp) {
            fail(exp.getMessage());
        }
    }

    @Test
    public final void testGetMappingDetails() {
        try {
            MappingDescriptor mappingDescriptor = getMappingDescriptorForTest();

            String tidName = "testingTid";
            when(mappingDelegate.readMapping(tidName)).thenReturn(mappingDescriptor);
            MvcResult mvcResult = mockMvc
                    .perform(get("/mapping/getMappingDetails/{tidName}", tidName).contentType(MediaType.APPLICATION_JSON))
                    .andDo(print()).andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertNotNull(mockResponse);
            assertEquals("application/json;charset=UTF-8", mockResponse.getContentType());
            String responseBody = mockResponse.getContentAsString();
            RestResponse<MappingDescriptor> restResponse = gson.fromJson(responseBody,
                    new TypeToken<RestResponse<MappingDescriptor>>() {
                    }.getType());
            assertMappingDescriptorValues(restResponse.getResponse());
            assertEquals("Retrieved mapping successfully for tid name testingTid.", restResponse.getMessage());
            verify(mappingDelegate, times(1)).readMapping(tidName);
        } catch (Exception exp) {
            fail(exp.getMessage());
        }
    }

    @Test
    public final void testGetMappingDetailsForException() {
        try {
            BusinessException businessExcp = new BusinessException(BusinessExceptionCodes.BSE000049, new Object[] {});
            String tidName = "testingTid";
            when(mappingDelegate.readMapping(tidName)).thenThrow(businessExcp);
            MvcResult mvcResult = mockMvc
                    .perform(get("/mapping/getMappingDetails/{tidName}", tidName).contentType(MediaType.APPLICATION_JSON))
                    .andDo(print()).andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertNotNull(mockResponse);
            assertEquals("application/json;charset=UTF-8", mockResponse.getContentType());
            String responseBody = mockResponse.getContentAsString();
            RestResponse<MappingDescriptor> restResponse = gson.fromJson(responseBody,
                    new TypeToken<RestResponse<MappingDescriptor>>() {
                    }.getType());
            assertTrue(restResponse.isError());
            assertEquals(restResponse.getErrorCode(), BusinessExceptionCodes.BSE000049);
            verify(mappingDelegate, times(1)).readMapping(tidName);
        } catch (Exception exp) {
            fail(exp.getMessage());
        }
    }

    @Test
    public final void testDeleteTidMapping() {
        try {
            String tidName = "testingTid";
            when(mappingDelegate.deleteMapping(tidName)).thenReturn(true);
            MvcResult mvcResult = mockMvc
                    .perform(get("/mapping/deleteTidMapping/{tidName}", tidName).contentType(MediaType.APPLICATION_JSON))
                    .andDo(print()).andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertNotNull(mockResponse);
            assertEquals("application/json;charset=UTF-8", mockResponse.getContentType());
            String responseBody = mockResponse.getContentAsString();
            RestResponse<Boolean> restResponse = gson.fromJson(responseBody, new TypeToken<RestResponse<Boolean>>() {
            }.getType());
            assertTrue(restResponse.getResponse());
            assertEquals("Tid Mapping successfully deleted for : testingTid.", restResponse.getMessage());
            verify(mappingDelegate, times(1)).deleteMapping(tidName);
        } catch (Exception exp) {
            fail(exp.getMessage());
        }
    }

    @Test
    public final void testDeleteTidMappingForException() {
        try {
            BusinessException businessExcp = new BusinessException(BusinessExceptionCodes.BSE000049, new Object[] {});
            String tidName = "testingTid";
            when(mappingDelegate.deleteMapping(tidName)).thenThrow(businessExcp);
            MvcResult mvcResult = mockMvc
                    .perform(get("/mapping/deleteTidMapping/{tidName}", tidName).contentType(MediaType.APPLICATION_JSON))
                    .andDo(print()).andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertNotNull(mockResponse);
            assertEquals("application/json;charset=UTF-8", mockResponse.getContentType());
            String responseBody = mockResponse.getContentAsString();
            RestResponse<Boolean> restResponse = gson.fromJson(responseBody, new TypeToken<RestResponse<Boolean>>() {
            }.getType());
            assertTrue(restResponse.isError());
            assertEquals(restResponse.getErrorCode(), BusinessExceptionCodes.BSE000049);
            verify(mappingDelegate, times(1)).deleteMapping(tidName);
        } catch (Exception exp) {
            fail(exp.getMessage());
        }
    }

    @Test
    public final void testCreateInputMapForQuery() {
        try {
            QueryLaunchInfo queryLaunchInfo = getQueryLaunchInfo();
            String type = "queryType";
            String tidName = "testingTid";
            when(mappingDelegate.createInputMapForQuery(type, tidName)).thenReturn(queryLaunchInfo);
            MvcResult mvcResult = mockMvc
                    .perform(
                            post("/mapping/createInputMapForQuery").param("type", type).param("tidName", tidName)
                                    .contentType(MediaType.APPLICATION_JSON)).andDo(print()).andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertNotNull(mockResponse);
            assertEquals("application/json;charset=UTF-8", mockResponse.getContentType());
            String responseBody = mockResponse.getContentAsString();
            RestResponse<QueryLaunchInfo> restResponse = gson.fromJson(responseBody,
                    new TypeToken<RestResponse<QueryLaunchInfo>>() {
                    }.getType());
            assertQueryLaunchInfo(restResponse.getResponse());
            //assertEquals("Mapping saved successfully.", restResponse.getMessage());
            verify(mappingDelegate, times(1)).createInputMapForQuery(type, tidName);
        } catch (Exception exp) {
            fail(exp.getMessage());
        }
    }

    @Test
    public final void testCreateInputMapForQueryForException() {
        try {
            BusinessException businessExcp = new BusinessException(BusinessExceptionCodes.BSE000049, new Object[] {});
            String type = "queryType";
            String tidName = "testingTid";
            when(mappingDelegate.createInputMapForQuery(type, tidName)).thenThrow(businessExcp);
            MvcResult mvcResult = mockMvc
                    .perform(
                            post("/mapping/createInputMapForQuery").param("type", type).param("tidName", tidName)
                                    .contentType(MediaType.APPLICATION_JSON)).andDo(print()).andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertNotNull(mockResponse);
            assertEquals("application/json;charset=UTF-8", mockResponse.getContentType());
            String responseBody = mockResponse.getContentAsString();
            RestResponse<QueryLaunchInfo> restResponse = gson.fromJson(responseBody,
                    new TypeToken<RestResponse<QueryLaunchInfo>>() {
                    }.getType());
            assertTrue(restResponse.isError());
            assertEquals(restResponse.getErrorCode(), BusinessExceptionCodes.BSE000049);
            verify(mappingDelegate, times(1)).createInputMapForQuery(type, tidName);
        } catch (Exception exp) {
            fail(exp.getMessage());
        }
    }

    @Test
    public final void testGetTidListForCopy() {
        try {
            List<MappingsCopyInfo> mappingsCopyInfoList = new ArrayList<MappingsCopyInfo>();
            MappingsCopyInfo mappingsCopyInfo = new MappingsCopyInfo();
            mappingsCopyInfo.setVersion("TestModelName");
            mappingsCopyInfoList.add(mappingsCopyInfo);
            when(mappingDelegate.getTidListForCopy()).thenReturn(mappingsCopyInfoList);
            MvcResult mvcResult = mockMvc.perform(get("/mapping/getTidListForCopy").contentType(MediaType.APPLICATION_JSON))
                    .andDo(print()).andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertNotNull(mockResponse);
            assertEquals("application/json;charset=UTF-8", mockResponse.getContentType());
            String responseBody = mockResponse.getContentAsString();
            RestResponse<List<MappingsCopyInfo>> restResponse = gson.fromJson(responseBody,
                    new TypeToken<RestResponse<List<MappingsCopyInfo>>>() {
                    }.getType());
            assertEquals("TestModelName", restResponse.getResponse().get(0).getVersion());
            assertEquals("Retrieved mapping successfully for tid Copy .", restResponse.getMessage());
            verify(mappingDelegate, times(1)).getTidListForCopy();
        } catch (Exception exp) {
            fail(exp.getMessage());
        }
    }

    @Test
    public final void testGetTidListForCopyForException() {
        try {
            BusinessException businessExcp = new BusinessException(BusinessExceptionCodes.BSE000049, new Object[] {});
            List<MappingsCopyInfo> mappingsCopyInfoList = new ArrayList<MappingsCopyInfo>();
            MappingsCopyInfo mappingsCopyInfo = new MappingsCopyInfo();
            mappingsCopyInfo.setVersion("TestModelName");
            mappingsCopyInfoList.add(mappingsCopyInfo);
            when(mappingDelegate.getTidListForCopy()).thenThrow(businessExcp);
            MvcResult mvcResult = mockMvc.perform(get("/mapping/getTidListForCopy").contentType(MediaType.APPLICATION_JSON))
                    .andDo(print()).andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertNotNull(mockResponse);
            assertEquals("application/json;charset=UTF-8", mockResponse.getContentType());
            String responseBody = mockResponse.getContentAsString();
            RestResponse<List<MappingsCopyInfo>> restResponse = gson.fromJson(responseBody,
                    new TypeToken<RestResponse<List<MappingsCopyInfo>>>() {
                    }.getType());
            assertTrue(restResponse.isError());
            assertEquals(restResponse.getErrorCode(), BusinessExceptionCodes.BSE000049);
            verify(mappingDelegate, times(1)).getTidListForCopy();
        } catch (Exception exp) {
            fail(exp.getMessage());
        }
    }

    @Test
    public final void testFind() {
        try {
            String id = "mappingId";
            when(mappingDelegate.find(id)).thenReturn(getMappingInfoForTest());
            MvcResult mvcResult = mockMvc.perform(get("/mapping/{id}", id).contentType(MediaType.APPLICATION_JSON))
                    .andDo(print()).andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertNotNull(mockResponse);
            assertEquals("application/json;charset=UTF-8", mockResponse.getContentType());
            String responseBody = mockResponse.getContentAsString();
            RestResponse<MappingInfo> restResponse = gson.fromJson(responseBody, new TypeToken<RestResponse<MappingInfo>>() {
            }.getType());
            assertMappingInfoValues(restResponse.getResponse());
            assertEquals("done", restResponse.getMessage());
            verify(mappingDelegate, times(1)).find(id);
        } catch (Exception exp) {
            fail(exp.getMessage());
        }
    }

    @Test
    public final void testFindForException() {
        try {
            String id = "mappingId";
            BusinessException businessExcp = new BusinessException(BusinessExceptionCodes.BSE000049, new Object[] {});

            when(mappingDelegate.find(id)).thenThrow(businessExcp);
            MvcResult mvcResult = mockMvc.perform(get("/mapping/{id}", id).contentType(MediaType.APPLICATION_JSON))
                    .andDo(print()).andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertNotNull(mockResponse);
            assertEquals("application/json;charset=UTF-8", mockResponse.getContentType());
            String responseBody = mockResponse.getContentAsString();
            RestResponse<MappingInfo> restResponse = gson.fromJson(responseBody, new TypeToken<RestResponse<MappingInfo>>() {
            }.getType());
            assertTrue(restResponse.isError());
            assertEquals(restResponse.getErrorCode(), BusinessExceptionCodes.BSE000049);
        } catch (Exception exp) {
            fail(exp.getMessage());
        }
    }

    @Test
    public final void testSave() {
        try {
            String mappingDescriptorJson = "{\"tidName\" : \"\", \"description\" : \"asdfsd\", \"midName\" : \"Test_Model1-Aug-20-2014-13-11\", \"modelName\" : \"Test_Model1\",\"tidTree\" : {\"tidOutput\" : [{\"description\":\"Example Parameter1. This is a $ value\",\"mandatory\":true,\"syndicate\":false,\"name\":\"OutputParam1\",\"text\":\"OutputParam1<span class=\\\"mandatoryParam\\\">*</span>\",\"dataFormat\":null,\"size\":0,\"precision\":0,\"userSelected\":false,\"flatenedName\":\"OutputParam1\",\"sequence\":1,\"datatype\":{\"type\":\"double\",\"properties\":{\"defaultValue\":\"23.45\"},\"array\":false},\"value\":null,\"sqlId\":null,\"expressionId\":null,\"exprsnOutput\":false,\"sqlOutput\":false,\"children\":null},{\"description\":\"Example Parameter2. This is a string value\",\"mandatory\":true,\"syndicate\":false,\"name\":\"OutputParam2\",\"text\":\"OutputParam2<span class=\\\"mandatoryParam\\\">*</span>\",\"dataFormat\":null,\"size\":0,\"precision\":0,\"userSelected\":false,\"flatenedName\":\"OutputParam2\",\"sequence\":2,\"datatype\":{\"type\":\"string\",\"properties\":{\"defaultValue\":\"23.45\"},\"array\":false},\"value\":null,\"sqlId\":null,\"expressionId\":null,\"exprsnOutput\":false,\"sqlOutput\":false,\"children\":null},{\"description\":\"Example Parameter3. This is an integer value\",\"mandatory\":true,\"syndicate\":false,\"name\":\"OutputParam3\",\"text\":\"OutputParam3<span class=\\\"mandatoryParam\\\">*</span>\",\"dataFormat\":null,\"size\":0,\"precision\":0,\"userSelected\":false,\"flatenedName\":\"OutputParam3\",\"sequence\":3,\"datatype\":{\"type\":\"integer\",\"properties\":{\"defaultValue\":\"23\"},\"array\":false},\"value\":null,\"sqlId\":null,\"expressionId\":null,\"exprsnOutput\":false,\"sqlOutput\":false,\"children\":null},{\"description\":\"Example Parameter4. This is aboolean value\",\"mandatory\":true,\"syndicate\":false,\"name\":\"OutputParam4\",\"text\":\"OutputParam4<span class=\\\"mandatoryParam\\\">*</span>\",\"dataFormat\":null,\"size\":0,\"precision\":0,\"userSelected\":false,\"flatenedName\":\"OutputParam4\",\"sequence\":4,\"datatype\":{\"type\":\"boolean\",\"properties\":{\"defaultValue\":\"true\"},\"array\":false},\"value\":null,\"sqlId\":null,\"expressionId\":null,\"exprsnOutput\":false,\"sqlOutput\":false,\"children\":null},{\"description\":\"Example Parameter5. This is a date value\",\"mandatory\":true,\"syndicate\":false,\"name\":\"OutputParam5\",\"text\":\"OutputParam5<span class=\\\"mandatoryParam\\\">*</span>\",\"dataFormat\":null,\"size\":0,\"precision\":0,\"userSelected\":false,\"flatenedName\":\"OutputParam5\",\"sequence\":5,\"datatype\":{\"type\":\"date\",\"properties\":{\"defaultValue\":\"2002-09-24\"},\"array\":false},\"value\":null,\"sqlId\":null,\"expressionId\":null,\"exprsnOutput\":false,\"sqlOutput\":false,\"children\":null}], \"tidInput\" : [] },\"tidMidMapping\" : {\"outputMappingViews\":[{\"mappingParam\":\"OutputParam1\",\"mappedTo\":\"OutputParam1\"},{\"mappingParam\":\"OutputParam2\",\"mappedTo\":\"OutputParam2\"},{\"mappingParam\":\"OutputParam3\",\"mappedTo\":\"OutputParam3\"},{\"mappingParam\":\"OutputParam4\",\"mappedTo\":\"OutputParam4\"},{\"mappingParam\":\"OutputParam5\",\"mappedTo\":\"OutputParam5\"}]}}";
            KeyValuePair<String, List<ValidationError>> validations = new KeyValuePair<String, List<ValidationError>>();
            validations.setKey("testTidName");
            validations.setValue(asList(new ValidationError("text", "ErrorMessage")));
            String type = "mappingType";
            when(mappingDelegate.saveMappingDescription(Mockito.any(MappingDescriptor.class), Mockito.anyString(),
                            Mockito.anyString())).thenReturn(validations);
            MvcResult mvcResult = mockMvc
                    .perform(
                            post("/mapping/saveMapping").param("mappingDescriptorJson", mappingDescriptorJson)
                                    .param("validate","validate").contentType(MediaType.APPLICATION_JSON)).andDo(print()).andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertNotNull(mockResponse);
            assertEquals("application/json;charset=UTF-8", mockResponse.getContentType());
            String responseBody = mockResponse.getContentAsString();
            RestResponse<MappingDescriptor> restResponse = gson.fromJson(responseBody,
                    new TypeToken<RestResponse<MappingDescriptor>>() {
                    }.getType());
            assertTrue(!restResponse.isError());
            assertEquals("testTidName", restResponse.getResponse().getTidName());
            assertEquals("Mapping testTidName saved successfully (post validation).", restResponse.getMessage());
        } catch (Exception exp) {
            fail(exp.getMessage());
        }
    }

    @Test
    public final void testSaveForEmptyKeyValue() {
        try {
            String mappingDescriptorJson = "{\"tidName\" : \"\", \"description\" : \"asdfsd\", \"midName\" : \"Test_Model1-Aug-20-2014-13-11\", \"modelName\" : \"Test_Model1\",\"tidTree\" : {\"tidOutput\" : [{\"description\":\"Example Parameter1. This is a $ value\",\"mandatory\":true,\"syndicate\":false,\"name\":\"OutputParam1\",\"text\":\"OutputParam1<span class=\\\"mandatoryParam\\\">*</span>\",\"dataFormat\":null,\"size\":0,\"precision\":0,\"userSelected\":false,\"flatenedName\":\"OutputParam1\",\"sequence\":1,\"datatype\":{\"type\":\"double\",\"properties\":{\"defaultValue\":\"23.45\"},\"array\":false},\"value\":null,\"sqlId\":null,\"expressionId\":null,\"exprsnOutput\":false,\"sqlOutput\":false,\"children\":null},{\"description\":\"Example Parameter2. This is a string value\",\"mandatory\":true,\"syndicate\":false,\"name\":\"OutputParam2\",\"text\":\"OutputParam2<span class=\\\"mandatoryParam\\\">*</span>\",\"dataFormat\":null,\"size\":0,\"precision\":0,\"userSelected\":false,\"flatenedName\":\"OutputParam2\",\"sequence\":2,\"datatype\":{\"type\":\"string\",\"properties\":{\"defaultValue\":\"23.45\"},\"array\":false},\"value\":null,\"sqlId\":null,\"expressionId\":null,\"exprsnOutput\":false,\"sqlOutput\":false,\"children\":null},{\"description\":\"Example Parameter3. This is an integer value\",\"mandatory\":true,\"syndicate\":false,\"name\":\"OutputParam3\",\"text\":\"OutputParam3<span class=\\\"mandatoryParam\\\">*</span>\",\"dataFormat\":null,\"size\":0,\"precision\":0,\"userSelected\":false,\"flatenedName\":\"OutputParam3\",\"sequence\":3,\"datatype\":{\"type\":\"integer\",\"properties\":{\"defaultValue\":\"23\"},\"array\":false},\"value\":null,\"sqlId\":null,\"expressionId\":null,\"exprsnOutput\":false,\"sqlOutput\":false,\"children\":null},{\"description\":\"Example Parameter4. This is aboolean value\",\"mandatory\":true,\"syndicate\":false,\"name\":\"OutputParam4\",\"text\":\"OutputParam4<span class=\\\"mandatoryParam\\\">*</span>\",\"dataFormat\":null,\"size\":0,\"precision\":0,\"userSelected\":false,\"flatenedName\":\"OutputParam4\",\"sequence\":4,\"datatype\":{\"type\":\"boolean\",\"properties\":{\"defaultValue\":\"true\"},\"array\":false},\"value\":null,\"sqlId\":null,\"expressionId\":null,\"exprsnOutput\":false,\"sqlOutput\":false,\"children\":null},{\"description\":\"Example Parameter5. This is a date value\",\"mandatory\":true,\"syndicate\":false,\"name\":\"OutputParam5\",\"text\":\"OutputParam5<span class=\\\"mandatoryParam\\\">*</span>\",\"dataFormat\":null,\"size\":0,\"precision\":0,\"userSelected\":false,\"flatenedName\":\"OutputParam5\",\"sequence\":5,\"datatype\":{\"type\":\"date\",\"properties\":{\"defaultValue\":\"2002-09-24\"},\"array\":false},\"value\":null,\"sqlId\":null,\"expressionId\":null,\"exprsnOutput\":false,\"sqlOutput\":false,\"children\":null}], \"tidInput\" : [] },\"tidMidMapping\" : {\"outputMappingViews\":[{\"mappingParam\":\"OutputParam1\",\"mappedTo\":\"OutputParam1\"},{\"mappingParam\":\"OutputParam2\",\"mappedTo\":\"OutputParam2\"},{\"mappingParam\":\"OutputParam3\",\"mappedTo\":\"OutputParam3\"},{\"mappingParam\":\"OutputParam4\",\"mappedTo\":\"OutputParam4\"},{\"mappingParam\":\"OutputParam5\",\"mappedTo\":\"OutputParam5\"}]}}";
            KeyValuePair<String, List<ValidationError>> validations = new KeyValuePair<String, List<ValidationError>>();
            String type = "mappingType";
            when(mappingDelegate.saveMappingDescription(Mockito.any(MappingDescriptor.class), Mockito.anyString(),
                            Mockito.anyString())).thenReturn(validations);
            MvcResult mvcResult = mockMvc
                    .perform(
                            post("/mapping/saveMapping").param("mappingDescriptorJson", mappingDescriptorJson)
                            		.param("validate","validate").contentType(MediaType.APPLICATION_JSON)).andDo(print()).andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertNotNull(mockResponse);
            assertEquals("application/json;charset=UTF-8", mockResponse.getContentType());
            String responseBody = mockResponse.getContentAsString();
            RestResponse<MappingDescriptor> restResponse = gson.fromJson(responseBody,
                    new TypeToken<RestResponse<MappingDescriptor>>() {
                    }.getType());
            assertTrue(restResponse.isError());
            assertEquals("Mapping could not be saved successfully.", restResponse.getMessage());
        } catch (Exception exp) {
            fail(exp.getMessage());
        }
    }

    @Test
    public final void testSaveForException() {
        try {
            String mappingDescriptorJson = "{\"tidName\" : \"\", \"description\" : \"asdfsd\", \"midName\" : \"Test_Model1-Aug-20-2014-13-11\", \"modelName\" : \"Test_Model1\",\"tidTree\" : {\"tidOutput\" : [{\"description\":\"Example Parameter1. This is a $ value\",\"mandatory\":true,\"syndicate\":false,\"name\":\"OutputParam1\",\"text\":\"OutputParam1<span class=\\\"mandatoryParam\\\">*</span>\",\"dataFormat\":null,\"size\":0,\"precision\":0,\"userSelected\":false,\"flatenedName\":\"OutputParam1\",\"sequence\":1,\"datatype\":{\"type\":\"double\",\"properties\":{\"defaultValue\":\"23.45\"},\"array\":false},\"value\":null,\"sqlId\":null,\"expressionId\":null,\"exprsnOutput\":false,\"sqlOutput\":false,\"children\":null},{\"description\":\"Example Parameter2. This is a string value\",\"mandatory\":true,\"syndicate\":false,\"name\":\"OutputParam2\",\"text\":\"OutputParam2<span class=\\\"mandatoryParam\\\">*</span>\",\"dataFormat\":null,\"size\":0,\"precision\":0,\"userSelected\":false,\"flatenedName\":\"OutputParam2\",\"sequence\":2,\"datatype\":{\"type\":\"string\",\"properties\":{\"defaultValue\":\"23.45\"},\"array\":false},\"value\":null,\"sqlId\":null,\"expressionId\":null,\"exprsnOutput\":false,\"sqlOutput\":false,\"children\":null},{\"description\":\"Example Parameter3. This is an integer value\",\"mandatory\":true,\"syndicate\":false,\"name\":\"OutputParam3\",\"text\":\"OutputParam3<span class=\\\"mandatoryParam\\\">*</span>\",\"dataFormat\":null,\"size\":0,\"precision\":0,\"userSelected\":false,\"flatenedName\":\"OutputParam3\",\"sequence\":3,\"datatype\":{\"type\":\"integer\",\"properties\":{\"defaultValue\":\"23\"},\"array\":false},\"value\":null,\"sqlId\":null,\"expressionId\":null,\"exprsnOutput\":false,\"sqlOutput\":false,\"children\":null},{\"description\":\"Example Parameter4. This is aboolean value\",\"mandatory\":true,\"syndicate\":false,\"name\":\"OutputParam4\",\"text\":\"OutputParam4<span class=\\\"mandatoryParam\\\">*</span>\",\"dataFormat\":null,\"size\":0,\"precision\":0,\"userSelected\":false,\"flatenedName\":\"OutputParam4\",\"sequence\":4,\"datatype\":{\"type\":\"boolean\",\"properties\":{\"defaultValue\":\"true\"},\"array\":false},\"value\":null,\"sqlId\":null,\"expressionId\":null,\"exprsnOutput\":false,\"sqlOutput\":false,\"children\":null},{\"description\":\"Example Parameter5. This is a date value\",\"mandatory\":true,\"syndicate\":false,\"name\":\"OutputParam5\",\"text\":\"OutputParam5<span class=\\\"mandatoryParam\\\">*</span>\",\"dataFormat\":null,\"size\":0,\"precision\":0,\"userSelected\":false,\"flatenedName\":\"OutputParam5\",\"sequence\":5,\"datatype\":{\"type\":\"date\",\"properties\":{\"defaultValue\":\"2002-09-24\"},\"array\":false},\"value\":null,\"sqlId\":null,\"expressionId\":null,\"exprsnOutput\":false,\"sqlOutput\":false,\"children\":null}], \"tidInput\" : [] },\"tidMidMapping\" : {\"outputMappingViews\":[{\"mappingParam\":\"OutputParam1\",\"mappedTo\":\"OutputParam1\"},{\"mappingParam\":\"OutputParam2\",\"mappedTo\":\"OutputParam2\"},{\"mappingParam\":\"OutputParam3\",\"mappedTo\":\"OutputParam3\"},{\"mappingParam\":\"OutputParam4\",\"mappedTo\":\"OutputParam4\"},{\"mappingParam\":\"OutputParam5\",\"mappedTo\":\"OutputParam5\"}]}}";
            KeyValuePair<String, List<ValidationError>> validations = new KeyValuePair<String, List<ValidationError>>();
            String type = "mappingType";
            BusinessException businessExcp = new BusinessException(BusinessExceptionCodes.BSE000049, new Object[] {});

            when(mappingDelegate.saveMappingDescription(Mockito.any(MappingDescriptor.class), Mockito.anyString(),
                             Mockito.anyString())).thenThrow(businessExcp);
            MvcResult mvcResult = mockMvc
                    .perform(
                            post("/mapping/saveMapping").param("mappingDescriptorJson", mappingDescriptorJson)
                            		.param("validate","validate").contentType(MediaType.APPLICATION_JSON)).andDo(print()).andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertNotNull(mockResponse);
            assertEquals("application/json;charset=UTF-8", mockResponse.getContentType());
            String responseBody = mockResponse.getContentAsString();
            RestResponse<MappingDescriptor> restResponse = gson.fromJson(responseBody,
                    new TypeToken<RestResponse<MappingDescriptor>>() {
                    }.getType());
            assertTrue(restResponse.isError());
            assertEquals(restResponse.getErrorCode(), BusinessExceptionCodes.BSE000049);
        } catch (Exception exp) {
            fail(exp.getMessage());
        }
    }
    
    @Test
    public final void testGetMappingStatus() {
        try {
            when(mappingDelegate.getMappingStatus(Mockito.anyString())).thenReturn(MappingStatus.FINALIZED.getMappingStatus());
            MvcResult mvcResult = mockMvc
                    .perform(post("/mapping/getMappingStatus").param("tidName", "test").contentType(MediaType.APPLICATION_JSON))
                    .andDo(print()).andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertNotNull(mockResponse);
            String responseBody = mockResponse.getContentAsString();
            RestResponse<String> restResponse = gson.fromJson(responseBody, new TypeToken<RestResponse<String>>() {
            }.getType());
            assertEquals(MappingStatus.FINALIZED.getMappingStatus(),restResponse.getResponse());
        } catch (Exception e) {
            // TODO Auto-generated catch block
        	LOGGER.error("Exception: ", e);
        }
    }
    
    @Test
    public final void testExceptionGetMappingStatus() {
        try {
            BusinessException businessExcp = new BusinessException(BusinessExceptionCodes.BSE000049, new Object[] {});
            when(mappingDelegate.getMappingStatus(Mockito.anyString())).thenThrow(businessExcp);
            MvcResult mvcResult = mockMvc
                    .perform(post("/mapping/getMappingStatus").param("tidName", "test").contentType(MediaType.APPLICATION_JSON))
                    .andDo(print()).andReturn();
            MockHttpServletResponse mockResponse = mvcResult.getResponse();
            assertNotNull(mockResponse);
            String responseBody = mockResponse.getContentAsString();
            RestResponse<String> restResponse = gson.fromJson(responseBody, new TypeToken<RestResponse<String>>() {
            }.getType());
            assertEquals(restResponse.getErrorCode(), BusinessExceptionCodes.BSE000049);
        } catch (Exception e) {
            // TODO Auto-generated catch block
        	LOGGER.error("Exception: ", e);
        }
    }

    private List<MappingHierarchyInfo> createMappingHierarchyInfo() {
        List<MappingHierarchyInfo> mappingHierarchyInfoList = new ArrayList<MappingHierarchyInfo>();

        MappingInfo mappingInfo = getMappingInfoForTest();
        MappingHierarchyInfo mappingHierarchyInfo = new MappingHierarchyInfo();
        mappingHierarchyInfo.setModelName("TestModel1");
        mappingHierarchyInfo.setMappingInfos(asList(mappingInfo));

        mappingHierarchyInfoList.add(mappingHierarchyInfo);

        return mappingHierarchyInfoList;

    }

    private MappingDescriptor getMappingDescriptorForTest() {
        MappingDescriptor mappingDescriptor = new MappingDescriptor();
        mappingDescriptor.setDescription("description");
        mappingDescriptor.setMidName("midName");
        mappingDescriptor.setMidTree(new MidIOInfo());
        mappingDescriptor.setModelName("modelName");
        mappingDescriptor.setQueryInputs(new HashMap<String, String>());
        mappingDescriptor.setTidMidMapping(new MappingViews());
        mappingDescriptor.setTidName("tidName");
        mappingDescriptor.setTidTree(new TidIOInfo());
        mappingDescriptor.setValidationErrors(new ArrayList<ValidationError>());
        return mappingDescriptor;
    }

    private void assertMappingDescriptorValues(MappingDescriptor mappingDescriptor) {
        assertEquals("description", mappingDescriptor.getDescription());
        assertEquals("midName", mappingDescriptor.getMidName());
        assertNotNull(mappingDescriptor.getMidTree());
        assertEquals("modelName", mappingDescriptor.getModelName());
        assertNotNull(mappingDescriptor.getQueryInputs());
        assertEquals("tidName", mappingDescriptor.getTidName());
        assertNotNull(mappingDescriptor.getTidTree());
        assertNotNull(mappingDescriptor.getValidationErrors());
    }

    private MappingInfo getMappingInfoForTest() {
        MappingInfo mappingInfo = new MappingInfo();

        mappingInfo.setActive(true);
        mappingInfo.setCreatedBy("createdBy");
        mappingInfo.setDescription("description");
        mappingInfo.setId("mappingInfoId");
        mappingInfo.setLastModifiedBy("lastModifiedBy");
        mappingInfo.setMappingData("mappingData");
        mappingInfo.setModel(new ModelInfo());
        mappingInfo.setModelName("modelName");
        mappingInfo.setName("mappingInfoName");
        mappingInfo.setUmgName("umgName");
        mappingInfo.setVersion(1);

        return mappingInfo;
    }

    private void assertMappingInfoValues(MappingInfo mappingInfo) {
        assertTrue(mappingInfo.isActive());
        assertEquals("createdBy", mappingInfo.getCreatedBy());
        assertEquals("description", mappingInfo.getDescription());
        assertEquals("mappingInfoId", mappingInfo.getId());
        assertEquals("lastModifiedBy", mappingInfo.getLastModifiedBy());
        assertEquals("mappingData", mappingInfo.getMappingData());
        assertNotNull(mappingInfo.getModel());
        assertEquals("modelName", mappingInfo.getModelName());
        assertEquals("mappingInfoName", mappingInfo.getName());
        assertEquals("umgName", mappingInfo.getUmgName());
        assertEquals(1, mappingInfo.getVersion());
    }

    private QueryLaunchInfo getQueryLaunchInfo() {
        QueryLaunchInfo queryLaunchInfo = new QueryLaunchInfo();
        queryLaunchInfo.setMidOutput(new HashMap<String, MidParamInfo>());
        queryLaunchInfo.setTidInput(new HashMap<String, TidParamInfo>());
        queryLaunchInfo.setTidName("tidName");
        queryLaunchInfo.setType("queryType");
        return queryLaunchInfo;
    }

    private void assertQueryLaunchInfo(QueryLaunchInfo queryLaunchInfo) {
        assertNotNull(queryLaunchInfo.getMidOutput());
        assertNotNull(queryLaunchInfo.getTidInput());
        assertEquals("tidName", queryLaunchInfo.getTidName());
        assertEquals("queryType", queryLaunchInfo.getType());

    }
}
