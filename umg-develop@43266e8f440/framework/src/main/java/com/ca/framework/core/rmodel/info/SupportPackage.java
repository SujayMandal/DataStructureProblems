package com.ca.framework.core.rmodel.info;

import static com.google.common.base.Objects.toStringHelper;

import java.io.Serializable;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.PojomaticPolicy;
import org.pojomatic.annotations.Property;

public class SupportPackage implements Serializable {

    private static final long serialVersionUID = 1780069699329386554L;

    @Property(policy = PojomaticPolicy.HASHCODE_EQUALS)
    private String versionName;
    @Property(policy = PojomaticPolicy.HASHCODE_EQUALS)
    private String majorVersion;
    @Property(policy = PojomaticPolicy.HASHCODE_EQUALS)
    private String minorVersion;

    @Property(policy = PojomaticPolicy.HASHCODE_EQUALS)
    private String modelLibraryName;
    @Property(policy = PojomaticPolicy.HASHCODE_EQUALS)
    private String jarName;
    @Property(policy = PojomaticPolicy.HASHCODE_EQUALS)
    private String modelLibraryVersionName;

    @Property(policy = PojomaticPolicy.HASHCODE_EQUALS)
    private Integer hierarchy;
    @Property(policy = PojomaticPolicy.HASHCODE_EQUALS)
    private String packageName;
    @Property(policy = PojomaticPolicy.HASHCODE_EQUALS)
    private String packageFolder;
    @Property(policy = PojomaticPolicy.HASHCODE_EQUALS)
    private String packageVersion;
    @Property(policy = PojomaticPolicy.HASHCODE_EQUALS)
    private String packageType;
    @Property(policy = PojomaticPolicy.HASHCODE_EQUALS)
    private String compiledOs;
    @Property(policy = PojomaticPolicy.HASHCODE_EQUALS)
    private String execEnv;
    @Property(policy = PojomaticPolicy.HASHCODE_EQUALS)
    private String envVersion;

    public String getModelLibraryName() {
        return modelLibraryName;
    }

    public void setModelLibraryName(String modelLibraryName) {
        this.modelLibraryName = modelLibraryName;
    }

    public String getJarName() {
        return jarName;
    }

    public void setJarName(String jarName) {
        this.jarName = jarName;
    }

    public String getModelLibraryVersionName() {
        return modelLibraryVersionName;
    }

    public void setModelLibraryVersionName(String modelLibraryVersionName) {
        this.modelLibraryVersionName = modelLibraryVersionName;
    }

    public Integer getHierarchy() {
        return hierarchy;
    }

    public void setHierarchy(Integer hierarchy) {
        this.hierarchy = hierarchy;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageFolder() {
        return packageFolder;
    }

    public void setPackageFolder(String packageFolder) {
        this.packageFolder = packageFolder;
    }

    public String getPackageVersion() {
        return packageVersion;
    }

    public void setPackageVersion(String packageVersion) {
        this.packageVersion = packageVersion;
    }

    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public String getCompiledOs() {
        return compiledOs;
    }

    public void setCompiledOs(String compiledOs) {
        this.compiledOs = compiledOs;
    }

    public String getExecEnv() {
        return execEnv;
    }

    public void setExecEnv(String execEnv) {
        this.execEnv = execEnv;
    }

    public String getEnvVersion() {
        return envVersion;
    }

    public void setEnvVersion(String envVersion) {
        this.envVersion = envVersion;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getMajorVersion() {
        return majorVersion;
    }

    public void setMajorVersion(String majorVersion) {
        this.majorVersion = majorVersion;
    }

    public String getMinorVersion() {
        return minorVersion;
    }

    public void setMinorVersion(String minorVersion) {
        this.minorVersion = minorVersion;
    }

    @Override
    public String toString() {
        return toStringHelper(this.getClass()).add("Package Name:", packageName).add("Package Folder:", packageFolder)
                .add("Package Version:", packageVersion).add("Env Version:", envVersion).add("Package Type:", packageType)
                .add("Hierarchy:", hierarchy).add("jarName:", jarName).toString();
    }

    @Override
    public boolean equals(Object object) {
        return Pojomatic.equals(this, object);
    }
    
    @Override
    public int hashCode() {
    	return Pojomatic.hashCode(this);
    }
}
