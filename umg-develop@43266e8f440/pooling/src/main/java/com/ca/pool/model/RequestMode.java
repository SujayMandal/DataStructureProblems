package com.ca.pool.model;

public enum RequestMode {

	BATCH("BATCH"), //
	ONLINE("ONLINE"),
	BULK("BULK");
	
	
	private final String mode;
	
	private RequestMode(final String mode) {
		this.mode = mode;
	}
	
	public String getMode() {
		return mode;
	}
}