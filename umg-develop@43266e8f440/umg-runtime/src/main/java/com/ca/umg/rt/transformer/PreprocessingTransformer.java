/*
 * MappingTransformer.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.transformer;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.Message;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.transformer.AbstractTransformer;

import com.codahale.metrics.annotation.Timed;

/**
 * Prepare final message contents. Validate all expected outputs and prepare messages if any required items are missing.
 * This is to avoid any chaos in further processing.
 **/
public class PreprocessingTransformer
	extends AbstractTransformer
{
	private static final Logger LOGGER        = LoggerFactory.getLogger(PreprocessingTransformer.class);
	private static final String MESSAGE_ID    = "id";
	//private static final String TENANT_REQUEST = "tenantRequest";
	private static final String TENANT_RESPONSE = "tenantResponse";
	//private static final String MODEL_REQUEST = "modelRequest";
	//private static final String MODEL_RESPONSE = "modelResponse";
	//private static final String VALIDATIONS = "validations";
	private static final String ERRORS = "errors";

	/**
	 * Does transformation of data from model call result to tenant format
	 *
	 * @param message {@link Message} 
	 *
	 * @return {@link Message}
	 **/
	@SuppressWarnings("unchecked")
	@Override
	@Timed
	protected Object doTransform(final Message<?> message)
	{
        long startTime = System.currentTimeMillis();
		LOGGER.debug("Output data transformation starated for message with id {}",
		             message.getHeaders().get(MESSAGE_ID));

		Map<String, Object> payload           = (Map<String, Object>)message.getPayload();
		//Map<String, Object> tnantRequest = (Map<String,Object>)payload.get(TENANT_REQUEST);
		Map<String, Object> tenantResponse = (Map<String,Object>)payload.get(TENANT_RESPONSE);
		//Map<String, Object> modelRequest = (Map<String,Object>)payload.get(MODEL_REQUEST);
		//Map<String, Object> modelResponse = (Map<String,Object>)payload.get(MODEL_RESPONSE);
		//Map<String, Object> validations = (Map<String,Object>)payload.get(VALIDATIONS);
		Map<String, Object> errors = (Map<String,Object>)payload.get(ERRORS);
		
		if(tenantResponse==null || tenantResponse.isEmpty()){
		    tenantResponse = new LinkedHashMap<String, Object>();
	        Map<String,Object> tenantResponseHeader = new LinkedHashMap<String, Object>();
	        tenantResponseHeader.put("modelName", message.getHeaders().get("modelName"));
	        tenantResponseHeader.put("majorVersion", message.getHeaders().get("majorVersion"));
	        tenantResponseHeader.put("minorVersion", message.getHeaders().get("minorVersion"));
	        tenantResponseHeader.put("date", message.getHeaders().get("DATE_USED"));
	        tenantResponseHeader.put("transactionId", message.getHeaders().get(MESSAGE_ID));
	        if(errors!=null && !errors.isEmpty()){
	            tenantResponseHeader.put("errors", errors.toString());
	        }
	        if(message.getHeaders().get("errorMessage")!=null){
	            tenantResponseHeader.put("errors", message.getHeaders().get("errorMessage"));
	        }
	        tenantResponse.put("header", tenantResponseHeader);
	        
	        tenantResponse.put("data", new LinkedHashMap<String, Object>());     

	        payload.put("tenantResponse", tenantResponse);
		}
		
		Object obj = MessageBuilder.withPayload(message.getPayload())
	            .copyHeaders(message.getHeaders()).build();
        LOGGER.debug("PreprocessingTransformer.doTransform : " + (System.currentTimeMillis() - startTime));
        return obj;
	}
}
