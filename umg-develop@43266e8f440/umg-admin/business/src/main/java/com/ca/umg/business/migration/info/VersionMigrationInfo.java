package com.ca.umg.business.migration.info;

import java.io.Serializable;
import java.util.List;

import com.ca.umg.business.syndicatedata.info.SyndicateDataQueryInfo;

public class VersionMigrationInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String modelName;

    private String modelDescription;

    private String modelLibraryName;

    private String modelLibraryDescription;

    private String executionLanguage;

    private String executionType;

    private String modelLibraryChecksum;

    private String modelLibraryChecksumAlgo;

    private MappingDetailsInfo mappingInfo;

    private List<TableMetaDataInfo> tableMetaData;

    private List<SyndicateDataQueryInfo> queryInfo;
    
    private String modelExecEnvName;

    private boolean allowNull;

    private String environmentVersion;

    public boolean isAllowNull() {
        return allowNull;
    }

    public void setAllowNull(boolean allowNull) {
        this.allowNull = allowNull;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelDescription() {
        return modelDescription;
    }

    public void setModelDescription(String modelDescription) {
        this.modelDescription = modelDescription;
    }

    public String getModelLibraryName() {
        return modelLibraryName;
    }

    public void setModelLibraryName(String modelLibraryName) {
        this.modelLibraryName = modelLibraryName;
    }

    public String getModelLibraryDescription() {
        return modelLibraryDescription;
    }

    public void setModelLibraryDescription(String modelLibraryDescription) {
        this.modelLibraryDescription = modelLibraryDescription;
    }

    public String getExecutionLanguage() {
        return executionLanguage;
    }

    public void setExecutionLanguage(String executionLanguage) {
        this.executionLanguage = executionLanguage;
    }

    public String getExecutionType() {
        return executionType;
    }

    public void setExecutionType(String executionType) {
        this.executionType = executionType;
    }

    public String getModelLibraryChecksum() {
        return modelLibraryChecksum;
    }

    public void setModelLibraryChecksum(String modelLibraryChecksum) {
        this.modelLibraryChecksum = modelLibraryChecksum;
    }

    public String getModelLibraryChecksumAlgo() {
        return modelLibraryChecksumAlgo;
    }

    public void setModelLibraryChecksumAlgo(String modelLibraryChecksumAlgo) {
        this.modelLibraryChecksumAlgo = modelLibraryChecksumAlgo;
    }

    public MappingDetailsInfo getMappingInfo() {
        return mappingInfo;
    }

    public void setMappingInfo(MappingDetailsInfo mappingInfo) {
        this.mappingInfo = mappingInfo;
    }

    public List<TableMetaDataInfo> getTableMetaData() {
        return tableMetaData;
    }

    public void setTableMetaData(List<TableMetaDataInfo> tableMetaData) {
        this.tableMetaData = tableMetaData;
    }

    public List<SyndicateDataQueryInfo> getQueryInfo() {
        return queryInfo;
    }

    public void setQueryInfo(List<SyndicateDataQueryInfo> queryInfo) {
        this.queryInfo = queryInfo;
    }
    
    public String getModelExecEnvName() {
        return modelExecEnvName;
    }

    public void setModelExecEnvName(String modelExecEnvName) {
        this.modelExecEnvName = modelExecEnvName;
    }

    public String getEnvironmentVersion() {
        return environmentVersion;
    }

    public void setEnvironmentVersion(String environmentVersion) {
        this.environmentVersion = environmentVersion;
    }

}