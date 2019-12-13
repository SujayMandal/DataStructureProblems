package com.ca.umg.business.transaction.report;

import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.LIBRARY_NAME;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.UMG_TRANSACTION_ID;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TransactionExcelReportEnumTest {

	@Test
	public void testOneReportField() {
		final TransactionExcelReportEnum reportField = UMG_TRANSACTION_ID;
		assertTrue(reportField.isReportField());
		assertTrue(reportField.isDbField());
		assertTrue(reportField.getColumnIndex() == 11);
		assertTrue(reportField.getDbColumnName().equals("ID"));
		assertTrue(reportField.getExcelHeaderName().equals("UMG Transaction ID"));
		assertTrue(reportField.getCellWidth() == 35 * 256);
	}
	
	@Test
	public void testOneNonReportField() {
		final TransactionExcelReportEnum nonReportField = LIBRARY_NAME;
		assertFalse(nonReportField.isReportField());
		assertTrue(nonReportField.isDbField());
		assertTrue(nonReportField.getColumnIndex() == -1);
		assertTrue(nonReportField.getDbColumnName().equals("LIBRARY_NAME"));
		assertTrue(nonReportField.getExcelHeaderName().equals("Library Name"));
		assertTrue(nonReportField.getCellWidth() == 1);
	}
}