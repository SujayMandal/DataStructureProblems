package com.ca.umg.business.transaction.info;

import java.util.List;

import com.ca.umg.business.common.info.PagingInfo;
import com.ca.umg.business.transaction.mongo.info.TransactionDocumentInfo;

public class TransactionWrapper {

    private PagingInfo pagingInfo;

    private List<TransactionInfo> transactionInfoList;

    private List<String> libraryNameList;

    private List<String> tenantModelNameList;

    private List<TransactionDocumentInfo> transactionDocumentInfos;
    
    private String searchResultMessage = "";
    
    private long totalCount;

    public List<TransactionInfo> getTransactionInfoList() {
        return transactionInfoList;
    }

    public PagingInfo getPagingInfo() {
        return pagingInfo;
    }

    public void setPagingInfo(PagingInfo pagingInfo) {
        this.pagingInfo = pagingInfo;
    }

    public void setTransactionInfoList(List<TransactionInfo> transactionInfoList) {
        this.transactionInfoList = transactionInfoList;
    }

    public List<String> getLibraryNameList() {
        return libraryNameList;
    }

    public void setLibraryNameList(List<String> libraryNameList) {
        this.libraryNameList = libraryNameList;
    }

    public List<String> getTenantModelNameList() {
        return tenantModelNameList;
    }

    public void setTenantModelNameList(List<String> tenantModelNameList) {
        this.tenantModelNameList = tenantModelNameList;
    }

    public List<TransactionDocumentInfo> getTransactionDocumentInfos() {
        return transactionDocumentInfos;
    }

    public void setTransactionDocumentInfos(List<TransactionDocumentInfo> transactionDocumentInfos) {
        this.transactionDocumentInfos = transactionDocumentInfos;
    }

	public String getSearchResultMessage() {
		return searchResultMessage;
	}

	public void setSearchResultMessage(String searchResultMessage) {
		this.searchResultMessage = searchResultMessage;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}
}
