package com.ca.umg.plugin.commons.excel.xmlconverter.entity;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "dimension", "type", "defaultValue", "length" })
public class ArrayDataType {

    private Dimension dimension;
    private String defaultValue;
    private Datatype type;
    private String length;
  
	@XmlElement(name = "dimensions")
    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }
    public Datatype getType() {
        return type;
    }

    public void setType(Datatype type) {
        this.type = type;
    }


    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Dimension getDimension() {
        return dimension;
    }
    
    public String getLength() {
  		return length;
  	}
	@XmlElement(name = "length")
  	public void setLength(String length) {
  		this.length = length;
  	}


}
