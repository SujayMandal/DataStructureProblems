package com.ca.umg.business.modelet.system.info;

import com.ca.framework.core.info.BaseInfo;
import com.ca.umg.business.modelet.profiler.info.ModeletProfilerInfo;

/**
 * The info class for the system modelets
 */
public class SystemModeletInfo extends BaseInfo {

	private static final long serialVersionUID = 4710818768734556277L;
	private String hostName;

	private int port;

	private String execLanguage;

	private String memberHost;

	private String executionEnvironment;

	private String poolName;

	private int rServePort;

	private String rMode;

	private ModeletProfilerInfo modeletProfiler;

	private String currentProfiler;

	private String modeletStatus;

	private Boolean uiStart;

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

	public ModeletProfilerInfo getModeletProfiler() {
		return modeletProfiler;
	}

	public void setModeletProfiler(ModeletProfilerInfo modeletProfiler) {
		this.modeletProfiler = modeletProfiler;
	}

	public String getCurrentProfiler() {
		return currentProfiler;
	}

	public void setCurrentProfiler(String currentProfiler) {
		this.currentProfiler = currentProfiler;
	}

	public String getModeletStatus() {
		return modeletStatus;
	}

	public void setModeletStatus(String modeletStatus) {
		this.modeletStatus = modeletStatus;
	}

	public Boolean getUiStart() {
		return uiStart;
	}

	public void setUiStart(Boolean uiStart) {
		this.uiStart = uiStart;
	}
}