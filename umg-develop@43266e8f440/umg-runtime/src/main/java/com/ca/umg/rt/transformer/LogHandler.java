/**
 * 
 */
package com.ca.umg.rt.transformer;

import static com.ca.umg.rt.util.ME2WaitingTimeUtil.getMe2WaitingTime;
import static com.ca.umg.rt.util.ME2WaitingTimeUtil.getModelExecutionTime;
import static com.ca.umg.rt.util.ME2WaitingTimeUtil.getModeletExecution;
import static com.ca.umg.rt.util.MessageVariables.ME2_RESPONSE;
import static java.lang.Integer.valueOf;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.integration.Message;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.support.MessageBuilder;

import com.ca.framework.core.batch.TransactionStatus;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.util.TransactionDocumentPayload;
import com.ca.framework.core.util.TransactionIOPayload;
import com.ca.pool.model.ExecutionEnvironment;
import com.ca.pool.model.TransactionCriteria;
import com.ca.umg.me2.util.ModelExecResponse;
import com.ca.umg.rt.core.deployment.constants.RuntimeConstants;
import com.ca.umg.rt.core.flow.dao.MongoTransactionLogDAO;
import com.ca.umg.rt.core.flow.dao.TransactionLogDAO;
import com.ca.umg.rt.core.flow.entity.TransactionLog;
import com.ca.umg.rt.custom.serializers.DoubleSerializerModule;
import com.ca.umg.rt.exception.codes.RuntimeExceptionCode;
import com.ca.umg.rt.util.IOTransformerUtil;
import com.ca.umg.rt.util.ME2WaitingTimeUtil;
import com.ca.umg.rt.util.MessageVariables;
import com.ca.umg.rt.util.TransactionPayload;
import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("PMD")
public class LogHandler {

    private static final String STATUS = "status";
    // private static final String HEADER = "header";
    // private static final String SUCCESS = "success";
    // private static final String TRANSACTION_ID = "transactionId";
    // private static final String MODEL_EXEC_TIME = "modelExecutionTime";
    // private static final String MODELET_EXEC_TIME = "modeletExecutionTime";
    // private static final String ME2_WAIT_TIME = "me2WaitingTime";

    private static final Logger LOGGER = LoggerFactory.getLogger(LogHandler.class);

    private MongoTransactionLogDAO mongoTransactionLogDAO;

    private TransactionLogDAO transactionLogDAO;

    @SuppressWarnings("unchecked")
    @ServiceActivator
    public Object logTransactionToMongoAndMySql(Message<?> message) throws SystemException {
        long startTime = System.currentTimeMillis();
        Map<String, Object> payload = null;
        ObjectMapper mapper = null;
        Map<String, Object> tenantRequest = null;
        Map<String, Object> headers = null;
        Map<String, Object> tenantResponse = null;
        Map<String, Object> modelRequest = null;
        Map<String, Object> modelResponse = null;
        String transactionId = null;
        Boolean mySqlSuccess = Boolean.FALSE;
        try {
            payload = (Map<String, Object>) message.getPayload();
            mapper = new ObjectMapper();
            mapper.registerModule(new DoubleSerializerModule());
            tenantRequest = (Map<String, Object>) payload.get(MessageVariables.TENANT_REQUEST);
            headers = message.getHeaders();
            tenantResponse = (Map<String, Object>) payload.get(MessageVariables.TENANT_RESPONSE);
            modelRequest = (Map<String, Object>) payload.get(MessageVariables.MODEL_REQUEST);

            ModelExecResponse<Map<String, Object>> modelExecResponse = (ModelExecResponse<Map<String, Object>>) payload
                    .get(MessageVariables.ME2_RESPONSE);

            modelResponse = modelExecResponse == null ? null : modelExecResponse.getResponse();

            // modelResponse = (Map<String, Object>) payload.get(MessageVariables.MODEL_RESPONSE);
            transactionId = (String) (message.getHeaders().get(MessageVariables.UMG_TRANSACTION_ID) != null
                    ? message.getHeaders().get(MessageVariables.UMG_TRANSACTION_ID)
                    : message.getHeaders().get(MessageVariables.MESSAGE_ID));

            logTransactionToMysql(payload, tenantRequest, headers, tenantResponse, transactionId);
            mySqlSuccess = Boolean.TRUE;
        } catch (Exception ex) {// NOPMD
            LOGGER.error("Error while logging transaction to mysql LogHandler::logTransactionToMongoAndMySql", ex);
            throw new SystemException(RuntimeExceptionCode.RSE000907, new Object[] { ex.getMessage() });
        }

        if (mySqlSuccess) {
            try {
                logTransactionToMongo(payload, tenantRequest, headers, tenantResponse, modelRequest, modelResponse,
                        transactionId);
            } catch (DataAccessResourceFailureException dex) {
                LOGGER.error("Error in saving transaction to mongo LogHandler::logTransactionToMongoAndMySql - mongo db is down",
                        dex);
                transactionLogDAO.remove(transactionId);
                Map<String, Object> tenantResponseHdr = (Map<String, Object>) tenantResponse.get(MessageVariables.HEADER);
                tenantResponseHdr.remove(MessageVariables.UMG_TRANSACTION_ID);
                tenantResponseHdr.put(MessageVariables.SUCCESS, Boolean.FALSE);
                tenantResponseHdr.put("errorCode", "RSE000905");
                tenantResponseHdr.put("errorMessage", "System Exception. Please contact administrator.");
                return MessageBuilder.withPayload(message.getPayload()).copyHeaders(message.getHeaders())
                        .setHeader("TEST", "mongo db is down").build();
            } catch (Exception ex) {// NOPMD
                LOGGER.error("Error in saving transaction to mongo LogHandler::logTransactionToMongoAndMySql", ex);
                transactionLogDAO.remove(transactionId);
                throw new SystemException(RuntimeExceptionCode.RSE000906, new Object[] { ex.getMessage() });
            }
        }

        // Reset the error message to generic error message
        Map<String, Object> tenantResponseHdr = (Map<String, Object>) tenantResponse.get(MessageVariables.HEADER);
        if ((!(Boolean) tenantResponseHdr.get(MessageVariables.SUCCESS))
                && (!(tenantResponseHdr.get(MessageVariables.ERROR_CODE) != null
                        && ((String) tenantResponseHdr.get(MessageVariables.ERROR_CODE))
                                .startsWith(RuntimeConstants.RVE_EXCEPTION)))) {
            tenantResponseHdr.put(MessageVariables.ERROR_MESSAGE, RuntimeConstants.GENERIC_ERROR_MESSAGE);
        }
        LOGGER.error("LogHandler.logTransactionToMongoAndMySql : " + (System.currentTimeMillis() - startTime));
        ((Map<String, Object>) message.getPayload()).put(MessageVariables.TENANT_RESPONSE, new LinkedHashMap<>());
        return message;
    }

    private void logTransactionToMongo(Map<String, Object> payload, Map<String, Object> tenantRequest,
            Map<String, Object> headers, Map<String, Object> tenantResponse, Map<String, Object> modelRequest,
            Map<String, Object> modelResponse, String transactionId) {
        TransactionDocumentPayload transactionDocumentPayload = new TransactionDocumentPayload();
        TransactionPayload txnPayload = new TransactionPayload();
        TransactionIOPayload txnTIPayload = new TransactionIOPayload();
        TransactionIOPayload txnTOPayload = new TransactionIOPayload();
        TransactionIOPayload txnMIPayload = new TransactionIOPayload();
        TransactionIOPayload txnMOPayload = new TransactionIOPayload();
        


        // added to fix UMG-4500 Additional variables in Transaction header
        txnPayload.setTest((Boolean) ((Integer) headers.get("test") == 1 ? true : false));
        txnPayload.setCreatedBy(headers.get("tenantCode") != null ? (String) headers.get("tenantCode")
                : RequestContext.getRequestContext().getTenantCode());

        // added this to fix umg-4251 to set versionCreationTest flag to true
        // if it is test transaction during version creation else the flag will be false
        if (tenantRequest != null) {
            Map<String, Object> tenantRequestHdr = (Map<String, Object>) tenantRequest.get(MessageVariables.HEADER);
            if (tenantRequestHdr != null) {
                tenantRequestHdr.remove(MessageVariables.VERSION_CREATION_TEST);
                tenantRequest.put(MessageVariables.HEADER, tenantRequestHdr);
                // added to fix UMG-4500 Additional variables in Transaction header
                if (StringUtils.isNotBlank((String) tenantRequestHdr.get("transactionType"))) { // UMG-4611 audhyabh
                    String tranType = ((String) tenantRequestHdr.get("transactionType")).trim();
                    txnPayload
                            .setTest(StringUtils.equalsIgnoreCase(tranType, "test") ? Boolean.TRUE : Boolean.FALSE);

                }
                if (StringUtils.isNotBlank((String) tenantRequestHdr.get("user"))) {
                	txnPayload.setCreatedBy((String) tenantRequestHdr.get("user"));
                }
                // UMG-4697
                if (StringUtils.isNotBlank((String) tenantRequestHdr.get("executionGroup"))) {
                	txnPayload.setExecutionGroup((String) tenantRequestHdr.get("executionGroup"));
                } else {
                	txnPayload.setExecutionGroup(MessageVariables.DEFAULT_EXECUTION_GROUP);
                }	
             
            }
        }

       
        txnPayload.setTransactionId(transactionId);
        
        txnTIPayload.setTransactionId(transactionId);
        txnTOPayload.setTransactionId(transactionId);
        txnMIPayload.setTransactionId(transactionId);
        txnMOPayload.setTransactionId(transactionId);
        
        txnPayload.setTenantId(RequestContext.getRequestContext().getTenantCode());
        String clientTransactionId = headers.get(MessageVariables.TRANSACTION_ID) != null
                ? (String) headers.get(MessageVariables.TRANSACTION_ID) : "";

        LOGGER.info("tenantRequest is " + tenantRequest);
        LOGGER.info("Status of the txn Id" + transactionId + " is :" + headers.get(STATUS));
        txnPayload.setClientTransactionID(clientTransactionId);
        txnPayload
                .setStatus(headers.get(STATUS) != null ? (String) headers.get(STATUS) : TransactionStatus.QUEUED.getStatus());
        String errorCode = (String) (headers.get(STATUS) != null
                ? (MessageVariables.SUCCESS.equalsIgnoreCase((String) headers.get(STATUS)) ? null
                        : ((Map<String, Object>) tenantResponse.get(MessageVariables.HEADER)).get(MessageVariables.ERROR_CODE))
                : null);
        txnPayload.setErrorCode(errorCode);
        
    	if (errorCode!=null || headers.get(MessageVariables.PAYLOAD_STORAGE)== null || ((Boolean) headers.get(MessageVariables.PAYLOAD_STORAGE))) {
    		txnTIPayload.setTxnIOPayload(tenantRequest);
			txnTOPayload.setTxnIOPayload(tenantResponse);
    		txnMIPayload.setTxnIOPayload(modelRequest);			
    		txnMOPayload.setTxnIOPayload(modelResponse);
		}else{					
			txnTIPayload.setTxnIOPayload(getHeaderOnly(tenantRequest));
			txnTOPayload.setTxnIOPayload(getHeaderOnly(tenantResponse));					
		}

        String errorDescription = (String) (headers.get(STATUS) != null
                ? (MessageVariables.SUCCESS.equalsIgnoreCase((String) headers.get(STATUS)) ? null
                        : ((Map<String, Object>) tenantResponse.get(MessageVariables.HEADER)).get(MessageVariables.ERROR_MESSAGE))
                : null);
        if (errorDescription != null) {
        	txnPayload.setErrorDescription(errorDescription);
        } else {
        	txnPayload.setErrorDescription(null);
        }
        Map<String, Object> tenantResponseHdr = (Map<String, Object>) tenantResponse.get(MessageVariables.HEADER);
        if ((!(Boolean) tenantResponseHdr.get(MessageVariables.SUCCESS))
                && (!(tenantResponseHdr.get(MessageVariables.ERROR_CODE) != null
                        && ((String) tenantResponseHdr.get(MessageVariables.ERROR_CODE))
                                .startsWith(RuntimeConstants.RVE_EXCEPTION)))) {
            ((Map<String, Object>) tenantResponse.get(MessageVariables.HEADER)).put(MessageVariables.ERROR_MESSAGE,
                    RuntimeConstants.GENERIC_ERROR_MESSAGE);
        }

        txnPayload.setLibraryName((String) headers.get("modelLibraryVersionName"));
        txnPayload.setVersionName((String) headers.get(MessageVariables.MODEL_NAME));
        txnPayload.setMajorVersion((Integer) headers.get(MessageVariables.MAJOR_VERSION));
        txnPayload.setMinorVersion((Integer) headers.get(MessageVariables.MINOR_VERSION));
        LOGGER.error("runAsofDate is :"+(Long) ((Map<String, Object>) payload.get("request")).get("TESTDATE_MILLIS"));

        txnPayload.setRunAsOfDate((Long) ((Map<String, Object>) payload.get("request")).get("TESTDATE_MILLIS"));
        txnPayload.setCreatedDate(System.currentTimeMillis());
        txnPayload.setRuntimeCallStart((Long) headers.get(MessageVariables.RNTM_CALL_START));
        txnPayload.setRuntimeCallEnd((Long) headers.get(MessageVariables.RNTM_CALL_END));

        txnPayload.setModelCallStart((Long) headers.get(MessageVariables.MODEL_CALL_START));

        txnPayload.setModelCallEnd((Long) headers.get(MessageVariables.MODEL_CALL_END));

        txnPayload.setModelExecutionTime(ME2WaitingTimeUtil.getModelExecutionTime(payload, headers));
        txnPayload.setModeletExecutionTime(ME2WaitingTimeUtil.getModeletExecution(payload, headers));
        txnPayload.setMe2WaitingTime(ME2WaitingTimeUtil.getMe2WaitingTime(payload, headers));

        setEnvironmentInfo(txnPayload, modelRequest, payload);
        if (payload.get("environment") != null) {
            payload.remove("environnment");
        }

        setModeletInfo(txnPayload, payload);
        
        transactionDocumentPayload.setTxnMIPayload(txnMIPayload);
        transactionDocumentPayload.setTxnMOPayload(txnMOPayload);
        transactionDocumentPayload.setTxnTIPayload(txnTIPayload);
        transactionDocumentPayload.setTxnTOPayload(txnTOPayload);       

        getMongoTransactionLogDAO().insertTransactionLogToMongo(txnPayload,transactionDocumentPayload);
    }

    private Integer logTransactionToMysql(Map<String, Object> payload, Map<String, Object> tenantRequest,
            Map<String, Object> headers, Map<String, Object> tenantResponse, String transactionId) {
        TransactionLog transactionLog = null;
        Integer result = null;
        transactionLog = new TransactionLog();
        transactionLog.setId(transactionId);
        String clientTransactionId = headers.get(MessageVariables.TRANSACTION_ID) != null
                ? (String) headers.get(MessageVariables.TRANSACTION_ID) : "";
        transactionLog.setTransactionId(clientTransactionId);
        transactionLog.setLibraryName((String) headers.get("modelLibraryVersionName"));
        transactionLog.setModelName((String) headers.get(MessageVariables.MODEL_NAME));
        transactionLog.setMajorVersion((Integer) headers.get(MessageVariables.MAJOR_VERSION));
        transactionLog.setMinorVersion((Integer) headers.get(MessageVariables.MINOR_VERSION));
        transactionLog
                .setStatus(headers.get(STATUS) != null ? (String) headers.get(STATUS) : TransactionStatus.QUEUED.getStatus());
        transactionLog.setTenantInput(RuntimeConstants.STR_EMPTY);
        transactionLog.setTenantOutput(RuntimeConstants.STR_EMPTY);
        transactionLog.setModelInput(RuntimeConstants.STR_EMPTY);
        transactionLog.setModelOutput(RuntimeConstants.STR_EMPTY);
        LOGGER.error("runAsofDate is ====="+(Long) ((Map<String, Object>) payload.get("request")).get("TESTDATE_MILLIS"));
        transactionLog.setRunAsOfDate((Long) ((Map<String, Object>) payload.get("request")).get("TESTDATE_MILLIS"));
        transactionLog.setRuntimeStart((Long) headers.get(MessageVariables.RNTM_CALL_START));
        transactionLog.setRuntimeEnd((Long) headers.get(MessageVariables.RNTM_CALL_END));
        transactionLog.setModelCallStart((Long) headers.get(MessageVariables.MODEL_CALL_START));
        transactionLog.setModelCallEnd((Long) headers.get(MessageVariables.MODEL_CALL_END));
        transactionLog.setIsTest((Integer) headers.get("test"));
        // added to fix UMG-4500 Additional variables in Transaction header
        // transactionLog.setCreatedBy(RequestContext.getRequestContext().getTenantCode());
        if (tenantRequest != null) {
            Map<String, Object> tenantRequestHdr = (Map<String, Object>) tenantRequest.get(MessageVariables.HEADER);
            if (tenantRequestHdr != null) {
                if (StringUtils.isNotBlank((String) tenantRequestHdr.get("transactionType"))) { // UMG-4611 audhyabh
                    String tranType = ((String) tenantRequestHdr.get("transactionType")).trim();
                    transactionLog.setIsTest(StringUtils.equalsIgnoreCase(tranType, "test") ? 1 : 0);
                }
                if (StringUtils.isNotBlank((String) tenantRequestHdr.get("user"))) {
                    transactionLog.setCreatedBy((String) tenantRequestHdr.get("user"));
                }
                IOTransformerUtil.setAddOnValidations(transactionLog, tenantRequestHdr);               
                
            }
        }

        String errorCode = (String) (headers.get(STATUS) != null
                ? (MessageVariables.SUCCESS.equalsIgnoreCase((String) headers.get(STATUS)) ? null
                        : ((Map<String, Object>) tenantResponse.get(MessageVariables.HEADER)).get(MessageVariables.ERROR_CODE))
                : null);
        transactionLog.setErrorCode(errorCode);
        String errorDescription = (String) (headers.get(STATUS) != null
                ? (MessageVariables.SUCCESS.equalsIgnoreCase((String) headers.get(STATUS)) ? null
                        : ((Map<String, Object>) tenantResponse.get(MessageVariables.HEADER)).get(MessageVariables.ERROR_MESSAGE))
                : null);
        if (errorDescription != null) {
            transactionLog.setErrorDescription(errorDescription.getBytes());
        } else {
            transactionLog.setErrorDescription(null);
        }

        transactionLog.setModelExecutionTime(getModelExecutionTime(payload, headers));
        transactionLog.setModeletExecutionTime(getModeletExecution(payload, headers));
        transactionLog.setMe2WaitingTime(getMe2WaitingTime(payload, headers));

        result = transactionLogDAO.insertTxnFlowLog(transactionLog);
        return result;
    }

    public MongoTransactionLogDAO getMongoTransactionLogDAO() {
        return mongoTransactionLogDAO;
    }

    public void setMongoTransactionLogDAO(MongoTransactionLogDAO mongoTransactionLogDAO) {
        this.mongoTransactionLogDAO = mongoTransactionLogDAO;
    }

    public TransactionLogDAO getTransactionLogDAO() {
        return transactionLogDAO;
    }

    public void setTransactionLogDAO(TransactionLogDAO transactionLogDAO) {
        this.transactionLogDAO = transactionLogDAO;
    }

    private void setEnvironmentInfo(final TransactionPayload transactionPayload,
            final Map<String, Object> modelRequest, final Map<String, Object> payload) {
        TransactionCriteria transactionCriteria = null;
        if (modelRequest != null) {
            if (modelRequest.get("headerInfo") != null) {
                final Map<Object, Object> headerInfo = (Map<Object, Object>) modelRequest.get("headerInfo");
                if (headerInfo != null && headerInfo.get("transactionCriteria") != null) {
                    transactionCriteria = (TransactionCriteria) headerInfo.get("transactionCriteria");
                }
            }
        }
        
        if (transactionCriteria != null) {
        	transactionPayload.setModellingEnv(transactionCriteria.getExecutionLanguage()+RuntimeConstants.CHAR_HYPHEN+transactionCriteria.getExecutionLanguageVersion());            
        	transactionPayload.setExecEnv(ExecutionEnvironment.getEnvironment(transactionCriteria.getExecutionEnvironment()).getEnvironment());
        }
    }


    private void setModeletInfo(final TransactionPayload transactionPayload, final Map<String, Object> payload) {
        if (payload != null) {
            final ModelExecResponse<Map<String, Object>> me2Response = (ModelExecResponse<Map<String, Object>>) payload
                    .get(ME2_RESPONSE);
            if (me2Response != null) {
            	transactionPayload.setModeletServerHost(me2Response.getHost());
            	transactionPayload.setModeletServerPort(me2Response.getPort());
            	transactionPayload.setModeletServerMemberHost(me2Response.getMemberHost());
            	transactionPayload.setModeletServerMemberPort(me2Response.getMemberPort());
            	transactionPayload.setModeletPoolName(me2Response.getPoolName());
            	transactionPayload.setModeletPoolCriteria(me2Response.getPoolCriteria());
            	transactionPayload.setModeletServerType(me2Response.getServerType());
            	transactionPayload.setModeletServerContextPath(me2Response.getContextPath());
            }
        }
    }

    private String getStringVlaueFromME2Response(final Map<String, Object> me2Response, final String key) {
        final Object value = me2Response.get(key);
        if (value != null) {
            return value.toString();
        } else {
            return null;
        }
    }

    private Integer getIntegerVlaueFromME2Response(final Map<String, Object> me2Response, final String key) {
        final Object value = me2Response.get(key);
        if (value != null) {
            return valueOf(value.toString());
        } else {
            return null;
        }
    }
    
    private Map<String, Object> getHeaderOnly(Map<String,Object> requestOrResponse) {
        if (requestOrResponse != null && requestOrResponse.get(MessageVariables.DATA) != null) {            
        		requestOrResponse.put(MessageVariables.DATA, null);            
        }
        return requestOrResponse;
    }
}