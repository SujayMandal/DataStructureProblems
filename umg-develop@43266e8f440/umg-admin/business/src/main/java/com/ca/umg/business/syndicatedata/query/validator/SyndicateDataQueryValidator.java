package com.ca.umg.business.syndicatedata.query.validator;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.mid.extraction.info.DatatypeInfo.Datatype;
import com.ca.umg.business.syndicatedata.entity.SyndicateDataQuery;
import com.ca.umg.business.syndicatedata.entity.SyndicateDataQueryInput;
import com.ca.umg.business.syndicatedata.entity.SyndicateDataQueryOutput;
import com.ca.umg.business.syndicatedata.info.SyndicateDataColumnInfo;
import com.ca.umg.business.syndicatedata.util.QueryResultTypes;

public class SyndicateDataQueryValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyndicateDataQueryValidator.class);

    public List<String> validate(Map<String, String> tableAliasMap, Map<String, String> colWithTableAliasMap,
            Map<String, String> colWithValueNameMap, Set<SyndicateDataQueryInput> inputParameters,
            Map<String, List<SyndicateDataColumnInfo>> tableColumnInfoMap, SyndicateDataQuery synDataQuery)
            throws SystemException, BusinessException {
        return verifyColumnDataTypes(tableAliasMap, colWithTableAliasMap, colWithValueNameMap, inputParameters,
                tableColumnInfoMap);
    }

    private List<String> verifyColumnDataTypes(Map<String, String> tableAliasMap, Map<String, String> colWithTableAliasMap,
            Map<String, String> colWithValueNameMap, Set<SyndicateDataQueryInput> inputParameters,
            Map<String, List<SyndicateDataColumnInfo>> tableColumnInfoMap) throws SystemException {
        List<String> dataTypeMisMatchList = new ArrayList<String>();

        // start iterating through the columns
        Iterator<String> colIterator = colWithTableAliasMap.keySet().iterator();
        while (colIterator.hasNext()) {
            String colName = colIterator.next();
            List<SyndicateDataColumnInfo> tableInfoList = getTableColumnInfoList(tableAliasMap, colWithTableAliasMap,
                    tableColumnInfoMap, colName);
            boolean found = false;
            String dataType = null;
            for (SyndicateDataColumnInfo syndicateDataColumnInfo : tableInfoList) {
                if (syndicateDataColumnInfo.getDisplayName().equalsIgnoreCase(colName)) {
                    found = true;
                    dataType = syndicateDataColumnInfo.getColumnType();
                    break;
                }
            }
            if (found) {
                Iterator<SyndicateDataQueryInput> inputParamsItr = inputParameters.iterator();
                while (inputParamsItr.hasNext()) {
                    SyndicateDataQueryInput inputParam = inputParamsItr.next();
                    if (inputParam.getName().equalsIgnoreCase(colWithValueNameMap.get(colName))
                            && !inputParam.getDataType().equalsIgnoreCase(dataType)) {
                        dataTypeMisMatchList.add("\n[" + colName + " should be " + dataType + "] ");
                    }
                }
            }
        }
        return dataTypeMisMatchList;
    }

    private List<SyndicateDataColumnInfo> getTableColumnInfoList(Map<String, String> tableAliasMap,
            Map<String, String> colWithTableAliasMap, Map<String, List<SyndicateDataColumnInfo>> tableColumnInfoMap,
            String colName) throws SystemException {
        String tableAlias = colWithTableAliasMap.get(colName);
        String tableName = tableAliasMap.get(tableAlias);
        if (tableName == null) {
            LOGGER.error("BSE000055:Exception occured as table aliases are not matching with the input parameters");
            SystemException.newSystemException(BusinessExceptionCodes.BSE000055, new String[] { tableAlias, colName });
        }
        return tableColumnInfoMap.get(tableName);
    }

    public void validateReferences(Map<String, Boolean> references) throws BusinessException {
        List<String> delParams = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : references.entrySet()) {
            if (entry.getValue()) {
                delParams.add(entry.getKey());
            }
        }
        if (CollectionUtils.isNotEmpty(delParams)) {
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000056,
                    new String[] { StringUtils.join(delParams, "/t") });
        }
    }

    /**
     * 
     * @param synDataQryInfo
     * @throws BusinessException
     */
    public void validateQReturnTypes(SyndicateDataQuery synDataQryInfo) throws BusinessException {
        String rowType = synDataQryInfo.getRowType();
        String dataType = synDataQryInfo.getDataType();
        int numberOfRows = synDataQryInfo.getOutputParameters().size();
        boolean selSameDataType = isDataTypesSame(synDataQryInfo.getOutputParameters());
        if (rowType.equalsIgnoreCase(String.valueOf(QueryResultTypes.SINGLEROW))) {
            validateSingleRow(rowType, dataType, numberOfRows, selSameDataType);
        } else {
            validateMultiRow(rowType, dataType, numberOfRows, selSameDataType);
        }
    }

    /**
     * 
     * @param synDataQryInfo
     */
    public void validateInputParameters(SyndicateDataQuery synDataQryInfo) throws BusinessException {
        Set<SyndicateDataQueryInput> syndDataQueInpSet = synDataQryInfo.getInputParameters();
        String inptDataType = null;
        String inptSampVal = null;
        String errorDataType = null;
        StringBuilder stringBuilder = new StringBuilder();

        for (SyndicateDataQueryInput syndDataQueInput : syndDataQueInpSet) {
            inptDataType = syndDataQueInput.getDataType();
            inptSampVal = syndDataQueInput.getSampleValue();
            switch (Datatype.valueOf(inptDataType)) {
            case DOUBLE:
                errorDataType = checkDoubleTypeError(inptSampVal, syndDataQueInput.getName());
                break;
            case NUMERIC:
            case INTEGER:
                errorDataType = checkIntegerTypeError(inptSampVal, syndDataQueInput.getName());
                break;
            case DATE:
            case DATETIME:
                errorDataType = checkDateTimeTypeError(inptSampVal, syndDataQueInput.getName(), syndDataQueInput.getDataTypeFormat());
                break;
            case BOOLEAN:
                errorDataType = checkBooleanTypeError(inptSampVal, syndDataQueInput.getName());
                break;
            default:
                break;
            }
            if (StringUtils.isNoneEmpty(errorDataType)) {
                stringBuilder.append(errorDataType).append(BusinessConstants.CHAR_COMMA);
                errorDataType = BusinessConstants.EMPTY_STRING;
            }
        }

        if (StringUtils.isNotEmpty(stringBuilder.toString())) {
            stringBuilder.replace(stringBuilder.length() - 1, stringBuilder.length(), "");
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000080, new String[] { stringBuilder.toString() });
        }
    }

    private String checkBooleanTypeError(String inptSampVal, String name) {
        String stringValue = null;
        if (!(inptSampVal.equalsIgnoreCase(Boolean.TRUE.toString()) || inptSampVal.equalsIgnoreCase(Boolean.FALSE.toString()))) {
            stringValue = name;
        }
        return stringValue;
    }

    private String checkIntegerTypeError(String inptSampVal, String name) {
        String stringValue = null;
        String tempInputValue = null;
        if (inptSampVal.contains("-")) {
            tempInputValue = inptSampVal.substring(BusinessConstants.NUMBER_ONE, inptSampVal.length());
            if (!NumberUtils.isDigits(tempInputValue)) {
                stringValue = name;
            }
        } else if (!NumberUtils.isDigits(inptSampVal)) {
            stringValue = name;
        }
        return stringValue;
    }

    private String checkDoubleTypeError(String inptSampVal, String name) {
        String stringValue = null;
        Pattern doublePattern = Pattern.compile("-?\\d+(\\.\\d*)?");
        if (!doublePattern.matcher(inptSampVal).matches()) {
            stringValue = name;
        }
        return stringValue;
    }

    /**
     * 
     * @param inptSampVal
     * @return
     */
    private String checkDateTimeTypeError(String inptSampVal, String inputFieldName, String dateFormat) {
        DateFormat dateFormatTime = new SimpleDateFormat(BusinessConstants.SYND_DATE_TIME_FORMAT, Locale.getDefault());
        dateFormatTime.isLenient();
        String errorCode = null;
        if (inptSampVal.length() == 10 || inptSampVal.length() == 11 || inptSampVal.length() == 17 || inptSampVal.length() == 16) {
            dateFormatTime = new SimpleDateFormat(dateFormat, Locale.getDefault());
        } else {
            errorCode = inputFieldName;
        }
        try {
            dateFormatTime.parse(inptSampVal);
        } catch (ParseException e) {
            errorCode = inputFieldName;
        }
        return errorCode;
    }

    /**
     * 
     * @param rowType
     * @param dataType
     * @param numberOfRows
     * @param selSameDataType
     * @throws BusinessException
     */
    private void validateMultiRow(String rowType, String dataType, int numberOfRows, boolean selSameDataType)
            throws BusinessException {
        if (dataType.equalsIgnoreCase(QueryResultTypes.PRIMITIVE.getDatatype())) {
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000074,
                    new String[] { rowType, dataType, String.valueOf(numberOfRows) });
        } else if (numberOfRows == BusinessConstants.NUMBER_ONE
                && !(dataType.equalsIgnoreCase(QueryResultTypes.ARRAY.getDatatype()) || dataType
                        .equalsIgnoreCase(QueryResultTypes.SINGLE_DIM_ARRAY.getDatatype()))) {
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000074,
                    new String[] { rowType, dataType, String.valueOf(numberOfRows) });
        } else if (numberOfRows > BusinessConstants.NUMBER_ONE
                && (dataType.equalsIgnoreCase(QueryResultTypes.ARRAY.getDatatype()) || dataType
                        .equalsIgnoreCase(QueryResultTypes.SINGLE_DIM_ARRAY.getDatatype())) && !selSameDataType) {
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000075,
                    new String[] { rowType, dataType, String.valueOf(numberOfRows) });
        }
    }

    /**
     * 
     * @param rowType
     * @param dataType
     * @param numberOfRows
     * @param selSameDataType
     * @throws BusinessException
     */
    private void validateSingleRow(String rowType, String dataType, int numberOfRows, boolean selSameDataType)
            throws BusinessException {
        if (numberOfRows == BusinessConstants.NUMBER_ONE) {
            if (!dataType.equalsIgnoreCase(QueryResultTypes.PRIMITIVE.getDatatype())) {
                BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000074, new String[] { rowType, dataType,
                        String.valueOf(numberOfRows) });
            }
        } else if (numberOfRows > BusinessConstants.NUMBER_ONE
                && dataType.equalsIgnoreCase(QueryResultTypes.PRIMITIVE.getDatatype())) {
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000074,
                    new String[] { rowType, dataType, String.valueOf(numberOfRows) });
        } else if (numberOfRows > BusinessConstants.NUMBER_ONE
                && (dataType.equalsIgnoreCase(QueryResultTypes.ARRAY.getDatatype()) || dataType
                        .equalsIgnoreCase(QueryResultTypes.SINGLE_DIM_ARRAY.getDatatype())) && !selSameDataType) {
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000075,
                    new String[] { rowType, dataType, String.valueOf(numberOfRows) });
        }
    }

    private boolean isDataTypesSame(Set<SyndicateDataQueryOutput> outputParameters) {
        boolean sameDataType = Boolean.TRUE;
        Set<String> dataTypes = new HashSet<>();
        for (SyndicateDataQueryOutput syndDataQueOutput : outputParameters) {
            dataTypes.add(syndDataQueOutput.getDataType().toUpperCase());
        }
        if (dataTypes.size() > BusinessConstants.NUMBER_ONE) {
            sameDataType = Boolean.FALSE;
        }
        return sameDataType;
    }

    public void validateReferences(Map<String, Boolean> references, Map<String, String> testedAliasColNameMap,
            Map<String, String> newAliasColNameMap) throws BusinessException {
        List<String> params = new ArrayList<>();
        if (testedAliasColNameMap != null && newAliasColNameMap != null) {
            for (Map.Entry<String, Boolean> entry : references.entrySet()) {
                if (entry.getValue()) {
                    String alias = entry.getKey().split("/")[1];
                    if (!testedAliasColNameMap.get(alias).equalsIgnoreCase(newAliasColNameMap.get(alias))) {
                        params.add(entry.getKey());
                    }
                }
            }

            if (CollectionUtils.isNotEmpty(params)) {
                BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000056,
                        new String[] { StringUtils.join(params, "/t") });
            }
        }
    }
}
