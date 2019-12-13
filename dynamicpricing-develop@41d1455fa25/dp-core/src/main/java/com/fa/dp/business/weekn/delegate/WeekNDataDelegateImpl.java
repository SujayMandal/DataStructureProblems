package com.fa.dp.business.weekn.delegate;

import static com.fa.dp.core.exception.codes.CoreExceptionCodes.DPWKN0001;
import static com.fa.dp.core.exception.codes.CoreExceptionCodes.DPWKN0002;
import static com.fa.dp.core.util.DateConversionUtil.DATE_DD_MMM_YY;
import static com.fa.dp.core.util.DateConversionUtil.DATE_TIME_FORMATTER;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import com.fa.dp.business.audit.delegate.DPAuditReportDelegate;
import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.db.client.HubzuDBClient;
import com.fa.dp.business.info.HubzuDBResponse;
import com.fa.dp.business.info.HubzuInfo;
import com.fa.dp.business.rr.migration.RRMigration;
import com.fa.dp.business.rr.migration.dao.DPMigrationMapDao;
import com.fa.dp.business.rr.migration.entity.DPMigrationMap;
import com.fa.dp.business.util.DPFileProcessStatus;
import com.fa.dp.business.util.DPFileProcesses;
import com.fa.dp.business.util.IntegrationType;
import com.fa.dp.business.util.TransactionStatus;
import com.fa.dp.business.validation.file.util.InputFileValidationUtil;
import com.fa.dp.business.validator.bo.DPFileProcessBO;
import com.fa.dp.business.validator.dao.DPProcessParamsDao;
import com.fa.dp.business.validator.delegate.DPProcessDelegate;
import com.fa.dp.business.week0.delegate.DPFileProcessDelegate;
import com.fa.dp.business.week0.entity.DPProcessParam;
import com.fa.dp.business.weekn.bo.WeekNBO;
import com.fa.dp.business.weekn.dao.DPWeekNProcessStatusRepo;
import com.fa.dp.business.weekn.entity.DPProcessWeekNParam;
import com.fa.dp.business.weekn.entity.DPWeekNProcessStatus;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamEntryInfo;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamInfo;
import com.fa.dp.business.weekn.input.info.DPWeekNProcessStatusInfo;
import com.fa.dp.business.weekn.input.info.DPWeekNToInfoMapper;
import com.fa.dp.core.cache.CacheManager;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.exception.codes.CoreExceptionCodes;
import com.fa.dp.core.systemparam.provider.SystemParameterProvider;
import com.fa.dp.core.systemparam.util.AppParameterConstant;
import com.fa.dp.core.util.DateConversionUtil;
import com.fa.dp.core.util.RAClientConstants;

@Named
@Slf4j
public class WeekNDataDelegateImpl implements WeekNDataDelegate {

	@Inject
	private HubzuDBClient hubzuDBClient;

	@Inject
	private DPProcessDelegate dPProcessDelegate;

	@Inject
	private DPWeekNToInfoMapper dpWeekNToInfoMapper;

	@Inject
	private CacheManager cacheManager;

	@Inject
	private WeekNBO weekNBO;

	@Inject
	private RRMigration rRMigration;

	@Inject
	private DPWeekNProcessStatusRepo dpWeekNProcessStatusRepo;

	@Inject
	private DPProcessParamsDao dpProcessParamsDao;

	@Inject
	private DPMigrationMapDao dpMigrationMapDao;

	@Inject
	private DPAuditReportDelegate dpAuditReportDelegate;

	@Inject
	private SystemParameterProvider systemParameterProvider;

	@Inject
	private DPFileProcessBO dpFileProcessBO;

	@Inject
	private DPFileProcessDelegate dpFileProcessDelegate;

	@Value("${WEEKN_INITIAL_QUERY_IN_CLAUSE_COUNT}")
	private int listSplitCount;

	@Override
	public DPProcessWeekNParamEntryInfo fetchInitialWeekNDataFromHubzu(Long selectedDateMillis) throws SystemException, ParseException {
		rRMigration.checkAndUpdateNonUpdatedAssets();
		Set<String> nonMigratedProps = rRMigration.getNonMigratedProps();
		DPProcessWeekNParamEntryInfo dpProcessParamEntryInfo = new DPProcessWeekNParamEntryInfo();
		DPProcessWeekNParamInfo dPProcessParamInfo = new DPProcessWeekNParamInfo();
		List<HubzuInfo> finalHubzuList = new ArrayList<>();
		List<String> listOfSkippedAssetId = new ArrayList<>();
		Long startTime;

		// --- changes required as per the story #531, needs to get Most Recent List End date from Week N table
		String startDateMillis = weekNBO.findFirstListEndDate();
		dPProcessParamInfo.setListEndDateDtNnstart(startDateMillis);
		dPProcessParamInfo.setListEndDateDtNnend(DateConversionUtil.getEstDate(selectedDateMillis).toString(DateTimeFormat.forPattern(DATE_DD_MMM_YY)).toUpperCase());


		Map<String, String> hubzuQuery = new HashMap<>(RAClientConstants.HUBZU_MAP_SIZE);

		hubzuQuery.put(RAClientConstants.HUBZU_QUERY, (String) cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_INITIAL_HUBZU_QUERY));
		hubzuQuery.put(RAClientConstants.HUBZU_INTEGRATION_TYPE, IntegrationType.HUBZU_INITIAL_INTEGRATION.getIntegrationType());

		startTime = DateTime.now().getMillis();
		HubzuDBResponse hubzuDBResponseTopRecords = hubzuDBClient.fetchHubzuData(dPProcessParamInfo, hubzuQuery, Boolean.TRUE);
		log.info("Time taken for initial fetch hubzu query : " + (DateTime.now().getMillis() - startTime) + "ms");
		if (hubzuDBResponseTopRecords != null) {
			if (StringUtils.equalsIgnoreCase(TransactionStatus.FAIL.getTranStatus(), hubzuDBResponseTopRecords.getTransactionStatus())) {
				String errorMessage = hubzuDBResponseTopRecords.getErrorMsg();
				if (errorMessage.contains("Error while")) {
					// handles exception scenario
					log.error("hubzu db response message : ", errorMessage);
					throw SystemException.newSystemException(DPWKN0001, errorMessage);
				} else {
					// handles no records found
					log.error("hubzu db response message : ", errorMessage);
					throw SystemException.newSystemException(DPWKN0002, errorMessage);
				}
			} else {
				List<String> topAssetsFromHbz = hubzuDBResponseTopRecords.getHubzuInfos().stream().map(hbzData -> hbzData.getSelrPropIdVcNn()).collect(Collectors.toList());
				final Map<String, String> migrationNewPropToPropMap = new HashMap<String, String>();
				final Map<String, String> migrationPropToLoanMap = new HashMap<String, String>();
				startTime = DateTime.now().getMillis();
				rRMigration.getMigrationMaps(migrationNewPropToPropMap, migrationPropToLoanMap, topAssetsFromHbz);
				log.info("Time taken to get Migration Maps from RR : " + (DateTime.now().getMillis() - startTime) + "ms");
				final Map<String, String> migrationPropToNewPropMap = migrationNewPropToPropMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
				dpMigrationMapDao.deleteAll();
				migrationPropToNewPropMap.forEach((k, v) -> {
					if (nonMigratedProps.contains(k)) {
						DPMigrationMap dpMigrationMap = new DPMigrationMap();
						dpMigrationMap.setAssetNumber(migrationPropToLoanMap.get(v));
						dpMigrationMap.setOldAssetNumber(migrationPropToLoanMap.get(k));
						dpMigrationMap.setPropTemp(v);
						dpMigrationMapDao.save(dpMigrationMap);
					}
				});
				rRMigration.retrospectUpdateMigrationInformation();
				dpMigrationMapDao.deleteAll();

				// Removing old prop if new prop already present
				Set<String> topPropTempSet = hubzuDBResponseTopRecords.getHubzuInfos().stream().map(HubzuInfo::getSelrPropIdVcNn).collect(Collectors.toSet());
				Set<String> topPropTempFilteredSet = topPropTempSet.stream().filter(prop -> !topPropTempSet.contains(migrationPropToNewPropMap.get(prop))).collect(Collectors.toSet());
				List<HubzuInfo> hubzuInfos = new ArrayList<HubzuInfo>();
				DateTime userSelectedDate = DateConversionUtil.getEstDate(selectedDateMillis);
				hubzuDBResponseTopRecords.getHubzuInfos().forEach(record -> {
					DateTime listEndDate = new DateTime(record.getListEndDateDtNn());
					if (topPropTempFilteredSet.contains(record.getSelrPropIdVcNn()) && ((userSelectedDate.getYear() > listEndDate.getYear()) || (
							  (userSelectedDate.getYear() == listEndDate.getYear()) && (userSelectedDate.getDayOfYear() > listEndDate.getDayOfYear())))) {
						hubzuInfos.add(record);
					}
				});
				hubzuDBResponseTopRecords.setHubzuInfos(hubzuInfos);

				hubzuQuery.put(RAClientConstants.HUBZU_QUERY,
						  (String) cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_INITIAL_HUBZU_QUERY_ALL_ROWS));
				hubzuQuery.put(RAClientConstants.HUBZU_INTEGRATION_TYPE, IntegrationType.HUBZU_INITIAL_ALL_ROWS_INTEGRATION.getIntegrationType());
				startTime = DateTime.now().getMillis();
				HubzuDBResponse hubzuDBResponse = hubzuDBClient
						  .fetchAllRowsOfInitialQueryOutput(hubzuDBResponseTopRecords, hubzuQuery, Boolean.TRUE, migrationNewPropToPropMap, dPProcessParamInfo.getListEndDateDtNnend());
				log.info("Time taken for initial fetch hubzu query for all rows : " + (DateTime.now().getMillis() - startTime) + "ms");

				if (hubzuDBResponse != null) {
					if (TransactionStatus.FAIL.getTranStatus().equalsIgnoreCase(hubzuDBResponse.getTransactionStatus())) {
						String errorMessage = hubzuDBResponse.getErrorMsg();
						if (errorMessage.contains("Error while")) {
							// handles exception scenario
							log.error("hubzu db response message : ", errorMessage);
							throw SystemException.newSystemException(DPWKN0001, errorMessage);
						} else {
							// handles no records found
							log.error("hubzu db response message : ", errorMessage);
							throw SystemException.newSystemException(DPWKN0002, errorMessage);
						}
					} else {
						String fetchDateStr = DateConversionUtil.getCurrentEstDate().toString(DATE_TIME_FORMATTER);

						// add code for success scenario
						List<DPProcessWeekNParamInfo> columnEntries = new ArrayList<>(hubzuDBResponse.getHubzuInfos().size());
						Map<String, String> skippedOddListings = new HashMap<>();

						// merging old and new loanNumber by replacing old with new
						hubzuDBResponse.getHubzuInfos().stream().filter(hubzuInfo -> migrationPropToNewPropMap.containsKey(hubzuInfo.getSelrPropIdVcNn()))
								  .forEach(hubzuInfo -> hubzuInfo.setSelrPropIdVcNn(migrationPropToNewPropMap.get(hubzuInfo.getSelrPropIdVcNn())));

						// DP-550 - Fetch loan issue with LOAN NUMBER as null - remove
						hubzuDBResponse.setHubzuInfos(hubzuDBResponse.getHubzuInfos().stream().filter(hubzuInfo -> {
							if (migrationPropToLoanMap.containsKey(hubzuInfo.getSelrPropIdVcNn())) {
								return true;
							} else {
								log.error("*** No record present for property Id : {} in RR db.", hubzuInfo.getSelrPropIdVcNn());
								return false;
							}
						})
								  .collect(Collectors.toList()));

						// Properties fetched from Hubzu DB  Validate against Week 0 DB
						List<List<HubzuInfo>> splitListHubzuInfos = ListUtils.partition(hubzuDBResponse.getHubzuInfos(), listSplitCount);
						List<DPProcessParam> consolidatedListOfweek0Params = new ArrayList<>();

						startTime = DateTime.now().getMillis();
						splitListHubzuInfos.stream().forEach(listOfBatch -> {
							Set<String> propsFromHbz = listOfBatch.stream().map(HubzuInfo::getSelrPropIdVcNn).collect(Collectors.toSet());
							Set<String> assetFromHbz = propsFromHbz.stream().map(prop -> migrationPropToLoanMap.get(prop)).collect(Collectors.toSet());
							List<DPProcessParam> week0DBList = dpProcessParamsDao.findLatestNonDuplicateInWeek0ForGivenAsset(assetFromHbz);
							if (!week0DBList.isEmpty()) {
								consolidatedListOfweek0Params.addAll(week0DBList);
							}
						});
						log.info("Time taken for retrospectUpdateMigrationInformation and  findLatestNonDuplicateInWeek0ForGivenAsset : " + (
								  DateTime.now().getMillis() - startTime) + "ms");

						Map<String, DPProcessParam> assetValueMap = consolidatedListOfweek0Params.stream()
								  .collect(Collectors.toMap(DPProcessParam::getPropTemp, Function.identity(), (r, s) -> r));

						List<HubzuInfo> hbzList = hubzuDBResponse.getHubzuInfos().stream().filter(item -> {
							if (assetValueMap.containsKey(item.getSelrPropIdVcNn()) && assetValueMap.get(item.getSelrPropIdVcNn()).getAssignmentDate() != null
									  && item.getListStrtDateDtNn() != null) {
								DateTime assignmentDate = DateConversionUtil.getEstDate(assetValueMap.get(item.getSelrPropIdVcNn()).getAssignmentDate());
								DateTime listStartDate = new DateTime(item.getListStrtDateDtNn());

								if ((listStartDate.getYear() > assignmentDate.getYear()) || ((listStartDate.getYear() == assignmentDate.getYear()) && (
										  listStartDate.getDayOfYear() >= assignmentDate.getDayOfYear())))
									return true;
								else
									return false;
							}
							return true;
						}).collect(Collectors.toList());

						log.info("Grouping Hubzu Initial response based on getSelrPropIdVcNn");
						List<List<HubzuInfo>> firstRecordHubzuInfoGroupedList = hbzList.stream()
								  .sorted(Comparator.comparing(HubzuInfo::getListStrtDateDtNn).reversed())
								  .collect(Collectors.groupingBy(hbzData -> hbzData.getSelrPropIdVcNn())).values().stream().collect(Collectors.toList());

						firstRecordHubzuInfoGroupedList.stream().forEach(listOfAllRecords -> {
							Boolean hasPriceChanged = Boolean.FALSE;
							int lastListCycle = listOfAllRecords.size();
							if (lastListCycle % 2 == 0) {
								for (int index = 0; index < listOfAllRecords.size() - 1; index++) {
									int finalIndex = index;
									if (!listOfSkippedAssetId.contains(listOfAllRecords.get(index).getSelrPropIdVcNn()) && !finalHubzuList.stream()
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
								if (!hasPriceChanged) {
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
						for (HubzuInfo hubzu : finalHubzuList) {
							DPProcessWeekNParamInfo dpProcessWeekNParamInfo = new DPProcessWeekNParamInfo();
							DPWeekNProcessStatusInfo dpWeekNProcessStatusInfo = new DPWeekNProcessStatusInfo();
							dpProcessWeekNParamInfo.setPropTemp(hubzu.getSelrPropIdVcNn());
							dpProcessWeekNParamInfo.setAssetNumber(migrationPropToLoanMap.get(hubzu.getSelrPropIdVcNn()));
							dpProcessWeekNParamInfo.setOldAssetNumber(migrationPropToLoanMap.get(migrationNewPropToPropMap.get(hubzu.getSelrPropIdVcNn())));
							dpProcessWeekNParamInfo.setZipCode(hubzu.getPropZipVcFk());
							dpProcessWeekNParamInfo.setState(hubzu.getPropStatIdVcFk());
							dpProcessWeekNParamInfo.setMostRecentPropertyStatus(hubzu.getPropSttsIdVcFk());
							dpProcessWeekNParamInfo.setRbidPropIdVcPk(hubzu.getRbidPropIdVcFk());
							dpProcessWeekNParamInfo.setAutoRLSTVc(hubzu.getAutoRLSTVc());
							dpProcessWeekNParamInfo.setListPrceNt(hubzu.getListPrceNt() != null ? hubzu.getListPrceNt().toString() : null);
							dpProcessWeekNParamInfo.setListStrtDateDtNn(hubzu.getListStrtDateDtNn() != null ? hubzu.getListStrtDateDtNn().toString() : null);
							dpProcessWeekNParamInfo.setListEndDateDtNn(hubzu.getListEndDateDtNn() != null ? hubzu.getListEndDateDtNn().toString() : null);
							dpProcessWeekNParamInfo.setListSttsDtlsVc(hubzu.getListSttsDtlsVc());
							dpProcessWeekNParamInfo.setDateOfLastReduction(hubzu.getDateOfLastReduction());
							dpProcessWeekNParamInfo.setLastListCycle(hubzu.getLastListCycle());
							dpProcessWeekNParamInfo
									  .setMostRecentListEndDate(hubzu.getListEndDateDtNn() != null ? hubzu.getListEndDateDtNn().toString() : null);
							dpProcessWeekNParamInfo.setMostRecentListPrice(
									  hubzu.getCurrentListPrceNt() != null ? BigDecimal.valueOf(Long.valueOf(hubzu.getCurrentListPrceNt())) : null);
							dpProcessWeekNParamInfo.setMostRecentListStatus(hubzu.getListSttsDtlsVc());
							dpWeekNProcessStatusInfo.setFetchedDateStr(fetchDateStr);
							dpProcessWeekNParamInfo.setDpWeekNProcessStatus(dpWeekNProcessStatusInfo);
							columnEntries.add(dpProcessWeekNParamInfo);
						}
						DPWeekNProcessStatusInfo dpWeekNProcessStatusInfo = new DPWeekNProcessStatusInfo();
						dpWeekNProcessStatusInfo.setFetchedDateStr(fetchDateStr);
						dpProcessParamEntryInfo.setDpWeeknProcessStatus(dpWeekNProcessStatusInfo);
						dpProcessParamEntryInfo.setColumnEntries(columnEntries);

						// Add all retrieved records from hubzu DB to Audit table where Fetched Date is same as current date
						// commented auditing as per story DP-603 --> start
//				if (DateConversionUtil.getCurrentEstDate().withTimeAtStartOfDay()
//						  .compareTo(DateConversionUtil.getEstDate(selectedDateMillis).withTimeAtStartOfDay()) == 0) {
//					log.error("Creating WeekN Audit entries in audit.");
//					dpAuditReportDelegate
//							  .createWeekNAuditEntries(skippedOddListings, selectedDateMillis, migrationNewPropToPropMap, migrationPropToLoanMap);
//				}
						// commented auditing as per story DP-603 --> end
						/*dpProcessParamEntryInfo.setSkippedTwelveDaysEntries(skippedOddListings);*/
						log.debug("Odd Listing: " + skippedOddListings);
						log.info("Time taken for creating week n param info : " + (DateTime.now().getMillis() - startTime) + "ms");
					}

				} else {
					log.error("Fetch All result: Records are not available.");
					dpProcessParamEntryInfo.setColumnEntries(null);
				}
			}
		} else {
			log.error("Top Records result : Records are not available from {} to {}.",  dPProcessParamInfo.getListEndDateDtNnstart(), dPProcessParamInfo.getListEndDateDtNnend());
			dpProcessParamEntryInfo.setColumnEntries(null);
		}
		return dpProcessParamEntryInfo;
	}

	@Override
	public void downloadWeekNDataFromHubzu(DPProcessWeekNParamEntryInfo dpProcessParamEntryInfo, Long userSelectedDate, HttpServletResponse response)
			  throws SystemException, ParseException {
		weekNBO.createWeekNExcel(dpProcessParamEntryInfo, userSelectedDate, response);
	}

	@Override
	public List<DPProcessWeekNParamInfo> getDPProcessWeekNParams(MultipartFile file) throws SystemException, IOException {
		if (file.isEmpty()) {
			throw new SystemException(CoreExceptionCodes.DPWKN0111, new Object[]{});
		}
		List<DPProcessWeekNParamInfo> dpProcessWeekNParamInfos = new ArrayList<>();
		log.info("Validating file name of uploaded file");
		InputFileValidationUtil.validateXLSFileName(FilenameUtils.getExtension(file.getOriginalFilename()));

		// Fetch Potential sheet from the workbook
		try {
			log.info("Fetching Potential sheet from the uploaded file");
			dpProcessWeekNParamInfos = weekNBO.getSheetsFromWeekNUploadedExcel(file);
		} catch (IOException ioe) {
			log.error(ioe.getLocalizedMessage(), ioe);
			throw ioe;
		}
		return dpProcessWeekNParamInfos;
	}

	@Override
	public List<DPProcessWeekNParamInfo> uploadWeekNExcel(String originalFilename, List<DPProcessWeekNParamInfo> listOfDPProcessWeekNParamInfos)
			  throws SystemException {
		List<DPProcessWeekNParamInfo> dpProcessWeekNParamInfos = new ArrayList<>();
		if (listOfDPProcessWeekNParamInfos.isEmpty()) {
			throw new SystemException(CoreExceptionCodes.DPWKN0109, new Object[]{});
		}
		DPWeekNProcessStatus dpWeekNProcessStatus = new DPWeekNProcessStatus();
		dpWeekNProcessStatus.setStatus(DPFileProcessStatus.UPLOADED.getFileStatus());
		dpWeekNProcessStatus.setInputFileName(originalFilename);
		dpWeekNProcessStatus.setSysGnrtdInputFileName(dPProcessDelegate.generateFileName(originalFilename));
		dpWeekNProcessStatus.setProcess(DPFileProcesses.VACANT_WEEKN.getProcess());

		String rrMigrationLoanNumQuery = (String) cacheManager.getAppParamValue(AppParameterConstant.RR_MIGRATION_LOAN_NUM_QUERY);
		List<String> weekNAssets = listOfDPProcessWeekNParamInfos.stream().map(DPProcessWeekNParamInfo::getAssetNumber).collect(Collectors.toList());
		List<String> weekNPropTemps = rRMigration.getPropTemps(rrMigrationLoanNumQuery, weekNAssets);
		final Map<String, String> migrationNewPropToPropMap = new HashMap<String, String>();
		final Map<String, String> migrationPropToLoanMap = new HashMap<String, String>();
		rRMigration.getMigrationMaps(migrationNewPropToPropMap, migrationPropToLoanMap, weekNPropTemps);
		final Map<String, String> migrationPropToNewPropMap = migrationNewPropToPropMap.entrySet().stream()
				  .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
		final Set<String> oldPropSet = migrationPropToNewPropMap.keySet();
		final Map<String, String> migrationLoanToPropMap = migrationPropToLoanMap.entrySet().stream()
				  .filter(entry -> !oldPropSet.contains(entry.getKey())).collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
		listOfDPProcessWeekNParamInfos.stream()
				  .filter(paramInfo -> migrationPropToNewPropMap.containsKey(migrationLoanToPropMap.get(paramInfo.getAssetNumber())))
				  .forEach(paramInfo -> {
					  paramInfo.setOldAssetNumber(paramInfo.getAssetNumber());
					  paramInfo.setAssetNumber(
								 migrationPropToLoanMap.get(migrationPropToNewPropMap.get(migrationLoanToPropMap.get(paramInfo.getAssetNumber()))));
				  });

		listOfDPProcessWeekNParamInfos.stream().forEach(item -> {
			DPProcessWeekNParam dpProcessWeekNParam = new DPProcessWeekNParam();
			dpProcessWeekNParam.setAssetNumber(item.getAssetNumber());
			dpProcessWeekNParam.setOldAssetNumber(item.getOldAssetNumber());
			dpProcessWeekNParam.setPropTemp(migrationLoanToPropMap.get(item.getAssetNumber()));
			dpProcessWeekNParam.setListEndDateDtNn(item.getListEndDateDtNn());
			dpProcessWeekNParam.setMostRecentListEndDate(item.getMostRecentListEndDate());
			dpProcessWeekNParam.setListSttsDtlsVc(item.getListSttsDtlsVc());
			dpProcessWeekNParam.setDateOfLastReduction(item.getDateOfLastReduction());
			dpProcessWeekNParam.setDpWeekNProcessStatus(dpWeekNProcessStatus);
			dpProcessWeekNParam.setMostRecentListStatus(item.getMostRecentListStatus());
			DPProcessWeekNParamInfo dpProcessWeekNParamInfo = dpWeekNToInfoMapper.dpWeekNToInfoMapper(dpProcessWeekNParam);
			dpProcessWeekNParamInfo.setClassification(item.getClassification());
			dpProcessWeekNParamInfo.setSellerOccupiedProperty(item.getSellerOccupiedProperty());
			dpProcessWeekNParamInfos.add(dpProcessWeekNParamInfo);
		});
		return dpProcessWeekNParamInfos;
	}

	@Override
	public void downloadWeekNReports(String id, String type, HttpServletResponse response, DPProcessWeekNParamEntryInfo dpProcessWeekNParamEntryInfo) throws SystemException, IOException {
		Map<String, List<DPProcessWeekNParam>> mapOfOCNandNRZListings = new HashMap<>();
		List<DPProcessWeekNParam> listOfDPProcessWeekNParamOCN = new ArrayList<>();
		List<DPProcessWeekNParam> listOfDPProcessWeekNParamNRZ = new ArrayList<>();
		List<DPProcessWeekNParam> listOfDPProcessWeekNParamPHH = new ArrayList<>();
		List<DPProcessWeekNParam> listOfAllWeekNParamNRZ = new ArrayList<>();
		List<DPProcessWeekNParam> listOfAllWeekNParamOCN = new ArrayList<>();
		List<DPProcessWeekNParam> listOfAllWeekNParamPHH = new ArrayList<>();

		List<DPProcessWeekNParam> listOfDPProcessWeekNParam = weekNBO.fetchWeekNFilesDetailsById(id);

		Optional<DPProcessWeekNParam> optionalObj = listOfDPProcessWeekNParam.stream().filter(s -> s != null).findFirst();
		DPProcessWeekNParam weekNParam = optionalObj.orElse(null);
		String zipFileName = "WeekN_" + DateConversionUtil.getEstDate(weekNParam.getDpWeekNProcessStatus().getLastModifiedDate())
				  .toString(DateConversionUtil.ZIP_DATE_TIME_FORMAT);
		String sysGnrtdInputFileName = dpWeekNProcessStatusRepo.findSysGnrtdInputFileNameById(id);
		if (type == null || !weekNBO.findWeekNReports(sysGnrtdInputFileName, zipFileName, response)) {

			// Preparing List of PHH, NRZ and OCN for Recommendation  sheet
			listOfDPProcessWeekNParam.forEach(weeknEntry -> {
				if (StringUtils.equals(weeknEntry.getClassification(), DPAConstants.OCN)) {
					listOfDPProcessWeekNParamOCN.add(weeknEntry);
				}
				if (StringUtils.equals(weeknEntry.getClassification(), DPAConstants.NRZ)) {
					listOfDPProcessWeekNParamNRZ.add(weeknEntry);
				}
				if (StringUtils.equals(weeknEntry.getClassification(), DPAConstants.PHH)) {
					listOfDPProcessWeekNParamPHH.add(weeknEntry);
				}
			});

			// Preparing List of PHH, NRZ and OCN for Successful under review sheet
			List<DPProcessWeekNParam> listOfAllWeekNParams = weekNBO.findAllWeekNParams();

			listOfAllWeekNParams.forEach(allWeekNParams -> {
				if (StringUtils.equals(allWeekNParams.getClassification(), DPAConstants.OCN)) {
					listOfAllWeekNParamOCN.add(allWeekNParams);
				}
				if (StringUtils.equals(allWeekNParams.getClassification(), DPAConstants.NRZ)) {
					listOfAllWeekNParamNRZ.add(allWeekNParams);
				}
				if (StringUtils.equals(allWeekNParams.getClassification(), DPAConstants.PHH)) {
					listOfAllWeekNParamPHH.add(allWeekNParams);
				}
			});

			mapOfOCNandNRZListings.put(DPAConstants.LIST_WKN_OCN, listOfDPProcessWeekNParamOCN);
			mapOfOCNandNRZListings.put(DPAConstants.LIST_WKN_NRZ, listOfDPProcessWeekNParamNRZ);
			mapOfOCNandNRZListings.put(DPAConstants.LIST_WKN_PHH, listOfDPProcessWeekNParamPHH);
			mapOfOCNandNRZListings.put(DPAConstants.LIST_SCCS_UDR_OCN, listOfAllWeekNParamOCN);
			mapOfOCNandNRZListings.put(DPAConstants.LIST_SCCS_UDR_NRZ, listOfAllWeekNParamNRZ);
			mapOfOCNandNRZListings.put(DPAConstants.LIST_SCCS_UDR_PHH, listOfAllWeekNParamPHH);

			weekNBO.generateWeekNOutputFile(mapOfOCNandNRZListings, response, zipFileName, sysGnrtdInputFileName, type);
		}
		if (dpProcessWeekNParamEntryInfo != null) {
			DPWeekNProcessStatus dpweeknProcess = dpFileProcessBO.findDPWeekNProcessById(dpProcessWeekNParamEntryInfo.getDpWeeknProcessStatus().getId());
			if (dpweeknProcess != null
					  && dpweeknProcess.getStatus().equals(DPFileProcessStatus.IN_PROGRESS.getFileStatus())) {
				dpweeknProcess.setStatus(dpFileProcessDelegate.setWeeknStatus(dpProcessWeekNParamEntryInfo));
				dpFileProcessBO.saveDPProcessWeekNStatus(dpweeknProcess);
			}
		}
	}

}