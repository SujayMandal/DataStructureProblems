/**
 * Copyright (C) 2012 Ryan W Tenney (ryan@10e.us)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ca.umg.rt.metrics.reporter;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.codahale.metrics.Clock;
import com.ryantenney.metrics.spring.reporter.AbstractScheduledReporterFactoryBean;

public class HazelCastReporterFactoryBean extends AbstractScheduledReporterFactoryBean<HazelCastReporter> {

	// Required
	public static final String PERIOD = "period";
	public static final String CACHE_REF = "cache-ref";

	// Optional
	public static final String CLOCK_REF = "clock-ref";
	public static final String DURATION_UNIT = "duration-unit";
	public static final String RATE_UNIT = "rate-unit";

	@Override
	public Class<HazelCastReporter> getObjectType() {
		return HazelCastReporter.class;
	}

	@Override
	protected HazelCastReporter createInstance() {
		final HazelCastReporter.Builder reporter = HazelCastReporter.forRegistry(getMetricRegistry());

		if (hasProperty(DURATION_UNIT)) {
			reporter.convertDurationsTo(getProperty(DURATION_UNIT, TimeUnit.class));
		}

		if (hasProperty(RATE_UNIT)) {
			reporter.convertRatesTo(getProperty(RATE_UNIT, TimeUnit.class));
		}

		reporter.filter(getMetricFilter());

		if (hasProperty(CLOCK_REF)) {
			reporter.withClock(getPropertyRef(CLOCK_REF, Clock.class));
		}

		return reporter.build(getPropertyRef(CACHE_REF, CacheRegistry.class));
	}

	@Override
	protected long getPeriod() {
		return convertDurationString(getProperty(PERIOD));
	}

	protected Locale parseLocale(String localeString) {
		final int underscore = localeString.indexOf('_');
		if (underscore == -1) {
			return new Locale(localeString);
		}
		else {
			return new Locale(localeString.substring(0, underscore), localeString.substring(underscore + 1));
		}
	}

}
