package com.ca.umg.plugin.commons.excel.xmlconverter.entity;

import java.util.List;

import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;

import com.ca.umg.plugin.commons.excel.reader.constants.ExcelConstants;
import com.ca.umg.plugin.commons.excel.xmlconverter.DataTypeMapUtil;

@Named
public class AcceptableValueValidator extends DatatypeValidator{
	private static final String DATATYPE_MISMATCH_ERR_MSG = "Invalid acceptable value defined for <b>%s</b> in %s definition. Acceptable value must be %s for RA Data type %s. Default value received is %s";

	public void validateAcceptableValues(Row dataRow, Row headerRow, String sheetName, List<String> errorList,
			Cell dataTypeCellValue) {	
		Cell accpetableCellVal = dataRow.getCell(12);	
		String dataType = StringUtils.trimToEmpty(new DataFormatter().formatCellValue(dataTypeCellValue));
		String columnName = new DataFormatter().formatCellValue(headerRow.getCell(12));
		String name = new DataFormatter().formatCellValue(dataRow.getCell(1));		
		if (!cellIsNullOrEmpty(accpetableCellVal) && DataTypeMapUtil.DATATYPE_OBJECT.equalsIgnoreCase(dataType)) {			
			errorList.add(String.format("Acceptable values must be blank for Objects. Invalid acceptable_value definition for API Parameter <b>%s</b> in %s sheet",
					name, sheetName));
		} else if (ExcelConstants.INPUTS.equalsIgnoreCase(sheetName)) {
			if(!cellIsNullOrEmpty(accpetableCellVal)){
				String accpetableCellValue = new DataFormatter().formatCellValue(accpetableCellVal);	
						validateArrayValue(sheetName, errorList, dataTypeCellValue,  dataType,
								columnName, name, accpetableCellValue, null,DATATYPE_MISMATCH_ERR_MSG);		
			}
			
		}
	}

}
