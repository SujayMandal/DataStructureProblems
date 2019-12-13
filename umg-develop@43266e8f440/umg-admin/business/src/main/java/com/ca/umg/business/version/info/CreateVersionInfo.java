package com.ca.umg.business.version.info;

import com.ca.framework.core.info.BaseInfo;

public class CreateVersionInfo extends BaseInfo {

    private static final long serialVersionUID = 1L;

    private String libraryName;

    private String libraryRecord;

    private String modelName;

    private String tenantModelName;

    private String tenantModelDescription;

    private String tidName;

    private String tidDescription;

    private String versionDescription;

    private String versionType;

    private Integer majorVersion;

    private String libDescValue;

    /**
     * @return the libDescValue
     */
    public String getLibDescValue() {
        return libDescValue;
    }

    /**
     * @param libDescValue
     *            the libDescValue to set
     */
    public void setLibDescValue(String libDescValue) {
        this.libDescValue = libDescValue;
    }

    public String getLibraryName() {
        return libraryName;
    }

    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName;
    }

    public String getLibraryRecord() {
        return libraryRecord;
    }

    public void setLibraryRecord(String libraryRecord) {
        this.libraryRecord = libraryRecord;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getTenantModelName() {
        return tenantModelName;
    }

    public void setTenantModelName(String tenantModelName) {
        this.tenantModelName = tenantModelName;
    }

    public String getTenantModelDescription() {
        return tenantModelDescription;
    }

    public void setTenantModelDescription(String tenantModelDescription) {
        this.tenantModelDescription = tenantModelDescription;
    }

    public String getTidName() {
        return tidName;
    }

    public void setTidName(String tidName) {
        this.tidName = tidName;
    }

    public String getTidDescription() {
        return tidDescription;
    }

    public void setTidDescription(String tidDescription) {
        this.tidDescription = tidDescription;
    }

    public String getVersionDescription() {
        return versionDescription;
    }

    public void setVersionDescription(String versionDescription) {
        this.versionDescription = versionDescription;
    }

    public String getVersionType() {
        return versionType;
    }

    public void setVersionType(String versionType) {
        this.versionType = versionType;
    }

    public Integer getMajorVersion() {
        return majorVersion;
    }

    public void setMajorVersion(Integer majorVersion) {
        this.majorVersion = majorVersion;
    }

}
