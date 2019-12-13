package com.ca.umg.business.tenant.info;

import java.io.Serializable;
import java.util.List;

public class TenantIORequest implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private TenantReqResHeader tenantReqResHeader;
    
    private List<TenantReqResInfo> tenantReqRes;

    public TenantReqResHeader getTenantReqResHeader() {
        return tenantReqResHeader;
    }

    public void setTenantReqResHeader(TenantReqResHeader tenantRequestHeader) {
        this.tenantReqResHeader = tenantRequestHeader;
    }

    public List<TenantReqResInfo> getTenantReqResInfo() {
        return tenantReqRes;
    }

    public void setTenantReqResInfo(List<TenantReqResInfo> tenantIOList) {
        this.tenantReqRes = tenantIOList;
    }
    
}
