package com.ca.modelet;

import org.pojomatic.annotations.PojomaticPolicy;
import org.pojomatic.annotations.Property;

import com.ca.pool.model.ExecutionLanguage;

@SuppressWarnings("PMD")
public class ModeletClientInfo extends BaseModel {

	private static final long serialVersionUID = -1613205814624420109L;
	@Property(policy = PojomaticPolicy.TO_STRING)
	private int port;

	private String serverType;

	@Property(policy = PojomaticPolicy.TO_STRING)
	private String host;

	private String contextPath;

	private String memberHost;

	private int memberPort;

	private String poolName;

	private String modeletStatus;

	private String executionLanguage;

	@Property(policy = PojomaticPolicy.TO_STRING)
	private String loadedModel;

	@Property(policy = PojomaticPolicy.TO_STRING)
	private String loadedModelVersion;

	private String modelLibraryVersionName;

	private String newPoolName;

	// holds tenant code of the previous execution
	private String tenantCode;

	// holds the request type of previous execution
	private String requestMode;

	private Integer poolWaitTimeOut;

	private Boolean isStartedNew = Boolean.TRUE;

	private String execEnvironment;

	private String modeletName;

	private int rServePort;

	private String rMode;

	private String profiler;

	public Integer getPoolWaitTimeOut() {
		return poolWaitTimeOut;
	}

	public void setPoolWaitTimeOut(Integer poolWaitTimeOut) {
		this.poolWaitTimeOut = poolWaitTimeOut;
	}

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

	public String getHost() {
		return host;
	}

	public void setHost(final String host) {
		this.host = host;
	}

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(final String contextPath) {
		this.contextPath = contextPath;
	}

	public String getMemberHost() {
		return memberHost;
	}

	public void setMemberHost(final String memberHost) {
		this.memberHost = memberHost;
	}

	public int getMemberPort() {
		return memberPort;
	}

	public void setMemberPort(final int memberPort) {
		this.memberPort = memberPort;
	}

	public String getPoolName() {
		return poolName;
	}

	public void setPoolName(final String poolName) {
		this.poolName = poolName;
	}

	public String getModeletStatus() {
		return modeletStatus;
	}

	public void setModeletStatus(final String modeletStatus) {
		this.modeletStatus = modeletStatus;
	}

	public String getHostKey() {
		return getMemberHost() + "-" + getPort();
	}

	public String getMemberKey() {
		return getMemberKey(getMemberHost(), getMemberPort());
	}

	public static String getMemberKey(final String host, final int memberPort) {
		return host + "-" + memberPort;
	}

	public String getExecutionLanguage() {
		return executionLanguage;
	}

	public void setExecutionLanguage(final String executionLanguage) {
		this.executionLanguage = executionLanguage;
	}

	public void setLoadedModel(final String loadedModel) {
		this.loadedModel = loadedModel;
	}

	public String getLoadedModel() {
		return loadedModel;
	}

	public String getLoadedModelVersion() {
		return loadedModelVersion;
	}

	public void setLoadedModelVersion(final String loadedModelVersion) {
		this.loadedModelVersion = loadedModelVersion;
	}

	public void setModelLibraryVersionName(final String modelLibraryVersionName) {
		this.modelLibraryVersionName = modelLibraryVersionName;
	}

	public String getModelLibraryVersionName() {
		return modelLibraryVersionName;
	}

	public String getNewPoolName() {
		return newPoolName;
	}

	public void setNewPoolName(final String newPoolName) {
		this.newPoolName = newPoolName;
	}

	public Boolean getIsStartedNew() {
		return isStartedNew;
	}

	public void setIsStartedNew(Boolean isStartedNew) {
		this.isStartedNew = isStartedNew;
	}

	public String getExecEnvironment() {
		return execEnvironment;
	}

	public void setExecEnvironment(String execEnvironment) {
		this.execEnvironment = execEnvironment;
	}

	public String getString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("Host : " + getHost())
				.append(" Member Host : " + getMemberHost())
				.append(" Log Port : " + getPort())
				.append(" Member Port : " + getMemberPort())
				.append(" Execution Langugage : " + getExecutionLanguage())
				.append(" Pool Name : " + getPoolName())
				.append(" Modelet Status : " + getModeletStatus())
				.append(" Tenant : ").append(tenantCode)
				.append(" Request Mode : ").append(requestMode)
				.append("Execution Environment : ").append(execEnvironment);

		if (getExecutionLanguage() != null
				&& getExecutionLanguage().equalsIgnoreCase(
						ExecutionLanguage.R.getValue())) {
			sb.append(" Loaded Model : " + getLoadedModel());
			sb.append(" Loaded Model Version : " + getLoadedModelVersion());
			sb.append(" Loaded Model Librarry Name : "
					+ getModelLibraryVersionName());
		}

		return sb.toString();
	}

	public String getLogMessage() {
		final StringBuilder sb = new StringBuilder();
		sb.append("Name : ").append(getModeletName());
		sb.append(" Modelet IP : ").append(getHost());
		sb.append(" Port : ").append(getPort());
		sb.append(" Pool Name : ").append(getPoolName());
		sb.append(" Language : ").append(getExecutionLanguage());
		sb.append(" Environment : ").append(getExecEnvironment());
		sb.append(" Mode : ").append(getrMode());

		if (getExecutionLanguage() != null
				&& getExecutionLanguage().equalsIgnoreCase(
						ExecutionLanguage.R.getValue())) {
			sb.append(" Loaded Model : " + getLoadedModel());
			sb.append(" Loaded Model Version : " + getLoadedModelVersion());
		}

		return sb.toString();
	}

	public String getTenantCode() {
		return tenantCode;
	}

	public void setTenantCode(String tenantCode) {
		this.tenantCode = tenantCode;
	}

	public String getRequestMode() {
		return requestMode;
	}

	public void setRequestMode(String requestMode) {
		this.requestMode = requestMode;
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

	public String getProfiler() {
		return profiler;
	}

	public void setProfiler(String profiler) {
		this.profiler = profiler;
	}
}