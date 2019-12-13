/**
 * 
 */
package com.ca.umg.business.mapping.info;

import java.util.List;

/**
 * @author elumalas
 * 
 */
public class ModelMappingInfo {
    private List<String> mappingNameList;
    private List<String> versionNameList;

    public List<String> getMappingNameList() {
        return mappingNameList;
    }

    public void setMappingNameList(List<String> mappingNameList) {
        this.mappingNameList = mappingNameList;
    }

    public List<String> getVersionNameList() {
        return versionNameList;
    }

    public void setVersionNameList(List<String> versionNameList) {
        this.versionNameList = versionNameList;
    }

}
