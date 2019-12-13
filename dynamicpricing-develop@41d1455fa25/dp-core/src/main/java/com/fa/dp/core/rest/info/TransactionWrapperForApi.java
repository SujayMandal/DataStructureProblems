package com.fa.dp.core.rest.info;

import java.util.List;

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
