package com.ca.umg.rt.batching.data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class BatchFileStatusResponse implements Serializable {

    /**
     * generated Serial Version id
     */

    private static final long serialVersionUID = -775975404814197844L;

    private Map<String, Object> header;

    private List<Map<String, Object>> data;

    public Map<String, Object> getHeader() {
        return header;
    }

    public void setHeader(Map<String, Object> header) {
        this.header = header;
    }

    public List<Map<String, Object>> getData() {
        return data;
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }

   
}
