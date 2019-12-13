package com.ca.umg.notification;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.notification.model.NotificationAuditDocument;

public interface NotificationAudit {

	public void saveNotification(final NotificationAuditDocument document) throws SystemException, BusinessException;
}
