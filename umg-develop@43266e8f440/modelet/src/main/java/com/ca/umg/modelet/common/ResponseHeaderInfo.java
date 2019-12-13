package com.ca.umg.modelet.common;

public class ResponseHeaderInfo {
    
    private String errorCode;
    private String errorMessage;
    private String error;
    
    /*changes for UMG-5015*/
    private String executionCommand;
    private String executionResponse;
    private String executionLogs;
    
    public String getExecutionLogs() {
		return executionLogs;
	}
	public void setExecutionLogs(String executionLogs) {
		this.executionLogs = executionLogs;
	}
	public String getExecutionCommand() {
		return executionCommand;
	}
	public void setExecutionCommand(String executionCommand) {
		this.executionCommand = executionCommand;
	}
	public String getExecutionResponse() {
		return executionResponse;
	}
	public void setExecutionResponse(String executionResponse) {
		this.executionResponse = executionResponse;
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
    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }

}
