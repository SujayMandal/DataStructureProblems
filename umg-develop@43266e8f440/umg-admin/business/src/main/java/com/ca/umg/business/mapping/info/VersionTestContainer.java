package com.ca.umg.business.mapping.info;

import java.util.List;

public class VersionTestContainer {
    private String tidName;
    private String modelName;
    private int majorVersion;
    private int minorVersion;
    private String asOnDate;
    private String versionId;
    private List<TidIoDefinition> tidIoDefinitions;
    private List<String> defaultValuesList;
    private List<String> additionalPropsList;
    private Boolean generateReport;
    private Boolean hasReportTemplate;
    private Boolean hasModelOpValidation;  
    private Boolean payloadStorage;
    private Boolean hasAcceptableValuesValidation;
    private Boolean storeRLogs;
	public String getTidName() {
        return tidName;
    }

    public void setTidName(String tidName) {
        this.tidName = tidName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public void setMajorVersion(int majorVersion) {
        this.majorVersion = majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public void setMinorVersion(int minorVersion) {
        this.minorVersion = minorVersion;
    }

    public String getAsOnDate() {
        return asOnDate;
    }

    public void setAsOnDate(String asOnDate) {
        this.asOnDate = asOnDate;
    }

    public List<TidIoDefinition> getTidIoDefinitions() {
        return tidIoDefinitions;
    }

    public void setTidIoDefinitions(List<TidIoDefinition> tidIoDefinitions) {
        this.tidIoDefinitions = tidIoDefinitions;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public List<String> getDefaultValuesList() {
        return defaultValuesList;
    }

    public void setDefaultValuesList(List<String> defaultValuesList) {
        this.defaultValuesList = defaultValuesList;
    }

    public List<String> getAdditionalPropsList() {
        return additionalPropsList;
    }

    public void setAdditionalPropsList(List<String> additionalPropsList) {
        this.additionalPropsList = additionalPropsList;
    }
    
    public Boolean getGenerateReport() {
    	return generateReport;
    }
    
    public void setGenerateReport(final Boolean generateReport) {
    	this.generateReport = generateReport;
    }

	public Boolean getHasReportTemplate() {
		return hasReportTemplate;
	}

	public void setHasReportTemplate(Boolean hasReportTemplate) {
		this.hasReportTemplate = hasReportTemplate;
	}
	
	public Boolean getHasModelOpValidation() {
		return hasModelOpValidation;
	}
	public void setHasModelOpValidation(Boolean hasModelOpValidation) {
		this.hasModelOpValidation = hasModelOpValidation;
	}
	
	public Boolean getPayloadStorage() {
		return payloadStorage;
	}

	public void setPayloadStorage(Boolean payloadStorage) {
		this.payloadStorage = payloadStorage;
	}

	public Boolean getHasAcceptableValuesValidation() {
		return hasAcceptableValuesValidation;
	}

	public void setHasAcceptableValuesValidation(Boolean hasAcceptableValuesValidation) {
		this.hasAcceptableValuesValidation = hasAcceptableValuesValidation;
	}

	public Boolean getStoreRLogs() {
		return storeRLogs;
	}

	public void setStoreRLogs(Boolean storeRLogs) {
		this.storeRLogs = storeRLogs;
	}
	
}
