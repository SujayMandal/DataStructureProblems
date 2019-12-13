/*
 * MessageConstants.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics 
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.message;

/**
 * Keys used to represent objects in {@link Message} for integration flows.
 * **/
public final class MessageConstants
{
	public static final String TENANT_REQUEST  = "tenantRequest";
	public static final String TENANT_RESPONSE = "tenantResponse";
	public static final String MODEL_REQUEST   = "modelRequest";
	public static final String MODEL_RESPONSE  = "tenantResponse";

	/**
	 * Creates a new MessageConstants object.
	 **/
	private MessageConstants()
	{
	}
}
