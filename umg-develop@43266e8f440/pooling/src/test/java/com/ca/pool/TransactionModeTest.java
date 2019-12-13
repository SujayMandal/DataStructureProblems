package com.ca.pool;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TransactionModeTest {

	@Test
	public void testGetTransactionMode() {
		final TransactionMode batchType = TransactionMode.getTransactionMode("B1");
		assertTrue(batchType == TransactionMode.BATCH);
		assertTrue(batchType.getMode().equals("Batch"));
		
		final TransactionMode onlineType = TransactionMode.getTransactionMode(null);
		assertTrue(onlineType == TransactionMode.ONLINE);
		assertTrue(onlineType.getMode().equals("Online"));
	}
}