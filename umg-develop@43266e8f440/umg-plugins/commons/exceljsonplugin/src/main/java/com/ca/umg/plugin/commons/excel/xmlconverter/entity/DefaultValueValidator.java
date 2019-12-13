package com.ca.umg.plugin.commons.excel.xmlconverter.entity;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Scriptable;

import com.ca.umg.plugin.commons.excel.reader.ExcelReadHelper;
import com.ca.umg.plugin.commons.excel.reader.constants.ExcelConstants;

@Named
public class DefaultValueValidator extends DatatypeValidator{
	
	private static final String DATATYPE_MISMATCH_ERR_MSG = "Invalid default value defined for <b>%s</b> in %s definition. Default value must be %s for RA Data type %s. Default value received is %s";												
	
    @Inject
    ExcelReadHelper excelReadHelper;

	public void validateMandateAndDefaultValues(Row dataRow, Row headerRow, String sheetName, List<String> errorList,
			Cell dataTypeCellValue) {
		Cell mandatoryCellValue = dataRow.getCell(4);
		Cell acceptableValues = dataRow.getCell(12);
		Cell defaultCellValue = dataRow.getCell(13);
		String mandCel = StringUtils.trimToEmpty(new DataFormatter().formatCellValue(mandatoryCellValue));
		String dataType = StringUtils.trimToEmpty(new DataFormatter().formatCellValue(dataTypeCellValue));
		String columnName = new DataFormatter().formatCellValue(headerRow.getCell(13));
		String name = new DataFormatter().formatCellValue(dataRow.getCell(1));	
		Object[] convertedArray = null;
		if(!cellIsNullOrEmpty(acceptableValues) && StringUtils.equals(ExcelConstants.INPUTS,sheetName)){
			try{
			   String acceptableValue = new DataFormatter().formatCellValue(acceptableValues);	
			   Context context = Context.enter();
			   Scriptable scope = context.initStandardObjects();
			   Object result = context.evaluateString(scope, acceptableValue, "<cmd>", 1, null);
               NativeArray arr = (NativeArray) result;
               Object[] array = new Object[(int) arr.getLength()];
               convertedArray   = excelReadHelper.convertToArray(arr, array, dataType);              
			}catch (NumberFormatException e) {
				errorList.add(String.format("acceptable_values for API Parameter <b>%s</b>  in %s sheet must be defined as array. e.g. [true,false], [\"1\",\"2\",\"3\"], [1,2,3]" ,
						name, sheetName));	        	
	        } catch (Exception e) {
	        	errorList.add(String.format("acceptable_values for API Parameter <b>%s</b>  in %s sheet must be defined as array. e.g. [true,false], [\"1\",\"2\",\"3\"], [1,2,3]" ,
						name, sheetName));
	        }
		}
		if (!cellIsNullOrEmpty(defaultCellValue) && "object".equals(dataType)) {
			errorList.add(String.format(
					"Invalid default value defined for <b>%s</b>  in %s definition. Default value must be Blank for RA Data type Object. Default value received is %s",
					name, sheetName, defaultCellValue));
		} else if (ExcelConstants.INPUTS.equalsIgnoreCase(sheetName) && !cellIsNullOrEmpty(mandatoryCellValue) && Boolean.valueOf(mandCel)) {			
			String defaultValue = new DataFormatter().formatCellValue(defaultCellValue);			
			if (cellIsNullOrEmpty(defaultCellValue)
					&& !"object".equals(new DataFormatter().formatCellValue(dataTypeCellValue))) {
				errorList.add("Default value not defined for mandatory field <b>" + name + "</b> in " + ExcelConstants.INPUTS + " definition");
			} else if (cellIsNullOrEmpty(dataRow.getCell(11))) {
				validatePrimitiveValue(sheetName, errorList, defaultCellValue, dataType, columnName, name, convertedArray,DATATYPE_MISMATCH_ERR_MSG);
			} else {
				validateArrayValue(sheetName, errorList, dataTypeCellValue, dataType, columnName,
						name, defaultValue,convertedArray,DATATYPE_MISMATCH_ERR_MSG);
			}
		} else {
			String defaultValue = new DataFormatter().formatCellValue(defaultCellValue);
			if (StringUtils.isNotBlank(defaultValue)) {
				if (cellIsNullOrEmpty(dataRow.getCell(11))) {
					validatePrimitiveValue(sheetName, errorList, defaultCellValue, dataType, columnName, name,convertedArray,DATATYPE_MISMATCH_ERR_MSG);

				} else {
					validateArrayValue(sheetName, errorList, dataTypeCellValue, dataType,
							columnName, name, defaultValue,convertedArray,DATATYPE_MISMATCH_ERR_MSG);
				}
			}
		}
	}

}
