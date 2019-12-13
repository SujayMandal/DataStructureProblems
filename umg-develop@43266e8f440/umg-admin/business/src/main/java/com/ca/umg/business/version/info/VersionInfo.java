package com.ca.umg.business.version.info;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;

import com.ca.umg.business.common.info.PagingInfo;
import com.ca.umg.business.mapping.info.MappingInfo;
import com.ca.umg.business.mapping.info.TestBedOutputInfo;
import com.ca.umg.business.model.info.ModelLibraryInfo;
import com.ca.umg.report.model.ModelReportTemplateInfo;
import com.ca.umg.report.model.ReportInfo;

@SuppressWarnings({"PMD.TooManyFields", "PMD.ExcessivePublicCount"})
public class VersionInfo extends PagingInfo {

    private static final long serialVersionUID = 1167220806789278953L;

    @NotEmpty(message = "Tenant model name cannot be empty")
    @Size(max = 50, message = "Tenant model name can be maximum 50 characters")
    private String name;

    @NotEmpty(message = "Tenant model description cannot be empty")
    @Size(max = 200, message = "Tenant model description can be maximum 200 characters")
    private String description;

    private Integer majorVersion;

    private Integer minorVersion;

    @Valid
    @NotNull(message = "Mapping cannot be empty")
    private MappingInfo mapping;

    @Valid
    @NotNull(message = "Model library cannot be empty")
    private ModelLibraryInfo modelLibrary;

    private String status;

    @NotEmpty(message = "Version description cannot be empty")
    @Size(max = 200, message = "Version description can be maximum 200 characters")
    private String versionDescription;

    private DateTime publishedOn;

    private String publishedBy;

    private DateTime deactivatedOn;

    private String deactivatedBy;

    private byte[] testExcel;

    private String sampleTestInput;

    private TestBedOutputInfo testBedOutputInfo;
    
    private String deactivatedDateTime;
    
    private String publishedDateTime;
    
    private String umgTransactionId;

    private boolean existingLibrary;
    
    private ModelReportTemplateInfo reportTemplateInfo;
    
    private ReportInfo reportInfo;
    
    private Boolean hasReportTemplate;

    private String reportTemplateName;
    
    private String jarName;
    
    private String manifestName;
    
    private String modelType;
    
    private DateTime requestedOn;

    private String requestedBy;
    
    private int emailApproval;
    
    private String requestedDateTime;
    
    private String approver;
    
    private String executionLanguage;
    
    private String clientID;

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

    public Integer getMajorVersion() {
        return majorVersion;
    }

    public void setMajorVersion(Integer majorVersion) {
        this.majorVersion = majorVersion;
    }

    public Integer getMinorVersion() {
        return minorVersion;
    }

    public void setMinorVersion(Integer minorVersion) {
        this.minorVersion = minorVersion;
    }

    public MappingInfo getMapping() {
        return mapping;
    }

    public void setMapping(MappingInfo mapping) {
        this.mapping = mapping;
    }

    public ModelLibraryInfo getModelLibrary() {
        return modelLibrary;
    }

    public void setModelLibrary(ModelLibraryInfo modelLibrary) {
        this.modelLibrary = modelLibrary;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVersionDescription() {
        return versionDescription;
    }

    public void setVersionDescription(String versionDescription) {
        this.versionDescription = versionDescription;
    }

    public DateTime getPublishedOn() {
        return publishedOn;
    }

    public void setPublishedOn(DateTime publishedOn) {
        this.publishedOn = publishedOn;
    }

    public String getPublishedBy() {
        return publishedBy;
    }

    public void setPublishedBy(String publishedBy) {
        this.publishedBy = publishedBy;
    }

    public DateTime getDeactivatedOn() {
        return deactivatedOn;
    }

    public void setDeactivatedOn(DateTime deactivatedOn) {
        this.deactivatedOn = deactivatedOn;
    }

    public String getDeactivatedBy() {
        return deactivatedBy;
    }

    public void setDeactivatedBy(String deactivatedBy) {
        this.deactivatedBy = deactivatedBy;
    }

    public byte[] getTestExcel() {
        return testExcel;
    }

    public void setTestExcel(byte[] testExcel) {
        this.testExcel = testExcel;
    }

    public String getSampleTestInput() {
        return sampleTestInput;
    }

    public void setSampleTestInput(String sampleTestInput) {
        this.sampleTestInput = sampleTestInput;
    }

    public TestBedOutputInfo getTestBedOutputInfo() {
        return testBedOutputInfo;
    }

    public void setTestBedOutputInfo(TestBedOutputInfo testBedOutputInfo) {
        this.testBedOutputInfo = testBedOutputInfo;
    }

	public String getDeactivatedDateTime() {
		return deactivatedDateTime;
	}

	public void setDeactivatedDateTime(String deactivatedDateTime) {
		this.deactivatedDateTime = deactivatedDateTime;
	}

	public String getPublishedDateTime() {
		return publishedDateTime;
	}

	public void setPublishedDateTime(String publishedDateTime) {
		this.publishedDateTime = publishedDateTime;
	}

	public String getUmgTransactionId() {
		return umgTransactionId;
	}

	public void setUmgTransactionId(String umgTransactionId) {
		this.umgTransactionId = umgTransactionId;
	}

    public boolean isExistingLibrary() {
        return existingLibrary;
    }

    public void setExistingLibrary(boolean existingLibrary) {
        this.existingLibrary = existingLibrary;
    }

    public void setReportTemplateInfo(final ModelReportTemplateInfo reportTemplateInfo) {
    	this.reportTemplateInfo = reportTemplateInfo;
    }
    
    public ModelReportTemplateInfo getReportTemplateInfo() {
    	return reportTemplateInfo;
    }
    
    public void setReportInfo(final ReportInfo reportInfo) {
    	this.reportInfo = reportInfo;
    }
    
    public ReportInfo getReportInfo() {
    	return reportInfo;
    }
    
    public Boolean getHasReportTemplate() {
		return hasReportTemplate;
	}

	public void setHasReportTemplate(Boolean hasReportTemplate) {
		this.hasReportTemplate = hasReportTemplate;
	}
	
	public String getReportTemplateName() {
		return reportTemplateName;
	}
	
	public void setReportTemplateName(final String reportTemplateName) {
		this.reportTemplateName = reportTemplateName;
	}

	public String getJarName() {
		return jarName;
	}

	public void setJarName(String jarName) {
		this.jarName = jarName;
	}

	public String getManifestName() {
		return manifestName;
	}

	public void setManifestName(String manifestName) {
		this.manifestName = manifestName;
	}

	public String getModelType() {
		return modelType;
	}

	public void setModelType(String modelType) {
		this.modelType = modelType;
	}

	public DateTime getRequestedOn() {
		return requestedOn;
	}

	public void setRequestedOn(DateTime requestedOn) {
		this.requestedOn = requestedOn;
	}

	public String getRequestedBy() {
		return requestedBy;
	}

	public void setRequestedBy(String requestedBy) {
		this.requestedBy = requestedBy;
	}

	public int getEmailApproval() {
		return emailApproval;
	}

	public void setEmailApproval(int emailApproval) {
		this.emailApproval = emailApproval;
	}

	public String getRequestedDateTime() {
		return requestedDateTime;
	}

	public void setRequestedDateTime(String requestedDateTime) {
		this.requestedDateTime = requestedDateTime;
	}

	public String getApprover() {
		return approver;
	}

	public void setApprover(String approver) {
		this.approver = approver;
	}

	public String getExecutionLanguage() {
		return executionLanguage;
	}

	public void setExecutionLanguage(String executionLanguage) {
		this.executionLanguage = executionLanguage;
	}

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }
	
}