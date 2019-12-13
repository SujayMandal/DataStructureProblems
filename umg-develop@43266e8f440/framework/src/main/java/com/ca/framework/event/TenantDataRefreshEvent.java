package com.ca.framework.event;

import java.io.Serializable;

/**
 * @author mandasuj
 *
 */

public class TenantDataRefreshEvent implements Serializable {

	  	private static final long serialVersionUID = -616409205217472731L;
	  	
	  	public static final String REFRESH_TENANT_CONFIG_EVENT = "REFRESH_TENANT_CONFIG_EVENT";

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
