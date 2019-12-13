package com.ca.umg.business.version.command.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.publishing.status.constants.PublishingStatus;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.model.delegate.ModelDelegate;
import com.ca.umg.business.version.command.annotation.CommandDescription;
import com.ca.umg.business.version.command.error.Error;
import com.ca.umg.business.version.info.VersionInfo;
import com.ca.umg.report.model.ModelReportTemplateInfo;

@Named
@Scope(BusinessConstants.SCOPE_PROTOTYPE)
@CommandDescription(name = "saveReportTemplate")
public class CreateModelReportTemplate extends AbstractModelReportCommand {

    @Inject
    private ModelDelegate modelDelegate;

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateModelReportTemplate.class);

    private static final String SAVE_REPORT_TEMPLATE = "saveReportTemplate";

    @Override
    public void execute(Object data) throws BusinessException, SystemException {
        List<Error> errors = new ArrayList<Error>();
        try {
            if (checkData(errors, data, SAVE_REPORT_TEMPLATE, VersionInfo.class) && hasModelReport(data)) {
                VersionInfo versionInfo;
                versionInfo = (VersionInfo) data;
                setExecuted(Boolean.TRUE);
                ModelReportTemplateInfo reportTemplateInfo;               
                if (versionInfo.getReportTemplateInfo().getId() != null) {              
                    reportTemplateInfo =   modelDelegate.getModelReportTemplateInfo(versionInfo.getReportTemplateInfo().getId());
                    reportTemplateInfo.setId(null);
                    reportTemplateInfo.setVersionId(versionInfo.getId());
               }else{
                   reportTemplateInfo  = versionInfo.getReportTemplateInfo();
               }
                reportTemplateInfo.setVersionId(versionInfo.getId());
                LOGGER.info("Before saving report template defination is :" + reportTemplateInfo.toString());
                final ModelReportTemplateInfo persistModelReportTemplateInfo = modelDelegate
                        .createModelReportTemplate(reportTemplateInfo);
                    setExecuted(Boolean.TRUE);
                if (persistModelReportTemplateInfo == null || persistModelReportTemplateInfo.getId() == null) {
                    errors.add(new Error("SYSTEM ERROR :Existing Model Report Template Id is not present!", SAVE_REPORT_TEMPLATE,
                            ""));
                } else {
                    versionInfo.setReportTemplateInfo(persistModelReportTemplateInfo);
                }
                LOGGER.info("After saving report template defination is :" + versionInfo.getReportTemplateInfo().toString());
                
            }
        } catch (SystemException ex) {
            errors.add(new Error(ex.getLocalizedMessage(), SAVE_REPORT_TEMPLATE, ex.getCode()));
            LOGGER.error("Error while saving report template definition into database");
            LOGGER.error(ex.getLocalizedMessage(), ex);
        } finally {
            sendStatusMessage(errors, data, PublishingStatus.VALIDATE_REPORT_TEMPLATE.getStatus());
        }

        getErrorController().setErrors(errors);
        if (CollectionUtils.isEmpty(errors)) {
            getErrorController().setExecutionBreak(Boolean.FALSE);
        } else {
            getErrorController().setExecutionBreak(Boolean.TRUE);
        }
    }

    @Override
    public void rollback(Object data) throws BusinessException, SystemException {
        LOGGER.error("Rollback called for :" + SAVE_REPORT_TEMPLATE);
        List<Error> errors = new ArrayList<Error>();
        Boolean execBreak = Boolean.FALSE;

        if (hasModelReport(data)) {
            VersionInfo versionInfo = null;
            try {
                if (checkData(errors, data, SAVE_REPORT_TEMPLATE, VersionInfo.class)) {
                    versionInfo = (VersionInfo) data;
                    ModelReportTemplateInfo info = versionInfo.getReportTemplateInfo();
                    modelDelegate.deleteModelReportTemplate(info.getId());
                } else {
                    execBreak = Boolean.TRUE;
                }
                LOGGER.error("Rollback success:" + SAVE_REPORT_TEMPLATE);
            } catch (SystemException | BusinessException ex) {
                errors.add(new Error(ex.getLocalizedMessage(), SAVE_REPORT_TEMPLATE, ex.getCode()));
                execBreak = Boolean.TRUE;
                LOGGER.error("Error while roll backing deleting saved report template");
                LOGGER.error(ex.getLocalizedMessage(), ex);
            }
        }

        getErrorController().setErrors(errors);
        getErrorController().setExecutionBreak(execBreak);

    }

    @Override
    public boolean isCreated() throws BusinessException, SystemException {
        Boolean isCreated = Boolean.FALSE;
        if (modelDelegate != null) {
            isCreated = Boolean.TRUE;
        }
        return isCreated;
    }

}