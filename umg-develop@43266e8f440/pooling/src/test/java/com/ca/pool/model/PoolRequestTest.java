package com.ca.pool.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class PoolRequestTest {

	@Test
	public void test() {
		PoolRequest pr = PoolRequest.getRequest(PoolRequest.CREATE.getRequest());
		assertTrue(pr == PoolRequest.CREATE);
		
		pr = PoolRequest.getRequest(PoolRequest.UPDATE.getRequest());
		assertTrue(pr == PoolRequest.UPDATE);
		
		pr = PoolRequest.getRequest(PoolRequest.DELETE.getRequest());
		assertTrue(pr == PoolRequest.DELETE);
		
		pr = PoolRequest.getRequest(null);
		assertTrue(pr == null);
	}

}
