package com.ca.umg.business.modelet.profiler.key.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.pojomatic.annotations.Property;

import com.ca.framework.core.db.domain.AbstractAuditable;

@Entity
@Table(name = "MODELET_PROFILER_KEY")
public class ModeletProfilerKey extends AbstractAuditable {

	private static final long serialVersionUID = -4559590099468120570L;
	@Column(name = "NAME")
	@Property
	private String name;

	@Column(name = "CODE")
	@Property
	private String code;

	@Column(name = "TYPE")
	@Property
	private String type;

	@Column(name = "DELIMITTER")
	@Property
	private String delimitter;

	@Column(name = "DESCRIPTION")
	@Property
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
