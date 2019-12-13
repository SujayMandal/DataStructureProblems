/**
 * 
 */
package com.ca.umg.business.mid.extraction.info;

import java.io.Serializable;
import java.util.List;

/**
 * @author kamathan
 *
 */
public class TenantIODefinition implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 3294402836427581867L;

    private List<TidParamInfo> tenantInputs;

    private List<TidParamInfo> tenantOutputs;

    public List<TidParamInfo> getTenantInputs() {
        return tenantInputs;
    }

    public void setTenantInputs(List<TidParamInfo> tenantInputs) {
        this.tenantInputs = tenantInputs;
    }

    public List<TidParamInfo> getTenantOutputs() {
        return tenantOutputs;
    }

    public void setTenantOutputs(List<TidParamInfo> tenantOutputs) {
        this.tenantOutputs = tenantOutputs;
    }
}
