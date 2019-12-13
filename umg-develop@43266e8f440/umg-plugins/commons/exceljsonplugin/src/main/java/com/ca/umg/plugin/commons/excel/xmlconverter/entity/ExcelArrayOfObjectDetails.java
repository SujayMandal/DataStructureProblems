package com.ca.umg.plugin.commons.excel.xmlconverter.entity;

public class ExcelArrayOfObjectDetails {
	
	private boolean arrayOfObjectFlag;
	private String objectApiName;
	private String objectLength;
	private String memberArrayDirection;
	
	public boolean isArrayOfObjectFlag() {
		return arrayOfObjectFlag;
	}
	public void setArrayOfObjectFlag(boolean arrayOfObjectFlag) {
		this.arrayOfObjectFlag = arrayOfObjectFlag;
	}
	public String getObjectApiName() {
		return objectApiName;
	}
	public void setObjectApiName(String objectApiName) {
		this.objectApiName = objectApiName;
	}
	public String getObjectLength() {
		return objectLength;
	}
	public void setObjectLength(String objectLength) {
		this.objectLength = objectLength;
	}
	public String getMemberArrayDirection() {
		return memberArrayDirection;
	}
	public void setMemberArrayDirection(String memberArrayDirection) {
		this.memberArrayDirection = memberArrayDirection;
	}
	
}
