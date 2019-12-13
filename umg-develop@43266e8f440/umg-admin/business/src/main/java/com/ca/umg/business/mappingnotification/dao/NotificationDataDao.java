package com.ca.umg.business.mappingnotification.dao;

import java.util.List;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.mappingnotification.entity.NotificationData;
import com.ca.umg.notification.model.MailDetails;

public interface NotificationDataDao {

	public String findId(String tableName , String name) throws SystemException;
	
	public String findTypeId(String tableName , String name) throws SystemException;
	
	public String getSuperAdminToAddresses() throws BusinessException, SystemException;
	
    public MailDetails getMappingDataByMappingId(final String mappingID)throws BusinessException,SystemException;
	
	public List<NotificationData> getAllFeatureNotification()throws BusinessException,SystemException; 
	
	public boolean isDuplicateMapping(final String tenantId , final String eventId)throws BusinessException,SystemException;
	
	public List<NotificationData> getAllEventDetails()throws BusinessException,SystemException;
	
	public MailDetails getTemplateDataByEventId(final String eventID)throws BusinessException,SystemException;
	
	public String getTemplateIdByEventId(final String eventID) throws BusinessException,SystemException;;
}
