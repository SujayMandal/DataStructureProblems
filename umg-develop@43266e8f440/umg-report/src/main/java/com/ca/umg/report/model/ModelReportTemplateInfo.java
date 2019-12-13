package com.ca.umg.report.model;

import java.io.InputStream;
import java.util.Arrays;

import com.ca.framework.core.info.BaseInfo;
import com.google.common.base.Objects;

public class ModelReportTemplateInfo extends BaseInfo {

	private static final long serialVersionUID = 1L;

	private String versionId;
	
	private String name;
	
	private String description;
	
	private int isActive;
	
	private String templateFileName;
	
    private String tenantId;

    private byte[] templateDefinition;

    private byte[] compiledDefinition;
    
    private int reportVersion;
    
    private String reportType;
    
    private String reportEngine;
    
    private InputStream reportTemplateStream;
    
    private String reportJsonString;
    
	public String getVersionId() {
		return versionId;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public int getIsActive() {
		return isActive;
	}

	public String getTemplateFileName() {
		return templateFileName;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setReportVersion(int majorVersion) {
		this.reportVersion = majorVersion;
	}

	public String getReportDescription() {
		return description;
	}

	public void setReportDescription(String reportDescription) {
		this.description = reportDescription;
	}

	public InputStream getReportTemplateStream() {
		return reportTemplateStream;
	}

	public void setReportTemplateStream(InputStream reportTemplateStream) {
		this.reportTemplateStream = reportTemplateStream;
	}

	public byte[] getTemplateDefinition() {
        return templateDefinition != null ? Arrays.copyOf(templateDefinition, templateDefinition.length) : null;
	}

	public void setTemplateDefinition(byte[] templateDefinition) {
		if (templateDefinition != null) {
            this.templateDefinition = Arrays.copyOf(templateDefinition, templateDefinition.length);
        }
	}

	public byte[] getCompiledDefinition() {
		return compiledDefinition != null ? Arrays.copyOf(compiledDefinition, compiledDefinition.length) : null;
	}

	public void setCompiledDefinition(byte[] compiledDefinition) {
		if (compiledDefinition != null) {
            this.compiledDefinition = Arrays.copyOf(compiledDefinition, compiledDefinition.length);
        }
	}

	public int getReportVersion() {
		return reportVersion;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	public void setTemplateFileName(String templateFileName) {
		this.templateFileName = templateFileName;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	
	public String getReportEngine() {
		return reportEngine;
	}

	public void setReportEngine(String reportEngine) {
		this.reportEngine = reportEngine;
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
				add("VersionId", versionId).
				add("Name", name).
				add("Description", description).
				add("IsActive", isActive).
				add("TenantId", tenantId).
				add("Id", getId()).
				add("ReportVersion", reportVersion).
				add("ReportType", reportType).
				add("ReportEngine", reportEngine).
				add("TemplateFileName", templateFileName).toString();
	}
}