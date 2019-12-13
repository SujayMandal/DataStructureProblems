/**
 * 
 */
package com.ca.umg.business.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotBlank;
import org.pojomatic.annotations.Property;

import com.ca.framework.core.db.domain.MultiTenantEntity;

/**
 * @author kamathan
 *
 */
@Entity
@Table(name = "MODEL_LIBRARY")
@Audited
public class ModelLibrary extends MultiTenantEntity {

    private static final long serialVersionUID = 4197268882399608727L;

    @NotNull(message = "Group name cannot be null.")
    @NotBlank(message = "Group name cannot be blank.")
    @Column(name = "NAME", unique = true)
    @Property
    private String name;

    @NotNull(message = "Execution language cannot be null.")
    @NotBlank(message = "Execution language cannot be blank.")
    @Column(name = "EXECUTION_LANGUAGE")
    @Property
    private String executionLanguage;

    @Column(name = "EXECUTION_TYPE")
    @Property
    private String executionType;

    @NotNull(message = "Description cannot be null.")
    @NotBlank(message = "Description cannot be blank.")
    @Property
    @Column(name = "DESCRIPTION")
    private String description;

    @NotNull(message = "Umg name be null.")
    @NotBlank(message = "Umg name cannot be blank.")
    @Property
    @Column(name = "UMG_NAME")
    private String umgName;

    @NotNull(message = "Jar name be null.")
    @NotBlank(message = "Jar name cannot be blank.")
    @Property
    @Column(name = "JAR_NAME")
    private String jarName;

    @NotNull(message = "Checksum value name be null.")
    @NotBlank(message = "Checksum value cannot be blank.")
    @Property
    @Column(name = "CHECKSUM_VALUE")
    private String checksum;

    @NotNull(message = "Checksum type name be null.")
    @NotBlank(message = "Checksum type cannot be blank.")
    @Property
    @Column(name = "CHECKSUM_TYPE")
    private String encodingType;

    @NotNull(message = "Model exec env name can not be null")
    @NotBlank(message = "Model exec env name can not be blank")
    @Column(name = "MODEL_EXEC_ENV_NAME")
    @Property
    private String modelExecEnvName;

    @Column(name = "R_MANIFEST_FILE_NAME")
    @Property
    private String rmanifestFileName;

    @Column(name = "PACKAGE_NAME")
    @Property
    private String packageName;

    @Column(name = "EXECUTION_ENVIRONMENT")
    @Property
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

    public String getJarName() {
        return jarName;
    }

    public void setJarName(String jarName) {
        this.jarName = jarName;
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

    public String getExecEnv() {
        return execEnv;
    }

    public void setExecEnv(String execEnv) {
        this.execEnv = execEnv;
    }
}