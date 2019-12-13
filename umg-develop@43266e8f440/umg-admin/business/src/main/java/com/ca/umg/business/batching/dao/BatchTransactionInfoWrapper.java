package com.ca.umg.business.batching.dao;

import java.util.List;
import java.util.Map;

import com.ca.umg.business.batching.info.BatchTransactionInfo;

public class BatchTransactionInfoWrapper {

	private List<BatchTransactionInfo> batchTransactionInfoList;
	private BatchDashboardPageInfo pageInfo;
	private String searchResultMessage;
	private long toatlCount;
    private Map<String, String> tenantConfigsMap;

    public List<BatchTransactionInfo> getBatchTransactionInfoList() {
		return batchTransactionInfoList;
	}
	
	public void setBatchTransactionInfoList(List<BatchTransactionInfo> batchTransactionInfoList) {
		this.batchTransactionInfoList = batchTransactionInfoList;
	}
	
	public BatchDashboardPageInfo getPageInfo() {
		return pageInfo;
	}
	
	public void setPageInfo(BatchDashboardPageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}

	public String getSearchResultMessage() {
		return searchResultMessage;
	}

	public void setSearchResultMessage(String searchResultMessage) {
		this.searchResultMessage = searchResultMessage;
	}

	public long getToatlCount() {
		return toatlCount;
	}

	public void setToatlCount(long toatlCount) {
		this.toatlCount = toatlCount;
	}
	
    public Map<String, String> getTenantConfigsMap() {
        return tenantConfigsMap;
    }

    public void setTenantConfigsMap(Map<String, String> tenantConfigsMap) {
        this.tenantConfigsMap = tenantConfigsMap;
    }

}