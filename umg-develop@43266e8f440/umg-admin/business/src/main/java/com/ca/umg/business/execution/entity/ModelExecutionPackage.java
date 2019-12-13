/**
 * 
 */
package com.ca.umg.business.execution.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.pojomatic.annotations.Property;

import com.ca.framework.core.db.domain.AbstractAuditable;

/**
 * @author kamathan
 *
 */
@Entity
@Table(name = "MODEL_EXEC_PACKAGES")
public class ModelExecutionPackage extends AbstractAuditable {

    private static final long serialVersionUID = -8406620343214914130L;

    @NotNull(message = "Model exec env id can not be null")
    @NotBlank(message = "Model exec env id can not be blank")
    @Column(name = "MODEL_EXEC_ENV_NAME")
    @Property
    private String modelExecEnvName;

    @NotNull(message = "Package folder cannot be null. ")
    @NotBlank(message = "Package folder cannot be blank.")
    @Column(name = "PACKAGE_FOLDER")
    @Property
    private String packageFolder;

    @NotNull(message = "Package name cannot be null.")
    @NotBlank(message = "Package name cannot be blank.")
    @Column(name = "PACKAGE_NAME")
    @Property
    private String packageName;

    @NotNull(message = "Package version cannot be null.")
    @NotBlank(message = "Package version cannot be blank.")
    @Column(name = "PACKAGE_VERSION")
    @Property
    private String packageVersion;

    @NotNull(message = "Package type cannot be null.")
    @NotBlank(message = "Package type cannot be blank.")
    @Column(name = "PACKAGE_TYPE")
    @Property
    private String packageType;

    @NotNull(message = "Compiled OS cannot be null.")
    @NotBlank(message = "Compiled OS cannot be blank.")
    @Column(name = "COMPILED_OS")
    @Property
    private String compiledOs;
    
    @NotNull(message = "Package execution environment cannot be null.")
    @NotBlank(message = "Package execution environment cannot be blank.")
    @Column(name = "EXECUTION_ENVIRONMENT")
    @Property
    private String execEnv;   

    public String getPackageFolder() {
        return packageFolder;
    }

    public void setPackageFolder(String packageFolder) {
        this.packageFolder = packageFolder;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
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
