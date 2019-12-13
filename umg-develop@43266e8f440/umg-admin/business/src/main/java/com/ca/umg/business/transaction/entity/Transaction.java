package com.ca.umg.business.transaction.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.joda.time.DateTime;
import org.pojomatic.annotations.Property;

import com.ca.framework.core.db.domain.MultiTenantEntity;

@Entity
@Table(name = "UMG_RUNTIME_TRANSACTION")
@Audited
@SuppressWarnings("PMD.TooManyFields")
public class Transaction extends MultiTenantEntity {

    private static final long serialVersionUID = 7601811238670276117L;

    @Property
    @NotNull(message = "Client Transaction ID cannot be null.")
    @Column(name = "CLIENT_TRANSACTION_ID")
    private String clientTransactionID;

    @Property
    @NotNull(message = "Library Name cannot be null.")
    @Column(name = "LIBRARY_NAME")
    private String libraryName;

    @Property
    @NotNull(message = "Tenant model name cannot be null.")
    @Column(name = "VERSION_NAME")
    private String tenantModelName;

    @Property
    @NotNull(message = "Major version cannot be null.")
    @Column(name = "MAJOR_VERSION")
    private Integer majorVersion;

    @Property
    @NotNull(message = "Minor version cannot be null.")
    @Column(name = "MINOR_VERSION")
    private Integer minorVersion;

    @Property
    @NotNull(message = "Status cannot be null.")
    @Column(name = "STATUS")
    private String status;

    @Column(name = "TENANT_INPUT")
    @Lob
    private byte[] tenantInput;

    @Column(name = "TENANT_OUTPUT")
    @Lob
    private byte[] tenantOutput;

    @Column(name = "MODEL_INPUT")
    @Lob
    private byte[] modelInput;

    @Column(name = "MODEL_OUTPUT")
    @Lob
    private byte[] modelOutput;

    @Property
    @NotNull(message = "Run as of date cannot be null.")
    @Column(name = "RUN_AS_OF_DATE")
    private Long runAsOfDate;

    @Property
    @Column(name = "RUNTIME_CALL_START")
    private Long runtimeCallStart;

    @Property
    @Column(name = "RUNTIME_CALL_END")
    private Long runtimeCallEnd;

    @Property
    @Column(name = "MODEL_CALL_START")
    private Long modelCallStart;

    @Property
    @Column(name = "MODEL_CALL_END")
    private Long modelCallEnd;

    @Property
    @Column(name = "IS_TEST")
    private boolean isTest;
    
    @Property
    @Column(name = "ERROR_CODE")
    private String errorCode;
    
    @Property
    @Column(name = "ERROR_DESCRIPTION")
    private byte[] errorDescription;
    
    @Property
    @Column(name = "OP_VALIDATION")
    private boolean isOpValidate;
    
    @Property
    @Column(name = "ACCEPTABLEVALUES_VALIDATION")
    private boolean isAcceptValuesValidate;
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

    public String getClientTransactionID() {
        return clientTransactionID;
    }

    public void setClientTransactionID(String clientTransactionID) {
        this.clientTransactionID = clientTransactionID;
    }

    public String getTenantModelName() {
        return tenantModelName;
    }

    public void setTenantModelName(String tenantModelName) {
        this.tenantModelName = tenantModelName;
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

    public byte[] getTenantInput() {
        return tenantInput;
    }

    public void setTenantInput(byte[] tenantInput) {
        this.tenantInput = tenantInput;
    }

    public byte[] getTenantOutput() {
        return tenantOutput;
    }

    public void setTenantOutput(byte[] tenantOutput) {
        this.tenantOutput = tenantOutput;
    }

    public byte[] getModelInput() {
        return modelInput;
    }

    public void setModelInput(byte[] modelInput) {
        this.modelInput = modelInput;
    }

    public byte[] getModelOutput() {
        return modelOutput;
    }

    public void setModelOutput(byte[] modelOutput) {
        this.modelOutput = modelOutput;
    }

    public DateTime getRunAsOfDate() {
        return runAsOfDate == null ? null : new DateTime(runAsOfDate.longValue());
    }

    public void setRunAsOfDate(DateTime runAsOfDate) {
        this.runAsOfDate = runAsOfDate == null ? null : runAsOfDate.getMillis();
    }

    public String getLibraryName() {
        return libraryName;
    }

    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName;
    }

    public boolean isTestTransaction() {
        return isTest;
    }

    public void setTestTransaction(boolean isTest) {
        this.isTest = isTest;
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

    public boolean isOpValidation() {
		return isOpValidate;
	}

	public void setOpValidation(boolean isOpValidation) {
		this.isOpValidate = isOpValidation;
	}
	

	public boolean isAcceptValuesValidation() {
		return isAcceptValuesValidate;
	}

	public void setAcceptValuesValidation(boolean isAcceptValuesValidate) {
		this.isAcceptValuesValidate = isAcceptValuesValidate;
	}


}
