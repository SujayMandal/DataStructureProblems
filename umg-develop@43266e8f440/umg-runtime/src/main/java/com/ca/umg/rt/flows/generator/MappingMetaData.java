package com.ca.umg.rt.flows.generator;

import java.io.Serializable;

public class MappingMetaData implements Serializable{
    private static final long serialVersionUID = -8249700715441817718L;

    private byte[] modelIoData;
    
    private byte[] mappingInput;
    
    private byte[] mappingOutput;
    
    private byte[] tenantInputDefinition;
    
    private byte[] tenantOutputDefinition;

    public byte[] getMappingInput() {
        return mappingInput;
    }

    public void setMappingInput(byte[] mappingInput) {
        this.mappingInput = mappingInput;
    }

    public byte[] getMappingOutput() {
        return mappingOutput;
    }

    public void setMappingOutput(byte[] mappingOutput) {
        this.mappingOutput = mappingOutput;
    }

    public byte[] getTenantInputDefinition() {
        return tenantInputDefinition;
    }

    public void setTenantInputDefinition(byte[] tenantInputDefinition) {
        this.tenantInputDefinition = tenantInputDefinition;
    }

    public byte[] getTenantOutputDefinition() {
        return tenantOutputDefinition;
    }

    public void setTenantOutputDefinition(byte[] tenantOutputDefinition) {
        this.tenantOutputDefinition = tenantOutputDefinition;
    }

    /**
     * @return the modelIoData
     */
    public byte[] getModelIoData() {
        return modelIoData;
    }

    /**
     * @param modelIoData the modelIoData to set
     */
    public void setModelIoData(byte[] modelIoData) {
        this.modelIoData = modelIoData;
    }

}
