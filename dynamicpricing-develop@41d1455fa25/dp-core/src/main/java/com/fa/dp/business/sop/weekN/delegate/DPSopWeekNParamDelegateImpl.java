package com.fa.dp.business.sop.weekN.delegate;

import static com.fa.dp.core.util.DateConversionUtil.DATE_DD_MMM_YY;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.concurrent.DelegatingSecurityContextCallable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;

import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.constant.DPProcessParamAttributes;
import com.fa.dp.business.db.client.SopHubzuDBClient;
import com.fa.dp.business.db.client.StageFiveDBClient;
import com.fa.dp.business.filter.constant.DPProcessFilterParams;
import com.fa.dp.business.info.HubzuDBResponse;
import com.fa.dp.business.info.HubzuInfo;
import com.fa.dp.business.rr.migration.RRMigration;
import com.fa.dp.business.rr.migration.dao.DPMigrationMapDao;
import com.fa.dp.business.rr.migration.entity.DPMigrationMap;
import com.fa.dp.business.sop.week0.bo.DPSopWeek0ParamBO;
import com.fa.dp.business.sop.week0.dao.DPSopWeek0ParamsDao;
import com.fa.dp.business.sop.week0.entity.DPSopWeek0Param;
import com.fa.dp.business.sop.week0.input.info.DPSopWeek0ParamInfo;
import com.fa.dp.business.sop.weekN.bo.DPSopWeekNFileProcessBO;
import com.fa.dp.business.sop.weekN.bo.DPSopWeekNParamBO;
import com.fa.dp.business.sop.weekN.dao.DPSopWeekNProcessStatusDao;
import com.fa.dp.business.sop.weekN.entity.DPSopWeekNParam;
import com.fa.dp.business.sop.weekN.entity.DPSopWeekNProcessStatus;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamEntryInfo;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamInfo;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNProcessStatusInfo;
import com.fa.dp.business.util.DPFileProcessStatus;
import com.fa.dp.business.util.IntegrationType;
import com.fa.dp.business.util.ThreadPoolExecutorUtil;
import com.fa.dp.core.cache.CacheManager;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.exception.codes.CoreExceptionCodes;
import com.fa.dp.core.systemparam.util.AppParameterConstant;
import com.fa.dp.core.util.DateConversionUtil;
import com.fa.dp.core.util.KeyValue;
import com.fa.dp.core.util.RAClientConstants;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

/**
 * @author misprakh
 */
@Slf4j
@Named
public class DPSopWeekNParamDelegateImpl implements DPSopWeekNParamDelegate {

	@Inject
	private DPSopWeekNParamBO dpSopWeekNParamBO;

	@Inject
	private RRMigration rRMigration;

	@Inject
	private CacheManager cacheManager;

	@Inject
	private SopHubzuDBClient sopHubzuDBClient;

	@Inject
	private DPMigrationMapDao dpMigrationMapDao;

	@Inject
	private DPSopWeek0ParamsDao dpSopWeek0ParamsDao;

	@Inject
	private DPSopWeekNProcessStatusDao dpSopWeekNProcessStatusDao;

	@Inject
	private DPSopWeekNFileProcessBO dpSopWeekNFileProcessBO;

	@Inject
	private DPSopWeekNFileProcessDelegate dpSopWeekNFileProcessDelegate;

	@Inject
	private DPSopWeek0ParamBO sopWeek0ParamBO;

	@Inject
	private StageFiveDBClient stageDBClient;

	@Value("${SOP_WEEKN_HUBZU_QUERY_IN_CLAUSE_COUNT}")
	private int listSplitCount;

	@Value("${SOPWEEKN_CONCURRENT_DBCALL_POOL_SIZE}")
	private int concurrentDbCallPoolSize;
	
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

	/**
	 * @param selectedDateMillis
	 *
	 * @return DPSopWeekNParamEntryInfo
	 *
	 * @throws SystemException
	 */
	@Override
	public DPSopWeekNParamEntryInfo fetchSopWeekNFromHubzu(Long selectedDateMillis) throws SystemException {
		log.info("RR Migration : checkAndUpdateNonUpdatedAssets started");
		rRMigration.checkAndUpdateNonUpdatedAssets();
		log.info("RR Migration : checkAndUpdateNonUpdatedAssets finished");
		Set<String> nonMigratedProps = rRMigration.getNonMigratedProps();
		DPSopWeekNParamEntryInfo dpProcessParamEntryInfo = new DPSopWeekNParamEntryInfo();
		DPSopWeekNParamInfo sopWeekNParamInfo = new DPSopWeekNParamInfo();
		List<HubzuInfo> finalHubzuList = new ArrayList<>();
		List<String> listOfSkippedAssetId = new ArrayList<>();
		Long startTime;

		String mostRecentListEndDate = dpSopWeekNParamBO.findMostRecentListEndDate();
		sopWeekNParamInfo.setListStrtDateDtNn(mostRecentListEndDate);
		sopWeekNParamInfo.setListEndDateDtNn(
				DateConversionUtil.getEstDate(selectedDateMillis).toString(DateTimeFormat.forPattern(DATE_DD_MMM_YY)).toUpperCase());

		String userSelectedDate = DateConversionUtil.getEstDate(selectedDateMillis).toString(DateTimeFormat.forPattern(DATE_DD_MMM_YY)).toUpperCase();

		Map<String, String> hubzuQuery = new HashMap<>(RAClientConstants.HUBZU_MAP_SIZE);
		hubzuQuery.put(RAClientConstants.HUBZU_QUERY,
				(String) cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_SOP_WEEKN_TOP_ROWS_HUBZU_QUERY));
		hubzuQuery.put(RAClientConstants.HUBZU_INTEGRATION_TYPE, IntegrationType.SOP_WEEKN_HUBZU_TOP_ROWS_INTEGRATION.getIntegrationType());

		startTime = DateTime.now().getMillis();
		HubzuDBResponse retrieveHubzuTopRows = sopHubzuDBClient.fetchSOPWeekNHubzuData(sopWeekNParamInfo, hubzuQuery, Boolean.TRUE);
		log.info("Time taken for top records from hubzu query : {}ms", (DateTime.now().getMillis() - startTime));
		if(CollectionUtils.isNotEmpty(retrieveHubzuTopRows.getHubzuInfos())) {
			List<String> topAssetsFromHbz = retrieveHubzuTopRows.getHubzuInfos().stream().map(hbzData -> hbzData.getSelrPropIdVcNn())
					.collect(Collectors.toList());
			final Map<String, String> newPropTempToOldPropTempMap = new HashMap<String, String>();
			final Map<String, String> propTempToLoanNumberMap = new HashMap<String, String>();
			startTime = DateTime.now().getMillis();
			rRMigration.getMigrationMaps(newPropTempToOldPropTempMap, propTempToLoanNumberMap, topAssetsFromHbz);
			log.info("Time taken to get Migration Maps from RR : {}ms", (DateTime.now().getMillis() - startTime));
			final Map<String, String> oldPropTempToNewPropTempMap = newPropTempToOldPropTempMap.entrySet().stream()
					.collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
			dpMigrationMapDao.deleteAll();
			oldPropTempToNewPropTempMap.forEach((k, v) -> {
				if(nonMigratedProps.contains(k)) {
					DPMigrationMap dpMigrationMap = new DPMigrationMap();
					dpMigrationMap.setAssetNumber(propTempToLoanNumberMap.get(v));
					dpMigrationMap.setOldAssetNumber(propTempToLoanNumberMap.get(k));
					dpMigrationMap.setPropTemp(v);
					dpMigrationMapDao.save(dpMigrationMap);
				}
			});
			rRMigration.retrospectUpdateMigrationInformation();
			dpMigrationMapDao.deleteAll();

			// Removing old prop if new prop already present
			Set<String> topPropTempSet = retrieveHubzuTopRows.getHubzuInfos().stream().map(HubzuInfo::getSelrPropIdVcNn).collect(Collectors.toSet());
			Set<String> topPropTempFilteredSet = topPropTempSet.stream()
					.filter(prop -> !topPropTempSet.contains(oldPropTempToNewPropTempMap.get(prop))).collect(Collectors.toSet());
			List<HubzuInfo> hubzuInfos = new ArrayList<HubzuInfo>();
			DateTime userSelectedDateInEst = DateConversionUtil.getEstDate(selectedDateMillis);
			retrieveHubzuTopRows.getHubzuInfos().forEach(record -> {
				DateTime listEndDate = new DateTime(record.getListEndDateDtNn());
				if(topPropTempFilteredSet.contains(record.getSelrPropIdVcNn()) && ((userSelectedDateInEst.getYear() > listEndDate.getYear()) || (
						(userSelectedDateInEst.getYear() == listEndDate.getYear()) && (userSelectedDateInEst.getDayOfYear() > listEndDate
								.getDayOfYear())))) {
					hubzuInfos.add(record);
				}
			});
			retrieveHubzuTopRows.setHubzuInfos(hubzuInfos);
			hubzuQuery.put(RAClientConstants.HUBZU_QUERY,
					(String) cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_SOP_WEEKN_ALL_ROWS_HUBZU_QUERY));
			hubzuQuery.put(RAClientConstants.HUBZU_INTEGRATION_TYPE, IntegrationType.SOP_WEEKN_HUBZU_ALL_ROWS_INTEGRATION.getIntegrationType());
			startTime = DateTime.now().getMillis();
			HubzuDBResponse retrieveHubzuAllRows = sopHubzuDBClient
					.retrieveHubzuAllRowsSOPWeekN(retrieveHubzuTopRows, hubzuQuery, Boolean.TRUE, newPropTempToOldPropTempMap, userSelectedDate);
			log.info("Time taken to retrieve all rows from hubzu : {}ms", (DateTime.now().getMillis() - startTime));

			if(CollectionUtils.isNotEmpty(retrieveHubzuAllRows.getHubzuInfos())) {
				List<DPSopWeekNParamInfo> columnEntries = new ArrayList<>(retrieveHubzuAllRows.getHubzuInfos().size());
				Map<String, String> skippedOddListings = new HashMap<>();

				// Merging old and new loanNumber by replacing old with new
				retrieveHubzuAllRows.getHubzuInfos().stream()
						.filter(hubzuInfo -> oldPropTempToNewPropTempMap.containsKey(hubzuInfo.getSelrPropIdVcNn()))
						.forEach(hubzuInfo -> hubzuInfo.setSelrPropIdVcNn(oldPropTempToNewPropTempMap.get(hubzuInfo.getSelrPropIdVcNn())));
				// Fetch loan issue with LOAN NUMBER as null - remove
				retrieveHubzuAllRows.setHubzuInfos(retrieveHubzuAllRows.getHubzuInfos().stream().filter(hubzuInfo -> {
					if(propTempToLoanNumberMap.containsKey(hubzuInfo.getSelrPropIdVcNn())) {
						return true;
					} else {
						log.error("*** No record present for property Id : {} in RR db.", hubzuInfo.getSelrPropIdVcNn());
						return false;
					}
				}).collect(Collectors.toList()));

				// Properties fetched from Hubzu DB  Validate against Week 0 DB
				List<List<HubzuInfo>> splitListHubzuInfos = ListUtils.partition(retrieveHubzuAllRows.getHubzuInfos(), listSplitCount);
				List<DPSopWeek0Param> consolidatedListOfSopWeek0Params = new ArrayList<>();

				startTime = DateTime.now().getMillis();
				splitListHubzuInfos.forEach(listOfBatch -> {
					Set<String> propsFromHbz = listOfBatch.stream().map(HubzuInfo::getSelrPropIdVcNn).collect(Collectors.toSet());
					Set<String> assetFromHbz = propsFromHbz.stream().map(prop -> propTempToLoanNumberMap.get(prop)).collect(Collectors.toSet());
					List<DPSopWeek0Param> week0DBList = dpSopWeek0ParamsDao.findLatestNonDuplicateInSopWeek0ForAsset(assetFromHbz);
					if(!week0DBList.isEmpty())
						consolidatedListOfSopWeek0Params.addAll(week0DBList);
					else
						log.error("Latest non duplicate in sop week0 not found for assets- {} ", assetFromHbz);
				});
				log.info("Time taken for retrospectUpdateMigrationInformation and  findLatestNonDuplicateInWeek0ForGivenAsset : {}ms",
						(DateTime.now().getMillis() - startTime));

				Map<String, DPSopWeek0Param> assetValueMap = consolidatedListOfSopWeek0Params.stream()
						.collect(Collectors.toMap(DPSopWeek0Param::getPropTemp, Function.identity(), (r, s) -> r));
				List<HubzuInfo> hbzList = retrieveHubzuAllRows.getHubzuInfos().stream().filter(item -> {
					if(assetValueMap.containsKey(item.getSelrPropIdVcNn()) && assetValueMap.get(item.getSelrPropIdVcNn()).getAssignmentDate() != null
							&& item.getListStrtDateDtNn() != null) {
						DateTime assignmentDate = DateConversionUtil.getEstDate(assetValueMap.get(item.getSelrPropIdVcNn()).getAssignmentDate());
						DateTime listStartDate = new DateTime(item.getListStrtDateDtNn());
						if((listStartDate.getYear() > assignmentDate.getYear()) || ((listStartDate.getYear() == assignmentDate.getYear()) && (
								listStartDate.getDayOfYear() >= assignmentDate.getDayOfYear())))
							return true;
						else
							return false;
					}
					return true;
				}).collect(Collectors.toList());

				List<List<HubzuInfo>> groupedListOfHubzuRecords = hbzList.stream()
						.sorted(Comparator.comparing(HubzuInfo::getListStrtDateDtNn).reversed())
						.collect(Collectors.groupingBy(hbzData -> hbzData.getSelrPropIdVcNn())).values().stream().collect(Collectors.toList());

				log.info("Record processing started for grouped hubzu records.");
				groupedListOfHubzuRecords.stream().forEach(listOfAllRecords -> {
					Boolean hasPriceChanged = Boolean.FALSE;
					int lastListCycle = listOfAllRecords.size();
					if(lastListCycle % 2 == 0) {
						for (int index = 0; index < listOfAllRecords.size() - 1; index++) {
							int finalIndex = index;
							if(!listOfSkippedAssetId.contains(listOfAllRecords.get(index).getSelrPropIdVcNn()) && !finalHubzuList.stream()
									.filter(o -> o.getSelrPropIdVcNn().equalsIgnoreCase(listOfAllRecords.get(finalIndex).getSelrPropIdVcNn()))
									.findFirst().isPresent() && (
									(listOfAllRecords.get(index + 1).getListPrceNt() - listOfAllRecords.get(index).getListPrceNt()) > 0)) {
								HubzuInfo hubzuInfo = new HubzuInfo();
								hubzuInfo.setListPrceNt(listOfAllRecords.get(index).getListPrceNt());
								hubzuInfo.setListStrtDateDtNn(listOfAllRecords.get(index).getListStrtDateDtNn());
								hubzuInfo.setListEndDateDtNn(listOfAllRecords.get(0).getListEndDateDtNn());
								hubzuInfo.setListSttsDtlsVc(StringUtils.isNotEmpty(listOfAllRecords.get(0).getListSttsDtlsVc()) ?
										listOfAllRecords.get(0).getListSttsDtlsVc() :
										DPAConstants.ACTIVE);
								hubzuInfo.setAutoRLSTVc(listOfAllRecords.get(0).getAutoRLSTVc());
								hubzuInfo.setDateOfLastReduction(listOfAllRecords.get(index).getListStrtDateDtNn().toString());
								hubzuInfo.setRbidPropIdVcFk(listOfAllRecords.get(index).getRbidPropIdVcFk());
								hubzuInfo.setPropStatIdVcFk(listOfAllRecords.get(index).getPropStatIdVcFk());
								hubzuInfo.setPropZipVcFk(listOfAllRecords.get(index).getPropZipVcFk());
								hubzuInfo.setSelrPropIdVcNn(listOfAllRecords.get(index).getSelrPropIdVcNn());
								hubzuInfo.setLastListCycle(lastListCycle);
								hubzuInfo.setCurrentListPrceNt(listOfAllRecords.get(0).getListPrceNt() != null ?
										String.valueOf(listOfAllRecords.get(0).getListPrceNt()) :
										null);
								hubzuInfo.setPropSttsIdVcFk(listOfAllRecords.get(0).getPropSttsIdVcFk());
								finalHubzuList.add(hubzuInfo);
								hasPriceChanged = Boolean.TRUE;
							}
						}
						if(!hasPriceChanged) {
							HubzuInfo hubzuInfo = new HubzuInfo();
							int lastRecord = lastListCycle - 1;
							hubzuInfo.setListPrceNt(listOfAllRecords.get(lastRecord).getListPrceNt());
							hubzuInfo.setListStrtDateDtNn(listOfAllRecords.get(lastRecord).getListStrtDateDtNn());
							hubzuInfo.setListEndDateDtNn(listOfAllRecords.get(0).getListEndDateDtNn());
							hubzuInfo.setListSttsDtlsVc(StringUtils.isNotEmpty(listOfAllRecords.get(0).getListSttsDtlsVc()) ?
									listOfAllRecords.get(0).getListSttsDtlsVc() :
									DPAConstants.ACTIVE);
							hubzuInfo.setAutoRLSTVc(listOfAllRecords.get(0).getAutoRLSTVc());
							hubzuInfo.setDateOfLastReduction(listOfAllRecords.get(lastRecord).getListStrtDateDtNn().toString());
							hubzuInfo.setRbidPropIdVcFk(listOfAllRecords.get(lastRecord).getRbidPropIdVcFk());
							hubzuInfo.setPropStatIdVcFk(listOfAllRecords.get(lastRecord).getPropStatIdVcFk());
							hubzuInfo.setPropZipVcFk(listOfAllRecords.get(lastRecord).getPropZipVcFk());
							hubzuInfo.setSelrPropIdVcNn(listOfAllRecords.get(lastRecord).getSelrPropIdVcNn());
							hubzuInfo.setLastListCycle(lastListCycle);
							hubzuInfo.setCurrentListPrceNt(
									listOfAllRecords.get(0).getListPrceNt() != null ? String.valueOf(listOfAllRecords.get(0).getListPrceNt()) : null);
							hubzuInfo.setPropSttsIdVcFk(listOfAllRecords.get(0).getPropSttsIdVcFk());
							finalHubzuList.add(hubzuInfo);
						} else {
							listOfSkippedAssetId.add(listOfAllRecords.get(lastListCycle - 1).getSelrPropIdVcNn());
						}
					} else {
						skippedOddListings.put(listOfAllRecords.get(0).getSelrPropIdVcNn(),
								StringUtils.equalsIgnoreCase(listOfAllRecords.get(0).getRbidPropIdVcFk().substring(0, 3), DPAConstants.PHH_ACNT_ID) ?
										DPAConstants.PHH :
										StringUtils.equalsIgnoreCase(listOfAllRecords.get(0).getRbidPropIdVcFk().substring(0, 3),
												DPAConstants.NRZ_ACNT_ID) ? DPAConstants.NRZ : DPAConstants.OCN);
					}
				});
				log.info("Record processing end for grouped hubzu records.");
				for (HubzuInfo hubzu : finalHubzuList) {
					DPSopWeekNParamInfo dpProcessWeekNParamInfo = new DPSopWeekNParamInfo();
					dpProcessWeekNParamInfo.setPropTemp(hubzu.getSelrPropIdVcNn());
					dpProcessWeekNParamInfo.setAssetNumber(propTempToLoanNumberMap.get(hubzu.getSelrPropIdVcNn()));
					dpProcessWeekNParamInfo
							.setOldAssetNumber(propTempToLoanNumberMap.get(newPropTempToOldPropTempMap.get(hubzu.getSelrPropIdVcNn())));
					dpProcessWeekNParamInfo.setZipCode(hubzu.getPropZipVcFk());
					dpProcessWeekNParamInfo.setState(hubzu.getPropStatIdVcFk());
					dpProcessWeekNParamInfo.setMostRecentPropertyStatus(hubzu.getPropSttsIdVcFk());
					dpProcessWeekNParamInfo.setRbidPropIdVcPk(hubzu.getRbidPropIdVcFk());
							/*dpProcessWeekNParamInfo.setAutoRLSTVc(hubzu.getAutoRLSTVc());
							dpProcessWeekNParamInfo.setListPrceNt(hubzu.getListPrceNt() != null ? hubzu.getListPrceNt().toString() : null);*/
					dpProcessWeekNParamInfo.setListStrtDateDtNn(hubzu.getListStrtDateDtNn() != null ? hubzu.getListStrtDateDtNn().toString() : null);
					dpProcessWeekNParamInfo.setListEndDateDtNn(hubzu.getListEndDateDtNn() != null ? hubzu.getListEndDateDtNn().toString() : null);
					dpProcessWeekNParamInfo.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
					dpProcessWeekNParamInfo.setDateOfLastReduction(hubzu.getDateOfLastReduction());
					dpProcessWeekNParamInfo.setLastListCycle(hubzu.getLastListCycle());
					dpProcessWeekNParamInfo
							.setMostRecentListEndDate(hubzu.getListEndDateDtNn() != null ? hubzu.getListEndDateDtNn().toString() : null);
					dpProcessWeekNParamInfo.setMostRecentListPrice(
							hubzu.getCurrentListPrceNt() != null ? BigDecimal.valueOf(Long.valueOf(hubzu.getCurrentListPrceNt())) : null);
					dpProcessWeekNParamInfo.setMostRecentListStatus(hubzu.getListSttsDtlsVc());
					columnEntries.add(dpProcessWeekNParamInfo);
				}
				DPSopWeekNProcessStatusInfo dpWeekNProcessStatusInfo = new DPSopWeekNProcessStatusInfo();
				//dpWeekNProcessStatusInfo.setFetchedDateStr(fetchDateStr);
				dpProcessParamEntryInfo.setDpSopWeekNProcessStatus(dpWeekNProcessStatusInfo);
				dpProcessParamEntryInfo.setColumnEntries(columnEntries);

				log.debug("Odd Listing: {}", skippedOddListings);
				log.info("Time taken for creating sop week n info : {}ms", (DateTime.now().getMillis() - startTime));

			} else {
				log.error("retrieve all records from hubzu : Records are not available.");
				dpProcessParamEntryInfo.setColumnEntries(null);
			}
		} else {
			log.error("Top records from hubzu : Records are not available from {} to {}.", mostRecentListEndDate, userSelectedDate);
			dpProcessParamEntryInfo.setColumnEntries(null);
		}
		return dpProcessParamEntryInfo;
	}

	/**
	 * @param sopWeekNParamEntryInfo
	 * @param userSelectedDate
	 * @param response
	 *
	 * @throws SystemException
	 */
	@Override
	public void sopWeekNDownloadFromHubzu(DPSopWeekNParamEntryInfo sopWeekNParamEntryInfo, Long userSelectedDate, HttpServletResponse response)
			throws SystemException {
		dpSopWeekNParamBO.createAndDownloadSopWeekNExcel(sopWeekNParamEntryInfo, userSelectedDate, response);
	}

	/**
	 * @param id
	 * @param sopWeekNParamEntryInfo
	 *
	 * @param type
	 * @return
	 * @throws SystemException
	 */
	@Override
	public void downloadSopWeekNZip(String id, DPSopWeekNParamEntryInfo sopWeekNParamEntryInfo, HttpServletResponse response,
			String type) throws SystemException {
		Map<String, List<DPSopWeekNParam>> mapOfOCNPHHandNRZListings = new HashMap<>();
		List<DPSopWeekNParam> listOfSopWeekNParamOCN = new ArrayList<>();
		List<DPSopWeekNParam> listOfSopWeekNParamNRZ = new ArrayList<>();
		List<DPSopWeekNParam> listOfSopWeekNParamPHH = new ArrayList<>();
		List<DPSopWeekNParam> listOfAllSopWeekNParamNRZ = new ArrayList<>();
		List<DPSopWeekNParam> listOfAllSopWeekNParamOCN = new ArrayList<>();
		List<DPSopWeekNParam> listOfAllSopWeekNParamPHH = new ArrayList<>();

		List<DPSopWeekNParam> listOfSopWeekNParam = dpSopWeekNParamBO.retrieveSopWeekNFilesDetailsById(id);

		Optional<DPSopWeekNParam> optionalObj = listOfSopWeekNParam.stream().filter(s -> s != null).findFirst();
		DPSopWeekNParam sopWeekNParam = optionalObj.orElse(null);
		String zipFileName =
				DPAConstants.FILE_SOP_WEEKN + DateConversionUtil.getEstDate(sopWeekNParam.getSopWeekNProcessStatus().getLastModifiedDate())
						.toString(DateConversionUtil.ZIP_DATE_TIME_FORMAT);
		String sysGnrtdInputFileName = dpSopWeekNProcessStatusDao.findSysGnrtdInputFileNameById(id);
		if(type == null || !dpSopWeekNParamBO.findSopWeekNReports(sysGnrtdInputFileName, zipFileName, response)) {
			// Preparing List of PHH, NRZ and OCN for Recommendation  sheet
			listOfSopWeekNParam.forEach(weeknEntry -> {
				if(StringUtils.equals(weeknEntry.getClassification(), DPAConstants.OCN))
					listOfSopWeekNParamOCN.add(weeknEntry);
				if(StringUtils.equals(weeknEntry.getClassification(), DPAConstants.NRZ))
					listOfSopWeekNParamNRZ.add(weeknEntry);
				if(StringUtils.equals(weeknEntry.getClassification(), DPAConstants.PHH))
					listOfSopWeekNParamPHH.add(weeknEntry);
			});

			// Preparing List of PHH, NRZ and OCN for Successful under review sheet
			List<DPSopWeekNParam> listOfAllWeekNParams = dpSopWeekNParamBO.findAllSopWeekNForDeliveryDateAndPropSoldDateNull();

			listOfAllWeekNParams.forEach(allWeekNParams -> {
				if(StringUtils.equals(allWeekNParams.getClassification(), DPAConstants.OCN))
					listOfAllSopWeekNParamOCN.add(allWeekNParams);
				if(StringUtils.equals(allWeekNParams.getClassification(), DPAConstants.NRZ))
					listOfAllSopWeekNParamNRZ.add(allWeekNParams);
				if(StringUtils.equals(allWeekNParams.getClassification(), DPAConstants.PHH))
					listOfAllSopWeekNParamPHH.add(allWeekNParams);
			});

			mapOfOCNPHHandNRZListings.put(DPAConstants.LIST_WKN_OCN, listOfSopWeekNParamOCN);
			mapOfOCNPHHandNRZListings.put(DPAConstants.LIST_WKN_NRZ, listOfSopWeekNParamNRZ);
			mapOfOCNPHHandNRZListings.put(DPAConstants.LIST_WKN_PHH, listOfSopWeekNParamPHH);
			mapOfOCNPHHandNRZListings.put(DPAConstants.LIST_SCCS_UDR_OCN, listOfAllSopWeekNParamOCN);
			mapOfOCNPHHandNRZListings.put(DPAConstants.LIST_SCCS_UDR_NRZ, listOfAllSopWeekNParamNRZ);
			mapOfOCNPHHandNRZListings.put(DPAConstants.LIST_SCCS_UDR_PHH, listOfAllSopWeekNParamPHH);

			dpSopWeekNParamBO.generateSopWeekNZipFile(mapOfOCNPHHandNRZListings, response, zipFileName, sysGnrtdInputFileName, type);
		}
		if(sopWeekNParamEntryInfo != null) {
			DPSopWeekNProcessStatus sopWeekNProcess = dpSopWeekNFileProcessBO
					.findSopWeekNProcessById(sopWeekNParamEntryInfo.getDpSopWeekNProcessStatus().getId());
			if(!ObjectUtils.isEmpty(sopWeekNProcess) && sopWeekNProcess.getStatus().equals(DPFileProcessStatus.IN_PROGRESS.getFileStatus())) {
				sopWeekNProcess.setStatus(dpSopWeekNFileProcessDelegate.setSopWeekNFileStatus(sopWeekNParamEntryInfo));
				dpSopWeekNProcessStatusDao.save(sopWeekNProcess);
			}
		}
	}

	@Override
	public List<DPSopWeekNParamInfo> searchSopWeekNParamSuccesfulUnderRiview(String assetNumber) throws SystemException {
		List<DPSopWeekNParamInfo> sopWeeknParamInfoList = null;
		try {
			sopWeeknParamInfoList = dpSopWeekNParamBO.searchSopWeekNParamSuccesfulUnderRiview(assetNumber);
		} catch (SystemException se) {
			log.error("Problem in fetching sop WeekN Param Succesful Underrivew", se);
			throw se;
		}
		return sopWeeknParamInfoList;
	}

	@Override
	public void saveSopWeekNParamInfo(DPSopWeekNParamInfo paramEntry) throws SystemException {
		try {
			dpSopWeekNParamBO.saveSopWeekNParamInfo(paramEntry);
		} catch (SystemException se) {
			log.error("Problem in saving WeekN Param data", se);
			throw se;
		}
	}

	@Override
	public void saveSopWeekNParamInfoList(List<DPSopWeekNParamInfo> sopWeekNInfoList) throws SystemException {
		try {
			dpSopWeekNParamBO.saveSopWeekNParamInfoList(sopWeekNInfoList);
		} catch (SystemException se) {
			log.error("Problem in saving WeekN Param data", se);
			throw se;
		}
	}

	@Override
	public List<DPSopWeek0ParamInfo> fetchSopWeek0ParamsRA(String assetNumber, String eligible) throws SystemException {
		List<DPSopWeek0ParamInfo> sopWeeknParamInfoList = null;
		try {
			sopWeeknParamInfoList = sopWeek0ParamBO.fetchSopWeek0ParamsRA(assetNumber, eligible);
		} catch (SystemException se) {
			log.error("Problem in fetching sop WeekN Param for ra call input preparation", se);
			throw se;
		}
		return sopWeeknParamInfoList;
	}

	@Override
	public DPSopWeekNParamInfo findSopWeekNParamById(String id) throws SystemException {
		DPSopWeekNParamInfo paramInfo = null;
		try {
			paramInfo = dpSopWeekNParamBO.findSopWeekNParamById(id);
		} catch (SystemException se) {
			log.error("Problem in fetching WeekN Param data", se);
			throw se;
		}
		return paramInfo;
	}

	@Override
	public void populateSopWeekNOutputParam(DPSopWeekNParamInfo paramInfo, Map response) throws SystemException {
		log.info("WeekNRAService -> DPProcessWeekZeroParamInfo ID : " + (paramInfo != null ? paramInfo.getId() : "NA"));
		log.info("WeekNRAService -> response : " + response);

		DocumentContext parsedContext = JsonPath.parse(response);

		boolean success = parsedContext.read("$.header.success", Boolean.class);
		log.info("$.header.success value : " + success);

		String errorMessage = null;
		DPSopWeekNParamInfo sopWeekNParamInfo = this.findSopWeekNParamById(paramInfo.getId());
		if(success) {
			String mostRecentListStatus = parsedContext.read(RAClientConstants.MOST_RECENT_LIST_STATUS, String.class);
			String listEndDate = parsedContext.read(RAClientConstants.MOST_RECENT_LIST_END_DATE, String.class);
			String recentPropList = parsedContext.read(RAClientConstants.MOST_RECENT_PROPERTY_STATUS, String.class);
			BigDecimal recentListPrice = BigDecimal.valueOf(parsedContext.read(RAClientConstants.MOST_RECENT_LIST_PRICE, Double.class));
			BigDecimal dollarAdjusRec = BigDecimal
					.valueOf(parsedContext.read(RAClientConstants.LIST_PRICE_DOLLAR_ADJUSTMENT_RECOMMENDATION, Double.class));
			BigDecimal percentAdjusRec = BigDecimal
					.valueOf(parsedContext.read(RAClientConstants.LIST_PRICE_PERCENT_ADJUSTMENT_RECOMMENDATION, Double.class));
			String modelVer = parsedContext.read("$.data.output.Model_Version", String.class);

			if(StringUtils.equalsIgnoreCase(mostRecentListStatus, RAClientConstants.SUCCESSFUL) || StringUtils
					.equalsIgnoreCase(mostRecentListStatus, RAClientConstants.UNDERREVIEW)) {
				// for mostRecentListStatus =SUCCESSFUL/UNDERREVIEW AS per story
				// DP-238
				// we need to set delivery date to null
				sopWeekNParamInfo.setDeliveryDate(null);
			} else {
				sopWeekNParamInfo.setMostRecentListStatus(mostRecentListStatus);
				sopWeekNParamInfo.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
			}
			sopWeekNParamInfo.setClassification(paramInfo.getClassification());
			sopWeekNParamInfo.setMostRecentListEndDate(listEndDate);
			sopWeekNParamInfo.setMostRecentPropertyStatus(recentPropList);
			sopWeekNParamInfo.setMostRecentListPrice(recentListPrice);
			sopWeekNParamInfo.setLpDollarAdjustmentRec(dollarAdjusRec);
			sopWeekNParamInfo.setLpPercentAdjustmentRec(percentAdjusRec);
			sopWeekNParamInfo.setModelVersion(modelVer);
			sopWeekNParamInfo.setEligible(DPProcessParamAttributes.ELIGIBLE.getValue());
			sopWeekNParamInfo.setClientCode(paramInfo.getClientCode());
			sopWeekNParamInfo.setPrivateMortgageInsurance(paramInfo.getPrivateMortgageInsurance());
			sopWeekNParamInfo.setSellerOccupiedProperty(paramInfo.getSellerOccupiedProperty());
			sopWeekNParamInfo.setState(paramInfo.getState());
			sopWeekNParamInfo.setZipCode(paramInfo.getZipCode());
			sopWeekNParamInfo.setInitialValuation(paramInfo.getInitialValuation());
			sopWeekNParamInfo.setRbidPropIdVcPk(paramInfo.getRbidPropIdVcPk());
			this.saveSopWeekNParamInfo(sopWeekNParamInfo);
			// BeanUtils.copyProperties(info, sopWeekNParamInfo);
		} else {
			errorMessage = parsedContext.read(RAClientConstants.HEADER_ERROR_MESSAGE, String.class);
			log.error("SOP WEEKN RA FAILURE " + DPProcessParamAttributes.NOTES_RA.getValue());
			sopWeekNParamInfo.setEligible(DPProcessFilterParams.ELIGIBLE.getValue());
			sopWeekNParamInfo.setExclusionReason(DPProcessParamAttributes.NOTES_RA.getValue());
			paramInfo.setFailedStepCommandName(MDC.get(RAClientConstants.COMMAND_PROCES));
			paramInfo.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
			sopWeekNParamInfo.setFailedStepCommandName(MDC.get(RAClientConstants.COMMAND_PROCES));
			sopWeekNParamInfo.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
			sopWeekNParamInfo.setSellerOccupiedProperty(paramInfo.getSellerOccupiedProperty());
			sopWeekNParamInfo.setState(paramInfo.getState());
			sopWeekNParamInfo.setZipCode(paramInfo.getZipCode());
			sopWeekNParamInfo.setInitialValuation(paramInfo.getInitialValuation());
			sopWeekNParamInfo.setClientCode(paramInfo.getClientCode());
			sopWeekNParamInfo.setPrivateMortgageInsurance(paramInfo.getPrivateMortgageInsurance());
			sopWeekNParamInfo.setRbidPropIdVcPk(paramInfo.getRbidPropIdVcPk());
			this.saveSopWeekNParamInfo(sopWeekNParamInfo);
		}

		// auditRAStatus(sopWeekNParamInfo, success, startTime, errorMessage);
	}

	@Override
	public void getHubzuData(DPSopWeekNParamEntryInfo infoObject) throws SystemException {
		Long startTime = System.currentTimeMillis();
		log.info("sopWeekNHubzuDBCall -> processTask started.");
		List<DPSopWeekNParamInfo> successEntries = new ArrayList<>();
		List<Future<KeyValue<HubzuDBResponse, DPSopWeekNParamInfo>>> futureList = new ArrayList<>();
		try {
			// Map Contains Integration Type , Hubzu Query
			Map<String, String> hubzuQuery = new HashMap<>(RAClientConstants.HUBZU_MAP_SIZE);
			hubzuQuery
					.put(RAClientConstants.HUBZU_QUERY, (String) cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_SOP_WEEKN_HUBZU_QUERY));
			hubzuQuery.put(RAClientConstants.HUBZU_INTEGRATION_TYPE, IntegrationType.HUBZU_SOP_INTEGRATION.getIntegrationType());

			infoObject.getColumnEntries().forEach(dpSopWeekNParamInfo -> {
				if(null == dpSopWeekNParamInfo.getFailedStepCommandName()) {
					Future<KeyValue<HubzuDBResponse, DPSopWeekNParamInfo>> future = executorService
							.submit(fetchHubzuData(dpSopWeekNParamInfo, hubzuQuery, infoObject.isFetchProcess()));
					futureList.add(future);
				}
			});

			for (Future<KeyValue<HubzuDBResponse, DPSopWeekNParamInfo>> keyValueFuture : futureList) {
				KeyValue<HubzuDBResponse, DPSopWeekNParamInfo> keyValuePair = keyValueFuture.get();
				HubzuDBResponse hubzuDBResponse = keyValuePair.getKey();
				DPSopWeekNParamInfo dpSopWeekNParamInfo = keyValuePair.getValue();
				MDC.put(RAClientConstants.LOAN_NUMBER, dpSopWeekNParamInfo.getAssetNumber());
				dpSopWeekNParamInfo.setHubzuDBResponse(keyValuePair.getKey());

				if(ObjectUtils.isEmpty(dpSopWeekNParamInfo.getHubzuDBResponse())) {
					dpSopWeekNParamInfo.setFailedStepCommandName(MDC.get(RAClientConstants.COMMAND_PROCES));
					dpSopWeekNParamInfo.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
					dpSopWeekNParamInfo.setEligible(DPProcessFilterParams.ELIGIBLE.getValue());
					dpSopWeekNParamInfo.setExclusionReason(DPProcessFilterParams.HUBZU_DB_CALL_EXCLUSION.getValue());
					if(!infoObject.isFetchProcess())
						dpSopWeekNParamBO.saveSopWeekNParamInfo(dpSopWeekNParamInfo);
				} else if(CollectionUtils.isEmpty(dpSopWeekNParamInfo.getHubzuDBResponse().getHubzuInfos())){
					dpSopWeekNParamInfo.setFailedStepCommandName(MDC.get(RAClientConstants.COMMAND_PROCES));
					dpSopWeekNParamInfo.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
					dpSopWeekNParamInfo.setEligible(DPProcessFilterParams.ELIGIBLE.getValue());
					dpSopWeekNParamInfo.setExclusionReason(DPProcessFilterParams.HUBZU_DB_CALL_EXCLUSION.getValue());
					if(!infoObject.isFetchProcess())
						dpSopWeekNParamBO.saveSopWeekNParamInfo(dpSopWeekNParamInfo);
				} else if(StringUtils.equalsIgnoreCase(dpSopWeekNParamInfo.getHubzuDBResponse().getHubzuInfos()
						.get(dpSopWeekNParamInfo.getHubzuDBResponse().getHubzuInfos().size() - 1).getSopProgramStatus(), "N")){
					dpSopWeekNParamInfo.setFailedStepCommandName(
						StringUtils.equalsIgnoreCase(dpSopWeekNParamInfo.getClassification(), DPAConstants.NRZ) ? 
							DPAConstants.NRZ_SOPWEEKN_SOPFILTER : StringUtils.equalsIgnoreCase(dpSopWeekNParamInfo.getClassification(), 
							DPAConstants.OCN) ? DPAConstants.OCN_SOPWEEKN_SOPFILTER : DPAConstants.PHH_SOPWEEKN_SOPFILTER);
					dpSopWeekNParamInfo.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
					dpSopWeekNParamInfo.setEligible(DPProcessParamAttributes.INELIGIBLE.getValue());
					dpSopWeekNParamInfo.setExclusionReason(DPProcessFilterParams.VACANT_EXCLUSION_REASON.getValue());
					if(!infoObject.isFetchProcess())
						dpSopWeekNParamBO.saveSopWeekNParamInfo(dpSopWeekNParamInfo);
				} else {
					dpSopWeekNParamInfo
							.setSellerOccupiedProperty(dpSopWeekNParamInfo.getHubzuDBResponse().getHubzuInfos().get(0).getSopProgramStatus());
					DateTime parsedDate = dpSopWeekNParamInfo.getHubzuDBResponse().getHubzuInfos().get(0).getCurrentListEndDate() != null ?
							DateTime.parse(dpSopWeekNParamInfo.getHubzuDBResponse().getHubzuInfos().get(0).getCurrentListEndDate(),
									DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")) :
							null;
					dpSopWeekNParamInfo.setMostRecentListEndDate(
							parsedDate != null ? parsedDate.toDateTime().toString(DateTimeFormat.forPattern(RAClientConstants.DATE_FORMAT)) : null);
					if(null == dpSopWeekNParamInfo.getState())
						dpSopWeekNParamInfo.setState(dpSopWeekNParamInfo.getHubzuDBResponse().getHubzuInfos().get(0).getPropStatIdVcFk());
					else if(!dpSopWeekNParamInfo.getState()
							.equals(dpSopWeekNParamInfo.getHubzuDBResponse().getHubzuInfos().get(0).getPropStatIdVcFk())) {
						for (HubzuInfo hubzuInfo : dpSopWeekNParamInfo.getHubzuDBResponse().getHubzuInfos()) {
							hubzuInfo.setPropStatIdVcFk(dpSopWeekNParamInfo.getState());
						}
					}
					if(null == dpSopWeekNParamInfo.getZipCode())
						dpSopWeekNParamInfo.setZipCode(dpSopWeekNParamInfo.getHubzuDBResponse().getHubzuInfos().get(0).getPropZipVcFk());
					else if(!dpSopWeekNParamInfo.getZipCode()
							.equals(dpSopWeekNParamInfo.getHubzuDBResponse().getHubzuInfos().get(0).getPropZipVcFk())) {
						for (HubzuInfo hubzuInfo : dpSopWeekNParamInfo.getHubzuDBResponse().getHubzuInfos()) {
							hubzuInfo.setPropZipVcFk(dpSopWeekNParamInfo.getZipCode());
						}
					}
					if(null == dpSopWeekNParamInfo.getRbidPropIdVcPk())
						dpSopWeekNParamInfo.setRbidPropIdVcPk(dpSopWeekNParamInfo.getHubzuDBResponse().getHubzuInfos()
								.get(dpSopWeekNParamInfo.getHubzuDBResponse().getHubzuInfos().size() - 1).getRbidPropIdVcPk());
					successEntries.add(dpSopWeekNParamInfo);
					MDC.remove(RAClientConstants.LOAN_NUMBER);
				}
			}

			log.info("time taken for all weekNHubzuDBCall records : " + (System.currentTimeMillis() - startTime));
			if(!infoObject.isFetchProcess())
				infoObject.setColumnEntries(successEntries);
		} catch (InterruptedException | ExecutionException e) {
			log.error("An error occurred while fetching hubzu data {}", e);
			throw new SystemException(CoreExceptionCodes.DPSOPWKN019, e.getMessage());
		} finally {
			MDC.remove(RAClientConstants.LOAN_NUMBER);
		}
		log.info("weekNHubzuDBCall -> processTask ended.");
	}

	private Callable<KeyValue<HubzuDBResponse, DPSopWeekNParamInfo>> fetchHubzuData(final DPSopWeekNParamInfo dpSopWeekNParamInfo,
			final Map<String, String> hubzuQuery, final Boolean isFetchProcess) {
		final Map<String, String> mdcContext = MDC.getCopyOfContextMap();
		return DelegatingSecurityContextCallable.create(() -> {
			if(mdcContext != null)
				MDC.setContextMap(mdcContext);
			MDC.put(RAClientConstants.LOAN_NUMBER, dpSopWeekNParamInfo.getAssetNumber());
			HubzuDBResponse hubzuDBResponse = sopHubzuDBClient.fetchSOPWeekNHubzuData(dpSopWeekNParamInfo, hubzuQuery, isFetchProcess);
			MDC.remove(RAClientConstants.LOAN_NUMBER);
			return new KeyValue<>(hubzuDBResponse, dpSopWeekNParamInfo);
		}, SecurityContextHolder.getContext());
	}

	@Override
	public void getStage5Data(DPSopWeekNParamEntryInfo infoObject) throws SystemException {
		Long startTime = System.currentTimeMillis();
		List<DPSopWeekNParamInfo> columnEntries = new ArrayList<>();
		List<DPSopWeekNParamInfo> successEntries = new ArrayList<>();
		List<DPSopWeekNParamInfo> failedEntries = new ArrayList<>();
		try {
			// Creating list of successfull entries from previous columns
			columnEntries = infoObject.getColumnEntries().stream().filter(entries -> null == entries.getFailedStepCommandName())
					.collect(Collectors.toList());
			// Calling Stored Procedure for Stage 5 DB Data
			if(CollectionUtils.isNotEmpty(columnEntries)) {
				successEntries = stageDBClient.getStage5SOPFromStoredProcedure(columnEntries);
			}
			if(CollectionUtils.isNotEmpty(successEntries))
				infoObject.setColumnEntries(successEntries);

			log.info("time taken for all stage5 records : " + (System.currentTimeMillis() - startTime));
		} catch (SystemException se) {
			log.error("Error Occured in Stage5 DB call" + se);
			columnEntries.stream().forEach(entry -> {
				entry.setFailedStepCommandName(MDC.get(RAClientConstants.COMMAND_PROCES));
				entry.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
				entry.setEligible(DPProcessFilterParams.ELIGIBLE.getValue());
				entry.setAssignment(DPProcessFilterParams.ASSIGNMENT_ERROR.getValue());
				entry.setExclusionReason(DPProcessFilterParams.STAGE5_DB_CALL_EXCLUSION.getValue());
				failedEntries.add(entry);
			});
		} catch (Exception e) {
			log.error("Error Occured in Stage5 DB call" + e);
			columnEntries.stream().forEach(entry -> {
				entry.setFailedStepCommandName(MDC.get(RAClientConstants.COMMAND_PROCES));
				entry.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
				entry.setEligible(DPProcessFilterParams.ELIGIBLE.getValue());
				entry.setAssignment(DPProcessFilterParams.ASSIGNMENT_ERROR.getValue());
				entry.setExclusionReason(DPProcessFilterParams.STAGE5_DB_CALL_EXCLUSION.getValue());
				failedEntries.add(entry);
			});
		} finally {
			if(CollectionUtils.isNotEmpty(failedEntries)) {
				dpSopWeekNParamBO.saveSopWeekNParamInfoList(failedEntries);
			}
		}
	}

	@Override
	public List<DPSopWeekNParamInfo> getAssetDetails(String fileId, String type) throws SystemException {
		return dpSopWeekNParamBO.getAssetDetails(fileId, type);
	}
	
	@Override
	public void processSopQAReportHubzuData(DPSopWeekNParamEntryInfo infoObject) {
		Long startTime = System.currentTimeMillis();
		log.info("sopWeekNHubzuDBCall -> processTask started.");

		List<Future<KeyValue<HubzuDBResponse, DPSopWeekNParamInfo>>> futureList = new ArrayList<>();
		String hubzuQuery = (String) cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_SOP_WEEKN_HUBZU_QUERY);
		infoObject.getColumnEntries().forEach(dpSopWeekNParamInfo -> {
			if(null == dpSopWeekNParamInfo.getFailedStepCommandName()) {
				Future<KeyValue<HubzuDBResponse, DPSopWeekNParamInfo>> future = executorService
						.submit(fetchSopQaHubzuData(dpSopWeekNParamInfo, hubzuQuery));
				futureList.add(future);
			}
		});

		for (Future<KeyValue<HubzuDBResponse, DPSopWeekNParamInfo>> keyValueFuture : futureList) {

			KeyValue<HubzuDBResponse, DPSopWeekNParamInfo> keyValuePair = null;
			try {
				keyValuePair = keyValueFuture.get();
			} catch (InterruptedException e) {
				log.error("Inturrupted exception {}", e);
			} catch (ExecutionException e) {
				log.error("Execution exception {}", e);
			}
			if(keyValuePair != null) {
				HubzuDBResponse hubzuDBResponse = keyValuePair.getKey();
				DPSopWeekNParamInfo dPProcessParamInfo = keyValuePair.getValue();
				MDC.put(RAClientConstants.LOAN_NUMBER, dPProcessParamInfo.getAssetNumber());
				dPProcessParamInfo.setHubzuDBResponse(hubzuDBResponse);
				if(StringUtils.isEmpty(dPProcessParamInfo.getState()) && dPProcessParamInfo.getHubzuDBResponse() != null && CollectionUtils
						.isNotEmpty(dPProcessParamInfo.getHubzuDBResponse().getHubzuInfos())
						&& dPProcessParamInfo.getHubzuDBResponse().getHubzuInfos().size() > 0) {
					dPProcessParamInfo.setState(dPProcessParamInfo.getHubzuDBResponse().getHubzuInfos().get(0).getPropStatIdVcFk());
				}
			}
		}

		log.info("time taken for all sopWeekNHubzuDBCall records : " + (System.currentTimeMillis() - startTime));
		log.info("sopWeekNHubzuDBCall -> processTask ended.");
	}
	
	@Override
	public void filterSOPQAReportAssigment(DPSopWeekNParamEntryInfo dpProcessParamEntryInfo) {
		log.info("Filtering on Assignment started..");
		if(CollectionUtils.isNotEmpty(dpProcessParamEntryInfo.getColumnEntries())) {
			log.info("Filtering on Assignment started..");
			dpSopWeekNParamBO.filterSopQAReportAssignment(dpProcessParamEntryInfo.getColumnEntries());
		}
		log.info("Filtering on Assignment ended..");
	}
	
	@Override
	public void filterSopQAReportState(DPSopWeekNParamEntryInfo inputParamEntry) {
		if(null != inputParamEntry.getColumnEntries() && !inputParamEntry.getColumnEntries().isEmpty()) {
			log.info("filterSopQAReportState started..");
			dpSopWeekNParamBO.filterSopQAReportState(inputParamEntry.getColumnEntries());
			log.info("filterSopQAReportState ended..");
		}
	}
	
	@Override
	public void filterSopQAReportSSPmi(DPSopWeekNParamEntryInfo infoObject) {
		dpSopWeekNParamBO.filterSopQAReportSSPmiFlag(infoObject);
	}
	
	private Callable<KeyValue<HubzuDBResponse, DPSopWeekNParamInfo>> fetchSopQaHubzuData(final DPSopWeekNParamInfo dpProcessWeekNParamInfo,
			final String hubzuQuery) {
		final Map<String, String> mdcContext = MDC.getCopyOfContextMap();
		return DelegatingSecurityContextCallable.create(() -> {
			Map<String, String> hubzuQueryMap = new HashMap<>(RAClientConstants.HUBZU_MAP_SIZE);
			hubzuQueryMap.put(RAClientConstants.HUBZU_QUERY, hubzuQuery);
			hubzuQueryMap.put(RAClientConstants.HUBZU_INTEGRATION_TYPE, IntegrationType.HUBZU_SOP_INTEGRATION.getIntegrationType());
			if(mdcContext != null)
				MDC.setContextMap(mdcContext);
			MDC.put(RAClientConstants.LOAN_NUMBER, dpProcessWeekNParamInfo.getAssetNumber());
			HubzuDBResponse hubzuDBResponse = sopHubzuDBClient.fetchSOPWeekNHubzuData(dpProcessWeekNParamInfo, hubzuQueryMap, false);
			MDC.remove(RAClientConstants.LOAN_NUMBER);
			return new KeyValue<>(hubzuDBResponse, dpProcessWeekNParamInfo);
		}, SecurityContextHolder.getContext());
	}
}