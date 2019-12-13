package com.ca.umg.sdc.rest.controller;

import static com.ca.framework.core.requestcontext.RequestContext.getRequestContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ca.framework.core.bo.ModelType;
import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.framework.core.util.ConversionUtil;
import com.ca.framework.core.util.UmgFileProxy;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.tenant.report.usage.bo.UsageExcelReport1;
import com.ca.umg.business.transaction.delegate.TransactionDelegate;
import com.ca.umg.business.transaction.info.AdvanceTransactionFilter;
import com.ca.umg.business.transaction.info.TransactionFilter;
import com.ca.umg.business.transaction.info.TransactionVersionInfo;
import com.ca.umg.business.transaction.info.TransactionWrapper;
import com.ca.umg.business.transaction.mongo.entity.TransactionDocument;
import com.ca.umg.business.transaction.report.TransactionExcelReport;
import com.ca.umg.business.transaction.util.TransactionUtil;
import com.ca.umg.business.util.AdminUtil;
import com.ca.umg.sdc.rest.constants.RestConstants;
import com.ca.umg.sdc.rest.utils.RestResponse;

@Controller
@RequestMapping("/txnDashBoard")
@SuppressWarnings({"PMD.CyclomaticComplexity"})
public class TransactionDashboardController {

    private static final String TXN_ID = "txnId";
    
    private static final String TRANSACTION_MODE = "transactionMode";
    
    private static final String ENDED = "ended";
    
    private static final String STATUS_IO_ERROR = "Error while writing to response outputstream for tenant/model IO data";

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionDashboardController.class);
    
    private static final String SUCCESS_STATUS = "SUCCESS";
    
    private static final String LOG_MESSAGE = "Error while closing response outputstream for tenant IO data for the transaction : ";
   
    private static final String DOWNLOAD_ERROR_MESSAGE_LOG = "Error while dowloading batch report writing to response outputstream for tenant IO for the transaction : ";
   
    private static final String CONTENT_DISPOSITION = "Content-Disposition";
    
    private static final String ERROR = "Error";
    
    private static final String TEXT_FILE_EXT = ".txt";
    @Inject
    private TransactionDelegate transactionDelegate;
    
    @Inject
    private SystemParameterProvider systemParameterProvider;
    
    @Inject
    private UmgFileProxy umgFileProxy;

    @RequestMapping(value = "/listAll", method = RequestMethod.POST)
    @ResponseBody
    public RestResponse<TransactionWrapper> listAll(@RequestParam(value = "txnFilterData") String txnFilterDataJson, 
    		@RequestParam(value = "advanceTransactionFilter") String advanceTransactionFilterJson) {
        LOGGER.info("Entered listAll method");
        RestResponse<TransactionWrapper> response = new RestResponse<TransactionWrapper>();
        TransactionWrapper transactionWrapper = null;
        TransactionFilter txnFilterData = null;
        AdvanceTransactionFilter advanceTransactionFilter = null;
        long requestStartTime = System.currentTimeMillis();
        try {
    		txnFilterData = ConversionUtil.convertJson(txnFilterDataJson, TransactionFilter.class);
    		advanceTransactionFilter = ConversionUtil.convertJson(advanceTransactionFilterJson, AdvanceTransactionFilter.class);
            transactionWrapper = transactionDelegate.searchTransactions(txnFilterData, advanceTransactionFilter);
            response.setError(false);
            response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
            if (transactionWrapper != null && CollectionUtils.isEmpty(transactionWrapper.getTransactionDocumentInfos())) {
                response.setMessage(RestConstants.NO_TRANSACTION_RECORDS_FOUND);
            }
            response.setResponse(transactionWrapper);
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setErrorCode(e.getCode());
            response.setError(true);
            response.setMessage(e.getLocalizedMessage());
        }
        long requestEndTime = System.currentTimeMillis();
        LOGGER.info("Time taken to Proccess the search request "+(requestEndTime-requestStartTime)+"ms");
        return response;
    }
    
    @RequestMapping(value = "/listAllDefault", method = RequestMethod.POST)
    @ResponseBody
    public RestResponse<TransactionWrapper> listAllDefault(@RequestParam(value = "pagesize") String pagesize) {
        LOGGER.info("Entered listAllDefault method");
        Integer pageSize = Integer.parseInt(pagesize);
        RestResponse<TransactionWrapper> response = new RestResponse<TransactionWrapper>();
        TransactionWrapper transactionWrapper = null;
        long startTime = System.currentTimeMillis();
        long endTime = System.currentTimeMillis();
        try {
            transactionWrapper = transactionDelegate.searchDefaultTransactions(pageSize);
            endTime = System.currentTimeMillis();
            LOGGER.info("///////////////////// TOTAL TIME TAKEN TO FETCH SEARCH RESULT AT REST LAYER //////////////////////// : "+(endTime-startTime) + " ms");
            response.setError(false);
            response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
            if (transactionWrapper != null && CollectionUtils.isEmpty(transactionWrapper.getTransactionDocumentInfos())) {
                response.setMessage(RestConstants.NO_TRANSACTION_RECORDS_FOUND);
            }
            response.setResponse(transactionWrapper);
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setErrorCode(e.getCode());
            response.setError(true);
            response.setMessage(e.getLocalizedMessage());
        }
        return response;
    }
    
    @RequestMapping(value="/getOperatorList",method = RequestMethod.GET)
    @ResponseBody
    public RestResponse<List<String>> getOperatorList () {
    	 RestResponse<List<String>> response = new RestResponse<>();
         List<String> operatorList = null;
         try {
        	 operatorList = transactionDelegate.getOperatorList();
             if (operatorList != null) {
                 response.setResponse(operatorList);

             } else {
                 response.setMessage("operators not found");
             }
         } catch (BusinessException | SystemException e) {
             LOGGER.error(e.getLocalizedMessage(), e);
             response.setErrorCode(e.getCode());
             response.setError(true);
             response.setMessage(e.getLocalizedMessage());
         }
         return response;
    }


    @RequestMapping(value = "/downloadTenantIO/{txnId}", method = RequestMethod.GET)
    @ResponseBody
    public void downloadTenantIOData(@PathVariable(TXN_ID) String txnId, HttpServletResponse response)// NOPMD
    {
        LOGGER.info("started tenantIO download for txnId:" + txnId);
        try {
            TransactionDocument txnDocument = transactionDelegate.getTntIoDocuments(txnId);
            String zipFileName;
            String inputFileName;
            String outputFileName;
            Map<String, Object> inputData = null;
            Map<String, Object> outputData = null;
            if (txnDocument != null) {
            	if(StringUtils.equalsIgnoreCase(txnDocument.getTransactionMode(),BusinessConstants.BULK) 
            			&& StringUtils.equalsIgnoreCase(txnDocument.getChannel(),BusinessConstants.HTTP)) {
            		zipFileName = txnDocument.getClientTransactionID() + BusinessConstants.HYPHEN + txnDocument.getBulkOnlineTimeStamp() + BusinessConstants.HYPHEN + BusinessConstants.TENANT_IO;
                    inputFileName = txnDocument.getClientTransactionID()  + BusinessConstants.HYPHEN + txnDocument.getBulkOnlineTimeStamp()  + BusinessConstants.HYPHEN + BusinessConstants.TENANT_IP + BusinessConstants.EXTN_JSON;
                    outputFileName = txnDocument.getClientTransactionID()  + BusinessConstants.HYPHEN + txnDocument.getBulkOnlineTimeStamp()  + BusinessConstants.HYPHEN + BusinessConstants.TENANT_OP + BusinessConstants.EXTN_JSON;
                    String sanBase = umgFileProxy.getSanPath(SystemConstants.SAN_BASE);
                    File inputDataFile = getFile(sanBase, inputFileName, BusinessConstants.ARCHIEVE_FOLDER);
                    File outputDataFile = getFile(sanBase, outputFileName, BusinessConstants.OUTPUT_ARCHIVE_FOLDER);
                    byte[] inputDataBytes = Files.readAllBytes(inputDataFile.toPath());
                    byte[] outputDataBytes = null ;
                    if(outputDataFile.exists()){
                    	outputDataBytes = Files.readAllBytes(outputDataFile.toPath());
                    }else{
                    	outputDataFile = getFile(sanBase, outputFileName, BusinessConstants.OUTPUT_FOLDER);
                    	if(outputDataFile.exists()){
                    		outputDataBytes = Files.readAllBytes(outputDataFile.toPath());                    		
                    	}else{                    		
                    		outputDataBytes = AdminUtil.createTempJson(outputFileName);                  		
                    	}
                    }
                    if(inputDataBytes != null && inputDataBytes.length > 0){
                    	inputData = ConversionUtil.convertJson(inputDataBytes, Map.class);
                    } 
                    if(outputDataBytes != null && outputDataBytes.length > 0){
                    	outputData = ConversionUtil.convertJson(outputDataBytes, Map.class);
                    }
                    writeDataAndDownloadIO(response, inputData, outputData, inputFileName,
                            outputFileName, zipFileName + BusinessConstants.EXTN_ZIP);
            	}else if(StringUtils.equalsIgnoreCase(txnDocument.getTransactionMode(),BusinessConstants.BULK) 
            			&& StringUtils.equalsIgnoreCase(txnDocument.getChannel(),BusinessConstants.FILE)) {
            		zipFileName = TransactionUtil.getFileName(txnDocument.getClientTransactionID().replaceAll(BusinessConstants.SLASH, BusinessConstants.EMPTY_STRING),
                            txnDocument.getRunAsOfDate(), BusinessConstants.TENANT_IO);
                    inputFileName = TransactionUtil.getFileName(txnDocument.getClientTransactionID().replaceAll(BusinessConstants.SLASH, BusinessConstants.EMPTY_STRING),
                            txnDocument.getRunAsOfDate(), BusinessConstants.TENANT_IP);
                    outputFileName = TransactionUtil.getFileName(txnDocument.getClientTransactionID().replaceAll(BusinessConstants.SLASH, BusinessConstants.EMPTY_STRING),
                            txnDocument.getRunAsOfDate(), BusinessConstants.TENANT_OP);
                    
                    String inputSanBaseFileName=String.valueOf(((Map<String, Object>)txnDocument.getTenantInput().get(BusinessConstants.HEADER)).get(BusinessConstants.FILENAME));
                    
            		String sanBase = umgFileProxy.getSanPath(SystemConstants.SAN_BASE);
                    File inputDataFile = getBulkFile(sanBase, inputSanBaseFileName, BusinessConstants.ARCHIEVE_FOLDER);
                    
                    String outputSanBaseFileName = null;
                    if(StringUtils.equalsIgnoreCase(txnDocument.getStatus(), ERROR)){
                    	outputSanBaseFileName=inputSanBaseFileName.replace(BusinessConstants.EXTN_JSON,BusinessConstants.CHAR_HYPHEN+  ERROR+ BusinessConstants.EXTN_JSON);
                    } else {
                    	outputSanBaseFileName=inputSanBaseFileName.replace(BusinessConstants.EXTN_JSON,BusinessConstants.CHAR_HYPHEN+  BusinessConstants.BULK_FILE_OUTPUT+ BusinessConstants.EXTN_JSON);
                    }
                    
                    File outputDataFile = getBulkFile(sanBase, outputSanBaseFileName, BusinessConstants.OUTPUT_FOLDER);
                    byte[] inputDataBytes = Files.readAllBytes(inputDataFile.toPath());
                    byte[] outputDataBytes = null ;
                    if(outputDataFile.exists()){
                    	outputDataBytes = Files.readAllBytes(outputDataFile.toPath());
                    }else{
                    	outputSanBaseFileName=inputSanBaseFileName.replace(BusinessConstants.EXTN_JSON,BusinessConstants.CHAR_HYPHEN+  BusinessConstants.BULK_STATUS_ERROR+ BusinessConstants.EXTN_JSON);
                        
                    	outputDataFile = getBulkFile(sanBase, outputFileName, BusinessConstants.OUTPUT_FOLDER);
                    	if(outputDataFile.exists()){
                    		outputDataBytes = Files.readAllBytes(outputDataFile.toPath());                    		
                    	}else{                    		
                    		outputDataBytes = AdminUtil.createTempJson(outputFileName);                  		
                    	}
                    }
                    if(inputDataBytes != null && inputDataBytes.length > 0){
                    	inputData = ConversionUtil.convertJson(inputDataBytes, Map.class);
                    } 
                    if(outputDataBytes != null && outputDataBytes.length > 0){
                    	outputData = ConversionUtil.convertJson(outputDataBytes, Map.class);
                    }
                    writeDataAndDownloadIO(response, inputData, outputData, inputFileName,
                            outputFileName, zipFileName + BusinessConstants.EXTN_ZIP);
            	}
            	else {
            		//replace all the occurrence of // with blank for jira ticket UMG-8904
            		zipFileName = TransactionUtil.getFileName(txnDocument.getClientTransactionID().replaceAll(BusinessConstants.SLASH, BusinessConstants.EMPTY_STRING),
                            txnDocument.getRunAsOfDate(), BusinessConstants.TENANT_IO);
                    inputFileName = TransactionUtil.getFileName(txnDocument.getClientTransactionID().replaceAll(BusinessConstants.SLASH, BusinessConstants.EMPTY_STRING),
                            txnDocument.getRunAsOfDate(), BusinessConstants.TENANT_IP);
                    if(StringUtils.equalsIgnoreCase(txnDocument.getStatus(), ERROR)){
                    	outputFileName = TransactionUtil.getErrorFileName(txnDocument.getClientTransactionID().replaceAll(BusinessConstants.SLASH, BusinessConstants.EMPTY_STRING),
                                txnDocument.getRunAsOfDate(), BusinessConstants.TENANT_OP);
                    } else {
                    	outputFileName = TransactionUtil.getFileName(txnDocument.getClientTransactionID().replaceAll(BusinessConstants.SLASH, BusinessConstants.EMPTY_STRING),
                                txnDocument.getRunAsOfDate(), BusinessConstants.TENANT_OP);
                    }
                    writeDataAndDownloadIO(response, txnDocument.getTenantInput(), txnDocument.getTenantOutput(), inputFileName,
                            outputFileName, zipFileName + BusinessConstants.EXTN_ZIP);
            	}
            } else {
                writeErrorData(response, txnId, null);
            }
        } catch (SystemException | IOException se) {
            LOGGER.error("Error while writing to response outputstream for tenant IO for the transaction : " + txnId, se);
            if (se.getLocalizedMessage() != null) {
                writeErrorData(response, txnId, se.getLocalizedMessage());
            } else {
                writeErrorData(response, txnId, se.getMessage());

            }

        } finally {
            try {
                response.getOutputStream().close();
            } catch (IOException e) {
                LOGGER.error(LOG_MESSAGE + txnId, e);
            }
        }
        LOGGER.info("tenantIO download for txnId:" + txnId + ENDED);
    }
    
    private static File getFile(String sanBase, String fileName, String folderName) throws SystemException {
        File file = null;
        if (StringUtils.isNotEmpty(sanBase)) {
        	StringBuilder buffer = new StringBuilder(sanBase);
            buffer.append(File.separatorChar).append(getRequestContext().getTenantCode())
                    .append(File.separatorChar).append(BusinessConstants.BULK_HTTP).append(File.separatorChar)
                    .append(folderName).append(File.separatorChar).append(fileName);
            String absoluteFileName = buffer.toString();
            file = new File(absoluteFileName);
        } else {
            LOGGER.error("San base not available");
        }
        return file;
    }
    private static File getBulkFile(String sanBase, String fileName, String folderName) throws SystemException {
        File file = null;
        if (StringUtils.isNotEmpty(sanBase)) {
        	StringBuilder buffer = new StringBuilder(sanBase);
            buffer.append(File.separatorChar).append(getRequestContext().getTenantCode())
                    .append(File.separatorChar).append(BusinessConstants.BULK_FILE).append(File.separatorChar)
                    .append(folderName).append(File.separatorChar).append(fileName);
            String absoluteFileName = buffer.toString();
            file = new File(absoluteFileName);
        } else {
            LOGGER.error("San base not available");
        }
        return file;
    }

    @RequestMapping(value = "/downloadModelIO/{txnId}/{transactionMode}", method = RequestMethod.GET)
    @ResponseBody
    public void downloadModelIOData(@PathVariable(TXN_ID) String txnId, @PathVariable(TRANSACTION_MODE) String transactionMode, HttpServletResponse response) {
    	LOGGER.info("started modelIO download for txnId:" + txnId);
    	try {
    		final TransactionDocument txnDocument = transactionDelegate.getModelIoDocuments(txnId);
    		String outputFileName;
    		String zipFileName;
    		String inputFileName;
    		Map<String, Object> inputData = null;
            Map<String, Object> outputData = null;
    		if (txnDocument != null) {
    			if(StringUtils.equalsIgnoreCase(txnDocument.getTransactionMode(),BusinessConstants.BULK) 
            			&& StringUtils.equalsIgnoreCase(txnDocument.getChannel(),BusinessConstants.HTTP)) {
    				zipFileName = txnDocument.getClientTransactionID() + BusinessConstants.HYPHEN + txnDocument.getBulkOnlineTimeStamp() + BusinessConstants.HYPHEN + BusinessConstants.MODEL_IO;	               
        			outputFileName = txnDocument.getClientTransactionID() + BusinessConstants.HYPHEN + txnDocument.getBulkOnlineTimeStamp() + BusinessConstants.HYPHEN + BusinessConstants.MODEL_OP + BusinessConstants.EXTN_JSON;
        			inputFileName = txnDocument.getClientTransactionID() + BusinessConstants.HYPHEN + txnDocument.getBulkOnlineTimeStamp() + BusinessConstants.HYPHEN + BusinessConstants.MODEL_IP + BusinessConstants.EXTN_JSON;
        			String sanBase = umgFileProxy.getSanPath(SystemConstants.SAN_BASE);
        			File inputDataFile = getFile(sanBase, inputFileName, BusinessConstants.ARCHIEVE_FOLDER);
                    File outputDataFile = getFile(sanBase, outputFileName, BusinessConstants.OUTPUT_ARCHIVE_FOLDER);
                    byte[] inputDataBytes = Files.readAllBytes(inputDataFile.toPath());
                    byte[] outputDataBytes = null;
                    if(txnDocument.getErrorCode()!=null && !txnDocument.getErrorCode().startsWith("RVE")){
	                    if(outputDataFile.exists()){
	                    	outputDataBytes = Files.readAllBytes(outputDataFile.toPath());
	                    }else{
	                    	outputDataFile = getFile(sanBase, outputFileName, BusinessConstants.OUTPUT_FOLDER);  
	                    	if(outputDataFile.exists()){
	                    		outputDataBytes = Files.readAllBytes(outputDataFile.toPath());
	                    	}else{
	                    		outputDataBytes = AdminUtil.createTempJson(outputFileName);
	                    	}
	                    }
                    }
                    if(inputDataBytes != null && inputDataBytes.length > 0){
                    	inputData = ConversionUtil.convertJson(inputDataBytes, Map.class);
                    }
                    if(outputDataBytes != null && outputDataBytes.length > 0){
                    	outputData = ConversionUtil.convertJson(outputDataBytes, Map.class);
                    } 
                    writeDataAndDownloadIO(response, inputData, outputData, inputFileName,
                            outputFileName, zipFileName + BusinessConstants.EXTN_ZIP);
    			} else {
    				zipFileName = TransactionUtil.getFileName(txnDocument.getClientTransactionID(), txnDocument.getRunAsOfDate(), BusinessConstants.MODEL_IO);	               
    				outputFileName = TransactionUtil.getFileName(txnDocument.getClientTransactionID(), txnDocument.getRunAsOfDate(), BusinessConstants.MODEL_OP);
    				inputFileName = TransactionUtil.getFileName(txnDocument.getClientTransactionID(), txnDocument.getRunAsOfDate(), BusinessConstants.MODEL_IP);
    				if (StringUtils.equalsIgnoreCase(txnDocument.getTransactionMode(),BusinessConstants.BULK)) {
    					final byte[] bulkOutputByte = transactionDelegate.getBulkModelErrorOuput(txnDocument.getTransactionId());
    					writeDataAndDownloadIOFile(response, txnDocument.getModelInput(), inputFileName, outputFileName, zipFileName + BusinessConstants.EXTN_ZIP, bulkOutputByte);
    				} else {
    					writeDataAndDownloadIO(response, txnDocument.getModelInput(), txnDocument.getModelOutput(), inputFileName, outputFileName, zipFileName + BusinessConstants.EXTN_ZIP);
    				}
    			}
    		} else {
    			writeErrorData(response, txnId, null);    			
    		}
    	} catch (SystemException | IOException se) {
    		LOGGER.error("Error while writing to response outputstream for model IO data for the transaction : " + txnId, se);
    		if (se.getLocalizedMessage() != null) {
    			writeErrorData(response, txnId, se.getLocalizedMessage());
    		} else {
    			writeErrorData(response, txnId, se.getMessage());
    		}

    	} finally {
    		try {
    			response.getOutputStream().close();
    		} catch (IOException e) {
    			LOGGER.error("Error while closing response outputstream for tenant/model IO ", e);
    		}
    	}
    	LOGGER.info("modelIO download for txnId:" + txnId + ENDED);
    }

    @RequestMapping(value = "/downloadModelAndTntIO", method = RequestMethod.GET)
    @ResponseBody
    public void downloadModelAndTntIO(@RequestParam("idList") String idList, @RequestParam("apiName") String apiName, 
    		@RequestParam("status") String status, HttpServletResponse response)
            throws BusinessException, SystemException {
    	createZipForTntAndModelIOForError(idList,response,apiName+"_"+status+".zip");
    }
    
    //keeping this commented code for future use if we need to use post for download 
    @RequestMapping(value = "/downloadSelectedItems", method = RequestMethod.POST)
    @ResponseBody
    public void  downloadSelectedItems(@RequestParam("idList") String idList, HttpServletResponse response)
            throws BusinessException, SystemException {
    	createZipForTntAndModelIO(idList,response,BusinessConstants.TENANT_OR_MODEL_IO);
    }
    
    @RequestMapping(value = "/downloadUsageReportByFilter", method = RequestMethod.POST)
	@ResponseBody
	public void downloadUsageReportByFilter(@RequestParam(value = "txnFilterData") String txnFilterDataJson, 
    		@RequestParam(value = "advanceTransactionFilter") String advanceTransactionFilterJson,
			final HttpServletResponse response) {
		
		final String tenantCode = getRequestContext().getTenantCode();

		try {
			TransactionWrapper transactionWrapper = null;
	        TransactionFilter txnFilterData = null;
	        AdvanceTransactionFilter advanceTransactionFilter = null;
	    	txnFilterData = ConversionUtil.convertJson(txnFilterDataJson, TransactionFilter.class);
	    	advanceTransactionFilter = ConversionUtil.convertJson(advanceTransactionFilterJson, AdvanceTransactionFilter.class);
	        transactionWrapper = transactionDelegate.searchTransactions(txnFilterData, advanceTransactionFilter);
			
			//LOGGER.info("UsageReportFilter:" + usageReportFilter);
			//final SqlRowSet sqlRowSet = tenantUsageReportDelegate.loadTransactionsRowSetByFilter(usageReportFilter);
			final UsageExcelReport1 report = new UsageExcelReport1(transactionWrapper.getTransactionDocumentInfos());
			Long startTime =  AdminUtil.getMillisFromEstToUtc(txnFilterData.getRunAsOfDateFromString(), null);
		    Long endTime = AdminUtil.getMillisFromEstToUtc(txnFilterData.getRunAsOfDateToString(), null);
		    
		    if (endTime == null && startTime == null) {
		    	startTime = System.currentTimeMillis();
		    } else if (endTime == null ) {
		    	endTime = System.currentTimeMillis();
		    }
			//if (!tenantUsageReportDelegate.getUsageSearchRequestCancelStatusFromCache(usageReportFilter.getCancelRequestId())) {
				final String reportFileName = report
						.getReportFileName(tenantCode, startTime, endTime);
				setResponseProperties(response, reportFileName);
				report.createReport(response.getOutputStream());
				response.flushBuffer();
			//}
		} catch (IOException se) {
			LOGGER.error("Error while writing Tenant Usage Report to response outputstream ", se);
		} catch (SystemException exception) {
			LOGGER.error(STATUS_IO_ERROR, exception);
		} catch (BusinessException be) {
			LOGGER.error(DOWNLOAD_ERROR_MESSAGE_LOG, be);
		} finally {
			try {
				response.getOutputStream().close();
			} catch (IOException e) {
				LOGGER.error(LOG_MESSAGE + e);
			}
		}
	}
    
    @RequestMapping(value = "/downloadExecReportByFilter", method = RequestMethod.POST)
   	@ResponseBody
   	public void downloadExeReportByFilter(@RequestParam(value = "txnFilterData") String txnFilterDataJson, 
       		@RequestParam(value = "advanceTransactionFilter") String advanceTransactionFilterJson,
   			final HttpServletResponse response) {
   		
   		final String tenantCode = getRequestContext().getTenantCode();

   		try {
   			TransactionWrapper transactionWrapper = null;
   	        TransactionFilter txnFilterData = null;
   	        AdvanceTransactionFilter advanceTransactionFilter = null;
   	    	txnFilterData = ConversionUtil.convertJson(txnFilterDataJson, TransactionFilter.class);
   	    	advanceTransactionFilter = ConversionUtil.convertJson(advanceTransactionFilterJson, AdvanceTransactionFilter.class);
   	        transactionWrapper = transactionDelegate.searchTransactions(txnFilterData, advanceTransactionFilter);
   			
   			//LOGGER.info("UsageReportFilter:" + usageReportFilter);
   			//final SqlRowSet sqlRowSet = tenantUsageReportDelegate.loadTransactionsRowSetByFilter(usageReportFilter);
   			final UsageExcelReport1 report = new UsageExcelReport1(transactionWrapper.getTransactionDocumentInfos());
   			Long startTime =  AdminUtil.getMillisFromEstToUtc(txnFilterData.getRunAsOfDateFromString(), null);
   		    Long endTime = AdminUtil.getMillisFromEstToUtc(txnFilterData.getRunAsOfDateToString(), null);
   		    
   		    if (endTime == null && startTime == null) {
   		    	startTime = System.currentTimeMillis();
   		    } else if (endTime == null ) {
   		    	endTime = System.currentTimeMillis();
   		    }
   			//if (!tenantUsageReportDelegate.getUsageSearchRequestCancelStatusFromCache(usageReportFilter.getCancelRequestId())) {
   				final String reportFileName = report
   						.getReportFileName(tenantCode, startTime, endTime);
   				setResponseProperties(response, reportFileName);
   				report.createExeReport(response.getOutputStream());
   				response.flushBuffer();
   			//}
   		} catch (IOException se) {
   			LOGGER.error("Error while writing Tenant Usage Report to response outputstream ", se);
   		} catch (SystemException exception) {
   			LOGGER.error(STATUS_IO_ERROR, exception);
   		} catch (BusinessException be) {
   			LOGGER.error(DOWNLOAD_ERROR_MESSAGE_LOG, be);
   		} finally {
   			try {
   				response.getOutputStream().close();
   			} catch (IOException e) {
   				LOGGER.error(LOG_MESSAGE + e);
   			}
   		}
   	}
       
    private void createZipForTntAndModelIO (String idList, HttpServletResponse response, String zipFileName) {
    	LOGGER.info("started modelandTenantIO download for txnIds:" + idList);
    	ZipOutputStream zos = null;
    	try {
    		zos = new ZipOutputStream(response.getOutputStream());
    		StringTokenizer txnIdsTokenizer = new StringTokenizer(idList, ",");
    		while (txnIdsTokenizer.hasMoreTokens()) {
    			String txnId = txnIdsTokenizer.nextToken();
    			String[] ary = txnId.split(":");
    			TransactionDocument txnDocument = transactionDelegate.getTntModelIoDocuments(ary[0]);
    			if (txnDocument != null) {
    				setResponseHeader(response, zipFileName);
    				if(ary[1].equalsIgnoreCase(ModelType.BULK.getType()) ){
    					if(ary[2].equalsIgnoreCase(SUCCESS_STATUS)){
    						writeDatatoZipFileForBulk(zos, txnDocument);
    					}
    					else{
    						writeDatatoZipFileForBulkError(zos, txnDocument);
    					}
    				}
    				else{
    					writeDatatoZipFile(zos, txnDocument);
    				}
    			} else {
    				writeErrorData(response, txnId, null);
    			}
    		}
    	} catch (SystemException | IOException exception) {
    		LOGGER.error(STATUS_IO_ERROR, exception);
    		if (exception.getLocalizedMessage() != null) {
    			writeErrorData(response, idList, exception.getLocalizedMessage());
    		} else {
    			writeErrorData(response, idList, exception.getMessage());

    		}

    	} finally {
    		try {
    			if (zos != null) {
    				zos.finish();
    				zos.close();
    			}
    			response.getOutputStream().flush();
    			response.getOutputStream().close();
    		} catch (IOException e) {
    			LOGGER.error("Error while closing response outputstream for tenant/model IO ", e);
    		}
    	}
    	LOGGER.info("modelandTenantIO download for txnIds:" + idList + ENDED);
    }
    
    private void createZipForTntAndModelIOForError (String idList, HttpServletResponse response, String zipFileName) {
    	LOGGER.info("started modelandTenantIO download for txnIds:" + idList);
    	ZipOutputStream zos = null;
    	try {
    		zos = new ZipOutputStream(response.getOutputStream());
    		StringTokenizer txnIdsTokenizer = new StringTokenizer(idList, ",");
    		while (txnIdsTokenizer.hasMoreTokens()) {
    			String txnId = txnIdsTokenizer.nextToken();
    			TransactionDocument txnDocument = transactionDelegate.getTxnDocument(txnId);
    			if (txnDocument != null) {
    				setResponseHeader(response, zipFileName);
    				writeDatatoZipFile(zos, txnDocument);
    			} else {
    				writeErrorData(response, txnId, null);
    			}
    		}
    	} catch (SystemException | IOException exception) {
    		LOGGER.error(STATUS_IO_ERROR, exception);
    		if (exception.getLocalizedMessage() != null) {
    			writeErrorData(response, idList, exception.getLocalizedMessage());
    		} else {
    			writeErrorData(response, idList, exception.getMessage());

    		}

    	} finally {
    		try {
    			if (zos != null) {
    				zos.finish();
    				zos.close();
    			}
    			response.getOutputStream().flush();
    			response.getOutputStream().close();
    		} catch (IOException e) {
    			LOGGER.error("Error while closing response outputstream for tenant/model IO ", e);
    		}
    	}
    	LOGGER.info("modelandTenantIO download for txnIds:" + idList + ENDED);

    }


   
    private void writeErrorData(HttpServletResponse response, String txnId, String msg) {
        try {
            String headerValue = String.format("attachment; filename=\"%s\"", "error_" + txnId + TEXT_FILE_EXT);
            response.setHeader(CONTENT_DISPOSITION, headerValue);
            String errorMsg = null;
            if (msg == null) {
                errorMsg = "No Data found for the transactionId's :" + txnId;
                response.getOutputStream().write(errorMsg.getBytes());
            } else {
                errorMsg = msg;
                response.getOutputStream().write(errorMsg.getBytes());
            }
        } catch (IOException excep) {
            LOGGER.error("Error while Writting error data  ", excep);
        }
        finally {
        	try {
				if(response.getOutputStream() != null){
					response.getOutputStream().close();
				}
			} catch (IOException e) {
				 LOGGER.error("Error while Writting error data  ", e);
			}
        }
    }

    private void writeDatatoZipFile(ZipOutputStream zos, TransactionDocument txnDocument) throws IOException, SystemException {
        String tenantIpFileName = TransactionUtil.getFileName(txnDocument.getClientTransactionID(), txnDocument.getRunAsOfDate(),
                BusinessConstants.TENANT_IP);
        String tenantOpFileName = TransactionUtil.getFileName(txnDocument.getClientTransactionID(), txnDocument.getRunAsOfDate(),
                BusinessConstants.TENANT_OP);
        String modelIpFileName = TransactionUtil.getFileName(txnDocument.getClientTransactionID(), txnDocument.getRunAsOfDate(),
                BusinessConstants.MODEL_IP);
        String modelOpFileName = TransactionUtil.getFileName(txnDocument.getClientTransactionID(), txnDocument.getRunAsOfDate(),
                BusinessConstants.MODEL_OP);

        ObjectMapper mapper = new ObjectMapper();

        TransactionUtil.addToZipFile(tenantIpFileName, mapper.writeValueAsBytes(txnDocument.getTenantInput()), zos);
        TransactionUtil.addToZipFile(tenantOpFileName, mapper.writeValueAsBytes(txnDocument.getTenantOutput()), zos);
        TransactionUtil.addToZipFile(modelIpFileName, mapper.writeValueAsBytes(txnDocument.getModelInput()), zos);
        TransactionUtil.addToZipFile(modelOpFileName, mapper.writeValueAsBytes(txnDocument.getModelOutput()), zos);
    }

    private void writeDataAndDownloadIO(HttpServletResponse response, Map<String, Object> inputData,
            Map<String, Object> outputData, String inputFileName, String outputFileName, String zipFileName) throws IOException,
            SystemException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValueAsBytes(inputData);
        ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
        setResponseHeader(response, zipFileName);
        try {
        	TransactionUtil.addToZipFile(inputFileName, mapper.writeValueAsBytes(inputData), zos);
        	TransactionUtil.addToZipFile(outputFileName, mapper.writeValueAsBytes(outputData), zos);
        } finally {
            zos.finish();
            zos.close();
        }
        response.getOutputStream().flush();
    }

    private void setResponseHeader(HttpServletResponse response, String zipFileName) {
        response.setHeader("Content-Type", "application/zip");
        response.setHeader(CONTENT_DISPOSITION, "attachment;filename=" + zipFileName);
    }

    /**
     * This method will accept the TransactionFilter data in the paramListCSV and return the Excel sheet with result
     * 
     * @param paramListCSV
     * @param response
     */
    @RequestMapping(value = "/downloadBatchReport")
    @ResponseBody
    public void downloadBatchReport(// NOPMD
            @RequestParam("clientTransactionID") String clientTransactionID, @RequestParam("libraryName") String libraryName,
            @RequestParam("runAsOfDateFromString") String runAsOfDateFromString,
            @RequestParam("runAsOfDateToString") String runAsOfDateToString,
            @RequestParam("tenantModelName") String tenantModelName, @RequestParam("fullVersion") String fullVersion,
            @RequestParam("showTestTxn") Boolean showTestTxn, @RequestParam("errorType") String errorType,
            @RequestParam("errorDescription") String errorDescription, @RequestParam("batchId") String batchId,
            HttpServletResponse response) {// NOPMD

        // setting TransactionFilter object
        TransactionFilter transactionFilter = new TransactionFilter();
        transactionFilter.setClientTransactionID(clientTransactionID);
        transactionFilter.setLibraryName(libraryName);
        transactionFilter.setRunAsOfDateFromString(runAsOfDateFromString);
        transactionFilter.setRunAsOfDateToString(runAsOfDateToString);
        transactionFilter.setTenantModelName(tenantModelName);
        transactionFilter.setFullVersion(fullVersion);
        // TODO do changes for batch accordingly 
        //transactionFilter.setShowTestTxn(showTestTxn);
        transactionFilter.setErrorType(errorType);
        transactionFilter.setErrorDescription(errorDescription);
        transactionFilter.setBatchId(batchId);

        try {
            LOGGER.info("transactionFilter:" + transactionFilter);
            final String tenantCode = getRequestContext().getTenantCode();
            final SqlRowSet sqlRowSet = transactionDelegate.loadTransactionsRowSet(transactionFilter, tenantCode);
            final TransactionExcelReport report = new TransactionExcelReport(sqlRowSet);
            final String reportFileName = report.getReportFileName(tenantCode, transactionFilter.getRunAsOfDateFrom(),
                    transactionFilter.getRunAsOfDateTo());
            setResponseProperties(response, reportFileName);
            report.createReport(response.getOutputStream());
            response.flushBuffer();
        } catch (IOException se) {
            LOGGER.error(
                    DOWNLOAD_ERROR_MESSAGE_LOG,
                    se);
        } catch (SystemException exception) {
            LOGGER.error(STATUS_IO_ERROR, exception);
        } catch (BusinessException be) {
            LOGGER.error(
            		DOWNLOAD_ERROR_MESSAGE_LOG,
                    be);
        } finally {
            try {
                response.getOutputStream().close();
            } catch (IOException e) {
                LOGGER.error(LOG_MESSAGE + e);
            }
        }
    }

    private void setResponseProperties(final HttpServletResponse response, final String reportFileName) {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader(CONTENT_DISPOSITION, "attachment; filename=" + reportFileName);
    }
    
    @RequestMapping(value = "/versionDetails", method = RequestMethod.POST)
    @ResponseBody
    public RestResponse<TransactionVersionInfo> getVersionDetails(// NOPMD
            @RequestParam("versionName") String versionName, @RequestParam("fullVersion") String fullVersion) {
    	LOGGER.info("Entered get version details method");
        RestResponse<TransactionVersionInfo> response = new RestResponse<TransactionVersionInfo>();
        TransactionVersionInfo versionInfo = new TransactionVersionInfo();
        try {
        	versionInfo = transactionDelegate.getTransactionVersionInfo(versionName, fullVersion);
            response.setError(false);
            response.setResponse(versionInfo);
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setErrorCode(e.getCode());
            response.setError(true);
            response.setMessage(e.getLocalizedMessage());
        }
        return response;
    }
    
    @RequestMapping(value="/getSelectedRecordsCountLimit",method = RequestMethod.GET)
    @ResponseBody
    public RestResponse<String> getSelectedRecordsCountLimit () {
    	 RestResponse<String> response = new RestResponse<>();
         String countLimit = null;
         try {
        	 countLimit = systemParameterProvider.getParameter(BusinessConstants.TRAN_DSHBRD_SEL_RECRD_LIMIT);
             if (countLimit != null) {
                 response.setResponse(countLimit);

             } else {
            	 Integer numberHundred = BusinessConstants.NUMBER_ONE_HUNDRED;
            	 response.setResponse(numberHundred.toString());
             }
         } catch (Exception e) {// NOPMD
             LOGGER.error(e.getLocalizedMessage(), e);
             //response.setErrorCode(e.getCode());
             response.setError(true);
             response.setMessage(e.getLocalizedMessage());
         }
         return response;
    }
    
    private void writeDataAndDownloadIOFile(final HttpServletResponse response, Map<String, Object> inputData, final String inputFileName, final String outputFileName, final String zipFileName, final byte[] output) 
    		throws IOException, SystemException {
    	ObjectMapper mapper = new ObjectMapper();
        mapper.writeValueAsBytes(inputData);
        ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
        setResponseHeader(response, zipFileName);
        try {
        	TransactionUtil.addToZipFile(inputFileName, mapper.writeValueAsBytes(inputData), zos);
            TransactionUtil.addToZipFile(outputFileName, output, zos);
        } finally {
            zos.finish();
            zos.close();
        }
        response.getOutputStream().flush();
    }
    private void writeDatatoZipFileForBulk(ZipOutputStream zos, TransactionDocument txnDocument) throws IOException, SystemException {
    	String outputFileName;
		//String zipFileName;
		String inputFileName;
    	String tenantIpFileName = TransactionUtil.getFileName(txnDocument.getClientTransactionID(), txnDocument.getRunAsOfDate(), BusinessConstants.TENANT_IP);
        String tenantOpFileName = TransactionUtil.getFileName(txnDocument.getClientTransactionID(), txnDocument.getRunAsOfDate(), BusinessConstants.TENANT_OP);

        if(StringUtils.equalsIgnoreCase(txnDocument.getTransactionMode(),BusinessConstants.BULK) 
    			&& StringUtils.equalsIgnoreCase(txnDocument.getChannel(),BusinessConstants.HTTP)) {
    		//zipFileName = txnDocument.getClientTransactionID() + BusinessConstants.HYPHEN + txnDocument.getBulkOnlineTimeStamp() + BusinessConstants.HYPHEN + BusinessConstants.TENANT_IO;
            inputFileName = txnDocument.getClientTransactionID()  + BusinessConstants.HYPHEN + txnDocument.getBulkOnlineTimeStamp()  + BusinessConstants.HYPHEN + BusinessConstants.TENANT_IP + BusinessConstants.EXTN_JSON;
            outputFileName = txnDocument.getClientTransactionID()  + BusinessConstants.HYPHEN + txnDocument.getBulkOnlineTimeStamp()  + BusinessConstants.HYPHEN + BusinessConstants.TENANT_OP + BusinessConstants.EXTN_JSON;
            String sanBase = umgFileProxy.getSanPath(SystemConstants.SAN_BASE);
            File inputDataFile = getFile(sanBase, inputFileName, BusinessConstants.ARCHIEVE_FOLDER);
            File outputDataFile = getFile(sanBase, outputFileName, BusinessConstants.OUTPUT_ARCHIVE_FOLDER);
            byte[] inputDataBytes = Files.readAllBytes(inputDataFile.toPath());
            byte[] outputDataBytes = null ;
            if(outputDataFile.exists()){
	            	outputDataBytes = Files.readAllBytes(outputDataFile.toPath());
	            }else{
	            	outputDataFile = getFile(sanBase, outputFileName, BusinessConstants.OUTPUT_FOLDER);    
	            	if(outputDataFile.exists()){
	            		outputDataBytes = AdminUtil.createTempJson(outputFileName);
	            		
	            	}
	            }
	            if(inputDataBytes != null && inputDataBytes.length > 0){
	            	 TransactionUtil.addToZipFile(tenantIpFileName, inputDataBytes, zos);
	            } 
	            if(outputDataBytes != null && outputDataBytes.length > 0){
	            	TransactionUtil.addToZipFile(tenantOpFileName, outputDataBytes, zos);
	            }
        }  else {        
        ObjectMapper mapper = new ObjectMapper();
        TransactionUtil.addToZipFile(tenantIpFileName, mapper.writeValueAsBytes(txnDocument.getTenantInput()), zos);
        TransactionUtil.addToZipFile(tenantOpFileName, mapper.writeValueAsBytes(txnDocument.getTenantOutput()), zos);
        }
    }
    private void  writeDatatoZipFileForBulkError(ZipOutputStream zos, TransactionDocument txnDocument) throws IOException, SystemException {
    	String tenantIpFileName = TransactionUtil.getFileName(txnDocument.getClientTransactionID(), txnDocument.getRunAsOfDate(), BusinessConstants.TENANT_IP);
    	String tenantOpFileName = TransactionUtil.getFileName(txnDocument.getClientTransactionID(), txnDocument.getRunAsOfDate(), BusinessConstants.TENANT_OP);
    	String modelOpFileName = TransactionUtil.getFileName(txnDocument.getClientTransactionID(), txnDocument.getRunAsOfDate(), BusinessConstants.MODEL_OP);
        
    	String outputFileName;
		//String zipFileName;
		String inputFileName;
        if(StringUtils.equalsIgnoreCase(txnDocument.getTransactionMode(),BusinessConstants.BULK) 
    			&& StringUtils.equalsIgnoreCase(txnDocument.getChannel(),BusinessConstants.HTTP)) {
    		//zipFileName = txnDocument.getClientTransactionID() + BusinessConstants.HYPHEN + txnDocument.getBulkOnlineTimeStamp() + BusinessConstants.HYPHEN + BusinessConstants.TENANT_IO;
            inputFileName = txnDocument.getClientTransactionID()  + BusinessConstants.HYPHEN + txnDocument.getBulkOnlineTimeStamp()  + BusinessConstants.HYPHEN + BusinessConstants.TENANT_IP + BusinessConstants.EXTN_JSON;
            outputFileName = txnDocument.getClientTransactionID()  + BusinessConstants.HYPHEN + txnDocument.getBulkOnlineTimeStamp()  + BusinessConstants.HYPHEN + BusinessConstants.TENANT_OP + BusinessConstants.EXTN_JSON;
            String sanBase = umgFileProxy.getSanPath(SystemConstants.SAN_BASE);
            File inputDataFile = getFile(sanBase, inputFileName, BusinessConstants.ARCHIEVE_FOLDER);
            File outputDataFile = getFile(sanBase, outputFileName, BusinessConstants.OUTPUT_ARCHIVE_FOLDER);
            byte[] inputDataBytes = Files.readAllBytes(inputDataFile.toPath());
            byte[] outputDataBytes = null ;
            if(outputDataFile.exists()){
            	outputDataBytes = Files.readAllBytes(outputDataFile.toPath());
            }else{
            	outputDataFile = getFile(sanBase, outputFileName, BusinessConstants.OUTPUT_FOLDER);
            	if(outputDataFile.exists()){
                	outputDataBytes = Files.readAllBytes(outputDataFile.toPath());            		
            	}else{
            		outputDataBytes = AdminUtil.createTempJson(outputFileName);            		
            	}
            }
            if(inputDataBytes != null && inputDataBytes.length > 0){
            	 TransactionUtil.addToZipFile(tenantIpFileName, inputDataBytes, zos);
            } 
            if(outputDataBytes != null && outputDataBytes.length > 0){
            	TransactionUtil.addToZipFile(tenantOpFileName, outputDataBytes, zos);
            }
            if(!txnDocument.getErrorCode().startsWith("RVE")){
            	TransactionUtil.addToZipFile(modelOpFileName, transactionDelegate.getBulkModelErrorOuput(txnDocument.getTransactionId()), zos);
            }
        } 
        else {
	    	ObjectMapper mapper = new ObjectMapper();
	    	TransactionUtil.addToZipFile(tenantIpFileName, mapper.writeValueAsBytes(txnDocument.getTenantInput()), zos);
	    	TransactionUtil.addToZipFile(tenantOpFileName, mapper.writeValueAsBytes(txnDocument.getTenantOutput()), zos);
	    	TransactionUtil.addToZipFile(modelOpFileName, transactionDelegate.getBulkModelErrorOuput(txnDocument.getTransactionId()), zos);
        }
        }
    @RequestMapping(value = "/downloadRLog/{txnId}", method = RequestMethod.GET)
    @ResponseBody
    public void downloadRLogFile(@PathVariable(TXN_ID) String fileName,
    		HttpServletResponse response) {
    	 ZipOutputStream zos = null;
    	try{
    		
    		String tenantCode = getRequestContext().getTenantCode();
    		String sanBase = systemParameterProvider.getParameter(SystemConstants.SAN_BASE);
    		Path path = Paths.get(sanBase + "/" + tenantCode + "/" + "rLog"  + "/" +  fileName +TEXT_FILE_EXT);
    		LOGGER.error("Downloading RLog file from file " + sanBase + "/" + tenantCode + "/" + "rLog"  + "/" +  fileName + TEXT_FILE_EXT);
    		byte[] data = null;
    		zos = new ZipOutputStream(response.getOutputStream());
    		if(Files.exists(path)){
    			data = Files.readAllBytes(path);
    			setResponseHeader(response, fileName+ "_Rlogs"  + ".zip");
       		    zos.putNextEntry(new ZipEntry(fileName + "_Rlogs" + BusinessConstants.EXTN_TXT));
    		} else {
    			String info = "Transaction R logs not available for download";
    			data = info.getBytes();
    			setResponseHeader(response, fileName + "_Error" + ".zip");
       		    zos.putNextEntry(new ZipEntry(fileName + "_Error" + BusinessConstants.EXTN_TXT));
    		}
	         zos.write(data);
	         zos.closeEntry();
    		 //response.getOutputStream().write(data);
    		
    	} catch (IOException  e) {
    		LOGGER.error(e.getMessage(), e);
    	}
    	finally {
    		try {
    			if(zos != null){
    			zos.finish();
    			zos.close();
    			}
    			if(response.getOutputStream() != null){
    			response.getOutputStream().flush();
    			}
    		} catch (IOException e) {
    			LOGGER.error(e.getMessage(), e);
    		}

    	}
    }
}