package com.ca.umg.rt.validator.impl;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.ca.umg.rt.validator.DataTypes;
import com.ca.umg.rt.validator.TypeValidator;

public class StringValidator implements TypeValidator{

    @Override
    public String validate(Object value) {
        String message = null;
        if(!(value instanceof String)){
            message = String.format("Expected %s but received %s", DataTypes.STRING, value.getClass().getName());
        }
        return message;
    }
    
    @Override
    public Object validateAndConvert(Object value) {
        String result = null;
        if(value !=null){
            result = value.toString(); 
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
        String strValue = (String)value;       
        if(!StringUtils.isEmpty((String)properties.get("minLength"))){
            String length = (String)properties.get("minLength");
            int len = Integer.parseInt(length);
            if(strValue.length() < len){
                builder.append(String.format("Value length is less than required minimum legth. Required minimum length is %s but found %s. \n",len, strValue.length()));
            }        
        }
        if(!StringUtils.isEmpty((String)properties.get("maxLength"))){
            String length = (String)properties.get("maxLength");
            int len = Integer.parseInt(length);
            if(strValue.length() > len){
                builder.append(String.format("Value length is greater than required maximum legth. Required maximum length is %s but found %s. \n",len, strValue.length()));
            } 
        }
        if(!StringUtils.isEmpty((String)properties.get("pattern"))){
            String patternString = (String)properties.get("pattern");
            Pattern pattern = Pattern.compile(patternString);
            Matcher matcher = pattern.matcher(strValue);
            boolean matches = matcher.matches();
            if(!matches){
                builder.append(String.format("Value does not match required pattern. Required pattern is %s but found %s. \n",patternString, strValue.length()));
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
