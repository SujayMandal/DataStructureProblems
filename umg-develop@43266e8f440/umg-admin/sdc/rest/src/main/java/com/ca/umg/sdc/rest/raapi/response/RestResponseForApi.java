package com.ca.umg.sdc.rest.raapi.response;

import java.io.Serializable;
import java.util.List;

public class RestResponseForApi<T> implements Serializable {

    private static final long serialVersionUID = 618151937793694961L;

    private boolean error;
    
    private String message;

    private List<RaApiErrorResponse> errorResponse;

    private T response;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public T getResponse() {
        return response;
    }

    public void setResponse(T response) {
        this.response = response;
    }

    public List<RaApiErrorResponse> getErrorResponse() {
        return errorResponse;
    }

    public void setErrorResponse(List<RaApiErrorResponse> errorResponse) {
        this.errorResponse = errorResponse;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
