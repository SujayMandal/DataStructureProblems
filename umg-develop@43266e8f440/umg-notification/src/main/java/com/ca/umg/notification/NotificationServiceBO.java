package com.ca.umg.notification;

import java.util.List;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.notification.dao.NotificationDao;
import com.ca.umg.notification.model.MailDetails;
import com.ca.umg.notification.model.NotificationAttachment;
import com.ca.umg.notification.model.NotificationAuditDocument;
import com.ca.umg.notification.model.NotificationDetails;
import com.ca.umg.notification.model.NotificationEventData;
import com.ca.umg.notification.model.NotificationStatus;
import com.ca.umg.notification.model.SMSDetails;

public interface NotificationServiceBO {

	public NotificationStatus sendMail(final MailDetails mailDetails, final NotificationEventData eventData) throws SystemException, BusinessException;
	
	public NotificationStatus sendMailWithAttachments(final MailDetails mailDetails, final NotificationEventData eventData, final List<NotificationAttachment> attachments) 
			throws SystemException, BusinessException;
	
	public NotificationStatus sendSMS(final SMSDetails smsDetails, final NotificationEventData eventData) throws SystemException, BusinessException;

	public void saveNotification(final NotificationAuditDocument document, final NotificationDetails notificationDetails, final NotificationStatus status) 
			throws SystemException, BusinessException;
	
	public boolean isNotificationEnabled(final String tenantCode) throws SystemException, BusinessException;
	
	public String getSuperAdminEmailId(final NotificationDao mysqlDao) throws SystemException, BusinessException;
	
	public boolean isSystemEvent(final NotificationDao mysqlDao, final String eventName) throws SystemException, BusinessException;
	
	public boolean canMailbeSend(final NotificationDao mysqlDao, final String eventName, final String tenantCode) throws SystemException, BusinessException;
}