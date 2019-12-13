package com.ca.umg.notification.model;

import java.io.Serializable;

public class NotificationType implements Serializable {

	private static final long serialVersionUID = 1l;
	
	private String id;
	
    private String type;
    
    private String description;
    
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
    
	@Override
	public String toString() {
		return type;
	}
}
