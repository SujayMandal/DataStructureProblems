package com.ca.umg.business.version.command.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.publishing.status.constants.PublishingStatus;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.model.delegate.ModelDelegate;
import com.ca.umg.business.transaction.mongo.entity.TransactionDocument;
import com.ca.umg.business.version.command.annotation.CommandDescription;
import com.ca.umg.business.version.command.error.Error;
import com.ca.umg.business.version.info.VersionInfo;
import com.ca.umg.report.model.ModelReportStatusInfo;
import com.ca.umg.report.model.ReportExecutedStatus;
import com.ca.umg.report.model.ReportInfo;
import com.ca.umg.report.service.ReportService;


@Named
@Scope(BusinessConstants.SCOPE_PROTOTYPE)
@CommandDescription(name = "generateModelReport")
public class GenerateModelReport extends AbstractModelReportCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateModelReport.class);
    
    @Inject
    private ReportService reportService;
    
    @Inject
    private ModelDelegate modelDelegate;   
   
    
    private final ReportInfo reportInfo = new ReportInfo();

    @Override
    public void execute(Object data) throws BusinessException, SystemException {    	
    	LOGGER.info("Execution called on Generate Report Template Command");
        List<Error> errors = new ArrayList<Error>();
        Boolean execBreak = Boolean.FALSE;

        try {
        	if (hasModelReport(data) ) {
        		setExecuted(Boolean.TRUE);
        		LOGGER.info("Model has report, hecen generting report...");
        		VersionInfo versionInfo = (VersionInfo) data;
        		ModelReportStatusInfo reoprtStatusInfo = null;
                reoprtStatusInfo = ModelReportStatusInfo.buildMRStatusInfo(versionInfo.getReportTemplateInfo());
            	reoprtStatusInfo.setModelName(versionInfo.getMapping().getModel().getName());
            	reoprtStatusInfo.setUmgTransactionId(versionInfo.getUmgTransactionId());
            	
            	final TransactionDocument  td = modelDelegate.getTransactionDocumentByTxnId(versionInfo.getUmgTransactionId());
            	reoprtStatusInfo.setTransactionCreatedDate(td.getCreatedDate());
            	reoprtStatusInfo.setClientTransactionId(td.getClientTransactionID());
            	reoprtStatusInfo.setReportJsonString(TransactionDocument.createJsonString(td));
            	
            	LOGGER.info("ModelReportStatusInfo object is :" + reoprtStatusInfo.toString());
            	reportService.generateReport(reoprtStatusInfo);
            	reportInfo.setReportExecutionStatus(ReportExecutedStatus.SUCCESS.getStatus());            	
            	reportInfo.setReportURL(reportService.getTransactionReportURL(reoprtStatusInfo).getReportURL());
            	reportInfo.setTransactionId(reoprtStatusInfo.getUmgTransactionId());
            	reportInfo.setReportName(reoprtStatusInfo.getReportName());
                sendStatusMessage(errors, data, PublishingStatus.TESTING_REPORT.getStatus());
        	}        	
        } catch (SystemException | BusinessException e) {
        	LOGGER.error(e.getMessage());
        	LOGGER.error(e.getLocalizedMessage(), e);
        	reportInfo.setReportExecutionStatus(ReportExecutedStatus.FAILED.getStatus());
        	reportInfo.setErrorMessage(e.getLocalizedMessage());
        }
        
        getErrorController().setErrors(errors);
        getErrorController().setExecutionBreak(execBreak);
    }

    @Override
    public void rollback(Object data) throws BusinessException, SystemException {
    	// TODO: think what to do
    	LOGGER.error("Rollback called on Generrate Report Template Command. Nothing will be rollbacked from here");
    }

    @Override
    public boolean isCreated() throws BusinessException, SystemException {
        return true;
    }
    
    public ReportInfo getReportInfo() {
    	return reportInfo;
    }
}