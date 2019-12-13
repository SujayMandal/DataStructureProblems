package com.ca.umg.business.transaction.info;

import static com.ca.umg.business.transaction.info.TransactionStatus.ERROR;
import static com.ca.umg.business.transaction.info.TransactionStatus.FAILED;
import static com.ca.umg.business.transaction.info.TransactionStatus.FAILURE;
import static com.ca.umg.business.transaction.info.TransactionStatus.OTHER;
import static com.ca.umg.business.transaction.info.TransactionStatus.SUCCESS;
import static com.ca.umg.business.transaction.info.TransactionStatus.valuOf;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TransactionStatusTest {

	@Test
	public void testSuccessStatus() {
		TransactionStatus success = valuOf("Success");
		assertTrue(success == SUCCESS);
		assertTrue(success.getReportStatus().equals("Success"));
		
		success = valuOf("SUCCESS");
		assertTrue(success == SUCCESS);
		assertTrue(success.getReportStatus().equals("Success"));
		
		success = valuOf("success");
		assertTrue(success == SUCCESS);
		assertTrue(success.getReportStatus().equals("Success"));
	}
	
	@Test
	public void testErrorStatus() {
		TransactionStatus error = valuOf("error");
		assertTrue(error == ERROR);
		assertTrue(error.getReportStatus().equals("Failure"));
		
		error = valuOf("ERROR");
		assertTrue(error == ERROR);
		assertTrue(error.getReportStatus().equals("Failure"));
		
		error = valuOf("Error");
		assertTrue(error == ERROR);
		assertTrue(error.getReportStatus().equals("Failure"));
	}
	
	@Test
	public void testFailedStatus() {
		TransactionStatus failed = valuOf("FAILED");
		assertTrue(failed == FAILED);
		assertTrue(failed.getReportStatus().equals("Failure"));
		
		failed = valuOf("Failed");
		assertTrue(failed == FAILED);
		assertTrue(failed.getReportStatus().equals("Failure"));
		
		failed = valuOf("failed");
		assertTrue(failed == FAILED);
		assertTrue(failed.getReportStatus().equals("Failure"));
	}
	
	@Test
	public void testFailureStatus() {
		TransactionStatus failure = valuOf("failure");
		assertTrue(failure == FAILURE);
		assertTrue(failure.getReportStatus().equals("Failure"));
		
		failure = valuOf("Failure");
		assertTrue(failure == FAILURE);
		assertTrue(failure.getReportStatus().equals("Failure"));
		
		failure = valuOf("FAILURE");
		assertTrue(failure == FAILURE);
		assertTrue(failure.getReportStatus().equals("Failure"));
	}
	
	@Test
	public void testOtherStatus() {
		TransactionStatus failure = valuOf("other");
		assertTrue(failure == OTHER);
		assertTrue(failure.getReportStatus().equals("Failure"));
		
		failure = valuOf(null);
		assertTrue(failure == OTHER);
		assertTrue(failure.getReportStatus().equals("Failure"));
		
		failure = valuOf("");
		assertTrue(failure == OTHER);
		assertTrue(failure.getReportStatus().equals("Failure"));
	}
}