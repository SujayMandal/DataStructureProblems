package com.ca.umg.notification.dao;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;

public class NotificationMongoDaoImplTest {

	@Test
	public void saveNotificationDocumentTest() throws SystemException, BusinessException {
		NotificationMongoDao dao = new NotificationMongoDaoImpl();
		//NotificationDocument document = new NotificationDocument();
		//dao.saveNotificationDocument(document);
		//assertNotNull(document);
	}

}
