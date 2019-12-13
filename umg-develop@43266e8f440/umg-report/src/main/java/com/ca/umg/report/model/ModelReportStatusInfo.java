package com.ca.umg.report.model;

import com.ca.framework.core.info.BaseInfo;
import com.google.common.base.Objects;

public class ModelReportStatusInfo extends BaseInfo {

	private static final long serialVersionUID = 1L;

    private String reportTemplateId;
    
    private String umgTransactionId;
    
    private String tenantId;
    
    private String reportUrl;
    
    private String reportLocation;
    
    private String reportExecutionstatus;
    
    private String modelName;
    
    private int reportVersion;
    
    private String reportName;
    
    private String reportFileName;
    
    private String reportEngine;
    
    private String reportType;
    
    private Long transactionCreatedDate;
    
    private String clientTransactionId;
    
    private String reportJsonString;
    
	public String getReportTemplateId() {
		return reportTemplateId;
	}

	public void setReportTemplateId(String reportTemplateId) {
		this.reportTemplateId = reportTemplateId;
	}

	public String getUmgTransactionId() {
		return umgTransactionId;
	}

	public void setUmgTransactionId(String umgTransactionId) {
		this.umgTransactionId = umgTransactionId;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getReportUrl() {
		return reportUrl;
	}

	public void setReportUrl(String reportUrl) {
		this.reportUrl = reportUrl;
	}

	public String getReportLocation() {
		return reportLocation;
	}

	public void setReportLocation(String reportLocation) {
		this.reportLocation = reportLocation;
	}

	public String getReportExecutionstatus() {
		return reportExecutionstatus;
	}

	public void setReportExecutionstatus(String reportExecutionstatus) {
		this.reportExecutionstatus = reportExecutionstatus;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public int getReportVersion() {
		return reportVersion;
	}

	public void setReportVersion(int reportVersion) {
		this.reportVersion = reportVersion;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	
	public void setReportFileName(final String reportFileName) {
		this.reportFileName = reportFileName;
	}
	
	public String getReportFileName() {
		return reportFileName;
	}
	
	public String getReportEngine() {
		return reportEngine;
	}

	public void setReportEngine(String reportEngine) {
		this.reportEngine = reportEngine;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}
	
	public void setTransactionCreatedDate(final Long transactionCreatedDate) {
		this.transactionCreatedDate = transactionCreatedDate;
	}
	
	public Long getTransactionCreatedDate() {
		return transactionCreatedDate;
	}
	
	public String getClientTransactionId() {
		return clientTransactionId;
	}

	public void setClientTransactionId(String clientTransactionId) {
		this.clientTransactionId = clientTransactionId;
	}

	public String getReportJsonString() {
		return reportJsonString;
	}

	public void setReportJsonString(String reportJsonString) {
		this.reportJsonString = reportJsonString;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this.getClass()).
				add("ReportTemplateId:", reportTemplateId).
				add("UMGTransactionId:", umgTransactionId).
				add("TransactioncreatedDate:", transactionCreatedDate).
				add("TenantId:", tenantId).
				add("ReportUrl:", reportUrl).
				add("ReportLocation:", reportLocation).
				add("ReportExecutionstatus:", reportExecutionstatus).
				add("ModelName:", modelName).
				add("ReportVersion:", reportVersion).
				add("ReportName:", reportName).
				add("ReportFileName:", reportFileName).
				add("ReportEngine:", reportEngine).
				add("ReportType:", reportType).
				add("clientTransactionId:", clientTransactionId).
				add("Id:", getId()).toString();
    }

    public static ModelReportStatusInfo buildMRStatusInfo(final ModelReportTemplateDefinition reportTemplate) {
        final ModelReportStatusInfo modelReportStatusInfo = new ModelReportStatusInfo();
        modelReportStatusInfo.setReportEngine(reportTemplate.getReportEngine());
        modelReportStatusInfo.setReportType(reportTemplate.getReportType());
        modelReportStatusInfo.setReportVersion(reportTemplate.getReportVersion());
        modelReportStatusInfo.setReportName(reportTemplate.getName());
        modelReportStatusInfo.setReportTemplateId(reportTemplate.getId());
        modelReportStatusInfo.setTenantId(reportTemplate.getTenantId());
        return modelReportStatusInfo;
    }
	
	public static ModelReportStatusInfo buildMRStatusInfo(final ModelReportTemplateInfo reportTemplateInfo){
		final ModelReportStatusInfo modelReportStatusInfo = new ModelReportStatusInfo();
    	modelReportStatusInfo.setReportEngine(reportTemplateInfo.getReportEngine());
    	modelReportStatusInfo.setReportType(reportTemplateInfo.getReportType());
    	modelReportStatusInfo.setReportVersion(reportTemplateInfo.getReportVersion());
    	modelReportStatusInfo.setReportName(reportTemplateInfo.getName());
    	modelReportStatusInfo.setReportTemplateId(reportTemplateInfo.getId());
    	modelReportStatusInfo.setTenantId(reportTemplateInfo.getTenantId());
		return modelReportStatusInfo;
	}	
}