package com.ca.umg.modelet.common;

import java.io.Serializable;

import javax.inject.Named;

import org.apache.commons.lang.SystemUtils;

@Named
public class SystemInfo implements Serializable {

	private static final long serialVersionUID = 168503649234805210L;
	private int port;

	private String serverType;

	private String logPath;

	private String sanPath;

	private String workspacePath;

	private String memberHost;

	private String execEnvironment;

	private String executionLanguage;

	private String modeletName;

	private int rServePort;

	private String rMode;

	private String status;

	private String OSinUse = System.getProperty("os.name").toLowerCase();

	private String modelIdentifier;

	private String modelName;

	private String modelVersion;

	private String localSanPath;

	private String profiler;

	public int getPort() {
		return port;
	}

	public void setPort(final int port) {
		this.port = port;
	}

	public String getServerType() {
		return serverType;
	}

	public void setServerType(final String serverType) {
		this.serverType = serverType;
	}

	public String getLogPath() {
		return logPath;
	}

	public void setLogPath(final String logPath) {
		this.logPath = logPath;
	}

	public String getSanPath() {
		return sanPath;
	}

	public void setSanPath(final String sanPath) {
		this.sanPath = sanPath;
	}

	public String getWorkspacePath() {
		return workspacePath;
	}

	public void setWorkspacePath(final String workspacePath) {
		this.workspacePath = workspacePath;
	}

	public String getMemberHost() {
		return memberHost;
	}

	public void setMemberHost(final String memberHost) {
		this.memberHost = memberHost;
	}

	public String getHostKey() {
		return getMemberHost() + "-" + getPort();
	}

	public String getExecEnvironment() {
		return execEnvironment;
	}

	public void setExecEnvironment(String execEnvironment) {
		this.execEnvironment = execEnvironment;
	}

	public String getExecutionLanguage() {
		return executionLanguage;
	}

	public void setExecutionLanguage(String executionLanguage) {
		this.executionLanguage = executionLanguage;
	}

	public String getModeletName() {
		return modeletName;
	}

	public void setModeletName(String modeletName) {
		this.modeletName = modeletName;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOSinUse() {
		return OSinUse;
	}

	public boolean isUnix() {
		if (this.OSinUse != null) {
			return (this.OSinUse.indexOf("nix") >= 0 || this.OSinUse.indexOf("nux") >= 0);
		}
		return false;
	}

	public String getModelIdentifier() {
		return modelIdentifier;
	}

	public void setModelIdentifier(String modelIdentifier) {
		this.modelIdentifier = modelIdentifier;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getModelVersion() {
		return modelVersion;
	}

	public void setModelVersion(String modelVersion) {
		this.modelVersion = modelVersion;
	}

	public String getLocalSanPath() {
		return localSanPath;
	}

	public void setLocalSanPath(String localSanPath) {
		this.localSanPath = localSanPath;
	}

	public String getProfiler() {
		return profiler;
	}

	public void setProfiler(String profiler) {
		this.profiler = profiler;
	}

	@Override
	public String toString() {
		return "SystemInfo{" + "port=" + port + ", serverType='" + serverType + '\'' + ", logPath='" + logPath + '\'' + ", sanPath='" + sanPath + '\''
				+ ", workspacePath='" + workspacePath + '\'' + ", memberHost='" + memberHost + '\'' + ", execEnvironment='" + execEnvironment + '\''
				+ ", executionLanguage='" + executionLanguage + '\'' + ", modeletName='" + modeletName + '\'' + ", rServePort=" + rServePort
				+ ", rMode='" + rMode + '\'' + ", status='" + status + '\'' + ", OSinUse='" + OSinUse + '\'' + ", modelIdentifier='" + modelIdentifier
				+ '\'' + ", modelName='" + modelName + '\'' + ", modelVersion='" + modelVersion + '\'' + ", localSanPath='" + localSanPath + '\''
				+ ", profiler='" + profiler + '\'' + '}';
	}
}