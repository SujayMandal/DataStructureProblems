package com.ca.umg.business.batching.execution;

import java.util.List;
import java.util.Map;

public class ResponseObj {
    private String batchId;
    private int responseCount;
    private int requestCount;
    private Map<String, List<String>> status;
    private final Object lock = new Object();

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public int getResponseCount() {
        return responseCount;
    }

    public void setResponseCount(int responseCount) {
        this.responseCount = responseCount;
    }

    public Map<String, List<String>> getStatus() {
        return status;
    }

    public void setStatus(Map<String, List<String>> status) {
        this.status = status;
    }

    public void increaseCount() {
        synchronized (lock) {
            ++responseCount;
        }
    }

    public int getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(int requestCount) {
        this.requestCount = requestCount;
    }

    @Override
    public String toString() {
        return "ResponseObj [batchId=" + batchId + ", responseCount=" + responseCount + ", success count="
                + status.get(BatchDataContainer.SUCCESS).size() + ", failure count="
                + status.get(BatchDataContainer.FAILURE).size() + "]";
    }
}