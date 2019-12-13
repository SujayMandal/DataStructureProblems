package com.ca.umg.report.service.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.ca.framework.core.db.domain.MultiTenantEntity;

@Entity
@Table(name = "MODEL_REPORT_STATUS")
public class ModelReportStatusDefinition extends MultiTenantEntity {

	private static final long serialVersionUID = 1L;

	@Column(name = "REPORT_TEMPLATE_ID")
	private String reportTemplateId;
    
	@Column(name = "UMG_TRANSACTION_ID")
    private String umgTransactionId;
    
	@Column(name = "REPORT_URL")
    private String reportUrl;
    
	@Column(name = "REPORT_LOCATION")
    private String reportLocation;
	
	@Column(name = "REPORT_FILE_NAME")
    private String reportFileName;
    
	@Column(name = "EXECUTION_STATUS")
    private String reportExecutionstatus;

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
	
	public String getReportFileName() {
		return reportFileName;
	}

	public void setReportFileName(String reportFileName) {
		this.reportFileName = reportFileName;
	}

	public String getReportExecutionstatus() {
		return reportExecutionstatus;
	}

	public void setReportExecutionstatus(String reportExecutionstatus) {
		this.reportExecutionstatus = reportExecutionstatus;
	}
}
