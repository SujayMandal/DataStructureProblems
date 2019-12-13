package com.ca.umg.report.service;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.report.model.ModelReportStatusInfo;
import com.ca.umg.report.model.ReportInfo;

public interface ReportService {
	
	public static final String RA_REPORT_REQUEST_MAPPING = "/ra-report";
	
	public static final String RA_REPORT_REQUEST_MAPPING_API = "/download/v1.0";
	
	public void generateReport(final ModelReportStatusInfo reoprtStatusInfo) throws BusinessException, SystemException;
	
	public byte[] generateAndDownloadReport(final ModelReportStatusInfo reoprtStatusInfo) throws BusinessException, SystemException;
	
	public byte[] generateAndDownloadReportTranDshbrd(final ModelReportStatusInfo reoprtStatusInfo) throws BusinessException, SystemException;
	public boolean isReportGeneratedForTransaction(final ModelReportStatusInfo reoprtStatusInfo) throws BusinessException, SystemException;
	
	public boolean isReportGeneratedForModel(final ModelReportStatusInfo reoprtStatusInfo) throws BusinessException, SystemException;
	
	public byte[] downloadReport(final ModelReportStatusInfo reoprtStatusInfo) throws BusinessException, SystemException;
	
	public ReportInfo getTransactionReportURL(final ModelReportStatusInfo reoprtStatusInfo) throws BusinessException, SystemException;
}
