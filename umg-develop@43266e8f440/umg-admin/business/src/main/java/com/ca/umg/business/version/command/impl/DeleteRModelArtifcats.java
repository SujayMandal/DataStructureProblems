package com.ca.umg.business.version.command.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.framework.core.util.UmgFileProxy;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.model.delegate.MediateModelLibraryDelegate;
import com.ca.umg.business.model.info.ModelLibraryInfo;
import com.ca.umg.business.util.AdminUtil;
import com.ca.umg.business.version.command.annotation.CommandDescription;
import com.ca.umg.business.version.command.base.AbstractCommand;
import com.ca.umg.business.version.command.error.Error;
import com.ca.umg.business.version.info.VersionInfo;

/**
 * This command used to delete R Temporary files and delete the Mediate Model library record
 * 
 * @author basanaga
 *
 */
@Named
@Scope(BusinessConstants.SCOPE_PROTOTYPE)
@CommandDescription(name = "deleteRModelArtifacts")
public class DeleteRModelArtifcats extends AbstractCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteRModelArtifcats.class);

    private static final String DELETE_R_MODEL_COMMAND = "deleteRModelArtifacts";

    @Inject
    private UmgFileProxy umgFileProxy;

    @Inject
    private SystemParameterProvider sysParam;

    @Inject
    private MediateModelLibraryDelegate mediateModelLibDelegate;

    @Override
    public void execute(Object data) throws BusinessException, SystemException {
        List<Error> errors = new ArrayList<Error>();
        Boolean execBreak = Boolean.FALSE;
        VersionInfo versionInfo;
        try {
            if (checkData(errors, data, DELETE_R_MODEL_COMMAND, VersionInfo.class)) {
                versionInfo = (VersionInfo) data;
                setExecuted(Boolean.TRUE);
                ModelLibraryInfo modelLibraryInfo = versionInfo.getModelLibrary();
                String sanPath = AdminUtil
                        .getSanBasePath(umgFileProxy.getSanPath(sysParam.getParameter(SystemConstants.SAN_BASE)));
                String uploadPath = sanPath + File.separatorChar + sysParam.getParameter(SystemConstants.FILE_UPLOAD_TEMP_PATH);
                File tempModelLibPath = new File(uploadPath + File.separatorChar + modelLibraryInfo.getJarName());
                if (tempModelLibPath.exists()) {
                    tempModelLibPath.delete();
                }
                mediateModelLibDelegate.deleteByNameAndchecksum(modelLibraryInfo.getJarName(), modelLibraryInfo.getChecksum());

            } else {
                execBreak = Boolean.TRUE;
            }
        } catch (SystemException ex)
        {
            errors.add(new Error(ex.getLocalizedMessage(), DELETE_R_MODEL_COMMAND, ex.getCode()));
            execBreak = Boolean.TRUE;
        }
        getErrorController().setErrors(errors);
        getErrorController().setExecutionBreak(execBreak);
    }

    @Override
    public void rollback(Object data) throws BusinessException, SystemException {
        LOGGER.info("roolback called on " + DELETE_R_MODEL_COMMAND + ". But no action required.");
    }

    @Override
    public boolean isCreated() throws BusinessException, SystemException {
        return true;
    }

}
