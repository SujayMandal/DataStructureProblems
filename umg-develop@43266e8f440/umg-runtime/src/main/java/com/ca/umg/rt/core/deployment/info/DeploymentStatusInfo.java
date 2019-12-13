package com.ca.umg.rt.core.deployment.info;

import java.io.Serializable;

public class DeploymentStatusInfo implements Serializable {
    private static final long serialVersionUID = -5684511782896289785L;
    private boolean error;
    private String errorCode;
    private String errorMessage;
    private String status;
    private long timeTaken;
    private boolean undeploymentReq;

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the timeTaken
     */
    public long getTimeTaken() {
        return timeTaken;
    }

    /**
     * @param timeTaken
     *            the timeTaken to set
     */
    public void setTimeTaken(long timeTaken) {
        this.timeTaken = timeTaken;
    }

    /**
     * @return the error
     */
    public boolean isError() {
        return error;
    }

    /**
     * @param error
     *            the error to set
     */
    public void setError(boolean error) {
        this.error = error;
    }

    /**
     * @return the errorCode
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * @param errorCode
     *            the errorCode to set
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @param errorMessage
     *            the errorMessage to set
     */
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
