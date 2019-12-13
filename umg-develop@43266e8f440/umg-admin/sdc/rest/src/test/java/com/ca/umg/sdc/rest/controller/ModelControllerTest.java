package com.ca.umg.sdc.rest.controller;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
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

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.common.info.PageRecord;
import com.ca.umg.business.common.info.SearchOptions;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.mapping.info.ModelMappingInfo;
import com.ca.umg.business.model.bo.ModelBOImpl;
import com.ca.umg.business.model.delegate.ModelDelegate;
import com.ca.umg.business.model.info.ModelInfo;
import com.ca.umg.business.model.info.ModelLibraryHierarchyInfo;
import com.ca.umg.business.model.info.ModelLibraryInfo;
import com.ca.umg.business.util.ResourceLoader;
import com.ca.umg.sdc.rest.constants.ModelConstants;
import com.ca.umg.sdc.rest.utils.RestResponse;
import com.ca.umg.sdc.rest.utils.TestUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class ModelControllerTest extends BaseTest {
	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Inject
	private ModelController modelController;

	@Inject
	private ModelDelegate modelDelegate;

	private List<ModelLibraryInfo> dummyModelLibList;

	private List<ModelLibraryHierarchyInfo> dummyModelLibHierarchyList;

	private static final Gson gson = new GsonBuilder().create();

	private ModelInfo dummyModelInfo;

	private List<String> derivedModelList;

	private List<String> allModelList;

	private List<ModelInfo> dummyModelInfoList;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
		this.mockMvc = MockMvcBuilders.standaloneSetup(modelController).build();
		dummyModelLibList = buildModelLibraryInfoList();
		dummyModelLibHierarchyList = buildModelLibraryHierarchyInfoList();
		List<List<String>> resultList = buildDerivedModel();
		derivedModelList = resultList.get(0);
		allModelList = resultList.get(1);
		dummyModelInfoList = buildModelInfoList();
		dummyModelInfo = dummyModelInfoList.get(0);
	}

	@Test
	public final void testfindAllLibraries() {
		try {
			when(modelDelegate.findAllLibraries())
					.thenReturn(dummyModelLibList);
			MvcResult mvcResult = mockMvc
					.perform(post("/model/listAllModelLibs")).andDo(print())
					.andReturn();
			MockHttpServletResponse mockResponse = mvcResult.getResponse();
			assertNotNull(mockResponse);
			assertEquals("application/json;charset=UTF-8",
					mockResponse.getContentType());
			String responseBody = mockResponse.getContentAsString();
			RestResponse<List<ModelLibraryInfo>> restResponse = gson.fromJson(
					responseBody,
					new TypeToken<RestResponse<List<ModelLibraryInfo>>>() {
					}.getType());
			assertEquals(3, restResponse.getResponse().size());
			assertEquals("Model Libraries Fetched", restResponse.getMessage());
			verify(modelDelegate, times(1)).findAllLibraries();
		} catch (Exception exp) {
			fail(exp.getMessage());
		}
	}

	@Test
	public final void testfindEmptyLibraries() {
		try {
			when(modelDelegate.findAllLibraries()).thenReturn(null);
			MvcResult mvcResult = mockMvc
					.perform(post("/model/listAllModelLibs")).andDo(print())
					.andReturn();
			MockHttpServletResponse mockResponse = mvcResult.getResponse();
			assertNotNull(mockResponse);
			assertEquals("application/json;charset=UTF-8",
					mockResponse.getContentType());
			String responseBody = mockResponse.getContentAsString();
			RestResponse<List<ModelLibraryInfo>> restResponse = gson.fromJson(
					responseBody,
					new TypeToken<RestResponse<List<ModelLibraryInfo>>>() {
					}.getType());
			assertEquals(null, restResponse.getResponse());
			assertEquals("Model Libraries Fetched", restResponse.getMessage());
			verify(modelDelegate, times(1)).findAllLibraries();
		} catch (Exception exp) {
			fail(exp.getMessage());
		}
	}

	@Test
	public final void testExceptionFindAllLibraries() {
		try {
			when(modelDelegate.findAllLibraries()).thenThrow(
					new BusinessException(BusinessExceptionCodes.BSE000046,
							new Object[] { "test Exception" }));
			MvcResult mvcResult = mockMvc
					.perform(post("/model/listAllModelLibs")).andDo(print())
					.andReturn();
			MockHttpServletResponse mockResponse = mvcResult.getResponse();
			assertNotNull(mockResponse);
			assertEquals("application/json;charset=UTF-8",
					mockResponse.getContentType());
			String responseBody = mockResponse.getContentAsString();
			RestResponse<List<ModelLibraryInfo>> restResponse = gson.fromJson(
					responseBody,
					new TypeToken<RestResponse<List<ModelLibraryInfo>>>() {
					}.getType());
			assertNotNull(restResponse);
			assertEquals(null, restResponse.getResponse());
			verify(modelDelegate, times(1)).findAllLibraries();
			assertEquals(BusinessExceptionCodes.BSE000046,
					restResponse.getErrorCode());
		} catch (Exception exp) {
			fail(exp.getMessage());
		}
	}

	@Test
	public final void testfindAllLibraryHierarchyInfo() {
		try {
			when(modelDelegate.getModelLibraryHierarchyInfos()).thenReturn(
					dummyModelLibHierarchyList);
			MvcResult mvcResult = mockMvc
					.perform(post("/model/listAllModelLibHierarchy"))
					.andDo(print()).andReturn();
			MockHttpServletResponse mockResponse = mvcResult.getResponse();
			assertNotNull(mockResponse);
			assertEquals("application/json;charset=UTF-8",
					mockResponse.getContentType());
			String responseBody = mockResponse.getContentAsString();
			RestResponse<List<ModelLibraryHierarchyInfo>> restResponse = gson
					.fromJson(
							responseBody,
							new TypeToken<RestResponse<List<ModelLibraryHierarchyInfo>>>() {
							}.getType());
			assertEquals(3, restResponse.getResponse().size());
			assertEquals("Model Libraries Fetched", restResponse.getMessage());
			verify(modelDelegate, times(1)).getModelLibraryHierarchyInfos();
		} catch (Exception exp) {
			fail(exp.getMessage());
		}
	}

	@Test
	public final void testfindEmptyLibraryHierarchyInfo() {
		try {
			when(modelDelegate.getModelLibraryHierarchyInfos())
					.thenReturn(null);
			MvcResult mvcResult = mockMvc
					.perform(post("/model/listAllModelLibHierarchy"))
					.andDo(print()).andReturn();
			MockHttpServletResponse mockResponse = mvcResult.getResponse();
			assertNotNull(mockResponse);
			assertEquals("application/json;charset=UTF-8",
					mockResponse.getContentType());
			String responseBody = mockResponse.getContentAsString();
			RestResponse<List<ModelLibraryHierarchyInfo>> restResponse = gson
					.fromJson(
							responseBody,
							new TypeToken<RestResponse<List<ModelLibraryHierarchyInfo>>>() {
							}.getType());
			assertEquals(null, restResponse.getResponse());
			assertEquals("Model Libraries Fetched", restResponse.getMessage());
			verify(modelDelegate, times(1)).getModelLibraryHierarchyInfos();
		} catch (Exception exp) {
			fail(exp.getMessage());
		}
	}

	@Test
	public final void testExceptionFindAllLibraryHierarchyInfo() {
		try {
			when(modelDelegate.getModelLibraryHierarchyInfos()).thenThrow(
					new BusinessException(BusinessExceptionCodes.BSE000046,
							new Object[] { "test Exception" }));
			MvcResult mvcResult = mockMvc
					.perform(post("/model/listAllModelLibHierarchy"))
					.andDo(print()).andReturn();
			MockHttpServletResponse mockResponse = mvcResult.getResponse();
			assertNotNull(mockResponse);
			assertEquals("application/json;charset=UTF-8",
					mockResponse.getContentType());
			String responseBody = mockResponse.getContentAsString();
			RestResponse<List<ModelLibraryHierarchyInfo>> restResponse = gson
					.fromJson(
							responseBody,
							new TypeToken<RestResponse<List<ModelLibraryHierarchyInfo>>>() {
							}.getType());
			assertEquals(null, restResponse.getResponse());
			verify(modelDelegate, times(1)).getModelLibraryHierarchyInfos();
			assertEquals(BusinessExceptionCodes.BSE000046,
					restResponse.getErrorCode());
		} catch (Exception exp) {
			fail(exp.getMessage());
		}
	}

	@Test
	public void testResourceLoader() throws SystemException {
		ResourceLoader.getResource(ModelBOImpl.UMG_MODEL_SCHEMA);
	}

	@Test
	public void testDeleteModel() {
		String id = "1";
		try {
			doNothing().when(modelDelegate).deleteModel(any(String.class));
			MvcResult mvcResult = mockMvc
					.perform(
							delete("/model/deleteModel/{id}", id).contentType(
									MediaType.APPLICATION_JSON).param("id", id))
					.andDo(print()).andReturn();
			MockHttpServletResponse mockResponse = mvcResult.getResponse();
			assertEquals("application/json;charset=UTF-8",
					mockResponse.getContentType());
			String responseBody = mockResponse.getContentAsString();
			RestResponse<ModelInfo> restResponse = gson.fromJson(responseBody,
					new TypeToken<RestResponse<ModelInfo>>() {
					}.getType());
			assertNotNull(mockResponse);
			assertEquals("Model has been deleted", restResponse.getMessage());
			verify(modelDelegate, times(1)).deleteModel(id);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testFailDeleteModel() {
		String id = "2";
		BusinessException thrown = new BusinessException(
				BusinessExceptionCodes.BSE000032,
				new Object[] { "model does not exsit" });
		try {
			doThrow(thrown).when(modelDelegate).deleteModel(any(String.class));
			MvcResult mvcResult = mockMvc
					.perform(
							delete("/model/deleteModel/{id}", id).contentType(
									MediaType.APPLICATION_JSON).param("id", id))
					.andDo(print()).andReturn();
			MockHttpServletResponse mockResponse = mvcResult.getResponse();
			String responseBody = mockResponse.getContentAsString();
			RestResponse<ModelInfo> restResponse = gson.fromJson(responseBody,
					new TypeToken<RestResponse<ModelInfo>>() {
					}.getType());
			assertEquals(true, restResponse.isError());
			assertEquals(
					"An error occurred while deleting model details. Cause :: model does not exsit.",
					restResponse.getMessage());
			assertEquals(BusinessExceptionCodes.BSE000032,
					restResponse.getErrorCode());
			verify(modelDelegate, times(1)).deleteModel(id);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testFetchModelDetail() {
		String id = "1_0";
		try {
			when(modelDelegate.getModelDetails(any(String.class))).thenReturn(
					dummyModelInfo);
			MvcResult mvcResult = mockMvc
					.perform(
							get("/model/fetchModelDetail/{id}", id)
									.contentType(MediaType.APPLICATION_JSON))
					.andDo(print()).andReturn();
			MockHttpServletResponse mockResponse = mvcResult.getResponse();
			String responseBody = mockResponse.getContentAsString();
			RestResponse<ModelInfo> restResponse = gson.fromJson(responseBody,
					new TypeToken<RestResponse<ModelInfo>>() {
					}.getType());
			assertNotNull(restResponse);
			assertEquals(id, restResponse.getResponse().getId());
			verify(modelDelegate, times(1)).getModelDetails(id);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testFailFetchModelDetail() {
		String id = "2";
		try {
			when(modelDelegate.getModelDetails(any(String.class))).thenReturn(
					null);
			MvcResult mvcResult = mockMvc
					.perform(
							get("/model/fetchModelDetail/{id}", id)
									.contentType(MediaType.APPLICATION_JSON)
									.param("id", id)).andDo(print())
					.andReturn();
			MockHttpServletResponse mockResponse = mvcResult.getResponse();
			String responseBody = mockResponse.getContentAsString();
			RestResponse<ModelInfo> restResponse = gson.fromJson(responseBody,
					new TypeToken<RestResponse<ModelInfo>>() {
					}.getType());
			assertNotNull(restResponse);
			assertEquals(null, restResponse.getResponse());
			verify(modelDelegate, times(1)).getModelDetails(id);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testExceptionFetchModelDetail() {
		String id = "2";
		try {
			when(modelDelegate.getModelDetails(any(String.class))).thenThrow(
					new BusinessException(BusinessExceptionCodes.BSE000046,
							new Object[] { "Test exception" }));
			MvcResult mvcResult = mockMvc
					.perform(
							get("/model/fetchModelDetail/{id}", id)
									.contentType(MediaType.APPLICATION_JSON)
									.param("id", id)).andDo(print())
					.andReturn();
			MockHttpServletResponse mockResponse = mvcResult.getResponse();
			String responseBody = mockResponse.getContentAsString();
			RestResponse<ModelInfo> restResponse = gson.fromJson(responseBody,
					new TypeToken<RestResponse<ModelInfo>>() {
					}.getType());
			assertNotNull(restResponse);
			verify(modelDelegate, times(1)).getModelDetails(id);
			assertEquals(BusinessExceptionCodes.BSE000046,
					restResponse.getErrorCode());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetAllModelNames() {
		String id = "1";
		try {
			when(modelDelegate.getAllModelNames()).thenReturn(allModelList);
			MvcResult mvcResult = mockMvc
					.perform(get("/model/getAllModelNames")).andDo(print())
					.andReturn();
			MockHttpServletResponse mockResponse = mvcResult.getResponse();
			String responseBody = mockResponse.getContentAsString();
			RestResponse<List<String>> restResponse = gson.fromJson(
					responseBody, new TypeToken<RestResponse<List<String>>>() {
					}.getType());
			assertNotNull(restResponse);
			assertNotSame(id, restResponse.getResponse().get(0));
			assertEquals(id, restResponse.getResponse().get(0));
			verify(modelDelegate, times(1)).getAllModelNames();
		} catch (Exception exp) {
			fail(exp.getMessage());
		}
	}

	@Test
	public void testFailGetAllModelNames() {
		try {
			when(modelDelegate.getAllModelNames()).thenReturn(null);
			MvcResult mvcResult = mockMvc
					.perform(get("/model/getAllModelNames")).andDo(print())
					.andReturn();
			MockHttpServletResponse mockResponse = mvcResult.getResponse();
			String responseBody = mockResponse.getContentAsString();
			RestResponse<List<String>> restResponse = gson.fromJson(
					responseBody, new TypeToken<RestResponse<List<String>>>() {
					}.getType());
			assertNotNull(restResponse);
			assertEquals(null, restResponse.getResponse());
			verify(modelDelegate, times(1)).getAllModelNames();
		} catch (Exception exp) {
			fail(exp.getMessage());
		}
	}

	@Test
	public void testExceptionGetAllModelNames() {
		try {
			when(modelDelegate.getAllModelNames()).thenThrow(
					new BusinessException(BusinessExceptionCodes.BSE000046,
							new Object[] { "test Exception" }));
			MvcResult mvcResult = mockMvc
					.perform(get("/model/getAllModelNames")).andDo(print())
					.andReturn();
			MockHttpServletResponse mockResponse = mvcResult.getResponse();
			String responseBody = mockResponse.getContentAsString();
			RestResponse<List<String>> restResponse = gson.fromJson(
					responseBody, new TypeToken<RestResponse<List<String>>>() {
					}.getType());
			assertNotNull(restResponse);
			assertEquals(null, restResponse.getResponse());
			verify(modelDelegate, times(1)).getAllModelNames();
			assertEquals(BusinessExceptionCodes.BSE000046,
					restResponse.getErrorCode());
		} catch (Exception exp) {
			fail(exp.getMessage());
		}
	}

	@Test
	public final void testFetchModelLibraryDetail() {
		try {
			String id = "1234_0";
			ModelLibraryInfo modelLibraryInfo = dummyModelLibList.get(0);
			when(modelDelegate.getModelLibraryDetails(any(String.class)))
					.thenReturn(modelLibraryInfo);
			MvcResult mvcResult = mockMvc
					.perform(
							get("/model/fetchModelLibraryDetail").contentType(
									MediaType.APPLICATION_JSON).param("id", id))
					.andDo(print()).andReturn();
			MockHttpServletResponse mockResponse = mvcResult.getResponse();
			assertNotNull(mockResponse);
			String responseBody = mockResponse.getContentAsString();
			RestResponse<ModelLibraryInfo> restResponse = gson.fromJson(
					responseBody,
					new TypeToken<RestResponse<ModelLibraryInfo>>() {
					}.getType());
			assertNotNull(restResponse);
			assertEquals(id, restResponse.getResponse().getId());
			verify(modelDelegate, times(1)).getModelLibraryDetails(id);
		} catch (Exception exp) {
			fail(exp.getMessage());
		}
	}

	@Test
	public final void testFailFetchModelLibraryDetail() {
		try {
			String id = "1234_0";
			when(modelDelegate.getModelLibraryDetails(any(String.class)))
					.thenReturn(null);
			MvcResult mvcResult = mockMvc
					.perform(
							get("/model/fetchModelLibraryDetail").contentType(
									MediaType.APPLICATION_JSON).param("id", id))
					.andDo(print()).andReturn();
			MockHttpServletResponse mockResponse = mvcResult.getResponse();
			assertNotNull(mockResponse);
			String responseBody = mockResponse.getContentAsString();
			RestResponse<ModelLibraryInfo> restResponse = gson.fromJson(
					responseBody,
					new TypeToken<RestResponse<ModelLibraryInfo>>() {
					}.getType());
			assertNotNull(restResponse);
			assertNull(restResponse.getResponse());
			assertEquals(ModelConstants.MODEL_LIB_DETAILS_NOT_FOUND,
					restResponse.getMessage());
			verify(modelDelegate, times(1)).getModelLibraryDetails(id);
		} catch (Exception exp) {
			fail(exp.getMessage());
		}
	}

	@Test
	public final void testExceptionFetchModelLibraryDetail() {
		try {
			String id = "1234_0";
			when(modelDelegate.getModelLibraryDetails(any(String.class)))
					.thenThrow(
							new BusinessException(
									BusinessExceptionCodes.BSE000046,
									new Object[] { "Test exception" }));
			MvcResult mvcResult = mockMvc
					.perform(
							get("/model/fetchModelLibraryDetail").contentType(
									MediaType.APPLICATION_JSON).param("id", id))
					.andDo(print()).andReturn();
			MockHttpServletResponse mockResponse = mvcResult.getResponse();
			assertNotNull(mockResponse);
			String responseBody = mockResponse.getContentAsString();
			RestResponse<ModelLibraryInfo> restResponse = gson.fromJson(
					responseBody,
					new TypeToken<RestResponse<ModelLibraryInfo>>() {
					}.getType());
			assertNotNull(restResponse);
			assertNull(restResponse.getResponse());
			verify(modelDelegate, times(1)).getModelLibraryDetails(id);
			assertEquals(BusinessExceptionCodes.BSE000046,
					restResponse.getErrorCode());
		} catch (Exception exp) {
			fail(exp.getMessage());
		}
	}
	
	@Test
	public void listFilteredModelInfo() {
		PageRecord<ModelInfo> modelInfoPage = buildModelInfoPageRecord();
		SearchOptions searchOptions = buildSearchOptions(0, 5, "createdDate",
				false);
		try {
			when(modelDelegate.getUniqueModelInfos(searchOptions)).thenReturn(modelInfoPage);
			this.mockMvc
            .perform(
                    post("/model/listUniqueModelInfo").contentType(TestUtil.APPLICATION_JSON_UTF8).content(
                            TestUtil.convertObjectToJsonBytes(searchOptions))).andExpect(status().isOk())
            .andExpect(jsonPath("$.error", is(Boolean.FALSE))).andExpect(jsonPath("$.errorCode", nullValue()))
            .andExpect(jsonPath("$.message", is("Done"))).andExpect(jsonPath("$.response", nullValue()));
			
		} catch (Exception exp) {
			fail(exp.getMessage());
		}
	}

	@Test
	public void testGetAllVersionNamesForModel() {
		String id = "1_0";
		ModelMappingInfo modelMappingInfo = buildModelMappingInfo();
		try {
			when(modelDelegate.getAllVersionNamesForModel(any(String.class)))
					.thenReturn(modelMappingInfo);
			MvcResult mvcResult = mockMvc
					.perform(
							get("/model/getAllVersionNamesForModel/{id}", id)
									.contentType(MediaType.APPLICATION_JSON))
					.andDo(print()).andReturn();
			MockHttpServletResponse mockResponse = mvcResult.getResponse();
			String responseBody = mockResponse.getContentAsString();
			RestResponse<ModelMappingInfo> restResponse = gson.fromJson(
					responseBody,
					new TypeToken<RestResponse<ModelMappingInfo>>() {
					}.getType());
			assertNotNull(restResponse);
			assertNotNull(restResponse.getResponse().getMappingNameList());
			assertNotNull(restResponse.getResponse().getVersionNameList());
			verify(modelDelegate, times(1)).getAllVersionNamesForModel(id);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testFailGetAllVersionNamesForModel() {
		String id = "1_0";
		try {
			when(modelDelegate.getAllVersionNamesForModel(any(String.class)))
					.thenReturn(null);
			MvcResult mvcResult = mockMvc
					.perform(
							get("/model/getAllVersionNamesForModel/{id}", id)
									.contentType(MediaType.APPLICATION_JSON))
					.andDo(print()).andReturn();
			MockHttpServletResponse mockResponse = mvcResult.getResponse();
			String responseBody = mockResponse.getContentAsString();
			RestResponse<ModelMappingInfo> restResponse = gson.fromJson(
					responseBody,
					new TypeToken<RestResponse<ModelMappingInfo>>() {
					}.getType());
			assertNotNull(restResponse);
			assertNull(restResponse.getResponse());
			verify(modelDelegate, times(1)).getAllVersionNamesForModel(id);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testDeleteModelLibrary() {
		String id = "1";
		try {
			doNothing().when(modelDelegate).deleteModelLibrary(
					any(String.class));
			MvcResult mvcResult = mockMvc
					.perform(
							post("/model/deleteModelLibrary").contentType(
									MediaType.APPLICATION_JSON).param(
									"modelLibraryID", id)).andDo(print())
					.andReturn();
			MockHttpServletResponse mockResponse = mvcResult.getResponse();
			assertEquals("application/json;charset=UTF-8",
					mockResponse.getContentType());
			String responseBody = mockResponse.getContentAsString();
			RestResponse<String> restResponse = gson.fromJson(responseBody,
					new TypeToken<RestResponse<String>>() {
					}.getType());
			assertNotNull(mockResponse);
			assertEquals("Success", restResponse.getResponse());
			verify(modelDelegate, times(1)).deleteModelLibrary(id);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testFailDeleteModelLibrary() {
		String id = "2";
		BusinessException thrown = new BusinessException(
				BusinessExceptionCodes.BSE000046,
				new Object[] { "model library does not exist" });
		try {
			doThrow(thrown).when(modelDelegate).deleteModelLibrary(
					any(String.class));
			MvcResult mvcResult = mockMvc
					.perform(
							post("/model/deleteModelLibrary").contentType(
									MediaType.APPLICATION_JSON).param(
									"modelLibraryID", id)).andDo(print())
					.andReturn();
			MockHttpServletResponse mockResponse = mvcResult.getResponse();
			String responseBody = mockResponse.getContentAsString();
			RestResponse<String> restResponse = gson.fromJson(responseBody,
					new TypeToken<RestResponse<String>>() {
					}.getType());
			assertEquals(true, restResponse.isError());
			assertEquals(BusinessExceptionCodes.BSE000046,
					restResponse.getErrorCode());
			verify(modelDelegate, times(1)).deleteModelLibrary(id);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

}
