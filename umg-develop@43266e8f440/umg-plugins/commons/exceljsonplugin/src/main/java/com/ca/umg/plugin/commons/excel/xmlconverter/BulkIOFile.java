package com.ca.umg.plugin.commons.excel.xmlconverter;

import java.util.Objects;

public class BulkIOFile {
	private Integer sequence;
	private String name;
    private String apiName;
    private String modelParamName;
	private String description;
	private boolean mandatoryFlag;
	private boolean syndicate;
	private String dataType;
	private String nativeDataType;
	private String length;
	private String precession;
	private String pattern;
	private String dimensions;
	private String defaultValue;

	
	public Integer getSequence() {
		return sequence;
	}
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
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
	public boolean isMandatoryFlag() {
		return mandatoryFlag;
	}
	public void setMandatoryFlag(boolean mandatoryFlag) {
		this.mandatoryFlag = mandatoryFlag;
	}
	public boolean isSyndicate() {
		return syndicate;
	}
	public void setSyndicate(boolean syndicate) {
		this.syndicate = syndicate;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getNativeDataType() {
		return nativeDataType;
	}
	public void setNativeDataType(String nativeDataType) {
		this.nativeDataType = nativeDataType;
	}
	public String getLength() {
		return length;
	}
	public void setLength(String length) {
		this.length = length;
	}
	public String getPrecession() {
		return precession;
	}
	public void setPrecession(String precession) {
		this.precession = precession;
	}
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	public String getDimensions() {
		return dimensions;
	}
	public void setDimensions(String dimensions) {
		this.dimensions = dimensions;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public String getApiName() {
		return apiName;
	}
	public void setApiName(String apiName) {
		this.apiName = apiName;
	}
	public String getModelParamName() {
		return modelParamName;
	}
	public void setModelParamName(String modelParamName) {
		this.modelParamName = modelParamName;
	}
	
	public int hashCode() {
        return Objects.hash(sequence, dataType, dimensions, length, name, nativeDataType, mandatoryFlag);
    }
	
	public boolean equals(Object obj) {
		BulkIOFile dto = (BulkIOFile)obj ;
		if(dto.getSequence() !=null && dto.getDataType() !=null && 
		   dto.getDimensions()!=null &&	dto.getLength() !=null &&
		   dto.getName()!=null && dto.getNativeDataType()!=null 
		   //&& dto.getPattern() !=null && dto.getPrecession()!=null
		   ){
		if(dto.getSequence() == this.sequence && 
				dto.getDataType().equals(this.dataType) && 
				// dto.getDefaultValue().equals(this.defaultValue) && 
				dto.getDimensions().equals(this.dimensions)&& 
				dto.getLength().equals(this.length) && 
				dto.getName().equals(this.name) && 
				dto.getNativeDataType().equals(this.nativeDataType) && 
				//dto.getPattern().equals(this.pattern) && 
				//dto.getPrecession().equals(this.precession) && 
				this.mandatoryFlag == dto.isMandatoryFlag()){
				//this.syndicate == dto.isSyndicate()){
			return true;
		}
		}
		return false;

	}
}
