package com.ca.umg.report.model;

import java.io.Serializable;

import com.google.common.base.Objects;

public class ReportInfo implements Serializable{

	private static final long serialVersionUID = 1L;

	private String reportURL;
	private String reportExecutionStatus;
	private String errorMessage;
	private String transactionId;
	private String reportName;
	
	public String getReportURL() {
		return reportURL;
	}
	
	public void setReportURL(String reportURL) {
		this.reportURL = reportURL;
	}
	
	public String getReportExecutionStatus() {
		return reportExecutionStatus;
	}
	
	public void setReportExecutionStatus(String reportExecutionStatus) {
		this.reportExecutionStatus = reportExecutionStatus;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this.getClass()).
				add("reportURL:", reportURL).
				add("reportExecutionStatus:", reportExecutionStatus).
				add("reportName:", reportName).
				add("transactionId:", transactionId).toString();
	}
}