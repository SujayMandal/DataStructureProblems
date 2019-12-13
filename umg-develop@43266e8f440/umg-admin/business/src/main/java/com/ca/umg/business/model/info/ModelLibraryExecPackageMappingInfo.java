package com.ca.umg.business.model.info;

import com.ca.umg.business.model.entity.ModelLibrary;

public class ModelLibraryExecPackageMappingInfo {
    private int execSequence;
    private String packageFolder;
    private String packageVersion;
    private ModelLibrary modelLibrary;
    private String modelExecPackageId;

    public ModelLibraryExecPackageMappingInfo(int execSequence, String packageFolder, String packageVersion) {
        super();
        this.execSequence = execSequence;
        this.packageFolder = packageFolder;
        this.packageVersion = packageVersion;
    }

    public int getExecSequence() {
        return execSequence;
    }

    public void setExecSequence(int execSequence) {
        this.execSequence = execSequence;
    }

    public String getPackageFolder() {
        return packageFolder;
    }

    public void setPackageFolder(String packageName) {
        this.packageFolder = packageName;
    }

    public String getPackageVersion() {
        return packageVersion;
    }

    public void setPackageVersion(String packageVersion) {
        this.packageVersion = packageVersion;
    }

    public ModelLibrary getModelLibrary() {
        return modelLibrary;
    }

    public void setModelLibrary(ModelLibrary modelLibrary) {
        this.modelLibrary = modelLibrary;
    }

    public String getModelExecPackageId() {
        return modelExecPackageId;
    }

    public void setModelExecPackageId(String modelExecPackageId) {
        this.modelExecPackageId = modelExecPackageId;
    }

}
