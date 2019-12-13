package com.ca.umg.business.mapping.info;

import org.hibernate.validator.constraints.NotEmpty;

import com.ca.framework.core.info.BaseInfo;
import com.ca.umg.business.model.info.ModelInfo;

public class MappingInfo extends BaseInfo {
    private static final long serialVersionUID = -2794691048240271901L;

    @NotEmpty(message = "Mapping name cannot be empty")
    private String name;

    private String description;

    private String mappingData;

    private int version;

    private boolean active;

    private ModelInfo model;

    private String modelName;

    private String umgName;
    
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMappingData() {
        return mappingData;
    }

    public void setMappingData(String mappingData) {
        this.mappingData = mappingData;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public ModelInfo getModel() {
        return model;
    }

    public void setModel(ModelInfo model) {
        this.model = model;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    /**
     * @return the umgName
     */
    public String getUmgName() {
        return umgName;
    }

    /**
     * @param umgName
     *            the umgName to set
     */
    public void setUmgName(String umgName) {
        this.umgName = umgName;
    }
}
