package com.ca.umg.rt.core.metrics.delegate;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.ca.umg.rt.core.metrics.bo.MetricsBO;
import com.ca.umg.rt.core.metrics.info.MetricsInfo;

@Component
public class MetricsDelegateImpl implements MetricsDelegate{

    @Inject
    private MetricsBO metricsBO;
    
    @Override
    public List<MetricsInfo> getMetrics() {
        return metricsBO.getMetrics();
    }

    @Override
    public MetricsInfo getMetrics(String name) {
        return metricsBO.getMetrics(name);
    }

}
