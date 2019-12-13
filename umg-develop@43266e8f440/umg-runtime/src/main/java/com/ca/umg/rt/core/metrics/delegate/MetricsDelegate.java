package com.ca.umg.rt.core.metrics.delegate;

import java.util.List;

import com.ca.umg.rt.core.metrics.info.MetricsInfo;

public interface MetricsDelegate {
    public List<MetricsInfo> getMetrics();

    public MetricsInfo getMetrics(String name);
}
