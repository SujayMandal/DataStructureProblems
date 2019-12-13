package com.ca.umg.notification.model;

import java.io.Serializable;
import java.util.Map;

public class NotificationEventData implements Serializable {

	public static final long serialVersionUID = 1L;
	
	private NotificationHeaders notificationHeaders;
	
	private NotificationAdditionalDetails additionalDetails;
	
	private Map<String, Object> subjectMap;
	
	private Map<String, Object> bodyMap;

	public NotificationHeaders getNotificationHeaders() {
		return notificationHeaders;
	}

	public void setNotificationHeaders(final NotificationHeaders notificationHeaders) {
		this.notificationHeaders = notificationHeaders;
	}
	
	public void setAdditionalDetails(final NotificationAdditionalDetails additionalDetails) {
		this.additionalDetails = additionalDetails;
	}
	
	public NotificationAdditionalDetails getAdditionalDetails() {
		return this.additionalDetails;
	}

	public Map<String, Object> getSubjectMap() {
		return subjectMap;
	}

	public void setSubjectMap(Map<String, Object> subjectMap) {
		this.subjectMap = subjectMap;
	}

	public Map<String, Object> getBodyMap() {
		return bodyMap;
	}

	public void setBodyMap(Map<String, Object> bodyMap) {
		this.bodyMap = bodyMap;
	}

}
