/**
 * 
 */
package com.ca.umg.rt.util;

/**
 * @author chandrsa
 * 
 */
public enum Datatype {
    STRING("STRING"), 
    DOUBLE("DOUBLE"), 
    INTEGER("INTEGER"),
    LONG("LONG"),
    BOOLEAN("BOOLEAN"),
    DATE("DATE"), 
    BIGINTEGER("BIGINTEGER"),
    BIGDECIMAL("BIGDECIMAL"),
    DATETIME("DATETIME"),
    OBJECT("OBJECT");


    private String datatype;

    Datatype(String datatype) {
        this.datatype = datatype;
    }

    public String getDatatype() {
        return datatype;
    }

}
