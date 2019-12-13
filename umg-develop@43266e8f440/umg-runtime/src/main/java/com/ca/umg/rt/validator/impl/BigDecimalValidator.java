package com.ca.umg.rt.validator.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.ca.umg.rt.validator.DataTypes;
import com.ca.umg.rt.validator.TypeValidator;

/**
 * Created by repvenk on 5/25/2016.
 */
public class BigDecimalValidator implements TypeValidator {

    private static final int ZERO_LENGTH = 0;
    private static final String FRACTION_DIGITS = "fractionDigits";
    private static final String TOTAL_DIGITS = "totalDigits";

    @Override
    public String validate(Object value) {
        String message = null;
        if (!(value instanceof Double || value instanceof Integer || value instanceof Long ||  value instanceof BigDecimal || value instanceof BigInteger)) {
            message = String.format("Expected %s but received %s", DataTypes.BIGDECIMAL, value.getClass().getName());
        }
        return message;
    }

    @Override
    public String validate(Object value, Map<String, Object> typeProperties, String apiName) {
        String message = validate(value);
        if (!StringUtils.isEmpty(message)) {
            return message;
        }
        Map<String, Object> properties = (Map<String, Object>) typeProperties.get("properties");
        if (properties == null || properties.isEmpty()) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        BigDecimal bigDecimal = setValue(value);
        if (!StringUtils.isEmpty((String) properties.get("maxExclusive"))) {
            String maxExclusiveStr = (String) properties.get("maxExclusive");
            BigDecimal maxExclusive = new BigDecimal(maxExclusiveStr);
            if (bigDecimal.compareTo(maxExclusive) >= NumberUtils.INTEGER_ZERO) {
                builder.append(String.format("Value exceeds maximum value. Maximum value allowd is %s but found %s. \n",
                        maxExclusive, bigDecimal));
            }
        }
        if (!StringUtils.isEmpty((String) properties.get("maxInclusive"))) {
            String maxInclusiveStr = (String) properties.get("maxInclusive");
            BigDecimal maxInclusive = new BigDecimal(maxInclusiveStr);
            if (bigDecimal.compareTo(maxInclusive) > NumberUtils.INTEGER_ZERO) {
                builder.append(String.format("Value exceeds maximum value. Maximum value allowd is %s but found %s. \n",
                        maxInclusive, bigDecimal));
            }
        }
        if (!StringUtils.isEmpty((String) properties.get("minInclusive"))) {
            String minInclusiveStr = (String) properties.get("minInclusive");
            BigDecimal minInclusive = new BigDecimal(minInclusiveStr);
            if (bigDecimal.compareTo(minInclusive) < NumberUtils.INTEGER_ZERO) {
                builder.append(String.format("Value is less than minimum value. Minimum value allowd is %s but found %s. \n",
                        minInclusive, bigDecimal));
            }
        }
        if (!StringUtils.isEmpty((String) properties.get("minExclusive"))) {
            String minExclusiveStr = (String) properties.get("minExclusive");
            BigDecimal minExclusive = new BigDecimal(minExclusiveStr);
            if (bigDecimal.compareTo(minExclusive) <= NumberUtils.INTEGER_ZERO) {
                builder.append(String.format("Value is less than minimum value. Minimum value allowd is %s but found %s. \n",
                        minExclusive, bigDecimal));
            }
        }
        if (!StringUtils.isEmpty((String) properties.get(TOTAL_DIGITS))) {
            String totalDigitsStr = (String) properties.get(TOTAL_DIGITS);
            int totalDigits = Integer.parseInt(totalDigitsStr);
            String strValue = bigDecimal.toPlainString();
            strValue = strValue.replaceAll("\\.", "");
            if (strValue.length() > totalDigits) {
                builder.append(String.format(
                        "Total digits required does not match. Total digits required is %s but found %s. \n", totalDigits,
                        strValue.length()));
            }
        }
        if (!StringUtils.isEmpty((String) properties.get(FRACTION_DIGITS))) {
            String fractionDigitsStr = (String) properties.get(FRACTION_DIGITS);
            int fractionDigits = Integer.parseInt(fractionDigitsStr);
            String strValue = bigDecimal.toPlainString();
            if (strValue.contains(".")) {
                strValue = strValue.substring(strValue.indexOf('.') + 1);
            } else {
                // This means that without a decimal point, we can assume a default ".0" for a double.
                strValue = "0";
            }
            if (strValue.length() > fractionDigits) {
                builder.append(String.format(
                        "Fraction digits required does not match. Fraction digits required is %s but found %s. \n",
                        fractionDigits, strValue.length()));
            }
        }
        if (!StringUtils.isEmpty((String) properties.get(TOTAL_DIGITS))
                && !StringUtils.isEmpty((String) properties.get(FRACTION_DIGITS))) {
            String totalDigitsStr = (String) properties.get(TOTAL_DIGITS);
            int totalDigits = Integer.parseInt(totalDigitsStr);
            String fractionDigitsStr = (String) properties.get(FRACTION_DIGITS);
            int fractionDigits = Integer.parseInt(fractionDigitsStr);
            int integerDigits = totalDigits - fractionDigits;
            if (integerDigits > ZERO_LENGTH) {
                String strValue = bigDecimal.toPlainString();
                if (strValue.contains(".")) {
                    strValue = strValue.substring(0, strValue.indexOf('.'));
                }
                if (strValue.length() > integerDigits) {
                    builder.append(String.format(
                            "Integer digits required does not match. Integer digits required is %s but found %s. \n",
                            integerDigits, strValue.length()));
                }
            }
        }
        message = builder.toString();
        if (!StringUtils.isEmpty(message)) {
            return message;
        } else {
            return null;
        }
    }

    @Override
    public Object validateAndConvert(Object value) {
        BigDecimal result = null;
        if (value instanceof String) {
            result = new BigDecimal((String) value);
        }
        return result;
    }

    private BigDecimal setValue(Object value) {
        BigDecimal bigDecimalValue = null;
        if (value instanceof Integer) {
            bigDecimalValue = new BigDecimal((Integer)value);
        } else if(value instanceof Double) {
            bigDecimalValue = new BigDecimal((Double)value);
        } else if(value instanceof Long) {
            bigDecimalValue = new BigDecimal((Long)value);
        } else if(value instanceof BigInteger) {
            bigDecimalValue = new BigDecimal((BigInteger)value);
        }
        return bigDecimalValue;
    }
}
