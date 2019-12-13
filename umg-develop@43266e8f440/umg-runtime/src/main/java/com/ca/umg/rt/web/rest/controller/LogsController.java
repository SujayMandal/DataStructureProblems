/*
 * LogsController.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics 
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.web.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for view and change levels at runtime.
 * 
 * **/
@Controller
@RequestMapping("/api/log")
public class LogsController
{
	private static final Logger LOGGER = LoggerFactory.getLogger(LogsController.class);

	/**
	 * GET /api/log
	 *
	 * @return DOCUMENT ME!
	 **/
	@RequestMapping(value = "/logs", method = RequestMethod.GET)
	public ResponseEntity<Object> listLogs()
	{
		LOGGER.debug("Processing request for logs");
		return new ResponseEntity<Object>("done", HttpStatus.OK);
	}
}
