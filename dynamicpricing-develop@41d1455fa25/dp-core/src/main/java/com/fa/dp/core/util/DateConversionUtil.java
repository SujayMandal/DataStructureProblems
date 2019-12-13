/**
 * 
 */
package com.fa.dp.core.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public final class DateConversionUtil {

    public static final String UTC_TIME_ZONE = "UTC";

    public static final String EST_TIME_ZONE = "EST";

    public static final String DATE_TIME_FORMAT = "dd-MMM-YY HH:mm:ss.SSS";

    public static final String DATE_TIME_FORMAT_PERMANENT_REPORT = "MM/dd/YYYY HH:mm:ss";

    public static final String DATE_TIME_FORMAT_RUN_DATE = "dd-MMM-yy HH:mm:ss.SSS";

    public static final String DATE_DD_MMM_YY = "dd-MMM-YY";

    public static final String DATE_YYYY_DD_MM = "YYYY-MM-dd";

    public static final String DATE_YYYY_M_D = "YYYY-M-d";

    public static final String ZIP_DATE_TIME_FORMAT = "ddMMMYYYY";

    public static final String SYSTEM_GENERATED_FILE_DATE_FORMAT_STR = "yyyyMMdd-HHmmss";

    public static final DateTimeZone EST_DATE_TIME_ZONE = DateTimeZone.forTimeZone(TimeZone.getTimeZone(EST_TIME_ZONE));

    public static final DateTimeZone UTC_DATE_TIME_ZONE = DateTimeZone.forTimeZone(TimeZone.getTimeZone(UTC_TIME_ZONE));

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern(DATE_TIME_FORMAT_PERMANENT_REPORT);

    public static final DateTimeFormatter US_DATE_TIME_FORMATTER = DateTimeFormat.forPattern(DATE_YYYY_DD_MM);

    public static final SimpleDateFormat US_SIMPLE_DATE_TIME_FORMATTER = new SimpleDateFormat(DATE_YYYY_DD_MM);

    public static final SimpleDateFormat DATE_YYYY_MM_DD_FORMATTER =  new SimpleDateFormat(RAClientConstants.DATE_FORMAT);

    public static final SimpleDateFormat DATE_YYYY_MMM_DD_FORMATTER =  new SimpleDateFormat(DATE_DD_MMM_YY);

    public static final java.time.format.DateTimeFormatter LOCAL_DATE_TIME_FORMATTER_JAVA = java.time.format.DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

    public static final java.time.format.DateTimeFormatter LOCAL_DATE_TIME_FORMATTER_RUN_DATE = java.time.format.DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_RUN_DATE);

    public static final java.time.format.DateTimeFormatter LOCAL_DATE_FORMATTER = java.time.format.DateTimeFormatter.ofPattern(DATE_DD_MMM_YY);

    public static final java.time.format.DateTimeFormatter SYSTEM_GENERATED_FILE_DATE_FORMAT = java.time.format.DateTimeFormatter.ofPattern(SYSTEM_GENERATED_FILE_DATE_FORMAT_STR);

    public static final DateTimeFormatter EST_DATE_TIME_FORMATTER = DATE_TIME_FORMATTER.withLocale(Locale.US).withZone(EST_DATE_TIME_ZONE);

    public static final DateTimeFormatter EST_DATE_TIME_FORMATTER_QA_REPORT = DateTimeFormat.forPattern(DATE_YYYY_DD_MM).withLocale(Locale.US).withZone(EST_DATE_TIME_ZONE);

    private DateConversionUtil() {
    }

    public static DateTime convertEstToUtcTimeZone(DateTime lastModifiedDate) {
        return lastModifiedDate == null ? null
                : new DateTime(lastModifiedDate.getMillis(), DateTimeZone.forTimeZone(TimeZone.getTimeZone(UTC_TIME_ZONE)));
    }

    public static DateTime convertUtcToEstTimeZone(DateTime createdDate) {
        return createdDate == null ? null
                : new DateTime(createdDate.getMillis(), DateTimeZone.forTimeZone(TimeZone.getTimeZone(EST_TIME_ZONE)));
    }

    public static Long convertToUtcForAuditable(DateTime createdDate) {
        return createdDate == null ? new DateTime(DateTimeZone.UTC).getMillis()
                : createdDate.toDateTime(DateTimeZone.UTC).getMillis();
    }

	public static Long getMillisFromUtcToEst(Long millis) {
		DateTime dateTime = new DateTime(millis, DateTimeZone.forTimeZone(TimeZone.getTimeZone(EST_TIME_ZONE)));
		return dateTime.getMillis();
	}

	public static DateTime getCurrentEstDate() {
        return new DateTime(EST_DATE_TIME_ZONE);
    }

    public static DateTime getEstDate(Long millis) {
        return new DateTime(millis, EST_DATE_TIME_ZONE);
    }

    public static String getEstDateText(Long millis) {
        return millis != null ? getEstDate(millis).toString(DATE_TIME_FORMATTER) : null;
    }
    
    public static DateTime getUTCDate(Long millis) {
        return new DateTime(millis, UTC_DATE_TIME_ZONE);
    }

    public static DateTime getCurrentUTCTime() {
        return new DateTime(UTC_DATE_TIME_ZONE);
    }
}
