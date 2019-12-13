package com.ca.umg.rt.web.rest.controller;

import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.util.ConstantUtils;
import com.ca.modelet.ModeletClientInfo;
import com.ca.modelet.common.ServerType;
import com.ca.pool.ModeletPoolingResponse;
import com.ca.pool.model.PoolAllocationInfo;
import com.ca.umg.me2.bo.ModelExecutorBO;
import com.ca.umg.me2.util.ModelExecResponse;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/modeletPooling")
public class ModeletPoolingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModeletPoolingController.class);

    @Inject
    private ModelExecutorBO modelExecutorBO;

    @RequestMapping(value = "/getModeletResponse", method = RequestMethod.POST)
    @ResponseBody
    public ModeletPoolingResponse getModeletResponse(@RequestBody String modelRequest) {
        final ModeletPoolingResponse response = new ModeletPoolingResponse();
        LOGGER.error("fetch Modelet response is called from UI");
        try {
            ModeletClientInfo modeletClientInfo = getModeletClientInfo(modelRequest);

            String modeletCommandResponse = modelExecutorBO.fetchModeletResponse(modeletClientInfo);

            response.setError(false);
            response.setStatus(modeletCommandResponse);
            LOGGER.error("fetch Modelet command result is Done from UI");
        } catch (SystemException ex) {
            LOGGER.error("System Exception occured while fetch Modelet response.Exception is :", ex);
            response.setError(true);
            response.setErrorMessage(ex.getLocalizedMessage());
        } catch (Exception ex) {// NOPMD
            LOGGER.error("Exception occured while fetch Modelet response.Exception is :", ex);
            response.setError(true);
            response.setErrorMessage(ex.getMessage());
        }
        return response;

    }

    @RequestMapping(value = "/startModelet", method = RequestMethod.POST)
    @ResponseBody
    public ModeletPoolingResponse startModelet(@RequestBody String modelRequest) {
        final ModeletPoolingResponse response = new ModeletPoolingResponse();
        LOGGER.error("Start Modelet is called from UI");
        try {
            ModeletClientInfo modeletClientInfo = getModeletClientInfo(modelRequest);

            // if (StringUtils.equalsIgnoreCase(modeletClientInfo.getExecutionLanguage(), ExecutionLanguage.R.getValue())) {
            // modelExecutorBO.startRserveProcess(getModeletClientInfo(modelRequest));
            // } else {
            modelExecutorBO.startModelet(modeletClientInfo);
            // }

            response.setError(false);
            response.setStatus("Successfully started modelet");
            LOGGER.error("Start Modelet is Done from UI");
        } catch (SystemException ex) {
            LOGGER.error("System Exception occured while starting modelet.Exception is :", ex);
            response.setError(true);
            response.setErrorMessage(ex.getLocalizedMessage());
        } catch (Exception ex) {// NOPMD
            LOGGER.error("Exception occured while starting modelet.Exception is :", ex);
            response.setError(true);
            response.setErrorMessage(ex.getMessage());
        }
        return response;

    }

    @RequestMapping(value = "/stopModelet", method = RequestMethod.POST)
    @ResponseBody
    public ModeletPoolingResponse stopModelet(@RequestBody String modelRequest) {
        final ModeletPoolingResponse response = new ModeletPoolingResponse();
        LOGGER.error("Stop Modelet is called from UI");
        try {
            modelExecutorBO.stopModelet(getModeletClientInfo(modelRequest));
            response.setError(false);
            response.setStatus("Successfully stopped modelet");
            LOGGER.error("Stop Modelet is done from UI");
        } catch (SystemException ex) {
            LOGGER.error("System Exception occured while stopping modelet.Exception is :", ex);
            response.setError(true);
            response.setErrorMessage(ex.getLocalizedMessage());
        } catch (Exception ex) {// NOPMD
            LOGGER.error("Exception occured while stopping modelet.Exception is :", ex);
            response.setError(true);
            response.setErrorMessage(ex.getMessage());
        }

        return response;

    }

    @RequestMapping(value = "/restartModelets", method = RequestMethod.POST)
    @ResponseBody
    public ModeletPoolingResponse restartModelets(@RequestBody String modelRestartRequest) {
        final ModeletPoolingResponse response = new ModeletPoolingResponse();
        LOGGER.error("Restart modelets is invoked from UI");
        try {
            modelExecutorBO.restartModelets(getModeletClientInfoList(modelRestartRequest));
        } catch (IOException exception) {
            LOGGER.error("Exception occurred while restarting modelet(s). Exception is :", exception);
            response.setError(Boolean.TRUE);
            response.setErrorMessage(exception.getMessage());
        }
        return response;
    }

    @RequestMapping(value = "/downloadModeletLog", method = RequestMethod.POST)
    @ResponseBody
    public ModeletPoolingResponse downloadModeletLogs(@RequestBody String modelRestartRequest) {
        final ModeletPoolingResponse response = new ModeletPoolingResponse();
        LOGGER.error("Download modelet logs API invoked");
        try {
            String result = modelExecutorBO.fetchModeletLogs(getModeletClientInfo(modelRestartRequest));
            response.setStatus(result);
        } catch (SystemException e) {
            LOGGER.error("System Exception occurred while fetching modelet logs. Exception is :", e);
            response.setError(true);
            response.setErrorMessage(e.getLocalizedMessage());
        } catch (IOException e) {
            LOGGER.error("Exception occurred while fetching modelet logs. Exception is :", e);
            response.setError(true);
            response.setErrorMessage(e.getMessage());
        }
        return response;
    }

    private List<ModeletClientInfo> getModeletClientInfoList(final String modelRestartRequest) throws IOException {
        LOGGER.debug("Converting modelRestartRequest json into ModeletClientInfos");
        List<ModeletClientInfo> modeletClientInfoList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        List<Map<String, Object>> modelRestartRequestList = mapper.readValue(modelRestartRequest, new TypeReference<List<LinkedHashMap<String, Object>>>() {
        });
        if (CollectionUtils.isNotEmpty(modelRestartRequestList)) {
            for (Map<String, Object> mapItem : modelRestartRequestList) {
                ModeletClientInfo modeletClientInfo = new ModeletClientInfo();
                modeletClientInfo.setPort((Integer) mapItem.get(ConstantUtils.PORT));
                modeletClientInfo.setHost(mapItem.get(ConstantUtils.HOST) != null ? mapItem.get(ConstantUtils.HOST).toString() : null);
                modeletClientInfo.setContextPath(StringUtils.EMPTY);
                modeletClientInfo.setServerType(ServerType.SOCKET.toString());
                modeletClientInfo.setExecutionLanguage(mapItem.get(ConstantUtils.EXECUTION_LANGUAGE) != null ? mapItem.get(ConstantUtils.EXECUTION_LANGUAGE).toString() : null);
                modeletClientInfo.setExecEnvironment(mapItem.get(ConstantUtils.EXECUTION_ENVIRONMENT) != null ? mapItem.get(ConstantUtils.EXECUTION_ENVIRONMENT).toString() : null);
                modeletClientInfo.setModeletStatus(mapItem.get(ConstantUtils.MODELET_STATUS) != null ? mapItem.get(ConstantUtils.MODELET_STATUS).toString() : null);
                modeletClientInfo.setrServePort((int) mapItem.get(ConstantUtils.R_SERVE_PORT));
                modeletClientInfo.setrMode(mapItem.get(ConstantUtils.R_MODE) != null ? mapItem.get(ConstantUtils.R_MODE).toString() : null);
                modeletClientInfoList.add(modeletClientInfo);
            }
        }
        LOGGER.debug("Converted Successfully");
        return modeletClientInfoList;
    }

    private ModeletClientInfo getModeletClientInfo(final String modelRequest) throws IOException {
        LOGGER.debug("Converting modelRequest json into ModeletClientInfo");
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        ModeletClientInfo modeletClientInfo = new ModeletClientInfo();
        Map<String, Object> reqObj = mapper.readValue(modelRequest, new TypeReference<LinkedHashMap<String, Object>>() {
        });
        modeletClientInfo.setPort((Integer) reqObj.get(ConstantUtils.PORT));
        modeletClientInfo.setHost(reqObj.get(ConstantUtils.HOST) != null ? reqObj.get(ConstantUtils.HOST).toString() : null);
        modeletClientInfo.setContextPath(StringUtils.EMPTY);
        modeletClientInfo.setServerType(ServerType.SOCKET.toString());
        modeletClientInfo.setExecutionLanguage(reqObj.get(ConstantUtils.EXECUTION_LANGUAGE) != null ? reqObj.get(ConstantUtils.EXECUTION_LANGUAGE).toString() : null);
        modeletClientInfo.setExecEnvironment(reqObj.get(ConstantUtils.EXECUTION_ENVIRONMENT) != null ? reqObj.get(ConstantUtils.EXECUTION_ENVIRONMENT).toString() : null);
        modeletClientInfo.setModeletStatus(reqObj.get(ConstantUtils.MODELET_STATUS) != null ? reqObj.get(ConstantUtils.MODELET_STATUS).toString() : null);
        modeletClientInfo.setrServePort((int) reqObj.get(ConstantUtils.R_SERVE_PORT));
        modeletClientInfo.setrMode(reqObj.get(ConstantUtils.R_MODE) != null ? reqObj.get(ConstantUtils.R_MODE).toString() : null);
        LOGGER.debug("Converted Successfully");
        return modeletClientInfo;
    }

    @RequestMapping(value = "/getAllModeletInfo", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public void getAllModeletInfo() {
        ModelExecResponse<String> execResponse = new ModelExecResponse<String>();
        try {
            LOGGER.error("Get All Modelet Info API is called from UI");
            modelExecutorBO.getAllModeletInfo();
            execResponse.setResponse("Me2 Refresh cache initiated : ");
            LOGGER.error("Get All Modelet Info API is done from UI");
        } catch (SystemException e) {
            execResponse.setSuccess(false);
            execResponse.setErrorCode(e.getCode());
            execResponse.setMessage(e.getLocalizedMessage());
            execResponse.setResponse("Error getting all modelet info");
        }
    }

    @RequestMapping(value = "/allocateModelets", method = RequestMethod.POST)
    @ResponseBody
    public ModeletPoolingResponse allocateModelets(@RequestBody String poolAllocationInfoInfoListStr) throws SystemException {
        LOGGER.info("Allocate modelets");
        ModeletPoolingResponse execResponse = new ModeletPoolingResponse();
        try {
            execResponse.setStatus("Modelet Allocation initiated from Modelet Pooling UI: ");

            List<PoolAllocationInfo> poolAllocationInfoInfoList = null;

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            objectMapper.setVisibilityChecker(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
            try {
                poolAllocationInfoInfoList = objectMapper.readValue(poolAllocationInfoInfoListStr, new TypeReference<List<PoolAllocationInfo>>() {
                });
            } catch (IOException e) {
                LOGGER.error("Error");
            }

            modelExecutorBO.allocateModelets(poolAllocationInfoInfoList);
            execResponse.setError(false);
            execResponse.setErrorMessage(null);
            execResponse.setStatus("DONE");
            execResponse.setErrorCode(null);
        } catch (SystemException e) {
            execResponse.setError(true);
            execResponse.setErrorCode(e.getCode());
            execResponse.setErrorMessage(e.getLocalizedMessage());
            execResponse.setStatus("Failed");
        }
        return execResponse;
    }

}