/*
 * ControllerUtil.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.business.util;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.model.info.ModelLibraryExecPackageMappingInfo;
import com.ca.umg.business.version.command.error.Error;

import au.com.bytecode.opencsv.CSVReader;

/**
 * 
 * Controller utilities.
 * 
 * @author basanaga
 * 
 */
public final class CSVUtil {

    private static final String HDR_REGEX = "[\\p{Alnum}_]*";
    private static final String ALL_NUM_REGEX = "[\\p{Digit}]*";


    private static final Logger LOGGER = LoggerFactory.getLogger(CSVUtil.class);
    private static final String R_ADDON_LIB_HIERARCHY = "Hierarchy";
    private static final String R_ADDON_LIB = "Library";
    private static final String R_ADDON_LIB_VERSION = "Version";

    private CSVUtil() {

    }
    private static void throwExceptionForHeader(String columnName) throws BusinessException {
        if (columnName.matches("[\\p{Alnum}\\p{Punct}\\p{Space}]*")) {
            throw new BusinessException(BusinessExceptionCodes.BSE000556, new Object[] { columnName });
        }
        throw new BusinessException(BusinessExceptionCodes.BSE000555, new Object[] {});

    }
    private static boolean isRecordsLessThanTwo(List<String[]> data) {
        return data.size() < 2;
    }

    /**
     * getRow for a given data.
     * 
     * @param headerRecord
     * @param row
     * @return
     * @throws BusinessException
     */
    private static void readAndValidateManifestRow(String[] row, Set<String> addedRows, List<String> duplicateRows,
            List<Error> errors, int rowNo, List<ModelLibraryExecPackageMappingInfo> addonPackagesInfos) {
        boolean isRowValid = true;

        if (!(StringUtils.isNotBlank(row[0]) && StringUtils.isNotBlank(row[1]) && StringUtils.isNotBlank(row[2]))) {
            errors.add(new Error("Missing one of the parameter(Hierarchy,Library,Version) in row : " + rowNo
                    + " in manifest file", BusinessConstants.VALIDATE_R_MANIFEST_FILE, StringUtils.EMPTY));
        }
        if (isRowValid && !row[0].matches(ALL_NUM_REGEX)) {
            errors.add(new Error("Invalid hierarchy in the row : " + rowNo + ". Expecting a numeric value",
                    BusinessConstants.VALIDATE_R_MANIFEST_FILE, StringUtils.EMPTY));
            isRowValid = false;
        }
        if (isRowValid && !addedRows.add(new StringBuffer(BusinessConstants.CHAR_COMMA).append(row[1]).toString())) {
            duplicateRows.add(new StringBuffer(row[1]).toString());
            isRowValid = false;
        }
        if (isRowValid) {
            ModelLibraryExecPackageMappingInfo addonPackagesInfo = new ModelLibraryExecPackageMappingInfo(
                    Integer.valueOf(row[0]), row[1], row[2]);
            addonPackagesInfos.add(addonPackagesInfo);
        }

    }

    public static List<String[]> getContentRecords(List<String[]> records) {
        return new ArrayList<String[]>(records.subList(1, records.size()));
    }

    public static String[] getHeaderRecord(List<String[]> records) {
        return records.get(0);
    }

    public static List<ModelLibraryExecPackageMappingInfo> readManifestFile(byte[] manifestArray, List<Error> errors)
            throws SystemException {
        LOGGER.debug("Reading manifest file started");
        CSVReader reader = null;
        List<ModelLibraryExecPackageMappingInfo> addonPackagesInfos = new ArrayList<ModelLibraryExecPackageMappingInfo>();
        try {
            reader = new CSVReader(new InputStreamReader(new ByteArrayInputStream(manifestArray)));
            List<String[]> data = readCsv(reader);
            String[] headerData = getHeaderRecord(data);
            if (validateHeader(headerData)) {
                Set<String> duplicateColumns = new HashSet<String>();
                Set<String> headerRecord = new LinkedHashSet<String>();
                for (String columnName : headerData) {
                    // added to check the csv file is corrupt
                    if (columnName.matches(HDR_REGEX)) {
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
                validateManifestFile(errors, data, addonPackagesInfos);

            } else {
                errors.add(new Error(
                        "Invalid header in manifest file.Require a CSV file with header 'Hierarchy,Library,Version'",
                        BusinessConstants.VALIDATE_R_MANIFEST_FILE, StringUtils.EMPTY));
            }

        } catch (BusinessException ex) {
            errors.add(new Error(ex.getLocalizedMessage(), BusinessConstants.VALIDATE_R_MANIFEST_FILE, ex.getCode()));
        } catch (IOException e) {
            errors.add(new Error(e.getMessage(), BusinessConstants.VALIDATE_R_MANIFEST_FILE, StringUtils.EMPTY));
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                errors.add(new Error(e.getMessage(), BusinessConstants.VALIDATE_R_MANIFEST_FILE, ""));
            }
        }
        LOGGER.debug("Reading manifest file ended");
        return addonPackagesInfos;
    }
    private static boolean validateHeader(String[] headerData) {
        return StringUtils.equals(headerData[0], R_ADDON_LIB_HIERARCHY) && StringUtils.equals(headerData[1], R_ADDON_LIB)
                && StringUtils.equals(headerData[2], R_ADDON_LIB_VERSION);
    }

    private static void validateManifestFile(List<Error> errors, List<String[]> data,
            List<ModelLibraryExecPackageMappingInfo> addonPackagesInfos) {
        List<String[]> contentRecords = getContentRecords(data);
        List<String> duplicateRows = new ArrayList<String>();
        Set<String> addedRows = new HashSet<String>();
        int rownNo = 1;
        for (String[] row : contentRecords) {
            readAndValidateManifestRow(row, addedRows, duplicateRows, errors, rownNo, addonPackagesInfos);
            rownNo = rownNo + 1;
        }
        for (String duplicateRow : duplicateRows) {
            errors.add(new Error("Duplicate library : '" + duplicateRow + "' found in manifest file",
                    BusinessConstants.VALIDATE_R_MANIFEST_FILE, StringUtils.EMPTY));
        }
    }

    private static List<String[]> readCsv(CSVReader reader) throws IOException, BusinessException {
        List<String[]> data = reader.readAll();
        if (isRecordsLessThanTwo(data)) {
            throw new BusinessException(BusinessExceptionCodes.BSE000026, new String[] { "" });
        }
        return data;
    }

}
