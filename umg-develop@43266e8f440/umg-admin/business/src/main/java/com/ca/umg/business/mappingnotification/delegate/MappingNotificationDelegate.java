package com.ca.umg.business.mappingnotification.delegate;

import java.util.List;
import java.util.Set;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.mappingnotification.entity.MappingNotification;
import com.ca.umg.business.mappingnotification.entity.NotificationData;
import com.ca.umg.notification.model.MailDetails;

public interface MappingNotificationDelegate {

	public void createMapping(final MappingNotification nd) throws BusinessException,SystemException;
	
	public void updateMapping(final MappingNotification nd)throws BusinessException,SystemException;
	
	public void deleteMapping(final String mappingId)throws BusinessException,SystemException;
	
    public MailDetails getMappingDataByMappingId(final String mappingID)throws BusinessException,SystemException;
    
    public List<NotificationData> getAllFeatureNotification()throws BusinessException,SystemException;
    
    public boolean isDuplicateMapping(final String tenantId , final String eventId)throws BusinessException,SystemException;
    
    public List<NotificationData> getAllEventDetails()throws BusinessException,SystemException;

    public String getNotificationEventId(String tableName, String notificationName) throws BusinessException,SystemException;
    
    public Set<String> getAllModels()throws BusinessException,SystemException;

    public MailDetails getTemplateDataByEventId(final String eventID)throws BusinessException,SystemException;
}
