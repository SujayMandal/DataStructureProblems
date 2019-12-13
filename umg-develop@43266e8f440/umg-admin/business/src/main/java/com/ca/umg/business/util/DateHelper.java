/*
 * DateHelper.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics 
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.business.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 
 * Date Helper
 * 
 * @author mandavak
 *
 */
public final class DateHelper {

    private static final String DEFAULT_DATE_FORMAT = "MM/dd/yyyy";

    /**
     * private constructor defined to make the Helpler class as singleton.
     */
    private DateHelper() {

    }

    /**
     * 
     * Compare between given dates.
     * 
     * Returns zero if both are equal Returns less than zero if startDate less than endDate. Returns greater than zero if
     * startDate more than endDate.
     * 
     * @param startDate
     * @param endDate
     * @return
     * @throws ParseException
     * @throws Exception
     */
    public static int dateComparator(Date startDate, Date endDate) throws ParseException {
        String startDateString = convertDateToString(startDate);
        String endDateString = convertDateToString(endDate);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.getDefault());
        Date dtSd = simpleDateFormat.parse(startDateString);
        Date dtEd = simpleDateFormat.parse(endDateString);
        return dtSd.compareTo(dtEd);
    }

    /**
     * 
     * Convert Date to String.
     * 
     * @param date
     * @return
     */
    private static String convertDateToString(Date date) {
        String formatString = null;
        if (date != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.getDefault());
            formatString = simpleDateFormat.format(date);
        }
        return formatString;
    }
}
