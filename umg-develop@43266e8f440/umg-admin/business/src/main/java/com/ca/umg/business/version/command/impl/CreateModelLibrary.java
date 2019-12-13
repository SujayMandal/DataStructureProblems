package com.ca.umg.business.version.command.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.publishing.status.constants.PublishingStatus;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.framework.core.util.UmgFileProxy;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.model.delegate.MediateModelLibraryDelegate;
import com.ca.umg.business.model.delegate.ModelDelegate;
import com.ca.umg.business.model.entity.MediateModelLibrary;
import com.ca.umg.business.model.info.ModelLibraryInfo;
import com.ca.umg.business.util.AdminUtil;
import com.ca.umg.business.util.ZipUtil;
import com.ca.umg.business.version.command.annotation.CommandDescription;
import com.ca.umg.business.version.command.base.AbstractCommand;
import com.ca.umg.business.version.command.error.Error;
import com.ca.umg.business.version.dao.VersionContainerDAO;
import com.ca.umg.business.version.data.VersionDataContainer;
import com.ca.umg.business.version.info.VersionInfo;

/**
 * This class would do the following steps:</br>
 * <table BORDER CELLPADDING=3 CELLSPACING=1>
 * <tr>
 * <td ALIGN=CENTER COLSPAN = 1><b>Create ({@link #execute(Object)})</b></td>
 * <td ALIGN=CENTER COLSPAN = 1><b>Rollback ({@link #rollback(Object)})</b></td>
 * </tr>
 * <tr>
 * <td>Create Model Library and store library jar in san path</td>
 * <td>Get the model library id, get the persistent Model library, delete Model Library from db and delete the jar from san</td>
 * <tr>
 * 
 * </table>
 * 
 * @author basanaga
 * 
 */
@Named
@Scope(BusinessConstants.SCOPE_PROTOTYPE)
@CommandDescription(name = "createModelLibrary")
public class CreateModelLibrary extends AbstractCommand {
    @Inject
    private ModelDelegate modelDelegate;

    @Inject
    private VersionDataContainer versionDataContainer;

    @Inject
    private MediateModelLibraryDelegate mediateModelLibraryDelegate;

    @Inject
    private UmgFileProxy umgFileProxy;

    @Inject
    private SystemParameterProvider sysParam;

    
    @Inject
    private VersionContainerDAO versionContainerDAO;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateModelLibrary.class);

    private static final String CREATE_MODELLIB = "createModelLibrary";
    private static final String PACKAGE = "Package";


    @Override
    public void execute(Object data) throws BusinessException, SystemException {
        List<Error> errors = new ArrayList<Error>();
        VersionInfo versionInfo;
        try {
            if (checkData(errors, data, CREATE_MODELLIB, VersionInfo.class)) {
                versionInfo = (VersionInfo) data;
                setExecuted(Boolean.TRUE);
                ModelLibraryInfo modelLibraryInfo = versionInfo.getModelLibrary();
                ModelLibraryInfo persistModelLibInfo = null;
                if (modelLibraryInfo.getJar() != null) {
                    if (modelLibraryInfo.getExecutionLanguage().startsWith(BusinessConstants.R_LANG)) {
                        createNewRModelLbrary(modelLibraryInfo, versionInfo);
                    } else {
                        createModelLib(versionInfo, modelLibraryInfo);
                    }
                } else {
                    if (modelLibraryInfo.getId() != null) {
                        if (modelLibraryInfo.getExecutionLanguage().startsWith(BusinessConstants.R_LANG)) {
                            persistModelLibInfo = createRModelLibrary(modelLibraryInfo, versionInfo);
                        } else {
                            persistModelLibInfo = modelDelegate.getModelLibraryDetails(modelLibraryInfo.getId());
                        }
                        if (persistModelLibInfo != null) {
                            versionInfo.setModelLibrary(persistModelLibInfo);
                        }
                    } else {
                        errors.add(new Error("SYSTEM ERROR :Existing model library id is not present!", CREATE_MODELLIB, ""));
                    }
                }
            }
        } catch (SystemException | BusinessException ex) {
            errors.add(new Error(ex.getLocalizedMessage(), CREATE_MODELLIB, ex.getCode()));
        } finally {
            sendStatusMessage(errors, data, PublishingStatus.UPLOADING_MODEL_PACKAGE.getStatus());
        }
        getErrorController().setErrors(errors);
        if (!CollectionUtils.isEmpty(errors)) {
            getErrorController().setExecutionBreak(Boolean.TRUE);
        }
    }

    private ModelLibraryInfo createNewRModelLbrary(ModelLibraryInfo modelLibraryInfo, VersionInfo versionInfo)
            throws SystemException, BusinessException {
        ModelLibraryInfo persistModelLibInfo = null;
        String tmpPath =  sysParam.getParameter(SystemConstants.TEMP_PATH);
        File tempPath = new File(tmpPath);
        if (!tempPath.exists()) {
            tempPath.mkdir();
        }

        File newFile = null;
        OutputStream outputStream = null;
        try {
            newFile = new File(tmpPath, modelLibraryInfo.getJarName());
            outputStream = new FileOutputStream(newFile);
            outputStream.write(modelLibraryInfo.getJar().getDataArray());

            setPackageName(modelLibraryInfo, newFile.getPath());

            persistModelLibInfo = modelDelegate.createModelLibraryWithOutValidation(modelLibraryInfo);
            if (persistModelLibInfo != null && modelLibraryInfo.getManifestFile() != null) {
                persistModelLibInfo.setSupportPackages(modelLibraryInfo.getSupportPackages());
                saveSupportPackages(persistModelLibInfo);
                persistModelLibInfo.setManifestFile(modelLibraryInfo.getManifestFile());
                persistModelLibInfo.setRmanifestFileName(modelLibraryInfo.getRmanifestFileName());
                modelLibraryInfo.getManifestFile().setUmgName(persistModelLibInfo.getUmgName());
                modelLibraryInfo.getManifestFile().setModelName(persistModelLibInfo.getName());
                modelDelegate.storeModelLibraryManifestFile(modelLibraryInfo);
                /*modelLibraryInfo.getManifestFile().setUmgName(persistModelLibInfo.getUmgName());
                modelLibraryInfo.getManifestFile().setModelName(persistModelLibInfo.getName());*/
            }
            modelLibraryInfo.getJar().setModelName(persistModelLibInfo.getName());
            modelLibraryInfo.getJar().setUmgName(persistModelLibInfo.getUmgName());
            modelDelegate.storeModelLibraryArtifacts(modelLibraryInfo);
            versionInfo.setModelLibrary(persistModelLibInfo);
        } catch (IOException e) {
            throw new SystemException(BusinessExceptionCodes.BSE000005, new Object[] {}, e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    LOGGER.error("Exception occured closing Output Stream", e);
                }
            }

            if (newFile != null) {
                newFile.delete();
            }
        }

        return persistModelLibInfo;
    }

    private ModelLibraryInfo createRModelLibrary(ModelLibraryInfo modelLibraryInfo, VersionInfo versionInfo)
            throws BusinessException, SystemException {
        MediateModelLibrary mediateModelLib = mediateModelLibraryDelegate.getMediateModelLibrray(modelLibraryInfo.getId());
        ModelLibraryInfo persistModelLibInfo = null;
        if (mediateModelLib != null) {
            versionInfo.setExistingLibrary(Boolean.FALSE);
            modelLibraryInfo.setId(StringUtils.EMPTY);
            String sanPath = AdminUtil.getSanBasePath(umgFileProxy.getSanPath(sysParam.getParameter(SystemConstants.SAN_BASE)));
            String fileUploadPath = sanPath + File.separatorChar + sysParam.getParameter(SystemConstants.FILE_UPLOAD_TEMP_PATH)
                    + File.separatorChar + modelLibraryInfo.getJarName();
            try {
                setPackageName(modelLibraryInfo, fileUploadPath);
            } catch (IOException ex) {
                BusinessException.newBusinessException(BusinessExceptionCodes.BSE000141,
                        new Object[] { modelLibraryInfo.getJarName(), ex.getMessage() });
            }
            persistModelLibInfo = modelDelegate.createModelLibraryWithOutValidation(modelLibraryInfo);
            if (persistModelLibInfo != null && modelLibraryInfo.getManifestFile() != null) {
                persistModelLibInfo.setSupportPackages(modelLibraryInfo.getSupportPackages());
                modelLibraryInfo.getManifestFile().setUmgName(persistModelLibInfo.getUmgName());
                modelLibraryInfo.getManifestFile().setModelName(persistModelLibInfo.getName());
                saveSupportPackages(persistModelLibInfo);
                persistModelLibInfo.setManifestFile(modelLibraryInfo.getManifestFile());
                persistModelLibInfo.setRmanifestFileName(modelLibraryInfo.getRmanifestFileName());
                modelDelegate.storeModelLibraryManifestFile(modelLibraryInfo);
            }
        } else {
            if (!versionInfo.isExistingLibrary()) {
                createModelLib(versionInfo, modelLibraryInfo);
            } else {
                persistModelLibInfo = modelDelegate.getModelLibraryDetails(modelLibraryInfo.getId());
            }

        }
        if (persistModelLibInfo != null && !versionInfo.isExistingLibrary()) {
            modelDelegate.moveRmodelFromTemptoSan(persistModelLibInfo);
        }

        return persistModelLibInfo;

    }

    private void setPackageName(ModelLibraryInfo modelLibraryInfo, String fileUploadPath) throws FileNotFoundException,
            SystemException, BusinessException, IOException {
        File uploadedFile = new File(fileUploadPath);
        InputStream is = new FileInputStream(uploadedFile);
        Properties tarDescription = null;
        if (fileUploadPath.endsWith(".zip")) {
            tarDescription = ZipUtil.getZipDescription(is);
        } else {
            tarDescription = ZipUtil.getTarDescription(is);
        }

        if (is != null) {
            is.close();
        }
        if (tarDescription != null && tarDescription.get(PACKAGE) != null) {
            modelLibraryInfo.setPackageName((String) tarDescription.get(PACKAGE));
        } else {
            BusinessException.newBusinessException(BusinessExceptionCodes.BSE000656, new Object[] { PACKAGE });
        }
    }

    private void saveSupportPackages(ModelLibraryInfo modelLibraryInfo) throws SystemException, BusinessException {
    	if(modelLibraryInfo.getSupportPackages()!=null){
    		LOGGER.error("modelLibraryInfo.getSupportPackages() "+modelLibraryInfo.getSupportPackages().size());
    	}
        if (CollectionUtils.isNotEmpty(modelLibraryInfo.getSupportPackages())) {
            modelDelegate.saveSupportModelExecPackages(modelLibraryInfo);
        }        
        versionContainerDAO.getSupportPackages(modelLibraryInfo.getId());
    }

    private void createModelLib(VersionInfo versionInfo, ModelLibraryInfo modelLibraryInfo) throws BusinessException,
            SystemException {
        ModelLibraryInfo persistModelLibInfo;
        setExecuted(Boolean.TRUE);
        persistModelLibInfo = modelDelegate.createModelLibraryWithOutValidation(modelLibraryInfo);
        if (persistModelLibInfo != null) {
            persistModelLibInfo.setJar(modelLibraryInfo.getJar());
            modelLibraryInfo.getJar().setModelName(persistModelLibInfo.getName());
            modelLibraryInfo.getJar().setUmgName(persistModelLibInfo.getUmgName());
            versionInfo.setModelLibrary(persistModelLibInfo);
            modelDelegate.storeModelLibraryArtifacts(modelLibraryInfo);
        }
        
        
    }

    @Override
    public void rollback(Object data) throws BusinessException, SystemException {
        LOGGER.error("Rollback called for :" + CREATE_MODELLIB);
        List<Error> errors = new ArrayList<Error>();
        Boolean execBreak = Boolean.FALSE;
        VersionInfo versionInfo = null;
        try {
            if (checkData(errors, data, CREATE_MODELLIB, VersionInfo.class)) {
                versionInfo = (VersionInfo) data;
                ModelLibraryInfo modelLibraryInfo = versionInfo.getModelLibrary();
                if (!versionInfo.isExistingLibrary()) {
                    modelDelegate.deleteModelLibrary(modelLibraryInfo.getId());
                }

            } else {
                execBreak = Boolean.TRUE;
            }
            LOGGER.error("Rollback ended for :" + CREATE_MODELLIB);
        } catch (SystemException | BusinessException ex) {
            errors.add(new Error(ex.getLocalizedMessage(), CREATE_MODELLIB, ex.getCode()));
            execBreak = Boolean.TRUE;
        }
        getErrorController().setErrors(errors);
        getErrorController().setExecutionBreak(execBreak);

    }

    @Override
    public boolean isCreated() throws BusinessException, SystemException {
        Boolean isCreated = Boolean.FALSE;
        if (modelDelegate != null && versionDataContainer != null) {
            isCreated = Boolean.TRUE;
        }
        return isCreated;
    }

}