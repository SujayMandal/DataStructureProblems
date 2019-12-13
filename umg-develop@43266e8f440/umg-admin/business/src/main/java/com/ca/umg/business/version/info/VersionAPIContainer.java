package com.ca.umg.business.version.info;

public class VersionAPIContainer {
    
    private byte[] tenantInputSchema;
    
    private String tenantInputSchemaName;
    
    private byte[] tenantOutputSchema;
    
    private String tenantOutputSchemaName;
    
    private byte[] sampleTenantInputJson;

    public byte[] getTenantInputSchema() {
        return tenantInputSchema;
    }

    public void setTenantInputSchema(byte[] tenantInputSchema) {
        this.tenantInputSchema = tenantInputSchema;
    }

    public String getTenantInputSchemaName() {
        return tenantInputSchemaName;
    }

    public void setTenantInputSchemaName(String tenantInputSchemaName) {
        this.tenantInputSchemaName = tenantInputSchemaName;
    }

    public byte[] getTenantOutputSchema() {
        return tenantOutputSchema;
    }

    public void setTenantOutputSchema(byte[] tenantOutputSchema) {
        this.tenantOutputSchema = tenantOutputSchema;
    }

    public String getTenantOutputSchemaName() {
        return tenantOutputSchemaName;
    }

    public void setTenantOutputSchemaName(String tenantOutputSchemaName) {
        this.tenantOutputSchemaName = tenantOutputSchemaName;
    }

    public byte[] getSampleTenantInputJson() {
        return sampleTenantInputJson;
    }

    public void setSampleTenantInputJson(byte[] sampleTenantInputJson) {
        this.sampleTenantInputJson = sampleTenantInputJson;
    }

}
