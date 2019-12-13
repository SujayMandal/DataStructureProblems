package com.ca.umg.plugin.commons.excel.reader;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.plugin.commons.excel.reader.constants.ExcelDataTypeConstants;
import com.ca.umg.plugin.commons.excel.reader.exception.codes.ExcelPluginExceptionCodes;
import com.ca.umg.plugin.commons.excel.reader.exception.codes.ExceptionCodeParameters;

@Named
public class ReadSheet {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReadSheet.class);
    
    private static final String NULL = "null";

    @Inject
    ExcelReadHelper excelReadHelper;

    @Inject
    ExceptionCodeParameters exceptionCodeParameters;

    /**
     * Read the sheets in excel (except data and header) and return a map of all rows in the sheet key for map will be ID or first
     * column of the sheet and value will be map of all columns
     * 
     * @param sheet
     * @return
     * @throws BusinessException
     */
    public Map<String, Object> readSheet(Sheet sheet) throws BusinessException {
        int rowNum = sheet.getLastRowNum() + 1;
        exceptionCodeParameters.setExceptionCodeParams(null);
        ArrayList<Object> codeParams = null;

        List<String> sheetHeaderList = new ArrayList<>();
        Map<String, Object> rowMap = new HashMap<>();
        Map<String, Object> sheetMap = new HashMap<>();
        List<Map<String, Object>> existingList = null;
        int i = 0;
        try {
            for (i = 0; i < rowNum; i++) {
                Row row = sheet.getRow(i);
                if (i == 0) {
                    sheetHeaderList = createSheetHeaderList(row);
                } else {
                    // create a map for each row with key as id(first column) and value as map of remaining cols of the row
                    rowMap = populateValuesForEachRow(row, sheetHeaderList);
                    Cell firstCellForMapkey = row.getCell(0);
                    String valueOfFirstCell = new DataFormatter().formatCellValue(firstCellForMapkey);
                    if (sheetMap.containsKey(valueOfFirstCell)) {
                        existingList = (List<Map<String, Object>>) sheetMap.get(valueOfFirstCell);
                        existingList.add(rowMap);
                        sheetMap.put(valueOfFirstCell, existingList);
                    } else {
                        List<Map<String, Object>> valueList = new ArrayList<>();
                        valueList.add(rowMap);
                        sheetMap.put(valueOfFirstCell, valueList);
                    }
                }
            }
        } catch (BusinessException e) {
            codeParams = new ArrayList<>();
            if (exceptionCodeParameters.getExceptionCodeParams() != null
                    && !exceptionCodeParameters.getExceptionCodeParams().isEmpty()) {
                codeParams.addAll(exceptionCodeParameters.getExceptionCodeParams());
            }
            codeParams.add(sheet.getSheetName());
            codeParams.add(String.valueOf(i));
            exceptionCodeParameters.setExceptionCodeParams(codeParams);
            throw new BusinessException(e.getCode(), null);
        }
        return sheetMap;
    }

    /**
     * return the list of header from the sheet
     * 
     * @param row
     * @return
     */
    public List<String> createSheetHeaderList(Row row) {
        List<String> sheetHeaderList = new ArrayList<>();
        for (int i = 1; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            sheetHeaderList.add(new DataFormatter().formatCellValue(cell));
        }
        return sheetHeaderList;
    }

    /**
     * Reads the one row of the sheet and returns a map all the columns of that row except the first column i.e id
     * 
     * @param row
     * @param sheetHeaderList
     * @return
     * @throws BusinessException
     */
    public Map<String, Object> populateValuesForEachRow(Row row, List<String> sheetHeaderList) throws BusinessException {
        Map<String, Object> rowMap = new HashMap<>();
        Cell cell = null;
        String dataType = null;
        ArrayList<Object> codeParams = null;
        try {
            for (int i = 1; i <= sheetHeaderList.size(); i++) {
                Cell cellValue = row.getCell(i);
                String arrayType = null;
                Map<String, String> keyAndDataType = excelReadHelper.getKeyAndDataType(sheetHeaderList.get(i - 1));
                String key = keyAndDataType.get("rowKey");
                dataType = keyAndDataType.get("dataType");
                if (keyAndDataType.containsKey("arrayType")) {
                    arrayType = keyAndDataType.get("arrayType");
                }

                if (cellValue == null || StringUtils.isBlank(new DataFormatter().formatCellValue(cellValue))) {
                    rowMap.put(key, null);
                    continue;
                }
                cell = cellValue;
                switch (dataType) {
                case ExcelDataTypeConstants.CELL_TYPE_BOOLEAN:
                	String value = new DataFormatter().formatCellValue(cell); 
                	if(value == null || StringUtils.equalsIgnoreCase(value, "true") || StringUtils.equalsIgnoreCase(value, "false")) {
                		rowMap.put(key, Boolean.valueOf(value));
                	} else {
                		rowMap.put(key, value);
                	}
                    break;
                case ExcelDataTypeConstants.CELL_TYPE_DATE:
                    rowMap.put(key, new DataFormatter().formatCellValue(cell));
                    break;
                case ExcelDataTypeConstants.CELL_TYPE_OBJECT:
                    rowMap.put((String) sheetHeaderList.get(i - 1), new DataFormatter().formatCellValue(cell));
                    break;
                case ExcelDataTypeConstants.CELL_TYPE_STRING:
                	String stringValue = new DataFormatter().formatCellValue(cell);
                	if(stringValue != null && !StringUtils.equals(stringValue, NULL)){
                		rowMap.put(key, new DataFormatter().formatCellValue(cell));
                	} else {
                		rowMap.put(key, null);
                	}
                    break;
                case ExcelDataTypeConstants.CELL_TYPE_INTEGER:
                	String integerValue = new DataFormatter().formatCellValue(cell);
                	if(integerValue != null){
                        rowMap.put(key, Integer.parseInt(new DataFormatter().formatCellValue(cell)));
                	} else {
                		rowMap.put(key, null);
                	}
                    break;
                case ExcelDataTypeConstants.CELL_TYPE_LONG:
                	String longValue = new DataFormatter().formatCellValue(cell);
                	if(longValue != null){
                        rowMap.put(key, Long.parseLong(new DataFormatter().formatCellValue(cell)));
                	} else {
                		 rowMap.put(key,null);
                	}
                    break;
                case ExcelDataTypeConstants.CELL_TYPE_DOUBLE:
                	String doubleValue = new DataFormatter().formatCellValue(cell);
                	if(doubleValue != null){
                    rowMap.put(key, Double.parseDouble(new DataFormatter().formatCellValue(cell)));
                	} else {
                		rowMap.put(key,null);
                	}
                    break;
                case ExcelDataTypeConstants.CELL_TYPE_BIGINTEGER:
                	String bigIntegerValue = new DataFormatter().formatCellValue(cell);
                	if(bigIntegerValue != null){
                    rowMap.put(key, new BigInteger(new DataFormatter().formatCellValue(cell)));
                	} else {
                		 rowMap.put(key,null);
                	}
                    break;
                case ExcelDataTypeConstants.CELL_TYPE_BIGDECIMAL:
                	String bigDecimalrValue = new DataFormatter().formatCellValue(cell);
                	if(bigDecimalrValue != null){
                    rowMap.put(key, new BigDecimal(new DataFormatter().formatCellValue(cell)));
                	} else {
                		 rowMap.put(key,null);
                	}
                    break;
                case ExcelDataTypeConstants.CELL_TYPE_ARRAY:
                    String arrayAsString = new DataFormatter().formatCellValue(cell);
                    Context context = Context.enter();
                    Scriptable scope = context.initStandardObjects();
                    Object result = context.evaluateString(scope, arrayAsString, "<cmd>", 1, null);
                    NativeArray arr = (NativeArray) result;
                    Object[] array = new Object[(int) arr.getLength()];
                    Object[] convertedArray = excelReadHelper.convertToArray(arr, array, arrayType);
                    rowMap.put(key, convertedArray);
                    break;
                }
            }
        } catch (NumberFormatException e) {
            LOGGER.error("NFE error while parsing the value in ReadSheet ", e);
            codeParams = new ArrayList<>();
            codeParams.add(new DataFormatter().formatCellValue(cell));
            codeParams.add(dataType);
            exceptionCodeParameters.setExceptionCodeParams(codeParams);
            throw new BusinessException(ExcelPluginExceptionCodes.EXPL000008, null);
        } catch (Exception e) {
            LOGGER.error("Error while parsing the ReadSheet ", e);
            codeParams = new ArrayList<>();
            codeParams.add(new DataFormatter().formatCellValue(cell));
            codeParams.add(dataType);
            exceptionCodeParameters.setExceptionCodeParams(codeParams);
            throw new BusinessException(ExcelPluginExceptionCodes.EXPL000009, null);
        }
        return rowMap;
    }
}
