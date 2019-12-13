package com.ca.umg.rt.core.deployment.info;

import java.io.Serializable;
import java.util.Map;

public class TestStatusInfo implements Serializable{
    private static final long serialVersionUID = -5684511782896289785L;
    private boolean error;
    private String errorCode;
    private String errorMessage;
    private long timeTaken;
    private Map<String,Object> response;
    
    /**
     * @return the timeTaken
     */
    public long getTimeTaken() {
        return timeTaken;
    }
    /**
     * @param timeTaken the timeTaken to set
     */
    public void setTimeTaken(long timeTaken) {
        this.timeTaken = timeTaken;
    }
    public Map<String,Object> getResponse() {
        return response;
    }
    public void setResponse(Map<String,Object> response) {
        this.response = response;
    }
    /**
     * @return the error
     */
    public boolean isError() {
        return error;
    }
    /**
     * @param error the error to set
     */
    public void setError(boolean error) {
        this.error = error;
    }
    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }
    /**
     * @param errorMessage the errorMessage to set
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    /**
     * @return the errorCode
     */
    public String getErrorCode() {
        return errorCode;
    }
    /**
     * @param errorCode the errorCode to set
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
