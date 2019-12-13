package com.ca.pool.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class TransactionCriteriaTest {

	@Test
	public void transactionCriteriaTest() {
		TransactionCriteria tc = new TransactionCriteria();
		
		tc.setBatchId("abc12");
		assertTrue(tc.getBatchId().equals("abc12"));
		
		tc.setClientTransactionId("clientTransactionId");
		assertTrue(tc.getClientTransactionId().equals("clientTransactionId"));
		
		tc.setExecutionLanguage("Matlab");
		assertTrue(tc.getExecutionLanguage().equals("Matlab"));
		
		tc.setExecutionLanguageVersion("7.16");
		assertTrue(tc.getExecutionLanguageVersion().equals("7.16"));
		
		tc.setIsVersionCreationTest(true);
		assertTrue(tc.getIsVersionCreationTest() == true);
		
		tc.setModelName("hubzuweekzero");
		assertTrue(tc.getModelName().equals("hubzuweekzero"));
		
		tc.setModelVersion("1.0");
		assertTrue(tc.getModelVersion().equals("1.0"));
		
		tc.setTenantCode("Hubzu");
		assertTrue(tc.getTenantCode().equals("Hubzu"));
		
		tc.setTransactionRequestMode("Online");
		assertTrue(tc.getTransactionRequestMode().equals("Online"));
		
		tc.setTransactionRequestType("test");
		assertTrue(tc.getTransactionRequestType().equals("test"));
		
		tc.setUmgTransactionId("acb1233");
		assertTrue(tc.getUmgTransactionId().equals("acb1233"));
	}

}
