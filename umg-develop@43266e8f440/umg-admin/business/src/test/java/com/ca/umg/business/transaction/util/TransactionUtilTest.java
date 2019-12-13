package com.ca.umg.business.transaction.util;

import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.framework.core.util.ConversionUtil;
import com.ca.umg.business.transaction.info.TransactionFilter;
import com.ca.umg.business.transaction.mongo.entity.TransactionDocument;

public class TransactionUtilTest {

	private static final String DATA_FILE = "./src/test/resources/TenantInput.txt";
	private Long JUL_31_2014 = Long.parseLong("140674500");
	private static final String SEARCHSTR_VALIDATION_ERRORTYPE = " AND TXN.LIBRARY_NAME = 'LIB' AND TXN.VERSION_NAME = 'VERSION TEST' AND LOWER(TXN.CLIENT_TRANSACTION_ID) like '%1%' AND TXN.MAJOR_VERSION = 1 AND TXN.MINOR_VERSION = 0 AND TXN.IS_TEST = true AND LOWER(TXN.ERROR_CODE) LIKE '%rve%' AND (LOWER(TXN.ERROR_DESCRIPTION) LIKE '%error%' OR LOWER(TXN.ERROR_CODE) LIKE '%error%')";
	private static final String SEARCHSTR_SYSTEMEXP_ERRORTYPE = " AND TXN.LIBRARY_NAME = 'LIB' AND TXN.VERSION_NAME = 'VERSION TEST' AND LOWER(TXN.CLIENT_TRANSACTION_ID) like '%1%' AND TXN.MAJOR_VERSION = 1 AND TXN.MINOR_VERSION = 0 AND TXN.IS_TEST = true AND LOWER(TXN.ERROR_CODE) LIKE '%rse%' AND (LOWER(TXN.ERROR_DESCRIPTION) LIKE '%error%' OR LOWER(TXN.ERROR_CODE) LIKE '%error%')";
	private static final String SEARCHSTR_MODELEXP_ERRORTYPE = " AND TXN.LIBRARY_NAME = 'LIB' AND TXN.VERSION_NAME = 'VERSION TEST' AND LOWER(TXN.CLIENT_TRANSACTION_ID) like '%1%' AND TXN.MAJOR_VERSION = 1 AND TXN.MINOR_VERSION = 0 AND TXN.IS_TEST = true AND LOWER(TXN.ERROR_CODE) LIKE '%rme%' AND (LOWER(TXN.ERROR_DESCRIPTION) LIKE '%error%' OR LOWER(TXN.ERROR_CODE) LIKE '%error%')";

	@Mock
	private SystemParameterProvider systemParameterProvider;

	@Before
	public void setUp() {

		initMocks(this);
	}

	@Test(expected = InvocationTargetException.class)
	public void testTransactionUtilPrivateConstuctor()
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		final Constructor<?>[] constructors = TransactionUtil.class.getDeclaredConstructors();
		constructors[0].setAccessible(true);
		constructors[0].newInstance((Object[]) null);
	}

	@Test
	public void testAddToZipFile() throws IOException, SystemException {
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		try {
			File zipFile = new File("Download.zip");
			fos = new FileOutputStream(zipFile);
			zos = new ZipOutputStream(fos);
			ObjectMapper mapper = new ObjectMapper();
			TransactionUtil.addToZipFile("test", mapper.writeValueAsBytes(readFile(DATA_FILE)), zos);
			TransactionUtil.addToZipFile("test", mapper.writeValueAsBytes(readFile(DATA_FILE)), zos);
			TransactionUtil.addToZipFile("test", mapper.writeValueAsBytes(readFile(DATA_FILE)), zos);
			assertTrue("Content not added to the file correctly", zipFile.length() > 0);
			zipFile.delete();
		} finally {
			if(fos != null) {
				fos.flush();
				fos.close();
			}
			
			if(zos != null) {
				zos.finish();
				zos.close();
			}
		}
	}

	@Test
	public void testGetFileName() throws IOException, SystemException {
		TransactionDocument txnDocument = createTransactionData("1", "TestTxn-1", "TestVersionName", "TestLibName", 1,
				2, "Success", JUL_31_2014, readFile(DATA_FILE));
		assertTrue("File name is wrong",
				TransactionUtil.getFileName(txnDocument.getClientTransactionID(), txnDocument.getRunAsOfDate(), "Model")
						.startsWith("TestTxn-1"));

	}

	// ignored this test as the method getSearchCriteria is not used anywhere
	@Ignore
	public void testgetSearchCriteria() {
		Mockito.when(systemParameterProvider.getParameter(SystemConstants.VALIDATION_ERROR_CODE_PATTERN))
				.thenReturn("RVE");
		StringBuffer serachCriteria_validationErrType = TransactionUtil.getSearchCriteria(
				createTransactionFilter("1", "VERSION TEST", "LIB", 1, 0, "validation"), systemParameterProvider);
		Assert.assertNotNull(serachCriteria_validationErrType);
		Assert.assertEquals(SEARCHSTR_VALIDATION_ERRORTYPE, serachCriteria_validationErrType.toString());

		Mockito.when(systemParameterProvider.getParameter(SystemConstants.SYSTEM_EXCEPTION_ERROR_CODE_PATTERN))
				.thenReturn("RSE");

		StringBuffer serachCriteria_SystemErrType = TransactionUtil.getSearchCriteria(
				createTransactionFilter("1", "VERSION TEST", "LIB", 1, 0, "systemException"), systemParameterProvider);
		Assert.assertNotNull(serachCriteria_SystemErrType);
		Assert.assertEquals(SEARCHSTR_SYSTEMEXP_ERRORTYPE, serachCriteria_SystemErrType.toString());

		Mockito.when(systemParameterProvider.getParameter(SystemConstants.MODEL_EXCEPTION_ERROR_CODE_PATTERN))
				.thenReturn("RME");

		StringBuffer serachCriteria_ModelErrType = TransactionUtil.getSearchCriteria(
				createTransactionFilter("1", "VERSION TEST", "LIB", 1, 0, "modelException"), systemParameterProvider);
		Assert.assertNotNull(serachCriteria_SystemErrType);
		Assert.assertEquals(SEARCHSTR_MODELEXP_ERRORTYPE, serachCriteria_ModelErrType.toString());
	}

	Map readFile(String fileName) throws IOException, SystemException {
		Path path = Paths.get(fileName);
		return ConversionUtil.convertJson(FileUtils.readFileToString(new File(fileName)), Map.class);
	}

	private TransactionDocument createTransactionData(String txnId, String clientTxnId, String tenantModelName,
			String libName, Integer majorVersion, Integer minorVersion, String status, Long runAsOfDate,
			Map<String, Object> data) {
		TransactionDocument txnDocument = new TransactionDocument();
		txnDocument.setLibraryName(libName);
		txnDocument.setClientTransactionID(clientTxnId);
		txnDocument.setMajorVersion(majorVersion);
		txnDocument.setMinorVersion(minorVersion);
		txnDocument.setRunAsOfDate(runAsOfDate.longValue());
		txnDocument.setStatus(status);
		txnDocument.setModelInput(data);
		txnDocument.setModelOutput(data);
		txnDocument.setTenantInput(data);
		txnDocument.setTenantOutput(data);

		return txnDocument;
	}

	private TransactionFilter createTransactionFilter(String clientTxnId, String tenantModelName, String libName,
			Integer majorVersion, Integer minorVersion, String errorType) {
		TransactionFilter txnFilter = new TransactionFilter();
		txnFilter.setLibraryName(libName);
		txnFilter.setTenantModelName(tenantModelName);
		txnFilter.setClientTransactionID(clientTxnId);
		txnFilter.setMajorVersion(majorVersion);
		txnFilter.setMinorVersion(minorVersion);
		txnFilter.setRunAsOfDateFromString(new DateTime().toString());
		txnFilter.setRunAsOfDateToString(new DateTime().toString());
		txnFilter.setRunAsOfDateTo(System.currentTimeMillis());
		txnFilter.setRunAsOfDateFrom(System.currentTimeMillis());
		txnFilter.setFullVersion("1.0");
		// TODO commented this as method is not used anywhere for umg-4200
		// need to change according to new filter object if this method is used
		// txnFilter.setShowTestTxn(true);
		txnFilter.setErrorType(errorType);
		txnFilter.setBatchId("1");
		txnFilter.setErrorDescription("Error");
		return txnFilter;
	}

}
