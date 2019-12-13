package com.ca.umg.notification.model;

import java.io.Serializable;
import java.util.Arrays;

public class NotificationSMSTemplate implements Serializable {
	
	private static final long serialVersionUID = 1l;
	
	private String id;
    
	private String name;
    
	private String description;
    
	private byte[] smsDefinition;
    
	private int isActive;
    
	private int majorVersion;
	
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

	public byte[] getSMSDefinition() {
		return smsDefinition != null ? Arrays.copyOf(smsDefinition, smsDefinition.length) : null;
	}

	public void setSMSDefinition(byte[] templateDefinition) {
		if (templateDefinition != null) {
            this.smsDefinition = Arrays.copyOf(templateDefinition, templateDefinition.length);
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
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		
		sb.append("Template Name : " + name);
		sb.append("Template Version : " + majorVersion);
		
		return sb.toString();
	}
}
