package com.ca.umg.business.modelet.profiler.request.info;

import java.io.Serializable;
import java.util.Map;

public class ModeletProfilerRequest implements Serializable {

	private static final long serialVersionUID = -7124601894746853580L;
	private String id;
	private String name;
	private String executionEnvironmentId;
	private String executionEnvironment;
	private String environmentVersion;
	private String description;
	private Map<String, String> params;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExecutionEnvironmentId() {
		return executionEnvironmentId;
	}

	public void setExecutionEnvironmentId(String executionEnvironmentId) {
		this.executionEnvironmentId = executionEnvironmentId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
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
}
