package com.ca.umg.business.modelet.system.entity;

import com.ca.framework.core.db.domain.AbstractAuditable;
import com.ca.umg.business.modelet.profiler.entity.ModeletProfiler;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * The persistent class for the system_modelets database table.
 */
@Entity
@Table(name = "SYSTEM_MODELETS")
public class SystemModelet extends AbstractAuditable {

	private static final long serialVersionUID = 4998370371644967843L;
	@Column(name = "HOST_NAME")
	private String hostName;

	@Column(name = "PORT")
	private int port;

	@Column(name = "EXEC_LANGUAGE")
	private String execLanguage;

	@Column(name = "MEMBER_HOST")
	private String memberHost;

	@Column(name = "EXECUTION_ENVIRONMENT")
	private String executionEnvironment;

	@Column(name = "POOL_NAME")
	private String poolName;

	@Column(name = "R_SERVE_PORT")
	private int rServePort;

	@Column(name = "R_MODE")
	private String rMode;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "SYSTEM_MODELET_PROFILER_MAP", joinColumns = @JoinColumn(name = "SYSTEM_MODELET_ID"), inverseJoinColumns = @JoinColumn(name = "PROFILER_ID"))
	private ModeletProfiler modeletProfiler;

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getExecLanguage() {
		return execLanguage;
	}

	public void setExecLanguage(String execLanguage) {
		this.execLanguage = execLanguage;
	}

	public String getMemberHost() {
		return memberHost;
	}

	public void setMemberHost(String memberHost) {
		this.memberHost = memberHost;
	}

	public String getExecutionEnvironment() {
		return executionEnvironment;
	}

	public void setExecutionEnvironment(String executionEnvironment) {
		this.executionEnvironment = executionEnvironment;
	}

	public String getPoolName() {
		return poolName;
	}

	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}

	public int getrServePort() {
		return rServePort;
	}

	public void setrServePort(int rServePort) {
		this.rServePort = rServePort;
	}

	public String getrMode() {
		return rMode;
	}

	public void setrMode(String rMode) {
		this.rMode = rMode;
	}

	public ModeletProfiler getModeletProfiler() {
		return modeletProfiler;
	}

	public void setModeletProfiler(ModeletProfiler modeletProfiler) {
		this.modeletProfiler = modeletProfiler;
	}
}