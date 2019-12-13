package com.ca.umg.rt.core.flow.entity;
@SuppressWarnings("PMD")
public class TransactionLog {
    private String id;
    private String transactionId;
    private String libraryName;
    private String modelName;
    private Integer majorVersion;
    private Integer minorVersion;
    private String tenantInput;
    private String tenantOutput;
    private String modelInput;
    private String modelOutput;
    private long runAsOfDate;
    private long runtimeStart;
    private long runtimeEnd;
    private String status;
    private Integer isTest;
    private String errorCode;
    private byte[] errorDescription;
    //added for insertion from LogHandler
    private Long modelCallStart;
    private Long modelCallEnd;
    private Long modelExecutionTime;
    private Long modeletExecutionTime;
    private Long me2WaitingTime;
    //added to fix UMG-4500 Additional variables in Transaction header
    private String createdBy;
    
    private String transactionMode;
    
    private String freeMemory;
    private double cpuUsage;
    private String freeMemoryAtStart;
    private double cpuUsageAtStart;
    private String poolName;
    private String ipAndPort;
    private int noOfAttempts;
    private boolean isOpValidate;  
    private boolean isACceptValuesValidation;
    private String modelExecEnvName;	
    private int rServePort;
	public String getTransactionMode() {
        return transactionMode;
    }
    public void setTransactionMode(String transactionMode) {
        this.transactionMode = transactionMode;
    }
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }
    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }
    /**
     * @return the transactionId
     */
    public String getTransactionId() {
        return transactionId;
    }
    /**
     * @param transactionId the transactionId to set
     */
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    /**
     * @return the modelName
     */
    public String getModelName() {
        return modelName;
    }
    /**
     * @param modelName the modelName to set
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
    /**
     * @return the majorVersion
     */
    public Integer getMajorVersion() {
        return majorVersion;
    }
    /**
     * @param majorVersion the majorVersion to set
     */
    public void setMajorVersion(Integer majorVersion) {
        this.majorVersion = majorVersion;
    }
    /**
     * @return the minorVersion
     */
    public Integer getMinorVersion() {
        return minorVersion;
    }
    /**
     * @param minorVersion the minorVersion to set
     */
    public void setMinorVersion(Integer minorVersion) {
        this.minorVersion = minorVersion;
    }
    /**
     * @return the tenantInput
     */
    public String getTenantInput() {
        return tenantInput;
    }
    /**
     * @param tenantInput the tenantInput to set
     */
    public void setTenantInput(String tenantInput) {
        this.tenantInput = tenantInput;
    }
    /**
     * @return the runAsOfDate
     */
    public long getRunAsOfDate() {
        return runAsOfDate;
    }
    /**
     * @param runAsOfDate the runAsOfDate to set
     */
    public void setRunAsOfDate(long runAsOfDate) {
        this.runAsOfDate = runAsOfDate;
    }
    /**
     * @return the runtimeStart
     */
    public long getRuntimeStart() {
        return runtimeStart;
    }
    /**
     * @param runtimeStart the runtimeStart to set
     */
    public void setRuntimeStart(long runtimeStart) {
        this.runtimeStart = runtimeStart;
    }
    /**
     * @return the runtimeEnd
     */
    public long getRuntimeEnd() {
        return runtimeEnd;
    }
    /**
     * @param runtimeEnd the runtimeEnd to set
     */
    public void setRuntimeEnd(long runtimeEnd) {
        this.runtimeEnd = runtimeEnd;
    }
    public String getTenantOutput() {
        return tenantOutput;
    }
    public void setTenantOutput(String tenantOutput) {
        this.tenantOutput = tenantOutput;
    }
    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }
    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }
    public Integer getIsTest() {
        return isTest;
    }
    public void setIsTest(Integer isTest) {
        this.isTest = isTest;
    }
    public String getModelOutput() {
        return modelOutput;
    }
    public void setModelOutput(String modelOutput) {
        this.modelOutput = modelOutput;
    }
    public String getModelInput() {
        return modelInput;
    }
    public void setModelInput(String modelInput) {
        this.modelInput = modelInput;
    }
    public String getLibraryName() {
        return libraryName;
    }
    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName;
    }
    /**
     * @return the errorCode
     */
    public String getErrorCode() {
        return errorCode;
    }
    /**
     * @param errorCode the errorCode to set
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    /**
     * @return the errorDescription
     */
    public byte[] getErrorDescription() {
        return errorDescription;
    }
    /**
     * @param errorDescription the errorDescription to set
     */
    public void setErrorDescription(byte[] errorDescription) {
        this.errorDescription = errorDescription;
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
	
	//added to fix UMG-4500 Additional variables in Transaction header
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
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
	public String getPoolName() {
		return poolName;
	}
	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}
	public String getIpAndPort() {
		return ipAndPort;
	}
	public void setIpAndPort(String ipAndPort) {
		this.ipAndPort = ipAndPort;
	}
	public int getNoOfAttempts() {
		return noOfAttempts;
	}
	public void setNoOfAttempts(int noOfAttempts) {
		this.noOfAttempts = noOfAttempts;
	}
	 
    public boolean isOpValidation() {
		return isOpValidate;
	}
	public void setOpValidation(boolean isOpValidate) {
		this.isOpValidate = isOpValidate;
	}
	
	public boolean isACceptValuesValidation() {
		return isACceptValuesValidation;
	}
	public void setACceptValuesValidation(boolean isACceptValuesValidation) {
		this.isACceptValuesValidation = isACceptValuesValidation;
	}
	
	public String getModelExecEnvName() {
		return modelExecEnvName;
	}
	public void setModelExecEnvName(String modelExecEnvName) {
		this.modelExecEnvName = modelExecEnvName;
	}
	public int getrServePort() {
		return rServePort;
	}
	public void setrServePort(int rServePort) {
		this.rServePort = rServePort;
	}
	
}
