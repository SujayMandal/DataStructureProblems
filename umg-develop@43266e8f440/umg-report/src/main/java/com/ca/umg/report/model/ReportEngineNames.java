package com.ca.umg.report.model;

public enum ReportEngineNames {
	JASPER_ENGINE("JASPER");
	
	private final String engineName;
	
	private ReportEngineNames(final String engineName) {
		this.engineName = engineName;
	}
	
	public String getEngineName() {
		return engineName;
	}
	
	public static ReportEngineNames getReportEngineName(final String engineName) {
		ReportEngineNames name = null;
		if (JASPER_ENGINE.getEngineName().equals(engineName)) {
			name = JASPER_ENGINE;
		}
		
		return name;
	}
}
