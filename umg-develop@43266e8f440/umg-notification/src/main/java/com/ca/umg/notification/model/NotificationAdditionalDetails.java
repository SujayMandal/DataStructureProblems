package com.ca.umg.notification.model;

import java.io.Serializable;

public class NotificationAdditionalDetails implements Serializable {

	public static final long serialVersionUID = 1L;
	
	private String logDetails;
	
	private String exceptionDetails;
	
	private String jsonDocument;

	public String getLogDetails() {
		return logDetails;
	}

	public void setLogDetails(String logDetails) {
		this.logDetails = logDetails;
	}

	public String getExceptionDetails() {
		return exceptionDetails;
	}

	public void setExceptionDetails(String exceptionDetails) {
		this.exceptionDetails = exceptionDetails;
	}

	public String getJsonDocument() {
		return jsonDocument;
	}

	public void setJsonDocument(String jsonDocument) {
		this.jsonDocument = jsonDocument;
	}
}
