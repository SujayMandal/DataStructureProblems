package com.ca.umg.notification;

import javax.inject.Inject;
import javax.inject.Named;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.notification.dao.NotificationMongoDao;
import com.ca.umg.notification.model.NotificationAuditDocument;

@Named
public class NotificationMongoAudit implements NotificationAudit {
	
	@Inject
	private NotificationMongoDao mongoDao;
	
	@Override
	public void saveNotification(final NotificationAuditDocument document) throws SystemException, BusinessException {
		mongoDao.saveNotificationDocument(document);
	}
}
