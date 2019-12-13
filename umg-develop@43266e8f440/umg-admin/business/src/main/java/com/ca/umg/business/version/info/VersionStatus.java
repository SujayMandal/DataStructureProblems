/**
 * 
 */
package com.ca.umg.business.version.info;

/**
 * @author kamathan
 *
 */
public enum VersionStatus {

    SAVED("SAVED"),

    PUBLISHED("PUBLISHED"),
    
    PENDING_APPROVAL("PENDING APPROVAL"),
    
    DELETED("DELETED"),

    DEACTIVATED("DEACTIVATED"),

    TESTED("TESTED");

    private VersionStatus(String versionStatus) {
        this.versionStatus = versionStatus;
    }

    private String versionStatus;

    public String getVersionStatus() {
        return versionStatus;
    }
}