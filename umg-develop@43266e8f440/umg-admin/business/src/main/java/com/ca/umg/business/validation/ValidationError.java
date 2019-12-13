package com.ca.umg.business.validation;

import java.io.Serializable;

import org.pojomatic.annotations.Property;

public class ValidationError implements Serializable {

    private static final long serialVersionUID = 1L;

    @Property
    private String field;

    @Property
    private String message;

    @Property
    private String errorCode;

    public ValidationError(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
