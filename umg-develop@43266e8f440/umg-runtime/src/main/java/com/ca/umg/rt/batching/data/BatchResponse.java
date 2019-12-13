/**
 * 
 */
package com.ca.umg.rt.batching.data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author chandrsa
 * 
 */
public class BatchResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    private String batchId;
    private String fileName;
    private List<Map<String, Object>> responses;

    public List<Map<String, Object>> getResponses() {
        return responses;
    }

    public void setResponses(List<Map<String, Object>> responses) {
        this.responses = responses;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
