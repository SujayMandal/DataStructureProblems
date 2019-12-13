package com.ca.umg.report.service.delegate;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.ca.framework.core.delegate.AbstractDelegate;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.report.model.ModelReportStatusInfo;
import com.ca.umg.report.model.ReportInfo;
import com.ca.umg.report.service.bo.ReportServiceBO;

@Component
public class ReportServiceDelegateImpl extends AbstractDelegate implements ReportServiceDelegate {

	@Inject
    private ReportServiceBO modelReportStatusBO;
	
	@Override
	public void generateReport(final ModelReportStatusInfo reoprtStatusInfo) throws BusinessException, SystemException {
		modelReportStatusBO.generateReport(reoprtStatusInfo);
	}

	@Override
	public ModelReportStatusInfo saveReportStatus(final ModelReportStatusInfo info) throws BusinessException, SystemException {
        return modelReportStatusBO.saveReportStatus(info);
	}
	
	@Override
    public boolean isReportGeneratedForTransaction(final ModelReportStatusInfo reoprtStatusInfo) throws BusinessException, SystemException {
		return modelReportStatusBO.isReportGeneratedForTransaction(reoprtStatusInfo);
	}
	
	@Override
    public boolean isReportGeneratedForModel(final ModelReportStatusInfo reoprtStatusInfo) throws BusinessException, SystemException {
		return modelReportStatusBO.isReportGeneratedForModel(reoprtStatusInfo);
	}
	
	@Override
	public byte[] downloadReport(final ModelReportStatusInfo reoprtStatusInfo) throws BusinessException, SystemException {
		return modelReportStatusBO.downloadReport(reoprtStatusInfo);
	}
	
	@Override
	public ReportInfo getTransactionReportURL(final ModelReportStatusInfo reoprtStatusInfo) throws BusinessException, SystemException {
		return modelReportStatusBO.getTransactionReportURL(reoprtStatusInfo);
	}
}
