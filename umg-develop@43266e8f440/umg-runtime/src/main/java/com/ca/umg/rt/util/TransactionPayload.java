package com.ca.umg.rt.util;

import java.io.Serializable;
import java.util.Map;

/**
 * @author basanaga
 * 
 * This class used to store the ra transaction document 
 *
 */
@SuppressWarnings("PMD")
public class TransactionPayload implements Serializable{

    private static final long serialVersionUID = 3634977026368704076L;
    private String transactionId;

    private String bulkOnlineTimeStamp;
    private String clientTransactionID;
    private String tenantId;
    private String libraryName;
    private String versionName;
    private Integer majorVersion;
    private Integer minorVersion;
    private String status;
    private Long runAsOfDate;
    private Boolean test;
    private String createdBy;
    private Long createdDate;
    private String errorCode;
    private String errorDescription;
    private Long runtimeCallStart;
    private Long runtimeCallEnd;
    private Long modelCallStart;   
	private Long modelCallEnd;
    private Long modelExecutionTime;
    private Long modeletExecutionTime;
    private Long me2WaitingTime;  
    private String modellingEnv;
    private String execEnv;
    private String modeletServerHost;
    private Integer modeletServerPort;
    private String modeletServerMemberHost;
    private Integer modeletServerMemberPort;
    private String modeletPoolName;
    private String modeletPoolCriteria;
    private String modeletServerType;
    private String modeletServerContextPath;
    private Map<String, Long> metricData;
    private String freeMemory;
    private double cpuUsage;
    private String freeMemoryAtStart;
    private double cpuUsageAtStart;
    // UMG-4697
    private String executionGroup;

    private String transactionMode;
    private Boolean payloadStorage;   


	private int noOfAttempts;

    private String channel;

    private int rServePort;
    
    private boolean storeRLogs;
    public String getTransactionMode() {
        return transactionMode;
    }

    public void setTransactionMode(String transactionMode) {
        this.transactionMode = transactionMode;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getExecutionGroup() {
        return executionGroup;
    }

    public void setExecutionGroup(String executionGroup) {
        this.executionGroup = executionGroup;
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

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
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

    public Boolean isTest() {
        return test;
    }

    public void setTest(Boolean test) {
        this.test = test;
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

    public Boolean getTest() {
        return test;
    }

    public String getExecEnv() {
  		return execEnv;
  	}

  	public void setExecEnv(String execEnv) {
  		this.execEnv = execEnv;
  	}

    public String getModeletServerHost() {
        return modeletServerHost;
    }

    public void setModeletServerHost(String modeletServerHost) {
        this.modeletServerHost = modeletServerHost;
    }

    public Integer getModeletServerPort() {
        return modeletServerPort;
    }

    public void setModeletServerPort(Integer modeletServerPort) {
        this.modeletServerPort = modeletServerPort;
    }

    public String getModeletServerMemberHost() {
        return modeletServerMemberHost;
    }

    public void setModeletServerMemberHost(String modeletServerMemberHost) {
        this.modeletServerMemberHost = modeletServerMemberHost;
    }

    public Integer getModeletServerMemberPort() {
        return modeletServerMemberPort;
    }

    public void setModeletServerMemberPort(Integer modeletServerMemberPort) {
        this.modeletServerMemberPort = modeletServerMemberPort;
    }

    public String getModeletPoolName() {
        return modeletPoolName;
    }

    public void setModeletPoolName(String modeletPoolName) {
        this.modeletPoolName = modeletPoolName;
    }

    public String getModeletPoolCriteria() {
        return modeletPoolCriteria;
    }

    public void setModeletPoolCriteria(String modeletPoolCriteria) {
        this.modeletPoolCriteria = modeletPoolCriteria;
    }

    public String getModeletServerType() {
        return modeletServerType;
    }

    public void setModeletServerType(String modeletServerType) {
        this.modeletServerType = modeletServerType;
    }

    public String getModeletServerContextPath() {
        return modeletServerContextPath;
    }

    public void setModeletServerContextPath(String modeletServerContextPath) {
        this.modeletServerContextPath = modeletServerContextPath;
    }

    public Map<String, Long> getMetricData() {
        return metricData;
    }

    public void setMetricData(Map<String, Long> metricData) {
        this.metricData = metricData;
    }

    public double getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(double cpuUsage) {
        this.cpuUsage = cpuUsage;
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

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
    

    public Boolean getPayloadStorage() {
		return payloadStorage;
	}

	public void setPayloadStorage(Boolean payloadStorage) {
		this.payloadStorage = payloadStorage;
	}

	public String getBulkOnlineTimeStamp() {
		return bulkOnlineTimeStamp;
	}

	public void setBulkOnlineTimeStamp(String bulkOnlineTimeStamp) {
		this.bulkOnlineTimeStamp = bulkOnlineTimeStamp;
	}	
  
	public static long getSerialversionuid() {
		return serialVersionUID;
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
