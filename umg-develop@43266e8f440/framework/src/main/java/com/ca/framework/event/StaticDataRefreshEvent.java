/**
 * 
 */

package com.ca.framework.event;

import java.io.Serializable;

/**
 * @author kamathan
 * @param <T>
 *
 */
public class StaticDataRefreshEvent<T> implements Serializable {

    private static final long serialVersionUID = -616409205217472731L;

    public static final String REFRESH_PACKAGE_NAMES = "REFRESH_PACKAGE_NAMES";

    public static final String REFRESH_VERSION_EXC_ENV_MAP = "REFRESH_VERSION_EXC_ENV_MAP";

    public static final String REFRESH_SUPPORT_PACKAGES_EVENT = "REFRESH_SUPPORT_PACKAGES_EVENT";

    public static final String REFRESH_ONLY_TENANT_EVENT = "REFRESH_ONLY_TENANT_EVENT";

    private T data;

    private String tenantCode;

    private String operation;

    private String event;

    /**
     * The key to read version details from the cache.
     * 
     * This is a combination of {version name}-{major version}-{minor version}.
     */
    private String versionKey;

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public String getVersionKey() {
        return versionKey;
    }

    public void setVersionKey(String versionKey) {
        this.versionKey = versionKey;
    }

}
