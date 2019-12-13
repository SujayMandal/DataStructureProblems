/**
 * 
 */
package com.ca.umg.business.mapping.info;

import java.io.Serializable;
import java.util.Map;

/**
 * @author chandrsa
 * 
 */
public class TidIoDefinition implements Serializable {

    private static final long serialVersionUID = 1L;
    private String name;
    private Object value;
    private String validationMethod;
    private boolean mandatory;
    private boolean arrayType;
    private String htmlElement;
    private String description;
    private Map<String, Object> datatype;
    private String errorMessage;
    private boolean error;
    private Object arrayValue;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getValidationMethod() {
        return validationMethod;
    }

    public void setValidationMethod(String validationMethod) {
        this.validationMethod = validationMethod;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public boolean isArrayType() {
        return arrayType;
    }

    public void setArrayType(boolean arrayType) {
        this.arrayType = arrayType;
    }

    public String getHtmlElement() {
        return htmlElement;
    }

    public void setHtmlElement(String htmlElement) {
        this.htmlElement = htmlElement;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Object> getDatatype() {
        return datatype;
    }

    public void setDatatype(Map<String, Object> datatype) {
        this.datatype = datatype;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public Object getArrayValue() {
        return arrayValue;
    }

    public void setArrayValue(Object arrayValue) {
        this.arrayValue = arrayValue;
    }
}
