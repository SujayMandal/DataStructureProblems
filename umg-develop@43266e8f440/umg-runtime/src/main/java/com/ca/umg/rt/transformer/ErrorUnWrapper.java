/*
 * ErrorUnWrapper.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.transformer;

import java.net.ConnectException;
import java.sql.Types;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jgroups.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.Message;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.MessageRejectedException;
import org.springframework.integration.MessagingException;
import org.springframework.integration.message.ErrorMessage;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.transformer.AbstractTransformer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.HttpClientErrorException;

import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.constants.PoolConstants;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.pool.PoolObjectsLoader;
import com.ca.pool.TransactionMode;
import com.ca.umg.me2.exception.codes.ModelExecutorExceptionCodes;
import com.ca.umg.notification.model.NotificationHeaderEnum;
import com.ca.umg.notification.notify.NotificationTriggerDelegate;
import com.ca.umg.rt.core.deployment.constants.RuntimeConstants;
import com.ca.umg.rt.exception.codes.RuntimeExceptionCode;
import com.ca.umg.rt.util.MessageVariables;
import com.mongodb.MongoException;

/**
 * Component to transform exceptions to message containing error code and error message.
 **/
@SuppressWarnings("PMD")
public class ErrorUnWrapper extends AbstractTransformer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorUnWrapper.class);
    private static final String TENANT_RESPONE = "tenantResponse";
    private static final String TENANT_REST_RESPONE = "tenantRestResponse";

    private final JdbcTemplate jdbcTemplate;

    private static final String DELETE_UMG_RUNTIMETX_RECORD = "DELETE FROM UMG_RUNTIME_TRANSACTION WHERE ID = ?";
    
    private NotificationTriggerDelegate notificationTriggerDelegate;
    
    private String notificationErrorCode;
    
    private String notificationErrorMsg;

    private PoolObjectsLoader poolObjectsLoader;

    /**
     * Initializes JDBC template with data source.
     */
    public ErrorUnWrapper(DataSource dataSource, final NotificationTriggerDelegate notificationTriggerDelegate, PoolObjectsLoader poolObjectsLoader) {
        super();
        jdbcTemplate = new JdbcTemplate(dataSource);
        this.notificationTriggerDelegate = notificationTriggerDelegate;
        this.poolObjectsLoader = poolObjectsLoader;
    }

    /**
     * Component to transform any exception to a meaningful response message. Avoids propagating actual exception to clients
     *
     * @param message
     *            {@link ErrorMessage} containing actual exception. Payload of message is an instance of exception
     *
     * @return {@link Message enriched with meaningful error code and error message for the user.
     **/
    @SuppressWarnings("unchecked")
    @Override
    protected Object doTransform(Message<?> message) {
        LOGGER.error("UMG runtime processing faced an error in one of the components while processing the request with id {}",
                message.getHeaders().getId().toString());
        ErrorMessage errorMessage = (ErrorMessage) message;

        LOGGER.error("Exception is: {}", errorMessage.getPayload(), errorMessage.getPayload());

        Message<?> failedMessage = ((MessagingException) errorMessage.getPayload()).getFailedMessage();
        Throwable th = searchCause(errorMessage.getPayload());
        Map<String, Object> tenantResponse = null;     

        if (th instanceof BusinessException) {
            BusinessException businessException = (BusinessException) errorMessage.getPayload().getCause();
            notificationErrorCode = businessException.getCode();
            notificationErrorMsg = businessException.getLocalizedMessage();
            tenantResponse = createTenantResponse(businessException.getCode(),
                    businessException.getLocalizedMessage(), failedMessage);

            if (businessException.getCode().equals(RuntimeExceptionCode.RVE000702)) {
                ((Map<String, Object>) tenantResponse.get(MessageVariables.HEADER)).put("errors",
                        ((Map<String, Object>) failedMessage.getPayload()).get(MessageVariables.VALIDATIONS));
            }

            ((Map<String, Object>) failedMessage.getPayload()).put(TENANT_RESPONE, tenantResponse);
            LOGGER.error("Caused a business exception {}", businessException.getLocalizedMessage());
        } else if (th instanceof SystemException) {
            tenantResponse = null;
            SystemException systemException = (SystemException) errorMessage.getPayload().getCause();
            notificationErrorCode = systemException.getCode();
            notificationErrorMsg = systemException.getLocalizedMessage();
            
            if (StringUtils.equalsIgnoreCase("ME0030", systemException.getCode())) {
                tenantResponse = createTenantResponse(RuntimeExceptionCode.RSE000830, systemException.getLocalizedMessage(), failedMessage);
                notificationErrorCode = RuntimeExceptionCode.RSE000830;
            } else if (StringUtils.equalsIgnoreCase(ModelExecutorExceptionCodes.MSE0000001, systemException.getCode())) {
                tenantResponse = createTenantResponse(RuntimeExceptionCode.RSE000807, systemException.getLocalizedMessage(),
                        failedMessage);
                notificationErrorCode = RuntimeExceptionCode.RSE000807;
            } else if (StringUtils.equalsIgnoreCase(ModelExecutorExceptionCodes.MSE0000203, notificationErrorCode)) {
                systemException = new SystemException(RuntimeExceptionCode.RSE000813, new Object[] {},
                        errorMessage.getPayload());
                tenantResponse = createTenantResponse(systemException.getCode(), systemException.getLocalizedMessage(),
                        failedMessage);
                notificationErrorCode = RuntimeExceptionCode.RSE000813;
                notificationErrorMsg = systemException.getLocalizedMessage();
            } else if (StringUtils.equalsIgnoreCase(ModelExecutorExceptionCodes.MSE0000217, notificationErrorCode)) {
                Object value = poolObjectsLoader.getCacheRegistry().getMap(SystemParameterProvider.SYSTEM_PARAMETER).get(PoolConstants.RETRY_COUNT);
                String retryCount = value == null ? null : value.toString();
                String timeOut = StringUtils.substringAfterLast(notificationErrorMsg, ":");
                systemException = new SystemException(RuntimeExceptionCode.RSE000817, new Object[] {
                        timeOut, retryCount},
                        errorMessage.getPayload());
                tenantResponse = createTenantResponse(systemException.getCode(), systemException.getLocalizedMessage(),
                        failedMessage);
                notificationErrorCode = RuntimeExceptionCode.RSE000817;
                notificationErrorMsg = systemException.getLocalizedMessage();
            } else if (StringUtils.equalsIgnoreCase(ModelExecutorExceptionCodes.MSE0000218, notificationErrorCode)) {
                Object value = poolObjectsLoader.getCacheRegistry().getMap(SystemParameterProvider.SYSTEM_PARAMETER).get(PoolConstants.RETRY_COUNT);
                String retryCount = value == null ? null : value.toString();
                String poolNameAndTimeout = StringUtils.substringAfterLast(notificationErrorMsg, ":");
                systemException = new SystemException(RuntimeExceptionCode.RSE000818, new Object[] {
                        StringUtils.substringBeforeLast(poolNameAndTimeout, ","),
                        StringUtils.substringAfterLast(poolNameAndTimeout, ","), retryCount},
                        errorMessage.getPayload());
                tenantResponse = createTenantResponse(systemException.getCode(), systemException.getLocalizedMessage(),
                        failedMessage);
                notificationErrorCode = RuntimeExceptionCode.RSE000818;
                notificationErrorMsg = systemException.getLocalizedMessage();
            } else {
                tenantResponse = createTenantResponse(systemException.getCode(), systemException.getLocalizedMessage(),
                        failedMessage);
            }
            ((Map<String, Object>) failedMessage.getPayload()).put(TENANT_RESPONE, tenantResponse);
            LOGGER.error("Caused a system exception {}", systemException.getLocalizedMessage());
        } else if (th instanceof MessageRejectedException) {
            Map<String, Object> payload = (Map<String, Object>) failedMessage.getPayload();
            Map<String, Object> me2Response = (Map<String, Object>) payload.get(MessageVariables.ME2_RESPONSE);
            String errorId = UUID.randomUUID().toString();

            String me2ReturnedCode = (String) me2Response.get(MessageVariables.ERROR_CODE);
            SystemException systemException = null;
            if (StringUtils.equals(me2ReturnedCode, "MSE0000203")) {
                systemException = new SystemException(RuntimeExceptionCode.RSE000813, new Object[] {
                        me2Response.get(MessageVariables.ERROR_CODE), me2Response.get(MessageVariables.ME2_ERROR_MESSAGE) },
                        errorMessage.getPayload());
            } else if (StringUtils.equals(me2ReturnedCode, "MSE0000401") || StringUtils.equals(me2ReturnedCode, "MSE0000402")) {
                systemException = new SystemException(RuntimeExceptionCode.RSE000814, new Object[] {
                        me2Response.get(MessageVariables.ERROR_CODE), me2Response.get(MessageVariables.ME2_ERROR_MESSAGE) },
                        errorMessage.getPayload());
            } else {
                systemException = new SystemException(RuntimeExceptionCode.RSE000807, new Object[] {
                        me2Response.get(MessageVariables.ERROR_CODE), me2Response.get(MessageVariables.ME2_ERROR_MESSAGE) },
                        errorMessage.getPayload());
            }
            notificationErrorCode = systemException.getCode();
            notificationErrorMsg = systemException.getLocalizedMessage();
            tenantResponse = createTenantResponse(systemException.getCode(),
                    systemException.getLocalizedMessage(), failedMessage);
            ((Map<String, Object>) failedMessage.getPayload()).put(TENANT_RESPONE, tenantResponse);
            LOGGER.error("Caused an integration exception error id is {} and error payload {}.", errorId,
                    errorMessage.getPayload());
        } else if (th instanceof MongoException) {
            Map<String, Object> payload = (Map<String, Object>) failedMessage.getPayload();
            tenantResponse = (Map<String, Object>) payload.get("tenantResponse");
            Map<String, Object> headers = (Map<String, Object>) tenantResponse.get(MessageVariables.HEADER);
            String umgTransactionId = headers.get("umgTransactionId") != null ? headers.get("umgTransactionId").toString()
                    : headers.get("id").toString();
            
            LOGGER.error(
                    "Caused an exception while tyring to insert record to Mongodb. Umg Transaction id is {} and error payload {}.",
                    umgTransactionId, errorMessage.getPayload());
            LOGGER.debug("Deleting record from Uumg_runtime_transcation table for id - ", umgTransactionId);
            Object[] params = { umgTransactionId };
            int[] types = { Types.VARCHAR };

            jdbcTemplate.update(DELETE_UMG_RUNTIMETX_RECORD, params, types);
        } else {
            String errorId = UUID.randomUUID().toString();
            SystemException systemException = new SystemException(RuntimeExceptionCode.RSE000808, new Object[] { errorId });
            notificationErrorCode = systemException.getCode();
            notificationErrorMsg =  systemException.getLocalizedMessage();
            tenantResponse = createTenantResponse(systemException.getCode(),
                    systemException.getLocalizedMessage(), failedMessage);
            ((Map<String, Object>) failedMessage.getPayload()).put(TENANT_RESPONE, tenantResponse);
            LOGGER.error("Caused an integration exception error id is {} and error payload {}.", errorId,
                    errorMessage.getPayload());
        }
        
        List<String> addOnValidation  = null;
        
        if(failedMessage.getPayload()!=null){
        	addOnValidation = ModelResponseUtil.setAddOnValidation(((Map<String, Object>)failedMessage.getPayload()), poolObjectsLoader.getCacheRegistry());
        }
        
        ((Map<String, Object>) failedMessage.getPayload()).put(TENANT_REST_RESPONE, createRestResponse(tenantResponse,addOnValidation,errorMessage));

        LOGGER.debug("Failed message is enriched with error messages.");
        
        final Map<String, String> notificationInfoMap = getInfoMap(failedMessage);
       
        try {
        	if (notificationErrorCode != null && notificationErrorCode.startsWith("RSE")) {
            	notificationTriggerDelegate.modelAndSystemFailureMail(notificationInfoMap, notificationErrorCode, notificationErrorMsg, true, poolObjectsLoader.getActiveAndInactiveModeletClients());
        	}
        } catch (SystemException | BusinessException e) {
        	LOGGER.error(e.getLocalizedMessage());
        }
        
        return MessageBuilder.withPayload(failedMessage.getPayload()).setHeader("status", "ERROR")
                .setHeader("errorMessage", ((MessagingException) errorMessage.getPayload()).getMessage())
                .copyHeadersIfAbsent(failedMessage.getHeaders()).copyHeadersIfAbsent(errorMessage.getHeaders())
                .setReplyChannel((MessageChannel) errorMessage.getHeaders().getReplyChannel()).build();
    }

    /**
     * Helper method to create tenant response object.
     *
     * @param errorCode
     *            UMG error code to be conveyed to tenant.
     * @param errorMessage
     *            UMG error message to be conveyed to tenant.
     * @param message
     *            DOCUMENT ME!
     *
     * @return {@link Map} an instance of UMG response.
     **/
    private Map<String, Object> createTenantResponse(String errorCode, String errorMessage, Message<?> message) {
        LOGGER.error("Error Code is :" + errorCode);
        LOGGER.error("Error Message is :" + errorMessage);
        Map<String, Object> testnantReponse = new LinkedHashMap<String, Object>();
        Map<String, Object> tenantResponseData = new LinkedHashMap<String, Object>();
        Map<String, Object> tenantResponseHeader = new LinkedHashMap<String, Object>();
        tenantResponseHeader.put(MessageVariables.MODEL_NAME, message.getHeaders().get(MessageVariables.MODEL_NAME));
        tenantResponseHeader.put(MessageVariables.MAJOR_VERSION, message.getHeaders().get(MessageVariables.MAJOR_VERSION));
        tenantResponseHeader.put(MessageVariables.MINOR_VERSION, message.getHeaders().get(MessageVariables.MINOR_VERSION));
        
        Map<String, Object> payloadRequest = (Map<String, Object>) message.getPayload();
        if (payloadRequest != null) {
            /*Map<String, Object> tenantRequest = (Map<String, Object>) payloadRequest.get("tenantRequest");
            if (tenantRequest != null) {*/
        	 Map<String, Object> tenantRequestHeader = (Map<String, Object>) payloadRequest.get("tenantRequestHeader");
                // UMG-4697
                if (tenantRequestHeader != null) {
                	if(tenantRequestHeader.get(MessageVariables.TENANT_TRAN_COUNT) != null){
                		tenantResponseHeader.put(MessageVariables.TENANT_TRAN_COUNT, tenantRequestHeader.get(MessageVariables.TENANT_TRAN_COUNT));
                	}
                	Object tranModeFromReq = tenantRequestHeader.get(MessageVariables.TRAN_MODE);
                	String tranMode = tranModeFromReq!=null?(String)tranModeFromReq: message.getHeaders().get(MessageVariables.FILE_NAME_HEADER) != null?MessageVariables.TRAN_BULK:TransactionMode.ONLINE.getMode();
                	if(StringUtils.equals(tranMode,TransactionMode.ONLINE.getMode())  && tenantRequestHeader.get(MessageVariables.BATCH_ID)!=null){
                		tranMode = TransactionMode.BATCH.getMode(); 
                	}
                	tenantResponseHeader.put(MessageVariables.TRAN_MODE,tranMode);
                 	Object payloadStorage = tenantRequestHeader.get(MessageVariables.PAYLOAD_STORAGE);
                 	if(payloadStorage!=null){
                 		tenantResponseHeader.put(MessageVariables.PAYLOAD_STORAGE,(Boolean)payloadStorage);                 		
                 	}
                    tenantResponseHeader.put(MessageVariables.EXECUTION_GROUP,
                            StringUtils.isBlank((String) tenantRequestHeader.get(MessageVariables.EXECUTION_GROUP))
                                    ? MessageVariables.DEFAULT_EXECUTION_GROUP
                                    : tenantRequestHeader.get(MessageVariables.EXECUTION_GROUP));
                    if (tenantRequestHeader.get(MessageVariables.ADD_ON_VALIDATION) != null) {
                        tenantResponseHeader.put(MessageVariables.ADD_ON_VALIDATION, tenantRequestHeader.get(MessageVariables.ADD_ON_VALIDATION));
                    }
                }
                if (tenantRequestHeader != null && tenantRequestHeader.get(MessageVariables.USER) != null) {
                    tenantResponseHeader.put(MessageVariables.USER, tenantRequestHeader.get(MessageVariables.USER));
                }
            //}
        }
        tenantResponseHeader.put(MessageVariables.DATE, message.getHeaders().get(MessageVariables.DATE_USED));
        tenantResponseHeader.put(MessageVariables.TRANSACTION_ID, message.getHeaders().get(MessageVariables.TRANSACTION_ID));
        tenantResponseHeader.put(MessageVariables.UMG_TRANSACTION_ID,
                message.getHeaders().get(MessageVariables.UMG_TRANSACTION_ID) != null
                        ? message.getHeaders().get(MessageVariables.UMG_TRANSACTION_ID)
                        : message.getHeaders().get(MessageVariables.MESSAGE_ID));
        tenantResponseHeader.put(MessageVariables.SUCCESS, false);
        tenantResponseHeader.put(MessageVariables.ERROR_CODE, errorCode);
        tenantResponseHeader.put(MessageVariables.ERROR_MESSAGE, errorMessage);
        testnantReponse.put(MessageVariables.HEADER, tenantResponseHeader);
        testnantReponse.put(MessageVariables.DATA, tenantResponseData);
        LOGGER.error("testnantReponse is :" + testnantReponse);
        return testnantReponse;
    }

    /**
     * Helper method to search for known exceptions. Known exceptions are then translated error codes for easy error
     * communication.
     *
     * @param exception
     *            Root cause.
     *
     * @return Specific exception.
     **/
    private Throwable searchCause(Throwable exception) {
        if (exception instanceof BusinessException || exception instanceof SystemException
                || exception instanceof MessageRejectedException || exception instanceof ConnectException
                || exception instanceof HttpClientErrorException || exception instanceof MongoException) {
            return exception;
        }
        if (exception.getCause() != null) {
            Throwable cause = searchCause(exception.getCause());

            if (cause instanceof BusinessException || cause instanceof SystemException
                    || cause instanceof MessageRejectedException || cause instanceof ConnectException
                    || cause instanceof HttpClientErrorException || cause instanceof MongoException) {
                return cause;
            }
        }

        return exception;
    }
    
    private Map<String,String> getInfoMap(Message<?> message){
    	Map<String,String> infoMap = new HashMap<String, String>();
    	infoMap.put(NotificationHeaderEnum.MODEL_NAME.getHeaderName(), (String) message.getHeaders().get(MessageVariables.MODEL_NAME));
        infoMap.put(NotificationHeaderEnum.MAJOR_VERSION.getHeaderName(), String.valueOf(message.getHeaders().get(MessageVariables.MAJOR_VERSION)));
        infoMap.put(NotificationHeaderEnum.MINOR_VERSION.getHeaderName(), String.valueOf(message.getHeaders().get(MessageVariables.MINOR_VERSION)));
		infoMap.put(NotificationHeaderEnum.TRANSACTION_ID.getHeaderName(), (String) message.getHeaders().get(MessageVariables.TRANSACTION_ID));
    	return infoMap;
    }

	public NotificationTriggerDelegate getNotificationTriggerDelegate() {
		return notificationTriggerDelegate;
	}

	public void setNotificationTriggerDelegate(NotificationTriggerDelegate notificationTriggerDelegate) {
		this.notificationTriggerDelegate = notificationTriggerDelegate;
	}
	
	private Map<String, Object> createRestResponse(Map<String, Object> tenantResponse,List<String> addOnValidation,ErrorMessage errorMessage) {
		Map<String, Object> tenantRestResponse = new HashMap<>();
		Map<String, Object> headerData = new HashMap<>();
		headerData.putAll((Map<String, Object>)tenantResponse.get(MessageVariables.HEADER));
		if(StringUtils.startsWith((CharSequence) headerData.get(MessageVariables.ERROR_CODE),RuntimeConstants.RMV_EXCEPTION)){
			headerData.put(MessageVariables.ERROR_MESSAGE, RuntimeConstants.GENERIC_ERROR_MESSAGE);
		}
		if(StringUtils.startsWith((CharSequence) headerData.get(MessageVariables.ERROR_CODE),RuntimeExceptionCode.RSE000835)) {
			headerData.put(MessageVariables.ERROR_MESSAGE, RuntimeConstants.GENERIC_ERROR_MESSAGE);
		}
		if(CollectionUtils.isNotEmpty(addOnValidation)){
			headerData.put(FrameworkConstant.ADD_ON_VALIDATION,addOnValidation);			
		}
		tenantRestResponse.put(MessageVariables.HEADER, headerData);
		tenantRestResponse.put(MessageVariables.DATA, new HashMap<>());
		return tenantRestResponse;
	}
}
