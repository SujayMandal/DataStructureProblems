/*
 * SyndicateDataUtil.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics 
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.business.util;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.TransformerUtils;
import org.apache.commons.collections.map.TransformedMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.syndicatedata.entity.SyndicateData;
import com.ca.umg.business.syndicatedata.info.ColumnNames;
import com.ca.umg.business.syndicatedata.info.SyndicateDataColumnInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataContainerInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataKeyColumnInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataKeyInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataVersionInfo;
import com.ca.umg.business.syndicatedata.util.DataTypes;
import com.ca.umg.business.validation.ValidationError;

/**
 * This utility provide utility methods for all SyndicateData scenarios.
 * 
 * @author mandavak
 * 
 */
public final class SyndicateDataUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyndicateDataUtil.class);

    /* Appender to append at the end of error code. */
    private static final String SEPERATOR = " \n ";
    private static final String CREATE = "create";
    private static final String UPDATE = "update";

    private SyndicateDataUtil() {
    }

    /**
     * 
     * @param syndicateDataList
     * @return
     */
    public static String getTableNameFromSyndicateDataList(List<SyndicateData> syndicateDataList) {
        return syndicateDataList.get(0).getTableName();
    }

    /**
     * 
     * @param syndicateDataInfo
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    public static SyndicateDataVersionInfo createSyndicateDataVersionInfo(SyndicateDataInfo syndicateDataInfo)
            throws BusinessException, SystemException {
        if (syndicateDataInfo == null) {
            LOGGER.debug("SyndicateDataInfo is Null.");
            throw new BusinessException(BusinessExceptionCodes.BSE000020, new String[] { "SyndicateDataInfo is Null." });
        }
        SyndicateDataVersionInfo syndicateDataVersionInfo = new SyndicateDataVersionInfo();
        List<SyndicateDataInfo> syndicateDataInfoList = new ArrayList<>();
        syndicateDataInfoList.add(syndicateDataInfo);
        syndicateDataVersionInfo.setVersions(syndicateDataInfoList);

        return syndicateDataVersionInfo;
    }

    public static void reportError(List<ValidationError> errors, String createOrUpdate) throws BusinessException {
        if (isNotEmpty(errors)) {
            StringBuilder errorMessages = new StringBuilder();
            for (ValidationError validationError : errors) {
                errorMessages.append(validationError.getMessage()).append(SEPERATOR);
            }
            if (StringUtils.equals(CREATE, createOrUpdate)) {
                throw new BusinessException(BusinessExceptionCodes.BSE000025, new String[] { errorMessages.toString() });
            } else if (StringUtils.equals(UPDATE, createOrUpdate)) {
                throw new BusinessException(BusinessExceptionCodes.BSE000123, new String[] { errorMessages.toString() });
            }
        }
    }

    /**
     * 
     * Get container Name from SyndicateDataContainerInfo.
     * 
     * @param sContainerInfo
     * @return
     */
    public static String getContainerName(SyndicateDataContainerInfo sContainerInfo) {
        return sContainerInfo.getContainerName();
    }

    /**
     * 
     * @param columnInfos
     * @param tableKeys
     * @return
     */
    public static List<SyndicateDataKeyInfo> getSyndicateDatakeyInfoList(List<SyndicateDataColumnInfo> columnInfos,
            Map<String, List<String>> tableKeys) {
        SyndicateDataKeyInfo syndicateDataKeyInfo = null;
        SyndicateDataKeyColumnInfo syndicateDataKeyColumnInfo = null;
        List<SyndicateDataKeyColumnInfo> syndicateDataKeyColumnInfoList = null;
        List<SyndicateDataKeyInfo> syndicateDataKeyInfoList = new ArrayList<>();
        for (Map.Entry<String, List<String>> indexInfo : tableKeys.entrySet()) {
            syndicateDataKeyInfo = new SyndicateDataKeyInfo();
            syndicateDataKeyInfo.setKeyName(indexInfo.getKey());
            syndicateDataKeyColumnInfoList = new ArrayList<>();
            for (SyndicateDataColumnInfo columnInfo : columnInfos) {
                syndicateDataKeyColumnInfo = new SyndicateDataKeyColumnInfo();
                syndicateDataKeyColumnInfo.setColumnName(columnInfo.getDisplayName());
                syndicateDataKeyColumnInfo.setStatus(indexInfo.getValue().contains(columnInfo.getDisplayName()));
                syndicateDataKeyColumnInfoList.add(syndicateDataKeyColumnInfo);
            }
            syndicateDataKeyInfo.setsColumnInfos(syndicateDataKeyColumnInfoList);
            syndicateDataKeyInfoList.add(syndicateDataKeyInfo);
        }
        return syndicateDataKeyInfoList;
    }

    /**
     * this method responsible to convert List<Map<String, Object>> to List<Map<String, String>>
     * 
     * @param dataMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<Map<String, String>> getConvertedData(List<Map<String, Object>> dataMap) {
        List<Map<String, String>> updatedDataList = new ArrayList<>();
        Transformer stringKeyTransformer = TransformerUtils.stringValueTransformer();
        Transformer valTransformer = new Transformer() {
            @Override
            public Object transform(Object input) {
                DateFormat dateFormat = new SimpleDateFormat(BusinessConstants.SYND_DATE_FORMAT, Locale.getDefault());
                String returnValue = null;
                if (input instanceof Date) {
                    returnValue = dateFormat.format(input);
                } else {
                    returnValue = String.valueOf(input);
                }
                return returnValue;
            }
        };
        Map subMap = null;
        for (Map<String, Object> map : dataMap) {
            map.remove(ColumnNames.SYND_VER_ID.getName());
            TransformedMap.decorateTransform(map, stringKeyTransformer, valTransformer);
            subMap = MapUtils.transformedMap(new HashMap<String, String>(), stringKeyTransformer, valTransformer);

            Map<String, Object> formattedMap = new HashMap<String, Object>();
            for (Entry<String, Object> tempEntry : map.entrySet()) {
                String columnName = tempEntry.getKey();
                if (StringUtils.isNotBlank(columnName) && Character.isDigit(columnName.charAt(0))) {
                    formattedMap.put(StringUtils.join(BusinessConstants.SYND_CLMN_NAME_ESC_CHAR, columnName),
                            tempEntry.getValue());
                } else {
                    formattedMap.put(columnName, tempEntry.getValue());
                }
            }
            subMap.putAll(formattedMap);
            updatedDataList.add(subMap);
        }
        return updatedDataList;
    }

    public static String getUIDataType(String dbDataType) {
        String uiDataType = null;
        for (DataTypes dataType : DataTypes.values()) {
            if (dbDataType.equals(dataType.getDbDataType())) {
                uiDataType = dataType.getUiDataType();
            }
        }
        return uiDataType;
    }
}
