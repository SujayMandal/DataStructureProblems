package com.fa.dp.business.validator.bo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.NoSuchMessageException;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

import com.fa.dp.business.command.entity.Command;
import com.fa.dp.business.command.info.CommandInfo;
import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.constant.DPProcessParamAttributes;
import com.fa.dp.business.util.DPFileProcessStatus;
import com.fa.dp.business.validation.file.header.constant.DPProcessFileHeader;
import com.fa.dp.business.validation.file.header.constant.DPWeekNProcessFileHeader;
import com.fa.dp.business.validation.input.info.DPFileProcessStatusInfo;
import com.fa.dp.business.validation.input.info.DPProcessParamInfo;
import com.fa.dp.business.validator.dao.DPProcessParamsDao;
import com.fa.dp.business.validator.dao.DynamicPricingFilePrcsStatusDao;
import com.fa.dp.business.validator.dao.DynamicPricingIntgAuditDao;
import com.fa.dp.business.week0.dao.Week0FilterDao;
import com.fa.dp.business.week0.entity.DPProcessParam;
import com.fa.dp.business.week0.entity.DynamicPricingFilePrcsStatus;
import com.fa.dp.business.week0.entity.DynamicPricingIntgAudit;
import com.fa.dp.business.week0.info.DPWeek0ToInfoMapper;
import com.fa.dp.business.week0.info.DashboardFilterInfo;
import com.fa.dp.business.weekn.bo.DPWeekNBOUtil;
import com.fa.dp.business.weekn.dao.DPProcessWeekNParamsDao;
import com.fa.dp.business.weekn.dao.DPWeekNProcessStatusRepo;
import com.fa.dp.business.weekn.dao.WeekNFilterDao;
import com.fa.dp.business.weekn.entity.DPProcessWeekNParam;
import com.fa.dp.business.weekn.entity.DPWeekNProcessStatus;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamInfo;
import com.fa.dp.business.weekn.input.info.DPWeekNProcessStatusInfo;
import com.fa.dp.business.weekn.input.info.DPWeekNToInfoMapper;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.exception.codes.CoreExceptionCodes;
import com.fa.dp.core.util.ConversionUtil;
import com.fa.dp.core.util.DateConversionUtil;
import com.fa.dp.localization.MessageContainer;

@Slf4j
@Named
public class DPFileProcessBOImpl implements DPFileProcessBO {

	private static final Logger LOGGER = LoggerFactory.getLogger(DPFileProcessBOImpl.class);
	private static final String SHEET_WEEK0_DB = "Week0_DB";

	@Inject
	private DynamicPricingFilePrcsStatusDao dynamicPricingFilePrcsStatusDao;

	@Inject
	private DPProcessParamsDao dpProcessParamsDao;

	@Inject
	private DPProcessWeekNParamsDao dpProcessWeekNParamsDao;

	@Inject
	private DynamicPricingIntgAuditDao dynamicPricingIntgAuditDao;

	@Inject
	private DPWeek0ToInfoMapper dpSourceToInfoMapper;

	@Inject
	private DPWeekNToInfoMapper dpWeekNToInfoMapper;

	@Inject
	private DPWeekNProcessStatusRepo dpWeekNProcessStatusRepo;

	@Inject
	private Week0FilterDao week0FilterDao;

	@Inject
	private WeekNFilterDao weekNFilterDao;

	@Inject
	private DPWeekNBOUtil dpWeekNBOUtil;

	/*
	 * (non-Javadoc)
	 *
	 * @see com.fa.ra.client.dp.validator.bo.DPFileProcessBO#
	 * findDPProcessStatusById(java.lang.String)
	 */
	@Override
	public DynamicPricingFilePrcsStatus findDPProcessStatusById(String id) {
		Optional<DynamicPricingFilePrcsStatus> obj = dynamicPricingFilePrcsStatusDao.findById(id);
		return obj.get();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.fa.ra.client.dp.validator.bo.DPFileProcessBO#
	 * saveDPProcessStatus(com.fa.ra.client.dp.dao.entity.
	 * DynamicPricingFilePrcsStatus)
	 */
	@Override
	public DynamicPricingFilePrcsStatus saveDPProcessStatus(DynamicPricingFilePrcsStatus dynamicPricingFilePrcsStatus) {
		return dynamicPricingFilePrcsStatusDao.save(dynamicPricingFilePrcsStatus);
	}

	@Override
	public DPWeekNProcessStatus saveDPProcessWeekNStatus(DPWeekNProcessStatus dpWeeknProcessStatus) {
		return dpWeekNProcessStatusRepo.save(dpWeeknProcessStatus);
	}

	@Override
	public DPWeekNProcessStatus findDPWeekNProcessById(String id) {
		Optional<DPWeekNProcessStatus> obj = dpWeekNProcessStatusRepo.findById(id);
		return obj.get();
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
	public void updateWeeknPrcsStatus(String status, String id) {
		dpWeekNProcessStatusRepo.updateWeekNProcessStatus(status, id);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.fa.ra.client.dp.validator.bo.DPFileProcessBO#
	 * findDPProcessParamById(java.lang.String)
	 */
	@Override
	public DPProcessParam findDPProcessParamById(String id) {
		DPProcessParam obj = dpProcessParamsDao.getOne(id);
		return obj;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.fa.ra.client.dp.validator.bo.DPFileProcessBO#
	 * saveDPProcessParam(com.fa.ra.client.dp.dao.entity.DynamicPricingInputParam)
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
	public DPProcessParam saveDPProcessParam(DPProcessParam dynamicPricingInputParam) {
		dynamicPricingInputParam.setUpdateTimestamp(DateConversionUtil.getCurrentUTCTime().getMillis());
		return dpProcessParamsDao.save(dynamicPricingInputParam);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.fa.ra.client.dp.validator.bo.DPFileProcessBO#
	 * saveAllDPProcessParam(java.util.List)
	 */
	@Override
	public List<DPProcessParam> saveAllDPProcessParam(List<DPProcessParam> dynamicPricingInputParams) {
		return dpProcessParamsDao.saveAll(dynamicPricingInputParams);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.fa.ra.client.dp.validator.bo.DPFileProcessBO#saveDPProcessIntgAudit(com.
	 * fa.ra.client.dp.dao.entity.DynamicPricingIntgAudit)
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
	public DynamicPricingIntgAudit saveDPProcessIntgAudit(DynamicPricingIntgAudit dpIntgAudit) {
		return dynamicPricingIntgAuditDao.save(dpIntgAudit);
	}

	@Override
	public int countModeled(String state, int lowerAssetValue, int higherAssetValue, String classification) {
		return dpProcessParamsDao
				.countAssignmentByStateAndAssetNumber(state, BigDecimal.valueOf(lowerAssetValue), BigDecimal.valueOf(higherAssetValue),
						DPProcessParamAttributes.MODELED_ASSIGNMENT.getValue(), classification, "WEEK0DB_INITIAL").size();
	}

	@Override
	public int countBenchmark(String state, int lowerAssetValue, int higherAssetValue, String classification) {
		return dpProcessParamsDao
				.countAssignmentByStateAndAssetNumber(state, BigDecimal.valueOf(lowerAssetValue), BigDecimal.valueOf(higherAssetValue),
						DPProcessParamAttributes.BENCHMARK_ASSIGNMENT.getValue(), classification, "WEEK0DB_INITIAL").size();
	}

	@Override
	public DynamicPricingFilePrcsStatus findDPProcessStatusByStatus(String status) {
		DynamicPricingFilePrcsStatus dpFilePrcsStatus = dynamicPricingFilePrcsStatusDao.findByStatus(status);
		return dpFilePrcsStatus;
	}

	@Override
	public DPWeekNProcessStatus findWeeknPrcsStatusByStatus(String status) {
		return dpWeekNProcessStatusRepo.findByStatus(status);
	}

	@Override
	public List<DPProcessParam> findDPProcessParamByProcessID(String id) {
		List<DPProcessParam> result = dpProcessParamsDao.findDPProcessParamByStatusID(id);
		return result;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
	public void updateDPProcessParamEligibleByID(String id, String eligibility) {
		dpProcessParamsDao.updateDPProcessParamEligibleByID(id, eligibility);
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
	public void saveDPProcessNotes(String id, String notes) {
		dpProcessParamsDao.updateDPProcessNotesByID(id, notes);
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
	public void saveDPProcessErrorDetail(String id, String error) {
		dpProcessParamsDao.updateDPProcessErrorDetailByID(id, error);
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
	public String saveDPProcessErrorDetail(String id, String type, String errorDetail, Exception ex) {

		LOGGER.info("saveDPProcessErrorDetail() started.");

		String genericErrorCode = null;
		String errorCode = null;
		String errorMessage = null;
		if (null != ex) {
			if (ex instanceof ExecutionException) {
				LOGGER.info("instanceof ExecutionException");
				ex = (Exception) ex.getCause();
			}

			if (ex instanceof DataAccessException) {
				LOGGER.info("instanceof DataAccessException");
				for (int i = 1; i < 5; i++) {
					if (null == errorCode && null != ex.getCause() && ex.getCause() instanceof SQLException) {
						ex = (SQLException) ex.getCause();
						errorCode = ((SQLException) ex).getSQLState();
					} else {
						break;
					}
				}
				if (null == errorCode) {
					errorMessage = ex.getMessage();
				}
				LOGGER.info("errorCode : " + errorCode);
			} else if (ex instanceof SystemException) {
				if (ex.getCause() instanceof HttpClientErrorException) {
					LOGGER.info("instanceof HttpClientErrorException");
					errorCode = "HTTP_" + String.valueOf(((HttpClientErrorException) ex.getCause()).getStatusCode().value());
				} else if (ex.getCause() instanceof ResourceAccessException) {
					LOGGER.info("instanceof ResourceAccessException");
					errorCode = "ERR001";
				} else if (ex.getCause() instanceof HttpStatusCodeException) {
					LOGGER.info("instanceof HttpStatusCodeException");
					errorCode = "HTTP_" + String.valueOf(((HttpStatusCodeException) ex.getCause()).getStatusCode().value());
				} else {
					LOGGER.info("instanceof SystemException");
					errorCode = ((SystemException) ex).getCode();
				}
				LOGGER.info("errorCode : " + errorCode);
			}

			if (null != errorCode) {
				try {
					genericErrorCode = MessageContainer.getMessage(errorCode, new Object[] {});
					errorMessage = MessageContainer.getMessage(genericErrorCode, new Object[] {});
				} catch (NoSuchMessageException e) {
					LOGGER.info(e.getLocalizedMessage(), e);
					errorMessage = ex.getLocalizedMessage();
				}
			} else {
				errorMessage = ex.getMessage();
			}
		} else {
			errorMessage = "No record found";
		}

		LOGGER.info("errorMessage : " + errorMessage);
		LOGGER.info("genericErrorCode : " + genericErrorCode);

		Map<String, String> errorMap = new HashMap<>();

		if (!StringUtils.isBlank(errorDetail)) {
			try {
				errorMap = ConversionUtil.convertJson(errorDetail, Map.class);
			} catch (SystemException e) {
				LOGGER.info(e.getLocalizedMessage(), e);
			}
		}

		// check for length of message
		if (StringUtils.isNotBlank(errorMessage) && errorMessage.length() >= 500) {
			errorMessage = errorMessage.substring(0, 500);
		}

		errorMap.put(type, errorMessage);

		try {
			errorDetail = ConversionUtil.convertToJsonString(errorMap);
		} catch (SystemException e) {
			LOGGER.info(e.getLocalizedMessage(), e);
		}

		LOGGER.info("errorDetail : " + errorDetail);

		dpProcessParamsDao.updateDPProcessErrorDetailByID(id, errorDetail);

		LOGGER.info("saveDPProcessErrorDetail() ended.");

		return errorDetail;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
	public void saveDPProcessRTSource(String id, String rtSource) {
		dpProcessParamsDao.updateDPProcessRTSourceByID(id, rtSource);
	}

	/**
	 * @return List<DPFileProcessStatusInfo>
	 * @throws SystemException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<DPProcessParamInfo> getWeekZeroAllUploadedFiles() throws SystemException {
		List<DPProcessParamInfo> listOfDPProcessParamInfo = new ArrayList<>();

		List allWeek0Params = dpProcessParamsDao.findAllDashboardParams();

		if (CollectionUtils.isEmpty(allWeek0Params)) {
			log.error("No records found for Week 0!");
			throw new SystemException(CoreExceptionCodes.DPWK00103, new Object[] {});
		} else {

			allWeek0Params.stream().forEach(obj -> {

				Object[] object = (Object[]) obj;

				DPProcessParamInfo dpProcessParamInfo = new DPProcessParamInfo();
				dpProcessParamInfo.setClassification((String) object[0]);

				DPFileProcessStatusInfo dynamicPricingFilePrcsStatus = new DPFileProcessStatusInfo();
				dynamicPricingFilePrcsStatus.setInputFileName((String) object[1]);
				dynamicPricingFilePrcsStatus.setId((String) object[3]);
				dynamicPricingFilePrcsStatus.setStatus((String) object[2]);
				dynamicPricingFilePrcsStatus.setUploadTimestamp((Long) object[4]);
				dynamicPricingFilePrcsStatus.setUploadTimestampStr(DateConversionUtil.getEstDate(dynamicPricingFilePrcsStatus.getUploadTimestamp())
						.toString(DateConversionUtil.DATE_TIME_FORMATTER));
				dpProcessParamInfo.setDynamicPricingFilePrcsStatus(dynamicPricingFilePrcsStatus);

				CommandInfo commandInfo = new CommandInfo();
				commandInfo.setName((String) object[5]);
				dpProcessParamInfo.setCommand(commandInfo);

				listOfDPProcessParamInfo.add(dpProcessParamInfo);
			});

		}
		return listOfDPProcessParamInfo;
	}

	@Override
	public List<DPProcessParamInfo> getWeekZeroFilteredFiles(final DashboardFilterInfo dashboardFilterInfo) throws SystemException {
		List<DPProcessParamInfo> listOfDPProcessParamInfo = new ArrayList<>();
		List<DPProcessParam> listOfDPProcessParams = week0FilterDao
				.getWeek0FilteredRecords(dashboardFilterInfo.getFileName(), dashboardFilterInfo.getStatus(), dashboardFilterInfo.getFromDate(),
						dashboardFilterInfo.getToDate());
		if (listOfDPProcessParams.isEmpty()) {
			log.info("No records found for Week 0!");
			throw new SystemException(CoreExceptionCodes.DPWK00103, new Object[] {});
		}
		listOfDPProcessParams.stream().forEach(list -> {
			DPProcessParamInfo dpProcessParamInfo = dpSourceToInfoMapper.dpWeek0ToInfoMapper(list);
			listOfDPProcessParamInfo.add(dpProcessParamInfo);
		});
		return listOfDPProcessParamInfo;
	}

	@Override
	public List<DPDashboardParamInfo> getDashboardParams(List<DPDashboardParamInfo> listOfDPDashboardParamInfo, Map map) throws SystemException {
		Map<String, DPDashboardParamInfo> mapOfFileAndStep = new HashMap<>();
		log.info("Preparing data for dashboard");
		for (DPDashboardParamInfo dashboardParamInfo : listOfDPDashboardParamInfo) {
			if (StringUtils.equalsIgnoreCase(dashboardParamInfo.getStatus(), DPFileProcessStatus.UPLOADED.getFileStatus())) {
				mapOfFileAndStep.put(dashboardParamInfo.getId(), dashboardParamInfo);
			}
			if (StringUtils.equalsIgnoreCase(dashboardParamInfo.getStatus(), DPFileProcessStatus.IN_PROGRESS.getFileStatus())) {
				mapOfFileAndStep.put(dashboardParamInfo.getId(), dashboardParamInfo);
			}
			if (!StringUtils.equalsIgnoreCase(dashboardParamInfo.getStatus(), DPFileProcessStatus.UPLOADED.getFileStatus()) && !StringUtils
					.equalsIgnoreCase(dashboardParamInfo.getStatus(), DPFileProcessStatus.IN_PROGRESS.getFileStatus())) {
				String commandName = (dashboardParamInfo.getName() != null) ? dashboardParamInfo.getName() : DPAConstants.PROCESSED_FILTER;
				if (mapOfFileAndStep.containsKey(dashboardParamInfo.getId())) {
					dashboardParamInfo = mapOfFileAndStep.get(dashboardParamInfo.getId());
					dashboardParamInfo = getStepObject(commandName, dashboardParamInfo);
					mapOfFileAndStep.put(dashboardParamInfo.getId(), dashboardParamInfo);
				} else {
					dashboardParamInfo = getStepObject(commandName, dashboardParamInfo);
					mapOfFileAndStep.put(dashboardParamInfo.getId(), dashboardParamInfo);
				}
			}
		}
		List<DPDashboardParamInfo> listOfDpDashboardParams = mapOfFileAndStep.values().stream().collect(Collectors.toList());
		if (listOfDpDashboardParams.isEmpty()) {
			log.error("Unable to create dashboard information");
			throw new SystemException(CoreExceptionCodes.DPDASHBOARD01);
		}
		//listOfDpDashboardParams.forEach(item -> item.setUploadTimeStampInMillis(DateConversionUtil.EST_DATE_TIME_FORMATTER.parseDateTime(item.getUploadTimestamp())));
		if (!map.isEmpty()) {
			listOfDpDashboardParams.stream().forEach(dpDashboard -> {
				Long totalAsset = 0L;
				if (map.containsKey(dpDashboard.getId())) {
					totalAsset = (Long) map.get(dpDashboard.getId());
					dpDashboard.setTotalAssets(totalAsset);
					map.remove(dpDashboard.getId());
				}
			});
		} else {
			throw new SystemException("Map for total count is empty.");
		}
		return listOfDpDashboardParams;
	}

	private DPDashboardParamInfo getStepObject(String fileStep, DPDashboardParamInfo dpDashboardParamInfo) {
		if (fileStep.equalsIgnoreCase(DPAConstants.DUPLICATE_FILTER)) {
			int duplicateCount = dpDashboardParamInfo.getDuplicateCount();
			duplicateCount++;
			dpDashboardParamInfo.setDuplicateCount(duplicateCount);
		}
		else if (fileStep.equalsIgnoreCase(DPAConstants.CLASSIFICATION_FILTER)) {
			int rrClassificationCount = dpDashboardParamInfo.getClassificationMismatchCount();
			rrClassificationCount++;
			dpDashboardParamInfo.setClassificationMismatchCount(rrClassificationCount);
		}
		else if (fileStep.equalsIgnoreCase(DPAConstants.INVESTOR_FILTER)) {
			int ssInvestorCount = dpDashboardParamInfo.getSsInvestorCount();
			ssInvestorCount++;
			dpDashboardParamInfo.setSsInvestorCount(ssInvestorCount);
		}
		else if (fileStep.equalsIgnoreCase(DPAConstants.UNSUPPORTEDPROP_FILTER)) {
			int unsupportedPropertyCount = dpDashboardParamInfo.getUnsupportedPropertyCount();
			unsupportedPropertyCount++;
			dpDashboardParamInfo.setUnsupportedPropertyCount(unsupportedPropertyCount);
		}
		else if (fileStep.equalsIgnoreCase(DPAConstants.UNSUPPORTEDASSET_FILTER)) {
			int unsupportedAssetCount = dpDashboardParamInfo.getUnsupportedAssetCount();
			unsupportedAssetCount++;
			dpDashboardParamInfo.setUnsupportedAssetCount(unsupportedAssetCount);
		}
		else if (fileStep.equalsIgnoreCase(DPAConstants.FAILED_REAL_RESOL_REAL_FILTER)) {
			int failedRealResolOrRealCount = dpDashboardParamInfo.getFailedRealResolOrRealCount();
			failedRealResolOrRealCount++;
			dpDashboardParamInfo.setFailedRealResolOrRealCount(failedRealResolOrRealCount);
		}
		else if (fileStep.equalsIgnoreCase(DPAConstants.PROCESSED_FILTER)) {
			int processedList = dpDashboardParamInfo.getProcessedListCount();
			processedList++;
			dpDashboardParamInfo.setProcessedListCount(processedList);
		}
		else if (fileStep.equalsIgnoreCase(DPAConstants.RA_FILTER)) {
			int raFailedCount = dpDashboardParamInfo.getRaFailedCount();
			raFailedCount++;
			dpDashboardParamInfo.setRaFailedCount(raFailedCount);
		}
		else if (fileStep.equalsIgnoreCase(DPAConstants.DATA_FETCH_FAILURE)) {
			int dataFetchFailCount = dpDashboardParamInfo.getDataFetchFailCount();
			dataFetchFailCount++;
			dpDashboardParamInfo.setDataFetchFailCount(dataFetchFailCount);
		}
		else if (fileStep.equalsIgnoreCase(DPAConstants.UNSUPPORTED_STATE_OR_ZIP)) {
			int unsupportedStateOrZipCount = dpDashboardParamInfo.getUnsupportedStateOrZipCount();
			unsupportedStateOrZipCount++;
			dpDashboardParamInfo.setUnsupportedStateOrZipCount(unsupportedStateOrZipCount);
		}
		else if (fileStep.equalsIgnoreCase(DPAConstants.SS_AND_PMI)) {
			int ssAndPmiCount = dpDashboardParamInfo.getSsAndPmiCount();
			ssAndPmiCount++;
			dpDashboardParamInfo.setSsAndPmiCount(ssAndPmiCount);
		}
		else if (fileStep.equalsIgnoreCase(DPAConstants.SOP)) {
			int sopCount = dpDashboardParamInfo.getSopCount();
			sopCount++;
			dpDashboardParamInfo.setSopCount(sopCount);
		}
		else if (fileStep.equalsIgnoreCase(DPAConstants.RA_FAIL_FILTER)) {
			int weekNRaFailCount = dpDashboardParamInfo.getWeekNRAFailedCount();
			weekNRaFailCount++;
			dpDashboardParamInfo.setWeekNRAFailedCount(weekNRaFailCount);
		}
		else if (fileStep.equalsIgnoreCase(DPAConstants.ASSIGNMNT_FILTER)) {
			int weekNAssignmentCount = dpDashboardParamInfo.getWeekNAssignmentCount();
			weekNAssignmentCount++;
			dpDashboardParamInfo.setWeekNAssignmentCount(weekNAssignmentCount);
		}
		else if (fileStep.equalsIgnoreCase(DPAConstants.ODD_PROPERTIES_FILTER)) {
			int weekNOddPropertiesCount = dpDashboardParamInfo.getOddListingsCount();
			weekNOddPropertiesCount++;
			dpDashboardParamInfo.setOddListingsCount(weekNOddPropertiesCount);
		}
		else if (fileStep.equalsIgnoreCase(DPAConstants.PAST12_CYCLES_FILTER)) {
			int weekNPast12CyclesCount = dpDashboardParamInfo.getPast12CyclesCount();
			weekNPast12CyclesCount++;
			dpDashboardParamInfo.setPast12CyclesCount(weekNPast12CyclesCount);
		}
		else if (fileStep.equalsIgnoreCase(DPAConstants.ACTIVE_LISTINGS_FILTER)) {
			int activeListingCount = dpDashboardParamInfo.getActiveListingsCount();
			activeListingCount++;
			dpDashboardParamInfo.setActiveListingsCount(activeListingCount);
		}
		else if (fileStep.equalsIgnoreCase(DPAConstants.SUCCESSFUL_UNDERREVIEW_FILTER)) {
			int successUnderreviewCount = dpDashboardParamInfo.getSuccessUnderreviewCount();
			successUnderreviewCount++;
			dpDashboardParamInfo.setSuccessUnderreviewCount(successUnderreviewCount);
		}
		else if (StringUtils.containsIgnoreCase(fileStep, DPAConstants.SOP_DUPLICATE_FILTER)) {
			int sopWeek0DuplicateCount = dpDashboardParamInfo.getSopWeek0DuplicateAssetCount();
			sopWeek0DuplicateCount++;
			dpDashboardParamInfo.setSopWeek0DuplicateAssetCount(sopWeek0DuplicateCount);
		}
		else if (StringUtils.containsIgnoreCase(fileStep, DPAConstants.SOP_ASSETVALUE_FILTER)) {
			int sopWeek0UnsupportedAssetValueCount = dpDashboardParamInfo.getSopWeek0UnsupportedAssetValueCount();
			sopWeek0UnsupportedAssetValueCount++;
			dpDashboardParamInfo.setSopWeek0UnsupportedAssetValueCount(sopWeek0UnsupportedAssetValueCount);
		}
		else if (StringUtils.containsIgnoreCase(fileStep, DPAConstants.SOPWEEKN_DATA_FETCH_FAILURE)) {
			int sopWeekNDataFetchFailCount = dpDashboardParamInfo.getDataFetchFailCount();
			sopWeekNDataFetchFailCount++;
			dpDashboardParamInfo.setDataFetchFailCount(sopWeekNDataFetchFailCount);
		}
		else if (StringUtils.containsIgnoreCase(fileStep, DPAConstants.SOPWEEKN_UNSUPPORTED_STATE_OR_ZIP)) {
			int sopWeekNUnsupportedStateOrZipCount = dpDashboardParamInfo.getUnsupportedStateOrZipCount();
			sopWeekNUnsupportedStateOrZipCount++;
			dpDashboardParamInfo.setUnsupportedStateOrZipCount(sopWeekNUnsupportedStateOrZipCount);
		}
		else if (StringUtils.containsIgnoreCase(fileStep, DPAConstants.SOPWEEKN_SS_AND_PMI)) {
			int sopWeekNSsAndPmiCount = dpDashboardParamInfo.getSsAndPmiCount();
			sopWeekNSsAndPmiCount++;
			dpDashboardParamInfo.setSsAndPmiCount(sopWeekNSsAndPmiCount);
		}
		else if (StringUtils.containsIgnoreCase(fileStep, DPAConstants.SOPWEEKN_SOPFILTER)) {
			int sopWeekNSopCount = dpDashboardParamInfo.getSopCount();
			sopWeekNSopCount++;
			dpDashboardParamInfo.setSopCount(sopWeekNSopCount);
		}
		else if (StringUtils.containsIgnoreCase(fileStep, DPAConstants.SOPWEEKN_RA_FAIL_FILTER)) {
			int sopWeekNRAFailedCount = dpDashboardParamInfo.getRaFailedCount();
			sopWeekNRAFailedCount++;
			dpDashboardParamInfo.setRaFailedCount(sopWeekNRAFailedCount);
		}
		else if (StringUtils.containsIgnoreCase(fileStep, DPAConstants.SOPWEEKN_ASSIGNMNT_FILTER)) {
			int sopWeekNAssignmentCount = dpDashboardParamInfo.getWeekNAssignmentCount();
			sopWeekNAssignmentCount++;
			dpDashboardParamInfo.setWeekNAssignmentCount(sopWeekNAssignmentCount);
		}
		else if (StringUtils.containsIgnoreCase(fileStep, DPAConstants.SOPWEEKN_PAST12_CYCLES_FILTER)) {
			int sopWeekNPast12CyclesCount = dpDashboardParamInfo.getPast12CyclesCount();
			sopWeekNPast12CyclesCount++;
			dpDashboardParamInfo.setPast12CyclesCount(sopWeekNPast12CyclesCount);
		}
		else if (StringUtils.containsIgnoreCase(fileStep, DPAConstants.SOPWEEKN_ODD_LISTING_FILTER)) {
			int sopWeekNOddListingsCount = dpDashboardParamInfo.getOddListingsCount();
			sopWeekNOddListingsCount++;
			dpDashboardParamInfo.setOddListingsCount(sopWeekNOddListingsCount);
		}
		else if (StringUtils.containsIgnoreCase(fileStep, DPAConstants.SOPWEEKN_ACTIVE_LISTINGS_FILTER)) {
			int sopWeekNActiveListingsCount = dpDashboardParamInfo.getActiveListingsCount();
			sopWeekNActiveListingsCount++;
			dpDashboardParamInfo.setActiveListingsCount(sopWeekNActiveListingsCount);
		}
		else if (StringUtils.containsIgnoreCase(fileStep, DPAConstants.SOPWEEKN_SUCCESSFUL_UNDERREVIEW_FILTER)) {
			int sopWeekNSuccessUnderreviewCount = dpDashboardParamInfo.getSuccessUnderreviewCount();
			sopWeekNSuccessUnderreviewCount++;
			dpDashboardParamInfo.setSuccessUnderreviewCount(sopWeekNSuccessUnderreviewCount);
		}
		return dpDashboardParamInfo;
	}

	/**
	 * @param fileId
	 * @param type
	 * @return List<DPProcessParam>
	 * @throws SystemException
	 */
	@Override
	public List<DPProcessParamInfo> getAssetDetails(String fileId, String type) throws SystemException {
		List<DPProcessParam> listOfDpProcessParams = new ArrayList<>();
		List<DPProcessParamInfo> listOfDpProcessParamsInfo = new ArrayList<>();

		if (type.equalsIgnoreCase(DPAConstants.WEEK0)) {
			List<DPProcessParam> dpProcessParamsList = dpProcessParamsDao.findByDynamicPricingFilePrcs(fileId);
			if (!dpProcessParamsList.isEmpty()) {
				listOfDpProcessParams.addAll(dpProcessParamsList);
			} else {
				log.error("Unable to create Asset details for given fileId ", fileId);
				throw new SystemException(CoreExceptionCodes.DPA0002, new Object[] {});
			}
		}
		//TODO use mapstruct iterator
		listOfDpProcessParams.stream().forEach(infoData -> {
			DPProcessParamInfo dpProcessParamInfo = dpSourceToInfoMapper.dpWeek0ToInfoMapper(infoData);
			listOfDpProcessParamsInfo.add(dpProcessParamInfo);
		});
		return listOfDpProcessParamsInfo;
	}

	/**
	 * @param listOfDPProcessParamOCN
	 * @param listOfDPProcessParamNRZ
	 * @return
	 */
	public void generateOutputFile(List<DPProcessParam> listOfDPProcessParamOCN, List<DPProcessParam> listOfDPProcessParamNRZ,
			List<DPProcessParam> listOfDPProcessParamPHH, HttpServletResponse httpResponse, String zipFileName) throws SystemException {
		String fileName = zipFileName;
		if (StringUtils.endsWith(StringUtils.lowerCase(fileName.toLowerCase(), Locale.getDefault()), ".xls")) {
			fileName = fileName.substring(0, fileName.length() - 4);
		} else if (StringUtils.endsWith(StringUtils.lowerCase(fileName.toLowerCase(), Locale.getDefault()), ".xlsx")) {
			fileName = fileName.substring(0, fileName.length() - 5);
		}
		String fileOcn = fileName + DPAConstants.OCN_OUTPUT_APPENDER;
		String fileNrz = fileName + DPAConstants.NRZ_OUTPUT_APPENDER;
		String filePhh = fileName + DPAConstants.PHH_OUTPUT_APPENDER;
		log.debug("fileOcn : {}, fileNrz : {}, filePhh : {}", fileOcn, fileNrz, filePhh);
		try (ZipOutputStream zos = new ZipOutputStream(httpResponse.getOutputStream())) {
			byte[] bytes = null;
			// Adding OCN file into ZIP folder
			if (!listOfDPProcessParamOCN.isEmpty()) {
				bytes = createAndDownloadExcel(listOfDPProcessParamOCN);
				zos.putNextEntry(new ZipEntry(fileOcn));
				zos.write(bytes);
			}

			// Adding PHH file into ZIP folder
			if (!listOfDPProcessParamPHH.isEmpty()) {
				bytes = createAndDownloadExcel(listOfDPProcessParamPHH);
				zos.putNextEntry(new ZipEntry(filePhh));
				zos.write(bytes);
			}

			// Adding NRZ file into ZIP folder
			if (!listOfDPProcessParamNRZ.isEmpty()) {
				bytes = createAndDownloadExcel(listOfDPProcessParamNRZ);
				zos.putNextEntry(new ZipEntry(fileNrz));
				zos.write(bytes);
			}
			// Creating response  and  adding zip folder into it.
			setResponseHeaderForZip(httpResponse, fileName);
			httpResponse.getOutputStream().flush();
		} catch (IOException e) {
			log.error("Unable to create zip folder " + e);
		}
	}

	private byte[] createAndDownloadExcel(List<DPProcessParam> listOfParamObject) {
		byte[] bytes = null;
		try (XSSFWorkbook workbook = new XSSFWorkbook()) {
			XSSFSheet sheet = workbook.createSheet(SHEET_WEEK0_DB);
			CellStyle style = workbook.createCellStyle();
			Font font = workbook.createFont();
			font.setBold(true);
			style.setFont(font);
			int rowNum = 0;
			Row row = sheet.createRow(rowNum++);
			int colNum = 0;
			generateHeader(style, row, colNum);
			for (int i = 1; i <= row.getPhysicalNumberOfCells(); i++) {
				sheet.autoSizeColumn(i);
			}
			List<DPProcessParam> columnEntries = listOfParamObject;
			for (DPProcessParam paramObject : columnEntries) {
				colNum = 0;
				rowNum = prepareOutputData(sheet, rowNum, colNum, paramObject);
			}
			if (rowNum > 1) {
				try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
					workbook.write(baos);
					bytes = baos.toByteArray();
				} catch (IOException e) {
					log.error("Unable to read file " + e);
				}
			}
		} catch (IOException e) {
			log.error("Unable to generate workbook " + e);
		}
		return bytes;
	}

	private void setResponseHeaderForZip(final HttpServletResponse response, final String filename) {
		response.setHeader("Content-Type", "application/zip");
		response.setHeader("Content-Disposition", "attachment;filename=" + filename + ".zip");
		response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
	}

	/**
	 * @param style
	 * @param row
	 * @param colNum Week 0 generate Header
	 */
	private void generateHeader(CellStyle style, Row row, int colNum) {
		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER30.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER1.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER29.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER2.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER3.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER4.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER5.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER6.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER7.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER8.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER9.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER10.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER11.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER12.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER13.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER14.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER15.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER16.getValue());

		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER17.getValue());
	}

	/**
	 * @param style
	 * @param row
	 * @param colNum Week N generate Header
	 */
	private void generateWeekNRecommendedHeader(CellStyle style, Row row, int colNum) {
		colNum = prepareCell(style, row, colNum, DPWeekNProcessFileHeader.HEADER1.getValue());

		colNum = prepareCell(style, row, colNum, DPWeekNProcessFileHeader.HEADER2.getValue());

		colNum = prepareCell(style, row, colNum, DPWeekNProcessFileHeader.HEADER3.getValue());

		colNum = prepareCell(style, row, colNum, DPWeekNProcessFileHeader.HEADER4.getValue());

		colNum = prepareCell(style, row, colNum, DPWeekNProcessFileHeader.HEADER5.getValue());

		colNum = prepareCell(style, row, colNum, DPWeekNProcessFileHeader.HEADER6.getValue());

		colNum = prepareCell(style, row, colNum, DPWeekNProcessFileHeader.HEADER7.getValue());

		colNum = prepareCell(style, row, colNum, DPWeekNProcessFileHeader.HEADER8.getValue());

		colNum = prepareCell(style, row, colNum, DPWeekNProcessFileHeader.HEADER9.getValue());
	}

	private void generateExcludedHeader(CellStyle style, Row row, int colNum) {
		colNum = prepareCell(style, row, colNum, DPWeekNProcessFileHeader.HEADER1.getValue());

		colNum = prepareCell(style, row, colNum, DPWeekNProcessFileHeader.HEADER2.getValue());

		colNum = prepareCell(style, row, colNum, DPWeekNProcessFileHeader.HEADER10.getValue());
	}

	/**
	 * @param row
	 * @param colNum
	 * @param data
	 * @return
	 */
	private int prepareCellValue(Row row, int colNum, String data) {
		Cell cell;
		cell = row.createCell(colNum++);
		cell.setCellValue(data);
		return colNum;
	}

	/**
	 * @param style
	 * @param row
	 * @param colNum
	 * @return
	 */
	private int prepareCell(CellStyle style, Row row, int colNum, String data) {
		Cell cell;
		cell = row.createCell(colNum++);
		cell.setCellStyle(style);
		cell.setCellValue(data);
		return colNum;
	}

	@Override
	public List<DPProcessParam> fetchFilesDetails() throws SystemException {
		return dpProcessParamsDao.findAll();
	}

	/**
	 * @param sheet
	 * @param rowNum
	 * @param colNum
	 * @param paramObject
	 * @return
	 */
	private int prepareOutputData(XSSFSheet sheet, int rowNum, int colNum, DPProcessParam paramObject) {

		Row row = sheet.createRow(rowNum++);

		colNum = prepareCellValue(row, colNum, paramObject.getPropTemp());

		colNum = prepareCellValue(row, colNum, paramObject.getAssetNumber());

		colNum = prepareCellValue(row, colNum, paramObject.getOldAssetNumber());

		colNum = prepareCellValue(row, colNum, paramObject.getClientCode());

		colNum = prepareCellValue(row, colNum, paramObject.getStatus());

		colNum = prepareCellValue(row, colNum, paramObject.getAssetValue() != null ? paramObject.getAssetValue().toString() : null);

		colNum = prepareCellValue(row, colNum, paramObject.getAvSetDate());

		colNum = prepareCellValue(row, colNum, paramObject.getListPrice() != null ? paramObject.getListPrice().toString() : null);

		colNum = prepareCellValue(row, colNum, paramObject.getClassification());

		colNum = prepareCellValue(row, colNum, paramObject.getEligible());

		colNum = prepareCellValue(row, colNum, paramObject.getAssignment());

		//Week0 price should be list price if assignment is benchmark.

		if (StringUtils.isNotBlank(paramObject.getAssignment()) && StringUtils
				.equalsIgnoreCase(paramObject.getAssignment(), DPProcessParamAttributes.BENCHMARK_ASSIGNMENT.getValue())) {
			colNum = prepareCellValue(row, colNum, paramObject.getListPrice() != null ? paramObject.getListPrice().toString() : null);
		} else {
			colNum = prepareCellValue(row, colNum, String.valueOf(paramObject.getWeek0Price()));
		}

		colNum = prepareCellValue(row, colNum, paramObject.getState());

		colNum = prepareCellValue(row, colNum, paramObject.getRtSource());

		colNum = prepareCellValue(row, colNum, paramObject.getNotes());

		colNum = prepareCellValue(row, colNum, paramObject.getPropertyType());

		colNum = prepareCellValue(row, colNum, paramObject.getAssignmentDate() != null ?
				DateConversionUtil.getEstDate(paramObject.getAssignmentDate()).toString(DateConversionUtil.US_DATE_TIME_FORMATTER) :
				null);

		colNum = prepareCellValue(row, colNum, paramObject.getPctAV());

		colNum = prepareCellValue(row, colNum, paramObject.getWithinBusinessRules());

		return rowNum;
	}

	@Override
	public List<Command> findfailedStepCommands(String fileId) {
		List<Command> commandList = new ArrayList<>();
		commandList = dpProcessParamsDao.findfailedStepCommands(fileId);
		return commandList;
	}

	@Override
	public List<Command> findFailedStepCommandsWeekn(String processId) {
		List<Command> commandList = dpProcessWeekNParamsDao.findFailedStepCommands(processId);
		return commandList;
	}

	/**
	 * @return List<DPProcessWeekNParamInfo>
	 * @throws SystemException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<DPProcessWeekNParamInfo> getWeekNData() throws SystemException {
		List<DPProcessWeekNParamInfo> listOfDPProcessWeekNParamInfo = new ArrayList<>();
		// List<DPProcessWeekNParam> listOfDPProcessWeekNParams = dpProcessWeekNParamsDao.findAll();

		List allWeekNParams = dpProcessWeekNParamsDao.findAllDashboardParams();

		if (CollectionUtils.isEmpty(allWeekNParams)) {
			log.info("No records found for Week N!");
			throw new SystemException(CoreExceptionCodes.DPWKN0104, new Object[] {});
		} else {

			allWeekNParams.stream().forEach(obj -> {

				Object[] object = (Object[]) obj;

				DPProcessWeekNParamInfo dpProcessWeekNParamInfo = new DPProcessWeekNParamInfo();
				dpProcessWeekNParamInfo.setClassification((String) object[0]);

				DPWeekNProcessStatusInfo dpWeekNProcessStatusInfo = new DPWeekNProcessStatusInfo();
				dpWeekNProcessStatusInfo.setSysGnrtdInputFileName((String) object[1]);
				dpWeekNProcessStatusInfo.setId((String) object[3]);
				dpWeekNProcessStatusInfo.setStatus((String) object[2]);

				dpWeekNProcessStatusInfo
						.setFetchedDateStr(DateConversionUtil.getEstDate((Long) object[5]).toString(DateConversionUtil.DATE_TIME_FORMATTER));
				DateTime parsedDate = DateConversionUtil.EST_DATE_TIME_FORMATTER
						.parseDateTime(DateConversionUtil.getEstDate((Long) object[6]).toString(DateConversionUtil.DATE_TIME_FORMATTER));
				dpWeekNProcessStatusInfo.setLastModifiedDate(DateConversionUtil.getEstDate(parsedDate.getMillis()));

				dpProcessWeekNParamInfo.setDpWeekNProcessStatus(dpWeekNProcessStatusInfo);
				CommandInfo commandInfo = new CommandInfo();
				commandInfo.setName((String) object[4]);
				dpProcessWeekNParamInfo.setCommand(commandInfo);

				listOfDPProcessWeekNParamInfo.add(dpProcessWeekNParamInfo);
			});
		}
		//        listOfDPProcessWeekNParams.stream().forEach(list -> {
		//            DPProcessWeekNParamInfo dpProcessWeekNParamInfo = dpWeekNToInfoMapper.dpWeekNToInfoMapper(list);
		//            listOfDPProcessWeekNParamInfo.add(dpProcessWeekNParamInfo);
		//        });
		return listOfDPProcessWeekNParamInfo;
	}

	@Override
	public List<DPProcessWeekNParamInfo> getWeekNAssetDetails(String weekNId, String weekType) throws SystemException {
		List<DPProcessWeekNParam> listOfDPProcessWeekNParam = new ArrayList<>();
		List<DPProcessWeekNParamInfo> listOfDPProcessWeekNParamInfo = new ArrayList<>();
		log.info("Create Week N Asset details for given Id " + weekNId);
		if (weekType.equalsIgnoreCase(DPAConstants.WEEKN)) {
			List<DPProcessWeekNParam> dPProcessWeekNParamList = dpProcessWeekNParamsDao.findByDpWeekNProcessStatus(weekNId);
			if (!dPProcessWeekNParamList.isEmpty()) {
				listOfDPProcessWeekNParam.addAll(dPProcessWeekNParamList);
			} else {
				log.error("Unable to create Week N Asset details for given Id " + weekNId);
				throw new SystemException(CoreExceptionCodes.DPA0003, new Object[] {});
			}
		}
		listOfDPProcessWeekNParam.stream().forEach(infoData -> {
			DPProcessWeekNParamInfo dpProcessWeekNParamInfo = dpWeekNToInfoMapper.dpWeekNToInfoMapper(infoData);
			listOfDPProcessWeekNParamInfo.add(dpProcessWeekNParamInfo);
		});
		return listOfDPProcessWeekNParamInfo;
	}

	/**
	 * @param id
	 * @return List of DPProcessWeekNParam for process status id
	 */
	@Override
	public List<DPProcessWeekNParam> findDPProcessWeekNParamByProcessID(String id) throws SystemException {
		return dpProcessWeekNParamsDao.findDPProcessWeekNParamByStatusID(id);
	}

	/**
	 * @param id
	 * @return find DPWeekNProcessStatus by id
	 */
	@Override
	public DPWeekNProcessStatus findDPWeekNProcessStatusById(String id) {
		return dpWeekNProcessStatusRepo.getOne(id);
	}

	@Override
	public List<DPProcessWeekNParamInfo> getWeekNFilteredData(DashboardFilterInfo dashboardFilterInfo) throws SystemException {
		List<DPProcessWeekNParamInfo> listOfDPProcessWeekNParamInfo = new ArrayList<>();
		List<DPProcessWeekNParam> listOfDPProcessWeekNParams = weekNFilterDao
				.getWeekNFilteredRecords(dashboardFilterInfo.getStatus(), dashboardFilterInfo.getFromDate(), dashboardFilterInfo.getToDate());
		if (listOfDPProcessWeekNParams.isEmpty()) {
			log.info("No records found for Week N!");
			throw new SystemException(CoreExceptionCodes.DPWKN0104, new Object[] {});
		}
		listOfDPProcessWeekNParams.stream().forEach(list -> {
			DPProcessWeekNParamInfo dpProcessWeekNParamInfo = dpWeekNToInfoMapper.dpWeekNToInfoMapper(list);
			listOfDPProcessWeekNParamInfo.add(dpProcessWeekNParamInfo);
		});
		return listOfDPProcessWeekNParamInfo;
	}

	@Override
	public List<DPProcessParam> findByAssetNumberAndEligibleOrderByCreatedDateDesc(String assetNumber, String eligible) {
		return dpProcessParamsDao.findByAssetNumberAndEligibleOrderByCreatedDateDesc(assetNumber, eligible);
	}

	@Override
	public List<DPProcessParam> findLatestNonDuplicateInWeek0ForGivenAsset(String assetNumber) {
		return dpProcessParamsDao.findLatestNonDuplicateInWeek0ForGivenAsset(assetNumber);
	}

	@Override
	public LocalDate getLatestWekNRunDate() {
		DPWeekNProcessStatus data = dpWeekNProcessStatusRepo.findFirstByOrderByLastModifiedDateDesc();
		return data != null && data.getLastModifiedDate() != null ? LocalDate.ofEpochDay(data.getLastModifiedDate() / 86400000L) : null;
	}

	@Override
	public Map<String, DPProcessParam> findLatestNonDuplicateInWeek0ForGivenAssetList(List<String> assetNumbers) {
		List<DPProcessParam> processParamList = dpProcessParamsDao.findLatestNonDuplicateInWeek0ForGivenAssetList(assetNumbers);
		return processParamList.stream().collect(Collectors.toMap(DPProcessParam::getAssetNumber, c -> c));
	}

}