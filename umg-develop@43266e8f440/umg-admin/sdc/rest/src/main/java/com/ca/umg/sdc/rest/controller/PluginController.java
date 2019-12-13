/**
 * 
 */
package com.ca.umg.sdc.rest.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ca.framework.core.bo.ModelType;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.model.delegate.MediateModelLibraryDelegate;
import com.ca.umg.business.model.info.MediateModelLibraryInfo;
import com.ca.umg.business.modelexecenvs.ModelExecEnvironmentProvider;
import com.ca.umg.business.plugin.delegate.PluginDelegate;
import com.ca.umg.sdc.rest.constants.ModelConstants;
import com.ca.umg.sdc.rest.constants.RestConstants;
import com.ca.umg.sdc.rest.utils.RestResponse;

/**
 * @author raddibas
 * 
 */

@Controller
@RequestMapping("/plugin")
public class PluginController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyndicateDataController.class);

    @Inject
    private PluginDelegate pluginDelegate;

    @Inject
    private MediateModelLibraryDelegate mediateModelLibraryDelegate;

    @Inject
    private ModelExecEnvironmentProvider modelExecEnvironmentProvider;

    @RequestMapping(value = "/getPlugins", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<Map<String, Boolean>> getPluginsForTenant() {
        RestResponse<Map<String, Boolean>> response = new RestResponse<Map<String, Boolean>>();
        try {
            Map<String, Boolean> pluginsMapForTenant = pluginDelegate.getPluginsMappedForTenant();
            response.setError(false);
            response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
            response.setResponse(pluginsMapForTenant);
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setErrorCode(e.getCode());
            response.setError(true);
            response.setMessage(e.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(value = "/downloadModelTemplate/{modelName}/{modelType}", method = RequestMethod.GET)
    public void downloadModelTemplate(@PathVariable("modelName") String modelName, @PathVariable("modelType") String modelType,
            HttpServletResponse response) throws SystemException, BusinessException {
        String headerValue;
        final ModelType mt = ModelType.getModelType(modelType);

        if (mt == ModelType.BULK) {
            headerValue = String.format("attachment; filename=\"%s\"", modelName + "_Bulk_Template.xlsx");
        } else {
            headerValue = String.format("attachment; filename=\"%s\"", modelName + "_Template.xlsx");
        }

        try {
            byte[] modelTemplate = pluginDelegate.getModelTemplate(modelName, mt);
            if (modelTemplate != null) {
                response.setHeader(ModelConstants.CONTENT_DISPOSITION, headerValue);
                response.getOutputStream().write(modelTemplate);
                response.getOutputStream().flush();
            } else {
                writeErrorData(response, modelName);
            }
        } catch (BusinessException | SystemException se) {
            LOGGER.error(se.getLocalizedMessage(), se);
        } catch (IOException e) {
            LOGGER.error("", e);
        }

    }

    private void writeErrorData(HttpServletResponse response, String modelName) {
        try {
            String headerValue = String.format("attachment; filename=\"%s\"", "error_" + modelName + ".txt");
            response.setHeader(ModelConstants.CONTENT_DISPOSITION, headerValue);
            String errorMsg = modelName + " Template doesn't exist.";
            response.getOutputStream().write(errorMsg.getBytes());
            response.getOutputStream().flush();
        } catch (IOException excep) {
            LOGGER.error("Error while Writting error data  ", excep);
        }
    }

    /**
     * @return Response with all the R Versions
     * @throws SystemException
     * @throws BusinessException
     */
    @RequestMapping(value = "/allRModelExecEnv", method = RequestMethod.GET)
    @ResponseBody
    public RestResponse<List<String>> getAllModelExecEnvironments() throws SystemException, BusinessException {
        RestResponse<List<String>> restResopnse = new RestResponse<>();
        try {
            restResopnse.setResponse(modelExecEnvironmentProvider.getNamesByEnvironment("R"));
            restResopnse.setError(false);
        } catch (Exception ex) {// NOPMD
            LOGGER.error("Exception is :", ex);
            restResopnse.setError(Boolean.TRUE);
            restResopnse.setMessage(ex.getMessage());
        }
        return restResopnse;
    }

    @RequestMapping(value = "/validateFileUpload", method = RequestMethod.POST)
    @ResponseBody
    public RestResponse<String> validateTarFile(@RequestParam("tarName") String tarName,
            @RequestParam("checksum") String checksum) throws SystemException, BusinessException {
        RestResponse<String> restResopnse = new RestResponse<>();
        try {
            MediateModelLibraryInfo mediateModelLibraryInfo = new MediateModelLibraryInfo();
            mediateModelLibraryInfo.setChecksum(checksum);
            mediateModelLibraryInfo.setTarName(tarName);
            mediateModelLibraryDelegate.validateMediateLibChecksum(mediateModelLibraryInfo);
            restResopnse.setError(Boolean.FALSE);
        } catch (BusinessException | SystemException ex) {
            LOGGER.error(ex.getLocalizedMessage(), ex);
            restResopnse.setError(Boolean.TRUE);
            restResopnse.setErrorCode(ex.getCode());
            restResopnse.setMessage(ex.getLocalizedMessage());

        }
        return restResopnse;
    }

    /**
     * @return Response with all the R Versions
     * @throws SystemException
     * @throws BusinessException
     */
    @RequestMapping(value = "/allExecutionEnvironments", method = RequestMethod.GET)
    @ResponseBody
    public RestResponse<Set<String>> getAllExecutionEnvironments() throws SystemException, BusinessException {
        RestResponse<Set<String>> restResopnse = new RestResponse<>();
        long requestStartTime = System.currentTimeMillis();
        try {
            Set<String> environments = new TreeSet<>();
            List<String> execEnvironments = modelExecEnvironmentProvider.getAllExecutionEnvironmentNames();
            for (String execEnvironment : execEnvironments) {
                environments.add(StringUtils.substringBefore(execEnvironment, "-"));
            }
            restResopnse.setResponse(environments);
            restResopnse.setError(false);
        } catch (SystemException ex) {
            LOGGER.error(ex.getLocalizedMessage(), ex);
            restResopnse.setError(Boolean.TRUE);
            restResopnse.setErrorCode(ex.getCode());
            restResopnse.setMessage(ex.getLocalizedMessage());
        }
        long requestEndTime = System.currentTimeMillis();
        LOGGER.info("Time taken to fetch all execution environment at Rest Layer "+(requestEndTime-requestStartTime)+" ms");
        return restResopnse;
    }

}
