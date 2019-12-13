package com.ca.umg.notification.dao;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.notification.model.NotificationAuditDocument;

public interface NotificationMongoDao {

	public void saveNotificationDocument(final NotificationAuditDocument notificationDocument) throws SystemException, BusinessException;
}
