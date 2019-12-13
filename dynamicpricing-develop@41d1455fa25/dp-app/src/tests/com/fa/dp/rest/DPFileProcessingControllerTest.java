package com.fa.dp.rest;

import com.fa.dp.business.week0.delegate.DPFileProcessDelegate;
import com.fa.dp.business.week0.info.DashboardFilterInfo;
import com.fa.dp.core.cache.CacheManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author misprakh
 */


/*@SqlGroup({
		,

})*/
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
@SqlGroup({
		@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:create.sql"),
		@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:drop.sql")
})
public class DPFileProcessingControllerTest extends AbstractControllerTest{

	/*@Before
	@Sql("classpath:create.sql")
	public void setUp() {
	}

	@Sql("classpath:drop.sql")
	@AfterClass
	public static void afterClassSetup() {
	}*/

	@Inject
	private DPFileProcessDelegate dpFileProcessDelegate;

	@Test
	@WithMockUser(username = "test", password = "132018")
	public void getDashboardDetails_Week0() throws Exception {
		String uri = "/getDashboardDetails";
		String weekType = "week0";

		MvcResult result = mvc.perform(MockMvcRequestBuilders
								.get(uri)
								.accept(MediaType.APPLICATION_JSON)
								.contentType(MediaType.APPLICATION_JSON)
								.param("weekType" , weekType))
								.andReturn();

		int status = result.getResponse().getStatus();
		Assert.assertEquals(200, status);
	}


	@Test
	@WithMockUser(username = "test", password = "132018")
	public void getDashboardDetails_WeekN() throws Exception {
		String uri = "/getDashboardDetails";
		String weekType = "weekn";

		MvcResult result = mvc.perform(MockMvcRequestBuilders
								.get(uri)
								.accept(MediaType.APPLICATION_JSON)
								.contentType(MediaType.APPLICATION_JSON)
								.param("weekType" , weekType))
								.andReturn();

		int status = result.getResponse().getStatus();
		Assert.assertEquals(200, status);
	}

	/*@Test
	@WithMockUser(username = "test", password = "132018")
	public void getDashboardDetails_Error() throws Exception {
		String uri = "/getDashboardDetails";
		String weekType = "na";

		MvcResult result = mvc.perform(MockMvcRequestBuilders
								.get(uri)
								.accept(MediaType.APPLICATION_JSON)
								.contentType(MediaType.APPLICATION_JSON)
								.param("weekType" , weekType))
								.andReturn();

		String errorMessage = result.getResponse().getContentAsString();
		Assert.assertTrue(StringUtils.contains(errorMessage, "System Exception - Dashboard API  failed"));
	}*/

	@Test
	@WithMockUser(username = "test", password = "132018")
	public void getFilteredDashboardDetails() throws Exception {
		String uri = "/getFilteredDashboardDetails";
		List<String> statusList = Arrays.asList("SUCCESS");
		DashboardFilterInfo dashboardFilterInfo = new DashboardFilterInfo();
		dashboardFilterInfo.setFileName("Test.xlsx");
		dashboardFilterInfo.setFromDate(1567276200000L);
		dashboardFilterInfo.setStatus(statusList);
		dashboardFilterInfo.setToDate(1571123360543L);
		dashboardFilterInfo.setWeekType("weekn");

		String dashboardFilterInfoString = super.mapToJson(dashboardFilterInfo);

		MvcResult result = mvc.perform(MockMvcRequestBuilders
								.post(uri)
								.content(dashboardFilterInfoString)
								.contentType(MediaType.APPLICATION_JSON)
								.characterEncoding("utf-8"))
								.andReturn();

		int status = result.getResponse().getStatus();
		Assert.assertEquals(200, status);
	}

	@Test
	@WithMockUser(username = "test", password = "132018")
	public void getAssetDetails() throws Exception {
		String uri = "/getAssetDetails";
		String fileId = "3269d652-0353-4741-86e5-281184987ca2";
		String weekType = "week0";

		MvcResult result = mvc.perform(MockMvcRequestBuilders
								.get(uri)
								.accept(MediaType.APPLICATION_JSON)
								.contentType(MediaType.APPLICATION_JSON)
								.param("fileId" , fileId)
								.param("weekType" , weekType))
								.andReturn();

		int status = result.getResponse().getStatus();
		Assert.assertEquals(200, status);
	}


	@Test
	@WithMockUser(username = "test", password = "132018")
	public void getAssetDetails_Error() throws Exception {
		String uri = "/getAssetDetails";
		String fileId = "3269d652-0353-4741-86e5-2811849872";
		String weekType = "week0";

		MvcResult result = mvc.perform(MockMvcRequestBuilders
								.get(uri)
								.accept(MediaType.APPLICATION_JSON)
								.contentType(MediaType.APPLICATION_JSON)
								.param("fileId" , fileId)
								.param("weekType" , weekType))
								.andReturn();

		String errorMessage = result.getResponse().getContentAsString();
		Assert.assertTrue(StringUtils.contains(errorMessage, "System Exception"));
	}

	@Test
	@WithMockUser(username = "test", password = "132018")
	public void getReport() throws Exception {
		String uri = "/downloadReport";
		String fileId = "3269d652-0353-4741-86e5-281184987ca2";
		String type = "week0";

		MvcResult result = mvc.perform(MockMvcRequestBuilders
								.get(uri)
								.contentType(MediaType.APPLICATION_JSON)
								.param("fileId" , fileId)
								.param("type", type))
								.andReturn();

		int status = result.getResponse().getStatus();
		Assert.assertEquals(200, status);

	}

	@Test
	@WithMockUser(username = "test", password = "132018")
	public void getReport_Error() throws Exception {
		String uri = "/downloadReport";
		String fileId = "3269d652-0353-4741-86e5-2811849872";
		String type = "week0";

		MvcResult result = mvc.perform(MockMvcRequestBuilders
								.get(uri)
								.contentType(MediaType.APPLICATION_JSON)
								.param("fileId" , fileId)
								.param("type", type))
								.andReturn();

		String errorMessage = result.getResponse().getContentAsString();
		Assert.assertTrue(StringUtils.contains(errorMessage, "There is no data found in the file. Please verify."));

	}

	@Test
	@WithMockUser(username = "test", password = "132018")
	public void getWeekNAssetDetails() throws Exception {
		String uri = "/getWeekNAssetDetails";
		String fileId = "a5b1f490-97d3-45e4-86d2-b7df33bb6cdc";
		String type = "weekn";

		MvcResult result = mvc.perform(MockMvcRequestBuilders
								.get(uri)
								.contentType(MediaType.APPLICATION_JSON)
								.param("weekNId" , fileId)
								.param("WeekType", type))
								.andReturn();

		int status = result.getResponse().getStatus();
		Assert.assertEquals(200, status);
	}

	@Test
	@WithMockUser(username = "test", password = "132018")
	public void getWeekNAssetDetails_Error() throws Exception {
		String uri = "/getWeekNAssetDetails";
		String fileId = "3269d652-0353-4741-86e5-2811849872";
		String type = "weekn";

		MvcResult result = mvc.perform(MockMvcRequestBuilders
								.get(uri)
								.contentType(MediaType.APPLICATION_JSON)
								.param("weekNId" , fileId)
								.param("WeekType", type))
								.andReturn();

		String errorMessage = result.getResponse().getContentAsString();
		Assert.assertTrue(StringUtils.contains(errorMessage, "System Exception - Asset details  failed."));
	}
}