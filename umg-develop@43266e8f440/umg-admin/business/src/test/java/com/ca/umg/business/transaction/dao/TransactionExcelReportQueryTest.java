package com.ca.umg.business.transaction.dao;

import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.BATCH_ID;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.ERROR_CODE;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.ERROR_DESCRIPTION;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.IS_TEST;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.LIBRARY_NAME;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.MAJOR_VERSION;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.MINOR_VERSION;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.MODEL;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.RUN_DATE_TIME;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.TENANT_ID;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.TENANT_TRANSACTION_ID;
import static java.lang.Integer.valueOf;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.framework.core.systemparameter.SystemParameterProviderImpl;
import com.ca.umg.business.transaction.info.TransactionFilter;

@Ignore
public class TransactionExcelReportQueryTest {

	private TransactionFilter filter;
	private TransactionExcelReportQuery reportQuery;
	private long startDateTime;
	private long endDateTime;

	@Before
	public void setTransactionFilter() {
		filter = Mockito.mock(TransactionFilter.class);
		reportQuery = new TransactionExcelReportQuery();
		startDateTime = System.currentTimeMillis();
		endDateTime = System.currentTimeMillis();
	}

	@Test
	public void testSelectClause() {
		final String expectedSelectClause = "select urt.ID, "
				+ "urt.TENANT_ID, "
				+ "mapping.BATCH_ID, "
				+ "urt.VERSION_NAME, "
				+ "urt.MAJOR_VERSION, "
				+ "urt.MINOR_VERSION, "
				+ "urt.CLIENT_TRANSACTION_ID, "
				+ "urt.RUN_AS_OF_DATE, "
				+ "urt.STATUS, "
				+ "urt.MAJOR_VERSION, "
				+ "urt.MINOR_VERSION, "
				+ "urt.RUNTIME_CALL_START, "
				+ "urt.RUNTIME_CALL_END, "
				+ "urt.ERROR_CODE, "
				+ "ml.EXECUTION_LANGUAGE ";

		final String query = reportQuery.createLoadReportQuery(filter, "ocwen");
		assertTrue(query.contains(expectedSelectClause));
	}

	@Test
	public void testFromClause() {
		final String expectedFromClause = " from UMG_RUNTIME_TRANSACTION urt"
				+ " left outer join BATCH_TXN_RUNTIME_TXN_MAPPING mapping"
				+ " on urt.ID = mapping.TRANSACTION_ID"
				+ " and urt.TENANT_ID = mapping.TENANT_ID";

		final String query = reportQuery.createLoadReportQuery(filter, "ocwen");
		assertTrue(query.contains(expectedFromClause));
	}

	@Test
	public void testOrderByClause() {
		final String expectedOrderByClause = " order by urt.created_on desc";
		final String query = reportQuery.createLoadReportQuery(filter, "ocwen");
		assertTrue(query.contains(expectedOrderByClause));
	}

	@Test
	public void testTenantIdCondition() {
		final String expectedCondition = "lower(urt." + TENANT_ID.getDbColumnName() + ") = 'ocwen'";
		final String query = reportQuery.createLoadReportQuery(filter, "ocwen");
		assertTrue(query.contains("where "));
		assertTrue(query.contains(expectedCondition));
	}

	@Test
	public void testEscapeSingleQuoteCondition() {
		final String expectedCondition = "lower(urt." + TENANT_ID.getDbColumnName() + ") = 'oc\\'wen'";
		final String query = reportQuery.createLoadReportQuery(filter, "oc'wen");
		assertTrue(query.contains("where "));
		assertTrue(query.contains(expectedCondition));
	}

	@Test
	public void testEscapePercentageCondition() {
		final String expectedCondition = "lower(urt." + TENANT_ID.getDbColumnName() + ") = 'oc\\%wen'";
		final String query = reportQuery.createLoadReportQuery(filter, "oc%wen");
		assertTrue(query.contains("where "));
		assertTrue(query.contains(expectedCondition));
	}

	@Test
	public void testEscapeUnderscoreCondition() {
		final String expectedCondition = "lower(urt." + TENANT_ID.getDbColumnName() + ") = 'oc\\_wen'";
		final String query = reportQuery.createLoadReportQuery(filter, "oc_wen");
		assertTrue(query.contains("where "));
		assertTrue(query.contains(expectedCondition));
	}

	@Test
	public void testLibraryNameCondition() {
		final String expectedCondition = "lower(urt." + LIBRARY_NAME.getDbColumnName() + ") = 'my library'";
		Mockito.when(filter.getLibraryName()).thenReturn("my library");
		final String query = reportQuery.createLoadReportQuery(filter, "OCWEN");
		assertTrue(query.contains("where "));
		assertTrue(query.contains(expectedCondition));
	}

	@Test
	public void testTenantModelCondition() {
		final String expectedCondition = "lower(urt." + MODEL.getDbColumnName() + ") = 'my model'";
		Mockito.when(filter.getTenantModelName()).thenReturn("my model");
		final String query = reportQuery.createLoadReportQuery(filter, "OCWEN");
		assertTrue(query.contains("where "));
		assertTrue(query.contains(expectedCondition));
	}

	@Test
	public void testTenantTransactionIdCondition() {
		final String expectedCondition = "lower(urt." + TENANT_TRANSACTION_ID.getDbColumnName() + ") like '%client tra id%'";
		Mockito.when(filter.getClientTransactionID()).thenReturn("CLIENT TRA ID");
		final String query = reportQuery.createLoadReportQuery(filter, "OCWEN");
		assertTrue(query.contains("where "));
		assertTrue(query.contains(expectedCondition));
	}

	@Test
	public void testMajorVersionCondition() {
		final String expectedCondition = "urt." + MAJOR_VERSION.getDbColumnName() + " = 1";
		Mockito.when(filter.getMajorVersion()).thenReturn(valueOf(1));
		final String query = reportQuery.createLoadReportQuery(filter, "OCWEN");
		assertTrue(query.contains("where "));
		assertTrue(query.contains(expectedCondition));
	}

	@Test
	public void testMinorVersionCondition() {
		final String expectedCondition = "urt." + MINOR_VERSION.getDbColumnName() + " = 2";
		Mockito.when(filter.getMinorVersion()).thenReturn(valueOf(2));
		final String query = reportQuery.createLoadReportQuery(filter, "OCWEN");
		assertTrue(query.contains("where "));
		assertTrue(query.contains(expectedCondition));
	}

	//TODO commented this as method is not used anywhere for umg-4200 
		//need to change according to new filter object if this method is used
	@Ignore
	public void testIsTrueTestCondition() {
		final String expectedCondition = "urt." + IS_TEST.getDbColumnName() + " = 1";
		//Mockito.when(filter.isTestTxn()).thenReturn(TRUE);
		final String query = reportQuery.createLoadReportQuery(filter, "OCWEN");
		assertTrue(query.contains("where "));
		assertTrue(query.contains(expectedCondition));
	}

	//TODO commented this as method is not used anywhere for umg-4200 
		//need to change according to new filter object if this method is used
	@Ignore
	public void testIsFalseTestCondition() {
		final String expectedCondition = "urt." + IS_TEST.getDbColumnName() + " = 0";
		//Mockito.when(filter.isTestTxn()).thenReturn(FALSE);
		final String query = reportQuery.createLoadReportQuery(filter, "OCWEN");
		assertTrue(query.contains("where "));
		assertTrue(query.contains(expectedCondition));
	}

	@Test
	public void testStartDateOnlyCondition() {
		final String expectedCondition = "urt." + RUN_DATE_TIME.getDbColumnName() + " >= " + startDateTime;
		Mockito.when(filter.getRunAsOfDateFrom()).thenReturn(startDateTime);
		Mockito.when(filter.getRunAsOfDateTo()).thenReturn(null);
		final String query = reportQuery.createLoadReportQuery(filter, "OCWEN");
		assertTrue(query.contains("where "));
		assertTrue(query.contains(expectedCondition));
	}

	@Test
	public void testEndDateOnlyCondition() {
		final String expectedCondition = "urt." + RUN_DATE_TIME.getDbColumnName() + " <= " + endDateTime;
		Mockito.when(filter.getRunAsOfDateTo()).thenReturn(endDateTime);
		Mockito.when(filter.getRunAsOfDateFrom()).thenReturn(null);
		final String query = reportQuery.createLoadReportQuery(filter, "OCWEN");
		assertTrue(query.contains("where "));
		assertTrue(query.contains(expectedCondition));
	}

	@Test
	public void testWithBothDatesCondition() {
		final String expectedCondition = "urt." + RUN_DATE_TIME.getDbColumnName() + " between " + startDateTime + " and "+ endDateTime;
		Mockito.when(filter.getRunAsOfDateTo()).thenReturn(endDateTime);
		Mockito.when(filter.getRunAsOfDateFrom()).thenReturn(startDateTime);
		final String query = reportQuery.createLoadReportQuery(filter, "OCWEN");
		assertTrue(query.contains("where "));
		assertTrue(query.contains(expectedCondition));
	}

	@Test
	public void testErrorCodeCondition() {
		final SystemParameterProvider systemParameterProvider = mock(SystemParameterProviderImpl.class);
		when(systemParameterProvider.getParameter(SystemConstants.VALIDATION_ERROR_CODE_PATTERN)).thenReturn("RVE");
		reportQuery.setSystemParameterProvider(systemParameterProvider);

		final String expectedCondition = "lower(urt." + ERROR_CODE.getDbColumnName() + ") like '%rve%'";
		when(filter.getErrorType()).thenReturn("validation");

		final String query = reportQuery.createLoadReportQuery(filter, "OCWEN");
		assertTrue(query.contains("where "));
		assertTrue(query.contains(expectedCondition));
	}

	@Test
	public void testErrorDescCondition() {
		final String expectedCondition = " ( lower(urt." + ERROR_DESCRIPTION.getDbColumnName() + ") like '%rme%' or "
				+ "lower(urt." +  ERROR_CODE.getDbColumnName() + ") like '%rme%' )";
		when(filter.getErrorDescription()).thenReturn("RME");
		final String query = reportQuery.createLoadReportQuery(filter, "OCWEN");
		assertTrue(query.contains("where "));
		assertTrue(query.contains(expectedCondition));
	}

	@Test
	public void testBatchIdCondition() {
		final String expectedCondition = "lower(mapping." + BATCH_ID.getDbColumnName() + ") = 'b1'";
		Mockito.when(filter.getBatchId()).thenReturn("b1");
		final String query = reportQuery.createLoadReportQuery(filter, "OCWEN");
		assertTrue(query.contains("where "));
		assertTrue(query.contains(expectedCondition));
	}

	//TODO commented this as method is not used anywhere for umg-4200 
		//need to change according to new filter object if this method is used
	@Ignore
	public void testCreateLoadReportQuery() {
		final String expectedQuery = "select urt.ID, "
				+ "urt.TENANT_ID, "
				+ "mapping.BATCH_ID, "
				+ "urt.VERSION_NAME, "
				+ "urt.MAJOR_VERSION, "
				+ "urt.MINOR_VERSION, "
				+ "urt.CLIENT_TRANSACTION_ID, "
				+ "urt.RUN_AS_OF_DATE, "
				+ "urt.STATUS, "
				+ "urt.MAJOR_VERSION, "
				+ "urt.MINOR_VERSION, "
				+ "urt.RUNTIME_CALL_START, "
				+ "urt.RUNTIME_CALL_END, "
				+ "urt.ERROR_CODE, "
				+ "ml.EXECUTION_LANGUAGE "
				+ " from UMG_RUNTIME_TRANSACTION urt"
				+ " left outer join BATCH_TXN_RUNTIME_TXN_MAPPING mapping"
				+ " on urt.ID = mapping.TRANSACTION_ID"
				+ " and urt.TENANT_ID = mapping.TENANT_ID"
				+ " left outer join UMG_VERSION uv"
				+ " on urt.version_name = uv.NAME"
				+ " and urt.major_version = uv.major_version"
				+ " and urt.minor_version = uv.minor_version"
				+ " left outer join MODEL_LIBRARY ml"
				+ " on uv.MODEL_LIBRARY_ID = ml.id"
				+ " where lower(urt.TENANT_ID) = 'ocwen' and"
				+ " lower(urt.LIBRARY_NAME) = 'my library' and"
		        + " urt.RUN_AS_OF_DATE between " + startDateTime + " and " + endDateTime + " and"
				+ " lower(urt.VERSION_NAME) = 'my model' and"
				+ " lower(urt.CLIENT_TRANSACTION_ID) like '%client tra id%' and"
				+ " urt.MAJOR_VERSION = 1 and"
				+ " urt.MINOR_VERSION = 2 and"
				+ " urt.IS_TEST = 1 and"
				+ " lower(urt." + ERROR_CODE.getDbColumnName() + ") like '%rve%' and "
				+ " ( lower(urt." + ERROR_DESCRIPTION.getDbColumnName() + ") like '%rme%' or lower(urt." +  ERROR_CODE.getDbColumnName() + ") like '%rme%' )  and"
				+ " lower(mapping." + BATCH_ID.getDbColumnName() + ") = 'b1'"
				+ " order by urt.created_on desc";


		when(filter.getLibraryName()).thenReturn("my library");
		when(filter.getTenantModelName()).thenReturn("my model");
		when(filter.getClientTransactionID()).thenReturn("CLIENT TRA ID");
		when(filter.getRunAsOfDateTo()).thenReturn(endDateTime);
		when(filter.getRunAsOfDateFrom()).thenReturn(startDateTime);
		when(filter.getMajorVersion()).thenReturn(valueOf(1));
		when(filter.getMinorVersion()).thenReturn(valueOf(2));
		//when(filter.isTestTxn()).thenReturn(TRUE);
		when(filter.getErrorType()).thenReturn("validation");
		when(filter.getErrorDescription()).thenReturn("RME");
		when(filter.getBatchId()).thenReturn("b1");

		final SystemParameterProvider systemParameterProvider = mock(SystemParameterProviderImpl.class);
		when(systemParameterProvider.getParameter(SystemConstants.VALIDATION_ERROR_CODE_PATTERN)).thenReturn("RVE");
		reportQuery.setSystemParameterProvider(systemParameterProvider);

		final String query = reportQuery.createLoadReportQuery(filter, "OCWEN");
		assertTrue(query.equals(expectedQuery));
	}

	//TODO commented this as method is not used anywhere for umg-4200 
		//need to change according to new filter object if this method is used
	@Ignore
	public void testCreateGetMaxMinRunAsOfDateQuery() {
		final String expectedQuery = "select max(urt.RUN_AS_OF_DATE) as maxdate, "
				+ "min(urt.RUN_AS_OF_DATE) mindate "
				+ " from UMG_RUNTIME_TRANSACTION urt"
				+ " left outer join BATCH_TXN_RUNTIME_TXN_MAPPING mapping"
				+ " on urt.ID = mapping.TRANSACTION_ID"
				+ " and urt.TENANT_ID = mapping.TENANT_ID "
				+ " where lower(urt.TENANT_ID) = 'ocwen' and"
				+ " lower(urt.LIBRARY_NAME) = 'my library' and"
				+ " urt.RUN_AS_OF_DATE between " + startDateTime + " and " + endDateTime + " and"
				+ " lower(urt.VERSION_NAME) = 'my model' and"
				+ " lower(urt.CLIENT_TRANSACTION_ID) like '%client tra id%' and"
				+ " urt.MAJOR_VERSION = 1 and"
				+ " urt.MINOR_VERSION = 2 and"
				+ " urt.IS_TEST = 1 and"
				+ " lower(urt." + ERROR_CODE.getDbColumnName() + ") like '%rve%' and "
				+ " ( lower(urt." + ERROR_DESCRIPTION.getDbColumnName() + ") like '%rme%' or lower(urt." +  ERROR_CODE.getDbColumnName() + ") like '%rme%' )  and"
				+ " lower(mapping." + BATCH_ID.getDbColumnName() + ") = 'b1'";


		when(filter.getLibraryName()).thenReturn("my library");
		when(filter.getTenantModelName()).thenReturn("my model");
		when(filter.getClientTransactionID()).thenReturn("CLIENT TRA ID");
		when(filter.getRunAsOfDateTo()).thenReturn(endDateTime);
		when(filter.getRunAsOfDateFrom()).thenReturn(startDateTime);
		when(filter.getMajorVersion()).thenReturn(valueOf(1));
		when(filter.getMinorVersion()).thenReturn(valueOf(2));
		//when(filter.isTestTxn()).thenReturn(TRUE);
		when(filter.getErrorType()).thenReturn("validation");
		when(filter.getErrorDescription()).thenReturn("RME");
		when(filter.getBatchId()).thenReturn("b1");

		final SystemParameterProvider systemParameterProvider = mock(SystemParameterProviderImpl.class);
		when(systemParameterProvider.getParameter(SystemConstants.VALIDATION_ERROR_CODE_PATTERN)).thenReturn("RVE");
		reportQuery.setSystemParameterProvider(systemParameterProvider);

		final String query = reportQuery.createGetMaxMinRunAsOfDateQuery(filter, "OCWEN");
		System.out.println(query);
		System.out.println(expectedQuery);
		assertTrue(query.equals(expectedQuery));
	}
}