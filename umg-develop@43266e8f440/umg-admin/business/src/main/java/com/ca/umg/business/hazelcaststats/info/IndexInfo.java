package com.ca.umg.business.hazelcaststats.info;

import java.io.Serializable;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public class IndexInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private Boolean unique;
	private Map<String, Object> key;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getUnique() {
		return unique;
	}

	public void setUnique(Boolean unique) {
		this.unique = unique;
	}

	public Map<String, Object> getKey() {
		return key;
	}

	public void setKey(Map<String, Object> key) {
		this.key = key;
	}

}
