/**
 * 
 */
package com.ca.pool.util;

import java.io.Serializable;

/**
 * @author kamathan
 *
 */
public class ModeletRegistrationEvent implements Serializable {

    private static final long serialVersionUID = -8997515170204068020L;

    private String modelName;

    private String majorVersion;

    private String tenantCode;

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getMajorVersion() {
        return majorVersion;
    }

    public void setMajorVersion(String majorVersion) {
        this.majorVersion = majorVersion;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

}
