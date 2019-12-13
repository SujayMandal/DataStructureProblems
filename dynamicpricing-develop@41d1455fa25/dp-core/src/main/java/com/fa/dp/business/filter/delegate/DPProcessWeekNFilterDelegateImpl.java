package com.fa.dp.business.filter.delegate;

import com.fa.dp.business.command.dao.CommandDAO;
import com.fa.dp.business.command.entity.Command;
import com.fa.dp.business.command.info.CommandInfo;
import com.fa.dp.business.command.info.CommandProcess;
import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.db.client.HubzuDBClient;
import com.fa.dp.business.db.client.PMIInscCompsDBClient;
import com.fa.dp.business.db.client.StageFiveDBClient;
import com.fa.dp.business.filter.bo.DPProcessWeekNParamsBO;
import com.fa.dp.business.filter.constant.DPProcessFilterParams;
import com.fa.dp.business.info.HubzuDBResponse;
import com.fa.dp.business.info.HubzuInfo;
import com.fa.dp.business.info.SSPMIInfo;
import com.fa.dp.business.info.StageFiveDBResponse;
import com.fa.dp.business.util.DPFileProcessStatus;
import com.fa.dp.business.util.DPFileProcesses;
import com.fa.dp.business.util.IntegrationType;
import com.fa.dp.business.util.ThreadPoolExecutorUtil;
import com.fa.dp.business.util.TransactionStatus;
import com.fa.dp.business.validator.bo.DPFileProcessBO;
import com.fa.dp.business.weekn.entity.DPProcessWeekNParam;
import com.fa.dp.business.weekn.entity.DPWeekNProcessStatus;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamEntryInfo;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamInfo;
import com.fa.dp.business.weekn.input.info.DPWeekNProcessStatusInfo;
import com.fa.dp.core.base.delegate.AbstractDelegate;
import com.fa.dp.core.cache.CacheManager;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.systemparam.util.AppParameterConstant;
import com.fa.dp.core.util.DateConversionUtil;
import com.fa.dp.core.util.KeyValue;
import com.fa.dp.core.util.RAClientConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.security.concurrent.DelegatingSecurityContextCallable;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Slf4j
@Named
public class DPProcessWeekNFilterDelegateImpl extends AbstractDelegate implements DPProcessWeekNFilterDelegate {

	@Inject
	private DPFileProcessBO dpFileProcessBO;

	@Inject
	private HubzuDBClient hubzuDBClient;

	@Inject
	private CacheManager cacheManager;

	@Inject
	private DPProcessWeekNParamsBO dpProcessWeekNParamsBO;

	@Inject
	private StageFiveDBClient stageDBClient;

	@Inject
	private CommandDAO commandDAO;

	@Value("${WEEKN_CONCURRENT_DBCALL_POOL_SIZE}")
	private int concurrentDbCallPoolSize;

	@Value("${WEEKN_INITIAL_QUERY_IN_CLAUSE_COUNT}")
	private int pmiQueryInClauseCount;

	@Inject
	private PMIInscCompsDBClient pmiInscCompsDBClient;

	private ExecutorService executorService;

	@PostConstruct
	public void initializeTemplate() {
		executorService = ThreadPoolExecutorUtil.getFixedSizeThreadPool(concurrentDbCallPoolSize);
	}

	@PreDestroy
	public void destroy() {
		if(executorService != null) {
			executorService.shutdown();
		}
	}

	@Override
	public void filterRecordsOnZipCodeAndState(DPProcessWeekNParamEntryInfo inputParamEntry) throws SystemException {

		if(null != inputParamEntry.getColumnEntries() && !inputParamEntry.getColumnEntries().isEmpty()) {
			log.info("Filtering on STATE and ZIPCODE started..");
			KeyValue<List<DPProcessWeekNParamInfo>, List<DPProcessWeekNParamInfo>> resultMap = dpProcessWeekNParamsBO
					.filterRecordsOnZipCodeAndState(inputParamEntry.getColumnEntries());

			if(!inputParamEntry.isFetchProcess()) {
				// setting the successful records after filtering, back to input object
				inputParamEntry.setColumnEntries(resultMap.getKey());

				// save ineligible records to DP_WEEKN_PARAMS table
				if(CollectionUtils.isNotEmpty(resultMap.getValue())) {
					log.info("Saving all ineligible AssetValue entries to db..");
					saveParams(resultMap.getValue());
				}
			}

			log.info("Filtering on STATE and ZIPCODE ended..");
		}

	}

	@Override
	public void filterRecordsOnSop(DPProcessWeekNParamEntryInfo inputParamEntry) throws SystemException {

		if(null != inputParamEntry.getColumnEntries() && !inputParamEntry.getColumnEntries().isEmpty()) {
			log.info("Filtering on SOP started..");
			KeyValue<List<DPProcessWeekNParamInfo>, List<DPProcessWeekNParamInfo>> resultMap = dpProcessWeekNParamsBO
					.filterRecordsOnSop(inputParamEntry.getColumnEntries());

			if(!inputParamEntry.isFetchProcess()) {
				// setting the successful records after filtering, back to input object
				inputParamEntry.setColumnEntries(resultMap.getKey());

				// save ineligible records to DP_WEEKN_PARAMS table
				if(CollectionUtils.isNotEmpty(resultMap.getValue())) {
					log.info("Saving all ineligible AssetValue entries to db..");
					saveParams(resultMap.getValue());
				}
			}
			log.info("Filtering on SOP ended..");
		}

	}

	@Override
	public void getHubzuData(DPProcessWeekNParamEntryInfo infoObject) {
		Long startTime = System.currentTimeMillis();
		log.info("weekNHubzuDBCall -> processTask started.");
		log.info("Enter WeekNHubzuDBCallImpl :: method invokeWeekNHubzuDBCall");
		List<DPProcessWeekNParamInfo> successEntries = new ArrayList<>();
		List<Future<KeyValue<HubzuDBResponse, DPProcessWeekNParamInfo>>> futureList = new ArrayList<>();
		try {
			// Map Contains Integration Type , Hubzu Query
			Map<String, String> hubzuQuery = new HashMap<>(RAClientConstants.HUBZU_MAP_SIZE);
			hubzuQuery.put(RAClientConstants.HUBZU_QUERY, (String) cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_HUBZU_QUERY));
			hubzuQuery.put(RAClientConstants.HUBZU_INTEGRATION_TYPE, IntegrationType.HUBZU_RA_INTEGRATION.getIntegrationType());

			infoObject.getColumnEntries().forEach(dPProcessParamInfo -> {
				if(null == dPProcessParamInfo.getCommand()) {
					Future<KeyValue<HubzuDBResponse, DPProcessWeekNParamInfo>> future = executorService
							.submit(fetchHubzuData(dPProcessParamInfo, hubzuQuery, infoObject.isFetchProcess()));
					futureList.add(future);
				}
			});

			for (Future<KeyValue<HubzuDBResponse, DPProcessWeekNParamInfo>> keyValueFuture : futureList) {
				KeyValue<HubzuDBResponse, DPProcessWeekNParamInfo> keyValuePair = keyValueFuture.get();
				HubzuDBResponse hubzuDBResponse = keyValuePair.getKey();
				DPProcessWeekNParamInfo dPProcessParamInfo = keyValuePair.getValue();
				MDC.put(RAClientConstants.LOAN_NUMBER, dPProcessParamInfo.getAssetNumber());
				dPProcessParamInfo.setHubzuDBResponse(keyValuePair.getKey());

				if(TransactionStatus.FAIL.getTranStatus().equals(dPProcessParamInfo.getHubzuDBResponse().getTransactionStatus())) {
					List<Command> command = commandDAO.findByProcess(CommandProcess.WEEKN.getCommmandProcess(), DPAConstants.DATA_FETCH_FAILURE);
					CommandInfo commandInfo = convert(command.get(0), CommandInfo.class);
					dPProcessParamInfo.setCommand(commandInfo);
					dPProcessParamInfo.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
					dPProcessParamInfo.setEligible(DPProcessFilterParams.ELIGIBLE.getValue());
					dPProcessParamInfo.setExclusionReason(DPProcessFilterParams.HUBZU_DB_CALL_EXCLUSION.getValue());
					if(!infoObject.isFetchProcess())
						dpProcessWeekNParamsBO.saveDPProcessWeekNParamInfo(dPProcessParamInfo);
				} else {
					// Merging Old Asset numbers with New Loan Numbers
					//					Set<String> unmergedAssetsFromHbz = hubzuDBResponse.getHubzuInfos().stream().map(HubzuInfo::getSelrPropIdVcNn)
					//							.collect(Collectors.toSet());
					//					Map<String, String> migrationMap = dpMigrationMapDao.findMigrationMapping(unmergedAssetsFromHbz).stream()
					//							.collect(Collectors.toMap(DPMigrationMap::getLoanNumber, DPMigrationMap::getNewLoanNumber));
					//					hubzuDBResponse.getHubzuInfos().stream().filter(hubzuInfo -> migrationMap.containsKey(hubzuInfo.getSelrPropIdVcNn()))
					//					.forEach(hubzuInfo -> hubzuInfo.setSelrPropIdVcNn(migrationMap.get(hubzuInfo.getSelrPropIdVcNn())));

					dPProcessParamInfo
							.setSellerOccupiedProperty(dPProcessParamInfo.getHubzuDBResponse().getHubzuInfos().get(0).getSopProgramStatus());
					DateTime parsedDate = dPProcessParamInfo.getHubzuDBResponse().getHubzuInfos().get(0).getCurrentListEndDate() != null ?
							DateTime.parse(dPProcessParamInfo.getHubzuDBResponse().getHubzuInfos().get(0).getCurrentListEndDate(),
									DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")) :
							null;
					dPProcessParamInfo.setMostRecentListEndDate(
							parsedDate != null ? parsedDate.toDateTime().toString(DateTimeFormat.forPattern(RAClientConstants.DATE_FORMAT)) : null);
					if(null == dPProcessParamInfo.getState())
						dPProcessParamInfo.setState(dPProcessParamInfo.getHubzuDBResponse().getHubzuInfos().get(0).getPropStatIdVcFk());
					else if(!dPProcessParamInfo.getState()
							.equals(dPProcessParamInfo.getHubzuDBResponse().getHubzuInfos().get(0).getPropStatIdVcFk())) {
						for (HubzuInfo hubzuInfo : dPProcessParamInfo.getHubzuDBResponse().getHubzuInfos()) {
							hubzuInfo.setPropStatIdVcFk(dPProcessParamInfo.getState());
						}
					}
					if(null == dPProcessParamInfo.getZipCode())
						dPProcessParamInfo.setZipCode(dPProcessParamInfo.getHubzuDBResponse().getHubzuInfos().get(0).getPropZipVcFk());
					else if(!dPProcessParamInfo.getZipCode()
							.equals(dPProcessParamInfo.getHubzuDBResponse().getHubzuInfos().get(0).getPropZipVcFk())) {
						for (HubzuInfo hubzuInfo : dPProcessParamInfo.getHubzuDBResponse().getHubzuInfos()) {
							hubzuInfo.setPropZipVcFk(dPProcessParamInfo.getZipCode());
						}
					}
					if(null == dPProcessParamInfo.getRbidPropIdVcPk())
						dPProcessParamInfo.setRbidPropIdVcPk(dPProcessParamInfo.getHubzuDBResponse().getHubzuInfos()
								.get(dPProcessParamInfo.getHubzuDBResponse().getHubzuInfos().size() - 1).getRbidPropIdVcPk());
					successEntries.add(dPProcessParamInfo);
					MDC.remove(RAClientConstants.LOAN_NUMBER);
				}
			}

			log.info("time taken for all weekNHubzuDBCall records : " + (System.currentTimeMillis() - startTime));
			if(!infoObject.isFetchProcess())
				infoObject.setColumnEntries(successEntries);
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(), e);
		} finally {
			MDC.remove(RAClientConstants.LOAN_NUMBER);
		}
		log.info("Exit WeekNHubzuDBCallImpl :: method invokeWeekNHubzuDBCall");
		log.info("weekNHubzuDBCall -> processTask ended.");
	}

	/*@Override
	public void filterRecords(DPProcessWeekNParamEntryInfo inputParamEntry) throws SystemException {
		if (null != inputParamEntry.getColumnEntries() && !inputParamEntry.getColumnEntries().isEmpty()) {
			log.info("Filtering on CLNT_CODE_VC  and IS_SPCL_HNDL_PROP_VC started..");
			KeyValue<List<DPProcessWeekNParamInfo>, List<DPProcessWeekNParamInfo>> resultMap = dpProcessWeekNParamsBO
					.filterRecords(inputParamEntry.getColumnEntries());

			if(!inputParamEntry.isFetchProcess()) {
				// setting the successful records after filtering, back to input object
				inputParamEntry.setColumnEntries(resultMap.getKey());

				// save ineligible records to DP_WEEKN_PARAMS table
				if (CollectionUtils.isNotEmpty(resultMap.getValue())) {
					log.info("Saving all ineligible AssetValue entries to db..");
					saveParams(resultMap.getValue());
				}
			}
			log.info("Filtering on CLNT_CODE_VC and IS_SPCL_HNDL_PROP_VC ended..");
		}
	}*/

	@Override
	public void processQAReportHubzuData(DPProcessWeekNParamEntryInfo infoObject) {
		Long startTime = System.currentTimeMillis();
		log.info("weekNHubzuDBCall -> processTask started.");

		List<Future<KeyValue<HubzuDBResponse, DPProcessWeekNParamInfo>>> futureList = new ArrayList<>();
		String hubzuQuery = (String) cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_HUBZU_QUERY);
		infoObject.getColumnEntries().forEach(dPProcessParamInfo -> {
			if(null == dPProcessParamInfo.getCommand()) {
				Future<KeyValue<HubzuDBResponse, DPProcessWeekNParamInfo>> future = executorService
						.submit(fetchQaHubzuData(dPProcessParamInfo, hubzuQuery));
				futureList.add(future);
			}
		});

		for (Future<KeyValue<HubzuDBResponse, DPProcessWeekNParamInfo>> keyValueFuture : futureList) {

			KeyValue<HubzuDBResponse, DPProcessWeekNParamInfo> keyValuePair = null;
			try {
				keyValuePair = keyValueFuture.get();
			} catch (InterruptedException e) {
				log.error("Inturrupted exception {}", e);
			} catch (ExecutionException e) {
				log.error("Execution exception {}", e);
			}
			if(keyValuePair != null) {
				HubzuDBResponse hubzuDBResponse = keyValuePair.getKey();
				DPProcessWeekNParamInfo dPProcessParamInfo = keyValuePair.getValue();
				MDC.put(RAClientConstants.LOAN_NUMBER, dPProcessParamInfo.getAssetNumber());
				dPProcessParamInfo.setHubzuDBResponse(hubzuDBResponse);
				if(StringUtils.isEmpty(dPProcessParamInfo.getState()) && dPProcessParamInfo.getHubzuDBResponse() != null && CollectionUtils
						.isNotEmpty(dPProcessParamInfo.getHubzuDBResponse().getHubzuInfos())
						&& dPProcessParamInfo.getHubzuDBResponse().getHubzuInfos().size() > 0) {
					dPProcessParamInfo.setState(dPProcessParamInfo.getHubzuDBResponse().getHubzuInfos().get(0).getPropStatIdVcFk());
				}
			}
		}

		log.info("time taken for all weekNHubzuDBCall records : " + (System.currentTimeMillis() - startTime));
		log.info("weekNHubzuDBCall -> processTask ended.");
	}

	private Callable<KeyValue<HubzuDBResponse, DPProcessWeekNParamInfo>> fetchQaHubzuData(final DPProcessWeekNParamInfo dpProcessWeekNParamInfo,
			final String hubzuQuery) {
		final Map<String, String> mdcContext = MDC.getCopyOfContextMap();
		return DelegatingSecurityContextCallable.create(() -> {
			if(mdcContext != null)
				MDC.setContextMap(mdcContext);
			MDC.put(RAClientConstants.LOAN_NUMBER, dpProcessWeekNParamInfo.getAssetNumber());
			HubzuDBResponse hubzuDBResponse = hubzuDBClient.fetchQaHubzuData(dpProcessWeekNParamInfo, hubzuQuery);
			MDC.remove(RAClientConstants.LOAN_NUMBER);
			return new KeyValue<>(hubzuDBResponse, dpProcessWeekNParamInfo);
		}, SecurityContextHolder.getContext());
	}

	/*@Override
	public void getHubzuSSPMIData(DPProcessWeekNParamEntryInfo infoObject) {
		Long startTime = System.currentTimeMillis();
		log.info("Enter WeekNSSPmiFilter :: method getHubzuData");

		List<Future<KeyValue<HubzuDBResponse, DPProcessWeekNParamInfo>>> futureList = new ArrayList<>();
		try {
			List<DPProcessWeekNParamInfo> successEntries = new ArrayList<>();
			Map<String,String> hubzuQuery = new HashMap<>(RAClientConstants.HUBZU_MAP_SIZE);
			hubzuQuery.put(RAClientConstants.HUBZU_QUERY, (String) cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_SS_PMI_HUBZU_QUERY));
			hubzuQuery.put(RAClientConstants.HUBZU_INTEGRATION_TYPE, IntegrationType.HUBZU_SS_PMI_INTEGRATION.getIntegrationType());

			infoObject.getColumnEntries().forEach(dPProcessParamInfo -> {
				if (null == dPProcessParamInfo.getCommand()) {
					Future<KeyValue<HubzuDBResponse, DPProcessWeekNParamInfo>> future = executorService
							.submit(fetchHubzuData(dPProcessParamInfo, hubzuQuery, infoObject.isFetchProcess()));
					futureList.add(future);
				}
			});
			for (Future<KeyValue<HubzuDBResponse, DPProcessWeekNParamInfo>> keyValueFuture : futureList) {
				KeyValue<HubzuDBResponse, DPProcessWeekNParamInfo> keyValuePair = keyValueFuture.get();
				DPProcessWeekNParamInfo dPProcessParamInfo = keyValuePair.getValue();
				MDC.put(RAClientConstants.LOAN_NUMBER, dPProcessParamInfo.getAssetNumber());
				log.debug("Added SS PMI Response start");
				dPProcessParamInfo.setSsPmiHubzuResponse(keyValuePair.getKey());
				if (TransactionStatus.FAIL.getTranStatus().equals(dPProcessParamInfo.getSsPmiHubzuResponse().getTransactionStatus())) {
					List<Command> command = commandDAO.findByProcess(CommandProcess.WEEKN.getCommmandProcess(), DPAConstants.DATA_FETCH_FAILURE);
					CommandInfo commandInfo = convert(command.get(0), CommandInfo.class);
					dPProcessParamInfo.setCommand(commandInfo);
					dPProcessParamInfo.setEligible(DPProcessFilterParams.ELIGIBLE.getValue());
					dPProcessParamInfo.setExclusionReason(DPProcessFilterParams.SS_PMI_EXCLUSION.getValue());
					if (!infoObject.isFetchProcess())
						dpProcessWeekNParamsBO.saveDPProcessWeekNParamInfo(dPProcessParamInfo);
				} else {
					if (StringUtils.isBlank(dPProcessParamInfo.getClientCode())) {
						dPProcessParamInfo.setClientCode(dPProcessParamInfo.getSsPmiHubzuResponse().getHubzuInfos().get(0).getClntCodeVc());
					} else {
						dPProcessParamInfo.getSsPmiHubzuResponse().getHubzuInfos().get(0).setClntCodeVc(dPProcessParamInfo.getClientCode());
					}
					dPProcessParamInfo.setPrivateMortgageInsurance(dPProcessParamInfo.getSsPmiHubzuResponse().getHubzuInfos().get(0).getIsSpclHndlPropVc());
					if (!infoObject.isFetchProcess()) {
						if (!infoObject.isReprocess() || StringUtils.isBlank(dPProcessParamInfo.getDpProcessWeekNParamOriginal().getClientCode())) {
							dPProcessParamInfo.getDpProcessWeekNParamOriginal().setClientCode(dPProcessParamInfo.getClientCode());
						}
						if (!infoObject.isReprocess() || StringUtils.isBlank(
								dPProcessParamInfo.getDpProcessWeekNParamOriginal().getPrivateMortgageInsurance())) {
							dPProcessParamInfo.getDpProcessWeekNParamOriginal().setPrivateMortgageInsurance(dPProcessParamInfo.getPrivateMortgageInsurance());
						}
					}
					successEntries.add(dPProcessParamInfo);
				}
				log.debug("Added SS PMI Response end");
				MDC.remove(RAClientConstants.LOAN_NUMBER);
			}
			log.info("Time taken for all weekNSSPMIHubzuDBCall records : " + (System.currentTimeMillis() - startTime));
			if(!infoObject.isFetchProcess())
				infoObject.setColumnEntries(successEntries);
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(), e);
		} finally {
			log.debug("Shutdown Executer service Start");
			executorService.shutdown();
			log.debug("Shutdown Executer service End");
			MDC.remove(RAClientConstants.LOAN_NUMBER);
		}
		log.info("Exit WeekNSSPmiFilter :: method getHubzuData");
	}*/

	@Override
	public void getStage5Data(DPProcessWeekNParamEntryInfo infoObject) {
		Long startTime = System.currentTimeMillis();
		List<DPProcessWeekNParamInfo> columnEntries = new ArrayList<>();
		List<DPProcessWeekNParamInfo> successEntries = new ArrayList<>();
		log.info("Enter WeekNStage5DBCallImpl :: method invokeWeekNStage5DBCall");
		try {
			// Creating list of successfull entries from previous columns
			columnEntries = infoObject.getColumnEntries().stream().filter(entries -> null == entries.getCommand()).collect(Collectors.toList());
			// Calling Stored Procedure for Stage 5 DB Data
			if(CollectionUtils.isNotEmpty(columnEntries)) {
				successEntries = stageDBClient.getStage5FromStoredProcedure(columnEntries);
			}
			if(CollectionUtils.isNotEmpty(successEntries))
				infoObject.setColumnEntries(successEntries);

			log.info("time taken for all stage5 records : " + (System.currentTimeMillis() - startTime));
		} catch (SystemException se) {
			log.error("Error Occured in Stage5 DB call" + se);
			columnEntries.stream().forEach(entry -> {
				List<Command> command = commandDAO.findByProcess(CommandProcess.WEEKN.getCommmandProcess(), DPAConstants.DATA_FETCH_FAILURE);
				CommandInfo commandInfo = convert(command.get(0), CommandInfo.class);
				entry.setCommand(commandInfo);
				entry.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
				entry.setEligible(DPProcessFilterParams.ELIGIBLE.getValue());
				entry.setAssignment(DPProcessFilterParams.ASSIGNMENT_ERROR.getValue());
				entry.setExclusionReason(DPProcessFilterParams.STAGE5_DB_CALL_EXCLUSION.getValue());
				dpProcessWeekNParamsBO.saveDPProcessWeekNParamInfo(entry);
			});
		} catch (Exception e) {
			log.error("Error Occured in Stage5 DB call" + e);
			columnEntries.stream().forEach(entry -> {
				List<Command> command = commandDAO.findByProcess(CommandProcess.WEEKN.getCommmandProcess(), DPAConstants.DATA_FETCH_FAILURE);
				CommandInfo commandInfo = convert(command.get(0), CommandInfo.class);
				entry.setCommand(commandInfo);
				entry.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
				entry.setEligible(DPProcessFilterParams.ELIGIBLE.getValue());
				entry.setAssignment(DPProcessFilterParams.ASSIGNMENT_ERROR.getValue());
				entry.setExclusionReason(DPProcessFilterParams.STAGE5_DB_CALL_EXCLUSION.getValue());
				dpProcessWeekNParamsBO.saveDPProcessWeekNParamInfo(entry);
			});
		}
		log.info("Exit WeekNStage5DBCallImpl :: method invokeWeekNStage5DBCall");
	}

	@Override
	public void saveWeekNProcessStatus(DPProcessWeekNParamEntryInfo dpWeeknParamEntry) {
		String fetchedDate = dpWeeknParamEntry.getColumnEntries().get(0).getDpWeekNProcessStatus().getFetchedDateStr();
		DateTime parsedDate = DateConversionUtil.EST_DATE_TIME_FORMATTER.parseDateTime(fetchedDate);
		Long fetchedDtToSave = parsedDate.getMillis();
		DPWeekNProcessStatus dpWeeknProcessStatus = new DPWeekNProcessStatus();
		if(dpWeeknParamEntry.isReprocess()) {
			dpWeeknProcessStatus.setId(dpWeeknParamEntry.getColumnEntries().get(0).getDpWeekNProcessStatus().getId());
		}
		dpWeeknProcessStatus.setFetchedDate(fetchedDtToSave);
		dpWeeknProcessStatus.setProcess(DPFileProcesses.VACANT_WEEKN.getProcess());
		dpWeeknProcessStatus.setStatus(DPFileProcessStatus.IN_PROGRESS.getFileStatus());
		dpWeeknProcessStatus = dpFileProcessBO.saveDPProcessWeekNStatus(dpWeeknProcessStatus);
		DPWeekNProcessStatusInfo dpWeekNProcessStatusInfo = convert(dpWeeknProcessStatus, DPWeekNProcessStatusInfo.class);
		dpWeeknParamEntry.setDpWeeknProcessStatus(dpWeekNProcessStatusInfo);
	}

	@Override
	public List<DPProcessWeekNParamInfo> saveParams(List<DPProcessWeekNParamInfo> recordsToSave) {
		List<DPProcessWeekNParamInfo> result = new ArrayList<>();
		for (DPProcessWeekNParamInfo info : recordsToSave) {
			DPProcessWeekNParam dpProcessWeekNParam = new DPProcessWeekNParam();
			dpProcessWeekNParam = convert(info, DPProcessWeekNParam.class);
			DPWeekNProcessStatus dpWeekNProcessStatus = new DPWeekNProcessStatus();
			dpWeekNProcessStatus = convert(info.getDpWeekNProcessStatus(), DPWeekNProcessStatus.class);
			dpProcessWeekNParam.setDpWeekNProcessStatus(dpWeekNProcessStatus);
			dpProcessWeekNParam = dpProcessWeekNParamsBO.saveDPProcessWeekNParam(dpProcessWeekNParam);
			info.setId(dpProcessWeekNParam.getId());
			result.add(info);
		}
		return result;
	}

	@Override
	public DPWeekNProcessStatus checkForWeekNPrcsStatus(String status) {
		DPWeekNProcessStatus dpWeeknPrcsStatus = dpFileProcessBO.findWeeknPrcsStatusByStatus(status);
		return dpWeeknPrcsStatus;
	}

	@Override
	public void updateWeeknPrcsStatus(String status, String id) {
		dpFileProcessBO.updateWeeknPrcsStatus(status, id);
	}

	@Override
	public void filterRecordsOnAssigment(DPProcessWeekNParamEntryInfo inputParamEntry) {
		if(null != inputParamEntry.getColumnEntries() && !inputParamEntry.getColumnEntries().isEmpty()) {
			log.info("Filtering on Assignment started..");
			KeyValue<List<DPProcessWeekNParamInfo>, List<DPProcessWeekNParamInfo>> resultMap = dpProcessWeekNParamsBO
					.filterRecordsOnAssignment(inputParamEntry.getColumnEntries(), inputParamEntry.isFetchProcess());

			if(!inputParamEntry.isFetchProcess()) {
				// setting the successful records after filtering, back to input object
				inputParamEntry.setColumnEntries(resultMap.getKey());

				// save ineligible records to DP_WEEKN_PARAMS table
				if(CollectionUtils.isNotEmpty(resultMap.getValue())) {
					log.info("Saving all ineligible entries to db..");
					saveParams(resultMap.getValue());
				}
			}

			log.info("Filtering on Assignment ended..");
		}

	}

	@Override
	public void filterSSPmi(DPProcessWeekNParamEntryInfo infoObject) {
		Long startTime = System.currentTimeMillis();
		log.info("Enter WeekNSSPmiFilter :: method getPMIFlag");
		//List<String> loanWithOldAndNewAsset = new ArrayList<>();
		//Map<String, String> ssPmiQueriesMap = new HashMap<>(RAClientConstants.HUBZU_MAP_SIZE);

		try {
			// filtering on PMI flag
			infoObject = dpProcessWeekNParamsBO.filterOnNewPMIFlag(infoObject);

			//infoObject = ssPmiFilter(infoObject, loanWithOldAndNewAsset, ssPmiQueriesMap);

			infoObject = dpProcessWeekNParamsBO.filterOnZeroPMIFlag(infoObject);

			//Filtering for special servicing investor codes(Client codes)
			dpProcessWeekNParamsBO.filterOnNewSpclServicing(infoObject);
			log.info("Time taken for all weekNSSPMIHubzuDBCall records : " + (System.currentTimeMillis() - startTime));
		} catch (DataAccessException dae) {
			log.error("Error occured in DataAccessException DPProcessWeekNFilterDelegateImpl::filterSSPmi ", dae);
		} catch (Exception e) {
			log.error("Error occured in Exception DPProcessWeekNFilterDelegateImpl::filterSSPmi ", e);
		}
		log.info("Exit WeekNSSPmiFilter :: method getHubzuData");
	}

	@Override
	public void filterQAReportAssigment(DPProcessWeekNParamEntryInfo dpProcessParamEntryInfo) {
		log.info("Filtering on Assignment started..");
		if(CollectionUtils.isNotEmpty(dpProcessParamEntryInfo.getColumnEntries())) {
			log.info("Filtering on Assignment started..");
			dpProcessWeekNParamsBO.filterQAReportAssignment(dpProcessParamEntryInfo.getColumnEntries());
		}
		log.info("Filtering on Assignment ended..");
	}

	@Override
	public void filterQAReportState(DPProcessWeekNParamEntryInfo inputParamEntry) {
		if(null != inputParamEntry.getColumnEntries() && !inputParamEntry.getColumnEntries().isEmpty()) {
			log.info("filterQAReportState started..");
			dpProcessWeekNParamsBO.filterQAReportState(inputParamEntry.getColumnEntries());
			log.info("filterQAReportState ended..");
		}
	}

	@Override
	public void filterQAReportSSPmi(DPProcessWeekNParamEntryInfo infoObject) {
		dpProcessWeekNParamsBO.filterQAReportSSPmiFlag(infoObject);
	}

	private Callable<KeyValue<HubzuDBResponse, DPProcessWeekNParamInfo>> fetchHubzuData(final DPProcessWeekNParamInfo dpProcessWeekNParamInfo,
			final Map<String, String> hubzuQuery, final Boolean isFetchProcess) {
		final Map<String, String> mdcContext = MDC.getCopyOfContextMap();
		return DelegatingSecurityContextCallable.create(() -> {
			if(mdcContext != null)
				MDC.setContextMap(mdcContext);
			MDC.put(RAClientConstants.LOAN_NUMBER, dpProcessWeekNParamInfo.getAssetNumber());
			HubzuDBResponse hubzuDBResponse = hubzuDBClient.fetchHubzuData(dpProcessWeekNParamInfo, hubzuQuery, isFetchProcess);
			MDC.remove(RAClientConstants.LOAN_NUMBER);
			return new KeyValue<>(hubzuDBResponse, dpProcessWeekNParamInfo);
		}, SecurityContextHolder.getContext());
	}

	private DPProcessWeekNParamEntryInfo ssPmiFilter(DPProcessWeekNParamEntryInfo infoObject, List<String> loanWithOldAndNewAsset,
			Map<String, String> ssPmiQueriesMap) throws InterruptedException, java.util.concurrent.ExecutionException {
		//Checking for PMI flag
		infoObject.getColumnEntries().stream().filter(param -> param.getPrivateMortgageInsurance().equalsIgnoreCase(RAClientConstants.NO))
				.collect(Collectors.toList()).forEach(assets -> {
			if(StringUtils.isNotEmpty(assets.getOldAssetNumber()))
				loanWithOldAndNewAsset.add(assets.getOldAssetNumber());
			else
				loanWithOldAndNewAsset.add(assets.getAssetNumber());
		});

		//Checking for PMI Insurance company
		if(!loanWithOldAndNewAsset.isEmpty()) {
			ssPmiQueriesMap.put(RAClientConstants.PMI_INSC_ARLT_QUERY,
					(String) cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_PMI_INSC_ARLT_QUERY));
			ssPmiQueriesMap.put(RAClientConstants.PMI_INTEGRATION_TYPE, IntegrationType.PMI_ARLT_INTEGRATION.getIntegrationType());
			String selectQuery = ssPmiQueriesMap.get(RAClientConstants.PMI_INSC_ARLT_QUERY);
			String integrationType = ssPmiQueriesMap.get(RAClientConstants.PMI_INTEGRATION_TYPE);

			List<List<String>> splitPmiInfos = ListUtils.partition(loanWithOldAndNewAsset, pmiQueryInClauseCount);
			List<Future<List<SSPMIInfo>>> futureList = new ArrayList<>();
			List<SSPMIInfo> ssPmiInfos = new ArrayList<>();
			for (List<String> subPmiInfoList : splitPmiInfos) {
				Future<List<SSPMIInfo>> pmiFlagsFuture = executorService
						.submit(pmiInscCompsDBClient.fetchInscIdsForAssetIds(selectQuery, integrationType, subPmiInfoList));
				futureList.add(pmiFlagsFuture);
			}
			//Second filtering for PMI on insurance company
			for (Future<List<SSPMIInfo>> pmiInfosFuture : futureList) {
				ssPmiInfos.addAll(pmiInfosFuture.get());
			}

			KeyValue<DPProcessWeekNParamEntryInfo, List<SSPMIInfo>> inscCompResult = dpProcessWeekNParamsBO.filterOnInscComp(infoObject, ssPmiInfos);
			infoObject = inscCompResult.getKey();
		}
		return infoObject;
	}

	private Callable<KeyValue<StageFiveDBResponse, DPProcessWeekNParamInfo>> fetchStage5Data(final DPProcessWeekNParamInfo dpProcessWeekNParamInfo,
			final Boolean isFetchProcess) {
		final Map<String, String> mdcContext = MDC.getCopyOfContextMap();
		return DelegatingSecurityContextCallable.create(() -> {
			if(mdcContext != null)
				MDC.setContextMap(mdcContext);
			MDC.put(RAClientConstants.LOAN_NUMBER, dpProcessWeekNParamInfo.getAssetNumber());
			StageFiveDBResponse StageFiveDBResponse = stageDBClient.fetchStageFiveData(dpProcessWeekNParamInfo, isFetchProcess);
			MDC.remove(RAClientConstants.LOAN_NUMBER);
			return new KeyValue<>(StageFiveDBResponse, dpProcessWeekNParamInfo);
		}, SecurityContextHolder.getContext());
	}

}