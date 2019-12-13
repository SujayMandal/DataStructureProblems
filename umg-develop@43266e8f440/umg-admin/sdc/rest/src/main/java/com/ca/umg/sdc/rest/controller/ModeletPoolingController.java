package com.ca.umg.sdc.rest.controller;

import com.ca.framework.core.entity.ModeletRestartInfo;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.util.ConstantUtils;
import com.ca.modelet.ModeletClientInfo;
import com.ca.umg.business.modelet.profiler.delegate.ModeletProfilerDelegate;
import com.ca.umg.business.pooling.delegate.ModeletPoolingDelegate;
import com.ca.umg.business.pooling.info.ModeletRestartDetails;
import com.ca.umg.business.pooling.model.CompletePoolDetails;
import com.ca.umg.business.pooling.model.ModeletPoolingDetails;
import com.ca.umg.sdc.rest.utils.RestResponse;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("PMD")
@Controller
@RequestMapping("/modeletPooling")
public class ModeletPoolingController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ModeletPoolingController.class);

	private static final String POOL_ID = "poolId";

	private static final String SEARCH_STRING = "searchString";

	@Inject
	private ModeletPoolingDelegate delegate;

	@Inject
	private ModeletProfilerDelegate modeletProfilerDelegate;

	@RequestMapping(value = "/getAllPoolDetails", method = RequestMethod.GET)
	@ResponseBody
	public RestResponse<List<CompletePoolDetails>> getAllPoolDetails() {
		LOGGER.info("getAllPoolDetails API is called");
		final RestResponse<List<CompletePoolDetails>> response = new RestResponse<>();
		try {
			final List<CompletePoolDetails> allCompletePoolDetails = delegate.getAllPoolDetails();
			if(allCompletePoolDetails != null && !allCompletePoolDetails.isEmpty()) {
				LOGGER.info("Pools found, Pool Count is : {}", allCompletePoolDetails.size());
				response.setResponse(allCompletePoolDetails);
			} else {
				LOGGER.debug("Pools are not found");
				response.setMessage("Pools not found");
			}
		} catch (SystemException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			response.setError(true);
			response.setErrorCode(e.getCode());
			response.setMessage(e.getLocalizedMessage());
		}
		return response;
	}

	@RequestMapping(value = "/getAllModeletClients", method = RequestMethod.GET)
	@ResponseBody
	public RestResponse<List<ModeletClientInfo>> getAllModeletClients() {
		LOGGER.info("getAllModeletClients API is called");
		final RestResponse<List<ModeletClientInfo>> response = new RestResponse<>();
		try {
			final List<ModeletClientInfo> allCompletePoolDetails = delegate.getAllModeletClientDetails();
			if(allCompletePoolDetails != null && !allCompletePoolDetails.isEmpty()) {
				LOGGER.info("modelet clients found, modelet client Count is : {}", allCompletePoolDetails.size());
				response.setResponse(allCompletePoolDetails);
			} else {
				LOGGER.debug("modelet client are not found");
				response.setMessage("modelet client not found");
			}
		} catch (SystemException e) {
			LOGGER.error("getAllModeletClients API failed. {}", e);
			response.setError(true);
			response.setErrorCode(e.getCode());
			response.setMessage(e.getLocalizedMessage());
		}
		return response;
	}

	@RequestMapping(value = "/getModeletPoolingDetails", method = RequestMethod.GET)
	@ResponseBody
	public RestResponse<ModeletPoolingDetails> getModeletPoolingDetails() {
		LOGGER.info("getModeletPoolingDetails API is called");
		final RestResponse<ModeletPoolingDetails> response = new RestResponse<>();
		try {
			final ModeletPoolingDetails modeletPoolingDetails = delegate.getModeletPoolingDetails();
			if(modeletPoolingDetails != null) {
				LOGGER.info("Modelet Pooling Details found");
				response.setResponse(modeletPoolingDetails);
			} else {
				LOGGER.debug("Modelet Pooling Details not found");
				response.setMessage("Modelet Pooling Details not found");
			}
		} catch (SystemException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			response.setError(true);
			response.setErrorCode(e.getCode());
			response.setMessage(e.getLocalizedMessage());
		}

		return response;
	}

	@RequestMapping(value = "/createPool", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public RestResponse<String> createPool(@RequestBody final CompletePoolDetails completePoolDetails) {
		LOGGER.info("Request reached for creating a new pool. New Pool Details : {}" + completePoolDetails);
		final RestResponse<String> response = new RestResponse<>();
		try {
			delegate.createPool(completePoolDetails);
			LOGGER.info("New Pool is created succefully");
			response.setError(false);
			response.setResponse("Created Successfully");
		} catch (SystemException | BusinessException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			response.setError(true);
			response.setErrorCode(e.getCode());
			response.setMessage(e.getLocalizedMessage());
		}

		return response;
	}

	@RequestMapping(value = "/updatePool", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public RestResponse<String> updatePool(@RequestBody final List<CompletePoolDetails> poolDetailsList) {
		LOGGER.info("Request reached for updating pools. Pool Details : {}" + poolDetailsList);
		final RestResponse<String> response = new RestResponse<>();
		try {
			delegate.updatePool(poolDetailsList);
			LOGGER.info("Pools are updated succefully");
			response.setError(false);
			response.setResponse("Updated Successfully");
		} catch (SystemException | BusinessException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			response.setError(true);
			response.setErrorCode(e.getCode());
			response.setMessage(e.getLocalizedMessage());
		}

		return response;
	}

	@RequestMapping(value = "/deletePool/{poolId}")
	@ResponseBody
	public RestResponse<String> deletePool(@PathVariable(POOL_ID) final String poolId) {
		LOGGER.info("Request reached for deleting pool Id:" + poolId);
		final RestResponse<String> response = new RestResponse<>();
		try {
			delegate.deletePool(poolId);
			LOGGER.info("Pool is deleted succefully");
			response.setError(false);
			response.setResponse("Deleted Successfully");
		} catch (SystemException | BusinessException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			response.setError(true);
			response.setErrorCode(e.getCode());
			response.setMessage(e.getLocalizedMessage());
		}

		return response;
	}

	@RequestMapping(value = "/searchPool/{searchString:.+}", method = RequestMethod.GET)
	@ResponseBody
	public RestResponse<List<CompletePoolDetails>> searchPool(@PathVariable(SEARCH_STRING) final String searchString) {
		LOGGER.info("Request reached for searching pool, Seaarch String is:" + searchString);
		final RestResponse<List<CompletePoolDetails>> response = new RestResponse<>();
		try {
			final List<CompletePoolDetails> matchedPoolDetails = delegate.searchPool(searchString);
			if(matchedPoolDetails != null && !matchedPoolDetails.isEmpty()) {
				LOGGER.info("Matched Pools found, Matched Pool Count is : {}", matchedPoolDetails.size());
				response.setResponse(matchedPoolDetails);
			} else {
				LOGGER.debug("Pools are not found");
				response.setMessage("Pools not found");
			}
		} catch (SystemException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			response.setError(true);
			response.setErrorCode(e.getCode());
			response.setMessage(e.getLocalizedMessage());
		}

		return response;
	}

	@RequestMapping(value = "/switchStatus", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public RestResponse<String> switchStatus(@RequestBody final Object modelClientDetails) {
		// TODO NAGA - ADD LOGS HERE
		// TODO NAGA - SEND STATUS - STOP OR START
		final RestResponse<String> response = new RestResponse<>();
		HashMap<String, Object> modelClientDetailsMap = (LinkedHashMap) modelClientDetails;
		ModeletClientInfo modeletClientInfo = new ModeletClientInfo();
		modeletClientInfo.setPort((Integer) modelClientDetailsMap.get("port"));
		modeletClientInfo.setHost((String) modelClientDetailsMap.get("host"));
		modeletClientInfo.setExecutionLanguage((String) modelClientDetailsMap.get("executionLanguage"));
		modeletClientInfo.setExecEnvironment((String) modelClientDetailsMap.get("execEnvironment"));
		modeletClientInfo.setModeletStatus((String) modelClientDetailsMap.get("modeletStatus"));
		modeletClientInfo.setrServePort((int) modelClientDetailsMap.get("rServePort"));
		modeletClientInfo.setrMode((String) modelClientDetailsMap.get("rMode"));

		LOGGER.info("Modelet Host : {}, Modelet Port : {}, Modelet Language : {}, Modelet Status : {}", modeletClientInfo.getHost(),
				modeletClientInfo.getPort(), modeletClientInfo.getExecutionLanguage(), modelClientDetailsMap.get("modeletStatus"));
		List<String> responseList = delegate.switchModelet(modeletClientInfo, modelClientDetailsMap.get("modeletStatus"));

		if(responseList.size() > 0) {
			response.setError(Boolean.TRUE);
			response.setMessage(responseList.get(0));
		} else {
			response.setError(Boolean.FALSE);
		}

		return response;
	}

	@RequestMapping(value = "/restartModelets", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public RestResponse<String> restartModelets(@RequestBody final Object modelClientDetails) {
		final RestResponse<String> response = new RestResponse<>();
		try {
			List<ModeletClientInfo> modeletClientInfoList = new ArrayList<>();
			List<LinkedHashMap> modelClientDetailsMap = (List<LinkedHashMap>) modelClientDetails;
			if(CollectionUtils.isNotEmpty(modelClientDetailsMap)) {
				for (Map<String, Object> modeletPayloadMap : modelClientDetailsMap) {
					ModeletClientInfo modeletClientInfo = new ModeletClientInfo();
					modeletClientInfo.setPort((Integer) modeletPayloadMap.get(ConstantUtils.PORT));
					modeletClientInfo.setHost((String) modeletPayloadMap.get(ConstantUtils.HOST));
					modeletClientInfo.setExecutionLanguage((String) modeletPayloadMap.get(ConstantUtils.EXECUTION_LANGUAGE));
					modeletClientInfo.setExecEnvironment((String) modeletPayloadMap.get(ConstantUtils.EXECUTION_ENVIRONMENT));
					modeletClientInfo.setModeletStatus((String) modeletPayloadMap.get(ConstantUtils.MODELET_STATUS));
					modeletClientInfo.setrServePort((int) modeletPayloadMap.get(ConstantUtils.R_SERVE_PORT));
					modeletClientInfo.setrMode(
							modeletPayloadMap.get(ConstantUtils.R_MODE) != null ? modeletPayloadMap.get(ConstantUtils.R_MODE).toString() : null);
					modeletClientInfoList.add(modeletClientInfo);
				}
			}
			modeletProfilerDelegate.restartModelets(modeletClientInfoList);
			response.setError(false);
			response.setResponse("Restart request for selected modelets has been submitted");
		} catch (SystemException | BusinessException ex) {
			LOGGER.error(ex.getLocalizedMessage(), ex);
			response.setError(true);
			response.setErrorCode(ex.getCode());
			response.setMessage(ex.getLocalizedMessage());
		}

		return response;
	}

	@RequestMapping(value = "/fetch-modelet-response", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public RestResponse<List<String>> getModeletCommandResponse(@RequestBody final Object modelClientDetails) {
		LOGGER.info("fetch modelet response is called");
		final RestResponse<List<String>> response = new RestResponse<>();
		HashMap<String, Object> modelClientDetailsMap = (LinkedHashMap) modelClientDetails;
		ModeletClientInfo modeletClientInfo = new ModeletClientInfo();
		modeletClientInfo.setPort((Integer) modelClientDetailsMap.get("port"));
		modeletClientInfo.setHost((String) modelClientDetailsMap.get("host"));
		modeletClientInfo.setExecutionLanguage((String) modelClientDetailsMap.get("executionLanguage"));
		modeletClientInfo.setExecEnvironment((String) modelClientDetailsMap.get("execEnvironment"));
		modeletClientInfo.setModeletStatus((String) modelClientDetailsMap.get("modeletStatus"));
		modeletClientInfo.setrServePort((int) modelClientDetailsMap.get("rServePort"));
		modeletClientInfo.setrMode((String) modelClientDetailsMap.get("rMode"));

		LOGGER.info("Modelet Host : {}, Modelet Port : {}, Modelet Language : {}, Modelet Status : {}", modeletClientInfo.getHost(),
				modeletClientInfo.getPort(), modeletClientInfo.getExecutionLanguage(), modelClientDetailsMap.get("modeletStatus"));
		List<String> responseList = delegate.fetchModeletCommandResult(modeletClientInfo, modelClientDetailsMap.get("modeletStatus"));

		if(responseList.size() > 0) {
			response.setError(Boolean.FALSE);
			response.setMessage("Running processes fetched successfully.");
			response.setResponse(responseList);
		} else {
			response.setError(Boolean.TRUE);
			response.setMessage("No message retrieved from modelet.");
		}

		return response;
	}

	@RequestMapping(value = "/getModeletRestartDetails", method = RequestMethod.GET)
	@ResponseBody
	public RestResponse<ModeletRestartDetails> getModeletRestartDetais() {
		LOGGER.info("getModeletRestartDetais API is called");
		final RestResponse<ModeletRestartDetails> response = new RestResponse<>();
		try {
			response.setResponse(delegate.getModeletRestartDetails());
		} catch (SystemException e) {
			LOGGER.error("Exception while getting modeletRestart config details : ", e);
			response.setError(true);
			response.setMessage(e.getLocalizedMessage());
		}
		return response;
	}

	@RequestMapping(value = "/addModeletRestartDetails", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public RestResponse<ModeletRestartDetails> addModeletRestartDetais(@RequestBody final Object modeletRestartDeatils) {
		LOGGER.info("addModeletRestartDetais API is called");
		final RestResponse<ModeletRestartDetails> response = new RestResponse<>();
		try {
			List<LinkedHashMap> modeletRestartInfoList = (List<LinkedHashMap>) modeletRestartDeatils;
			List<ModeletRestartInfo> list = new ArrayList<ModeletRestartInfo>();
			for (LinkedHashMap modeletRestartInfo : modeletRestartInfoList) {
				ModeletRestartInfo info = new ModeletRestartInfo();
				info.setId((String) modeletRestartInfo.get("id"));
				info.setModelNameAndVersion((String) modeletRestartInfo.get("modelNameAndVersion"));
				info.setTenantId((String) modeletRestartInfo.get("tenantId"));
				info.setRestartCount(Integer.parseInt(modeletRestartInfo.get("restartCount").toString()));
				list.add(info);
			}
			final ModeletRestartDetails modeletRestartDetails = delegate.addModeletSetting(list);
			response.setResponse(modeletRestartDetails);

		} catch (SystemException e) { // NOPMD
			LOGGER.error("Exception while add/updating modeletRestart config details : ", e);
			response.setError(true);
			response.setMessage(e.getLocalizedMessage());
		}
		return response;
	}

	@RequestMapping(value = "/deleteModeletRestartDetails", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public RestResponse<ModeletRestartDetails> deleteModeletRestartDetais(@RequestBody final Object modeletRestartDeatils) {
		LOGGER.info("deleteModeletRestartDetails API is called");
		final RestResponse<ModeletRestartDetails> response = new RestResponse<>();
		try {
			LinkedHashMap<String, Object> modeletRestartInfoMap = (LinkedHashMap<String, Object>) modeletRestartDeatils;
			ModeletRestartInfo info = new ModeletRestartInfo();
			info.setModelNameAndVersion((String) modeletRestartInfoMap.get("modelNameAndVersion"));
			info.setTenantId((String) modeletRestartInfoMap.get("tenantId"));
			info.setId((String) modeletRestartInfoMap.get("id"));
			final ModeletRestartDetails modeletRestartDetails = delegate.deleteModeletSetting(info);
			response.setResponse(modeletRestartDetails);

		} catch (SystemException e) { // NOPMD
			LOGGER.error("Exception while deleting modeletRestart config details : ", e);
			response.setError(true);
			response.setMessage(e.getLocalizedMessage());
		}
		return response;
	}

}