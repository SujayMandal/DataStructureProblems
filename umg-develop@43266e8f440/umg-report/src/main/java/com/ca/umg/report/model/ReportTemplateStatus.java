package com.ca.umg.report.model;

public enum ReportTemplateStatus {

	ACTIVE(1), //
	NOT_ACTIVE(0), //
	UNKNOW(-1);
	
	private final int status;
	
	private ReportTemplateStatus(final int status) {
		this.status = status;
	}
	
	public int getStatus() {
		return status;
	}
	
	public ReportTemplateStatus getReportStatus(final int status) {
		ReportTemplateStatus rs = UNKNOW;
		if (ACTIVE.getStatus() == status) {
			rs = ACTIVE;
		} else if (NOT_ACTIVE.getStatus() == status) {
			rs = NOT_ACTIVE;
		}
		
		return rs;
	}
}