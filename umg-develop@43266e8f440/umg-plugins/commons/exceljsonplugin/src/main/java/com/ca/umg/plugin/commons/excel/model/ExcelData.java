package com.ca.umg.plugin.commons.excel.model;

import java.util.List;
import java.util.Map;

public class ExcelData {
	
	private List<Map<String, Object>> excelData;
	
	private byte[] modifiedExcel;
	
	private Map<String,Object> headerDetails;

	public List<Map<String, Object>> getExcelData() {
		return excelData;
	}

	public void setExcelData(List<Map<String, Object>> excelData) {
		this.excelData = excelData;
	}

	public byte[] getModifiedExcel() {
		return modifiedExcel;
	}

	public void setModifiedExcel(byte[] modifiedExcel) {
		this.modifiedExcel = modifiedExcel;
	}
	
	public void setHeaderDetails(final Map<String,Object> headerDetails) {
		this.headerDetails = headerDetails;
	}
	
	public Map<String,Object> getHeaderDetails() {
		return headerDetails;
	}

}
