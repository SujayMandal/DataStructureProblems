package com.ca.umg.business.version.command.impl;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.publishing.status.constants.PublishingStatus;
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
 * <td>Validate model IO xml with UMG MATLAB IO XSD</td>
 * <td>Added only log as rollback action is not required</td>
 * <tr>
 * 
 * </table>
 * 
 * @author basanaga
 * 
 */
@Named
@Scope(BusinessConstants.SCOPE_PROTOTYPE)
@CommandDescription(name = "validateModelIOXml")
public class ValidateModelIOXml extends AbstractCommand {

    @Inject
    private ModelDelegate modelDelegate;

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidateModelIOXml.class);
    private static final String VALIDATE_MODEL_IO = "validateModelIOXml";

    @Override
    public void execute(Object data) throws BusinessException, SystemException {
        List<Error> errors = new ArrayList<Error>();
        Boolean execBreak = Boolean.FALSE;
        VersionInfo versionInfo = null;
        try {
            if (checkData(errors, data, VALIDATE_MODEL_IO, VersionInfo.class)) {
                versionInfo = (VersionInfo) data;
                if (versionInfo != null && versionInfo.getMapping() != null && versionInfo.getMapping().getModel() != null) {
                    ModelInfo modelInfo = versionInfo.getMapping().getModel();
                    if (modelInfo.getXml() != null) {
                        byte[] xmlArray = modelInfo.getXml().getDataArray();
                        modelDelegate.validateModelXml(new ByteArrayInputStream(xmlArray));
                        setExecuted(Boolean.TRUE);
                    } else {
                        // If ioDefinitionName is not null,then validation is not required as the validation already happened
                        // first time
                        if (modelInfo.getId() == null) {
                            execBreak = Boolean.TRUE;
                            errors.add(new Error("SYSTEM ERROR :ModelIO xml does not construct correctly.", VALIDATE_MODEL_IO, ""));
                        }
                    }
                } else {
                    errors.add(new Error("SYSTEM ERROR : Data provided is empty, could not proceed!", VALIDATE_MODEL_IO, ""));
                    execBreak = Boolean.TRUE;
                }

            } else {
                execBreak = Boolean.TRUE;

            }
        } catch (BusinessException | SystemException ex) {
            execBreak = Boolean.TRUE;
            errors.add(new Error(ex.getLocalizedMessage(), VALIDATE_MODEL_IO, ex.getCode()));
        } catch (Exception ex) {// NOPMD
            errors.add(new Error("SYSTEM ERROR :" + ex.getMessage(),
                    VALIDATE_MODEL_IO, ""));
            execBreak = Boolean.TRUE;

        } finally {
            sendStatusMessage(errors, data, PublishingStatus.VALIDATING_IO_DEFINITION.getStatus());
        }
        getErrorController().setErrors(errors);
        getErrorController().setExecutionBreak(execBreak);

    }

    @Override
    public void rollback(Object data) throws BusinessException, SystemException {
        LOGGER.info("Rollback called for " + VALIDATE_MODEL_IO + ".But No Action required");
    }

    @Override
    public boolean isCreated() throws BusinessException, SystemException {
        return true;
    }

}
