package com.ca.umg.notification.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class NotificationDetails {

	private String id;
	
	private String notificationType;
	
	private long mailTriggerTimestamp;

	public String getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public long getMailTriggerTimestamp() {
		return mailTriggerTimestamp;
	}

	public void setMailTriggerTimestamp(long mailTriggerTimestamp) {
		this.mailTriggerTimestamp = mailTriggerTimestamp;
	}

	public static NotificationDetails of(final ResultSet rs) throws SQLException{
		final NotificationTypes type = NotificationTypes.getType(rs.getString("TYPE"));
		NotificationDetails notificationDetails = null;
		
		switch(type) {
			case MAIL :
				notificationDetails = MailDetails.of(rs);
				break;
			case SMS :
				notificationDetails = SMSDetails.of(rs);
				break;
		}
		
		return notificationDetails;
	}
}
