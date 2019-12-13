package com.ca.umg.rt.core.metrics.info;

import java.util.SortedMap;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
@SuppressWarnings("rawtypes")
public class MetricsInfo {

    private SortedMap<String, Gauge> gauges;
    private SortedMap<String, Counter> counters;
    private SortedMap<String, Histogram> histograms;
    private SortedMap<String, Meter> meters;
    private SortedMap<String, com.codahale.metrics.Timer> timers;
    /**
     * @return the gauges
     */
    public SortedMap<String, Gauge> getGauges() {
        return gauges;
    }
    /**
     * @param gauges the gauges to set
     */
    public void setGauges(SortedMap<String, Gauge> gauges) {
        this.gauges = gauges;
    }
    /**
     * @return the counters
     */
    public SortedMap<String, Counter> getCounters() {
        return counters;
    }
    /**
     * @param counters the counters to set
     */
    public void setCounters(SortedMap<String, Counter> counters) {
        this.counters = counters;
    }
    /**
     * @return the histograms
     */
    public SortedMap<String, Histogram> getHistograms() {
        return histograms;
    }
    /**
     * @param histograms the histograms to set
     */
    public void setHistograms(SortedMap<String, Histogram> histograms) {
        this.histograms = histograms;
    }
    /**
     * @return the meters
     */
    public SortedMap<String, Meter> getMeters() {
        return meters;
    }
    /**
     * @param meters the meters to set
     */
    public void setMeters(SortedMap<String, Meter> meters) {
        this.meters = meters;
    }
    /**
     * @return the timers
     */
    public SortedMap<String, com.codahale.metrics.Timer> getTimers() {
        return timers;
    }
    /**
     * @param timers the timers to set
     */
    public void setTimers(SortedMap<String, com.codahale.metrics.Timer> timers) {
        this.timers = timers;
    }
}
