package com.ca.umg.business.modelet.profiler.entity;

import com.ca.framework.core.db.domain.AbstractAuditable;
import com.ca.umg.business.modelet.system.entity.SystemModelet;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "SYSTEM_MODELET_PROFILER_MAP")
public class SystemModeletProfilerMap extends AbstractAuditable {

	private static final long serialVersionUID = 1895232835387169330L;
	//bi-directional many-to-one association to ModeletProfiler
	@ManyToOne
	@JoinColumn(name = "PROFILER_ID")
	private ModeletProfiler modeletProfiler;

	//bi-directional many-to-one association to SystemModelet
	@ManyToOne
	@JoinColumn(name = "SYSTEM_MODELET_ID")
	private SystemModelet systemModelet;

	public ModeletProfiler getModeletProfiler() {
		return modeletProfiler;
	}

	public void setModeletProfiler(ModeletProfiler modeletProfiler) {
		this.modeletProfiler = modeletProfiler;
	}

	public SystemModelet getSystemModelet() {
		return systemModelet;
	}

	public void setSystemModelet(SystemModelet systemModelet) {
		this.systemModelet = systemModelet;
	}
}
