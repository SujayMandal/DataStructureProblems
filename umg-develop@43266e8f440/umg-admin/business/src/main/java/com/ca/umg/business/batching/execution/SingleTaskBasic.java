package com.ca.umg.business.batching.execution;

import static com.ca.umg.business.batching.execution.BatchExecuterPool.isBatchTerminated;
import static com.ca.umg.business.batching.execution.BatchExecuterPool.removeTerminatedBatchFromCache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.batch.TransactionStatus;
import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.util.ConversionUtil;
import com.ca.umg.business.batching.bo.BatchTransactionBO;
import com.ca.umg.business.batching.entity.BatchRuntimeTransactionMapping;
import com.ca.umg.business.integration.info.TestStatusInfo;
import com.ca.umg.business.integration.runtime.RuntimeIntegrationClient;
import com.ca.umg.plugin.commons.excel.converter.JsonToExcelConverter;

@SuppressWarnings({"PMD.ExcessiveParameterList", "PMD.CyclomaticComplexity"})
public class SingleTaskBasic implements Runnable {
    private final static Logger LOGGER = LoggerFactory.getLogger(SingleTaskBasic.class);
    private final static String HEADER = "header";
    public static final String CORRELATION_ID = "ExcelCorrelationID";
    private final static String UMG_TRNS_ID = "umgTransactionId";
    private final static String SUCCESS_FLG = "success";
    private final static String FAIL_TRNS_ID = "failure";
    public final static String TERMINATED_TRNS_ID = "terminated";
    public final static String TERMINATE_MESSAGE = "Batch Terminated by User";

    private final RuntimeIntegrationClient runtimeIntegrationClient;
    private final BatchTransactionBO batchTransactionBO;
    private final String tenantUrl;
    private String tranId;
    private boolean success;
    private final ResponseObj responseObj;
    private final BatchDataContainer batchDataContainer;
    private final String batchId;
    private String threadName;
    private final List<Map<String, Object>> jsonList;
    private final JsonToExcelConverter jsonToExcelConverter;
    private final String outputXlslFile;
    private final String tenantCode;
    private final String authToken;
    private final CacheRegistry cacheRegistry;

    public SingleTaskBasic(List<Map<String, Object>> jsonList, String batchId, BatchDataContainer batchDataContainer,
            RuntimeIntegrationClient runtimeIntegrationClient, BatchTransactionBO batchTransactionBO, String tenantUrl,
            String tenantCode, String authToken, JsonToExcelConverter jsonToExcelConverter, String outputXlslFile,
            final CacheRegistry cacheRegistry) {
        this.runtimeIntegrationClient = runtimeIntegrationClient;
        this.tenantUrl = tenantUrl;
        this.batchTransactionBO = batchTransactionBO;
        this.responseObj = batchDataContainer.getResponseMap().get(batchId);
        this.batchDataContainer = batchDataContainer;
        this.batchId = batchId;
        this.jsonList = jsonList;
        this.jsonToExcelConverter=jsonToExcelConverter;
        this.outputXlslFile = outputXlslFile;
        this.authToken = authToken;
        this.tenantCode = tenantCode;

        this.cacheRegistry = cacheRegistry;
    }

    public void run() {
        threadName = Thread.currentThread().getName();
        Map<String, Object> headerData = null;
        RequestContext reqeustContext = null;
        String jsonReqStr = null;
        TestStatusInfo response = null;
        int counter = 0;
        boolean terminated = false;
        try {
            LOGGER.error(String.format("BatchId ::[%s] :: Basic Test execution Started. Thread Name :: %s", batchId, threadName));
            Properties properties = new Properties();
            properties.put(RequestContext.TENANT_CODE, tenantCode);
            reqeustContext = new RequestContext(properties);
            if (CollectionUtils.isNotEmpty(jsonList)) {
                jsonToExcelConverter.start(batchId, outputXlslFile);
                for (Map<String, Object> jsonData : jsonList) {                	
                	terminated = terminated || isBatchTerminated(cacheRegistry, batchId);
                    counter++;
                    LOGGER.error(String.format("BatchId ::[%s] :: Basic Test execution Started. Count :: %s - Thread Name :: %s", batchId, counter, threadName));
                    String correlationId = (String) ((Map) jsonData.get("header")).remove(CORRELATION_ID);
                    try {              
                        if (!terminated) {
                            if (((Map<String, Object>) jsonData.get(HEADER)).get("error") != null) {
                                response = new TestStatusInfo();
                                response.setError(true);
                                response.setErrorCode((String) ((Map<String, Object>) jsonData.get("data")).get("errorCode"));
                                response.setErrorMessage((String) ((Map<String, Object>) jsonData.get("data")).get("errorMessage"));
                            } else {
                                jsonReqStr = ConversionUtil.convertToJsonString(jsonData);
                                response = runtimeIntegrationClient.versionTest(jsonReqStr, tenantUrl, authToken);
                            }                        	
                        } else {
                        	response = new TestStatusInfo();
                        	response.setTerminated(true);
                        }
                        headerData = getHeaderData(response);
                        updateResponse(headerData, response, batchId, correlationId);
                        if (response.isTerminated()) {
                            Map<String,Object> errorMap = new HashMap<String,Object>();
                            errorMap.put(CORRELATION_ID, correlationId);
                            errorMap.put("Errors", TERMINATE_MESSAGE);
                            jsonToExcelConverter.addTerminatedTransaction(batchId, tranId, errorMap, counter);                        	
                        } else if(response.isError()){
                            Map<String,Object> errorMap = new HashMap<String,Object>();
                            errorMap.put(CORRELATION_ID, correlationId);
                            errorMap.put("Errors",response.getErrorCode() + " : " + response.getErrorMessage());
                            if(response.getResponse()!=null){
                                errorMap.put("validationErrors", response.getResponse().get("errors")); 
                            }
                            jsonToExcelConverter.addTransaction(batchId, tranId, errorMap, counter,true);
                        }else{
                            jsonToExcelConverter.addTransaction(batchId, tranId, response.getResponse(), counter,false);
                        }
                        responseObj.increaseCount();

                    } catch (SystemException | BusinessException exp) {
                        LOGGER.error(
                                String.format("BatchId ::[%s] :: Test execution failed for request. Message : %s", batchId,
                                        exp.getMessage()), exp);
                        Map<String,Object> errorMap = new HashMap<String,Object>();
                        errorMap.put(CORRELATION_ID, correlationId);
                        errorMap.put("Errors",exp.getCode() + " : " + exp.getLocalizedMessage());
                        if(response!=null && response.getResponse()!=null){
                            errorMap.put("validationErrors", response.getResponse().get("errors")); 
                        }
                        jsonToExcelConverter.addTransaction(batchId, tranId, errorMap, counter,true);
                    }
                }
                
            }
        } catch (Exception exp) {// NOPMD
            LOGGER.error(String.format("BatchId ::[%s] :: Test execution failed. Message : %s. Counter :: %s, Thread Name :: %s", batchId, exp.getMessage(), counter, threadName), exp);
        } finally {
            LOGGER.error(String.format("BatchId ::[%s] :: Single Test execution Completed. Counter :: %s, Thread Name :: %s", batchId, counter, threadName));
            removeTerminatedBatchFromCache(cacheRegistry, batchId);
            finalizeBatchProcessing(terminated);
            reqeustContext.destroy();
        }
    }

    private void finalizeBatchProcessing(final boolean terminated) {
        try {
            LOGGER.error(String.format("BatchId ::[%s] :: Test execution completed. Message : %s . Thread Id :: %s", batchId,
                    responseObj.toString(), threadName));
            String outputFileName = FilenameUtils.getName(jsonToExcelConverter.getLocation(batchId));
            
            String status;
            if (terminated) {
            	status = TransactionStatus.TERMINATED.getStatus();
            } else {
            	status = TransactionStatus.PROCESSED.getStatus();
            }
            
            batchTransactionBO.updateBatch(batchId, responseObj.getRequestCount(), responseObj.getStatus().get(BatchDataContainer.SUCCESS).size(),
                    responseObj.getStatus().get(BatchDataContainer.FAILURE).size(), 
                    status,outputFileName);
            jsonToExcelConverter.stop(batchId);
        } catch (SystemException | BusinessException exp) {
            LOGGER.error(String.format("BatchId ::[%s] :: Test execution final update failed. Message : %s", batchId,
                    exp.getMessage()), exp);
        } finally {
            batchDataContainer.getResponseMap().remove(batchId);
        }
    }

    private Map<String, Object> getHeaderData(TestStatusInfo response) {
        Map<String, Object> headerData = null;
        if (response != null && MapUtils.isNotEmpty(response.getResponse())) {
            headerData = (Map<String, Object>) response.getResponse().get(HEADER);
        }
        return headerData;
    }

    /**
     * This method will update the aggregate map and the mapping table with status
     */
    private void updateResponse(Map<String, Object> headerData, TestStatusInfo response, String batchId, String correlationId)
            throws SystemException, BusinessException {

        BatchRuntimeTransactionMapping brtm = null;
        if (headerData != null) {
            tranId = (String) headerData.get(UMG_TRNS_ID);
            success = (boolean) headerData.get(SUCCESS_FLG);
            
            brtm = new BatchRuntimeTransactionMapping();
            brtm.setBatchTransaction(batchId);
            brtm.setTransaction(tranId);
            
            if (response.isTerminated()) {
                responseObj.getStatus().get(BatchDataContainer.TERMINATED).add(TERMINATED_TRNS_ID);
                brtm.setStatus(BatchDataContainer.TERMINATED);
                brtm.setError(TERMINATE_MESSAGE); 
                LOGGER.error(String.format("BatchId ::[%s] :: Thread Name :: %s , Error Response - %s", batchId, threadName, "Terminated"));            	
            } else {
	            if (success) {
	                LOGGER.error(String.format("BatchId ::[%s] :: Thread Name :: %s , Success TrnsId :: %s", batchId, threadName, tranId));
	                responseObj.getStatus().get(BatchDataContainer.SUCCESS).add(tranId);
	                brtm.setStatus(BatchDataContainer.SUCCESS);
	                ((Map) response.getResponse().get("data")).put(CORRELATION_ID, correlationId);
	            } else {
	                LOGGER.error(String.format("BatchId ::[%s] :: Thread Name :: %s , Failure TrnsId :: %s", batchId, threadName, tranId));
	                responseObj.getStatus().get(BatchDataContainer.FAILURE).add(tranId);
	                brtm.setStatus(BatchDataContainer.FAILURE);
	                brtm.setError("Error Msg not available");
	            }
            }
        } else if (response.isTerminated()) {
            responseObj.getStatus().get(BatchDataContainer.TERMINATED).add(TERMINATED_TRNS_ID);
            brtm = new BatchRuntimeTransactionMapping();
            brtm.setBatchTransaction(batchId);
            brtm.setTransaction(TERMINATED_TRNS_ID);
            brtm.setStatus(BatchDataContainer.TERMINATED);
            brtm.setError(TERMINATE_MESSAGE); 
            LOGGER.error(String.format("BatchId ::[%s] :: Thread Name :: %s , Error Response - %s", batchId, threadName, "Terminated"));
        } else {
            responseObj.getStatus().get(BatchDataContainer.FAILURE).add(FAIL_TRNS_ID);
            brtm = new BatchRuntimeTransactionMapping();
            brtm.setBatchTransaction(batchId);
            brtm.setTransaction(FAIL_TRNS_ID);
            brtm.setStatus(BatchDataContainer.FAILURE);
            brtm.setError("No response generated by the runtime.");
            LOGGER.error(String.format("BatchId ::[%s] :: Thread Name :: %s , Error Response %s - %s", batchId, threadName,
                    response.getErrorCode(), response.getErrorMessage()));
        }
        batchTransactionBO.addBatchTransactionMapping(brtm);
    }
}
