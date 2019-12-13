package com.ca.umg.plugin.commons.excel.reader;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.mozilla.javascript.NativeArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.plugin.commons.excel.reader.exception.codes.ExcelPluginExceptionCodes;
import com.ca.umg.plugin.commons.excel.reader.exception.codes.ExceptionCodeParameters;

@Named
public class ExcelReadHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelReadHelper.class);

    @Inject
    ExceptionCodeParameters exceptionCodeParameters;

    private static final String CORRELATION_ID = "ExcelCorrelationID";

    /**
     * Returns the final list<Map> which has all the object references populated with values by reading the corresponding object
     * sheets
     * 
     * @param dataSheetAsList
     * @param allSheetsAsMap
     * @param headerMap
     * @return
     * @throws BusinessException
     */
    public List<Map<String, Object>> resolveObjectReference(List<Map<String, Object>> dataSheetAsList,
            Map<String, Object> allSheetsAsMap, Map<String, Object> headerMap) throws BusinessException {
        List<Map<String, Object>> returnList = new ArrayList<>();
        Map<String, Object> newHeaderMap = null;
        exceptionCodeParameters.setExceptionCodeParams(null);
        for (Map<String, Object> dataRowMap : dataSheetAsList) {
            Map<String, Object> headerAndData = new HashMap<>();
            Map<String, Object> rowMap = dataRowMap;
            try {
                Map<String, Object> eachEntryMap = new HashMap<>();
                for (String key : rowMap.keySet()) {
                    if (StringUtils.contains((String) key, "OBJECT")) {
                        String[] keyAndDataType = StringUtils.split((String) key, "|");
                        String objectName = keyAndDataType[0];
                        boolean array = false;
                        if (keyAndDataType.length == 3) {
                            array = true;
                        }
                        Map<String, Object> objectSheetRows = (Map<String, Object>) allSheetsAsMap.get(objectName);
                        Object value = rowMap.get(key);
                        isSheetAndIdPresent(value, objectName, "Data", objectSheetRows);
                        List<Map<String, Object>> eachEntryList = resolveAllRefIndividual(allSheetsAsMap,
                                (List<Map<String, Object>>) objectSheetRows.get(value), objectName);
                        if (!array && eachEntryList.size() == 1) {
                            Map tempMap = eachEntryList.get(0);
                            eachEntryMap.put(objectName, tempMap);
                        } else {
                            eachEntryMap.put(objectName, eachEntryList);
                        }
                    } else {
                        eachEntryMap.put(key, rowMap.get(key));
                    }
                }
                String transactionId = (String) eachEntryMap.remove("transactionId");
                newHeaderMap = new HashMap<>(headerMap);
                if (transactionId != null) {
                    newHeaderMap.put("transactionId", transactionId);
                    headerAndData.put("header", newHeaderMap);
                    headerAndData.put("data", eachEntryMap);
                    returnList.add(headerAndData);
                } else {
                    if (eachEntryMap.containsKey("error")) {
                        Map errorMap = (Map) eachEntryMap.get("error");
                        newHeaderMap.put("error", "true");
                        newHeaderMap.put(CORRELATION_ID, errorMap.get(CORRELATION_ID));
                        headerAndData.put("header", newHeaderMap);
                        headerAndData.put("data", errorMap);
                        returnList.add(headerAndData);
                    } else {
                        newHeaderMap.put("transactionId", null);
                        LOGGER.error("Transaction id cannot be null");
                        throw new BusinessException(ExcelPluginExceptionCodes.EXPL000012, null);
                    }
                }
            } catch (BusinessException ex) {
                newHeaderMap = new HashMap<>(headerMap);
                newHeaderMap.put("error", "true");
                if (newHeaderMap.get("transactionId") == null) {
                    newHeaderMap.put("transactionId", null);
                }
                headerAndData.put("header", newHeaderMap);
                Map<String, Object> eachEntryMap = new HashMap<>();
                if (rowMap != null && rowMap.get("ExcelCorrelationID") != null) {
                    eachEntryMap.put(CORRELATION_ID, (String) rowMap.get("ExcelCorrelationID"));
                }
                eachEntryMap.put("errorMessage", ex.getLocalizedMessage());
                eachEntryMap.put("errorCode", ex.getCode());
                headerAndData.put("data", eachEntryMap);
                returnList.add(headerAndData);
            }
        }
        return returnList;
    }

    /**
     * recursively iterate through all the object references and return the value for the object
     * 
     * @param allSheetsAsMap
     * @param objectSheetRow
     * @param sheetName
     * @return
     * @throws BusinessException
     */
    private List<Map<String, Object>> resolveAllRefIndividual(Map<String, Object> allSheetsAsMap,
            List<Map<String, Object>> objectSheetRow, String sheetName) throws BusinessException {
        List<Map<String, Object>> finalList = new ArrayList<>();
        for (Map<String, Object> eachEntry : objectSheetRow) {
            Map<String, Object> eachEntryMap = new HashMap<>();
            for (String key1 : eachEntry.keySet()) {
                if (StringUtils.contains((String) key1, "OBJECT")) {
                    String[] keyAndDataType = StringUtils.split((String) key1, "|");
                    String objectName = keyAndDataType[0];
                    Map<String, Object> objectSheetRows = (Map<String, Object>) allSheetsAsMap.get(objectName);
                    Object value = eachEntry.get(key1);
                    isSheetAndIdPresent(value, objectName, sheetName, objectSheetRows);
                    List<Map<String, Object>> eachEntryList = resolveAllRefIndividual(allSheetsAsMap,
                            (List<Map<String, Object>>) objectSheetRows.get(value), objectName);
                    if (eachEntryList.size() == 1 && keyAndDataType.length<3) {
                        Map<String, Object> tempMap = eachEntryList.get(0);
                        eachEntryMap.put(objectName, tempMap);
                    } else {
                        eachEntryMap.put(objectName, eachEntryList);
                    }
                } else {
                    eachEntryMap.put(key1, eachEntry.get(key1));
                }
            }
            finalList.add(eachEntryMap);
        }
        return finalList;
    }

    /**
     * checks if the Sheet exists and if corresponding ids are present if sheet exists
     * 
     * @param value
     * @param objectName
     * @param sheetName
     * @param objectSheetRows
     * @throws BusinessException
     */
    private void isSheetAndIdPresent(Object value, String objectName, String sheetName, Map<String, Object> objectSheetRows)
            throws BusinessException {
        ArrayList<Object> codeParams = null;
        if (objectSheetRows == null) {
            codeParams = new ArrayList<>();
            codeParams.add(objectName);
            codeParams.add(sheetName);
            exceptionCodeParameters.setExceptionCodeParams(codeParams);
            throw new BusinessException(ExcelPluginExceptionCodes.EXPL000018,
                    exceptionCodeParameters.getExceptionCodeParamsAsArray());
        } else if (objectSheetRows.get(value) == null) {
            codeParams = new ArrayList<>();
            codeParams.add(value);
            codeParams.add(objectName);
            codeParams.add(sheetName);
            exceptionCodeParameters.setExceptionCodeParams(codeParams);
            throw new BusinessException(ExcelPluginExceptionCodes.EXPL000005,
                    exceptionCodeParameters.getExceptionCodeParamsAsArray());
        }
    }

    /**
     * converts the string representation of primitive-data-type-array entered in excel sheet to the object array
     * 
     * @param arr
     * @param array
     * @param dataType
     * @return
     */
    public Object[] convertToArray(NativeArray arr, Object[] array, String dataType) {
        for (Object o : arr.getIds()) {
            int index = (Integer) o;
            if (arr.get(index, null) instanceof NativeArray) {
                NativeArray childarr = (NativeArray) arr.get(index, null);
                Object[] childArray = new Object[(int) childarr.getLength()];
                array[index] = convertToArray(childarr, childArray, dataType);
            } else {
                if (StringUtils.equalsIgnoreCase(dataType, "DOUBLE")) {
                    if (arr.get(index, null) != null) {
                        array[index] = Double.parseDouble(arr.get(index, null).toString());
                    } else {
                        array[index] = arr.get(index, null);
                    }
                } else if (StringUtils.equalsIgnoreCase(dataType, "INTEGER")) {
                    if (arr.get(index, null) instanceof Double) {
                        array[index] = new Double(Double.parseDouble(arr.get(index, null).toString())).intValue();
                    } else {
                        array[index] = arr.get(index, null);
                    }
                } else if (StringUtils.equalsIgnoreCase(dataType, "BIGINTEGER")) {
                    if (arr.get(index, null) instanceof Double) {
                        array[index] = new BigDecimal(Double.parseDouble(arr.get(index, null).toString())).toBigInteger();
                    } else {
                        array[index] = arr.get(index, null);
                    }
                } else if (StringUtils.equalsIgnoreCase(dataType, "LONG")) {
                    if (arr.get(index, null) instanceof Double) {
                        array[index] = new Double(Double.parseDouble(arr.get(index, null).toString())).longValue();
                    } else {
                        array[index] = arr.get(index, null);
                    }
                } else {
                    array[index] = arr.get(index, null);
                }
            }
        }
        return array;
    }

    /**
     * returns the Map of key, datatype and arraytype for each column
     * 
     * @param rowHeader
     * @return
     */
    public Map<String, String> getKeyAndDataType(String rowHeader) {

        Map<String, String> keyandDataTypeMap = new HashMap<>();

        String key = StringUtils.substringBefore(rowHeader, "|");
        keyandDataTypeMap.put("rowKey", key);

        String type = StringUtils.substringAfter(rowHeader, "|");
        String[] dataTypeArray = StringUtils.split(type, "|");

        if (dataTypeArray.length == 1) {
            if (StringUtils.contains(type, "DATE")) {
                String dateType = StringUtils.substringBefore(dataTypeArray[0], "-");
                keyandDataTypeMap.put("dataType", dateType.toUpperCase(Locale.getDefault()));
            } else {
                keyandDataTypeMap.put("dataType", dataTypeArray[0].toUpperCase(Locale.getDefault()));
            }
        } else {
            if (StringUtils.contains(type, "OBJECT")) {
                keyandDataTypeMap.put("dataType", dataTypeArray[0].toUpperCase(Locale.getDefault()));
            } else {
                keyandDataTypeMap.put("arrayType", dataTypeArray[0].toUpperCase(Locale.getDefault()));
                keyandDataTypeMap.put("dataType", dataTypeArray[1].toUpperCase(Locale.getDefault()));
            }
        }

        return keyandDataTypeMap;
    }

}
