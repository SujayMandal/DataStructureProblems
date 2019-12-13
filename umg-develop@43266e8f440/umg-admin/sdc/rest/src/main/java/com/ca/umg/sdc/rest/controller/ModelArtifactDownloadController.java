package com.ca.umg.sdc.rest.controller;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.model.delegate.ModelDelegate;
import com.ca.umg.business.model.entity.Model;
import com.ca.umg.business.model.info.ModelArtifact;
import com.ca.umg.sdc.rest.constants.ModelConstants;

@Controller
@RequestMapping(ModelConstants.MODEL_DOWNLOAD_URL)
public class ModelArtifactDownloadController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelController.class);
    
    private static final String MODEL_ID = "modelId";
    
    private static final String ATTACHMENT_TYPE = "attachment; filename=\"%s\"";

    @Inject
    private ModelDelegate modelDelegate;
    
    @RequestMapping(value = "/downloadModelDoc/{modelId}", method = RequestMethod.GET)
    public void downloadDoc(@PathVariable(MODEL_ID) String modelId,
            HttpServletResponse response) throws SystemException, BusinessException {
        try {
            List<ModelArtifact> modelArtifact = modelDelegate.getModelArtifactsFromDownloadModelDoc(modelId);
            if (modelArtifact != null && ! modelArtifact.isEmpty() ) {
            	String headerValue = String.format(ATTACHMENT_TYPE, modelArtifact.get(0).getName());
                response.setHeader(ModelConstants.CONTENT_DISPOSITION, headerValue);
                response.getOutputStream().write(modelArtifact.get(0).getDataArray());
                response.getOutputStream().flush();
            } else {
                writeErrorData(response, modelId, ModelConstants.MODEL_DOC_NOT_FOUND);
            }
        } catch (BusinessException | SystemException se) {
            LOGGER.error(se.getLocalizedMessage(), se);
        } catch (IOException e) {
            LOGGER.error("", e);
        }
    }

    @RequestMapping(value = "/downloadLibraryJar/{modelId}", method = RequestMethod.GET)
    public void downloadJar(@PathVariable(MODEL_ID) String modelLibraryId,
            HttpServletResponse response) throws SystemException, BusinessException {
        try {
            List<ModelArtifact> modelArtifacts = modelDelegate.getModelLibraryArtifactsFromDownloadLibraryJar(modelLibraryId);
            boolean isLibFound = Boolean.FALSE;
            if (modelArtifacts != null && CollectionUtils.isNotEmpty(modelArtifacts)) {
                for(ModelArtifact modelArtifact : modelArtifacts){
                    if (extensionCheck(modelArtifact)) {
                        String headerValue = String.format(ATTACHMENT_TYPE, modelArtifact.getName());
                        response.setHeader(ModelConstants.CONTENT_DISPOSITION,headerValue);
                        response.getOutputStream().write(modelArtifact.getDataArray());
                        response.getOutputStream().flush();
                        isLibFound = Boolean.TRUE;
                        break;
                    }
                }
                if (!isLibFound) {
                    writeErrorData(response, modelLibraryId, ModelConstants.MODEL_LIBRARY_NOT_FOUND);
                }
            } else {
                writeErrorData(response, modelLibraryId, ModelConstants.MODEL_LIBRARY_NOT_FOUND);
            }

        } catch (BusinessException | SystemException se) {
            LOGGER.error(se.getLocalizedMessage(), se);
        } catch (IOException e) {
            LOGGER.error("", e);
        }
    }

	private boolean extensionCheck(ModelArtifact modelArtifact) {
		return modelArtifact.getName().endsWith(".jar") || modelArtifact.getName().endsWith(".tar.gz") || modelArtifact.getName().endsWith(".zip") || modelArtifact.getName().endsWith(".xls") || modelArtifact.getName().endsWith(".xlsx") || modelArtifact.getName().endsWith(".xlsm") ;
	}
    
    @RequestMapping(value = "/downloadModelXml/{modelId}", method = RequestMethod.GET)
    public void downloadXml(@PathVariable(MODEL_ID) String modelLibraryId,
            HttpServletResponse response) throws SystemException, BusinessException {
        try {
            Model model = modelDelegate.getModelXML(modelLibraryId);
            if (model != null) {
            	String headerValue = String.format(ATTACHMENT_TYPE, model.getIoDefinitionName());
                response.setHeader(ModelConstants.CONTENT_DISPOSITION, headerValue);
                response.getOutputStream().write(model.getModelDefinition().getIoDefinition());
                response.getOutputStream().flush();
            } else {
                writeErrorData(response, modelLibraryId, ModelConstants.MODEL_LIBRARY_NOT_FOUND);
            }
        } catch (BusinessException | SystemException se) {
            LOGGER.error(se.getLocalizedMessage(), se);
        } catch (IOException e) {
            LOGGER.error("", e);
        }
    }
    
    @RequestMapping(value = "/downloadModelExcel/{modelId}", method = RequestMethod.GET)
    public void downloadExcel(@PathVariable(MODEL_ID) String modelLibraryId,
            HttpServletResponse response) throws SystemException, BusinessException {
        try {
            Model model = modelDelegate.getModelXML(modelLibraryId);
            if (model != null) {
            		byte[] modelExcel = modelDelegate.getModelExcel(model);
            		String headerValue = String.format(ATTACHMENT_TYPE, model.getIoDefExcelName());
	                response.setHeader(ModelConstants.CONTENT_DISPOSITION, headerValue);
	                response.getOutputStream().write(modelExcel);
	                response.getOutputStream().flush();
            } else {
                writeErrorData(response, modelLibraryId, ModelConstants.MODEL_LIBRARY_NOT_FOUND);
            }
        } catch (BusinessException | SystemException se) {
        	writeErrorData(response, modelLibraryId, ModelConstants.MODEL_LIBRARY_NOT_FOUND);
        } catch (IOException e) {
            LOGGER.error("", e);
        }
    }

    @RequestMapping(value = "/downloadModelLibManifest/{modelId}", method = RequestMethod.GET)
    public void downloadManifest(@PathVariable(MODEL_ID) String modelLibraryId, HttpServletResponse response)
            throws SystemException, BusinessException {
        try {
            List<ModelArtifact> modelArtifacts = modelDelegate.getModelLibraryArtifactsFromDownloadModelLibManifest(modelLibraryId);
            boolean isCsvFound = Boolean.FALSE;
            if (modelArtifacts != null && modelArtifacts.size() > 1) {
                for (ModelArtifact modelArtifact : modelArtifacts) {
                    String csvName = modelArtifact.getName();
                    if (csvName != null && csvName.endsWith(".csv")) {
                        String headerValue = String.format(ATTACHMENT_TYPE, modelArtifact.getName());
                        response.setHeader(ModelConstants.CONTENT_DISPOSITION, headerValue);
                        response.getOutputStream().write(modelArtifact.getDataArray());
                        response.getOutputStream().flush();
                        isCsvFound = Boolean.TRUE;
                        break;
                    }
                }
                if (!isCsvFound) {
                    writeErrorData(response, modelLibraryId, ModelConstants.MODEL_MANIFEST_NOT_FOUND);
                }
            } else {
                writeErrorData(response, modelLibraryId, ModelConstants.MODEL_MANIFEST_NOT_FOUND);
            }
        } catch (BusinessException | SystemException | IOException se) {
            LOGGER.error(se.getMessage(), se);
            writeErrorData(response, modelLibraryId, ModelConstants.MODEL_MANIFEST_NOT_FOUND);
        }
    }

    private void writeErrorData(HttpServletResponse response, String modelId, String msg) {
        try {
            String headerValue = String.format("attachment; filename=\"%s\"", "error_" + modelId + ".txt");
            response.setHeader(ModelConstants.CONTENT_DISPOSITION, headerValue);
            String errorMsg = null;
            if (msg == null) {
                errorMsg = "File doesn't exist: " + modelId;
                response.getOutputStream().write(errorMsg.getBytes());
                response.getOutputStream().flush();
            } else {
                errorMsg = msg;
                response.getOutputStream().write(errorMsg.getBytes());
                response.getOutputStream().flush();
            }
        } catch (IOException excep) {
            LOGGER.error("Error while Writting error data  ", excep);
        }
    }
}
