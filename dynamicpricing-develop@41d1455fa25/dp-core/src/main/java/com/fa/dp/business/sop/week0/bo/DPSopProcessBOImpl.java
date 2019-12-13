package com.fa.dp.business.sop.week0.bo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.fa.dp.business.sop.weekN.dao.DPSopWeekNProcessStatusDao;
import com.fa.dp.business.sop.weekN.entity.DPSopWeekNProcessStatus;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamInfo;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNProcessStatusInfo;
import com.fa.dp.business.sop.weekN.mapper.DPSopWeekNProcessStatusMapper;
import com.fa.dp.business.weekn.bo.DPWeekNBOUtil;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.NoSuchMessageException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.NoSuchMessageException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.multipart.MultipartFile;

import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.filter.constant.DPProcessFilterParams;
import com.fa.dp.business.rr.migration.RRMigration;
import com.fa.dp.business.rr.migration.dao.DPMigrationMapDao;
import com.fa.dp.business.rr.migration.entity.DPMigrationMap;
import com.fa.dp.business.sop.validator.dao.DPSopWeek0ProcessStatusDao;
import com.fa.dp.business.sop.week0.dao.DPSopWeek0ParamsDao;
import com.fa.dp.business.sop.week0.dao.SOPWeek0FilterDao;
import com.fa.dp.business.sop.week0.entity.DPSopWeek0Param;
import com.fa.dp.business.sop.week0.entity.DPSopWeek0ProcessStatus;
import com.fa.dp.business.sop.week0.input.info.DPSopParamEntryInfo;
import com.fa.dp.business.sop.week0.input.info.DPSopWeek0ParamInfo;
import com.fa.dp.business.sop.week0.input.info.DPSopWeek0ProcessStatusInfo;
import com.fa.dp.business.sop.week0.input.mapper.DPSopWeek0ParamMapper;
import com.fa.dp.business.sop.week0.input.mapper.DPSopWeek0ProcessStatusMapper;
import com.fa.dp.business.sop.weekN.dao.DPSopWeekNParamDao;
import com.fa.dp.business.sop.weekN.entity.DPSopWeekNParam;
import com.fa.dp.business.sop.weekN.mapper.DPSopWeekNParamMapper;
import com.fa.dp.business.util.IntegrationType;
import com.fa.dp.business.util.ThreadPoolExecutorUtil;
import com.fa.dp.business.validation.file.header.constant.DPProcessFileHeader;
import com.fa.dp.business.week0.entity.DPProcessParam;
import com.fa.dp.business.week0.info.DashboardFilterInfo;
import com.fa.dp.core.cache.CacheManager;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.exception.codes.CoreExceptionCodes;
import com.fa.dp.core.systemparam.util.AppParameterConstant;
import com.fa.dp.core.util.ConversionUtil;
import com.fa.dp.core.util.DateConversionUtil;
import com.fa.dp.core.util.KeyValue;
import com.fa.dp.core.util.RAClientConstants;
import com.fa.dp.localization.MessageContainer;

/**
 * @author misprakh
 */

@Slf4j
@Named
public class DPSopProcessBOImpl implements DPSopProcessBO {
	private static final String SHEET_SOP_WEEK0_DB = "SOP_Week0_DB";

	@Inject
	private DPSopWeek0ProcessStatusDao dpSopWeek0ProcessStatusDao;

	@Inject
	private DPSopWeekNProcessStatusDao sopWeekNProcessStatusDao;

	@Inject
	private DPSopWeek0ParamsDao dpSopWeek0ParamsDao;

	@Inject
	private DPSopWeekNParamDao dpSopWeekNParamDao;

	@Inject
	private DPSopWeek0ParamMapper sopWeek0ParamMapper;

	@Inject
	private DPSopWeekNParamMapper sopWeekNParamMapper;

	@Inject
	private DPSopWeekNProcessStatusMapper sopWeekNProcessStatusMapper;

	@Inject
	private DPSopWeek0ProcessStatusMapper dpSopWeek0ProcessStatusMapper;

	@Inject
	private CacheManager cacheManager;

	@Inject
	private DPSopWeekNParamDao sopWeekNParamDao;

	private JdbcTemplate jdbcTemplate;

	private ExecutorService executorService;

	@Value("${SOP_WEEK0_CONCURRENT_DBCALL_POOL_SIZE}")
	private int concurrentSopWeek0DbCallPoolSize;

	@Inject
	@Named(value = "rrDataSource")
	private DataSource dataSource;

	@Inject
	private DPMigrationMapDao dpMigrationMapDao;

	@Inject
	private RRMigration rRMigration;
	@Inject
	private SOPWeek0FilterDao sopWeek0FilterDao;

	@PostConstruct
	public void initializeTemplate() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		executorService = ThreadPoolExecutorUtil.getFixedSizeThreadPool(concurrentSopWeek0DbCallPoolSize);
	}

	@PreDestroy
	public void destroy() {
		if (executorService != null) {
			executorService.shutdown();
		}
	}

	/**
	 * Save uploaded files  into SOP week 0 Process Status table
	 *
	 * @param processStatus
	 * @throws SystemException
	 */
	@Override
	public void saveDPSopProcessStatus(DPSopWeek0ProcessStatus processStatus) throws SystemException {
		dpSopWeek0ProcessStatusDao.save(processStatus);
	}

	/**
	 * Retrieve file in SOP week 0 table for given file id
	 *
	 * @param id
	 * @return
	 */
	@Override
	public DPSopWeek0ProcessStatus findDpSopWeek0ProcessStatusById(String id) throws SystemException {
		Optional<DPSopWeek0ProcessStatus> obj = dpSopWeek0ProcessStatusDao.findById(id);
		return obj.get();
	}

	/**
	 * @param fileId
	 * @return
	 * @throws SystemException
	 */
	@Override
	public List<String> findFailedStepCommands(String fileId) throws SystemException {
		List<String> commandList = dpSopWeek0ParamsDao.findSopFailedStepCommands(fileId);
		return commandList;
	}

	/**
	 * Retrieve Assets from SOP week 0 table for given file Id
	 *
	 * @param fileId
	 * @param sopType
	 * @return
	 */
	@Override
	public List<?> getSopAssetsByFileId(String fileId, String sopType) throws SystemException {
		List<?> listOfDpSopParamsInfo = new ArrayList<>();
		log.debug("Assets retrieved for id {} for {} process", fileId, sopType);
		if (StringUtils.equalsIgnoreCase(DPAConstants.SOP_WEEK0, sopType)) {
			List<DPSopWeek0Param> listOfDpSopWeek0Params = dpSopWeek0ParamsDao.findByDPSopWeek0ProcessStatusId(fileId);
			if (!CollectionUtils.isEmpty(listOfDpSopWeek0Params)) {
				listOfDpSopParamsInfo = sopWeek0ParamMapper.mapDomainToInfoList(listOfDpSopWeek0Params);
			} else {
				log.error("Unable to find assets for given fileId", fileId);
				throw new SystemException(CoreExceptionCodes.DPSOPWK0003, new Object[] {});
			}

		}
		if (StringUtils.equalsIgnoreCase(DPAConstants.SOP_WEEKN, sopType)) {
			List<DPSopWeekNParam> listOfDpSopWeekNParams = dpSopWeekNParamDao.findBySopWeekNProcessStatusId(fileId);
			if (!CollectionUtils.isEmpty(listOfDpSopWeekNParams)) {
				listOfDpSopParamsInfo = sopWeekNParamMapper.mapDomainToLinfoList(listOfDpSopWeekNParams);
			} else {
				log.error("Unable to find assets for given fileId", fileId);
				throw new SystemException(CoreExceptionCodes.DPSOPWK0003, new Object[] {});
			}

		}
		return listOfDpSopParamsInfo;
	}

	/**
	 * @param dpSopParamEntryInfo
	 * @throws SystemException
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
	public void saveFileEntriesInDB(DPSopParamEntryInfo dpSopParamEntryInfo) throws SystemException {

		DPSopWeek0ProcessStatus processStatus = dpSopWeek0ProcessStatusMapper.mapInfoToDomain(dpSopParamEntryInfo.getDpSopWeek0ProcessStatusInfo());
		processStatus = dpSopWeek0ProcessStatusDao.save(processStatus);
		DPSopWeek0ProcessStatusInfo processStatusInfo = dpSopWeek0ProcessStatusMapper.mapDomainToInfo(processStatus);
		List<DPSopWeek0ParamInfo> entries = dpSopParamEntryInfo.getColumnEntries();
		for (DPSopWeek0ParamInfo dpInfo : entries) {
			DPSopWeek0Param dpParam = new DPSopWeek0Param();
			dpParam = sopWeek0ParamMapper.mapInfoToDomain(dpInfo);
			dpParam.setSopWeek0ProcessStatus(processStatus);
			dpParam = dpSopWeek0ParamsDao.save(dpParam);
			dpInfo.setId(dpParam.getId());
			dpInfo.setSopWeek0ProcessStatus(processStatusInfo);
		}
		dpSopParamEntryInfo.setDpSopWeek0ProcessStatusInfo(processStatusInfo);
	}

	/**
	 * @param fileStatus
	 * @return
	 * @throws SystemException
	 */
	@Override
	public DPSopWeek0ProcessStatus findFileByStatus(String fileStatus) throws SystemException {
		return dpSopWeek0ProcessStatusDao.findByStatus(fileStatus);
	}

	@Override
	public List<DPSopWeekNProcessStatusInfo> findSopWeekNFileStatus(String fileStatus) throws SystemException {
		List<DPSopWeekNProcessStatus> processStatus = sopWeekNProcessStatusDao.findByStatus(fileStatus);
		return sopWeekNProcessStatusMapper.mapDomainToLinfoList(processStatus);
	}

	/**
	 * Filter SOP Week 0 Asset number based on Asset Values
	 *
	 * @param inputParamEntry
	 * @param filterName
	 * @return
	 * @throws SystemException
	 */
	@Override
	public KeyValue<List<DPSopWeek0ParamInfo>, List<DPSopWeek0ParamInfo>> filterOnAssetValue(DPSopParamEntryInfo inputParamEntry, String filterName)
			throws SystemException {
		List<DPSopWeek0ParamInfo> filteredOutEntries = new ArrayList<>();
		List<DPSopWeek0ParamInfo> successEntries = new ArrayList<>();

		BigDecimal maxAssetValue = null;
		BigDecimal minAssetValue = null;
		for (DPSopWeek0ParamInfo columnEntry : inputParamEntry.getColumnEntries()) {
			log.info("Asset value filter : Loan number - {}, classification {} ", columnEntry.getAssetNumber(), columnEntry.getClassification());
			MDC.put(RAClientConstants.LOAN_NUMBER, columnEntry.getAssetNumber());

			if (StringUtils.equals(DPAConstants.OCN, columnEntry.getClassification())) {
				maxAssetValue = new BigDecimal(String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_OCN_MAXVALUE)));
				minAssetValue = new BigDecimal(String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_OCN_MINVALUE)));
			} else if (StringUtils.equals(DPAConstants.PHH, columnEntry.getClassification())) {
				maxAssetValue = new BigDecimal(String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_PHH_MAXVALUE)));
				minAssetValue = new BigDecimal(String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_PHH_MINVALUE)));
			} else if (StringUtils.equals(DPAConstants.NRZ, columnEntry.getClassification())) {
				maxAssetValue = new BigDecimal(String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_NRZ_MAXVALUE)));
				minAssetValue = new BigDecimal(String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_NRZ_MINVALUE)));
			}

			if (columnEntry.getAssetValue().compareTo(maxAssetValue) == 1
					  || columnEntry.getAssetValue().compareTo(minAssetValue) <= 0) {
				DPSopWeek0ParamInfo filteredParam = setParams(columnEntry, true);
				filteredParam.setFailedStepCommandName(filterName);
				filteredOutEntries.add(filteredParam);
			} else {
				successEntries.add(columnEntry);
			}
			MDC.remove(RAClientConstants.LOAN_NUMBER);
		}
		return new KeyValue<>(successEntries, filteredOutEntries);
	}

	/*
	 * Setting fields for ineligible records in SOP week 0 table
	 */
	private static DPSopWeek0ParamInfo setParams(DPSopWeek0ParamInfo colEntry, boolean isAssetValue) {
		DPSopWeek0ParamInfo filteredEntry = colEntry;
		String failureNotes = DPAConstants.BLANK;
		String notes = colEntry.getNotes();
		if (StringUtils.isNotEmpty(notes)) {
			int index = notes.indexOf(DPProcessFilterParams.NOTES_PT.getValue());
			if (index > -1) {
				failureNotes = notes.substring(index + 27, notes.length());
			} else {
				failureNotes = notes;
			}
		}
		if (!isAssetValue) {
			filteredEntry.setNotes(DPProcessFilterParams.NOTES_DUP.getValue());
		}
		filteredEntry.setEligible(DPProcessFilterParams.INELIGIBLE.getValue());
		filteredEntry.setListPrice(colEntry.getListPrice());
		filteredEntry.setAssignment(DPAConstants.BLANK);
		filteredEntry.setAssignmentDate(DateConversionUtil.getCurrentUTCTime().getMillis());
		if (isAssetValue) {
			filteredEntry.setNotes(DPProcessFilterParams.NOTES_AV.getValue() + failureNotes);
		}
		return filteredEntry;
	}

	/**
	 * @param in
	 * @return
	 */
	@Override
	public DPSopWeek0Param saveDPSopWeek0Param(DPSopWeek0Param in) throws SystemException {
		return dpSopWeek0ParamsDao.save(in);
	}

	/**
	 * @param columnEntry
	 * @param dpProcessParams
	 * @param filterName
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
	public DPSopWeek0ParamInfo filterOnDuplicates(DPSopWeek0ParamInfo columnEntry, List<DPSopWeek0ParamInfo> dpProcessParams, String filterName) {
		DPSopWeek0ParamInfo filteredParam = null;

		for (DPSopWeek0ParamInfo dpProcessEntry : dpProcessParams) {
			if (StringUtils.equalsIgnoreCase(DPProcessFilterParams.ELIGIBLE.getValue(), dpProcessEntry.getEligible())) {
				if (!StringUtils.equalsIgnoreCase(dpProcessEntry.getClassification(), columnEntry.getClassification())) {
					// If classification has changed, update old record and process new record in normal flow
					updateSopWeek0OldRecord(dpProcessEntry.getAssetNumber(), DPProcessFilterParams.INELIGIBLE.getValue(),
							String.format(DPProcessFilterParams.NOTES_TRANS.getValue(), dpProcessEntry.getClassification(),
									columnEntry.getClassification()));
				} else if (dpProcessEntry.getAssetValue().compareTo(columnEntry.getAssetValue()) == 0) {
					// duplicate record is returned back
					filteredParam = setParams(columnEntry, false);
					filteredParam.setFailedStepCommandName(filterName);
				} else {
					DateTimeFormatter dateFormat = DateTimeFormat.forPattern("MM/dd/yyyy");
					DateTime date = new DateTime();
					updateSopWeek0OldRecord(dpProcessEntry.getAssetNumber(), DPProcessFilterParams.ELIGIBLE_OUT_OF_SCOPE.getValue(),
							String.format(DPProcessFilterParams.NOTES_REV.getValue(), date.toString(dateFormat)));
				}
				break;
			}
		}
		return filteredParam;
	}

	/**
	 * @param assetNumber
	 * @return
	 */
	@Override
	public List<DPSopWeek0Param> searchByAssetNumber(String assetNumber) throws SystemException {
		return dpSopWeek0ParamsDao.findByAssetNumber(assetNumber);
	}

	@Override
	public List<DPSopWeek0Param> countAssignmentByStateAndAssetNumber(String state, BigDecimal lowerAssetValue, BigDecimal higherAssetValue, String benchMarkAssignment, String classification, String sopWeek0dbInitial) throws SystemException {
		return dpSopWeek0ParamsDao.countAssignmentByStateAndAssetNumber(state, lowerAssetValue, higherAssetValue, benchMarkAssignment, classification, sopWeek0dbInitial);
	}

	/**
	 * @param dpSopParamEntryInfo
	 * @throws SystemException
	 */
	@Override
	public void checkSopWeek0Migration(DPSopParamEntryInfo dpSopParamEntryInfo) throws SystemException {
		String rrMigrationQuery = (String) cacheManager.getAppParamValue(AppParameterConstant.RR_MIGRATION_QUERY);
		log.debug("RR Migration Query : {}", rrMigrationQuery);
		Long startTime = System.currentTimeMillis();
		List<Future<KeyValue<String, DPSopWeek0ParamInfo>>> futureList = new ArrayList<>();

		for (DPSopWeek0ParamInfo obj : dpSopParamEntryInfo.getColumnEntries()) {
			Future<KeyValue<String, DPSopWeek0ParamInfo>> keyValueFuture = executorService.submit(fetchSopWeek0MigrationInfo(rrMigrationQuery, obj));
			futureList.add(keyValueFuture);
		}

		for (Future<KeyValue<String, DPSopWeek0ParamInfo>> keyValueFuture : futureList) {
			try {
				KeyValue<String, DPSopWeek0ParamInfo> week0ParamInfoKeyValue = keyValueFuture.get();
				DPSopWeek0ParamInfo obj = week0ParamInfoKeyValue.getValue();
				log.debug("Migration Details for {} : Old asset number - {}, Prop temp - {}", obj.getAssetNumber(), obj.getOldAssetNumber(),
						obj.getPropTemp());
				if (obj != null && StringUtils.isNoneEmpty(obj.getAssetNumber(), obj.getPropTemp())) {
					DPMigrationMap dpMigrationMap = new DPMigrationMap();
					dpMigrationMap.setAssetNumber(obj.getAssetNumber());
					dpMigrationMap.setOldAssetNumber(obj.getOldAssetNumber());
					dpMigrationMap.setPropTemp(obj.getPropTemp());
					dpMigrationMapDao.deleteAll();
					dpMigrationMapDao.save(dpMigrationMap);
					rRMigration.retrospectUpdateMigrationInformation();
					dpMigrationMapDao.deleteAll();
				}

			} catch (InterruptedException | ExecutionException e) {
				log.error("An error occurred while fetching rrMigration data ", e);
			}
		}
		log.info("Time taken for all Week0 RR Migration records : {}ms", (System.currentTimeMillis() - startTime));
	}

	/*
	 * Update Old record in db to 'Out of Scope' and 'Property Revalued'
	 */
	private void updateSopWeek0OldRecord(String assetNumber, String eligiblity, String notes) {
		log.info("Updating the existing week0 record <{}> to Ineligible/Out of scope", assetNumber);
		dpSopWeek0ParamsDao.updateSopWeek0Record(eligiblity, notes, assetNumber, DPProcessFilterParams.ELIGIBLE.getValue());
	}

	/**
	 * @param rrMigrationQuery
	 * @param obj
	 * @return
	 */
	private Callable<KeyValue<String, DPSopWeek0ParamInfo>> fetchSopWeek0MigrationInfo(String rrMigrationQuery, DPSopWeek0ParamInfo obj) {
		return () -> {
			String oldAssetNumber = jdbcTemplate.execute(rrMigrationQuery, (PreparedStatementCallback<String>) ps -> {
				String oldAssetNumber1 = obj.getAssetNumber();
				try {
					ps.setString(1, obj.getAssetNumber());
					ps.setString(2, obj.getAssetNumber());
					ResultSet rs = ps.executeQuery();
					if (null != rs && rs.next()) {
						if (null != rs.getString(DPAConstants.PROP_TEMP)) {
							obj.setPropTemp(StringUtils.trim(rs.getString(DPAConstants.PROP_TEMP)));
						}
						if (null != rs.getString(DPAConstants.OLD_RR_LOAN_NUM)) {
							obj.setOldAssetNumber(StringUtils.trim(rs.getString(DPAConstants.OLD_RR_LOAN_NUM)));
						}
						if (null != rs.getString(DPAConstants.LOAN_NUM)) {
							obj.setAssetNumber(StringUtils.trim(rs.getString(DPAConstants.LOAN_NUM)));
						}
					}
				} catch (SQLException sqle) {
					log.info(sqle.getLocalizedMessage(), sqle);
					String errorDetail = saveSopWeek0ErrorDetail(obj.getId(), IntegrationType.RR_INTEGRATION.getIntegrationType(),
							obj.getErrorDetail(), sqle);
					obj.setErrorDetail(errorDetail);
				}
				return oldAssetNumber1;
			});
			return new KeyValue<>(oldAssetNumber, obj);
		};
	}

	/**
	 * @param id
	 * @param type
	 * @param errorDetail
	 * @param ex
	 * @return
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
	private String saveSopWeek0ErrorDetail(String id, String type, String errorDetail, Exception ex) {
		log.info("saveSopWeek0ErrorDetail() started.");
		String genericErrorCode = null;
		String errorCode = null;
		String errorMessage = null;
		if (null != ex) {
			if (ex instanceof ExecutionException) {
				log.info("instanceof ExecutionException");
				ex = (Exception) ex.getCause();
			}

			if (ex instanceof DataAccessException) {
				log.info("instanceof DataAccessException");
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
				log.info("errorCode : " + errorCode);
			} else if (ex instanceof SystemException) {
				if (ex.getCause() instanceof HttpClientErrorException) {
					log.info("instanceof HttpClientErrorException");
					errorCode = "HTTP_" + String.valueOf(((HttpClientErrorException) ex.getCause()).getStatusCode().value());
				} else if (ex.getCause() instanceof ResourceAccessException) {
					log.info("instanceof ResourceAccessException");
					errorCode = "ERR001";
				} else if (ex.getCause() instanceof HttpStatusCodeException) {
					log.info("instanceof HttpStatusCodeException");
					errorCode = "HTTP_" + String.valueOf(((HttpStatusCodeException) ex.getCause()).getStatusCode().value());
				} else {
					log.info("instanceof SystemException");
					errorCode = ((SystemException) ex).getCode();
				}
				log.info("errorCode : " + errorCode);
			}

			if (null != errorCode) {
				try {
					genericErrorCode = MessageContainer.getMessage(errorCode, new Object[] {});
					errorMessage = MessageContainer.getMessage(genericErrorCode, new Object[] {});
				} catch (NoSuchMessageException e) {
					log.info(e.getMessage(), e);
					errorMessage = ex.getMessage();
				}
			} else {
				errorMessage = ex.getMessage();
			}
		} else {
			errorMessage = "No record found";
		}

		log.info("errorMessage : " + errorMessage);
		log.info("genericErrorCode : " + genericErrorCode);

		Map<String, String> errorMap = new HashMap<>();

		if (!StringUtils.isBlank(errorDetail)) {
			try {
				errorMap = ConversionUtil.convertJson(errorDetail, Map.class);
			} catch (SystemException e) {
				log.info(e.getMessage(), e);
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
			log.info(e.getMessage(), e);
		}

		log.info("errorDetail : " + errorDetail);

		dpSopWeek0ParamsDao.updateSopWeek0ErrorDetailsById(id, errorDetail);

		log.info("saveDPProcessErrorDetail() ended.");

		return errorDetail;
	}

	public List<DPSopWeek0ParamInfo> saveFailedRecord(List<DPSopWeek0ParamInfo> recordsToSave) throws SystemException {
		List<DPSopWeek0ParamInfo> result = new ArrayList<>();
		for (DPSopWeek0ParamInfo info : recordsToSave) {
			DPSopWeek0Param in = sopWeek0ParamMapper.mapInfoToDomain(info);
			in = dpSopWeek0ParamsDao.save(in);
			info.setId(in.getId());
			result.add(info);
		}
		return result;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<DPSopWeek0ParamInfo> getSOPWeek0Data() throws SystemException {
		List<DPSopWeek0ParamInfo> listOfDPSOPWeek0ProcessParamInfo = new ArrayList<>();
		List allSOPWeek0Params = dpSopWeek0ParamsDao.findAllDashboardParams();
		if (allSOPWeek0Params.isEmpty()) {
			log.error("No records found for SOP Week 0!");
			throw new SystemException(CoreExceptionCodes.DPSOPWK0012, new Object[] {});
		} else {

			allSOPWeek0Params.stream().forEach(obj -> {
				Object[] object = (Object[]) obj;
				DPSopWeek0ParamInfo dpSopWeek0ParamInfo = new DPSopWeek0ParamInfo();
				dpSopWeek0ParamInfo.setClassification((String) object[0]);
				DPSopWeek0ProcessStatusInfo dpSopWeek0ProcessStatusInfo = new DPSopWeek0ProcessStatusInfo();
				dpSopWeek0ProcessStatusInfo.setInputFileName((String) object[1]);
				dpSopWeek0ProcessStatusInfo.setId((String) object[2]);
				dpSopWeek0ProcessStatusInfo.setStatus((String) object[3]);
				dpSopWeek0ProcessStatusInfo.setLastModifiedDate(DateConversionUtil.getEstDate((Long) object[4]));
				dpSopWeek0ParamInfo.setSopWeek0ProcessStatus(dpSopWeek0ProcessStatusInfo);
				dpSopWeek0ParamInfo.setFailedStepCommandName((String) object[5]);
				listOfDPSOPWeek0ProcessParamInfo.add(dpSopWeek0ParamInfo);
			});
		}
		return listOfDPSOPWeek0ProcessParamInfo;
	}

	@Override
	public List<DPSopWeek0ParamInfo> getWeekZeroFilteredFiles(DashboardFilterInfo dashboardFilterInfo) throws SystemException {
		List<DPSopWeek0ParamInfo> listOfDPSopWeek0ParamInfo = new ArrayList<>();
		List<DPSopWeek0Param> listOfDPSopWeek0Param = sopWeek0FilterDao
				.getSOPWeek0FilteredRecords(dashboardFilterInfo.getFileName(), dashboardFilterInfo.getStatus(), dashboardFilterInfo.getFromDate(),
						dashboardFilterInfo.getToDate());
		if (listOfDPSopWeek0Param.isEmpty()) {
			log.info("No records found for SOP Week 0!");
			throw new SystemException(CoreExceptionCodes.DPSOPWK0005, new Object[] {});
		}
		listOfDPSopWeek0Param.stream().forEach(param -> {
			DPSopWeek0ParamInfo dpSopWeek0ParamInfo = sopWeek0ParamMapper.mapDomainToInfo(param);
			listOfDPSopWeek0ParamInfo.add(dpSopWeek0ParamInfo);
		});
		return listOfDPSopWeek0ParamInfo;
	}

	@Override
	public DPSopWeekNProcessStatusInfo saveSopWeekNProcessData(DPSopWeekNProcessStatusInfo sopWeekNProcessStatusInfo) throws SystemException {
		DPSopWeekNProcessStatus sopProcessStatus = null;
		try {
			sopProcessStatus = sopWeekNProcessStatusDao.save(sopWeekNProcessStatusMapper.mapInfoToDomain(sopWeekNProcessStatusInfo));
		} catch (Exception e) {
			log.error("Problem in saving sop weekn process status.{}", e);
			SystemException.newSystemException(CoreExceptionCodes.DPSOPWKN010);
		}
		return sopWeekNProcessStatusMapper.mapDomainToInfo(sopProcessStatus);
	}

	@Override
	public List<DPSopWeekNParamInfo> saveSopWeekNParams(List<DPSopWeekNParamInfo> columnEntries) throws SystemException {
		List<DPSopWeekNParam> dpSopWeekNParams = null;
		try {
			dpSopWeekNParams = dpSopWeekNParamDao.saveAll(sopWeekNParamMapper.mapInfoToDomainList(columnEntries));
		} catch (Exception e) {
			log.error("Problem in saving sop weekn param data. {}", e);
			SystemException.newSystemException(CoreExceptionCodes.DPSOPWKN011);
		}
		return sopWeekNParamMapper.mapDomainToLinfoList(dpSopWeekNParams);
	}

	@Override
	public List<DPSopWeekNParamInfo> fetchSopWeekNDataBySheet(MultipartFile file) throws SystemException {
		Workbook workbook = null;
		Sheet dataTypeProcessedSheet = null;
		List<DPSopWeekNParamInfo> dpProcessWeekNParamInfos = new ArrayList<>();
		try {
			workbook = new XSSFWorkbook(file.getInputStream());
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				if (StringUtils.equals(workbook.getSheetName(i), DPAConstants.PROCESSED_RECORDS)) {
					dataTypeProcessedSheet = workbook.getSheetAt(i);
					dpProcessWeekNParamInfos = getSopWeekNParamInfos(dataTypeProcessedSheet);
				}
			}
		} catch (IOException e) {
			log.error("Problem in reading sop weekn excel file. {}", e);
			SystemException.newSystemException(CoreExceptionCodes.DPSOPWKN016);
		} finally {
			IOUtils.closeQuietly(workbook);
		}
		return dpProcessWeekNParamInfos;
	}

	@Override
	public List<DPSopWeekNParamInfo> findSopWeekNParamsData(String fileId) throws SystemException {
		List<DPSopWeekNParam> sopWeekNParams = null;
		try {
			sopWeekNParams = sopWeekNParamDao.findBySopWeekNProcessStatusId(fileId);
		} catch (Exception e) {
			log.error("Problem in fetching data for sop weekn process. id : {}, Error stack : {}", fileId, e);
			SystemException.newSystemException(CoreExceptionCodes.DPSOPWK0003);
		}
		return sopWeekNParamMapper.mapDomainToLinfoList(sopWeekNParams);
	}

	@Override
	@Transactional
	public void updateSopWeeknRunningStatus(String id, String fileStatus) throws SystemException {
		try {
			sopWeekNProcessStatusDao.updateRunningStatus(id, fileStatus);
		} catch (Exception e) {
			log.error("sop weekn status chnage failed.", e);
			SystemException.newSystemException(CoreExceptionCodes.DPSOPWKN012);
		}
	}

	private List<DPSopWeekNParamInfo> getSopWeekNParamInfos(Sheet dataTypeProcessedSheet) throws SystemException {
		log.info("sop Validating the columns in Potential sheet");
		if (Objects.isNull(dataTypeProcessedSheet)) {
			log.error("Potential sheet is not available in uploaded file");
			SystemException.newSystemException(CoreExceptionCodes.DPWKN0107);
		}
		List<DPSopWeekNParamInfo> dpProcessWeekNParamInfos = new ArrayList<>();
		// TODO Add latest list end date logic
		int assetNumberColNum = -1;
		//int latestListEndDateColNum = -1;

		Row headerRow = dataTypeProcessedSheet.getRow(0);
		DataFormatter df = new DataFormatter();
		for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
			String cellValue = df.formatCellValue(headerRow.getCell(i));
			if (StringUtils.equalsIgnoreCase(DPProcessFileHeader.HEADER1.getValue(), cellValue)) {
				assetNumberColNum = i;
			}
			/*if (StringUtils.equalsIgnoreCase(DPProcessFileHeader.HEADER18.getValue(), cellValue)) {
				latestListEndDateColNum = i;
			}*/
		}
		if (assetNumberColNum == -1 /*&& latestListEndDateColNum == -1*/) {
			/*List<String> missingCols = new ArrayList<>();
			if (assetNumberColNum == -1) {
				missingCols.add(DPProcessFileHeader.HEADER1.getValue());
				missingCols.add(DPProcessFileHeader.HEADER18.getValue());
			}*/
			log.error("sop Asset # / Most recent list end date column/columns is/are Missing in Potential List Sheet: {}",
					DPProcessFileHeader.HEADER1.getValue());
			//throw new SystemException(CoreExceptionCodes.DPWKN0108, new Object[] { String.join(", ", missingCols) });
			SystemException.newSystemException(CoreExceptionCodes.DPWKN0108, DPProcessFileHeader.HEADER1.getValue());
		}
		DPSopWeekNParamInfo dpProcessWeekNParamInfo;
		for (int rowIndex = 1; rowIndex <= dataTypeProcessedSheet.getLastRowNum(); rowIndex++) {
			dpProcessWeekNParamInfo = new DPSopWeekNParamInfo();
			Row currentRow = dataTypeProcessedSheet.getRow(rowIndex);
			String assetNumber = DPWeekNBOUtil.fetchCelValue(currentRow, df, assetNumberColNum);
			if (StringUtils.isBlank(assetNumber)) {
				log.error("Asset # cannot be empty");
				throw new SystemException(CoreExceptionCodes.DPWKN0112, new Object[] {});
			}

			//String mostRecentListEndDate = DPWeekNBOUtil.fetchCelValue(currentRow, df, latestListEndDateColNum);
			if (StringUtils.isNotEmpty(StringUtils.trim(assetNumber))) {
				dpProcessWeekNParamInfo.setAssetNumber(assetNumber);
				//dpProcessWeekNParamInfo.setMostRecentListEndDate(mostRecentListEndDate);
				dpProcessWeekNParamInfos.add(dpProcessWeekNParamInfo);
			}
		}
		if (org.apache.commons.collections4.CollectionUtils.isEmpty(dpProcessWeekNParamInfos)) {
			log.error("No records found in uploaded sop weekn file to process");
			throw new SystemException(CoreExceptionCodes.DP020, new Object[] {});
		}

		return dpProcessWeekNParamInfos;
	}

	@Override
	public List<DPSopWeek0Param> findDPSOPWeek0ProcessParamByProcessID(String id) {
		List<DPSopWeek0Param> result = dpSopWeek0ParamsDao.findDPProcessParamByStatusID(id);
		return result;
	}

	@Override
	public void generateSOPWeek0OutputFile(List<DPSopWeek0Param> listOfDPSOPWeek0ProcessParamOCN,
			List<DPSopWeek0Param> listOfDPSOPWeek0ProcessParamNRZ, List<DPSopWeek0Param> listOfDPSOPWeek0ProcessParamPHH,
			HttpServletResponse httpResponse, String zipFileName) throws SystemException {
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
			if (!listOfDPSOPWeek0ProcessParamOCN.isEmpty()) {
				bytes = createAndDownloadExcel(listOfDPSOPWeek0ProcessParamOCN);
				zos.putNextEntry(new ZipEntry(fileOcn));
				zos.write(bytes);
			}
			// Adding PHH file into ZIP folder
			if (!listOfDPSOPWeek0ProcessParamPHH.isEmpty()) {
				bytes = createAndDownloadExcel(listOfDPSOPWeek0ProcessParamPHH);
				zos.putNextEntry(new ZipEntry(filePhh));
				zos.write(bytes);
			}
			// Adding NRZ file into ZIP folder
			if (!listOfDPSOPWeek0ProcessParamNRZ.isEmpty()) {
				bytes = createAndDownloadExcel(listOfDPSOPWeek0ProcessParamNRZ);
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

	private void setResponseHeaderForZip(final HttpServletResponse response, final String filename) {
		response.setHeader("Content-Type", "application/zip");
		response.setHeader("Content-Disposition", "attachment;filename=" + filename + ".zip");
		response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
	}

	private byte[] createAndDownloadExcel(List<DPSopWeek0Param> listOfParamObject) {
		byte[] bytes = null;
		try (XSSFWorkbook workbook = new XSSFWorkbook()) {
			XSSFSheet sheet = workbook.createSheet(DPAConstants.SHEET_SOP_WEEK0_DB);
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
			List<DPSopWeek0Param> columnEntries = listOfParamObject;
			for (DPSopWeek0Param paramObject : columnEntries) {
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

	/**
	 * @param style
	 * @param row
	 * @param colNum Week 0 generate Header
	 */
	private void generateHeader(CellStyle style, Row row, int colNum) {
		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER30.getValue());
		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER1.getValue());
		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER29.getValue());
		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER11.getValue());
		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER31.getValue());
		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER3.getValue());
		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER4.getValue());
		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER5.getValue());
		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER32.getValue());
		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER6.getValue());
		colNum = prepareCell(style, row, colNum, DPProcessFileHeader.HEADER7.getValue());
	}

	private int prepareCell(CellStyle style, Row row, int colNum, String data) {
		Cell cell;
		cell = row.createCell(colNum++);
		cell.setCellStyle(style);
		cell.setCellValue(data);
		return colNum;
	}

	private int prepareOutputData(XSSFSheet sheet, int rowNum, int colNum, DPSopWeek0Param paramObject) {
		Row row = sheet.createRow(rowNum++);
		colNum = prepareCellValue(row, colNum, paramObject.getPropTemp());
		colNum = prepareCellValue(row, colNum, paramObject.getAssetNumber());
		colNum = prepareCellValue(row, colNum, paramObject.getOldAssetNumber());
		colNum = prepareCellValue(row, colNum, paramObject.getState());
		colNum = prepareCellValue(row, colNum, paramObject.getPropertyType());
		colNum = prepareCellValue(row, colNum, paramObject.getStatus());
		colNum = prepareCellValue(row, colNum, paramObject.getAssetValue() != null ? paramObject.getAssetValue().toString() : null);
		colNum = prepareCellValue(row, colNum, paramObject.getAvSetDate());
		colNum = prepareCellValue(row, colNum, paramObject.getReoDate());
		colNum = prepareCellValue(row, colNum, paramObject.getListPrice() != null ? paramObject.getListPrice().toString() : null);
		colNum = prepareCellValue(row, colNum, paramObject.getClassification());
		/*colNum = prepareCellValue(row, colNum, paramObject.getEligible());
		colNum = prepareCellValue(row, colNum, paramObject.getAssignment());
		//Week0 price should be list price if assignment is benchmark.
		if (StringUtils.isNotBlank(paramObject.getAssignment()) && StringUtils
				.equalsIgnoreCase(paramObject.getAssignment(), DPProcessParamAttributes.BENCHMARK_ASSIGNMENT.getValue())) {
			colNum = prepareCellValue(row, colNum, paramObject.getListPrice() != null ? paramObject.getListPrice().toString() : null);
		} else {
			colNum = prepareCellValue(row, colNum, String.valueOf(paramObject.getWeek0Price()));
		}
		colNum = prepareCellValue(row, colNum, paramObject.getRtSource());
		colNum = prepareCellValue(row, colNum, paramObject.getNotes());
		colNum = prepareCellValue(row, colNum, paramObject.getPropertyType());
		colNum = prepareCellValue(row, colNum, paramObject.getAssignmentDate() != null ?
				DateConversionUtil.getEstDate(paramObject.getAssignmentDate()).toString(DateConversionUtil.US_DATE_TIME_FORMATTER) :
				null);
		colNum = prepareCellValue(row, colNum, paramObject.getPctAV());
		colNum = prepareCellValue(row, colNum, paramObject.getWithinBusinessRules());*/
		return rowNum;
	}

	private int prepareCellValue(Row row, int colNum, String data) {
		Cell cell;
		cell = row.createCell(colNum++);
		cell.setCellValue(data);
		return colNum;
	}

	@Override
	public List<DPSopWeek0ParamInfo> getAssetDetails(String fileId, String type) throws SystemException {
		List<DPSopWeek0Param> listOfDpProcessParams = new ArrayList<>();
		List<DPSopWeek0ParamInfo> listOfDpProcessParamsInfo = new ArrayList<>();
		if (type.equalsIgnoreCase(DPAConstants.SOP_WEEK0)) {
			List<DPSopWeek0Param> dpProcessParamsList = dpSopWeek0ParamsDao.findByDynamicPricingFilePrcs(fileId);
			if (!dpProcessParamsList.isEmpty()) {
				listOfDpProcessParams.addAll(dpProcessParamsList);
			} else {
				log.error("Unable to create Asset details for given fileId ", fileId);
				throw new SystemException(CoreExceptionCodes.DPA0002, new Object[] {});
			}
		}
		listOfDpProcessParams.stream().forEach(infoData -> {
			DPSopWeek0ParamInfo dpProcessParamInfo = sopWeek0ParamMapper.mapDomainToInfo(infoData);
			listOfDpProcessParamsInfo.add(dpProcessParamInfo);
		});
		return listOfDpProcessParamsInfo;
	}

	@Override
	public DPSopWeek0Param findInSOPWeek0ForAssetNumber(String selrPropIdVcNn) throws SystemException {
		List<DPSopWeek0Param> dpProcessParams = dpSopWeek0ParamsDao.findLatestNonDuplicateInSOPWeek0ForGivenAsset(selrPropIdVcNn);
		return !dpProcessParams.isEmpty() ? dpProcessParams.get(0) : null;
	}

	@Override
	public DPSopWeek0Param findOcwenLoanByAssetNumber(String assetNumber) {
		DPSopWeek0Param dpProcessParam = dpSopWeek0ParamsDao.findOcwenLoanBYAssetNumber(assetNumber);
		return dpProcessParam;
	}

	@Override
	public DPSopWeek0Param findOutOfScopeLoanByAssetNumber(String assetNumber) {
		DPSopWeek0Param dpProcessParam = dpSopWeek0ParamsDao.findOutOfScopeLoanByAssetNumber(assetNumber);
		return dpProcessParam;
	}
}
