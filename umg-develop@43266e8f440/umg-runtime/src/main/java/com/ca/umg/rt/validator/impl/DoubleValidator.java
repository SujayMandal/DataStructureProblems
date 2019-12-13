package com.ca.umg.rt.validator.impl;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.ca.umg.rt.validator.DataTypes;
import com.ca.umg.rt.validator.TypeValidator;

public class DoubleValidator implements TypeValidator {

    private static final int ZERO_LENGTH = 0;
    private static final String FRACTION_DIGITS = "fractionDigits";
    private static final String TOTAL_DIGITS = "totalDigits";

    @Override
    public String validate(Object value) {
        String message = null;
        if (!(value instanceof Double || value instanceof Integer)) {
            message = String.format("Expected %s but received %s", DataTypes.DOUBLE, value.getClass().getName());
        }
        return message;
    }

    @Override
    public Object validateAndConvert(Object value) {
        Double result = null;
        if (value instanceof String) {
            result = Double.parseDouble((String) value);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
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
        Double doubleValue = setValue(value);
        if (!StringUtils.isEmpty((String) properties.get("maxExclusive"))) {
            String maxExclusiveStr = (String) properties.get("maxExclusive");
            double maxExclusive = Double.parseDouble(maxExclusiveStr);
            if (doubleValue >= maxExclusive) {
                builder.append(String.format("Value exceeds maximum value. Maximum value allowd is %s but found %s. \n",
                        maxExclusive, doubleValue));
            }
        }
        if (!StringUtils.isEmpty((String) properties.get("maxInclusive"))) {
            String maxInclusiveStr = (String) properties.get("maxInclusive");
            double maxInclusive = Double.parseDouble(maxInclusiveStr);
            if (doubleValue > maxInclusive) {
                builder.append(String.format("Value exceeds maximum value. Maximum value allowd is %s but found %s. \n",
                        maxInclusive, doubleValue));
            }
        }
        if (!StringUtils.isEmpty((String) properties.get("minInclusive"))) {
            String minInclusiveStr = (String) properties.get("minInclusive");
            double minInclusive = Double.parseDouble(minInclusiveStr);
            if (doubleValue < minInclusive) {
                builder.append(String.format("Value is less than minimum value. Minimum value allowd is %s but found %s. \n",
                        minInclusive, doubleValue));
            }
        }
        if (!StringUtils.isEmpty((String) properties.get("minExclusive"))) {
            String minExclusiveStr = (String) properties.get("minExclusive");
            double minExclusive = Double.parseDouble(minExclusiveStr);
            if (doubleValue <= minExclusive) {
                builder.append(String.format("Value is less than minimum value. Minimum value allowd is %s but found %s. \n",
                        minExclusive, doubleValue));
            }
        }
        if (!StringUtils.isEmpty((String) properties.get(TOTAL_DIGITS))) {
            String totalDigitsStr = (String) properties.get(TOTAL_DIGITS);
            int totalDigits = Integer.parseInt(totalDigitsStr);
            String strValue = BigDecimal.valueOf(doubleValue).toPlainString();
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
            String strValue = BigDecimal.valueOf(doubleValue).toPlainString();
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
                String strValue = BigDecimal.valueOf(doubleValue).toPlainString();
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

    private Double setValue(Object value) {
        Double doubleValue = null;

        if (value instanceof Integer) {
            doubleValue = ((Integer) value).doubleValue();
        } else {
            doubleValue = (Double) value;
        }
        return doubleValue;
    }

}
