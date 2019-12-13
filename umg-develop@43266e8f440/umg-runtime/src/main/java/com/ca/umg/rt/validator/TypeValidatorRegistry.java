package com.ca.umg.rt.validator;

import java.util.HashMap;
import java.util.Map;

import com.ca.umg.rt.validator.impl.ArrayValidator;
import com.ca.umg.rt.validator.impl.BigDecimalValidator;
import com.ca.umg.rt.validator.impl.BigIntegerValidator;
import com.ca.umg.rt.validator.impl.BooleanValidator;
import com.ca.umg.rt.validator.impl.DateValidator;
import com.ca.umg.rt.validator.impl.DoubleValidator;
import com.ca.umg.rt.validator.impl.IntegerValidator;
import com.ca.umg.rt.validator.impl.LongValidator;
import com.ca.umg.rt.validator.impl.ObjectValidator;
import com.ca.umg.rt.validator.impl.StringValidator;


public final class TypeValidatorRegistry {
    private static Map<String,TypeValidator> validatorMap = new HashMap<String, TypeValidator>();
    
    private TypeValidatorRegistry(){}
    static{
        validatorMap.put("integer", new IntegerValidator());
        validatorMap.put("double", new DoubleValidator());
        validatorMap.put("string", new StringValidator());
        validatorMap.put("boolean", new BooleanValidator());
        validatorMap.put("date", new DateValidator());
        validatorMap.put("array", new ArrayValidator());
        validatorMap.put("object", new ObjectValidator());
        validatorMap.put("long", new LongValidator());
        validatorMap.put("biginteger", new BigIntegerValidator());
        validatorMap.put("bigdecimal", new BigDecimalValidator());
    }
    
    public static TypeValidator getTypeValidator(String type){
        return validatorMap.get(type);
    }
}
