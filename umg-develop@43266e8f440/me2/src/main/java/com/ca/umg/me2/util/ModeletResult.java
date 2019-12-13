package com.ca.umg.me2.util;

import com.ca.modelet.ModeletClientInfo;
import com.ca.pool.model.TransactionCriteria;

import java.util.Map;

public class ModeletResult {

    private Map<String, Object> modeletResponse;
    private ModeletClientInfo modeletClientInfo;
    private TransactionCriteria transactionCriteria;

    public ModeletClientInfo getModeletClientInfo() {
        return modeletClientInfo;
    }

    public void setModeletClientInfo(ModeletClientInfo modeletClientInfo) {
        this.modeletClientInfo = modeletClientInfo;
    }

    public TransactionCriteria getTransactionCriteria() {
        return transactionCriteria;
    }

    public void setTransactionCriteria(final TransactionCriteria transactionCriteria) {
        this.transactionCriteria = transactionCriteria;
    }

    public Map<String, Object> getModeletResponse() {
        return modeletResponse;
    }

    public void setModeletResponse(Map<String, Object> modeletResponse) {
        this.modeletResponse = modeletResponse;
    }
}