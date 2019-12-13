package com.ca.umg.sdc.rest.raapi.response;

import java.io.Serializable;

public class RaApiErrorResponse implements Serializable{
    
    private static final long serialVersionUID = 618491938693694961L;
    
    private String errorCode;

    private String field;
    
    private String message;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
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
}
