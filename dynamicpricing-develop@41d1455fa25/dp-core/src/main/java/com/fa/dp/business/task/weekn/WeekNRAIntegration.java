package com.fa.dp.business.task.weekn;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.MDC;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.security.concurrent.DelegatingSecurityContextCallable;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fa.dp.business.command.annotation.CommandDescription;
import com.fa.dp.business.command.base.AbstractCommand;
import com.fa.dp.business.command.dao.CommandDAO;
import com.fa.dp.business.command.entity.Command;
import com.fa.dp.business.command.info.CommandInfo;
import com.fa.dp.business.command.info.CommandProcess;
import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.constant.DPProcessParamAttributes;
import com.fa.dp.business.filter.bo.DPProcessWeekNParamsBO;
import com.fa.dp.business.filter.constant.DPProcessFilterParams;
import com.fa.dp.business.util.IntegrationType;
import com.fa.dp.business.util.ThreadPoolExecutorUtil;
import com.fa.dp.business.util.TransactionStatus;
import com.fa.dp.business.validator.dao.DPWeekNIntgAuditDao;
import com.fa.dp.business.weekn.dao.DPProcessWeekNParamsDao;
import com.fa.dp.business.weekn.entity.DPProcessWeekNParam;
import com.fa.dp.business.weekn.entity.DPWeekNIntgAudit;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamEntryInfo;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamInfo;
import com.fa.dp.core.cache.CacheManager;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.rest.RAClient;
import com.fa.dp.core.systemparam.util.AppParameterConstant;
import com.fa.dp.core.util.DateConversionUtil;
import com.fa.dp.core.util.KeyValue;
import com.fa.dp.core.util.RAClientConstants;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Named
@Scope("prototype")
@CommandDescription(name = "weekNRAIntegrarion")
public class WeekNRAIntegration extends AbstractCommand {

    @Inject
    private WeekNRACall weekNRACall;

    @Inject
    private CacheManager cacheManager;

    @Inject
    private RAClient raClient;

    @Inject
    private DPWeekNIntgAuditDao dpWeekNIntgAuditDao;

    @Inject
    private DPProcessWeekNParamsDao dpProcessWeekNParamsDao;

    @Inject
    private CommandDAO commandDAO;

    @Inject
    private DPProcessWeekNParamsBO dpProcessWeekNParamsBO;

    @Value("${WEEKN_CONCURRENT_RACALL_POOL_SIZE}")
    private int concurrentRaCallPoolSize;

    private ExecutorService executorService;

    @PostConstruct
    public void initializeTemplate() {
        executorService = ThreadPoolExecutorUtil.getFixedSizeThreadPool(concurrentRaCallPoolSize);
    }

    @PreDestroy
    public void destroy() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void execute(Object data) throws SystemException {
        DPProcessWeekNParamEntryInfo dpProcessParamEntryInfo = null;
        long startTime = 0;
        if (checkData(data, DPProcessWeekNParamEntryInfo.class)) {
            dpProcessParamEntryInfo = ((DPProcessWeekNParamEntryInfo) data);

            //Execute RACall only in actual process flow
            if (!dpProcessParamEntryInfo.isFetchProcess() && !dpProcessParamEntryInfo.getColumnEntries().isEmpty()) {

                log.info("weekNRAIntegrarion -> processTask started.");
                startTime = DateTime.now().getMillis();
                String nrzModelName = String
                        .valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_NRZ_WEEKN_MODEL_NAME));
                String nrzModelMajorVersion = String
                        .valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_NRZ_WEEKN_MAJOR_VERSION));
                String nrzModelMinorVersion = String
                        .valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_NRZ_WEEKN_MINOR_VERSION));
                String nrzauthToken = String
                        .valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_NRZ_WEEKN_AUTH_TOKEN));

                String ocnModelName = String
                        .valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_OCN_WEEKN_MODEL_NAME));
                String ocnModelMajorVersion = String
                        .valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_OCN_WEEKN_MAJOR_VERSION));
                String ocnModelMinorVersion = String
                        .valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_OCN_WEEKN_MINOR_VERSION));
                String ocnauthToken = String
                        .valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_OCN_WEEKN_AUTH_TOKEN));

                String nrzTenantCode = String
                        .valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_NRZ_WEEKN_TENANT_CODE));
                String ocnTenantCode = String
                        .valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_OCN_WEEKN_TENANT_CODE));

                // Phh Ra Call params for WeekN
                String phhModelName = String
                        .valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_PHH_WEEKN_MODEL_NAME));
                String phhModelMajorVersion = String
                        .valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_PHH_WEEKN_MAJOR_VERSION));
                String phhModelMinorVersion = String
                        .valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_PHH_WEEKN_MINOR_VERSION));
                String phhauthToken = String
                        .valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_PHH_WEEKN_AUTH_TOKEN));
                String phhTenantCode = String
                        .valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_PHH_WEEKN_TENANT_CODE));


                List<Future<KeyValue<Map, DPProcessWeekNParamInfo>>> futureList = new ArrayList<>();
                List<DPProcessWeekNParamInfo> successEntries = new ArrayList<>();
                try {
                    for (DPProcessWeekNParamInfo info : dpProcessParamEntryInfo.getColumnEntries()) {
                        if (StringUtils.equals(info.getClassification(), DPProcessParamAttributes.NRZ.getValue())) {
                            Map<String, Object> raRequest = weekNRACall.prepareRAMapping(info, nrzModelName,
                                    nrzModelMajorVersion, nrzModelMinorVersion);
                            if (null != raRequest && !raRequest.isEmpty()) {
                                Future<KeyValue<Map, DPProcessWeekNParamInfo>> future = executorService.submit(executeDPAModel(nrzTenantCode, nrzModelName,
                                        nrzModelMajorVersion + RAClientConstants.CHAR_DOT + nrzModelMinorVersion, nrzauthToken, raRequest, info));
                                futureList.add(future);
                            }
                        } else if (StringUtils.equals(info.getClassification(), DPProcessParamAttributes.OCN.getValue())) {
                            Map<String, Object> raRequest = weekNRACall.prepareRAMapping(info, ocnModelName,
                                    ocnModelMajorVersion, ocnModelMinorVersion);
                            if (null != raRequest && !raRequest.isEmpty()) {
                                Future<KeyValue<Map, DPProcessWeekNParamInfo>> future = executorService.submit(executeDPAModel(ocnTenantCode, ocnModelName,
                                        ocnModelMajorVersion + RAClientConstants.CHAR_DOT + ocnModelMinorVersion, ocnauthToken, raRequest, info));
                                futureList.add(future);
                            }
                        } else if (StringUtils.equals(info.getClassification(), DPProcessParamAttributes.PHH.getValue())) {
                            Map<String, Object> raRequest = weekNRACall.prepareRAMapping(info, phhModelName,
                                    phhModelMajorVersion, phhModelMinorVersion);
                            if (null != raRequest && !raRequest.isEmpty()) {
                                Future<KeyValue<Map, DPProcessWeekNParamInfo>> future = executorService.submit(executeDPAModel(phhTenantCode, phhModelName,
                                        phhModelMajorVersion + RAClientConstants.CHAR_DOT + phhModelMinorVersion, phhauthToken, raRequest, info));
                                futureList.add(future);
                            }
                        }
                    }
                    for (Future<KeyValue<Map, DPProcessWeekNParamInfo>> keyValueFuture : futureList) {
                        try {
                            KeyValue<Map, DPProcessWeekNParamInfo> keyValuePair = keyValueFuture.get();
                            DPProcessWeekNParamInfo dPProcessParamInfo = keyValuePair.getValue();
                            if (keyValuePair.getKey() != null) {
                                populateWeekNOutputParam(dPProcessParamInfo, keyValuePair.getKey(), startTime);
                            }
                            if (dPProcessParamInfo.getCommand() == null) {
                                successEntries.add(dPProcessParamInfo);
                            }
                        } catch (Exception ex) {
                            log.error("Error occured during RA execution for weekn : ", ex);
                        }
                    }
                    dpProcessParamEntryInfo.setColumnEntries(successEntries);
                } catch (Exception e) {
                    log.error("Error occurred in WeekNRAIntegration : ", e);
                }
                log.info("Time taken for weekNRAIntegrarion : " + (DateTime.now().getMillis() - startTime) + "ms");
                log.info("weekNRAIntegrarion -> processTask ended.");
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private Callable<KeyValue<Map, DPProcessWeekNParamInfo>> executeDPAModel(String tenantCode, String modelName,
                                                                             String modelVersion, String authToken, Map<String, Object> raRequest, DPProcessWeekNParamInfo info) {
        final Map<String, String> mdcContext = MDC.getCopyOfContextMap();
        return DelegatingSecurityContextCallable.create(new Callable<KeyValue<Map, DPProcessWeekNParamInfo>>() {
            @Override
            public KeyValue<Map, DPProcessWeekNParamInfo> call() throws Exception {
                if (mdcContext != null)
                    MDC.setContextMap(mdcContext);
                MDC.put(RAClientConstants.LOAN_NUMBER, info.getAssetNumber());
                Map response = raClient.executeWeekNDPAModel(tenantCode, modelName, modelVersion, authToken, raRequest, info);
                MDC.remove(RAClientConstants.LOAN_NUMBER);
                return new KeyValue<Map, DPProcessWeekNParamInfo>(response, info);
            }
        }, SecurityContextHolder.getContext());
    }

    @SuppressWarnings("rawtypes")
    private void populateWeekNOutputParam(DPProcessWeekNParamInfo info, Map response, long startTime) {

        log.info("WeekNRAService -> DPProcessWeekZeroParamInfo ID : " + (info != null ? info.getId() : "NA"));
        log.info("WeekNRAService -> response : " + response);

        DocumentContext parsedContext = JsonPath.parse(response);

        boolean success = parsedContext.read("$.header.success", Boolean.class);
        log.info("$.header.success value : " + success);

        String errorMessage = null;
        Optional<DPProcessWeekNParam> dpProcessWeekNParamResult = dpProcessWeekNParamsDao.findById(info.getId());
        DPProcessWeekNParam dpProcessWeekNParam = dpProcessWeekNParamResult.get();
        if (success) {
            String mostRecentListStatus = parsedContext.read("$.data.output.Most_Recent_List_Status", String.class);
            String listEndDate = parsedContext.read("$.data.output.Most_Recent_List_End_Date", String.class);
            String recentPropList = parsedContext.read("$.data.output.Most_Recent_Property_Status", String.class);
            BigDecimal recentListPrice = BigDecimal
                    .valueOf(parsedContext.read("$.data.output.Most_Recent_List_Price", Double.class));
            BigDecimal dollarAdjusRec = BigDecimal.valueOf(
                    parsedContext.read("$.data.output.List_Price_Dollar_Adjustment_Recommendation", Double.class));
            BigDecimal percentAdjusRec = BigDecimal.valueOf(
                    parsedContext.read("$.data.output.List_Price_Percent_Adjustment_Recommendation", Double.class));
            String modelVer = parsedContext.read("$.data.output.Model_Version", String.class);


            if (StringUtils.equalsIgnoreCase(mostRecentListStatus, RAClientConstants.SUCCESSFUL)
                    || StringUtils.equalsIgnoreCase(mostRecentListStatus, RAClientConstants.UNDERREVIEW)) {
                // for mostRecentListStatus =SUCCESSFUL/UNDERREVIEW AS per story DP-238
                // we need to set delivery date to null
                dpProcessWeekNParam.setDeliveryDate(null);
            } else {
                dpProcessWeekNParam.setMostRecentListStatus(mostRecentListStatus);
                dpProcessWeekNParam.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
            }
            dpProcessWeekNParam.setClassification(info.getClassification());
            dpProcessWeekNParam.setMostRecentListEndDate(listEndDate);
            dpProcessWeekNParam.setMostRecentPropertyStatus(recentPropList);
            dpProcessWeekNParam.setMostRecentListPrice(recentListPrice);
            dpProcessWeekNParam.setLpDollarAdjustmentRec(dollarAdjusRec);
            dpProcessWeekNParam.setLpPercentAdjustmentRec(percentAdjusRec);
            dpProcessWeekNParam.setModelVersion(modelVer);
            dpProcessWeekNParam.setEligible(DPProcessParamAttributes.ELIGIBLE.getValue());
            dpProcessWeekNParam.setClientCode(info.getClientCode());
            dpProcessWeekNParam.setPrivateMortgageInsurance(info.getPrivateMortgageInsurance());
            dpProcessWeekNParam.setSellerOccupiedProperty(info.getSellerOccupiedProperty());
            dpProcessWeekNParam.setState(info.getState());
            dpProcessWeekNParam.setZipCode(info.getZipCode());
            dpProcessWeekNParam.setInitialValuation(info.getInitialValuation());
            dpProcessWeekNParam.setRbidPropIdVcPk(info.getRbidPropIdVcPk());
            dpProcessWeekNParam = dpProcessWeekNParamsDao.save(dpProcessWeekNParam);
            BeanUtils.copyProperties(info, dpProcessWeekNParam);

        } else {
            errorMessage = parsedContext.read("$.header.errorMessage", String.class);
            log.error("RA FAILURE " + DPProcessParamAttributes.NOTES_RA.getValue());
            dpProcessWeekNParam.setEligible(DPProcessFilterParams.ELIGIBLE.getValue());
            dpProcessWeekNParam.setExclusionReason(DPProcessParamAttributes.NOTES_RA.getValue());
            String process = null;
            if (DPProcessParamAttributes.OCN.getValue().equals(dpProcessWeekNParam.getClassification()))
                process = CommandProcess.WEEKN_OCN.getCommmandProcess();
            else if (DPProcessParamAttributes.NRZ.getValue().equals(dpProcessWeekNParam.getClassification()))
                process = CommandProcess.WEEKN_NRZ.getCommmandProcess();
            else if (DPProcessParamAttributes.PHH.getValue().equals(dpProcessWeekNParam.getClassification()))
                process = CommandProcess.WEEKN_PHH.getCommmandProcess();
            List<Command> command = commandDAO.findByProcess(process, DPAConstants.RA_FAIL_FILTER);
            CommandInfo commandInfo = convert(command.get(0), CommandInfo.class);
            info.setCommand(commandInfo);
            info.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
            dpProcessWeekNParam.setCommand(command.get(0));
            dpProcessWeekNParam.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
            dpProcessWeekNParam.setSellerOccupiedProperty(info.getSellerOccupiedProperty());
            dpProcessWeekNParam.setState(info.getState());
            dpProcessWeekNParam.setZipCode(info.getZipCode());
            dpProcessWeekNParam.setInitialValuation(info.getInitialValuation());
            dpProcessWeekNParam.setClientCode(info.getClientCode());
            dpProcessWeekNParam.setPrivateMortgageInsurance(info.getPrivateMortgageInsurance());
            dpProcessWeekNParam.setRbidPropIdVcPk(info.getRbidPropIdVcPk());
            dpProcessWeekNParam = dpProcessWeekNParamsDao.save(dpProcessWeekNParam);
        }

        auditRAStatus(dpProcessWeekNParam, success, startTime, errorMessage);

    }

    private void auditRAStatus(DPProcessWeekNParam dpProcessWeekNParam, boolean success, long startTime, String errorMessage) {
        CompletableFuture.runAsync(() -> {
            DPWeekNIntgAudit dpWeekNIntgAudit = new DPWeekNIntgAudit();
            dpWeekNIntgAudit.setEventType(IntegrationType.RA_INTEGRATION.getIntegrationType());
            dpWeekNIntgAudit.setStatus(
                    success ? TransactionStatus.SUCCESS.getTranStatus() : TransactionStatus.FAIL.getTranStatus());
            dpWeekNIntgAudit.setErrorDescription(errorMessage);
            dpWeekNIntgAudit.setDpProcessWeekNParam(dpProcessWeekNParam);
            dpWeekNIntgAudit.setStartTime(startTime);
            dpWeekNIntgAudit.setEndTime(System.currentTimeMillis());
            dpWeekNIntgAuditDao.save(dpWeekNIntgAudit);
        });
    }

}
