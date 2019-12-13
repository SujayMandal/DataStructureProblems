package com.ca.modelet.common;

public class ExceptionResponse {
    
    private String exceptionType;
    private String errorCode;
    private String[] arguments;
    
    public ExceptionResponse(final String exceptionType, final String errorCode, final String[] arguments) {
        this.exceptionType = exceptionType;
        this.arguments = arguments;
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    public void setErrorCode(final String errorCode) {
        this.errorCode = errorCode;
    }
    public String[] getArguments() {
        return arguments;
    }
    public void setArguments(final String[] arguments) {
        this.arguments = arguments;
    }
    public String getExceptionType() {
        return exceptionType;
    }
    public void setExceptionType(final String exceptionType) {
        this.exceptionType = exceptionType;
    }
    
}
