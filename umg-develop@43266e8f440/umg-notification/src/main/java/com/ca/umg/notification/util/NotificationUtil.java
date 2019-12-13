package com.ca.umg.notification.util;

import static com.ca.umg.notification.NotificationConstants.EST_TIME_ZONE;
import static com.ca.umg.notification.NotificationConstants.EST_UMG_TIME_ZONE;
import static com.ca.umg.notification.NotificationConstants.ONE_YEAR;
import static com.ca.umg.notification.NotificationConstants.RA_MODEL_REQUEST_MAPPING;
import static com.ca.umg.notification.NotificationConstants.RA_MODEL_REQUEST_MAPPING_API;
import static com.ca.umg.notification.NotificationConstants.RA_MODEL_TENANCY_CHECK;
import static com.ca.umg.notification.NotificationConstants.UMG_EST_DATE_FORMAT;
import static java.util.Locale.getDefault;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class NotificationUtil {
	public static String getFormattedDate(final Long dateTime) {
		final Calendar now = Calendar.getInstance();
	    
	    final Calendar targetCalendar = Calendar.getInstance();
	    for (int field : new int[] {
	    		Calendar.YEAR, 
	    		Calendar.MONTH, 
	    		Calendar.DAY_OF_MONTH, 
	    		Calendar.HOUR, 
	    		Calendar.MINUTE, 
	    		Calendar.SECOND}) {
	        targetCalendar.set(field, now.get(field));
	    }
	    
	    targetCalendar.setTimeZone(TimeZone.getTimeZone("est"));
	    
		final SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy_HH_mm_ss", getDefault());
		String formattedDate = "";
		if (dateTime != null) {
			formattedDate = df.format(targetCalendar.getTime());
		}

		return formattedDate;
	}
	
	public static String getDateTimeFormatted(){
		String timeEST=null;
		timeEST = getDateFormatMillisForEst(getMillisFromUtcToEst(System.currentTimeMillis()), UMG_EST_DATE_FORMAT);
		return timeEST;
	}

	public static String getDateTimeFormatted(final Long millis){
		String timeEST=null;
		timeEST = getDateFormatMillisForEst(getMillisFromUtcToEst(millis), UMG_EST_DATE_FORMAT);
		return timeEST;
	}

	public static String getDateFormatMillisForEst(long timeMillis, String format) {
		String dateFormat = format;
		TimeZone estTZ = TimeZone.getTimeZone(EST_UMG_TIME_ZONE);
		
		if (dateFormat == null) {
			dateFormat = UMG_EST_DATE_FORMAT;
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
		sdf.setTimeZone(estTZ);
		DateTime dateTime = new DateTime(timeMillis, DateTimeZone.forTimeZone(estTZ));
		Calendar utcCal = Calendar.getInstance(estTZ);
		utcCal.setTimeInMillis(dateTime.getMillis());
		return sdf.format(utcCal.getTime());
	}

	public static Long getMillisFromUtcToEst(Long millis) {
		DateTime dateTime = null;
		dateTime = new DateTime(millis, DateTimeZone.forTimeZone(TimeZone.getTimeZone(EST_TIME_ZONE)));
		return dateTime.getMillis();
	}
	
	public static void main(String[] args) {
		
		TimeZone estTZ = TimeZone.getTimeZone("est");
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy_HH_mm_ss", Locale.getDefault());
		sdf.setTimeZone(estTZ);

		DateTime dateTime = new DateTime(System.currentTimeMillis());
		Calendar utcCal = Calendar.getInstance(estTZ);
		utcCal.setTimeInMillis(dateTime.getMillis());
		
		String timeEST=null;
		timeEST = getDateFormatMillisForEst(getMillisFromUtcToEst(System.currentTimeMillis()), UMG_EST_DATE_FORMAT);
		
		System.out.println(timeEST);
		
		System.out.println(sdf.format(utcCal.getTime()));
		
		System.out.println(getFormattedDate(System.currentTimeMillis()));
	}
	
	public static String getEncryptedIdFromURL(final String url) {
    	final int lastIndexOfModelApproval = url.lastIndexOf(RA_MODEL_REQUEST_MAPPING + RA_MODEL_REQUEST_MAPPING_API);
    	final int lengthOfModelApproval = (RA_MODEL_REQUEST_MAPPING + RA_MODEL_REQUEST_MAPPING_API).length();
    	return url.substring(lastIndexOfModelApproval + lengthOfModelApproval + 1, url.length());
	}
	
	public static String getEncryptedIdForTenantMatch(final String url) {
    	final int lastIndexOfModelTenancyCheck = url.lastIndexOf(RA_MODEL_REQUEST_MAPPING + RA_MODEL_TENANCY_CHECK);
    	final int lengthOfModelTenancyCheck = (RA_MODEL_REQUEST_MAPPING + RA_MODEL_TENANCY_CHECK).length();
    	return url.substring(lastIndexOfModelTenancyCheck + lengthOfModelTenancyCheck + 1, url.length());
	}
	
	public static long getOneYearFromDate(final long datetimeInMillies) {
		final DateTime dateTime = new DateTime(datetimeInMillies);
		dateTime.plusYears(ONE_YEAR);
		return dateTime.getMillis();
	}
}
