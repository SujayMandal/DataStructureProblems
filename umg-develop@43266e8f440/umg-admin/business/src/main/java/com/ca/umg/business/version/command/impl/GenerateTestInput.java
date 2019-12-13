/**
 * 
 */
package com.ca.umg.business.version.command.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.version.command.annotation.CommandDescription;
import com.ca.umg.business.version.command.base.AbstractCommand;
import com.ca.umg.business.version.command.error.Error;
import com.ca.umg.business.version.info.VersionInfo;
import com.ca.umg.business.versiontest.delegate.VersionTestDelegate;

/**
 * @author chandrsa
 *
 */
@Named
@Scope("prototype")
@CommandDescription(name = "generateTestInput")
public class GenerateTestInput extends AbstractCommand {

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm";
    private static final String GENERATE_TEST_INPUT = "generateTestInput";
    @Inject
    private VersionTestDelegate versionTestDelegate;

    private final static Logger LOGGER = LoggerFactory.getLogger(GenerateTestInput.class);

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.version.command.Command#execute(java.lang.Object)
     */
    @Override
    public void execute(Object data) throws BusinessException, SystemException {
        List<Error> errors = new ArrayList<Error>();
        VersionInfo versionInfo;
        boolean execBreak = Boolean.FALSE;
        LOGGER.debug("Started GenerateTestInput");
        if (checkData(errors, data, GENERATE_TEST_INPUT, VersionInfo.class)) {
            LOGGER.debug("Data validated for GenerateTestInput");
            versionInfo = (VersionInfo) data;
            if (validateData(errors, versionInfo)) {
                setExecuted(Boolean.TRUE);
                DateTimeFormatter fmtTz = DateTimeFormat.forPattern(DATE_FORMAT);
                String dateStr = new StringBuffer(DateTime.now(DateTimeZone.UTC).toString(fmtTz)).toString();

                // Sending date as EMPTY ("") as runtime would take the UTC/GMT time if no date is found in the Tenant Input
                versionInfo
                        .setSampleTestInput(new String(versionTestDelegate.getSampleTenantInput(versionInfo.getMapping()
                                .getName(), versionInfo.getName(), versionInfo.getMajorVersion(), versionInfo.getMinorVersion(),
                                dateStr, Boolean.TRUE,Boolean.TRUE,Boolean.TRUE,Boolean.TRUE)));
            } else {
                execBreak = Boolean.TRUE;
            }
        } else {
            execBreak = Boolean.TRUE;
        }
        getErrorController().setErrors(errors);
        getErrorController().setExecutionBreak(execBreak);
        LOGGER.debug("Ended GenerateTestInput");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.version.command.Command#rollback(java.lang.Object)
     */
    @Override
    public void rollback(Object data) throws BusinessException, SystemException {
        LOGGER.debug("Rollback GenerateTestInput Called");
    }

    private boolean validateData(List<Error> errors, VersionInfo versionInfo) {
        boolean valid = Boolean.TRUE;
        if (versionInfo.getMapping() != null) {
            if (StringUtils.isBlank(versionInfo.getMapping().getName())) {
                errors.add(new Error("Mapping is not saved or the mapping name not set, cannot create test data",
                        GENERATE_TEST_INPUT, ""));
                valid = Boolean.FALSE;
            }
            if (StringUtils.isBlank(versionInfo.getName())) {
                errors.add(new Error("Tenant model name not found, cannot create test data", GENERATE_TEST_INPUT, ""));
                valid = Boolean.FALSE;
            }
            if (versionInfo.getMajorVersion() <= BusinessConstants.NUMBER_ZERO) {
                errors.add(new Error("Major version for tenant model has to be greater than zero", GENERATE_TEST_INPUT, ""));
                valid = Boolean.FALSE;
            }
        }
        return valid;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.version.command.Command#isCreated()
     */
    @Override
    public boolean isCreated() throws BusinessException, SystemException {
        LOGGER.debug("GenerateTestInput command created and getting added to exeution");
        return true;
    }
}
