package com.ca.umg.plugin.commons.excel.reader;

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
import com.ca.umg.plugin.commons.excel.reader.exception.codes.ExcelPluginExceptionCodes;
import com.ca.umg.plugin.commons.excel.reader.exception.codes.ExceptionCodeParameters;

@Named
public class ReadHeaderSheet {

    public static final Logger LOGGER = LoggerFactory.getLogger(ReadHeaderSheet.class);
    public static final String MINOR_VERSION = "minorVersion";
    public static final String MAJOR_VERSION = "majorVersion";
    public static final String TRANSACTION_TYPE = "transactionType";
    public static final String TRANSACTION_MODE = "transactionMode";
    public static final String USER = "user";
    public static final String ADD_ON_VALIDATION = "addOnValidation";
    public static final String PAYLOAD_STORAGE = "payloadStorage";
    public static final String TRANSACTIONID = "transactionId";
    public static final String MODELNAME = "modelName";
    public static final String SUCCESS = "success";
    public static final String EXECUTION_GROUP = "executionGroup";
    public static final String STORE_RLOGS = "storeRLogs";
    public static final String STRING_NULL = "null";
    public static final String MODEL_IDENTIFIER = "modelIdentifier";

    
    
    
    @Inject
    ExcelReadHelper excelReadHelper;

    @Inject
    ExceptionCodeParameters exceptionCodeParameters;
    
    /**
     * reads the header sheet and returns map   
     * @param sheet
     * @return
     * @throws BusinessException
     */
    public Map<String, Object> readSheet(Sheet sheet) throws BusinessException {
    	exceptionCodeParameters.setExceptionCodeParams(null);
        int rowNum = sheet.getLastRowNum() + 1;
        if  (rowNum < 2) {
            BusinessException.raiseBusinessException(ExcelPluginExceptionCodes.EXPL000004, null);
        } else if (rowNum > 2) {
            BusinessException.raiseBusinessException(ExcelPluginExceptionCodes.EXPL000017, null);
        }
        List<String> sheetHeaderList = new ArrayList<>();
        Map<String, Object> rowMap = new HashMap<>();
        for (int i = 0; i < rowNum; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                throw new BusinessException(ExcelPluginExceptionCodes.EXPL000020, null);
            }
            if (i == 0) {
                sheetHeaderList = createSheetHeaderList(row);
            } else {
                rowMap = populateValuesForEachRow(row, sheetHeaderList);
            }
        }
        return rowMap;
    }

    /**
     * returns the list of header 
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
     * Reads the one row of the header sheet and returns a map all the columns 
     * @param row
     * @param sheetHeaderList
     * @return
     * @throws BusinessException
     */
    public Map<String, Object> populateValuesForEachRow(Row row, List<String> sheetHeaderList) throws BusinessException {
        Map<String, Object> rowMap = new HashMap<>();
        Cell cell = null;
        ArrayList<Object> codeParams = null;
        try {
            for (int i=0 ;i<sheetHeaderList.size(); i++) {
                cell = row.getCell(i);
                String columnName =  sheetHeaderList.get(i);
                
                if ((cell == null || StringUtils.isBlank(new DataFormatter().formatCellValue(cell))) && !(MINOR_VERSION.equalsIgnoreCase(columnName) || "date".equalsIgnoreCase(columnName) || TRANSACTION_TYPE.equalsIgnoreCase(columnName) || USER.equalsIgnoreCase(columnName) || ADD_ON_VALIDATION.equalsIgnoreCase(columnName) || PAYLOAD_STORAGE.equalsIgnoreCase(columnName) || TRANSACTIONID.equalsIgnoreCase(columnName)||EXECUTION_GROUP.equalsIgnoreCase(columnName) || TRANSACTION_MODE.equalsIgnoreCase(columnName) || STORE_RLOGS.equalsIgnoreCase(columnName))) {
                    codeParams = new ArrayList<>();
                	codeParams.add(sheetHeaderList.get(i));
                	exceptionCodeParameters.setExceptionCodeParams(codeParams);
                	 throw new BusinessException(ExcelPluginExceptionCodes.EXPL000013, null);
                }                
                if (columnName.equalsIgnoreCase(MAJOR_VERSION)) {
                    rowMap.put(sheetHeaderList.get(i), Integer.parseInt(new DataFormatter().formatCellValue(cell)));
                } else if (columnName.equalsIgnoreCase(MINOR_VERSION)) {
                	String minorVersion = StringUtils.trimToNull(new DataFormatter().formatCellValue(cell));
                	if(StringUtils.equalsIgnoreCase(minorVersion, STRING_NULL)){
                		minorVersion = null;
                	}
                    rowMap.put(sheetHeaderList.get(i),minorVersion==null ? null : Integer.parseInt(minorVersion));
                } else if(columnName.equalsIgnoreCase(USER) && StringUtils.isNotBlank(new DataFormatter().formatCellValue(cell))){
                	rowMap.put(sheetHeaderList.get(i), new DataFormatter().formatCellValue(cell));                   
                }else if(columnName.equalsIgnoreCase(TRANSACTION_TYPE) && StringUtils.isNotBlank(new DataFormatter().formatCellValue(cell))){
                	 rowMap.put(sheetHeaderList.get(i), new DataFormatter().formatCellValue(cell));
                }else if(columnName.equalsIgnoreCase(ADD_ON_VALIDATION) && StringUtils.isNotBlank(new DataFormatter().formatCellValue(cell))){       
                	 String addOnValidation = new DataFormatter().formatCellValue(cell);	
      			   Context context = Context.enter();
      			   Scriptable scope = context.initStandardObjects();
      			   Object result = context.evaluateString(scope, addOnValidation, "<cmd>", 1, null);
                     NativeArray arr = (NativeArray) result;
                     Object[] array = new Object[(int) arr.getLength()];
                     Object[] convertedArray   = excelReadHelper.convertToArray(arr, array, "string"); 
            		List<String> addOnValidationList = new ArrayList<String>();
            		for(Object addOnValidationStrs : convertedArray){            			
            			addOnValidationList.add((String)addOnValidationStrs);
            		}
            		 rowMap.put(sheetHeaderList.get(i), addOnValidationList);               	 
               }else if(columnName.equalsIgnoreCase(PAYLOAD_STORAGE) && StringUtils.isNotBlank(new DataFormatter().formatCellValue(cell))){            	   
                 	 rowMap.put(sheetHeaderList.get(i), Boolean.valueOf(new DataFormatter().formatCellValue(cell)));
                 }else if(columnName.equalsIgnoreCase(TRANSACTIONID) && StringUtils.isNotBlank(new DataFormatter().formatCellValue(cell))){
                 	 rowMap.put(sheetHeaderList.get(i), new DataFormatter().formatCellValue(cell));
                 }else if(columnName.equalsIgnoreCase(MODELNAME) && StringUtils.isNotBlank(new DataFormatter().formatCellValue(cell))){
                	 rowMap.put(sheetHeaderList.get(i), new DataFormatter().formatCellValue(cell));
                 }
                 else if(columnName.equalsIgnoreCase(EXECUTION_GROUP) && StringUtils.isNotBlank(new DataFormatter().formatCellValue(cell))){
                	 rowMap.put(sheetHeaderList.get(i), new DataFormatter().formatCellValue(cell));
                 }
                 else if(columnName.equalsIgnoreCase(STORE_RLOGS) && StringUtils.isNotBlank(new DataFormatter().formatCellValue(cell))){
                 	rowMap.put(sheetHeaderList.get(i), new DataFormatter().formatCellValue(cell));                   
                 }
            }
        } catch (NumberFormatException e) {
            LOGGER.error("error while parsing the integer value in header sheet ", e);
            codeParams = new ArrayList<>();
        	codeParams.add(new DataFormatter().formatCellValue(cell));
        	exceptionCodeParameters.setExceptionCodeParams(codeParams);
        	 throw new BusinessException(ExcelPluginExceptionCodes.EXPL000014, null);
        } catch (BusinessException e) {
        	throw new BusinessException(e.getCode(), null);
        } catch (Exception e) {
            LOGGER.error("error while parsing the header sheet ", e);
            codeParams = new ArrayList<>();
        	codeParams.add(new DataFormatter().formatCellValue(cell));
        	exceptionCodeParameters.setExceptionCodeParams(codeParams);
            throw new BusinessException(ExcelPluginExceptionCodes.EXPL000015, null);
        }
        return rowMap;
    }
}
