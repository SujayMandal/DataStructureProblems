/**
 * 
 */
package com.ca.framework.core.ioreduce;

/**
 * @author chandrsa
 * 
 */
public enum Datatype {
    STRING("STRING"), 
    DOUBLE("DOUBLE"), 
    INTEGER("INTEGER"),
    BOOLEAN("BOOLEAN"),
    DATE("DATE"), 
    DATETIME("DATETIME");

    private String datatype;

    Datatype(String datatype) {
        this.datatype = datatype;
    }

    public String getDatatype() {
        return datatype;
    }

}
