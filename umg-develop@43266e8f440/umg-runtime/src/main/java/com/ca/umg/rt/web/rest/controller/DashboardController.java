/*
 * DashboardController.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.web.rest.controller;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ca.umg.rt.core.dashboard.delegate.DashboardDelegate;
import com.ca.umg.rt.core.dashboard.info.DashboardInfo;

/**
 * REST controller for dash board reporting.  Report metrics for the runtime system.
 **/
@Controller
@RequestMapping("/api/dashboard")
public class DashboardController
{
	private static final Logger      LOGGER            = LoggerFactory.getLogger(DashboardController.class);
	@Inject
	private DashboardDelegate        dashboardDelegate;

	/**
	 * GET /api/dashboard/statistics - > get container statistics.
	 *
	 * @return DOCUMENT ME!
	 **/
	@RequestMapping(value = "/statistics", method = RequestMethod.GET)
	public @ResponseBody
	ResponseEntity<DashboardInfo> getStatistics()
	{
		LOGGER.debug("Processing request for dashboard statistics");
		return new ResponseEntity<DashboardInfo>(dashboardDelegate.getStatistics(), HttpStatus.OK);
	}
}
