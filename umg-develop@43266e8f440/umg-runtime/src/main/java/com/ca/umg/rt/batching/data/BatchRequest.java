/**
 * 
 */
package com.ca.umg.rt.batching.data;

import java.util.List;
import java.util.Map;

/**
 * @author chandrsa
 * 
 */
public class BatchRequest {
    private int requestCount;
    private List<Map<String, Object>> requests;

    public int getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(int requestCount) {
        this.requestCount = requestCount;
    }

    public List<Map<String, Object>> getRequests() {
        return requests;
    }

    public void setRequests(List<Map<String, Object>> requests) {
        this.requests = requests;
    }
}
