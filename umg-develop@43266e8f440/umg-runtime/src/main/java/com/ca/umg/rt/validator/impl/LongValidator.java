package com.ca.umg.rt.validator.impl;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.ca.umg.rt.validator.DataTypes;
import com.ca.umg.rt.validator.TypeValidator;

/**
 * Created by repvenk on 5/25/2016.
 */
public class LongValidator implements TypeValidator {
    @Override
    public String validate(Object value) {
        String message = null;
        if(!(value instanceof Long || value instanceof Integer)){
            message = String.format("Expected %s but received %s", DataTypes.LONG, value.getClass().getName());
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
        Long longValue = null;
        if(value instanceof Integer) {
            longValue = Long.valueOf((Integer)value);
        }
        else {
            longValue = (Long)value;
        }
        if(!StringUtils.isEmpty((String)properties.get("maxExclusive"))){
            String maxExclusiveStr = (String)properties.get("maxExclusive");
            long maxExclusive = Long.parseLong(maxExclusiveStr);
            if(longValue >= maxExclusive){
                builder.append(String.format("Value exceeds maximum value. Maximum value allowd is %s but found %s. \n",maxExclusive, longValue));
            }
        }
        if(!StringUtils.isEmpty((String)properties.get("maxInclusive"))){
            String maxInclusiveStr = (String)properties.get("maxInclusive");
            long maxInclusive = Long.parseLong(maxInclusiveStr);
            if(longValue > maxInclusive){
                builder.append(String.format("Value exceeds maximum value. Maximum value allowd is %s but found %s. \n",maxInclusive, longValue));
            }
        }
        if(!StringUtils.isEmpty((String)properties.get("minInclusive"))){
            String minInclusiveStr = (String)properties.get("minInclusive");
            long minInclusive = Long.parseLong(minInclusiveStr);
            if(longValue < minInclusive){
                builder.append(String.format("less than minimum value. Minimum value allowd is %s but found %s. \n",minInclusive, longValue));
            }
        }
        if(!StringUtils.isEmpty((String)properties.get("minExclusive"))){
            String minExclusiveStr = (String)properties.get("minExclusive");
            long minExclusive = Long.parseLong(minExclusiveStr);
            if(longValue <= minExclusive){
                builder.append(String.format("less than minimum value. Minimum value allowd is %s but found %s. \n",minExclusive, longValue));
            }
        }
        if(!StringUtils.isEmpty((String)properties.get("totalDigits"))){
            String totalDigitsStr = (String)properties.get("totalDigits");
            int totalDigits = Integer.parseInt(totalDigitsStr);
            String strValue = Long.toString(longValue);
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
        Long result = null;
        if( value instanceof String){
            result = Long.parseLong((String)value);
        }
        return result;
    }
}
