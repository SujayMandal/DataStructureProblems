package com.fa.dp.core.email.service.constants;

public enum DPOutputEmailExceptionCodes {

	TEMPLATE_NOT_AVAILABLE("NF0000001", "Template is not avaiable"), //
	
	MAIL_SENDING_FAILED("NF0000002", "Mail Sending Failed"); //
	
	private final String code;
	private final String description;
	
	private DPOutputEmailExceptionCodes(final String code, final String description) {
		this.code = code;
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}
	
	@Override
	public String toString() {
		return code + " : " + description;
	}	
}