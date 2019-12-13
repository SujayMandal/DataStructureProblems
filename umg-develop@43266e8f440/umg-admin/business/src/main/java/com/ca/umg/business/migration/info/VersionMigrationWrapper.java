package com.ca.umg.business.migration.info;

import java.io.Serializable;

import com.ca.umg.business.migration.audit.info.MigrationAuditInfo;

public class VersionMigrationWrapper implements Serializable {

    private static final long serialVersionUID = 1L;

    private VersionMigrationInfo versionMigrationInfo;

    private MigrationAuditInfo migrationAuditInfo;

    private byte[] modelDefinition;
    
    private byte[] modelExcelDefinition;

    private byte[] modelDoc;

    private byte[] modelLibraryJar;

    private String modelXMLName;
    
    private String modelExcelName;

    private String modelDocName;

    private String modelLibraryJarName;

    private String zipChecksumAlgo;

    private String modelDefinitionType;

    public VersionMigrationInfo getVersionMigrationInfo() {
        return versionMigrationInfo;
    }

    public void setVersionMigrationInfo(VersionMigrationInfo versionMigrationInfo) {
        this.versionMigrationInfo = versionMigrationInfo;
    }

    public MigrationAuditInfo getMigrationAuditInfo() {
        return migrationAuditInfo;
    }

    public void setMigrationAuditInfo(MigrationAuditInfo migrationAuditInfo) {
        this.migrationAuditInfo = migrationAuditInfo;
    }

    public byte[] getModelIODefinition() {
        return modelDefinition;
    }

    public void setModelIODefinition(byte[] modelXML) {
        this.modelDefinition = modelXML;
    }

    public String getModelDefinitionType() {
        return modelDefinitionType;
    }

    public void setModelDefinitionType(String modelDefinitionType) {
        this.modelDefinitionType = modelDefinitionType;
    }

    public byte[] getModelDoc() {
        return modelDoc;
    }

    public void setModelDoc(byte[] modelDoc) {
        this.modelDoc = modelDoc;
    }

    public byte[] getModelLibraryJar() {
        return modelLibraryJar;
    }

    public void setModelLibraryJar(byte[] modelLibraryJar) {
        this.modelLibraryJar = modelLibraryJar;
    }

    public String getZipChecksumAlgo() {
        return zipChecksumAlgo;
    }

    public void setZipChecksumAlgo(String zipChecksumAlgo) {
        this.zipChecksumAlgo = zipChecksumAlgo;
    }

    public String getModelXMLName() {
        return modelXMLName;
    }

    public void setModelXMLName(String modelXMLName) {
        this.modelXMLName = modelXMLName;
    }

    public String getModelDocName() {
        return modelDocName;
    }

    public void setModelDocName(String modelDocName) {
        this.modelDocName = modelDocName;
    }

    public String getModelLibraryJarName() {
        return modelLibraryJarName;
    }

    public void setModelLibraryJarName(String modelLibraryJarName) {
        this.modelLibraryJarName = modelLibraryJarName;
    }

	public byte[] getModelExcelDefinition() {
		return modelExcelDefinition;
	}

	public void setModelExcelDefinition(byte[] modelExcelDefinition) {
		this.modelExcelDefinition = modelExcelDefinition;
	}

	public String getModelExcelName() {
		return modelExcelName;
	}

	public void setModelExcelName(String modelExcelName) {
		this.modelExcelName = modelExcelName;
	}
    
}
