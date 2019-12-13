package com.ca.framework.core.requestcontext;

import java.io.Serializable;
import java.util.Properties;

public class RequestContext implements Serializable {

    public static final String TENANT_URL = "TENANT_URL".intern();
    public static final String TENANT_CODE = "TENANT_CODE".intern();

    private static final long serialVersionUID = 6802230342889527130L;

    private static ThreadLocal<RequestContext> context = new ThreadLocal<RequestContext>();

    private String tenantCode;

    private boolean syndicateAware=false;
    
    private boolean adminAware=false;
    
    public boolean isAdminAware() {
		return adminAware;
	}

	public void setAdminAware(boolean adminAware) {
		this.adminAware = adminAware;
	}

	public boolean isSyndicateAware() {
		return syndicateAware;
	}

	public void setSyndicateAware(boolean syndicateAware) {
		this.syndicateAware = syndicateAware;
	}

	public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public static RequestContext getRequestContext() {
        return context.get();
    }

    public RequestContext(Properties properties) {
        tenantCode = properties.getProperty(TENANT_CODE);
        context.set(this);
    }

    public void destroy() {
        context.remove();
        context.set(null);

    }

}
