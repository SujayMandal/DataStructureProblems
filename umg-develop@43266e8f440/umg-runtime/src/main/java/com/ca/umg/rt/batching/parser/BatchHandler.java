/**
 * 
 */
package com.ca.umg.rt.batching.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.Message;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.support.MessageBuilder;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.framework.core.util.UmgFileProxy;
import com.ca.umg.plugin.commons.excel.converter.JsonToExcelBatchContainer;
import com.ca.umg.plugin.commons.excel.converter.JsonToExcelConverter;
import com.ca.umg.plugin.commons.excel.reader.ReadHeaderSheet;
import com.ca.umg.plugin.commons.excel.reader.constants.ExcelConstants;
import com.ca.umg.plugin.commons.excel.util.JsonToExcelConverterUtil;
import com.ca.umg.rt.batching.data.BatchResponse;
import com.ca.umg.rt.batching.delegate.BatchingDelegate;
import com.ca.umg.rt.batching.entity.BatchTransaction;
import com.ca.umg.rt.core.deployment.constants.RuntimeConstants;
import com.ca.umg.rt.flows.container.EnvironmentVariables;
import com.ca.umg.rt.timer.LruContainer;
import com.ca.umg.rt.util.JsonDataUtil;
import com.ca.umg.rt.util.MessageVariables;

/**
 * @author chandrsa
 * 
 */
@SuppressWarnings("PMD")
@Named
public class BatchHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchHandler.class);

    private LruContainer container;

    private BatchingDelegate batchingDelegate;

    private CacheRegistry cacheRegistry;

    private JsonToExcelConverter jsonToExcelConverter;

    private UmgFileProxy umgFileProxy;

    private SystemParameterProvider systemParameterProvider;

    public static final String CORRELATION_ID = "ExcelCorrelationID";

    private static final String CHILDERN = "children";

    @SuppressWarnings("unchecked")
    @ServiceActivator
    public Object aggregateFileConstruction(Message<?> message) throws SystemException, BusinessException {
        Message<?> transformedMessage = null;
        List<Map<String, Object>> results = null;
        BatchResponse batchResponse = null;
        String batchResponseJson = null;
        String originalFileName = null;
        String batchId = null;
        String absolutePath = null;
        String reponseFileName = null;
        RequestContext reqeustContext = null;
        String tenantCode = null;
        try {
            batchResponse = new BatchResponse();
            results = (List<Map<String, Object>>) message.getPayload();
            tenantCode = (String) message.getHeaders().get(RuntimeConstants.TENANT_CODE);
            originalFileName = (String) message.getHeaders().get(FileHeaders.FILENAME);
            if(results != null) {
            LOGGER.error("No of transactions received a :" + results.size());
            if (CollectionUtils.isNotEmpty(results)) {
                Properties properties = new Properties();
                properties.put(RequestContext.TENANT_CODE, tenantCode);
                reqeustContext = new RequestContext(properties);
                batchId = (String) message.getHeaders().get(RuntimeConstants.BATCH_ID);
                LOGGER.error(String.format("Aggregation finished -Finalizing the batchId %s ", batchId));
                absolutePath = (String) message.getHeaders().get(FileHeaders.ORIGINAL_FILE);
                batchResponse.setResponses(results);
                batchResponse.setBatchId(batchId);
                batchResponse.setFileName(originalFileName);
                batchResponseJson = JsonDataUtil.convertToJsonStringPrettyPrint(batchResponse);
                BatchTransaction batchTransaction = batchingDelegate.getBatch(batchId);
                int successCount = batchTransaction.getSuccessCount().intValue();
                int failureCount = batchTransaction.getFailCount().intValue();
                int totalCount = batchTransaction.getTotalRecords().intValue();
                String storeRLogs = batchTransaction.getStoreRlogs();
                String status = batchTransaction.getStatus();
                int notPickedCount = batchTransaction.getNotPickedCount().intValue();
                String modelIdentifier = (String) message.getHeaders().get(EnvironmentVariables.MODEL_CHECKSUM);

                reponseFileName = getResponseFileName(originalFileName, "Success");
                if (reponseFileName.endsWith(RuntimeConstants.XLS_EXTN) || reponseFileName.endsWith(RuntimeConstants.XLSX_EXTN)) {
                    String extension = FilenameUtils.getExtension(reponseFileName);
                    JsonToExcelBatchContainer jsonToExcelBatchContainer = jsonToExcelConverter.start(batchId, reponseFileName,
                            false, extension);
                    int count = 1;
                    for (Map<String, Object> jsonData : results) {
                        Map<String, Object> headerData = (Map<String, Object>) jsonData.get("header");
                        Map<String, Object> data = (Map<String, Object>) jsonData.get("data");
                        String tranId = (String) headerData.get(RuntimeConstants.UMG_TRNS_ID);
                        if ((String) headerData.get(RuntimeConstants.ERROR_CODE) != null) {// In case of error comes from runtime
                            setHeaderFieldstoData(headerData, data);
                            addErrorTransaction(batchId, RuntimeConstants.INT_ZERO, jsonData, tranId);
                        } else if ((String) data.get(RuntimeConstants.ERROR_CODE) != null) { // in case of error comes from excel
                                                                                              // validations
                            setHeaderFieldstoData(headerData, data);
                            addErrorTransaction(batchId, RuntimeConstants.INT_ZERO, jsonData, tranId);
                        } else {
                            setJsonData(jsonData, headerData, data);
                            headerData.remove(ReadHeaderSheet.TRANSACTIONID);
                            headerData.remove(ReadHeaderSheet.SUCCESS);
                            headerData.remove(RuntimeConstants.UMG_TRNS_ID);
                            jsonToExcelConverter.addTransaction(batchId, tranId, jsonData, count++, false);
                        }
                        updateHeaderWithCount(headerData, successCount, failureCount, totalCount, notPickedCount, storeRLogs ,modelIdentifier);
                    }
                    jsonToExcelConverter.stop(batchId);
                    reponseFileName = getResponseFileName(originalFileName, status);
                    transformedMessage = MessageBuilder.withPayload(jsonToExcelBatchContainer.getBatchOutput().toByteArray())
                            .copyHeaders(message.getHeaders()).setHeader(FileHeaders.FILENAME, reponseFileName)
                            .setHeader(RuntimeConstants.SAN_PATH,
                                    umgFileProxy.getSanPath(systemParameterProvider.getParameter(SystemConstants.SAN_BASE)))
                            .build();

                } else {
                    reponseFileName = getResponseFileName(originalFileName, status);
                    transformedMessage = MessageBuilder.withPayload(batchResponseJson).copyHeaders(message.getHeaders())
                            .setHeader(FileHeaders.FILENAME, reponseFileName)
                            .setHeader(RuntimeConstants.SAN_PATH,
                                    umgFileProxy.getSanPath(systemParameterProvider.getParameter(SystemConstants.SAN_BASE)))
                            .build();
                }
                deleteInprogressFile(absolutePath);
                batchingDelegate.updateBatchOutputFile(batchId, reponseFileName);
                LOGGER.error(String.format("Aggregation finished -Finalized the batchId %s ", batchId));
            }
        } 
        }catch (IOException ioException) {
            LOGGER.error(ioException.getMessage(), ioException);
        } finally {
        	if(reqeustContext != null){
            reqeustContext.destroy();
        	}
            removeFileLock(originalFileName, tenantCode);
            if(batchId != null){
            cacheRegistry.getMap("BATCH_STATUS_UPDTAE_MAP").remove(batchId);
            }
        }
        return transformedMessage;
    }

    private void updateHeaderWithCount(Map<String, Object> headerData, int successCount, int failureCount, int totalCount, int notPickedCount,String storeRLogs,String modelIdentifier) {
        headerData.put(JsonToExcelConverterUtil.SUCCESS_COUNT, successCount);
        headerData.put(JsonToExcelConverterUtil.FAILURE_COUNT, failureCount);
        headerData.put(JsonToExcelConverterUtil.TOTAL_COUNT, totalCount);
        headerData.put(JsonToExcelConverterUtil.NOT_PICKED_COUNT, notPickedCount);
        headerData.put(JsonToExcelConverterUtil.STORE_RLOG, storeRLogs);
        headerData.put(JsonToExcelConverterUtil.MODEL_IDENTIFIER, modelIdentifier);
    }

    private void setJsonData(Map<String, Object> jsonData, Map<String, Object> headerData, Map<String, Object> data)
            throws SystemException, BusinessException {
        Map<String, Object> modelInterfaceDefinition = getModelInterfaceDefinition(headerData);
        List<Map<String, Object>> midOutput = (List<Map<String, Object>>) modelInterfaceDefinition
                .get(MessageVariables.MID_OUTPUT);
        Map<String, Object> newMap = new LinkedHashMap<String, Object>();
        setHeaderFieldstoData(headerData, newMap);
        newMap.putAll(getHeaderWithDatatype(midOutput, data, headerData));
        newMap.put(ExcelConstants.ROW_NO, data.get(ExcelConstants.ROW_NO));
        jsonData.put("data", newMap);
    }

    private void setHeaderFieldstoData(Map<String, Object> headerData, Map<String, Object> data) {
        if (headerData.get("transactionId") != null) {
            data.put("transactionId|STRING", new String((String) headerData.get("transactionId")));
        }

        if (headerData.get("umgTransactionId") != null) {
            data.put("umgTransactionId|STRING", new String((String) headerData.get("umgTransactionId")));
        }

        if (headerData.get("success") != null) {
            data.put("success|BOOLEAN", (Boolean) headerData.get("success"));
        }
    }

    private int addErrorTransaction(String batchId, int count, Map<String, Object> jsonData, String tranId) {
        Map<String, Object> header = (Map<String, Object>) jsonData.get("header");
        Map<String, Object> failureTerminated = new LinkedHashMap<String, Object>();
        jsonData.put(FrameworkConstant.FAILURE_TERMINATED, failureTerminated);
        failureTerminated.put("transactionId|STRING", header.get("transactionId"));
        failureTerminated.put("umgTransactionId|STRING", header.get("umgTransactionId"));
        failureTerminated.put("errorCode|STRING", (String) header.get(RuntimeConstants.ERROR_CODE));
        if (StringUtils.contains((String) header.get(RuntimeConstants.ERROR_CODE), RuntimeConstants.RSE_EXCEPTION)) {
            failureTerminated.put("errorMessage|STRING", RuntimeConstants.GENERIC_ERROR_MESSAGE);
        } else {
            failureTerminated.put("errorMessage|STRING", (String) header.get("errorMessage"));
        }
        header.remove(ReadHeaderSheet.TRANSACTIONID);
        header.remove(ReadHeaderSheet.SUCCESS);
        header.remove(RuntimeConstants.UMG_TRNS_ID);
        header.remove("errorMessage");
        jsonToExcelConverter.addTransaction(batchId, tranId, jsonData, count, true);
        return count;
    }

    private String getResponseFileName(String originalFileName, String status) {
        return FilenameUtils.getBaseName(originalFileName) + RuntimeConstants.CHAR_UNDERSCORE + status + RuntimeConstants.CHAR_DOT
                + FilenameUtils.getExtension(originalFileName);
    }

    private void deleteInprogressFile(String absolutePath) throws SystemException {
        String inProgressFilePath = null;
        String oldPath = StringUtils.substringBefore(absolutePath, RequestContext.getRequestContext().getTenantCode());
        String newAbsolutePath = StringUtils.replaceOnce(absolutePath, oldPath,
                umgFileProxy.getSanPath(systemParameterProvider.getParameter(SystemConstants.SAN_BASE)) + File.separator);
        StringBuffer inputString = new StringBuffer(File.separatorChar).append(RuntimeConstants.BATCH_INPUT)
                .append(File.separatorChar);
        StringBuffer inprogressString = new StringBuffer(File.separatorChar).append(RuntimeConstants.BATCH_INPROGRESS)
                .append(File.separatorChar);
        if (StringUtils.isNotBlank(newAbsolutePath)) {
            inProgressFilePath = StringUtils.replaceOnce(newAbsolutePath, inputString.toString(), inprogressString.toString());
        }
        LOGGER.debug(String.format("Deleting the file from %s", inProgressFilePath));
        File file = new File(inProgressFilePath);
        if (!file.delete()) {
            LOGGER.error(String.format("Error deleting the file from %s", inProgressFilePath));
        }
    }

    /**
     * Removes the lock on the file after the processing completes.
     * 
     * @param originalFileName
     * @param tenantCode
     */
    private void removeFileLock(String originalFileName, String tenantCode) {
        Map<Object, Object> lockMap = null;
        if (StringUtils.isNotBlank(originalFileName) && StringUtils.isNotBlank(tenantCode)) {
            lockMap = cacheRegistry.getMap(tenantCode + RuntimeConstants.LOCK_APPENDER);
            if (MapUtils.isNotEmpty(lockMap)) {
                lockMap.remove(originalFileName);
            }
        }
    }

    public LruContainer getContainer() {
        return container;
    }

    public void setContainer(LruContainer container) {
        this.container = container;
    }

    public BatchingDelegate getBatchingDelegate() {
        return batchingDelegate;
    }

    public void setBatchingDelegate(BatchingDelegate batchingDelegate) {
        this.batchingDelegate = batchingDelegate;
    }

    public CacheRegistry getCacheRegistry() {
        return cacheRegistry;
    }

    public void setCacheRegistry(CacheRegistry cacheRegistry) {
        this.cacheRegistry = cacheRegistry;
    }

    /**
     * @return the jsonToExcelConverter
     */
    public JsonToExcelConverter getJsonToExcelConverter() {
        return jsonToExcelConverter;
    }

    /**
     * @param jsonToExcelConverter
     *            the jsonToExcelConverter to set
     */
    public void setJsonToExcelConverter(JsonToExcelConverter jsonToExcelConverter) {
        this.jsonToExcelConverter = jsonToExcelConverter;
    }

    public UmgFileProxy getUmgFileProxy() {
        return umgFileProxy;
    }

    public void setUmgFileProxy(UmgFileProxy umgFileProxy) {
        this.umgFileProxy = umgFileProxy;
    }

    public SystemParameterProvider getSystemParameterProvider() {
        return systemParameterProvider;
    }

    public void setSystemParameterProvider(SystemParameterProvider systemParameterProvider) {
        this.systemParameterProvider = systemParameterProvider;
    }

    /**
     * DOCUMENT ME!
     *
     * @param flowMetadata
     *            DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     * @throws SystemException
     **/
    private Map<String, Object> getModelInterfaceDefinition(final Map<String, Object> headers) throws SystemException { 
    	LOGGER.error("batchingDelegate==="+batchingDelegate+" tenantCode==="+RequestContext.getRequestContext().getTenantCode()+" ModelName==="+headers.get(EnvironmentVariables.MODEL_NAME)
    	+" MinorVersion==="+headers.get(EnvironmentVariables.MAJOR_VERSION)+" MajorVersion==="+headers.get(EnvironmentVariables.MINOR_VERSION));
        byte[] modelOutput = batchingDelegate.getModelOutput(RequestContext.getRequestContext().getTenantCode(),
                (String) headers.get(EnvironmentVariables.MODEL_NAME), (Integer) headers.get(EnvironmentVariables.MAJOR_VERSION),
                (Integer) headers.get(EnvironmentVariables.MINOR_VERSION));
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> mappingData = null;
        try {
            mappingData = mapper.readValue(modelOutput, new TypeReference<HashMap<String, Object>>() {
            });
        } catch (IOException e) {
            LOGGER.error("Error while converting input mapping data to json object");
        }

        return mappingData;
    }

    private Map<String, Object> getHeaderWithDatatype(List<Map<String, Object>> midOutput, Object data,
            Map<String, Object> headerData) {
        Map<String, Object> dataWithDatatype = new LinkedHashMap<String, Object>();
        if(data != null){
        	for (Map<String, Object> midParam : midOutput) {
        		if (midParam.get(CHILDERN) != null) {
        			if (StringUtils.equalsIgnoreCase("OBJECT|ARRAY", (String) midParam.get("dataTypeStr"))) {
        				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        				List<Map<String, Object>> dataList = null;
        				List<Map<String, Object>> midParamChilds = (List<Map<String, Object>>) (midParam.get(CHILDERN));
        				String paramName = (String) midParam.get(FrameworkConstant.API_NAME);
        				if (paramName == null) {
        					paramName = (String) midParam.get(FrameworkConstant.NAME);
        				}
        				dataList = (List<Map<String, Object>>) (((Map<String, Object>) data).get(paramName));
        				iterateMidParam(headerData, list, dataList, midParamChilds);
        				dataWithDatatype.put(paramName, list);
        			} else {
        				if (midParam.get(FrameworkConstant.NAME) != null) {
        					dataWithDatatype.put((String) midParam.get(FrameworkConstant.NAME),
        							getHeaderWithDatatype((List<Map<String, Object>>) midParam.get(CHILDERN),
        									((Map<String, Object>) data).get(midParam.get(FrameworkConstant.NAME)), headerData));
        				} else {
        					dataWithDatatype.put((String) midParam.get(FrameworkConstant.API_NAME),
        							getHeaderWithDatatype((List<Map<String, Object>>) midParam.get(CHILDERN),
        									((Map<String, Object>) data).get(midParam.get(FrameworkConstant.API_NAME)), headerData));
        				}
        			}
        		} else {
        			if (midParam.get(FrameworkConstant.NAME) != null) {
        				dataWithDatatype.put(
        						midParam.get(FrameworkConstant.NAME) + RuntimeConstants.CHAR_PIPE
        						+ (StringUtils
        								.upperCase((String) ((Map<String, Object>) midParam.get("datatype")).get("type"))),
        								((Map<String, Object>) data).get(midParam.get(FrameworkConstant.NAME)));
        			} else {
        				dataWithDatatype.put(
        						midParam.get(FrameworkConstant.API_NAME) + RuntimeConstants.CHAR_PIPE
        						+ (StringUtils
        								.upperCase((String) ((Map<String, Object>) midParam.get("datatype")).get("type"))),
        								((Map<String, Object>) data).get(midParam.get(FrameworkConstant.API_NAME)));
        			}
        		}
        	}
        }
        return dataWithDatatype;

    }

    private void iterateMidParam(Map<String, Object> headerData, List<Map<String, Object>> list,
    		List<Map<String, Object>> dataList, List<Map<String, Object>> midParamChilds) {
    	if(CollectionUtils.isNotEmpty(dataList)){
    		for (Map<String, Object> childObj : dataList) {
    			list.add(getHeaderWithDatatype(midParamChilds, childObj, headerData));
    		}
    	}
    }

}