package com.ca.pool.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class PoolCriteriaTest {

	@Test
	public void poolCriteriaTest() {
		PoolCriteria pc = new PoolCriteria();
		pc.setId("1");
		assertTrue(pc.getId().equals("1"));
		
		pc.setCriteriaName("criteriaName");
		assertTrue(pc.getCriteriaName().equals("criteriaName"));
		
		pc.setCriteriaPriority(1);
		assertTrue(pc.getCriteriaPriority() == 1);
	}

}
