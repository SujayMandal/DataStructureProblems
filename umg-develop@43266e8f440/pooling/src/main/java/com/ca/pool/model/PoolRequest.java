package com.ca.pool.model;

public enum PoolRequest {

	CREATE("Create"),
	DELETE("Delete"),
	UPDATE("Update");
	
	private final String request;
	
	private PoolRequest(final String request) {
		this.request = request;
	}
	
	public String getRequest() {
		return request;
	}
	
	public static PoolRequest getRequest(final String request) {
		PoolRequest pr = null;
		if (CREATE.getRequest().equalsIgnoreCase(request)) {
			pr = CREATE;
		} else if (DELETE.getRequest().equalsIgnoreCase(request)) {
			pr = DELETE;
		} else if (UPDATE.getRequest().equalsIgnoreCase(request)) {
			pr = UPDATE;
		}
		return pr;
	}
}
