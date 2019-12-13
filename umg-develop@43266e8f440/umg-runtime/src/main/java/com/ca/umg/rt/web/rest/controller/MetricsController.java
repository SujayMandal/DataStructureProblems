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

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ca.umg.rt.core.metrics.delegate.MetricsDelegate;
import com.ca.umg.rt.core.metrics.info.MetricsInfo;

/**
 * 
 **/
@Controller
@RequestMapping("/api/metrics")
public class MetricsController
{
	@Inject
	private MetricsDelegate metricsDelegate;

	/**
	 * DOCUMENT ME!
	 **/
	@RequestMapping(value = "/metrics", method = RequestMethod.GET)
	public @ResponseBody
	ResponseEntity<List<MetricsInfo>> getMetrics()
	{
	    return new ResponseEntity<List<MetricsInfo>>(metricsDelegate.getMetrics(), HttpStatus.OK);
	}
	
	/**
     * DOCUMENT ME!
     **/
    @RequestMapping(value = "/metrics/{instanceName}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<MetricsInfo> getMetrics(@RequestParam("instanceName")
    String                                         instanceName)
    {
        return new ResponseEntity<MetricsInfo>(metricsDelegate.getMetrics(instanceName), HttpStatus.OK);
    }
}
