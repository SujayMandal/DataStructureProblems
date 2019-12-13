package com.ca.umg.plugin.commons.excel.xmlconverter.entity;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "metadata")
@XmlType(propOrder = { "modelName", "modelVersion", "modelPublisher", "modelClass", "modelMethod" })
public class ModelMetadata {

    private String modelName;
    private String modelVersion;
    private String modelPublisher;
    private String modelClass;
    private String modelMethod;

    @XmlElement(name = "model-name")
    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    @XmlElement(name = "model-version")
    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    @XmlElement(name = "model-publisher")
    public String getModelPublisher() {
        return modelPublisher;
    }

    public void setModelPublisher(String modelPublisher) {
        this.modelPublisher = modelPublisher;
    }

    @XmlElement(name = "model-class")
    public String getModelClass() {
        return modelClass;
    }

    public void setModelClass(String modelClass) {
        this.modelClass = modelClass;
    }

    @XmlElement(name = "model-method")
    public String getModelMethod() {
        return modelMethod;
    }

    public void setModelMethod(String modelMethod) {
        this.modelMethod = modelMethod;
    }

}
