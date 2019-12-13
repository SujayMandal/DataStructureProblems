package com.ca.pool.model;

public enum DefaultPool {
	DEFAULT(1),
	NON_DEFAULT(0);
	
	private final int defaultPool;
	
	private DefaultPool(final int defaultPool) {
		this.defaultPool = defaultPool;
	}
	
	public int getDefaultPool() {
		return defaultPool;
	}
	
	public static boolean isDefaultPool(final Pool pool) {
		if (DEFAULT.getDefaultPool() == pool.getDefaultPool()) {
			return true;
		} else {
			return false;
		}
	}
}
