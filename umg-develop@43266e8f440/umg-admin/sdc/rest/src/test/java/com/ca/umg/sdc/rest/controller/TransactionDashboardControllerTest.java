package com.ca.umg.sdc.rest.controller;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.zip.ZipOutputStream;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.transaction.delegate.TransactionDelegate;
import com.ca.umg.business.transaction.info.AdvanceTransactionFilter;
import com.ca.umg.business.transaction.info.TransactionFilter;
import com.ca.umg.business.transaction.info.TransactionInfo;
import com.ca.umg.business.transaction.info.TransactionWrapper;
import com.ca.umg.business.transaction.mongo.entity.TransactionDocument;
import com.ca.umg.business.transaction.util.TransactionUtil;
import com.ca.umg.sdc.rest.constants.RestConstants;
import com.ca.umg.sdc.rest.utils.ByteArrayAdapter;
import com.ca.umg.sdc.rest.utils.DateTimeTypeConverter;
import com.ca.umg.sdc.rest.utils.RestResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

//@ContextConfiguration
@ContextHierarchy({ @ContextConfiguration("classpath:root-db-context.xml"), @ContextConfiguration })
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)

// TODO fix ignored test cases
public class TransactionDashboardControllerTest {

	private Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeConverter())
			.registerTypeHierarchyAdapter(byte[].class, new ByteArrayAdapter()).create();

	private static final String DATA_FILE = "./src/test/resources/testdata/MSA_HPI_factor.csv";

	@Inject
	private TransactionDelegate mockTransactionDelegate;

	@Inject
	private TransactionDashboardController controller;

	@Autowired
	private WebApplicationContext ctx;

	private MockMvc mockMvc;

	private TransactionWrapper txnWrapper;

	private Long currentTime = System.currentTimeMillis();

	@Mock
	private TransactionDelegate transactionDelegate;

	@Before
	public void setUp() {
		initMocks(this);
		mockMvc = webAppContextSetup(ctx).build();
		mockMvc = standaloneSetup(controller).build();
	}

	@Ignore
	public void testFindAllTransactions() throws Exception {
		txnWrapper = buildTransactionResponse();
		TransactionFilter transactionFilter = new TransactionFilter();
		transactionFilter.setPage(1);
		transactionFilter.setPageSize(100);
		when(mockTransactionDelegate.searchTransactions(Mockito.any(TransactionFilter.class),
				Mockito.any(AdvanceTransactionFilter.class))).thenReturn(txnWrapper);
		MvcResult mvcResult = mockMvc.perform(post("/txnDashBoard/listAll")
				.param("txnFilterData", "{\"clientTransactionID\":\"\"}")
				.param("advanceTransactionFilter", "{\"criteria\":\"\"}").content(gson.toJson(transactionFilter)))
				.andDo(print()).andReturn();
		MockHttpServletResponse mockResponse = mvcResult.getResponse();
		RestResponse<TransactionWrapper> restResponse = gson.fromJson(mockResponse.getContentAsString(),
				new TypeToken<RestResponse<TransactionWrapper>>() {
				}.getType());
		assertThat(mockResponse, notNullValue());
		assertThat(mockResponse.getContentType(), is("application/json;charset=UTF-8"));
		// assertThat(restResponse.getResponse().getTransactionInfoList().size(),
		// is(2));
		assertThat(restResponse.isError(), is(false));
		assertThat(restResponse.getErrorCode(), nullValue());
	}

	@Ignore
	public void testListAllWE() throws Exception {
		TransactionFilter transactionFilter = new TransactionFilter();
		transactionFilter.setPage(1);
		transactionFilter.setPageSize(100);
		when(mockTransactionDelegate.searchTransactions(Mockito.any(TransactionFilter.class),
				Mockito.any(AdvanceTransactionFilter.class)))
						.thenThrow(new SystemException(BusinessExceptionCodes.BSE000001, new Object[] {}));
		this.mockMvc
				.perform(post("/txnDashBoard/listAll").param("txnFilterData", "{\"clientTransactionID\":\"\"}")
						.param("advanceTransactionFilter", "{\"criteria\":\"\"}")
						.content(gson.toJson(transactionFilter)))
				.andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.error", is(Boolean.TRUE)))
				.andExpect(jsonPath("$.errorCode", is("BSE000001"))).andExpect(jsonPath("$.message", notNullValue()))
				.andExpect(jsonPath("$.response", nullValue()));
	}

	@Ignore
	public void testFindAllTransactionsHavingZeroRecords() throws Exception {
		txnWrapper = new TransactionWrapper();
		TransactionFilter transactionFilter = new TransactionFilter();
		transactionFilter.setPage(1);
		transactionFilter.setPageSize(100);
		txnWrapper.setTransactionInfoList(new ArrayList<TransactionInfo>());
		when(mockTransactionDelegate.searchTransactions(Mockito.any(TransactionFilter.class),
				Mockito.any(AdvanceTransactionFilter.class))).thenReturn(txnWrapper);
		MvcResult mvcResult = mockMvc.perform(post("/txnDashBoard/listAll")
				.param("txnFilterData", "{\"clientTransactionID\":\"\"}")
				.param("advanceTransactionFilter", "{\"criteria\":\"\"}").content(gson.toJson(transactionFilter)))
				.andDo(print()).andReturn();
		MockHttpServletResponse mockResponse = mvcResult.getResponse();
		RestResponse<TransactionWrapper> restResponse = gson.fromJson(mockResponse.getContentAsString(),
				new TypeToken<RestResponse<TransactionWrapper>>() {
				}.getType());
		assertThat(mockResponse, notNullValue());
		assertThat(mockResponse.getContentType(), is("application/json;charset=UTF-8"));
		assertThat(restResponse.getResponse().getTransactionInfoList().size(), is(0));
		assertThat(restResponse.isError(), is(false));
		assertThat(restResponse.getErrorCode(), nullValue());
		assertThat(restResponse.getMessage(), is(RestConstants.NO_TRANSACTION_RECORDS_FOUND));
	}

	@Test
	public void testDownloadTenantIOData() throws Exception {
		when(mockTransactionDelegate.getTxnDocument("1")).thenReturn(createTransactionDocData("1", "TestTxn-1",
				"TestVersionName", "TestLibName", 1, 2, "Success", currentTime, readFile(DATA_FILE)));
		MvcResult mvcResult = mockMvc
				.perform(get("/txnDashBoard/downloadTenantIO/1").contentType(MediaType.APPLICATION_JSON)).andDo(print())
				.andReturn();
		MockHttpServletResponse mockResponse = mvcResult.getResponse();
		assertThat(mockResponse, notNullValue());
		if (mockResponse.getContentType() != null) {
			assertThat(mockResponse.getContentType(), is("application/zip"));
		}
		assertThat(mockResponse.getContentAsString(), notNullValue());
	}

	@Test
	public void testDownloadTenantIODataWE() throws Exception {
		when(mockTransactionDelegate.getTxnDocument("1"))
				.thenThrow(new SystemException(BusinessExceptionCodes.BSE000001, new Object[] {}));
		this.mockMvc.perform(get("/txnDashBoard/downloadTenantIO/{txnId}", 1).accept(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isOk());
	}

	@Test
	public void testDownloadModelIODataWE() throws Exception {
		when(mockTransactionDelegate.getTxnDocument("1"))
				.thenThrow(new SystemException(BusinessExceptionCodes.BSE000001, new Object[] {}));
		this.mockMvc.perform(get("/txnDashBoard/downloadModelIO/{txnId}/{transactionMode}", 1, "online")
				.accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isOk());
	}

	@Test
	public void testdownloadModelIOData() throws Exception {

		when(mockTransactionDelegate.getTxnDocument("1")).thenReturn(createTransactionDocData("1", "TestTxn-1",
				"TestVersionName", "TestLibName", 1, 2, "Success", currentTime, readFile(DATA_FILE)));
		MvcResult mvcResult = mockMvc
				.perform(get("/txnDashBoard/downloadModelIO/1").contentType(MediaType.APPLICATION_JSON)).andDo(print())
				.andReturn();
		MockHttpServletResponse mockResponse = mvcResult.getResponse();
		assertThat(mockResponse, notNullValue());
		if (mockResponse.getContentType() != null) {
			assertThat(mockResponse.getContentType(), is("application/zip"));
		}
		assertThat(mockResponse.getContentAsString(), notNullValue());

	}

	@Ignore
	public void testDownloadtSelectedItems() throws Exception {

		when(mockTransactionDelegate.getTxnDocument("1")).thenReturn(createTransactionDocData("1", "TestTxn-1",
				"TestVersionName", "TestLibName", 1, 2, "Success", currentTime, readFile(DATA_FILE)));
		when(mockTransactionDelegate.getTxnDocument("1")).thenReturn(createTransactionDocData("1", "TestTxn-1",
				"TestVersionName", "TestLibName", 1, 2, "Success", currentTime, readFile(DATA_FILE)));
		when(mockTransactionDelegate.getTxnDocument("2")).thenReturn(createTransactionDocData("1", "TestTxn-1",
				"TestVersionName", "TestLibName", 1, 2, "Success", currentTime, readFile(DATA_FILE)));
		when(mockTransactionDelegate.getTxnDocument("2")).thenReturn(createTransactionDocData("1", "TestTxn-1",
				"TestVersionName", "TestLibName", 1, 2, "Success", currentTime, readFile(DATA_FILE)));
		MvcResult mvcResult = mockMvc
				.perform(get("/txnDashBoard/downloadSelectedItems?idList=1,2").contentType(MediaType.APPLICATION_JSON))
				.andDo(print()).andReturn();
		MockHttpServletResponse mockResponse = mvcResult.getResponse();
		assertThat(mockResponse, notNullValue());
		assertThat(mockResponse.getContentType(), is("application/zip"));

		assertThat(mockResponse.getContentAsString(), notNullValue());

	}

	@Ignore
	public void testDownloadSelectedItems() throws Exception {
		when(mockTransactionDelegate.getTxnDocument("1"))
				.thenThrow(new SystemException(BusinessExceptionCodes.BSE000001, new Object[] {}));
		this.mockMvc
				.perform(get("/txnDashBoard/downloadSelectedItems?idList=1,2", 1).accept(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isOk());
	}

	private File getZipFile(TransactionInfo transactionInfo, String tenantOrModel)
			throws FileNotFoundException, IOException, SystemException {
		String zipFileName = null;
		String inputFileName = null;
		String outputFileName = null;
		if (StringUtils.equals(tenantOrModel, "Model")) {
			zipFileName = TransactionUtil.getFileName(transactionInfo.getClientTransactionID(),
					transactionInfo.getRunAsOfDate().getMillis(), BusinessConstants.MODEL_IO);
			inputFileName = TransactionUtil.getFileName(transactionInfo.getClientTransactionID(),
					transactionInfo.getRunAsOfDate().getMillis(), BusinessConstants.MODEL_IP);
			outputFileName = TransactionUtil.getFileName(transactionInfo.getClientTransactionID(),
					transactionInfo.getRunAsOfDate().getMillis(), BusinessConstants.MODEL_OP);
		} else {
			zipFileName = TransactionUtil.getFileName(transactionInfo.getClientTransactionID(),
					transactionInfo.getRunAsOfDate().getMillis(), BusinessConstants.TENANT_IO);
			inputFileName = TransactionUtil.getFileName(transactionInfo.getClientTransactionID(),
					transactionInfo.getRunAsOfDate().getMillis(), BusinessConstants.TENANT_IP);
			outputFileName = TransactionUtil.getFileName(transactionInfo.getClientTransactionID(),
					transactionInfo.getRunAsOfDate().getMillis(), BusinessConstants.TENANT_OP);
		}
		File zipFile = new File(zipFileName);
		FileOutputStream fos = new FileOutputStream(zipFile);
		writeDatatoZipFile(inputFileName, outputFileName, fos);
		return zipFile;
	}

	private void writeDatatoZipFile(String inputFileName, String outputFileName,
			FileOutputStream fos) throws FileNotFoundException, IOException, SystemException {
		ZipOutputStream zos = null;
		try {
			zos = new ZipOutputStream(fos);
			TransactionUtil.addToZipFile(inputFileName, readFile(DATA_FILE), zos);
			TransactionUtil.addToZipFile(outputFileName, readFile(DATA_FILE), zos);
		} finally {
			if(zos!=null) {
				zos.finish();
				zos.close();
			}
           if(fos !=null) {
        	   fos.flush();
        	   fos.close();
			}
		}
	}

	private File getGroupZipFile(TransactionInfo transactionInfo1, TransactionInfo transactionInfo2)
			throws FileNotFoundException, IOException, SystemException {
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		File zipFile = null;
		try {

			String modelInputFileName1 = TransactionUtil.getFileName(transactionInfo1.getClientTransactionID(),
					transactionInfo1.getRunAsOfDate().getMillis(), BusinessConstants.MODEL_IP);
			String modelOoutputFileName1 = TransactionUtil.getFileName(transactionInfo1.getClientTransactionID(),
					transactionInfo1.getRunAsOfDate().getMillis(), BusinessConstants.MODEL_OP);

			String tenantInputFileName1 = TransactionUtil.getFileName(transactionInfo1.getClientTransactionID(),
					transactionInfo1.getRunAsOfDate().getMillis(), BusinessConstants.TENANT_IP);
			String tenantOutputFileName1 = TransactionUtil.getFileName(transactionInfo1.getClientTransactionID(),
					transactionInfo1.getRunAsOfDate().getMillis(), BusinessConstants.TENANT_OP);

			String modelInputFileName2 = TransactionUtil.getFileName(transactionInfo2.getClientTransactionID(),
					transactionInfo2.getRunAsOfDate().getMillis(), BusinessConstants.MODEL_IP);
			String modelOutputFileName2 = TransactionUtil.getFileName(transactionInfo2.getClientTransactionID(),
					transactionInfo2.getRunAsOfDate().getMillis(), BusinessConstants.MODEL_OP);

			String tenantInputFileName2 = TransactionUtil.getFileName(transactionInfo2.getClientTransactionID(),
					transactionInfo2.getRunAsOfDate().getMillis(), BusinessConstants.TENANT_IP);
			String tenantOutputFileName2 = TransactionUtil.getFileName(transactionInfo2.getClientTransactionID(),
					transactionInfo2.getRunAsOfDate().getMillis(), BusinessConstants.TENANT_OP);

		    zipFile = new File(BusinessConstants.TENANT_OR_MODEL_IO);
			fos = new FileOutputStream(zipFile);

			zos = new ZipOutputStream(new FileOutputStream(new File(BusinessConstants.TENANT_OR_MODEL_IO)));
			TransactionUtil.addToZipFile(tenantInputFileName1, readFile(DATA_FILE), zos);
			TransactionUtil.addToZipFile(tenantOutputFileName1, readFile(DATA_FILE), zos);
			TransactionUtil.addToZipFile(modelInputFileName1, readFile(DATA_FILE), zos);
			TransactionUtil.addToZipFile(modelOoutputFileName1, readFile(DATA_FILE), zos);
			TransactionUtil.addToZipFile(tenantInputFileName2, readFile(DATA_FILE), zos);
			TransactionUtil.addToZipFile(tenantOutputFileName2, readFile(DATA_FILE), zos);
			TransactionUtil.addToZipFile(modelInputFileName2, readFile(DATA_FILE), zos);
			TransactionUtil.addToZipFile(modelOutputFileName2, readFile(DATA_FILE), zos);
		} finally {
			if(zos!=null) {
				zos.finish();
				zos.close();
			}
           if(fos !=null) {
        	   fos.flush();
        	   fos.close();
			}
		}
		return zipFile;
	}

	byte[] readFile(String fileName) throws IOException {
		Path path = Paths.get(fileName);
		return Files.readAllBytes(path);
	}

	private TransactionWrapper buildTransactionResponse() throws IOException {
		TransactionWrapper transactionWrapper = new TransactionWrapper();
		transactionWrapper.setTransactionInfoList(new ArrayList<TransactionInfo>());
		// transactionWrapper.setTransactionInfoList(buildTransactionInfoList());
		transactionWrapper.setLibraryNameList(new ArrayList<String>());
		transactionWrapper.setTenantModelNameList(new ArrayList<String>());
		return transactionWrapper;
	}

	/*
	 * private List<TransactionInfo> buildTransactionInfoList() throws IOException {
	 * List<TransactionInfo> txnInfoList = new ArrayList<TransactionInfo>(); for
	 * (int i = 1; i <= 2; i++) { TransactionInfo transactionInfo =
	 * createTransactionData(String.valueOf(i), "TestTxn-" + i, "TestVersionName",
	 * "TestLibName", 1, 2, "Success", JUL_31_2014, readFile(DATA_FILE));
	 * txnInfoList.add(transactionInfo); } return txnInfoList; }
	 */

	private TransactionInfo createTransactionData(String txnId, String clientTxnId, String tenantModelName,
			String libName, Integer majorVersion, Integer minorVersion, String status, Long runAsOfDate, byte[] data) {
		TransactionInfo txn = new TransactionInfo();
		txn.setId(txnId);
		txn.setLibraryName(libName);
		txn.setTenantModelName(tenantModelName);
		txn.setClientTransactionID(clientTxnId);
		txn.setMajorVersion(majorVersion);
		txn.setMinorVersion(minorVersion);
		txn.setRunAsOfDate(new DateTime(runAsOfDate.longValue()));
		txn.setStatus(status);
		txn.setModelIp(data);
		txn.setModelOp(data);
		txn.setTenantIp(data);
		txn.setTenantOp(data);
		txn.setModelCallEnd(1409232269322l);
		txn.setModelCallStart(1409232269322l);
		txn.setRuntimeCallEnd(1409232269322l);
		txn.setRuntimeCallStart(1409232269322l);

		return txn;
	}

	private TransactionDocument createTransactionDocData(String txnId, String clientTxnId, String tenantModelName,
			String libName, Integer majorVersion, Integer minorVersion, String status, Long runAsOfDate, byte[] data) {
		TransactionDocument txn = new TransactionDocument();
		txn.setClientTransactionID(txnId);
		txn.setLibraryName(libName);
		txn.setVersionName(tenantModelName);
		txn.setClientTransactionID(clientTxnId);
		txn.setMajorVersion(majorVersion);
		txn.setMinorVersion(minorVersion);
		txn.setRunAsOfDate(currentTime);
		txn.setStatus(status);
		txn.setModelCallEnd(1409232269322l);
		txn.setModelCallStart(1409232269322l);
		txn.setRuntimeCallEnd(1409232269322l);
		txn.setRuntimeCallStart(1409232269322l);

		return txn;
	}

	@Test
	public void testdownloadExeReportByFilter() throws SystemException {
		String txnFilterDataJson = "";
		String advanceTransactionFilterJson = "";
		TransactionFilter txnFilterData = null;
		AdvanceTransactionFilter advanceTransactionFilter = null;
		// txnFilterData = ConversionUtil.convertJson(txnFilterDataJson,
		// TransactionFilter.class);
		// advanceTransactionFilter =
		// ConversionUtil.convertJson(advanceTransactionFilterJson,
		// AdvanceTransactionFilter.class);
		// when(transactionDelegate.searchTransactions(txnFilterData,
		// advanceTransactionFilter)).the
	}
}
