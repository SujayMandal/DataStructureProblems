package com.ca.umg.business.transaction.mongo.info;

import org.apache.commons.lang.StringUtils;

import com.ca.umg.business.transaction.info.TransactionVersionInfo;

@SuppressWarnings({ "PMD.TooManyFields", "PMD.ExcessivePublicCount" })
public class TransactionDocumentInfo {

    private String transactionId;
    private String clientTransactionID;
    private String libraryName;
    private String versionName;
    private Integer majorVersion;
    private Integer minorVersion;
    private String status;
    private Long runAsOfDate;
    private boolean test;
    private String errorCode;
    private String errorDescription;
    private String createdBy;
    private Long createdDate;
    private String runAsOfDateTime;
    private String createdDateTime;
    private String environment;
    private Long runtimeCallStart;
    private Long runtimeCallEnd;
    private Long modelCallStart;
    private Long modelCallEnd;
    private Long modelExecutionTime;
    private Long modeletExecutionTime;
    private Long me2WaitingTime;
    private String environmentVersion;
    private String tenantId;
    //Adding for UMG-4702
    private String executionGroup;
    private TransactionVersionInfo transactionVersionInfo;
    private String transactionMode;
    private String modeletServerHost;
    private String modeletServerPort;
    private String modeletHostPortInfo;
    private String modeletPoolName;
    private String freeMemory;
    private double cpuUsage;
    private String freeMemoryAtStart;
    private double cpuUsageAtStart;
    private int noOfAttempts;
    private Boolean payloadStorage;    
    
    private String modellingEnv; 
	private String execEnv;	
    private int rServePort;
    
    private boolean storeRLogs;
    
	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getClientTransactionID() {
        return clientTransactionID;
    }

    public void setClientTransactionID(String clientTransactionID) {
        this.clientTransactionID = clientTransactionID;
    }

    public String getLibraryName() {
        return libraryName;
    }

    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public Integer getMajorVersion() {
        return majorVersion;
    }

    public void setMajorVersion(Integer majorVersion) {
        this.majorVersion = majorVersion;
    }

    public Integer getMinorVersion() {
        return minorVersion;
    }

    public void setMinorVersion(Integer minorVersion) {
        this.minorVersion = minorVersion;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getRunAsOfDate() {
        return runAsOfDate;
    }

    public void setRunAsOfDate(Long runAsOfDate) {
        this.runAsOfDate = runAsOfDate;
    }

    public boolean isTest() {
        return test;
    }

    public void setTest(boolean test) {
        this.test = test;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public String getRunAsOfDateTime() {
        return runAsOfDateTime;
    }

    public void setRunAsOfDateTime(String runAsOfDateTime) {
        this.runAsOfDateTime = runAsOfDateTime;
    }

    public String getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(String createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public TransactionVersionInfo getTransactionVersionInfo() {
        return transactionVersionInfo;
    }

    public void setTransactionVersionInfo(TransactionVersionInfo transactionVersionInfo) {
        this.transactionVersionInfo = transactionVersionInfo;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public Long getRuntimeCallStart() {
        return runtimeCallStart;
    }

    public void setRuntimeCallStart(Long runtimeCallStart) {
        this.runtimeCallStart = runtimeCallStart;
    }

    public Long getRuntimeCallEnd() {
        return runtimeCallEnd;
    }

    public void setRuntimeCallEnd(Long runtimeCallEnd) {
        this.runtimeCallEnd = runtimeCallEnd;
    }

    public Long getModelCallStart() {
        return modelCallStart;
    }

    public void setModelCallStart(Long modelCallStart) {
        this.modelCallStart = modelCallStart;
    }

    public Long getModelCallEnd() {
        return modelCallEnd;
    }

    public void setModelCallEnd(Long modelCallEnd) {
        this.modelCallEnd = modelCallEnd;
    }

    public Long getModelExecutionTime() {
        return modelExecutionTime;
    }

    public void setModelExecutionTime(Long modelExecutionTime) {
        this.modelExecutionTime = modelExecutionTime;
    }

    public Long getModeletExecutionTime() {
        return modeletExecutionTime;
    }

    public void setModeletExecutionTime(Long modeletExecutionTime) {
        this.modeletExecutionTime = modeletExecutionTime;
    }

    public Long getMe2WaitingTime() {
        return me2WaitingTime;
    }

    public void setMe2WaitingTime(Long me2WaitingTime) {
        this.me2WaitingTime = me2WaitingTime;
    }

	public String getEnvironmentVersion() {
		return environmentVersion;
	}

	public void setEnvironmentVersion(String environmentVersion) {
		this.environmentVersion = environmentVersion;
	}

	public String getExecutionGroup() {
		return executionGroup;
	}

	public void setExecutionGroup(String executionGroup) {
		this.executionGroup = executionGroup;
	}

	public String getTransactionMode() {
		return transactionMode;
	}

	public void setTransactionMode(String transactionMode) {
		this.transactionMode = transactionMode;
	}

	public String getModeletServerHost() {
		return modeletServerHost;
	}

	public void setModeletServerHost(String modeletServerHost) {
		this.modeletServerHost = modeletServerHost;
	}

	public String getModeletServerPort() {
		return modeletServerPort;
	}

	public void setModeletServerPort(String modeletServerPort) {
		this.modeletServerPort = modeletServerPort;
	}

	public String getModeletHostPortInfo() {
		return modeletHostPortInfo;
	}

	
	public double getCpuUsage() {
		return cpuUsage;
	}

	public void setCpuUsage(double cpuUsage) {
		this.cpuUsage = cpuUsage;
	}

	public void setModeletHostPortInfo(String modeletServerHost, String modeletServerPort, int rServePort) {
		int comp = 0;
		if(StringUtils.isBlank(modeletServerHost) || StringUtils.isBlank(modeletServerPort)) {
			this.modeletHostPortInfo = "";

		}
		else {
			if(rServePort != comp){
				this.modeletHostPortInfo = modeletServerHost + ":" + modeletServerPort + "/" + rServePort;
			} else {
				this.modeletHostPortInfo = modeletServerHost + ":" + modeletServerPort;
			}
		}
	}

    public String getModeletPoolName() {
        return modeletPoolName;
    }

    public void setModeletPoolName(String modeletPoolName) {
        this.modeletPoolName = modeletPoolName;
    }

	public String getFreeMemory() {
		return freeMemory;
	}

	public void setFreeMemory(String freeMemory) {
		this.freeMemory = freeMemory;
	}

	public String getFreeMemoryAtStart() {
		return freeMemoryAtStart;
	}

	public void setFreeMemoryAtStart(String freeMemoryAtStart) {
		this.freeMemoryAtStart = freeMemoryAtStart;
	}

	public double getCpuUsageAtStart() {
		return cpuUsageAtStart;
	}

	public void setCpuUsageAtStart(double cpuUsageAtStart) {
		this.cpuUsageAtStart = cpuUsageAtStart;
	}

	public int getNoOfAttempts() {
		return noOfAttempts;
	}

	public void setNoOfAttempts(int noOfAttempts) {
		this.noOfAttempts = noOfAttempts;
	}
	public Boolean getPayloadStorage() {
		return payloadStorage;
	}

	public void setPayloadStorage(Boolean payloadStorage) {
		this.payloadStorage = payloadStorage;
	}
	
	public String getExecEnv() {
		return execEnv;
	}

	public void setExecEnv(String execEnv) {
		this.execEnv = execEnv;
	}
	
	public String getModellingEnv() {
		return modellingEnv;
	}

	public void setModellingEnv(String modellingEnv) {
		this.modellingEnv = modellingEnv;
	}

	public int getrServePort() {
		return rServePort;
	}

	public void setrServePort(int rServePort) {
		this.rServePort = rServePort;
	}

	public boolean isStoreRLogs() {
		return storeRLogs;
	}

	public void setStoreRLogs(boolean storeRLogs) {
		this.storeRLogs = storeRLogs;
	}

}
