/**
 * 
 */
package com.ca.umg.business.model.info;

import java.io.Serializable;

import com.ca.umg.business.common.info.PagingInfo;

/**
 * @author basanaga
 * 
 */
public class ModelExecutionPackageInfo extends PagingInfo implements Serializable {
	
    /**
     * generated serial version id
     */
    private static final long serialVersionUID = -9079717276358339246L;

    private String modelExecEnvName;
	
	private String packageFolder;
	
	private String packageName;
	
	private String packageVersion;
	
    private String execEnv;

	private String packageType;
	
	private String compiledOs;
	
	private ModelArtifact executionPackage;

	public String getModelExecEnvName() {
		return modelExecEnvName;
	}

	public void setModelExecEnvName(String modelExecEnvName) {
		this.modelExecEnvName = modelExecEnvName;
	}

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

	public ModelArtifact getExecutionPackage() {
		return executionPackage;
	}

	public void setExecutionPackage(ModelArtifact executionPackage) {
		this.executionPackage = executionPackage;
	}

    public String getExecEnv() {
        return execEnv;
    }

    public void setExecEnv(String execEnv) {
        this.execEnv = execEnv;
    }

}
