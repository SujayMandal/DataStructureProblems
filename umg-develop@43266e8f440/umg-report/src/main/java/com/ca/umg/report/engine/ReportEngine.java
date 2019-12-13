package com.ca.umg.report.engine;

import java.io.OutputStream;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.report.model.ModelReportTemplateInfo;

public interface ReportEngine {
	
	public void execute(final ModelReportTemplateInfo reportTemplateInfo, final OutputStream reportOutputStream) throws SystemException, BusinessException;
	
/*	
	public void validateReportTemplate();
	public void compileReportTemplate();
	public void loadReportData();
	public void fillReportData();
	public void generateReport();
	public void createReportURL();
	public void saveGeneratedReport();
	public void saveGeneratedReportDetails();
*/}