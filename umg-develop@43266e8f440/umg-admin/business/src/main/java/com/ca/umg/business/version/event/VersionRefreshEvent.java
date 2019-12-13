/**
 * 
 */
package com.ca.umg.business.version.event;

import java.io.Serializable;

/**
 * @author kamathan
 * @param <T>
 *
 */
public class VersionRefreshEvent<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String REFRESH_VERSION = "REFRESH_VERSION";

    private String event;

    private T data;

    private String tenantCode;

    private String operation;

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

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }
}
