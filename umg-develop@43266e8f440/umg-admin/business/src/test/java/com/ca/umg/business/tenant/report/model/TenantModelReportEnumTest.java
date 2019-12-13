package com.ca.umg.business.tenant.report.model;

import static com.ca.umg.business.tenant.report.model.TenantModelReportEnum.MODEL_INPUT;
import static com.ca.umg.business.tenant.report.model.TenantModelReportEnum.TENANT_INPUT;
import static com.ca.umg.business.tenant.report.model.TenantModelReportEnum.TRANSACTION_ID;
import static com.ca.umg.business.tenant.report.model.TenantModelReportEnum.getModelReport;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TenantModelReportEnumTest {

	@Test
	public void testReportField() {
		final TenantModelReportEnum reportField = MODEL_INPUT;
		assertTrue(reportField.isReportField());
		assertTrue(reportField.getColumn() != null);
		assertTrue(reportField.getReportName() != null);
	}

	@Test
	public void testNonReportField() {
		final TenantModelReportEnum reportField = TRANSACTION_ID;
		assertFalse(reportField.isReportField());
		assertTrue(reportField.getColumn() != null);
		assertTrue(reportField.getReportName() != null);
	}

	@Test
	public void testGetModelReport() {
		final TenantModelReportEnum reportField = getModelReport("tenantInput");
		assertTrue(reportField.isReportField());
		assertTrue(reportField.getColumn() != null);
		assertEquals(reportField, TENANT_INPUT);
	}

	@Test
	public void testGetModelReportForException() {
		try {
			getModelReport("Tenant Input1");
			org.junit.Assert.fail("Exception expected");
		} catch (IllegalArgumentException iae) {
			assertTrue(true);
		}

		try {
			getModelReport(null);
			org.junit.Assert.fail("Exception expected");
		} catch (IllegalArgumentException iae) {
			assertTrue(true);
		}
	}
}