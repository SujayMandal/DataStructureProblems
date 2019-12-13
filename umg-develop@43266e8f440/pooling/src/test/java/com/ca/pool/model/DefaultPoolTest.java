package com.ca.pool.model;

import org.junit.Assert;
import org.junit.Test;

public class DefaultPoolTest {

	@Test
	public void testIsDefaultPool1() {
		final Pool pool = new Pool();
		pool.setDefaultPool(DefaultPool.DEFAULT.getDefaultPool());
		Assert.assertTrue(DefaultPool.isDefaultPool(pool));
	}
	
	@Test
	public void testIsDefaultPool2() {
		final Pool pool = new Pool();
		pool.setDefaultPool(DefaultPool.NON_DEFAULT.getDefaultPool());
		Assert.assertFalse(DefaultPool.isDefaultPool(pool));
	}

}
