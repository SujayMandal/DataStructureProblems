package com.ca.umg.sdc.rest.controller;

import static com.ca.umg.report.util.ReportUtil.getFileNameWithoutExt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
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

import com.ca.framework.core.bo.ModelType;
import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.util.ConversionUtil;
import com.ca.umg.business.common.info.SearchOptions;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.execution.delegate.ModelExecutionEnvironmentDelegate;
import com.ca.umg.business.model.delegate.MediateModelLibraryDelegate;
import com.ca.umg.business.model.delegate.ModelDelegate;
import com.ca.umg.business.model.info.MediateModelLibraryInfo;
import com.ca.umg.business.model.info.ModelArtifact;
import com.ca.umg.business.model.info.ModelDefinitionInfo;
import com.ca.umg.business.model.info.ModelExecutionEnvironmentInfo;
import com.ca.umg.business.model.info.ModelInfo;
import com.ca.umg.business.model.info.ModelLibraryInfo;
import com.ca.umg.business.transaction.delegate.TransactionDelegate;
import com.ca.umg.business.transaction.mongo.entity.TransactionDocument;
import com.ca.umg.business.util.AdminUtil;
import com.ca.umg.business.version.command.error.Error;
import com.ca.umg.business.version.command.info.CommandReportInfo;
import com.ca.umg.business.version.command.master.CommandMaster;
import com.ca.umg.business.version.delegate.VersionDelegate;
import com.ca.umg.business.version.info.VersionInfo;
import com.ca.umg.report.ReportExceptionCodes;
import com.ca.umg.report.model.ModelReportTemplateDefinition;
import com.ca.umg.report.model.ModelReportTemplateInfo;
import com.ca.umg.report.model.ReportEngineNames;
import com.ca.umg.report.model.ReportTemplateStatus;
import com.ca.umg.report.model.ReportTypes;
import com.ca.umg.sdc.rest.constants.RestConstants;
import com.ca.umg.sdc.rest.exception.RESTExceptionCodes;
import com.ca.umg.sdc.rest.utils.RestResponse;

/**
 * new controller for streamline version publishing
 * 
 * @author raddibas
 *
 */
@SuppressWarnings("PMD")
@Controller
@RequestMapping("/publishVersion")
public class VersionPublishController {

    @Inject
    private VersionDelegate versionDelegate;

    @Inject
    @Named("versionCommandMaster")
    private CommandMaster commandMaster;

    @Inject
    private TransactionDelegate transactionDelegate;

    @Inject
    private ModelExecutionEnvironmentDelegate modelExecEnvironmentDelegate;

    @Inject
    private MediateModelLibraryDelegate mediateModelLibraryDelegate;
    
    @Inject
    private ModelDelegate modelDelegate;

    private static final Logger LOGGER = LoggerFactory.getLogger(VersionPublishController.class);
    private static final String NAME = "name";
    private static final String EXECUTION_LANGUAGE = "executionLanguage";
    private static final String MODEL_TYPE = "modelType";
    private static final String RMV000702 = "RMV000702"; 

    @RequestMapping(value = "/listVersionNames", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<List<String>> getVersionNames() {
        RestResponse<List<String>> response = new RestResponse<>();
        List<String> versionNames = null;
        try {
            versionNames = versionDelegate.getAllVersionNames();
            response.setResponse(versionNames);
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

    @RequestMapping(value = "/getVersionDescription/{name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<String> getVersionDescription(@PathVariable(NAME) String versionName) {
        RestResponse<String> response = new RestResponse<>();
        String versionDescription = null;
        try {
            versionDescription = versionDelegate.getVersionDescription(versionName);
            response.setResponse(versionDescription);
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

    @RequestMapping(value = "/getMajorVersions/{name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<List<Integer>> getMajorVersions(@PathVariable(NAME) String versionName) {
        RestResponse<List<Integer>> response = new RestResponse<>();
        List<Integer> majorVersions = null;
        try {
            majorVersions = versionDelegate.getMajorVersions(versionName);
            response.setResponse(majorVersions);
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

    @RequestMapping(value = "/listLibraryDetails/{executionLanguage:.+}/{modelType}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<List<VersionInfo>> getExistingRecords(@PathVariable(EXECUTION_LANGUAGE) String executionLanguage, @PathVariable(MODEL_TYPE) String modelType) {
        RestResponse<List<VersionInfo>> response = new RestResponse<>();
        List<VersionInfo> libraryDetails = new ArrayList<>();
        try {
            libraryDetails = versionDelegate.getVersionDetails(executionLanguage, ModelType.getModelType(modelType),Boolean.FALSE);
            response.setResponse(libraryDetails);
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

    @RequestMapping(value = "/searchLibrary/{executionLanguage:.+}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<List<VersionInfo>> searchLibrary(@RequestBody SearchOptions searchOptions,
            @PathVariable(EXECUTION_LANGUAGE) String executionLanguage) {
        RestResponse<List<VersionInfo>> response = new RestResponse<>();
        List<VersionInfo> versionInfos = null;
        try {
            versionInfos = versionDelegate
                    .searchLibrary(searchOptions, executionLanguage.split(BusinessConstants.CHAR_HYPHEN)[0]);
            response.setResponse(versionInfos);
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

    /**
     * get the version object for the given id
     * 
     * @param versionId
     * @return
     */
    @RequestMapping(value = "/getModelApi/{versionId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<VersionInfo> getVersion(@PathVariable("versionId") String versionId) {
        RestResponse<VersionInfo> response = new RestResponse<>();
        VersionInfo versionInfos = null;
        try {
            versionInfos = versionDelegate.getVersionById(versionId);
            
            
            ModelReportTemplateDefinition reportTemplate = null;
            
            try {
            	reportTemplate = modelDelegate.getModelReportTemplate(versionId);
            } catch (BusinessException | SystemException e) {
            	if (!ReportExceptionCodes.isReportTemplateNotAvlbCode(e.getCode())) {
            		throw e;
            	}
    		}
            	
    		if (reportTemplate != null) {
    			versionInfos.setHasReportTemplate(Boolean.TRUE);
    			versionInfos.setReportTemplateName(reportTemplate.getTemplateFileName());
    		} else {
    			versionInfos.setHasReportTemplate(Boolean.FALSE);
    			versionInfos.setReportTemplateName(null);
    		}
    		
    		versionInfos.setJarName(versionInfos.getModelLibrary().getJarName());
    		versionInfos.setManifestName(versionInfos.getModelLibrary().getRmanifestFileName());
    		
            response.setResponse(versionInfos);
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

    @RequestMapping(value = "/searchIoDefn/{executionLanguage:.+}/{modelType}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<List<VersionInfo>> searchIoDefn(@RequestBody SearchOptions searchOptions,
            @PathVariable(EXECUTION_LANGUAGE) String executionLanguage, @PathVariable(MODEL_TYPE) String modelType) {
        RestResponse<List<VersionInfo>> response = new RestResponse<>();
        
        final ModelType mt = ModelType.getModelType(modelType);
        
        List<VersionInfo> versionInfos = null;
        try {
            versionInfos = versionDelegate
                    .searchIoDefns(searchOptions, executionLanguage.split(BusinessConstants.CHAR_HYPHEN)[0], mt);
            response.setResponse(versionInfos);
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

    @RequestMapping(value = "/saveAllVersionData", method = RequestMethod.POST)
    @ResponseBody
    public RestResponse<CommandReportInfo> saveAllVersionData(@RequestParam("versionInfo") String verInfoObj,
            @RequestParam(value = "excel", required = false) MultipartFile excelFile,
            @RequestParam(value = "jar", required = false) MultipartFile jarFile,
            @RequestParam(value = "documentation", required = false) MultipartFile documentationFile,
            @RequestParam(value = "testFile", required = false) MultipartFile testFile,
            @RequestParam(value = "manifestFile", required = false) MultipartFile manifestFile,
            @RequestParam(value = "reportTemplate", required = false) MultipartFile reportTemplate) {
        RestResponse<CommandReportInfo> response = new RestResponse<>();
        VersionInfo verInfo = null;
        try {
            verInfo = ConversionUtil.convertJson(verInfoObj, VersionInfo.class);
            buildVersionInfo(verInfo, jarFile, excelFile, documentationFile, testFile, manifestFile, reportTemplate);
            CommandReportInfo commandReportInfo = commandMaster.createVersion(verInfo);
            response.setResponse(commandReportInfo);
            if (commandReportInfo.isSuccess()) {
                response.setMessage("Successfully created version");
            } else {
                if (verInfo.getUmgTransactionId() != null) {
                    TransactionDocument txnDoc = transactionDelegate.getTxnDocument(verInfo.getUmgTransactionId());                    
                    Map<String, Object> modelExceptions = txnDoc.getModelOutput();
                    if (txnDoc.getErrorCode()!=null && RMV000702.equals(txnDoc.getErrorCode())) {
                    	 modelExceptions = txnDoc.getTenantOutput();
                    	 modelExceptions.put("errorDescription", txnDoc.getErrorDescription());
                    }else if(modelExceptions == null){
                    	 modelExceptions = txnDoc.getModelOutput();            	
                    }
                    commandReportInfo.setModelExceptions(modelExceptions);
                }
                response.setMessage("Version creation has some errors");
            }
        } catch (SystemException | BusinessException e) {
            CommandReportInfo reportInfo = new CommandReportInfo();
            reportInfo.setSuccess(Boolean.FALSE);
            LOGGER.error(e.getLocalizedMessage(), e);
            List<Error> errors = new ArrayList<Error>();
            errors.add(new Error(e.getLocalizedMessage(), "globalError", e.getCode()));
            reportInfo.setErrors(errors);
            response.setResponse(reportInfo);

        }
        return response;
    }

    private VersionInfo buildVersionInfo(VersionInfo verInfoObj, MultipartFile jarFile, MultipartFile excelFile,
            MultipartFile documentationFile, MultipartFile testFile, MultipartFile manifestFile, final MultipartFile reportTemplateFile) throws SystemException,
            BusinessException {
        ModelArtifact modelArtifact = null;
        ModelInfo modelInfo = null;
        try {

            // populate version description
            if (StringUtils.isBlank(verInfoObj.getVersionDescription())) {
                verInfoObj.setVersionDescription(verInfoObj.getMapping().getModel().getDescription() + " Initial Version - 1.0.");
            }
            // setting values for model library
            ModelLibraryInfo modelLibraryInfo = verInfoObj.getModelLibrary();
            if (jarFile != null) {
                modelArtifact = buildArtifacts(jarFile, jarFile.getName());
                modelLibraryInfo.setJar(modelArtifact);
                if (modelLibraryInfo.getExecutionLanguage().contains(BusinessConstants.HYPHEN)) {
                modelLibraryInfo.setExecutionLanguage(modelLibraryInfo.getExecutionLanguage().substring(BusinessConstants.NUMBER_ZERO, 
                		modelLibraryInfo.getExecutionLanguage().indexOf(BusinessConstants.HYPHEN)));
                } else {
                    modelLibraryInfo.setExecutionLanguage(modelLibraryInfo.getExecutionLanguage());                	
                }
                if(jarFile.getOriginalFilename().endsWith("tar.gz") || jarFile.getOriginalFilename().endsWith("jar")){
                	modelLibraryInfo.setExecEnv(SystemConstants.LINUX_OS);                	
                }else if(jarFile.getOriginalFilename().endsWith("zip") || jarFile.getOriginalFilename().endsWith("xlsx") || jarFile.getOriginalFilename().endsWith("xlsm") || jarFile.getOriginalFilename().endsWith("xls")){
                	modelLibraryInfo.setExecEnv(SystemConstants.WINDOWS_OS);             	
                }
                modelLibraryInfo.setJarName(modelArtifact.getName());
            } else {
            	if (modelLibraryInfo.getExecutionLanguage().contains(BusinessConstants.HYPHEN)) {
                modelLibraryInfo.setExecutionLanguage(modelLibraryInfo.getExecutionLanguage().substring(BusinessConstants.NUMBER_ZERO, 
                		modelLibraryInfo.getExecutionLanguage().indexOf(BusinessConstants.HYPHEN)));
            	} else {
            		modelLibraryInfo.setExecutionLanguage(modelLibraryInfo.getExecutionLanguage());
            	}
                verInfoObj.setExistingLibrary(Boolean.TRUE);
            }
            if (manifestFile != null) {
                ModelArtifact manifestModelArtifact = new ModelArtifact();
                manifestModelArtifact.setDataArray(AdminUtil.convertStreamToByteArray(manifestFile.getInputStream()));
                modelLibraryInfo.setManifestFile(manifestModelArtifact);
                manifestModelArtifact.setName(manifestFile.getOriginalFilename());
                modelLibraryInfo.setRmanifestFileName(manifestFile.getOriginalFilename());
            }

            // setting values for model
            if (excelFile != null) {
                modelInfo = verInfoObj.getMapping().getModel();
                if (StringUtils.isBlank(modelInfo.getIoDefinitionName())) {
                    modelArtifact = buildArtifacts(excelFile, modelInfo.getName());
                    if (modelArtifact.getName().endsWith(BusinessConstants.XLSX_EXTENSION)) {
                        modelInfo.setExcel(modelArtifact);
                        modelInfo.setIoDefExcelName(modelArtifact.getName());
                    } else {
                        modelInfo.setXml(modelArtifact);
                        modelInfo.setIoDefinitionName(modelArtifact.getName());
                    }
                }
            }
            if (documentationFile != null && modelInfo != null) {
                modelArtifact = buildArtifacts(documentationFile, modelInfo.getName());
                modelInfo.setDocumentation(modelArtifact);
                modelInfo.setDocumentationName(modelArtifact.getName());
                ModelDefinitionInfo modelDefinitionInfo = new ModelDefinitionInfo();
                modelDefinitionInfo.setType("text/xml");
                modelInfo.setModelDefinition(modelDefinitionInfo);
            }
            // setting value for test excel
            if (testFile != null) {
                verInfoObj.setTestExcel(AdminUtil.convertStreamToByteArray(testFile.getInputStream()));
            }
            
            if (reportTemplateFile != null) {
            	final ModelReportTemplateInfo reportTemplateInfo = new ModelReportTemplateInfo();
            	
            	reportTemplateInfo.setTemplateDefinition(AdminUtil.convertStreamToByteArray(reportTemplateFile.getInputStream()));
            	reportTemplateInfo.setName(getFileNameWithoutExt(reportTemplateFile.getOriginalFilename()));
            	reportTemplateInfo.setReportDescription(reportTemplateFile.getOriginalFilename());
            	reportTemplateInfo.setTemplateFileName(reportTemplateFile.getOriginalFilename());
            	reportTemplateInfo.setIsActive(ReportTemplateStatus.ACTIVE.getStatus());
            	reportTemplateInfo.setReportType(ReportTypes.PDF.getType());
            	reportTemplateInfo.setReportEngine(ReportEngineNames.JASPER_ENGINE.getEngineName());
            	reportTemplateInfo.setReportVersion(1);
            	
            	verInfoObj.setReportTemplateInfo(reportTemplateInfo);
            }
        } catch (IOException e) {
            throw new SystemException(RESTExceptionCodes.RSE0000001, new Object[] { testFile.getName(), e });
        }
        return verInfoObj;
    }

    private ModelArtifact buildArtifacts(MultipartFile multipartFile, String modelName) throws SystemException {
        ModelArtifact modelArtifact = new ModelArtifact();
        try {
            modelArtifact.setContentType(multipartFile.getContentType());
            modelArtifact.setDataArray(AdminUtil.convertStreamToByteArray(multipartFile.getInputStream()));
            modelArtifact.setName(multipartFile.getOriginalFilename());
            modelArtifact.setModelName(modelName);
        } catch (IOException e) {
            throw new SystemException(RESTExceptionCodes.RSE0000001, new Object[] { modelName, e });
        }
        return modelArtifact;
    }

    @RequestMapping(value = "/rModelPackageLibraries/{executionLanguage:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<List<MediateModelLibraryInfo>> getRModelPackages(
            @PathVariable(EXECUTION_LANGUAGE) String executionLanguage) {
        RestResponse<List<MediateModelLibraryInfo>> response = new RestResponse<>();
        List<MediateModelLibraryInfo> mediateModelLibraryInfos = null;
        String[] executionVersion = StringUtils.split(executionLanguage, BusinessConstants.CHAR_HYPHEN);
        if (executionVersion.length == BusinessConstants.NUMBER_TWO) {
            try {
                AdminUtil.setAdminAwareTrue();
                ModelExecutionEnvironmentInfo modelExecutionEnvironment = modelExecEnvironmentDelegate
                        .getModelExecutionEnvironment(executionVersion[0], executionVersion[1]);
                AdminUtil.setAdminAwareFalse();
                if (modelExecutionEnvironment != null) {
                    mediateModelLibraryInfos = mediateModelLibraryDelegate.getAllMediateModelLibraries(modelExecutionEnvironment);
                    response.setResponse(mediateModelLibraryInfos);
                    response.setError(false);
                    response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
                } else {
                    response.setResponse(mediateModelLibraryInfos);
                    response.setError(true);
                    response.setMessage("Environment Doesn't Exist");
                }
            } catch (BusinessException | SystemException e) {
                LOGGER.error(e.getLocalizedMessage(), e);
                response.setError(true);
                response.setErrorCode(e.getCode());
                response.setMessage(e.getLocalizedMessage());
            }
        }
        return response;
    }

    @RequestMapping(value = "/searchNewLibrary/{executionLanguage:.+}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<List<MediateModelLibraryInfo>> searchNewRModelPackages(@RequestBody SearchOptions searchOptions,
            @PathVariable(EXECUTION_LANGUAGE) String executionLanguage) {
        RestResponse<List<MediateModelLibraryInfo>> response = new RestResponse<>();
        List<MediateModelLibraryInfo> mediateModelLibraryInfos = null;
        try {
            AdminUtil.setAdminAwareFalse();
            mediateModelLibraryInfos = versionDelegate.searchNewLibrary(searchOptions, executionLanguage);
            response.setResponse(mediateModelLibraryInfos);
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

    @RequestMapping(value = "/getVersionDetails/{versionName}", method = RequestMethod.GET)
    @ResponseBody
    public RestResponse<VersionInfo> getVersionDetails(@PathVariable(value = "versionName") String versionName) {
        RestResponse<VersionInfo> response = new RestResponse<VersionInfo>();
        try {
            VersionInfo versionInfo = versionDelegate.searchVersionByName(versionName);
            response.setResponse(versionInfo);
        } catch (SystemException e) {
            response.setError(true);
            response.setErrorCode(e.getCode());
            response.setMessage(e.getMessage());
        }
        return response;
    }
    
    @RequestMapping(value = "/listModelReports/{executionLanguage:.+}/{modelType}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<List<VersionInfo>> getExistingModelReports(@PathVariable(EXECUTION_LANGUAGE) String executionLanguage, @PathVariable(MODEL_TYPE) String modelType) {
        RestResponse<List<VersionInfo>> response = new RestResponse<>();
        List<VersionInfo> libraryDetails = new ArrayList<>();
        try {
            libraryDetails = versionDelegate.getVersionDetails(executionLanguage, ModelType.getModelType(modelType),Boolean.TRUE);
            response.setResponse(libraryDetails);
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
    @RequestMapping(value = "/searchReport/{executionLanguage:.+}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<List<VersionInfo>> searchReports(@RequestBody SearchOptions searchOptions,
            @PathVariable(EXECUTION_LANGUAGE) String executionLanguage) {
        RestResponse<List<VersionInfo>> response = new RestResponse<>();
        List<VersionInfo> versionInfos = null;
        try {
            versionInfos = versionDelegate
                    .searchReports(searchOptions, executionLanguage.split(BusinessConstants.CHAR_HYPHEN)[0]);
            response.setResponse(versionInfos);
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
}