package com.ca.umg.plugin.commons.excel.xmlconverter;

import javax.inject.Inject;
import javax.inject.Named;

import com.ca.framework.core.util.ModelLanguages;

@Named
public class ModelExcelReaderFactory {
	@Inject
	private MatlabModelExcelReader matlabModelExcelReader;

	@Inject
	private RModelExcelReader rModelExcelReader;
	
	@Inject
	private ExcelModelExcelReader excelModelExcelReader;

	public ModelExcelReaderFactory() {
		// TODO Auto-generated constructor stub
	}

	public ModelExcelReader getReader(String language) {
		ModelExcelReader reader = null;
		switch (ModelLanguages.valueOf(language)) {
		case MATLAB:
			reader = matlabModelExcelReader;
			break;
		case R:
			reader = rModelExcelReader;
			break;
		case EXCEL:
			reader = excelModelExcelReader;
			break;
		}
		return reader;
	}
}
