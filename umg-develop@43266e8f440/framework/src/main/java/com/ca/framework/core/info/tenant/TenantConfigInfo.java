/**
 * 
 */
package com.ca.framework.core.info.tenant;

import com.ca.framework.core.info.BaseInfo;

/**
 * @author kamathan
 * @version 1.0
 */
public class TenantConfigInfo extends BaseInfo {

    private static final long serialVersionUID = 2347793548624667196L;

    private SystemKeyInfo systemKey;

    private String value;

    private TenantInfo tenantInfo;

    private String role;


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public TenantInfo getTenantInfo() {
        return tenantInfo;
    }

    public void setTenantInfo(TenantInfo tenantInfo) {
        this.tenantInfo = tenantInfo;
    }

    public SystemKeyInfo getSystemKey() {
        return systemKey;
    }

    public void setSystemKey(SystemKeyInfo systemKey) {
        this.systemKey = systemKey;
    }


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}
