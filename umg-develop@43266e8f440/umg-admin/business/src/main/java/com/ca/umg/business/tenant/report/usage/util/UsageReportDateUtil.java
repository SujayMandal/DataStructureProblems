package com.ca.umg.business.tenant.report.usage.util;

import static com.ca.umg.business.constants.BusinessConstants.UMG_UTC_REPORT_DATE_FORMAT;
import static com.ca.umg.business.util.AdminUtil.getReportMillisFromEstToUtc;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.business.tenant.report.usage.UsageReportFilter;

@SuppressWarnings("PMD")
public final class UsageReportDateUtil {

	private static final int HOUR_MIN = 0;
	private static final int HOUR_MAX = 23;
	private static final int MINITUE_MIN = 0;
	private static final int MINITUE_MAX = 59;
	private static final int MAX_SEARCH_DAYS_RANGE = 61;

	public static final String START_DATE_GREATER_THAN_CURRENT_DATE = "Start Date should be before or equal to current date";
	public static final String END_DATE_GREATER_THAN_CURRENT_DATE = "End Date should be before or equal to current date";
	public static final String START_DATE_GREATER_THAN_END_DATE = "Start Date should be before or equal to end date";
	public static final String NOT_IN_RANGE = "Date Range cannot be greater than 61 days. Change Start/End Date accordingly to meet the criteria";

	private UsageReportDateUtil() {

	}

	public static DateTime getDateWithMinTime(final String dateString) throws BusinessException {
		if (dateString != null) {
			final Long dateInMillis = getReportMillisFromEstToUtc(dateString, null);
			final DateTime now = new DateTime(dateInMillis);
			final int year = now.getYear();
			final int month = now.getMonthOfYear();
			final int day = now.getDayOfMonth();
			final int hour = HOUR_MIN;
			final int min = MINITUE_MIN;

			return new DateTime(year, month, day, hour, min);
		}

		return null;
	}

	public static DateTime getDateWithMaxTime(final String dateString) throws BusinessException {
		if (StringUtils.isNotEmpty(dateString)) {
			final Long dateInMillis = getReportMillisFromEstToUtc(dateString, null);
			final DateTime now = new DateTime(dateInMillis);
			final int year = now.getYear();
			final int month = now.getMonthOfYear();
			final int day = now.getDayOfMonth();
			final int hour = HOUR_MAX;
			final int min = MINITUE_MAX;

			return new DateTime(year, month, day, hour, min);
		} else {
			return null;
		}
	}

	public static DateTime getCurrentDateWithMinTime() {
		final DateTime now = DateTime.now();
		final int year = now.getYear();
		final int month = now.getMonthOfYear();
		final int day = now.getDayOfMonth();
		final int hour = HOUR_MIN;
		final int min = MINITUE_MIN;

		return new DateTime(year, month, day, hour, min);
	}

	public static DateTime getCurrentDateWithMaxTime() {
		final DateTime now = DateTime.now();
		final int year = now.getYear();
		final int month = now.getMonthOfYear();
		final int day = now.getDayOfMonth();
		final int hour = HOUR_MAX;
		final int min = MINITUE_MAX;

		return new DateTime(year, month, day, hour, min);
	}

	public static DateTime getDatePlus61DaysMaxTime(final Date date) {
		final DateTime dateTime = new DateTime(date);
		final int year = dateTime.getYear();
		final int month = dateTime.getMonthOfYear();
		final int day = dateTime.getDayOfMonth();
		final int hour = HOUR_MAX;
		final int min = MINITUE_MAX;

		DateTime dateTimeWithMinTime = new DateTime(year, month, day, hour, min);
		return dateTimeWithMinTime.plusDays(MAX_SEARCH_DAYS_RANGE - 1);
	}

	public static DateTime getDateMinus61DaysMinTime(final Date date) {
		final DateTime dateTime = new DateTime(date);
		final int year = dateTime.getYear();
		final int month = dateTime.getMonthOfYear();
		final int day = dateTime.getDayOfMonth();
		final int hour = HOUR_MIN;
		final int min = MINITUE_MIN;

		DateTime dateTimeWithMinTime = new DateTime(year, month, day, hour, min);
		return dateTimeWithMinTime.minusDays(MAX_SEARCH_DAYS_RANGE - 1);
	}

	public static String convertDateToString(final DateTime dateTime) {
		final DateFormat df = new SimpleDateFormat(UMG_UTC_REPORT_DATE_FORMAT);
		return df.format(dateTime.toDate());
	}

	public static boolean isAfterCurrentDateMaxTime(final DateTime dateTime) {
		final DateTime currentDateTime = getCurrentDateWithMaxTime();
		return dateTime.isAfter(currentDateTime.getMillis());
	}

	public static boolean isInMaxDateRange(final DateTime startDateTime, final DateTime endDateTime) {
		boolean flag = true;
		if (startDateTime != null && endDateTime != null) {
			final DateTime minStartDateTime = getDateMinus61DaysMinTime(endDateTime.toDate());
			flag = minStartDateTime.isEqual(startDateTime.getMillis()) || minStartDateTime.isBefore(startDateTime.getMillis());
		}

		return flag;
	}

	public static boolean isStartDateMoreThanEndDate(final DateTime startDateTime, final DateTime endDateTime) {
		boolean flag = false;
		if (startDateTime != null && endDateTime != null) {
			return startDateTime.isAfter(endDateTime.getMillis());
		}

		return flag;
	}

	public static String getStartDate(final UsageReportFilter filter) throws BusinessException {
		String startDate = filter.getRunAsOfDateFromString();
		if (filter.getRunAsOfDateFromString() == null && filter.getRunAsOfDateToString() == null) {
			final DateTime startDateWithMinTime = getCurrentDateWithMinTime();
			final DateTime startDateWith61DayMinus = getDateMinus61DaysMinTime(startDateWithMinTime.toDate());
			startDate = convertDateToString(startDateWith61DayMinus);
		} else if (filter.getRunAsOfDateFromString() == null && filter.getRunAsOfDateToString() != null) {
			final DateTime startDateWithMinTime = getDateWithMinTime(filter.getRunAsOfDateToString());
			final DateTime startDateWith61DayMinus = getDateMinus61DaysMinTime(startDateWithMinTime.toDate());
			startDate = convertDateToString(startDateWith61DayMinus);
		}

		return startDate;
	}

	public static String getEndDate(final UsageReportFilter filter, final boolean startDateSelectedForMonthReport) throws BusinessException {
		String endDate = filter.getRunAsOfDateToString();
		if (filter.getRunAsOfDateToString() == null && filter.getRunAsOfDateFromString() == null) {
			final DateTime endDateWithMaxTime = getCurrentDateWithMaxTime();
			endDate = convertDateToString(endDateWithMaxTime);
		} else if (filter.getRunAsOfDateToString() == null && filter.getRunAsOfDateFromString() != null) {
			if (!startDateSelectedForMonthReport) {
				final DateTime startDateWithMaxTime = getDateWithMaxTime(filter.getRunAsOfDateFromString());
				final DateTime startDateWith61DayPlus = getDatePlus61DaysMaxTime(startDateWithMaxTime.toDate());
				boolean isAfterMoreThanCurrentTime = isAfterCurrentDateMaxTime(startDateWith61DayPlus);
				if (isAfterMoreThanCurrentTime) {
					endDate = convertDateToString(getCurrentDateWithMaxTime());
				} else {
					endDate = convertDateToString(startDateWith61DayPlus);
				}
			} else {
				final DateTime startDateWithMaxTime = getDateWithMaxTime(filter.getRunAsOfDateFromString());
				final DateTime lastDateInMonth = getLastDateInMonthMaxTime(startDateWithMaxTime.toDate());
				boolean isAfterMoreThanCurrentTime = isAfterCurrentDateMaxTime(lastDateInMonth);
				if (isAfterMoreThanCurrentTime) {
					endDate = convertDateToString(getCurrentDateWithMaxTime());
				} else {
					endDate = convertDateToString(lastDateInMonth);
				}
			}
		}

		return endDate;
	}

	public static DateTime getLastDateInMonthMaxTime(final Date date) {
		final DateTime dateTime = new DateTime(date);
		final int year = dateTime.getYear();
		final int month = dateTime.getMonthOfYear();
		final int day = dateTime.dayOfMonth().getMaximumValue();
		final int hour = HOUR_MAX;
		final int min = MINITUE_MAX;

		return new DateTime(year, month, day, hour, min);
	}

	public static DateTime getFirstDateInMonthMaxTime(final Date date) {
		final DateTime dateTime = new DateTime(date);
		final int year = dateTime.getYear();
		final int month = dateTime.getMonthOfYear();
		final int day = dateTime.dayOfYear().getMinimumValue();
		final int hour = HOUR_MAX;
		final int min = MINITUE_MAX;

		return new DateTime(year, month, day, hour, min);
	}

	public static boolean isStartDateMoreThanCurrentDate(final String startDate) throws BusinessException {
		boolean validation = false;
		if (startDate != null) {
			final DateTime startDateTime = getDateWithMinTime(startDate);
			validation = isAfterCurrentDateMaxTime(startDateTime);
		}

		return validation;
	}

	public static boolean isEndDateMoreThanCurrentDate(final String endDate) throws BusinessException {
		boolean validation = false;
		if (endDate != null) {
			final DateTime endDateTime = getDateWithMaxTime(endDate);
			validation = isAfterCurrentDateMaxTime(endDateTime);
		}

		return validation;
	}

	public static boolean setDatesAtUI(final UsageReportFilter filter) {
		final String startDateString = filter.getRunAsOfDateFromString();
		final String endDateString = filter.getRunAsOfDateToString();

		boolean flag = false;
		if (!isNotEmpty(startDateString)) {
			flag = true;
		} else if (!isNotEmpty(endDateString) && filter.isCustomDate()) {
			flag = true;
		}

		return flag;
	}

	public static String getDateOnly(final String dateTimeString) {
		String dateOnly = "";
		if (isNotEmpty(dateTimeString)) {
			final String[] dateParts = dateTimeString.split("[ ]");
			if (dateParts.length > 1) {
				dateOnly = dateParts[0];
			}
		}

		return dateOnly;
	}

	public static void main(final String args[]) {
		final DateTime fromDateTime = new DateTime(2015, 1, 9, 0, 0, 0);
		System.out.println("Start Time:" + fromDateTime.getMillis());

		final DateTime toDateTime = new DateTime(2015, 3, 10, 23, 59, 59);
		System.out.println("End Time:" + toDateTime.getMillis());
	}
}