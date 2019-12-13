package com.ca.umg.report.service.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ca.umg.report.model.ModelReportTemplateDefinition;

public interface ModelReportTemplateDAO extends JpaRepository<ModelReportTemplateDefinition, String>{
		
	public ModelReportTemplateDefinition findByVersionIdAndIsActive(final String versionId, final int isActive);
}
