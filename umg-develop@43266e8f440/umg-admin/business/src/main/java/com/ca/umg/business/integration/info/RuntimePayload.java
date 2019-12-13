/**
 * 
 */
package com.ca.umg.business.integration.info;

import java.io.Serializable;

/**
 * @author kamathan
 *
 */
public class RuntimePayload implements Serializable {

    private static final long serialVersionUID = -8853143654449526792L;

    private String errorCode;
    private boolean error;
    private String status;
    private String errorMessage;
    private long timeTaken;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public long getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(long timeTaken) {
        this.timeTaken = timeTaken;
    }

}
