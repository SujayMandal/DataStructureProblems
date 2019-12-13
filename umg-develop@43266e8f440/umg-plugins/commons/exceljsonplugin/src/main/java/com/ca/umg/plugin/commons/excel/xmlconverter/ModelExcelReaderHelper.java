package com.ca.umg.plugin.commons.excel.xmlconverter;

import static com.ca.umg.plugin.commons.excel.xmlconverter.ModelExcelReaderHelper.nativeDataTypeEnum.isValid;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Named;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;

import com.ca.framework.core.ioreduce.DataTypes;
import com.ca.umg.plugin.commons.excel.reader.constants.ExcelConstants;
import com.ca.umg.plugin.commons.excel.xmlconverter.entity.DatatypeValidator;

@Named
public class ModelExcelReaderHelper {
	
	private static final String MINUS_ONE = "-1";
	
	private static final String ONE = "1";
	
	private static final String TWO = "2";
	
	// move below code to frame work
    private static Map<String, String> dateFormatMap = new HashMap<String, String>();

    static {
        dateFormatMap.put("DD-MMM-YYYY", "dd-MMM-yyyy");
        dateFormatMap.put("MMM-DD-YYYY", "MMM-dd-yyyy");
        dateFormatMap.put("MM-DD-YYYY", "MM-dd-yyyy");
        dateFormatMap.put("DD-MM-YYYY", "dd-MM-yyyy");
        dateFormatMap.put("YYYY-MM-DD", "yyyy-MM-dd");
        dateFormatMap.put("YYYY-MMM-DD", "yyyy-MMM-dd");
        dateFormatMap.put("DD/MM/YYYY", "dd/MM/yyyy");
        dateFormatMap.put("DD/MMM/YYYY", "dd/MMM/yyyy");
        dateFormatMap.put("MMM/DD/YYYY", "MMM/dd/yyyy");
        dateFormatMap.put("MM/DD/YYYY", "MM/dd/yyyy");
        dateFormatMap.put("YYYY/MM/DD", "yyyy/MM/dd");
        dateFormatMap.put("YYYY/MMM/DD", "yyyy/MMM/dd");
    }

    public ModelExcelReaderHelper() {
       
    }

    public enum UmgDataTypeEnum {
        STRING, DOUBLE, INTEGER, BOOLEAN, OBJECT, DATE, DATETIME, NUMERIC
    };

    public enum VectorDataTypeEnum {
    	   DOUBLE, INTEGER, STRING, BOOLEAN,  DATE, LONG, BIGINTEGER, BIGDECIMAL
    };

    public enum DataFrameDataTypeEnum {
        // STRING, DOUBLE, INTEGER, BOOLEAN, OBJECT, NUMERIC, DATE
        OBJECT
    };

    public enum MatrixDataTypeEnum {
        DOUBLE, INTEGER, STRING, BOOLEAN,  DATE, LONG, BIGINTEGER, BIGDECIMAL
        // OBJECT
    };

    public enum FactorDataTypeEnum {
    	 STRING
    };
  

    public enum nativeDataTypeEnum {
        CHARACTER("CHARACTER"), DOUBLE("DOUBLE"), INTEGER("INTEGER"), LOGICAL("LOGICAL"), VECTOR("VECTOR"), DATAFRAME(
                "DATA.FRAME"), FACTOR("FACTOR"), MATRIX("MATRIX"), LIST("LIST"), NUMERIC("NUMERIC"),PERCENTAGE("PERCENTAGE"),CURRENCY("CURRENCY");

        private final String value;

        private nativeDataTypeEnum(final String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static boolean isValid(final String value) {
            for (nativeDataTypeEnum enums : values()) {
                if (enums.getValue().equalsIgnoreCase(value)) {
                    return true;
                }
            }

            return false;
        }

    };

    public enum ListDataTypeEnum {
        /* DOUBLE, INTEGER, STRING, BOOLEAN, */
        OBJECT
    };

    public enum nativeComplexDataTypeEnum {
        VECTOR, DATAF_RAME, FACTOR, MATRIX, NUMERIC, LIST, LOGICAL, INTEGER
    };

    public enum CharacterTypeEnum {
        STRING, DATE;
    };

    public enum IntgerDataTypeEnum {
        INTEGER
    };

    public enum NumericDataTypeEnum {
        DOUBLE, INTEGER, LONG, BIGDECIMAL, BIGINTEGER
    };

    private final static String API_NAME_REG_EXP = "[^0-9A-Za-z_.-]+";
    
    private final static String LONG = "LONG";
    
    private final static String BIGINTEGER = "BIGINTEGER";
    
    public void validateLengthColumn(Cell cellValue, String name, List<String> errorList, String sheetName,
    		String defaultValue, String dataType, String length) {
    	if(!errorList.isEmpty() || StringUtils.trimToEmpty(defaultValue)==""){
    		return;
    	}
    	String dimension = StringUtils.trimToEmpty(new DataFormatter().formatCellValue(cellValue));
    	String lengthCopy = StringUtils.trimToEmpty(length);
            if (dimension.equalsIgnoreCase(ONE)) {
            	if(StringUtils.isNumeric(lengthCopy) && DatatypeValidator.isValidInteger(lengthCopy) && (Integer.parseInt(lengthCopy) > 0)) {
            		if(defaultValue.split(",").length > Integer.parseInt(lengthCopy)) {
            			errorList.add("Length mismatch for <b>" + name + "</b> in " + sheetName + " definition. Defined length " + lengthCopy + 
                				", actual length " + defaultValue.split(",").length + ".");
            		}
            	} else if(!lengthCopy.equalsIgnoreCase(MINUS_ONE)) {
            		errorList.add("Length incorrect for <b>" + name + "</b> in " + sheetName + " definition. Length can only be -1 or >=1 "
            				+ "for 1 dimensional arrays.");
            	}
            } else if(dimension.equalsIgnoreCase(TWO)) {
            	Boolean xDimensionMismatch = false;
            	Boolean yDimensionMismatch = false;
            	String[] splitedValue = defaultValue.split("],\\[");
            	String[] splitedLength = lengthCopy.split(",");
            	if((splitedLength.length == 2) && DatatypeValidator.isValidInteger(splitedLength[0].trim()) && StringUtils.isNumeric(splitedLength[1].trim()) 
            			&& DatatypeValidator.isValidInteger(splitedLength[1].trim()) && (Integer.parseInt(splitedLength[1].trim())>=1)) {
            		if((Integer.parseInt(StringUtils.trimToEmpty(splitedLength[0])) <= 0) && !(StringUtils.equalsIgnoreCase(StringUtils.trimToEmpty(splitedLength[0]), MINUS_ONE))){
            			errorList.add("Length incorrect for <b>" + name + "</b> in " + sheetName + " definition. Length must be in row,column format for 2 dimensional arrays. Row value shall be -1 or >=1 and column value shall be >=1.");
            		} else {
            			int i = 0;
            			if((Integer.parseInt(splitedLength[0].trim()) > 0) && (splitedValue.length > Integer.parseInt(splitedLength[0].trim()))) {
            				xDimensionMismatch = true;
            			} 
            			for(i=0; i<splitedValue.length; i++){
            				if(splitedValue[i].split(",").length != Integer.parseInt(splitedLength[1].trim())){
            						if(StringUtils.equals(sheetName,ExcelConstants.OUTPUTS)){
            							if(StringUtils.isNotEmpty(splitedValue[i])){
            								yDimensionMismatch = true;
            								break;
            							}
            						}else{
            							yDimensionMismatch = true;
            							break;
            						}
            				}
            			}
            			if(xDimensionMismatch && yDimensionMismatch) {
            				errorList.add("Row and Column length mismatch for <b>" + name + "</b> in " + sheetName + " definition. Defined row value in length column " 
            			+ splitedLength[0] + ", actual row length from default value " + splitedValue.length + ". Actual row length must be less than or equal to defined length. Defined column value in length column " 
            						+ splitedLength[1] + ", actual column length from default value " + splitedValue[i].split(",").length + ". Actual column length must be equal to defined length.");
            			} else if(xDimensionMismatch) {
            				errorList.add("Row length mismatch for <b>" + name + "</b> in " + sheetName + " definition. Defined row value in length column " 
                        			+ splitedLength[0] + ", actual row length from default value " + splitedValue.length + ". Actual length must be equal <= to defined length.");
            			} else if(yDimensionMismatch) {
            				errorList.add("Column length mismatch for <b>" + name + "</b> in " + sheetName + " definition. Defined column value in length column "
            						+ splitedLength[1] + ", actual column length from default value " + splitedValue[i].split(",").length + ". Actual length must be equal to defined length.");
            			}
            		}
            	} else {
            		errorList.add("Length incorrect for <b>" + name + "</b> in " + sheetName + " definition. Length must be in row,column format for 2 dimensional arrays. Row value shall be -1 or >=1 and column value shall be >=1.");
            	}
            } else if (DataTypes.STRING.equalsIgnoreCase(dataType.trim())) {
            	if(StringUtils.isNumeric(lengthCopy) && DatatypeValidator.isValidInteger(lengthCopy) && (Integer.parseInt(lengthCopy) > 0)) {
            		if(defaultValue.replace("\"","").trim().length() > Integer.parseInt(lengthCopy)) {
            			errorList.add("Length mismatch for <b>" + name + "</b> in " + sheetName + " definition. Defined length " + lengthCopy + 
                				", actual length " + defaultValue.replace("\"","").trim().length() + ".");
            		}
            	} else if(!lengthCopy.isEmpty()) {
            		errorList.add("Length incorrect for <b>" + name + "</b> in " + sheetName + " definition. Length can be blank or "
            				+ ">=1 for primitive string elements.");
            	}
            } else if(!lengthCopy.isEmpty()){
            	errorList.add("Length incorrect for <b>" + name + "</b> in " + sheetName + " definition. Length must be blank "
        				+ "for primitive non string elements.");
            }
    }
    public void validateDimensionsColumn(Cell cellValue, String columnName, String name, List<String> errorList, String sheetName,
    		String defaultValue,String dataType) {
    	if(!errorList.isEmpty()){
    		return;
    	}
    	String cellVal = StringUtils.trimToEmpty(new DataFormatter().formatCellValue(cellValue));
            if (DatatypeValidator.cellIsNullOrEmpty(cellValue)) {
            	if(StringUtils.isNotBlank(defaultValue) && !dataType.equalsIgnoreCase("string") && defaultValue.indexOf('[') != -1){
            		errorList.add("<b>" + name + "</b> defined as primitive but default value is array in " + sheetName + " definition");
            	}
            	if(StringUtils.isNotBlank(defaultValue) && dataType.equalsIgnoreCase("string") && defaultValue.indexOf('[') == 0){
            		errorList.add("<b>" + name + "</b> defined as primitive but default value is array in " + sheetName + " definition");
            	}
            } else if (cellVal.trim().equalsIgnoreCase(ONE)) {
            	if((StringUtils.isNotBlank(defaultValue) && (defaultValue.trim().charAt(0) != '[' || 
            			defaultValue.trim().indexOf('[', 1) != -1)) && !StringUtils.equals("object",dataType)){
            		errorList.add("<b>" + name + "</b> defined as 1 dimensional array but default value is not in correct format in " + sheetName 
            				+ " definition");
            	}
            } else if (cellVal.trim().equalsIgnoreCase(TWO)) {
            	if(StringUtils.equals("object",dataType)){
            		errorList.add("Invalid dimension defined for <b>" + name + "</b> in " + sheetName + " definition"+" Dimension must be 1 for array of objects and blank otherwise.");            		
            	}else if(StringUtils.isNotBlank(defaultValue) && (defaultValue.trim().charAt(0) != '[' || 
            			defaultValue.trim().charAt(1) != '[' || defaultValue.trim().charAt(2) == '[')){
            		errorList.add("<b>" + name + "</b> defined as 2 dimensional array but default value is not in correct format in " + sheetName 
            				+ " definition");
            	}
		    } else if(StringUtils.equals("object",dataType)){
		    		errorList.add("Invalid dimension defined for <b>" + name + "</b> in " + sheetName + " definition"+" Dimension must be 1 for array of objects and blank otherwise.");            		
		    }else {
            	errorList.add("Invalid dimension defined for <b>" + name + "</b> in " + sheetName + " definition");
            }
    }

    public void validateSequenceColumn(Cell cellValue, String columnName, String name, List<String> errorList, String sheetName,
    		Map<String,Integer> parameterSequenceMap, String dataType) {
            if (DatatypeValidator.cellIsNullOrEmpty(cellValue)) {
                errorList.add("No value set to column : " + columnName + " for variable name : <b>" + name + "</b> in : " + sheetName
                        + " sheet");
            } else {
                isPositiveInteger(cellValue, columnName, name, errorList, sheetName);
                isProperSequenceNumber(cellValue, columnName, name.trim(), errorList, sheetName, parameterSequenceMap, dataType.trim());
            }
    }

    private void isProperSequenceNumber(Cell cellValue, String columnName, String name, List<String> errorList,
            String sheetName, Map<String,Integer> parameterSequenceMap, String dataType) {
        String cellVal = new DataFormatter().formatCellValue(cellValue).trim();
        
        // logic to validate sequence numbers for UMG-6863
        if (!DatatypeValidator.cellIsNullOrEmpty(cellValue)) {
        	if (StringUtils.isNumeric(cellVal) && DatatypeValidator.isValidInteger(cellVal)) {

        		if (!StringUtils.contains(name, ExcelConstants.CORR_ID_DOT)){
        			if (parameterSequenceMap.isEmpty()){
        				parameterSequenceMap.put(null, 1);
        			} else  {
        				parameterSequenceMap.put(null, parameterSequenceMap.get(null)+1);
        			} 

        			if (Integer.parseInt(cellVal) != parameterSequenceMap.get(null)) {
        				errorList.add("Sequence Number invalid in " + sheetName + " definition for <b>" + name + "</b>. Expected - "
        						+ parameterSequenceMap.get(null) +" Received - " + cellVal + ".");
        			}
        		} else {
        			String parentName = name.substring(0, name.lastIndexOf(ExcelConstants.CORR_ID_DOT));
        			if(!parameterSequenceMap.containsKey(parentName)){
        				errorList.add("Sequence Number invalid in " + sheetName + " definition for <b>" + name + "</b>. Parent object " 
        			+ parentName + " does not exist.");
        			} else {
        				parameterSequenceMap.put(parentName, parameterSequenceMap.get(parentName)+1);
        				if(Integer.parseInt(cellVal) != parameterSequenceMap.get(parentName)) {
        					errorList.add("Sequence Number invalid in " + sheetName + " definition for <b>" + name + "</b>. Expected - "
            						+ parameterSequenceMap.get(parentName) +" Received - " + cellVal + ".");
        				}
        			}
        		}
        	}
        }	
        if (DataTypes.OBJECT.equalsIgnoreCase(dataType)) {
        	parameterSequenceMap.put(name, 0);
        }
        
    }

    public void validateSpclCharsInName(Cell cellValue, String columnName, String name, String dataType, Set<String> objectNameSet, List<String> errorList, String sheetName) {
        if (DatatypeValidator.cellIsNullOrEmpty(cellValue)) {
            errorList.add("No value set to column : " + columnName + " for variable name : <b>" + name + "</b> in : " + sheetName
                    + " sheet");
        } else {
            String cellVal = StringUtils.trimToEmpty(new DataFormatter().formatCellValue(cellValue));
            Pattern disallowedNamePattern = Pattern.compile(API_NAME_REG_EXP);
            Matcher disallowedNameMatch = disallowedNamePattern.matcher(cellVal);
            if (disallowedNameMatch.find()) {
                errorList.add("Invalid character found in API name <b>" + cellVal + "</b> in " + sheetName + " sheet. Only these characters are allowed in object name - numbers alphabets _ - .");
            } else if(StringUtils.contains(cellVal, ' ')) {
            	errorList.add("Incorrect API Parameter Name for <b>" + cellVal + "</b> in " + sheetName + " sheet. API Parameter name cannot contain spaces.");
            } else if(StringUtils.equalsIgnoreCase(DataTypes.OBJECT, dataType)) {
            	String objectName = null;
            	if(StringUtils.contains(cellVal, ".")) {
            		objectName = StringUtils.substringAfterLast(cellVal, ".");
            	} else {
            		objectName = cellVal;
            	}
            	if(objectName != null) {
            		if(objectNameSet.contains(StringUtils.upperCase(objectName))) {
            			errorList.add("Duplicate object name <b>" + cellVal +"</b> found in the " + sheetName + " sheet.");
            		}
            		objectNameSet.add(StringUtils.upperCase(objectName));
            	}
            }
        }
    }


    public void validateDataType(Cell cellValue, String columnName, String name, List<String> errorList, String sheetName) {
        String cellVal = StringUtils.trimToEmpty(new DataFormatter().formatCellValue(cellValue));
        if (DatatypeValidator.cellIsNullOrEmpty(cellValue)) {
            errorList.add("No value set to column : " + columnName + " for variable name : <b>" + name + "</b> in : " + sheetName
                    + " sheet");
        } else if (!(EnumUtils.isValidEnum(UmgDataTypeEnum.class, cellVal.toUpperCase()))) {
            errorList.add("The value set to column : " + columnName + " for variable name : <b>" + name + "</b> in : " + sheetName
                    + " sheet is not valid datatype");
        }
    }

    public void validateNativeDataType(Cell cellValue, String columnName, String name, List<String> errorList, String sheetName) {
        String cellVal = StringUtils.trimToEmpty(new DataFormatter().formatCellValue(cellValue));
        if (DatatypeValidator.cellIsNullOrEmpty(cellValue)) {
            errorList.add("No value set to column : " + columnName + " for variable name : <b>" + name + "</b> in : " + sheetName
                    + " sheet");
        } else if (!(isValid(cellVal.toUpperCase()))) {
            errorList.add("The value set to column : " + columnName + " for variable name : <b>" + name + "</b> in : " + sheetName
                    + " sheet is not valid datatype");
        }
    }

    public void checkNativeAndUmgDataTypeAssociation(Row dataRow, List<String> errorList, String sheetName) {
        String name = new DataFormatter().formatCellValue(dataRow.getCell(1));
        Cell dataTypeCell = dataRow.getCell(6);
        Cell NativedataTypeCell = dataRow.getCell(7);
        String dataTypeCellVal = StringUtils.trimToEmpty(new DataFormatter().formatCellValue(dataTypeCell));
        String nativedataTypeCellVal = StringUtils.trimToEmpty(new DataFormatter().formatCellValue(NativedataTypeCell));
        if(ExcelConstants.OUTPUTS.equals(sheetName)){
        	if(LONG.equalsIgnoreCase(dataTypeCellVal) || BIGINTEGER.equalsIgnoreCase(dataTypeCellVal) ){
        		errorList.add("RA Datatype incorrect for <b>"+name+"</b> in Ouput definition. To prevent loss of precision use Double or Big Decimal datatypes for large numbers.");
        	}
        	
        }
        if (!DatatypeValidator.cellIsNullOrEmpty(NativedataTypeCell) && nativeDataTypeEnum.isValid(nativedataTypeCellVal.toUpperCase())) {
            Map<String, String> rDataTypeMap = DataTypeMapUtil.getDataTypeMap().get(ExcelConstants.R_LANG);
            switch (nativedataTypeCellVal.toLowerCase(Locale.ENGLISH)) {
            case DataTypeMapUtil.DATATYPE_MATRIX:
                if (!DatatypeValidator.cellIsNullOrEmptyWithMsg(dataTypeCell, errorList, sheetName, name)
                        && !(EnumUtils.isValidEnum(MatrixDataTypeEnum.class, dataTypeCellVal.toUpperCase()))) {
                    allowedDataTypeErrMsg(errorList, sheetName, name, nativedataTypeCellVal, rDataTypeMap);
                }             
                break;
            case DataTypeMapUtil.DATATYPE_VECTOR:
                if (!DatatypeValidator.cellIsNullOrEmptyWithMsg(dataTypeCell, errorList, sheetName, name)
                        && !(EnumUtils.isValidEnum(VectorDataTypeEnum.class, dataTypeCellVal.toUpperCase()))) {
                    allowedDataTypeErrMsg(errorList, sheetName, name, nativedataTypeCellVal, rDataTypeMap);
                }
                if (!checkIfSingleDimArray(dataRow.getCell(11))) {
                    errorList.add("Vector is a one dimension array. "
                            + "Expecting one dimension in dimensions column for variable name : <b>" + name + "</b> in : " + sheetName
                            + " sheet");
                }
                break;
            case DataTypeMapUtil.DATATYPE_FACTOR:
                if (DatatypeValidator.cellIsNullOrEmpty(dataTypeCell)) {
                    emptyDataTypeValueCheck(errorList, sheetName, name);
                } else if (!(EnumUtils.isValidEnum(FactorDataTypeEnum.class, dataTypeCellVal.toUpperCase()))) {
                    allowedDataTypeErrMsg(errorList, sheetName, name, nativedataTypeCellVal, rDataTypeMap);
                }
              /*  if (!checkIfSingleDimArray(dataRow.getCell(10))) {
                    errorList.add("Factor is a one dimension array. "
                            + "Expecting one dimension in dimensions column for variable name : " + name + " in : " + sheetName
                            + " sheet");
                }*/
                break;
            case DataTypeMapUtil.DATATYPE_DATAFRAME:
                if (DatatypeValidator.cellIsNullOrEmpty(dataTypeCell)) {
                    emptyDataTypeValueCheck(errorList, sheetName, name);
                } else if (!EnumUtils.isValidEnum(DataFrameDataTypeEnum.class, dataTypeCellVal.toUpperCase())) {
                    allowedDataTypeErrMsg(errorList, sheetName, name, nativedataTypeCellVal, rDataTypeMap);
                }
                break;
            case DataTypeMapUtil.DATATYPE_LIST:
                if (DatatypeValidator.cellIsNullOrEmpty(dataTypeCell)) {
                    emptyDataTypeValueCheck(errorList, sheetName, name);
                } else if (!(EnumUtils.isValidEnum(ListDataTypeEnum.class, dataTypeCellVal.toUpperCase()))) {
                    allowedDataTypeErrMsg(errorList, sheetName, name, nativedataTypeCellVal, rDataTypeMap);
                }
                break;
            case DataTypeMapUtil.DATATYPE_CHARACTER:
                if (DatatypeValidator.cellIsNullOrEmpty(dataTypeCell)) {
                    emptyDataTypeValueCheck(errorList, sheetName, name);
                } else if (!EnumUtils.isValidEnum(CharacterTypeEnum.class, dataTypeCellVal.toUpperCase())) {
                    allowedDataTypeErrMsg(errorList, sheetName, name, nativedataTypeCellVal, rDataTypeMap);
                }
                break;
            case DataTypeMapUtil.DATATYPE_LOGICAL:
                if (DatatypeValidator.cellIsNullOrEmpty(dataTypeCell)) {
                    emptyDataTypeValueCheck(errorList, sheetName, name);
                } else if (!StringUtils.equalsIgnoreCase("boolean", dataTypeCellVal.toUpperCase())) {
                    allowedDataTypeErrMsg(errorList, sheetName, name, nativedataTypeCellVal, rDataTypeMap);
                }
                break;
            case DataTypeMapUtil.DATATYPE_INTEGER:
                if (DatatypeValidator.cellIsNullOrEmpty(dataTypeCell)) {
                    emptyDataTypeValueCheck(errorList, sheetName, name);
                } else if (!EnumUtils.isValidEnum(IntgerDataTypeEnum.class, dataTypeCellVal.toUpperCase())) {
                    allowedDataTypeErrMsg(errorList, sheetName, name, nativedataTypeCellVal, rDataTypeMap);
                }
                break;
                    
            case DataTypeMapUtil.DATATYPE_NUMERIC:
                if (DatatypeValidator.cellIsNullOrEmpty(dataTypeCell)) {
                    emptyDataTypeValueCheck(errorList, sheetName, name);
                } else if (!EnumUtils.isValidEnum(NumericDataTypeEnum.class, dataTypeCellVal.toUpperCase())) {
                    allowedDataTypeErrMsg(errorList, sheetName, name, nativedataTypeCellVal, rDataTypeMap);
                }
                break;
            }
        }
    }

    private void emptyDataTypeValueCheck(List<String> errorList, String sheetName, String name) {
        errorList.add("No value set to column : datatype for variable name : <b>" + name + "</b> in : " + sheetName + " sheet");
    }

    private void allowedDataTypeErrMsg(List<String> errorList, String sheetName, String name, String nativedataTypeCellVal,
            Map<String, String> rDataTypeMap) {
        errorList.add("Allowed values for datatype column are : '" + rDataTypeMap.get(nativedataTypeCellVal.toLowerCase(Locale.ENGLISH))
                + "' , when native datype is : " + nativedataTypeCellVal + " , for variable name : <b>" + name + "</b> in : "
                + sheetName + " sheet");
    }

    public Boolean checkIfTwoDimArray(Cell cellValue) {
        Boolean isMultiDimArray = Boolean.TRUE;
        if (DatatypeValidator.cellIsNullOrEmpty(cellValue)) {
            isMultiDimArray = Boolean.FALSE;
        }
        String dimensionCellVal = new DataFormatter().formatCellValue(cellValue);
        Integer length = dimensionCellVal.split(ExcelConstants.CORR_ID_COMMA).length;
        if (length != 2) {
            isMultiDimArray = Boolean.FALSE;
        }
        return isMultiDimArray;
    }

    private Boolean checkIfSingleDimArray(Cell cellValue) {
        Boolean isMultiDimArray = Boolean.TRUE;
        if (DatatypeValidator.cellIsNullOrEmpty(cellValue)) {
            isMultiDimArray = Boolean.FALSE;
        }
        String dimensionCellVal = new DataFormatter().formatCellValue(cellValue);
        Integer length = dimensionCellVal.split(ExcelConstants.CORR_ID_COMMA).length;
        if (length != 1) {
            isMultiDimArray = Boolean.FALSE;
        }
        return isMultiDimArray;
    }


    public void validateLengthAndPrecsn(Row dataRow, Row headerRow, String sheetName, List<String> errorList) {
        Cell precisionCellValue = dataRow.getCell(9);
        String name = new DataFormatter().formatCellValue(dataRow.getCell(1));
        isPositiveInteger(precisionCellValue, new DataFormatter().formatCellValue(headerRow.getCell(9)), name, errorList,
                sheetName);
    }

    private void isPositiveInteger(Cell cellValue, String columnName, String name, List<String> errorList, String sheetName) {
        String cellVal = new DataFormatter().formatCellValue(cellValue);
        if (!DatatypeValidator.cellIsNullOrEmpty(cellValue)) {
            if (!StringUtils.isNumeric(cellVal)) {
                errorList.add("The value set to column : " + columnName + " for variable name : <b>" + name + "</b> in : " + sheetName
                        + " sheet is not a valid positive integer");
            } else {
                if (DatatypeValidator.isValidInteger(cellVal) && Integer.parseInt(cellVal) < 1) {
                    errorList.add("The value set to column : " + columnName + " for variable name : <b>" + name + "</b> in : "
                            + sheetName + " sheet should be greater than zero");
                }
            }
        }
    }


    public Boolean rowEmpty(Row row) {
        Boolean isEmptyRow = Boolean.FALSE;
        if (row == null) {
            isEmptyRow = Boolean.TRUE;
        } else if (DatatypeValidator.cellIsNullOrEmpty(row.getCell(1)) && DatatypeValidator.cellIsNullOrEmpty(row.getCell(4)) && DatatypeValidator.cellIsNullOrEmpty(row.getCell(6))) {
            isEmptyRow = Boolean.TRUE;
        }
        return isEmptyRow;
    }

    public Boolean objectHasChild(Row childRow, String parent, String sheetName, List<String> errorList) {
        Boolean hasChild = Boolean.TRUE;
        Cell childCellValue = childRow.getCell(1);
        String childName = new DataFormatter().formatCellValue(childCellValue);
        if (DatatypeValidator.cellIsNullOrEmpty(childCellValue)) {
            errorList.add("Unable to find a child in next row for variable name : <b>" + parent + "</b> in : " + sheetName + " sheet");
            hasChild = Boolean.FALSE;
        } else {
            String childsparent = StringUtils.substringBeforeLast(childName, ExcelConstants.CORR_ID_DOT);
            if (!StringUtils.equals(childsparent, parent)) {
                errorList.add("Unable to find a child in next row for variable name : <b>" + parent + "</b> in : " + sheetName
                        + " sheet");
                hasChild = Boolean.FALSE;
            }
        }
        return hasChild;
    }


    public void validateDateFormat(Row headerRow, Row dataRow, List<String> errorList, String sheetName) {
        Cell cellValue = dataRow.getCell(6);
        String dataType = StringUtils.trimToEmpty(new DataFormatter().formatCellValue(cellValue));

        if (StringUtils.equals(dataType, DataTypeMapUtil.DATATYPE_DATE) && StringUtils.equalsIgnoreCase(sheetName, "inputs")) {
            String columnName = new DataFormatter().formatCellValue(headerRow.getCell(10));
            String name = new DataFormatter().formatCellValue(dataRow.getCell(1));
            Cell dateFormatCell = dataRow.getCell(10);
            String dateFormat = new DataFormatter().formatCellValue(dateFormatCell);
            if (DatatypeValidator.cellIsNullOrEmpty(dateFormatCell)) {
                errorList.add("No value set to column : " + columnName + " for variable name : <b>" + name + "</b> in : " + sheetName
                        + " sheet");
            } else if (!dateFormatMap.containsKey(dateFormat)) {
                errorList.add("The value set to column : " + columnName + " for variable name : <b>" + name + "</b> in : " + sheetName
                        + " sheet is not valid date format");
            }
        }
    }

	public void validateDataTypesForMatlab(Row dataRow, Row headerRow, String sheetName, List<String> errorList, Cell dataType, Cell nativeDataType) {
		String columnName = new DataFormatter().formatCellValue(dataRow.getCell(1));
		String dataTypeCellValue = StringUtils.trimToEmpty(new DataFormatter().formatCellValue(dataType));
        String nativeDataTypeCellValue = StringUtils.trimToEmpty(new DataFormatter().formatCellValue(nativeDataType));
        Boolean flag = false;
		if (StringUtils.equals(nativeDataTypeCellValue, DataTypeMapUtil.DATATYPE_MWNUMERICARRAY)) {
			if (!(StringUtils.equals(dataTypeCellValue, DataTypeMapUtil.DATATYPE_INTEGER)
					|| StringUtils.equals(dataTypeCellValue, DataTypeMapUtil.DATATYPE_BIGINTEGER)
					|| StringUtils.equals(dataTypeCellValue, DataTypeMapUtil.DATATYPE_DOUBLE) || StringUtils.equals(dataTypeCellValue, DataTypeMapUtil.DATATYPE_LONG)
					|| StringUtils.equals(dataTypeCellValue, DataTypeMapUtil.DATATYPE_BIGDECIMAL))) {
				flag = true;
			}
		} else if (StringUtils.equals(nativeDataTypeCellValue, DataTypeMapUtil.DATATYPE_MWCHARARRAY)) {
			if (!(StringUtils.equals(dataTypeCellValue, DataTypeMapUtil.DATATYPE_STRING))) {
				flag = true;
			}
		} else if (StringUtils.equals(nativeDataTypeCellValue, DataTypeMapUtil.DATATYPE_MWLOGICALARRAY)) {
			if (!StringUtils.equals(dataTypeCellValue, DataTypeMapUtil.DATATYPE_BOOLEAN)) {
				flag = true;
			}
		} else if (StringUtils.equals(nativeDataTypeCellValue, DataTypeMapUtil.DATATYPE_MWSTRUCTARRAY)) {
			if (!StringUtils.equals(dataTypeCellValue, DataTypeMapUtil.DATATYPE_OBJECT)) {
				flag = true;
			}
		} else {
			flag = true;
		}
		
		if(flag){
			errorList.add("Invalid datatype definition for <b>" + columnName + "</b> in " + sheetName + " definition."
                    + " Native Data type is " + nativeDataTypeCellValue + " and RA data type is " + dataTypeCellValue);
		}
	}
}