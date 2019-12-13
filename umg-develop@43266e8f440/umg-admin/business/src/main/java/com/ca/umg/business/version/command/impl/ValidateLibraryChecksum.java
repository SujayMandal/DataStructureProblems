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
import com.ca.framework.core.publishing.status.constants.PublishingStatus;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.model.delegate.ModelDelegate;
import com.ca.umg.business.model.info.ModelLibraryInfo;
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
 * <td>Validate uploaded library is already available and check checksum of the library is correct</td>
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
@CommandDescription(name = "validateLibraryChecksum")
public class ValidateLibraryChecksum extends AbstractCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidateLibraryChecksum.class);
    private static final String CHECKSUM_VALIDATION = "validateLibraryChecksum";

    /**
     * ModelDelegate used to validate the checksum
     */
    @Inject
    private ModelDelegate modelDelegate;

    @Override
    public void execute(Object data) throws BusinessException, SystemException {
        List<Error> errors = new ArrayList<Error>();
        boolean execBreak = Boolean.FALSE;
        if (checkData(errors, data, CHECKSUM_VALIDATION, VersionInfo.class)) {
            try {
                ModelLibraryInfo modelLibraryInfo = ((VersionInfo) data).getModelLibrary();
                if (modelLibraryInfo.getJar() != null) {
                	  setExecuted(Boolean.TRUE);
                    modelDelegate.validateCheckSum(modelLibraryInfo);
                    sendStatusMessage(errors, data, PublishingStatus.VALIDATING_CHECKSUM.getStatus());
                    modelDelegate.checkJarAvailability(modelLibraryInfo);
                } else {
                    // If jar name is not null,then validation is not required as the validation already happened first time
                    if (modelLibraryInfo.getId() == null) {
                        execBreak = Boolean.TRUE;
                        errors.add(new Error("SYSTEM ERROR : Existing model library id is not present!", CHECKSUM_VALIDATION, ""));
                    }
                }
            } catch (BusinessException | SystemException ex) {
                execBreak = Boolean.TRUE;
                errors.add(new Error(ex.getLocalizedMessage(), CHECKSUM_VALIDATION, ex.getCode()));
            }
        } else {
            execBreak = Boolean.TRUE;
        }
        getErrorController().setErrors(errors);
        getErrorController().setExecutionBreak(execBreak);
    }

    @Override
    public void rollback(Object data) throws BusinessException, SystemException {
        LOGGER.info("Rollback called for " + CHECKSUM_VALIDATION + ".But No Action required");
    }

    @Override
    public boolean isCreated() throws BusinessException, SystemException {
        boolean isCreated = false;
        if (modelDelegate != null) {
            isCreated = true;
        }
        return isCreated;
    }

}
