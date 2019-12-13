package com.ca.umg.sdc.rest.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.tranasction.ExecutionGroupEnum;
import com.ca.framework.core.util.ConversionUtil;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.transaction.info.RAExposedApiEnum;
import com.ca.umg.business.transaction.info.TransactionFilter;
import com.ca.umg.business.transaction.info.TransactionFilterForApi;
import com.ca.umg.business.transaction.info.TransactionWrapperForApi;
import com.ca.umg.business.util.AdminUtil;
import com.ca.umg.sdc.rest.constants.RaApiConstants;
import com.ca.umg.sdc.rest.raapi.info.RaApiSortInfo;
import com.ca.umg.sdc.rest.raapi.response.RaApiErrorResponse;
import com.ca.umg.sdc.rest.raapi.response.RestResponseForApi;

@SuppressWarnings({ "PMD.CyclomaticComplexity" })
public final class RAExposedApiControllerHelper {

    private RAExposedApiControllerHelper() {
    }
    
    public static boolean validateApiRequestParams(TransactionFilter transactionFilter, List<RaApiErrorResponse> errorResponseList) {
        Boolean isValidDate = Boolean.TRUE;
        for (RAExposedApiEnum paramNameEnum : RAExposedApiEnum.values()) {
            switch (paramNameEnum.getExposedApiParam()) {
            case RaApiConstants.MODEL_NAME:
                if (StringUtils.isBlank(transactionFilter.getTenantModelName())) {
                    errorResponseList.add(setErrorResponse(BusinessExceptionCodes.BSE000751, RaApiConstants.MODEL_NAME,
                            "Model name is a required parameter."));
                }
                break;
            case RaApiConstants.TRANSACTION_TYPE:
                if (StringUtils.isBlank(transactionFilter.getTransactionType())) {
                    // setting default value to prod always for now
                    transactionFilter.setTransactionType(RaApiConstants.TRAN_STATUS_PROD);
                } else {
                    if (!StringUtils.equalsIgnoreCase(transactionFilter.getTransactionType(), RaApiConstants.TRAN_STATUS_PROD)) {
                        // enable the below check when u want to allow test transactions
                        // && !StringUtils.equalsIgnoreCase(transactionFilter.getTransactionType(),
                        // RaApiConstants.TRAN_STATUS_TEST)) {
                        errorResponseList.add(setErrorResponse(BusinessExceptionCodes.BSE000752, RaApiConstants.TRANSACTION_TYPE,
                                "Transaction type can have a value of prod only."));
                    }
                }
                break;
            case RaApiConstants.RUN_AS_OF_DATE_FROM:
                if (StringUtils.isNotBlank(transactionFilter.getRunAsOfDateFromString())) {
                    isValidDate = validateRunAsOfDate(transactionFilter.getRunAsOfDateFromString(),
                            RaApiConstants.RUN_AS_OF_DATE_FROM, errorResponseList);
                }
                break;
            case RaApiConstants.RUN_AS_OF_DATE_TO:
                if (StringUtils.isNotBlank(transactionFilter.getRunAsOfDateToString())) {
                    isValidDate = validateRunAsOfDate(transactionFilter.getRunAsOfDateToString(),
                            RaApiConstants.RUN_AS_OF_DATE_TO, errorResponseList);
                }
                break;
            case RaApiConstants.VERSION:
                if (StringUtils.isNotBlank(transactionFilter.getFullVersion())) {
                    validateVersion(transactionFilter.getFullVersion(), errorResponseList);
                }
                break;
            case RaApiConstants.STATUS:
                if (StringUtils.isNotBlank(transactionFilter.getTransactionStatus())) {
                    validateStatus(transactionFilter.getTransactionStatus(), errorResponseList);
                }
                break;
            case RaApiConstants.EXECUTION_GROUP: //added for umg-4698
                if (StringUtils.isNotBlank(transactionFilter.getExecutionGroup())) {
                    validateExecGroup(transactionFilter.getExecutionGroup(), errorResponseList);
                }
                break;
            default:
                break;
            }
        }

        if (isValidDate) {
            checkValidFromToDates(transactionFilter.getRunAsOfDateFromString(), transactionFilter.getRunAsOfDateToString(),
                    errorResponseList);
        }

        return CollectionUtils.isNotEmpty(errorResponseList) ? Boolean.FALSE : Boolean.TRUE;
    }

    private static Boolean validateRunAsOfDate(String runAsOfDate, String fieldName, List<RaApiErrorResponse> errorResponseList) {
        Boolean isValidDate = Boolean.TRUE;
        try {
            AdminUtil.getMillisForUtc(runAsOfDate, null);
        } catch (BusinessException e) {
            errorResponseList.add(setErrorResponse(e.getCode(), fieldName, e.getLocalizedMessage()));
            isValidDate = Boolean.FALSE;
        }
        return isValidDate;
    }

    private static void checkValidFromToDates(String runAsOfDateFrom, String runAsOfDateTo,
            List<RaApiErrorResponse> errorResponseList) {
        try {
            if (runAsOfDateFrom != null && runAsOfDateTo != null
                    && AdminUtil.getMillisForUtc(runAsOfDateFrom, null) > AdminUtil.getMillisForUtc(runAsOfDateTo, null)) {
                errorResponseList.add(setErrorResponse(BusinessExceptionCodes.BSE000753, null,
                        "The Run Dates Range is invalid. Run Date To should be greater than Run Date From."));
            }
        } catch (BusinessException e) {
            errorResponseList.add(setErrorResponse(e.getCode(), null, e.getLocalizedMessage()));
        }
    }

    private static void validateStatus(String status, List<RaApiErrorResponse> errorResponseList) {
        if (StringUtils.isNotBlank(status) && !StringUtils.equalsIgnoreCase(status, BusinessConstants.STATUS_SUCCESS)
                && !StringUtils.equalsIgnoreCase(status, BusinessConstants.STATUS_FAILURE)) {
            errorResponseList.add(setErrorResponse(BusinessExceptionCodes.BSE000755, RaApiConstants.STATUS,
                    "Status can be either success or failure."));
        }
    }
    
  //added for umg-4698
    private static void validateExecGroup(String execGroup, List<RaApiErrorResponse> errorResponseList) {
        if (StringUtils.isNotBlank(execGroup) && !ExecutionGroupEnum.isValid(execGroup)) {
            errorResponseList.add(setErrorResponse(BusinessExceptionCodes.BSE000760, RaApiConstants.EXECUTION_GROUP,
                    "Execution group can be one of Benchmark, Modeled, Ineligible."));
        }
    }

    private static void validateVersion(String version, List<RaApiErrorResponse> errorResponseList) {
        if (StringUtils.isNotBlank(version)) {
            String[] versionArr = version.split("\\.");
            if (versionArr.length > BusinessConstants.NUMBER_TWO) {
                errorResponseList.add(setErrorResponse(BusinessExceptionCodes.BSE000754, RaApiConstants.VERSION,
                        "Version parameter format is invalid."));
            }
        }
    }

    public static void validateAndSetSortParams(String sort, TransactionFilter transactionFilter,
            RestResponseForApi<TransactionWrapperForApi> response) {
        if (StringUtils.isNotBlank(sort)) {
            try {
                if (!(StringUtils.contains(sort, "descending") || StringUtils.contains(sort, "sortColumn"))) {
                    response.getErrorResponse().add(
                            setErrorResponse(BusinessExceptionCodes.BSE000757, RaApiConstants.SORT,
                                    "Invalid properties in sort parameter, allowed properties are : sortColumn and descending."));
                } else {
                    RaApiSortInfo sortInfo = ConversionUtil.convertJson(sort, RaApiSortInfo.class);
                    if (StringUtils.isNotBlank(sortInfo.getSortColumn())) {
                        transactionFilter.setSortColumn(StringUtils.trimToNull(sortInfo.getSortColumn()));
                    } else {
                        transactionFilter.setSortColumn(RaApiConstants.RUN_AS_OF_DATE);
                    }
                    if (sortInfo.getDescending() != null) {
                        transactionFilter.setDescending(sortInfo.getDescending());
                    } else {
                        transactionFilter.setDescending(Boolean.TRUE);
                    }
                }
            } catch (SystemException e) {
                response.getErrorResponse().add(
                        setErrorResponse(e.getCode(), RaApiConstants.SORT, "Request param sort has incorrect format."));
            }
        } else {
            transactionFilter.setSortColumn(RaApiConstants.RUN_AS_OF_DATE);
            transactionFilter.setDescending(Boolean.TRUE);
        }
    }

    public static void validateAndSetModelNameIfPresent(long count, TransactionFilter transactionFilter,
            RestResponseForApi<TransactionWrapperForApi> response) {
        if (count == BusinessConstants.NUMBER_ZERO_LONG) {
            response.getErrorResponse().add(
                    setErrorResponse(BusinessExceptionCodes.BSE000756, RaApiConstants.MODEL_NAME,
                            "No Model name found with this name : " + transactionFilter.getTenantModelName() + "."));
        }
    }

    public static Boolean isValidNumericValue(String inputValue, String fieldName,
            RestResponseForApi<TransactionWrapperForApi> response) {
        Boolean valid = Boolean.TRUE;
        if (!StringUtils.isNumeric(inputValue)) {
            response.getErrorResponse().add(
                    setErrorResponse(BusinessExceptionCodes.BSE000758, fieldName, "The field " + fieldName
                            + " accepts only numeric values."));
            valid = Boolean.FALSE;
        }
        return valid;
    }
    
    public static Boolean isValidBooleanValue(String inputValue, String fieldName,
            RestResponseForApi<TransactionWrapperForApi> response) {
        Boolean valid = Boolean.TRUE;
        String trimmedInputValue = StringUtils.trimToNull(inputValue);
        if (!(StringUtils.equalsIgnoreCase(trimmedInputValue, "true") || 
                StringUtils.equalsIgnoreCase(trimmedInputValue, "false"))) {
            response.getErrorResponse().add(
                    setErrorResponse(BusinessExceptionCodes.BSE000761, fieldName, "The field " + fieldName
                            + " accepts only valid boolean values."));
            valid = Boolean.FALSE;
        }
        return valid;
    }
    
    //added for umg-4849
    /**
     * sets the Tenant I/O and Tenant I/O payload fields in  {@link TransactionFilterForApi}
     * and returns Booelan value which is used to determine the max limit on number of records fetched from mongodb
     * @param includeTenantInput
     * @param includeTenantOutput
     * @param payloadOutputFields
     * @param payloadInputFields
     * @param response
     * @param transactionFilterForApi
     * @return
     */
    public static Boolean validateAndSetTntIO(String includeTenantInput, String includeTenantOutput,
            String payloadOutputFields, String payloadInputFields,
            RestResponseForApi<TransactionWrapperForApi> response,
            TransactionFilterForApi transactionFilterForApi ) {
        Boolean setCountLimitForMetadata = Boolean.FALSE;
        transactionFilterForApi.setIncludeTntOutput(Boolean.FALSE);
        transactionFilterForApi.setIncludeTntInput(Boolean.FALSE);
        
        if (StringUtils.isNotBlank(includeTenantOutput) || StringUtils.isNotBlank(includeTenantInput)) {
            if (StringUtils.isNotBlank(includeTenantOutput) && 
                    isValidBooleanValue(includeTenantOutput, RaApiConstants.INCLUDE_TNT_OUTPUT, response)) {
                String trimmedOutValue = StringUtils.trimToNull(includeTenantOutput);
                transactionFilterForApi.setIncludeTntOutput(Boolean.parseBoolean(trimmedOutValue));
                if (Boolean.parseBoolean(trimmedOutValue) && StringUtils.isNotBlank(payloadOutputFields)
                        && validPayloadFields(payloadOutputFields, RaApiConstants.PAYLOAD_OUTPUT_FIELDS, response)) {
                    transactionFilterForApi.setPayloadOutputFields(getListFromCommaString(payloadOutputFields,
                            RaApiConstants.INCLUDE_TNT_OUTPUT_FIELDS));
                } else {
                    setCountLimitForMetadata = Boolean.TRUE;
                }
            }
            
            if (StringUtils.isNotBlank(includeTenantInput) && 
                    isValidBooleanValue(includeTenantInput, RaApiConstants.INCLUDE_TNT_INPUT, response)) {
                String trimmedInValue = StringUtils.trimToNull(includeTenantInput);
                transactionFilterForApi.setIncludeTntInput(Boolean.parseBoolean(trimmedInValue));
                if (Boolean.parseBoolean(trimmedInValue) && StringUtils.isNotBlank(payloadInputFields)
                        && validPayloadFields(payloadInputFields, RaApiConstants.PAYLOAD_INPUT_FIELDS, response)) {
                    transactionFilterForApi.setPayloadInputFields(getListFromCommaString(payloadInputFields,
                            RaApiConstants.INCLUDE_TNT_INPUT_FIELDS));
                } else {
                    setCountLimitForMetadata = Boolean.TRUE;
                }
            }
            
            if (transactionFilterForApi.getIncludeTntOutput() || transactionFilterForApi.getIncludeTntInput()) {
                setCountLimitForMetadata = Boolean.FALSE;
            }
        } else {
            setCountLimitForMetadata = Boolean.TRUE;
        }
        return setCountLimitForMetadata;
    }
    
    public static void validateAndSetLimit(String limit, String fieldName, RestResponseForApi<TransactionWrapperForApi> response,
            String maxAllowedValue, TransactionFilter transactionFilter) {
        Integer maxAllowedValueInt = StringUtils.isBlank(maxAllowedValue) ? BusinessConstants.NUMBER_TWO_THOUSAND : Integer
                .parseInt(maxAllowedValue);
        if (StringUtils.isBlank(limit)) {
            transactionFilter.setPageSize(maxAllowedValueInt);
        } else {
            Boolean validNumValue = isValidNumericValue(limit, fieldName, response);
            if (validNumValue && Integer.valueOf(limit) <= maxAllowedValueInt) {
                transactionFilter.setPageSize(Integer.valueOf(limit));
            } else if (validNumValue && Integer.valueOf(limit) > maxAllowedValueInt) {
               /* response.getErrorResponse().add(
                        setErrorResponse(BusinessExceptionCodes.BSE000759, fieldName, "The field " + fieldName
                                + " cannot have value more than " + maxAllowedValue + "."));*/
              //removed error for umg-4849
                transactionFilter.setPageSize(maxAllowedValueInt);
            }
        }
    }

    public static void validateAndSetOffset(String offset, TransactionFilter transactionFilter,
            RestResponseForApi<TransactionWrapperForApi> response) {
        if (offset == null) {
            transactionFilter.setPage(0);
        } else {
            if (isValidNumericValue(offset, RaApiConstants.OFFSET, response)) {
                transactionFilter.setPage(Integer.valueOf(offset));
            }
        }
    }

    private static RaApiErrorResponse setErrorResponse(String errorCode, String fieldName, String message) {
        RaApiErrorResponse errorResponse = new RaApiErrorResponse();
        errorResponse.setErrorCode(errorCode);
        errorResponse.setField(fieldName);
        errorResponse.setMessage(message);
        return errorResponse;
    }
    
    private static Boolean validPayloadFields (String payloadField, String fieldName,
            RestResponseForApi<TransactionWrapperForApi> response) {
        Boolean valid = Boolean.TRUE;
        if (!payloadField.matches("[\\w\\s\\.,]*")) {
            response.getErrorResponse().add(
                    setErrorResponse(BusinessExceptionCodes.BSE000762, fieldName, "The field " + fieldName
                            + " accepts only alphanumeric, underscore and dot values."));
            valid = Boolean.FALSE;
        }
        return valid;
    }
    
    private static List<String> getListFromCommaString(final String commaString, final String tntIoPrefix) {
        List<String> retList = new ArrayList<>();
        final String[] valuesInArray = commaString.split("[,]");
        for (String value : valuesInArray) {
            String trimmedVal = StringUtils.trim(value);
            if (StringUtils.isNotBlank(trimmedVal)) {
                retList.add(tntIoPrefix+trimmedVal);
            }
        }
        return retList;
    }
    
}