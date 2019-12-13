package com.ca.umg.rt.validator.impl;

import java.math.BigInteger;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.ca.umg.rt.validator.DataTypes;
import com.ca.umg.rt.validator.TypeValidator;

/**
 * Created by repvenk on 5/25/2016.
 */
public class BigIntegerValidator implements TypeValidator {

    @Override
    public String validate(Object value) {
        String message = null;
        if(!(value instanceof Long || value instanceof Integer || value instanceof BigInteger)){
            message = String.format("Expected %s but received %s", DataTypes.BIGINTEGER, value.getClass().getName());
        }
        return message;
    }

    @Override
    public String validate(Object value, Map<String, Object> typeProperties, String apiName) {
        String message = validate(value);
        if(!StringUtils.isEmpty(message)){
            return message;
        }
        Map<String, Object> properties = (Map<String, Object>)typeProperties.get("properties");
        if(properties==null || properties.isEmpty()){
            return null;
        }
        StringBuilder builder = new StringBuilder();
        BigInteger bigInteger = null;
        if(value instanceof Integer) {
            bigInteger = new BigInteger(Integer.toString((Integer)value));
        }
        else if(value instanceof Long) {
            bigInteger = new BigInteger(Long.toString((Long)value));
        }
        else {
            bigInteger = (BigInteger)value;
        }
        if(!StringUtils.isEmpty((String)properties.get("maxExclusive"))){
            String maxExclusiveStr = (String)properties.get("maxExclusive");
            BigInteger maxExclusive = new BigInteger(maxExclusiveStr);
            if(bigInteger.compareTo(maxExclusive) >= NumberUtils.INTEGER_ZERO) {
                builder.append(String.format("Value exceeds maximum value. Maximum value allowd is %s but found %s. \n",maxExclusive, bigInteger));
            }
        }
        if(!StringUtils.isEmpty((String)properties.get("maxInclusive"))){
            String maxInclusiveStr = (String)properties.get("maxInclusive");
            BigInteger maxInclusive = new BigInteger(maxInclusiveStr);
            if(bigInteger.compareTo(maxInclusive)> NumberUtils.INTEGER_ZERO){
                builder.append(String.format("Value exceeds maximum value. Maximum value allowd is %s but found %s. \n",maxInclusive, bigInteger));
            }
        }
        if(!StringUtils.isEmpty((String)properties.get("minInclusive"))){
            String minInclusiveStr = (String)properties.get("minInclusive");
            BigInteger minInclusive = new BigInteger(minInclusiveStr);
            if(bigInteger.compareTo(minInclusive) < NumberUtils.INTEGER_ZERO) {
                builder.append(String.format("less than minimum value. Minimum value allowd is %s but found %s. \n",minInclusive, bigInteger));
            }
        }
        if(!StringUtils.isEmpty((String)properties.get("minExclusive"))){
            String minExclusiveStr = (String)properties.get("minExclusive");
            BigInteger minExclusive = new BigInteger(minExclusiveStr);
            if(bigInteger.compareTo(minExclusive) <= NumberUtils.INTEGER_ZERO) {
                builder.append(String.format("less than minimum value. Minimum value allowd is %s but found %s. \n",minExclusive, bigInteger));
            }
        }
        if(!StringUtils.isEmpty((String)properties.get("totalDigits"))){
            String totalDigitsStr = (String)properties.get("totalDigits");
            int totalDigits = Integer.parseInt(totalDigitsStr);
            String strValue = bigInteger.toString();
            if(strValue.length() != totalDigits){
                builder.append(String.format("Total digits required does not match. Total digits required is %s but found %s. \n",totalDigits, strValue.length()));
            }
        }
        message = builder.toString();
        if(!StringUtils.isEmpty(message)){
            return message;
        } else {
            return null;
        }
    }

    @Override
    public Object validateAndConvert(Object value) {
        BigInteger result = null;
        if( value instanceof String){
            result = new BigInteger((String)value);
        }
        return result;
    }
}
