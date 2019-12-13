

package com.ca.umg.sdc.rest.controller;

import static com.ca.framework.core.requestcontext.RequestContext.getRequestContext;
import static com.ca.umg.report.ReportExceptionCodes.REPORT_DOWNLOAD_ERROR;
import static com.ca.umg.report.ReportExceptionCodes.REPORT_GENERATE_ERROR;
import static com.ca.umg.report.ReportExceptionCodes.REPORT_TEMPLATE_NOT_AVL_ERROR;
import static com.ca.umg.report.util.ReportUtil.createAdminErrorMessage;
import static com.ca.umg.report.util.ReportUtil.getFileNameWithoutExt;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.model.delegate.ModelDelegate;
import com.ca.umg.business.transaction.delegate.TransactionDelegate;
import com.ca.umg.business.transaction.mongo.entity.TransactionDocument;
import com.ca.umg.business.util.AdminUtil;
import com.ca.umg.business.version.entity.Version;
import com.ca.umg.report.ReportExceptionCodes;
import com.ca.umg.report.model.ModelReportStatusInfo;
import com.ca.umg.report.model.ModelReportTemplateDefinition;
import com.ca.umg.report.model.ModelReportTemplateInfo;
import com.ca.umg.report.model.ReportEngineNames;
import com.ca.umg.report.model.ReportTemplateStatus;
import com.ca.umg.report.model.ReportTypes;
import com.ca.umg.report.service.ReportService;
import com.ca.umg.sdc.rest.constants.ModelConstants;
import com.ca.umg.sdc.rest.constants.RaApiConstants;
import com.ca.umg.sdc.rest.utils.RestResponse;
@SuppressWarnings({"PMD.CyclomaticComplexity"})
@Controller
@RequestMapping("/report")
public class ReportController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportController.class);
	
    private static final String VERSION_ID = "versionId";

    private static final String ATTACHMENT_TYPE = "attachment; filename=\"%s\"";
    
    @Inject
    private TransactionDelegate transactionDelegate;
    
    @Inject
    private ReportService reportService; 
    
    @Inject
    private ModelDelegate modelDelegate;
    
    private static final String DOWNLOAD_ERROR_MSG = "Generating and Downloading Report Failed";
    
    @RequestMapping(value = "/generateReport/{reportData}", method = RequestMethod.GET)
    public void generateReport(@PathVariable("reportData") String reportData, HttpServletResponse responseReport) {
    	LOGGER.info("Entered Generate Report Method");
    	OutputStream outputStream=null;
        try {
        	final String[] splits = reportData.split("[,]");
        	final String majorVersion = splits[0];
        	final String minorVersion = splits[1];
        	final String transactionId = splits[2];
        	final String versionName = splits[3];
        	final String fullVersion = majorVersion + "." + minorVersion;
      	
        	LOGGER.info("Version name:" + versionName + ", Full Version :" + fullVersion + ", TransactionId" + transactionId);
        	final Version versionInfo = transactionDelegate.getVersionInfo(versionName, fullVersion);
        	ModelReportTemplateDefinition reportTemplate = modelDelegate.getModelReportTemplate(versionInfo.getId());
        	if (reportTemplate != null) {
	        	final ModelReportStatusInfo reoprtStatusInfo = ModelReportStatusInfo.buildMRStatusInfo(reportTemplate);
	        	reoprtStatusInfo.setModelName(versionInfo.getMapping().getModel().getName());
	        	reoprtStatusInfo.setUmgTransactionId(transactionId);	 
	        	final TransactionDocument td = modelDelegate.getTransactionDocumentByTxnId(transactionId);
	        	reoprtStatusInfo.setTransactionCreatedDate(td.getCreatedDate());
	        	reoprtStatusInfo.setClientTransactionId(td.getClientTransactionID());
            	reoprtStatusInfo.setTenantId(getRequestContext().getTenantCode());
            	reoprtStatusInfo.setReportJsonString(TransactionDocument.createJsonString(td));
            	
	        	LOGGER.info("ModelReportStatusInfo object is :" + reoprtStatusInfo.toString());
	        	final byte[] reportByteArray = reportService.generateAndDownloadReportTranDshbrd(reoprtStatusInfo);  
                String headerValue = String.format(ATTACHMENT_TYPE, reoprtStatusInfo.getReportFileName());
                responseReport.setHeader(ModelConstants.CONTENT_DISPOSITION, headerValue);
                outputStream=responseReport.getOutputStream();
                outputStream.write(reportByteArray);
                outputStream.flush();
        	} else {
        		LOGGER.info("Model does not have any report template, hence not able to generate report");        		
				responseReport.setStatus(PRECONDITION_FAILED.value());
				responseReport.getWriter().print(createAdminErrorMessage(REPORT_TEMPLATE_NOT_AVL_ERROR.getErrorCode(), REPORT_TEMPLATE_NOT_AVL_ERROR.getErrorDescription()));
        	}
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            LOGGER.error(e.getCode());
            LOGGER.error(e.getLocalizedMessage());
            responseReport.setStatus(PRECONDITION_FAILED.value());
            try {
            	responseReport.getWriter().print(e.getLocalizedMessage());
            } catch(IOException ioe) {
            	LOGGER.error(DOWNLOAD_ERROR_MSG);
                LOGGER.error(e.getLocalizedMessage(), ioe);            	
				responseReport.setStatus(PRECONDITION_FAILED.value());
            }
           
        } catch (IOException e) {
        	LOGGER.error(DOWNLOAD_ERROR_MSG);
            LOGGER.error(e.getLocalizedMessage(), e);
            try {
    			responseReport.setStatus(PRECONDITION_FAILED.value());
    			responseReport.getWriter().print(createAdminErrorMessage(REPORT_GENERATE_ERROR.getErrorCode(), e.getLocalizedMessage()));
            } catch(IOException ioe) {
            	LOGGER.error(DOWNLOAD_ERROR_MSG);
                LOGGER.error(e.getLocalizedMessage(), ioe);            	
				responseReport.setStatus(PRECONDITION_FAILED.value());
            } 
		}
        finally{
			try {
				if(outputStream !=  null){
					outputStream.close();
				}
			} catch (IOException e1) {
				LOGGER.error(DOWNLOAD_ERROR_MSG);
	        	LOGGER.error("", e1);
	            LOGGER.error(e1.getLocalizedMessage(), e1);
			}
		} 
    }
    
    @RequestMapping(value = "/downloadReportTemplate/{versionId}", method = RequestMethod.GET)
    public void downloadReportTemplate(@PathVariable(VERSION_ID) String versionId, final HttpServletResponse response) throws SystemException, BusinessException {
        try {
        	LOGGER.info("Donwload repoort template request is received for version : {}", versionId);
            final ModelReportTemplateDefinition reportTemplate = modelDelegate.getModelReportTemplate(versionId);
            if (reportTemplate != null) {
            	LOGGER.info("Report template found for version id : {}", versionId);
            	String headerValue = String.format(ATTACHMENT_TYPE, reportTemplate.getTemplateFileName());
                response.setHeader(ModelConstants.CONTENT_DISPOSITION, headerValue);
                response.getOutputStream().write(reportTemplate.getTemplateDefinition());
                response.getOutputStream().flush();
            } else {
            	LOGGER.info("Report template not found for version id : {}", versionId);
            	LOGGER.info("Hence, no report will be download");
                writeErrorData(response, versionId, ModelConstants.MODEL_RPT_TMPL_DOES_NOT);
            }
        } catch (BusinessException | SystemException se) {
            LOGGER.error(se.getLocalizedMessage(), se);
        } catch (IOException e) {
            LOGGER.error("", e);
        }
    }
    
    private void writeErrorData(final HttpServletResponse response, final String modelId, final String msg) {
        try {
            String headerValue = String.format("attachment; filename=\"%s\"", "error_" + modelId + ".txt");
            response.setHeader(ModelConstants.CONTENT_DISPOSITION, headerValue);
            String errorMsg = null;
            if (msg == null) {
                errorMsg = "File doesn't exist: " + modelId;
                response.getOutputStream().write(errorMsg.getBytes());
                response.getOutputStream().flush();
            } else {
                errorMsg = msg;
                response.getOutputStream().write(errorMsg.getBytes());
                response.getOutputStream().flush();
            }
        } catch (IOException excep) {
            LOGGER.error("Error while Writting error data  ", excep);
        }
    }
    
    @RequestMapping(value = "/uploadReportTemplate", method = RequestMethod.POST)
    @ResponseBody
    public RestResponse<String> uploadReportTemplate(@RequestParam("versionId") String versionId, 
    		@RequestParam(value = "reportTemplate", required = false) MultipartFile reportTemplate,@RequestParam("templateId") String templateId) {
    	
    	RestResponse<String> response = new RestResponse<>();

    	try {
    		ModelReportTemplateDefinition existingOne = null;
    		try {
        		existingOne = modelDelegate.getModelRprtTemplateVerListScrn(versionId);    			
    		} catch (SystemException | BusinessException e) {
    			if (!ReportExceptionCodes.isReportTemplateNotAvlbCode(e.getCode())) {
    				throw e;
    			}
    		}
    		
    		final ModelReportTemplateInfo reportTemplateInfo = new ModelReportTemplateInfo();
        	reportTemplateInfo.setReportType(ReportTypes.PDF.getType());
        	reportTemplateInfo.setReportEngine(ReportEngineNames.JASPER_ENGINE.getEngineName());    			
        	reportTemplateInfo.setIsActive(ReportTemplateStatus.ACTIVE.getStatus());
        	reportTemplateInfo.setVersionId(versionId);
        	if(reportTemplate!=null) {
            	reportTemplateInfo.setTemplateDefinition(AdminUtil.convertStreamToByteArray(reportTemplate.getInputStream()));
            	reportTemplateInfo.setName(getFileNameWithoutExt(reportTemplate.getOriginalFilename()));
            	reportTemplateInfo.setReportDescription(reportTemplate.getOriginalFilename());
            	reportTemplateInfo.setTemplateFileName(reportTemplate.getOriginalFilename());
        	    if (existingOne != null) {
        	        reportTemplateInfo.setTenantId(existingOne.getTenantId());
        	        reportTemplateInfo.setReportVersion(existingOne.getReportVersion() + 1);
        	        modelDelegate.uploadModelReportTemplate(reportTemplateInfo);
        	    } else {
                   reportTemplateInfo.setReportVersion(1);
                   modelDelegate.createModelReportTemplate(reportTemplateInfo);
        	    }                         
    	    }else{    	        
    	        final ModelReportTemplateInfo existReportTemplateInfo  =  modelDelegate.getModelReportTemplateInfo(templateId);
    	        reportTemplateInfo.setTemplateDefinition(existReportTemplateInfo.getTemplateDefinition());
                reportTemplateInfo.setName(existReportTemplateInfo.getName());
                reportTemplateInfo.setReportDescription(existReportTemplateInfo.getReportDescription());
                reportTemplateInfo.setTemplateFileName(existReportTemplateInfo.getTemplateFileName());                
    	        if (existingOne != null) {    	           
                    reportTemplateInfo.setReportVersion(existingOne.getReportVersion() + 1);
                    modelDelegate.uploadModelReportTemplate(reportTemplateInfo);
                } else {
                   existReportTemplateInfo.setVersionId(versionId);                   
                   modelDelegate.createModelReportTemplate(reportTemplateInfo);
                }      
    	        //TODO
    	        
    	    }

          	
        	
        	response.setMessage("Successfully uploaded report template");
        	response.setError(false);
    	} catch (SystemException | BusinessException | IOException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setMessage("Failed to uploaded report template");
            response.setError(true);
    	}
    	
    	return response;
    }
    
    @RequestMapping(value = "/hasTransactionReportTemplate", method = RequestMethod.POST)
    @ResponseBody
    public RestResponse<String> hasTransactionReportTemplate(@RequestParam("modelName") String modelName, 
              @RequestParam(value = "fullVersion") String fullVersion) {           
       final RestResponse<String> response = new RestResponse<>();
       try {
    	   final Boolean flag = modelDelegate.hasModelReportTemplate(modelName, fullVersion);
           response.setMessage(String.valueOf(flag));
           response.setError(false);
       } catch (BusinessException | SystemException e) {
    	   LOGGER.error(e.getLocalizedMessage(), e);
           response.setMessage("Failed to find report template for transaction");
           response.setError(true);    	   
       }
       return response;
    }
    
    @RequestMapping(value = {"/download/{transactionId}/{reportName}"}, method = RequestMethod.GET)
    @ResponseBody
    public void downloadReport(@PathVariable Map<String, String> pathVariables, HttpServletResponse response) {
		LOGGER.info("Download Report API V1.0 is called");
		OutputStream outputStream=null;
		try {
			final String transactionId = pathVariables.get(RaApiConstants.TRANSACTION_ID);
			final String reportName = pathVariables.get(RaApiConstants.REPORT_NAME);
			LOGGER.info("Transaction Id is : {} and Report Name is : {}", transactionId, reportName);	
			
        	final ModelReportTemplateDefinition reportTemplate = modelDelegate.getModelReportTemplateByTxnId(transactionId);
        	
        	if (reportTemplate == null) {
        		LOGGER.info("Model does not have any report template, hence not able to generate report");        		
        		response.setStatus(PRECONDITION_FAILED.value());
        		response.getWriter().print(createAdminErrorMessage(REPORT_TEMPLATE_NOT_AVL_ERROR.getErrorCode(), REPORT_TEMPLATE_NOT_AVL_ERROR.getErrorDescription()));
        		return;
        	}
        	
        	final ModelReportStatusInfo modelReportStatusInfo = ModelReportStatusInfo.buildMRStatusInfo(reportTemplate);
        	modelReportStatusInfo.setUmgTransactionId(transactionId);
        	final TransactionDocument td = modelDelegate.getTransactionDocumentByTxnId(transactionId);
        	
        	modelReportStatusInfo.setReportJsonString(TransactionDocument.createJsonString(td));
        	modelReportStatusInfo.setTransactionCreatedDate(td.getCreatedDate());
        	modelReportStatusInfo.setClientTransactionId(td.getClientTransactionID());
        	
			final byte[] reportByteArray = reportService.downloadReport(modelReportStatusInfo);
			
			String headerValue = String.format(ATTACHMENT_TYPE, modelReportStatusInfo.getReportFileName());
			response.setHeader(ModelConstants.CONTENT_DISPOSITION, headerValue);
			 outputStream=response.getOutputStream();
			 outputStream.write(reportByteArray);
			 outputStream.flush();
			
		} catch (SystemException | BusinessException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            LOGGER.error(e.getCode());
            LOGGER.error(e.getLocalizedMessage());
            response.setStatus(PRECONDITION_FAILED.value());
            try {
            	response.getWriter().print(e.getLocalizedMessage());
            } catch(IOException ioe) {
            	LOGGER.error(DOWNLOAD_ERROR_MSG);
                LOGGER.error(e.getLocalizedMessage(), ioe);            	
				response.setStatus(PRECONDITION_FAILED.value());
            } 
            
		} catch (IOException e) {
        	LOGGER.error(DOWNLOAD_ERROR_MSG);
        	LOGGER.error("", e);
            LOGGER.error(e.getLocalizedMessage(), e);
            try {
    			response.setStatus(PRECONDITION_FAILED.value());
    			response.getWriter().print(createAdminErrorMessage(REPORT_DOWNLOAD_ERROR.getErrorCode(), e.getLocalizedMessage()));
            } catch(IOException ioe) {
            	LOGGER.error(DOWNLOAD_ERROR_MSG);
                LOGGER.error(e.getLocalizedMessage(), ioe);            	
				response.setStatus(PRECONDITION_FAILED.value());
            } 
		}
		 finally{
				try {
					if(outputStream !=  null){
						outputStream.close();
					}
				} catch (IOException e1) {
					LOGGER.error(DOWNLOAD_ERROR_MSG);
		        	LOGGER.error("", e1);
		            LOGGER.error(e1.getLocalizedMessage(), e1);
				}
			}
	}	
}
