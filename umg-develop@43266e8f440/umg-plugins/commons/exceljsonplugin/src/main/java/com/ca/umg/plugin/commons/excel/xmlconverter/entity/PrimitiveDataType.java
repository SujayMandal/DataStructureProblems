package com.ca.umg.plugin.commons.excel.xmlconverter.entity;

import javax.xml.bind.annotation.XmlAttribute;


public class PrimitiveDataType {

    private String defaultValue;
    private String pattern;
    private String totalDigits;
    private String fractionDigits;
    private String length;

    @XmlAttribute
    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @XmlAttribute
    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    @XmlAttribute
    public String getTotalDigits() {
        return totalDigits;
    }

    public void setTotalDigits(String totalDigits) {
        this.totalDigits = totalDigits;
    }

    @XmlAttribute
    public String getFractionDigits() {
        return fractionDigits;
    }

    public void setFractionDigits(String fractionDigits) {
        this.fractionDigits = fractionDigits;
    }

    @XmlAttribute
    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

}
