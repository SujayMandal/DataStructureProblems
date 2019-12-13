/*
 * ControllerUtil.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.sdc.rest.utils;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.syndicatedata.info.SyndicateDataColumnInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataKeyColumnInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataKeyInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataVersionInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateFileDataInfo;
import com.ca.umg.business.syndicatedata.util.DataTypes;
import com.ca.umg.business.validation.SyndicateDataValidator;
import com.ca.umg.business.validation.ValidationError;

import au.com.bytecode.opencsv.CSVReader;

/**
 * 
 * Controller utilities.
 * 
 * @author mandavak
 *
 */
public final class CSVUtil {

    private static final int TWO = 2;
    private static final int FIFTH_ROW = 4;
    private static final int FOURTH_ROW = 3;
    private static final int THIRD_ROW = TWO;
    private static final int SECOND_ROW = 1;
    private static final int FIRST_ROW = 0;
    private static final int KEYNAMEINDEX = FIRST_ROW;
    private static final String DATA_REGEX = "[\\p{Alnum}\\p{Punct}\\p{Space}]*";
    private static final String HDR_REGEX = "[\\p{Alnum}_]*";
    private static final Logger LOGGER = LoggerFactory.getLogger(CSVUtil.class);

    private CSVUtil() {

    }

    /**
     * helper method to getCSV
     * 
     * @param inputStream
     * @return
     * @throws SystemException
     * @throws BusinessException
     */
    public static SyndicateFileDataInfo readAllRecords(InputStream inputStream) throws SystemException, BusinessException {
        CSVReader reader = null;
        List<Map<String, String>> records = new ArrayList<Map<String, String>>();
        try {
            reader = new CSVReader(new InputStreamReader(inputStream));
            List<String[]> data = reader.readAll();
            if (isRecordsLessThanTwo(data)) {
                throw new BusinessException(BusinessExceptionCodes.BSE000026, new String[] { "" });
            }
            String[] headerData = getHeaderRecord(data); // TODO : what if header is not present in CSV,
                                                         // check
            Set<String> duplicateColumns = new HashSet<String>();
            Set<String> headerRecord = new LinkedHashSet<String>();
            for (String columnName : headerData) {
                // added to check the csv file is corrupt
                if (columnName.matches(HDR_REGEX)) {
                    // UMG-4459 start
                    columnName = formatColumnName(columnName);
                    // UMG-4459 end
                    if (!headerRecord.add(columnName)) {
                        duplicateColumns.add(columnName);
                    } else {
                        headerRecord.add(columnName);
                    }
                } else {
                    throwExceptionForHeader(columnName);
                }
            }
            if (isNotEmpty(duplicateColumns)) {
                throw new BusinessException(BusinessExceptionCodes.BSE000128, new Object[] { duplicateColumns });
            }

            List<String[]> contentRecords = getContentRecords(data);
            for (String[] row : contentRecords) {
                records.add(readRow(headerRecord, row));
            }

        } catch (IOException e) {
            logFileReadErrorAndThrowException(e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                logFileCloseErrorAndThrowException(e);
            }
        }

        SyndicateFileDataInfo syndicateFileDataInfo = new SyndicateFileDataInfo();
        syndicateFileDataInfo.setData(records);
        return syndicateFileDataInfo;
    }

    // UMG-4459 start
    private static String formatColumnName(String columnName) {
        String formattedColumnName = columnName;
        if (StringUtils.isNotBlank(formattedColumnName) && Character.isDigit(formattedColumnName.charAt(0))) {
            formattedColumnName = StringUtils.join(BusinessConstants.SYND_CLMN_NAME_ESC_CHAR,
                    formattedColumnName);
        }
        return formattedColumnName;
    }

    // UMG-4459 end

    private static void throwExceptionForHeader(String columnName) throws BusinessException {
        if (columnName.matches("[\\p{Alnum}\\p{Punct}\\p{Space}]*")) {
            throw new BusinessException(BusinessExceptionCodes.BSE000556, new Object[] { columnName });
        }
        throw new BusinessException(BusinessExceptionCodes.BSE000555, new Object[] {});

    }

    public static SyndicateDataVersionInfo getContainerDefinition(InputStream inputStream,
            List<SyndicateDataColumnInfo> syndicateDataColumnInfo) throws SystemException, BusinessException {
        CSVReader reader = null;
        List<String[]> tableDefinition = null;
        List<String[]> keyDefinition = null;
        try {
            reader = new CSVReader(new InputStreamReader(inputStream));
            List<String[]> data = reader.readAll();
            if (isRecordsLessThanFive(data)) {
                throw new BusinessException(BusinessExceptionCodes.BSE000110, new String[] { "" });
            }
            tableDefinition = getTableColumnDefinition(data);
            keyDefinition = getKeyDefinition(data);
        } catch (IOException e) {
            logFileReadErrorAndThrowException(e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                logFileCloseErrorAndThrowException(e);
            }
        }
        SyndicateDataVersionInfo syndicateDataVersionInfo = new SyndicateDataVersionInfo();
        syndicateDataVersionInfo.setMetaData(generateTableColumnInfo(tableDefinition));
        boolean isValid = validateDefinition(syndicateDataVersionInfo.getMetaData(), syndicateDataColumnInfo);
        if (!isValid) {
            throw new BusinessException(BusinessExceptionCodes.BSE000118, new String[] { "" });
        }
        Map<String, String> columnNamesMap = getColumnNamesMap(syndicateDataVersionInfo.getMetaData());
        if (keyDefinition != null) {
            syndicateDataVersionInfo.setKeys(generateKeyInfo(keyDefinition, columnNamesMap));
        }
        return syndicateDataVersionInfo;
    }

    private static boolean validateDefinition(List<SyndicateDataColumnInfo> generatedMetadata,
            List<SyndicateDataColumnInfo> metaDataFromUI) {
        boolean isValid = false;
        if (generatedMetadata.size() == metaDataFromUI.size()) {
            boolean hasSameData = false;
            for (SyndicateDataColumnInfo syndDataColInfoFrmUI : metaDataFromUI) {
                hasSameData = false;
                String columnName = syndDataColInfoFrmUI.getDisplayName();
                for (SyndicateDataColumnInfo generatedSyndDataColInfo : generatedMetadata) {
                    if (columnName.equalsIgnoreCase(generatedSyndDataColInfo.getDisplayName())) {
                        hasSameData = true;
                        break;
                    }
                }
                if (!hasSameData) {
                    break;
                }
            }
            isValid = hasSameData;
        }
        return isValid;
    }

    private static void logFileCloseErrorAndThrowException(IOException e) throws SystemException {
        LOGGER.error("Unable to close the file reader", e);
        throw new SystemException("", new String[] { "" }, e);
    }

    private static void logFileReadErrorAndThrowException(IOException e) throws SystemException {
        LOGGER.error("Unable to read the file", e);
        throw new SystemException("", new String[] { "" }, e);
    }

    private static Map<String, String> getColumnNamesMap(List<SyndicateDataColumnInfo> metaData) {
        Map<String, String> colNameMap = new LinkedHashMap<String, String>();
        for (SyndicateDataColumnInfo syndicateDataColumnInfo : metaData) {
            colNameMap.put(syndicateDataColumnInfo.getDisplayName(), null);
        }
        return colNameMap;
    }

    private static List<SyndicateDataKeyInfo> generateKeyInfo(List<String[]> keyDefinitions, Map<String, String> columnNamesMap)
            throws BusinessException {
        List<SyndicateDataKeyInfo> keyList = new ArrayList<SyndicateDataKeyInfo>();
        List<String> invalidKeyList = new ArrayList<String>();
        for (String[] keyDefinition : keyDefinitions) {
            List<SyndicateDataKeyColumnInfo> keyColInfoList = new ArrayList<SyndicateDataKeyColumnInfo>();
            Iterator<String> columnsItr = columnNamesMap.keySet().iterator();
            while (columnsItr.hasNext()) {
                SyndicateDataKeyColumnInfo syndicateDataKeyColumnInfo = new SyndicateDataKeyColumnInfo();
                syndicateDataKeyColumnInfo.setColumnName(columnsItr.next());
                keyColInfoList.add(syndicateDataKeyColumnInfo);
            }
            SyndicateDataKeyInfo syndicateDataKeyInfo = new SyndicateDataKeyInfo();
            syndicateDataKeyInfo.setsColumnInfos(keyColInfoList);
            keyList.add(syndicateDataKeyInfo);
            for (int i = 0; i < keyDefinition.length; i++) {
                if (i == KEYNAMEINDEX) {
                    syndicateDataKeyInfo.setKeyName(keyDefinition[i]);
                    continue;
                }
                SyndicateDataKeyColumnInfo syndicateDataKeyColumnInfo = null;
                for (SyndicateDataKeyColumnInfo syndDataKeyColumnInfo : keyColInfoList) {
                    if (syndDataKeyColumnInfo.getColumnName().equalsIgnoreCase(keyDefinition[i])) {
                        syndicateDataKeyColumnInfo = syndDataKeyColumnInfo;
                        break;
                    }
                }
                if (columnNamesMap.containsKey(keyDefinition[i])) {
                    syndicateDataKeyColumnInfo.setStatus(true);
                } else {
                    if (!invalidKeyList.contains(syndicateDataKeyInfo.getKeyName())) {
                        invalidKeyList.add(syndicateDataKeyInfo.getKeyName());
                    }
                }
            }
        }
        verifyAndThrowError(invalidKeyList);
        SyndicateDataValidator validator = new SyndicateDataValidator();
        List<ValidationError> errors = validator.validateKeys(keyList);
        reportValidationErrors(errors);
        return keyList;
    }

    private static void verifyAndThrowError(List<String> invalidKeyList) throws BusinessException {
        if (!invalidKeyList.isEmpty()) {
            throw new BusinessException(BusinessExceptionCodes.BSE000111, new String[] { StringUtils.join(invalidKeyList, ",") });
        }
    }

    private static void reportValidationErrors(List<ValidationError> errors) throws BusinessException {
        if (isNotEmpty(errors)) {
            StringBuilder errorMessages = new StringBuilder();
            for (ValidationError validationError : errors) {
                errorMessages.append(validationError.getMessage()).append('\n');
            }
            throw new BusinessException(BusinessExceptionCodes.BSE000129, new String[] { errorMessages.toString() });
        }
    }

    private static List<SyndicateDataColumnInfo> generateTableColumnInfo(List<String[]> tableDefinition) throws BusinessException {
        checkTableStructure(tableDefinition);
        checkDuplicateColumns(tableDefinition);
        List<SyndicateDataColumnInfo> colList = new ArrayList<SyndicateDataColumnInfo>();
        for (int i = 0; i < tableDefinition.get(0).length; i++) {
            SyndicateDataColumnInfo syndicateDataColumnInfo = new SyndicateDataColumnInfo();
            colList.add(syndicateDataColumnInfo);
        }
        for (int i = 0; i < tableDefinition.size(); i++) {
            String[] colDefinitionData = tableDefinition.get(i);
            if (i == FIRST_ROW) {
                populateColumnName(colDefinitionData, colList);
            }
            if (i == SECOND_ROW) {
                populateColumnDesc(colDefinitionData, colList);
            }
            if (i == THIRD_ROW) {
                populateColumnType(colDefinitionData, colList);
            }
            if (i == FOURTH_ROW) {
                populateColumnSize(colDefinitionData, colList);
            }
            if (i == FIFTH_ROW) {
                populateNullable(colDefinitionData, colList);
            }
        }
        return colList;
    }

    private static void checkDuplicateColumns(List<String[]> tableDefinition) throws BusinessException {
        String[] colNames = tableDefinition.get(0);
        boolean hasDups = false;
        List<String> dupsList = new ArrayList<String>();
        Map<String, String> dupsMap = new HashMap<String, String>();
        for (int i = 0; i < colNames.length; i++) {
            if (dupsMap.containsKey(colNames[i])) {
                hasDups = true;
                dupsList.add(colNames[i]);
                continue;
            }
            dupsMap.put(colNames[i], colNames[i]);
        }
        if (hasDups) {
            throw new BusinessException(BusinessExceptionCodes.BSE000127, dupsList.toArray());
        }
    }

    private static void checkTableStructure(List<String[]> tableDefinition) throws BusinessException {
        int colLength = tableDefinition.get(0).length;
        boolean tabStructureOk = true;
        for (int i = 1; i < tableDefinition.size(); i++) {
            if (tableDefinition.get(i).length != colLength) {
                tabStructureOk = false;
                break;
            }
        }
        if (!tabStructureOk) {
            throw new BusinessException(BusinessExceptionCodes.BSE000119, new String[] { "" });
        }
    }

    private static void populateNullable(String[] colDefinitionData, List<SyndicateDataColumnInfo> colList)
            throws BusinessException {
        for (int i = 0; i < colDefinitionData.length; i++) {
            String nullable = colDefinitionData[i];
            if (nullable.equalsIgnoreCase(BusinessConstants.YES)) {
                colList.get(i).setMandatory(false);
            } else if (nullable.equalsIgnoreCase(BusinessConstants.NO)) {
                colList.get(i).setMandatory(true);
            } else {
                throw new BusinessException(BusinessExceptionCodes.BSE000112, new String[] { "" });
            }
        }
    }

    private static void populateColumnSize(String[] colDefinitionDataArr, List<SyndicateDataColumnInfo> colList)
            throws BusinessException {
        for (int i = 0; i < colDefinitionDataArr.length; i++) {
            if (colDefinitionDataArr[i].contains(BusinessConstants.CHAR_PIPE)) {
                validateAndBuildDoubleType(colDefinitionDataArr[i], colList.get(i));
            } else {
                if (colList.get(i).getColumnType().equalsIgnoreCase(DataTypes.DOUBLE.toString())) {
                    throw new BusinessException(BusinessExceptionCodes.BSE000113, new String[] { "" });
                }
                try {
                    if (!(colList.get(i).getColumnType().equalsIgnoreCase(DataTypes.DATE.toString())
                            || colList.get(i).getColumnType().equalsIgnoreCase(DataTypes.BOOLEAN.toString()) || colList.get(i)
                            .getColumnType().equalsIgnoreCase(DataTypes.INTEGER.toString()))) {
                        Integer size = Integer.parseInt(colDefinitionDataArr[i]);
                        colList.get(i).setColumnSize(size);
                    }
                } catch (Exception excp) { // NOPMD
                    throw new BusinessException(BusinessExceptionCodes.BSE000116, new String[] { "" }, excp);
                }
            }
        }
    }

    private static void validateAndBuildDoubleType(String colDefinitionData, SyndicateDataColumnInfo syndDataColInfo)
            throws BusinessException {
        if (syndDataColInfo.getColumnType().equalsIgnoreCase(DataTypes.DOUBLE.toString())) {
            String[] doubleSize = colDefinitionData.split("\\|");
            if (doubleSize.length > TWO) {
                throw new BusinessException(BusinessExceptionCodes.BSE000113, new String[] { "" });
            } else {
                boolean isPrecisionGreater = false;
                try {
                    Integer size = Integer.parseInt(doubleSize[0]);
                    Integer precision = Integer.parseInt(doubleSize[1]);
                    if (precision > size) {
                        isPrecisionGreater = true;
                    }
                    syndDataColInfo.setColumnSize(size);
                    syndDataColInfo.setPrecision(precision);
                } catch (Exception excp) { // NOPMD
                    throw new BusinessException(BusinessExceptionCodes.BSE000114, new String[] { "" }, excp);
                }
                if (isPrecisionGreater) {
                    throw new BusinessException(BusinessExceptionCodes.BSE000120, new String[] { "" });
                }
            }
        } else {
            throw new BusinessException(BusinessExceptionCodes.BSE000115, new String[] { "" });
        }
    }

    private static void populateColumnType(String[] colDefinitionData, List<SyndicateDataColumnInfo> colList)
            throws BusinessException {
        for (int i = 0; i < colDefinitionData.length; i++) {
            try {
                if (DataTypes.valueOf(colDefinitionData[i].toUpperCase()) != null) {
                    colList.get(i).setColumnType(colDefinitionData[i].toUpperCase());
                }
            } catch (Exception excp) { // NOPMD
                throw new BusinessException(BusinessExceptionCodes.BSE000117, new String[] { "" }, excp);
            }
        }
    }

    private static void populateColumnDesc(String[] colDefinitionData, List<SyndicateDataColumnInfo> colList)
            throws BusinessException {
        for (int i = 0; i < colDefinitionData.length; i++) {
            if (colDefinitionData[i].trim().length() == 0 || colDefinitionData[i].trim().length() > 200) {
                throw new BusinessException(BusinessExceptionCodes.BSE000122, new String[] { "" });
            }
            colList.get(i).setDescription(colDefinitionData[i].trim());
        }
    }

    private static void populateColumnName(String[] colDefinitionData, List<SyndicateDataColumnInfo> colList)
            throws BusinessException {
        for (int i = 0; i < colDefinitionData.length; i++) {
            if (colDefinitionData[i].trim().contains(BusinessConstants.SPACE) || colDefinitionData[i].trim().length() == 0
                    || colDefinitionData[i].trim().length() > 64 || !colDefinitionData[i].trim().matches("^[a-zA-Z0-9_]{1,64}$")) {
                throw new BusinessException(BusinessExceptionCodes.BSE000121, new String[] { "" });
            }
            colList.get(i).setDisplayName(colDefinitionData[i].trim());
            colList.get(i).setField(colDefinitionData[i].trim());
        }
    }

    private static List<String[]> getKeyDefinition(List<String[]> data) {
        List<String[]> keyDefn = null;
        if (isHavingKeyDefinition(data)) {
            keyDefn = new ArrayList<String[]>();
            for (int i = 5; i < data.size(); i++) {
                keyDefn.add(data.get(i));
            }
        }
        return keyDefn;
    }

    private static boolean isHavingKeyDefinition(List<String[]> data) {
        return data.size() > 5;
    }

    private static List<String[]> getTableColumnDefinition(List<String[]> data) {
        List<String[]> tableColDefn = new ArrayList<String[]>();
        // UMG-4459 start
        String[] columnNames = data.get(0);
        String[] formattedColumnNames = null;
        if (columnNames != null) {
            formattedColumnNames = new String[columnNames.length];
            for (int i = 0; i < columnNames.length; i++) {
                if (Character.isDigit(columnNames[i].charAt(0))) {
                    formattedColumnNames[i] = BusinessConstants.SYND_CLMN_NAME_ESC_CHAR + columnNames[i];
                } else {
                    formattedColumnNames[i] = columnNames[i];
                }
            }
        }
        // UMG-4459 end

        tableColDefn.add(formattedColumnNames);
        tableColDefn.add(data.get(1));
        tableColDefn.add(data.get(2));
        tableColDefn.add(data.get(3));
        tableColDefn.add(data.get(4));
        return tableColDefn;
    }

    private static boolean isRecordsLessThanTwo(List<String[]> data) {
        return data.size() < 2;
    }

    private static boolean isRecordsLessThanFive(List<String[]> data) {
        return data.size() < 5;
    }

    /**
     * getRow for a given data.
     * 
     * @param headerRecord
     * @param row
     * @return
     * @throws BusinessException
     */
    private static Map<String, String> readRow(Set<String> headerRecord, String[] row) throws BusinessException {
        Iterator<String> headerIterator = headerRecord.iterator();
        Map<String, String> values = new LinkedHashMap<String, String>();
        for (String coloumnValue : row) {
            // added to check the csv file is corrupt
            if (coloumnValue.matches(DATA_REGEX)) {
                boolean columnHeader = headerIterator.hasNext();
                if (columnHeader && !org.springframework.util.StringUtils.isEmpty(columnHeader)) { // TODO:test
                    values.put(headerIterator.next().trim(), coloumnValue.trim());
                } else {
                    // TODO: give message that user has data which does not have
                    // column definition
                    LOGGER.info("No items in Header Record.");
                }
            } else {
                throw new BusinessException(BusinessExceptionCodes.BSE000555, new Object[] {});
            }
        }
        return values;
    }

    public static List<String[]> getContentRecords(List<String[]> records) {
        return new ArrayList<String[]>(records.subList(1, records.size()));
    }

    public static String[] getHeaderRecord(List<String[]> records) {
        return records.get(0);
    }
}
