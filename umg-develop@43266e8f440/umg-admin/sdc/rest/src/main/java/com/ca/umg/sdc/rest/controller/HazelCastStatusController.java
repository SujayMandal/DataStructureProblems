package com.ca.umg.sdc.rest.controller;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ca.umg.business.accessprivilege.bo.MongoIndexValidatorBO;
import com.ca.umg.business.hazelcaststats.delegate.HazelCastStatusDelegate;
import com.ca.umg.sdc.rest.constants.RestConstants;
import com.ca.umg.sdc.rest.utils.RestResponse;

@SuppressWarnings("PMD")
@Controller
@RequestMapping("/hazelCastStatus")
public class HazelCastStatusController {

	private static final Logger LOGGER = LoggerFactory.getLogger(HazelCastStatusController.class);

	@Inject
	private HazelCastStatusDelegate hazelCastStatusDelegate;

	@Inject
	private MongoIndexValidatorBO mongoIndexValidatorBO;

	@RequestMapping(value = "/listAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public RestResponse<Map<Object, Object>> getAllSystemParameters() {
		RestResponse<Map<Object, Object>> response = new RestResponse<Map<Object, Object>>();
		Map<Object, Object> systemParameterInfos = null;
		try {
			systemParameterInfos = hazelCastStatusDelegate.getAllHazelCastEntries();
			response.setError(false);
			response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
			response.setResponse(systemParameterInfos);
		} catch (AccessDeniedException ade) {
			LOGGER.error("Access denied exception in hazelcast ", ade);
			throw ade;
		} catch (Exception se) {// NOPMD
			LOGGER.error("An error occured during getting the hazelcast entries ", se);
			response.setError(true);
			response.setMessage(se.getMessage());
		}
		return response;
	}

	@SuppressWarnings("resource")
	@RequestMapping(value = "/showIndexes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public RestResponse<Map<Object, Object>> getIndexes() {
		RestResponse<Map<Object, Object>> response = new RestResponse<Map<Object, Object>>();
		Map<Object, Object> mongoIndexMap = new HashMap<>();
		try {

			mongoIndexMap = mongoIndexValidatorBO.getIndexStatusResult();
			response.setResponse(mongoIndexMap);
			response.setError(false);
			response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
		} catch (AccessDeniedException ade) {
			LOGGER.error("Access denied exception ", ade);
			throw ade;
		} catch (Exception se) {// NOPMD
			LOGGER.error("An error occured while fetching mongo entries ", se);
			response.setError(true);
			response.setMessage(se.getMessage());
		}
		return response;
	}

}
