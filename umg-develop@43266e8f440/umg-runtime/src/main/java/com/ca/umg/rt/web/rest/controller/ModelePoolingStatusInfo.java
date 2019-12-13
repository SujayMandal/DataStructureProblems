package com.ca.umg.rt.web.rest.controller;

import java.io.Serializable;

public class ModelePoolingStatusInfo implements Serializable {
	
    private static final long serialVersionUID = -5684511782896289785L;
    
    private boolean error;
    private String errorCode;
    private String errorMessage;
    private String status;
    private long timeTaken;
    private boolean undeploymentReq;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(long timeTaken) {
        this.timeTaken = timeTaken;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isUndeploymentReq() {
        return undeploymentReq;
    }

    public void setUndeploymentReq(boolean undeploymentReq) {
        this.undeploymentReq = undeploymentReq;
    }
}