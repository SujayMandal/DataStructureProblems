package com.ca.umg.business.validation;

import static com.ca.umg.business.constants.BusinessConstants.SYND_DATE_FORMAT;

import java.text.ParseException;

import org.apache.commons.lang3.time.DateUtils;

public final class DataTypeUtils {

    private DataTypeUtils() {

    }

    public static boolean isNotBoolean(String value) {
        boolean notBoolean = true;
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            notBoolean = false;
        }
        return notBoolean;
    }

    public static boolean isNotDate(final String value) {
        boolean notDate = false;
        try {
            DateUtils.parseDateStrictly(value, SYND_DATE_FORMAT);
        } catch (ParseException e1) {
            notDate = true;
        }
        return notDate;
    }

    public static boolean isNotChar(final String value) {
        return value.length() != 1;
    }

    public static boolean isNotInteger(final String value) {
        boolean notInteger = false;
        try {
            Integer.parseInt(value);
        } catch (NumberFormatException e) {
            notInteger = true;
        }
        return notInteger;
    }

}
