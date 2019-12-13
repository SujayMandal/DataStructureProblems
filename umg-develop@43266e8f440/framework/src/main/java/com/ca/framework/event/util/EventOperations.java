/**
 * 
 */
package com.ca.framework.event.util;

/**
 * @author kamathan
 *
 */
public enum EventOperations {

    ADD("ADD"),

    REMOVE("REMOVE"),

    UPDATE("UPDATE");

    private String operation;

    private EventOperations(final String operation) {
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }

}
