package com.ca.umg.plugin.commons.excel.xmlconverter.entity;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "umg-model-io")
@XmlType(propOrder = { "metadata", "modelInput", "modelOutput" })
public class UmgModel {
    private String xmlns;
    private ModelMetadata metadata;
    private ModelIO modelInput;
    private ModelIO modelOutput;

    public ModelMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(ModelMetadata metadata) {
        this.metadata = metadata;
    }

    @XmlAttribute
    public String getXmlns() {
        return xmlns;
    }

    public void setXmlns(String xmlns) {
        this.xmlns = xmlns;
    }

    @XmlElement(name = "input")
    public ModelIO getModelInput() {
        return modelInput;
    }

    public void setModelInput(ModelIO modelInput) {
        this.modelInput = modelInput;
    }

    @XmlElement(name = "output")
    public ModelIO getModelOutput() {
        return modelOutput;
    }

    public void setModelOutput(ModelIO modelOutput) {
        this.modelOutput = modelOutput;
    }


}
