package com.ca.umg.report.service.bo;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.report.model.ModelReportStatusInfo;
import com.ca.umg.report.model.ReportInfo;

public interface ReportServiceBO {

	public void generateReport(final ModelReportStatusInfo reoprtStatusInfo) throws BusinessException, SystemException;
	
	public ModelReportStatusInfo saveReportStatus(final ModelReportStatusInfo info) throws BusinessException, SystemException;
	
    public String getReportLocation(final ModelReportStatusInfo info) throws BusinessException, SystemException;
    
    public void storeGeneratedReport(final ModelReportStatusInfo info, final byte[] byteArray) throws BusinessException, SystemException;
    
    public boolean isReportGeneratedForTransaction(final ModelReportStatusInfo reoprtStatusInfo) throws BusinessException, SystemException;
    
    public boolean isReportGeneratedForModel(final ModelReportStatusInfo reoprtStatusInfo) throws BusinessException, SystemException;
    
    public byte[] downloadReport(final ModelReportStatusInfo reoprtStatusInfo) throws BusinessException, SystemException;
    
    public ReportInfo getTransactionReportURL(final ModelReportStatusInfo reoprtStatusInfo) throws BusinessException, SystemException;
}
