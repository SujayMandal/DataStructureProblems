package com.ca.umg.business.version.command.impl;

import org.apache.commons.lang3.StringUtils;

import com.ca.umg.business.version.command.base.AbstractCommand;
import com.ca.umg.business.version.info.VersionInfo;
import com.ca.umg.report.model.ModelReportTemplateInfo;

public abstract class AbstractModelReportCommand extends AbstractCommand {
	protected boolean hasModelReport(Object data) {
        boolean status = false;
		if (data != null) {
			final VersionInfo versionInfo = (VersionInfo) data;			
			final ModelReportTemplateInfo reportTemplateInfo = versionInfo.getReportTemplateInfo();
			if (reportTemplateInfo != null && (reportTemplateInfo.getTemplateDefinition() != null || StringUtils.isNotBlank(reportTemplateInfo.getId()))) {
				status = true;
			} else {
				status = false;
			}
		}
		
        return status;
    }
}
