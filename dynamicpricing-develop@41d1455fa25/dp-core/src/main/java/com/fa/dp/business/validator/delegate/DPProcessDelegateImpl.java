package com.fa.dp.business.validator.delegate;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolation;
import javax.validation.Path.Node;
import javax.validation.Validator;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fa.dp.business.util.DPFileProcessStatus;
import com.fa.dp.business.util.DPFileProcesses;
import com.fa.dp.business.validation.file.header.constant.DPProcessFileHeader;
import com.fa.dp.business.validation.file.util.InputFileValidationUtil;
import com.fa.dp.business.validation.input.info.DPFileProcessStatusInfo;
import com.fa.dp.business.validation.input.info.DPProcessParamEntryInfo;
import com.fa.dp.business.validation.input.info.DPProcessParamInfo;
import com.fa.dp.business.validator.bo.DPFileProcessBO;
import com.fa.dp.business.week0.entity.DPProcessParam;
import com.fa.dp.business.week0.entity.DynamicPricingFilePrcsStatus;
import com.fa.dp.business.week0.info.DPWeek0ToInfoMapper;
import com.fa.dp.business.weekn.dao.DPProcessWeekNParamsDao;
import com.fa.dp.business.weekn.dao.DPWeekNProcessStatusRepo;
import com.fa.dp.business.weekn.entity.DPProcessWeekNParam;
import com.fa.dp.business.weekn.entity.DPWeekNProcessStatus;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamEntryInfo;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamInfo;
import com.fa.dp.business.weekn.input.info.DPWeekNProcessStatusInfo;
import com.fa.dp.business.weekn.input.info.DPWeekNToInfoMapper;
import com.fa.dp.core.base.delegate.AbstractDelegate;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.exception.codes.CoreExceptionCodes;
import com.fa.dp.core.systemparam.provider.SystemParameterProvider;
import com.fa.dp.core.systemparam.util.SystemParameterConstant;
import com.fa.dp.core.util.DateConversionUtil;
import com.fa.dp.core.util.RAClientConstants;
import com.fa.dp.localization.MessageContainer;

/**
 * @author misprakh
 * 
 *         Class for validation and file upload service
 *
 */
@Named
public class DPProcessDelegateImpl extends AbstractDelegate implements DPProcessDelegate {

	/**
	 *  Error message constants
	 */
	private static final String DP017 = "DP017";
	private static final String DPWN001 = "DPWN001";
	private static final String DPWN002 = "DPWN002";
	private static final String DPWN003 = "DPWN003";

	private static final String XLS_EXTENSION = ".xls";

	private static final Logger LOGGER = LoggerFactory.getLogger(DPProcessDelegateImpl.class);

	@Inject
	private Validator validator;
	
	@Inject
	private DPWeekNProcessStatusRepo dpWeekNProcessStatusRepo;

	@Inject
	private DPFileProcessBO dpFileProcessBO;

	@Inject
	private SystemParameterProvider systemParameterProvider;

	@Inject
	private DPWeek0ToInfoMapper dpWeekToInfoMapper;
	
	@Inject
    private DPWeekNToInfoMapper dpWeekNToInfoMapper;
	
	@Inject
	private DPProcessWeekNParamsDao dpProcessWeekNParamsDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fa.ra.client.core.validator.delegate.DPProcessDelegate#validateFile(
	 * org. springframework.web.multipart.MultipartFile, java.lang.String)
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
	public DPProcessParamEntryInfo validateFile(MultipartFile file, String generatedFileName,
			List<String> errorMessages) throws SystemException, IOException {

		DPProcessParamEntryInfo inputFileEntry = null;

		Long startTime;
		Long endTime;

		/*DynamicPricingFilePrcsStatus dpFilePrcsStatus = dpFileProcessBO
				.findDPProcessStatusByStatus(DPFileProcessStatus.IN_PROCESS.getFileStatus());
		if (!Objects.isNull(dpFilePrcsStatus)) {
			errorMessages.add(MessageContainer.getMessage(DP017, new Object[] {}));
		}*/

		if (errorMessages != null && errorMessages.size() == 0) {
			// validation logic for file format
			InputFileValidationUtil.validateXLSFileName(FilenameUtils.getExtension(file.getOriginalFilename()));
			DataFormatter df = new DataFormatter();

			// Fetch content of file.
			Sheet datatypeSheet = null;
			try {
				startTime = DateTime.now().getMillis();
				datatypeSheet = getFileContent(file);
				endTime = DateTime.now().getMillis()-startTime;
				LOGGER.info("TIme taken for getFilecontent : "+endTime);
			} catch (IOException ioe) {
				LOGGER.error(ioe.getLocalizedMessage(), ioe);
				throw ioe;
			}

			if (datatypeSheet != null) {
				// Iterator<Row> iterator = datatypeSheet.iterator();

				// validate header of excel file.
				List<String> headerColumns = extractHeader(df, datatypeSheet);

				InputFileValidationUtil.validateHeaderColumns(headerColumns);

				startTime = DateTime.now().getMillis();
				inputFileEntry = populateFields(df, datatypeSheet);
				endTime = DateTime.now().getMillis()-startTime;
				LOGGER.info("TIme taken for populateFields : "+endTime);

				if (Objects.isNull(inputFileEntry) || Objects.isNull(inputFileEntry.getColumnEntries())
						|| inputFileEntry.getColumnEntries().size() == 0) {
					errorMessages.add(MessageContainer.getMessage(CoreExceptionCodes.DP020, new Object[] {}));
				}

				startTime = DateTime.now().getMillis();
				Set<ConstraintViolation<DPProcessParamEntryInfo>> constraintViolations = validator
						.validate(inputFileEntry);
				endTime = DateTime.now().getMillis()-startTime;
				LOGGER.info("TIme taken for validator : "+endTime);

				LOGGER.info("Error voilation size : " + constraintViolations.size());
				if (constraintViolations.size() > 0) {
					for (ConstraintViolation<DPProcessParamEntryInfo> errors : constraintViolations) {
						List<Node> nodeList = IteratorUtils.toList(errors.getPropertyPath().iterator());
						int index = nodeList.get(1).getIndex();

						errorMessages.add(MessageFormat.format(errors.getMessage(),
								errors.getRootBean().getColumnEntries().get(index).getAssetNumber(), index + 1));
					}
					inputFileEntry.setDataLevelError(true);
				} else {
					List<String> assetNumbers = new ArrayList<>();
					inputFileEntry.getColumnEntries().forEach(paramInfo ->
					{
						paramInfo.setOldAssetNumber(paramInfo.getOldAssetNumber());
						paramInfo.setAssetNumber(paramInfo.getAssetNumber());
						paramInfo.setPropTemp(paramInfo.getPropTemp());
					});

					for(DPProcessParamInfo info: inputFileEntry.getColumnEntries()) {
						assetNumbers.add(info.getAssetNumber());
					}
					InputFileValidationUtil.validateAssetNumbers(assetNumbers);
					DynamicPricingFilePrcsStatus processStatus = new DynamicPricingFilePrcsStatus();
					processStatus.setInputFileName(file.getOriginalFilename());
					processStatus.setSysGnrtdInputFileName(generatedFileName);
					processStatus.setOcnOutputFileName(null);
					processStatus.setNrzOutputFileName(null);
					processStatus.setPhhOutputFileName(null);
					if(errorMessages.size() > 0) {
						processStatus.setStatus(DPFileProcessStatus.ERROR.getFileStatus());
					} else {
						processStatus.setStatus(DPFileProcessStatus.UPLOADED.getFileStatus());
					}
					processStatus.setProcess(DPFileProcesses.VACANT_WEEK0.getProcess());
					processStatus.setUploadTimestamp(DateConversionUtil.getCurrentEstDate().getMillis());
					DPFileProcessStatusInfo processStatusInfo =  dpWeekToInfoMapper.dpFileProcessStatusToInfo(processStatus);
					List<DPProcessParamInfo> entries = inputFileEntry.getColumnEntries();
					for (DPProcessParamInfo dpInfo : entries) {
						DPProcessParam dpParam = new DPProcessParam();
						dpParam = convert(dpInfo, DPProcessParam.class);
						dpParam.setDynamicPricingFilePrcsStatus(processStatus);
						dpParam.setAssignmentDate(DateConversionUtil.getCurrentUTCTime().getMillis());
						dpInfo.setDynamicPricingFilePrcsStatus(processStatusInfo);
					}
					inputFileEntry.setDPFileProcessStatusInfo(processStatusInfo);
				}
			}
		}

		return inputFileEntry;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fa.ra.client.core.validator.delegate.DPProcessDelegate#createFile(
	 * org. springframework.web.multipart.MultipartFile, java.lang.String)
	 */
	@Override
	public void createFile(MultipartFile file, String generatedFileName) throws IOException {
		// Creating the directory to store file String rootPath =

		// Create the file on server File serverFile = new
		// File(dir.getAbsolutePath()
		byte[] bytes;
		String fileName = systemParameterProvider.getSystemParamValue(SystemParameterConstant.SYS_PARAM_SAN_PATH)
				+ File.separator + generatedFileName;
		File serverFile = new File(fileName);
		try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));) {
			bytes = file.getBytes();
			stream.write(bytes);
		} catch (IOException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			throw e;
		}

		LOGGER.info("Input File Location=" + fileName);
	}

	/**
	 * @param df
	 * @param datatypeSheet
	 * @return
	 */
	private List<String> extractHeader(DataFormatter df, Sheet datatypeSheet) {
		List<String> headerColumns = new ArrayList<String>();
		Row currentRow = datatypeSheet.getRow(0);
		for (int i = 0; i < currentRow.getPhysicalNumberOfCells(); i++) {
			Cell cell = currentRow.getCell(i);
			
			if(null != cell) {
				switch (cell.getCellTypeEnum()) {
				case FORMULA:
					switch (cell.getCachedFormulaResultTypeEnum()) {
					case NUMERIC:
						headerColumns.add(String.valueOf(cell.getNumericCellValue()));
					case STRING:
					default:
						headerColumns.add(cell.getRichStringCellValue().toString());
					}
					break;
				case NUMERIC:
				case STRING:
				default:
					headerColumns.add(df.formatCellValue(cell));
				}
			} else {
				headerColumns.add(RAClientConstants.CHAR_EMPTY);
			}

			
		}
		return headerColumns;
	}

	/**
	 * @param df
	 * @param datatypeSheet
	 * @return
	 */
	private DPProcessParamEntryInfo populateFields(DataFormatter df, Sheet datatypeSheet) {
		Row currentRow;
		DPProcessParamEntryInfo inputFileEntry = new DPProcessParamEntryInfo();
		List<DPProcessParamInfo> list = new ArrayList<>();

		// validate each row of excel file.
		for (int i = 1; i <= datatypeSheet.getLastRowNum(); i++) {
			currentRow = datatypeSheet.getRow(i);

			if(null == currentRow)
				continue;
			LOGGER.info("DPProcessParamInfo asset number : " + df.formatCellValue(currentRow.getCell(0)));

			DPProcessParamInfo column = new DPProcessParamInfo();
			column.setAssetNumber(getCellValue(currentRow, df, 0));
			column.setClientCode(getCellValue(currentRow, df, 1));
			column.setStatus(getCellValue(currentRow, df, 2));
			column.setAssetValue(getCellValue(currentRow, df, 3));
			column.setAvSetDate(df.formatCellValue(currentRow.getCell(4)));
			column.setListPrice(getCellValue(currentRow, df, 5));
			column.setClassification(getCellValue(currentRow, df, 6));

			if (StringUtils.isBlank(column.getAssetNumber()) && StringUtils.isBlank(column.getClientCode())
					&& StringUtils.isBlank(column.getStatus()) && StringUtils.isBlank(column.getAssetValue())
					&& StringUtils.isBlank(column.getAvSetDate()) && StringUtils.isBlank(column.getListPrice())
					&& StringUtils.isBlank(column.getClassification())) {
				continue;
			}

			list.add(column);
		}

		LOGGER.info("DPProcessParamInfo list collected : " + list.size());

		inputFileEntry.setColumnEntries(list);
		inputFileEntry.setColumnCount(list.size());
		return inputFileEntry;
	}

	private String getCellValue(Row currentRow, DataFormatter df, int index) {
		LOGGER.debug("getCellValue() current row : " + currentRow);
		Cell cell = currentRow.getCell(index);
		LOGGER.debug("getCellValue() cell : " + cell);
		String result = null;
		if(null != cell) {
			cell.setCellType(CellType.STRING);
			result = df.formatCellValue(cell);
		}
		return result;
	}

	/**
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private Sheet getFileContent(MultipartFile file) throws IOException {
		Workbook workbook = null;
		Sheet datatypeSheet = null;
		try {
			workbook = new XSSFWorkbook(file.getInputStream());
			datatypeSheet = workbook.getSheetAt(0);
		} catch (IOException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			throw e;
		} finally {
			IOUtils.closeQuietly(workbook);
		}
		return datatypeSheet;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fa.ra.client.core.validator.delegate.DPProcessDelegate#
	 * generateFileName( java.lang.String)
	 */
	@Override
	public String generateFileName(String fileName) {
		DateFormat format = new SimpleDateFormat("yyyyMMdd-HHmmss");
		return FilenameUtils.getBaseName(fileName) + RAClientConstants.CHAR_UNDER_SCORE + format.format(new Date())
				+ XLS_EXTENSION;
	}
	
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
	public DPProcessWeekNParamEntryInfo validateWeeknFile(MultipartFile file, String generatedFileName,
			List<String> errorMessages) throws SystemException, IOException {
		List<String> columnEntries = new ArrayList<>();
		DPProcessWeekNParamEntryInfo inputFileEntry = null;
		
		DynamicPricingFilePrcsStatus dpFilePrcsStatus = dpFileProcessBO
				.findDPProcessStatusByStatus(DPFileProcessStatus.IN_PROGRESS.getFileStatus());
		if (!Objects.isNull(dpFilePrcsStatus)) {
			errorMessages.add(MessageContainer.getMessage(DP017, new Object[] {}));
		}
		
		if (errorMessages != null && errorMessages.size() == 0) {

			// validation logic for file format
			InputFileValidationUtil.validateXLSFileName(FilenameUtils.getExtension(file.getOriginalFilename()));
			DataFormatter df = new DataFormatter();

			// Fetch content of file.
			Sheet inputDataSheet = null;
			try {
				inputDataSheet = getFileContent(file);
			} catch (IOException ioe) {
				LOGGER.error(ioe.getLocalizedMessage(), ioe);
				throw ioe;
			}
			if (inputDataSheet != null) {
				LOGGER.info("Validating the columns in WeekN input sheet");
				Row headerRow = inputDataSheet.getRow(0);
				if (headerRow.getPhysicalNumberOfCells() == 1) {
					if (inputDataSheet.getPhysicalNumberOfRows() >= 2) {
						if (DPProcessFileHeader.HEADER1.getValue().equals(df.formatCellValue(headerRow.getCell(0)))) {
							for (int rowIndex = 1; rowIndex <= inputDataSheet.getLastRowNum(); rowIndex++) {
								Row currentRow = inputDataSheet.getRow(rowIndex);
								if (StringUtils.isBlank(df.formatCellValue(currentRow.getCell(0))))
									continue;
								columnEntries.add(df.formatCellValue(currentRow.getCell(0)));
							}
						} else {
							errorMessages.add(MessageContainer.getMessage(DPWN001, new Object[] {}));
						}
					} else {
						errorMessages.add(MessageContainer.getMessage(DPWN002, new Object[] {}));
					}
				} else {
					errorMessages.add(MessageContainer.getMessage(DPWN003, new Object[] {}));
				}
			}
			if (columnEntries.size() == 0) {
				errorMessages.add(MessageContainer.getMessage(CoreExceptionCodes.DP020, new Object[] {}));
			}
			//Validate if input sheet has duplicate assetNumbers
			InputFileValidationUtil.validateAssetNumbers(columnEntries);
			
			//Save the input file in db
			DynamicPricingFilePrcsStatus processStatus = new DynamicPricingFilePrcsStatus();
			processStatus.setInputFileName(file.getOriginalFilename());
			processStatus.setSysGnrtdInputFileName(generatedFileName);
			processStatus.setOcnOutputFileName(null);
			processStatus.setNrzOutputFileName(null);
			processStatus.setPhhOutputFileName(null);
			if(errorMessages.size() > 0) {
				processStatus.setStatus(DPFileProcessStatus.ERROR.getFileStatus());
			} else {
				processStatus.setStatus(DPFileProcessStatus.UPLOADED.getFileStatus());
			}
			processStatus.setProcess(DPFileProcesses.VACANT_WEEKN.getProcess());
			processStatus.setUploadTimestamp(DateConversionUtil.convertToUtcForAuditable(null));

			processStatus = dpFileProcessBO.saveDPProcessStatus(processStatus);

			DPWeekNProcessStatusInfo processStatusInfo = convert(processStatus, DPWeekNProcessStatusInfo.class);

			if (processStatus.getId() != null) {
				inputFileEntry = new DPProcessWeekNParamEntryInfo();
				inputFileEntry.setDpWeeknProcessStatus(processStatusInfo);
			}
			
			List<DPProcessWeekNParamInfo> dpProcessWeekNParams = new ArrayList<>();
			for(String assetNumber : columnEntries) {
				DPProcessWeekNParamInfo dpProcessWeeknParamInfo = new DPProcessWeekNParamInfo();
				dpProcessWeeknParamInfo.setAssetNumber(assetNumber);
				dpProcessWeeknParamInfo.setDpWeekNProcessStatus(processStatusInfo);
				dpProcessWeekNParams.add(dpProcessWeeknParamInfo);
			}
			inputFileEntry.setColumnEntries(dpProcessWeekNParams);
		}
		return inputFileEntry;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
	public void saveDpPrcsStatus(DPFileProcessStatusInfo dpFilePrcsStatusInfo) {
		DynamicPricingFilePrcsStatus processStatus = convert(dpFilePrcsStatusInfo, DynamicPricingFilePrcsStatus.class);
		//DynamicPricingFilePrcsStatus processStatus = dpWeekToInfoMapper.dpFileProcessStsInfoToStatus(dpFilePrcsStatusInfo);
		if(null != processStatus && null != processStatus.getId() && !processStatus.getId().isEmpty()) {
			dpFileProcessBO.saveDPProcessStatus(processStatus);
		}
	}

	@Override
	public DynamicPricingFilePrcsStatus checkForPrcsStatus(String fileStatus) {
		DynamicPricingFilePrcsStatus dpFilePrcsStatus = dpFileProcessBO
				.findDPProcessStatusByStatus(fileStatus);
		return dpFilePrcsStatus;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
	public void saveFileEntriesInDB(DPProcessParamEntryInfo dpParamEntry) {
		DynamicPricingFilePrcsStatus processStatus = convert(dpParamEntry.getDPFileProcessStatusInfo(), DynamicPricingFilePrcsStatus.class);
		processStatus = dpFileProcessBO.saveDPProcessStatus(processStatus);
		DPFileProcessStatusInfo processStatusInfo =  dpWeekToInfoMapper.dpFileProcessStatusToInfo(processStatus);
		List<DPProcessParamInfo> entries = dpParamEntry.getColumnEntries();
		for (DPProcessParamInfo dpInfo : entries) {
			DPProcessParam dpParam = new DPProcessParam();
			dpParam = convert(dpInfo, DPProcessParam.class);
			dpParam.setDynamicPricingFilePrcsStatus(processStatus);
			dpParam = dpFileProcessBO.saveDPProcessParam(dpParam);
			dpInfo.setId(dpParam.getId());
			dpInfo.setDynamicPricingFilePrcsStatus(processStatusInfo);
		}
		dpParamEntry.setDPFileProcessStatusInfo(processStatusInfo);
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
	public void saveWeekNFileEntriesInDB(DPProcessWeekNParamEntryInfo dpWeeknParamEntry) {
		DPWeekNProcessStatus dpWeekNProcessStatus = dpWeekNProcessStatusRepo.save(convert(dpWeeknParamEntry.getDpWeeknProcessStatus(), DPWeekNProcessStatus.class));
		dpWeeknParamEntry.setDpWeeknProcessStatus(dpWeekNToInfoMapper.map(dpWeekNProcessStatus));
		List<DPProcessWeekNParamInfo> columnEntries = new ArrayList<>(dpWeeknParamEntry.getColumnEntries().size());
		dpWeeknParamEntry.getColumnEntries().forEach(item->{
			DPProcessWeekNParam dpProcessWeekNParam = convert(item, DPProcessWeekNParam.class);
			dpProcessWeekNParam.setDpWeekNProcessStatus(dpWeekNProcessStatus);
			dpProcessWeekNParam = dpProcessWeekNParamsDao.save(dpProcessWeekNParam);
			columnEntries.add(dpWeekNToInfoMapper.dpWeekNToInfoMapper(dpProcessWeekNParam));
		});
		dpWeeknParamEntry.setColumnEntries(columnEntries);
	}
}
