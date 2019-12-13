
package com.ca.umg.report.util;


import static java.io.File.separator;

import org.joda.time.DateTime;

public final class ReportUtil {
	
	private static final String DOT = ".";
	
	private static final String SLASH = separator;
	
	private ReportUtil() {
		
	}
	
	public static final String getFileNameWithoutExt(final String fileName) {
		String fileNameWithExt = null;
		if (fileName.contains(DOT)) {
			final int lastIndex = fileName.lastIndexOf(DOT);
			if (fileName.contains(SLASH)){
			    final int firstIndex = fileName.lastIndexOf(SLASH);
			    fileNameWithExt = fileName.substring(firstIndex,  lastIndex);
			}
			else{	
			fileNameWithExt = fileName.substring(0,  lastIndex);
			}
			
		} else {
			fileNameWithExt = fileName;
		}
		
		return fileNameWithExt;
	}
	
	public static final String getFileExt(final String fileName) {
		String ext = null;
		if (fileName.contains(DOT)) {
			final int lastIndex = fileName.lastIndexOf(DOT);
			ext = fileName.substring(lastIndex + 1);
		} else {
			ext = "";
		}
		
		return ext;
	}
	
	public static final String replaceFileExt(final String fileName, final String newExt) {
		String newFileName = null;
		if (fileName.contains(DOT)) {
			final String oldExt = getFileExt(fileName);
			newFileName = fileName.replace(oldExt, newExt);
		} else {
			newFileName = fileName;
		}
		
		return newFileName;
	}
	
	public static String getFormattedDate(final Long dateTime) {
/*		final SimpleDateFormat df = new SimpleDateFormat("yyyy-MMM-dd_hh-mm-ss", getDefault());
		String formattedDate = "";
		if (dateTime != null) {
			formattedDate = df.format(new Date(dateTime));
		}
*/
		DateTime dt = new DateTime(dateTime);
/*		dt.getYear());
		dt.getMonthOfYear());
		dt.getDayOfYear());
		dt.getHourOfDay());
		dt.getMinuteOfHour());
		dt.getSecondOfMinute());
*/		
		StringBuilder sb = new StringBuilder();
		
		sb.append(dt.getYear());

		if (dt.getMonthOfYear() <= 9) {
			sb.append("0").append(dt.getMonthOfYear());
		} else {
			sb.append(dt.getMonthOfYear());
		}
		
		if (dt.getDayOfMonth() <= 9) {
			sb.append("0").append(dt.getDayOfMonth());
		} else {
			sb.append(dt.getDayOfMonth());
		}
		
		sb.append("_");
		
		if (dt.getHourOfDay() <= 9) {
			sb.append("0").append(dt.getHourOfDay());
		} else {
			sb.append(dt.getHourOfDay());
		}
		
		if (dt.getMinuteOfHour() <= 9) {
			sb.append("0").append(dt.getMinuteOfHour());
		} else {
			sb.append(dt.getMinuteOfHour());
		}
		
		if (dt.getSecondOfMinute() <= 9) {
			sb.append("0").append(dt.getSecondOfMinute());
		} else {
			sb.append(dt.getSecondOfMinute());
		}
		
		return sb.toString();
	}
	
    public static String createAdminErrorMessage(final String errorCode, String message) {
    	String errorMessage = "Report generation failed with error code: XXXXX. Error message from Reporting engine: YYYYY";
    	errorMessage = errorMessage.replace("XXXXX", errorCode);
    	
    	if (message == null) {
    		message = "";
    	}

		errorMessage = errorMessage.replace("YYYYY", message);

    	return errorMessage;
    }
    
    public static String createAppErrorMessage(final String errorCode, String message) {
    	String errorMessage = "Report generation failed with error code: XXXXX. Please try after sometime. In case the issue persists, "
    			+ "please proceed with offline process and contact RA support @ X12345 or email ecma.support@altisource.com with error code.";
    	
    	errorMessage = errorMessage.replace("XXXXX", errorCode);

    	return errorMessage;
    }

}
