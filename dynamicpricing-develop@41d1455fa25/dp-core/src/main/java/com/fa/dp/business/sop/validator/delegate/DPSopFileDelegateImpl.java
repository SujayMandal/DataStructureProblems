package com.fa.dp.business.sop.validator.delegate;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolation;
import javax.validation.Path.Node;
import javax.validation.Validator;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.joda.time.DateTime;
import org.slf4j.MDC;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fa.dp.business.command.master.CommandMaster;
import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.rr.migration.RRMigration;
import com.fa.dp.business.sop.week0.bo.DPSopProcessBO;
import com.fa.dp.business.sop.week0.entity.DPSopWeek0Param;
import com.fa.dp.business.sop.week0.entity.DPSopWeek0ProcessStatus;
import com.fa.dp.business.sop.week0.input.info.DPSopParamEntryInfo;
import com.fa.dp.business.sop.week0.input.info.DPSopWeek0ParamInfo;
import com.fa.dp.business.sop.week0.input.info.DPSopWeek0ProcessStatusInfo;
import com.fa.dp.business.sop.week0.input.mapper.DPSopWeek0ParamMapper;
import com.fa.dp.business.sop.week0.input.mapper.DPSopWeek0ProcessStatusMapper;
import com.fa.dp.business.sop.weekN.bo.DPSopWeekNParamBO;
import com.fa.dp.business.sop.weekN.dao.DPSopWeekNParamDao;
import com.fa.dp.business.sop.weekN.delegate.DPSopWeekNParamDelegate;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamEntryInfo;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamInfo;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNProcessStatusInfo;
import com.fa.dp.business.sop.weekN.mapper.DPSopWeekNParamMapper;
import com.fa.dp.business.util.DPFileProcessStatus;
import com.fa.dp.business.validation.file.util.InputFileValidationUtil;
import com.fa.dp.business.weekn.bo.DPWeekNBOUtil;
import com.fa.dp.core.cache.CacheManager;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.exception.business.BusinessException;
import com.fa.dp.core.exception.codes.CoreExceptionCodes;
import com.fa.dp.core.systemparam.util.AppParameterConstant;
import com.fa.dp.core.util.DateConversionUtil;
import com.fa.dp.localization.MessageContainer;

/**
 * @author misprakh
 */
@Slf4j
@Named
public class DPSopFileDelegateImpl implements DPSopFileDelegate {

	@Inject
	private DPSopProcessBO dpSopProcessBO;

	@Inject
	private DPSopWeekNParamBO dpSopWeekNParamBO;

	@Inject
	private DPSopWeekNParamDelegate dpSopWeekNParamDelegate;

	@Inject
	private Validator validator;

	@Inject
	private DPSopWeek0ProcessStatusMapper dpSopWeek0ProcessStatusMapper;

	@Inject
	private DPSopWeek0ParamMapper dpSopWeek0ParamMapper;

	@Inject
	private DPSopWeekNParamDao sopWeekNParamDao;

	@Inject
	private DPSopWeekNParamMapper sopWeekNParamMapper;

	@Inject
	private CacheManager cacheManager;

	@Inject
	private RRMigration rrMigration;

	@Inject
	@Named("dpCommandMaster")
	private CommandMaster commandMaster;

	/**
	 * @param infoObject
	 *
	 * @return String
	 *
	 * @throws SystemException
	 */
	@Override
	public String setStatus(DPSopParamEntryInfo infoObject) throws SystemException {
		List<String> failedStepCommands = dpSopProcessBO.findFailedStepCommands(infoObject.getDpSopWeek0ProcessStatusInfo().getId());
		if(ObjectUtils.isEmpty(failedStepCommands) || failedStepCommands.size() == 0) {
			return DPFileProcessStatus.SUCCESSFUL.getFileStatus();
		} else {
			List<DPSopWeek0ParamInfo> dpInfos = (List<DPSopWeek0ParamInfo>) dpSopProcessBO
					.getSopAssetsByFileId(infoObject.getDpSopWeek0ProcessStatusInfo().getId(), DPAConstants.SOP_WEEK0);
			if(failedStepCommands.size() == dpInfos.size()) {
				return DPFileProcessStatus.FAILED.getFileStatus();
			} else {
				return DPFileProcessStatus.PARTIAL.getFileStatus();
			}
		}
	}

	@Override
	public DPSopParamEntryInfo validateFile(MultipartFile file, String generatedFileName, List<String> errorMessages) throws SystemException {
		DPSopParamEntryInfo inputFileEntry = null;

		Long startTime;
		Long endTime;

		if(errorMessages != null && errorMessages.size() == 0) {
			// validation logic for file format
			InputFileValidationUtil.validateXLSFileName(FilenameUtils.getExtension(file.getOriginalFilename()));
			DataFormatter df = new DataFormatter();

			// Fetch content of file.
			Sheet sheet = null;
			try {
				startTime = DateTime.now().getMillis();
				sheet = InputFileValidationUtil.getFileContent(file);
				endTime = DateTime.now().getMillis() - startTime;
				log.info("TIme taken for getFilecontent : " + endTime);
			} catch (IOException ioe) {
				log.error("Exception while reading excel file content: {}", ioe);
				throw new SystemException(CoreExceptionCodes.DP019, new Object[] {});
			}

			if(sheet != null) {
				// validate header of excel file.
				List<String> headerColumns = InputFileValidationUtil.extractHeader(df, sheet);

				InputFileValidationUtil.validateSOPWeek0FileHeader(headerColumns);

				startTime = DateTime.now().getMillis();
				inputFileEntry = populateFields(df, sheet);
				endTime = DateTime.now().getMillis() - startTime;
				log.info("TIme taken for populateFields : " + endTime);

				if(Objects.isNull(inputFileEntry) || Objects.isNull(inputFileEntry.getColumnEntries())
						|| inputFileEntry.getColumnEntries().size() == 0) {
					errorMessages.add(MessageContainer.getMessage(CoreExceptionCodes.DP020, new Object[] {}));
				}

				startTime = DateTime.now().getMillis();
				Set<ConstraintViolation<DPSopParamEntryInfo>> constraintViolations = validator.validate(inputFileEntry);
				endTime = DateTime.now().getMillis() - startTime;
				log.info("TIme taken for validator : " + endTime);

				log.info("Error voilation size : " + constraintViolations.size());
				if(constraintViolations.size() > 0) {
					for (ConstraintViolation<DPSopParamEntryInfo> errors : constraintViolations) {
						List<Node> nodeList = IteratorUtils.toList(errors.getPropertyPath().iterator());
						int index = nodeList.get(1).getIndex();

						errorMessages.add(MessageFormat
								.format(errors.getMessage(), errors.getRootBean().getColumnEntries().get(index).getAssetNumber(), index + 1));
					}
					inputFileEntry.setDataLevelError(true);
				} else {
					List<String> assetNumbers = new ArrayList<>();
					inputFileEntry.getColumnEntries().forEach(paramInfo -> {
						paramInfo.setOldAssetNumber(paramInfo.getOldAssetNumber());
						paramInfo.setAssetNumber(paramInfo.getAssetNumber());
						paramInfo.setPropTemp(paramInfo.getPropTemp());
					});

					for (DPSopWeek0ParamInfo info : inputFileEntry.getColumnEntries()) {
						assetNumbers.add(info.getAssetNumber());
					}
					InputFileValidationUtil.validateAssetNumbers(assetNumbers);
					DPSopWeek0ProcessStatus processStatus = new DPSopWeek0ProcessStatus();
					processStatus.setInputFileName(file.getOriginalFilename());
					processStatus.setSysGnrtdInputFileName(generatedFileName);
					if(errorMessages.size() > 0) {
						processStatus.setStatus(DPFileProcessStatus.ERROR.getFileStatus());
					} else {
						processStatus.setStatus(DPFileProcessStatus.UPLOADED.getFileStatus());
					}
					DPSopWeek0ProcessStatusInfo processStatusInfo = dpSopWeek0ProcessStatusMapper.mapDomainToInfo(processStatus);
					List<DPSopWeek0ParamInfo> entries = inputFileEntry.getColumnEntries();
					for (DPSopWeek0ParamInfo dpInfo : entries) {
						DPSopWeek0Param dpParam = new DPSopWeek0Param();
						dpParam = dpSopWeek0ParamMapper.mapInfoToDomain(dpInfo);
						dpParam.setSopWeek0ProcessStatus(processStatus);
						dpParam.setAssignmentDate(DateConversionUtil.getCurrentUTCTime().getMillis());
						dpInfo.setSopWeek0ProcessStatus(processStatusInfo);
					}
					inputFileEntry.setDpSopWeek0ProcessStatusInfo(processStatusInfo);
				}
			}
		}
		return inputFileEntry;
	}

	private DPSopWeekNParamEntryInfo populateSOPWeekNFields(DataFormatter df, Sheet sheet) {
		Row currentRow;
		DPSopWeekNParamEntryInfo inputFileEntry = new DPSopWeekNParamEntryInfo();
		List<DPSopWeekNParamInfo> list = new ArrayList<>();

		// validate each row of excel file.
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			currentRow = sheet.getRow(i);

			if(null == currentRow)
				continue;
			log.info("DPSopWeekNParamInfo asset number : " + df.formatCellValue(currentRow.getCell(0)));

			DPSopWeekNParamInfo column = new DPSopWeekNParamInfo();
			column.setAssetNumber(InputFileValidationUtil.getCellValue(currentRow, df, 0));
			column.setState(InputFileValidationUtil.getCellValue(currentRow, df, 1));
			/*column.setPropertyType(InputFileValidationUtil.getCellValue(currentRow, df, 2));
			column.setStatus(InputFileValidationUtil.getCellValue(currentRow, df, 3));
			column.setAssetValue(InputFileValidationUtil.getNumericCellValue(currentRow, df, 4));
			column.setAvSetDate(df.formatCellValue(currentRow.getCell(5)));
			column.setReoDate(df.formatCellValue(currentRow.getCell(6)));
			column.setListPrice(InputFileValidationUtil.getCellValue(currentRow, df, 7));*/
			column.setClassification(InputFileValidationUtil.getCellValue(currentRow, df, 8));

			/*if (StringUtils.isBlank(column.getAssetNumber()) && StringUtils.isBlank(column.getStatus()) && (column.getAssetValue() == null)
					&& StringUtils.isBlank(column.getAvSetDate()) && StringUtils.isBlank(column.getListPrice()) && StringUtils
					.isBlank(column.getClassification())) {
				continue;
			}*/

			list.add(column);
		}

		log.info("DPProcessParamInfo list collected : " + list.size());

		inputFileEntry.setColumnEntries(list);
		inputFileEntry.setColumnCount(list.size());
		return inputFileEntry;
	}

	@Override
	public void checkForFileStatus(String fileStatus) throws BusinessException, SystemException {
		List<DPSopWeekNProcessStatusInfo> sopWeek0ProcessStatus = null;
		try {
			sopWeek0ProcessStatus = dpSopProcessBO.findSopWeekNFileStatus(fileStatus);
		} catch (SystemException se) {
			log.error("Problem in reading sop weekn file status.", se);
			throw se;
		}
		if(CollectionUtils.isNotEmpty(sopWeek0ProcessStatus)) {
			BusinessException.newBusinessException(CoreExceptionCodes.DP017);
		}
	}

	@Override
	public void saveSopWeekNProcess(DPSopWeekNParamEntryInfo dpSopWeekNParamEntryInfo) throws BusinessException, SystemException {
		dpSopWeekNParamEntryInfo.getDpSopWeekNProcessStatus().setStatus(DPFileProcessStatus.UPLOADED.getFileStatus());
		DPSopWeekNProcessStatusInfo sopProcessStatusData = null;
		try {
			sopProcessStatusData = dpSopProcessBO.saveSopWeekNProcessData(dpSopWeekNParamEntryInfo.getDpSopWeekNProcessStatus());
		} catch (SystemException e) {
			log.error("Sop weekn process saving failed.", e);
			throw e;
		}
		dpSopWeekNParamEntryInfo.setDpSopWeekNProcessStatus(sopProcessStatusData);
		if(dpSopWeekNParamEntryInfo.getColumnEntries() != null) {
			DPSopWeekNProcessStatusInfo finalSopProcessStatusData = sopProcessStatusData;
			dpSopWeekNParamEntryInfo.getColumnEntries().stream().forEach(entry -> entry.setSopWeekNProcessStatus(finalSopProcessStatusData));
		}
		List<DPSopWeekNParamInfo> columnEntries = null;
		try {
			columnEntries = dpSopProcessBO.saveSopWeekNParams(dpSopWeekNParamEntryInfo.getColumnEntries());
		} catch (SystemException e) {
			log.error("Sop weekn process paras saving failed.", e);
			throw e;
		}
		dpSopWeekNParamEntryInfo.setColumnEntries(columnEntries);
	}

	@Override
	public List<DPSopWeekNParamInfo> findSopWeekNParamsData(String fileId) throws BusinessException, SystemException {
		List<DPSopWeekNParamInfo> sopWeekNParams = null;
		try {
			sopWeekNParams = dpSopProcessBO.findSopWeekNParamsData(fileId);
		} catch (SystemException e) {
			log.error("Problem in fetching sop weekn param data.", e);
			throw e;
		}
		return sopWeekNParams;
	}

	@Override
	public List<DPSopWeekNParamInfo> getSOPWeekNParams(MultipartFile file) throws BusinessException {
		if(file.isEmpty()) {
			BusinessException.newBusinessException(CoreExceptionCodes.DPWKN0111);
		}
		List<DPSopWeekNParamInfo> dpProcessWeekNParamInfos = null;
		log.info("Validating file name of uploaded file");
		try {
			InputFileValidationUtil.validateXLSFileName(FilenameUtils.getExtension(file.getOriginalFilename()));
		} catch (SystemException e) {
			log.error("Validation failure : {}", e);
			BusinessException.newBusinessException(e.getCode());
		}

		// Fetch Potential sheet from the workbook
		log.info("Fetching Potential sheet from the uploaded file");
		try {
			dpProcessWeekNParamInfos = dpSopProcessBO.fetchSopWeekNDataBySheet(file);
		} catch (SystemException e) {
			log.error("sop retieval failure : {}", e);
			BusinessException.newBusinessException(e.getCode());
		}
		return dpProcessWeekNParamInfos;
	}

	@Override
	public List<DPSopWeekNParamInfo> uploadSopWeekNExcel(String originalFilename, List<DPSopWeekNParamInfo> listOfDPProcessWeekNParamInfos)
			throws BusinessException {
		if(listOfDPProcessWeekNParamInfos.isEmpty()) {
			BusinessException.newBusinessException(CoreExceptionCodes.DPWKN0109);
		}
		DPSopWeekNProcessStatusInfo sopWeekNProcessStatusInfo = new DPSopWeekNProcessStatusInfo();
		sopWeekNProcessStatusInfo.setStatus(DPFileProcessStatus.UPLOADED.getFileStatus());
		sopWeekNProcessStatusInfo.setInputFileName(originalFilename);
		sopWeekNProcessStatusInfo.setSysGnrtdInputFileName(DPWeekNBOUtil.generateWeekNFileName(originalFilename));

		final Map<String, String> migrationNewPropToPropMap = new HashMap<>();
		final Map<String, String> migrationPropToLoanMap = new HashMap<>();

		String rrMigrationLoanNumQuery = (String) cacheManager.getAppParamValue(AppParameterConstant.RR_MIGRATION_LOAN_NUM_QUERY);
		List<String> weekNAssets = listOfDPProcessWeekNParamInfos.stream().map(DPSopWeekNParamInfo::getAssetNumber).collect(Collectors.toList());
		try {
			List<String> weekNPropTemps = rrMigration.getPropTemps(rrMigrationLoanNumQuery, weekNAssets);
			rrMigration.getMigrationMaps(migrationNewPropToPropMap, migrationPropToLoanMap, weekNPropTemps);
		} catch (Exception e) {
			log.error("Problem in calling RR migration.", e);
			BusinessException.newBusinessException(CoreExceptionCodes.DP035);
		}
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
			item.setPropTemp(migrationLoanToPropMap.get(item.getAssetNumber()));
			item.setSopWeekNProcessStatus(sopWeekNProcessStatusInfo);
		});

		return listOfDPProcessWeekNParamInfos;
	}

	@Override
	public void updateSopWeeknRunningStatus(DPSopWeekNProcessStatusInfo dpSopWeekNProcessStatus, String fileStatus)
			throws BusinessException, SystemException {
		try {
			dpSopProcessBO.updateSopWeeknRunningStatus(dpSopWeekNProcessStatus.getId(), fileStatus);
		} catch (SystemException se) {
			log.error("sop weekn process status change failure.", se);
			throw se;
		}
	}

	@Override
	public void sopWeekNProcessCommand(DPSopWeekNParamEntryInfo dpSopWeekNParamEntryInfo, String id) throws SystemException{
		final Map<String, String> mdcContext = MDC.getCopyOfContextMap();
		DelegatingSecurityContextExecutor executor = new DelegatingSecurityContextExecutor(new SimpleAsyncTaskExecutor(),
				SecurityContextHolder.getContext());
		final DPSopWeekNParamEntryInfo finalDpSopWeekNParamEntryInfo = dpSopWeekNParamEntryInfo;
		executor.execute(() -> {
			MDC.setContextMap(mdcContext);
			try {
				commandMaster.prepareSopWeekN(finalDpSopWeekNParamEntryInfo);
				dpSopWeekNParamDelegate.downloadSopWeekNZip(id, finalDpSopWeekNParamEntryInfo, null, null);
			} catch (SystemException | BusinessException e) {
				log.error("Problem in executing sop weekn process. {}", e);
			}
		});
	}

	/**
	 * @param df
	 * @param sheet
	 *
	 * @return
	 */
	private DPSopParamEntryInfo populateFields(DataFormatter df, Sheet sheet) {
		Row currentRow;
		DPSopParamEntryInfo inputFileEntry = new DPSopParamEntryInfo();
		List<DPSopWeek0ParamInfo> list = new ArrayList<>();

		// validate each row of excel file.
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			currentRow = sheet.getRow(i);

			if(null == currentRow)
				continue;
			log.info("DPSopWeek0ParamInfo asset number : " + df.formatCellValue(currentRow.getCell(0)));

			DPSopWeek0ParamInfo column = new DPSopWeek0ParamInfo();
			column.setAssetNumber(InputFileValidationUtil.getCellValue(currentRow, df, 0));
			column.setState(InputFileValidationUtil.getCellValue(currentRow, df, 1));
			column.setPropertyType(InputFileValidationUtil.getCellValue(currentRow, df, 2));
			column.setStatus(InputFileValidationUtil.getCellValue(currentRow, df, 3));
			column.setAssetValue(InputFileValidationUtil.getNumericCellValue(currentRow, df, 4));
			column.setAvSetDate(df.formatCellValue(currentRow.getCell(5)));
			column.setReoDate(df.formatCellValue(currentRow.getCell(6)));
			column.setListPrice(InputFileValidationUtil.getCellValue(currentRow, df, 7));
			column.setClassification(InputFileValidationUtil.getCellValue(currentRow, df, 8));

			if(StringUtils.isBlank(column.getAssetNumber()) && StringUtils.isBlank(column.getStatus()) && StringUtils
					.equalsIgnoreCase(column.getAssetValue().toString(), "0.0") && StringUtils.isBlank(column.getAvSetDate()) && StringUtils
					.isBlank(column.getListPrice()) && StringUtils.isBlank(column.getClassification())) {
				continue;
			}

			list.add(column);
		}

		log.info("DPProcessParamInfo list collected : " + list.size());

		inputFileEntry.setColumnEntries(list);
		inputFileEntry.setColumnCount(list.size());
		return inputFileEntry;
	}

}
