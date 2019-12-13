package com.ca.umg.rt.metrics.reporter;

import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.codahale.metrics.Clock;
import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;

/**
 * A reporter which stores metrics in Hazelcast cache.
 */
public class HazelCastReporter extends ScheduledReporter {
    /**
     * Returns a new {@link Builder} for {@link HazelCastReporter}.
     *
     * @param registry
     *            the registry to report
     * @return a {@link Builder} instance for a {@link HazelCastReporter}
     */
    public static Builder forRegistry(MetricRegistry registry) {
        return new Builder(registry);
    }

    /**
     * A builder for {@link HazelCastReporter} instances. Defaults to using the default locale and time zone, writing to
     * {@code System.out}, converting rates to events/second, converting durations to milliseconds, and not filtering metrics.
     */
    public static class Builder {
        private final MetricRegistry registry;
        private Clock clock;
        private TimeUnit rateUnit;
        private TimeUnit durationUnit;
        private MetricFilter filter;

        private Builder(MetricRegistry registry) {
            this.registry = registry;
            this.clock = Clock.defaultClock();
            this.rateUnit = TimeUnit.SECONDS;
            this.durationUnit = TimeUnit.MILLISECONDS;
            this.filter = MetricFilter.ALL;
        }

        /**
         * Use the given {@link Clock} instance for the time.
         *
         * @param clock
         *            a {@link Clock} instance
         * @return {@code this}
         */
        public Builder withClock(Clock clock) {
            this.clock = clock;
            return this;
        }

        /**
         * Convert rates to the given time unit.
         *
         * @param rateUnit
         *            a unit of time
         * @return {@code this}
         */
        public Builder convertRatesTo(TimeUnit rateUnit) {
            this.rateUnit = rateUnit;
            return this;
        }

        /**
         * Convert durations to the given time unit.
         *
         * @param durationUnit
         *            a unit of time
         * @return {@code this}
         */
        public Builder convertDurationsTo(TimeUnit durationUnit) {
            this.durationUnit = durationUnit;
            return this;
        }

        /**
         * Only report metrics which match the given filter.
         *
         * @param filter
         *            a {@link MetricFilter}
         * @return {@code this}
         */
        public Builder filter(MetricFilter filter) {
            this.filter = filter;
            return this;
        }

        /**
         * Builds a {@link HazelCastReporter} with the given properties.
         *
         * @return a {@link HazelCastReporter}
         */
        public HazelCastReporter build(final CacheRegistry cache) {
            return new HazelCastReporter(registry, cache, clock, rateUnit, durationUnit, filter);
        }
    }

    private final Clock clock;
    private final CacheRegistry cache;
    private TimeUnit rateUnit;
    private TimeUnit durationUnit;

    private HazelCastReporter(MetricRegistry registry, CacheRegistry cache, Clock clock, TimeUnit rateUnit,
            TimeUnit durationUnit, MetricFilter filter) {
        super(registry, "hazelcast-reporter", filter, rateUnit, durationUnit);
        this.clock = clock;
        this.cache = cache;
        this.rateUnit = rateUnit;
        this.durationUnit = durationUnit;
    }

    @Override
    public void report(SortedMap<String, Gauge> gauges, SortedMap<String, Counter> counters,
            SortedMap<String, Histogram> histograms, SortedMap<String, Meter> meters,
            SortedMap<String, com.codahale.metrics.Timer> timers) {
        // logger.info("Started metrics reporting to hazelcast metrics reporter");
        // ObjectMapper mapper = new ObjectMapper().registerModule(new MetricsModule(this.rateUnit,this.durationUnit,true));
        // Map<String,Object> reporterMap = this.cache.getMap("anish");
        // reporterMap.put("metrics", mapper);

        // logger.info("Completed metrics reporting to hazelcast metrics reporter");
    }
}
