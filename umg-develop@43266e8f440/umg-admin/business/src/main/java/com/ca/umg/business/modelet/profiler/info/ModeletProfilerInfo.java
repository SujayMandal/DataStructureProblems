package com.ca.umg.business.modelet.profiler.info;

import com.ca.framework.core.info.BaseInfo;
import com.ca.umg.business.model.info.ModelExecutionEnvironmentInfo;

public class ModeletProfilerInfo extends BaseInfo {

	private static final long serialVersionUID = 8610440835698777088L;
	private String name;

	private String description;

	private ModelExecutionEnvironmentInfo modelExecutionEnvironment;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ModelExecutionEnvironmentInfo getModelExecutionEnvironment() {
		return modelExecutionEnvironment;
	}

	public void setModelExecutionEnvironment(ModelExecutionEnvironmentInfo modelExecutionEnvironment) {
		this.modelExecutionEnvironment = modelExecutionEnvironment;
	}

}
