package com.fa.dp.business.weekn.bo;

import static com.fa.dp.core.exception.codes.CoreExceptionCodes.DPWKN0001;
import static com.fa.dp.core.exception.codes.CoreExceptionCodes.DPWKN0002;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import com.fa.dp.business.audit.delegate.DPAuditReportDelegate;
import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.db.client.HubzuDBClient;
import com.fa.dp.business.info.HubzuDBResponse;
import com.fa.dp.business.info.HubzuInfo;
import com.fa.dp.business.util.IntegrationType;
import com.fa.dp.business.util.ThreadPoolExecutorUtil;
import com.fa.dp.business.util.TransactionStatus;
import com.fa.dp.business.validation.file.header.constant.DPProcessFileHeader;
import com.fa.dp.business.weekn.dao.DPProcessWeekNParamsDao;
import com.fa.dp.business.weekn.dao.DPWeekNProcessStatusRepo;
import com.fa.dp.business.weekn.entity.DPProcessWeekNParam;
import com.fa.dp.business.weekn.entity.DPWeekNProcessStatus;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamEntryInfo;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamInfo;
import com.fa.dp.business.weekn.input.info.DPWeekNToInfoMapper;
import com.fa.dp.core.cache.CacheManager;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.exception.codes.CoreExceptionCodes;
import com.fa.dp.core.systemparam.provider.SystemParameterProvider;
import com.fa.dp.core.systemparam.util.AppParameterConstant;
import com.fa.dp.core.systemparam.util.SystemParameterConstant;
import com.fa.dp.core.util.DateConversionUtil;
import com.fa.dp.core.util.RAClientConstants;

@Slf4j
@Named
public class WeekNBOImpl implements WeekNBO {
    private static final String WEEKN_PROCESS_REPORT_FOLDER = "weekNProcessReport";
    private static final String ZIP_EXTENSION = ".zip";

    @Inject
    private DPWeekNProcessStatusRepo dpWeekNProcessStatusRepo;

    @Inject
    private DPWeekNBOUtil dpWeekNBOUtil;

    @Inject
    private DPWeekNToInfoMapper dpWeekNToInfoMapper;

    @Inject
    private DPProcessWeekNParamsDao dpProcessWeekNParamsDao;

    @Inject
    private CacheManager cacheManager;

    @Inject
    private HubzuDBClient hubzuDBClient;

    @Inject
    private DPAuditReportDelegate dpAuditReportDelegate;

    @Inject
    private SystemParameterProvider systemParameterProvider;

    @Value("${WEEKN_CONCURRENT_DBCALL_POOL_SIZE}")
    private int concurrentDbCallPoolSize;

    private ExecutorService executorService;

    @PostConstruct
    public void initializeTemplate() {
        executorService = ThreadPoolExecutorUtil.getFixedSizeThreadPool(concurrentDbCallPoolSize);
    }

    @PreDestroy
    public void destroy() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    public void createWeekNExcel(DPProcessWeekNParamEntryInfo dpProcessParamEntryInfo, Long userSelectedDate, HttpServletResponse response) throws SystemException, ParseException {
        log.info("Creating workbook for DP Week N Params");

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet processedSheet = workbook.createSheet(DPAConstants.PROCESSED_RECORDS);
            XSSFSheet failedSheet = workbook.createSheet(DPAConstants.FAILED_RECORDS);
            /*	XSSFSheet priorRecommendedSheet = workbook.createSheet(DPAConstants.PRIOR_RECOMMENDED);*/

            List<DPProcessWeekNParamInfo> priorRecommendedEntries = new ArrayList<>();
            List<DPProcessWeekNParamInfo> recommendedEntriesForAudit = new ArrayList<>();

            CellStyle style = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            style.setFont(font);
            int processedRowNum = 0;
            int failedRowNum = 0;
            /*int priorRecommendRowNum = 0;*/
            int colNum = 0;

            Row processedRow = processedSheet.createRow(processedRowNum++);
            dpWeekNBOUtil.generateHeaderForPotential(style, processedRow, colNum);
            for (int i = 1; i <= processedRow.getPhysicalNumberOfCells(); i++) {
                processedSheet.autoSizeColumn(i);
            }

            Row failedRow = failedSheet.createRow(failedRowNum++);
            dpWeekNBOUtil.generateHeader(style, failedRow, colNum);
            for (int i = 1; i <= failedRow.getPhysicalNumberOfCells(); i++) {
                failedSheet.autoSizeColumn(i);
            }

			/*Row priorRecommendRow = priorRecommendedSheet.createRow(priorRecommendRowNum++);
        	dpWeekNBOUtil.generatePriorRecommendHeader(style, priorRecommendRow, colNum);
        	for (int i = 1; i <= priorRecommendRow.getPhysicalNumberOfCells(); i++) {
        		priorRecommendedSheet.autoSizeColumn(i);
        	}*/


            List<DPProcessWeekNParamInfo> recommendedEntries = dpProcessParamEntryInfo.getColumnEntries().stream().filter(item -> item.getCommand() == null).collect(toList())
                    .stream().filter(item -> {
                        List<DPProcessWeekNParam> dpProcessWeekNParams = dpProcessWeekNParamsDao.findByAssetNumberAndDeliveryDateIsNull(item.getAssetNumber());
                        if (dpProcessWeekNParams.isEmpty())
                            return true;
                        else
                            return false;
                    }).collect(toList());

            List<DPProcessWeekNParamInfo> failedEntries = dpProcessParamEntryInfo.getColumnEntries().stream().filter(item -> item.getCommand() != null).collect(toList());

            // commented auditing as per story DP-603 --> start
            if (DateConversionUtil.getCurrentEstDate().withTimeAtStartOfDay().compareTo(DateConversionUtil.getEstDate(userSelectedDate).withTimeAtStartOfDay()) == 0) {
              log.error("Creating Failed entries in audit.");
                dpAuditReportDelegate.createFailedEntriesInAudit(failedEntries, userSelectedDate);
            }
            // commented auditing as per story DP-603 --> end
            //       List<DPProcessWeekNParamInfo> skipped12DaysList = failedEntries.stream().filter(cm ->  cm.getCommand().getName().equalsIgnoreCase(DPAConstants.PAST12_CYCLES_FILTER)).collect(toList());

            List<DPProcessWeekNParam> listOfWeekNParams = null;
            log.info("The maximum latest list end date in Week N DB is taken and all the properties having this list end date are selected.");
            listOfWeekNParams = dpProcessWeekNParamsDao.findAssetNumberForProiorRecommendation();
            if (CollectionUtils.isEmpty(listOfWeekNParams)) {
                log.info("Data Not found from Week n for Prior Recommendation. ");
            } else {
                // prior recommendation
                Map<String, HubzuInfo> hubzuMap = priorRecommendationHubzuCall(listOfWeekNParams);
                for (DPProcessWeekNParam priorRecommendedEntry : listOfWeekNParams) {
                    DPProcessWeekNParamInfo dpProcessWeekNParamInfo = dpWeekNToInfoMapper.dpWeekNToInfoMapper(priorRecommendedEntry);
                    dpProcessWeekNParamInfo.setListEndDateDtNn((hubzuMap.get(priorRecommendedEntry.getPropTemp()) != null) ?
                            hubzuMap.get(priorRecommendedEntry.getPropTemp()).getListEndDateDtNn().toString() : null);
                    if (StringUtils.isNotEmpty(dpProcessWeekNParamInfo.getListEndDateDtNn())
                            && diffInDate(dpProcessWeekNParamInfo, priorRecommendedEntry)) {
                        colNum = 0;

                        // Story 432
                        log.info(priorRecommendedEntry.getAssetNumber() + " was prior recommended.");
                        priorRecommendedEntry.setIsPriorRecommended(RAClientConstants.YES);
                        dpProcessWeekNParamsDao.save(priorRecommendedEntry);

                        //priorRecommendRowNum = dpWeekNBOUtil.prepareWeekNPriorRecommendationOutputData(priorRecommendedSheet, priorRecommendRowNum, colNum, paramObject, param);
                        dpProcessWeekNParamInfo.setDeliveryDate(priorRecommendedEntry.getDeliveryDate());
                        priorRecommendedEntries.add(dpProcessWeekNParamInfo);

                    }
                }
            }

            for (DPProcessWeekNParamInfo paramObject : recommendedEntries) {
                colNum = 0;
                processedRowNum = dpWeekNBOUtil.prepareWeekNOutputData(processedSheet, processedRowNum, colNum, paramObject, userSelectedDate);
                recommendedEntriesForAudit.add(paramObject);
            }

            // commented auditing as per story DP-603 --> start
//			if (DateConversionUtil.getCurrentEstDate().withTimeAtStartOfDay().compareTo(DateConversionUtil.getEstDate(userSelectedDate).withTimeAtStartOfDay()) == 0) {
//				log.error("Creating Recommended entries in audit.");
//				dpAuditReportDelegate.createRecommendedEntriesInAudit(recommendedEntriesForAudit, userSelectedDate);
//				log.error("Creating Prior Recommended entries in audit.");
//				dpAuditReportDelegate.createPriorRecommendedEntriesInAudit(priorRecommendedEntries, userSelectedDate);
//			}
            // commented auditing as per story DP-603 --> end
            for (DPProcessWeekNParamInfo paramObject : failedEntries) {
                colNum = 0;
                failedRowNum = dpWeekNBOUtil.prepareWeekNOutputData(failedSheet, failedRowNum, colNum, paramObject, userSelectedDate);
            }

			/*for (DPProcessWeekNParamInfo paramObject : skipped12DaysList) {
        		colNum = 0;
        		passed12CyclesRowNum = dpWeekNBOUtil.prepareWeekNSkipped12CyclesOutputData(skipped12DaysSheet, passed12CyclesRowNum, colNum, paramObject);
        	}*/
            if (processedRowNum > 1 || failedRowNum > 1) {
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    workbook.write(baos);
                    byte[] outArray = baos.toByteArray();
                    response.setContentLength(outArray.length);
                    dpWeekNBOUtil.setResponseHeader(response, "WeekN_Potential_" + DateConversionUtil.getEstDate(userSelectedDate).toString("YYYYddMM") + ".xlsx");
                    response.getOutputStream().write(outArray);
                    response.flushBuffer();
                } catch (IOException e) {
                    log.error("Unable to write into  file ", e);
                }
            }
        } catch (Exception e) {
            log.error("Unable create excel documents ", e);
            throw new SystemException("Error while creating excel file.", e.getLocalizedMessage());
        } finally {
            try {
                response.getOutputStream().close();
            } catch (IOException e) {
                log.error("Unable to close response stream ", e);
            }
        }
    }

    private boolean diffInDate(DPProcessWeekNParamInfo paramObject, DPProcessWeekNParam param) throws ParseException {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd").withLocale(Locale.US)
                .withZone(DateTimeZone.forTimeZone(TimeZone.getTimeZone("EST")));
        if (StringUtils.isNotEmpty(paramObject.getListEndDateDtNn())
                && param.getDeliveryDate() > 0
                && DateConversionUtil.getEstDate(formatter.parseDateTime(paramObject.getListEndDateDtNn()).getMillis()).withTimeAtStartOfDay()
                .compareTo(DateConversionUtil.getEstDate(param.getDeliveryDate()).withTimeAtStartOfDay()) <= 0) {
            return true;
        }
        return false;

    }

    /**
     * @param fileStatus
     * @return find DPFileProcessStatus By Status
     */
    @Override
    public DPWeekNProcessStatus findDPFileProcessStatusByStatus(String fileStatus) {
        DPWeekNProcessStatus dpFilePrcsStatus = dpWeekNProcessStatusRepo.findByStatus(fileStatus);
        return dpFilePrcsStatus;
    }

    @Override
    public List<DPProcessWeekNParamInfo> getSheetsFromWeekNUploadedExcel(MultipartFile file) throws IOException, SystemException {
        Workbook workbook = null;
        Sheet dataTypeProcessedSheet = null;
        List<DPProcessWeekNParamInfo> dpProcessWeekNParamInfos = new ArrayList<>();
        try {
            workbook = new XSSFWorkbook(file.getInputStream());
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                if (StringUtils.equals(workbook.getSheetName(i), DPAConstants.PROCESSED_RECORDS)) {
                    dataTypeProcessedSheet = workbook.getSheetAt(i);
                    dpProcessWeekNParamInfos = getDPProcessWeekNParamInfos(dataTypeProcessedSheet);
                }
            }
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
            throw e;
        } finally {
            IOUtils.closeQuietly(workbook);
        }
        return dpProcessWeekNParamInfos;
    }

    @Override
    public List<DPProcessWeekNParam> fetchWeekNFilesDetailsById(String id) throws IOException, SystemException {
        List<DPProcessWeekNParam> listOfDPProcessWeekNParam = dpProcessWeekNParamsDao.findDPProcessWeekNParamByStatusID(id);
        if (listOfDPProcessWeekNParam.isEmpty()) {
            log.error("Week N details are not available for requested Id - " + id);
            throw new SystemException(CoreExceptionCodes.DPWKN0104);
        }
        return listOfDPProcessWeekNParam;
    }

    @Override
    public void generateWeekNOutputFile(Map<String, List<DPProcessWeekNParam>> consolidatedMap, HttpServletResponse response, String zipFileName, String sysGnrtdInputFileName, String type) throws SystemException {
        String fileName = zipFileName;
        String fileOcn = fileName + DPAConstants.OCN_OUTPUT_APPENDER;
        String fileNrz = fileName + DPAConstants.NRZ_OUTPUT_APPENDER;
        String filePhh = fileName + DPAConstants.PHH_OUTPUT_APPENDER;
        String storedFilePath = systemParameterProvider.getSystemParamValue(SystemParameterConstant.SYS_PARAM_SAN_PATH)
                + File.separator
                + WEEKN_PROCESS_REPORT_FOLDER
                + File.separator
                + StringUtils.substringBefore(sysGnrtdInputFileName, ".")
                + ZIP_EXTENSION;
        if (!Files.exists(Paths.get(StringUtils.substringBeforeLast(storedFilePath, File.separator)), LinkOption.NOFOLLOW_LINKS)) {
            boolean isFolderCreated = new File(StringUtils.substringBeforeLast(storedFilePath, File.separator)).mkdir();
            if (!isFolderCreated) {
                log.error("Unable to create " + WEEKN_PROCESS_REPORT_FOLDER + " folder in sanbase.");
            }
        }
        File zippedFile = new File(storedFilePath);
        try (
                ZipOutputStream zos = new ZipOutputStream(response != null ? response.getOutputStream() : new FileOutputStream(zippedFile));
                ZipOutputStream zipStream = new ZipOutputStream(new FileOutputStream(zippedFile));
        ) {
            byte[] bytes = null;

            // Adding OCN file into ZIP folder
            if (!consolidatedMap.isEmpty()) {
                // bytes = createAndDownloadWeekNExcel(consolidatedMap.get("listOfDPProcessWeekNParamOCN"));
                bytes = createAndDownloadWeekNExcel(consolidatedMap.get(DPAConstants.LIST_WKN_OCN), consolidatedMap.get(DPAConstants.LIST_SCCS_UDR_OCN), DPAConstants.OCN);
                if (bytes != null) {
                    zos.putNextEntry(new ZipEntry(fileOcn));
                    zos.write(bytes);
                    zipStream.putNextEntry(new ZipEntry(fileOcn));
                    zipStream.write(bytes);
                }
            }
            // Adding NRZ file into ZIP folder
            if (!consolidatedMap.isEmpty()) {
                bytes = createAndDownloadWeekNExcel(consolidatedMap.get(DPAConstants.LIST_WKN_NRZ), consolidatedMap.get(DPAConstants.LIST_SCCS_UDR_NRZ), DPAConstants.NRZ);
                if (bytes != null) {
                    zos.putNextEntry(new ZipEntry(fileNrz));
                    zos.write(bytes);
                    zipStream.putNextEntry(new ZipEntry(fileNrz));
                    zipStream.write(bytes);
                }
            }
            // Adding PHH file into ZIP folder
            if (!consolidatedMap.isEmpty()) {
                bytes = createAndDownloadWeekNExcel(consolidatedMap.get(DPAConstants.LIST_WKN_PHH), consolidatedMap.get(DPAConstants.LIST_SCCS_UDR_PHH), DPAConstants.PHH);
                if (bytes != null) {
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
                setResponseHeaderForZip(response, fileName);
                response.getOutputStream().flush();
            }
        } catch (IOException e) {
            log.error("Unable to create zip folder ", e);
        }
    }

    @Override
    public boolean findWeekNReports(String sysGnrtdInputFileName, String zipFileName, HttpServletResponse response) {
        String storedFilePath = systemParameterProvider.getSystemParamValue(SystemParameterConstant.SYS_PARAM_SAN_PATH)
                + File.separator + WEEKN_PROCESS_REPORT_FOLDER + File.separator + StringUtils.substringBefore(sysGnrtdInputFileName, ".")
                + ZIP_EXTENSION;
        if (Files.exists(Paths.get(storedFilePath), LinkOption.NOFOLLOW_LINKS)) {
            byte[] bytes = null;
            File file = new File(storedFilePath);
            try (InputStream inputStream = new FileInputStream(file)) {
                bytes = IOUtils.toByteArray(inputStream);
                response.getOutputStream().write(bytes);
                setResponseHeaderForZip(response, zipFileName);
                response.getOutputStream().flush();
            } catch (IOException e) {
                log.error("Unable to Download zip file");
            }
            return true;
        }
        return false;
    }

    @Override
    public List<DPProcessWeekNParam> findAllWeekNParams() throws SystemException {
        return dpProcessWeekNParamsDao.findAllByDeliveryDateIsNullAndPropSoldDateDtIsNull();
    }


    private List<DPProcessWeekNParamInfo> getDPProcessWeekNParamInfos(Sheet datatypeSheet) throws SystemException {
        log.info("Validating the columns in Potential sheet");
        if (Objects.isNull(datatypeSheet)) {
            log.error("Potential sheet is not available in uploaded file");
            throw new SystemException(CoreExceptionCodes.DPWKN0107, new Object[]{});
        }
        List<DPProcessWeekNParamInfo> dpProcessWeekNParamInfos = new ArrayList<>();
        int assetNumberColNum = -1;
        int latestListEndDateColNum = -1;

        Row headerRow = datatypeSheet.getRow(0);
        DataFormatter df = new DataFormatter();
        for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
            String cellValue = df.formatCellValue(headerRow.getCell(i));
            if (StringUtils.equalsIgnoreCase(DPProcessFileHeader.HEADER1.getValue(), cellValue)) {
                assetNumberColNum = i;
            }
            if (StringUtils.equalsIgnoreCase(DPProcessFileHeader.HEADER18.getValue(), cellValue)) {
                latestListEndDateColNum = i;
            }
        }
        if (assetNumberColNum == -1 && latestListEndDateColNum == -1) {
            List<String> missingCols = new ArrayList<>();
            if (assetNumberColNum == -1) {
                missingCols.add(DPProcessFileHeader.HEADER1.getValue());
                missingCols.add(DPProcessFileHeader.HEADER18.getValue());
            }
            log.error("Asset # / Most recent list end date column/columns is/are Missing in Potential List Sheet: " + missingCols.toString());
            throw new SystemException(CoreExceptionCodes.DPWKN0108, new Object[]{String.join(", ", missingCols)});
        }
        DPProcessWeekNParamInfo dpProcessWeekNParamInfo;
        for (int rowIndex = 1; rowIndex <= datatypeSheet.getLastRowNum(); rowIndex++) {
            dpProcessWeekNParamInfo = new DPProcessWeekNParamInfo();
            Row currentRow = datatypeSheet.getRow(rowIndex);

            if (StringUtils.equals(getCellValue(currentRow, df, assetNumberColNum), null)) {
                log.error("Asset # cannot be empty");
                throw new SystemException(CoreExceptionCodes.DPWKN0112, new Object[]{});
            }
            String assetNumber = getCellValue(currentRow, df, assetNumberColNum);
            String mostRecentListEndDate = getCellValue(currentRow, df, latestListEndDateColNum);
            if (!StringUtils.trim(assetNumber).equals(RAClientConstants.CHAR_EMPTY)) {
                dpProcessWeekNParamInfo.setAssetNumber(assetNumber);
                dpProcessWeekNParamInfo.setMostRecentListEndDate(mostRecentListEndDate);
                dpProcessWeekNParamInfos.add(dpProcessWeekNParamInfo);
            }
        }
        if (CollectionUtils.isEmpty(dpProcessWeekNParamInfos)) {
            log.error("No records found in uploaded file to process");
            throw new SystemException(CoreExceptionCodes.DP020, new Object[]{});
        }

        return dpProcessWeekNParamInfos;
    }

    private String getCellValue(Row currentRow, DataFormatter df, int index) {
        log.info("getCellValue() current row : " + currentRow);
        Cell cell = currentRow.getCell(index);
        log.info("getCellValue() cell : " + cell);
        String result = null;
        if (null != cell) {
            cell.setCellType(CellType.STRING);
            result = df.formatCellValue(cell);
        }
        return result;
    }


    private void setResponseHeaderForZip(final HttpServletResponse response, final String filename) {
        response.setHeader("Content-Type", "application/zip");
        response.setHeader("Content-Disposition", "attachment;filename=" + filename + ".zip");
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
    }

    private byte[] createAndDownloadWeekNExcel(List<DPProcessWeekNParam> listOfParamObject, List<DPProcessWeekNParam> listOfSuccessUnderReview, String classification) throws SystemException {
        byte[] bytes = null;
        Long startTime;
        List<DPProcessWeekNParamInfo> recommendedEntriesForAudit = new ArrayList<>();
        List<DPProcessWeekNParamInfo> successUnderReviewEntriesForAudit = new ArrayList<>();
        List<DPProcessWeekNParamInfo> exclusionEntriesForAudit = new ArrayList<>();
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
            dpWeekNBOUtil.generateWeekNSuccessUnderReviewHeader(style, successUnderRow, colNum);
            for (int i = 1; i <= successUnderRow.getPhysicalNumberOfCells(); i++) {
                successUnderReviewSheet.autoSizeColumn(i);
            }

            Row processedRow = processedSheet.createRow(processedRowNum++);
            dpWeekNBOUtil.generateWeekNRecommendedHeader(style, processedRow, colNum);
            for (int i = 1; i <= processedRow.getPhysicalNumberOfCells(); i++) {
                processedSheet.autoSizeColumn(i);
            }

            Row failedRow = failedSheet.createRow(failedRowNum++);
            dpWeekNBOUtil.generateExcludedHeader(style, failedRow, colNum);
            for (int i = 1; i <= failedRow.getPhysicalNumberOfCells(); i++) {
                failedSheet.autoSizeColumn(i);
            }

            Row priorRecommendRow = priorRecommendedSheet.createRow(priorRecommendRowNum++);
            dpWeekNBOUtil.generatePriorRecommendHeader(style, priorRecommendRow, colNum);
            for (int i = 1; i <= priorRecommendRow.getPhysicalNumberOfCells(); i++) {
                priorRecommendedSheet.autoSizeColumn(i);
            }

            // Success and Under Review entries
            List<DPProcessWeekNParam> successEntries = listOfSuccessUnderReview;
            DPProcessWeekNParamInfo paramObjectInfo = new DPProcessWeekNParamInfo();
            for (DPProcessWeekNParam paramObject : successEntries) {
                paramObjectInfo.setAssetNumber(paramObject.getAssetNumber());
                paramObjectInfo.setOldAssetNumber(paramObject.getOldAssetNumber());
                paramObjectInfo.setPropTemp(paramObject.getPropTemp());
                Map<String, String> hubzuQuery = new HashMap<>(RAClientConstants.HUBZU_MAP_SIZE);
                hubzuQuery.put(RAClientConstants.HUBZU_QUERY, (String) cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_SUCCESS_UNDERREVIEW_INITAL_HUBZU_QUERY));
                hubzuQuery.put(RAClientConstants.HUBZU_INTEGRATION_TYPE, IntegrationType.HUBZU_INITIAL_ALL_ROWS_INTEGRATION.getIntegrationType());
                startTime = DateTime.now().getMillis();
                HubzuDBResponse hubzuWeekNInitalDBResponse = hubzuDBClient.fetchInitialSuccessReviewQueryOutput(paramObjectInfo, hubzuQuery);
                log.info("Time taken for initial fetch hubzu query for all rows : " + (DateTime.now().getMillis() - startTime) + "ms");
                if (hubzuWeekNInitalDBResponse != null) {
                    String errorMessage = hubzuWeekNInitalDBResponse.getErrorMsg();
                    if (errorMessage != null && errorMessage.contains("No Record Found In")) {
                        hubzuQuery.put(RAClientConstants.HUBZU_QUERY, (String) cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_SUCCESS_UNDERREVIEW_HUBZU_QUERY));
                        hubzuQuery.put(RAClientConstants.HUBZU_INTEGRATION_TYPE, IntegrationType.HUBZU_INITIAL_ALL_ROWS_INTEGRATION.getIntegrationType());
                        startTime = DateTime.now().getMillis();
                        HubzuDBResponse hubzuDBResponse = hubzuDBClient.fetchSuccessReviewQueryOutput(paramObjectInfo, hubzuQuery, Boolean.TRUE);
                        log.info("Time taken for initial fetch hubzu query for all rows : " + (DateTime.now().getMillis() - startTime) + "ms");
                        if (hubzuDBResponse != null) {
                            if (hubzuDBResponse.getTransactionStatus() != null && "SUCCESS".contains(hubzuDBResponse.getTransactionStatus())) {
                                if (!hubzuDBResponse.getHubzuInfos().isEmpty() && hubzuDBResponse.getHubzuInfos().get(0).getListSttsDtlsVc() == null) {
                                    paramObject.setDeliveryDate(hubzuDBResponse.getHubzuInfos().get(0).getListStrtDateDtNn().getTime());
                                    dpProcessWeekNParamsDao.save(paramObject);
                                    successUnderReviewEntriesForAudit.add(dpWeekNToInfoMapper.dpWeekNToInfoMapper(paramObject));
                                    removeAssetsFromProcessedSheet.add(paramObject.getAssetNumber());
                                    nonPriorRecomendedAssets.add(paramObject.getAssetNumber());
                                } else if (!hubzuDBResponse.getHubzuInfos().isEmpty() && (hubzuDBResponse.getHubzuInfos().get(0).getListSttsDtlsVc().equalsIgnoreCase(RAClientConstants.UNDERREVIEW)
                                        || hubzuDBResponse.getHubzuInfos().get(0).getListSttsDtlsVc().equalsIgnoreCase(RAClientConstants.SUCCESSFUL))) {
                                    colNum = 0;
                                    paramObject.setMostRecentListStatus(hubzuDBResponse.getHubzuInfos().get(0).getListSttsDtlsVc());
                                    DPProcessWeekNParamInfo dpProcessWeekNParamInfo = dpWeekNToInfoMapper.dpWeekNToInfoMapper(paramObject);
                                    successUnderReviewRowNum = dpWeekNBOUtil.prepareWeekNsuccessUnderReviewOutputData(successUnderReviewSheet, successUnderReviewRowNum, colNum, dpProcessWeekNParamInfo);
                                    removeAssetsFromProcessedSheet.add(paramObject.getAssetNumber());
                                    dpProcessWeekNParamInfo.setDeliveryDate(null);
                                    successUnderReviewEntriesForAudit.add(dpProcessWeekNParamInfo);
                                    nonPriorRecomendedAssets.add(paramObject.getAssetNumber());
                                } else {
                                    colNum = 0;
                                    paramObject.setMostRecentListStatus(hubzuDBResponse.getHubzuInfos().get(0).getListSttsDtlsVc());
                                    paramObject.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
                                    dpProcessWeekNParamsDao.save(paramObject);
                                    processedRowNum = dpWeekNBOUtil.prepareWeekNRecommendedData(processedSheet, processedRowNum, colNum, paramObject);
                                    recommendedEntriesForAudit.add(dpWeekNToInfoMapper.dpWeekNToInfoMapper(paramObject));
                                    nonPriorRecomendedAssets.add(paramObject.getAssetNumber());
                                }
                            }
                        }
                    } else if (hubzuWeekNInitalDBResponse.getTransactionStatus().contains("SUCCESS") && !hubzuWeekNInitalDBResponse.getHubzuInfos().isEmpty()) {
                        paramObject.setPropSoldDateDt(hubzuWeekNInitalDBResponse.getHubzuInfos().get(0).getPropSoldDateDt());
                        dpProcessWeekNParamsDao.save(paramObject);
                        successUnderReviewEntriesForAudit.add(dpWeekNToInfoMapper.dpWeekNToInfoMapper(paramObject));
                        nonPriorRecomendedAssets.add(paramObject.getAssetNumber());
                    }
                }
            }

            List<DPProcessWeekNParam> columnEntries = listOfParamObject;
            for (DPProcessWeekNParam paramObject : columnEntries) {
                if ((paramObject.getCommand() == null) && (!removeAssetsFromProcessedSheet.contains(paramObject.getAssetNumber()))) {
                    colNum = 0;
                    processedRowNum = dpWeekNBOUtil.prepareWeekNRecommendedData(processedSheet, processedRowNum, colNum, paramObject);
                    recommendedEntriesForAudit.add(dpWeekNToInfoMapper.dpWeekNToInfoMapper(paramObject));
                    nonPriorRecomendedAssets.add(paramObject.getAssetNumber());
                }

                if (paramObject.getCommand() != null) {
                    colNum = 0;
                    failedRowNum = dpWeekNBOUtil.prepareWeekNExcludedData(failedSheet, failedRowNum, colNum, paramObject);
                    exclusionEntriesForAudit.add(dpWeekNToInfoMapper.dpWeekNToInfoMapper(paramObject));
                    nonPriorRecomendedAssets.add(paramObject.getAssetNumber());
                }
            }

            // fetch all prior recommended entries
            List<DPProcessWeekNParam> priorRecommendedEntries = dpProcessWeekNParamsDao.findPriorRecommendedAssets(classification);
            Map<String, HubzuInfo> hubzuMap = priorRecommendationHubzuCall(priorRecommendedEntries);
            for (DPProcessWeekNParam priorRecommendedEntry : priorRecommendedEntries) {
                if (nonPriorRecomendedAssets.contains(priorRecommendedEntry.getAssetNumber())) {
                    priorRecommendedEntry.setIsPriorRecommended(RAClientConstants.NO);
                    dpProcessWeekNParamsDao.save(priorRecommendedEntry);
                } else {
                    DPProcessWeekNParamInfo dpProcessWeekNParamInfo = new DPProcessWeekNParamInfo();
                    dpProcessWeekNParamInfo.setListEndDateDtNn((hubzuMap.get(priorRecommendedEntry.getPropTemp()) != null) ?
                            hubzuMap.get(priorRecommendedEntry.getPropTemp()).getListEndDateDtNn().toString() : null);
                    if (StringUtils.isNotEmpty(dpProcessWeekNParamInfo.getListEndDateDtNn())
                            && diffInDate(dpProcessWeekNParamInfo, priorRecommendedEntry)) {
                        priorRecommendRowNum = dpWeekNBOUtil.prepareWeekNPriorRecommendationOutputData(priorRecommendedSheet, priorRecommendRowNum, colNum, priorRecommendedEntry);
                    } else {
                        priorRecommendedEntry.setIsPriorRecommended(RAClientConstants.NO);
                        dpProcessWeekNParamsDao.save(priorRecommendedEntry);
                    }
                }
            }

            // commented auditing as per story DP-603 --> start
//            log.error("Creating Recommended entries in audit.");
//            dpAuditReportDelegate.createRecommendedEntriesInAudit(recommendedEntriesForAudit, null);
//            log.error("Creating Sucessful/Underreview entries in audit.");
//            dpAuditReportDelegate.createSucessfulUnderreviewEntriesInAudit(successUnderReviewEntriesForAudit);
            // commented auditing as per story DP-603 --> end
            log.error("Creating Failed entries in audit.");
            dpAuditReportDelegate.createFailedEntriesInAudit(exclusionEntriesForAudit, null);

            // Create Zip folder with all files
            if (processedRowNum > 1 || failedRowNum > 1 || successUnderReviewRowNum > 1 || priorRecommendRowNum > 1) {
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

    private Map<String, HubzuInfo> priorRecommendationHubzuCall(List<DPProcessWeekNParam> priorRecommendedEntries) throws SystemException {
        Map<String, String> hubzuQuery = new HashMap<>(RAClientConstants.HUBZU_MAP_SIZE);
        hubzuQuery.put(RAClientConstants.HUBZU_QUERY,
                (String) cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_PRIOR_RECOMMENDATION_INITIAL_HUBZU_QUERY_ALL_ROWS));
        hubzuQuery.put(RAClientConstants.HUBZU_INTEGRATION_TYPE,
                IntegrationType.HUBZU_INITIAL_ALL_ROWS_INTEGRATION.getIntegrationType());
        HubzuDBResponse hubzuDBResponse = hubzuDBClient.fetchAllRowsOfPriorRecommendation(priorRecommendedEntries, hubzuQuery);
        Map<String, HubzuInfo> hubzuMap = new HashMap<>();

        if (hubzuDBResponse != null) {
            if (TransactionStatus.FAIL.getTranStatus().equalsIgnoreCase(hubzuDBResponse.getTransactionStatus())) {
                String errorMessage = hubzuDBResponse.getErrorMsg();
                if (errorMessage.contains("Error while")) {
                    // handles exception scenario
                    log.error("hubzu db response message : ", errorMessage);
                    throw SystemException.newSystemException(DPWKN0001, new Object[]{});
                } else {
                    // handles no records found
                    log.error("hubzu db response message : ", errorMessage);
                    throw SystemException.newSystemException(DPWKN0002, new Object[]{});
                }
            } else {
                log.info("Grouping Hubzu response based on getSelrPropIdVcNn");
                List<List<HubzuInfo>> hubzuInfoGroupedList = hubzuDBResponse.getHubzuInfos().stream()
                        .sorted(Comparator.comparing(HubzuInfo::getListStrtDateDtNn).reversed())
                        .collect(Collectors.groupingBy(hbzData -> hbzData.getSelrPropIdVcNn()))
                        .values().stream()
                        .collect(Collectors.toList());

                hubzuInfoGroupedList.forEach(hubzuInfoGroup -> {
                    hubzuMap.put(hubzuInfoGroup.get(0).getSelrPropIdVcNn(), hubzuInfoGroup.get(0));
                });
            }
        } else {
            log.error("No response received from Hubzu db ");
        }
        return hubzuMap;
    }

    /**
     *
     */
    @Override
    public String findFirstListEndDate() throws SystemException {
        DPProcessWeekNParam processWeekNParam = dpProcessWeekNParamsDao.findFirstByOrderByMostRecentListEndDateDesc();
        String dt = null;
        if (Objects.nonNull(processWeekNParam)) {
            try {
                dt = DateConversionUtil.DATE_YYYY_MMM_DD_FORMATTER.format(DateConversionUtil.DATE_YYYY_MM_DD_FORMATTER.parse(processWeekNParam.getMostRecentListEndDate())).toUpperCase();
            } catch (ParseException e) {
                throw SystemException.newSystemException(CoreExceptionCodes.DPWKN0114, e.getMessage());
            }
        } else
            throw SystemException.newSystemException(CoreExceptionCodes.DPWKN0113);

        return dt;
    }


}
