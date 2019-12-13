/**
 * 
 */
package com.ca.umg.business.migration.audit.info;

import org.hibernate.validator.constraints.NotEmpty;

import com.ca.framework.core.info.BaseInfo;
import com.ca.umg.business.version.info.VersionInfo;

/**
 * @author nigampra
 * 
 */
public class MigrationAuditInfo extends BaseInfo {

    private static final long serialVersionUID = 4946176290725811526L;

    private VersionInfo version;

    @NotEmpty(message = "Type can't be null.")
    private String type;

    private byte[] versionData;

    @NotEmpty(message = "Status can't be null.")
    private String status;

    public VersionInfo getVersion() {
        return version;
    }

    public void setVersion(VersionInfo version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getVersionData() {
        return versionData;
    }

    public void setVersionData(byte[] versionData) {
        this.versionData = versionData;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
