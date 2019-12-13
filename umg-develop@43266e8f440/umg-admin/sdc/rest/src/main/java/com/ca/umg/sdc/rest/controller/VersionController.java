
package com.ca.umg.sdc.rest.controller;

import static com.ca.umg.business.version.entity.EmailApprovalEnum.PORTAL_APPROVAL;
import static com.ca.umg.business.version.info.VersionStatus.PENDING_APPROVAL;
import static com.ca.umg.notification.model.NotificationEventNameEnum.MODEL_PUBLISH_APPROVAL;
import static com.ca.umg.notification.model.NotificationHeaderEnum.MAJOR_VERSION;
import static com.ca.umg.notification.model.NotificationHeaderEnum.MINOR_VERSION;
import static com.ca.umg.notification.model.NotificationHeaderEnum.MODEL_NAME;
import static com.ca.umg.notification.model.NotificationHeaderEnum.PUBLISHER_NAME;
import static com.ca.umg.notification.model.NotificationHeaderEnum.TRANSACTION_ID;
import static com.ca.umg.notification.model.NotificationHeaderEnum.UMG_ADMIN_URL;
import static com.ca.umg.notification.model.NotificationHeaderEnum.VERSION_ID;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.info.tenant.TenantInfo;
import com.ca.framework.core.info.tenant.TenantUtil;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.systemparameter.SystemParameterConstants;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.umg.business.common.info.PageRecord;
import com.ca.umg.business.common.info.ResponseWrapper;
import com.ca.umg.business.common.info.SearchOptions;
import com.ca.umg.business.mapping.delegate.MappingDelegate;
import com.ca.umg.business.mapping.info.MappingInfo;
import com.ca.umg.business.mappingnotification.delegate.MappingNotificationDelegate;
import com.ca.umg.business.mid.extraction.info.TenantIODefinition;
import com.ca.umg.business.model.delegate.ModelDelegate;
import com.ca.umg.business.model.info.ModelArtifact;
import com.ca.umg.business.tenant.delegate.TenantDelegate;
import com.ca.umg.business.version.delegate.VersionDelegate;
import com.ca.umg.business.version.info.CreateVersionInfo;
import com.ca.umg.business.version.info.MappingVersionInfo;
import com.ca.umg.business.version.info.VersionAPIInfo;
import com.ca.umg.business.version.info.VersionHierarchyInfo;
import com.ca.umg.business.version.info.VersionInfo;
import com.ca.umg.business.version.info.VersionMetricRequestInfo;
import com.ca.umg.business.version.info.VersionStatus;
import com.ca.umg.business.version.info.VersionSummaryInfo;
import com.ca.umg.business.versiontest.delegate.VersionTestDelegate;
import com.ca.umg.notification.NotificationConstants;
import com.ca.umg.notification.model.NotificationAttachment;
import com.ca.umg.notification.notify.NotificationTriggerDelegate;
import com.ca.umg.report.ReportExceptionCodes;
import com.ca.umg.report.model.ModelReportStatusInfo;
import com.ca.umg.report.model.ModelReportTemplateDefinition;
import com.ca.umg.report.service.ReportService;
import com.ca.umg.sdc.rest.constants.RestConstants;
import com.ca.umg.sdc.rest.utils.RestResponse;

@Controller
@RequestMapping("/version")
public class VersionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyndicateDataController.class);
    private static final String LIBRARY_NAME = "libraryName";
    private static final String TID_NAME = "tidName";
    private static final String VERSION_LIST = "versionList";
    private static final String EMAIL_NOTIFCN_ENABLED = "emailNotificationEnabled";
    private static final String MODEL_PUBLSH_APPROVAL = "modelPublishApproval";

    @Inject
    private VersionDelegate versionDelegate;

    @Inject
    private TenantDelegate tenantDelegate;

    @Inject
    private VersionTestDelegate versionTestDelegate;

    @Inject
    private ReportService reportService;

    @Inject
    private ModelDelegate modelDelegate;

    @Inject
    private NotificationTriggerDelegate notificationDelegate;

    @Inject
    private MappingNotificationDelegate mappingNotificationDelegate;

    @Inject
    private SystemParameterProvider systemParameterProvider;

    @Inject
    private CacheRegistry cacheRegistry;

    @Inject
    private MappingDelegate mappingDelegate;

    @RequestMapping(value = "/listAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<List<VersionHierarchyInfo>> getAllVersions() {
        RestResponse<List<VersionHierarchyInfo>> response = new RestResponse<>();
        List<VersionHierarchyInfo> hierarchyInfos = null;
        try {
            hierarchyInfos = versionDelegate.getAllVersions();
            response.setResponse(hierarchyInfos);
            response.setError(false);
            response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setError(true);
            response.setErrorCode(e.getCode());
            response.setMessage(e.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(value = "/listAllLibraryNames", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<List<String>> getAllLibraryNames() {
        RestResponse<List<String>> response = new RestResponse<>();
        List<String> allLibraryNames = new ArrayList<>();
        try {
            allLibraryNames = versionDelegate.getAllLibraryNames();
            response.setResponse(allLibraryNames);
            response.setError(false);
            response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setError(true);
            response.setErrorCode(e.getCode());
            response.setMessage(e.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(value = "/listAllLibraryRecords/{libraryName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<List<String>> getAllLibraryRecords(@PathVariable(LIBRARY_NAME) String libraryName) {
        RestResponse<List<String>> response = new RestResponse<>();
        List<String> allLibraryRecords = new ArrayList<>();
        try {
            if (isNotEmpty(libraryName)) {
                allLibraryRecords = versionDelegate.getAllLibraryRecords(libraryName);
            }
            response.setResponse(allLibraryRecords);
            response.setError(false);
            response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setError(true);
            response.setErrorCode(e.getCode());
            response.setMessage(e.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(value = "/listAllLibraryRecNameDescs/{libraryName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<List<MappingInfo>> getlistAllLibraryRecNameDescs(@PathVariable(LIBRARY_NAME) String libraryName) {
        RestResponse<List<MappingInfo>> response = new RestResponse<>();
        List<MappingInfo> allLibraryRecords = new ArrayList<>();
        try {
            if (isNotEmpty(libraryName)) {
                allLibraryRecords = versionDelegate.listAllLibraryRecNameDescs(libraryName);
            }
            response.setResponse(allLibraryRecords);
            response.setError(false);
            response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setError(true);
            response.setErrorCode(e.getCode());
            response.setMessage(e.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(value = "/listAllModelNames", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<List<String>> getAllModelNames() {
        RestResponse<List<String>> response = new RestResponse<>();
        List<String> allModelNames = new ArrayList<>();
        try {
            allModelNames = versionDelegate.getAllModelNames();
            response.setResponse(allModelNames);
            response.setError(false);
            response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setError(true);
            response.setErrorCode(e.getCode());
            response.setMessage(e.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(value = "/listAllTidVersionNames/{modelName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<List<String>> getAllTidVersionNames(@PathVariable("modelName") String modelName) {
        RestResponse<List<String>> response = new RestResponse<>();
        List<String> allTidsForModel = new ArrayList<>();
        try {
            if (isNotEmpty(modelName)) {
                allTidsForModel = versionDelegate.getAllTidVersionNames(modelName);
            }
            response.setResponse(allTidsForModel);
            response.setError(false);
            response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setError(true);
            response.setErrorCode(e.getCode());
            response.setMessage(e.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(value = "/listAllTidVersions/{modelName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<List<MappingInfo>> getTidMappings(@PathVariable("modelName") String modelName) {
        RestResponse<List<MappingInfo>> response = new RestResponse<>();
        List<MappingInfo> allTidsForModel = new ArrayList<>();
        try {
            if (isNotEmpty(modelName)) {
                allTidsForModel = versionDelegate.getTidMappings(modelName);
            }
            response.setResponse(allTidsForModel);
            response.setError(false);
            response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setError(true);
            response.setErrorCode(e.getCode());
            response.setMessage(e.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<VersionInfo> create(@RequestBody CreateVersionInfo input) {
        RestResponse<VersionInfo> response = new RestResponse<VersionInfo>();
        try {
            VersionInfo version = VersionControllerHelper.mapToVersionInfo(input);
            version = versionDelegate.create(version);
            response.setError(false);
            response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
            response.setResponse(version);
        } catch (BusinessException | SystemException se) {
            LOGGER.error(se.getLocalizedMessage(), se);
            response.setError(true);
            response.setErrorCode(se.getCode());
            response.setMessage(se.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<String> update(@RequestBody CreateVersionInfo input) {
        RestResponse<String> response = new RestResponse<String>();
        try {
            VersionInfo version = buildVersionInfo(input);
            versionDelegate.update(version);
            response.setError(false);
            response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
            response.setResponse(null);
        } catch (BusinessException | SystemException se) {
            LOGGER.error(se.getLocalizedMessage(), se);
            response.setError(true);
            response.setErrorCode(se.getCode());
            response.setMessage(se.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(value = "/deleteVersion/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public RestResponse<String> delete(@PathVariable("id") String id) {
        RestResponse<String> response = new RestResponse<String>();
        try {
            versionDelegate.deleteVersion(id);
            response.setError(false);
            response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
            response.setResponse(null);
        } catch (BusinessException | SystemException se) {
            LOGGER.error(se.getLocalizedMessage(), se);
            response.setError(true);
            response.setErrorCode(se.getCode());
            response.setMessage(se.getLocalizedMessage());
        }
        return response;
    }

    private VersionInfo buildVersionInfo(CreateVersionInfo input) {
        VersionInfo version = new VersionInfo();
        version.setId(input.getId());
        version.setVersionDescription(input.getVersionDescription());
        return version;
    }

    @RequestMapping(value = "/getTidMappingStatus", method = RequestMethod.POST)
    @ResponseBody
    public RestResponse<List<String>> getTidMappingStatus(@RequestParam(value = "tidName") String tidName) {
        LOGGER.info("Received request for cheking the status in ver table for Tid with name : ", tidName);
        RestResponse<List<String>> response = new RestResponse<List<String>>();
        try {
            KeyValuePair<Boolean, List<String>> result = versionDelegate.getTidMappingStatus(tidName);
            if (result.getKey()) {
                response.setError(false);
                response.setMessage("Umg Versions retrieved successfully");
                response.setResponse(result.getValue());
            } else {
                response.setError(false);
                response.setMessage("No Umg Versions retrieved");
            }
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setError(true);
            response.setErrorCode(e.getCode());
            response.setMessage(e.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(value = "/getVersionStatus", method = RequestMethod.POST)
    @ResponseBody
    public RestResponse<String> getVersionStatus(@RequestParam(value = TID_NAME) String tidName) {
        LOGGER.info("Received request for cheking the status in ver table for Tid with name : ", tidName);
        RestResponse<String> response = new RestResponse<String>();
        try {
            Boolean result = versionDelegate.getVersionStatus(tidName);
            if (result) {
                response.setError(false);
                response.setMessage("Umg Versions retrieved successfully");
                response.setResponse("umg versions retrieved");
            } else {
                response.setError(false);
                response.setMessage("No Umg Versions retrieved");
            }
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setError(true);
            response.setErrorCode(e.getCode());
            response.setMessage(e.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(value = "/listVersionedLibraries", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<PageRecord<String>> getPagedModelLIbraries(@RequestBody VersionInfo pageInfo) {
        RestResponse<PageRecord<String>> response = new RestResponse<PageRecord<String>>();
        try {
            PageRecord<String> pageRecord = versionDelegate.getAllLibraries(pageInfo);
            response.setResponse(pageRecord);
        } catch (BusinessException | SystemException e) {
            response.setError(true);
            response.setErrorCode(e.getCode());
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @RequestMapping(value = "/listVersionedModels/{libraryName}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<PageRecord<String>> getModelsForLibraries(@PathVariable(value = LIBRARY_NAME) String libraryName,
            @RequestBody VersionInfo pageInfo) {
        RestResponse<PageRecord<String>> response = new RestResponse<PageRecord<String>>();
        try {
            PageRecord<String> pageRecord = versionDelegate.getAllModelsForLibrary(libraryName, pageInfo);
            response.setResponse(pageRecord);
        } catch (BusinessException | SystemException e) {
            response.setErrorCode(e.getCode());
            response.setError(true);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @RequestMapping(value = "/listAllVersions/{libraryName}/{modelName}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<PageRecord<VersionInfo>> getAllVersion(@PathVariable(value = LIBRARY_NAME) String libraryName,
            @PathVariable(value = "modelName") String modelName, @RequestBody VersionInfo pageInfo) {
        RestResponse<PageRecord<VersionInfo>> response = new RestResponse<PageRecord<VersionInfo>>();
        try {
            PageRecord<VersionInfo> pageRecord = versionDelegate.getAllVersions(libraryName, modelName, pageInfo);
            response.setResponse(pageRecord);
        } catch (BusinessException | SystemException e) {
            response.setErrorCode(e.getCode());
            response.setError(true);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @RequestMapping(value = "/getNotDeletedVersions", method = RequestMethod.POST)
    @ResponseBody
    public RestResponse<List<String>> getNotDeletedVersions(@RequestParam(value = "tidName") String tidName) {
        LOGGER.info("Received request for cheking the status in ver table for Tid with name : ", tidName);
        RestResponse<List<String>> response = new RestResponse<List<String>>();
        try {
            List<String> result = versionDelegate.getNotDeletedVersions(tidName);
            if (result != null) {
                response.setError(false);
                response.setMessage("Umg Versions retrieved successfully");
                response.setResponse(result);
            } else {
                response.setError(false);
                response.setMessage("No Umg Versions retrieved");
            }
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setError(true);
            response.setErrorCode(e.getCode());
            response.setMessage(e.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(value = "/getVersionsForModelLibrary/{id}")
    @ResponseBody
    public RestResponse<List<String>> getVersionsForModelLibrary(@PathVariable("id") String id) {
        LOGGER.info("Entered getVersionsForModelLibrary method");
        RestResponse<List<String>> response = new RestResponse<List<String>>();
        List<String> modelLibVersionMap = null;
        try {
            modelLibVersionMap = versionDelegate.getUmgVersionsOnModelLibraryId(id);
            response.setError(false);
            response.setMessage("Versions for Model Libraries Fetched");
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setErrorCode(e.getCode());
            response.setError(true);
            response.setMessage(e.getLocalizedMessage());
        }
        response.setResponse(modelLibVersionMap);
        return response;
    }

    @RequestMapping(value = "/publishVersion/{id}")
    @ResponseBody
    public RestResponse<String> publishVersion(@PathVariable("id") String id) {
        LOGGER.info("Entered publishVersion method");
        RestResponse<String> response = new RestResponse<String>();

        try {
            if (isAtleastAReportGenerated(id)) {
                String user = SecurityContextHolder.getContext().getAuthentication().getName();
                String tenantUrl = VersionControllerHelper.getTenantBaseUrl(tenantDelegate);
                String authToken = VersionControllerHelper.getTenantAuthToken(cacheRegistry);
                VersionInfo versionInfo = versionDelegate.publishVersion(id, user, tenantUrl, authToken,
                        PORTAL_APPROVAL.getValue());
                if (versionInfo != null) {
                    response.setResponse(versionInfo.getStatus());
                    response.setMessage("Version published successfully.");
                }

                response.setError(false);

                final Map<String, String> versionInfoMap = new HashMap<>();
                versionInfoMap.put(MODEL_NAME.getHeaderName(), versionInfo.getName());
                versionInfoMap.put(MINOR_VERSION.getHeaderName(), String.valueOf(versionInfo.getMinorVersion()));
                versionInfoMap.put(MAJOR_VERSION.getHeaderName(), String.valueOf(versionInfo.getMajorVersion()));
                versionInfoMap.put(TRANSACTION_ID.getHeaderName(), versionInfo.getUmgTransactionId());

                versionInfoMap.put(PUBLISHER_NAME.getHeaderName(),
                        SecurityContextHolder.getContext().getAuthentication().getName());

                notificationDelegate.notifyModelPublishSuccess(versionInfoMap, false);
            } else {
                LOGGER.error("Report is not generated, hence Publish porcess is not procedded");
                response.setError(true);
                response.setMessage("Unable to publish model. Test the report from test bed before publishing the model");
            }
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setErrorCode(e.getCode());
            response.setError(true);
            response.setMessage(e.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(value = "/deactivateVersion/{id}")
    @ResponseBody
    public RestResponse<String> deactivateVersion(@PathVariable("id") String id) {
        LOGGER.info("Entered deactivateVersion method");
        RestResponse<String> response = new RestResponse<String>();
        try {
            String user = SecurityContextHolder.getContext().getAuthentication().getName();
            String tenantUrl = VersionControllerHelper.getTenantBaseUrl(tenantDelegate);
            VersionInfo versionInfo = versionDelegate.deactivateVersion(id, user, tenantUrl,
                    VersionControllerHelper.getTenantAuthToken(cacheRegistry));
            if (versionInfo != null) {
                response.setResponse(versionInfo.getStatus());
            }
            response.setMessage("Version deactivated successfully.");
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setErrorCode(e.getCode());
            response.setError(true);
            response.setMessage(e.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(value = "/listAllTenantModelNames", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<List<String>> getTenantModelNames() {
        RestResponse<List<String>> response = new RestResponse<>();
        List<String> allTenantModelNames = new ArrayList<>();
        try {
            allTenantModelNames = versionDelegate.getAllTenantModelNames();
            response.setResponse(allTenantModelNames);
            response.setError(false);
            response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setError(true);
            response.setErrorCode(e.getCode());
            response.setMessage(e.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(value = "/getEnvironments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<Map<String, List<String>>> getEnvironments() {
        RestResponse<Map<String, List<String>>> response = new RestResponse<>();
        Map<String, List<String>> environments = new HashMap();
        try {
            environments = versionDelegate.getEnvironments();
            response.setResponse(environments);
            response.setError(false);
            response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
        } catch (SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setError(true);
            response.setErrorCode(e.getCode());
            response.setMessage(e.getLocalizedMessage());
        }
        return response;
    }
    
    @RequestMapping(value = "/getWebsocketURL", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<String> getWebsocketURL() {
    	RestResponse<String> response = new RestResponse<>();
    	String websocketBaseURL = null;
    	websocketBaseURL = (String) cacheRegistry.getMap(SystemParameterProvider.SYSTEM_PARAMETER)
    			.get(SystemParameterConstants.MODEL_PUBLISH_STATUS_UPDATE_URL);
    	response.setResponse(websocketBaseURL);
    	response.setError(false);
    	response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
    	return response;
    }

    @RequestMapping(value = "/listAllTenantModelNamesByEnv", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<List<String>> getTenantModelNamesByEnv(@RequestParam(value = "execLangauge") String execLangauge) {
        RestResponse<List<String>> response = new RestResponse<>();
        List<String> allTenantModelNames = new ArrayList<>();
        try {
            allTenantModelNames = versionDelegate.getAllModelsbyEnvironment(execLangauge);
            response.setResponse(allTenantModelNames);
            response.setError(false);
            response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
        } catch (SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setError(true);
            response.setErrorCode(e.getCode());
            response.setMessage(e.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(value = "/versionSummary/{tenantModelName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<VersionSummaryInfo> getVersionSummary(@PathVariable("tenantModelName") String tenantModelName) {
        RestResponse<VersionSummaryInfo> response = new RestResponse<>();
        try {
            response.setResponse(versionDelegate.getVersionSummary(tenantModelName));
            response.setError(false);
            response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setError(true);
            response.setErrorCode(e.getCode());
            response.setMessage(e.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(value = "/listAllModelNames/{libraryName}/{isDescending}/{searchString}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<List<String>> getModelNamesForLibraryNameAndCharsInNameOrDescription(
            @PathVariable(value = LIBRARY_NAME) String libraryName, @PathVariable(value = "isDescending") boolean isDescending,
            @PathVariable(value = "searchString") String searchString) {
        RestResponse<List<String>> response = new RestResponse<List<String>>();
        try {
            response.setResponse(versionDelegate.getModelNamesForLibraryNameAndCharsInNameOrDescription(libraryName, searchString,
                    isDescending));
            response.setError(false);
            response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setError(true);
            response.setErrorCode(e.getCode());
            response.setMessage(e.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(value = "/listAllModelNames/{libraryName}/{isDescending}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<List<String>> getModelNamesForLibraryNameAndCharsInNameOrDescription(
            @PathVariable(value = LIBRARY_NAME) String libraryName, @PathVariable(value = "isDescending") boolean isDescending) {
        RestResponse<List<String>> response = new RestResponse<List<String>>();
        try {
            response.setResponse(
                    versionDelegate.getModelNamesForLibraryNameAndCharsInNameOrDescription(libraryName, "", isDescending));
            response.setError(false);
            response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setError(true);
            response.setErrorCode(e.getCode());
            response.setMessage(e.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(value = "/getVersionDetails/{tenantModelName}/{majorVersion}/{minorVersion}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<VersionAPIInfo> getVersionDetails(@PathVariable(value = "tenantModelName") String name,
            @PathVariable(value = "majorVersion") Integer majorVersion,
            @PathVariable(value = "minorVersion") Integer minorVersion) {
        RestResponse<VersionAPIInfo> response = new RestResponse<VersionAPIInfo>();
        try {
            VersionAPIInfo versionAPIInfo = versionTestDelegate.getVersionDetails(name, majorVersion, minorVersion);
            if (versionAPIInfo != null) {
                response.setResponse(versionAPIInfo);
            } else {
                response.setMessage(
                        String.format("Version details not found for model name %s , major version %d and minor version %d.",
                                name, majorVersion, minorVersion));
            }
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setError(true);
            response.setErrorCode(e.getCode());
            response.setMessage(e.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(value = "/getVersionDetails/{tenantModelName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<List<VersionAPIInfo>> getVersionDetails(@PathVariable(value = "tenantModelName") String name) {
        RestResponse<List<VersionAPIInfo>> response = new RestResponse<List<VersionAPIInfo>>();
        try {
            List<VersionAPIInfo> versionAPIInfos = versionTestDelegate.getVersionDetails(name);
            if (CollectionUtils.isNotEmpty(versionAPIInfos)) {
                response.setResponse(versionAPIInfos);
            } else {
                response.setMessage(String.format("Version details not found for model name %s.", name));
            }
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setError(true);
            response.setErrorCode(e.getCode());
            response.setMessage(e.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(value = "/getVersionDetails", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<List<VersionAPIInfo>> getVersionDetails() {
        RestResponse<List<VersionAPIInfo>> response = new RestResponse<List<VersionAPIInfo>>();
        try {
            List<VersionAPIInfo> versionAPIInfos = versionTestDelegate.getAllVersionDetails();
            if (CollectionUtils.isNotEmpty(versionAPIInfos)) {
                response.setResponse(versionAPIInfos);
            } else {
                response.setMessage("No versions are present.");
            }
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setError(true);
            response.setErrorCode(e.getCode());
            response.setMessage(e.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(value = "downloadExcel/{tidName}/{versionName}/{version}", method = RequestMethod.GET)
    @ResponseBody
    public void downloadVersionInExcelFile(@PathVariable(value = TID_NAME) String tidName,
            @PathVariable("versionName") String versionName, @PathVariable("version") String version,
            HttpServletResponse response) {
        // String[] versions = version.split(".");

        MappingVersionInfo mvi = new MappingVersionInfo();
        mvi.setTidName(tidName);
        mvi.setVersionName(versionName);

        if (version.indexOf(".") != -1) {
            String[] versions = version.split(".");
            mvi.setMajorVersion(versions[0]);
            mvi.setMinorVersion(versions[1]);
        } else {
            mvi.setMajorVersion(version);
            mvi.setMinorVersion("0");
        }

        try {
            Workbook book = versionDelegate.exportExcel(mvi);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            book.write(bos);

            String fileName = tidName + "_" + versionName + "_" + mvi.getMajorVersion() + "_" + mvi.getMinorVersion() + ".xls";
            setResponseHeader(response, fileName);

            response.getOutputStream().write(bos.toByteArray());
            response.getOutputStream().flush();
            response.getOutputStream().close();
            bos.close();
        } catch (BusinessException | SystemException | IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void setResponseHeader(HttpServletResponse response, String fileName) {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
    }

    /**
     * This method will retrieve all major version names with search criteria and return paginated data back with page info
     */
    @RequestMapping(value = "/listAllVersionName", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<ResponseWrapper<List<String>>> findAllVersionName(@RequestBody SearchOptions searchOptions) {
        RestResponse<ResponseWrapper<List<String>>> response = new RestResponse<ResponseWrapper<List<String>>>();
        try {
            response.setResponse(versionDelegate.findAllVersionName(searchOptions));
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

    /**
     * This method will retrieve all major&minor version and return paginated data back with page info
     */
    @RequestMapping(value = "/findAllVersion/{versionName:.+}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<PageRecord<VersionInfo>> findAllversionByVersionName(
            @PathVariable(value = "versionName") String versionName, @RequestBody SearchOptions searchOptions) {
        RestResponse<PageRecord<VersionInfo>> response = new RestResponse<PageRecord<VersionInfo>>();
        try {
            PageRecord<VersionInfo> pageRecord = versionDelegate.findAllversionByVersionName(versionName, searchOptions);
            response.setResponse(pageRecord);
        } catch (BusinessException | SystemException e) {
            LOGGER.error("findAllversionByVersionName : " + e.getLocalizedMessage(), e);
            response.setErrorCode(e.getCode());
            response.setError(true);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @RequestMapping(value = "/versionMetrics", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<Map<String, Object>> getVersionMetrics(@RequestBody VersionMetricRequestInfo versionReq) {
        RestResponse<Map<String, Object>> response = new RestResponse<Map<String, Object>>();
        try {
            Map<String, Object> metricsMap = versionDelegate.getVersionMetrics(versionReq);
            response.setResponse(metricsMap);
        } catch (BusinessException | SystemException e) {
            response.setError(true);
            response.setErrorCode(e.getCode());
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @RequestMapping(value = "/validateVersionName/{versionName}", method = RequestMethod.GET)
    @ResponseBody
    public RestResponse<Long> validateModelName(@PathVariable(value = "versionName") String versionName) {
        RestResponse<Long> response = new RestResponse<Long>();
        try {
            response.setResponse(versionDelegate.getVersionCountByName(versionName));
            response.setMessage("Version " + versionName + " already exist in the system.");
        } catch (SystemException e) {
            response.setError(true);
            response.setErrorCode(e.getCode());
            response.setMessage(e.getMessage());
        }
        return response;
    }

    /**
     * This method will retrieve all major&minor version and return paginated data back with page info
     */
    @RequestMapping(value = "/findAllVersions/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<Map<String, Object>> findAllVersions(@RequestBody SearchOptions searchOptions) {
        RestResponse<Map<String, Object>> response = new RestResponse<Map<String, Object>>();
        try {
            List<VersionInfo> pageRecord = versionDelegate.findAllVersions(searchOptions);
            Map<String, Object> responseObj = new HashMap<>();
            responseObj.put(VERSION_LIST, pageRecord);
            responseObj.put(EMAIL_NOTIFCN_ENABLED, isEmailNotificationEnabled());
            responseObj.put(MODEL_PUBLSH_APPROVAL, isModelPublishApproval());
            response.setResponse(responseObj);
        } catch (BusinessException | SystemException e) {
            LOGGER.error("findAllVersions : " + e.getLocalizedMessage(), e);
            response.setErrorCode(e.getCode());
            response.setError(true);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    private Boolean isEmailNotificationEnabled() throws SystemException, BusinessException {
        Boolean emailNotificationEnabled = Boolean.FALSE;
        TenantInfo tenantInfo = new TenantInfo();
        tenantInfo.setCode(RequestContext.getRequestContext().getTenantCode());
        emailNotificationEnabled = TenantUtil.isTenantConfigEnabled(tenantInfo, NotificationConstants.EMAIL_NOTIFICATIONS_ENABLED,
                cacheRegistry);
        return emailNotificationEnabled;
    }

    private Boolean isModelPublishApproval() throws BusinessException, SystemException {
        Boolean modelPublishApproval = Boolean.FALSE;
        String eventId = mappingNotificationDelegate.getNotificationEventId(NotificationConstants.NOTIFICATION_EVENT_TABLENAME,
                MODEL_PUBLISH_APPROVAL.getName());
        modelPublishApproval = mappingNotificationDelegate.isDuplicateMapping(RequestContext.getRequestContext().getTenantCode(),
                eventId);
        return modelPublishApproval;
    }

    @RequestMapping(value = "/sendModelApprovalEmail/{id}")
    @ResponseBody
    public RestResponse<String> sendModelApprovalEmail(@PathVariable("id") String id) {
        LOGGER.info("Entered sendModelApprovalEmail method");
        final RestResponse<String> response = new RestResponse<String>();

        boolean isReportNotGenerated = false;
        boolean isModelPublished = false;
        final String user = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean mailFailed = false;
        try {
            isReportNotGenerated = isAtleastAReportGenerated(id);
            isModelPublished = versionDelegate.isVersionPublished(id);

            if (isReportNotGenerated && !(isModelPublished)) {
                // final String tenantUrl = VersionControllerHelper.getTenantBaseUrl(tenantDelegate);
                // final String authToken = VersionControllerHelper.getTenantAuthToken(tenantDelegate);

                final VersionInfo versionInfo = versionDelegate.updateModelApprovalStatus(id, user, PENDING_APPROVAL);
                if (versionInfo != null) {
                    response.setResponse(versionInfo.getStatus());
                    response.setMessage("Approval email sent successfully.");
                }
                response.setError(false);

                mailFailed = true;
                sendModelApprovalMail(versionInfo, user);
                mailFailed = false;
            } else {
                response.setError(true);

                if (isReportNotGenerated) {
                    LOGGER.error("Report is not generated, hence sending approval mail porcess is not procedded");
                    response.setMessage(
                            "Unable to send approval mail. Test the report from test bed before publishing the model");
                }

                if (isModelPublished) {
                    LOGGER.error("Model is already published, hence sending approval mail porcess is not procedded");
                    response.setMessage("Unable to send approval mail because Model was already published");
                }
            }
        } catch (BusinessException | SystemException e) {
            try {
                versionDelegate.updateModelApprovalStatus(id, user, VersionStatus.TESTED);
            } catch (BusinessException | SystemException ee) {
                LOGGER.error(e.getLocalizedMessage(), ee);
                response.setErrorCode(ee.getCode());
                response.setError(true);
                response.setMessage(e.getLocalizedMessage());
            }

            LOGGER.error(e.getLocalizedMessage(), e);
            response.setErrorCode(e.getCode());
            response.setError(true);
            response.setMessage(e.getLocalizedMessage());

            if (mailFailed) {
                response.setMessage(VersionDelegate.SENIND_EMAIL_APPROVAL_FAILED);
            }
        }
        return response;
    }

    private boolean isAtleastAReportGenerated(final String versionId) throws BusinessException, SystemException {
        boolean flag = true;
        LOGGER.info(
                "Validating whether version has a report template or not, if it has, checking whether a succesful report is generated or not");

        try {
            final ModelReportTemplateDefinition reportTemplate = modelDelegate.getModelReportTemplate(versionId);
            if (reportTemplate != null) {
                final ModelReportStatusInfo reoprtStatusInfo = ModelReportStatusInfo.buildMRStatusInfo(reportTemplate);
                flag = reportService.isReportGeneratedForModel(reoprtStatusInfo);
            }
        } catch (BusinessException | SystemException e) {
            if (!ReportExceptionCodes.isReportTemplateNotAvlbCode(e.getCode())) {
                throw e;
            }
        }

        return flag;
    }

    private void sendModelApprovalMail(final VersionInfo versionInfo, final String user)
            throws SystemException, BusinessException {
        final Map<String, String> versionInfoMap = new HashMap<>();

        versionInfoMap.put(MODEL_NAME.getHeaderName(), versionInfo.getName());
        versionInfoMap.put(UMG_ADMIN_URL.getHeaderName(),
                systemParameterProvider.getParameter(NotificationConstants.UMG_ADMIN_URL));
        versionInfoMap.put(MAJOR_VERSION.getHeaderName(), String.valueOf(versionInfo.getMajorVersion()));
        versionInfoMap.put(MINOR_VERSION.getHeaderName(), String.valueOf(versionInfo.getMinorVersion()));
        versionInfoMap.put(VERSION_ID.getHeaderName(), versionInfo.getId());
        versionInfoMap.put(PUBLISHER_NAME.getHeaderName(), user);

        final List<NotificationAttachment> attachments = new ArrayList<NotificationAttachment>();
        NotificationAttachment attNotificationAttachment = new NotificationAttachment();

        final List<ModelArtifact> modelArtifacts = modelDelegate.getModelArtifacts(versionInfo.getMapping().getModel().getId());

        final ModelArtifact modelReleaseNotes = modelArtifacts.get(0);
        StringBuilder dataFileLocation = new StringBuilder(modelReleaseNotes.getAbsolutePath());

        LOGGER.info("Model Release notes absolute path : {}", modelReleaseNotes.getAbsolutePath());
        final File file = new File(dataFileLocation.append(modelReleaseNotes.getName()).toString());
        LOGGER.info("Model Release notes file name : {}", modelReleaseNotes.getName());
        LOGGER.info("Model Release notes complete file path : {}", dataFileLocation.toString());

        attNotificationAttachment.setFile(file);
        attNotificationAttachment.setFileName(modelReleaseNotes.getName());
        attNotificationAttachment.setDeleteFileAfterSend(false);
        attachments.add(attNotificationAttachment);

        notificationDelegate.sendModelApprovalEmail(versionInfoMap, attachments, false);
    }

    @RequestMapping(value = "/getTenantIODefinition/{mName}/{majorVersion}/{minorVersion}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<TenantIODefinition> getTenantIODefinitions(@PathVariable(value = "mName") String name,
            @PathVariable(value = "majorVersion") Integer majorVersion,
            @PathVariable(value = "minorVersion") Integer minorVersion) {
        RestResponse<TenantIODefinition> response = new RestResponse<TenantIODefinition>();
        VersionInfo versionInfo;
        try {
            versionInfo = versionDelegate.getVersionDetails(name, majorVersion, minorVersion);
            if (versionInfo != null) {
                String tidName = versionInfo.getMapping().getName();
                TenantIODefinition ioDefinitions = mappingDelegate.getTIDParams(tidName);
                response.setResponse(ioDefinitions);
            }
        } catch (BusinessException | SystemException e) {
            LOGGER.error("An error occurred while fetching IO definitions");
            response.setErrorCode(e.getCode());
            response.setError(true);
            response.setMessage(e.getLocalizedMessage());
        }

        return response;
    }
}
