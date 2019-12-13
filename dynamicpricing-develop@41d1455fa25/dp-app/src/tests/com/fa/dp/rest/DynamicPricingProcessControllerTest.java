package com.fa.dp.rest;

import com.fa.dp.business.filter.delegate.DPProcessWeekNFilterDelegate;
import com.fa.dp.business.validation.input.info.DPFileProcessStatusInfo;
import com.fa.dp.business.validation.input.info.DPProcessParamEntryInfo;
import com.fa.dp.business.validation.input.info.DPProcessParamInfo;
import com.fa.dp.business.validator.delegate.DPProcessDelegate;
import com.fa.dp.business.week0.delegate.DPFileProcessDelegate;
import com.fa.dp.business.weekn.delegate.WeekNDataDelegate;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamEntryInfo;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamInfo;
import com.fa.dp.business.weekn.input.info.DPWeekNProcessStatusInfo;
import com.fa.dp.core.systemparam.provider.SystemParameterProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.StringUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import javax.inject.Inject;
import java.io.FileInputStream;
import java.util.Arrays;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author misprakh
 */
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
@Transactional
@SqlGroup({
		@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:create.sql"),
		@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:drop.sql")
})
public class DynamicPricingProcessControllerTest extends AbstractControllerTest {

	/*@Before
	@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:create.sql")
	public void setUp() {}

	@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:drop.sql")
	@AfterClass
	public static void afterClassSetup() {}*/

	@Inject
	private WeekNDataDelegate weekNDataDelegate;

	@Inject
	private DPProcessDelegate dPProcessDelegate;

	@Inject
	private DPFileProcessDelegate dpFileProcessDelegate;

	@Inject
	private DPProcessWeekNFilterDelegate dpProcessWeekNFilterDelegate;

	@Inject
	private SystemParameterProvider systemParameterProvider;


	@Test
	@WithMockUser(username = "test", password = "132018")
	public void uploadFile_Error() throws Exception {
		String sharedFolderPath = "D:/GitCodeBase/DynPric-Prakhar/InputExcels/Vacant/Week0/";
		String uri = "/uploadFile";
		FileInputStream fis = new FileInputStream(sharedFolderPath+"DP-382-1.xlsx");
		MockMultipartFile mockMultipartFile = new MockMultipartFile("file", fis );

		MvcResult result = mvc.perform(MockMvcRequestBuilders.multipart(uri).file("file", mockMultipartFile.getBytes()).characterEncoding("UTF-8")).andExpect(status().isOk())
								.andReturn();
		Assert.assertTrue(StringUtils.contains(result.getResponse().getContentAsString(), "File Upload failed. Only Excel format supported"));
		//Assert.assertEquals("DP-382-1.xlsx", result.getResponse().getContentAsString());
	}

	/*@Test
	@WithMockUser(username = "test", password = "132018")
	public void uploadFile() throws Exception {
		String sharedFolderPath = "D:/GitCodeBase/DynPric-Prakhar/InputExcels/Vacant/Week0/";
		String uri = "/uploadFile";
		String fileName = "DP-382-1.xlsx";
		FileInputStream fis = new FileInputStream(sharedFolderPath+fileName);
		MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "DP-382-1.xlsx", "text/plain", fis );

		MvcResult result = mvc
				.perform(MockMvcRequestBuilders.multipart(uri)
						.file(mockMultipartFile)
						.content(mockMultipartFile.getBytes())
						.characterEncoding("UTF-8"))
				.andExpect(status().isOk())
				.andReturn();
		Assert.assertTrue(StringUtils.contains(  result.getResponse().getContentAsString(), "File Upload failed. Only Excel format supported"));
		//Assert.assertEquals("DP-382-1.xlsx", result.getResponse().getContentAsString());
	}*/

	@Test
	@WithMockUser(username = "test", password = "132018")
	public void processFile() throws Exception {
		String uri = "/processFile";
		// Preparing File data
		DPFileProcessStatusInfo statusInfo = new DPFileProcessStatusInfo();
		DPProcessParamInfo paramInfo = new DPProcessParamInfo();
		paramInfo.setDynamicPricingFilePrcsStatus(statusInfo);

		DPProcessParamEntryInfo dpProcessParamEntryInfo = new DPProcessParamEntryInfo();
		dpProcessParamEntryInfo.setColumnEntries(Arrays.asList(paramInfo));
		dpProcessParamEntryInfo.setDPFileProcessStatusInfo(statusInfo);
		String inputJson2 = "3269d652-0353-4741-86e5-281184987ca2";

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson=ow.writeValueAsString(dpProcessParamEntryInfo );


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
		String uri = "/processFile";
		// Preparing File data
		DPFileProcessStatusInfo statusInfo = new DPFileProcessStatusInfo();

		DPProcessParamEntryInfo dpProcessParamEntryInfo = new DPProcessParamEntryInfo();
		dpProcessParamEntryInfo.setDPFileProcessStatusInfo(statusInfo);
		String inputJson2 = null;

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson=ow.writeValueAsString(dpProcessParamEntryInfo );


		MvcResult result = mvc.perform(MockMvcRequestBuilders
				.post(uri)
				.content(requestJson.getBytes())
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.accept(MediaType.APPLICATION_JSON_UTF8)
				.param("fileId", inputJson2))
				.andReturn();

		String status = result.getResponse().getContentAsString();
		Assert.assertTrue(StringUtils.contains(status, "Exception occurred while processing"));
	}


	@Test
	@WithMockUser(username = "test", password = "132018")
	public void processWeekN() throws Exception {
		String uri = "/processWeekN";
		DPProcessWeekNParamEntryInfo dpWeeknParamEntry = new DPProcessWeekNParamEntryInfo();
		// Preparing File data
		DPWeekNProcessStatusInfo statusInfo = new DPWeekNProcessStatusInfo();
		// Preparing Week0 data
		DPProcessWeekNParamInfo paramInfo = new DPProcessWeekNParamInfo();
		dpWeeknParamEntry.setColumnEntries(Arrays.asList(paramInfo));
		dpWeeknParamEntry.setDpWeeknProcessStatus(statusInfo);

		String dpParamEntry = super.mapToJson(dpWeeknParamEntry);
		String inputJson2 = "5069ea4d-c7ed-4cd1-b583-eb70fd32f997";

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson=ow.writeValueAsString(dpWeeknParamEntry );


		MvcResult result = mvc.perform(MockMvcRequestBuilders
								.post(uri)
								.contentType(MediaType.APPLICATION_JSON)
								.content(dpParamEntry)
								.accept(MediaType.APPLICATION_JSON_UTF8)
								.param("fileId", inputJson2))
								.andReturn();

		int status = result.getResponse().getStatus();
		Assert.assertEquals(200, status);
	}

}