package com.ca.umg.rt.validator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DataTypes {
	
    public static final String OBJECT="object";
    public static final String ARRAY="array";
    public static final String DOUBLE="double";
    public static final String INTEGER="integer";
    public static final String LONG="long";
    public static final String BIGINTEGER="biginteger";
    public static final String BIGDECIMAL="bigdecimal";
    public static final String STRING="string";
    public static final String DATE="date";
    public static final String DATETIME="datetime";
    public static final String BOOLEAN="boolean";
    private static Map<String,Class> classMap = new HashMap<String, Class>();   
    private static Map<String,List<Class>> typeClassMap = new HashMap<String, List<Class>>();  
    
    
   

	private DataTypes(){}   

    static{
        classMap.put("integer", Integer.class); 
        classMap.put("string", String.class);
        classMap.put("boolean",Boolean.class);
        classMap.put("date", String.class);
        classMap.put("datetime", String.class);
        List<Class> bigintClasses = new ArrayList<Class>();
        bigintClasses.add(Long.class);
        bigintClasses.add(Integer.class);
        bigintClasses.add(BigInteger.class);
        typeClassMap.put("biginteger",bigintClasses);
        List<Class> bigdecimallasses = new ArrayList<Class>();
        bigdecimallasses.add(Double.class);
        bigdecimallasses.add(Integer.class);
        bigdecimallasses.add(Long.class);
        bigdecimallasses.add(BigDecimal.class);
        bigdecimallasses.add(BigInteger.class);
        typeClassMap.put("bigdecimal",bigdecimallasses);
        List<Class> objectClasses = new ArrayList<Class>();
        objectClasses.add(Map.class);
        objectClasses.add(List.class);
        typeClassMap.put("object", objectClasses);
        List<Class> doubleClasses = new ArrayList<Class>();
        doubleClasses.add(Integer.class);
        doubleClasses.add(Double.class);
        typeClassMap.put("double",doubleClasses);
        List<Class> longClasses = new ArrayList<Class>();
        longClasses.add(Integer.class);
        longClasses.add(Long.class);
        typeClassMap.put("long",longClasses);
        
        
    }  
    
    public static Class getClassMap(String type){
        return classMap.get(type);
    }
    
    public static List<Class> getTypeClassMap(String type){
        return typeClassMap.get(type);
    }
   
    
   
}

