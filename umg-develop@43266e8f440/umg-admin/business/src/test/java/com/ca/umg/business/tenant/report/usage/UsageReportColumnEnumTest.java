package com.ca.umg.business.tenant.report.usage;

import static com.ca.umg.business.tenant.report.usage.UsageReportColumnEnum.LIBRARY_NAME;
import static com.ca.umg.business.tenant.report.usage.UsageReportColumnEnum.MODEL;
import static com.ca.umg.business.tenant.report.usage.UsageReportColumnEnum.UMG_TRANSACTION_ID;
import static com.ca.umg.business.tenant.report.usage.UsageReportColumnEnum.valueOfByColumnName;
import static com.ca.umg.business.tenant.report.usage.UsageReportColumnEnum.valueOfByHeaderName;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UsageReportColumnEnumTest {

	@Test
	public void testOneReportField() {
		final UsageReportColumnEnum reportField = UMG_TRANSACTION_ID;
		assertTrue(reportField.isReportField());
		assertTrue(reportField.isDbField());
		assertTrue(reportField.getColumnIndex() == 10);
		assertTrue(reportField.getDbColumnName().equals("ID"));
		assertTrue(reportField.getExcelHeaderName().equals("UMG Transaction ID"));
		assertTrue(reportField.getCellWidth() == 35 * 256);
	}

	@Test
	public void testOneNonReportField() {
		final UsageReportColumnEnum nonReportField = LIBRARY_NAME;
		assertFalse(nonReportField.isReportField());
		assertTrue(nonReportField.isDbField());
		assertTrue(nonReportField.getColumnIndex() == -1);
		assertTrue(nonReportField.getDbColumnName().equals("LIBRARY_NAME"));
		assertTrue(nonReportField.getExcelHeaderName().equals("Library Name"));
		assertTrue(nonReportField.getCellWidth() == 1);
	}

	@Test
	public void testValueOfByColumnName() {
		final String dbColumnName = "VERSION_NAME";
		UsageReportColumnEnum column = valueOfByColumnName(dbColumnName);
		assertTrue(column == MODEL);

		column = valueOfByColumnName("UN KNOWN");
		assertTrue(column == null);
	}

	@Test
	public void testValueOfByHeaderName() {
		final String headerName = "Model";
		UsageReportColumnEnum column = valueOfByHeaderName(headerName);
		assertTrue(column == MODEL);

		column = valueOfByHeaderName("UN KNOWN");
		assertTrue(column == null);
	}
}