package com.ca.umg.rt.core.metrics.bo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.umg.rt.core.metrics.info.MetricsInfo;

@Component
public class MetricsBOImpl implements MetricsBO {

    @Autowired
    private CacheRegistry cacheRegistry;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<MetricsInfo> getMetrics() {
        List<MetricsInfo> metricsList = new ArrayList<MetricsInfo>();
        MetricsInfo metrics = new MetricsInfo();
        Map<Object, Object> reporterMap = this.cacheRegistry.getMap("anish");
        metrics.setGauges((SortedMap) reporterMap.get("gauges"));
        metrics.setCounters((SortedMap) reporterMap.get("counters"));
        metrics.setHistograms((SortedMap) reporterMap.get("histograms"));
        metrics.setMeters((SortedMap) reporterMap.get("meters"));
        metrics.setTimers((SortedMap) reporterMap.get("timers"));
        metricsList.add(metrics);
        return metricsList;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public MetricsInfo getMetrics(String name) {
        MetricsInfo metrics = new MetricsInfo();
        Map<Object,Object> reporterMap = this.cacheRegistry.getMap("anish");
        metrics.setGauges((SortedMap)reporterMap.get("gauges"));
        metrics.setCounters((SortedMap)reporterMap.get("counters"));
        metrics.setHistograms((SortedMap)reporterMap.get("histograms"));
        metrics.setMeters((SortedMap)reporterMap.get("meters"));
        metrics.setTimers((SortedMap)reporterMap.get("timers"));
        return metrics;
    }
}
