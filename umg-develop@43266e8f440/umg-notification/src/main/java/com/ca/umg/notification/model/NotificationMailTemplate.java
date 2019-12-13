package com.ca.umg.notification.model;

import java.io.Serializable;
import java.util.Arrays;

public class NotificationMailTemplate implements Serializable {

	private static final long serialVersionUID = 1l;
	
	private String id;
    
	private String name;
    
	private String description;
    
	private byte[] bodyDefinition;
    
	private byte[] subjectDefinition;
    
	private int isActive;
    
	private int majorVersion;
    
	private String mailContentType;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public byte[] getBodyDefinition() {
		return bodyDefinition != null ? Arrays.copyOf(bodyDefinition, bodyDefinition.length) : null;
	}

	public void setBodyDefinition(byte[] templateDefinition) {
		if (templateDefinition != null) {
            this.bodyDefinition = Arrays.copyOf(templateDefinition, templateDefinition.length);
        }
	}

	public byte[] getSubjectDefinition() {
		return subjectDefinition != null ? Arrays.copyOf(subjectDefinition, subjectDefinition.length) : null;
	}

	public void setSubjectDefinition(byte[] subjectDefinition) {
		if (subjectDefinition != null) {
            this.subjectDefinition = Arrays.copyOf(subjectDefinition, subjectDefinition.length);
        }
	}

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	public int getMajorVersion() {
		return majorVersion;
	}

	public void setMajorVersion(int majorVersion) {
		this.majorVersion = majorVersion;
	}

	public String getMailContentType() {
		return mailContentType;
	}

	public void setMailContentType(String mailContentType) {
		this.mailContentType = mailContentType;
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		
		sb.append("Template Name : " + name);
		sb.append("Mail Subject : " + subjectDefinition);	
		sb.append("Template Version : " + majorVersion);
		sb.append("Mail Content Type :" + mailContentType);
		
		return sb.toString();
	}
}
