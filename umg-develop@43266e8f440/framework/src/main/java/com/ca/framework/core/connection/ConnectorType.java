/**
 * 
 */
package com.ca.framework.core.connection;

/**
 * @author kamathan
 *
 */
public enum ConnectorType {

    SSH("SSH");

    private String type;

    private ConnectorType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
