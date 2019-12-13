package com.ca.umg.notification.notify;

import static com.ca.umg.notification.NotificationConstants.AUTH_CODE_RESEND_EVENT;
import static com.ca.umg.notification.NotificationConstants.DEFAULT_MINOR_VERSION;
import static com.ca.umg.notification.NotificationConstants.ID_FIELD_SEPERATOR;
import static com.ca.umg.notification.NotificationExceptionCodes.TEMPLATE_NOT_AVAILABLE;
import static com.ca.umg.notification.model.NotificationEventNameEnum.EXCESSIVE_MODEL_EXEC_TIME;
import static com.ca.umg.notification.model.NotificationEventNameEnum.MODELET_RESTART;
import static com.ca.umg.notification.model.NotificationEventNameEnum.MODEL_PUBLISH_APPROVAL;
import static com.ca.umg.notification.model.NotificationEventNameEnum.NEW_TENANT_ADDED;
import static com.ca.umg.notification.model.NotificationEventNameEnum.NOTIFY_MODELET_STATUS_CHANGE;
import static com.ca.umg.notification.model.NotificationEventNameEnum.ON_MODEL_PUBLISH;
import static com.ca.umg.notification.model.NotificationEventNameEnum.RUNTIME_TRANSACTION_FAILURE;
import static com.ca.umg.notification.model.NotificationHeaderEnum.ACTIVE_FROM;
import static com.ca.umg.notification.model.NotificationHeaderEnum.ACTIVE_UNTIL;
import static com.ca.umg.notification.model.NotificationHeaderEnum.AUTH_TOKEN;
import static com.ca.umg.notification.model.NotificationHeaderEnum.BATCH_ENABLED;
import static com.ca.umg.notification.model.NotificationHeaderEnum.BULK_ENABLED;
import static com.ca.umg.notification.model.NotificationHeaderEnum.EMAIL_NOTIFICATION_ENABLED;
import static com.ca.umg.notification.model.NotificationHeaderEnum.LOADED_MODEL;
import static com.ca.umg.notification.model.NotificationHeaderEnum.LOADED_MODEL_VERSION;
import static com.ca.umg.notification.model.NotificationHeaderEnum.MAJOR_VERSION;
import static com.ca.umg.notification.model.NotificationHeaderEnum.MINOR_VERSION;
import static com.ca.umg.notification.model.NotificationHeaderEnum.MODEL_NAME;
import static com.ca.umg.notification.model.NotificationHeaderEnum.MODEL_TO_LOAD;
import static com.ca.umg.notification.model.NotificationHeaderEnum.MODEL_VERSION_NAME_TO_LOAD;
import static com.ca.umg.notification.model.NotificationHeaderEnum.MODEL_VERSION_TO_LOAD;
import static com.ca.umg.notification.model.NotificationHeaderEnum.NEW_POOL_NAME;
import static com.ca.umg.notification.model.NotificationHeaderEnum.POOL_NAME;
import static com.ca.umg.notification.model.NotificationHeaderEnum.PORT;
import static com.ca.umg.notification.model.NotificationHeaderEnum.PUBLISHER_NAME;
import static com.ca.umg.notification.model.NotificationHeaderEnum.REASON;
import static com.ca.umg.notification.model.NotificationHeaderEnum.RESET_BY;
import static com.ca.umg.notification.model.NotificationHeaderEnum.RESET_REASON;
import static com.ca.umg.notification.model.NotificationHeaderEnum.R_SERVE_PORT;
import static com.ca.umg.notification.model.NotificationHeaderEnum.TENANT_CODE;
import static com.ca.umg.notification.model.NotificationHeaderEnum.TENANT_NAME;
import static com.ca.umg.notification.model.NotificationHeaderEnum.TENANT_ONBOARDED_BY;
import static com.ca.umg.notification.model.NotificationHeaderEnum.TENANT_ONBOARDED_ON;
import static com.ca.umg.notification.model.NotificationHeaderEnum.TRANSACTION_ID;
import static com.ca.umg.notification.model.NotificationHeaderEnum.TRANSACTION_RUN_DATE;
import static com.ca.umg.notification.model.NotificationHeaderEnum.VERSION_ID;
import static com.ca.umg.notification.model.NotificationHeaderEnum.EXEC_COUNT;
import static com.ca.umg.notification.model.NotificationHeaderEnum.RESTART_COUNT;
import static com.ca.umg.notification.util.NotificationUtil.getDateTimeFormatted;
import static com.ca.umg.notification.util.NotificationUtil.getFormattedDate;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.encryption.EncryptionUtil;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.info.TenantData;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.notification.NotificationConstants;
import com.ca.umg.notification.NotificationService;
import com.ca.umg.notification.dao.NotificationDao;
import com.ca.umg.notification.model.NotificationAdditionalDetails;
import com.ca.umg.notification.model.NotificationAttachment;
import com.ca.umg.notification.model.NotificationAuditDocument;
import com.ca.umg.notification.model.NotificationEvent;
import com.ca.umg.notification.model.NotificationEventData;
import com.ca.umg.notification.model.NotificationEventNameEnum;
import com.ca.umg.notification.model.NotificationHeaderEnum;
import com.ca.umg.notification.model.NotificationHeaders;
import com.hazelcast.core.IMap;

@Named
public class NotificationTriggerBOImpl implements NotificationTriggerBO {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationTriggerBOImpl.class);

	private static final String UMG_ENV = "umg-env";

	private static final String DOT = ".";

	@Inject
	private NotificationService notificationService;

	@Inject
	private CacheRegistry cacheRegistry;
	
	@Inject
	private NotificationDao mysqlDao;
	
	@Override
	public void notifyModelPublishSuccess(final Map<String, String> versionInfoMap, final boolean async) throws SystemException, BusinessException {
		final Long triggerTime = System.currentTimeMillis();
    	final NotificationEvent event = new NotificationEvent();
    	event.setName(ON_MODEL_PUBLISH.getName());
    	
    	final NotificationEventData eventData = new NotificationEventData();
    	final String tenantCode = RequestContext.getRequestContext().getTenantCode();
    	
    	final NotificationHeaders headers = new NotificationHeaders();
    	headers.setTenantName(getTenantName(tenantCode));
    	headers.setTenantCode(tenantCode);
    	headers.setPublishedDateTime(getDateTimeFormatted(triggerTime));
    	
    	headers.setPublisherName(versionInfoMap.get(PUBLISHER_NAME.getHeaderName()));
    	
    	headers.setModelName(versionInfoMap.get(MODEL_NAME.getHeaderName()));
    	headers.setMinorVersion(getMinorVersion(versionInfoMap.get(MINOR_VERSION.getHeaderName())));
    	headers.setMajorVersion(Integer.valueOf(versionInfoMap.get(MAJOR_VERSION.getHeaderName())));
    	headers.setModelVersion(getModelVersion(versionInfoMap));
    	headers.setEnvironment(getEnvironment());
    	headers.setTransactionId(versionInfoMap.get(TRANSACTION_ID.getHeaderName()));

    	eventData.setNotificationHeaders(headers);
    	eventData.setSubjectMap(headers.getHeadersMap());
    	eventData.setBodyMap(headers.getHeadersMap());
    	
    	final NotificationAdditionalDetails additionalDetails = new NotificationAdditionalDetails();
    	eventData.setAdditionalDetails(additionalDetails);
    	
    	final NotificationAuditDocument document = buildNotificationDocument(event, eventData, headers.getPublisherName(), triggerTime);
    	
    	notificationService.sendNotification(event, eventData, async, document);
    }

	@Override
	public void notifyRuntimeFailure(final Map<String, String> versionInfoMap, final String errorCode, final String errorMessage, final boolean async, List<Map<String, Object>> modeletClientInfo)
			throws SystemException, BusinessException {

		final Long triggerTime = System.currentTimeMillis();
		final NotificationEvent event = new NotificationEvent();
		event.setName(RUNTIME_TRANSACTION_FAILURE.getName());

		final NotificationEventData eventData = new NotificationEventData();
		final String tenantCode = RequestContext.getRequestContext().getTenantCode();

		final NotificationHeaders headers = new NotificationHeaders();
		headers.setTenantName(getTenantName(tenantCode));
		headers.setTenantCode(tenantCode);
		headers.setTranExecDateTime(getDateTimeFormatted(triggerTime));
		headers.setEnvironment(getEnvironment());
		headers.setTransactionId(versionInfoMap.get(TRANSACTION_ID.getHeaderName()));
		headers.setModelName(versionInfoMap.get(MODEL_NAME.getHeaderName()));
		headers.setMinorVersion(getMinorVersion(versionInfoMap.get(MINOR_VERSION.getHeaderName())));
		headers.setMajorVersion(Integer.parseInt(versionInfoMap.get(MAJOR_VERSION.getHeaderName())));
		headers.setModelVersion(getModelVersion(versionInfoMap));
		headers.setErrorCode(errorCode);
		headers.setErrorMessage(errorMessage);
		headers.setModeletList(modeletClientInfo);

		eventData.setNotificationHeaders(headers);
		eventData.setSubjectMap(headers.getHeadersMap());
		eventData.setBodyMap(headers.getHeadersMap());

		final NotificationAdditionalDetails additionalDetails = new NotificationAdditionalDetails();
		eventData.setAdditionalDetails(additionalDetails);
		
		final NotificationAuditDocument document = buildNotificationDocument(event, eventData, versionInfoMap.get(TRANSACTION_ID.getHeaderName()), triggerTime);

		notificationService.sendNotification(event, eventData, async, document);
	}

	@Override
	public void sendModelApprovalEmail(Map<String, String> versionInfoMap, final List<NotificationAttachment> attachments, 
			final boolean async) throws SystemException, BusinessException {
		final Long triggerTime = System.currentTimeMillis();
		final NotificationEvent event = new NotificationEvent();
    	event.setName(MODEL_PUBLISH_APPROVAL.getName());
    	
    	final NotificationEventData eventData = new NotificationEventData();
    	final String tenantCode = RequestContext.getRequestContext().getTenantCode();
    	
    	final NotificationHeaders headers = new NotificationHeaders();
    	headers.setTenantName(getTenantName(tenantCode));
    	headers.setTenantCode(tenantCode);
    	headers.setPublishedDateTime(getFormattedDate(System.currentTimeMillis()));
    	headers.setPublisherName(versionInfoMap.get(PUBLISHER_NAME.getHeaderName()));
    	headers.setModelName(versionInfoMap.get(MODEL_NAME.getHeaderName()));
 	    headers.setUmgAdminUrl(versionInfoMap.get(NotificationHeaderEnum.UMG_ADMIN_URL.getHeaderName()));
	    headers.setMajorVersion(Integer.valueOf(versionInfoMap.get(MAJOR_VERSION.getHeaderName())));
    	headers.setModelVersion(getModelVersion(versionInfoMap));
    	headers.setEnvironment(getEnvironment());
    	
    	headers.setTransactionId(versionInfoMap.get(TRANSACTION_ID.getHeaderName()));
    	
    	headers.setVersionId(versionInfoMap.get(VERSION_ID.getHeaderName()));
    	headers.setModelApprovalURL(createdModelApprovalURL(headers, MODEL_PUBLISH_APPROVAL.getName()));
    	        
    	eventData.setNotificationHeaders(headers);
    	eventData.setSubjectMap(headers.getHeadersMap());
    	eventData.setBodyMap(headers.getHeadersMap());
    	
    	final NotificationAdditionalDetails additionalDetails = new NotificationAdditionalDetails();
    	eventData.setAdditionalDetails(additionalDetails);
    	
    	final NotificationAuditDocument document = buildNotificationDocument(event, eventData, versionInfoMap.get(PUBLISHER_NAME.getHeaderName()), triggerTime);
    	
    	notificationService.sendMailWithAttachments(event, eventData, attachments, async, document);		
	}

	@Override
	public void notifyNewTenantAdded(Map<String, String> newTenantInfoMap,boolean async) throws SystemException, BusinessException {
		final Long triggerTime = System.currentTimeMillis();
    	final NotificationEvent event = new NotificationEvent();
    	event.setName(NEW_TENANT_ADDED.getName());
    	
    	final NotificationEventData eventData = new NotificationEventData();
    	
    	final NotificationHeaders headers = new NotificationHeaders();
    	headers.setTenantName(newTenantInfoMap.get(TENANT_NAME.getHeaderName()));
    	headers.setTenantCode(newTenantInfoMap.get(TENANT_CODE.getHeaderName()));
    	headers.setEnvironment(getEnvironment());
    	/*headers.setAuthCode(newTenantInfoMap.get(AUTH_TOKEN.getHeaderName()));
    	headers.setActiveFrom(getDateTimeFormatted(Long.valueOf(newTenantInfoMap.get(ACTIVE_FROM.getHeaderName())).longValue()));
    	headers.setActiveUntil(getDateTimeFormatted(Long.valueOf(newTenantInfoMap.get(ACTIVE_UNTIL.getHeaderName())).longValue()));*/
    	headers.setEmailNotificationEnabled(Boolean.valueOf(newTenantInfoMap.get(EMAIL_NOTIFICATION_ENABLED.getHeaderName())));
    	headers.setBulkEnabled(Boolean.valueOf(newTenantInfoMap.get(BULK_ENABLED.getHeaderName())));
    	headers.setBatchEnabled(Boolean.valueOf(newTenantInfoMap.get(BATCH_ENABLED.getHeaderName())));
    	headers.setTenantOnboardedOn(newTenantInfoMap.get(TENANT_ONBOARDED_ON.getHeaderName()));
    	headers.setTenantOnboardedBy(newTenantInfoMap.get(TENANT_ONBOARDED_BY.getHeaderName()));
    	
    	eventData.setNotificationHeaders(headers);
    	eventData.setSubjectMap(headers.getHeadersMap());
    	eventData.setBodyMap(headers.getHeadersMap());
    	
    	final NotificationAdditionalDetails additionalDetails = new NotificationAdditionalDetails();
    	eventData.setAdditionalDetails(additionalDetails);
    	
    	final NotificationAuditDocument document = buildNotificationDocument(event, eventData, headers.getPublisherName(), triggerTime);
    	notificationService.sendNotification(event, eventData, async, document);
	}

	@Override
	public void nofitySendAuthToken(Map<String, String> authTokenChangeInfoMap,boolean async) throws SystemException, BusinessException {
		final Long triggerTime = System.currentTimeMillis();
    	final NotificationEvent event = new NotificationEvent();
    	event.setName(AUTH_CODE_RESEND_EVENT);
    	
    	final NotificationEventData eventData = new NotificationEventData();
    	final String tenantCode = authTokenChangeInfoMap.get(TENANT_CODE.getHeaderName());
    	final NotificationHeaders headers = new NotificationHeaders();
    	headers.setTenantName(authTokenChangeInfoMap.get(TENANT_NAME.getHeaderName()));
    	headers.setTenantCode(tenantCode);
    	headers.setEnvironment(getEnvironment());
    	headers.setAuthCode(authTokenChangeInfoMap.get(AUTH_TOKEN.getHeaderName()));
    	headers.setActiveFrom(getDateTimeFormatted(Long.valueOf(authTokenChangeInfoMap.get(ACTIVE_FROM.getHeaderName())).longValue()));
    	headers.setActiveUntil(getDateTimeFormatted(Long.valueOf(authTokenChangeInfoMap.get(ACTIVE_UNTIL.getHeaderName())).longValue()));
    	headers.setResetBy(authTokenChangeInfoMap.get(RESET_BY.getHeaderName()));
    	headers.setResetReason(authTokenChangeInfoMap.get(RESET_REASON.getHeaderName()));
    	
    	eventData.setNotificationHeaders(headers);
    	eventData.setSubjectMap(headers.getHeadersMap());
    	eventData.setBodyMap(headers.getHeadersMap());
    	
    	final NotificationAdditionalDetails additionalDetails = new NotificationAdditionalDetails();
    	eventData.setAdditionalDetails(additionalDetails);
    	
    	final NotificationAuditDocument document = buildNotificationDocument(event, eventData, headers.getPublisherName(), triggerTime);
    	
    	notificationService.sendNotification(event, eventData, async, document);
	}
	
	private String getTenantName(final String tenantCode) {
		final IMap<String, TenantData> map = cacheRegistry.getMap(FrameworkConstant.TENANT_URL_MAP);
		return map.get(tenantCode) != null ? map.get(tenantCode).getTenantName() : tenantCode;
	}

	private String getModelVersion(Map<String, String> versionInfoMap) {
		return versionInfoMap.get(MAJOR_VERSION.getHeaderName()) + DOT + getMinorVersion(versionInfoMap.get(MINOR_VERSION.getHeaderName()));
	}
	
	private String getEnvironment() {
		final IMap<String, String> map = cacheRegistry.getMap(CacheRegistry.UMG_PROPERTIES_MAP);
		
        for (final String key : map.keySet()) {
        	LOGGER.info("key : {}, Value : {}", key, map.get(key));
        }
        
		return map.get(UMG_ENV);
	}
	
	private String createdModelApprovalURL(final NotificationHeaders headers, final String eventName) throws SystemException, BusinessException {
		final StringBuilder sb = new StringBuilder();
		
		sb.append(headers.getUmgAdminUrl());
		sb.append("/#/model/approval/");
		sb.append(getEncriptedURLId(eventName, headers.getTenantCode(), headers.getVersionId()));
		
		return sb.toString();
	}
	
	private String getEncriptedURLId(final String eventName, final String tenantCode, final String versionId) throws SystemException, BusinessException {
		final String toAddress = mysqlDao.getToAddresses(eventName, tenantCode);
		if (StringUtils.isBlank(toAddress)) {
	    	LOGGER.error(TEMPLATE_NOT_AVAILABLE.getDescription());
            SystemException.newSystemException(TEMPLATE_NOT_AVAILABLE.getCode(), new Object[] {TEMPLATE_NOT_AVAILABLE.getDescription()});     
        }
		final String toAddUUIDAndTCode = toAddress + ID_FIELD_SEPERATOR + versionId + ID_FIELD_SEPERATOR + tenantCode;
		return EncryptionUtil.encryptToken(toAddUUIDAndTCode);
	}
	
	private NotificationAuditDocument buildNotificationDocument(final NotificationEvent event, final NotificationEventData eventData, final String userId,
			final Long triggerTime) {
		final NotificationAuditDocument document = new NotificationAuditDocument();
		
		document.setEventName(event.getName());
		document.setEventTriggerTimestamp(getDateTimeFormatted(triggerTime));	
		document.setEventTriggerTimestampInMillies(triggerTime);
		
		document.setUserId(userId);
		document.setModelNameAndVersion(eventData.getNotificationHeaders().getModelName() + eventData.getNotificationHeaders().getModelVersion());
		document.setTransactionId(eventData.getNotificationHeaders().getTransactionId());
		document.setTenant(eventData.getNotificationHeaders().getTenantName());
		
		return document;
	}

	private NotificationAuditDocument buildNotificationDocument(final NotificationEvent event, final NotificationEventData eventData,
			final Long triggerTime) {
		final NotificationAuditDocument document = new NotificationAuditDocument();
		
		document.setEventName(event.getName());
		document.setEventTriggerTimestamp(getDateTimeFormatted(triggerTime));	
		document.setEventTriggerTimestampInMillies(triggerTime);
		document.setModelNameAndVersion(eventData.getNotificationHeaders().getModelName() + eventData.getNotificationHeaders().getModelVersion());
		document.setTransactionId(eventData.getNotificationHeaders().getTransactionId());
		document.setTenant(eventData.getNotificationHeaders().getTenantName());
		
		return document;
	}

    private NotificationAuditDocument buildModeletRestartNotificationDocument(final NotificationEvent event, final NotificationEventData eventData, final String userId,
                                                                final Long triggerTime) {
        final NotificationAuditDocument document = new NotificationAuditDocument();

        document.setEventName(event.getName());
        document.setEventTriggerTimestamp(getDateTimeFormatted(triggerTime));
        document.setEventTriggerTimestampInMillies(triggerTime);

        document.setUserId(userId);
        document.setModelNameAndVersion(eventData.getNotificationHeaders().getModelName() + eventData.getNotificationHeaders().getModelVersion());
        if(StringUtils.isNotEmpty(eventData.getNotificationHeaders().getTransactionId())) {
            document.setTransactionId(eventData.getNotificationHeaders().getTransactionId());
        }
        if(StringUtils.isNotEmpty(eventData.getNotificationHeaders().getModelToLoad())) {

        }
        return document;
    }

	private int getMinorVersion(final String minorVersion) {
		if (minorVersion == null ||  minorVersion.equalsIgnoreCase("null")|| minorVersion.length() == DEFAULT_MINOR_VERSION) {
			return DEFAULT_MINOR_VERSION;
		} else {
			return Integer.valueOf(minorVersion);
		}
	}

    @Override
    public void notifyModeletRestart(final Map<String, String> modeletClientInfoMap, Map<String, String> info, boolean async) throws BusinessException, SystemException {
        final Long triggerTime = System.currentTimeMillis();
        final String tenantCode = RequestContext.getRequestContext().getTenantCode();
        final NotificationEvent event = new NotificationEvent();
        event.setName(MODELET_RESTART.getName());

        final NotificationEventData eventData = new NotificationEventData();

        final NotificationHeaders headers = new NotificationHeaders();
        headers.setEnvironment(getEnvironment());
        headers.setPort(modeletClientInfoMap.get(PORT.getHeaderName()));
        headers.setrServePort(modeletClientInfoMap.get(R_SERVE_PORT.getHeaderName()));
        headers.setModeletHost(modeletClientInfoMap.get("memberHost"));
        headers.setPoolName(modeletClientInfoMap.get(POOL_NAME.getHeaderName()));
        headers.setLoadedModel(info.get(LOADED_MODEL.getHeaderName())!=null?info.get(LOADED_MODEL.getHeaderName()):"NA");
        headers.setLoadedModelVersion(info.get(LOADED_MODEL_VERSION.getHeaderName())!=null?info.get(LOADED_MODEL_VERSION.getHeaderName()):"NA");
        headers.setReason(info.get(REASON.getHeaderName()));
        headers.setTransactionId(info.get(TRANSACTION_ID.getHeaderName()) != null?info.get(TRANSACTION_ID.getHeaderName()):"NA");
        headers.setTransactionRunDate(info.get(TRANSACTION_RUN_DATE.getHeaderName())!=null?info.get(TRANSACTION_RUN_DATE.getHeaderName()):"NA");
        headers.setModelToLoad(info.get(MODEL_TO_LOAD.getHeaderName())!=null?info.get(MODEL_TO_LOAD.getHeaderName()):"NA");
        headers.setModelVersionToLoad(info.get(MODEL_VERSION_TO_LOAD.getHeaderName())!=null?info.get(MODEL_VERSION_TO_LOAD.getHeaderName()):"NA");
        headers.setModelVersionNameToLoad(info.get(MODEL_VERSION_NAME_TO_LOAD.getHeaderName()));
        headers.setNewPoolName(info.get(NEW_POOL_NAME.getHeaderName())!=null?info.get(NEW_POOL_NAME.getHeaderName()):"NA");
        headers.setResetBy(info.get(RESET_BY.getHeaderName())!=null?info.get(RESET_BY.getHeaderName()):"SYSTEM");
        headers.setExecLimit(info.get(EXEC_COUNT.getHeaderName()));
        headers.setRestartCount(info.get(RESTART_COUNT.getHeaderName()));
        
        headers.setTenantCode(tenantCode);
        headers.setRestartInitiatedTime(getDateTimeFormatted(triggerTime));

        eventData.setNotificationHeaders(headers);
        eventData.setSubjectMap(headers.getHeadersMap());
        eventData.setBodyMap(headers.getHeadersMap());

        final NotificationAuditDocument document = buildModeletRestartNotificationDocument(event, eventData, "SYSTEM", triggerTime);

        notificationService.sendNotification(event, eventData, async, document);
    }
    
    @Override
	public void nofityExcessModelExecTime(NotificationHeaders headers, boolean async)
			throws SystemException, BusinessException {
		final Long triggerTime = System.currentTimeMillis();
    	final NotificationEvent event = new NotificationEvent();
    	event.setName(EXCESSIVE_MODEL_EXEC_TIME.getName());    	
    	final NotificationEventData eventData = new NotificationEventData();    	
    	headers.setEnvironment(getEnvironment());    	
    	headers.setPublisherName("SYSTEM");
    	eventData.setNotificationHeaders(headers);
    	eventData.setSubjectMap(headers.getHeadersMap());
    	eventData.setBodyMap(headers.getHeadersMap());    	
    	final NotificationAdditionalDetails additionalDetails = new NotificationAdditionalDetails();
    	eventData.setAdditionalDetails(additionalDetails);    	
    	final NotificationAuditDocument document = buildNotificationDocument(event, eventData, headers.getPublisherName(), triggerTime);
    	
    	notificationService.sendNotification(event, eventData, async, document);
		
	}

	@Override
	public void notifyModeletStatusfinal(Map<String, Object> modeletClientInfoMap,boolean async) throws SystemException, BusinessException {
		final Long triggerTime = System.currentTimeMillis();
    	final NotificationEvent event = new NotificationEvent();
    	event.setName(NOTIFY_MODELET_STATUS_CHANGE.getName());    	
    	final NotificationEventData eventData = new NotificationEventData();

    	//fetch the tenant code from notification_event_template_mapping based on NOTIFY_MODELET_STATUS_CHANGE 
    	
    	 String tenantCode = mysqlDao.getTenantCode(NOTIFY_MODELET_STATUS_CHANGE.getName());
    	
         final NotificationHeaders headers = new NotificationHeaders();
         headers.setTenantCode(tenantCode);
         headers.setTenantName(NotificationConstants.NA);
         headers.setModelName(NotificationConstants.NA);
         headers.setModelVersion(NotificationConstants.NA);
         headers.setTransactionId(NotificationConstants.NA);
         headers.setEnvironment(getEnvironment());
         headers.setRestartInitiatedTime(getDateTimeFormatted(triggerTime));
         if(modeletClientInfoMap.get("modeletList") != null ){
     	 headers.setModeletList((List<Map<String, Object>>)modeletClientInfoMap.get("modeletList"));
         }
         eventData.setNotificationHeaders(headers);
         eventData.setSubjectMap(headers.getHeadersMap());
         eventData.setBodyMap(headers.getHeadersMap());
    	final NotificationAuditDocument document = buildNotificationDocument(event, eventData,triggerTime);
    	
    	notificationService.sendNotification(event, eventData, async, document);
		
	}
	
}
