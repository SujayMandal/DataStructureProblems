/*
 * HttpTransportHandler.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics 
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.endpoint.http;

import static com.ca.umg.rt.util.MessageVariables.NOTIFICATION_RSE_FLAG;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.util.UrlPathHelper;

import com.ca.framework.core.batch.TransactionStatus;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.rmodel.dao.RModelDAO;
import com.ca.framework.core.rmodel.info.VersionExecInfo;
import com.ca.framework.core.task.AbstractCallableTask;
import com.ca.framework.core.task.executor.CustomTaskExecutor;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.framework.core.util.TransactionDocumentPayload;
import com.ca.framework.core.util.TransactionIOPayload;
import com.ca.framework.core.util.UmgFileProxy;
import com.ca.framework.object.size.util.ObjectSizeCalculator;
import com.ca.pool.PoolObjectsLoader;
import com.ca.pool.TransactionMode;
import com.ca.umg.notification.model.NotificationHeaderEnum;
import com.ca.umg.notification.notify.NotificationTriggerDelegate;
import com.ca.umg.rt.batching.dao.BatchTransactionDAO;
import com.ca.umg.rt.batching.delegate.BatchingDelegate;
import com.ca.umg.rt.batching.entity.BatchRuntimeTransactionMapping;
import com.ca.umg.rt.batching.entity.BatchTransaction;
import com.ca.umg.rt.core.deployment.constants.RuntimeConstants;
import com.ca.umg.rt.core.flow.dao.MongoTransactionLogDAO;
import com.ca.umg.rt.core.flow.dao.TransactionLogDAO;
import com.ca.umg.rt.core.flow.entity.TransactionLog;
import com.ca.umg.rt.custom.serializers.DoubleSerializerModule;
import com.ca.umg.rt.exception.codes.RuntimeExceptionCode;
import com.ca.umg.rt.flows.container.ContainerManager;
import com.ca.umg.rt.transformer.MoveFileAdapter;
import com.ca.umg.rt.util.MessageVariables;
import com.ca.umg.rt.util.RequestValidator;
import com.ca.umg.rt.util.TransactionPayload;
import com.ca.umg.rt.web.filter.RequestMarkerFilter;
import com.ca.umg.rt.web.request.ResettableStreamHttpServletRequest;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

/**
 * Simple HTTP front end for UMG runtime integration flow. It delegates the http request processing to executer.
 *
 * @author devasiaa
 **/
@SuppressWarnings("PMD")
@Named
public class HttpTransportHandler implements HttpRequestHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpTransportHandler.class);
    @Inject
    @Qualifier("flowContainerManager")
    private ContainerManager containerManager;
    @Inject

    private TransactionLogDAO transactionLogDAO;
    
    @Inject
    private RModelDAO rModelDAO;

    @Inject
    private MongoTransactionLogDAO mongoTransactionLogDAO;
    
    @Inject
    private BatchTransactionDAO batchTransactionDAO;    

    @Inject
    private NotificationTriggerDelegate notificationTriggerDelegate;

    private String notificationErrorCode;

    private String notificationErrorMsg;

    private static final String CONTENT_TYPE = "application/json";
    private static final String NULL = "null";
    private static final String ERROR = "Error";
    private static final String TEST = "test";

    @Inject
    private CustomTaskExecutor customTaskExecutor;

    @Inject
    private UmgFileProxy umgFileProxy;

    @Inject
    private MoveFileAdapter moveFileAdapter;

    @Inject
    private BatchingDelegate batchingDelegate;

    @Inject
    private PoolObjectsLoader poolObjectsLoader;
    
    /**
     * DOCUMENT ME!
     *
     * @param request
     *            DOCUMENT ME!
     * @param response
     *            DOCUMENT ME!
     *
     * @throws ServletException
     *             DOCUMENT ME!
     * @throws IOException
     *             DOCUMENT ME!
     **/
    @Override
    @Timed
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestUri = new UrlPathHelper().getRequestUri(request);
        if (!validateURI(requestUri, response)) {
            return;
        }
        long start = System.currentTimeMillis();
        LOGGER.error("Received request for execution :: " + System.currentTimeMillis());
        ResettableStreamHttpServletRequest wrappedRequest = null;
        ModelRequest modelRequest = null;// NOPMD
        boolean error = false;
        Map<String, Object> tenantResponse = null; // NOPMD
        KeyValuePair<String, byte[]> bytesOfFile = new KeyValuePair<>();
        final Map<String, Boolean> notificationMap = new HashMap<String, Boolean>();
        notificationMap.put(NOTIFICATION_RSE_FLAG, Boolean.FALSE);
        boolean changeAddOnValidation=true;
        try {
            String sanBase = umgFileProxy.getSanPath(SystemConstants.SAN_BASE);
            request.setAttribute(MessageVariables.SAN_PATH, sanBase);
            // BufferedReader bufferedReader = new BufferedReader(new FileReader("D:\\UMG\\Sample\\TenantInpurSample.txt"));
            if (!(request instanceof ResettableStreamHttpServletRequest)) {
                wrappedRequest = new ResettableStreamHttpServletRequest((HttpServletRequest) request);
            } else {
                wrappedRequest = (ResettableStreamHttpServletRequest) request;
            }
            // long validationStartTime = System.currentTimeMillis();
            modelRequest = validateRequest(wrappedRequest, bytesOfFile);
            ObjectSizeCalculator.getObjectDeepSize(modelRequest, modelRequest.getHeader().getTransactionId(), "Request received in runtime");
            // LOGGER.error("Validation time: " + (System.currentTimeMillis() - validationStartTime));
            long startTime = System.currentTimeMillis();
            LOGGER.debug("Request content length: " + request.getContentLength());
            DateTime dt = new DateTime();
        	DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MMM-dd-HH-mm-ss-SSS");
        	String timeStamp = fmt.print(dt);
            String batchId = createBatchTranForBulk(modelRequest, timeStamp, modelRequest.getHeader().getTransactionId());
            if(modelRequest.getHeader().getBatchId()!= null && batchId == null){
            	BatchTransaction batchTransaction = batchingDelegate.getBatch(modelRequest.getHeader().getBatchId());
            	if (batchTransaction != null && StringUtils.equalsIgnoreCase(batchTransaction.getStatus(),
            			TransactionStatus.IN_EXECUTION.getStatus())) {
            		final String transactionId = MDC.get(RequestMarkerFilter.TRANSACTION_ID);
            		BatchRuntimeTransactionMapping runtimeTransactionMapping = new BatchRuntimeTransactionMapping();
            		runtimeTransactionMapping.setBatchId(modelRequest.getHeader().getBatchId());
            		runtimeTransactionMapping.setTransactionId(transactionId);
            		runtimeTransactionMapping.setStatus("IN-PROGRESS");
            		batchingDelegate.addBatchTransactionMapping(runtimeTransactionMapping, Boolean.FALSE);
            	} 
            }
            executeRequest(response, requestUri, wrappedRequest, modelRequest, bytesOfFile, batchId, timeStamp);
            LOGGER.debug("Response content length: " + response.getHeader("Content-Length"));
            LOGGER.error("Request execution time: " + (System.currentTimeMillis() - startTime));
        } catch (SystemException | BusinessException e) {
            LOGGER.error("Error while processing request {}", requestUri, e);
            if(e.getCode().equalsIgnoreCase(RuntimeExceptionCode.RVE000213))
            {
            	changeAddOnValidation=false;
            }
            tenantResponse = createTenantResponse(e.getCode(), e.getLocalizedMessage(), wrappedRequest, notificationMap);
            error = true;
        } catch (Exception ex) {// NOPMD
            LOGGER.error("Error while processing request {}", requestUri, ex);
            SystemException systemException = new SystemException(RuntimeExceptionCode.RSE000800,
                    new Object[] { ex.getMessage() }, ex);
            tenantResponse = createTenantResponse(systemException.getCode(), systemException.getLocalizedMessage(),
                    wrappedRequest, notificationMap);
            error = true;
        }
        if (error) {// NOPMD
            if (notificationMap.get(NOTIFICATION_RSE_FLAG)) {
            	if(tenantResponse != null){
                notificationErrorCode = (String) ((Map<String, Object>) tenantResponse.get(MessageVariables.HEADER))
                        .get(MessageVariables.ERROR_CODE);
                notificationErrorMsg = (String) ((Map<String, Object>) tenantResponse.get(MessageVariables.HEADER))
                        .get(MessageVariables.ERROR_MESSAGE);
            	}
                final Map<String, String> notificationInfoMap = getInfoMap(tenantResponse);
            	
                try {
                    if (notificationErrorCode != null && notificationErrorCode.startsWith("RSE")) {
                        notificationTriggerDelegate.modelAndSystemFailureMail(notificationInfoMap, notificationErrorCode,
                                notificationErrorMsg, true, poolObjectsLoader.getActiveAndInactiveModeletClients());
                    }
                } catch (SystemException | BusinessException e) {
                    LOGGER.error(e.getLocalizedMessage());
                }
            }

            sendResponse(response, start, wrappedRequest, modelRequest, tenantResponse,changeAddOnValidation);
        }
        LOGGER.error("Total Execution time: " + (System.currentTimeMillis() - start));
        LOGGER.error("Finished request processing response is {}", response);
    }

    private boolean validateURI(String requestUri, HttpServletResponse response) throws IOException {
        boolean valid = requestUri.endsWith("/runtime");
        if (!valid) {
            response.setStatus(404);
            response.getWriter().write("<html>\n" + "<body>\n" + "<h1>HTTP error 404</h1>\n" + "</body>\n" + "</html>");
            response.getWriter().flush();
        }
        return valid;
    }

    public void executeRequest(HttpServletResponse response, String requestUri, ResettableStreamHttpServletRequest wrappedRequest,
            ModelRequest modelRequest, KeyValuePair<String, byte[]> bytesOfFile, String batchId, String timeStamp)
            throws SystemException, BusinessException, ServletException, IOException {
        long resetStart = System.currentTimeMillis();
        wrappedRequest.resetInputStream();
        LOGGER.debug("Reset Stream: " + (System.currentTimeMillis() - resetStart));
        HttpRequestHandler dispatcherHandler = containerManager.getHandler(wrappedRequest, modelRequest);
        if (dispatcherHandler != null) {
            Header header = modelRequest.getHeader();
            if (StringUtils.isEmpty(header.getFileName())) {
            	if(TransactionMode.BULK.getMode().equals(header.getTransactionMode())){
            		  wrappedRequest.resetInputStream(batchId, timeStamp);            		
            	}else{
                    wrappedRequest.resetInputStream();           		
            	}
       
            } else {
	            VersionExecInfo versionExecInfo = 	rModelDAO.getEnvironmentDetails(RequestContext.getRequestContext().getTenantCode(),
	            		header.getModelName(), String.valueOf(header.getMajorVersion()), String.valueOf(header.getMinorVersion()));            
	            batchTransactionDAO.updateEnvAndModelEnvs(batchId, versionExecInfo.getExecEnv(), versionExecInfo.getExecLanguage()+RuntimeConstants.CHAR_HYPHEN+versionExecInfo.getExecLangVer());
	            	
	            wrappedRequest.resetInputStream(bytesOfFile.getValue(), header.getFileName(), batchId);
            }
            dispatcherHandler.handleRequest(wrappedRequest, response);
        } else {
            LOGGER.error("No dispatcher handler found for reqeust with uri {}", requestUri);
            throw new SystemException(RuntimeExceptionCode.RVE000210, new Object[] {});
        }
    }

    public ModelRequest validateRequest(ResettableStreamHttpServletRequest wrappedRequest,
            KeyValuePair<String, byte[]> bytesOfFile) throws SystemException, BusinessException, IOException {
        long resetStart = System.currentTimeMillis();
        wrappedRequest.resetInputStream();
        LOGGER.debug("Reset Stream: " + (System.currentTimeMillis() - resetStart));
        String body = null;
        try {
            long start = System.currentTimeMillis();
            body = IOUtils.toString(wrappedRequest.getReader());
            LOGGER.debug("Input stream read time: " + (System.currentTimeMillis() - start));
        } catch (IOException ex) {
            throw new SystemException(RuntimeExceptionCode.RVE000201, new Object[] { ex.getMessage() }, ex);
        }
        long validStart = System.currentTimeMillis();
        String sanBase = umgFileProxy.getSanPath(SystemConstants.SAN_BASE);
        ModelRequest modelRequest = RequestValidator.validateRequest(body, sanBase, bytesOfFile);
        LOGGER.debug("Validation time: " + (System.currentTimeMillis() - validStart));
        return modelRequest;
    }

    public void sendResponse(HttpServletResponse response, final long start,
            final ResettableStreamHttpServletRequest wrappedRequest, final ModelRequest modelRequest,
            final Map<String, Object> tenantResponse,final boolean changeAddOnValidation) throws JsonProcessingException, IOException {
        LOGGER.error("Started creating error response for tenantResponse :"+tenantResponse);
        response.setStatus(HttpStatus.OK.value());
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding("UTF-8");

        String responseString = mapper.writeValueAsString(tenantResponse);
        final long end = System.currentTimeMillis();
        final String transactionId = MDC.get(RequestMarkerFilter.TRANSACTION_ID);
        String tenantCode = RequestContext.getRequestContext().getTenantCode();       
        LOGGER.error("tenantCode is :"+tenantCode);
        AbstractCallableTask<Void> callable = new AbstractCallableTask<Void>(tenantCode) {
            @Override
            public Void call() throws SystemException {
                setRequestContext();
                try {
                    LOGGER.error("Calling async call to save transaction to Mysql and mongo for umgTransactionId :"+transactionId);
                    logTransactionToMongoAndSql(modelRequest, wrappedRequest, start, end, tenantResponse, transactionId,changeAddOnValidation);
                } catch (Exception e) {// NOPMD
                    LOGGER.error("Error occured while saving data to Mongo and MySql", e);
                }
                return null;
            }
        };
        
        customTaskExecutor.submit(callable);
        // logTransactionToMongoAndSql(modelRequest, wrappedRequest, start, end, tenantResponse, transactionId);

        // Reset the error message to generic error message
        Map<String, Object> tenantResponseHdr = (Map<String, Object>) tenantResponse.get(MessageVariables.HEADER);
        LOGGER.error("tenantResponseHdr is :"+tenantResponseHdr);
        tenantResponseHdr.put(MessageVariables.TRAN_MODE, tenantResponseHdr.get(MessageVariables.FILE_NAME) == null
                ? (String)tenantResponseHdr.get(MessageVariables.TRAN_MODE) : MessageVariables.TRAN_BULK);
        if (!(Boolean) tenantResponseHdr.get(MessageVariables.SUCCESS)
                && (!(tenantResponseHdr.get(MessageVariables.ERROR_CODE) != null
                        && ((String) tenantResponseHdr.get(MessageVariables.ERROR_CODE))
                                .startsWith(RuntimeConstants.RVE_EXCEPTION)))) {
            tenantResponseHdr.put(MessageVariables.ERROR_MESSAGE, RuntimeConstants.GENERIC_ERROR_MESSAGE);
        }else{
        	LOGGER.error("It is RVE Exception.Error Code is:"+(String) tenantResponseHdr.get(MessageVariables.ERROR_CODE));
        }

        try {
            LOGGER.error("initiating to move the files for :" + transactionId);
            if(modelRequest!=null) {
            moveFilesForBulk(tenantResponse,modelRequest.getHeader().getChannel());
            }
            else {
            	   wrappedRequest.resetInputStream();
            	 LinkedHashMap<String, Object> obj = mapper.readValue(IOUtils.toString(wrappedRequest.getReader()), new TypeReference<LinkedHashMap<String, Object>>() {
                 });
            	 Map<String, Object> header = (Map<String, Object>) obj.get(MessageVariables.HEADER);
            	 String fileName = (String) header.get(MessageVariables.FILE_NAME);
            	 Boolean isVersionCreationTest = Boolean.FALSE;
                 if (header.get(MessageVariables.VERSION_CREATION_TEST) != null) {
                     isVersionCreationTest = (Boolean) header.get(MessageVariables.VERSION_CREATION_TEST);
                 }
            	 String channel = isVersionCreationTest ? MessageVariables.ChannelType.HTTP.getChannel()
                         : StringUtils.isBlank(fileName) ? MessageVariables.ChannelType.HTTP.getChannel()
                                 : MessageVariables.ChannelType.FILE.getChannel();
                         moveFilesForBulk(tenantResponse,channel);
            }
            LOGGER.error("finished moving the files for :" + transactionId);
        } catch (SystemException e) {
            LOGGER.error("Error occured while moving files from input to archive or writing the error response" + e.getCode()
                    + e.getLocalizedMessage());
        }

        responseString = mapper.writeValueAsString(tenantResponse);
        response.getWriter().write(responseString);
        response.getWriter().flush();
    }

    private void moveFilesForBulk(Map<String, Object> tenantResponse,String channel) throws SystemException {
        Map<String, Object> tenantResponseHdr = (Map<String, Object>) tenantResponse.get("header");
        if (tenantResponseHdr.get(MessageVariables.FILE_NAME) != null) {
            LOGGER.error("initiating to move the files for filname:" + tenantResponseHdr.get(MessageVariables.FILE_NAME));
            String sanBase = umgFileProxy.getSanPath(SystemConstants.SAN_BASE);
            moveFileAdapter.moveInptToArchvAndWriteErrRespns(sanBase, tenantResponse,channel);
            LOGGER.error("moved the file :" + tenantResponseHdr.get(MessageVariables.FILE_NAME) + " successfully");
        }
    }

    private void logTransactionToMongoAndSql(ModelRequest modelRequest, ResettableStreamHttpServletRequest wrappedRequest,
            long start, long end, Map<String, Object> tenantResponse, String transactionId,boolean changeAddOnValidation)
            throws JsonProcessingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        String responseString = mapper.writeValueAsString(tenantResponse);
        Boolean mySqlSuccess = Boolean.FALSE;
        Long defaultRunAsOfDate = DateTime.now().getMillis();
        try {
            logTransaction(modelRequest, wrappedRequest, start, end, tenantResponse, transactionId, defaultRunAsOfDate);
            logErrBatchTranForBulk(modelRequest, tenantResponse, transactionId);
            mySqlSuccess = Boolean.TRUE;
        } catch (Exception ex) {// NOPMD
            LOGGER.error("Error while logging transaction to mysql handleRequest::logTransactionToMongoAndMySql", ex);
        }

        if (mySqlSuccess) {
            try {
                logTransactionToMongo(modelRequest, responseString, wrappedRequest, start, end, tenantResponse, transactionId,
                        defaultRunAsOfDate,changeAddOnValidation);
            } catch (DataAccessResourceFailureException dex) {
                LOGGER.error(
                        "Error in saving transaction to mongo handleRequest::logTransactionToMongoAndMySql - mongo db is down",
                        dex);
                transactionLogDAO.remove(transactionId);
                Map<String, Object> tenantResponseHdr = (Map<String, Object>) tenantResponse.get(MessageVariables.HEADER);
                tenantResponseHdr.remove(MessageVariables.UMG_TRANSACTION_ID);
                tenantResponseHdr.put(MessageVariables.SUCCESS, Boolean.FALSE);
                tenantResponseHdr.put(MessageVariables.ERROR_CODE, "RSE000905");
                tenantResponseHdr.put(MessageVariables.ERROR_MESSAGE, "System Exception. Please contact administrator.");
            } catch (Exception ex) {// NOPMD
                LOGGER.error("Error in saving transaction to mongo handleRequest::logTransactionToMongoAndMySql", ex);
                transactionLogDAO.remove(transactionId);
            }
        }
    }

    private String createBatchTranForBulk(ModelRequest modelRequest, String timeStamp, String transactionId) throws SystemException, BusinessException {
        String batchId = null;
        if (modelRequest != null) {
            String fileName = modelRequest.getHeader().getFileName();
            if (StringUtils.isNotEmpty(fileName) || StringUtils.equals(modelRequest.getHeader().getTransactionMode(),TransactionMode.BULK.getMode()))
            		{
                BatchTransaction batchTransaction = batchingDelegate.getBatchForFileName(fileName);
                if (batchTransaction != null) {
                    batchId = batchTransaction.getId();
                    Header headers = modelRequest.getHeader();
                    headers.setBatchId(batchId);
                    modelRequest.setHeader(headers);
                } else {
                    int test = RuntimeConstants.INT_ZERO;
                    if (modelRequest.getHeader() != null && StringUtils.isNotBlank(modelRequest.getHeader().getTransactionType())
                            && modelRequest.getHeader().getTransactionType().equalsIgnoreCase(TEST)) {
                        test = RuntimeConstants.INT_ONE;
                    }
                    String user = null, modelName = null, majorVersion = null, minorVersion = null;
                    boolean storeRLogs = false;
                    if(modelRequest != null && modelRequest.getHeader() != null){
                    	user = modelRequest.getHeader().getUser();
                    	modelName = modelRequest.getHeader().getModelName();
                    	majorVersion = String.valueOf(modelRequest.getHeader().getMajorVersion());
                    	minorVersion = modelRequest.getHeader().getMinorVersion() != null ?
                    	        String.valueOf(modelRequest.getHeader().getMinorVersion()) : null;
                    	storeRLogs =modelRequest.getHeader().isStoreRLogs();
                    }

                    batchId = batchingDelegate.createBatch(fileName, TransactionStatus.QUEUED, Boolean.TRUE, test, user, modelName,
                            majorVersion, minorVersion, timeStamp, transactionId,storeRLogs);
                }
            }
        }
        return batchId;
    }

    private void logErrBatchTranForBulk(ModelRequest modelRequest, Map<String, Object> tenantResponse, String umgTransactionId)
            throws SystemException, BusinessException {
        LOGGER.error("Started creating object for writing transaction to batch for umgTransactionId :"+umgTransactionId);
        int test = RuntimeConstants.INT_ZERO;
        if (modelRequest != null && modelRequest.getHeader() != null
                && StringUtils.isNotBlank(modelRequest.getHeader().getTransactionType())
                && modelRequest.getHeader().getTransactionType().equalsIgnoreCase(TEST)) {
            test = RuntimeConstants.INT_ONE;
        }
        boolean storeRLogs = false;
        if (modelRequest != null) {
        	String user = null, fileName = null, modelName = null, majorVersion = null, minorVersion = null;
        	
        	if(modelRequest.getHeader() != null){
	            fileName = modelRequest.getHeader().getFileName();
	            user = modelRequest.getHeader().getUser();
	            modelName = String.valueOf(modelRequest.getHeader().getModelName()) ;
                majorVersion = String.valueOf(modelRequest.getHeader().getMajorVersion());
                storeRLogs =modelRequest.getHeader().isStoreRLogs();
                if (modelRequest.getHeader().getMinorVersion() != null) {
                    minorVersion = String.valueOf(modelRequest.getHeader().getMinorVersion());
	            }

        	}
            createErrBatchEntryForBulk(fileName, umgTransactionId, test, user, modelName, majorVersion, minorVersion,storeRLogs);
        } else {
            Map<String, Object> errorResponseData = (Map<String, Object>) tenantResponse.get(MessageVariables.HEADER);
            String fileName = (String) errorResponseData.get(MessageVariables.FILE_NAME);
            String user = (String) errorResponseData.get(MessageVariables.USER);
            String modelName = (String) errorResponseData.get(MessageVariables.MODEL_NAME);
            String majorVersion = String.valueOf(errorResponseData.get(MessageVariables.MAJOR_VERSION));
            String minorVersion = null;
            if (errorResponseData.get(MessageVariables.MINOR_VERSION) != null) {
                minorVersion = String.valueOf(errorResponseData.get(MessageVariables.MINOR_VERSION));
            }
            createErrBatchEntryForBulk(fileName, umgTransactionId, test, user, modelName, majorVersion, minorVersion,storeRLogs);
        }
    }

    private void createErrBatchEntryForBulk(String fileName, String umgTransactionId, final int test, String user, String modelName, String majorVersion, String minorVersion,boolean storeRLogs)
            throws SystemException, BusinessException {
        if (StringUtils.isNotEmpty(fileName)) {
            BatchTransaction batchTransaction = batchingDelegate.getBatchForFileName(fileName);
            if (batchTransaction != null) {
                LOGGER.error("Writing transaction to batch for umgTransactionId :"+umgTransactionId);
                batchingDelegate.updateBatch(batchTransaction.getId(), 0, 0, 0, TransactionStatus.ERROR.getStatus());
                String outputFileName = getOutputErrorFileName(fileName);
                batchingDelegate.updateBatchOutputFile(batchTransaction.getId(), outputFileName);
                createEntryInBatchTranMapping(batchTransaction.getId(), umgTransactionId);
            } else {
                LOGGER.error("Writing transaction to batch for umgTransactionId :"+umgTransactionId);
                String batchId = batchingDelegate.createBatch(fileName, TransactionStatus.ERROR, Boolean.TRUE, test, user, modelName, majorVersion, minorVersion, null, null,storeRLogs);
                batchingDelegate.updateBatch(batchId, 0, 0, 0, TransactionStatus.ERROR.getStatus());
                String outputFileName = getOutputErrorFileName(fileName);
                batchingDelegate.updateBatchOutputFile(batchId, outputFileName);
                createEntryInBatchTranMapping(batchId, umgTransactionId);
            }
        }
    }

    private void createEntryInBatchTranMapping(String batchId, String umgTransactionId)
            throws SystemException, BusinessException {
        LOGGER.error("Writing transaction to batch tran mapping for umgTransactionId :"+umgTransactionId);
        BatchRuntimeTransactionMapping runtimeTransactionMapping = new BatchRuntimeTransactionMapping();
        runtimeTransactionMapping.setBatchId(batchId);
        runtimeTransactionMapping.setTransactionId(umgTransactionId);
        runtimeTransactionMapping.setStatus(MessageVariables.FAILURE);
        runtimeTransactionMapping.setError("No Error!");
        batchingDelegate.addBatchTransactionMapping(runtimeTransactionMapping, Boolean.TRUE);
    }

    private String getOutputErrorFileName(String fileName) {
        String fileNameWithoutExtn = StringUtils.substringBeforeLast(fileName, FrameworkConstant.DOT);
        StringBuffer stringBuffer = new StringBuffer(fileNameWithoutExtn);
        stringBuffer.append(FrameworkConstant.HYPHEN).append(ERROR).append(FrameworkConstant.DOT)
                .append(StringUtils.substringAfterLast(fileName, FrameworkConstant.DOT));
        return stringBuffer.toString();
    }

    private void logTransaction(ModelRequest modelRequest, ResettableStreamHttpServletRequest wrappedRequest, long start,
            long end, Map<String, Object> tenantResponse, String transactionId, Long defaultRunAsofDate) throws IOException {
        LOGGER.error("Started creating object for writing transaction to Mysql for umgTransactionId :"+transactionId);
        String fileName = null;
        TransactionLog transactionLog = new TransactionLog();
        transactionLog.setId(transactionId);
        if (modelRequest != null) {
            transactionLog.setMajorVersion(
                    modelRequest.getHeader().getMajorVersion() != null ? modelRequest.getHeader().getMajorVersion() : 0);
            transactionLog.setMinorVersion(
                    modelRequest.getHeader().getMinorVersion() != null ? modelRequest.getHeader().getMinorVersion() : 0);
            transactionLog.setModelName(
                    modelRequest.getHeader().getModelName() != null ? modelRequest.getHeader().getModelName() : NULL);
            transactionLog.setRunAsOfDate(modelRequest.getHeader().getDate() != null
                    ? modelRequest.getHeader().getDate().getMillis() : defaultRunAsofDate);
            transactionLog.setTransactionId(
                    modelRequest.getHeader().getTransactionId() != null ? modelRequest.getHeader().getTransactionId() : NULL);
            // added to fix UMG-4500 Additional variables in Transaction header
            transactionLog.setCreatedBy(modelRequest.getHeader().getUser() != null ? modelRequest.getHeader().getUser()
                    : RequestContext.getRequestContext().getTenantCode());
            if (StringUtils.isNotBlank(modelRequest.getHeader().getTransactionType())) { // UMG-4611 audhyabh
                String tranType = modelRequest.getHeader().getTransactionType().trim();
                transactionLog.setIsTest(StringUtils.equalsIgnoreCase(tranType, TEST) ? 1 : 0);
            } else {
                transactionLog.setIsTest(0);
            }
            fileName = modelRequest.getHeader().getFileName();
        } else {
            Map errorResponseData = (Map) tenantResponse.get(MessageVariables.HEADER);
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
            transactionLog.setRunAsOfDate(defaultRunAsofDate);
            // added to fix UMG-4500 Additional variables in Transaction header
            transactionLog.setCreatedBy(RequestContext.getRequestContext().getTenantCode());
            transactionLog.setIsTest(0);
            fileName = (String) errorResponseData.get(MessageVariables.FILE_NAME);
        }

        if (StringUtils.isNotEmpty(fileName)) {
            transactionLog.setTransactionMode(MessageVariables.TRAN_BULK);
        } else {
            transactionLog.setTransactionMode(MessageVariables.TRAN_ONLINE);
        }

        transactionLog.setLibraryName(NULL);
        transactionLog.setRuntimeStart(start);
        transactionLog.setRuntimeEnd(end);     
        wrappedRequest.resetInputStreamForErrorResponse();
        // setting tenant i/p & o/p and model i/p & o/p to null as this is persisted in mongo
        // in the below method logTransactionToMongo
        transactionLog.setTenantInput(RuntimeConstants.STR_EMPTY);
        transactionLog.setTenantOutput(RuntimeConstants.STR_EMPTY);
        transactionLog.setModelInput(RuntimeConstants.STR_EMPTY);
        transactionLog.setModelOutput(RuntimeConstants.STR_EMPTY);
        transactionLog.setStatus(TransactionStatus.ERROR.getStatus());
        transactionLog.setErrorCode(getErrorCode(tenantResponse));
        if (getErrorDescription(tenantResponse) != null) {
            transactionLog.setErrorDescription(getErrorDescription(tenantResponse).getBytes());
        } else {
            transactionLog.setErrorDescription(null);
        }
        LOGGER.error("Calling create transaction to Mysql for umgTransactionId :"+transactionId);
        transactionLogDAO.insertTransactionLog(transactionLog);
    }

    private void logTransactionToMongo(ModelRequest modelRequest, String responseString,
            ResettableStreamHttpServletRequest wrappedRequest, long start, long end, Map<String, Object> tenantResponse,
            String transactionId, Long defaultRunAsofDate,boolean changeAddOnValidation) throws JsonProcessingException, IOException {
        LOGGER.error("Started creating object for writing transaction to mongo for umgTransactionId :"+transactionId);
        String fileName = null;
        String addonValidation = "addonValidation";
        Map<String, Object> tenantInput = null;
        Map<String, Object> tenantOutput = null;
        String body = null;
        ObjectMapper mapper = new ObjectMapper();       
        mapper.registerModule(new JodaModule());
        mapper.registerModule(new DoubleSerializerModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        TransactionPayload  transactionPayload = new TransactionPayload();
        transactionPayload.setTransactionId(transactionId);
        if (modelRequest != null) {
            setTranDocPyldForModelReq(transactionPayload, modelRequest, defaultRunAsofDate);
            fileName = modelRequest.getHeader().getFileName();
        } else {
            Map errorResponseData = (Map) tenantResponse.get(MessageVariables.HEADER);
            transactionPayload.setRunAsOfDate(defaultRunAsofDate);
            setTranDocPyldForTntRespns(transactionPayload, errorResponseData);
            fileName = (String) errorResponseData.get(MessageVariables.FILE_NAME);
        }

        if (StringUtils.isNotEmpty(fileName)) {
        	transactionPayload.setTransactionMode(MessageVariables.TRAN_BULK);
        } else {
        	transactionPayload.setTransactionMode(MessageVariables.TRAN_ONLINE);
        }

        transactionPayload.setLibraryName(NULL);
        transactionPayload.setRuntimeCallStart(start);
        transactionPayload.setRuntimeCallEnd(end);        
        body = IOUtils.toString(wrappedRequest.getReader());
        if (StringUtils.isNotBlank(body) && modelRequest != null) {           
            ModelRequest tenantRequest = mapper.readValue(body, ModelRequest.class);
            body = mapper.writeValueAsString(tenantRequest);
            tenantInput = mapper.readValue(body, new TypeReference<Map<String, Object>>() {
            });
            // removing tenantTranCount in case it is null.
            Map<String, Object> tenantInpHeader = (Map<String, Object>) tenantInput.get(MessageVariables.HEADER);
            Integer tenantTranCount = (Integer) tenantInpHeader.get(MessageVariables.TENANT_TRAN_COUNT);
            if (tenantTranCount == null) {
                tenantInpHeader.remove(MessageVariables.TENANT_TRAN_COUNT);
            }
        }
        if (tenantResponse != null) {
        	  tenantOutput = mapper.readValue(responseString, new TypeReference<Map<String, Object>>() {
              });
            Map<String, Object> header = (Map<String, Object>) tenantResponse.get(MessageVariables.HEADER);
            header.put(MessageVariables.TRAN_MODE, transactionPayload.getTransactionMode());
            if (!(Boolean) (header.get(MessageVariables.SUCCESS)) && (!(header.get(MessageVariables.ERROR_CODE) != null
                    && ((String) header.get(MessageVariables.ERROR_CODE)).startsWith(RuntimeConstants.RVE_EXCEPTION)))) {
                ((Map<String, Object>) tenantResponse.get(MessageVariables.HEADER)).put(MessageVariables.ERROR_MESSAGE,
                        RuntimeConstants.GENERIC_ERROR_MESSAGE);
            }
        }
        // added this to fix umg-4251 to set versionCreationTest flag to true
        // if it is test transaction during version creation else the flag will be false
        if (tenantInput != null) {
            Map<String, Object> tenantRequestHdr = (Map<String, Object>) tenantInput.get(MessageVariables.HEADER);
            if (tenantRequestHdr != null) {
                tenantRequestHdr.remove(MessageVariables.VERSION_CREATION_TEST);
                if(changeAddOnValidation && tenantRequestHdr.containsKey(addonValidation))
                {
                	tenantRequestHdr.put(FrameworkConstant.ADD_ON_VALIDATION, tenantRequestHdr.get(addonValidation));
                	 tenantRequestHdr.remove(addonValidation);
                }
                tenantInput.put(MessageVariables.HEADER, tenantRequestHdr);
            }
            if (StringUtils.isNotEmpty(fileName)) {
                tenantInput.put(MessageVariables.DATA, new HashMap<>());
            }
        }else{
        	if(body!=null){
	        	tenantInput =    mapper.readValue(body, new TypeReference<Map<String, Object>>() {
	            });;
        	}
        }
        TransactionDocumentPayload transactionDocumentPayload = new TransactionDocumentPayload();       
        
        if(tenantInput!=null){    
        	TransactionIOPayload txnIOPayload = new TransactionIOPayload();
        	txnIOPayload.setTransactionId(transactionId);
        	txnIOPayload.setTxnIOPayload(tenantInput);
        	transactionDocumentPayload.setTxnTIPayload(txnIOPayload);       	
        }
        if(tenantOutput!=null){
        	TransactionIOPayload txnTOPayload = new TransactionIOPayload();
            txnTOPayload.setTransactionId(transactionId);
            txnTOPayload.setTxnIOPayload(tenantOutput);
            transactionDocumentPayload.setTxnTOPayload(txnTOPayload);                 	
        }    
        transactionPayload.setStatus(TransactionStatus.ERROR.getStatus());
        transactionPayload.setCreatedDate(System.currentTimeMillis());
        transactionPayload.setErrorCode(getErrorCode(tenantResponse));
        if (getErrorDescription(tenantResponse) != null) {
        	transactionPayload.setErrorDescription(getErrorDescription(tenantResponse));
        } else {
        	transactionPayload.setErrorDescription(null);
        }
        LOGGER.error("Calling save transaction to mongo for umgTransactionId :"+transactionId);
        mongoTransactionLogDAO.insertTransactionLogToMongo(transactionPayload,transactionDocumentPayload);
    }

    private void setTranDocPyldForModelReq(TransactionPayload transactionPayload, ModelRequest modelRequest,
            Long runAsofDate) {
    	transactionPayload.setMajorVersion(
                modelRequest.getHeader().getMajorVersion() != null ? modelRequest.getHeader().getMajorVersion() : 0);
    	transactionPayload.setMinorVersion(
                modelRequest.getHeader().getMinorVersion() != null ? modelRequest.getHeader().getMinorVersion() : 0);
    	transactionPayload
                .setVersionName(modelRequest.getHeader().getModelName() != null ? modelRequest.getHeader().getModelName() : NULL);
    	transactionPayload.setRunAsOfDate(modelRequest.getHeader().getDate() != null
 ? modelRequest.getHeader().getDate().getMillis() : runAsofDate);
    	transactionPayload.setClientTransactionID(
                modelRequest.getHeader().getTransactionId() != null ? modelRequest.getHeader().getTransactionId() : NULL);
    	transactionPayload.setExecutionGroup(StringUtils.isBlank(modelRequest.getHeader().getExecutionGroup())
                ? MessageVariables.DEFAULT_EXECUTION_GROUP : modelRequest.getHeader().getExecutionGroup()); // UMG-4697 & added to
                                                                                                            // fix UMG-4500
                                                                                                            // Additional
                                                                                                            // variables in
                                                                                                            // Transaction header
    	transactionPayload.setCreatedBy(modelRequest.getHeader().getUser() != null ? modelRequest.getHeader().getUser()
                : RequestContext.getRequestContext().getTenantCode());
        if (StringUtils.isNotBlank(modelRequest.getHeader().getTransactionType())) { // UMG-4611 audhyabh
            String tranType = modelRequest.getHeader().getTransactionType().trim();
            transactionPayload.setTest(StringUtils.equalsIgnoreCase(tranType, TEST) ? Boolean.TRUE : Boolean.FALSE);
        } else {
        	transactionPayload.setTest(Boolean.FALSE);
        }
    }

    private void setTranDocPyldForTntRespns(TransactionPayload transactionPayload, Map errorResponseData) {
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
        	transactionPayload.setClientTransactionID((String) errorResponseData.get(MessageVariables.TRANSACTION_ID));
        } else {
        	transactionPayload.setClientTransactionID(NULL);
        }
        if (StringUtils.isNotBlank((String) errorResponseData.get(MessageVariables.EXECUTION_GROUP))) {// UMG-4697
        	transactionPayload.setExecutionGroup((String) errorResponseData.get(MessageVariables.EXECUTION_GROUP));
        } else {
        	transactionPayload.setExecutionGroup(MessageVariables.DEFAULT_EXECUTION_GROUP);
        }
        if (errorResponseData.get(MessageVariables.USER) != null) { // added to fix UMG-4500 Additional variables in Transaction
                                                                    // header
        	transactionPayload.setCreatedBy((String) errorResponseData.get(MessageVariables.USER));
        } else {
        	transactionPayload.setCreatedBy(RequestContext.getRequestContext().getTenantCode());
        }
        String transactionType = errorResponseData.get(MessageVariables.TRANSACTION_TYPE) != null
                ? (String) errorResponseData.get(MessageVariables.TRANSACTION_TYPE) : null;
        if (transactionType != null && StringUtils.equals(transactionType, MessageVariables.TEST)) {
        	transactionPayload.setTest(Boolean.TRUE);
        } else {
        	transactionPayload.setTest(Boolean.FALSE);
        }
    }

    private String getErrorDescription(Map<String, Object> tenantResponse) {
        String errorDescription = null;
        Map<String, Object> headerMap = (Map<String, Object>) tenantResponse.get(MessageVariables.HEADER);
        if (MapUtils.isNotEmpty(headerMap)) {
            errorDescription = (String) headerMap.get(MessageVariables.ERROR_MESSAGE);
        }
        return errorDescription;
    }

    private String getErrorCode(Map<String, Object> tenantResponse) {
        String errorCode = null;
        Map<String, Object> headerMap = (Map<String, Object>) tenantResponse.get(MessageVariables.HEADER);
        if (MapUtils.isNotEmpty(headerMap)) {
            errorCode = (String) headerMap.get(MessageVariables.ERROR_CODE);
        }
        return errorCode;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     **/
    public ContainerManager getContainerManager() {
        return containerManager;
    }

    /**
     * DOCUMENT ME!
     *
     * @param containerManager
     *            DOCUMENT ME!
     **/
    public void setContainerManager(ContainerManager containerManager) {
        this.containerManager = containerManager;
    }

    /**
     * Helper method to create tenant response object.
     *
     * @param errorCode
     *            UMG error code to be conveyed to tenant.
     * @param errorMessage
     *            UMG error message to be conveyed to tenant.
     * @param request
     *            DOCUMENT ME!
     *
     * @return {@link Map} an instance of UMG response.
     * @throws IOException
     **/
    private Map<String, Object> createTenantResponse(String errorCode, String errorMessage,
            ResettableStreamHttpServletRequest request, final Map<String, Boolean> notificationMap) throws IOException {
        Map<String, Object> tenantResponse = new LinkedHashMap<String, Object>();
        Map<String, Object> tenantResponseData = new LinkedHashMap<String, Object>();
        Map<String, Object> tenantResponseHeader = new LinkedHashMap<String, Object>();
        request.resetInputStreamForErrorResponse();
        String body = null;
        try {
            body = IOUtils.toString(request.getReader());
        } catch (IOException ex) {
            LOGGER.error("Error while reading request body to UmgRequest object", ex);
            SystemException systemException = new SystemException(RuntimeExceptionCode.RVE000201,
                    new Object[] { ex.getMessage() }, ex);
            tenantResponseHeader.put(MessageVariables.UMG_TRANSACTION_ID, MDC.get(RequestMarkerFilter.TRANSACTION_ID));
            tenantResponseHeader.put("success", false);
            tenantResponseHeader.put(MessageVariables.ERROR_CODE, systemException.getCode());
            tenantResponseHeader.put(MessageVariables.ERROR_MESSAGE, systemException.getLocalizedMessage());
            tenantResponse.put(MessageVariables.HEADER, tenantResponseHeader);
            tenantResponse.put(MessageVariables.DATA, tenantResponseData);
            return tenantResponse;
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        ModelRequest modelRequest = null;
        try {
            modelRequest = mapper.readValue(body, ModelRequest.class);
        } catch (IOException ex) {
            if (errorCode == null) {
                LOGGER.error("Error while converting reqeust body to UmgRequest object", ex);
                SystemException systemException = new SystemException(RuntimeExceptionCode.RVE000202,
                        new Object[] { ex.getMessage() }, ex);
                tenantResponseHeader.put(MessageVariables.UMG_TRANSACTION_ID, MDC.get(RequestMarkerFilter.TRANSACTION_ID));
                tenantResponseHeader.put("success", false);
                tenantResponseHeader.put(MessageVariables.ERROR_CODE, systemException.getCode());
                tenantResponseHeader.put(MessageVariables.ERROR_MESSAGE, systemException.getLocalizedMessage());
                tenantResponse.put(MessageVariables.HEADER, tenantResponseHeader);
                tenantResponse.put(MessageVariables.DATA, tenantResponseData);
                return tenantResponse;
            }
            else {
            	// UMG-9509
            	try {
            		ObjectMapper objMapper = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).registerModule(new JodaModule());
                	ModelRequest modelReq  = objMapper.readValue(body, ModelRequest.class);
                     Header header = modelReq.getHeader();
                     if (header.getUser() != null) {
                         tenantResponseHeader.put(MessageVariables.USER, header.getUser());
                     }
                     if (StringUtils.isNotBlank(header.getExecutionGroup())) {
                         tenantResponseHeader.put(MessageVariables.EXECUTION_GROUP, header.getExecutionGroup());
                     } else {
                         tenantResponseHeader.put(MessageVariables.EXECUTION_GROUP, MessageVariables.DEFAULT_EXECUTION_GROUP);
                     }
                     tenantResponseHeader.put(MessageVariables.TRANSACTION_TYPE, header.getTransactionType());
                     tenantResponseHeader.put(MessageVariables.MODEL_NAME, header.getModelName());
                     tenantResponseHeader.put(MessageVariables.MAJOR_VERSION, header.getMajorVersion());
                     tenantResponseHeader.put(MessageVariables.MINOR_VERSION, header.getMinorVersion());
                     tenantResponseHeader.put(MessageVariables.DATE,
                             ISODateTimeFormat.dateHourMinuteSecondMillis().print(header.getDate()));
                     tenantResponseHeader.put(MessageVariables.TRANSACTION_ID, header.getTransactionId());
                     tenantResponseHeader.put(MessageVariables.FILE_NAME, header.getFileName());
                     if (header.getTenantTranCount() != null) {
                         tenantResponseHeader.put(MessageVariables.TENANT_TRAN_COUNT, header.getTenantTranCount());
                     }
				} catch (IOException exp) {
	                LOGGER.error("Error while converting reqeust body to ModelRequest object", ex);
				}
            	
             
            }
        }
        if (modelRequest != null && modelRequest.getHeader() != null) {
            // UMG-4697
            if (modelRequest.getHeader().getUser() != null) {
                tenantResponseHeader.put(MessageVariables.USER, modelRequest.getHeader().getUser());
            }
            if (StringUtils.isNotBlank(modelRequest.getHeader().getExecutionGroup())) {
                tenantResponseHeader.put(MessageVariables.EXECUTION_GROUP, modelRequest.getHeader().getExecutionGroup());
            } else {
                tenantResponseHeader.put(MessageVariables.EXECUTION_GROUP, MessageVariables.DEFAULT_EXECUTION_GROUP);
            }

            notificationMap.put(NOTIFICATION_RSE_FLAG, Boolean.TRUE);

            tenantResponseHeader.put(MessageVariables.TRANSACTION_TYPE, modelRequest.getHeader().getTransactionType());
            tenantResponseHeader.put(MessageVariables.MODEL_NAME, modelRequest.getHeader().getModelName());
            tenantResponseHeader.put(MessageVariables.MAJOR_VERSION, modelRequest.getHeader().getMajorVersion());
            tenantResponseHeader.put(MessageVariables.MINOR_VERSION, modelRequest.getHeader().getMinorVersion());
            tenantResponseHeader.put(MessageVariables.DATE,
                    ISODateTimeFormat.dateHourMinuteSecondMillis().print(modelRequest.getHeader().getDate()));
            tenantResponseHeader.put(MessageVariables.TRANSACTION_ID, modelRequest.getHeader().getTransactionId());
            tenantResponseHeader.put(MessageVariables.FILE_NAME, modelRequest.getHeader().getFileName());
            if (modelRequest.getHeader().getTenantTranCount() != null) {
                tenantResponseHeader.put(MessageVariables.TENANT_TRAN_COUNT, modelRequest.getHeader().getTenantTranCount());
            }
        }
        tenantResponseHeader.put(MessageVariables.UMG_TRANSACTION_ID, MDC.get(RequestMarkerFilter.TRANSACTION_ID));
        tenantResponseHeader.put("success", false);
        tenantResponseHeader.put(MessageVariables.ERROR_CODE, errorCode);
        tenantResponseHeader.put(MessageVariables.ERROR_MESSAGE, errorMessage);
        tenantResponse.put(MessageVariables.HEADER, tenantResponseHeader);
        tenantResponse.put(MessageVariables.DATA, tenantResponseData);
        return tenantResponse;
    }

    /**
     * @return the transactionLogDAO
     */
    public TransactionLogDAO getTransactionLogDAO() {
        return transactionLogDAO;
    }

    /**
     * @param transactionLogDAO
     *            the transactionLogDAO to set
     */
    public void setTransactionLogDAO(TransactionLogDAO transactionLogDAO) {
        this.transactionLogDAO = transactionLogDAO;
    }

    /**
     * used for populating notification info map
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    private Map<String, String> getInfoMap(Map<String, Object> tenantResponse) {

        Map<String, String> infoMap = new HashMap<String, String>();
        infoMap.put(NotificationHeaderEnum.MODEL_NAME.getHeaderName(),
                (String) ((Map<String, Object>) tenantResponse.get(MessageVariables.HEADER)).get(MessageVariables.MODEL_NAME));
        infoMap.put(NotificationHeaderEnum.MAJOR_VERSION.getHeaderName(), String.valueOf(
                ((Map<String, Object>) tenantResponse.get(MessageVariables.HEADER)).get(MessageVariables.MAJOR_VERSION)));
        infoMap.put(NotificationHeaderEnum.MINOR_VERSION.getHeaderName(), String.valueOf(
                ((Map<String, Object>) tenantResponse.get(MessageVariables.HEADER)).get(MessageVariables.MINOR_VERSION)));
        infoMap.put(NotificationHeaderEnum.TRANSACTION_ID.getHeaderName(),
                (String) ((Map<String, Object>) tenantResponse.get(MessageVariables.HEADER))
                        .get(MessageVariables.TRANSACTION_ID));
        return infoMap;
    }

    public NotificationTriggerDelegate getNotificationTriggerDelegate() {
        return notificationTriggerDelegate;
    }

    public void setNotificationTriggerDelegate(NotificationTriggerDelegate notificationTriggerDelegate) {
        this.notificationTriggerDelegate = notificationTriggerDelegate;
    }
}