/**
 * 
 */
package com.ca.umg.business.util;

import static com.ca.framework.core.requestcontext.RequestContext.getRequestContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.exception.codes.FrameworkExceptionCodes;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.plugin.commons.excel.reader.constants.ExcelConstants;

/**
 * @author chandrsa
 * 
 */
public final class AdminUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AdminUtil.class);

    private static Map<String, String> dateFormatMap = new HashMap<String, String>();

    private AdminUtil() {
    }

    static {
        dateFormatMap.put("DD-MMM-YYYY", "dd-MMM-yyyy");
        dateFormatMap.put("MMM-DD-YYYY", "MMM-dd-yyyy");
        dateFormatMap.put("MM-DD-YYYY", "MM-dd-yyyy");
        dateFormatMap.put("DD-MM-YYYY", "dd-MM-yyyy");
        dateFormatMap.put("YYYY-MM-DD", "yyyy-MM-dd");
        dateFormatMap.put("YYYY-MMM-DD", "yyyy-MMM-dd");
        dateFormatMap.put("DD/MM/YYYY", "dd/MM/yyyy");
        dateFormatMap.put("DD/MMM/YYYY", "dd/MMM/yyyy");
        dateFormatMap.put("MMM/DD/YYYY", "MMM/dd/yyyy");
        dateFormatMap.put("MM/DD/YYYY", "MM/dd/yyyy");
        dateFormatMap.put("YYYY/MM/DD", "yyyy/MM/dd");
        dateFormatMap.put("YYYY/MMM/DD", "yyyy/MMM/dd");
        dateFormatMap.put("yyyy-MMM-dd HH:mm", "yyyy-MMM-dd HH:mm");
        dateFormatMap.put("yyyy-MM-dd HH:mm", "yyyy-MM-dd HH:mm");
    }

    public static String getLikePattern(final String searchTerm) {
        StringBuilder pattern = new StringBuilder();
        pattern.append(BusinessConstants.CHAR_PERCENTAGE);
        if (StringUtils.isNotBlank(searchTerm)) {
            pattern.append(searchTerm.toLowerCase(Locale.getDefault()));
        }
        pattern.append(BusinessConstants.CHAR_PERCENTAGE);
        return pattern.toString();
    }

    public static String generateUmgName(String name) {
        StringBuffer umgName = new StringBuffer(name);
        umgName.append(BusinessConstants.CHAR_HYPHEN)
                .append(getDateFormatMillis(DateTime.now().getMillis(), BusinessConstants.UMG_NAME_UTC_DATE_FORMAT));
        return umgName.toString();
    }

    public static int countOccurence(String input, String key) {
        int occurence = 0;
        int length = 0;
        String[] inputArray = null;
        if (StringUtils.isNotBlank(input) && StringUtils.isNotBlank(key)) {
            inputArray = StringUtils.split(input, key);
            length = inputArray.length;
            occurence = length > 0 ? length - 1 : 0;
        }
        return occurence;
    }

    public static String generateSyndDataTableName(String containerName) {
        return StringUtils.join(BusinessConstants.SYND_DATA_TABLE_PREFIX, containerName.toUpperCase(Locale.getDefault()));
    }

    public static byte[] convertStreamToByteArray(InputStream stream) throws SystemException {
        byte[] data = null;

        try {
            data = new byte[stream.available()];
            stream.read(data);
        } catch (IOException ioException) {
            throw new SystemException(FrameworkExceptionCodes.BSE000009,
                    new Object[] { "An error occurred while reading the stream", ioException });
        }
        return data;
    }

    public static String getDateFormatMillis(long timeMillis, String format) {
        TimeZone estTZ = TimeZone.getTimeZone(BusinessConstants.UTC_TIME_ZONE);
        SimpleDateFormat sdf = new SimpleDateFormat(BusinessConstants.UMG_NAME_UTC_DATE_FORMAT, Locale.getDefault());
        sdf.setTimeZone(estTZ);

        DateTime dateTime = new DateTime(timeMillis, DateTimeZone.forTimeZone(estTZ));
        Calendar utcCal = Calendar.getInstance(estTZ);
        utcCal.setTimeInMillis(dateTime.getMillis());
        return sdf.format(utcCal.getTime());
    }

    public static String getFormattedDate(long timeMillis, String format) {
        String dateFormate = null;
        if (format == null) {
            dateFormate = BusinessConstants.UMG_NAME_UTC_DATE_FORMAT;
        } else {
            dateFormate = format;
        }
        TimeZone estTZ = TimeZone.getTimeZone(BusinessConstants.UTC_TIME_ZONE);
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormate, Locale.getDefault());
        sdf.setTimeZone(estTZ);

        DateTime dateTime = new DateTime(timeMillis, DateTimeZone.forTimeZone(estTZ));
        Calendar utcCal = Calendar.getInstance(estTZ);
        utcCal.setTimeInMillis(dateTime.getMillis());
        return sdf.format(utcCal.getTime());

    }

    public static String getDateFormatMillisForEst(long timeMillis, String format) {
        String dateFormat = format;
        TimeZone estTZ = TimeZone.getTimeZone(BusinessConstants.EST_UMG_TIME_ZONE);
        if (dateFormat == null) {
            dateFormat = BusinessConstants.UMG_EST_DATE_FORMAT;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
        sdf.setTimeZone(estTZ);
        DateTime dateTime = new DateTime(timeMillis, DateTimeZone.forTimeZone(estTZ));
        Calendar utcCal = Calendar.getInstance(estTZ);
        utcCal.setTimeInMillis(dateTime.getMillis());
        return sdf.format(utcCal.getTime());
    }

    public static String getDateFormatMillisForUTC(long timeMillis, String format) {
        String dateFormat = format;
        TimeZone estTZ = TimeZone.getTimeZone(BusinessConstants.UTC_TIME_ZONE);
        if (dateFormat == null) {
            dateFormat = BusinessConstants.UMG_UTC_DATE_FORMAT;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
        sdf.setTimeZone(estTZ);
        DateTime dateTime = new DateTime(timeMillis, DateTimeZone.forTimeZone(estTZ));
        Calendar utcCal = Calendar.getInstance(estTZ);
        utcCal.setTimeInMillis(dateTime.getMillis());
        return sdf.format(utcCal.getTime());
    }

    public static String getDateFormatForEst(DateTime timeMillis, String format) {
        TimeZone estTZ = TimeZone.getTimeZone(BusinessConstants.EST_UMG_TIME_ZONE);
        String parseFormat = StringUtils.isNotBlank(format) ? format : BusinessConstants.UMG_EST_DATE_FORMAT;
        SimpleDateFormat sdf = new SimpleDateFormat(parseFormat, Locale.getDefault());
        sdf.setTimeZone(estTZ);

        DateTime dateTime = new DateTime(timeMillis.getMillis(), DateTimeZone.forTimeZone(estTZ));
        Calendar utcCal = Calendar.getInstance(estTZ);
        utcCal.setTimeInMillis(dateTime.getMillis());
        return sdf.format(utcCal.getTime());
    }
    
    public static Long getMillisFromString(String dateString, String format) throws BusinessException {
        Long millis = null;
        if (StringUtils.isNotEmpty(dateString)) {
            String parseFormat = StringUtils.isNotBlank(format) ? format : BusinessConstants.UMG_UTC_DATE_FORMAT;
            SimpleDateFormat dateFormat = new SimpleDateFormat(parseFormat, Locale.getDefault());
            Date date = null;
            try {
                date = dateFormat.parse(dateString);
                millis = date.getTime();
            } catch (ParseException e) {
                BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000024,
                        new String[] { dateString, parseFormat });
            }
           // millis = date.getTime();
        }
        return millis;
    }

    public static String getDateFormatEpoch(long timeMillis, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                StringUtils.isNotBlank(format) ? format : BusinessConstants.UMG_UTC_DATE_FORMAT, Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeMillis * 1000);
        return dateFormat.format(cal.getTime());
    }

    public static Long getMillisFromStringJoda(String dateString, String format) throws BusinessException {
        Long millis = null;
        if (StringUtils.isNotEmpty(dateString)) {
            String parseFormat = StringUtils.isNotBlank(format) ? format : BusinessConstants.UMG_UTC_DATE_FORMAT;
            DateTimeFormatter dateFormat = DateTimeFormat.forPattern(parseFormat);
            try {
                millis = dateFormat.parseMillis(dateString);
            } catch (IllegalArgumentException e) {
                BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000024,
                        new String[] { dateString, parseFormat });
            }
        }
        return millis;
    }

    public static Long getMillisFromStringJodaOffset(String dateString, String format) throws BusinessException {
        Long millis = null;
        if (StringUtils.isNotEmpty(dateString)) {
            String parseFormat = StringUtils.isNotBlank(format) ? format : BusinessConstants.UMG_UI_DATE_FORMAT_TIMEZONE;
            DateTimeFormatter dateFormat = DateTimeFormat.forPattern(parseFormat);
            try {
                millis = dateFormat.withOffsetParsed().parseDateTime(dateString).getMillis();
            } catch (IllegalArgumentException e) {
                BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000024,
                        new String[] { dateString, parseFormat });
            }
        }
        return millis;
    }

    public static Long getMillisFromEstToUtc(String dateString, String format) throws BusinessException {
        DateTime dateTime = null;
        if (StringUtils.isNotEmpty(dateString)) {
            String parseFormat = StringUtils.isNotBlank(format) ? format : BusinessConstants.UMG_UTC_DATE_FORMAT;
            DateTimeFormatter dateFormat = DateTimeFormat.forPattern(parseFormat)
                    .withZone(DateTimeZone.forID(BusinessConstants.EST_UMG_TIME_ZONE));
            try {
                DateTime dateTime1 = dateFormat.parseDateTime(dateString);
                dateTime = dateTime1.toDateTime(DateTimeZone.UTC);

            } catch (IllegalArgumentException e) {
                BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000024,
                        new String[] { dateString, parseFormat });
            }
        }
        return dateTime == null ? null : dateTime.getMillis();
    }

    public static Long getMillisForUtc(String dateString, String format) throws BusinessException {
        DateTime dateTime = null;
        if (StringUtils.isNotEmpty(dateString)) {
            String parseFormat = StringUtils.isNotBlank(format) ? format : BusinessConstants.UMG_UTC_DATE_FORMAT;
            DateTimeFormatter dateFormat = DateTimeFormat.forPattern(parseFormat)
                    .withZone(DateTimeZone.forID(BusinessConstants.UTC_TIME_ZONE));
            try {
                dateTime = dateFormat.parseDateTime(dateString);
            } catch (IllegalArgumentException e) {
                BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000024,
                        new String[] { dateString, parseFormat });
            }
        }
        return dateTime == null ? null : dateTime.getMillis();
    }

    public static Long getReportMillisFromEstToUtc(String dateString, String format) throws BusinessException {
        Long millis = null;
        DateTime dateTime = null;
        if (StringUtils.isNotEmpty(dateString)) {
            String parseFormat = StringUtils.isNotBlank(format) ? format : BusinessConstants.UMG_UTC_REPORT_DATE_FORMAT;
            DateTimeFormatter dateFormat = DateTimeFormat.forPattern(parseFormat);
            try {
                millis = dateFormat.withOffsetParsed().parseDateTime(dateString).getMillis();
                dateTime = new DateTime(millis, DateTimeZone.forTimeZone(TimeZone.getTimeZone(BusinessConstants.UTC_TIME_ZONE)));
            } catch (IllegalArgumentException e) {
                BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE0000504,
                        new String[] { dateString, parseFormat });
            }
        }
        return dateTime == null ? null : dateTime.getMillis();
    }

    public static Long getMillisFromUtcToEst(Long millis) throws BusinessException {
        DateTime dateTime = null;
        dateTime = new DateTime(millis, DateTimeZone.forTimeZone(TimeZone.getTimeZone(BusinessConstants.EST_TIME_ZONE)));
        return dateTime.getMillis();
    }

    public static String convertDateFormat(String originalFormat, String targetFormat, String dateInput) throws ParseException {
        DateFormat originalDateFormat = new SimpleDateFormat(dateFormatMap.get(originalFormat), Locale.getDefault());
        DateFormat targetDateFormat = new SimpleDateFormat(dateFormatMap.get(targetFormat), Locale.getDefault());
        Date date = originalDateFormat.parse(dateInput);
        return targetDateFormat.format(date);
    }

    public static String convertDateFormatStrict(String originalFormat, String targetFormat, String dateInput)
            throws ParseException {
        DateFormat originalDateFormat = new SimpleDateFormat(dateFormatMap.get(originalFormat), Locale.getDefault());
        DateFormat targetDateFormat = new SimpleDateFormat(dateFormatMap.get(targetFormat), Locale.getDefault());
        originalDateFormat.setLenient(false);
        Date date = originalDateFormat.parse(dateInput);
        return targetDateFormat.format(date);
    }

    /**
     * returns the base model directory path
     * 
     * @return
     */
    public static String getSanBasePath(String sanBase) {
        // read san location from config file
        // String sanBase = System.getProperty(SystemConstants.SAN_BASE);
        // read tenant code from request context
        String tenantCode = getRequestContext().getTenantCode();
        // prepare base model directory path and return it
        return new StringBuffer(sanBase).append(File.separatorChar).append(tenantCode).toString();
    }

    /**
     * @return
     */
    public static int getTestBatchRunMode(String runMode) {
        int mode = 1;
        if (StringUtils.isNotBlank(runMode)) {
            try {
                mode = Integer.parseInt(runMode);
            } catch (NumberFormatException nfe) {
                mode = 1;
            }
        }
        return mode;
    }

    public static void getStringArrayWithDoubleQuotes(Object tenantInputEntryValue, List<Object> stringListwithDoubleQuotes) {
        if (tenantInputEntryValue instanceof List) {
            for (Object tenantInput : (List<Object>) tenantInputEntryValue) {
                if (tenantInput != null) {
                    if ("String".equals(tenantInput.getClass().getSimpleName())) {
                        stringListwithDoubleQuotes
                                .add(BusinessConstants.CHAR_DOUBLE_QUOTE + tenantInput + BusinessConstants.CHAR_DOUBLE_QUOTE);
                    } else if (tenantInput instanceof List) {
                        multiDimenStringArray((List<Object>) tenantInput, stringListwithDoubleQuotes);
                    } else {
                        stringListwithDoubleQuotes.add(tenantInput);// added this for fixing umg-4599
                        // break;
                    }
                } else {// added this else block to fix UMG-4552
                    stringListwithDoubleQuotes.add(tenantInput);
                }
            }
        }
    }

    private static void multiDimenStringArray(List<Object> tenantInput, List<Object> stringListwithDoubleQuotes) {
        List<Object> multiDimenList = new ArrayList<Object>();
        for (Object obj : tenantInput) {
            if (obj != null) {
                if ("String".equals(obj.getClass().getSimpleName())) {
                    multiDimenList.add(BusinessConstants.CHAR_DOUBLE_QUOTE + obj + BusinessConstants.CHAR_DOUBLE_QUOTE);
                    // stringListwithDoubleQuotes.add(multiDimenList);
                } else if (obj instanceof List) {
                    multiDimenStringArray((List<Object>) obj, multiDimenList);
                } else {
                    multiDimenList.add(obj);// added this for fixing umg-4599
                    // break;
                }
            } else {// added this else block to fix UMG-4552
                multiDimenList.add(obj);
            }
        }
        // moved this line out of if block above for fixing UMG-4539
        stringListwithDoubleQuotes.add(multiDimenList);
    }

    public static void setAdminAwareTrue() {
        if(getRequestContext() != null) {
        	getRequestContext().setAdminAware(Boolean.TRUE);
		}
    }

    public static void setAdminAwareFalse() {
		if(getRequestContext() != null) {
			getRequestContext().setAdminAware(Boolean.FALSE);
		}
    }

    public static boolean isValidDateFormat(String dateFormat) {
        return dateFormatMap.containsKey(StringUtils.upperCase(dateFormat));
    }

    public static boolean getActualAdminAware() {// NOPMD
        boolean isAdminAware = false;
        if (getRequestContext() != null) {
            isAdminAware = getRequestContext().isAdminAware();
        }
        return isAdminAware;
    }
    
    public static void setActualAdminAware(Boolean actualAdminAware){
		if(getRequestContext() != null) {
			getRequestContext().setAdminAware(actualAdminAware);
		}
    }
    
	public static byte[] createTempJson(String absoluteFilePath) throws SystemException {
		byte[] fileData = null;
		int index = absoluteFilePath.lastIndexOf(File.separator);
		String fileName = absoluteFilePath.substring(index + 1);
		FileInputStream fis = null;
		File tempFile = null;
		FileWriter fileWriter = null;
		try {
			tempFile = new File(fileName);
		    fileWriter = new FileWriter(tempFile);
			JSONObject json = new JSONObject();
			json.put("error", "File " + absoluteFilePath
					+ " not available for download. It might have been either archived to a different location or purged'.");
			fileWriter.write(json.toString());
			fis = new FileInputStream(tempFile);
			fileData = AdminUtil.convertStreamToByteArray(fis);
			tempFile.delete();

		} catch (IOException | JSONException e) {
			SystemException.newSystemException(BusinessExceptionCodes.BSE000010, new Object[] { e.getMessage() });
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
				if (fileWriter != null) {
					fileWriter.flush();
					fileWriter.close();
				}
				if (tempFile != null) {
					tempFile.delete();
				}
			} catch (IOException e) {
				SystemException.newSystemException(BusinessExceptionCodes.BSE000010, new Object[] { e.getMessage() });
			}
		}

		return fileData;
	}
	
	public static byte[] createTempExc(String absoluteFilePath) throws SystemException {
		byte[] fileData = null;
		int index = absoluteFilePath.lastIndexOf(File.separator);
		String fileName = absoluteFilePath.substring(index + 1);
		String[] fileNames = fileName.split("\\.");
		FileInputStream fis = null;
		File tempFile = null;	
		Workbook wb = null;
		try {
			tempFile = new File(fileName);
			FileOutputStream fileOut = new FileOutputStream(tempFile);
			if (fileNames[1].equals("xlsx")) {
				wb = new XSSFWorkbook();
			} else {
				wb = new HSSFWorkbook();
			}
			Sheet sheet = wb.createSheet(ExcelConstants.EXCEL_ERROR_SHEET);
			Row row = sheet.createRow(0);
			row.createCell(0).setCellValue(ExcelConstants.EXCEL_ERROR_SHEET);
			row = sheet.createRow(1);
			row.createCell(0).setCellValue("'File " + absoluteFilePath
					+ " not available for download. It might have been either archived to a different location or purged'.");
			wb.write(fileOut);
			fileOut.close();
			fis = new FileInputStream(tempFile);
			fileData = AdminUtil.convertStreamToByteArray(fis);

		} catch (IOException exp) {
			LOGGER.error("BatchTransactionBOImpl:getFileContent :: File not found in path : " + absoluteFilePath);
			SystemException.newSystemException(BusinessExceptionCodes.BSE000010, new Object[] { exp.getMessage() });
		} finally {
			try{
				if (fis != null) {
					fis.close();
				}
				if (tempFile != null) {
					tempFile.delete();
				}
			} catch (IOException exp) {
				LOGGER.error("BatchTransactionBOImpl:getFileContent :: File not found in path : " + absoluteFilePath);
				SystemException.newSystemException(BusinessExceptionCodes.BSE000010, new Object[] { exp.getMessage() });
			}

		}
		return fileData;
	}

}