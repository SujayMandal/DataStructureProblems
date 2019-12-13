package com.ca.umg.sdc.rest.controller;

import static com.ca.umg.sdc.rest.controller.RAExposedApiControllerHelper.validateAndSetLimit;
import static com.ca.umg.sdc.rest.controller.RAExposedApiControllerHelper.validateAndSetModelNameIfPresent;
import static com.ca.umg.sdc.rest.controller.RAExposedApiControllerHelper.validateAndSetOffset;
import static com.ca.umg.sdc.rest.controller.RAExposedApiControllerHelper.validateAndSetSortParams;
import static com.ca.umg.sdc.rest.controller.RAExposedApiControllerHelper.validateAndSetTntIO;
import static com.ca.umg.sdc.rest.controller.RAExposedApiControllerHelper.validateApiRequestParams;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.transaction.delegate.TransactionDelegate;
import com.ca.umg.business.transaction.info.AdvanceTransactionFilter;
import com.ca.umg.business.transaction.info.TransactionFilter;
import com.ca.umg.business.transaction.info.TransactionFilterForApi;
import com.ca.umg.business.transaction.info.TransactionWrapperForApi;
import com.ca.umg.business.version.delegate.VersionDelegate;
import com.ca.umg.sdc.rest.constants.RaApiConstants;
import com.ca.umg.sdc.rest.raapi.response.RaApiErrorResponse;
import com.ca.umg.sdc.rest.raapi.response.RestResponseForApi;

@Controller
@RequestMapping("/api")
@SuppressWarnings({ "PMD.CyclomaticComplexity" })
public class RealAnalyticsExposedApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RealAnalyticsExposedApiController.class);

    @Inject
    private TransactionDelegate transactionDelegate;

    @Inject
    private SystemParameterProvider systemParameterProvider;

    @Inject
    private VersionDelegate versionDelegate;

   
    /**
     * @param transactionId
     * @param umgTransactionId
     * @param runAsOfDateFrom
     * @param runAsOfDateTo
     * @param fullVersion
     * @param transactionType
     * @param status
     * @param batchId
     * @param includeTenantInput //added for umg-4849
     * @param includeTenantInput //added for umg-4849
     * @param limit
     * @param offset
     * @param sort
     * @param userName //added for umg-4698
     * @param executionGroup //added for umg-4698
     * @param payloadOutputFields //added for umg-4849
     * @param payloadInputFields //added for umg-4849
     * @param pathVariables
     * @param response1
     * @return
     */
    @RequestMapping(value = { "/v1.0/search/{modelName}", "/v1.0/search" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponseForApi<TransactionWrapperForApi> getTransactionDetails(//NOPMD
            @RequestParam(value = RaApiConstants.TRANSACTION_ID, required = false) String transactionId,
            @RequestParam(value = RaApiConstants.UMG_TRANSACTION_ID, required = false) String umgTransactionId,
            @RequestParam(value = RaApiConstants.RUN_AS_OF_DATE_FROM, required = false) String runAsOfDateFrom,
            @RequestParam(value = RaApiConstants.RUN_AS_OF_DATE_TO, required = false) String runAsOfDateTo,
            @RequestParam(value = RaApiConstants.VERSION, required = false) String fullVersion,
            @RequestParam(value = RaApiConstants.TRANSACTION_TYPE, required = false) String transactionType,
            @RequestParam(value = RaApiConstants.STATUS, required = false) String status,
            @RequestParam(value = RaApiConstants.BATCH_ID, required = false) String batchId,
            @RequestParam(value = RaApiConstants.INCLUDE_TNT_INPUT, required = false) String includeTenantInput,
            @RequestParam(value = RaApiConstants.INCLUDE_TNT_OUTPUT, required = false) String includeTenantOutput,
            @RequestParam(value = RaApiConstants.LIMIT, required = false) String limit,
            @RequestParam(value = RaApiConstants.OFFSET, required = false) String offset,
            @RequestParam(value = RaApiConstants.SORT, required = false) String sort,
            @RequestParam(value = RaApiConstants.USER_NAME, required = false) String userName,
            @RequestParam(value = RaApiConstants.EXECUTION_GROUP, required = false) String executionGroup,
            @RequestParam(value = RaApiConstants.PAYLOAD_OUTPUT_FIELDS, required = false) String payloadOutputFields,
            @RequestParam(value = RaApiConstants.PAYLOAD_INPUT_FIELDS, required = false) String payloadInputFields,
            @PathVariable Map<String, String> pathVariables, HttpServletResponse response1) {
        RestResponseForApi<TransactionWrapperForApi> response = new RestResponseForApi<TransactionWrapperForApi>();
        List<RaApiErrorResponse> errorResponseList = new ArrayList<>();
        response.setErrorResponse(errorResponseList);
        TransactionWrapperForApi baseTransactionWrapper = null;
        AdvanceTransactionFilter advanceTransactionFilter = null;
        TransactionFilterForApi transactionFilterForApi = new TransactionFilterForApi();
        try {
            TransactionFilter transactionFilter = new TransactionFilter();
            transactionFilter.setClientTransactionID(transactionId);
            transactionFilter.setRaTransactionID(umgTransactionId);
            transactionFilter.setRunAsOfDateFromString(StringUtils.trimToNull(runAsOfDateFrom));
            transactionFilter.setRunAsOfDateToString(StringUtils.trimToNull(runAsOfDateTo));
            if (pathVariables.containsKey(RaApiConstants.MODEL_NAME)) {
                transactionFilter.setTenantModelName(pathVariables.get(RaApiConstants.MODEL_NAME));
                long count = versionDelegate.getVersionCountByName(transactionFilter.getTenantModelName());
                validateAndSetModelNameIfPresent(count, transactionFilter, response);
            }
            transactionFilter.setFullVersion(StringUtils.trimToNull(fullVersion));
            transactionFilter.setTransactionType(StringUtils.trimToNull(transactionType));
            transactionFilter.setTransactionStatus(StringUtils.trimToNull(status));
            transactionFilter.setBatchId(batchId);
            //added username and execution group for umg-4698
            transactionFilter.setCreatedBy(StringUtils.trimToNull(userName));
            transactionFilter.setExecutionGroup(StringUtils.trimToNull(executionGroup));
            
            String countLimitMetatdata = null;
          //added below if block for umg-4849
            if (!validateAndSetTntIO(includeTenantInput, includeTenantOutput, payloadOutputFields, payloadInputFields, 
                    response,transactionFilterForApi )) {
                countLimitMetatdata = systemParameterProvider.getParameter(RaApiConstants.RA_API_RECORD_LIMIT_TENANT_OUT_ONLY);
                if (StringUtils.isBlank(countLimitMetatdata)) {
                    countLimitMetatdata = Integer.toString(BusinessConstants.NUMBER_TWO_HUNDRED);
                }
            } else {
                countLimitMetatdata = systemParameterProvider.getParameter(RaApiConstants.RA_API_RECORD_LIMIT_METADATA_ONLY);
            }
            
            validateAndSetLimit(StringUtils.trimToNull(limit), RaApiConstants.LIMIT, response, countLimitMetatdata, transactionFilter);
            validateAndSetOffset(StringUtils.trimToNull(offset), transactionFilter, response);
            validateAndSetSortParams(sort, transactionFilter, response);
            // payload to be set in adv tran filter in next implementation

            Boolean areRequestParamsValid = validateApiRequestParams(transactionFilter, errorResponseList);
            if (areRequestParamsValid) {
                baseTransactionWrapper = transactionDelegate.searchTransactionsForRaApi(transactionFilter,
                        advanceTransactionFilter, transactionFilterForApi);
                response.setError(false);
                response.setMessage(RaApiConstants.CONTROLLER_SUCCESS_MESSAGE);
                if (baseTransactionWrapper != null && CollectionUtils.isEmpty(baseTransactionWrapper.getTransactions())) {
                    response.setMessage(RaApiConstants.NO_TRANSACTION_RECORDS_FOUND_API);
                }
                response.setResponse(baseTransactionWrapper);
            } else {
                response.setError(true);
                response.setMessage("Validation Failed");
                response1.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
            }
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setError(true);
            RaApiErrorResponse errorResponse = new RaApiErrorResponse();
            errorResponse.setErrorCode(e.getCode());
            errorResponse.setMessage(e.getLocalizedMessage());
            response.setMessage("failed with errorCode: "+e.getCode()+". "+ e.getLocalizedMessage());
            response.getErrorResponse().add(errorResponse);
        }
        return response;
    }
}
