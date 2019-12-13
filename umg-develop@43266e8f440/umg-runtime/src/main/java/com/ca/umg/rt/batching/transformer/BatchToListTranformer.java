/**
 *
 */
package com.ca.umg.rt.batching.transformer;

import static com.ca.pool.model.RequestType.PROD;
import static java.lang.Integer.valueOf;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.Message;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.transformer.AbstractTransformer;
import org.springframework.util.FileCopyUtils;

import com.ca.framework.core.batch.TransactionStatus;
import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.rmodel.info.VersionExecInfo;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.framework.core.util.UmgFileProxy;
import com.ca.framework.object.size.util.ObjectSizeCalculator;
import com.ca.pool.PoolObjectsLoader;
import com.ca.pool.model.Pool;
import com.ca.pool.model.RequestMode;
import com.ca.pool.model.TransactionCriteria;
import com.ca.umg.plugin.commons.excel.model.ExcelData;
import com.ca.umg.plugin.commons.excel.reader.ExcelReader;
import com.ca.umg.plugin.commons.excel.reader.ReadHeaderSheet;
import com.ca.umg.plugin.commons.excel.reader.constants.ExcelConstants;
import com.ca.umg.plugin.commons.excel.reader.exception.codes.ExcelPluginExceptionCodes;
import com.ca.umg.rt.batching.data.BatchRequest;
import com.ca.umg.rt.batching.data.BatchTransformedPayload;
import com.ca.umg.rt.batching.data.JsonParseStatus;
import com.ca.umg.rt.batching.delegate.BatchingDelegate;
import com.ca.umg.rt.core.deployment.bo.DeploymentBO;
import com.ca.umg.rt.core.deployment.constants.RuntimeConstants;
import com.ca.umg.rt.exception.codes.RuntimeExceptionCode;
import com.ca.umg.rt.flows.container.EnvironmentVariables;
import com.ca.umg.rt.timer.BatchLru;
import com.ca.umg.rt.timer.LruContainer;
import com.ca.umg.rt.util.JsonDataUtil;
import com.ca.umg.rt.util.MessageVariables;

/**
 * @author chandrsa
 *
 */

@SuppressWarnings("PMD")
public class BatchToListTranformer extends AbstractTransformer {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchToListTranformer.class);

    private BatchingDelegate batchingDelegate;

    private CacheRegistry cacheRegistry;

    private LruContainer lruContainer;

    private SystemParameterProvider systemParameterProvider;

    private ExcelReader excelReader;

    private UmgFileProxy umgFileProxy;

    private DeploymentBO deploymentBO;

    private PoolObjectsLoader poolObjectsLoader;

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.integration.transformer.AbstractTransformer#doTransform(org.springframework.integration.Message)
     */
    @Override
    protected Object doTransform(Message<?> message) throws SystemException, BusinessException {
        File input = null;
        Reader reader = null;
        String fileName = null;
        String tenantCode = null;
        Message<?> transformedMessage = null;
        BatchRequest batchRequest = null;
        RequestContext reqeustContext = null;
        String absolutePath = null;
        JsonParseStatus jsonParseStatus = null;
        FileInputStream fis = null;
        ExcelData excelData = null;
        List<Map<String, Object>> jsonList = null;
        Object payLoad = null;
        String batchId = null;
        InputStreamReader isr = null;
        try {
            input = (File) message.getPayload();
            fileName = input.getName();
            LOGGER.error(String.format("Starting Batch Processing For File :: %s", fileName));
            absolutePath = input.getAbsolutePath();
            tenantCode = getTenantCode(input.getAbsolutePath());
            Properties properties = new Properties();
            properties.put(RequestContext.TENANT_CODE, tenantCode);
            reqeustContext = new RequestContext(properties);
            if (fileName.endsWith(RuntimeConstants.XLS_EXTN) || fileName.endsWith(RuntimeConstants.XLSX_EXTN)) {
                fis = new FileInputStream(input);
                excelData = excelReader.parseXLData(fis, fileName);
                ObjectSizeCalculator.getObjectDeepSize(excelData, fileName, "ExcelData parsed in runtime");
                jsonList = excelData.getExcelData();
                payLoad = excelData.getModifiedExcel();
                if (CollectionUtils.isNotEmpty(jsonList)) {
                    batchRequest = new BatchRequest();
                    batchRequest.setRequestCount(jsonList.size());
                    batchRequest.setRequests(jsonList);
                    ObjectSizeCalculator.getObjectDeepSize(batchRequest, fileName, "Excel parsed batchRequest in runtime");
                } else {
                    fis = new FileInputStream(input);
                    payLoad = IOUtils.toByteArray(fis);
                }
            } else {
                try {
                    fis = new FileInputStream(input);
                    isr = new InputStreamReader(fis, Charset.defaultCharset());
                    reader = new BufferedReader(isr);
                    payLoad = FileCopyUtils.copyToString(reader);
                    batchRequest = getJsonList((String) payLoad);
                    if (batchRequest.getRequestCount() != batchRequest.getRequests().size()) {
                        BusinessException.raiseBusinessException(RuntimeExceptionCode.RSE000511, new Object[]{fileName});
                    }
                } catch (BusinessException exp) {
                    jsonParseStatus = new JsonParseStatus();
                    jsonParseStatus.setErrorCode(exp.getCode());
                    jsonParseStatus.setErrorMessage(exp.getLocalizedMessage());
                }
            }

            BatchTransformedPayload batchPayload = new BatchTransformedPayload();
            LOGGER.error(String.format("Started Batch Processing For File :: %s", absolutePath));
            batchPayload.setTenantCode(tenantCode);
            batchPayload.setAbsolutePath(absolutePath);
            batchPayload.setFileName(fileName);
            batchPayload.setMessage(message);
            batchPayload.setSanPath(umgFileProxy.getSanPath(systemParameterProvider.getParameter(SystemConstants.SAN_BASE)));
            transformedMessage = setTransformedMessage(fileName, tenantCode, batchRequest, absolutePath, excelData, payLoad,
                    batchPayload, jsonParseStatus, batchId);
            if (!input.delete()) {
                LOGGER.error("failed to delete File '" + input + "'");
            } else {
                Map<String, Map<String, String>> batchInputFilesMap = cacheRegistry.getMap(FrameworkConstant.BATCH_INPUT_FILES_MAP);
                if (MapUtils.isNotEmpty(batchInputFilesMap) && MapUtils.isNotEmpty(batchInputFilesMap.get(tenantCode)) &&
                        batchInputFilesMap.get(tenantCode).containsKey(fileName)) {
                    Map<String, String> files = batchInputFilesMap.get(tenantCode);
                    files.remove(fileName);
                    batchInputFilesMap.put(tenantCode, files);
                    cacheRegistry.getMap(FrameworkConstant.BATCH_INPUT_FILES_MAP).putAll(batchInputFilesMap);
                }
            }
        } catch (Exception exception) {// NOPMD
            LOGGER.error(exception.getMessage(), exception);
            KeyValuePair<Object, byte[]> errorMsgPayLoad = null;
            if (fileName.endsWith(RuntimeConstants.XLS_EXTN) || fileName.endsWith(RuntimeConstants.XLSX_EXTN)) {
                Workbook wb = null;
                if (fileName.endsWith(RuntimeConstants.XLS_EXTN)) {
                    wb = new HSSFWorkbook();
                } else {
                    wb = new XSSFWorkbook();
                }
                Sheet sheet = wb.createSheet(ExcelConstants.EXCEL_ERROR_SHEET);
                Row row = sheet.createRow(0);
                row.createCell(0).setCellValue(ExcelConstants.EXCEL_ERROR_SHEET);
                row = sheet.createRow(1);
                row.createCell(0).setCellValue(RuntimeConstants.GENERIC_ERROR_MESSAGE);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    wb.write(baos);
                } catch (IOException e) {
                    BusinessException.newBusinessException(ExcelPluginExceptionCodes.EXPL000021, new Object[]{});
                }
                if (payLoad != null) {
                    errorMsgPayLoad = new KeyValuePair<>(payLoad, baos.toByteArray());
                }
            } else {
                if (payLoad != null) {
                    errorMsgPayLoad = new KeyValuePair<>(payLoad,
                            RuntimeConstants.GENERIC_ERROR_MESSAGE.getBytes());
                }
            }
            BatchTransformedPayload batchPayload = new BatchTransformedPayload();
            LOGGER.error(String.format("Started Batch Processing For File :: %s", absolutePath));
            batchPayload.setTenantCode(tenantCode);
            batchPayload.setAbsolutePath(absolutePath);
            batchPayload.setFileName(fileName);
            batchPayload.setMessage(message);
            batchPayload.setError(true);
            batchPayload.setSanPath(umgFileProxy.getSanPath(systemParameterProvider.getParameter(SystemConstants.SAN_BASE)));
            if (batchId != null) {
                updateErrorInPyldAndOutputFile(batchPayload, errorMsgPayLoad, fileName, batchId);
            }
            transformedMessage = getBatchTransformedMsg(batchPayload);
            LOGGER.error(String.format("Writing into error folder :: %s, Reference batchId :: %s", absolutePath, batchId));
            removeFileLock(fileName, tenantCode);
        } finally {
            if (reqeustContext != null) {
                reqeustContext.destroy();
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    LOGGER.debug("Error occurred while closing stream.");;
                }
            }
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    LOGGER.debug("Error occurred while closing stream.");
                }
            }
            if(reader != null){
                try{
                    reader.close();
                }catch (IOException e) {
                    LOGGER.debug("Error occurred while closing stream.");
                }
            }
        }
        return transformedMessage;
    }

    private Message<?> setTransformedMessage(String fileName, String tenantCode, BatchRequest batchRequest, String absolutePath,
                                             ExcelData excelData, Object payLoad, BatchTransformedPayload batchPayload, JsonParseStatus jsonParseStatus, String batchId)
            throws SystemException, BusinessException, JsonGenerationException, JsonMappingException,
            org.omg.CORBA.SystemException, IOException {
        String versionName;
        int majorVer;
        int minorVer = 0;
        boolean maxMinorVerFound = false;
        Message<?> transformedMessage;
        KeyValuePair<Object, List<Map<String, Object>>> msgPayload;

        if (excelData != null) {
            final Map<String, Object> headerDetails = excelData.getHeaderDetails();
            versionName = headerDetails.get("modelName").toString();
            majorVer = valueOf(headerDetails.get("majorVersion").toString());
            if (headerDetails != null && headerDetails.get("minorVersion") == null) {
                minorVer = deploymentBO.getMaxMinorVersion(versionName, majorVer);
                maxMinorVerFound = true;
                headerDetails.put("minorVersion", minorVer);
                excelData.setHeaderDetails(headerDetails);
            }
        }
        if (batchRequest != null) {
            List<Map<String, Object>> batchRequestList = new ArrayList<Map<String, Object>>();
            batchRequestList = batchRequest.getRequests();
            for (Map<String, Object> request : batchRequestList) {
                final Map<Object, Object> headerDetails = (Map<Object, Object>) request.get("header");
                versionName = headerDetails.get("modelName").toString();
                majorVer = valueOf(headerDetails.get("majorVersion").toString());
                if (headerDetails != null && headerDetails.get("minorVersion") == null) {
                    if (!maxMinorVerFound) {
                        minorVer = deploymentBO.getMaxMinorVersion(versionName, majorVer);
                    }
                    headerDetails.put("minorVersion", minorVer);
                    request.put("header", headerDetails);
                }
            }
        }


        if (batchRequest == null || CollectionUtils.isEmpty(batchRequest.getRequests())
                || batchRequest.getRequestCount() != batchRequest.getRequests().size()) {
            batchId = createBatchRecords(fileName, batchPayload, excelData);
            if (excelData != null && excelData.getModifiedExcel() != null && excelData.getModifiedExcel().length > 0) {
                KeyValuePair<Object, byte[]> errorMsgPayLoad = new KeyValuePair<>(payLoad, excelData.getModifiedExcel());
                updateErrorInPyldAndOutputFile(batchPayload, errorMsgPayLoad, fileName, batchId);
            } else {
                KeyValuePair<Object, byte[]> errorMsgPayLoad = null;
                if (jsonParseStatus != null && isNotEmpty(jsonParseStatus.getErrorCode())) {

                    errorMsgPayLoad = new KeyValuePair<>(payLoad, JsonDataUtil.convertToJsonString(jsonParseStatus).getBytes());
                }
                updateErrorInPyldAndOutputFile(batchPayload, errorMsgPayLoad, fileName, batchId);
            }
            transformedMessage = getBatchTransformedMsg(batchPayload);
            LOGGER.error(String.format("Writing into error folder :: %s, Reference batchId :: %s", absolutePath, batchId));
            removeFileLock(fileName, tenantCode);
        } else {
            Map<String, Object> headerMap = (Map<String, Object>) batchRequest.getRequests().get(0).get("header");

            KeyValuePair<Object, byte[]> errorMsgPayLoad = null;
            if (headerMap.get("error") != null && Boolean.valueOf((String) headerMap.get("error"))) {
                batchId = createBatchRecords(fileName, batchPayload, excelData);
                if (fileName.endsWith(RuntimeConstants.XLS_EXTN) || fileName.endsWith(RuntimeConstants.XLSX_EXTN)) {
                    Workbook wb = null;
                    if (fileName.endsWith(RuntimeConstants.XLS_EXTN)) {
                        wb = new HSSFWorkbook();
                    } else {
                        wb = new XSSFWorkbook();
                    }
                    Sheet sheet = wb.createSheet(ExcelConstants.EXCEL_ERROR_SHEET);
                    Row row = sheet.createRow(0);
                    row.createCell(0).setCellValue(ExcelConstants.EXCEL_ERROR_SHEET);
                    row = sheet.createRow(1);
                    row.createCell(0).setCellValue(JsonDataUtil.convertToJsonString(batchRequest.getRequests().get(0).get("data")));
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    try {
                        wb.write(baos);
                    } catch (IOException e) {
                        BusinessException.newBusinessException(ExcelPluginExceptionCodes.EXPL000021, new Object[]{});
                    }

                    errorMsgPayLoad = new KeyValuePair<>(payLoad, baos.toByteArray());
                } else {
                    errorMsgPayLoad = new KeyValuePair<>(payLoad,
                            JsonDataUtil.convertToJsonString(batchRequest.getRequests().get(0).get("data")).getBytes());
                }
                updateErrorInPyldAndOutputFile(batchPayload, errorMsgPayLoad, fileName, batchId);
                transformedMessage = getBatchTransformedMsg(batchPayload);
                LOGGER.error(String.format("Writing into error folder :: %s, Reference batchId :: %s", absolutePath, batchId));
                removeFileLock(fileName, tenantCode);

            } else {
                JsonParseStatus parseStatusForPoolName = new JsonParseStatus();

                final KeyValuePair<String, TransactionCriteria> poolCriteriaDetail = getPoolName(tenantCode, batchRequest, excelData, parseStatusForPoolName);
                batchPayload.setTransactionCriteria(poolCriteriaDetail.getValue());
                // added this check to handle the pool not found in batch
                // execution
                if (StringUtils.isEmpty(poolCriteriaDetail.getKey())) {
                    if (parseStatusForPoolName != null && isNotEmpty(parseStatusForPoolName.getErrorCode())) {
                        if (parseStatusForPoolName != null && isNotEmpty(parseStatusForPoolName.getErrorCode())) {
                            if (fileName.endsWith(RuntimeConstants.XLS_EXTN) || fileName.endsWith(RuntimeConstants.XLSX_EXTN)) {
                                Workbook wb = null;
                                if (fileName.endsWith(RuntimeConstants.XLS_EXTN)) {
                                    wb = new HSSFWorkbook();
                                } else {
                                    wb = new XSSFWorkbook();
                                }
                                Sheet sheet = wb.createSheet(ExcelConstants.EXCEL_ERROR_SHEET);
                                Row row = sheet.createRow(0);
                                row.createCell(0).setCellValue(ExcelConstants.EXCEL_ERROR_SHEET);
                                row = sheet.createRow(1);
                                if (StringUtils.contains(parseStatusForPoolName.getErrorCode(), RuntimeConstants.RSE_EXCEPTION)) {
                                    parseStatusForPoolName.setErrorMessage(RuntimeConstants.GENERIC_ERROR_MESSAGE);
                                }
                                row.createCell(0).setCellValue(JsonDataUtil.convertToJsonString(parseStatusForPoolName));
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                try {
                                    wb.write(baos);
                                } catch (IOException e) {
                                    BusinessException.newBusinessException(ExcelPluginExceptionCodes.EXPL000021, new Object[]{});
                                }
                                errorMsgPayLoad = new KeyValuePair<>(payLoad, baos.toByteArray());
                            } else {
                                errorMsgPayLoad = new KeyValuePair<>(payLoad,
                                        JsonDataUtil.convertToJsonString(parseStatusForPoolName).getBytes());
                            }
                        }
                    }
                    batchId = createBatchRecords(fileName, batchPayload, excelData);
                    updateErrorInPyldAndOutputFile(batchPayload, errorMsgPayLoad, fileName, batchId);
                    transformedMessage = getBatchTransformedMsg(batchPayload);
                    LOGGER.error(
                            String.format("Writing into error folder :: %s, Reference batchId :: %s", absolutePath, batchId));
                    removeFileLock(fileName, tenantCode);
                } else {
                    String modelName = null, majorVersion = null, minorVersion = null, user = null;
                    boolean storeRLog = false;
                    List<Map<String, Object>> batchRequestList = batchRequest.getRequests();
                    if (excelData != null && excelData.getHeaderDetails() != null) {
                        modelName = String.valueOf(excelData.getHeaderDetails().get("modelName"));
                        majorVersion = String.valueOf(excelData.getHeaderDetails().get("majorVersion"));
                        minorVersion = String.valueOf(excelData.getHeaderDetails().get("minorVersion"));
                        Object storeRLogValue = excelData.getHeaderDetails().get("storeRLogs");
                        if (storeRLogValue instanceof Boolean) {
                            storeRLog = (boolean) storeRLogValue;
                        } else {
                            if (storeRLogValue instanceof String && StringUtils.equalsIgnoreCase(storeRLogValue.toString(), "true")) {
                                storeRLog = true;
                            }
                        }
                    } else if (batchRequestList != null) {
                        Map<String, Object> mp = batchRequestList.get(0);
                        Map<String, Object> headers = (Map<String, Object>) mp.get("header");
                        modelName = String.valueOf(headers.get("modelName"));
                        majorVersion = String.valueOf(headers.get("majorVersion"));
                        minorVersion = String.valueOf(headers.get("minorVersion"));
                        user = headers.get(ReadHeaderSheet.USER) == null ? null : String.valueOf(headers.get(ReadHeaderSheet.USER) != null ? String.valueOf(headers.get(ReadHeaderSheet.USER)) : null);
                    }

                    batchId = batchingDelegate.createBatch(fileName, TransactionStatus.QUEUED, Boolean.FALSE,
                            RuntimeConstants.INT_ZERO, user, modelName, majorVersion, minorVersion, null, null, storeRLog);
                    updatePoolBatchStatus(poolCriteriaDetail.getKey(), TransactionStatus.IN_PROGRESS.toString());
                    batchingDelegate.updateBatch(batchId, batchRequest.getRequests().size());
                    msgPayload = new KeyValuePair<>(payLoad, batchRequest.getRequests());
                    batchPayload.setBatchId(batchId);
                    batchPayload.setError(false);
                    batchPayload.setMsgPayload(msgPayload);
                    transformedMessage = getBatchTransformedMsg(batchPayload);
                    injectLRU(batchId, batchRequest.getRequests().size(), tenantCode, poolCriteriaDetail.getKey());
                    LOGGER.error(String.format("Writing into in-progress folder :: %s, Reference batchId :: %s", absolutePath,
                            batchId));
                }
            }
        }
        return transformedMessage;
    }

    private String createBatchRecords(String fileName, BatchTransformedPayload batchPayload, ExcelData excelData)
            throws SystemException, BusinessException {
        String modelName = null, majorVersion = null, minorVersion = null, user = null;
        boolean storeRLog = false;
        List<Map<String, Object>> batchRequestList = batchPayload.getBatchRequest() == null ? null
                : batchPayload.getBatchRequest().getRequests();
        if (excelData != null && excelData.getHeaderDetails() != null) {
            modelName = String.valueOf(excelData.getHeaderDetails().get("modelName"));
            majorVersion = String.valueOf(excelData.getHeaderDetails().get("majorVersion"));
            minorVersion = String.valueOf(excelData.getHeaderDetails().get("minorVersion"));
            Object storeRLogValue = excelData.getHeaderDetails().get("storeRLogs");
            if (storeRLogValue instanceof Boolean) {
                storeRLog = (boolean) storeRLogValue;
            } else {
                if (storeRLogValue instanceof String && StringUtils.equalsIgnoreCase(storeRLogValue.toString(), "true")) {
                    storeRLog = true;
                }
            }
        } else if (batchRequestList != null) {
            Map<String, Object> mp = batchRequestList.get(0);
            Map<String, Object> headers = (Map<String, Object>) mp.get("header");
            modelName = String.valueOf(headers.get("modelName"));
            majorVersion = String.valueOf(headers.get("majorVersion"));
            minorVersion = String.valueOf(headers.get("minorVersion"));
            user = headers.get(ReadHeaderSheet.USER) == null ? null : String.valueOf(headers.get(ReadHeaderSheet.USER) != null ? String.valueOf(headers.get(ReadHeaderSheet.USER)) : null);
        }
        String batchId = batchingDelegate.createBatch(fileName, TransactionStatus.ERROR, Boolean.FALSE, RuntimeConstants.INT_ZERO,
                user, modelName, majorVersion, minorVersion, null, null, storeRLog);
        batchingDelegate.updateBatch(batchId, RuntimeConstants.INT_ZERO, RuntimeConstants.INT_ZERO, RuntimeConstants.INT_ZERO,
                TransactionStatus.ERROR.getStatus());
        batchPayload.setBatchId(batchId);
        return batchId;
    }

    private void updateErrorInPyldAndOutputFile(BatchTransformedPayload batchPayload,
                                                KeyValuePair<Object, byte[]> errorMsgPayLoad, String fileName, String batchId) {
        batchPayload.setError(true);
        batchPayload.setErrorMsgPayLoad(errorMsgPayLoad);
        batchingDelegate.updateBatchOutputFile(batchId,
                FilenameUtils.getBaseName(fileName) + "_Error." + FilenameUtils.getExtension(fileName));
    }

    private Message<?> getBatchTransformedMsg(BatchTransformedPayload batchTransformedPayload) {
        Message<?> transformedMessage;
        if (batchTransformedPayload.isError()) {
            transformedMessage = MessageBuilder.withPayload(batchTransformedPayload.getErrorMsgPayLoad())
                    .copyHeaders(batchTransformedPayload.getMessage().getHeaders())
                    .setHeaderIfAbsent(FileHeaders.ORIGINAL_FILE, batchTransformedPayload.getAbsolutePath())
                    .setHeaderIfAbsent(FileHeaders.FILENAME, batchTransformedPayload.getFileName())
                    .setHeaderIfAbsent(RuntimeConstants.ERROR, batchTransformedPayload.isError())
                    .setHeaderIfAbsent(RuntimeConstants.TENANT_CODE, batchTransformedPayload.getTenantCode())
                    .setHeaderIfAbsent(RuntimeConstants.BATCH_ID, batchTransformedPayload.getBatchId())
                    .setHeaderIfAbsent(RuntimeConstants.SAN_PATH, batchTransformedPayload.getSanPath()).build();
        } else {
            transformedMessage = MessageBuilder.withPayload(batchTransformedPayload.getMsgPayload())
                    .copyHeaders(batchTransformedPayload.getMessage().getHeaders())
                    .setHeaderIfAbsent(FileHeaders.ORIGINAL_FILE, batchTransformedPayload.getAbsolutePath())
                    .setHeaderIfAbsent(FileHeaders.FILENAME, batchTransformedPayload.getFileName())
                    .setHeaderIfAbsent(RuntimeConstants.ERROR, batchTransformedPayload.isError())
                    .setHeaderIfAbsent(RuntimeConstants.TENANT_CODE, batchTransformedPayload.getTenantCode())
                    .setHeaderIfAbsent(RuntimeConstants.BATCH_ID, batchTransformedPayload.getBatchId())
                    .setHeaderIfAbsent(RuntimeConstants.SAN_PATH, batchTransformedPayload.getSanPath())
                    .setHeaderIfAbsent(RuntimeConstants.MODEL_IDENTIFIER, batchTransformedPayload.getTransactionCriteria() != null ? batchTransformedPayload.getTransactionCriteria().getModelIdentifier() : "")
                    .setHeaderIfAbsent(RuntimeConstants.BATCH_TRANSACTION_CRITERIA, batchTransformedPayload.getTransactionCriteria()).build();

        }
        return transformedMessage;
    }

    private BatchRequest getJsonList(String fileDataStr) throws BusinessException {
        BatchRequest batchRequest = null;
        try {
            if (StringUtils.isNotBlank(fileDataStr)) {
                batchRequest = JsonDataUtil.convertJson(fileDataStr, BatchRequest.class);
            }
        } catch (IOException exp) {
            LOGGER.error("Error while parsing Json ", exp);
            BusinessException.raiseBusinessException(RuntimeExceptionCode.RSE000510, new Object[]{});
        }
        return batchRequest;
    }

    private String getTenantCode(String fileAbsoulteName) {
        String tenantCode = null;
        String[] segments = null;
        String pattern = Pattern.quote(File.separator);
        if (StringUtils.isNotBlank(fileAbsoulteName)) {
            segments = fileAbsoulteName.split(pattern);
            if (segments != null && segments.length > 4) {
                tenantCode = segments[segments.length - 4];
            }
        }
        return tenantCode;
    }

    private void injectLRU(String batchId, int batchCnt, String tenantCode, final String poolName) throws SystemException {
        LOGGER.error(String.format("Injecting LRU for batchId :: %s", batchId));
        BatchLru batchLru = new BatchLru(batchId, batchCnt, cacheRegistry, tenantCode, systemParameterProvider, poolName);
        lruContainer.setBatchLRU(batchId, batchLru);
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

    public LruContainer getLruContainer() {
        return lruContainer;
    }

    public void setLruContainer(LruContainer lruContainer) {
        this.lruContainer = lruContainer;
    }

    public SystemParameterProvider getSystemParameterProvider() {
        return systemParameterProvider;
    }

    public void setSystemParameterProvider(SystemParameterProvider systemParameterProvider) {
        this.systemParameterProvider = systemParameterProvider;
    }

    /**
     * @param excelReader
     *            the excelReader to set
     */
    public void setExcelReader(ExcelReader excelReader) {
        this.excelReader = excelReader;
    }

    /**
     * @return the excelReader
     */
    public ExcelReader getExcelReader() {
        return excelReader;
    }

    public UmgFileProxy getUmgFileProxy() {
        return umgFileProxy;
    }

    public void setUmgFileProxy(UmgFileProxy umgFileProxy) {
        this.umgFileProxy = umgFileProxy;
    }

    private VersionExecInfo getExecutionLanguage(final BatchRequest batchRequest, final ExcelData excelData)
            throws BusinessException, SystemException {
        String versionName;
        int majorVersion;
        int minorVersion;
        if (excelData != null) {
            final Map<String, Object> headerDetails = excelData.getHeaderDetails();
            versionName = headerDetails.get("modelName").toString();
            majorVersion = valueOf(headerDetails.get("majorVersion").toString());
            minorVersion = valueOf(headerDetails.get("minorVersion").toString());
        } else {
            final Map<Object, Object> headerDetails = (Map<Object, Object>) batchRequest.getRequests().get(0).get("header");
            versionName = headerDetails.get("modelName").toString();
            majorVersion = valueOf(headerDetails.get("majorVersion").toString());
            minorVersion = valueOf(headerDetails.get("minorVersion").toString());
        }

        final VersionExecInfo versionExecInfo = deploymentBO.getExecutionLanguageDeatils(versionName, majorVersion,
                minorVersion);
        if (versionExecInfo == null) {
            SystemException.newSystemException(RuntimeExceptionCode.RSE000832,
                    new Object[]{versionName, majorVersion, minorVersion});
        }
        return versionExecInfo;
    }

    private Map<String, String> getExecutionEnvironment(final BatchRequest batchRequest, final ExcelData excelData)
            throws SystemException, BusinessException {
        String versionName;
        int majorVersion;
        int minorVersion;
        if (excelData != null) {
            final Map<String, Object> headerDetails = excelData.getHeaderDetails();
            versionName = headerDetails.get("modelName").toString();
            majorVersion = valueOf(headerDetails.get("majorVersion").toString());
            minorVersion = valueOf(headerDetails.get("minorVersion").toString());
        } else {
            final Map<Object, Object> headerDetails = (Map<Object, Object>) batchRequest.getRequests().get(0).get("header");
            versionName = headerDetails.get("modelName").toString();
            majorVersion = valueOf(headerDetails.get("majorVersion").toString());
            minorVersion = valueOf(headerDetails.get("minorVersion").toString());
        }


        Map<String, String> executionVsionAndChkSum = deploymentBO.getExecutionEnvtVersion(versionName, majorVersion, minorVersion);
        String executionVvrsion = executionVsionAndChkSum.get(EnvironmentVariables.EXE_ENV);
        if (StringUtils.isBlank(executionVvrsion)) {
            SystemException.newSystemException(RuntimeExceptionCode.RSE000833,
                    new Object[]{versionName, majorVersion, minorVersion});
        }

        return executionVsionAndChkSum;

    }

    private void updatePoolBatchStatus(final String poolName, final String batchPoolStatus)
            throws BusinessException, SystemException {
        poolObjectsLoader.updatePoolStatus(poolName, batchPoolStatus);
    }

    private KeyValuePair<String, TransactionCriteria> getPoolName(final String tenantCode, final BatchRequest batchRequest, final ExcelData excelData,
                                                                  JsonParseStatus parseStatusForPoolName) throws BusinessException, SystemException {
        final TransactionCriteria poolCriteria = new TransactionCriteria();
        String poolName = StringUtils.EMPTY;
        try {
            final VersionExecInfo versionExecInfo = getExecutionLanguage(batchRequest, excelData);

            final String environment = versionExecInfo.getExecLanguage();
            final String environmentVersion = versionExecInfo.getExecLangVer();

            final Map<String, String> executionVsionAndChkSum = getExecutionEnvironment(batchRequest, excelData);

            poolCriteria.setTenantCode(tenantCode);
            poolCriteria.setExecutionLanguage(environment);
            poolCriteria.setExecutionLanguageVersion(environmentVersion);
            poolCriteria.setTransactionRequestChannel(MessageVariables.ChannelType.HTTP.getChannel());
            poolCriteria.setExecutionEnvironment(executionVsionAndChkSum.get(EnvironmentVariables.EXE_ENV));
            poolCriteria.setModelIdentifier(executionVsionAndChkSum.get(EnvironmentVariables.MODEL_CHECKSUM));
            Map<String, Object> headerMap = (Map<String, Object>) batchRequest.getRequests().get(0).get("header");

            int majorVersion = (Integer) headerMap.get("majorVersion");
            int minorVersion = (Integer) headerMap.get("minorVersion");

            String version = Integer.toString(majorVersion) + RuntimeConstants.CHAR_DOT
                    + Integer.toString(minorVersion);

            poolCriteria.setModelName(headerMap.get("modelName").toString());
            poolCriteria.setModelVersion(version);
            poolCriteria.setTransactionRequestType(PROD.toString());
            poolCriteria.setTransactionRequestMode(RequestMode.BATCH.getMode());
            Pool pool = poolObjectsLoader.getPoolByCriteria(poolCriteria);
            poolName = pool.getPoolName();
        } catch (SystemException ex) {
            LOGGER.error("Error occured while getting pool name : BatchToListTranformer::getPoolName ", ex);
            if (StringUtils.equals(ex.getCode(), "MSE0000203")) {
                parseStatusForPoolName.setErrorCode(RuntimeExceptionCode.RSE000813);
            } else {
                parseStatusForPoolName.setErrorCode(ex.getCode());
            }

            parseStatusForPoolName.setErrorMessage(ex.getLocalizedMessage());
        }

        return new KeyValuePair<String, TransactionCriteria>(poolName, poolCriteria);
    }

    public DeploymentBO getDeploymentBO() {
        return deploymentBO;
    }

    public void setDeploymentBO(DeploymentBO deploymentBO) {
        this.deploymentBO = deploymentBO;
    }

    public PoolObjectsLoader getPoolObjectsLoader() {
        return poolObjectsLoader;
    }

    public void setPoolObjectsLoader(PoolObjectsLoader poolObjectsLoader) {
        this.poolObjectsLoader = poolObjectsLoader;
    }
}
