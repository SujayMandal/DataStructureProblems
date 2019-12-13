/**
 * 
 */
package com.ca.umg.business.migration.info;

import java.io.Serializable;

/**
 * @author kamathan
 *
 */
public class VersionDetail implements Serializable {

    private static final long serialVersionUID = 7751489837999138549L;

    private String name;

    private String description;

    private String versionType;

    private Integer majorVersion;

    private String versionDescription;

    private String checksum;
    
    private String importFileName;
    
    private String migrationId;
    
    private String modelType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getVersionDescription() {
        return versionDescription;
    }

    public void setVersionDescription(String versionDescription) {
        this.versionDescription = versionDescription;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }
    
    public String getImportFileName() {
    	return importFileName;
    }
    
    public void setImportFileName(final String importFileName) {
    	this.importFileName = importFileName;
    }
    
    public String getMigrationId() {
    	return migrationId;
    }
    
    public void setMigrationId(final String migrationId) {
    	this.migrationId = migrationId;
    }

	public String getModelType() {
		return modelType;
	}

	public void setModelType(String modelType) {
		this.modelType = modelType;
	}
}