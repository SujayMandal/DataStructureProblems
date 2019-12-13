/**
 * 
 */
package com.fa.dp.core.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RAClientUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(RAClientUtil.class);

	public static final String EST_TIME_ZONE = "America/New_York";

	private RAClientUtil() {

	}

	public static String getLikePattern(final String searchTerm) {
		StringBuilder pattern = new StringBuilder();
		pattern.append(RAClientConstants.CHAR_PERCENTAGE);
		if (StringUtils.isNotBlank(searchTerm)) {
			pattern.append(searchTerm.toLowerCase(Locale.getDefault()));
		}
		pattern.append(RAClientConstants.CHAR_PERCENTAGE);
		return pattern.toString();
	}

	public static Long utcDateStringToLong(String dateString) {
		Long millis = null;
		try {
			DateTimeFormatter format = ISODateTimeFormat.dateHourMinuteSecondMillis().withZoneUTC();
			DateTime dateTime = format.parseDateTime(dateString);
			millis = dateTime.getMillis();
		} catch (UnsupportedOperationException | IllegalArgumentException e) // NOPMD
		{
			LOGGER.error("Error while converting request date", e);
		}
		return millis;
	}

	public static String getDateFormatEpoch(long timeMillis, String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				StringUtils.isNotBlank(format) ? format : RAClientConstants.RA_UTC_DATE_FORMAT, Locale.getDefault());
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timeMillis);
		return dateFormat.format(cal.getTime());
	}

	public static long convertTimeToMills(String dateString, String format) {
		DateTimeFormatter formatter = DateTimeFormat
				.forPattern(StringUtils.isNotBlank(format) ? format : RAClientConstants.RA_CLIENT_DATE_FORMAT);
		DateTime dateTime = formatter.parseDateTime(dateString);
		return dateTime.getMillis();
	}
}
