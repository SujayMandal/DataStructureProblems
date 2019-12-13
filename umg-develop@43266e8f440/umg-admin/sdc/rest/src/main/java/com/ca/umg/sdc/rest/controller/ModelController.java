package com.ca.umg.sdc.rest.controller;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

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
import org.springframework.web.multipart.MultipartFile;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.util.ConversionUtil;
import com.ca.umg.business.common.info.PageRecord;
import com.ca.umg.business.common.info.SearchOptions;
import com.ca.umg.business.mapping.info.ModelMappingInfo;
import com.ca.umg.business.model.delegate.ModelDelegate;
import com.ca.umg.business.model.info.ModelArtifact;
import com.ca.umg.business.model.info.ModelDefinitionInfo;
import com.ca.umg.business.model.info.ModelInfo;
import com.ca.umg.business.model.info.ModelLibraryHierarchyInfo;
import com.ca.umg.business.model.info.ModelLibraryInfo;
import com.ca.umg.sdc.rest.constants.ModelConstants;
import com.ca.umg.sdc.rest.constants.RestConstants;
import com.ca.umg.sdc.rest.exception.RESTExceptionCodes;
import com.ca.umg.sdc.rest.utils.RestResponse;

@Controller
@RequestMapping(ModelConstants.MODEL_URL)
public class ModelController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelController.class);

    @Inject
    private ModelDelegate modelDelegate;

    @RequestMapping(value = ModelConstants.LIST_ALL_MODEL_LIBS)
    @ResponseBody
    public RestResponse<List<ModelLibraryInfo>> listAllModelLibs() {
        LOGGER.info("Entered listAll method");
        RestResponse<List<ModelLibraryInfo>> response = new RestResponse<List<ModelLibraryInfo>>();
        List<ModelLibraryInfo> modelLibList = null;
        try {
            modelLibList = modelDelegate.findAllLibraries();
            response.setError(false);
            response.setMessage("Model Libraries Fetched");
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setErrorCode(e.getCode());
            response.setError(true);
            response.setMessage(e.getLocalizedMessage());
        }
        response.setResponse(modelLibList);
        return response;
    }

    @RequestMapping(value = ModelConstants.LIST_ALL_MODEL_LIB_HIERARCHY)
    @ResponseBody
    public RestResponse<List<ModelLibraryHierarchyInfo>> listAllModelLibsHierarchy() {
        LOGGER.info("Entered listAll method");
        RestResponse<List<ModelLibraryHierarchyInfo>> response = new RestResponse<List<ModelLibraryHierarchyInfo>>();
        List<ModelLibraryHierarchyInfo> modelLibHierarchyList = null;
        try {
            modelLibHierarchyList = modelDelegate.getModelLibraryHierarchyInfos();
            response.setError(false);
            response.setMessage("Model Libraries Fetched");
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setErrorCode(e.getCode());
            response.setError(true);
            response.setMessage(e.getLocalizedMessage());
        }
        response.setResponse(modelLibHierarchyList);
        return response;
    }

    @RequestMapping(value = ModelConstants.DELETE_MODEL_LIBRARY, method = RequestMethod.POST)
    @ResponseBody
    public RestResponse<String> deleteModelLibrary(@RequestParam("modelLibraryID") String modelLibId) throws SystemException {
        RestResponse<String> response = new RestResponse<String>();
        try {
            modelDelegate.deleteModelLibrary(modelLibId);
            response.setResponse("Success");
            response.setError(false);
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setErrorCode(e.getCode());
            response.setError(true);
            response.setMessage(e.getLocalizedMessage());
            response.setResponse("Failure");
        }
        return response;
    }

    @RequestMapping(value = "/listUniqueModelInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<PageRecord<ModelInfo>> listUniqueModelInfo(@RequestBody SearchOptions searchOptions) {
        RestResponse<PageRecord<ModelInfo>> response = new RestResponse<>();
        try {
            PageRecord<ModelInfo> pageRecord = modelDelegate.getUniqueModelInfos(searchOptions);
            response.setResponse(pageRecord);
            response.setError(false);
            response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
        } catch (SystemException | BusinessException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setErrorCode(RESTExceptionCodes.RSE0000001);
            response.setError(true);
            response.setMessage(e.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(value = "/listFilteredModelInfo/{modelName}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<List<ModelInfo>> listFilteredModelInfo(@PathVariable("modelName") String modelName,
            @RequestBody SearchOptions searchOptions) {
        RestResponse<List<ModelInfo>> response = new RestResponse<>();
        try {
            List<ModelInfo> modelInfos = modelDelegate.getAllFilteredModelInfos(searchOptions, modelName);
            response.setResponse(modelInfos);
            response.setError(false);
            response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
        } catch (SystemException | BusinessException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setErrorCode(RESTExceptionCodes.RSE0000001);
            response.setError(true);
            response.setMessage(e.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(value = "/listUniqueModelLibraries", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<PageRecord<ModelLibraryInfo>> listUniqueModelLibraries(@RequestBody SearchOptions searchOptions) {
        RestResponse<PageRecord<ModelLibraryInfo>> response = new RestResponse<>();
        try {
            PageRecord<ModelLibraryInfo> pageRecord = modelDelegate.getUniqueModelLibraries(searchOptions);
            response.setResponse(pageRecord);
            response.setError(false);
            response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
        } catch (SystemException | BusinessException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setErrorCode(RESTExceptionCodes.RSE0000001);
            response.setError(true);
            response.setMessage(e.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(value = "/listFilteredModelLibraries/{modelLibName}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<List<ModelLibraryInfo>> listFilteredModelLibraries(@PathVariable("modelLibName") String modelLibName,
            @RequestBody SearchOptions searchOptions) {
        RestResponse<List<ModelLibraryInfo>> response = new RestResponse<>();
        try {
            List<ModelLibraryInfo> modelLibraries = modelDelegate.getAllFilteredModelLibraries(searchOptions, modelLibName);
            response.setResponse(modelLibraries);
            response.setError(false);
            response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
        } catch (SystemException | BusinessException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setErrorCode(RESTExceptionCodes.RSE0000001);
            response.setError(true);
            response.setMessage(e.getLocalizedMessage());
        }
        return response;
    }

    /**
     * The method to add a model library
     * 
     * @param model
     * @param jarFile
     * @return
     * @throws SystemException
     */
    @RequestMapping(value = ModelConstants.ADD_MODEL_LIBRARY, method = RequestMethod.POST)
    @ResponseBody
    public RestResponse<ModelLibraryInfo> createModelLibrary(@RequestParam("model") String model,
            @RequestParam("jarPath") MultipartFile jarFile) throws SystemException {
        RestResponse<ModelLibraryInfo> response = new RestResponse<ModelLibraryInfo>();
        ModelLibraryInfo modelLibraryInfo = null;
        ModelArtifact jar = null;
        try {
            modelLibraryInfo = ConversionUtil.convertJson(model, ModelLibraryInfo.class);
            jar = new ModelArtifact();
            jar.setName(jarFile.getOriginalFilename());
            jar.setModelName(jarFile.getName());
            jar.setData(jarFile.getInputStream());
            jar.setContentType(jarFile.getContentType());
            modelLibraryInfo.setJar(jar);
            modelLibraryInfo.setJarName(jar.getName());
            modelLibraryInfo = modelDelegate.createModelLibrary(modelLibraryInfo);
            response.setResponse(modelLibraryInfo);
            response.setError(false);
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            throw new SystemException(RESTExceptionCodes.RSE0000001, new Object[] { modelLibraryInfo.getName() }, e);
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setErrorCode(e.getCode());
            response.setError(true);
            response.setMessage(e.getLocalizedMessage());
        }
        return response;
    }

    private ModelArtifact buildArtifacts(MultipartFile multipartFile, String modelName) throws SystemException {
        ModelArtifact modelArtifact = new ModelArtifact();
        try {
            modelArtifact.setContentType(multipartFile.getContentType());
            modelArtifact.setData(multipartFile.getInputStream());
            modelArtifact.setName(multipartFile.getOriginalFilename());
            modelArtifact.setModelName(modelName);
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            throw new SystemException(RESTExceptionCodes.RSE0000001, new Object[] { modelName, e });
        }
        return modelArtifact;
    }

    @RequestMapping(value = ModelConstants.CREATE_MODEL, method = RequestMethod.POST)
    @ResponseBody
    public RestResponse<ModelInfo> createModel(@RequestParam(ModelConstants.NAME) String name,
            @RequestParam(ModelConstants.DESCRIPTION) String description,
            @RequestParam(ModelConstants.ALLOWNULL) Boolean allowNull,
            @RequestParam(ModelConstants.XMLPATH) MultipartFile xmlFile,
            @RequestParam(ModelConstants.DOCUMENTATION) MultipartFile documentation) throws SystemException {
        RestResponse<ModelInfo> response = new RestResponse<ModelInfo>();
        ModelInfo modelInfo = new ModelInfo();
        modelInfo.setName(name);
        modelInfo.setDescription(description);
        modelInfo.setAllowNull(allowNull);
        ModelArtifact artifact = buildArtifacts(xmlFile, name);
        modelInfo.setXml(artifact);
        modelInfo.setIoDefinitionName(artifact.getName());
        artifact = buildArtifacts(documentation, name);
        modelInfo.setDocumentation(artifact);
        modelInfo.setDocumentationName(artifact.getName());
        ModelDefinitionInfo modelDefinitionInfo = new ModelDefinitionInfo();
        modelDefinitionInfo.setType(xmlFile.getContentType());
        modelInfo.setModelDefinition(modelDefinitionInfo);
        try {
            modelInfo = modelDelegate.createModel(modelInfo);
            response.setError(false);
            response.setMessage(modelInfo.getUmgName());
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setErrorCode(e.getCode());
            response.setError(true);
            response.setMessage(e.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(value = ModelConstants.DELETE_MODEL, method = RequestMethod.DELETE)
    @ResponseBody
    public RestResponse<ModelInfo> deleteModel(@PathVariable(ModelConstants.ID) String id) {
        RestResponse<ModelInfo> response = new RestResponse<ModelInfo>();
        try {
            modelDelegate.deleteModel(id);
            response.setMessage(ModelConstants.MODEL_DELETE_MESSAGE);
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setErrorCode(e.getCode());
            response.setError(true);
            response.setMessage(e.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(value = "/getAllVersionNamesForModel/{id}", method = RequestMethod.GET)
    @ResponseBody
    public RestResponse<ModelMappingInfo> getAllVersionNamesForModel(@PathVariable(value = "id") String id)
            throws SystemException, BusinessException {
        RestResponse<ModelMappingInfo> response = new RestResponse<ModelMappingInfo>();
        ModelMappingInfo modelMappingInfo = modelDelegate.getAllVersionNamesForModel(id);
        ModelMappingInfo responseModelMappingInfo = new ModelMappingInfo();
        if (modelMappingInfo != null) {
            responseModelMappingInfo.setMappingNameList(modelMappingInfo.getMappingNameList());
            responseModelMappingInfo.setVersionNameList(modelMappingInfo.getVersionNameList());
            response.setResponse(responseModelMappingInfo);
        }
        return response;
    }

    @RequestMapping(value = ModelConstants.FETCH_MODEL_DETAIL, method = RequestMethod.GET)
    @ResponseBody
    public RestResponse<ModelInfo> fetchModelDetail(@PathVariable(ModelConstants.ID) String id) throws SystemException {
        RestResponse<ModelInfo> response = new RestResponse<ModelInfo>();
        ModelInfo modelInfo = null;
        try {
            modelInfo = modelDelegate.getModelDetails(id);
            if (modelInfo != null) {
                response.setResponse(modelInfo);
            } else {
                response.setMessage(ModelConstants.MODEL_DETAILS_NOT_FOUND);
            }
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setErrorCode(e.getCode());
            response.setError(true);
            response.setMessage(e.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(value = ModelConstants.FETCH_MODEL_LIBRARY_DETAIL, method = RequestMethod.GET)
    @ResponseBody
    public RestResponse<ModelLibraryInfo> fetchModelLibraryDetail(@RequestParam(ModelConstants.ID) String id)
            throws SystemException {
        RestResponse<ModelLibraryInfo> response = new RestResponse<ModelLibraryInfo>();
        ModelLibraryInfo modelLibraryInfo = null;
        try {
            modelLibraryInfo = modelDelegate.getModelLibraryDetails(id);
            if (modelLibraryInfo != null) {
                response.setResponse(modelLibraryInfo);

            } else {
                response.setMessage(ModelConstants.MODEL_LIB_DETAILS_NOT_FOUND);
            }
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setErrorCode(e.getCode());
            response.setError(true);
            response.setMessage(e.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(value = "/getAllModelNames", method = RequestMethod.GET)
    @ResponseBody
    public RestResponse<List<String>> getAllModelNames() {
        RestResponse<List<String>> response = new RestResponse<>();
        List<String> allModelNames = null;
        try {
            allModelNames = modelDelegate.getAllModelNames();
            if (allModelNames != null) {
                response.setResponse(allModelNames);

            } else {
                response.setMessage("model names not found");
            }
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setErrorCode(e.getCode());
            response.setError(true);
            response.setMessage(e.getLocalizedMessage());
        }
        return response;
    }

}
