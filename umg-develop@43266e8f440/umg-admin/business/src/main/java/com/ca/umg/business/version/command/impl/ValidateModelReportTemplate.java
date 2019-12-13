package com.ca.umg.business.version.command.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.version.command.annotation.CommandDescription;
import com.ca.umg.business.version.command.error.Error;

@Named
@Scope(BusinessConstants.SCOPE_PROTOTYPE)
@CommandDescription(name = "validateReportTemplate")
public class ValidateModelReportTemplate extends AbstractModelReportCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidateModelReportTemplate.class);

    @Override
    public void execute(Object data) throws BusinessException, SystemException {
    	LOGGER.info("Execution called on Validate Report Template Command");
        List<Error> errors = new ArrayList<Error>();
    	if (hasModelReport(data)) {
	    	//TODO Empty implementation, can be implemented later on
	    	LOGGER.info("Model has report, hence validating report template");
	        getErrorController().setErrors(errors);
	        getErrorController().setExecutionBreak(Boolean.FALSE);
    	} else {
    		LOGGER.info("Model does not have report, hence not validating report template");
    		getErrorController().setErrors(errors);
	        getErrorController().setExecutionBreak(Boolean.FALSE);
    	}
    }

    @Override
    public void rollback(Object data) throws BusinessException, SystemException {
    	LOGGER.error("Rollback called on Validate Report Template Command, nothing will be roll backed here");
    }

    @Override
    public boolean isCreated() throws BusinessException, SystemException {
        return true;
    }
}