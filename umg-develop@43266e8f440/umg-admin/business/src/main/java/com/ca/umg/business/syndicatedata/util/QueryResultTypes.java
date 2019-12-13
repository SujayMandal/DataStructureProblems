/**
 * 
 */
package com.ca.umg.business.syndicatedata.util;

/**
 * @author kamathan
 *
 */
public enum QueryResultTypes {

    SINGLEROW("SINGLEROW"),

    PRIMITIVE("PRIMITIVE"),

    ARRAY("ARRAY"),

    MULTIPLEROW("MULTIPLEROW"),

    SINGLE_DIM_ARRAY("SINGLE_DIM_ARRAY");

    private String datatype;

    private QueryResultTypes(String datatype) {
        this.datatype = datatype;
    }

    public String getDatatype() {
        return datatype;
    }
}
