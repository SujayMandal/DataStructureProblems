package com.ca.umg.plugin.commons.excel.xmlconverter;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;

import com.ca.umg.plugin.commons.excel.xmlconverter.entity.DatatypeValidator;
import com.ca.umg.plugin.commons.excel.xmlconverter.entity.ExcelArrayOfObjectDetails;

@Named
public class ExcelModelHelper extends ModelExcelReaderHelper {

	private final static String REG_EXP_ACROSS_SHEET = ".+\\*[A-Z]{1,2}\\d+:.+\\*[A-Z]{1,2}\\d+";

	private final static String REG_EXP_PRIMITIVE = ".+\\*[A-Z]{1,2}\\d+";

	private final static String REG_EXP_NAMED = "N\\[.+\\*.+\\]";

	private final static String REG_EXP_ARRAY = ".+\\*[A-Z]{1,2}\\d+:[A-Z]{1,2}\\d+";

	private final static String REG_EXP_EXCEL_COLUMN = "[A-Z]{1,2}";

	private final static String REG_EXP_EXCEL_ROW = "\\d+";

	private final static String REG_EXP_DATE = "\"\\d{4}-\\d\\d-\\d\\d\"";

	private final static String REG_EXP_DATE_TIME = "\"\\d{4}-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d\"";

	private static final String MINUS_ONE = "-1";

	private static final String ONE = "1";

	private static final String TWO = "2";

	private static final String ROW_DIRECTION = "ROW";

	private static final String COLUMN_DIRECTION = "COLUMN";

	private static final String INPUTS = "INPUTS";

	private static final String OUTPUTS = "OUTPUTS";

	private static Map<String, String> dateFormatMap = new HashMap<String, String>();

	static {
		dateFormatMap.put("YYYY-MM-DD", "yyyy-MM-dd");
	}

	private static Map<String, String> dateTimeFormatMap = new HashMap<String, String>();

	static {
		dateTimeFormatMap.put("YYYY-MM-DD HH:MM:SS", "yyyy-MM-dd HH:mm:ss");
	}

	private enum excelDataTypeEnum {
		DOUBLE("DOUBLE"), INTEGER("INTEGER"), STRING("STRING"), BOOLEAN("BOOLEAN"), OBJECT("OBJECT"), DATE("DATE"), DATETIME("DATETIME"), CURRENCY("CURRENCY"), PERCENTAGE("PERCENTAGE"), LONG("LONG");

		private final String value;

		private excelDataTypeEnum(final String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public static boolean isValidDataType(final String value) {
			for (excelDataTypeEnum enums : values()) {
				if (enums.getValue().equalsIgnoreCase(value)) {
					return true;
				}
			}

			return false;
		}

	};

	public void validateDateFormat(Row headerRow, Row dataRow, List<String> errorList, String sheetName) {
		if(!errorList.isEmpty()) {
			return;
		}
		List<String> dateList = new ArrayList<>();
		Cell cellValue = dataRow.getCell(6);
		String dataType = StringUtils.trimToEmpty(new DataFormatter().formatCellValue(cellValue));
		if(!StringUtils.equalsIgnoreCase(dataType, DataTypeMapUtil.DATATYPE_DATE) && !StringUtils.equalsIgnoreCase(dataType, DataTypeMapUtil.DATATYPE_DATETIME)) {
			return;
		}
		String apiName = new DataFormatter().formatCellValue(dataRow.getCell(1));
		String date = StringUtils.trimToEmpty(new DataFormatter().formatCellValue(dataRow.getCell(13))); 
		Pattern dateRegPattern = Pattern.compile(REG_EXP_DATE);
		Matcher dateMatcher = dateRegPattern.matcher(date);
		while(dateMatcher.find()) {
			String tempString = dateMatcher.group();
			dateList.add(tempString);
			date = StringUtils.remove(date, tempString);
		}
		Pattern dateTimeRegPattern = Pattern.compile(REG_EXP_DATE_TIME);
		Matcher dateTimeMatcher = dateTimeRegPattern.matcher(date);
		while(dateTimeMatcher.find()) {
			String tempString = dateTimeMatcher.group();
			dateList.add(tempString);
			date = StringUtils.remove(date, tempString);
		}
		date = StringUtils.remove(date, '[');
		date = StringUtils.remove(date, ']');
		date = StringUtils.remove(date, ',');
		date = StringUtils.trimToEmpty(date);
		if(!StringUtils.isEmpty(date)){
			if(StringUtils.contains(date, '"')) {
				errorList.add("Default value is not a valid " + dataType + " in the pattern defined for API parameter <b>" + apiName + "</b> in " + sheetName + " sheet.");
				return;
			} else {
				errorList.add("Default value must be within \"\" for " + dataType + " field for API parameter <b>" + apiName  + "</b> in " + sheetName + " sheet." );
				return;
			}
		}
		Cell patternCell = dataRow.getCell(10);
		String pattern = StringUtils.trimToEmpty(new DataFormatter().formatCellValue(patternCell));
		if (DatatypeValidator.cellIsNullOrEmpty(patternCell)) {
			errorList.add("Pattern must be defined for " + dataType + " datatype for API parameter <b>" + apiName + "</b> in " + sheetName + " sheet.");
			return;
		} else if (StringUtils.equalsIgnoreCase(dataType, DataTypeMapUtil.DATATYPE_DATE)) {
			if (!dateFormatMap.containsKey(pattern)) {
				errorList.add("Incorrect Pattern defined for API parameter <b>" + apiName + "</b> in the " + sheetName + " sheet. Only YYYY-MM-DD pattern is supported for " + dataType + " datatype");
				return;
			}
		} else if (StringUtils.equalsIgnoreCase(dataType, DataTypeMapUtil.DATATYPE_DATETIME)) {
			if (!dateTimeFormatMap.containsKey(pattern)) {
				errorList.add("Incorrect Pattern defined for API parameter <b>" + apiName + "</b> in the " + sheetName + " sheet. Only YYYY-MM-DD HH:MM:SS pattern is supported for " + dataType + " datatype");
				return;
			}
		}
		for(String dateString : dateList) {
			if (StringUtils.equalsIgnoreCase(dataType, DataTypeMapUtil.DATATYPE_DATE)) {
				if (StringUtils.equalsIgnoreCase(INPUTS, sheetName)) {
					dateString = StringUtils.trimToNull(dateString);
					if(dateString != null && !isValidDate(StringUtils.remove(dateString, '"'), dateFormatMap.get(pattern))) {
						errorList.add("Default value is not a valid date in the pattern defined for API parameter <b>" + apiName + "</b> in " + sheetName + " sheet.");
						return;
					}
				}
			} else if (StringUtils.equalsIgnoreCase(dataType, DataTypeMapUtil.DATATYPE_DATETIME)) {
				if (StringUtils.equalsIgnoreCase(INPUTS, sheetName)) {
					dateString = StringUtils.trimToNull(dateString);
					if(dateString != null && !isValidDate(StringUtils.remove(dateString, '"'), dateTimeFormatMap.get(pattern))) {
						errorList.add("Default value is not a valid datetime in the pattern defined for API parameter <b>" + apiName + "</b> in " + sheetName + " sheet.");
						return;
					}
				}
			}
		}
	}

	private static boolean isValidDate(String dateString, String datePattern) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
		dateFormat.setLenient(false);
		if(StringUtils.length(dateString) != StringUtils.length(datePattern)) {
			return false;
		}
		try {
			dateFormat.parse(dateString);
		} catch (ParseException pe) {
			return false;
		}
		return true;
	}

	public void validateModelParamName(Cell cellValue, String columnName, String apiName, List<String> errorList, String sheetName, String dataType, Set<String> modelParamNameSet) {
		if(StringUtils.equalsIgnoreCase(DataTypeMapUtil.DATATYPE_OBJECT, dataType)) {
			if (!DatatypeValidator.cellIsNullOrEmpty(cellValue)) {
				errorList.add("Model Parameter Name must be blank for object API Name <b>" + apiName +"</b> in " + sheetName + " Sheet.");
			}
		} else {
			if (DatatypeValidator.cellIsNullOrEmpty(cellValue)) {
				errorList.add("Model Parameter Name missing for API Name <b>" + apiName +"</b> in " + sheetName + " Sheet.");
			} else {
				String cellVal = new DataFormatter().formatCellValue(cellValue);
				cellVal = StringUtils.trimToEmpty(cellVal);
				if (modelParamNameSet.contains(cellVal)) {
					errorList.add("Duplicate Model Parameter Name <b>" + cellValue + "</b> found in " + sheetName + " Sheet.");
				} else {
					Pattern acrossSheetPattern = Pattern.compile(REG_EXP_ACROSS_SHEET);
					Pattern primitivePattern = Pattern.compile(REG_EXP_PRIMITIVE);
					Pattern namedPattern = Pattern.compile(REG_EXP_NAMED);
					Pattern arrayPattern = Pattern.compile(REG_EXP_ARRAY);
					Matcher acrossSheetMatch = acrossSheetPattern.matcher(cellVal);
					if(acrossSheetMatch.find()) {
						errorList.add("Model Parameter Name definition incorrect for array API Name <b>" + apiName +"</b> in " + sheetName + " Sheet. Start and End cell number must be in the same sheet. e.g. Sheet1*A1:C6");
					} else {
						Matcher primitiveMatch = primitivePattern.matcher(cellVal);
						Matcher primitiveNamedMatch = namedPattern.matcher(cellVal);
						if(primitiveMatch.find() && StringUtils.equals(primitiveMatch.group(), cellVal)) {
							modelParamNameSet.add(cellVal);
						} else if(primitiveNamedMatch.find() && StringUtils.equals(primitiveNamedMatch.group(), cellVal)) {
							modelParamNameSet.add(cellVal);
						} else {
							Matcher arrayMatch = arrayPattern.matcher(cellVal);
							if(arrayMatch.find() && StringUtils.equals(arrayMatch.group(), cellVal)) {
								modelParamNameSet.add(cellVal);
							} else {
								errorList.add("Incorrect modelParameterName format for <b>" + apiName +"</b> in " + sheetName + ". Correct format for location based mapping - sheet name*field location (e.g. input*B1). Correct format for name based mapping N[sheet name*field name] (e.g. N[input*address]).");
							}
						}
					}
				}
			}
		}
	}

	public void validateModelParamNameDimension(Cell cellValue, String columnName, String apiName, List<String> errorList, String sheetName, String dataType, String modelParamName, String length, ExcelArrayOfObjectDetails excelArrayOfObjectDetails) {
		if(!errorList.isEmpty()){
			return;
		}
		String cellVal = new DataFormatter().formatCellValue(cellValue);
		cellVal = StringUtils.trimToEmpty(cellVal);
		if(excelArrayOfObjectDetails.getObjectApiName() != null && !StringUtils.equalsIgnoreCase(StringUtils.substringBeforeLast(apiName, "."),excelArrayOfObjectDetails.getObjectApiName())) {
			excelArrayOfObjectDetails.setArrayOfObjectFlag(Boolean.FALSE);
			excelArrayOfObjectDetails.setObjectApiName(null);
			excelArrayOfObjectDetails.setObjectLength(null);
			excelArrayOfObjectDetails.setMemberArrayDirection(null);
		}
		if(StringUtils.equalsIgnoreCase(excelDataTypeEnum.OBJECT.getValue(), dataType) && StringUtils.equalsIgnoreCase(MINUS_ONE, cellVal)) {
			errorList.add("Invalid Dimension defined for <b>" + apiName +"</b> in " + sheetName);		
		}
		if(!DatatypeValidator.cellIsNullOrEmpty(cellValue) && excelArrayOfObjectDetails.isArrayOfObjectFlag()) {
			errorList.add("Invalid Dimension defined for <b>" + apiName +"</b> in " + sheetName + " definition. Dimension must be blank for all parameters within the object when the object is defined as array.");
		}
		if(StringUtils.equalsIgnoreCase(excelDataTypeEnum.OBJECT.getValue(), dataType) && StringUtils.equalsIgnoreCase(ONE, cellVal)) {
			excelArrayOfObjectDetails.setArrayOfObjectFlag(Boolean.TRUE);
			excelArrayOfObjectDetails.setObjectApiName(apiName);
			excelArrayOfObjectDetails.setObjectLength(length);
			if(StringUtils.isBlank(length) || StringUtils.equalsIgnoreCase(MINUS_ONE, length)) {
				errorList.add("Invalid length defined for <b>" + apiName +"</b> in " + sheetName + " definition. Length must be >= 1 when dimension is 1 for an object.");
			}
		}
		if ((DatatypeValidator.cellIsNullOrEmpty(cellValue) && !excelArrayOfObjectDetails.isArrayOfObjectFlag()) || (excelArrayOfObjectDetails.isArrayOfObjectFlag() && StringUtils.equalsIgnoreCase(ONE, excelArrayOfObjectDetails.getObjectLength()))) {
			Pattern arrayPattern = Pattern.compile(REG_EXP_ARRAY);
			Matcher arrayMatch = arrayPattern.matcher(cellVal);
			if((arrayMatch.find() && StringUtils.equals(arrayMatch.group(), cellVal))) {
				errorList.add("Model Parameter Name is defined as array for non-array element for API Name <b>" + apiName +"</b> in " + sheetName + " Sheet.");
			}
			if(!StringUtils.isBlank(modelParamName) && StringUtils.substringBetween(modelParamName, "*", ":")!=null){				
				errorList.add("Model Parameter Name is defined as array for non-array element for API Name <b>" + apiName +"</b> in " + sheetName + " Sheet.");				
			}
		} else if(!StringUtils.isBlank(modelParamName)) {
			Pattern arrayNamedPattern = Pattern.compile(REG_EXP_NAMED);
			Matcher arrayNamedMatch = arrayNamedPattern.matcher(modelParamName);
			if(!(arrayNamedMatch.find() && StringUtils.equals(arrayNamedMatch.group(), modelParamName))) {
				String startCell = StringUtils.substringBetween(modelParamName, "*", ":");
				if(startCell==null){
					errorList.add("Model Parameter Name is not defined as array for array element for API Name <b>" + apiName +"</b> in " + sheetName + " Sheet.");				
				}else {			
					String EndCell = StringUtils.split(modelParamName, ":")[1];
					Pattern excelColumnPattern = Pattern.compile(REG_EXP_EXCEL_COLUMN);
					Pattern excelRowPattern = Pattern.compile(REG_EXP_EXCEL_ROW);
					Matcher startCellColumnMatcher = excelColumnPattern.matcher(startCell);
					startCellColumnMatcher.find();
					String startCellColumnChar = startCellColumnMatcher.group();
					Matcher startCellRowMatcher = excelRowPattern.matcher(startCell);
					startCellRowMatcher.find();
					int startCellRow = Integer.parseInt(startCellRowMatcher.group());
					Matcher endCellColumnMatcher = excelColumnPattern.matcher(EndCell);
					endCellColumnMatcher.find();
					String endCellColumnChar = endCellColumnMatcher.group();
					Matcher endCellRowMatcher = excelRowPattern.matcher(EndCell);
					endCellRowMatcher.find();
					int endCellRow = Integer.parseInt(endCellRowMatcher.group());
					int startCellColumn;
					int endCellColumn;
					if(startCellColumnChar.length()==2) {
						startCellColumn = (((int)startCellColumnChar.charAt(0)-65)+1)*26 + ((int)startCellColumnChar.charAt(1)-65);
					} else {
						startCellColumn = (int)startCellColumnChar.charAt(0)-65;
					}
					if(endCellColumnChar.length()==2) {
						endCellColumn = (((int)endCellColumnChar.charAt(0)-65)+1)*26 + ((int)endCellColumnChar.charAt(1)-65);
					} else {
						endCellColumn = (int)endCellColumnChar.charAt(0)-65;
					}
					if((startCellColumn<endCellColumn && startCellRow<endCellRow) || (startCellColumn==endCellColumn && startCellRow<endCellRow) || (startCellColumn<endCellColumn && startCellRow==endCellRow)) {
						if ((!excelArrayOfObjectDetails.isArrayOfObjectFlag() && StringUtils.equalsIgnoreCase(ONE,cellVal)) || excelArrayOfObjectDetails.isArrayOfObjectFlag() ) {
							if(startCellColumn!=endCellColumn && startCellRow!=endCellRow) {
								if(!excelArrayOfObjectDetails.isArrayOfObjectFlag()) {
									errorList.add("Model Parameter Name definition incorrect for 1D array API Name <b>" + apiName +"</b> in " + sheetName + " Sheet. Either row or column must be same for start and end cell number. e.g. Sheet1*A1:C1");
								} else {
									errorList.add("Invalid Model Parameter Name for <b>" + apiName +"</b> in " + sheetName + " definition. Model parameter name should be defined as array for all parameter within a Object that is defined as array e.g. Sheet1*A1:C1 or Sheet1*A1:A6");
								}
							} else {
								String modelParamLength = "" + (endCellColumn - startCellColumn + endCellRow - startCellRow + 1) ;	
								if(!(DatatypeValidator.cellIsNullOrEmpty(cellValue)) && StringUtils.equalsIgnoreCase(MINUS_ONE, length)) {
									errorList.add("Infinite length arrays are not supported for Excel Models. Incorrect length for API Name <b>" + apiName +"</b> in " + sheetName + " Sheet.");
								}else if(!excelArrayOfObjectDetails.isArrayOfObjectFlag() && !StringUtils.equalsIgnoreCase(length, modelParamLength)) {
									errorList.add("Incorrect length defined for API Name <b>" + apiName +"</b> in " + sheetName + " Sheet. Defined length = "+ length + ", actual length " + modelParamLength);
								} else if(excelArrayOfObjectDetails.isArrayOfObjectFlag() && StringUtils.isNotBlank(excelArrayOfObjectDetails.getObjectLength()) && !StringUtils.equalsIgnoreCase(excelArrayOfObjectDetails.getObjectLength(), modelParamLength)) {
									errorList.add("Incorrect length defined for API Name <b>" + apiName +"</b> in " + sheetName + " Sheet. Defined length = "+ excelArrayOfObjectDetails.getObjectLength() + ", actual length " + modelParamLength);
								} 
								if(excelArrayOfObjectDetails.getMemberArrayDirection() == null && excelArrayOfObjectDetails.isArrayOfObjectFlag()) {
									if((endCellColumn - startCellColumn) == 0) {
										excelArrayOfObjectDetails.setMemberArrayDirection(COLUMN_DIRECTION);
									} else {
										excelArrayOfObjectDetails.setMemberArrayDirection(ROW_DIRECTION);
									}
								} else if(excelArrayOfObjectDetails.isArrayOfObjectFlag()) {
									if((((endCellColumn - startCellColumn) == 0) && StringUtils.equalsIgnoreCase(excelArrayOfObjectDetails.getMemberArrayDirection(), ROW_DIRECTION)) || (((endCellRow - startCellRow) == 0) && StringUtils.equalsIgnoreCase(excelArrayOfObjectDetails.getMemberArrayDirection(), COLUMN_DIRECTION))) {
										errorList.add("Invalid Model Parameter Name for <b>" + apiName +"</b> in " + sheetName + " definition. All cell definitions within the object must be either row wise or column wise when the object is defined as array e.g. Sheet1*A1:C1 or Sheet1*A1:A6");
									}
								}
							}
						} else if (!excelArrayOfObjectDetails.isArrayOfObjectFlag() && StringUtils.equalsIgnoreCase(TWO,cellVal)) {	            	
							if(startCellColumn==endCellColumn || startCellRow==endCellRow) {
								errorList.add("Model Parameter Name definition incorrect for 2D array API Name <b>" + apiName +"</b> in " + sheetName + " Sheet. Both row or column must be different for start and end cell number. e.g. Sheet1*A1:C6");
							} else {
								String modelParamLength = "" + (endCellRow - startCellRow + 1) + "," + (endCellColumn - startCellColumn + 1);
								if(!(DatatypeValidator.cellIsNullOrEmpty(cellValue)) && StringUtils.equalsIgnoreCase(MINUS_ONE, length)) {
									errorList.add("Infinite length arrays are not supported for Excel Models. Incorrect length for API Name <b>" + apiName +"</b> in " + sheetName + " Sheet.");
								}else if(!StringUtils.equalsIgnoreCase(length, modelParamLength)) {
									errorList.add("Incorrect length defined for API Name <b>" + apiName +"</b> in " + sheetName + " Sheet. Defined length = "+ length + ", actual length " + modelParamLength);
								}
							}
						}


					} else {
						errorList.add("Model Parameter Name definition incorrect for array API Name <b>" + apiName +"</b> in " + sheetName + " Sheet. Start Cell number should be less than End cell number.");
					}							
				}
			}
		}
	}

	public void validateDataType(Cell cellValue, String columnName, String apiName, List<String> errorList, String sheetName) {
		String cellVal = StringUtils.trimToEmpty(new DataFormatter().formatCellValue(cellValue));
		if (DatatypeValidator.cellIsNullOrEmpty(cellValue)) {
			errorList.add("No value set to column : " + columnName + " for variable name : <b>" + apiName + "</b> in : " + sheetName
					+ " sheet");
		} else if (!(excelDataTypeEnum.isValidDataType(cellVal.toUpperCase()))) {
			errorList.add("The value set to column : " + columnName + " for variable name : <b>" + apiName + "</b> in : " + sheetName
					+ " sheet is not valid datatype");
		}
	}

	public void validateNativeDataType(Cell cellValue, String columnName, String apiName, List<String> errorList, String sheetName, String dataType, String pattern) {
		String cellVal = StringUtils.trimToEmpty(new DataFormatter().formatCellValue(cellValue));
		if(StringUtils.equalsIgnoreCase(cellVal,excelDataTypeEnum.CURRENCY.getValue()) || StringUtils.equalsIgnoreCase(cellVal,excelDataTypeEnum.PERCENTAGE.getValue())) {
			if(!StringUtils.equalsIgnoreCase(dataType,excelDataTypeEnum.DOUBLE.getValue()) && !StringUtils.equalsIgnoreCase(dataType,excelDataTypeEnum.INTEGER.getValue())) {
				errorList.add("Incorrect RA datatype for API Parameter <b>" + apiName + "</b> in " + sheetName + " sheet. Only " + excelDataTypeEnum.INTEGER.getValue() +", " + excelDataTypeEnum.DOUBLE.getValue()  + " datatypes are supported for Native datatype " + cellVal + " for Excel Models");
			}
		} else if(StringUtils.equalsIgnoreCase(cellVal,excelDataTypeEnum.INTEGER.getValue()) && StringUtils.equalsIgnoreCase(OUTPUTS, sheetName)) { 
			if(!StringUtils.equalsIgnoreCase(dataType,excelDataTypeEnum.INTEGER.getValue()) && !StringUtils.equalsIgnoreCase(dataType,excelDataTypeEnum.DOUBLE.getValue())) {
				errorList.add("Incorrect RA datatype for API Parameter <b>" + apiName + "</b> in " + sheetName + " sheet. Only " + excelDataTypeEnum.INTEGER.getValue() +", " + excelDataTypeEnum.DOUBLE.getValue()  + " datatypes are supported for Native datatype " + cellVal + " for Excel Models");
			}
		}  else if(StringUtils.equalsIgnoreCase(cellVal,excelDataTypeEnum.LONG.getValue()) && StringUtils.equalsIgnoreCase(INPUTS, sheetName)) { 
			if(!StringUtils.equalsIgnoreCase(dataType,excelDataTypeEnum.INTEGER.getValue()) && !StringUtils.equalsIgnoreCase(dataType,excelDataTypeEnum.LONG.getValue())) {
				errorList.add("Incorrect RA datatype for API Parameter <b>" + apiName + "</b> in " + sheetName + " sheet. Only " + excelDataTypeEnum.INTEGER.getValue() +", " + excelDataTypeEnum.LONG.getValue()  + " datatypes are supported for Native datatype " + cellVal + " for Excel Models");
			}
		}  else if(StringUtils.equalsIgnoreCase(cellVal,excelDataTypeEnum.DOUBLE.getValue()) && StringUtils.equalsIgnoreCase(INPUTS, sheetName)) { 
			if(!StringUtils.equalsIgnoreCase(dataType,excelDataTypeEnum.INTEGER.getValue()) && !StringUtils.equalsIgnoreCase(dataType,excelDataTypeEnum.DOUBLE.getValue())) {
				errorList.add("Incorrect RA datatype for API Parameter <b>" + apiName + "</b> in " + sheetName + " sheet. Only " + excelDataTypeEnum.INTEGER.getValue() +", " + excelDataTypeEnum.DOUBLE.getValue()  + " datatypes are supported for Native datatype " + cellVal + " for Excel Models");
			}
		} else if(!(excelDataTypeEnum.isValidDataType(cellVal.toUpperCase()))) { 
			errorList.add("Incorrect native datatype for API Parameter <b>" + apiName + "</b> in " + sheetName + " sheet. Native dataypes supported for Excel models are " + excelDataTypeEnum.STRING.getValue() + ", " + excelDataTypeEnum.INTEGER.getValue() + ", " + excelDataTypeEnum.LONG.getValue() + ", " +
					excelDataTypeEnum.DOUBLE.getValue() + ", " + excelDataTypeEnum.DATE.getValue() + ", " + excelDataTypeEnum.DATETIME.getValue() + ", " + excelDataTypeEnum.PERCENTAGE.getValue() + ", " + excelDataTypeEnum.CURRENCY.getValue() + ", " + excelDataTypeEnum.OBJECT.getValue() + ", " + excelDataTypeEnum.BOOLEAN.getValue());
		} else if(!StringUtils.equalsIgnoreCase(cellVal, dataType)) {
			errorList.add("Incorrect RA datatype for API Parameter <b>" + apiName + "</b> in " + sheetName + " sheet. Only " + cellVal  + " datatype is supported for Native datatype " + cellVal + " for Excel Models");
		}
	}

	public void validatePrecision(Cell cellValue, String columnName, String apiName, List<String> errorList, String sheetName,
			String dataType, String nativeDataType) {
		String cellVal = StringUtils.trimToEmpty(new DataFormatter().formatCellValue(cellValue));
		if(StringUtils.isEmpty(cellVal)) { 
			if (StringUtils.equalsIgnoreCase(dataType, excelDataTypeEnum.DOUBLE.getValue()) && 
					(StringUtils.equalsIgnoreCase(nativeDataType, excelDataTypeEnum.CURRENCY.getValue()) || StringUtils.equalsIgnoreCase(nativeDataType, excelDataTypeEnum.PERCENTAGE.getValue()))) {
				errorList.add("Precision not defined for API Parameter name <b>" + apiName + "</b> in " + sheetName + " sheet. Precision must be defined for percentage and currency datatypes when RA datatype is double.");
			}
		} else {
			try {
				Integer precision = Integer.parseInt(cellVal);
				if(!(precision>=1 && precision<=4)) {
					throw new NumberFormatException();
				}
			} catch (NumberFormatException ex) {
				errorList.add("Incorrect precision for API Parameter name <b>" + apiName + "</b> in " + sheetName + " sheet. Precision must be between 1 and 4");
			}
		}
	}

	public void validateValuesPrecision(Row dataRow, List<String> errorList, String sheetName) {
		String defaultValue = null;
		String acceptableValue = null;
		String apiName = new DataFormatter().formatCellValue(dataRow.getCell(1));
		String precisionValue = new DataFormatter().formatCellValue(dataRow.getCell(9));
		int precision = Integer.valueOf(precisionValue);
		if(StringUtils.equalsIgnoreCase(INPUTS, sheetName)) {
			defaultValue = new DataFormatter().formatCellValue(dataRow.getCell(13));
			acceptableValue = new DataFormatter().formatCellValue(dataRow.getCell(12));
			String[] acceptableValues = StringUtils.split(acceptableValue, ',');
			for(String value : acceptableValues) {
				value = StringUtils.trimToEmpty(StringUtils.remove(StringUtils.remove(value, ']'), '['));
				if(StringUtils.substringAfter(value, ".").length()>precision) {
					errorList.add("Incorrect acceptable value defined for API Parameter name <b>" + apiName + "</b> in " + sheetName + " sheet. All acceptable values must be in accordance with the defined precision. Precision defined " + precision + " , acceptable values " + acceptableValue);
					break;
				}
			}
		} else {
			defaultValue = new DataFormatter().formatCellValue(dataRow.getCell(12));
		}
		String[] defaultValues = StringUtils.split(defaultValue, ',');
		for(String value : defaultValues) {
			value = StringUtils.trimToEmpty(StringUtils.remove(StringUtils.remove(value, ']'), '['));
			if(StringUtils.substringAfter(value, ".").length()>precision) {
				errorList.add("Incorrect default value defined for API Parameter name <b>"+ apiName + "</b> in " + sheetName + " sheet. Default value must be in accordance with the defined precision. Precision defined " + precision + " , default value " + defaultValue);
				break;
			}
		}
	}

}