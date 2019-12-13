package com.ca.framework.core.info.tenant;

import com.ca.framework.core.info.BaseInfo;

public class AuthTokenInfo extends BaseInfo {
    /**
     * dsefault serialVersionId
     */
    private static final long serialVersionUID = 1L;


    private String authCode;

    private TenantInfo tenantInfo;

   
    private Long activeFrom;


    private Long activeUntil;


    private String status;

    private String comment;
    
    
    private String activeFromStr;
    
    
	private String activeUntilStr;

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public TenantInfo getTenantInfo() {
        return tenantInfo;
    }

    public void setTenantInfo(TenantInfo tenantInfo) {
        this.tenantInfo = tenantInfo;
    }

    public Long getActiveFrom() {
        return activeFrom;
    }

    public void setActiveFrom(Long activeFrom) {
        this.activeFrom = activeFrom;
    }

    public Long getActiveUntil() {
        return activeUntil;
    }

    public void setActiveUntil(Long activeUntil) {
        this.activeUntil = activeUntil;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public String getActiveUntilStr() {
		return activeUntilStr;
	}

	public void setActiveUntilStr(String activeUntilStr) {
		this.activeUntilStr = activeUntilStr;
	}

	public String getActiveFromStr() {
		return activeFromStr;
	}

	public void setActiveFromStr(String activeFromStr) {
		this.activeFromStr = activeFromStr;
	}


}
