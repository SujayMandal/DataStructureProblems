package com.ca.umg.report.model;

public enum ReportTypes {

	PDF("PDF", ".pdf"), //
	HTML("HTML", ".html"), //
	CSV("CSV", ".csvv"), //
	TEXT("TEXT", ".txt"), //
	JSON("JSON", ".json"), //
	XLS("XLS", ".xls"), //
	XLSX("XLSX", ".xlsx");
	
	private final String type;
	private final String reportFileExt;
	
	private ReportTypes(final String type, final String reportFileExt) {
		this.type = type;
		this.reportFileExt = reportFileExt;
	}
	
	public String getType() {
		return type;
	}
	
	public String getReportFileExt() {
		return reportFileExt;
	}
}
