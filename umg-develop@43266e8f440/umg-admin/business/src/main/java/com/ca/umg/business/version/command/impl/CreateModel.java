package com.ca.umg.business.version.command.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.model.delegate.ModelDelegate;
import com.ca.umg.business.model.info.ModelInfo;
import com.ca.umg.business.version.command.annotation.CommandDescription;
import com.ca.umg.business.version.command.base.AbstractCommand;
import com.ca.umg.business.version.command.error.Error;
import com.ca.umg.business.version.info.VersionInfo;

/**
 * This class would do the following steps:</br>
 * <table BORDER CELLPADDING=3 CELLSPACING=1>
 * <tr>
 * <td ALIGN=CENTER COLSPAN = 1><b>Create ({@link #execute(Object)})</b></td>
 * <td ALIGN=CENTER COLSPAN = 1><b>Rollback ({@link #rollback(Object)})</b></td>
 * </tr>
 * <tr>
 * <td>Create MODEL by calling {@link ModelDelegate#createModelWithoutValidn(ModelInfo)}</td>
 * <td>call delete {@link ModelDelegate#deleteModel(String)} using the id}</td>
 * </tr>
 * </table>
 * 
 * @author raddibas
 * 
 */
@Named
@Scope("prototype")
@CommandDescription(name = "createModel")
public class CreateModel extends AbstractCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateModel.class);
    private static final String CREATE_MODEL = "createModel";

    @Inject
    private ModelDelegate modelDelegate;

    @Override
    public void execute(Object data) throws BusinessException, SystemException {
        ModelInfo modelInfo = null;
        List<Error> errors = new ArrayList<Error>();
        boolean breakExecution = Boolean.FALSE;
        ModelInfo persistentModelInfo = null;
        VersionInfo versionInfo = null;
        try {
            if (checkData(errors, data, CREATE_MODEL, VersionInfo.class)) {
                versionInfo = ((VersionInfo) data);
                modelInfo = versionInfo.getMapping().getModel();
                if (modelInfo.getXml() != null) {
                    setExecuted(Boolean.TRUE);
                    persistentModelInfo = modelDelegate.createModelWithoutValidn(modelInfo);
                    if (persistentModelInfo != null) {
                        versionInfo.getMapping().setModel(persistentModelInfo);
                        persistentModelInfo.setDocumentation(modelInfo.getDocumentation());
                        modelDelegate.storeModelArtifacts(persistentModelInfo);
                        persistentModelInfo.setExcel(modelInfo.getExcel());
                        modelDelegate.storeModelDefArtifacts(persistentModelInfo);

                    }

                } else {
                    if (modelInfo.getId() != null) {
                        persistentModelInfo = modelDelegate.getModelDetails(modelInfo.getId());
                        if (persistentModelInfo != null) {
                            versionInfo.getMapping().setModel(persistentModelInfo);
                        }
                    } else {
                        breakExecution = Boolean.TRUE;
                        errors.add(new Error("SYSTEM ERROR :Existing Model id is not present!", CREATE_MODEL, ""));

                    }

                }

            } else {
                breakExecution = Boolean.TRUE;
            }
        } catch (SystemException | BusinessException ex) {
            breakExecution = Boolean.TRUE;
            LOGGER.error("error in CreateModel::execute method : ", ex);
            errors.add(new Error(ex.getLocalizedMessage(), BusinessConstants.EXECUTE, ex.getCode()));
        }
        getErrorController().setErrors(errors);
        getErrorController().setExecutionBreak(breakExecution);
    }

    @Override
    public void rollback(Object data) throws BusinessException, SystemException {
        ModelInfo modelInfo = null;
        List<Error> errors = new ArrayList<Error>();
        boolean breakExecution = Boolean.FALSE;
        try {
            if (checkData(errors, data, CREATE_MODEL, VersionInfo.class)) {
                modelInfo = ((VersionInfo) data).getMapping().getModel();
                modelDelegate.deleteModel(modelInfo.getId());
            } else {
                breakExecution = Boolean.TRUE;
            }
        } catch (SystemException | BusinessException ex) {
            LOGGER.error("error in CreateModel::rollback method : ", ex);
            errors.add(new Error(ex.getLocalizedMessage(), BusinessConstants.ROLLBACK, ex.getCode()));
        }
        getErrorController().setErrors(errors);
        getErrorController().setExecutionBreak(breakExecution);
    }

    @Override
    public boolean isCreated() throws BusinessException, SystemException {
        return true;
    }

}
