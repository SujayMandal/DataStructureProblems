package com.ca.pool.model;

import static com.google.common.base.Objects.toStringHelper;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.Property;

import com.ca.framework.core.constants.PoolConstants;

public class TransactionCriteria implements Serializable, Comparable<TransactionCriteria> {

	private static final long serialVersionUID = -6110609676160387158L;

	@Property
	private String tenantCode;
	/**
	 * can have either of these two values 'online' or 'batch'
	 */
	@Property
	private String transactionRequestType;

	@Property
	private String executionLanguage;

	@Property
	private String executionLanguageVersion;
	/**
	 * this is synonym for tenantModelName/versionName/umgName/apiName
	 */
	@Property
	private String modelName;

	@Property
	private String modelVersion;
	/**
	 * can contain any of two values Eg:- 'test'(requests from test-bed/test-url) or
	 * 'prod' (requests for published versions)
	 */
	@Property
	private String transactionRequestMode;

	/**
	 * can have one of these three values 'HTTP', 'File' or 'Any'
	 */
	@Property
	private String transactionRequestChannel;

	/**
	 * used for setting the transaction id the value can be "Publishing-Test" if it
	 * is version creation or the value will be uuid for test/prod transactions
	 */
	@Property
	private String clientTransactionId;

	@Property
	private String umgTransactionId;
	/**
	 * added this to fix umg-4251 to set versionCreationTest flag to true if it is
	 * test transaction during version creation else the flag will be false
	 */
	@Property
	private Boolean isVersionCreationTest;

	@Property
	private String runAsData;

	@Property
	private String batchId;

	@Property
	private List<String> addOnValidation;

	/**
	 * Operating system of the server i.e. Windows or Linux
	 */
	@Property
	private String executionEnvironment;

	/**
	 * check sum value of the proccessing file
	 */
	@Property
	private String modelIdentifier;

	private transient List<String> classFieldsList;

	private transient String clientID;

	public String getTenantCode() {
		return tenantCode;
	}

	public void setTenantCode(String tenantCode) {
		this.tenantCode = tenantCode;
	}

	public String getExecutionLanguage() {
		return executionLanguage;
	}

	public void setExecutionLanguage(String executionLanguage) {
		this.executionLanguage = executionLanguage;
	}

	public String getExecutionLanguageVersion() {
		return executionLanguageVersion;
	}

	public void setExecutionLanguageVersion(String executionLanguageVersion) {
		this.executionLanguageVersion = executionLanguageVersion;
	}

	public String getTransactionRequestType() {
		return transactionRequestType;
	}

	public void setTransactionRequestType(String transactionRequestType) {
		this.transactionRequestType = transactionRequestType;
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

	public String getTransactionRequestMode() {
		return transactionRequestMode;
	}

	public void setTransactionRequestMode(String transactionRequestMode) {
		this.transactionRequestMode = transactionRequestMode;
	}

	public void setClassFieldsList(List<String> fieldList) {
		this.classFieldsList = fieldList;
	}

	public List<String> getClassFieldsList() {
		return this.classFieldsList;
	}

	public String getClientTransactionId() {
		return clientTransactionId;
	}

	public void setClientTransactionId(String clientTransactionId) {
		this.clientTransactionId = clientTransactionId;
	}

	public Boolean getIsVersionCreationTest() {
		return isVersionCreationTest;
	}

	public void setIsVersionCreationTest(Boolean isVersionCreationTest) {
		this.isVersionCreationTest = isVersionCreationTest;
	}

	public String getUmgTransactionId() {
		return umgTransactionId;
	}

	public void setUmgTransactionId(String umgTransactionId) {
		this.umgTransactionId = umgTransactionId;
	}

	public String getRunAsData() {
		return runAsData;
	}

	public void setRunAsData(String runAsData) {
		this.runAsData = runAsData;
	}

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public List<String> getAddOnValidation() {
		return addOnValidation;
	}

	public void setAddOnValidation(List<String> addOnValidation) {
		this.addOnValidation = addOnValidation;
	}

	@Override
	public String toString() {
		return toStringHelper(this.getClass()).add("Tenant Code:", tenantCode)
				.add("Request Type:", transactionRequestType).add("Execution Environment:", executionLanguage)
				.add("Execution Environment Version:", executionLanguageVersion).add("Model name:", modelName)
				.add("Model version:", modelVersion).add("Transaction Request Mode:", transactionRequestMode)
				.add("Run As Date:", runAsData).add("addOnValidation:", addOnValidation)
				.add("Channel:", transactionRequestChannel).toString();
	}

	@Override
	public final boolean equals(Object obj) {
		return Pojomatic.equals(this, obj);
	}

	@Override
	public final int hashCode() {
		return Pojomatic.hashCode(this);
	}

	private Integer compareParams(String thisParam1, String thatParam2) {
		Integer result = null;
		if ((StringUtils.equalsIgnoreCase(thisParam1, "any") && StringUtils.equalsIgnoreCase(thatParam2, "any"))
				|| (StringUtils.equals(thisParam1, thatParam2))) { // handles (any, any), (specific,specific)
			result = null;
		} else if (StringUtils.equalsIgnoreCase(thisParam1, "any") || StringUtils.equalsIgnoreCase(thatParam2, "any")) { // handles
																															// (specific,
																															// any)
																															// or
																															// (any,specific)
			result = Boolean.compare(StringUtils.equalsIgnoreCase(thisParam1, "any"),
					StringUtils.equalsIgnoreCase(thatParam2, "any"));
		} else {// -- handles (specific1, specific2)
			result = 0;
		}
		return result;
	}

	@Override
	public int compareTo(TransactionCriteria that) {
		Integer result = null;
		List<String> fieldList = this.getClassFieldsList();
		for (int i = 0; i < fieldList.size(); i++) {
			switch (fieldList.get(i)) {
			case PoolConstants.MODEL:
				result = compareParams(this.getModelName(), that.getModelName());
				break;
			case PoolConstants.TENANT:
				result = compareParams(this.getTenantCode(), that.getTenantCode());
				break;
			case PoolConstants.TRANSACTION_MODE:// test/prod
				result = compareParams(this.getTransactionRequestMode(), that.getTransactionRequestMode());
				break;
			case PoolConstants.TRANSACTION_TYPE: // online/batch
				result = compareParams(this.getTransactionRequestType(), that.getTransactionRequestType());
				break;
			case PoolConstants.CHANNEL: // HTTP/File/Any
				result = compareParams(this.getTransactionRequestChannel(), that.getTransactionRequestChannel());
				break;
			}

			if (result != null) {
				break;
			} else if (result == null && i == fieldList.size() - 1) {
				result = 0;
				break;
			}
		}
		return result;

	}

	public String getTransactionRequestChannel() {
		return transactionRequestChannel;
	}

	public void setTransactionRequestChannel(String transactionRequestChannel) {
		this.transactionRequestChannel = transactionRequestChannel;
	}

	public String getExecutionEnvironment() {
		return executionEnvironment;
	}

	public void setExecutionEnvironment(String executionEnvironment) {
		this.executionEnvironment = executionEnvironment;
	}

	public String getClientID() {
		return clientID;
	}

	public void setClientID(String clientID) {
		this.clientID = clientID;
	}

	public String getModelIdentifier() {
		return modelIdentifier;
	}

	public void setModelIdentifier(String modelIdentifier) {
		this.modelIdentifier = modelIdentifier;
	}

}
