/**
 * 
 */
package com.ca.framework.event;

import java.io.Serializable;

/**
 * @author mandasuj
 *
 */
public class BulkFilePollingEvent implements Serializable {
	
	private static final long serialVersionUID = -616409205217872731L;

	public static final String BULK_FILE_ADDED_EVENT = "BULK_FILE_ADDED_EVENT";
  	
  	private String tenantCode;
  	
  	private String fileName;
  	
  	private String event;

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
}
