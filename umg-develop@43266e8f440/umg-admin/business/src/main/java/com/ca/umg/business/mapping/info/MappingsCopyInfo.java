package com.ca.umg.business.mapping.info;

import java.io.Serializable;

/**
 * Class is reponsible for containing the Mapping details for TID Copy
 * 
 * @author putaneha
 *
 */
public class MappingsCopyInfo implements Serializable {

    /**
     * generated Serial version id
     */
    private static final long serialVersionUID = -8385825101912723135L;

    private String version;
    private String versionNo;
    private String tidName;


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(String versionNo) {
        this.versionNo = versionNo;
    }

    public String getTidName() {
        return tidName;
    }

    public void setTidName(String tidName) {
        this.tidName = tidName;
    }
}
