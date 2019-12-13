package com.ca.umg.business.batching.report.usage;

import static com.google.common.base.Objects.toStringHelper;

import java.util.List;

import com.ca.umg.business.common.info.PagingInfo;

@SuppressWarnings("PMD")
public class BatchUsageReportFilter extends PagingInfo {

	private static final long serialVersionUID = 1L;

	private Long fromDate;

	private Long toDate;

	private String fromDateToString;

	private String toDateToString;
	
	private String batchId;
	
	private String inputFileName;
	
	private String pageSize;

	private String tenantCode;

	private String cancelRequestId;

	private boolean customDate;

	private boolean includeTest;

	private String searchString;

	private List<String> selectedTransactions;

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
	
	
	
	public Long getFromDate() {
		return fromDate;
	}

	public void setFromDate(Long fromDate) {
		this.fromDate = fromDate;
	}

	public Long getToDate() {
		return toDate;
	}

	public void setToDate(Long toDate) {
		this.toDate = toDate;
	}

	public String getFromDateToString() {
		return fromDateToString;
	}

	public void setFromDateToString(String fromDateToString) {
		this.fromDateToString = fromDateToString;
	}

	public String getToDateToString() {
		return toDateToString;
	}

	public void setToDateToString(String toDateToString) {
		this.toDateToString = toDateToString;
	}

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public String getInputFileName() {
		return inputFileName;
	}

	public void setInputFileName(String inputFileName) {
		this.inputFileName = inputFileName;
	}

	public int getPageSize() {
		return pageSize==null ? 0 : Integer.parseInt(pageSize);
	}

	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("Tenant Code", tenantCode).add("Include Test", includeTest).add("From Date", fromDateToString)
				.add("To Date", toDateToString).add("Custom Date", customDate).toString();
	}
}