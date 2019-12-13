package com.ca.umg.report.service.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ModelReportStatusDAO extends JpaRepository<ModelReportStatusDefinition, String>, JpaSpecificationExecutor<ModelReportStatusDefinition>{
	
//	public ModelReportStatusDefinition findByUmgTransactionIdAndTenantId(final String umgTransactionId, final String tenantId);
	
	public List<ModelReportStatusDefinition> findByUmgTransactionIdAndReportExecutionstatusAndReportTemplateId(final String umgTransactionId, final String reportExecutionstatus, 
			final String reportTemplateId);
	
	public List<ModelReportStatusDefinition> findByReportExecutionstatusAndReportTemplateId(final String reportExecutionstatus, final String reportTemplateId);
}
