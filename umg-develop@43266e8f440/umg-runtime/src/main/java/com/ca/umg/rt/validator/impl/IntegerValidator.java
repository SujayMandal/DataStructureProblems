package com.ca.umg.rt.validator.impl;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.ca.umg.rt.validator.DataTypes;
import com.ca.umg.rt.validator.TypeValidator;

public class IntegerValidator implements TypeValidator{

    @Override
    public String validate(Object value) {
        String message = null;
        if(!(value instanceof Integer)){
            message = String.format("Expected %s but received %s", DataTypes.INTEGER, value.getClass().getName());
        }
        return message;
    }
    @Override
    public Object validateAndConvert(Object value) {
        Integer result = null;
        if( value instanceof String){
            result = Integer.parseInt((String)value); 
        }
        return result;
    }
    
    @SuppressWarnings("unchecked")
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
        Integer intValue = (Integer)value;
        if(!StringUtils.isEmpty((String)properties.get("maxExclusive"))){
            String maxExclusiveStr = (String)properties.get("maxExclusive");
            int maxExclusive = Integer.parseInt(maxExclusiveStr);
            if(intValue >= maxExclusive){
                builder.append(String.format("Value exceeds maximum value. Maximum value allowd is %s but found %s. \n",maxExclusive, intValue));
            }
        }
        if(!StringUtils.isEmpty((String)properties.get("maxInclusive"))){
            String maxInclusiveStr = (String)properties.get("maxInclusive");
            int maxInclusive = Integer.parseInt(maxInclusiveStr);
            if(intValue > maxInclusive){
                builder.append(String.format("Value exceeds maximum value. Maximum value allowd is %s but found %s. \n",maxInclusive, intValue));
            }        
        }
        if(!StringUtils.isEmpty((String)properties.get("minInclusive"))){
            String minInclusiveStr = (String)properties.get("minInclusive");
            int minInclusive = Integer.parseInt(minInclusiveStr);
            if(intValue < minInclusive){
                builder.append(String.format("less than minimum value. Minimum value allowd is %s but found %s. \n",minInclusive, intValue));
            } 
        }
        if(!StringUtils.isEmpty((String)properties.get("minExclusive"))){
            String minExclusiveStr = (String)properties.get("minExclusive");
            int minExclusive = Integer.parseInt(minExclusiveStr);
            if(intValue <= minExclusive){
                builder.append(String.format("less than minimum value. Minimum value allowd is %s but found %s. \n",minExclusive, intValue));
            }   
        }
        if(!StringUtils.isEmpty((String)properties.get("totalDigits"))){
            String totalDigitsStr = (String)properties.get("totalDigits");
            int totalDigits = Integer.parseInt(totalDigitsStr);
            String strValue = Integer.toString(intValue);
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
}
