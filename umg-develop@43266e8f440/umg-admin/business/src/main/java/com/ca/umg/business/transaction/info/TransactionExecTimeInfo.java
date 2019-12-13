package com.ca.umg.business.transaction.info;

import com.ca.framework.core.info.BaseInfo;

public class TransactionExecTimeInfo extends BaseInfo {

    private static final long serialVersionUID = 1L;

    private Long modelCallEnd;

    private Long modelCallStart;

    private Long runtimeCallStart;

    private Long runtimeCallEnd;



    public Long getModelCallStart() {
        return modelCallStart;
    }

    public void setModelCallStart(Long modelCallStart) {
        this.modelCallStart = modelCallStart;
    }

    public Long getRuntimeCallStart() {
        return runtimeCallStart;
    }

    public void setRuntimeCallStart(Long runtimeCallStart) {
        this.runtimeCallStart = runtimeCallStart;
    }

    public Long getRuntimeCallEnd() {
        return runtimeCallEnd;
    }

    public void setRuntimeCallEnd(Long runtimeCallEnd) {
        this.runtimeCallEnd = runtimeCallEnd;
    }

    public Long getModelCallEnd() {
        return modelCallEnd;
    }

    public void setModelCallEnd(Long modelCallEnd) {
        this.modelCallEnd = modelCallEnd;
    }

}
