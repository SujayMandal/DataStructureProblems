package com.ca.framework.core.batch;

public enum TransactionStatus {
    QUEUED("Queued"),
    IN_EXECUTION("In Execution"),
    IN_PROGRESS("In Progress"), 
    PROCESSED("Processed"),
    ERROR("Error"),
    TIMEOUT("Time-Out"),  
    TERMINATED("Terminated"),
	SUCCESS("Success"),
	PARTIAL_SUCCESS("PartialSuccess"),
	FAILURE("Failure");
    
    private String status;

    private TransactionStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}