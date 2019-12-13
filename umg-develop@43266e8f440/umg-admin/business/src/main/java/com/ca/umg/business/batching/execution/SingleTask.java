package com.ca.umg.business.batching.execution;

import static com.ca.umg.business.batching.execution.BatchExecuterPool.isBatchTerminated;
import static com.ca.umg.business.batching.execution.BatchExecuterPool.removeTerminatedBatchFromCache;
import static com.ca.umg.business.batching.execution.SingleTaskBasic.TERMINATED_TRNS_ID;
import static com.ca.umg.business.batching.execution.SingleTaskBasic.TERMINATE_MESSAGE;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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

@SuppressWarnings({ "PMD.TooManyFields", "PMD.CyclomaticComplexity", "PMD.ExcessiveParameterList" })
public class SingleTask implements Runnable {
    private final static Logger LOGGER = LoggerFactory.getLogger(SingleTask.class);
    private final static String HEADER = "header";
    private final static String UMG_TRNS_ID = "umgTransactionId";
    private final static String SUCCESS_FLG = "success";
    private final static String FAIL_TRNS_ID = "failure";
    private final static boolean FAIL_FLG = false;
    private final static int MAX_WAIT_SEC = 300;
    private final static int WAIT_PERIOD_MS = 10000;
    public static final String CORRELATION_ID = "ExcelCorrelationID";

    private final RequestObj obj;
    private final RuntimeIntegrationClient runtimeIntegrationClient;
    private final BatchTransactionBO batchTransactionBO;
    private final String tenantUrl;
    private String jsonReqStr;
    private String tranId;
    private boolean success;
    private final ResponseObj responseObj;
    private final BatchDataContainer batchDataContainer;
    private final String batchId;
    private String threadName;
    private TestStatusInfo response;
    private final JsonToExcelConverter jsonToExcelConverter;
    private final int index;
    private final String tenantCode;
    private final String authToken;
    private final CacheRegistry cacheRegistry;

    public SingleTask(RequestObj obj, BatchDataContainer batchDataContainer, RuntimeIntegrationClient runtimeIntegrationClient,
            BatchTransactionBO batchTransactionBO, String tenantUrl, String tenantCode, String authToken,
            JsonToExcelConverter jsonToExcelConverter,
            int index, final CacheRegistry cacheRegistry) {
        this.obj = obj;
        this.runtimeIntegrationClient = runtimeIntegrationClient;
        this.tenantUrl = tenantUrl;
        this.batchTransactionBO = batchTransactionBO;
        this.responseObj = batchDataContainer.getResponseMap().get(obj.getBatchId());
        this.batchDataContainer = batchDataContainer;
        this.batchId = obj.getBatchId();
        this.jsonToExcelConverter=jsonToExcelConverter;
        this.index=index;
        this.tenantCode = tenantCode;
        this.authToken = authToken;
        this.cacheRegistry = cacheRegistry;
    }

    public void run() {
    	boolean terminated = isBatchTerminated(cacheRegistry, batchId);
    	
        threadName = Thread.currentThread().getName();
        Map<String, Object> headerData = null;
        RequestContext reqeustContext = null;
        try {
            LOGGER.error(String.format("BatchId ::[%s] :: Test execution Started. Thread Name :: %s", batchId, threadName));
            Properties properties = new Properties();
            properties.put(RequestContext.TENANT_CODE, tenantCode);
            reqeustContext = new RequestContext(properties);

            if (obj != null && MapUtils.isNotEmpty(obj.getJsonData())) {
                String correlationId = (String) ((Map) obj.getJsonData().get("header")).remove(CORRELATION_ID);
                try {
                	if (!terminated) {
	                    if (((Map<String, Object>) obj.getJsonData().get(HEADER)).get("error") != null) {
	                        response = new TestStatusInfo();
	                        response.setError(true);
	                        response.setErrorCode((String) ((Map<String, Object>) obj.getJsonData()).get("errorCode"));
	                        response.setErrorMessage((String) ((Map<String, Object>) obj.getJsonData().get("data"))
	                                .get("errorMessage"));
	                    } else {
	                    jsonReqStr = ConversionUtil.convertToJsonString(obj.getJsonData());
                            response = runtimeIntegrationClient.versionTest(jsonReqStr, tenantUrl, authToken);
	                    }
                	} else {
                    	response = new TestStatusInfo();
                    	response.setTerminated(true);
                	}
                } catch (SystemException | BusinessException exp) {
                    LOGGER.error(String.format("BatchId ::[%s] :: Test execution failed for request. Message : %s", batchId,exp.getMessage()), exp);
                    headerData = new HashMap<>();
                    headerData.put(UMG_TRNS_ID, FAIL_TRNS_ID);
                    headerData.put(SUCCESS_FLG, FAIL_FLG);
                    LOGGER.error(
                            String.format("BatchId ::[%s] :: Test execution failed for request. Message : %s", batchId,
                                    exp.getMessage()), exp);
                    Map<String,Object> errorMap = new HashMap<String,Object>();
                    errorMap.put(CORRELATION_ID, correlationId);
                    errorMap.put("Errors",exp.getCode() + " : " + exp.getLocalizedMessage());
                    if(response!=null && response.getResponse()!=null){
                        errorMap.put("validationErrors", response.getResponse().get("errors")); 
                    }                    
                    jsonToExcelConverter.addTransaction(batchId, tranId, errorMap, index,true);
                }
                headerData = getHeaderData();
                updateResponse(headerData, correlationId);
                if (response.isTerminated()) {
                    Map<String,Object> errorMap = new HashMap<String,Object>();
                    errorMap.put(CORRELATION_ID, correlationId);
                    errorMap.put("Errors", TERMINATE_MESSAGE);
                    jsonToExcelConverter.addTerminatedTransaction(batchId, tranId, errorMap, index);                        	
                } else if(response.isError()){
                    Map<String,Object> errorMap = new HashMap<String,Object>();
                    errorMap.put(CORRELATION_ID, correlationId);
                    errorMap.put("Errors",response.getErrorCode() + " : " + response.getErrorMessage());
                    if(response.getResponse()!=null){
                        errorMap.put("validationErrors", response.getResponse().get("errors")); 
                    }
                    jsonToExcelConverter.addTransaction(batchId, tranId, errorMap, index,true);
                }else{
                    jsonToExcelConverter.addTransaction(batchId, tranId, response.getResponse(), index,false);
                }

            }

        } catch (Exception exp) {// NOPMD
            LOGGER.error(String.format("BatchId ::[%s] :: Test execution failed. Message : %s", batchId, exp.getMessage()), exp);
        } finally {
            LOGGER.error(String.format("BatchId ::[%s] :: Single Test execution Completed. Thread Name :: %s", batchId,threadName));
            responseObj.increaseCount();
            finalizeBatchProcessing(terminated);
            reqeustContext.destroy();
        }
    }

    private void finalizeBatchProcessing(final boolean terminated) {
        int waitCount = 0;
        int currentSize = 0;
        if (responseObj.getRequestCount() == responseObj.getResponseCount()) {
            try {
                LOGGER.error(String.format("BatchId ::[%s] :: Test execution completed. Message : %s . Thread Id :: %s", batchId,
                        responseObj.toString(), threadName));
                currentSize = countTransactions();
                while (currentSize < responseObj.getRequestCount() && waitCount < MAX_WAIT_SEC) {
                    waitCount = waitCount + WAIT_PERIOD_MS / 1000;
                    LOGGER.error(String
                            .format("BatchId ::[%s] :: Waiting for all transactions to update status : Current Count :: %s , Expected Count :: %s",
                                    batchId, currentSize, responseObj.getRequestCount()));
                    Thread.sleep(WAIT_PERIOD_MS);
                    currentSize = countTransactions();
                }
                
                String status;
                if (terminated) {
                	status = TransactionStatus.TERMINATED.getStatus();
                } else {
                	status = TransactionStatus.PROCESSED.getStatus();
                }
                
                String outputFileName = FilenameUtils.getName(jsonToExcelConverter.getLocation(batchId));
                batchTransactionBO.updateBatch(obj.getBatchId(), responseObj.getRequestCount(), responseObj.getStatus().get(BatchDataContainer.SUCCESS).size(),
                        responseObj.getStatus().get(BatchDataContainer.FAILURE).size(),
                        status, outputFileName);
            } catch (SystemException | BusinessException | InterruptedException exp) {
                LOGGER.error(
                        String.format("BatchId ::[%s] :: Test execution final update failed. Message : %s", batchId,
                                exp.getMessage()), exp);
            } finally {
            	removeTerminatedBatchFromCache(cacheRegistry, batchId);
                batchDataContainer.getResponseMap().remove(obj.getBatchId());
            }
            jsonToExcelConverter.stop(batchId);
        }
    }

    private Map<String, Object> getHeaderData() {
        Map<String, Object> headerData = null;
        if (response != null && MapUtils.isNotEmpty(response.getResponse())) {
            headerData = (Map<String, Object>) response.getResponse().get(HEADER);
        }
        return headerData;
    }
    
    /**
     * This method will update the aggregate map and the mapping table with status
     */
    private void updateResponse(Map<String, Object> headerData, String correlationId) throws SystemException, BusinessException {
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
	                ((Map)response.getResponse().get("data")).put(CORRELATION_ID, correlationId);
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

    private int countTransactions() {
        return responseObj.getStatus().get(BatchDataContainer.SUCCESS).size()
                + responseObj.getStatus().get(BatchDataContainer.FAILURE).size();
    }

}
