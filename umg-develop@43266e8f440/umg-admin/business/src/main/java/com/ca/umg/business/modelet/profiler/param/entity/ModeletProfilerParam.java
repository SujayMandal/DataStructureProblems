package com.ca.umg.business.modelet.profiler.param.entity;

import com.ca.framework.core.db.domain.AbstractAuditable;
import com.ca.umg.business.modelet.profiler.entity.ModeletProfiler;
import com.ca.umg.business.modelet.profiler.key.entity.ModeletProfilerKey;
import org.pojomatic.annotations.Property;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "MODELET_PROFILER_PARAM")
public class ModeletProfilerParam extends AbstractAuditable {

	private static final long serialVersionUID = -4176822741111278358L;
	@ManyToOne
	@JoinColumn(name = "PROFILER_ID")
	@Property
	private ModeletProfiler modeletProfiler;

	@ManyToOne
	@JoinColumn(name = "PROFILER_KEY_ID")
	@Property
	private ModeletProfilerKey modeletProfilerKey;

	@Column(name = "PARAM_VALUE")
	@Property
	private String paramValue;

	public ModeletProfiler getModeletProfiler() {
		return modeletProfiler;
	}

	public void setModeletProfiler(ModeletProfiler modeletProfiler) {
		this.modeletProfiler = modeletProfiler;
	}

	public ModeletProfilerKey getModeletProfilerKey() {
		return modeletProfilerKey;
	}

	public void setModeletProfilerKey(ModeletProfilerKey modeletProfilerKey) {
		this.modeletProfilerKey = modeletProfilerKey;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}
}
