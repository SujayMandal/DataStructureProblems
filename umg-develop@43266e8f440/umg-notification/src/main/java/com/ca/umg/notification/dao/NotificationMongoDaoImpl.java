package com.ca.umg.notification.dao;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.data.mongodb.core.MongoTemplate;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.notification.NotificationConstants;
import com.ca.umg.notification.model.NotificationAuditDocument;

@Named
public class NotificationMongoDaoImpl implements NotificationMongoDao {

	@Inject
    private MongoTemplate mongoTemplate;
	
	@Override
	public void saveNotificationDocument(final NotificationAuditDocument notificationDocument) throws SystemException, BusinessException{
		mongoTemplate.insert(notificationDocument, NotificationConstants.NOTIFICATION_DOCUMENTS);
	}
}
