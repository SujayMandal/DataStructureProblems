/*
 * DeploymentController.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 *
 *
 * Author : KR Kumar
 */
package com.ca.umg.rt.web.rest.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ca.framework.core.batch.TransactionStatus;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.framework.core.util.MessageContainer;
import com.ca.framework.core.util.TransactionDocumentPayload;
import com.ca.framework.core.util.TransactionIOPayload;
import com.ca.framework.core.util.UmgFileProxy;
import com.ca.umg.rt.core.deployment.constants.RuntimeConstants;
import com.ca.umg.rt.core.deployment.delegate.DeploymentDelegate;
import com.ca.umg.rt.core.deployment.info.DeploymentDescriptor;
import com.ca.umg.rt.core.deployment.info.DeploymentStatusInfo;
import com.ca.umg.rt.core.deployment.info.TestStatusInfo;
import com.ca.umg.rt.core.flow.dao.MongoTransactionLogDAO;
import com.ca.umg.rt.core.flow.dao.TransactionLogDAO;
import com.ca.umg.rt.core.flow.entity.TransactionLog;
import com.ca.umg.rt.custom.serializers.DoubleSerializerModule;
import com.ca.umg.rt.endpoint.http.ModelRequest;
import com.ca.umg.rt.exception.codes.RuntimeExceptionCode;
import com.ca.umg.rt.util.MessageVariables;
import com.ca.umg.rt.util.RequestValidator;
import com.ca.umg.rt.util.TransactionPayload;
import com.ca.umg.rt.web.filter.RequestMarkerFilter;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

/**
 * 
 **/
@Controller
@RequestMapping("/api/deployment")
public class DeploymentController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentController.class);
    @Inject
    private DeploymentDelegate deploymentDelegate;

    @Inject
    private TransactionLogDAO transactionLogDAO;

    @Inject
    private MongoTransactionLogDAO mongoTransactionLogDAO;
    
    @Inject
    private UmgFileProxy umgFileProxy;

    private static final String NULL = "null";

    /**
     * DOCUMENT ME!
     *
     * @param deploymentDescriptor
     *            DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     **/
    @RequestMapping(value = "/deploy", method = RequestMethod.POST)
    public @ResponseBody DeploymentStatusInfo deploy(@RequestBody DeploymentDescriptor deploymentDescriptor) {
        LOGGER.info("Deploying {}", deploymentDescriptor);
        long start = System.currentTimeMillis();
        DeploymentStatusInfo deploymentStatusInfo = new DeploymentStatusInfo();
        try {
            deploymentStatusInfo = deploymentDelegate.deploy(deploymentDescriptor);
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            deploymentStatusInfo.setErrorCode(e.getCode());
            deploymentStatusInfo.setError(true);
            deploymentStatusInfo.setErrorMessage(e.getLocalizedMessage());
        } catch (Exception ex) {// NOPMD
            LOGGER.error(ex.getLocalizedMessage(), ex);
            deploymentStatusInfo.setErrorCode(RuntimeExceptionCode.RSE000100);
            deploymentStatusInfo.setError(true);
            deploymentStatusInfo.setErrorMessage(ex.getLocalizedMessage());
        }
        long end = System.currentTimeMillis();
        deploymentStatusInfo.setTimeTaken(end - start);
        return deploymentStatusInfo;
    }

    /**
     * DOCUMENT ME!
     *
     * @param deploymentDescriptor
     *            DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     **/
    @RequestMapping(value = "/undeploy", method = RequestMethod.POST)
    public @ResponseBody DeploymentStatusInfo undeploy(@RequestBody DeploymentDescriptor deploymentDescriptor) {
        LOGGER.info("UnDeploying {}", deploymentDescriptor);
        long start = System.currentTimeMillis();
        DeploymentStatusInfo deploymentStatusInfo = new DeploymentStatusInfo();
        try {
            deploymentStatusInfo = deploymentDelegate.undeploy(deploymentDescriptor);
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            deploymentStatusInfo.setErrorCode(e.getCode());
            deploymentStatusInfo.setError(true);
            deploymentStatusInfo.setErrorMessage(e.getLocalizedMessage());
        } catch (Exception ex) {// NOPMD
            LOGGER.error(ex.getLocalizedMessage(), ex);
            deploymentStatusInfo.setErrorCode(RuntimeExceptionCode.RSE000100);
            deploymentStatusInfo.setError(true);
            deploymentStatusInfo.setErrorMessage(MessageContainer.getMessage(RuntimeExceptionCode.RSE000100, new Object[] {}));
        }
        long end = System.currentTimeMillis();
        deploymentStatusInfo.setTimeTaken(end - start);
        return deploymentStatusInfo;
    }

    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public @ResponseBody TestStatusInfo deploy(@RequestBody String modelRequest) {
        LOGGER.info("Tesing model {}", modelRequest);
        long start = System.currentTimeMillis();
        TestStatusInfo testStatusInfo = new TestStatusInfo();
        ModelRequest modelRequestObj = null;
        boolean error = false;
        String errorCode = null;
        String errorMessage = null;
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        LinkedHashMap<String, Object> reqObj = null;
        Map<String,Object> reqHeader = null;
        try {
        	reqObj = mapper.readValue(modelRequest, new TypeReference<LinkedHashMap<String, Object>>() {
            });
        	reqHeader = (Map<String, Object>) reqObj.get(MessageVariables.HEADER);
            String sanBase = umgFileProxy.getSanPath(SystemConstants.SAN_BASE);
            KeyValuePair<String, byte[]> bytesOfFile = new KeyValuePair<>();
            modelRequestObj = RequestValidator.validateRequest(modelRequest, sanBase, bytesOfFile);
            Map<String, Object> requestBody = createRequestBody(modelRequestObj,reqHeader);
            testStatusInfo = deploymentDelegate.executeTestFlow(modelRequestObj, requestBody);
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            errorCode = e.getCode();
            errorMessage = e.getLocalizedMessage();
            testStatusInfo.setErrorCode(errorCode);
            testStatusInfo.setError(true);
            testStatusInfo.setErrorMessage(errorMessage);
            error = true;
        } catch (Exception ex) {// NOPMD
            LOGGER.error(ex.getLocalizedMessage(), ex);
            errorCode = RuntimeExceptionCode.RSE000100;
            errorMessage = ex.getLocalizedMessage();
            testStatusInfo.setErrorCode(errorCode);
            testStatusInfo.setError(true);
            testStatusInfo.setErrorMessage(errorMessage);
            error = true;
        }        
        Map<String,Object> newHeader =  new HashMap<String,Object>();
        Map<String,Object> newFileMap =  new HashMap<String,Object>();
        Map<String,Object> response =(Map<String, Object>) (testStatusInfo.getResponse() == null ? newHeader : testStatusInfo.getResponse());
        if(response.get(MessageVariables.HEADER) == null){
        	response.put(MessageVariables.HEADER,newFileMap);
        	testStatusInfo.setResponse(response);
        }
        Map<String,Object> responseHeader = (Map<String, Object>) (response.get(MessageVariables.HEADER));
       // responseHeader.put(MessageVariables.TRAN_MODE,fileName != null ? MessageVariables.TRAN_BULK : MessageVariables.TRAN_ONLINE);
        Integer tenTranCnt = (Integer) (reqHeader != null ? reqHeader.get(MessageVariables.TENANT_TRAN_COUNT) : null); 
        if(tenTranCnt != null){
        	responseHeader.put(MessageVariables.TENANT_TRAN_COUNT, tenTranCnt);
        }
        long end = System.currentTimeMillis();
        testStatusInfo.setTimeTaken(end - start);
        if ((error || testStatusInfo.isError()) && testStatusInfo.getErrorCode() == RuntimeExceptionCode.RSE000101) {
            String transactionId = MDC.get(RequestMarkerFilter.TRANSACTION_ID);
            logTransactionToMongoAndSql(modelRequestObj, testStatusInfo.getResponse(), modelRequest, start, end, testStatusInfo,
                    transactionId);
        }
        if (testStatusInfo.isError()) {
            LOGGER.error("testStatusInfo is :" + testStatusInfo);
            LOGGER.error("testStatusInfo error Code is" + testStatusInfo.getErrorCode());
            LOGGER.error("testStatusInfo error message is" + testStatusInfo.getErrorMessage());
            LOGGER.error("testStatusInfo response is :" + testStatusInfo.getResponse());
        }

        return testStatusInfo;
    }

    private void logTransactionToMongoAndSql(ModelRequest modelRequest, Map<String, Object> tenantResponse,
            String tenantRequestString, long start, long end, TestStatusInfo testStatusInfo, String transactionId) {
        Boolean mySqlSuccess = Boolean.FALSE;
        try {
            logTransaction(modelRequest, tenantRequestString, start, end, testStatusInfo, transactionId);
            mySqlSuccess = Boolean.TRUE;
        } catch (Exception ex) {// NOPMD
            LOGGER.error("Error while logging transaction to mysql DeploymentController::logTransactionToMongoAndMySql", ex);
        }

        if (mySqlSuccess) {
            try {
                logTransactionToMongo(modelRequest, tenantResponse, tenantRequestString, start, end, testStatusInfo,
                        transactionId);
            } catch (DataAccessResourceFailureException dex) {
                LOGGER.error(
                        "Error in saving transaction to mongo DeploymentController::logTransactionToMongoAndMySql - mongo db is down",
                        dex);
                transactionLogDAO.remove(transactionId);
                testStatusInfo.setErrorCode("RSE000905");
                testStatusInfo.setErrorMessage("System Exception. Please contact administrator.");
            } catch (Exception ex) {// NOPMD
                LOGGER.error("Error in saving transaction to mongo DeploymentController::logTransactionToMongoAndMySql", ex);
                transactionLogDAO.remove(transactionId);
            }
        }

        // Reset the error message to generic error message
        if (tenantResponse != null) {
            Map<String, Object> tenantResponseHdr = (Map<String, Object>) tenantResponse.get(MessageVariables.HEADER);
            if (tenantResponseHdr.get(MessageVariables.SUCCESS) != null
                    && !(Boolean) tenantResponseHdr.get(MessageVariables.SUCCESS)
                    && (!(tenantResponseHdr.get(MessageVariables.ERROR_CODE) != null && ((String) tenantResponseHdr
                            .get(MessageVariables.ERROR_CODE)).startsWith(RuntimeConstants.RVE_EXCEPTION)))) {
                tenantResponseHdr.put(MessageVariables.ERROR_MESSAGE, RuntimeConstants.GENERIC_ERROR_MESSAGE);
            }
        }
    }

    private Map<String, Object> createRequestBody(ModelRequest modelRequest, Map<String,Object> reqHeader) {
        Map<String, Object> requestBody = new LinkedHashMap<String, Object>();
        Map<String, Object> header = new LinkedHashMap<String, Object>();
        header.put(MessageVariables.MODEL_NAME, modelRequest.getHeader().getModelName());
        header.put(MessageVariables.MAJOR_VERSION, modelRequest.getHeader().getMajorVersion());
        header.put(MessageVariables.MINOR_VERSION, modelRequest.getHeader().getMinorVersion());
        header.put(MessageVariables.FILE_NAME, modelRequest.getHeader().getFileName());
        header.put(MessageVariables.DATE, ISODateTimeFormat.dateHourMinuteSecondMillis()
                .print(modelRequest.getHeader().getDate()));
        header.put(MessageVariables.TRANSACTION_ID, modelRequest.getHeader().getTransactionId());
        if(reqHeader.containsKey(MessageVariables.STORE_RLOGS)){
        	header.put(MessageVariables.STORE_RLOGS, reqHeader.get(MessageVariables.STORE_RLOGS));
        }
        if (modelRequest.getHeader().getBatchId() != null) {
            header.put(MessageVariables.BATCH_ID, modelRequest.getHeader().getBatchId());        	
        }
      //added this to fix umg-4251 to set versionCreationTest flag to true 
        //if it is test transaction during version creation else the flag will be false
        header.put(MessageVariables.VERSION_CREATION_TEST, modelRequest.getHeader().getVersionCreationTest());
        //added to fix UMG-4500, UMG-4697 Additional variables in Transaction header
        if(modelRequest.getHeader().getUser()!=null){
        header.put(MessageVariables.USER, modelRequest.getHeader().getUser());
        }
        header.put(MessageVariables.TRANSACTION_TYPE, modelRequest.getHeader().getTransactionType());
        if(modelRequest.getHeader().getTenantTranCount() != null){
        	header.put(MessageVariables.TENANT_TRAN_COUNT, modelRequest.getHeader().getTenantTranCount());
        }
        if(StringUtils.isNotBlank(modelRequest.getHeader().getExecutionGroup())){
        	header.put(MessageVariables.EXECUTION_GROUP,modelRequest.getHeader().getExecutionGroup());
        }else{
        	header.put(MessageVariables.EXECUTION_GROUP,MessageVariables.DEFAULT_EXECUTION_GROUP);
        }
        header.put(FrameworkConstant.ADD_ON_VALIDATION, modelRequest.getHeader().getAddonValidation());
        header.put(MessageVariables.CHANNEL, modelRequest.getHeader().getChannel());
       
        requestBody.put(MessageVariables.HEADER, header);
        requestBody.put(MessageVariables.DATA, modelRequest.getData());

        return requestBody;
    }

    private void logTransaction(ModelRequest modelRequest, String tenantRequestString, long start, long end,
            TestStatusInfo testStatusInfo, String transactionId) throws JsonParseException, JsonMappingException, IOException {
        TransactionLog transactionLog = new TransactionLog();
        ObjectMapper requestMapper = new ObjectMapper();
        LinkedHashMap<String, Object> reqObj = requestMapper.readValue(tenantRequestString,
                new TypeReference<LinkedHashMap<String, Object>>() {
                });
        transactionLog.setId(transactionId);
        if (modelRequest != null) {
            transactionLog.setMajorVersion(modelRequest.getHeader().getMajorVersion() != null ? modelRequest.getHeader()
                    .getMajorVersion() : 0);
            transactionLog.setMinorVersion(modelRequest.getHeader().getMinorVersion() != null ? modelRequest.getHeader()
                    .getMinorVersion() : 0);
            transactionLog.setModelName(modelRequest.getHeader().getModelName() != null ? modelRequest.getHeader().getModelName()
                    : NULL);
            transactionLog.setRunAsOfDate(modelRequest.getHeader().getDate() != null ? modelRequest.getHeader().getDate()
                    .getMillis() : DateTime.now().getMillis());
            transactionLog.setTransactionId(modelRequest.getHeader().getTransactionId() != null ? modelRequest.getHeader()
                    .getTransactionId() : NULL);
            //added to fix UMG-4500 Additional variables in Transaction header
            transactionLog.setCreatedBy(modelRequest.getHeader().getUser() != null ? modelRequest
                    .getHeader().getUser() : RequestContext.getRequestContext().getTenantCode());
            if(StringUtils.isNotBlank(modelRequest.getHeader().getTransactionType())) { //UMG-4611 - audhyabh
            	String tranTypeTrimed=modelRequest.getHeader().getTransactionType().trim();
                transactionLog.setIsTest(StringUtils.equalsIgnoreCase(tranTypeTrimed, "test") ? 1 : 0);
            } else {
            	transactionLog.setIsTest(1);
            }
        } else {
            Map errorResponseData = (Map) reqObj.get(MessageVariables.HEADER);
            if (errorResponseData.get(MessageVariables.MAJOR_VERSION) != null) {
                transactionLog.setMajorVersion((Integer) errorResponseData.get(MessageVariables.MAJOR_VERSION));
            } else {
                transactionLog.setMajorVersion(0);
            }
            if (errorResponseData.get(MessageVariables.MINOR_VERSION) != null) {
                transactionLog.setMinorVersion((Integer) errorResponseData.get(MessageVariables.MINOR_VERSION));
            } else {
                transactionLog.setMinorVersion(0);
            }
            if (errorResponseData.get(MessageVariables.MODEL_NAME) != null) {
                transactionLog.setModelName((String) errorResponseData.get(MessageVariables.MODEL_NAME));
            } else {
                transactionLog.setModelName(NULL);
            }
            if (errorResponseData.get(MessageVariables.TRANSACTION_ID) != null) {
                transactionLog.setTransactionId((String) errorResponseData.get(MessageVariables.TRANSACTION_ID));
            } else {
                transactionLog.setTransactionId(NULL);
            }
            transactionLog.setRunAsOfDate(DateTime.now().getMillis());
            //added to fix UMG-4500 Additional variables in Transaction header
            transactionLog.setCreatedBy(RequestContext.getRequestContext().getTenantCode());
            transactionLog.setIsTest(1);
        }

        transactionLog.setLibraryName(NULL);

        transactionLog.setRuntimeStart(start);
        transactionLog.setRuntimeEnd(end);
        transactionLog.setTenantInput(RuntimeConstants.STR_EMPTY);
        transactionLog.setTenantOutput(RuntimeConstants.STR_EMPTY);
        transactionLog.setModelInput(RuntimeConstants.STR_EMPTY);
        transactionLog.setModelOutput(RuntimeConstants.STR_EMPTY);
        transactionLog.setStatus(TransactionStatus.ERROR.getStatus());
        transactionLog.setErrorCode(testStatusInfo.getErrorCode());
        if (testStatusInfo.getErrorMessage() != null) {
            transactionLog.setErrorDescription(testStatusInfo.getErrorMessage().getBytes());
        } else {
            transactionLog.setErrorDescription(null);
        }
        transactionLogDAO.insertTransactionLog(transactionLog);
    }

    private void logTransactionToMongo(ModelRequest modelRequest, Map<String, Object> tenantResponse, String tenantRequestString,
            long start, long end, TestStatusInfo testStatusInfo, String transactionId) throws JsonParseException,
            JsonMappingException, IOException {        
        Map<String, Object> tenantInput = null;
        TransactionPayload transactionPayload =new TransactionPayload();
        transactionPayload.setTransactionId(transactionId);
        ObjectMapper requestMapper = new ObjectMapper();
        LinkedHashMap<String, Object> reqObj = requestMapper.readValue(tenantRequestString,
                new TypeReference<LinkedHashMap<String, Object>>() {
                });
        if (modelRequest != null) {
        	transactionPayload.setMajorVersion(modelRequest.getHeader().getMajorVersion() != null ? modelRequest
                    .getHeader().getMajorVersion() : 0);
        	transactionPayload.setMinorVersion(modelRequest.getHeader().getMinorVersion() != null ? modelRequest
                    .getHeader().getMinorVersion() : 0);
        	transactionPayload.setVersionName(modelRequest.getHeader().getModelName() != null ? modelRequest.getHeader()
                    .getModelName() : NULL);
        	transactionPayload.setRunAsOfDate(modelRequest.getHeader().getDate() != null ? modelRequest.getHeader()
                    .getDate().getMillis() : DateTime.now().getMillis());
        	transactionPayload.setClientTransactionID(modelRequest.getHeader().getTransactionId() != null ? modelRequest
                    .getHeader().getTransactionId() : NULL);
            //UMG-4697
            if(StringUtils.isNotBlank(modelRequest.getHeader().getExecutionGroup())) {
            	transactionPayload.setExecutionGroup(modelRequest.getHeader().getExecutionGroup());
	             }else{ transactionPayload.setExecutionGroup(MessageVariables.DEFAULT_EXECUTION_GROUP);}
          //added to fix UMG-4500 Additional variables in Transaction header
            transactionPayload.setCreatedBy(modelRequest.getHeader().getUser() != null ? modelRequest
                    .getHeader().getUser() : RequestContext.getRequestContext().getTenantCode());
            if(StringUtils.isNotBlank(modelRequest.getHeader().getTransactionType())) { //UMG-4611
            	String tranTypeTrimed=modelRequest.getHeader().getTransactionType().trim();
            	transactionPayload.setTest(StringUtils.equalsIgnoreCase(tranTypeTrimed, "test") ? Boolean.TRUE : Boolean.FALSE);
            } else {
            	transactionPayload.setTest(Boolean.TRUE);
            }	
        } else {
            Map errorResponseData = (Map) reqObj.get(MessageVariables.HEADER);
            if (errorResponseData.get(MessageVariables.MAJOR_VERSION) != null) {
            	transactionPayload.setMajorVersion((Integer) errorResponseData.get(MessageVariables.MAJOR_VERSION));
            } else {
            	transactionPayload.setMajorVersion(0);
            }
            if (errorResponseData.get(MessageVariables.MINOR_VERSION) != null) {
            	transactionPayload.setMinorVersion((Integer) errorResponseData.get(MessageVariables.MINOR_VERSION));
            } else {
            	transactionPayload.setMinorVersion(0);
            }
            if (errorResponseData.get(MessageVariables.MODEL_NAME) != null) {
            	transactionPayload.setVersionName((String) errorResponseData.get(MessageVariables.MODEL_NAME));
            } else {
            	transactionPayload.setVersionName(NULL);
            }
            if (errorResponseData.get(MessageVariables.TRANSACTION_ID) != null) {
            	transactionPayload
                        .setClientTransactionID((String) errorResponseData.get(MessageVariables.TRANSACTION_ID));
            } else {
            	transactionPayload.setClientTransactionID(NULL);
            }
          //added to fix UMG-4500 Additional variables in Transaction header
            transactionPayload.setCreatedBy(RequestContext.getRequestContext().getTenantCode());
            transactionPayload.setTest(Boolean.TRUE);
            
            transactionPayload.setRunAsOfDate(DateTime.now().getMillis());
        }

        transactionPayload.setLibraryName(NULL);
        transactionPayload.setTenantId(RequestContext.getRequestContext().getTenantCode());
        transactionPayload.setRuntimeCallStart(start);
        transactionPayload.setRuntimeCallEnd(end);

        if (tenantRequestString != null) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JodaModule());
            mapper.registerModule(new DoubleSerializerModule());
            tenantInput = mapper.readValue(tenantRequestString, new TypeReference<Map<String, Object>>() {
            });
        }
       TransactionDocumentPayload transactionDocumentPayload =  setPayloadAndTest(tenantResponse, tenantInput);
    
        
        transactionPayload.setStatus(TransactionStatus.ERROR.getStatus());
        transactionPayload.setTest(Boolean.TRUE);
        transactionPayload.setCreatedBy(RequestContext.getRequestContext().getTenantCode());
        transactionPayload.setCreatedDate(System.currentTimeMillis());
        transactionPayload.setErrorCode(testStatusInfo.getErrorCode());
        if (testStatusInfo.getErrorMessage() != null) {
        	transactionPayload.setErrorDescription(testStatusInfo.getErrorMessage());
        } else {
        	transactionPayload.setErrorDescription(null);
        }
        mongoTransactionLogDAO.insertTransactionLogToMongo(transactionPayload, transactionDocumentPayload);
    }

	private TransactionDocumentPayload setPayloadAndTest(Map<String, Object> tenantResponse,
			Map<String, Object> tenantInput) {	
		
		TransactionDocumentPayload transactionDocumentPayload = null;	
		
		if (tenantInput != null) {
			TransactionIOPayload txnTIPayload = new TransactionIOPayload();
			TransactionIOPayload txnTOPayload = new TransactionIOPayload();
            Map<String, Object> tenantRequestHdr = (Map<String, Object>) tenantInput.get(MessageVariables.HEADER);
            if (tenantRequestHdr != null) {
	             tenantRequestHdr.remove(MessageVariables.VERSION_CREATION_TEST);
	             tenantInput.put(MessageVariables.HEADER, tenantRequestHdr);
	             if (tenantRequestHdr.get(MessageVariables.PAYLOAD_STORAGE)== null || ((Boolean) tenantRequestHdr.get(MessageVariables.PAYLOAD_STORAGE))) {	  
	            	 transactionDocumentPayload = new TransactionDocumentPayload();
	            	 txnTIPayload.setTxnIOPayload(tenantInput);
	            	 transactionDocumentPayload.setTxnTIPayload(txnTIPayload);
	            	if(tenantResponse!=null){
	            		txnTOPayload.setTxnIOPayload(tenantResponse);   
	            		transactionDocumentPayload.setTxnTOPayload(txnTOPayload);            		
	            	} 		
	     		}/*else{
	  	     			transactionDocumentPayload.setTenantInput(tenantInput != null ? tenantInput : null);
	  	     			transactionDocumentPayload.setTenantOutput(tenantResponse != null ? tenantResponse : null);	     		
	  	     		
	     		}*/
            }          
           
       }
		return transactionDocumentPayload;
	}

}
