package com.ca.umg.notification;

import static com.ca.umg.notification.NotificationConstants.NOTIFICATION_TEMPLATE_DIR;
import static com.ca.umg.notification.model.NotificationStatus.SUCCESS;
import static com.ca.umg.notification.util.NotificationUtil.getDateTimeFormatted;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.info.tenant.TenantInfo;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.umg.notification.dao.NotificationDao;
import com.ca.umg.notification.model.MailDetails;
import com.ca.umg.notification.model.NotificationAttachment;
import com.ca.umg.notification.model.NotificationAuditDocument;
import com.ca.umg.notification.model.NotificationClassification;
import com.ca.umg.notification.model.NotificationDetails;
import com.ca.umg.notification.model.NotificationEvent;
import com.ca.umg.notification.model.NotificationEventData;
import com.ca.umg.notification.model.NotificationStatus;
import com.ca.umg.notification.model.NotificationTypes;
import com.ca.umg.notification.model.SMSDetails;
import com.ca.umg.notification.util.VelocityUtil;

@Named
public class NotificationServiceBOImpl implements NotificationServiceBO {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceBOImpl.class);

	@Inject
	private NotificationAudit notificationAudit;
	
	@Inject
	private SystemParameterProvider systemParameterProvider;
	
	@Inject
	private CacheRegistry cacheRegistry;
	
	@Override
	public NotificationStatus sendMail(final MailDetails mailDetails, final NotificationEventData eventData) throws SystemException, BusinessException {
		LOGGER.info("Sending Mail");
		mailDetails.setSubject(VelocityUtil.getSubject(mailDetails, eventData, getTemplateDirectory()));
		mailDetails.setBodyText(VelocityUtil.getBody(mailDetails, eventData, getTemplateDirectory()));
		
		return SendMail.sendEMail(systemParameterProvider, mailDetails);
	}
	
	@Override
	public NotificationStatus sendMailWithAttachments(final MailDetails mailDetails, final NotificationEventData eventData, final List<NotificationAttachment> attachments) 
			throws SystemException, BusinessException {
		LOGGER.info("Sending Mail with attachments");
		mailDetails.setSubject(VelocityUtil.getSubject(mailDetails, eventData, getTemplateDirectory()));
		mailDetails.setBodyText(VelocityUtil.getBody(mailDetails, eventData, getTemplateDirectory()));
		
		final NotificationStatus status = SendMail.sendEMailWithAttachments(systemParameterProvider, mailDetails, attachments);
		deleteAttachments(attachments);		
		return status;
	}

	@Override
	public NotificationStatus sendSMS(final SMSDetails mailDetails, final NotificationEventData eventData) throws SystemException, BusinessException {
		LOGGER.info("Sending SMS");
		return SUCCESS;
	}
	
	@Override
	public void saveNotification(final NotificationAuditDocument document, final NotificationDetails notificationDetails, final NotificationStatus status) throws SystemException, BusinessException {
		updateAuditDocument(document, notificationDetails, status);
		notificationAudit.saveNotification(document);
	}
	
	public void setSystemParameterProvider(final SystemParameterProvider systemParameterProvider) {
        this.systemParameterProvider = systemParameterProvider;
    }

    public SystemParameterProvider getSystemParameterProvider() {
        return systemParameterProvider;
    }
    
    private String getTemplateDirectory() {
    	String dir = VelocityUtil.dirLocation;
    	if (systemParameterProvider.getParameter(NOTIFICATION_TEMPLATE_DIR) != null) {
    		dir = systemParameterProvider.getParameter(NOTIFICATION_TEMPLATE_DIR);
    	}
    	
    	LOGGER.info("Notification template dir is : {}", dir);
    	return dir;
    }
    
    private void deleteAttachments(final List<NotificationAttachment> attachments) {
    	LOGGER.info("Deleteuing attachments after sending mail");
    	if (attachments != null && !attachments.isEmpty()) {
    		for (final NotificationAttachment attachment : attachments) {
    			if (attachment.isDeleteFileAfterSend()) {
    				LOGGER.info("Deleting file : {}", attachment.getFileName());
    				attachment.getFile().deleteOnExit();
    			}
    		}
    	}
    }
    
	
	private void updateAuditDocument(final NotificationAuditDocument document, final NotificationDetails notificationDetails, final NotificationStatus status) {
		document.setEmailTriggerTimestamp(getDateTimeFormatted(notificationDetails.getMailTriggerTimestamp()));
		document.setEmailTriggerTimestampInMillies(notificationDetails.getMailTriggerTimestamp());
		document.setNotificationType(notificationDetails.getNotificationType());		
		document.setStatus(status.getStatus());
		
		if(notificationDetails.getNotificationType().equalsIgnoreCase(NotificationTypes.MAIL.getType())){
			document.setEmailContent(((MailDetails) notificationDetails).getBodyText());
			document.setSubject(((MailDetails) notificationDetails).getSubject());
			document.setTo(((MailDetails) notificationDetails).getToAddress());
			document.setCc(((MailDetails) notificationDetails).getCcAddress());
			document.setFrom(((MailDetails) notificationDetails).getFromAddress());
		}
		
		if(notificationDetails.getNotificationType().equalsIgnoreCase(NotificationTypes.SMS.getType())){
			document.setMobile(((SMSDetails) notificationDetails).getMobile());
		}
	}
	
	@Override
    public boolean isNotificationEnabled(final String tenantCode) throws SystemException, BusinessException {
    	boolean enabled = false;
    	final TenantInfo tenantInfo = (TenantInfo)cacheRegistry.getMap(FrameworkConstant.TENANT_MAP).get(tenantCode);		    	  
    	
    	if (Boolean.valueOf(tenantInfo.getTenantConfigsMap().get(NotificationConstants.EMAIL_NOTIFICATIONS_ENABLED))) {
    		enabled = true;
    	} 
    	
    	if (enabled) {
    		LOGGER.info("Notification is enabled");
    	} else {
    		LOGGER.info("Notification is disabled");
    	}
    	
    	return enabled;
    }
	
	@Override
	public String getSuperAdminEmailId(final NotificationDao mysqlDao) throws SystemException, BusinessException {
		return mysqlDao.getSuperAdminToAddresses();
	}
	
	@Override
	public boolean isSystemEvent(final NotificationDao mysqlDao, final String eventName) throws SystemException, BusinessException {
		final NotificationEvent event = mysqlDao.getNotificationEvent(eventName);	
		LOGGER.info(event.toString());
		return NotificationClassification.isSystemClassification(event.getClassification());
	}
	
	@Override
	public boolean canMailbeSend(final NotificationDao mysqlDao, final String eventName, final String tenantCode) throws SystemException, BusinessException {
		boolean isSystemEvent = isSystemEvent(mysqlDao, eventName);
		
		boolean flag = false;
		if (isSystemEvent) {
			LOGGER.info("{} is system event", eventName);
			flag = true;
		} else {
			LOGGER.info("{} is feature event", eventName);
			flag = isNotificationEnabled(tenantCode);
		}
		
		return flag;
	}
	
}