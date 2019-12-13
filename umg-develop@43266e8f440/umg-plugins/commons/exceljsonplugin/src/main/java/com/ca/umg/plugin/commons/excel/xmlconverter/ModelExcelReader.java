package com.ca.umg.plugin.commons.excel.xmlconverter;

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Sheet;

import com.ca.framework.core.bo.ModelType;
import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.plugin.commons.excel.xmlconverter.entity.UmgModel;

public interface ModelExcelReader {

	 public UmgModel readSheets(Map<String, Sheet> sheets, List<String> errorList) throws BusinessException;
	 
	 public void setModelType(final ModelType modelType);
}
