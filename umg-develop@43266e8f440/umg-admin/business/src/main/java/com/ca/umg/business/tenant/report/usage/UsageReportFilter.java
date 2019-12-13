package com.ca.umg.business.tenant.report.usage;

import static com.google.common.base.Objects.toStringHelper;

import java.util.List;

import com.ca.umg.business.common.info.PagingInfo;

@SuppressWarnings("PMD")
public class UsageReportFilter extends PagingInfo {

	private static final long serialVersionUID = 1L;

	private String tenantModelName;

	private Integer majorVersion;

	private Integer minorVersion;

	private String fullVersion;

	private Long runAsOfDateFrom;

	private Long runAsOfDateTo;

	private String runAsOfDateFromString;

	private String runAsOfDateToString;

	private String transactionStatus;

	private long matchedTransactionCount;

	private String tenantCode;

	private String cancelRequestId;

	private boolean customDate;

	private boolean includeTest;

	private String searchString;

	private List<String> selectedTransactions;

	public long getMatchedTransactionCount() {
		return matchedTransactionCount;
	}

	public void setMatchedTransactionCount(final long matchedTransactionCount) {
		this.matchedTransactionCount = matchedTransactionCount;
	}

	public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(final String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public Integer getMajorVersion(){
		return majorVersion;
	}

	public void setMajorVersion(final Integer majorVersion){
		this.majorVersion = majorVersion;
	}

	public Integer getMinorVersion(){
		return minorVersion;
	}

	public void setMinorVersion(final Integer minorVersion){
		this.minorVersion = minorVersion;
	}

	public Long getRunAsOfDateFrom(){
		return runAsOfDateFrom;
	}

	public void setRunAsOfDateFrom(final Long runAsOfDateFrom){
		this.runAsOfDateFrom = runAsOfDateFrom;
	}

	public Long getRunAsOfDateTo(){
		return runAsOfDateTo;
	}

	public void setRunAsOfDateTo(final Long runAsOfDateTo){
		this.runAsOfDateTo = runAsOfDateTo;
	}

	public String getTenantModelName(){
		return tenantModelName;
	}

	public void setTenantModelName(final String tenantModelName){
		this.tenantModelName = tenantModelName;
	}

	public String getRunAsOfDateFromString(){
		return runAsOfDateFromString;
	}

	public void setRunAsOfDateFromString(final String runAsOfDateFromString){
		this.runAsOfDateFromString = runAsOfDateFromString;
	}

	public String getRunAsOfDateToString(){
		return runAsOfDateToString;
	}

	public void setRunAsOfDateToString(final String runAsOfDateToString){
		this.runAsOfDateToString = runAsOfDateToString;
	}

	public String getFullVersion(){
		return fullVersion;
	}

	public void setFullVersion(final String fullVersion){
		this.fullVersion = fullVersion;
	}

	public String getTenantCode() {
		return tenantCode;
	}

	public void setTenantCode(final String tenantCode) {
		this.tenantCode = tenantCode;
	}

	public String getCancelRequestId() {
		return cancelRequestId;
	}

	public void setCancelRequestId(final String cancelRequestId) {
		this.cancelRequestId = cancelRequestId;
	}

	public boolean isCustomDate() {
		return customDate;
	}

	public void setCustomDate(final boolean customDate) {
		this.customDate = customDate;
	}

	public boolean isIncludeTest() {
		return includeTest;
	}

	public void setIncludeTest(final boolean includeTest) {
		this.includeTest = includeTest;
	}

	@Override
	public void setSearchString(final String searchString) {
		this.searchString = searchString;
	}

	@Override
	public String getSearchString() {
		return searchString;
	}

	public List<String> getSelectedTransactions() {
		return selectedTransactions;
	}

	public void setSelectedTransactions(final List<String> selectedTransactions) {
		this.selectedTransactions = selectedTransactions;
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("Tenant Code", tenantCode).add("Tenant Model Name", tenantModelName).add("Major Version", majorVersion)
				.add("Minor Version", minorVersion).add("Include Test", includeTest).add("From Date", runAsOfDateFromString)
				.add("To Date", runAsOfDateToString).add("Custom Date", customDate).toString();
	}
}