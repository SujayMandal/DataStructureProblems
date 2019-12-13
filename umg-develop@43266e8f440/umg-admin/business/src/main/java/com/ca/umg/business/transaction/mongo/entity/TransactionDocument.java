package com.ca.umg.business.transaction.mongo.entity;

import java.util.Map;

import javax.persistence.Basic;

import org.pojomatic.annotations.PojomaticPolicy;
import org.pojomatic.annotations.Property;

import com.ca.framework.core.bo.ModelType;
import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.report.ReportExceptionCodes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("PMD")
public class TransactionDocument {

    @Property
    private String transactionId;

    @Property
    private Map<String, Object> tenantInput;

    @Property
    private Map<String, Object> tenantOutput;

    @Property
    private Map<String, Object> modelInput;

    @Property
    private Map<String, Object> modelOutput;

    @Property
    private String clientTransactionID;

    @Property
    private String libraryName;

    @Property
    private String versionName;

    @Property
    private Integer majorVersion;

    @Property
    private Integer minorVersion;

    @Property
    private String status;

    @Property
    private Long runAsOfDate;

    @Property
    private boolean test;

    @Property
    private String errorCode;

    @Property
    private String errorDescription;
    
    //added execution group for umg-4698
    @Property
    private String executionGroup;

    @Basic
    private String createdBy;

    @Basic
    private Long createdDate;
    
    @Property
    private Long runtimeCallStart;
    
    @Property
    private Long runtimeCallEnd;
    
    @Property
    private Long modelCallStart;
    
    @Property
    private Long modelCallEnd;
    
    @Property
    private Long modelExecutionTime;
    
    @Property
    private Long modeletExecutionTime;
    
    @Property
    private Long me2WaitingTime;
    
    @Property
    private String environment;
    
    @Property
    private String environmentVersion;
    
    @Property
    private String tenantId;
    
    @Property
    private String transactionMode = ModelType.ONLINE.getType();
    
    @Property
    private String modeletServerHost;
    
    @Property
    private String modeletServerPort;
    
   
    private String modeletPoolName;
    
    @Property (policy = PojomaticPolicy.TO_STRING)
    private String freeMemory;
    
    @Property (policy = PojomaticPolicy.TO_STRING)
    private double cpuUsage;
    
    @Property (policy = PojomaticPolicy.TO_STRING)
    private String freeMemoryAtStart;
    
    @Property (policy = PojomaticPolicy.TO_STRING)
    private double cpuUsageAtStart;
    
    @Property (policy = PojomaticPolicy.TO_STRING)
    private int noOfAttempts;
    
    @Property
    private Boolean payloadStorage;

    @Property
    private String bulkOnlineTimeStamp;

    @Property
    private String channel; 

	@Property
    private String modellingEnv;
  

	@Property
    private String execEnv;

	@Property
    private int rServePort;
	
	@Property
    private boolean storeRLogs;
	

    public String getBulkOnlineTimeStamp() {
        return bulkOnlineTimeStamp;
    }

    public void setBulkOnlineTimeStamp(String bulkOnlineTimeStamp) {
        this.bulkOnlineTimeStamp = bulkOnlineTimeStamp;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

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

    public Map<String, Object> getTenantInput() {
        return tenantInput;
    }

    public void setTenantInput(Map<String, Object> tenantInput) {
        this.tenantInput = tenantInput;
    }

    public Map<String, Object> getTenantOutput() {
        return tenantOutput;
    }

    public void setTenantOutput(Map<String, Object> tenantOutput) {
        this.tenantOutput = tenantOutput;
    }

    public Map<String, Object> getModelInput() {
        return modelInput;
    }

    public void setModelInput(Map<String, Object> modelInput) {
        this.modelInput = modelInput;
    }

    public Map<String, Object> getModelOutput() {
        return modelOutput;
    }

    public void setModelOutput(Map<String, Object> modelOutput) {
        this.modelOutput = modelOutput;
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

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getExecutionGroup() {
        return executionGroup;
    }

    public void setExecutionGroup(String executionGroup) {
        this.executionGroup = executionGroup;
    }

    public String getEnvironmentVersion() {
        return environmentVersion;
    }

    public void setEnvironmentVersion(String environmentVersion) {
        this.environmentVersion = environmentVersion;
    }

    public String getTransactionMode() {
        return transactionMode;
    }

    public void setTransactionMode(String transactionMode) {
        if (transactionMode != null) {
            this.transactionMode = transactionMode;
        } else {
            this.transactionMode = ModelType.ONLINE.getType();
        }
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

    public String getModeletPoolName() {
        return modeletPoolName;
    }

    public void setModeletPoolName(String modeletPoolName) {
        this.modeletPoolName = modeletPoolName;
    }

    public double getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public double getCpuUsageAtStart() {
        return cpuUsageAtStart;
    }

    public void setCpuUsageAtStart(double cpuUsageAtStart) {
        this.cpuUsageAtStart = cpuUsageAtStart;
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


    public static String createJsonString(final TransactionDocument td) throws BusinessException {
        String jsonString = null;
        try {
            final ObjectMapper mapper = new ObjectMapper();
            jsonString = mapper.writeValueAsString(td);
        } catch (JsonProcessingException jpe) {
            BusinessException.newBusinessException(ReportExceptionCodes.REPORT_JSON_STR_CONVERTION.getErrorCode(),
                    new String[] { jpe.getLocalizedMessage() });
        }

        return jsonString;
    }

    public int getNoOfAttempts() {
        return noOfAttempts;
    }

    public void setNoOfAttempts(int noOfAttempts) {
        this.noOfAttempts = noOfAttempts;
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
