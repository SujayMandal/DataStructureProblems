package com.ca.umg.report.model;

public enum ReportExecutedStatus {

	SUCCESS("SUCCESS"), //
	FAILED("FAILED"), //
	UNKNOWN("UNKNOWN");
	
	private final String status;
	
	private ReportExecutedStatus(final String status) {
		this.status = status;
	}
	
	public String getStatus() {
		return status;
	}
	
	public ReportExecutedStatus getReportExecutedStatus(final String status) {
		ReportExecutedStatus res = UNKNOWN;
		if (SUCCESS.getStatus().equals(status)) {
			return SUCCESS;
		} else if (FAILED.getStatus().equals(status)) {
			return FAILED;
		} 
		
		return res;
	}
}
