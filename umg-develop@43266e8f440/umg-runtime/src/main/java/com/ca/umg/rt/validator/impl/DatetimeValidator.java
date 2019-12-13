package com.ca.umg.rt.validator.impl;

import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import com.ca.umg.rt.validator.DataTypes;
import com.ca.umg.rt.validator.TypeValidator;

public class DatetimeValidator implements TypeValidator{

    @Override
    public String validate(Object value) {
        String message = null;
        if(!(value instanceof DateTime)){
            message = String.format("Expected %s but received %s", DataTypes.DATETIME, value.getClass().getName());
        }
        return message;
    }
    
    @Override
    public Object validateAndConvert(Object value) {
        Boolean result = null;
        if( value instanceof String){
            result = Boolean.parseBoolean((String)value); 
        }
        return result;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public String validate(Object value, Map<String, Object> dataTypeProperties, String apiName) {
        Map<String, Object> properties = (Map<String, Object>)dataTypeProperties.get("properties");
        String       pattern  = (String)properties.get("pattern");
        if(pattern == null){
            pattern = "YYYY-MM-DD HH:MM:SS";
        }
        String message = null;
        if(!(value instanceof String)){
            message = String.format("Expected %s but received %s", "Date string formated as "+ pattern , value.getClass().getName());
        }
        
        try{
        DateTimeFormat.forPattern(pattern).parseDateTime((String)value);
        } catch (UnsupportedOperationException|IllegalArgumentException ex){
            message = String.format("Input datetime is not valid as per the pattern defined for API parameter %s. Received value is %s. Expected pattern is %s", apiName, value, pattern);
        }
        return message;
    }
}
