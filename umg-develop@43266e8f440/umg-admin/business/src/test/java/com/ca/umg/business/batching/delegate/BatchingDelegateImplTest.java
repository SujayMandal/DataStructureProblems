/**
 * 
 */
package com.ca.umg.business.batching.delegate;

import static com.ca.umg.business.batching.delegate.BatchingDelegateImpl.DEFAULT_PAGE_SIZE;
import static com.ca.umg.business.batching.delegate.BatchingDelegateImpl.MAX_DISPLAY_RECORDS_SIZE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.multipart.MultipartFile;

import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.umg.business.batching.bo.BatchTransactionBO;
import com.ca.umg.business.batching.dao.BatchDashboardFilter;
import com.ca.umg.business.batching.dao.BatchTransactionDAO;
import com.ca.umg.business.batching.entity.BatchTransaction;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.model.AbstractModelTest;
import com.ca.umg.business.systemparam.delegate.SystemParameterDelegate;
import com.ca.umg.business.systemparam.info.SystemParameterInfo;
import com.ca.umg.business.util.AdminUtil;

/**
 * @author raddibas
 * 
 */

@ContextHierarchy({ @ContextConfiguration("classpath:root-db-context.xml"),
		@ContextConfiguration })
@RunWith(SpringJUnit4ClassRunner.class)
//@Ignore
public class BatchingDelegateImplTest extends AbstractModelTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BatchingDelegateImplTest.class);

	@Inject
	private BatchTransactionBO batchTransactionBO;

	@Inject
	BatchingDelegate batchingDelegate;

	@Inject
	private BatchTransactionDAO batchTransactionDAO;

	@Inject
	SystemParameterDelegate systemParameterDelegate;

	@Inject
	private SystemParameterProvider systemParameterProvider;

	private RequestContext requestContext;

	@Before
	public void setup() {
		requestContext = getLocalhostTenantContext();
	}

	@Ignore
	@Test
	public void testGetBatchInputFileContent() throws SystemException,
			BusinessException, IOException {
		String batchId_3 = createBatch("testbatch_dlgt-3");
		createSysParamAndSan("testbatch_dlgt-3", "inputFile");

		byte[] bytArray = batchingDelegate.getBatchInputFileContent(batchId_3);
		assertNotNull(bytArray);
		bytArray = null;

		FileUtils.deleteDirectory(new File(systemParameterProvider
				.getParameter(SystemConstants.SAN_BASE)));
		deleteBatch(batchId_3);
	}

	@Ignore
	@Test
	public void testGetBatchOutputFileContent() throws SystemException,
			BusinessException, IOException {
		String batchId_4 = createBatch("testbatch_dlgt-4");
		createSysParamAndSan("testbatch_dlgt-4", "outputFile");
		BatchTransaction batchTransaction = batchTransactionDAO.findOne(batchId_4);
		batchTransaction.setBatchOutputFile("testbatch_dlgt-4");
		batchTransactionDAO.saveAndFlush(batchTransaction);
		
		byte[] bytArray = batchingDelegate.getBatchOutputFileContent(batchId_4);
		assertNotNull(bytArray);
		bytArray = null;

		FileUtils.deleteDirectory(new File(systemParameterProvider
				.getParameter(SystemConstants.SAN_BASE)));
		deleteBatch(batchId_4);
	}

	@Ignore
	@Test
	public void testSaveExcelFile() throws IOException, BusinessException,
			SystemException {
		InputStream inputStream =  null;
		try {
			String batchId_6 = createBatch("testbatch_dlgt-6");
			createSysParamAndSan("testbatch_dlgt-6", "inputFile");
			String excelFile = "com/ca/umg/business/batching/delegate/test_excel_api.xlsx";
		    inputStream = this.getClass().getClassLoader()
					.getResourceAsStream(excelFile);
			MultipartFile multipartFile = new MockMultipartFile("testbatch_dlgt-6",
					"testbatch_dlgt-6", "", inputStream);
			assertNotNull(multipartFile);
			batchingDelegate.saveExcelFile(multipartFile, "testbatch_dlgt-6");
			
			List filesList = (List) FileUtils.listFiles(new File(systemParameterProvider
					.getParameter(SystemConstants.SAN_BASE)), null, true);
			assertNotNull(filesList);
			FileUtils.deleteDirectory(new File(systemParameterProvider
					.getParameter(SystemConstants.SAN_BASE)));
			deleteBatch(batchId_6);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
		
	}
	
	private String createBatch(String batchFileName) throws SystemException,
			BusinessException {
		return batchTransactionBO.createBatch(batchFileName);
	}

	private void deleteBatch(String batchName) {
		batchTransactionDAO.delete(batchName);
	}

	private void createSysParamAndSan(String batchFileName, String fileIdentifier)
			throws BusinessException, SystemException, IOException {
		BufferedWriter bwr = null;
		BufferedWriter bwrArchive = null;
		BufferedWriter bwArchive = null;
		FileWriter fwr = null;
		FileWriter fwrArchive = null;
		FileWriter fwArchive = null;
		try {
			// creating the san entry in system parameter
			SystemParameterInfo systemParameterInfo = new SystemParameterInfo();
			systemParameterInfo.setSysKey("sanBase");
			systemParameterInfo.setSysValue("batchTest");
			systemParameterInfo.setIsActive('Y');
			systemParameterDelegate.saveParameter(systemParameterInfo);

			// creating the folders and file in classpath
			String sanBasePath = AdminUtil.getSanBasePath(systemParameterProvider
					.getParameter(SystemConstants.SAN_BASE));

			
			  StringBuffer batchPath = new StringBuffer(sanBasePath);
			  batchPath.append(File.separatorChar).append(BusinessConstants.BATCH_FILE);
			  File batchFolder = new File (batchPath.toString());
			  batchFolder.mkdirs();
			 

			if (fileIdentifier.equals("inputFile")) {
				StringBuffer archiveFolderPath = new StringBuffer(batchPath);
				archiveFolderPath.append(File.separatorChar)
						.append(BusinessConstants.BATCH_TEST)
						.append(File.separatorChar)
						.append(BusinessConstants.ARCHIEVE_FOLDER);
				File archiveFolder = new File(archiveFolderPath.toString());
				archiveFolder.mkdirs();

				archiveFolderPath.append(File.separatorChar).append(batchFileName);
				File fileArchive = new File(archiveFolderPath.toString());
				fileArchive.createNewFile();
				fwrArchive = new FileWriter(fileArchive);
			    bwrArchive = new BufferedWriter(fwrArchive);
				bwrArchive.write("test text archive");
			} else if (fileIdentifier.equals("outputFile")){ 
				StringBuffer archiveFolderPath = new StringBuffer(batchPath);
				archiveFolderPath.append(File.separatorChar)
						.append(BusinessConstants.BATCH_TEST)
						.append(File.separatorChar)
						.append(BusinessConstants.OUTPUT_FOLDER);
				File archiveFolder = new File(archiveFolderPath.toString());
				archiveFolder.mkdirs();

				archiveFolderPath.append(File.separatorChar).append(batchFileName);
				File fileArchive = new File(archiveFolderPath.toString());
				fileArchive.createNewFile();
				fwArchive = new FileWriter(fileArchive);
			    bwArchive = new BufferedWriter(fwArchive);
				bwArchive.write("test output text archive");
			}else {
				StringBuffer batchFileInprogressPath = new StringBuffer(batchPath);
				batchFileInprogressPath.append(File.separatorChar)
						.append(BusinessConstants.INPROGRESS_FOLDER);
				File inProgressFolder = new File(batchFileInprogressPath.toString());
				inProgressFolder.mkdirs();

				StringBuffer inputFolderPath = new StringBuffer(sanBasePath);
				inputFolderPath.append(File.separatorChar)
						.append(BusinessConstants.BATCH_FILE).append(File.separatorChar)
						.append(BusinessConstants.INPUT_FOLDER);
				File inputFolder = new File(inputFolderPath.toString());
				inputFolder.mkdirs();

				batchFileInprogressPath.append(File.separatorChar).append(
						batchFileName);
				File file = new File(batchFileInprogressPath.toString());
				file.createNewFile();
				fwr = new FileWriter(file);
			    bwr = new BufferedWriter(fwr);
				bwr.write("test text");
			}
		} finally {
			if(fwArchive != null) {
				IOUtils.closeQuietly(fwArchive);
			}
			if(fwrArchive != null) {
				IOUtils.closeQuietly(fwrArchive);
			}
			if(fwr != null) {
				IOUtils.closeQuietly(fwr);
			}
			closeResources(bwr, bwrArchive, bwArchive);
		}
		
	}

	private void closeResources(BufferedWriter bwr, BufferedWriter bwrArchive, BufferedWriter bwArchive) {
		try {
			if(bwrArchive != null) {
				bwArchive.flush();
				bwrArchive.close();
			}
			if(bwArchive != null) {
				bwArchive.flush();
				bwArchive.close();
			}
			if(bwrArchive != null) {
				bwr.flush();
				bwr.close();
			}
		} catch (Exception e) {
			LOGGER.error("Exception while closing the Stream :" +e.getMessage());

		}
	}
	
	@Test
	public void testIsEmptySearch() {
		final BatchDashboardFilter filter = mock(BatchDashboardFilter.class);
		when(filter.getBatchId()).thenReturn(null);
		when(filter.getInputFileName()).thenReturn(null);
		when(filter.getFromDate()).thenReturn(null);
		when(filter.getToDate()).thenReturn(null);
		
		assertTrue(batchingDelegate.isEmptySearch(filter));
		
		when(filter.getBatchId()).thenReturn("");
		when(filter.getInputFileName()).thenReturn("");
		when(filter.getFromDate()).thenReturn("");
		when(filter.getToDate()).thenReturn("");
		
		assertTrue(batchingDelegate.isEmptySearch(filter));
		
		when(filter.getBatchId()).thenReturn("batchId");
		when(filter.getInputFileName()).thenReturn("");
		when(filter.getFromDate()).thenReturn("");
		when(filter.getToDate()).thenReturn("");
		
		assertFalse(batchingDelegate.isEmptySearch(filter));
		
		when(filter.getBatchId()).thenReturn("");
		when(filter.getInputFileName()).thenReturn("inputFile");
		when(filter.getFromDate()).thenReturn("");
		when(filter.getToDate()).thenReturn("");
		
		assertFalse(batchingDelegate.isEmptySearch(filter));
		
		when(filter.getBatchId()).thenReturn("");
		when(filter.getInputFileName()).thenReturn("");
		when(filter.getFromDate()).thenReturn("2015");
		when(filter.getToDate()).thenReturn("");
		
		assertFalse(batchingDelegate.isEmptySearch(filter));
		
		when(filter.getBatchId()).thenReturn("");
		when(filter.getInputFileName()).thenReturn("");
		when(filter.getFromDate()).thenReturn("");
		when(filter.getToDate()).thenReturn("2016");
		
		assertFalse(batchingDelegate.isEmptySearch(filter));		
	}

	@Test
	public void testAllRecordsSearchResultMessageWithEmptySearchWithDefaultPageSize() {
		final BatchDashboardFilter filter = mock(BatchDashboardFilter.class);
		when(filter.getBatchId()).thenReturn("");
		when(filter.getInputFileName()).thenReturn("");
		when(filter.getFromDate()).thenReturn("");
		when(filter.getToDate()).thenReturn("");
		when(filter.getPageSize()).thenReturn(DEFAULT_PAGE_SIZE);

		long totalCount = DEFAULT_PAGE_SIZE - 1;
		long returnCount = totalCount;
		
		String searchResultMessage = batchingDelegate.formSearchResultMessage(filter, totalCount, returnCount);
		assertTrue(searchResultMessage.equals("Showing all " + totalCount + " records"));
		
		totalCount = DEFAULT_PAGE_SIZE;
		returnCount = totalCount;
		searchResultMessage = batchingDelegate.formSearchResultMessage(filter, totalCount, returnCount);
		assertTrue(searchResultMessage.equals("Showing all " + returnCount + " records"));				
	}
	
	@Test
	public void testAllRecordsSearchResultMessageWithEmptySearchWithCustomPageSize() {
		final BatchDashboardFilter filter = mock(BatchDashboardFilter.class);
		when(filter.getBatchId()).thenReturn("");
		when(filter.getInputFileName()).thenReturn("");
		when(filter.getFromDate()).thenReturn("");
		when(filter.getToDate()).thenReturn("");
		when(filter.getPageSize()).thenReturn(DEFAULT_PAGE_SIZE + DEFAULT_PAGE_SIZE);

		long totalCount = DEFAULT_PAGE_SIZE + DEFAULT_PAGE_SIZE - 1;
		long returnCount = totalCount;
		
		String searchResultMessage = batchingDelegate.formSearchResultMessage(filter, totalCount, returnCount);
		assertTrue(searchResultMessage.equals("Showing all " + returnCount + " records"));
		
		totalCount = DEFAULT_PAGE_SIZE + DEFAULT_PAGE_SIZE;
		returnCount = totalCount;
		searchResultMessage = batchingDelegate.formSearchResultMessage(filter, totalCount, returnCount);
		assertTrue(searchResultMessage.equals("Showing all " + returnCount + " records"));				
	}
	
	@Test
	public void testLatestRecordsMessageWithEmptySearch() {
		final BatchDashboardFilter filter = mock(BatchDashboardFilter.class);
		when(filter.getBatchId()).thenReturn("");
		when(filter.getInputFileName()).thenReturn("");
		when(filter.getFromDate()).thenReturn("");
		when(filter.getToDate()).thenReturn("");
		when(filter.getPageSize()).thenReturn(DEFAULT_PAGE_SIZE);

		long totalCount = DEFAULT_PAGE_SIZE + 1;
		long returnCount = totalCount;
		
		String searchResultMessage = batchingDelegate.formSearchResultMessage(filter, totalCount, returnCount);
		assertTrue(searchResultMessage.equals("Showing latest 500 records"));
		
		when(filter.getPageSize()).thenReturn(DEFAULT_PAGE_SIZE + DEFAULT_PAGE_SIZE);
		totalCount = DEFAULT_PAGE_SIZE + DEFAULT_PAGE_SIZE + 1;
		returnCount = totalCount;
		searchResultMessage = batchingDelegate.formSearchResultMessage(filter, totalCount, returnCount);
		assertTrue(searchResultMessage.equals("Showing latest 1000 records"));				
	}
	
	@Test
	public void testLatestRecordsMessageWithNonEmptySearchWithDefaultPageSize() {
		final BatchDashboardFilter filter = mock(BatchDashboardFilter.class);
		when(filter.getBatchId()).thenReturn("batchId");
		when(filter.getInputFileName()).thenReturn("");
		when(filter.getFromDate()).thenReturn("");
		when(filter.getToDate()).thenReturn("");
		when(filter.getPageSize()).thenReturn(DEFAULT_PAGE_SIZE);

		long totalCount = DEFAULT_PAGE_SIZE - 1;
		long returnCount = totalCount;		
		String searchResultMessage = batchingDelegate.formSearchResultMessage(filter, totalCount, returnCount);
		assertTrue(searchResultMessage.equals("Showing all 499 resulting records"));
		
		totalCount = DEFAULT_PAGE_SIZE;
		returnCount = totalCount;
		searchResultMessage = batchingDelegate.formSearchResultMessage(filter, totalCount, returnCount);
		assertTrue(searchResultMessage.equals("Showing all 500 resulting records"));
		
		totalCount = DEFAULT_PAGE_SIZE + 1;
		returnCount = DEFAULT_PAGE_SIZE;
		searchResultMessage = batchingDelegate.formSearchResultMessage(filter, totalCount, returnCount);
		assertTrue(searchResultMessage.equals("Showing latest 500 of 501 resulting records"));
	}
	
	@Test
	public void testLatestRecordsMessageWithNonEmptySearchWithCustomPageSize() {
		final BatchDashboardFilter filter = mock(BatchDashboardFilter.class);
		when(filter.getBatchId()).thenReturn("batchId");
		when(filter.getInputFileName()).thenReturn("");
		when(filter.getFromDate()).thenReturn("");
		when(filter.getToDate()).thenReturn("");
		when(filter.getPageSize()).thenReturn(DEFAULT_PAGE_SIZE + DEFAULT_PAGE_SIZE);

		long totalCount = DEFAULT_PAGE_SIZE + DEFAULT_PAGE_SIZE - 1;
		long returnCount = totalCount;		
		String searchResultMessage = batchingDelegate.formSearchResultMessage(filter, totalCount, returnCount);
		assertTrue(searchResultMessage.equals("Showing all 999 resulting records"));
		
		totalCount = DEFAULT_PAGE_SIZE + DEFAULT_PAGE_SIZE;
		returnCount = totalCount;
		searchResultMessage = batchingDelegate.formSearchResultMessage(filter, totalCount, returnCount);
		assertTrue(searchResultMessage.equals("Showing all 1000 resulting records"));
		
		totalCount = DEFAULT_PAGE_SIZE + DEFAULT_PAGE_SIZE + 1;
		returnCount = DEFAULT_PAGE_SIZE;
		searchResultMessage = batchingDelegate.formSearchResultMessage(filter, totalCount, returnCount);
		assertTrue(searchResultMessage.equals("Showing latest 1000 of 1001 resulting records"));
	}
	
	@Test
	public void test50000KWithNonEmptySearchWithCustomPageSize() {
		final BatchDashboardFilter filter = mock(BatchDashboardFilter.class);
		when(filter.getBatchId()).thenReturn("123");
		when(filter.getInputFileName()).thenReturn("");
		when(filter.getFromDate()).thenReturn("");
		when(filter.getToDate()).thenReturn("");
		when(filter.getPageSize()).thenReturn(DEFAULT_PAGE_SIZE);

		long totalCount = MAX_DISPLAY_RECORDS_SIZE + 1;
		long returnCount = DEFAULT_PAGE_SIZE;		
		String searchResultMessage = batchingDelegate.formSearchResultMessage(filter, totalCount, returnCount);
		assertTrue(searchResultMessage.equals("Search is resulting in more than 50,000 records, please refine your criteria"));
	}
}
