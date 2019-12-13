package com.ca.umg.business.migration.info;

import java.io.Serializable;

public class MappingOutputDetailsInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String mappingJson;

    protected String tidJson;

    public String getMappingJson() {
        return mappingJson;
    }

    public void setMappingJson(String mappingJson) {
        this.mappingJson = mappingJson;
    }

    public String getTidJson() {
        return tidJson;
    }

    public void setTidJson(String tidJson) {
        this.tidJson = tidJson;
    }

}
