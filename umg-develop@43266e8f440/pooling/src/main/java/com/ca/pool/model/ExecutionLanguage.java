package com.ca.pool.model;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

public enum ExecutionLanguage implements Serializable {
	
	MATLAB("Matlab"),
	R("R"),
	EXCEL("Excel");
	
	private final String value;
	
	private ExecutionLanguage(final String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	@Override
	public String toString() {
	    return getValue();
	}
	
	public static ExecutionLanguage getEnvironment(final String name) {
		ExecutionLanguage e = null;
		if (StringUtils.equalsIgnoreCase(name, MATLAB.getValue())) {
			e = ExecutionLanguage.MATLAB;
		} else if (StringUtils.equalsIgnoreCase(name, R.getValue())) {
			e = ExecutionLanguage.R;
		} else if (StringUtils.equalsIgnoreCase(name, EXCEL.getValue())) {
			e = ExecutionLanguage.EXCEL;
		}
		
		return e;
	}
}