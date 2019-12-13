package com.ca.umg.business.tenant.report.model;

import static com.ca.umg.business.tenant.report.model.TenantModelReportEnum.MODEL_INPUT;
import static com.ca.umg.business.tenant.report.model.TenantModelReportEnum.MODEL_OUTPUT;
import static com.ca.umg.business.tenant.report.model.TenantModelReportEnum.TENANT_INPUT;
import static com.ca.umg.business.tenant.report.model.TenantModelReportEnum.TENANT_OUTPUT;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class TenantModelReportTest {

	TenantModelReport tenantModelReport;
	final Map<String, Object> data = new HashMap<>();

	@Before
	public void setup() {
		tenantModelReport = mock(TenantModelReport.class);
		tenantModelReport.setModelInput(data);
		tenantModelReport.setModelOutput(data);
		tenantModelReport.setTenantInput(data);
		tenantModelReport.setTenantOutput(data);
	}

	@Test
	public void testGetTenantInputReportData() {
		final Map<String, Object> tenantInput = tenantModelReport.getReportData(TENANT_INPUT);
		assertTrue(tenantInput != null);
		assertTrue(tenantInput.equals(data));
	}

	@Test
	public void testGetTenantOutputReportData() {
		final Map<String, Object> tenantOutput = tenantModelReport.getReportData(TENANT_OUTPUT);
		assertTrue(tenantOutput != null);
		assertTrue(tenantOutput.equals(data));
	}

	@Test
	public void testGetModelInputReportData() {
		final Map<String, Object> modelInput = tenantModelReport.getReportData(MODEL_INPUT);
		assertTrue(modelInput != null);
		assertTrue(modelInput.equals(data));
	}

	@Test
	public void testGetModelOutputReportData() {
		final Map<String, Object> modelOutput = tenantModelReport.getReportData(MODEL_OUTPUT);
		assertTrue(modelOutput != null);
		assertTrue(modelOutput.equals(data));
	}
}
