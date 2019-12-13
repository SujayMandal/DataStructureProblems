package com.ca.pool.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class PoolCriteriaDefMappingTest {

	@Test
	public void poolCriteriaDefMappingTest() {
		
		PoolCriteriaDefMapping pcd = new PoolCriteriaDefMapping();
		
		pcd.setId("id");
		assertTrue(pcd.getId().equals("id"));
		
		pcd.setPoolCriteriaValue("poolCriteriaValue");
		assertTrue(pcd.getPoolCriteriaValue().equals("poolCriteriaValue"));
		
		pcd.setPoolId("poolId");
		assertTrue(pcd.getPoolId().equals("poolId"));
		
		pcd.setPoolName("poolName");
		assertTrue(pcd.getPoolName().equals("poolName"));
	}

}
