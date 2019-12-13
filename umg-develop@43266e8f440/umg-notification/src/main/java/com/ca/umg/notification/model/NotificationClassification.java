package com.ca.umg.notification.model;

public enum NotificationClassification {

	SYSTEM("System"), //
	FEATURE("Feature");
	
	private String classification;
	
	private NotificationClassification(final String classification) {
		this. classification = classification;
	}
	
	public String getClassification() {
		return classification;
	}
	
	public static NotificationClassification of(final String classification) {
		NotificationClassification notificationClassification = null;
		
		for (final NotificationClassification nc : values()) {
			if (nc.classification.equalsIgnoreCase(classification)) {
				notificationClassification = nc;
				break;
			}
		}
		
		if (notificationClassification == null) {
			throw new IllegalArgumentException("Notification Classification is not found for value : " + classification);
		}
		return notificationClassification;
	}
	
	public static boolean isSystemClassification(final String classification) {
		return SYSTEM.getClassification().equalsIgnoreCase(classification);
	}
	
	public static boolean isFeatureClassification(final String classification) {
		return FEATURE.getClassification().equalsIgnoreCase(classification);
	}
}
