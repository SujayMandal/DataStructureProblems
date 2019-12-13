package com.ca.umg.rt.validator.impl;

import java.util.List;
import java.util.Map;

import com.ca.umg.rt.validator.DataTypes;
import com.ca.umg.rt.validator.TypeValidator;

public class ObjectValidator implements TypeValidator{

    @Override
    public String validate(Object value) {
        String message = null;
        if(!(value instanceof Map || value instanceof List)){
            message = String.format("Expected %s but received %s", DataTypes.OBJECT, value.getClass().getName());
        }
        return message;
    }

    @Override
    public Object validateAndConvert(Object value) {
        return null;
    }
    
    @Override
    public String validate(Object value, Map<String, Object> properties, String apiName) {
        return validate(value);
    }

}
