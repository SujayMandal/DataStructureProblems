/*
 * ArrayValidator.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics 
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.validator.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormat;

import com.ca.umg.rt.validator.DataTypes;
import com.ca.umg.rt.validator.TypeValidator;
import com.ca.umg.rt.validator.TypeValidatorRegistry;

/**
 * 
 * **/
public class ArrayValidator
	implements TypeValidator
{
	/**
	 * DOCUMENT ME!
	 *
	 * @param value DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 **/
	
	public String validate(Object value,String type,String pattern)
	{
		StringBuffer sb = new StringBuffer();		
	        if(!((value instanceof List) || (value.getClass().isArray()))){
	        	sb.append(String.format("Expected %s but received %s", DataTypes.ARRAY, value.getClass().getName()));
	        } else if ((value instanceof List) && ((List)value).size() == 0 ) {
	        	sb.append(String.format("Array cannot be empty please enter some values"));
	        }
	        else if ((value.getClass().isArray()) && ((Object [])value).length == 0) {
	            sb.append(String.format("Array cannot be empty please enter some values"));
	        }else{	 
	        	if(value!=null){
	        		validate(value,type,pattern,sb);
	        	}
			
	        }	      
	        return sb.toString();
	    
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param value DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 **/
	@Override
	public Object validateAndConvert(Object value)
	{
		return null;
	}
	
	/**
	 * DOCUMENT ME!
	 *
	 * @param value DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 **/
	@Override
	public String validate(Object value)
	{
		return null;
	}


	/**
	 * DOCUMENT ME!
	 *
	 * @param value DOCUMENT ME!
	 * @param dataTypeProperties DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 **/
	@SuppressWarnings("unchecked")
	@Override
	public String validate(Object              value,
	                        Map<String, Object> dataTypeProperties, String apiName)
	{
		Map<String, Object> properties = (Map<String, Object>)dataTypeProperties.get("properties");
		List<Integer>       dimensions  = (List<Integer>)properties.get("dimensions");
		String              type       = (String)dataTypeProperties.get("type");
		String              pattern       = (String)properties.get("pattern");
		boolean             oneElement = true;
		int   limit = 1;
		for (Integer item : dimensions)
		{
			if (item > limit)
			{
				oneElement = false;
			}
		}
		if (oneElement && DataTypes.OBJECT.equalsIgnoreCase(type))
		{
			TypeValidator validator = TypeValidatorRegistry.getTypeValidator(DataTypes.OBJECT);
			return validator.validate(value);
		} else {
		    return validate(value,type,pattern);
		}
	}
	
	private String validate( Object value,String type,String pattern,StringBuffer sb){
		if(value!=null){
			 Object[] array = null;
				if(value instanceof List){
			   		 array = ((List) value).toArray();
			   	}else if(value.getClass().isArray()){
			   		array = (Object[]) value;
			   	}
				if(array!=null){
				for (Object obj : array) {		
					if (obj != null && ! (obj instanceof List)) {
						validation(type, pattern, sb, obj);
					}else if(obj instanceof List){
						List<Object[]> childern = (List) obj;
						for(Object child : childern){
							validate(child,type, pattern,sb);					
						}			
						
					}
				}}else{
					validation(type, pattern, sb, value);
				}
		}
		return sb.toString();
	}

	private void validation(String type, String pattern, StringBuffer sb, Object obj) {
		if ((DataTypes.getClassMap(type)!=null && !obj.getClass().equals(DataTypes.getClassMap(type))) ||
				(DataTypes.getTypeClassMap(type)!=null && !DataTypes.getTypeClassMap(type).contains(obj.getClass()))) {	
			if(DataTypes.getTypeClassMap(type)!=null && obj.getClass().equals(Double.class)){
				String doubleStringVal = BigDecimal.valueOf((Double)obj).toPlainString();
				BigDecimal withOutScieNote = BigDecimal.valueOf((Double)obj);						
				if (!doubleStringVal.contains(".")) {
					withOutScieNote = withOutScieNote.setScale(1);
				}
				sb.append(String.format("Expected %s but received %s. Value received is %s. ", type,obj.getClass().getName(),withOutScieNote));
				
			}else{
			sb.append(String.format("Expected %s but received %s. Value received is %s. ", type,obj.getClass().getName(),obj));
			}
		}
		if (StringUtils.equalsIgnoreCase(type, "date")) {
			String datePattern = pattern == null ? "YYYY-MM-DD" : pattern ;						
			try {
				DateTimeFormat.forPattern(datePattern).parseDateTime((String) obj);
			} catch (UnsupportedOperationException | IllegalArgumentException ex) {
				sb.append(String.format("Expected date pattern is %s but received %s", datePattern,
						(String) obj));
			}
		}
	}
}
