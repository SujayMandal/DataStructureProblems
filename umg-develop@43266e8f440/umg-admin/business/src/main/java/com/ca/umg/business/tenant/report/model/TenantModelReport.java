package com.ca.umg.business.tenant.report.model;

import static com.ca.umg.business.tenant.report.model.TenantModelReportEnum.CLIENT_TRANSACTION_ID;
import static com.ca.umg.business.tenant.report.model.TenantModelReportEnum.CREATED_DATE;
import static com.ca.umg.business.tenant.report.model.TenantModelReportEnum.TRANSACTION_ID;

import java.util.List;
import java.util.Map;

import org.pojomatic.annotations.Property;

import com.google.common.base.Objects;

@SuppressWarnings({"PMD.TooManyFields"})
public class TenantModelReport {

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
	private Long createdDate;

	@Property
	private String versionName;

	@Property
	private int majorVersion;

	@Property
	private int minorVersion;

    @Property
    private String status;

    @Property
    private String errorMessage;
    
    @Property
    private String createdBy;
    
    @Property
    private boolean test;
    
    @Property
    private String modeletPoolCriteria;   
  
	@Property
    private boolean payloadStorage;;

    private List<TabularInfo> inputTabularInfo;

	private List<TabularInfo> outputTabularInfo;	
    
    private String addOnValidation;
    
    private String transactionType;

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(final String transactionId) {
		this.transactionId = transactionId;
	}

	public Map<String, Object> getTenantInput() {
		return tenantInput;
	}

	public void setTenantInput(final Map<String, Object> tenantInput) {
		this.tenantInput = tenantInput;
	}

	public Map<String, Object> getTenantOutput() {
		return tenantOutput;
	}

	public void setTenantOutput(final Map<String, Object> tenantOutput) {
		this.tenantOutput = tenantOutput;
	}

	public Map<String, Object> getModelInput() {
		return modelInput;
	}

	public void setModelInput(final Map<String, Object> modelInput) {
		this.modelInput = modelInput;
	}

	public Map<String, Object> getModelOutput() {
		return modelOutput;
	}

	public void setModelOutput(final Map<String, Object> modelOutput) {
		this.modelOutput = modelOutput;
	}

	public String getClientTransactionID() {
		return clientTransactionID;
	}

	public void setClientTransactionID(final String clientTransactionID) {
		this.clientTransactionID = clientTransactionID;
	}

	public Long getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(final Long createdDate) {
		this.createdDate = createdDate;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(final String versionName) {
		this.versionName = versionName;
	}

	public Map<String, Object> getReportData(final TenantModelReportEnum report) {
		Map<String, Object> reportData = null;
		switch (report) {
			case MODEL_INPUT:
				reportData = modelInput;
				break;
			case MODEL_OUTPUT:
				reportData = modelOutput;
				break;
			case TENANT_INPUT:
				reportData = tenantInput;
				break;
			case TENANT_OUTPUT:
				reportData = tenantOutput;
				break;
			default:
				break;
		}

		return reportData;
	}

	public List<TabularInfo> getInputTabularInfo() {
		return inputTabularInfo;
	}

	public void setInputTabularInfo(final List<TabularInfo> inputTabularInfo) {
		this.inputTabularInfo = inputTabularInfo;
	}

	public List<TabularInfo> getOutputTabularInfo() {
		return outputTabularInfo;
	}

	public void setOutputTabularInfo(final List<TabularInfo> outputTabularInfo) {
		this.outputTabularInfo = outputTabularInfo;
	}

	public int getMajorVersion() {
		return majorVersion;
	}

	public void setMajorVersion(final int majorVersion) {
		this.majorVersion = majorVersion;
	}

	public int getMinorVersion() {
		return minorVersion;
	}

	public void setMinorVersion(final int minorVersion) {
		this.minorVersion = minorVersion;
	}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    
    public String getCreatedBy() {
  		return createdBy;
  	}

  	public void setCreatedBy(String createdBy) {
  		this.createdBy = createdBy;
  	}

  	public boolean isTest() {
  		return test;
  	}

  	public void setTest(boolean test) {
  		this.test = test;
  	}

  
  	public boolean isPayloadStorage() {
  		return payloadStorage;
  	}

  	public void setPayloadStorage(boolean payloadStorage) {
  		this.payloadStorage = payloadStorage;
  	}

    public String getModeletPoolCriteria() {
  		return modeletPoolCriteria;
  	}

  	public void setModeletPoolCriteria(String modeletPoolCriteria) {
  		this.modeletPoolCriteria = modeletPoolCriteria;
  	}
  	
  	public String getAddOnValidation() {
		return addOnValidation;
	}

	public void setAddOnValidation(String addOnValidation) {
		this.addOnValidation = addOnValidation;
	}
	
	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
  	
 

	@Override
	public String toString() {
		return Objects.toStringHelper(this.getClass()).add(TRANSACTION_ID.getReportName(), getTransactionId())
				.add(CLIENT_TRANSACTION_ID.getReportName(), getClientTransactionID()).add(CREATED_DATE.getReportName(), getCreatedDate()).toString();
	}
}
