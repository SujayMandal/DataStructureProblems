/*
 * RuntimeController.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.web.rest.controller;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ca.umg.rt.core.runtime.delegate.RuntimeDelegate;
import com.ca.umg.rt.core.runtime.info.RuntimeFlowInfo;

/**
 * 
 **/
@Controller
@RequestMapping("/api/runtime")
public class RuntimeController
{
	@Inject
	private RuntimeDelegate runtimeDelgate;

	/**
	 * DOCUMENT ME!
	 **/
	@RequestMapping(value = "/flows", method = RequestMethod.GET)
	public @ResponseBody
	ResponseEntity<Map<String,List<RuntimeFlowInfo>>> listFlow()
	{
	    return new ResponseEntity<Map<String,List<RuntimeFlowInfo>>>(runtimeDelgate.getFlowList(), HttpStatus.OK);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param tenantCode Tenant code.
	 * @param flowName Flow name
	 **/
	@RequestMapping(value = "/flows/{tenantcode}/{flowName}")
	public @ResponseBody
	ResponseEntity<String> getFlow(@RequestParam("tenantcode")
	String                                         tenantCode,
	             @RequestParam("flowName")
	String                                         flowName)
	{
		return new ResponseEntity<String>(HttpStatus.OK);
	}
}
