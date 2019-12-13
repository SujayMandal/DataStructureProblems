/**
 * 
 */
package com.ca.umg.business.mid.extraction.info;

import java.io.Serializable;

/**
 * @author chandrsa
 * 
 */
public class MappingViewInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    private String mappingParam;
    private String mappedTo;

    public String getMappingParam() {
        return mappingParam;
    }

    public void setMappingParam(String mappingParam) {
        this.mappingParam = mappingParam;
    }

    public String getMappedTo() {
        return mappedTo;
    }

    public void setMappedTo(String mappedTo) {
        this.mappedTo = mappedTo;
    }

}
