package com.fa.dp.business.sop.weekN.bo;

import static java.util.stream.Collectors.toList;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;

import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.constant.DPProcessParamAttributes;
import com.fa.dp.business.db.client.SopHubzuDBClient;
import com.fa.dp.business.filter.constant.DPProcessFilterParams;
import com.fa.dp.business.info.HubzuDBResponse;
import com.fa.dp.business.info.HubzuInfo;
import com.fa.dp.business.pmi.entity.PmiInsuranceCompany;
import com.fa.dp.business.sop.week0.entity.DPSopWeek0Param;
import com.fa.dp.business.sop.weekN.dao.DPSopWeekNFilterDao;
import com.fa.dp.business.sop.weekN.dao.DPSopWeekNParamDao;
import com.fa.dp.business.sop.weekN.entity.DPSopWeekNParam;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamEntryInfo;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamInfo;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNProcessStatusInfo;
import com.fa.dp.business.sop.weekN.mapper.DPSopWeekNParamMapper;
import com.fa.dp.business.sop.weekN.util.SopWeekNFileUtil;
import com.fa.dp.business.ssinvestor.bo.SpclServicingInvestorBO;
import com.fa.dp.business.util.IntegrationType;
import com.fa.dp.business.week0.info.DashboardFilterInfo;
import com.fa.dp.core.cache.CacheManager;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.exception.codes.CoreExceptionCodes;
import com.fa.dp.core.systemparam.provider.SystemParameterProvider;
import com.fa.dp.core.systemparam.util.AppParameterConstant;
import com.fa.dp.core.systemparam.util.SystemParameterConstant;
import com.fa.dp.core.util.DateConversionUtil;
import com.fa.dp.core.util.RAClientConstants;

@Named
@Slf4j
public class DPSopWeekNParamBOImpl implements DPSopWeekNParamBO {
	
	@Value("${weekN.excluded.state}")
	private String[] excludedStates;

	@Inject
	private DPSopWeekNParamDao dpSopWeekNParamDao;

	@Inject
	private SopWeekNFileUtil sopWeekNFileUtil;

	@Inject
	private DPSopWeekNParamMapper sopWeekNParamMapper;

	@Inject
	private CacheManager cacheManager;

	@Inject
	private SopHubzuDBClient sopHubzuDBClient;

	@Inject
	private SystemParameterProvider systemParameterProvider;

	@Inject
	private DPSopWeekNFilterDao dpSopWeekNFilterDao;
	
	@Inject
	private DPSopWeekNFileProcessBO dPSopWeekNFileProcessBO;
	
	@Inject
	private SpclServicingInvestorBO spclServicingInvestorBo;

	/**
	 * Get the most recent list end date from DB
	 *
	 * @return
	 *
	 * @throws SystemException
	 */
	@Override
	public String findMostRecentListEndDate() throws SystemException {
		String dt = null;
		DPSopWeekNParam sopWeekNParam = dpSopWeekNParamDao.findFirstByOrderByMostRecentListEndDateDesc();
		if(Objects.nonNull(sopWeekNParam)) {
			try {
				dt = DateConversionUtil.DATE_YYYY_MMM_DD_FORMATTER
						.format(DateConversionUtil.DATE_YYYY_MM_DD_FORMATTER.parse(sopWeekNParam.getMostRecentListEndDate())).toUpperCase();
			} catch (ParseException e) {
				throw new SystemException(CoreExceptionCodes.DPSOPWKN006, e.getMessage());
			}
		} else
			throw new SystemException(CoreExceptionCodes.DPSOPWKN007);

		return dt;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public List<DPSopWeekNParamInfo> getSOPWeekNData() throws SystemException {
		List<DPSopWeekNParamInfo> listOfDPSOPWeekNProcessParamInfo = new ArrayList<>();
		List allSOPWeekNParams = dpSopWeekNParamDao.findAllDashboardParams();
		if(allSOPWeekNParams.isEmpty()) {
			log.error("No records found for SOP Week N!");
			throw new SystemException(CoreExceptionCodes.DPSOPWKN004, new Object[] {});
		} else {

			allSOPWeekNParams.stream().forEach(obj -> {
				Object[] object = (Object[]) obj;
				DPSopWeekNParamInfo dpSopWeekNParamInfo = new DPSopWeekNParamInfo();
				dpSopWeekNParamInfo.setClassification((String) object[0]);
				DPSopWeekNProcessStatusInfo dpSopWeekNProcessStatusInfo = new DPSopWeekNProcessStatusInfo();
				dpSopWeekNProcessStatusInfo.setInputFileName((String) object[1]);
				dpSopWeekNProcessStatusInfo.setId((String) object[2]);
				dpSopWeekNProcessStatusInfo.setStatus((String) object[3]);
				dpSopWeekNProcessStatusInfo.setLastModifiedDate(DateConversionUtil.getEstDate((Long) object[4]));
				dpSopWeekNParamInfo.setSopWeekNProcessStatus(dpSopWeekNProcessStatusInfo);
				dpSopWeekNParamInfo.setFailedStepCommandName((String) object[5]);
				listOfDPSOPWeekNProcessParamInfo.add(dpSopWeekNParamInfo);
			});
		}
		return listOfDPSOPWeekNProcessParamInfo;
	}

	@Override
	public List<DPSopWeekNParamInfo> searchSopWeekNParamSuccesfulUnderRiview(String assetNumber) throws SystemException {
		List<DPSopWeekNParamInfo> sopWeeknParamInfoList = null;
		try {
			List<DPSopWeekNParam> sopWeeknParamList = dpSopWeekNParamDao.findByAssetNumberAndDeliveryDateNullAndModelVersionNotNull(assetNumber);
			sopWeeknParamInfoList = sopWeekNParamMapper.mapDomainToLinfoList(sopWeeknParamList);
		} catch (Exception e) {
			log.error("searchSopWeekNParamSuccesfulUnderRiview failure.", e);
			SystemException.newSystemException(CoreExceptionCodes.DPWKN1001);
		}
		return sopWeeknParamInfoList;
	}

	@Override
	public void saveSopWeekNParamInfo(DPSopWeekNParamInfo paramEntry) throws SystemException {
		try {
			DPSopWeekNParam sopWeeknData = sopWeekNParamMapper.mapInfoToDomain(paramEntry);
			dpSopWeekNParamDao.save(sopWeeknData);
		} catch (Exception e) {
			log.error("Failed in saving sop weekn paranm.", e);
			SystemException.newSystemException(CoreExceptionCodes.DPWKN1002);
		}
	}

	@Override
	public void saveSopWeekNParamInfoList(List<DPSopWeekNParamInfo> sopWeekNInfoList) throws SystemException {
		try {
			List<DPSopWeekNParam> sopWeeknData = sopWeekNParamMapper.mapInfoToDomainList(sopWeekNInfoList);
			dpSopWeekNParamDao.saveAll(sopWeeknData);
		} catch (Exception e) {
			log.error("Failed in saving sop weekn paranm.", e);
			SystemException.newSystemException(CoreExceptionCodes.DPWKN1002);
		}
	}

	@Override
	public DPSopWeekNParamInfo findSopWeekNParamById(String id) throws SystemException {
		DPSopWeekNParamInfo paramInfo = null;
		try {
			Optional<DPSopWeekNParam> sopWeekNParam = dpSopWeekNParamDao.findById(id);
			paramInfo = sopWeekNParamMapper.mapDomainToInfo(sopWeekNParam.get());
		} catch (Exception e) {
			log.error("Problem in fetching sop weekn param by id.", e);
			SystemException.newSystemException(CoreExceptionCodes.DPWKN1004);
		}
		return paramInfo;
	}

	/**
	 * @param sopWeekNParamEntryInfo
	 * @param userSelectedDate
	 * @param response
	 *
	 * @throws SystemException
	 */
	@Override
	public void createAndDownloadSopWeekNExcel(DPSopWeekNParamEntryInfo sopWeekNParamEntryInfo, Long userSelectedDate, HttpServletResponse response)
			throws SystemException {
		log.info("Creating workbook for sop week n table");

		try (XSSFWorkbook workbook = new XSSFWorkbook()) {
			XSSFSheet processedSheet = workbook.createSheet(DPAConstants.PROCESSED_RECORDS);
			XSSFSheet failedSheet = workbook.createSheet(DPAConstants.FAILED_RECORDS);
			List<DPSopWeekNParamInfo> priorRecommendedEntries = new ArrayList<>();
			List<DPSopWeekNParamInfo> recommendedEntriesForAudit = new ArrayList<>();

			CellStyle style = workbook.createCellStyle();
			Font font = workbook.createFont();
			font.setBold(true);
			style.setFont(font);
			int processedRowNum = 0;
			int failedRowNum = 0;
			int colNum = 0;

			Row processedRow = processedSheet.createRow(processedRowNum++);
			sopWeekNFileUtil.generateHeaderForPotential(style, processedRow, colNum);
			for (int i = 1; i <= processedRow.getPhysicalNumberOfCells(); i++) {
				processedSheet.autoSizeColumn(i);
			}
			Row failedRow = failedSheet.createRow(failedRowNum++);
			sopWeekNFileUtil.generateHeaderForFailedRecords(style, failedRow, colNum);
			for (int i = 1; i <= failedRow.getPhysicalNumberOfCells(); i++) {
				failedSheet.autoSizeColumn(i);
			}
			List<DPSopWeekNParamInfo> recommendedEntries = sopWeekNParamEntryInfo.getColumnEntries().stream()
					.filter(item -> item.getFailedStepCommandName() == null).collect(toList()).stream().filter(item -> {
						List<DPSopWeekNParam> dpProcessWeekNParams = dpSopWeekNParamDao.findByAssetNumberAndDeliveryDateIsNull(item.getAssetNumber());
						if(dpProcessWeekNParams.isEmpty())
							return true;
						else
							return false;
					}).collect(toList());

			List<DPSopWeekNParamInfo> failedEntries = sopWeekNParamEntryInfo.getColumnEntries().stream()
					.filter(item -> item.getFailedStepCommandName() != null).collect(toList());

			List<DPSopWeekNParam> listOfWeekNParams = null;
			log.info("The maximum latest list end date in sop week n is taken and all the properties having this list end date are selected.");
			listOfWeekNParams = dpSopWeekNParamDao.findAssetNumberForProiorRecommendation();

			if(CollectionUtils.isEmpty(listOfWeekNParams)) {
				log.info("Data Not found from sop week n table for Prior Recommendation. ");
			} else {
				// prior recommendation
				Map<String, HubzuInfo> hubzuMap = priorRecommendationHubzuCall(listOfWeekNParams);
				for (DPSopWeekNParam priorRecommendedEntry : listOfWeekNParams) {
					DPSopWeekNParamInfo sopWeekNParamInfo = sopWeekNParamMapper.mapDomainToInfo(priorRecommendedEntry);
					sopWeekNParamInfo.setListEndDateDtNn((hubzuMap.get(priorRecommendedEntry.getPropTemp()) != null) ?
							hubzuMap.get(priorRecommendedEntry.getPropTemp()).getListEndDateDtNn().toString() : null);
					if(StringUtils.isNotEmpty(sopWeekNParamInfo.getListEndDateDtNn()) && diffInDate(sopWeekNParamInfo, priorRecommendedEntry)) {
						// Story 432
						log.info("{} was prior recommended.", priorRecommendedEntry.getAssetNumber());
						priorRecommendedEntry.setIsPriorRecommended(RAClientConstants.YES);
						dpSopWeekNParamDao.save(priorRecommendedEntry);
						//priorRecommendRowNum = dpWeekNBOUtil.prepareWeekNPriorRecommendationOutputData(priorRecommendedSheet, priorRecommendRowNum, colNum, paramObject, param);
						sopWeekNParamInfo.setDeliveryDate(priorRecommendedEntry.getDeliveryDate());
						priorRecommendedEntries.add(sopWeekNParamInfo);

					}
				}
			}
			for (DPSopWeekNParamInfo paramObject : recommendedEntries) {
				colNum = 0;
				processedRowNum = sopWeekNFileUtil.prepareSopWeekNOutputSheet(processedSheet, processedRowNum, colNum, paramObject, userSelectedDate);
				recommendedEntriesForAudit.add(paramObject);
			}
			// commented auditing as per story DP-603 --> end
			for (DPSopWeekNParamInfo paramObject : failedEntries) {
				colNum = 0;
				failedRowNum = sopWeekNFileUtil.prepareSopWeekNOutputSheet(failedSheet, failedRowNum, colNum, paramObject, userSelectedDate);
			}
			if(processedRowNum > 1 || failedRowNum > 1) {
				try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
					workbook.write(baos);
					byte[] outArray = baos.toByteArray();
					response.setContentLength(outArray.length);
					sopWeekNFileUtil.setResponseHeader(response,
							DPAConstants.SOP_WEEKN_FILE_NAME + DateConversionUtil.getEstDate(userSelectedDate).toString("YYYYddMM") + ".xlsx");
					response.getOutputStream().write(outArray);
					response.flushBuffer();
				} catch (IOException e) {
					log.error("Unable to write into  file ", e);
				}
			}
		} catch (Exception e) {
			log.error("Unable create excel documents ", e);
			throw new SystemException(CoreExceptionCodes.DPSOPWKN014, e.getLocalizedMessage());
		} finally {
			try {
				response.getOutputStream().close();
			} catch (IOException e) {
				log.error("Unable to close response stream ", e);
			}
		}

	}

	/**
	 * @param id
	 *
	 * @return
	 *
	 * @throws SystemException
	 */
	@Override
	public List<DPSopWeekNParam> retrieveSopWeekNFilesDetailsById(String id) throws SystemException {
		List<DPSopWeekNParam> listOfSopWeekNParam = dpSopWeekNParamDao.findSopWeekNParamByStatusID(id);
		if(listOfSopWeekNParam.isEmpty()) {
			log.error("Sop week n details are not available for requested Id - {} ", id);
			throw new SystemException(CoreExceptionCodes.DPSOPWKN001);
		}
		return listOfSopWeekNParam;
	}

	@Override
	public boolean findSopWeekNReports(String sysGnrtdInputFileName, String zipFileName, HttpServletResponse response) throws SystemException {
		String storedFilePath = systemParameterProvider.getSystemParamValue(SystemParameterConstant.SYS_PARAM_SAN_PATH) + File.separator
				+ DPAConstants.SOP_WEEKN_PROCESS_REPORT_FOLDER + File.separator + StringUtils.substringBefore(sysGnrtdInputFileName, ".")
				+ DPAConstants.ZIP_EXTENSION;
		if(Files.exists(Paths.get(storedFilePath), LinkOption.NOFOLLOW_LINKS)) {
			byte[] bytes = null;
			File file = new File(storedFilePath);
			try (InputStream inputStream = new FileInputStream(file)) {
				bytes = IOUtils.toByteArray(inputStream);
				response.getOutputStream().write(bytes);
				sopWeekNFileUtil.setResponseHeaderForZip(response, zipFileName);
				response.getOutputStream().flush();
			} catch (IOException e) {
				log.error("Unable to Download zip file");
			}
			return true;
		}
		return false;
	}

	/**
	 * @return
	 *
	 * @throws SystemException
	 */
	@Override
	public List<DPSopWeekNParam> findAllSopWeekNForDeliveryDateAndPropSoldDateNull() throws SystemException {
		return dpSopWeekNParamDao.findAllByDeliveryDateIsNullAndPropSoldDateDtIsNull();
	}

	/**
	 * @param consolidatedMap
	 * @param response
	 * @param zipFileName
	 * @param sysGnrtdInputFileName
	 *
	 * @throws SystemException
	 */
	@Override
	public void generateSopWeekNZipFile(Map<String, List<DPSopWeekNParam>> consolidatedMap, HttpServletResponse response, String zipFileName,
			String sysGnrtdInputFileName, String type) throws SystemException {
		String fileName = zipFileName;
		String fileOcn = fileName + DPAConstants.OCN_OUTPUT_APPENDER;
		String fileNrz = fileName + DPAConstants.NRZ_OUTPUT_APPENDER;
		String filePhh = fileName + DPAConstants.PHH_OUTPUT_APPENDER;
		String storedFilePath = systemParameterProvider.getSystemParamValue(SystemParameterConstant.SYS_PARAM_SAN_PATH) + File.separator
				+ DPAConstants.SOP_WEEKN_PROCESS_REPORT_FOLDER + File.separator + StringUtils.substringBefore(sysGnrtdInputFileName, ".")
				+ DPAConstants.ZIP_EXTENSION;

		if(!Files.exists(Paths.get(StringUtils.substringBeforeLast(storedFilePath, File.separator)), LinkOption.NOFOLLOW_LINKS)) {
			boolean isFolderCreated = new File(StringUtils.substringBeforeLast(storedFilePath, File.separator)).mkdir();
			if(!isFolderCreated) {
				log.error("Unable to create " + DPAConstants.SOP_WEEKN_PROCESS_REPORT_FOLDER + " folder in sanbase.");
			}
		}
		File zippedFile = new File(storedFilePath);
		try (ZipOutputStream zos = new ZipOutputStream(response != null ? response.getOutputStream() : new FileOutputStream(zippedFile));
				ZipOutputStream zipStream = new ZipOutputStream(new FileOutputStream(zippedFile));) {
			byte[] bytes = null;

			// Adding OCN file into ZIP folder
			if(!consolidatedMap.isEmpty()) {
				bytes = createSopWeekNExcelZip(consolidatedMap.get(DPAConstants.LIST_WKN_OCN), consolidatedMap.get(DPAConstants.LIST_SCCS_UDR_OCN),
						DPAConstants.OCN);
				if(bytes != null) {
					zos.putNextEntry(new ZipEntry(fileOcn));
					zos.write(bytes);
					zipStream.putNextEntry(new ZipEntry(fileOcn));
					zipStream.write(bytes);
				}
			}
			// Adding NRZ file into ZIP folder
			if(!consolidatedMap.isEmpty()) {
				bytes = createSopWeekNExcelZip(consolidatedMap.get(DPAConstants.LIST_WKN_NRZ), consolidatedMap.get(DPAConstants.LIST_SCCS_UDR_NRZ),
						DPAConstants.NRZ);
				if(bytes != null) {
					zos.putNextEntry(new ZipEntry(fileNrz));
					zos.write(bytes);
					zipStream.putNextEntry(new ZipEntry(fileNrz));
					zipStream.write(bytes);
				}
			}
			// Adding PHH file into ZIP folder
			if(!consolidatedMap.isEmpty()) {
				bytes = createSopWeekNExcelZip(consolidatedMap.get(DPAConstants.LIST_WKN_PHH), consolidatedMap.get(DPAConstants.LIST_SCCS_UDR_PHH),
						DPAConstants.PHH);
				if(bytes != null) {
					zos.putNextEntry(new ZipEntry(filePhh));
					zos.write(bytes);
					zipStream.putNextEntry(new ZipEntry(filePhh));
					zipStream.write(bytes);
				}
			}
			// save the file in server
			try {
				zipStream.flush();
			} catch (IOException e) {
				log.error("Unable to store downloaded " + fileName + " file in sanbase.");
				log.error(e.getLocalizedMessage(), e);
			}

			// Creating response  and  adding zip folder into it.
			if (type != null) {
				sopWeekNFileUtil.setResponseHeaderForZip(response, fileName);
				response.getOutputStream().flush();
			}
		} catch (IOException e) {
			log.error("Unable to create zip folder ", e);
		}
	}

	/**
	 * @param priorRecommendedEntries
	 *
	 * @return
	 *
	 * @throws SystemException
	 */
	private Map<String, HubzuInfo> priorRecommendationHubzuCall(List<DPSopWeekNParam> priorRecommendedEntries) throws SystemException {
		Map<String, String> hubzuQuery = new HashMap<>(RAClientConstants.HUBZU_MAP_SIZE);
		hubzuQuery.put(RAClientConstants.HUBZU_QUERY,
				(String) cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_SOP_WEEKN_PRIOR_RECOMMANDATION_ALL_ROWS_HUBZU_QUERY));
		hubzuQuery.put(RAClientConstants.HUBZU_INTEGRATION_TYPE, IntegrationType.SOP_WEEKN_HUBZU_ALL_ROWS_INTEGRATION.getIntegrationType());
		Map<String, HubzuInfo> hubzuMap = new HashMap<>();
		HubzuDBResponse hubzuDBResponse = sopHubzuDBClient.sopWeekNHubzuAllRecordsForPriorRecommendation(priorRecommendedEntries, hubzuQuery);
		if(CollectionUtils.isNotEmpty(hubzuDBResponse.getHubzuInfos())) {
			log.info("Grouping priorRecommendationHubzuCall response based on getSelrPropIdVcNn");
			List<List<HubzuInfo>> hubzuInfoGroupedList = hubzuDBResponse.getHubzuInfos().stream()
					.sorted(Comparator.comparing(HubzuInfo::getListStrtDateDtNn).reversed())
					.collect(Collectors.groupingBy(hbzData -> hbzData.getSelrPropIdVcNn())).values().stream().collect(Collectors.toList());

			hubzuInfoGroupedList.forEach(hubzuInfoGroup -> {
				hubzuMap.put(hubzuInfoGroup.get(0).getSelrPropIdVcNn(), hubzuInfoGroup.get(0));
			});
		} else {
			log.error("No response received from Hubzu db ");
		}
		return hubzuMap;
	}

	/**
	 * @param paramObject
	 * @param param
	 *
	 * @return
	 */
	private boolean diffInDate(DPSopWeekNParamInfo paramObject, DPSopWeekNParam param) {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd").withLocale(Locale.US)
				.withZone(DateTimeZone.forTimeZone(TimeZone.getTimeZone("EST")));
		if(StringUtils.isNotEmpty(paramObject.getListEndDateDtNn()) && param.getDeliveryDate() > 0 &&
				DateConversionUtil.getEstDate(formatter.parseDateTime(paramObject.getListEndDateDtNn()).getMillis()).withTimeAtStartOfDay()
				.compareTo(DateConversionUtil.getEstDate(param.getDeliveryDate()).withTimeAtStartOfDay()) <= 0) {
			return true;
		}
		return false;

	}

	/**
	 * @param listOfParamObject
	 * @param listOfSuccessUnderReview
	 * @param classification
	 *
	 * @return
	 *
	 * @throws SystemException
	 */
	private byte[] createSopWeekNExcelZip(List<DPSopWeekNParam> listOfParamObject, List<DPSopWeekNParam> listOfSuccessUnderReview,
			String classification) throws SystemException {
		byte[] bytes = null;
		Long startTime;
		List<DPSopWeekNParamInfo> recommendedEntriesForAudit = new ArrayList<>();
		List<DPSopWeekNParamInfo> successUnderReviewEntriesForAudit = new ArrayList<>();
		List<DPSopWeekNParamInfo> exclusionEntriesForAudit = new ArrayList<>();
		try (XSSFWorkbook workbook = new XSSFWorkbook()) {
			XSSFSheet processedSheet = workbook.createSheet(DPAConstants.PROCESSED_RECORDS);
			XSSFSheet failedSheet = workbook.createSheet(DPAConstants.FAILED_RECORDS);
			XSSFSheet successUnderReviewSheet = workbook.createSheet(DPAConstants.SUCCESS_UNDERREVIEW);
			XSSFSheet priorRecommendedSheet = workbook.createSheet(DPAConstants.PRIOR_RECOMMENDED);
			CellStyle style = workbook.createCellStyle();
			Font font = workbook.createFont();
			font.setBold(true);
			style.setFont(font);
			int processedRowNum = 0;
			int failedRowNum = 0;
			int successUnderReviewRowNum = 0;
			int priorRecommendRowNum = 0;
			int colNum = 0;
			Set<String> removeAssetsFromProcessedSheet = new HashSet<>();
			Set<String> nonPriorRecomendedAssets = new HashSet<>();

			Row successUnderRow = successUnderReviewSheet.createRow(successUnderReviewRowNum++);
			sopWeekNFileUtil.generateSopWeekNSuccessUnderReviewHeader(style, successUnderRow, colNum);
			for (int i = 1; i <= successUnderRow.getPhysicalNumberOfCells(); i++) {
				successUnderReviewSheet.autoSizeColumn(i);
			}

			Row processedRow = processedSheet.createRow(processedRowNum++);
			sopWeekNFileUtil.generateSopWeekNRecommendedHeader(style, processedRow, colNum);
			for (int i = 1; i <= processedRow.getPhysicalNumberOfCells(); i++) {
				processedSheet.autoSizeColumn(i);
			}

			Row failedRow = failedSheet.createRow(failedRowNum++);
			sopWeekNFileUtil.generateExcludedHeader(style, failedRow, colNum);
			for (int i = 1; i <= failedRow.getPhysicalNumberOfCells(); i++) {
				failedSheet.autoSizeColumn(i);
			}

			Row priorRecommendRow = priorRecommendedSheet.createRow(priorRecommendRowNum++);
			sopWeekNFileUtil.generatePriorRecommendHeader(style, priorRecommendRow, colNum);
			for (int i = 1; i <= priorRecommendRow.getPhysicalNumberOfCells(); i++) {
				priorRecommendedSheet.autoSizeColumn(i);
			}

			// Success and Under Review entries
			List<DPSopWeekNParam> successEntries = listOfSuccessUnderReview;
			DPSopWeekNParamInfo sopWeekNObjectInfo = new DPSopWeekNParamInfo();
			for (DPSopWeekNParam sopWeekNParam : successEntries) {
				sopWeekNObjectInfo.setAssetNumber(sopWeekNParam.getAssetNumber());
				sopWeekNObjectInfo.setOldAssetNumber(sopWeekNParam.getOldAssetNumber());
				sopWeekNObjectInfo.setPropTemp(sopWeekNParam.getPropTemp());
				Map<String, String> hubzuQuery = new HashMap<>(RAClientConstants.HUBZU_MAP_SIZE);

				hubzuQuery.put(RAClientConstants.HUBZU_QUERY,
						(String) cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_SOP_WEEKN_SUCCESS_UNDERREVIEW_TOP_ROWS_HUBZU_QUERY));
				hubzuQuery.put(RAClientConstants.HUBZU_INTEGRATION_TYPE, IntegrationType.SOP_WEEKN_HUBZU_TOP_ROWS_INTEGRATION.getIntegrationType());

				startTime = DateTime.now().getMillis();
				HubzuDBResponse hubzuSopWeekNInitalDBResponse = sopHubzuDBClient
						.getTopHubzuSuccessUnderReviewQueryOutput(sopWeekNObjectInfo, hubzuQuery);
				log.info("Time taken to get top records from hubzu : {}ms ", (DateTime.now().getMillis() - startTime));

				if(CollectionUtils.isEmpty(hubzuSopWeekNInitalDBResponse.getHubzuInfos())) {
					hubzuQuery.put(RAClientConstants.HUBZU_QUERY, (String) cacheManager
							.getAppParamValue(AppParameterConstant.APP_PARAM_SOP_WEEKN_SUCCESS_UNDERREVIEW_ALL_ROWS_HUBZU_QUERY));
					hubzuQuery
					.put(RAClientConstants.HUBZU_INTEGRATION_TYPE, IntegrationType.SOP_WEEKN_HUBZU_ALL_ROWS_INTEGRATION.getIntegrationType());

					startTime = DateTime.now().getMillis();
					HubzuDBResponse hubzuDBResponse = sopHubzuDBClient
							.getAllHubzuSuccessUnderReviewQueryOutput(sopWeekNObjectInfo, hubzuQuery, Boolean.TRUE);
					log.info("Time taken for get hubzu records for all rows : {}ms", (DateTime.now().getMillis() - startTime));
					if(!hubzuDBResponse.getHubzuInfos().isEmpty() && hubzuDBResponse.getHubzuInfos().get(0).getListSttsDtlsVc() == null) {
						sopWeekNParam.setDeliveryDate(hubzuDBResponse.getHubzuInfos().get(0).getListStrtDateDtNn().getTime());
						dpSopWeekNParamDao.save(sopWeekNParam);
						successUnderReviewEntriesForAudit.add(sopWeekNParamMapper.mapDomainToInfo(sopWeekNParam));
						removeAssetsFromProcessedSheet.add(sopWeekNParam.getAssetNumber());
						nonPriorRecomendedAssets.add(sopWeekNParam.getAssetNumber());
					} else if(!hubzuDBResponse.getHubzuInfos().isEmpty() && (
							StringUtils.equalsIgnoreCase(hubzuDBResponse.getHubzuInfos().get(0).getListSttsDtlsVc(), RAClientConstants.UNDERREVIEW)
							|| StringUtils
							.equalsIgnoreCase(hubzuDBResponse.getHubzuInfos().get(0).getListSttsDtlsVc(), RAClientConstants.SUCCESSFUL))) {
						colNum = 0;
						sopWeekNParam.setMostRecentListStatus(hubzuDBResponse.getHubzuInfos().get(0).getListSttsDtlsVc());
						DPSopWeekNParamInfo dpSopWeekNParamInfo = sopWeekNParamMapper.mapDomainToInfo(sopWeekNParam);
						successUnderReviewRowNum = sopWeekNFileUtil
								.prepareSopWeekNsuccessUnderReviewOutput(successUnderReviewSheet, successUnderReviewRowNum, colNum,
										dpSopWeekNParamInfo);
						removeAssetsFromProcessedSheet.add(sopWeekNParam.getAssetNumber());
						dpSopWeekNParamInfo.setDeliveryDate(null);
						successUnderReviewEntriesForAudit.add(dpSopWeekNParamInfo);
						nonPriorRecomendedAssets.add(sopWeekNParam.getAssetNumber());
					} else if(!hubzuDBResponse.getHubzuInfos().isEmpty()) {
						colNum = 0;
						sopWeekNParam.setMostRecentListStatus(hubzuDBResponse.getHubzuInfos().get(0).getListSttsDtlsVc());
						sopWeekNParam.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
						dpSopWeekNParamDao.save(sopWeekNParam);
						processedRowNum = sopWeekNFileUtil.prepareSopWeekNRecommended(processedSheet, processedRowNum, colNum, sopWeekNParam);
						recommendedEntriesForAudit.add(sopWeekNParamMapper.mapDomainToInfo(sopWeekNParam));
						nonPriorRecomendedAssets.add(sopWeekNParam.getAssetNumber());
					}
				} else if(!hubzuSopWeekNInitalDBResponse.getHubzuInfos().isEmpty()) {
					sopWeekNParam.setPropSoldDateDt(hubzuSopWeekNInitalDBResponse.getHubzuInfos().get(0).getPropSoldDateDt());
					dpSopWeekNParamDao.save(sopWeekNParam);
					successUnderReviewEntriesForAudit.add(sopWeekNParamMapper.mapDomainToInfo(sopWeekNParam));
					nonPriorRecomendedAssets.add(sopWeekNParam.getAssetNumber());
				}
			}

			List<DPSopWeekNParam> columnEntries = listOfParamObject;
			for (DPSopWeekNParam sopWeekNParam : columnEntries) {
				if((sopWeekNParam.getFailedStepCommandName() == null) && (!removeAssetsFromProcessedSheet.contains(sopWeekNParam.getAssetNumber()))) {
					colNum = 0;
					processedRowNum = sopWeekNFileUtil.prepareSopWeekNRecommended(processedSheet, processedRowNum, colNum, sopWeekNParam);
					recommendedEntriesForAudit.add(sopWeekNParamMapper.mapDomainToInfo(sopWeekNParam));
					nonPriorRecomendedAssets.add(sopWeekNParam.getAssetNumber());
				}

				if(sopWeekNParam.getFailedStepCommandName() != null) {
					colNum = 0;
					failedRowNum = sopWeekNFileUtil.prepareSopWeekNFailed(failedSheet, failedRowNum, colNum, sopWeekNParam);
					exclusionEntriesForAudit.add(sopWeekNParamMapper.mapDomainToInfo(sopWeekNParam));
					nonPriorRecomendedAssets.add(sopWeekNParam.getAssetNumber());
				}
			}

			// fetch all prior recommended entries
			List<DPSopWeekNParam> priorRecommendedEntries = dpSopWeekNParamDao.findPriorRecommendedAssets(classification);
			Map<String, HubzuInfo> hubzuMap = priorRecommendationHubzuCall(priorRecommendedEntries);
			for (DPSopWeekNParam priorRecommendedEntry : priorRecommendedEntries) {
				if(nonPriorRecomendedAssets.contains(priorRecommendedEntry.getAssetNumber())) {
					priorRecommendedEntry.setIsPriorRecommended(RAClientConstants.NO);
					dpSopWeekNParamDao.save(priorRecommendedEntry);
				} else {
					DPSopWeekNParamInfo sopWeekNParamInfo = new DPSopWeekNParamInfo();
					sopWeekNParamInfo.setListEndDateDtNn((hubzuMap.get(priorRecommendedEntry.getPropTemp()) != null) ?
							hubzuMap.get(priorRecommendedEntry.getPropTemp()).getListEndDateDtNn().toString() :
								null);
					if(StringUtils.isNotEmpty(sopWeekNParamInfo.getListEndDateDtNn()) && diffInDate(sopWeekNParamInfo, priorRecommendedEntry)) {
						priorRecommendRowNum = sopWeekNFileUtil
								.prepareSopWeekNPriorRecommended(priorRecommendedSheet, priorRecommendRowNum, colNum, priorRecommendedEntry);
					} else {
						priorRecommendedEntry.setIsPriorRecommended(RAClientConstants.NO);
						dpSopWeekNParamDao.save(priorRecommendedEntry);
					}
				}
			}
			/*log.error("Creating Failed entries in audit.");
			dpAuditReportDelegate.createFailedEntriesInAudit(exclusionEntriesForAudit, null);
			 */
			// Create Zip folder with all files
			if(processedRowNum > 1 || failedRowNum > 1 || successUnderReviewRowNum > 1 || priorRecommendRowNum > 1) {
				try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
					workbook.write(baos);
					bytes = baos.toByteArray();
				} catch (IOException e) {
					log.error("Unable to read file " + e);
					throw new SystemException(CoreExceptionCodes.DPSOPWKN027, new Object[] {});
				}
			}
		} catch (Exception e) {
			log.error("Unable to generate workbook " + e);
			throw new SystemException(CoreExceptionCodes.DPSOPWKN027, new Object[] {});

		}
		return bytes;
	}

	@Override
	public List<DPSopWeekNParamInfo> getAssetDetails(String fileId, String type) throws SystemException {
		List<DPSopWeekNParam> listOfDpProcessParams = new ArrayList<>();
		List<DPSopWeekNParamInfo> listOfDpProcessParamsInfo = new ArrayList<>();
		if (type != null && type.equalsIgnoreCase(DPAConstants.SOP_WEEKN)) {
			List<DPSopWeekNParam> dpProcessParamsList = dpSopWeekNParamDao.findByDynamicPricingFilePrcs(fileId);
			if (!dpProcessParamsList.isEmpty()) {
				listOfDpProcessParams.addAll(dpProcessParamsList);
			} else {
				log.error("Unable to create Asset details for given fileId ", fileId);
				throw new SystemException(CoreExceptionCodes.DPA0002, new Object[] {});
			}
		}
		listOfDpProcessParams.stream().forEach(infoData -> {
			DPSopWeekNParamInfo dpProcessParamInfo = sopWeekNParamMapper.mapDomainToInfo(infoData);
			listOfDpProcessParamsInfo.add(dpProcessParamInfo);
		});
		return listOfDpProcessParamsInfo;
	}

	@Override
	public List<DPSopWeekNParam> searchByAssetNumber(String assetNumber) {
		return dpSopWeekNParamDao.findByAssetNumberAndDeliveryDateNotNull(assetNumber);
	}

	/**
	 * @param fileId
	 *
	 * @return
	 *
	 * @throws SystemException
	 */
	@Override
	public List<String> findFailedStepCommands(String fileId) throws SystemException {
		return  dpSopWeekNParamDao.findSopFailedStepCommands(fileId);
	}

	@Override
	public List<DPSopWeekNParamInfo> getWeekNFilteredFiles(DashboardFilterInfo dashboardFilterInfo) throws SystemException {
		List<DPSopWeekNParamInfo> listOfDPSopWeekNParamInfo = new ArrayList<>();
		List<DPSopWeekNParam> listOfDPSopWeekNParam = dpSopWeekNFilterDao
				.getSOPWeekNFilteredRecords(dashboardFilterInfo.getFileName(), dashboardFilterInfo.getStatus(), dashboardFilterInfo.getFromDate(),
						dashboardFilterInfo.getToDate());
		if (listOfDPSopWeekNParam.isEmpty()) {
			log.info("No records found for SOP Week N!");
			throw new SystemException(CoreExceptionCodes.DPSOPWKN001, new Object[] {});
		}
		listOfDPSopWeekNParam.stream().forEach(param -> {
			DPSopWeekNParamInfo dpSopWeekNParamInfo = sopWeekNParamMapper.mapDomainToInfo(param);
			listOfDPSopWeekNParamInfo.add(dpSopWeekNParamInfo);
		});
		return listOfDPSopWeekNParamInfo;
	}

	@Override
	public void filterSopQAReportAssignment(List<DPSopWeekNParamInfo> columnEntries) {

		log.info("filterQAReportAssignment() started.");

		Map<String, DPSopWeek0Param> dpProcessParamsMap = dPSopWeekNFileProcessBO.findLatestNonDuplicateInSopWeek0ForAsset(columnEntries.stream().map(a->a.getOldAssetNumber()).collect(Collectors.toList()));

		for (DPSopWeekNParamInfo columnEntry : columnEntries) {
			MDC.put(RAClientConstants.LOAN_NUMBER, columnEntry.getAssetNumber());

			//List<DPProcessParam> dpProcessParams = dpFileProcessBO.findLatestNonDuplicateInWeek0ForGivenAssetList(columnEntry.getOldAssetNumber());
			DPSopWeek0Param dpProcessParam = dpProcessParamsMap.get(columnEntry.getOldAssetNumber());;
			DateTime assignmentDate;

			if (dpProcessParam == null) {
				log.info("There is no Week0 entry for loan no: " + columnEntry.getAssetNumber());
			} else {
				log.info("Week0 entry for loan: " + columnEntry.getAssetNumber() + " Assignment : '" + dpProcessParam.getAssignment()
						+ "' Assignment Date : " + dpProcessParam.getAssignmentDate());
			}

			if (dpProcessParam != null && dpProcessParam.getAssignmentDate() != null) {
				DateTimeParser[] dateParsers = { DateTimeFormat.forPattern("MM/dd/yyyy").getParser(), DateTimeFormat.forPattern("M/d/yy").getParser(),
						DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").getParser() };
				DateTimeFormatter formatter = new DateTimeFormatterBuilder().append(null, dateParsers).toFormatter();
				//					assignmentDate = formatter.parseDateTime(dpProcessParam.getAssignmentDate());
				assignmentDate = DateConversionUtil.getEstDate(dpProcessParam.getAssignmentDate());
				List<HubzuInfo> updatedHubzuInfoList = new ArrayList<>();
				for (HubzuInfo hubzuInfo : columnEntry.getHubzuDBResponse().getHubzuInfos()) {
					if (StringUtils.isBlank(hubzuInfo.getCurrentListStrtDate())) {
						continue;
					}
					DateTime listStrtDt = DateConversionUtil.getEstDate(formatter.parseDateTime(hubzuInfo.getCurrentListStrtDate()).getMillis());
					if (listStrtDt.isBefore(assignmentDate)) {
						continue;
					} else {
						updatedHubzuInfoList.add(hubzuInfo);
					}
				}
				columnEntry.getHubzuDBResponse().setHubzuInfos(updatedHubzuInfoList);
				//if all listings are before assignment date, put that columnEntry in assignment filter with a different exclusion reason
				if (updatedHubzuInfoList.isEmpty()) {
					columnEntry.setExclusionReason(DPProcessFilterParams.ASSIGNMENT_DATE_EXCLUSION.getValue());
					columnEntry.setEligible(DPProcessParamAttributes.INELIGIBLE.getValue());
					continue;
				}
			}

			if (StringUtils
					.equalsAny(columnEntry.getClassification(), DPProcessParamAttributes.OCN.getValue(), DPProcessParamAttributes.PHH.getValue())) {
				if (dpProcessParam == null || StringUtils
						.equalsIgnoreCase(dpProcessParam.getAssignment(), DPProcessParamAttributes.ERROR_ASSIGNMENT.getValue())) {
					columnEntry.setExclusionReason(DPProcessFilterParams.WEEK_ZERO_NOT_RUN.getValue());
					columnEntry.setEligible(DPProcessParamAttributes.INELIGIBLE.getValue());
				} else if (StringUtils.equalsIgnoreCase(dpProcessParam.getAssignment(), DPProcessParamAttributes.BENCHMARK_ASSIGNMENT.getValue())) {
					columnEntry.setExclusionReason(DPProcessParamAttributes.BENCHMARK_ASSIGNMENT.getValue());
				} else if (StringUtils.equalsIgnoreCase(dpProcessParam.getAssignment(), DPProcessParamAttributes.MODELED_ASSIGNMENT.getValue())) {
				} else {
					//For any other assignment statuses
					log.debug("No Success or Failure condition Matches for given LoanNumber=" + columnEntry.getAssetNumber() + " have Assignment="
							+ dpProcessParam.getAssignment() + " and classification=" + dpProcessParam.getClassification());
					columnEntry.setExclusionReason(DPProcessFilterParams.WEEK_ZERO_NOT_RUN.getValue());
				}
			}
		}
		MDC.remove(RAClientConstants.LOAN_NUMBER);
	}
	
	@Override
	public void filterSopQAReportState(List<DPSopWeekNParamInfo> columnEntries) {
		for (DPSopWeekNParamInfo columnEntry : columnEntries) {
			MDC.put(RAClientConstants.LOAN_NUMBER, columnEntry.getAssetNumber());
			// check for state
			if (Arrays.asList(excludedStates).contains(columnEntry.getState())) {
				columnEntry.setExclusionReason(DPProcessFilterParams.STATE_LAW.getValue().replace("#", "State :" + columnEntry.getState()));
			}
			MDC.remove(RAClientConstants.LOAN_NUMBER);
		}
	}
	
	@Override
	public void filterSopQAReportSSPmiFlag(DPSopWeekNParamEntryInfo infoObject) {
		List<PmiInsuranceCompany> pmiInscCompanies = spclServicingInvestorBo.findPmiInsCompsByActiveTrue();
		List<String> pmiInscCompIds = pmiInscCompanies.stream().map(a -> new String[] { a.getInsuranceCompany(), a.getCompanyCode() })
				.flatMap(a -> Arrays.stream(a)).distinct().collect(Collectors.toList());
		log.info("filterOnZeroPMIFlag() pmiInscCompIds - > {}", pmiInscCompIds.toString());
		//dpProcessWeekNFilterDelegate.filterSSPmi(infoObject);
		for (DPSopWeekNParamInfo columnEntry : infoObject.getColumnEntries()) {
			if (StringUtils.equals(columnEntry.getPrivateMortgageInsurance(), RAClientConstants.YES)) {
				columnEntry.setExclusionReason(DPProcessFilterParams.PMI.getValue());
			} else if (StringUtils.equals(columnEntry.getPrivateMortgageInsurance(), RAClientConstants.NO) && pmiInscCompIds
					.contains(columnEntry.getPmiCompanyInsuranceId())) {
				columnEntry.setExclusionReason(DPProcessFilterParams.PMI.getValue());
			}
			if (StringUtils.equalsIgnoreCase(columnEntry.getSpecialServicingFlag(), RAClientConstants.YES)) {
				columnEntry.setExclusionReason(DPProcessFilterParams.SPECIAL_SERVICE.getValue());
			}
		}
	}

}
