/**
 * 
 */
package com.ca.umg.business.migration.audit.info;

import java.io.Serializable;

/**
 * @author nigampra
 * 
 */
public class VersionData implements Serializable {

    private static final long serialVersionUID = -1457270704333459002L;

    private String instanceName;
    private String releaseVersion;
    private String exportedBy;
    private String exportedOn;
    private String tenantModelName;
    private String status;


    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getReleaseVersion() {
        return releaseVersion;
    }

    public void setReleaseVersion(String releaseVersion) {
        this.releaseVersion = releaseVersion;
    }

    public String getExportedBy() {
        return exportedBy;
    }

    public void setExportedBy(String exportedBy) {
        this.exportedBy = exportedBy;
    }

    public String getExportedOn() {
        return exportedOn;
    }

    public void setExportedOn(String exportedOn) {
        this.exportedOn = exportedOn;
    }

    public String getTenantModelName() {
        return tenantModelName;
    }

    public void setTenantModelName(String tenantModelName) {
        this.tenantModelName = tenantModelName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
