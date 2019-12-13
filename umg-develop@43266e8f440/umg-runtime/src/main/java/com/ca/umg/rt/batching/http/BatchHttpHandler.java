/**
 * 
 */
package com.ca.umg.rt.batching.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.integration.Message;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.web.client.RestTemplate;

import com.ca.framework.core.batch.TransactionStatus;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.framework.object.size.util.ObjectSizeCalculator;
import com.ca.pool.PoolManager;
import com.ca.pool.TransactionMode;
import com.ca.pool.model.TransactionCriteria;
import com.ca.umg.plugin.commons.excel.reader.constants.ExcelConstants;
import com.ca.umg.rt.batching.delegate.BatchingDelegate;
import com.ca.umg.rt.batching.entity.BatchRuntimeTransactionMapping;
import com.ca.umg.rt.core.deployment.constants.RuntimeConstants;
import com.ca.umg.rt.core.deployment.info.TestStatusInfo;
import com.ca.umg.rt.timer.BatchLru;
import com.ca.umg.rt.timer.LruContainer;
import com.ca.umg.rt.util.JsonDataUtil;
import com.ca.umg.rt.util.MessageVariables;
import com.ca.umg.rt.util.container.StaticDataContainer;

/**
 * @author chandrsa
 * 
 */
public class BatchHttpHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(BatchHttpHandler.class);
	private final static String HEADER = "header";
	private final static String DATA = "data";
	private final static String UMG_TRNS_ID = "umgTransactionId";
	private final static String SUCCESS_FLG = "success";
	public static final String CORRELATION_ID = "ExcelCorrelationID";
	public static final String ERROR_MESSAGE = "errorMessage";
	public static final String BATCH_MODELET_POLLING_INTERVAL = "BATCH_MODELET_POLLING_INTERVAL";
	public static final String VERSION_TEST_API = "version-test-api";
	public static final String RUNTIME = "runtime";
	public static final String TRACE = "TRACE";
	
	// private String batchId;

	private RestTemplate restTemplate;

	private BatchingDelegate batchingDelegate;

	private LruContainer lruContainer;

	private StaticDataContainer staticDataContainer;

	private SystemParameterProvider systemParameterProvider;

	private PoolManager poolManager;
	
    private class CallableExecuteRuntimeRequestTask implements Callable<Map<String, Object>> {

        private final Message<?> message;
        private final Map<String, Object> requestMap;
        private final String batchId;
        private final BatchTransactionCounter batchTransactionCounter;

        public CallableExecuteRuntimeRequestTask(Message<?> message, Map<String, Object> requestMap, String batchId, BatchTransactionCounter batchTransactionCounter) {
            this.message = message;
            this.requestMap = requestMap;
            this.batchId = batchId;
            this.batchTransactionCounter = batchTransactionCounter;
        }
        
        @Override
        public Map<String, Object> call() {
        	ObjectSizeCalculator.getObjectDeepSize(requestMap, batchId, "Request to runtime");
        	return executeRuntimeRequest(message, requestMap, batchId, batchTransactionCounter);
        }

    }

	@SuppressWarnings("unchecked")
	@ServiceActivator
	public Object doTransform(Message<?> message) {
		Map<String, Object> requestMap = null;
		Message<?> resultMessage = null;
		List<Map<String, Object>> requestMaps = null;
		List<Map<String, Object>> responseMaps = new LinkedList<Map<String, Object>>();
		TransactionCriteria transactionCriteria = (TransactionCriteria) message.getHeaders()
				.get(RuntimeConstants.BATCH_TRANSACTION_CRITERIA);
		String batchId = (String) message.getHeaders().get(RuntimeConstants.BATCH_ID);
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		List<Future<Map<String, Object>>> resultList = new ArrayList<>();
		RequestContext reqeustContext = null;
		int batchCount = lruContainer.getBatchLRU(batchId).getBatchCnt();
		BatchTransactionCounter batchTransactionCounter = new BatchTransactionCounter(batchId, batchCount);
		try {
			requestMaps = new LinkedList((List<Map<String, Object>>) message.getPayload());
			ObjectSizeCalculator.getObjectDeepSize(requestMaps, batchId, "List of RequestMaps for all batch transactions");
			int requestCount = 0;
			Properties properties = new Properties();
            properties.put(RequestContext.TENANT_CODE, lruContainer.getBatchLRU(batchId).getTenantCode());
            reqeustContext = new RequestContext(properties);
            updateNotPickedCount(batchId, batchTransactionCounter);
			while (CollectionUtils.isNotEmpty(requestMaps) && lruContainer.getBatchLRU(batchId) != null) {
				if(lruContainer.getBatchLRU(batchId).isBatchTerminated()){
					break;
				}
				int availableModelets = getAvailableModeletCount(transactionCriteria);
				int i = 0;
				while (i < availableModelets && CollectionUtils.isNotEmpty(requestMaps) && lruContainer.getBatchLRU(batchId) != null) {
					if(lruContainer.getBatchLRU(batchId).isBatchTerminated()){
						break;
					}
					requestMap = requestMaps.remove(0);
					CallableExecuteRuntimeRequestTask runtimeRequest  = new CallableExecuteRuntimeRequestTask(message, requestMap, batchId, batchTransactionCounter);
					if(requestCount == RuntimeConstants.INT_ZERO){
						try {
							batchingDelegate.updateBatchStatusOnly(batchId, TransactionStatus.IN_EXECUTION.getStatus());
						} catch (BusinessException e) {
							LOGGER.error("Error updating batch status", e);
						}
					}
					requestCount ++;
		            Future<Map<String, Object>> result = executor.submit(runtimeRequest);
		            batchTransactionCounter.updateNotPickedCount(requestCount);
		            updateNotPickedCount(batchId, batchTransactionCounter);
		            resultList.add(result);
					i++;
				}
				Thread.sleep(StringUtils.isNotBlank(systemParameterProvider.getParameter(BATCH_MODELET_POLLING_INTERVAL))
                        ? Long.parseLong(systemParameterProvider.getParameter(BATCH_MODELET_POLLING_INTERVAL)) : 5000);
			}
			for(Future<Map<String, Object>> result : resultList){
				responseMaps.add(result.get());
			}
		} catch (SystemException e) {
			LOGGER.error("An error occurred while fetching available modletes.", e);
		} catch (InterruptedException e) {
			LOGGER.error("An error occurred while fetching available modletes.", e);
		} catch (ExecutionException e) {
			LOGGER.error("An error occurred while fetching response from callables.", e);
		} finally {
			LOGGER.error("reqeustContext :"+reqeustContext);
            reqeustContext.destroy();
			resultMessage = MessageBuilder.withPayload(responseMaps).copyHeaders(message.getHeaders()).build();
			executor.shutdown();
		}
		return resultMessage;
	}
	
	private Map<String, Object> executeRuntimeRequest(Message<?> message, Map<String, Object> requestMap,
			String batchId, BatchTransactionCounter batchTransactionCounter) {
		Map<String, Object> data = null;
		Map<String, Object> responseMap = null;
		String requestJson;
		HttpHeaders headers;
		HttpEntity<String> entity;
		int rowNo = 0;
		RequestContext reqeustContext = null;
		try {
			if (message != null && message.getPayload() != null) {
				Properties properties = new Properties();
				properties.put(RequestContext.TENANT_CODE, message.getHeaders().get(RuntimeConstants.TENANT_CODE));
				reqeustContext = new RequestContext(properties);
				Map<String, Object> headerObj = (Map<String, Object>) requestMap.get(HEADER);
				Map<String, Object> dataObj = (Map<String, Object>) requestMap.get(DATA);
				if (dataObj != null && dataObj.get(ExcelConstants.ROW_NO) != null) {
					rowNo = Integer.valueOf((Integer) dataObj.remove(ExcelConstants.ROW_NO));
				}
				headerObj.put(MessageVariables.BATCH_ID, batchId);
				headerObj.put(MessageVariables.TRAN_MODE, TransactionMode.BATCH.getMode());
				if (!(headerObj != null && headerObj.get(MessageVariables.ERROR) != null)) {// NO
																				// PMD
					requestJson = JsonDataUtil.convertToJsonString(requestMap);
					headers = new HttpHeaders();
					headers.setContentType(MediaType.APPLICATION_JSON);
					headers.add(MessageVariables.AUTH_TOKEN, getAuthTokenForTenant(
							getAuthCode((String) message.getHeaders().get(RuntimeConstants.TENANT_CODE))));
					entity = new HttpEntity<String>(requestJson, headers);
					if (headerObj.get(MessageVariables.TRANSACTION_TYPE) != null
							&& StringUtils.equals(MessageVariables.TEST, (String) headerObj.get(MessageVariables.TRANSACTION_TYPE))) {
						TestStatusInfo statusInfo = restTemplate.postForObject(
								getRuntimeUrl().concat(systemParameterProvider.getParameter(VERSION_TEST_API)),
								entity, TestStatusInfo.class);
						responseMap = statusInfo.getResponse();
					} else {
						responseMap = (Map<String, Object>) restTemplate
								.postForObject(getRuntimeUrl().concat(RUNTIME), entity, Map.class);
					}
				} else {
					responseMap = requestMap;
				}
				if (rowNo != RuntimeConstants.INT_ZERO) {
					LOGGER.error("responseMap is :" + responseMap);
					if (responseMap != null && responseMap.get(DATA) != null) {
						LOGGER.error("responseMap in if  is" + responseMap);
						((Map<String, Object>) responseMap.get(DATA)).put(ExcelConstants.ROW_NO, rowNo);
					} else {
						LOGGER.error("responseMap in else is" + responseMap);
						Map<String, Object> dataMap = new LinkedHashMap<String, Object>();
						dataMap.put(ExcelConstants.ROW_NO, rowNo);
						responseMap.put(DATA, dataMap);
					}
				}

			}
		} catch (Exception exp) {// NOPMD
			responseMap = new HashMap<>();
			data = new HashMap<>();
			Map<String, Object> header = new HashMap<String, Object>();
			header.put(ERROR_MESSAGE, exp.getMessage());
			header.put(TRACE, ExceptionUtils.getStackTrace(exp));
			if (rowNo != RuntimeConstants.INT_ZERO) {
				data.put(ExcelConstants.ROW_NO, rowNo);
			}
			responseMap.put(HEADER, header);
			responseMap.put(DATA, data);
			LOGGER.error("Error posting batch request to runtime", exp);
		} finally {
			if (MapUtils.isNotEmpty(requestMap)) {
				boolean success = finalizeResults(requestMap, responseMap, batchId);
				updateLRU(batchId, success);
				batchTransactionCounter.incrementCount(success);
				try {
					batchingDelegate.updateSuccessFailCount(batchId, batchTransactionCounter.getSuccessCount(), batchTransactionCounter.getFailCount());
				} catch (BusinessException | SystemException e) {
					LOGGER.error(String.format("Batch Id :: %s, Error occured while updating batch success fail count. Exception msg :: %s",
							batchId, e));
				}
			}
			if (reqeustContext != null) {
				reqeustContext.destroy();
			}
		}
		return responseMap;
	}
	
	private void updateNotPickedCount(String batchId, BatchTransactionCounter batchTransactionCounter) {
		try {
			batchingDelegate.updateNotPickedCount(batchId, batchTransactionCounter.getNotPickedCount());
		} catch (BusinessException | SystemException e) {
			LOGGER.error(String.format("Batch Id :: %s, Error occured while updating batch notPickedCount. Exception msg :: %s",
					batchId, e));
		}
	}

	private int getAvailableModeletCount(TransactionCriteria transactionCriteria) throws SystemException {
		KeyValuePair<String, Integer> poolDetail = poolManager.getModeletPoolAndCount(transactionCriteria);
		if(poolDetail.getValue() != null){
			return poolDetail.getValue();
		} else {
			return RuntimeConstants.INT_ZERO;
		}
	}

	private String getRuntimeUrl() {
		String baseUrl = staticDataContainer.getTenantUrlMap().get(RequestContext.getRequestContext().getTenantCode())
				.getRuntimeBaseUrl();
		// TODO move string literal to constant class
		String runtime = systemParameterProvider.getParameter("umg-runtime-context");
		StringBuffer url = new StringBuffer(baseUrl);
		// TODO move "runtime" literal to system param
		url.append(RuntimeConstants.CHAR_SLASH).append(runtime).append(RuntimeConstants.CHAR_SLASH);
		return url.toString();
	}

	private String getAuthCode(String tenantCode) {
		return staticDataContainer.getActiveAuthToken(tenantCode);
		  
	}

	private String getAuthTokenForTenant(String authToken) throws BusinessException, SystemException {
		return RequestContext.getRequestContext().getTenantCode() + "." + authToken;
	}

	private boolean finalizeResults(Map<String, Object> requestMap, Map<String, Object> responseMap, String batchId) {
		Map<String, Object> headerData = null;

		boolean success = false;
		BatchRuntimeTransactionMapping runtimeTransactionMapping = null;
		try {
			headerData = getHeaderData(requestMap, responseMap);
			if (headerData != null && (String) headerData.get(UMG_TRNS_ID) != null) {// If
																						// the
																						// response
																						// comes
																						// from
																						// runtime,
																						// it
																						// contains
																						// umg
																						// transaction
																						// id
				success = (boolean) headerData.get(SUCCESS_FLG);
				runtimeTransactionMapping = new BatchRuntimeTransactionMapping();
				runtimeTransactionMapping.setBatchId(batchId);
				runtimeTransactionMapping.setTransactionId((String) headerData.get(UMG_TRNS_ID));
				runtimeTransactionMapping.setStatus(success ? "SUCCESS" : "FAILURE");
				runtimeTransactionMapping.setError("No Error!");
				batchingDelegate.updateBatchTransactionMapping(runtimeTransactionMapping);
			} else {// request not sent to runtime , it is a excel input error
					// or any validation errors
				runtimeTransactionMapping = new BatchRuntimeTransactionMapping();
				runtimeTransactionMapping.setBatchId(batchId);
				runtimeTransactionMapping.setTransactionId("EXCEL_ERROR");
				runtimeTransactionMapping.setStatus(success ? "SUCCESS" : "FAILURE");
				runtimeTransactionMapping.setError("ERROR");
				batchingDelegate.addBatchTransactionMapping(runtimeTransactionMapping, Boolean.FALSE);
			}
		} catch (SystemException | BusinessException exp) {
			LOGGER.error("Error finalizing batch request", exp);
		}
		return success;
	}

	private void updateLRU(String batchId, boolean success) {
		BatchLru batchLru = lruContainer.getBatchLRU(batchId);
		if (batchLru != null) {
			batchLru.incrementCounter(success);
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getHeaderData(Map<String, Object> requestMap, Map<String, Object> responseMap) {
		Map<String, Object> headerData = null;
		if (MapUtils.isNotEmpty(responseMap) && MapUtils.isNotEmpty(requestMap)) {
			headerData = (Map<String, Object>) responseMap.get(HEADER);
			if (MapUtils.isEmpty(headerData)) {
				headerData = (Map<String, Object>) requestMap.get(HEADER);
				responseMap.put(HEADER, headerData);
			}
		}
		return headerData;
	}

	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public BatchingDelegate getBatchingDelegate() {
		return batchingDelegate;
	}

	public void setBatchingDelegate(BatchingDelegate batchingDelegate) {
		this.batchingDelegate = batchingDelegate;
	}

	public LruContainer getLruContainer() {
		return lruContainer;
	}

	public void setLruContainer(LruContainer lruContainer) {
		this.lruContainer = lruContainer;
	}

	public StaticDataContainer getStaticDataContainer() {
		return staticDataContainer;
	}

	public void setStaticDataContainer(StaticDataContainer staticDataContainer) {
		this.staticDataContainer = staticDataContainer;
	}

	public SystemParameterProvider getSystemParameterProvider() {
		return systemParameterProvider;
	}

	public void setSystemParameterProvider(SystemParameterProvider systemParameterProvider) {
		this.systemParameterProvider = systemParameterProvider;
	}

	public PoolManager getPoolManager() {
		return poolManager;
	}

	public void setPoolManager(PoolManager poolManager) {
		this.poolManager = poolManager;
	}

}