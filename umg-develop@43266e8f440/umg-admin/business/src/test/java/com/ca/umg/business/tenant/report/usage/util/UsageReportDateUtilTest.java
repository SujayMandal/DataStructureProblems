package com.ca.umg.business.tenant.report.usage.util;

import static com.ca.umg.business.exception.codes.BusinessExceptionCodes.BSE0000504;
import static com.ca.umg.business.tenant.report.usage.util.UsageReportDateUtil.convertDateToString;
import static com.ca.umg.business.tenant.report.usage.util.UsageReportDateUtil.getCurrentDateWithMaxTime;
import static com.ca.umg.business.tenant.report.usage.util.UsageReportDateUtil.getCurrentDateWithMinTime;
import static com.ca.umg.business.tenant.report.usage.util.UsageReportDateUtil.getDateMinus61DaysMinTime;
import static com.ca.umg.business.tenant.report.usage.util.UsageReportDateUtil.getDateOnly;
import static com.ca.umg.business.tenant.report.usage.util.UsageReportDateUtil.getDatePlus61DaysMaxTime;
import static com.ca.umg.business.tenant.report.usage.util.UsageReportDateUtil.getDateWithMaxTime;
import static com.ca.umg.business.tenant.report.usage.util.UsageReportDateUtil.getDateWithMinTime;
import static com.ca.umg.business.tenant.report.usage.util.UsageReportDateUtil.getEndDate;
import static com.ca.umg.business.tenant.report.usage.util.UsageReportDateUtil.getFirstDateInMonthMaxTime;
import static com.ca.umg.business.tenant.report.usage.util.UsageReportDateUtil.getLastDateInMonthMaxTime;
import static com.ca.umg.business.tenant.report.usage.util.UsageReportDateUtil.getStartDate;
import static com.ca.umg.business.tenant.report.usage.util.UsageReportDateUtil.isAfterCurrentDateMaxTime;
import static com.ca.umg.business.tenant.report.usage.util.UsageReportDateUtil.isEndDateMoreThanCurrentDate;
import static com.ca.umg.business.tenant.report.usage.util.UsageReportDateUtil.isInMaxDateRange;
import static com.ca.umg.business.tenant.report.usage.util.UsageReportDateUtil.isStartDateMoreThanCurrentDate;
import static com.ca.umg.business.tenant.report.usage.util.UsageReportDateUtil.isStartDateMoreThanEndDate;
import static com.ca.umg.business.tenant.report.usage.util.UsageReportDateUtil.setDatesAtUI;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.joda.time.DateTime;
import org.junit.Test;

import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.business.tenant.report.usage.UsageReportFilter;

@SuppressWarnings("PMD")
public class UsageReportDateUtilTest {

	@Test
	public void testGetCurrentDateWithMinTime() {
		final DateTime currentDateTime = getCurrentDateWithMinTime();
		final DateTime now = now();
		assertTrue(currentDateTime != null);
		assertTrue(currentDateTime.getHourOfDay() == 0);
		assertTrue(currentDateTime.getMinuteOfHour() == 0);
		assertTrue(currentDateTime.getYear() == now.getYear());
		assertTrue(currentDateTime.getMonthOfYear() == now.getMonthOfYear());
		assertTrue(currentDateTime.getDayOfMonth() == now.getDayOfMonth());
	}

	@Test
	public void testGetCurrentDateWithMaxTime() {
		final DateTime currentDateTime = getCurrentDateWithMaxTime();
		final DateTime now = now();
		assertTrue(currentDateTime != null);
		assertTrue(currentDateTime.getHourOfDay() == 23);
		assertTrue(currentDateTime.getMinuteOfHour() == 59);
		assertTrue(currentDateTime.getYear() == now.getYear());
		assertTrue(currentDateTime.getMonthOfYear() == now.getMonthOfYear());
		assertTrue(currentDateTime.getDayOfMonth() == now.getDayOfMonth());
	}

	@Test
	public void testGetDateWithMinTime() {
		// MMM-dd-yyyy HH:mm
		final String dateString = "Feb-27-2015 12:57";
		try {
			final DateTime dateTime = getDateWithMinTime(dateString);
			assertTrue(dateTime != null);
			assertTrue(dateTime.getHourOfDay() == 0);
			assertTrue(dateTime.getMinuteOfHour() == 0);
			assertTrue(dateTime.getYear() == 2015);
			assertTrue(dateTime.getMonthOfYear() == 2);
			assertTrue(dateTime.getDayOfMonth() == 27);
		} catch (BusinessException be) {
			fail("Does not expect exception here");
		}
	}

	@Test(expected = BusinessException.class)
	public void testGetDateWithMinTimeWithException() throws BusinessException {
		final String wrongDateString = "ABC-27-2015 12:57";
		try {
			getDateWithMinTime(wrongDateString);
		} catch (BusinessException be) {
			assertTrue(be.getCode().endsWith(BSE0000504));
			throw be;
		}
	}

	@Test
	public void testGetDateWithMaxTime() {
		// MMM-dd-yyyy HH:mm
		final String dateString = "Feb-27-2015 12:57";
		try {
			final DateTime dateTime = getDateWithMaxTime(dateString);
			assertTrue(dateTime != null);
			assertTrue(dateTime.getHourOfDay() == 23);
			assertTrue(dateTime.getMinuteOfHour() == 59);
			assertTrue(dateTime.getYear() == 2015);
			assertTrue(dateTime.getMonthOfYear() == 2);
			assertTrue(dateTime.getDayOfMonth() == 27);
		} catch (BusinessException be) {
			fail("Does not expect exception here");
		}
	}

	@Test(expected = BusinessException.class)
	public void testGetDateWithMaxTimeWithException() throws BusinessException {
		final String wrongDateString = "ABC-27-2015 12:57";
		try {
			getDateWithMaxTime(wrongDateString);
		} catch (BusinessException be) {
			assertTrue(be.getCode().endsWith(BSE0000504));
			throw be;
		}
	}

	@Test
	public void testGetDatePlus61Days() {
		final DateTime dateTime = new DateTime(2014, 3, 1, 0, 0);
		final DateTime dateTimePlus61Days = getDatePlus61DaysMaxTime(dateTime.toDate());
		assertTrue(dateTimePlus61Days != null);
		assertTrue(dateTimePlus61Days.getHourOfDay() == 23);
		assertTrue(dateTimePlus61Days.getMinuteOfHour() == 59);
		assertTrue(dateTimePlus61Days.getYear() == 2014);
		assertTrue(dateTimePlus61Days.getMonthOfYear() == 4);
		assertTrue(dateTimePlus61Days.getDayOfMonth() == 30);
	}

	@Test
	public void testGetDateMinus61Days() {
		final DateTime dateTime = new DateTime(2014, 4, 30, 0, 0);
		final DateTime dateTimePlus61Days = getDateMinus61DaysMinTime(dateTime.toDate());
		assertTrue(dateTimePlus61Days != null);
		assertTrue(dateTimePlus61Days.getHourOfDay() == 0);
		assertTrue(dateTimePlus61Days.getMinuteOfHour() == 0);
		assertTrue(dateTimePlus61Days.getYear() == 2014);
		assertTrue(dateTimePlus61Days.getMonthOfYear() == 3);
		assertTrue(dateTimePlus61Days.getDayOfMonth() == 1);
	}

	@Test
	public void testConvertDateToString() {
		DateTime dateTime = new DateTime(2014, 3, 1, 0, 0);
		String convertedString = convertDateToString(dateTime);
		assertTrue(convertedString.equalsIgnoreCase("Mar-01-2014 00:00"));

		dateTime = new DateTime(2014, 3, 1, 23, 59);
		convertedString = convertDateToString(dateTime);
		assertTrue(convertedString.equalsIgnoreCase("Mar-01-2014 23:59"));
	}

	@Test
	public void testIsAfterCurrentDate() {
		final DateTime before = new DateTime(2014, 3, 1, 0, 0);
		assertFalse(isAfterCurrentDateMaxTime(before));

		final DateTime after = DateTime.now().plusDays(1);
		assertTrue(isAfterCurrentDateMaxTime(after));
	}

	@Test
	public void testIsStartDateMoreThanEndDate() {
		final DateTime startDateTimeBefore = new DateTime(2014, 3, 1, 0, 0);
		final DateTime endDateTime = new DateTime(2014, 3, 2, 0, 0);
		assertFalse(isStartDateMoreThanEndDate(startDateTimeBefore, endDateTime));

		final DateTime startDateTimeAfter = new DateTime(2014, 3, 3, 0, 0);
		assertTrue(isStartDateMoreThanEndDate(startDateTimeAfter, endDateTime));

		assertFalse(isStartDateMoreThanEndDate(endDateTime, endDateTime));
	}

	@Test
	public void testIsInMaxDateRange() {
		DateTime startDateTime = new DateTime(2014, 3, 1, 0, 0);
		DateTime endDateTime = new DateTime(2014, 3, 2, 23, 59);
		assertTrue(isInMaxDateRange(startDateTime, endDateTime));

		startDateTime = new DateTime(2014, 3, 1, 0, 0);
		endDateTime = new DateTime(2014, 4, 30, 23, 59);
		assertTrue(isInMaxDateRange(startDateTime, endDateTime));

		startDateTime = new DateTime(2014, 3, 1, 0, 0);
		endDateTime = new DateTime(2014, 5, 1, 23, 59);
		assertFalse(isInMaxDateRange(startDateTime, endDateTime));

		startDateTime = new DateTime(2014, 3, 1, 0, 0);
		endDateTime = new DateTime(2014, 3, 1, 23, 59);
		assertTrue(isInMaxDateRange(startDateTime, endDateTime));

		startDateTime = new DateTime(2013, 3, 1, 0, 0);
		endDateTime = new DateTime(2014, 4, 30, 23, 59);
		assertFalse(isInMaxDateRange(startDateTime, endDateTime));

	}

	@Test
	public void testStartDateWithBothNull() throws BusinessException {
		final UsageReportFilter filter = mock(UsageReportFilter.class);
		when(filter.getRunAsOfDateFromString()).thenReturn(null);
		when(filter.getRunAsOfDateToString()).thenReturn(null);
		final String startDate = getStartDate(filter);
		assertTrue(startDate != null);

		final DateTime now = getCurrentDateWithMinTime();
		final DateTime startDateWith61DayMinus = getDateMinus61DaysMinTime(now.toDate());
		final String nowString = convertDateToString(startDateWith61DayMinus);
		assertTrue(nowString.equals(startDate));
	}

	@Test
	public void testStartDateWithEndDateNotNull() throws BusinessException {
		final UsageReportFilter filter = mock(UsageReportFilter.class);
		when(filter.getRunAsOfDateFromString()).thenReturn(null);
		final DateTime endDateTime = new DateTime(2014, 4, 30, 23, 59);
		when(filter.getRunAsOfDateToString()).thenReturn(convertDateToString(endDateTime));
		final String startDate = getStartDate(filter);
		assertTrue(startDate != null);
		assertTrue("Mar-01-2014 00:00".equals(startDate));
	}

	@Test
	public void testStartDateWithBothNotNull() throws BusinessException {
		final UsageReportFilter filter = mock(UsageReportFilter.class);
		final DateTime startDateTime = new DateTime(2014, 4, 30, 0, 0);
		when(filter.getRunAsOfDateFromString()).thenReturn(convertDateToString(startDateTime));
		final DateTime endDateTime = new DateTime(2014, 4, 30, 23, 59);
		when(filter.getRunAsOfDateToString()).thenReturn(convertDateToString(endDateTime));
		final String startDate = getStartDate(filter);
		assertTrue(startDate != null);
		assertTrue("Apr-30-2014 00:00".equals(startDate));
	}

	@Test
	public void testEndDateWithBothNullForCustom() throws BusinessException {
		final UsageReportFilter filter = mock(UsageReportFilter.class);
		when(filter.getRunAsOfDateFromString()).thenReturn(null);
		when(filter.getRunAsOfDateToString()).thenReturn(null);
		final String endDate = getEndDate(filter, false);
		assertTrue(endDate != null);

		final DateTime now = getCurrentDateWithMaxTime();
		final String nowString = convertDateToString(now);
		assertTrue(nowString.equals(endDate));
	}

	@Test
	public void testEndDateWithStartDateNotNullForCustom() throws BusinessException {
		final UsageReportFilter filter = mock(UsageReportFilter.class);
		final DateTime startDateTime = new DateTime(2014, 3, 1, 0, 0);
		when(filter.getRunAsOfDateFromString()).thenReturn(convertDateToString(startDateTime));
		when(filter.getRunAsOfDateToString()).thenReturn(null);
		final String endDate = getEndDate(filter, false);
		assertTrue(endDate != null);
		assertTrue("Apr-30-2014 23:59".equals(endDate));
	}

	@Test
	public void testEndDateWithStartDateIsNowForCustom() throws BusinessException {
		final UsageReportFilter filter = mock(UsageReportFilter.class);
		final DateTime startDateTime = DateTime.now();
		when(filter.getRunAsOfDateFromString()).thenReturn(convertDateToString(startDateTime));
		when(filter.getRunAsOfDateToString()).thenReturn(null);
		final String endDate = getEndDate(filter, false);
		assertTrue(endDate != null);

		final DateTime now = getCurrentDateWithMaxTime();
		final String nowString = convertDateToString(now);
		assertTrue(nowString.equals(endDate));
	}

	@Test
	public void testEndDateWithBothNotNullForCustom() throws BusinessException {
		final UsageReportFilter filter = mock(UsageReportFilter.class);
		final DateTime startDateTime = new DateTime(2014, 4, 30, 0, 0);
		when(filter.getRunAsOfDateFromString()).thenReturn(convertDateToString(startDateTime));
		final DateTime endDateTime = new DateTime(2014, 4, 30, 23, 59);
		when(filter.getRunAsOfDateToString()).thenReturn(convertDateToString(endDateTime));
		final String endDate = getEndDate(filter, false);
		assertTrue(endDate != null);
		assertTrue("Apr-30-2014 23:59".equals(endDate));
	}

	@Test
	public void testEndDateWithBothNullForNonCustom() throws BusinessException {
		final UsageReportFilter filter = mock(UsageReportFilter.class);
		when(filter.getRunAsOfDateFromString()).thenReturn(null);
		when(filter.getRunAsOfDateToString()).thenReturn(null);
		final String endDate = getEndDate(filter, false);
		assertTrue(endDate != null);

		final DateTime now = getCurrentDateWithMaxTime();
		final String nowString = convertDateToString(now);
		assertTrue(nowString.equals(endDate));
	}

	@Test
	public void testEndDateWithStartDateNotNullForNonCustom() throws BusinessException {
		final UsageReportFilter filter = mock(UsageReportFilter.class);
		final DateTime startDateTime = new DateTime(2014, 3, 1, 0, 0);
		when(filter.getRunAsOfDateFromString()).thenReturn(convertDateToString(startDateTime));
		when(filter.getRunAsOfDateToString()).thenReturn(null);
		final String endDate = getEndDate(filter, true);
		assertTrue(endDate != null);
		assertTrue("Mar-31-2014 23:59".equals(endDate));
	}

	@Test
	public void testEndDateWithStartDateIsNowForNonCustom() throws BusinessException {
		final UsageReportFilter filter = mock(UsageReportFilter.class);
		final DateTime startDateTime = getFirstDateInMonthMaxTime(now().toDate());
		when(filter.getRunAsOfDateFromString()).thenReturn(convertDateToString(startDateTime));
		when(filter.getRunAsOfDateToString()).thenReturn(null);
		final String endDate = getEndDate(filter, true);
		assertTrue(endDate != null);

		final DateTime now = getCurrentDateWithMaxTime();
		final String nowString = convertDateToString(now);
		assertTrue(nowString.equals(endDate));
	}

	@Test
	public void testGetLastDateInMonthMaxTime() {
		final DateTime janFirst = new DateTime(2014, 1, 1, 0, 0);
		DateTime janLast = getLastDateInMonthMaxTime(janFirst.toDate());
		String janLastString = convertDateToString(janLast);
		assertTrue(janLastString != null);
		assertTrue("Jan-31-2014 23:59".equals(janLastString));

		final DateTime janMiddle = new DateTime(2014, 1, 15, 0, 0);
		janLast = getLastDateInMonthMaxTime(janMiddle.toDate());
		janLastString = convertDateToString(janLast);
		assertTrue(janLastString != null);
		assertTrue("Jan-31-2014 23:59".equals(janLastString));

		final DateTime jan31 = new DateTime(2014, 1, 31, 0, 0);
		janLast = getLastDateInMonthMaxTime(jan31.toDate());
		janLastString = convertDateToString(janLast);
		assertTrue(janLastString != null);
		assertTrue("Jan-31-2014 23:59".equals(janLastString));
	}

	@Test
	public void testGetFirstDateInMonthMaxTime() {
		final DateTime janFirst = new DateTime(2014, 1, 1, 0, 0);
		DateTime janLast = getFirstDateInMonthMaxTime(janFirst.toDate());
		String janLastString = convertDateToString(janLast);
		assertTrue(janLastString != null);
		assertTrue("Jan-01-2014 23:59".equals(janLastString));

		final DateTime janMiddle = new DateTime(2014, 1, 15, 0, 0);
		janLast = getFirstDateInMonthMaxTime(janMiddle.toDate());
		janLastString = convertDateToString(janLast);
		assertTrue(janLastString != null);
		assertTrue("Jan-01-2014 23:59".equals(janLastString));

		final DateTime jan31 = new DateTime(2014, 1, 31, 0, 0);
		janLast = getFirstDateInMonthMaxTime(jan31.toDate());
		janLastString = convertDateToString(janLast);
		assertTrue(janLastString != null);
		assertTrue("Jan-01-2014 23:59".equals(janLastString));
	}

	@Test
	public void testIsStartDateMoreThanCurrentDate() throws BusinessException {
		final DateTime now = DateTime.now();
		final String nowString = convertDateToString(now);
		assertFalse(isStartDateMoreThanCurrentDate(nowString));

		final DateTime tomorrow = now.plusDays(1);
		final String tomorrowString = convertDateToString(tomorrow);
		assertTrue(isStartDateMoreThanCurrentDate(tomorrowString));

		final DateTime yesterday = now.minusDays(1);
		final String yesterdayString = convertDateToString(yesterday);
		assertFalse(isStartDateMoreThanCurrentDate(yesterdayString));

	}

	@Test
	public void testIsEndDateMoreThanCurrentDate() throws BusinessException {
		final DateTime now = DateTime.now();
		final String nowString = convertDateToString(now);
		assertFalse(isEndDateMoreThanCurrentDate(nowString));

		final DateTime tomorrow = now.plusDays(1);
		final String tomorrowString = convertDateToString(tomorrow);
		assertTrue(isEndDateMoreThanCurrentDate(tomorrowString));

		final DateTime yesterday = now.minusDays(1);
		final String yesterdayString = convertDateToString(yesterday);
		assertFalse(isEndDateMoreThanCurrentDate(yesterdayString));
	}

	@Test
	public void testSetDatesAtUI() {
		final UsageReportFilter filter = mock(UsageReportFilter.class);

		when(filter.isCustomDate()).thenReturn(false);
		when(filter.getRunAsOfDateFromString()).thenReturn(null);
		when(filter.getRunAsOfDateToString()).thenReturn(null);
		assertTrue(setDatesAtUI(filter));

		when(filter.isCustomDate()).thenReturn(false);
		when(filter.getRunAsOfDateFromString()).thenReturn("");
		when(filter.getRunAsOfDateToString()).thenReturn("");
		assertTrue(setDatesAtUI(filter));

		when(filter.isCustomDate()).thenReturn(false);
		when(filter.getRunAsOfDateFromString()).thenReturn("selected");
		when(filter.getRunAsOfDateToString()).thenReturn(null);
		assertFalse(setDatesAtUI(filter));

		when(filter.isCustomDate()).thenReturn(true);
		when(filter.getRunAsOfDateFromString()).thenReturn(null);
		when(filter.getRunAsOfDateToString()).thenReturn(null);
		assertTrue(setDatesAtUI(filter));

		when(filter.isCustomDate()).thenReturn(true);
		when(filter.getRunAsOfDateFromString()).thenReturn("");
		when(filter.getRunAsOfDateToString()).thenReturn("");
		assertTrue(setDatesAtUI(filter));

		when(filter.isCustomDate()).thenReturn(true);
		when(filter.getRunAsOfDateFromString()).thenReturn("selected");
		when(filter.getRunAsOfDateToString()).thenReturn(null);
		assertTrue(setDatesAtUI(filter));

		when(filter.isCustomDate()).thenReturn(true);
		when(filter.getRunAsOfDateFromString()).thenReturn(null);
		when(filter.getRunAsOfDateToString()).thenReturn("selected");
		assertTrue(setDatesAtUI(filter));

		when(filter.isCustomDate()).thenReturn(false);
		when(filter.getRunAsOfDateFromString()).thenReturn("selceted");
		when(filter.getRunAsOfDateToString()).thenReturn("selcted");
		assertFalse(setDatesAtUI(filter));
	}

	@Test
	public void testGetDateOnly() {
		String dateTimeString = null;
		String dateOnly = getDateOnly(dateTimeString);
		assertTrue(dateOnly.equals(""));

		dateTimeString = "";
		dateOnly = getDateOnly(dateTimeString);
		assertTrue(dateOnly.equals(""));

		dateTimeString = "Mar-12-2014 10:10";
		dateOnly = getDateOnly(dateTimeString);
		assertTrue(dateOnly.equals("Mar-12-2014"));

		dateTimeString = "Mar-12-2014";
		dateOnly = getDateOnly(dateTimeString);
		assertTrue(dateOnly.equals(""));
	}
}