/**
 * 
 */
package com.ca.umg.modelet.util;

/**
 * @author kamathan
 *
 */
public enum ExcelDatatypes {

    DOUBLE("DOUBLE"),

    STRING("STRING"),

    BOOLEAN("BOOLEAN"),

    INTEGER("INTEGER"),

    DATE("DATE"),
    
    DATETIME("DATETIME"),
    
    CURRENCY("CURRENCY"),
    
    PERCENTAGE("PERCENTAGE"),

    OBJECT("OBJECT"),
    
    LONG("LONG");

    private String datatype;

    private ExcelDatatypes(String datatype) {
        this.datatype = datatype;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

}
