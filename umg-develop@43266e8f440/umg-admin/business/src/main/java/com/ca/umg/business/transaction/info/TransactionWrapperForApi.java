package com.ca.umg.business.transaction.info;

import java.util.List;

import com.ca.umg.business.transaction.mongo.info.TransactionDocumentForApi;

public class TransactionWrapperForApi {
    
    private String searchResultMessage = "";
    
    private long totalCount;

    private List<TransactionDocumentForApi> transactions;
    
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

    public List<TransactionDocumentForApi> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionDocumentForApi> transactions) {
        this.transactions = transactions;
    }
}
