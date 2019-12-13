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

import org.springframework.integration.Message;
import org.springframework.integration.transformer.AbstractTransformer;

/**
 * 
 **/
public class MeTwoFailoverTransformer
	extends AbstractTransformer
{
	/**
	 * DOCUMENT ME!
	 *
	 * @param message DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 **/
	@Override
	protected Object doTransform(final Message<?> message)
	{
		return message;
	}
}
