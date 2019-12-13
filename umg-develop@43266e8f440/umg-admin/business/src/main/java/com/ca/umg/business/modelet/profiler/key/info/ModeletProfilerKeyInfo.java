package com.ca.umg.business.modelet.profiler.key.info;

import com.ca.framework.core.info.BaseInfo;

public class ModeletProfilerKeyInfo extends BaseInfo {

	private static final long serialVersionUID = 2638031932167492111L;
	private String name;
	private String code;
	private String type;
	private String delimitter;
	private String description;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
