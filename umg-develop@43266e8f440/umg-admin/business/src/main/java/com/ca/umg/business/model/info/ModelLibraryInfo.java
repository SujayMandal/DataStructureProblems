package com.ca.umg.business.model.info;

import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

import com.ca.umg.business.common.info.SearchOptions;

@SuppressWarnings("PMD.TooManyFields")
public class ModelLibraryInfo extends SearchOptions {

    private static final long serialVersionUID = -4306745650830490251L;

    private String name;

    private String executionLanguage;

    private String executionType;

    private String description;

    @NotEmpty(message = "Model Library UMG name cannot be empty")
    private String umgName;

    private String jarName;

    private String checksum;

    private String encodingType;

    private ModelArtifact jar;

    private String rmanifestFileName;

    private ModelArtifact manifestFile;

    private String modelExecEnvName;

    private List<ModelLibraryExecPackageMappingInfo> supportPackages;

    private ModelExecutionEnvironmentInfo modelExecutionEnvironment;

    private String packageName;

    private String programmingLanguage;

    private String execEnv;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExecutionLanguage() {
        return executionLanguage;
    }

    public void setExecutionLanguage(String executionLanguage) {
        this.executionLanguage = executionLanguage;
    }

    public String getJarName() {
        return jarName;
    }

    public void setJarName(String jarName) {
        this.jarName = jarName;
    }

    public String getExecutionType() {
        return executionType;
    }

    public void setExecutionType(String executionType) {
        this.executionType = executionType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUmgName() {
        return umgName;
    }

    public void setUmgName(String umgName) {
        this.umgName = umgName;
    }

    public ModelArtifact getJar() {
        return jar;
    }

    public void setJar(ModelArtifact jar) {
        this.jar = jar;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getEncodingType() {
        return encodingType;
    }

    public void setEncodingType(String encodingType) {
        this.encodingType = encodingType;
    }

    public ModelArtifact getManifestFile() {
        return manifestFile;
    }

    public void setManifestFile(ModelArtifact manifestFile) {
        this.manifestFile = manifestFile;
    }

    public List<ModelLibraryExecPackageMappingInfo> getSupportPackages() {
        return supportPackages;
    }

    public void setSupportPackages(List<ModelLibraryExecPackageMappingInfo> supportPackages) {
        this.supportPackages = supportPackages;
    }

    public String getRmanifestFileName() {
        return rmanifestFileName;
    }

    public void setRmanifestFileName(String rmanifestFileName) {
        this.rmanifestFileName = rmanifestFileName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(final String packageName) {
        this.packageName = packageName;
    }

    public String getModelExecEnvName() {
        return modelExecEnvName;
    }

    public void setModelExecEnvName(String modelExecEnvName) {
        this.modelExecEnvName = modelExecEnvName;
    }

    public ModelExecutionEnvironmentInfo getModelExecutionEnvironment() {
        return modelExecutionEnvironment;
    }

    public void setModelExecutionEnvironment(ModelExecutionEnvironmentInfo modelExecutionEnvironment) {
        this.modelExecutionEnvironment = modelExecutionEnvironment;
    }

    public String getProgrammingLanguage() {
        return programmingLanguage;
    }

    public void setProgrammingLanguage(String programmingLanguage) {
        this.programmingLanguage = programmingLanguage;
    }

    public String getExecEnv() {
        return execEnv;
    }

    public void setExecEnv(String execEnv) {
        this.execEnv = execEnv;
    }

}
