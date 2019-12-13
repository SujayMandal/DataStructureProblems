package com.ca.umg.notification;

import static com.ca.umg.notification.model.NotificationStatus.NOTIFICATION_DISABLED;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.notification.dao.NotificationDao;
import com.ca.umg.notification.dao.NotificationMongoDao;
import com.ca.umg.notification.model.NotificationAttachment;
import com.ca.umg.notification.model.NotificationAuditDocument;
import com.ca.umg.notification.model.NotificationEvent;
import com.ca.umg.notification.model.NotificationEventData;
import com.ca.umg.notification.model.NotificationStatus;

@Component
public class NotificationServiceImpl implements NotificationService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceImpl.class);

	private final ExecutorService executorService;
	
	private final static int NO_OF_THREADS = 10;
	
	@Inject
	private NotificationDao mysqlDao;
	
	@Inject
	private NotificationMongoDao mongoDao;
	
	@Inject
	private NotificationServiceBO bo;
	
	public NotificationServiceImpl() {
		LOGGER.info("No of threads : {}", NO_OF_THREADS);
		executorService = Executors.newFixedThreadPool(NO_OF_THREADS); 
	}
	
	@Override
	public Map<String, NotificationStatus> sendNotification(final NotificationEvent event, final NotificationEventData eventData, final boolean async,
			final NotificationAuditDocument document) throws SystemException, BusinessException {
		LOGGER.info("Send Notification is called, Event is : {} ", event);
		
		Map<String, NotificationStatus> statusMap;

		if (bo.canMailbeSend(mysqlDao, event.getName(), eventData.getNotificationHeaders().getTenantCode())) {			
			LOGGER.info("Mail can be sentEmail Notification is enabled, Event is : {} ", event.getName());
			if (async) {
				statusMap = submitTaskAsynchronously(event, eventData, document);
			} else {
				statusMap = submitTaskSynchronously(event, eventData, document);
			}
		} else {
			LOGGER.info("Email can not be sent, Event is : {} ", event);
			statusMap = getDisabledStatusMap();
		}
		
		return statusMap;
	}

	@Override
	public Map<String, NotificationStatus> sendMailWithAttachments(final NotificationEvent event, final NotificationEventData eventData, 
			final List<NotificationAttachment> attachments, final boolean async, final NotificationAuditDocument document) throws SystemException, BusinessException {
		LOGGER.info("Send Mail With Attachment is called, Event is : {} ", event);
		
		Map<String, NotificationStatus> statusMap;
		
		if (bo.canMailbeSend(mysqlDao, event.getName(), eventData.getNotificationHeaders().getTenantCode())) {	
			LOGGER.info("Email Notification is enabled, Event is : {} ", event.getName());
			if (async) {
				statusMap = submitTaskAsynchronously(event, eventData, attachments, document);
			} else {
				statusMap = submitTaskSynchronously(event, eventData, attachments, document);
			}			
		} else {
			LOGGER.info("Email can not be sent, Event is : {} ", event);
			statusMap = getDisabledStatusMap();
		}
		return statusMap;
	}
	
	
	private Map<String, NotificationStatus> submitTaskSynchronously(final NotificationEvent event, final NotificationEventData eventData,
			final NotificationAuditDocument document) throws SystemException, BusinessException {
		LOGGER.info("Task is submitting for sending notifcation, It is being executed by synchronously, Event is : {} ", event);
		final NotificationTask task = new NotificationTask(event, mysqlDao, bo, eventData, document);		
		return task.executeTask();		
	}
	
	private Map<String, NotificationStatus> submitTaskAsynchronously(final NotificationEvent event, final NotificationEventData eventData, 
			final NotificationAuditDocument document) throws SystemException, BusinessException {
		LOGGER.info("Task is submitting for sending notifcation, Event is : {} ", event);
		final NotificationTask task = new NotificationTask(event, mysqlDao, bo, eventData, document);
		executorService.submit(task);	
		
		final Map<String, NotificationStatus> statusMap = new HashMap<>();
		
		statusMap.put("key", NotificationStatus.INITIATED);
		
		return statusMap;
	}

	private Map<String, NotificationStatus> submitTaskSynchronously(final NotificationEvent event, final NotificationEventData eventData, 
			final List<NotificationAttachment> attachments, final NotificationAuditDocument document) throws SystemException, BusinessException {
		LOGGER.info("Task is submitting for sending notifcation, It is being executed by synchronously, Event is : {} ", event);
		final NotificationTask task = new NotificationTask(event, mysqlDao, bo, eventData, attachments, document);		
		return task.executeTask();		
	}
	
	private Map<String, NotificationStatus> submitTaskAsynchronously(final NotificationEvent event, final NotificationEventData eventData, 
			final List<NotificationAttachment> attachments, final NotificationAuditDocument document) throws SystemException, BusinessException {
		LOGGER.info("Task is submitting for sending notifcation, Event is : {} ", event);
		final NotificationTask task = new NotificationTask(event, mysqlDao, bo, eventData, attachments, document);
		executorService.submit(task);	
		
		final Map<String, NotificationStatus> statusMap = new HashMap<>();
		
		statusMap.put("key", NotificationStatus.INITIATED);
		
		return statusMap;
	}

	
	public void setMysqlDao(final NotificationDao mysqlDao) {
		this.mysqlDao = mysqlDao;
	}
	
	public NotificationDao getMysqlDao() {
		return mysqlDao;
	}
	
	public void setMoongoDao(final NotificationMongoDao mongoDao) {
		this.mongoDao = mongoDao;
	}
	
	public NotificationMongoDao getMongoDao() {
		return mongoDao;
	}
	
	public NotificationServiceBO getBo() {
		return bo;
	}
	
	public void getBo(final NotificationServiceBO bo) {
		this.bo = bo;
	}
	
	private Map<String, NotificationStatus> getDisabledStatusMap() {
		final Map<String, NotificationStatus> map = new HashMap<String, NotificationStatus>();
		map.put("key", NOTIFICATION_DISABLED);
		return map;
	}
}