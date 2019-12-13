package com.ca.umg.rt.validator.impl;

import java.util.Map;

import com.ca.umg.rt.validator.DataTypes;
import com.ca.umg.rt.validator.TypeValidator;

public class BooleanValidator implements TypeValidator{

    @Override
    public String validate(Object value) {
        String message = null;
        if(!(value instanceof Boolean)){
            message = String.format("Expected %s but received %s", DataTypes.BOOLEAN, value.getClass().getName());
        }
        return message;
    }

    @Override
    public Object validateAndConvert(Object value) {
        Boolean result = null;
        if(value instanceof String){
            result = Boolean.parseBoolean((String)value); 
        }
        return result;
    }
    
    @Override
    public String validate(Object value, Map<String, Object> properties, String apiName) {
       return validate(value);
    }

}
