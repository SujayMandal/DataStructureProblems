/*
 * SyndicateDataController.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.sdc.rest.controller;

import static com.ca.umg.sdc.rest.utils.CSVUtil.readAllRecords;
import static org.apache.commons.io.FilenameUtils.getExtension;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.util.ConversionUtil;
import com.ca.umg.business.common.info.PageRecord;
import com.ca.umg.business.common.info.SearchOptions;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.syndicatedata.delegate.SyndicateDataDelegate;
import com.ca.umg.business.syndicatedata.info.SyndicateDataColumnInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataContainerInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataVersionInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateFileDataInfo;
import com.ca.umg.sdc.rest.constants.RestConstants;
import com.ca.umg.sdc.rest.utils.CSVUtil;
import com.ca.umg.sdc.rest.utils.RestResponse;

/**
 * 
 * Syndicate Data controller to fetch syndicate data.
 * 
 * @author mandavak
 * 
 */
@Controller
@RequestMapping("/syndicateData")
public class SyndicateDataController {

    private static final String CSV_ATTACHMENT = "attachment;filename=";
	private static final String CONTENT_DISPOSITION = "Content-Disposition";
	private static final String UNABLE_TO_READ_THE_FILE = "Unable to read the file";
	private static final Logger LOGGER = LoggerFactory.getLogger(SyndicateDataController.class);
    @Inject
    private SyndicateDataDelegate syndicateDataDelegate;

    /**
     * GetCSV Data for a given CSV file
     * 
     * @param syndicateDataFile
     * @return
     */
    @RequestMapping(value = "/parseFileData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<SyndicateFileDataInfo> getCSVData(@RequestPart MultipartFile syndicateDataFile) {
        RestResponse<SyndicateFileDataInfo> response = new RestResponse<SyndicateFileDataInfo>();
        SyndicateFileDataInfo syndicateFileDataInfo = null;
        try {
            if (isNotCSVFile(syndicateDataFile.getOriginalFilename())) {
                throw new BusinessException(BusinessExceptionCodes.BSE000026, new String[] { "" });
            }
            syndicateFileDataInfo = buildInfoObject(syndicateDataFile);
            response.setError(false);
            response.setMessage("CSV parsing done");
            response.setResponse(syndicateFileDataInfo);
        } catch (SystemException | BusinessException e) {
            LOGGER.error(UNABLE_TO_READ_THE_FILE, e);
            response.setError(true);
            response.setErrorCode(e.getCode());
            response.setMessage(e.getLocalizedMessage());
        }
        return response;
    }
    
    /**
     * Get container definition from a csv file
     * 
     * @param syndContainerDefFile
     * @return
     */
    @RequestMapping(value = "/parseDefinition", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<SyndicateDataVersionInfo> getContainerDefinition(@RequestParam("syndContainerDefFile") MultipartFile syndContainerDefFile,
    		@RequestParam("syndContainerDataFile") String syndDataColInfoStr) {
        RestResponse<SyndicateDataVersionInfo> response = new RestResponse<SyndicateDataVersionInfo>();
        SyndicateDataVersionInfo syndicateDataVersionInfo = null;
        try {
            if (isNotCSVFile(syndContainerDefFile.getOriginalFilename())) {
                throw new BusinessException(BusinessExceptionCodes.BSE000026, new String[] { "Invalid File Format" });
            }
            List<SyndicateDataColumnInfo> syndDataColInfo = ConversionUtil.convertJson(syndDataColInfoStr, new TypeReference<List<SyndicateDataColumnInfo>>(){});
            syndicateDataVersionInfo = CSVUtil.getContainerDefinition(syndContainerDefFile.getInputStream(), syndDataColInfo);
            response.setError(false);
            response.setMessage("CSV parsing done");
            response.setResponse(syndicateDataVersionInfo);
        } catch (SystemException | BusinessException e) {
            LOGGER.error(UNABLE_TO_READ_THE_FILE, e);
            response.setError(true);
            response.setErrorCode(e.getCode());
            response.setMessage(e.getLocalizedMessage());
        } catch (IOException e) {
            LOGGER.error(UNABLE_TO_READ_THE_FILE, e);
            response.setError(true);
            response.setMessage(e.getLocalizedMessage());
		}
        return response;
    }

    private SyndicateFileDataInfo buildInfoObject(MultipartFile syndicateDataFile) throws SystemException, BusinessException {
        SyndicateFileDataInfo syndicateFileDataInfo = null;
        try {
            syndicateFileDataInfo = readAllRecords(syndicateDataFile.getInputStream());
        } catch (IOException e) {
            LOGGER.error(UNABLE_TO_READ_THE_FILE, e);
            throw new SystemException("", new String[] { "" }, e);
        }
        return syndicateFileDataInfo;
    }

    private boolean isNotCSVFile(String fileName) {
        return !getExtension(fileName).equalsIgnoreCase("csv");
    }

    /**
     * 
     * List all versions for the service (/version/listAll/) and givne container name.
     * 
     * @author mandavak
     * 
     * @param containerName
     * @return
     */
    @RequestMapping(value = "/version/listAll/{containerName:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<SyndicateDataVersionInfo> findAllSyndicateData(
            @PathVariable(RestConstants.CONTAINER_NAME) String containerName) {
        RestResponse<SyndicateDataVersionInfo> response = new RestResponse<SyndicateDataVersionInfo>();
        SyndicateDataVersionInfo syndicateDataVersionInfo = null;
        try {
            syndicateDataVersionInfo = syndicateDataDelegate.listVersions(containerName);
            response.setError(false);
            response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
            response.setResponse(syndicateDataVersionInfo);
        } catch (BusinessException | SystemException se) {
            LOGGER.error(se.getLocalizedMessage(), se);
            response.setError(true);
            response.setErrorCode(se.getCode());
            response.setMessage(se.getLocalizedMessage());
        }
        return response;
    }

    /**
     * 
     * List container information for a given versionId and ContainerName
     * 
     * @author mandavak
     * 
     * @param id
     * @param containerName
     * @return
     */
    @RequestMapping(value = "/version/{versionid:.+}/{containerName:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<SyndicateDataContainerInfo> findSyndicateData(@PathVariable("versionid") Long versionid,
            @PathVariable(RestConstants.CONTAINER_NAME) String containerName) {
        RestResponse<SyndicateDataContainerInfo> response = new RestResponse<SyndicateDataContainerInfo>();
        SyndicateDataContainerInfo containerInfo = null;

        try {
            containerInfo = syndicateDataDelegate.getContainerVersionInformation(versionid, containerName);
            response.setError(false);
            response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
            response.setResponse(containerInfo);
        } catch (BusinessException | SystemException se) {
            LOGGER.error(se.getLocalizedMessage(), se);
            response.setError(true);
            response.setErrorCode(se.getCode());
            response.setMessage(se.getLocalizedMessage());
        }
        return response;
    }

    /**
     * Lists all containers available.
     * 
     * @return
     */
    @RequestMapping(value = "/container/listAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<List<SyndicateDataContainerInfo>> listContainers() {
        RestResponse<List<SyndicateDataContainerInfo>> response = new RestResponse<List<SyndicateDataContainerInfo>>();
        List<SyndicateDataContainerInfo> containerInfoList = null;

        try {
            containerInfoList = syndicateDataDelegate.getContainerInformation();
            response.setError(false);
            response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
            response.setResponse(containerInfoList);
        } catch (BusinessException | SystemException se) {
            LOGGER.error(se.getLocalizedMessage(), se);
            response.setError(true);
            response.setErrorCode(se.getCode());
            response.setMessage(se.getLocalizedMessage());
        }
        return response;
    }
    
    
    @RequestMapping(value = "/container/listFilteredContainer", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public RestResponse<PageRecord<SyndicateDataContainerInfo>> getPagedSupportPackages(
			@RequestBody SearchOptions pageInfo) {
		RestResponse<PageRecord<SyndicateDataContainerInfo>> response = new RestResponse<PageRecord<SyndicateDataContainerInfo>>();
		try {
			PageRecord<SyndicateDataContainerInfo> pageRecord = syndicateDataDelegate
					.getContainerInformation(pageInfo);
			response.setResponse(pageRecord);
			response.setError(false);
			response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
		} catch (SystemException | BusinessException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			response.setErrorCode(e.getCode());
			response.setError(true);
			response.setMessage(e.getLocalizedMessage());
		}
		return response;
	}

    /**
     * Data retrieval based on container name
     * 
     * @param id
     * @return
     */
    @RequestMapping(value = "/container/{containerName:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<SyndicateDataContainerInfo> findContainer(@PathVariable(RestConstants.CONTAINER_NAME) String containerName) {
        RestResponse<SyndicateDataContainerInfo> response = new RestResponse<SyndicateDataContainerInfo>();
        SyndicateDataContainerInfo syndicateDataContainerInfo = null;
        try {
            syndicateDataContainerInfo = syndicateDataDelegate.getContainerInformation(containerName);
            response.setError(false);
            response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
            response.setResponse(syndicateDataContainerInfo);
        } catch (BusinessException | SystemException se) {
            LOGGER.error(se.getLocalizedMessage(), se);
            response.setError(true);
            response.setErrorCode(se.getCode());
            response.setMessage(se.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(value = "/{containerName:.+}/{versionId:.+}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<String> deleteContainerVersion(@PathVariable(RestConstants.CONTAINER_NAME) String containerName,
            @PathVariable("versionId") String versionId) {
        RestResponse<String> response = new RestResponse<String>();
        try {
            Long verId = Long.parseLong(versionId);
            syndicateDataDelegate.deleteContainerVersion(verId, containerName);
            response.setError(false);
            response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
            response.setResponse("Success, container version successfully deleted");
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setError(true);
            response.setErrorCode(e.getCode());
            response.setMessage(e.getLocalizedMessage());
        }

        return response;
    }

    /**
     * This method creates a container version. Steps involved are: 1. Creation of new version for a provider. 2. If first version
     * is created the a new dynamic table (SYND_DATA_<provider name>) to hold the container data is created.
     * 
     * 3. Indexes are created on the dynamic table. 4. Version data is inserted into the dynamic table.
     * 
     * @param sContainerInfo
     * @return
     */
    @RequestMapping(value = "/version/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<SyndicateDataContainerInfo> createProvider(@RequestParam("containerInfo") String sContainerInfoJSON,
            @RequestParam("syndData") MultipartFile csvFile) {
        RestResponse<SyndicateDataContainerInfo> response = new RestResponse<SyndicateDataContainerInfo>();
        try {
            SyndicateDataContainerInfo sContainerInfo = ConversionUtil.convertJson(sContainerInfoJSON,
                    SyndicateDataContainerInfo.class);
            sContainerInfo.setCsvFile(csvFile);
            syndicateDataDelegate.createProvider(sContainerInfo);
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

    @RequestMapping(value = "/update/container", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<String> updateProvider(@RequestBody SyndicateDataContainerInfo sContainerInfo) {
        RestResponse<String> response = new RestResponse<String>();
        try {
            syndicateDataDelegate.updateProvider(sContainerInfo);
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

    @RequestMapping(value = "/create/version", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<String> createProviderVersion(@RequestParam("containerInfo") String sContainerInfoJSON,
            @RequestParam("syndData") MultipartFile csvFile) {
        RestResponse<String> response = new RestResponse<String>();
        try {
            SyndicateDataContainerInfo sContainerInfo = ConversionUtil.convertJson(sContainerInfoJSON,
                    SyndicateDataContainerInfo.class);
            sContainerInfo.setCsvFile(csvFile);
            syndicateDataDelegate.createProviderVersion(sContainerInfo);
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

    @RequestMapping(value = "/update/version", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<String> updateProviderVersion(@RequestBody SyndicateDataContainerInfo sContainerInfo) {
        RestResponse<String> response = new RestResponse<String>();
        try {
            syndicateDataDelegate.updateProviderVersion(sContainerInfo);
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

    /**
     * This method used for downloading of syndicate data column and data
     * 
     * @param containerName
     * @param versionName
     * @param response
     */
    @RequestMapping(value = "/version/template/{containerName:.+}/{versionId:.+}/{versionName:.+}", method = RequestMethod.GET, produces = "text/csv")
    public void downloadSyndData(@PathVariable("containerName") String containerName, @PathVariable("versionId") Long versionId,
            @PathVariable("versionName") String versionName, HttpServletResponse response) {
        try {
            response.setHeader(CONTENT_DISPOSITION, CSV_ATTACHMENT + containerName + BusinessConstants.UNDERSCORE + versionName
                    + BusinessConstants.UNDERSCORE + "Data.csv");
            List<String> syndTableData = syndicateDataDelegate.downloadSyndTableData(containerName, versionId);
            ServletOutputStream outputStream = response.getOutputStream();
            for (String syndData : syndTableData) {
                outputStream.write(syndData.getBytes());
            }
            outputStream.flush();
            outputStream.close();

        } catch (BusinessException | SystemException se) {
            LOGGER.error(se.getLocalizedMessage(), se);
        } catch (IOException e) {
            LOGGER.error("", e);
        }
    }
    
    @RequestMapping(value = "/version/definition/{containerName}", method = RequestMethod.GET, produces = "text/csv")
    public void downloadSyndContainerDefinition(@PathVariable("containerName") String containerName, HttpServletResponse response) {
    	try {
    		response.setHeader(CONTENT_DISPOSITION, CSV_ATTACHMENT + containerName + BusinessConstants.UNDERSCORE + "Definition.csv");
    		String csvTemplateContent = syndicateDataDelegate.downloadSyndContainerDefinition(containerName);
    		OutputStream ros = response.getOutputStream();
    		ros.write(csvTemplateContent.getBytes());
    		ros.flush();
    	} catch (BusinessException | SystemException se) {
    		LOGGER.error(se.getLocalizedMessage(), se);
    	} catch (IOException e) {
    		LOGGER.error("Exception occured while writing to response", e);
    	}
    }    

}
