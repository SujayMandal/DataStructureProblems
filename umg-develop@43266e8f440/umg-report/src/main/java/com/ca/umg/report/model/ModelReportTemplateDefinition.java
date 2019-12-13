/**
 * 
 */
package com.ca.umg.report.model;

import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotBlank;

import com.ca.framework.core.db.domain.MultiTenantEntity;

@Entity
@Table(name = "MODEL_REPORT_TEMPLATE")
@Audited
public class ModelReportTemplateDefinition extends MultiTenantEntity {

    private static final long serialVersionUID = 2196345324800597758L;

    @NotNull(message = "Version ID cannot be null")
    @NotBlank(message = "Version Id cannot be blank")
//    @OneToOne(orphanRemoval = true)
//    @JoinColumn(name = "UMG_VERSION_ID")
    @Column(name = "UMG_VERSION_ID")
    private String versionId;
    
    
    @Column(name = "NAME")
    private String name;
    
    @Column(name = "DESCRIPTION")
    private String description;
    
//    @NotNull(message = "Active field cannot be null or empty")
//    @NotBlank(message = "Active field cannot be blank")
    @Column(name = "IS_ACTIVE")
    private int isActive;
    
//    @NotNull(message = "Template File Name field cannot be null or empty")
//    @NotBlank(message = "Template File Name cannot be blank")
    @Column(name = "TEMPLATE_FILE_NAME")
    private String templateFileName;

//    @NotNull(message = "IO definition cannot be null.")
    @Column(name = "TEMPLATE_DEFINATION")
    @Lob
    private byte[] templateDefinition;

    @Column(name = "COMPILED_DEFINATION")
    @Lob
    private byte[] compiledDefinition;
    
//    @NotNull(message = "Major Version field cannot be null or empty")
//    @NotBlank(message = "Major Version cannot be blank")
    @Column(name = "MAJOR_VERSION")
    private int reportVersion;

    @Column(name = "REPORT_TYPE")
    private String reportType;
    
    @Column(name = "REPORT_ENGINE")
    private String reportEngine;
    
    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(final String versionId) {
        this.versionId = versionId;
    }

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	public String getTemplateFileName() {
		return templateFileName;
	}

	public void setTemplateFileName(String templateFileName) {
		this.templateFileName = templateFileName;
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

	public void setReportVersion(int reportVersion) {
		this.reportVersion = reportVersion;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}
	
	public String getReportEngine() {
		return reportEngine;
	}

	public void setReportEngine(String reportEngine) {
		this.reportEngine = reportEngine;
	}
	
	/*@Override
	public String toString() {
		return Objects.toStringHelper(this.getClass()).add("Report Name", name).add("Report description", description).add("Report File Name", templateFileName).
				add("Active", isActive).add("Major Version", majorVersion).add("Tenant Id", getTenantId()).add("UMG Version Id", versionId).toString();
	}*/
}