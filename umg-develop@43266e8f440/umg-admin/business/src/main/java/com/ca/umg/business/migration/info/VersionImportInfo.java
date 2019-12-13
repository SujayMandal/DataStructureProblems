/**
 * 
 */
package com.ca.umg.business.migration.info;

import java.io.Serializable;

/**
 * @author kamathan
 *
 */
public class VersionImportInfo implements Serializable {

    private static final long serialVersionUID = 7751489837999138549L;

    private String calculatedChecksum;
    
    private String readChecksum;

    private VersionMigrationWrapper versionMigrationWrapper;
    
    private String importFileName;

    public String getChecksum() {
        return calculatedChecksum;
    }

    public void setChecksum(String checksum) {
        this.calculatedChecksum = checksum;
    }

    public VersionMigrationWrapper getVersionMigrationWrapper() {
        return versionMigrationWrapper;
    }

    public void setVersionMigrationWrapper(VersionMigrationWrapper versionMigrationWrapper) {
        this.versionMigrationWrapper = versionMigrationWrapper;
    }

    public String getReadChecksum() {
        return readChecksum;
    }

    public void setReadChecksum(String readChecksum) {
        this.readChecksum = readChecksum;
    }
    
    public boolean isValidZipCheckSum() {
        boolean isValid = false;
        if (readChecksum != null && calculatedChecksum != null) {
            isValid = readChecksum.equals(calculatedChecksum);
        }
        return isValid;
    }
    
    public void setImportFileName(final String importFileName) {
    	this.importFileName = importFileName;
    }
    
    public String getImportFileName() {
    	return importFileName;
    }
}
