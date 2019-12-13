/*
 * TypeValidator.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics 
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.validator;

import java.util.Map;

/**
 * Interface used for data type validation
 * **/
public interface TypeValidator
{
	/**
	 * Validates given value for data type.
	 *
	 * @param value DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 **/
	public String validate(Object value);
	
	public String validate(Object value,Map<String,Object> properties, String apiName);
	
	public Object validateAndConvert(Object value);
}
