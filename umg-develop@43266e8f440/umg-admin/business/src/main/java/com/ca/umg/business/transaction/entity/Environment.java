package com.ca.umg.business.transaction.entity;

public enum Environment {
	
	MATLAB("Matlab"),
	R("R");
	
	private final String value;
	
	private Environment(final String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	@Override
	public String toString() {
	    return getValue();
	}
	
	public static Environment getEnvironment(final String value) {
		Environment e = null;
		if (MATLAB.getValue().equalsIgnoreCase(value)) {
			e = Environment.MATLAB;
		} else if (R.getValue().equalsIgnoreCase(value)) {
			e = Environment.R;
		} 
		
		return e;
	}
}
