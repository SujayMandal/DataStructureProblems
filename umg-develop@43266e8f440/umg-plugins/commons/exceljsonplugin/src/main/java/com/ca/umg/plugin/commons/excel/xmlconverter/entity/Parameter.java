package com.ca.umg.plugin.commons.excel.xmlconverter.entity;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "parameter")
@XmlType(propOrder = { "nativeDataType", "syndicate", "mandatory", "datatype", "description", "name", "apiName", "modelParamName", "sequence","acceptableValues" })
public class Parameter {

    private Datatype datatype;
    private String sequence;
    private String name;
    private String apiName;
    private String modelParamName;
    private String description;
    private String mandatory;
    private String syndicate;
    private String nativeDataType;
    private String acceptableValues;
	
	public Datatype getDatatype() {
        return datatype;
    }

    public void setDatatype(Datatype datatype) {
        this.datatype = datatype;
    }

    @XmlAttribute
    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlAttribute
    public String getMandatory() {
        return mandatory;
    }

    public void setMandatory(String mandatory) {
        this.mandatory = mandatory;
    }

    @XmlAttribute
    public String getSyndicate() {
        return syndicate;
    }

    public void setSyndicate(String syndicate) {
        this.syndicate = syndicate;
    }

    @XmlAttribute
    public String getNativeDataType() {
        return nativeDataType;
    }

    public void setNativeDataType(String nativeDataType) {
        this.nativeDataType = nativeDataType;
    }
    
    @XmlAttribute
	public String getApiName() {
		return apiName;
	}

	public void setApiName(String apiName) {
		this.apiName = apiName;
	}
	
	@XmlAttribute
	public String getModelParamName() {
		return modelParamName;
	}

	public void setModelParamName(String modelParamName) {
		this.modelParamName = modelParamName;
	}
	
	@XmlAttribute	
	public String getAcceptableValues() {
		return acceptableValues;
	}

	public void setAcceptableValues(String acceptableValues) {
		this.acceptableValues = acceptableValues;
	}

	
	
	
}
