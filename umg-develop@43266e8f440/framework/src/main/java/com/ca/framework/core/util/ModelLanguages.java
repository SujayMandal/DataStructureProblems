package com.ca.framework.core.util;

public enum ModelLanguages {

	MATLAB("MATLAB"), R("R"), EXCEL("EXCEL");

	private String language;

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	private ModelLanguages(String language) {
		this.language = language;
	}
};
