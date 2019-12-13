package com.ca.umg.business.versiontest.bo;

import java.util.Map;

public class TenantInput {

    private Map<String, Object> header;
    private Map<String, Object> data;

    public Map<String, Object> getHeader() {
        return header;
    }

    public void setHeader(Map<String, Object> header) {
        this.header = header;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

}
