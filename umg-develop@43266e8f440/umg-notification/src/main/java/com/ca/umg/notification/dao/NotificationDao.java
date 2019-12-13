package com.ca.umg.notification.dao;

import java.util.List;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.notification.model.NotificationDetails;
import com.ca.umg.notification.model.NotificationEvent;
import com.ca.umg.notification.model.NotificationEventData;

public interface NotificationDao {

	public List<NotificationDetails> getNotificationDetails(final NotificationEvent event, final NotificationEventData eventData) throws BusinessException, SystemException;
	
	public String getToAddresses(final String eventName, final String tenantCode) throws BusinessException, SystemException;

	public String getSuperAdminToAddresses() throws BusinessException, SystemException;
	
	public NotificationEvent getNotificationEvent(final String eventName) throws BusinessException, SystemException;

	String getTenantCode(String eventName) throws BusinessException, SystemException;
	
}
