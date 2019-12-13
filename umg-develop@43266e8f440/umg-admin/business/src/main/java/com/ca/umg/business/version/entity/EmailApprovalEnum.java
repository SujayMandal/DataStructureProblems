package com.ca.umg.business.version.entity;

public enum EmailApprovalEnum {

	EMAIL_APPROVAL(1), //
	PORTAL_APPROVAL(0);
	
	private int value;
	
	private EmailApprovalEnum(final int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
