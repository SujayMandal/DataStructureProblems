package com.ca.umg.plugin.commons.excel.reader;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import com.ca.umg.plugin.commons.excel.reader.constants.ExcelConstants;
import com.ca.umg.plugin.commons.excel.reader.constants.ExcelDataTypeConstants;
import com.ca.umg.plugin.commons.excel.reader.exception.codes.ExcelPluginExceptionCodes;
import com.ca.umg.plugin.commons.excel.reader.exception.codes.ExceptionCodeParameters;

@Named
public class ReadDataSheet {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReadDataSheet.class);

    @Inject
    ExcelReadHelper excelReadHelper;
    
    @Inject
    ExceptionCodeParameters exceptionCodeParameters;

    /**
     * Reads the data sheet and returns list<Map> of all rows
     * 
     * @param sheet
     * @return
     * @throws BusinessException
     */
    public List<Map<String, Object>> readSheet(Sheet sheet) throws BusinessException {
        exceptionCodeParameters.setExceptionCodeParams(null);
        //short correlationColumnIndex = getCorrelationColumnIndex(sheet);
        int rowNum = sheet.getLastRowNum() + 1;
        if (rowNum < 2) {
            BusinessException.raiseBusinessException(ExcelPluginExceptionCodes.EXPL000003,null);
        }
        List<String> sheetHeaderList = new ArrayList<>();
        Map<String, Object> rowMap = new HashMap<>();
        List<Map<String, Object>> dataList = new ArrayList<>();
        ArrayList<Object> codeParams = null;
        int i = 0;
        for (i = 0; i < rowNum; i++) {
            Row row = sheet.getRow(i);
            try {
                if (row == null) {
                    throw new BusinessException(ExcelPluginExceptionCodes.EXPL000016, exceptionCodeParameters.getExceptionCodeParamsAsArray());
                }
                if (i == 0) {
                    sheetHeaderList = createSheetHeaderList(row);
                } else {            
                    rowMap = populateValuesForEachRow(row, sheetHeaderList);
                    rowMap.put(ExcelConstants.ROW_NO, i);
                    dataList.add(rowMap);
                }
            } catch (BusinessException e) {
                codeParams = new ArrayList<>();
                if (exceptionCodeParameters.getExceptionCodeParams() != null && !exceptionCodeParameters.getExceptionCodeParams().isEmpty()) {
                    codeParams.addAll(exceptionCodeParameters.getExceptionCodeParams());
                }
                codeParams.add(sheet.getSheetName());
                codeParams.add(String.valueOf(i));
                exceptionCodeParameters.setExceptionCodeParams(codeParams);
                Map<String,Object> error = new HashMap<String,Object>();
                Map errorMap = new HashMap();
                BusinessException be = new BusinessException(e.getCode(), exceptionCodeParameters.getExceptionCodeParamsAsArray());
                errorMap.put("errorCode", e.getCode());
                errorMap.put("errorMessage", be.getLocalizedMessage());
                //errorMap.put(ExcelConstants.CORRELATION_ID, getCorrelationId(row, correlationColumnIndex));
                error.put("error", errorMap);
                dataList.add(error);  
            }
        }
        return dataList;
    }
    
    private short getCorrelationColumnIndex(Sheet dataSheet) {
        short correlationColumnIndex = 0;
        Row rowData = dataSheet.getRow(0);
        short startCtr = rowData.getFirstCellNum();
        short endCtr = rowData.getLastCellNum();
        for(short ctr = startCtr; ctr < endCtr; ctr++) {
            Cell cell = rowData.getCell(ctr);
            if (cell.getStringCellValue().equalsIgnoreCase(ExcelConstants.CORR_ID_HEADER)) {
                correlationColumnIndex = ctr;
                break;
            }
        }
        return correlationColumnIndex;
    }

 /*   private String getCorrelationId(Row row, short correlationColumnIndex) {
        Cell correlationCell = row.getCell(correlationColumnIndex);
        correlationCell.setCellType(Cell.CELL_TYPE_STRING);
        return correlationCell.getStringCellValue();
    }*/

    /**
     * creates the list of sheet header
     * 
     * @param row
     * @return
     */
    public List<String> createSheetHeaderList(Row row) {
        List<String> sheetHeaderList = new ArrayList<>();
        Iterator<Cell> cellIterator = row.cellIterator();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            sheetHeaderList.add(new DataFormatter().formatCellValue(cell));
        }
        return sheetHeaderList;
    }

    /**
     * Reads one row of the sheet and returns a map of all the columns of that row
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
            for (int i = 0; i < sheetHeaderList.size(); i++) {
                Cell cellValue = row.getCell(i);
                String arrayType = null;
                Map<String, String> keyAndDataType = excelReadHelper.getKeyAndDataType(sheetHeaderList.get(i));
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
                    rowMap.put((String) sheetHeaderList.get(i), new DataFormatter().formatCellValue(cell));
                    break;
                case ExcelDataTypeConstants.CELL_TYPE_STRING:
                    rowMap.put(key, new DataFormatter().formatCellValue(cell));
                    break;
                case ExcelDataTypeConstants.CELL_TYPE_INTEGER:
                        rowMap.put(key, Integer.parseInt(new DataFormatter().formatCellValue(cell)));
                    break;
                case ExcelDataTypeConstants.CELL_TYPE_DOUBLE:
                    rowMap.put(key, Double.parseDouble(new DataFormatter().formatCellValue(cell)));
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
                case ExcelDataTypeConstants.CELL_TYPE_BIGDECIMAL:
                     rowMap.put(key, new BigDecimal(new DataFormatter().formatCellValue(cell)));
                     break;
                case ExcelDataTypeConstants.CELL_TYPE_BIGINTEGER:
                     rowMap.put(key, new BigInteger(new DataFormatter().formatCellValue(cell)));
                     break;
                case ExcelDataTypeConstants.CELL_TYPE_LONG:
                        rowMap.put(key, Long.parseLong(new DataFormatter().formatCellValue(cell)));
                     break;
            }
            }
        } catch (NumberFormatException e) {
            LOGGER.error("NFE error while parsing the value in ReadDataSheet ", e);
            codeParams = new ArrayList<>();
            codeParams.add(new DataFormatter().formatCellValue(cell));
            codeParams.add(dataType);
            exceptionCodeParameters.setExceptionCodeParams(codeParams);
            throw new BusinessException(ExcelPluginExceptionCodes.EXPL000008, null);
        } catch (Exception e) {
            codeParams = new ArrayList<>();
            codeParams.add(new DataFormatter().formatCellValue(cell));
            codeParams.add(dataType);
            exceptionCodeParameters.setExceptionCodeParams(codeParams);
            throw new BusinessException(ExcelPluginExceptionCodes.EXPL000009, null);
        }
        return rowMap;
    }

}
