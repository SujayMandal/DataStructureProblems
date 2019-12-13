/**
 * 
 */
package com.ca.framework.core.info;

import java.io.Serializable;

/**
 * This class holds tenant details such as runtime base url, tenant code and encrypted auth key
 * 
 * @author kamathan
 *
 */
public class TenantData implements Serializable {

    private static final long serialVersionUID = 8441728045125184931L;

    private String runtimeBaseUrl;

    private String tenantCode;

    private String authToken;
    
    private String tenantName;

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getRuntimeBaseUrl() {
        return runtimeBaseUrl;
    }

    public void setRuntimeBaseUrl(String runtimeBaseUrl) {
        this.runtimeBaseUrl = runtimeBaseUrl;
    }

	public String getTenantName() {
		return tenantName;
	}

	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}
}
