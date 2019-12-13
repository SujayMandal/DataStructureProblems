package com.ca.umg.notification;

//import static com.ca.umg.notification.model.NotificationClassification.SYSTEM;
import static com.ca.umg.notification.model.NotificationTypes.MAIL;
import static com.ca.umg.notification.model.NotificationTypes.SMS;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.notification.dao.NotificationDao;
import com.ca.umg.notification.model.MailDetails;
import com.ca.umg.notification.model.NotificationAttachment;
import com.ca.umg.notification.model.NotificationDetails;
import com.ca.umg.notification.model.NotificationAuditDocument;
import com.ca.umg.notification.model.NotificationEvent;
import com.ca.umg.notification.model.NotificationEventData;
import com.ca.umg.notification.model.NotificationStatus;
import com.ca.umg.notification.model.SMSDetails;

public class NotificationTask implements Callable<Map<String, NotificationStatus>> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationTask.class);
	
	private final NotificationDao mysqlDao;	
	
	private final NotificationEvent event;
	
	private final NotificationServiceBO bo;
	
	private final NotificationEventData eventData;
	
	private final List<NotificationAttachment> attachments;
	
	private final NotificationAuditDocument document;
		
	public NotificationTask(final NotificationEvent event, final NotificationDao mysqlDao, final NotificationServiceBO bo, final NotificationEventData eventData,
			final NotificationAuditDocument document) {
		this.event = event;
		this.mysqlDao = mysqlDao;
		this.bo = bo;
		this.eventData = eventData;
		this.attachments = null;
		this.document = document;
	}
	
	public NotificationTask(final NotificationEvent event, final NotificationDao mysqlDao, final NotificationServiceBO bo, final NotificationEventData eventData, 
			final List<NotificationAttachment> attachments, final NotificationAuditDocument document) {
		this.event = event;
		this.mysqlDao = mysqlDao;
		this.bo = bo;
		this.eventData = eventData;
		this.attachments = attachments;
		this.document = document;
	}

	@Override
	public Map<String, NotificationStatus> call() throws Exception {
		return executeTask();
	}	
	
	public Map<String, NotificationStatus> executeTask() throws BusinessException, SystemException {
		final Map<String, NotificationStatus> statusMap = new HashMap<>();
		
		try {
			final List<NotificationDetails> notificationDetails = mysqlDao.getNotificationDetails(event, eventData);
			if (notificationDetails != null) {
				LOGGER.info("Got Notification Details, No of notification details are : {}", notificationDetails.size());
				NotificationStatus status;
				
				for (final NotificationDetails nd : notificationDetails) {
					status = NotificationStatus.FAILED;
					if (nd.getNotificationType().equalsIgnoreCase(MAIL.getType())) {
						try {
							LOGGER.info("Initiated sending mail");
							status = sendMail(nd);
						} finally {
							bo.saveNotification(document, nd, status);
						}
						
						statusMap.put(nd.getId(), status);
						continue;
					}
					
					if (nd.getNotificationType().equalsIgnoreCase(SMS.getType())) {
						try {
							LOGGER.info("Initiated sending SMS");
							status = bo.sendSMS((SMSDetails) nd, eventData);				
						} finally {
							bo.saveNotification(document, nd, status);
						}
						
						statusMap.put(nd.getId(), status);
						continue;
					}
				}	
			}			
		} catch (Exception e) {
			LOGGER.error("while sending notifications, got error, Error is {}", e.getMessage());
			throw e;
		}
		
		return statusMap;		
	}
	
	private NotificationStatus sendMail(final NotificationDetails nd) throws SystemException, BusinessException {
		final NotificationStatus status;
		
		final MailDetails md = (MailDetails) nd;
//		setSuperAdminEmailId(md);
		
		if (attachments != null && !attachments.isEmpty()) {
			status = bo.sendMailWithAttachments(md, eventData, attachments);
		} else {
			status = bo.sendMail(md, eventData);
		}
		
		return status;
	}
	
	/*private void setSuperAdminEmailId(final MailDetails md) throws SystemException, BusinessException {
		if (SYSTEM == md.getClassiffication()) {
			md.setToAddress(bo.getSuperAdminEmailId(mysqlDao));
		}
	}*/
}