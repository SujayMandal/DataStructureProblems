package com.ca.framework.event;

import java.io.Serializable;

/**
 * @author mandasuj
 *
 */


public class TenantBulkPollingEvent implements Serializable {

  	private static final long serialVersionUID = -616409205217472731L;
  	
  	public static final String TENANT_BULK_POLLING_ENABLE_EVENT = "TENANT_BULK_POLLING_ENABLE_EVENT";
  	
  	public static final String TENANT_BULK_POLLING_DISABLE_EVENT = "TENANT_BULK_POLLING_DISABLE_EVENT";

  	private String tenantCode;
  	
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
}
