package com.ca.umg.rt.core.metrics.bo;

import java.util.List;

import com.ca.umg.rt.core.metrics.info.MetricsInfo;

public interface MetricsBO {
    public List<MetricsInfo> getMetrics();
    
    public MetricsInfo getMetrics(String name);
}
