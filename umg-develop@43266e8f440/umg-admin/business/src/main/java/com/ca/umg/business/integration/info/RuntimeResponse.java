/**
 * 
 */
package com.ca.umg.business.integration.info;

import java.io.Serializable;

/**
 * @author kamathan
 *
 */
public class RuntimeResponse implements Serializable {

    private static final long serialVersionUID = -6065153999613342544L;

    private boolean error;
    private String errorCode;
    private String errorMessage;
    private String status;
    private long timeTaken;
    private boolean undeploymentReq;

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

    /**
     * @return the undeploymentReq
     */
    public boolean isUndeploymentReq() {
        return undeploymentReq;
    }

    /**
     * @param undeploymentReq the undeploymentReq to set
     */
    public void setUndeploymentReq(boolean undeploymentReq) {
        this.undeploymentReq = undeploymentReq;
    }
}
