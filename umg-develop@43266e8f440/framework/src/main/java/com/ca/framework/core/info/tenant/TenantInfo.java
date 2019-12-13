package com.ca.framework.core.info.tenant;

import java.util.Map;
import java.util.Set;

import com.ca.framework.core.info.BaseInfo;

/**
 * 
 * @author kamathan
 * @version 1.0
 *
 */
@SuppressWarnings("PMD")
public class TenantInfo extends BaseInfo {
    private static final long serialVersionUID = -5733489468117897659L;

    private String code;
    private String name;
    private String description;
    private String tenantType;
    private Set<AddressInfo> addresses;
    private Set<TenantConfigInfo> tenantConfigs;
    private Map<String, String> tenantConfigsMap;
    private Set<AuthTokenInfo> authTokens;
    private String activeAuthToken;
    private String activeUntil;
    private String resetReason;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public String getTenantType() {
        return tenantType;
    }

    public void setTenantType(String tenantType) {
        this.tenantType = tenantType;
    }

    public Set<AddressInfo> getAddresses() {
        return addresses;
    }

    public void setAddresses(Set<AddressInfo> addresses) {
        this.addresses = addresses;
    }

    public Set<TenantConfigInfo> getTenantConfigs() {
        return tenantConfigs;
    }

    public void setTenantConfigs(Set<TenantConfigInfo> tenantConfigs) {
        this.tenantConfigs = tenantConfigs;
    }

    public String getResetReason() {
        return resetReason;
    }

    public void setResetReason(String resetReason) {
        this.resetReason = resetReason;
    }

    public Map<String, String> getTenantConfigsMap() {
        return tenantConfigsMap;
    }

    public void setTenantConfigsMap(Map<String, String> tenantConfigsMap) {
        this.tenantConfigsMap = tenantConfigsMap;
    }

    public Set<AuthTokenInfo> getAuthTokens() {
        return authTokens;
    }

    public void setAuthTokens(Set<AuthTokenInfo> authTokens) {
        this.authTokens = authTokens;
    }

	public String getActiveAuthToken() {
		return activeAuthToken;
	}

	public void setActiveAuthToken(String activeAuthToken) {
		this.activeAuthToken = activeAuthToken;
	}

	public String getActiveUntil() {
		return activeUntil;
	}

	public void setActiveUntil(String activeUntil) {
		this.activeUntil = activeUntil;
	}


}