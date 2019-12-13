package com.ca.umg.notification;

import java.util.List;
import java.util.Map;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.notification.model.NotificationAttachment;
import com.ca.umg.notification.model.NotificationAuditDocument;
import com.ca.umg.notification.model.NotificationEvent;
import com.ca.umg.notification.model.NotificationEventData;
import com.ca.umg.notification.model.NotificationStatus;

public interface NotificationService {

	public Map<String, NotificationStatus> sendNotification(final NotificationEvent event, final NotificationEventData eventData, final boolean async, 
		final NotificationAuditDocument document) throws SystemException, BusinessException;
	
	public Map<String, NotificationStatus> sendMailWithAttachments(final NotificationEvent event, final NotificationEventData eventData, 			
		final List<NotificationAttachment> attachments, final boolean async, final NotificationAuditDocument document) throws SystemException, BusinessException;
}
