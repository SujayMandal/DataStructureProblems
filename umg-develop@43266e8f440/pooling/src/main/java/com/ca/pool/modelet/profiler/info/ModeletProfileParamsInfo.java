package com.ca.pool.modelet.profiler.info;

import java.io.Serializable;

public class ModeletProfileParamsInfo implements Serializable {
	private static final long serialVersionUID = 5120443169316390139L;
	private String profileName;
	private String code;
	private String paramValue;
	private String type;
	private String delimitter;
	private String executionEnvironment;
	private String environmentVersion;

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDelimitter() {
		return delimitter;
	}

	public void setDelimitter(String delimitter) {
		this.delimitter = delimitter;
	}

	public String getExecutionEnvironment() {
		return executionEnvironment;
	}

	public void setExecutionEnvironment(String executionEnvironment) {
		this.executionEnvironment = executionEnvironment;
	}

	public String getEnvironmentVersion() {
		return environmentVersion;
	}

	public void setEnvironmentVersion(String environmentVersion) {
		this.environmentVersion = environmentVersion;
	}

	@Override
	public String toString() {
		return "ModeletProfileParamsInfo{" + "profileName='" + profileName + '\'' + ", code='" + code + '\'' + ", paramValue='" + paramValue + '\''
				+ ", type='" + type + '\'' + ", delimitter='" + delimitter + '\'' + ", executionEnvironment='" + executionEnvironment + '\'' + ", environmentVersion='" + environmentVersion + '\'' + '}';
	}
}
