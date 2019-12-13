/**
 * 
 */

package com.ca.framework.core.task;

import java.util.Properties;
import java.util.concurrent.Callable;

import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;

/**
 * @author kamathan
 * @param <V>
 *
 */
public abstract class AbstractCallableTask<V> implements Callable<V> {

    private final String tenantCode;

    protected AbstractCallableTask(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    @Override
    public abstract V call() throws SystemException;

    protected void setRequestContext() {
        Properties properties = new Properties();
        properties.put(RequestContext.TENANT_CODE, tenantCode);
        new RequestContext(properties);
    }

    protected void destroyRequestContext() {
        RequestContext.getRequestContext().destroy();
    }

    public String getTenantCode() {
        return tenantCode;
    }
}
