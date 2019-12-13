/*
 * PayloadToMapTransformer.java
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

import com.ca.umg.rt.custom.serializers.DoubleSerializerModule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 **/
public class TransactionTransformer
	extends AbstractTransformer
{
    private static final Logger LOGGER        = LoggerFactory.getLogger(TransactionTransformer.class);
	/**
	 * DOCUMENT ME!
	 *
	 * @param message DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws JsonProcessingException
	 **/
	@SuppressWarnings("unchecked")
	@Override
	protected Object doTransform(final Message<?> message)
	  throws JsonProcessingException
	{
        long startTime = System.currentTimeMillis();
		Map<String, Object> payload         = (Map<String, Object>)message.getPayload();
		ObjectMapper        mapper          = new ObjectMapper();
		mapper.registerModule(new DoubleSerializerModule());
		Map<String, Object> tenantRequest   = (Map<String, Object>)payload.get("tenantRequest");
		Map<String, Object> tenantRresponse = (Map<String, Object>)payload.get("tenantResponse");
		Map<String, Object> modelRequest    = (Map<String, Object>)payload.get("modelRequest");
		Map<String, Object> modelResponse   = (Map<String, Object>)payload.get("modelResponse");
		Map<String, Object> newPayload      = new LinkedHashMap<String, Object>();
		newPayload.put("tenantRequest", mapper.writeValueAsString(tenantRequest));
		newPayload.put("tenantResponse", mapper.writeValueAsString(tenantRresponse));
		newPayload.put("modelRequest", mapper.writeValueAsString(modelRequest));
		newPayload.put("modelResponse", mapper.writeValueAsString(modelResponse));
		
		Object obj = MessageBuilder.withPayload(newPayload).copyHeaders(message.getHeaders()).build();
        LOGGER.debug("TransactionTransformer.doTransform : " + (System.currentTimeMillis() - startTime));
        return obj;
	}
}
