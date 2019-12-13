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

import static com.ca.umg.rt.metrics.reporter.HazelCastReporterFactoryBean.CACHE_REF;
import static com.ca.umg.rt.metrics.reporter.HazelCastReporterFactoryBean.CLOCK_REF;
import static com.ca.umg.rt.metrics.reporter.HazelCastReporterFactoryBean.DURATION_UNIT;
import static com.ca.umg.rt.metrics.reporter.HazelCastReporterFactoryBean.PERIOD;
import static com.ca.umg.rt.metrics.reporter.HazelCastReporterFactoryBean.RATE_UNIT;

import com.ryantenney.metrics.spring.reporter.AbstractReporterElementParser;

public class HazelCastReporterElementParser extends AbstractReporterElementParser {

	@Override
	public String getType() {
		return "hazelcast";
	}

	@Override
	protected Class<?> getBeanClass() {
		return HazelCastReporterFactoryBean.class;
	}

	@Override
	protected void validate(ValidationContext c) {
		c.require(PERIOD, DURATION_STRING_REGEX, "Period is required and must be in the form '\\d+(ns|us|ms|s|m|h|d)'");
		c.require(CACHE_REF);
		
		c.optional(CLOCK_REF);
		c.optional(RATE_UNIT, TIMEUNIT_STRING_REGEX, "Rate unit must be one of the enum constants from java.util.concurrent.TimeUnit");
		c.optional(DURATION_UNIT, TIMEUNIT_STRING_REGEX, "Duration unit must be one of the enum constants from java.util.concurrent.TimeUnit");

		c.rejectUnmatchedProperties();
	}

}
