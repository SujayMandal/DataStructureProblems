/**
 * 
 */
package com.ca.umg.business.version.command.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.umg.business.mapping.delegate.MappingDelegate;
import com.ca.umg.business.mapping.info.MappingDescriptor;
import com.ca.umg.business.validation.ValidationError;
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
 * <td>Create default mapping</td>
 * <td>Find mapping from version info {Version Name, Major & Minor Version Numbers}</td>
 * <tr>
 * <tr>
 * <td>Validate default mapping</td>
 * <td>Get the TID name from the version information</td>
 * <tr>
 * <tr>
 * <td>Save default mapping, with status <i>FINALIZED</i></td>
 * <td>Call delete using TID name {@link MappingDelegate#deleteMapping(String)}</td>
 * <tr>
 * </table>
 * 
 * @author chandrsa
 *
 */
@Named
@CommandDescription(name = "createMapping")
@Scope("prototype")
public class CreateMapping extends AbstractCommand { // NOPMD

    private static final String CREATE_MAPPING = "createMapping";

    protected static final String DEFAULT_DESCRIPTION = "Default Generated";

    protected static final String VALIDATE = "validate";

    private final static Logger LOGGER = LoggerFactory.getLogger(CreateMapping.class);

    @Inject
    private MappingDelegate mappingDelegate;

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.version.command.Command#execute(java.lang.Object)
     */
    @Override
    public void execute(Object data) throws BusinessException, SystemException { // NOPMD
        LOGGER.debug("Create Mapping started!!");
        VersionInfo versionInfo;
        String derievedModelName;
        MappingDescriptor mappingDescriptor;
        List<Error> errors = new ArrayList<Error>();
        boolean execBreak = Boolean.FALSE;
        if (checkData(errors, data, CREATE_MAPPING, VersionInfo.class)) {
            versionInfo = (VersionInfo) data;
            if (versionInfo != null && versionInfo.getMapping() != null && versionInfo.getMapping().getModel() != null
                    && StringUtils.isNotBlank(versionInfo.getMapping().getModel().getUmgName())) {
                setExecuted(Boolean.TRUE);
                derievedModelName = versionInfo.getMapping().getModel().getUmgName();
                LOGGER.debug(String.format("Derieved Model Name :: %s", derievedModelName));

                mappingDescriptor = mappingDelegate.generateMapping(derievedModelName);
                if (mappingDescriptor != null) {
                    execBreak = saveMapping(versionInfo, derievedModelName, mappingDescriptor, errors, execBreak);
                } else {
                    errors.add(new Error("SYSTEM ERROR : Could not create default mapping", CREATE_MAPPING, ""));
                    execBreak = Boolean.TRUE;
                }
            } else {
                errors.add(new Error("SYSTEM ERROR : Data provided is empty, could not proceed!", CREATE_MAPPING, ""));
                execBreak = Boolean.TRUE;
            }
        } else {
            execBreak = Boolean.TRUE;
        }
        getErrorController().setErrors(errors);
        getErrorController().setExecutionBreak(execBreak);
    }

    private boolean saveMapping(VersionInfo versionInfo, String derievedModelName, MappingDescriptor mappingDescriptor,
            List<Error> errors, boolean execBreak) throws BusinessException, SystemException {
        boolean errorsFound = execBreak;
        LOGGER.debug(String.format("Saving Mapping For Model Name :: %s", derievedModelName));
        mappingDescriptor.setDescription(DEFAULT_DESCRIPTION);
        KeyValuePair<String, List<ValidationError>> nameErrors = mappingDelegate.saveMappingDescription(mappingDescriptor,
                derievedModelName, VALIDATE);

        if (nameErrors != null) {
            versionInfo.getMapping().setName(nameErrors.getKey());
            versionInfo.getMapping().setUmgName(nameErrors.getKey());
            if (CollectionUtils.isNotEmpty(nameErrors.getValue())) {
                for (ValidationError validationError : nameErrors.getValue()) {
                    errors.add(new Error(validationError.getField() + validationError.getMessage(), CREATE_MAPPING,
                            validationError.getErrorCode()));
                }
                errorsFound = Boolean.TRUE;
            }
        } else {
            errors.add(new Error("SYSTEM ERROR : Mapping could not be persisted", CREATE_MAPPING, ""));
            errorsFound = Boolean.TRUE;
        }
        return errorsFound;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.version.command.Command#rollback(java.lang.Object)
     */
    @Override
    public void rollback(Object data) throws BusinessException, SystemException {
        List<Error> errors = new ArrayList<Error>();
        VersionInfo versionInfo;
        LOGGER.debug("Rolling Back Mapping :: START");
        if (checkData(errors, data, CREATE_MAPPING, VersionInfo.class)) {
            versionInfo = (VersionInfo) data;
            mappingDelegate.deleteMapping(versionInfo.getMapping().getName());
        } else {
            LOGGER.debug("Rolling Back Mapping :: FAILED in data check");
            getErrorController().setErrors(errors);
            getErrorController().setExecutionBreak(Boolean.TRUE);
        }
        LOGGER.debug("Rolling Back Mapping :: END");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.version.command.Command#isCreated()
     */
    @Override
    public boolean isCreated() throws BusinessException, SystemException {
        LOGGER.debug("CreateMapping command created and getting added to exeution");
        return true;
    }
}
