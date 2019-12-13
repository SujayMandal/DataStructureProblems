package com.fa.dp.rest;

import com.fa.dp.business.command.master.CommandMaster;
import com.fa.dp.business.sop.validator.delegate.DPSopFileDelegate;
import com.fa.dp.business.sop.week0.bo.DPSopProcessBOImpl;
import com.fa.dp.business.sop.week0.delegate.DPSopProcessFilterDelegate;
import com.fa.dp.business.sop.week0.delegate.DPSopProcessReportDelegate;
import com.fa.dp.business.sop.week0.input.info.DPSopParamEntryInfo;
import com.fa.dp.business.sop.week0.input.info.DPSopWeek0ParamInfo;
import com.fa.dp.business.sop.week0.input.info.DPSopWeek0ProcessStatusInfo;
import com.fa.dp.business.sop.week0.input.mapper.DPSopWeek0ProcessStatusMapper;
import com.fa.dp.core.systemparam.provider.SystemParameterProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author misprakh
 */
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
@Transactional
@SqlGroup({
		@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:sop_create.sql"),
		@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:drop.sql")
})
public class SOPWeek0ControllerTest extends AbstractControllerTest{

	/*@Before
	@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:sop_create.sql")
	public void setUp() {}

	@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:drop.sql")
	@AfterClass
	public static void afterClassSetup() {}*/

	@Inject
	@Named("dpCommandMaster")
	private CommandMaster commandMaster;

	@Inject
	private DPSopProcessFilterDelegate dpSopProcessFilterDelegate;

	@Inject
	private DPSopProcessBOImpl dpSopFileProcessBO;

	@Inject
	private DPSopWeek0ProcessStatusMapper dpSopWeek0ProcessStatusMapper;

	@Inject
	private DPSopFileDelegate dpSopFileDelegate;

	@Inject
	private DPSopProcessReportDelegate dpSopProcessReportDelegate;

	@Inject
	private SystemParameterProvider systemParameterProvider;

	@Test
	@WithMockUser(username = "test", password = "132018")
	public void processFile() throws Exception {
		String uri = "/processSopWeek0File";
		String inputJson2 = null;
		DPSopParamEntryInfo dpSopParamEntryInfo = new DPSopParamEntryInfo();
		DPSopWeek0ParamInfo paramInfo = new DPSopWeek0ParamInfo();
		paramInfo.setAssetNumber("989898889");
		List<DPSopWeek0ParamInfo> columnEntries = Arrays.asList(paramInfo);
		DPSopWeek0ProcessStatusInfo statusInfo  = new DPSopWeek0ProcessStatusInfo();
		statusInfo.setStatus("UPLOADED");
		statusInfo.setInputFileName("SOP-Wk0-DP-617-AV-test.xlsx");
		statusInfo.setInputFileName("SOP-Wk0-DP-617-AV-test-20190722-035438.xls");
		dpSopParamEntryInfo.setDpSopWeek0ProcessStatusInfo(statusInfo);
		dpSopParamEntryInfo.setColumnEntries(columnEntries);

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson=ow.writeValueAsString(dpSopParamEntryInfo );


		MvcResult result = mvc.perform(MockMvcRequestBuilders
				.post(uri)
				.content(requestJson.getBytes())
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.accept(MediaType.APPLICATION_JSON_UTF8)
				.param("fileId", inputJson2))
				.andReturn();

		String status = result.getResponse().getContentAsString();
		Assert.assertTrue(StringUtils.contains(status, "Processing of loans initiated. Check Dashboard for Status."));
	}


	@Test
	@WithMockUser(username = "test", password = "132018")
	public void processFile_Error() throws Exception {
		String uri = "/processSopWeek0File";
		String inputJson2 = null;
		DPSopParamEntryInfo dpSopParamEntryInfo = new DPSopParamEntryInfo();
		DPSopWeek0ParamInfo paramInfo = new DPSopWeek0ParamInfo();
		paramInfo.setAssetNumber("989898889");
		List<DPSopWeek0ParamInfo> columnEntries = null;
		DPSopWeek0ProcessStatusInfo statusInfo  = new DPSopWeek0ProcessStatusInfo();
		dpSopParamEntryInfo.setDpSopWeek0ProcessStatusInfo(statusInfo);
		dpSopParamEntryInfo.setColumnEntries(columnEntries);

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson=ow.writeValueAsString(dpSopParamEntryInfo );


		MvcResult result = mvc.perform(MockMvcRequestBuilders
				.post(uri)
				.content(requestJson.getBytes())
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.accept(MediaType.APPLICATION_JSON_UTF8)
				.param("fileId", inputJson2))
				.andReturn();

		String status = result.getResponse().getContentAsString();
		Assert.assertTrue(StringUtils.contains(status, "Exception occurred while processing Week0 input file"));
	}

	/*@Test
	public void uploadFile() {
	}*/

	@Test
	@WithMockUser(username = "test", password = "132018")
	public void getSOPWeek0Report() throws Exception {
		String uri = "/downloadSOPWeek0Report";
		String fileId = "042d904d-d83e-4fd8-be7f-5a6c14fe91fa";
		String type = "sopWeek0";

		MvcResult result = mvc.perform(MockMvcRequestBuilders
								.get(uri)
								.contentType(MediaType.APPLICATION_JSON)
								.param("fileId" , fileId)
								.param("type", type))
								.andReturn();

		int status = result.getResponse().getStatus();
		Assert.assertEquals("200", status);
	}

	@Test
	@WithMockUser(username = "test", password = "132018")
	public void getSOPWeek0Report_Error() throws Exception {
		String uri = "/downloadSOPWeek0Report";
		String fileId = null;
		String type = "sopWeek0";

		MvcResult result = mvc.perform(MockMvcRequestBuilders
				.get(uri)
				.contentType(MediaType.APPLICATION_JSON)
				.param("fileId" , fileId)
				.param("type", type))
				.andReturn();

		String status = result.getResponse().getContentAsString();
		Assert.assertTrue(StringUtils.contains(status, "There is no data found in the file. Please verify."));
	}

	@Test
	@WithMockUser(username = "test", password = "132018")
	public void getSopWeek0AssetDetails() throws Exception {
		String uri = "/getSopWeek0AssetDetails";
		String fileId = "042d904d-d83e-4fd8-be7f-5a6c14fe91fa";
		String type = "sopWeek0";

		MvcResult result = mvc.perform(MockMvcRequestBuilders
				.get(uri)
				.contentType(MediaType.APPLICATION_JSON)
				.param("fileId" , fileId)
				.param("weekType", type))
				.andReturn();

		String status = result.getResponse().getContentAsString();
		Assert.assertTrue(StringUtils.contains(status, "SOP Week 0 Asset details  successful"));
	}


	@Test
	@WithMockUser(username = "test", password = "132018")
	public void getSopWeek0AssetDetails_Error() throws Exception {
		String uri = "/getSopWeek0AssetDetails";
		String fileId = null;
		String type = "sopWeek0";

		MvcResult result = mvc.perform(MockMvcRequestBuilders
				.get(uri)
				.contentType(MediaType.APPLICATION_JSON)
				.param("fileId" , fileId)
				.param("weekType", type))
				.andReturn();

		String status = result.getResponse().getContentAsString();
		Assert.assertTrue(StringUtils.contains(status, "System Exception - SOP Week 0 Asset details  failed."));
	}
}