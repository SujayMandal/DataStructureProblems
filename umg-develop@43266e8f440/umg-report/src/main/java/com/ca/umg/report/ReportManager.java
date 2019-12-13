package com.ca.umg.report;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.SimpleJasperReportsContext;

public class ReportManager {

	private final JasperReportsContext context;
	
	public ReportManager() {
		context = new SimpleJasperReportsContext();
	}
	
	public void compileReport(final String reportName) {
		if (reportName != null) {
			try {
				final JasperReport jasperReport = JasperCompileManager.compileReport(getSourceFileName(reportName));
			} catch (JRException jre) {
				jre.printStackTrace();
			}
		}
	}
	
	public String getSourceFileName(final String reportName) {
		return "D:\\Work\\Tasks\\JasperReports\\reports\\" + reportName + ".jrxml";
	}
}
