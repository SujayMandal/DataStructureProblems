package com.ca.umg.rt.batching.entity;

import org.pojomatic.annotations.Property;
@SuppressWarnings("PMD")
public class BatchTransaction {
    @Property
    private String status;

    @Property
    private Long startTime;

    @Property
    private Long endTime;

    @Property
    private Long totalRecords;

    @Property
    private Long successCount;

    @Property
    private Long failCount;
    
    @Property
    private Long notPickedCount;

    @Property
    private String id;

    @Property
    private String tenantId;

    @Property
    private String batchInputFileName;

    private String batchOutputFileName;

    private String createdBy;

    private Long createdDate;

    private String lastModifiedBy;

    private Long lastModifiedDate;
    
    private String transactionMode;  
    
    @Property
    private String user;
    
    @Property
    private String modelName;
    
    @Property
    private String modelVersion;    
    
    @Property
    private String execEnv;  

	@Property
    private String modellingEnv;
 
	@Property
    private String storeRlogs;
 
    
    public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
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

	public String getTransactionMode() {
        return transactionMode;
    }

    public void setTransactionMode(String transactionMode) {
        this.transactionMode = transactionMode;
    }

    public String getBatchInputFileName() {
        return batchInputFileName;
    }

    public void setBatchInputFileName(String batchFileName) {
        this.batchInputFileName = batchFileName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Long getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(Long totalRecords) {
        this.totalRecords = totalRecords;
    }

    public Long getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Long successCount) {
        this.successCount = successCount;
    }

    public Long getFailCount() {
        return failCount;
    }

    public void setFailCount(Long failCount) {
        this.failCount = failCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
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

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Long getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Long lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getBatchOutputFileName() {
        return batchOutputFileName;
    }

    public void setBatchOutputFileName(String batchOutputFileName) {
        this.batchOutputFileName = batchOutputFileName;
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

	public Long getNotPickedCount() {
		return notPickedCount;
	}

	public void setNotPickedCount(Long notPickedCount) {
		this.notPickedCount = notPickedCount;
	}

	public String getStoreRlogs() {
		return storeRlogs;
	}

	public void setStoreRlogs(String storeRlogs) {
		this.storeRlogs = storeRlogs;
	}

	
	
}
