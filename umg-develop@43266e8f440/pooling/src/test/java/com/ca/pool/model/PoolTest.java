package com.ca.pool.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class PoolTest {

	@Test
	public void test() {
		Pool pool = Pool.getSystemDefaultPool();
		
		pool.setWaitTimeout(1000);		
		assertTrue(pool.getWaitTimeout() == 1000);
		
		pool.setDefaultPool(1);		
		assertTrue(pool.getDefaultPool() == 1);
		
		pool.setExecutionLanguage(ExecutionLanguage.R.getValue());		
		assertTrue(pool.getExecutionLanguage().equals(ExecutionLanguage.R.getValue()));
		
		pool.setPoolName(ExecutionLanguage.R.getValue());		
		assertTrue(pool.getPoolName().equals(ExecutionLanguage.R.getValue()));
		
		pool.setPoolStatus(ExecutionLanguage.R.getValue());		
		assertTrue(pool.getPoolStatus().equals(ExecutionLanguage.R.getValue()));
		
		pool.setPoolDesc(ExecutionLanguage.R.getValue());		
		assertTrue(pool.getPoolDesc().equals(ExecutionLanguage.R.getValue()));
		
		pool.setId(ExecutionLanguage.R.getValue());		
		assertTrue(pool.getId().equals(ExecutionLanguage.R.getValue()));
		
		pool.setInactiveModeletCount(0);		
		assertTrue(pool.getInactiveModeletCount() == 0);
		
		pool.setModeletAdded(0);		
		assertTrue(pool.getModeletAdded() == 0);
		
		pool.setModeletCount(0);		
		assertTrue(pool.getModeletCount() == 0);
		
		pool.setModeletRemoved(0);		
		assertTrue(pool.getModeletRemoved() == 0);
		
		pool.setOldWaitTimeout(0);		
		assertTrue(pool.getOldWaitTimeout() == 0);
		
		pool.setPriority(0);		
		assertTrue(pool.getPriority() == 0);
	}

}
