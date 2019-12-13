package com.ca.umg.modelet.common;

import java.util.List;

public class ModelRequestInfo {

    private List<FieldInfo> payload;

    private HeaderInfo headerInfo;

    private List<FieldInfo> output;

    public List<FieldInfo> getPayload() {
        return payload;
    }

    public void setPayload(final List<FieldInfo> payload) {
        this.payload = payload;
    }

    public HeaderInfo getHeaderInfo() {
        return headerInfo;
    }

    public void setHeaderInfo(HeaderInfo headerInfo) {
        this.headerInfo = headerInfo;
    }

    public List<FieldInfo> getOutput() {
        return output;
    }

    public void setOutput(List<FieldInfo> output) {
        this.output = output;
    }

}
