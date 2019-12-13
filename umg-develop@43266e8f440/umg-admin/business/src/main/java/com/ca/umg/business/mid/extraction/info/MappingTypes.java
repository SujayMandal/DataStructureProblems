/**
 * 
 */
package com.ca.umg.business.mid.extraction.info;

/**
 * @author chandrsa
 * 
 */
public enum MappingTypes {

    ONE_TO_ONE("ONE_TO_ONE"), OPTIONAL("OPTIONAL"), INVALID("INVALID"), NONE("NONE");

    private String mappingType;

    private MappingTypes(String mappingType) {
        this.mappingType = mappingType;
    }

    public String getMappingType() {
        return mappingType;
    }

}
