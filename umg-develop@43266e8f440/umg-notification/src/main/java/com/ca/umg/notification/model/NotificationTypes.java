package com.ca.umg.notification.model;

public enum NotificationTypes {

	MAIL("Mail"), //
	SMS("SMS");
	
	private final String type;
	
	private NotificationTypes(final String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	
	public static NotificationTypes getType(final String type) {
		NotificationTypes nt;
		if (MAIL.getType().equalsIgnoreCase(type)) {
			nt = MAIL;
		} else if (SMS.getType().equalsIgnoreCase(type)) {
			nt = SMS;
		} else {
			throw new IllegalArgumentException(type + "Type does not support" );
		}
		
		return nt;
	}
	
	@Override
	public String toString() {
		return type;
	}
}
