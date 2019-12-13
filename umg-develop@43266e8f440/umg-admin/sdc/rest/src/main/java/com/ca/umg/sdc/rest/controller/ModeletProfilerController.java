package com.ca.umg.sdc.rest.controller;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.util.ConstantUtils;
import com.ca.modelet.ModeletClientInfo;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.model.info.ModelExecutionEnvironmentInfo;
import com.ca.umg.business.modelet.profiler.delegate.ModeletProfilerDelegate;
import com.ca.umg.business.modelet.profiler.key.info.ModeletProfilerKeyInfo;
import com.ca.umg.business.modelet.profiler.request.info.ModeletProfilerRequest;
import com.ca.umg.business.modelet.system.info.SystemModeletInfo;
import com.ca.umg.business.util.AdminUtil;
import com.ca.umg.sdc.rest.utils.RestResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("PMD")
@Controller
@RequestMapping("/modelet/profiler")
public class ModeletProfilerController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ModeletProfilerController.class);

	@Inject
	private ModeletProfilerDelegate modeletProfilerDelegate;

	@RequestMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
	@ResponseBody
	public RestResponse<String> createProfile(@RequestBody final ModeletProfilerRequest modeletProfiler) {
		LOGGER.info("Request reached for creating a new modelet profiler. New modelet profiler Details : {}", modeletProfiler);
		final RestResponse<String> response = new RestResponse<>();
		boolean actualAdminAware = AdminUtil.getActualAdminAware();
		AdminUtil.setAdminAwareTrue();
		try {
			modeletProfilerDelegate.createModeletProfile(modeletProfiler);
			LOGGER.info("Modelet Profiler is created succefully");
			response.setError(false);
			response.setResponse("Modelet profiler created Successfully.");
		} catch (BusinessException | SystemException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			response.setError(true);
			response.setErrorCode(e.getCode());
			response.setMessage(e.getLocalizedMessage());
		} finally {
			AdminUtil.setActualAdminAware(actualAdminAware);
		}

		return response;
	}

	@RequestMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
	@ResponseBody
	public RestResponse<String> updateProfile(@RequestBody final ModeletProfilerRequest modeletProfiler) {
		LOGGER.info("Request reached for updating modelet profiler. Profiler Details : {}", modeletProfiler);
		final RestResponse<String> response = new RestResponse<>();
		boolean actualAdminAware = AdminUtil.getActualAdminAware();
		AdminUtil.setAdminAwareTrue();
		try {
			modeletProfilerDelegate.updateModeletProfile(modeletProfiler);
			LOGGER.info("Modelet profiler updated succefully");
			response.setError(false);
			response.setResponse("Modelet profiler updated Successfully.");
		} catch (BusinessException | SystemException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			response.setError(true);
			response.setErrorCode(e.getCode());
			response.setMessage(e.getLocalizedMessage());
		} finally {
			AdminUtil.setActualAdminAware(actualAdminAware);
		}

		return response;
	}

	@RequestMapping(value = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
	@ResponseBody
	public RestResponse<String> deleteProfile(@RequestBody final ModeletProfilerRequest modeletProfiler) {
		LOGGER.info("Request reached for deleting modelet profiler. {}", modeletProfiler.getId());
		final RestResponse<String> response = new RestResponse<>();
		boolean actualAdminAware = AdminUtil.getActualAdminAware();
		AdminUtil.setAdminAwareTrue();
		try {
			modeletProfilerDelegate.removeModeletProfile(modeletProfiler.getId());
			LOGGER.info("Modelet Profiler removed succefully");
			response.setError(false);
			response.setResponse("Modelet profiler deleted Successfully.");
		} catch (BusinessException | SystemException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			response.setError(true);
			response.setErrorCode(e.getCode());
			response.setMessage(e.getLocalizedMessage());
		} finally {
			AdminUtil.setActualAdminAware(actualAdminAware);
		}

		return response;
	}

	@RequestMapping(value = "/list/all", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
	@ResponseBody
	public RestResponse<List<ModeletProfilerRequest>> fetchAllModeletProfiles() {
		LOGGER.info("Request reached for fetching all modelet profiler.");
		final RestResponse<List<ModeletProfilerRequest>> response = new RestResponse<>();
		try {
			List<ModeletProfilerRequest> profilerInfos = modeletProfilerDelegate.fetchAllModeletProfiler();
			LOGGER.info("All modelet profiler retrieved succefully");
			response.setError(false);
			response.setResponse(profilerInfos);
		} catch (BusinessException | SystemException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			response.setError(true);
			response.setErrorCode(e.getCode());
			response.setMessage(e.getLocalizedMessage());
		}

		return response;
	}

	@RequestMapping(value = "/get/{id:.+}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
	@ResponseBody
	public RestResponse<ModeletProfilerRequest> fetchModeletProfile(@PathVariable final String id) {
		LOGGER.info("Request reached to fetch modelet profiler for profiler ID : {}.", id);
		final RestResponse<ModeletProfilerRequest> response = new RestResponse<>();
		try {
			ModeletProfilerRequest profiler = modeletProfilerDelegate.fetchModeletProfiler(id);
			LOGGER.info("modelet profiler retrieved succefully");
			response.setError(false);
			response.setResponse(profiler);
		} catch (BusinessException | SystemException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			response.setError(true);
			response.setErrorCode(e.getCode());
			response.setMessage(e.getLocalizedMessage());
		}

		return response;
	}

	@RequestMapping(value = "/default-data", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
	@ResponseBody
	public RestResponse<ModeletProfilerRequest> fetchDefaultModeletProfile() {
		LOGGER.info("Request reached to fetch default modelet profiler.");
		final RestResponse<ModeletProfilerRequest> response = new RestResponse<>();
		try {
			ModeletProfilerRequest profiler = modeletProfilerDelegate.fetchDefaultModeletProfilerData();
			LOGGER.info("modelet profiler default values retrieved succefully");
			response.setError(false);
			response.setResponse(profiler);
		} catch (BusinessException | SystemException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			response.setError(true);
			response.setErrorCode(e.getCode());
			response.setMessage(e.getLocalizedMessage());
		}

		return response;
	}

	@RequestMapping(value = "/key/list", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
	@ResponseBody
	public RestResponse<List<ModeletProfilerKeyInfo>> fetchModeletProfileKeyList() {
		LOGGER.info("Request reached for fetching all modelet profiler key list.");
		final RestResponse<List<ModeletProfilerKeyInfo>> response = new RestResponse<>();
		try {
			List<ModeletProfilerKeyInfo> profilerKeys = modeletProfilerDelegate.fetchModeletProfilerKeys();
			LOGGER.info("All modelet profiler keys fetched succefully");
			response.setError(false);
			response.setResponse(profilerKeys);
		} catch (BusinessException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			response.setError(true);
			response.setErrorCode(e.getCode());
			response.setMessage(e.getLocalizedMessage());
		}

		return response;
	}

	@RequestMapping(value = "/system/modelets", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
	@ResponseBody
	public RestResponse<List<SystemModeletInfo>> fetchSystemModeletList() {
		LOGGER.info("Request reached for fetching all system modelet.");
		final RestResponse<List<SystemModeletInfo>> response = new RestResponse<>();
		try {
			List<SystemModeletInfo> profilerKeys = modeletProfilerDelegate.fetchSystemModeletList();
			LOGGER.info("All system modelet fetched successfully");
			response.setError(false);
			response.setResponse(profilerKeys);
		} catch (BusinessException | SystemException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			response.setError(true);
			response.setErrorCode(e.getCode());
			response.setMessage(e.getLocalizedMessage());
		}

		return response;
	}

	@RequestMapping(value = "/modify-map", method = RequestMethod.POST)
	@ResponseBody
	public RestResponse<String> updateSystemModeletProfile(@RequestParam(value = "hostName") final String hostName,
			@RequestParam(value = "port") final String port, @RequestParam(value = "profilerId") final String profilerId) {
		LOGGER.info("Request reached for updating system modelet. hostName : {}, port number : {}, profiler id : {}", hostName, port, profilerId);
		final RestResponse<String> response = new RestResponse<>();
		try {
			modeletProfilerDelegate.updateSytemModeletProfiler(hostName, port, profilerId);
			LOGGER.info("System modelet linked successfully succefully");
			response.setError(false);
			response.setResponse("System modelet linked successfully with profiler.");
		} catch (BusinessException | SystemException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			response.setError(true);
			response.setErrorCode(e.getCode());
			response.setMessage(e.getLocalizedMessage());
		}

		return response;
	}

	//Delinking profiler disabled.
	/*
	@RequestMapping(value = "/remove-profiler", method = RequestMethod.POST)
	@ResponseBody
	public RestResponse<String> removeModeletProfilerMap(@RequestParam(value = "modeletId") final String modeletId) {
		LOGGER.info("Request reached for removing system modelet map with profiler. Modelet id : {}", modeletId);
		final RestResponse<String> response = new RestResponse<>();
		try {
			modeletProfilerDelegate.removeModeletProfileMap(modeletId);
			LOGGER.info("system modelet unlinked succefully with profiler.");
			response.setError(false);
			response.setResponse("System modelet unlinked succefully with profiler.");
		} catch (BusinessException | SystemException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			response.setError(true);
			response.setErrorCode(e.getCode());
			response.setMessage(e.getLocalizedMessage());
		}

		return response;
	}
	*/

	@RequestMapping(value = "/execution/environments", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
	@ResponseBody
	public RestResponse<List<ModelExecutionEnvironmentInfo>> getActiveModelExecutionEnvList() {
		LOGGER.info("Request reached for fetching execution environments");
		final RestResponse<List<ModelExecutionEnvironmentInfo>> response = new RestResponse<>();
		try {
			List<ModelExecutionEnvironmentInfo> result = modeletProfilerDelegate.getActiveModelExecutionEnvList();
			LOGGER.info("Execution environment fetched successfully");
			response.setError(false);
			response.setResponse(result);
		} catch (BusinessException | SystemException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			response.setError(true);
			response.setErrorCode(e.getCode());
			response.setMessage(e.getLocalizedMessage());
		}

		return response;
	}

	@RequestMapping(value = "/restartModelets", method = RequestMethod.POST)
	public RestResponse<String> restartModelets(@RequestBody final String modeletPayload) {
		final RestResponse<String> response = new RestResponse<>();
		try {
			List<ModeletClientInfo> modeletClientInfoList = formatRequestPayloadToList(modeletPayload);
			modeletProfilerDelegate.restartModelets(modeletClientInfoList);
			response.setError(false);
			response.setResponse("Restart request for selected modelets has been submitted");
		} catch (SystemException | BusinessException ex) {
			LOGGER.error(ex.getLocalizedMessage(), ex);
			response.setError(true);
			response.setErrorCode(ex.getCode());
			response.setMessage(ex.getLocalizedMessage());
		} catch (IOException ex) {
			LOGGER.error(ex.getLocalizedMessage(), ex);
			response.setError(true);
			response.setMessage(ex.getLocalizedMessage());
		}
		return response;
	}

	@RequestMapping(value = "/downloadModeletLog", method = RequestMethod.POST)
	public void downloadModeletLogs(HttpServletResponse response, @RequestBody final String modeletPayload) {
		List<String> logLines = new ArrayList<>();
		try {
			ModeletClientInfo modeletClientInfo = formatRequestPayload(modeletPayload);
			String data = modeletProfilerDelegate.downloadModeletLogs(modeletClientInfo);
			if(StringUtils.isNotEmpty(data)) {
				logLines.addAll(Arrays.asList(StringUtils.split(data, BusinessConstants.CHAR_NEWLINE)));
			}
			response.setContentType("text/plain");
			response.addHeader("Content-Disposition", "attachment; filename=" + modeletClientInfo.getPort() + ".txt");
			for (String line : logLines) {
				response.getOutputStream().println(line);
			}
			response.getOutputStream().flush();
		} catch (SystemException | BusinessException | IOException ex) {
			LOGGER.error("Exception occurred while downloading log file of the modelet.");
			LOGGER.error(ex.getLocalizedMessage(), ex);
		}
	}

	private List<ModeletClientInfo> formatRequestPayloadToList(final String modelRestartRequest) throws IOException {
		List<ModeletClientInfo> modeletClientInfoList = new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JodaModule());
		List<Map<String, Object>> modelRestartRequestList = mapper
				.readValue(modelRestartRequest, new TypeReference<List<LinkedHashMap<String, Object>>>() {});
		if(CollectionUtils.isNotEmpty(modelRestartRequestList)) {
			for (Map<String, Object> modeletPayloadMap : modelRestartRequestList) {
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
		return modeletClientInfoList;
	}

	private ModeletClientInfo formatRequestPayload(final String modelRestartRequest) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JodaModule());
		Map<String, Object> modeletPayloadMap = mapper.readValue(modelRestartRequest, new TypeReference<LinkedHashMap<String, Object>>() {});
		ModeletClientInfo modeletClientInfo = new ModeletClientInfo();
		modeletClientInfo.setPort((Integer) modeletPayloadMap.get(ConstantUtils.PORT));
		modeletClientInfo.setHost((String) modeletPayloadMap.get(ConstantUtils.HOST));
		modeletClientInfo.setExecutionLanguage((String) modeletPayloadMap.get(ConstantUtils.EXECUTION_LANGUAGE));
		modeletClientInfo.setExecEnvironment((String) modeletPayloadMap.get(ConstantUtils.EXECUTION_ENVIRONMENT));
		modeletClientInfo.setModeletStatus((String) modeletPayloadMap.get(ConstantUtils.MODELET_STATUS));
		modeletClientInfo.setrServePort((int) modeletPayloadMap.get(ConstantUtils.R_SERVE_PORT));
		modeletClientInfo
				.setrMode(modeletPayloadMap.get(ConstantUtils.R_MODE) != null ? modeletPayloadMap.get(ConstantUtils.R_MODE).toString() : null);
		return modeletClientInfo;
	}
}
