package com.ca.umg.rt.core.flow.entity;

import java.io.Serializable;

public class TenantModelIO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 7810378563448028654L;

    private String tenantInput;
    private String tenantOutput;
    private String modelInput;
    private String modelOutput;

    public String getTenantInput() {
        return tenantInput;
    }

    public void setTenantInput(String tenantInput) {
        this.tenantInput = tenantInput;
    }

    public String getTenantOutput() {
        return tenantOutput;
    }

    public void setTenantOutput(String tenantOutput) {
        this.tenantOutput = tenantOutput;
    }

    public String getModelInput() {
        return modelInput;
    }

    public void setModelInput(String modelInput) {
        this.modelInput = modelInput;
    }

    public String getModelOutput() {
        return modelOutput;
    }

    public void setModelOutput(String modelOutput) {
        this.modelOutput = modelOutput;
    }

}
