package com.ca.umg.sdc.rest.controller;

import static com.ca.framework.core.exception.SystemException.newSystemException;
import static com.ca.umg.report.ReportExceptionCodes.REPORT_DOWNLOAD_URL_ERROR;
import static com.ca.umg.report.ReportExceptionCodes.REPORT_TEMPLATE_NOT_AVL_ERROR;
import static com.ca.umg.report.util.ReportUtil.createAppErrorMessage;
import static org.springframework.http.HttpStatus.PRECONDITION_FAILED;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.model.delegate.ModelDelegate;
import com.ca.umg.business.transaction.mongo.entity.TransactionDocument;
import com.ca.umg.report.ReportExceptionCodes;
import com.ca.umg.report.model.ModelReportStatusInfo;
import com.ca.umg.report.model.ModelReportTemplateDefinition;
import com.ca.umg.report.service.ReportService;
import com.ca.umg.sdc.rest.constants.ModelConstants;
import com.ca.umg.sdc.rest.constants.RaApiConstants;

@Controller
@RequestMapping("/ra-report")
public class RAReportController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RAReportController.class);
	private static final String ATTACHMENT_TYPE = "attachment; filename=\"%s\"";
	private static final int NO_OF_PARAMETERS = 2;
	
	@Inject
    private ReportService reportService;
	
	@Inject
	private ModelDelegate modelDelegate;
	
	@RequestMapping(value = {"/download/v1.0/{transactionId}/{reportName}"}, method = RequestMethod.GET)
    @ResponseBody
    public void downloadReport(@PathVariable Map<String, String> pathVariables, HttpServletResponse response) {
		LOGGER.info("Download Report API V1.0 is called");
		OutputStream outputStream=null;
		try {
			validateReportURL(pathVariables);
			final String transactionId = pathVariables.get(RaApiConstants.TRANSACTION_ID);
			final String reportName = pathVariables.get(RaApiConstants.REPORT_NAME);
			LOGGER.info("Transaction Id is : {} and Report Name is : {}", transactionId, reportName);	
			
        	final ModelReportTemplateDefinition reportTemplate = modelDelegate.getModelReportTemplateByTxnId(transactionId);
        	
        	if (reportTemplate == null) {
        		BusinessException.newBusinessException(REPORT_TEMPLATE_NOT_AVL_ERROR.getErrorCode(), new String[] {REPORT_TEMPLATE_NOT_AVL_ERROR.getErrorDescription()});
        	}
        	
        	final ModelReportStatusInfo modelReportStatusInfo = ModelReportStatusInfo.buildMRStatusInfo(reportTemplate); 
        	modelReportStatusInfo.setUmgTransactionId(transactionId);
        	final TransactionDocument td = modelDelegate.getTransactionDocumentByTxnId(transactionId);
        	modelReportStatusInfo.setReportJsonString(TransactionDocument.createJsonString(td));
        	modelReportStatusInfo.setModelName(td.getVersionName());
        	modelReportStatusInfo.setTransactionCreatedDate(td.getCreatedDate());
        	modelReportStatusInfo.setClientTransactionId(td.getClientTransactionID());
        	
			final byte[] reportByteArray = reportService.generateAndDownloadReport(modelReportStatusInfo);
			
			String headerValue = String.format(ATTACHMENT_TYPE, modelReportStatusInfo.getReportFileName());
			response.setHeader(ModelConstants.CONTENT_DISPOSITION, headerValue);
			response.setContentType("application/pdf");
			outputStream=response.getOutputStream();
			outputStream.write(reportByteArray);
			outputStream.flush();
			
		} catch (SystemException | BusinessException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            LOGGER.error(e.getCode());
            LOGGER.error(e.getLocalizedMessage());
            response.setStatus(PRECONDITION_FAILED.value());
            try {
            	response.getWriter().print(createAppErrorMessage(e.getCode(), e.getLocalizedMessage()));
            } catch(IOException ioe) {
            	LOGGER.error("Generating and Downloading Report Failed.", e);
            	LOGGER.error("", e);
                LOGGER.error(e.getLocalizedMessage(), e);            	
				response.setStatus(PRECONDITION_FAILED.value());
			}
		} catch (IOException e) {
        	LOGGER.error("Downloading Report Failed.", e);
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setStatus(PRECONDITION_FAILED.value());
            try {
            	response.getWriter().print(createAppErrorMessage(ReportExceptionCodes.REPORT_GENERATE_ERROR.getErrorCode(), e.getLocalizedMessage()));
            } catch(IOException ioe) {
            	LOGGER.error("Generating and Downloading Report Failed.", e);
            	LOGGER.error("", e);
                LOGGER.error(e.getLocalizedMessage(), e);            	
				response.setStatus(PRECONDITION_FAILED.value());
            }
		} 
		finally{
			try {
				if(outputStream !=  null){
					outputStream.close();
				}
			} catch (IOException e) {
				LOGGER.error("Downloading Report Failed.", e);
	            LOGGER.error(e.getLocalizedMessage(), e);
			}
		}
	}	
	
	private void validateReportURL(Map<String, String> pathVariables) throws SystemException {
		LOGGER.info("Validating Report URL ...");
		if (pathVariables == null) {
			LOGGER.error("Validating Report URL ... It is failed, No Path Variables passed");
			newSystemException(REPORT_DOWNLOAD_URL_ERROR.getErrorCode(), new String[] {"Transaction Id and Report Name as Path Variable in report URL are empty"});
		} else if (pathVariables.keySet().size() != NO_OF_PARAMETERS) {
			LOGGER.error("Validating Report URL ... It is failed, No of path variable passed are not equal to 2");
			newSystemException(REPORT_DOWNLOAD_URL_ERROR.getErrorCode(), new String[] {"Expecting Transaction Id and Report Name as Path Variable, But recevied more or less path variables as part of report URL"});
		}
		
		LOGGER.info("Validating Report URL ... Successful");
	}
}