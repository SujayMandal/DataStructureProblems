/**
 * 
 */
package com.ca.framework.core.util;

import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * @author elumalas
 * 
 */
public final class DateConversionUtil {

	public static final String UTC_TIME_ZONE = "UTC";

	public static final String EST_TIME_ZONE = "EST";

	private DateConversionUtil() {
	}

	public static DateTime convertEstToUtcTimeZone(DateTime lastModifiedDate) {
		return lastModifiedDate == null ? null : new DateTime(
				lastModifiedDate.getMillis(), DateTimeZone.forTimeZone(TimeZone
						.getTimeZone(UTC_TIME_ZONE)));
	}

	public static DateTime convertUtcToEstTimeZone(DateTime createdDate) {
		
		return createdDate == null ? null : new DateTime(createdDate.getMillis(),
				DateTimeZone.forTimeZone(TimeZone
						.getTimeZone(EST_TIME_ZONE)));
	}

	public static Long convertToUtcForAuditable(
			DateTime createdDate) {
		return createdDate == null ? new DateTime(DateTimeZone.UTC).getMillis()
				: createdDate.toDateTime(DateTimeZone.UTC).getMillis();
	}

}
