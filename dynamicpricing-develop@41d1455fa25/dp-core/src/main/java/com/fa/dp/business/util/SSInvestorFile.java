package com.fa.dp.business.util;

public enum SSInvestorFile {
	
	INVESTOR_MATRIX("Investor Matrix"),
	ASPS_CLIENT_ID("ASPS Client #"),
	INVESTOR_NAME("Investor Name");
	
	private String value;
	
	private SSInvestorFile(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
