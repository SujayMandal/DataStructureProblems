package com.ca.umg.notification;

public enum NotificationExceptionCodes {

	TEMPLATE_NOT_AVAILABLE("NF0000001", "Template is not avaiable"), //
	
	MAIL_SENDING_FAILED("NF0000002", "Mail Sending Failed"), //
	
	SMS_SENDING_FAILED("NF0000003", "SMS Sending Failed"), //
	
	SAVING_INTO_MONGO_FAILED("NF0000004", "Saving into Mongo Failed"), //
	
	TEMPLATE_CONVERT_FAILED("NF0000005", "Template conversion is failed"), //
	
	NO_SUPER_ADMIN("NF0000006", "There is no super admin in system"), 
	
	EVENT_NOOT_FOUND("NF0000007", "Notification Event is found"),
	
    FIND_ID_FAILED("NF0000013", "Finding Id is Failed"),
	
	NO_FEATURE_NOTIFICATION("NF0000008", "There is no feature notification in system"),
	
	FIND_TYPE_FAILED("NF0000009", "Find Type id failed"),
	
	NO_MAPPING_DATA("NF0000010", "There is no mapping data in system"),
	
	DUPLICATE_MAPPING_FAILED("NF0000011", "Duplicate mapping check failed"),
	
	NO_EVENT_DETAIL("NF0000012", "There is no event data in system");
	
	private final String code;
	private final String description;
	
	private NotificationExceptionCodes(final String code, final String description) {
		this.code = code;
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}
	
	@Override
	public String toString() {
		return code + " : " + description;
	}	
}