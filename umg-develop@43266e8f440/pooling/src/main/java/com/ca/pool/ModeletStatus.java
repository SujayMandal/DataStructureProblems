package com.ca.pool;

import java.io.Serializable;

public enum ModeletStatus implements Serializable {

	REGISTERED("Free"),
	UNREGISTERED("Unavailable"),
	REGISTRATION_INPROGRESS("Registration Inprogress"),
	BUSY("Busy"),
	FAILED("Failed"),
	STARTED("Started"),
	STOPPED("Stopped"),
	REGISTERED_WITH_SYSTEM_DEFAULT_POOL("Registered With System Default Pool");
	
	private final String status;
	
	private ModeletStatus(final String status) {
		this.status = status;
	}
	
	public String getStatus() {
		return status;
	}
	
	public static ModeletStatus getModeletStatus(final String status) {
		ModeletStatus modeletStatus = null;
		
		if (status.equalsIgnoreCase(REGISTERED.getStatus())) {
			modeletStatus = REGISTERED;
		} else if (status.equalsIgnoreCase(BUSY.getStatus())) {
			modeletStatus = BUSY;
		} else if (status.equalsIgnoreCase(FAILED.getStatus())) {
			modeletStatus = FAILED;
		} else if (status.equalsIgnoreCase(REGISTRATION_INPROGRESS.getStatus())) {
			modeletStatus = REGISTRATION_INPROGRESS;
		} else if (status.equalsIgnoreCase(REGISTERED_WITH_SYSTEM_DEFAULT_POOL.getStatus())) {
			modeletStatus = REGISTERED_WITH_SYSTEM_DEFAULT_POOL;
		} else {
            modeletStatus = UNREGISTERED;
        } 
		
		return modeletStatus;
	}
}