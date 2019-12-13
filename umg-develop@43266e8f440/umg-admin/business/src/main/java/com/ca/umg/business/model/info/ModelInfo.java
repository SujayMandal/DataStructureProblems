package com.ca.umg.business.model.info;

import com.ca.framework.core.info.BaseInfo;

public class ModelInfo extends BaseInfo {

    private static final long serialVersionUID = -7586306291834654377L;

    private String name;

    private String description;

    private String umgName;

    private String ioDefinitionName;

    private String documentationName;

    private ModelArtifact xml;

    private ModelArtifact documentation;

    private ModelDefinitionInfo modelDefinition;
    
    private boolean allowNull;

    private ModelArtifact excel;

    private String ioDefExcelName;

    public String getUmgName() {
        return umgName;
    }

    public void setUmgName(String umgName) {
        this.umgName = umgName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIoDefinitionName() {
        return ioDefinitionName;
    }

    public void setIoDefinitionName(String ioDefinitionName) {
        this.ioDefinitionName = ioDefinitionName;
    }

    public ModelArtifact getDocumentation() {
        return documentation;
    }

    public void setDocumentation(ModelArtifact documentation) {
        this.documentation = documentation;
    }

    public ModelArtifact getXml() {
        return xml;
    }

    public void setXml(ModelArtifact xml) {
        this.xml = xml;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ModelDefinitionInfo getModelDefinition() {
        return modelDefinition;
    }

    public void setModelDefinition(ModelDefinitionInfo modelDefinition) {
        this.modelDefinition = modelDefinition;
    }

    public String getDocumentationName() {
        return documentationName;
    }

    public void setDocumentationName(String documentationName) {
        this.documentationName = documentationName;
    }

    public boolean isAllowNull() {
        return allowNull;
    }

    public void setAllowNull(boolean allowNull) {
        this.allowNull = allowNull;
    }
    
    public ModelArtifact getExcel() {
        return excel;
    }

    public void setExcel(ModelArtifact excel) {
        this.excel = excel;
    }

    public String getIoDefExcelName() {
        return ioDefExcelName;
    }

    public void setIoDefExcelName(String ioDefExcelName) {
        this.ioDefExcelName = ioDefExcelName;
    }

}