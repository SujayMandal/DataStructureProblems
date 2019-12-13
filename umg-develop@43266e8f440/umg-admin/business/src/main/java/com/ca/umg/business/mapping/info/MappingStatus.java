/**
 * 
 */
package com.ca.umg.business.mapping.info;

/**
 * @author raddibas
 *
 */
public enum MappingStatus {

    SAVED("SAVED"),

    FINALIZED("FINALIZED");

    private MappingStatus(String mappingStatus) {
        this.mappingStatus = mappingStatus;
    }

    private String mappingStatus;

    public String getMappingStatus() {
        return mappingStatus;
    }

}
