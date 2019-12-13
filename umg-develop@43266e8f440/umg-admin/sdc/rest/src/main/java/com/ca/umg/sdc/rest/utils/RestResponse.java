package com.ca.umg.sdc.rest.utils;

import java.io.Serializable;

public class RestResponse<T> implements Serializable {

    private static final long serialVersionUID = 618151937793694961L;

    private boolean error;

    private String errorCode;

    private String message;

    private T response;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResponse() {
        return response;
    }

    public void setResponse(T response) {
        this.response = response;
    }
}
