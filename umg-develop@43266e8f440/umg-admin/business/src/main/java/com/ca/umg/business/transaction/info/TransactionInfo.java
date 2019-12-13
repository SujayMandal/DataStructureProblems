package com.ca.umg.business.transaction.info;

import org.joda.time.DateTime;

@SuppressWarnings("PMD.TooManyFields")
public class TransactionInfo extends TransactionExecTimeInfo {

    private String clientTransactionID;
    private String libraryName;
    private Integer majorVersion;
    private Integer minorVersion;
    private String status;
    private String errorCode;
    private String errorDescription;
    private DateTime runAsOfDate;
    private String tenantModelName;
    private TransactionVersionInfo transactionVersionInfo;
    private byte[] tenantIp;
    private byte[] tenantOp;
    private byte[] modelIp;
    private byte[] modelOp;
    private String runAsOfDateTime;
    private String environment;
    private String transactionMode;
    
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

    public void setRunAsOfDate(DateTime runAsOfDate) {
        this.runAsOfDate = runAsOfDate;
    }

    public DateTime getRunAsOfDate() {
        return runAsOfDate;
    }

    public String getTenantModelName() {
        return tenantModelName;
    }

    public void setTenantModelName(String tenantModelName) {
        this.tenantModelName = tenantModelName;
    }

    public TransactionVersionInfo getTransactionVersionInfo() {
        return transactionVersionInfo;
    }

    public void setTransactionVersionInfo(TransactionVersionInfo transactionVersionInfo) {
        this.transactionVersionInfo = transactionVersionInfo;
    }

    public byte[] getTenantIp() {
        return tenantIp;
    }

    public void setTenantIp(byte[] tenantIp) {
        this.tenantIp = tenantIp;
    }

    public byte[] getTenantOp() {
        return tenantOp;
    }

    public void setTenantOp(byte[] tenantOp) {
        this.tenantOp = tenantOp;
    }

    public byte[] getModelIp() {
        return modelIp;
    }

    public void setModelIp(byte[] modelIp) {
        this.modelIp = modelIp;
    }

    public byte[] getModelOp() {
        return modelOp;
    }

    public void setModelOp(byte[] modelOp) {
        this.modelOp = modelOp;
    }

	public String getRunAsOfDateTime() {
		return runAsOfDateTime;
	}

	public void setRunAsOfDateTime(String runAsOfDateTime) {
		this.runAsOfDateTime = runAsOfDateTime;
	}

	public void setEnvironment(final String environment) {
		this.environment = environment;
	}
    
	public String getEnvironment() {
		return environment;
	}

	public String getTransactionMode() {
		return transactionMode;
	}

	public void setTransactionMode(String transactionMode) {
		this.transactionMode = transactionMode;
	}		
}