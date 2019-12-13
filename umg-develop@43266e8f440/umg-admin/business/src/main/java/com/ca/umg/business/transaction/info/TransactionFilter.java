package com.ca.umg.business.transaction.info;

import com.ca.umg.business.common.info.PagingInfo;

@SuppressWarnings("PMD")
public class TransactionFilter extends PagingInfo {

	private static final long serialVersionUID = -6693082028313343492L;

	private String clientTransactionID;

	private String libraryName;

	private Integer majorVersion;

	private Integer minorVersion;

	private Long runAsOfDateFrom;

	private Long runAsOfDateTo;

	private String runAsOfDateFromString;

	private String runAsOfDateToString;

	private String tenantModelName;

	private String fullVersion;
	
	//added for RA search api
	private String raTransactionID;

	//added for umg-4698
	/**
	 * this is used as place holder for username field in search api
	 */
	private String createdBy;
	
	//added for umg-4698
	/**
	 * this accepts any of these 3 values "benchmark", "modeled", "ineligible"
	 */
	private String executionGroup;
	
	/**
	 * this can have value Any or Prod or Test
	 */
	private String transactionType;
	
	/**
	 * this can have value Any or Success or Failure
	 */
	private String transactionStatus;
	
	/**
	 * this can have Any or Batch or Online
	 */
	private String transactionMode;

	private String errorType;

	private String errorDescription;

	private String batchId;
	
	private String[] tenantNames;
	
	private String[] selectedTnt;
	
	private String selectionType;

	public String getErrorType(){
		return errorType;
	}

	public void setErrorType(String errorType){
		this.errorType = errorType;
	}

	public String getErrorDescription(){
		return errorDescription;
	}

	public void setErrorDescription(String errorDescription){
		this.errorDescription = errorDescription;
	}

	public String getClientTransactionID(){
		return clientTransactionID;
	}

	public void setClientTransactionID(String clientTransactionID){
		this.clientTransactionID = clientTransactionID;
	}

	public String getLibraryName(){
		return libraryName;
	}

	public void setLibraryName(String libraryName){
		this.libraryName = libraryName;
	}

	public Integer getMajorVersion(){
		return majorVersion;
	}

	public void setMajorVersion(Integer majorVersion){
		this.majorVersion = majorVersion;
	}

	public Integer getMinorVersion(){
		return minorVersion;
	}

	public void setMinorVersion(Integer minorVersion){
		this.minorVersion = minorVersion;
	}

	public Long getRunAsOfDateFrom(){
		return runAsOfDateFrom;
	}

	public void setRunAsOfDateFrom(Long runAsOfDateFrom){
		this.runAsOfDateFrom = runAsOfDateFrom;
	}

	public Long getRunAsOfDateTo(){
		return runAsOfDateTo;
	}

	public void setRunAsOfDateTo(Long runAsOfDateTo){
		this.runAsOfDateTo = runAsOfDateTo;
	}

	public String getTenantModelName(){
		return tenantModelName;
	}

	public void setTenantModelName(String tenantModelName){
		this.tenantModelName = tenantModelName;
	}

	public String getRunAsOfDateFromString(){
		return runAsOfDateFromString;
	}

	public void setRunAsOfDateFromString(String runAsOfDateFromString){
		this.runAsOfDateFromString = runAsOfDateFromString;
	}

	public String getRunAsOfDateToString(){
		return runAsOfDateToString;
	}

	public void setRunAsOfDateToString(String runAsOfDateToString){
		this.runAsOfDateToString = runAsOfDateToString;
	}

	public String getFullVersion(){
		return fullVersion;
	}

	public void setFullVersion(String fullVersion){
		this.fullVersion = fullVersion;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getCreatedBy() {
        return createdBy;
    }

    /* (non-Javadoc)
     * @see com.ca.framework.core.info.BaseInfo#setCreatedBy(java.lang.String)
     * this is used to set username field passed from search api
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getExecutionGroup() {
        return executionGroup;
    }

    public void setExecutionGroup(String executionGroup) {
        this.executionGroup = executionGroup;
    }

    public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public String getTransactionMode() {
		return transactionMode;
	}

	public void setTransactionMode(String transactionMode) {
		this.transactionMode = transactionMode;
	}


    public String getBatchId(){
		return batchId;
	}

	public void setBatchId(String batchId){
		this.batchId = batchId;
	}
	
	public String getRaTransactionID() {
		return raTransactionID;
	}

	public void setRaTransactionID(String raTransactionID) {
		this.raTransactionID = raTransactionID;
	}

	public String[] getTenantNames() {
		return tenantNames;
	}

	public void setTenantNames(String[] tenantNames) {
		this.tenantNames = tenantNames;
	}

	public String[] getSelectedTnt() {
		return selectedTnt;
	}

	public void setSelectedTnt(String[] selectedTnt) {
		this.selectedTnt = selectedTnt;
	}

	public String getSelectionType() {
		return selectionType;
	}

	public void setSelectionType(String selectionType) {
		this.selectionType = selectionType;
	}
}