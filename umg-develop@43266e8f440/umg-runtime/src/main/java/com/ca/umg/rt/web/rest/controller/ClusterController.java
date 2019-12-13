/*
 * ClusterController.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics 
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.web.rest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ca.umg.rt.web.rest.response.RestResponse;

/**
 * 
 * **/
@Controller
@RequestMapping("/api/cluster")
public class ClusterController
{
	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 **/
	public RestResponse<String> getStatus()
	{
		return new RestResponse<String>();
	}
}
