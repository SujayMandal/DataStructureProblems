package com.ca.pool.model;

public enum RequestType {

	TEST("TEST"),
	PROD("PROD");
	
	private final String type;
	
	private RequestType(final String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
}
