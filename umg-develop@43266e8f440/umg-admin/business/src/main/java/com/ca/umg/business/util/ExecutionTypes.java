package com.ca.umg.business.util;

/**
 * Enum to list all execution types.
 * 
 * @author kamathan
 *
 */
public enum ExecutionTypes {

    INTERNAL("INTERNAL"),

    EXTERNAL("EXTERNAL");

    private ExecutionTypes(String executionType) {
        this.executionType = executionType;
    }

    private String executionType;

    public String getExecutionType() {
        return executionType;
    }

    public void setExecutionType(String executionType) {
        this.executionType = executionType;
    }
}
