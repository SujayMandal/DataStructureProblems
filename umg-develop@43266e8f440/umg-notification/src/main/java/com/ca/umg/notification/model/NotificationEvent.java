package com.ca.umg.notification.model;

import java.io.Serializable;

public class NotificationEvent implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String id;
	
	private String name;
	
	private String description;
	
	private String classification;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
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

	public String getClassification() {
		return classification;
	}

	public void setClassification(final String classification) {
		this.classification = classification;
	}
	
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		
		sb.append("Name : " + getName());
		sb.append(", Classification : " + getClassification());
		
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean flag = false;
		if (obj instanceof NotificationEvent) {
			flag = this.getName().equals(((NotificationEvent) obj).getName());
		}
		
		return flag;
	}
}