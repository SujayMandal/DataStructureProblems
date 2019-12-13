package com.ca.umg.business.tenant.report.usage.util;

import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import junit.framework.Assert;

public class UsageReportUtilTest {

	SqlRowSet sqlRowSet;

	@Before
	public void before() {
		sqlRowSet = mock(SqlRowSet.class);
	}

	@Test
	public void TestGetProcessingTime() {
		final String processingTime = UsageReportUtil.getProcessingTime(101l);
		Assert.assertTrue(processingTime.equals("101 ms"));
	}
}