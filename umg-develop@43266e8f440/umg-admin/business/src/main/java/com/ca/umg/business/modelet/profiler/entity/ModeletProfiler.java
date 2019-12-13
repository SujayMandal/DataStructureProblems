package com.ca.umg.business.modelet.profiler.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.pojomatic.annotations.Property;

import com.ca.framework.core.db.domain.AbstractAuditable;
import com.ca.umg.business.execution.entity.ModelExecutionEnvironment;

@Entity
@Table(name = "MODELET_PROFILER")
public class ModeletProfiler extends AbstractAuditable {

	private static final long serialVersionUID = -4208144166950757257L;
	@Column(name = "NAME")
    @Property
	private String name;
	
	@Column(name = "DESCRIPTION")
    @Property
	private String description;
	
	@ManyToOne
    @JoinColumn(name = "EXECUTION_ENV_ID")
    @Property
    private ModelExecutionEnvironment modelExecutionEnvironment;

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

	public ModelExecutionEnvironment getModelExecutionEnvironment() {
		return modelExecutionEnvironment;
	}

	public void setModelExecutionEnvironment(ModelExecutionEnvironment modelExecutionEnvironment) {
		this.modelExecutionEnvironment = modelExecutionEnvironment;
	}
	
	
}
