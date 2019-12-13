package com.fa.dp.business.task;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.MDC;
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
import com.fa.dp.business.filter.bo.DPProcessParamsBO;
import com.fa.dp.business.util.IntegrationType;
import com.fa.dp.business.util.ThreadPoolExecutorUtil;
import com.fa.dp.business.util.TransactionStatus;
import com.fa.dp.business.validation.input.info.DPProcessParamEntryInfo;
import com.fa.dp.business.validation.input.info.DPProcessParamInfo;
import com.fa.dp.business.validator.bo.DPFileProcessBO;
import com.fa.dp.business.week0.entity.DPProcessParam;
import com.fa.dp.business.week0.entity.DynamicPricingIntgAudit;
import com.fa.dp.core.cache.CacheManager;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.rest.RAClient;
import com.fa.dp.core.systemparam.util.AppParameterConstant;
import com.fa.dp.core.util.ConversionUtil;
import com.fa.dp.core.util.DateConversionUtil;
import com.fa.dp.core.util.KeyValue;
import com.fa.dp.core.util.RAClientConstants;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

@Slf4j
@Named
@Scope("prototype")
@CommandDescription(name = "week0RAInputPayload")
public class Week0RAInputPayload extends AbstractCommand {

	private static final String FALSE = "FALSE";

	private static final String TRUE = "TRUE";

	private static final double OCN_PCT_AV_LOWER_SLAB = 1.06;

	private static final double OCN_PCT_AV_UPPER_SLAB = 1.2;

	private static final double PHH_PCT_AV_LOWER_SLAB = 1.06;

	private static final double PHH_PCT_AV_UPPER_SLAB = 1.2;

	private static final double NRZ_PCT_AV_UPPER_SLAB = 1.1;

	private static final int NRZ_PCT_AV_LOWER_SLAB = 1;

	@Inject
	private RAClient raClient;

	@Inject
	private Week0RACall raCall;

	@Inject
	private CacheManager cacheManager;

	@Inject
	private DPProcessParamsBO dpProcessParamsBO;

	@Inject
	private DPFileProcessBO dpFileProcessBO;

	@Inject
	private CommandDAO commandDAO;

	@Value("${WEEK0_CONCURRENT_RACALL_POOL_SIZE}")
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

	@Override
	public void execute(Object data) throws SystemException {
		log.info("Week0RAInputPayload -> processTask started.");
		Long start = DateTime.now().getMillis();
		DPProcessParamEntryInfo dpProcessParamEntryInfo = null;
		if (checkData(data, DPProcessParamEntryInfo.class)) {
			dpProcessParamEntryInfo = ((DPProcessParamEntryInfo) data);
			if (null != dpProcessParamEntryInfo.getColumnEntries()
					&& !dpProcessParamEntryInfo.getColumnEntries().isEmpty()
					&& dpProcessParamEntryInfo.getColumnEntries().size() > 0) {
				String nrzModelName = String
						.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_NRZ_WEEK0_MODEL_NAME));
				String nrzModelMajorVersion = String
						.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_NRZ_WEEK0_MAJOR_VERSION));
				String nrzModelMinorVersion = String
						.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_NRZ_WEEK0_MINOR_VERSION));
				String nrzPriceModeInput = String
						.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_NRZ_PRICE_MODE_INPUT));
				String nrzauthToken = String
						.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_NRZ_AUTH_TOKEN));

				String ocnModelName = String
						.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_OCN_WEEK0_MODEL_NAME));
				String ocnModelMajorVersion = String
						.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_OCN_WEEK0_MAJOR_VERSION));
				String ocnModelMinorVersion = String
						.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_OCN_WEEK0_MINOR_VERSION));
				String ocnPriceModeInput = String
						.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_OCN_PRICE_MODE_INPUT));
				String ocnAuthToken = String
						.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_OCN_AUTH_TOKEN));

				String phhModelName = String
						.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_PHH_WEEK0_MODEL_NAME));
				String phhModelMajorVersion = String
						.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_PHH_WEEK0_MAJOR_VERSION));
				String phhModelMinorVersion = String
						.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_PHH_WEEK0_MINOR_VERSION));
				String phhPriceModeInput = String
						.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_PHH_PRICE_MODE_INPUT));
				String phhAuthToken = String
						.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_PHH_AUTH_TOKEN));

				String nrzTenantCode = String
						.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_NRZ_WEEK0_TENANT_CODE));
				String ocnTenantCode = String
						.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_OCN_WEEK0_TENANT_CODE));
				String phhTenantCode = String
						.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_PHH_WEEK0_TENANT_CODE));

				List<Future<KeyValue<Map, DPProcessParamInfo>>> futureList = new ArrayList<>();

				// raRequest will come from RA Call
				try {
					for (DPProcessParamInfo info : dpProcessParamEntryInfo.getColumnEntries()) {

						Long startTime = DateConversionUtil.getMillisFromUtcToEst(System.currentTimeMillis());

						if (StringUtils.equals(info.getClassification(), DPProcessParamAttributes.NRZ.getValue())) {
							info.setPrMode(nrzPriceModeInput);
							Map<String, Object> raRequest = raCall.prepareRAMapping(info, nrzModelName,
									nrzModelMajorVersion, nrzModelMinorVersion, nrzPriceModeInput);
							if (null != raRequest && !raRequest.isEmpty()) {
								try {
									Future<KeyValue<Map, DPProcessParamInfo>> future = executorService
											.submit(executeDPAModel(nrzTenantCode, nrzModelName,
													nrzModelMajorVersion + RAClientConstants.CHAR_DOT + nrzModelMinorVersion, nrzauthToken, raRequest, info));
									futureList.add(future);
								} catch (Exception e) {

									String errorDetail = dpFileProcessBO.saveDPProcessErrorDetail(info.getId(),
											IntegrationType.RA_INTEGRATION.getIntegrationType(), info.getErrorDetail(), e);

									info.setErrorDetail(errorDetail);

									raExceptionExecute(info);

									logAudit(info, startTime, false);

									log.error(e.getLocalizedMessage() + e.getLocalizedMessage());
								}
							}
						} else if (StringUtils.equals(info.getClassification(), DPProcessParamAttributes.OCN.getValue())) {
							info.setPrMode(ocnPriceModeInput);
							Map<String, Object> raRequest = raCall.prepareRAMapping(info, ocnModelName,
									ocnModelMajorVersion, ocnModelMinorVersion, ocnPriceModeInput);
							if (null != raRequest && !raRequest.isEmpty()) {
								try {
									Future<KeyValue<Map, DPProcessParamInfo>> future = executorService
											.submit(executeDPAModel(ocnTenantCode, ocnModelName,
													ocnModelMajorVersion + RAClientConstants.CHAR_DOT + ocnModelMinorVersion, ocnAuthToken, raRequest, info));
									futureList.add(future);
								} catch (Exception e) {

									String errorDetail = dpFileProcessBO.saveDPProcessErrorDetail(info.getId(),
											IntegrationType.RA_INTEGRATION.getIntegrationType(), info.getErrorDetail(), e);

									info.setErrorDetail(errorDetail);

									raExceptionExecute(info);

									logAudit(info, startTime, false);

									log.error(e.getLocalizedMessage() + e.getLocalizedMessage());
								}
							}
						} else if (StringUtils.equals(info.getClassification(), DPProcessParamAttributes.PHH.getValue())) {
							info.setPrMode(phhPriceModeInput);
							Map<String, Object> raRequest = raCall.prepareRAMapping(info, phhModelName,
									phhModelMajorVersion, phhModelMinorVersion, phhPriceModeInput);
							if (null != raRequest && !raRequest.isEmpty()) {
								try {
									Future<KeyValue<Map, DPProcessParamInfo>> future = executorService
											.submit(executeDPAModel(phhTenantCode, phhModelName,
													phhModelMajorVersion + RAClientConstants.CHAR_DOT + phhModelMinorVersion, phhAuthToken, raRequest, info));
									futureList.add(future);
								} catch (Exception e) {

									String errorDetail = dpFileProcessBO.saveDPProcessErrorDetail(info.getId(),
											IntegrationType.RA_INTEGRATION.getIntegrationType(), info.getErrorDetail(), e);

									info.setErrorDetail(errorDetail);

									raExceptionExecute(info);

									logAudit(info, startTime, false);

									log.error(e.getLocalizedMessage() + e.getLocalizedMessage());
								}
							}
						}
						log.info("RAInputPayloadService -> week0price value : " + info.getWeek0Price());
					}
					for (Future<KeyValue<Map, DPProcessParamInfo>> keyValueFuture : futureList) {
						try {
							KeyValue<Map, DPProcessParamInfo> keyValuePair = keyValueFuture.get();
							DPProcessParamInfo dPProcessParamInfo = keyValuePair.getValue();
							populateOutputParam(dPProcessParamInfo, keyValuePair.getKey(),
									DateConversionUtil.getMillisFromUtcToEst(System.currentTimeMillis()));
						} catch (Exception e) {
							log.error("exception occured in RA execution for Week0 - Week0RAInputPayload : ", e);
						}
					}
				} catch (Exception e) {
					log.error("Excetion occured in Week0RAInputPayload : ", e);
				}
			}
		}
		log.info("Time taken for Week0RAInputPayload : " + (DateTime.now().getMillis() - start) + "ms");
		log.info("Week0RAInputPayload -> processTask() ended.");
	}

	private Callable<KeyValue<Map, DPProcessParamInfo>> executeDPAModel(String tenantCode, String modelName,
																		String modelVersion, String authToken, Map<String, Object> raRequest, DPProcessParamInfo info) {
		final Map<String, String> mdcContext = MDC.getCopyOfContextMap();
		return DelegatingSecurityContextCallable.create(() -> {
			if (mdcContext != null)
				MDC.setContextMap(mdcContext);
			MDC.put(RAClientConstants.LOAN_NUMBER, info.getAssetNumber());
			Map response = raClient.executeWeek0DPAModel(tenantCode, modelName, modelVersion, authToken, raRequest, info);
			MDC.remove(RAClientConstants.LOAN_NUMBER);
			return new KeyValue<>(response, info);
		}, SecurityContextHolder.getContext());
	}

	/**
	 * @param info
	 */
	private void raExceptionExecute(DPProcessParamInfo info) {

		log.info("RA Exception occurs.");

		info.setNotes(DPProcessParamAttributes.NOTES_RA.getValue());
		info.setWeek0Price(new BigDecimal(info.getListPrice()));
		info.setAssignment(DPProcessParamAttributes.ERROR_ASSIGNMENT.getValue());
		info.setAssignmentDate(DateConversionUtil.getCurrentUTCTime().getMillis());
		String process = null;
		if(DPProcessParamAttributes.OCN.getValue().equals(info.getClassification()))
			process = CommandProcess.WEEK0_OCN.getCommmandProcess();
		else if(DPProcessParamAttributes.PHH.getValue().equals(info.getClassification()))
			process = CommandProcess.WEEK0_PHH.getCommmandProcess();
		else if(DPProcessParamAttributes.NRZ.getValue().equals(info.getClassification()))
			process = CommandProcess.WEEK0_NRZ.getCommmandProcess();
		List<Command> command = commandDAO.findByProcess(process, DPAConstants.RA_FILTER);
		CommandInfo commandInfo = convert(command.get(0), CommandInfo.class);
		info.setCommand(commandInfo);
		dpProcessParamsBO.saveDPProcessParamInfo(info);
	}

	/**
	 * @param info
	 * @param response
	 * @throws SystemException
	 */
	@SuppressWarnings("unchecked")
	private void populateOutputParam(DPProcessParamInfo info, Map response, Long startTime) throws SystemException {
		log.info("RAInputPayloadService -> DPProcessParamInfo ID : " + (info != null ? info.getId() : "NA"));
		log.info("RAInputPayloadService -> response : " + response);
		Map<String, Object> responseParsed = new LinkedHashMap<>();

		DocumentContext parsedContext = JsonPath.parse(response);

		boolean success = parsedContext.read("$.header.success", Boolean.class);
		log.info("$.header.success value : " + success);
		if (success) {
			Map<String, List<Object>> processedData = parsedContext.read("$.data.hubzuwk0output.processed_data",
					Map.class);

			List<Object> varNames = processedData.get("VarName");
			List<Object> varValues = processedData.get("Value");

			for (int i = 0; i < varNames.size(); i++) {
				responseParsed.put(String.valueOf(varNames.get(i)), varValues.get(i));
			}

			log.info("Assignment blank test : " + StringUtils.isNotBlank(info.getAssignment()));
			log.info("Assignment = " + info.getAssignment());
			log.info("Modeled test : " + (StringUtils.isNotBlank(info.getAssignment()) && !StringUtils.equals(info.getAssignment(),
					DPProcessParamAttributes.BENCHMARK_ASSIGNMENT.getValue())));

			log.info("week0 price before : " + info.getWeek0Price());

			/*if (StringUtils.isNotBlank(info.getAssignment()) && !StringUtils.equals(info.getAssignment(),
					DPProcessParamAttributes.BENCHMARK_ASSIGNMENT.getValue())) {
				info.setWeek0Price(BigDecimal.valueOf(
						parsedContext.read("$.data.hubzuwk0output.wk0_price.sugg__list__final", Double.class)));
			}*/
			info.setWeek0Price(BigDecimal.valueOf(
					parsedContext.read("$.data.hubzuwk0output.wk0_price.sugg__list__final", Double.class)));
			info.setEnsemble(parsedContext.read("$.data.hubzuwk0output.wk0_price.final_ensemble", String.class));
			info.setNotesRa(parsedContext.read("$.data.hubzuwk0output.wk0_price.notes", String.class));

			log.info("week0 price after : " + info.getWeek0Price());

			log.info("$.data.hubzuwk0output.wk0_price.sugg__list__final value : " + info.getWeek0Price());
			log.info("$.data.hubzuwk0output.wk0_price.final_ensemble value : " + info.getEnsemble());
			log.info("$.data.hubzuwk0output.wk0_price.notes value : " + info.getNotesRa());
			log.info("Asset value : " + info.getAssetValue());

			if (StringUtils.isNotBlank(info.getAssetValue()) && null != info.getWeek0Price()
					&& !info.getAssetValue().equals("0")) {
				BigDecimal assetValue = new BigDecimal(info.getAssetValue());
				BigDecimal pctAV = info.getWeek0Price().divide(assetValue, 6, RoundingMode.HALF_EVEN);
				log.info("pctAV calculated is : " + pctAV.toString());
				info.setPctAV(pctAV.toString());
				if (info.getClassification().equalsIgnoreCase(DPProcessParamAttributes.NRZ.getValue())) {
					if (pctAV.compareTo(BigDecimal.valueOf(NRZ_PCT_AV_LOWER_SLAB)) >= 0
							&& pctAV.compareTo(new BigDecimal(NRZ_PCT_AV_UPPER_SLAB)) <= 0) {
						info.setWithinBusinessRules(TRUE);
					} else {
						info.setWithinBusinessRules(FALSE);
					}
				} else if (info.getClassification().equalsIgnoreCase(DPProcessParamAttributes.OCN.getValue())) {
					if (pctAV.compareTo(BigDecimal.valueOf(OCN_PCT_AV_LOWER_SLAB)) >= 0
							&& pctAV.compareTo(BigDecimal.valueOf(OCN_PCT_AV_UPPER_SLAB)) <= 0) {
						info.setWithinBusinessRules(TRUE);
					} else {
						info.setWithinBusinessRules(FALSE);
					}
				} else if (info.getClassification().equalsIgnoreCase(DPProcessParamAttributes.PHH.getValue())) {
					if (pctAV.compareTo(BigDecimal.valueOf(PHH_PCT_AV_LOWER_SLAB)) >= 0
							&& pctAV.compareTo(BigDecimal.valueOf(PHH_PCT_AV_UPPER_SLAB)) <= 0) {
						info.setWithinBusinessRules(TRUE);
					} else {
						info.setWithinBusinessRules(FALSE);
					}
				}
			}
			info.setAssignmentDate(DateConversionUtil.getCurrentUTCTime().getMillis());

			log.info("week0 price before saving  : " + info.getWeek0Price());

			dpProcessParamsBO.saveDPProcessParamInfo(info);

			log.info("week0 price after saving : " + info.getWeek0Price());
		} else {
			Map<String, String> errorMap = new HashMap<>();
			if (!StringUtils.isBlank(info.getErrorDetail())) {
				try {
					errorMap = ConversionUtil.convertJson(info.getErrorDetail(), Map.class);
				} catch (SystemException e) {
					log.info(e.getLocalizedMessage(), e);
				}
			}
			errorMap.put(IntegrationType.RA_INTEGRATION.getIntegrationType(), parsedContext.read("$.header.errorMessage", String.class));
			info.setErrorDetail(ConversionUtil.convertToJsonString(errorMap));
			raExceptionExecute(info);
		}
		logAudit(info, startTime, success);
	}

	/**
	 * @param info
	 * @param startTime
	 * @param success
	 * @throws SystemException
	 */
	@SuppressWarnings("unchecked")
	private void logAudit(DPProcessParamInfo info, Long startTime, boolean success) throws SystemException {
		DynamicPricingIntgAudit dpIntgAudit = new DynamicPricingIntgAudit();
		DPProcessParam dpProcessParam = new DPProcessParam();
		dpProcessParam.setId(info.getId());
		dpIntgAudit.setDpProcessParam(dpProcessParam);
		dpIntgAudit.setStartTime(BigInteger.valueOf(startTime));
		Long endTime = DateConversionUtil.getMillisFromUtcToEst(System.currentTimeMillis());
		dpIntgAudit.setEndTime(BigInteger.valueOf(endTime));
		dpIntgAudit.setEventType(IntegrationType.RA_INTEGRATION.getIntegrationType());
		dpIntgAudit.setStatus(
				success ? TransactionStatus.SUCCESS.getTranStatus() : TransactionStatus.FAIL.getTranStatus());
		if(info.getErrorDetail() != null){
			Map<String, String> errorMap = ConversionUtil.convertJson(info.getErrorDetail(), Map.class);
			dpIntgAudit.setErrorDescription(errorMap.get(IntegrationType.RA_INTEGRATION.getIntegrationType()));
		}

		dpFileProcessBO.saveDPProcessIntgAudit(dpIntgAudit);
	}

}