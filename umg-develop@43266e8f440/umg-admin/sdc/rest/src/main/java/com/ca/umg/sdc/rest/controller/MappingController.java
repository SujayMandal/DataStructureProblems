package com.ca.umg.sdc.rest.controller;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.util.ConversionUtil;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.umg.business.common.info.ResponseWrapper;
import com.ca.umg.business.common.info.SearchOptions;
import com.ca.umg.business.mapping.delegate.MappingDelegate;
import com.ca.umg.business.mapping.info.MappingDescriptor;
import com.ca.umg.business.mapping.info.MappingHierarchyInfo;
import com.ca.umg.business.mapping.info.MappingInfo;
import com.ca.umg.business.mapping.info.MappingsCopyInfo;
import com.ca.umg.business.mapping.info.QueryLaunchInfo;
import com.ca.umg.business.validation.ValidationError;
import com.ca.umg.sdc.rest.constants.RestConstants;
import com.ca.umg.sdc.rest.utils.RestResponse;

@Controller
@RequestMapping("/mapping")
public class MappingController {

    private static final String TID_NAME = "tidName";

    private static final Logger LOGGER = LoggerFactory.getLogger(MappingController.class);

    @Inject
    private MappingDelegate mappingDelegate;

    @RequestMapping(value = "/listAll")
    @ResponseBody
    public RestResponse<List<MappingHierarchyInfo>> listAll() {
        LOGGER.info("Entered listAll method");
        RestResponse<List<MappingHierarchyInfo>> response = new RestResponse<List<MappingHierarchyInfo>>();
        List<MappingHierarchyInfo> mappingHierarchyInfos = null;
        try {
            mappingHierarchyInfos = mappingDelegate.getMappingHierarchyInfos();
            response.setError(false);
            response.setMessage("Done");
            response.setResponse(mappingHierarchyInfos);
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setErrorCode(e.getCode());
            response.setError(true);
            response.setMessage(e.getLocalizedMessage());
            // return response;
        }
        return response;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public RestResponse<MappingInfo> find(@PathVariable("id") String identifier) {
        LOGGER.info("Entered the find method");
        RestResponse<MappingInfo> response = new RestResponse<MappingInfo>();
        // passing to delegate
        MappingInfo mappingInfo = null;
        try {
            mappingInfo = mappingDelegate.find(identifier);
            response.setError(false);
            response.setMessage("done");
            response.setResponse(mappingInfo);
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setErrorCode(e.getCode());
            response.setError(true);
            response.setMessage(e.getLocalizedMessage());
            // return response;
        }
        return response;
    }

    @RequestMapping(value = "/getMappingsForModel", method = RequestMethod.POST)
    @ResponseBody
    public RestResponse<MappingDescriptor> extractTidMid(@RequestParam(value = "derivedModelName") String derivedModelName,
            @RequestParam(value = "derivedTidName") String derivedTidName, @RequestParam(value = TID_NAME) String tidName) {
        LOGGER.info("Received request to get mapping details for derivedModel {} and TID name - {}.", derivedModelName, tidName);
        RestResponse<MappingDescriptor> response = new RestResponse<MappingDescriptor>();
        MappingDescriptor mappingDescriptor = null;
        try {
            if (tidName == null || StringUtils.isBlank(tidName) || StringUtils.equalsIgnoreCase(tidName, "null")) {
                mappingDescriptor = mappingDelegate.generateMapping(derivedModelName);
            } else {
                mappingDescriptor = mappingDelegate.generateMapping(derivedModelName, derivedTidName, tidName, StringUtils.EMPTY);
            }
            response.setResponse(mappingDescriptor);
            response.setMessage(String.format("Generated mapping successfully for model %s.", derivedModelName));
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setError(true);
            response.setErrorCode(e.getCode());
            response.setMessage(e.getLocalizedMessage());
        } catch (DataAccessException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setError(true);
            response.setMessage("Unable to Extract mapping, as mapping with same name already exists");
        }
        return response;
    }

    @RequestMapping(value = "/saveMapping", method = RequestMethod.POST)
    @ResponseBody
    public RestResponse<MappingDescriptor> save(@RequestParam(value = "mappingDescriptorJson") String mappingDescriptorJson,
            @RequestParam(value = "validate") String validate) {
        RestResponse<MappingDescriptor> response = new RestResponse<MappingDescriptor>();
        try {
            MappingDescriptor mappingDescriptor = ConversionUtil.convertJson(mappingDescriptorJson, MappingDescriptor.class);
            KeyValuePair<String, List<ValidationError>> result = mappingDelegate.saveMappingDescription(mappingDescriptor,
                    mappingDescriptor.getMidName(), validate);

            mappingDescriptor = new MappingDescriptor();
            if (StringUtils.isBlank(result.getKey())) {
                response.setError(true);
                response.setMessage("Mapping could not be saved successfully.");
            } else {
                response.setError(false);
                mappingDescriptor.setTidName(result.getKey());
                if (org.apache.commons.lang.StringUtils.equals(validate, "validate")) {
                    response.setMessage(String.format("Mapping %s saved successfully (post validation).", result.getKey()));
                } else {
                    response.setMessage(String.format("Mapping %s saved successfully (without validation).", result.getKey()));
                }
            }
            mappingDescriptor.setValidationErrors(result.getValue());
            response.setResponse(mappingDescriptor);
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setError(true);
            response.setErrorCode(e.getCode());
            response.setMessage(e.getLocalizedMessage());
        } catch (DataAccessException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setError(true);
            response.setMessage(
                    String.format("Error occured while saving the mapping. Error : %s", e.getRootCause().getMessage()));
        }
        return response;
    }

    @RequestMapping(value = "/getMappingDetails/{tidName}", method = RequestMethod.GET)
    @ResponseBody
    public RestResponse<MappingDescriptor> getMappingDetails(@PathVariable(value = TID_NAME) String tidName) {
        LOGGER.info("Received request to fetch mapping information for TID {}.", tidName);
        RestResponse<MappingDescriptor> response = new RestResponse<MappingDescriptor>();
        try {
            MappingDescriptor mappingDescriptor = mappingDelegate.readMapping(tidName);
            response.setResponse(mappingDescriptor);
            response.setMessage(String.format("Retrieved mapping successfully for tid name %s.", tidName));
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setError(true);
            response.setErrorCode(e.getCode());
            response.setMessage(e.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(value = "/deleteTidMapping/{tidName}", method = RequestMethod.GET)
    @ResponseBody
    public RestResponse<Boolean> deleteTidMapping(@PathVariable(value = TID_NAME) String tidName) {
        LOGGER.info("Received request for deleting Tid wiht name : ", tidName);
        RestResponse<Boolean> response = new RestResponse<Boolean>();
        try {
            Boolean deleteMapStatus = mappingDelegate.deleteMapping(tidName);
            response.setResponse(deleteMapStatus);
            response.setMessage(String.format("Tid Mapping successfully deleted for : %s.", tidName));
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setError(true);
            response.setErrorCode(e.getCode());
            response.setMessage(e.getLocalizedMessage());
        }
        return response;
    }

    // @RequestMapping(value = "/listAllPage/{page}/{pageSize}", method = RequestMethod.GET)
    // @ResponseBody
    // public RestResponse<List<MappingHierarchyInfo>> listAllPage(@PathVariable("page") Integer pageId, @PathVariable("pageSize")
    // Integer pageSize) {
    // LOGGER.info("Entered listAll method");
    // RestResponse<List<MappingHierarchyInfo>> response = new RestResponse<List<MappingHierarchyInfo>>();
    // List<MappingHierarchyInfo> mappingHierarchyInfos = null;
    // try {
    // mappingHierarchyInfos = mappingDelegate.getPagindData(pageId, pageSize);
    // response.setError(false);
    // response.setMessage("Done");
    // response.setResponse(mappingHierarchyInfos);
    // } catch (BusinessException | SystemException e) {
    // LOGGER.error(e.getLocalizedMessage(), e);
    // response.setErrorCode(e.getCode());
    // response.setError(true);
    // response.setMessage(e.getLocalizedMessage());
    // //return response;
    // }
    // return response;
    // }
    /**
     * 
     * @param mappingDescriptorJson
     *            as json string for {@link MappingDescriptor}
     * @param type
     * @return
     */
    @RequestMapping(value = "/createInputMapForQuery", method = RequestMethod.POST)
    @ResponseBody
    public RestResponse<QueryLaunchInfo> createInputMapForQuery(@RequestParam(value = "type") String type,
            @RequestParam(value = TID_NAME) String tidName) {
        LOGGER.info("Received request to generate mapping for type {}.", type);
        RestResponse<QueryLaunchInfo> response = new RestResponse<QueryLaunchInfo>();
        try {
            QueryLaunchInfo result = mappingDelegate.createInputMapForQuery(type, tidName);
            if (result != null) {
                response.setError(false);
                response.setResponse(result);
            }
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setError(true);
            response.setErrorCode(e.getCode());
            response.setMessage(e.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(value = "/getTidListForCopy", method = RequestMethod.GET)
    @ResponseBody
    public RestResponse<List<MappingsCopyInfo>> getTidListForCopy() {
        LOGGER.info("Received request to fetch list of tids(mapping) ");
        RestResponse<List<MappingsCopyInfo>> response = new RestResponse<List<MappingsCopyInfo>>();
        try {
            List<MappingsCopyInfo> tidListToCopy = mappingDelegate.getTidListForCopy();
            response.setResponse(tidListToCopy);
            response.setMessage(String.format("Retrieved mapping successfully for tid Copy ."));
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setError(true);
            response.setErrorCode(e.getCode());
            response.setMessage(e.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(value = "/getMappingStatus", method = RequestMethod.POST)
    @ResponseBody
    public RestResponse<String> getMappingStatus(@RequestParam(value = TID_NAME) String tidName) {
        LOGGER.info("Received request to get mapping status");
        RestResponse<String> response = new RestResponse<String>();
        try {
            String result = mappingDelegate.getMappingStatus(tidName);
            if (result != null) {
                response.setError(false);
                response.setResponse(result);
                response.setMessage(String.format("Mapping staus retrived successfully."));
            }
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setError(true);
            response.setErrorCode(e.getCode());
            response.setMessage(e.getLocalizedMessage());
        }
        return response;
    }

    /**
     * This method will retrieve all mapping and return paginated data back with pageinfo It will be grouped based on model
     */
    @RequestMapping(value = "/listAllMapping", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<ResponseWrapper<List<MappingHierarchyInfo>>> findAllMappings(@RequestBody SearchOptions searchOptions) {
        RestResponse<ResponseWrapper<List<MappingHierarchyInfo>>> response = new RestResponse<ResponseWrapper<List<MappingHierarchyInfo>>>();
        try {
            response.setResponse(mappingDelegate.findAllMappings(searchOptions));
            response.setError(false);
            response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
        } catch (BusinessException | SystemException e) {
            LOGGER.error("findAllVersionName : " + e.getLocalizedMessage(), e);
            response.setError(true);
            response.setErrorCode(e.getCode());
            response.setMessage(e.getLocalizedMessage());
        }
        return response;
    }

}
