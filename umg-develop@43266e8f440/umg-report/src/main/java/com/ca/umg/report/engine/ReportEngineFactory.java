package com.ca.umg.report.engine;

import com.ca.umg.report.model.ModelReportTemplateInfo;
import com.ca.umg.report.model.ReportEngineNames;

public class ReportEngineFactory {

	public static ReportEngine getReportEngine(final ModelReportTemplateInfo modelReportTemplateInfo) {
		final ReportEngineNames reportEngineName = ReportEngineNames.getReportEngineName(modelReportTemplateInfo.getReportEngine());
		ReportEngine reportEngine;
		switch(reportEngineName) {
			case JASPER_ENGINE :
				reportEngine = new JasperReportEngine();
				break;
			default :
				reportEngine = new JasperReportEngine();
		}
		
		return reportEngine;
	}
}
