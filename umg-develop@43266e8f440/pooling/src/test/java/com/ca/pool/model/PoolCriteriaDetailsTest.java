package com.ca.pool.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class PoolCriteriaDetailsTest {

	@Test
	public void poolCriteriaDetailsTest() {
		PoolCriteriaDetails pcd = new PoolCriteriaDetails();
		
		pcd.setExecLangVersion("environment");
		assertTrue(pcd.getExecLangVersion().equals("environment"));
		
		pcd.setExecutionLanguage("environment");
		assertTrue(pcd.getExecutionLanguage().equals("environment"));
		
		pcd.setModel("environment");
		assertTrue(pcd.getModel().equals("environment"));
		
		pcd.setModelName("environment");
		assertTrue(pcd.getModelName().equals("environment"));
		
		pcd.setModelVersion("environment");
		assertTrue(pcd.getModelVersion().equals("environment"));
		
		pcd.setTenant("environment");
		assertTrue(pcd.getTenant().equals("environment"));
		
		pcd.setTransactionMode("environment");
		assertTrue(pcd.getTransactionMode().equals("environment"));
		
		pcd.setTransactionType("environment");
		assertTrue(pcd.getTransactionType().equals("environment"));
	}

}
