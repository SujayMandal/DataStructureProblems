package com.ca.umg.report.service;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.report.model.ModelReportStatusInfo;
import com.ca.umg.report.model.ReportInfo;
import com.ca.umg.report.service.delegate.ReportServiceDelegate;

@Component
public class ReportServiceImpl implements ReportService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ReportServiceImpl.class);
	
	@Inject
	private ReportServiceDelegate delegate;
	
	@Override
	public void generateReport(final ModelReportStatusInfo reoprtStatusInfo) throws BusinessException, SystemException {
		LOGGER.info("generateReport is called, Report Status Info is :" + reoprtStatusInfo.toString());
		delegate.generateReport(reoprtStatusInfo);
	}
	
	@Override
	public byte[] generateAndDownloadReport(final ModelReportStatusInfo reoprtStatusInfo) throws BusinessException, SystemException {
		return generateAndDwnldReport(reoprtStatusInfo);
	}
	
	@Override
	@PreAuthorize("hasRole(@accessPrivilege.getDashboardTransactionDownloadReport())")
	public byte[] generateAndDownloadReportTranDshbrd(final ModelReportStatusInfo reoprtStatusInfo) throws BusinessException, SystemException {
        return generateAndDwnldReport(reoprtStatusInfo);
    }
	
	private byte[] generateAndDwnldReport (final ModelReportStatusInfo reoprtStatusInfo) throws BusinessException, SystemException {
	    LOGGER.info("generateReport is called, Report Status Info is :" + reoprtStatusInfo.toString());
        delegate.generateReport(reoprtStatusInfo);
        LOGGER.info("downloadReport is called, Report Status Info is :" + reoprtStatusInfo.toString());
        return downloadReport(reoprtStatusInfo);
	}
	
	@Override
    public boolean isReportGeneratedForTransaction(final ModelReportStatusInfo reoprtStatusInfo) throws BusinessException, SystemException {
		LOGGER.info("isReportGeneratedForTransaction is called, Report Status Info is :" + reoprtStatusInfo.toString());
		return delegate.isReportGeneratedForTransaction(reoprtStatusInfo);
	}
	
	@Override
    public boolean isReportGeneratedForModel(final ModelReportStatusInfo reoprtStatusInfo) throws BusinessException, SystemException {
		LOGGER.info("isReportGeneratedForModel is called, Report Status Info is :" + reoprtStatusInfo.toString());
		return delegate.isReportGeneratedForModel(reoprtStatusInfo);
	}
	
	@Override
	public byte[] downloadReport(final ModelReportStatusInfo reoprtStatusInfo) throws BusinessException, SystemException {
		LOGGER.info("downloadReport is called, Report Status Info is :" + reoprtStatusInfo.toString());
		return delegate.downloadReport(reoprtStatusInfo);
	}
	
	@Override
	public ReportInfo getTransactionReportURL(final ModelReportStatusInfo reoprtStatusInfo) throws BusinessException, SystemException {
		return delegate.getTransactionReportURL(reoprtStatusInfo);
	}
}