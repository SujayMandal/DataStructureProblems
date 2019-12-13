package com.ca.umg.business.batching.delegate;

import static com.ca.umg.business.batching.dao.BatchDashboardColumnEnum.FAILED_COUNT;
import static com.ca.umg.business.batching.dao.BatchDashboardColumnEnum.SUCCESS_COUNT;
import static com.ca.umg.business.batching.dao.BatchDashboardColumnEnum.valueOfByHeaderName;
import static java.lang.Long.valueOf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;

import com.ca.framework.core.batch.TransactionStatus;
import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.delegate.AbstractDelegate;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.info.tenant.TenantInfo;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.framework.core.util.UmgFileProxy;
import com.ca.umg.business.batching.bo.BatchTransactionBO;
import com.ca.umg.business.batching.dao.BatchDashboardColumnEnum;
import com.ca.umg.business.batching.dao.BatchDashboardFilter;
import com.ca.umg.business.batching.dao.BatchDashboardPageInfo;
import com.ca.umg.business.batching.dao.BatchTransactionInfoWrapper;
import com.ca.umg.business.batching.entity.BatchTransaction;
import com.ca.umg.business.batching.execution.BatchDataContainer;
import com.ca.umg.business.batching.execution.BatchExecuterPool;
import com.ca.umg.business.batching.execution.RequestObj;
import com.ca.umg.business.batching.execution.SingleTask;
import com.ca.umg.business.batching.execution.SingleTaskBasic;
import com.ca.umg.business.batching.info.BatchTransactionInfo;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.integration.runtime.RuntimeIntegrationClient;
import com.ca.umg.business.tenant.delegate.TenantDelegate;
import com.ca.umg.business.util.AdminUtil;
import com.ca.umg.plugin.commons.excel.converter.JsonToExcelConverter;
import com.ca.umg.plugin.commons.excel.model.ExcelData;
import com.ca.umg.plugin.commons.excel.reader.ExcelReader;
import com.hazelcast.util.StringUtil;

@SuppressWarnings("PMD")
@Named
public class BatchingDelegateImpl extends AbstractDelegate implements //NOPMD
        BatchingDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchingDelegateImpl.class);

    @Autowired
    private BatchTransactionBO batchTransactionBO;

    @Autowired
    private BatchExecuterPool batchExecuterPool;

    @Autowired
    private BatchDataContainer batchDataContainer;

    @Autowired
    private RuntimeIntegrationClient runtimeIntegrationClient;

    @Inject
    private ExcelReader excelReader;

    @Inject
    private SystemParameterProvider systemParameterProvider;

    @Inject
    private TenantDelegate tenantDelegate;

    @Inject
    private JsonToExcelConverter jsonToExcelConverter;

    @Inject
    private UmgFileProxy umgFileProxy;  

    @Inject
    private CacheRegistry cacheRegistry;
    
    public static final int DEFAULT_PAGE_SIZE = 500;
    
    public static final int MAX_DISPLAY_RECORDS_SIZE = 50000;

    @Override
    @PreAuthorize("hasRole(@accessPrivilege.getDashboardBatchBulkUpload())")
    public String executeBatchAsync(final String tenantUrl, // NOPMD
            final String fileName, final InputStream excelInputStream) throws SystemException, BusinessException {
        String batchId = null;
        if (StringUtils.isNotBlank(fileName) && excelInputStream != null) {
            final String tenantCode = RequestContext.getRequestContext().getTenantCode();
            batchId = batchTransactionBO.createBatch(fileName);
            final String bid = batchId;
            final int bulkRunMode = AdminUtil.getTestBatchRunMode(systemParameterProvider
                    .getParameter(SystemConstants.BULK_TEST_MODE));
            ExecutorService service = Executors.newFixedThreadPool(1);// TODO
                                                                      // use
                                                                      // spring
                                                                      // task
                                                                      // executor
                                                                      // service.
            service.submit(new Runnable() {

                @Override
                public void run() {
                    List<SingleTask> tasks = null;
                    SingleTask task = null;
                    List<SingleTaskBasic> basicTasks = null;
                    SingleTaskBasic basicTask = null;
                    RequestObj requestObj = null;
                    Properties properties = new Properties();
                    properties.put(RequestContext.TENANT_CODE, tenantCode);
                    RequestContext reqeustContext = new RequestContext(properties);

                    List<Map<String, Object>> jsonList = null;
                    try {
                        ExcelData excelData = parseExcelSheet(excelInputStream, fileName);
                        if (excelData.getExcelData() != null) {
                            jsonList = excelData.getExcelData();
                            updateBatchId(jsonList, bid);
                            batchTransactionBO.updateBatch(bid, jsonList.size(), excelData);
                            Map<String, TenantInfo> tenantMap = cacheRegistry.getMap(FrameworkConstant.TENANT_MAP);
                            TenantInfo tenantInfo = tenantMap.get(tenantCode);                        
                            String authToken = tenantInfo.getActiveAuthToken();
                            if(authToken == null){
                                throw new Exception("Either authToken not Active or authToken not yet set in TENANT_MAP");
                            }
                            StringBuffer outputXlslFile = getOutputXLSFile(fileName, bid);
                            if (CollectionUtils.isNotEmpty(jsonList)) {
                                batchDataContainer.initializeForBatch(bid, jsonList.size());
                                if (bulkRunMode == BusinessConstants.NUMBER_ONE) {
                                    basicTasks = new ArrayList<>();
                                    basicTask = new SingleTaskBasic(jsonList, bid, batchDataContainer, runtimeIntegrationClient,
                                            batchTransactionBO, tenantUrl, tenantCode, authToken, jsonToExcelConverter,
                                            outputXlslFile
.toString(), cacheRegistry);
                                    basicTasks.add(basicTask);
                                    batchExecuterPool.runTask(basicTasks, bid);
                                } else {
                                    tasks = new ArrayList<>();
                                    jsonToExcelConverter.start(bid, outputXlslFile.toString());
                                    int count = 1;
                                    for (Map<String, Object> jsonData : jsonList) {
                                        requestObj = new RequestObj(bid, jsonData);
                                        task = new SingleTask(requestObj, batchDataContainer, runtimeIntegrationClient,
                                                batchTransactionBO, tenantUrl, tenantCode, authToken, jsonToExcelConverter,
                                                count++, cacheRegistry);
                                        tasks.add(task);
                                    }
                                    batchExecuterPool.runTask(tasks, bid);
                                }
                            }
                        } else {
                            writeErrorResponse(excelData.getModifiedExcel());
                            updateBatchErrorStatus(bid, excelData, FilenameUtils.getBaseName(fileName) + "-response-" + bid
                                    + ".xls");
                        }
                    } catch (Exception exp) { // NOPMD
                        LOGGER.error(String.format("Error in parsing excel while executing batch id :: %s", bid), exp);
                        updateBatchErrorStatus(bid);
                    } finally {
                        reqeustContext.destroy();
                    }
                }

                private StringBuffer getOutputXLSFile(final String fileName, final String bid) throws SystemException {
                    StringBuffer tenantSanBase = new StringBuffer(AdminUtil.getSanBasePath(umgFileProxy
                            .getSanPath(systemParameterProvider.getParameter(SystemConstants.SAN_BASE))));
                    return new StringBuffer(tenantSanBase).append(File.separator).append(BusinessConstants.BATCH_FILE)
                            .append(File.separator).append(BusinessConstants.BATCH_TEST).append(File.separator)
                            .append(BusinessConstants.OUTPUT_FOLDER).append(File.separator)
                            .append(FilenameUtils.getBaseName(fileName)).append("-response-" + bid + ".xls");
                }

                private void writeErrorResponse(byte[] errorXLS) throws SystemException {
                    File file = new File(getOutputXLSFile(fileName, bid).toString());
                    FileOutputStream fileOut = null;
                    try {
                        file.createNewFile();
                        fileOut = new FileOutputStream(file);
                        fileOut.write(errorXLS);
                    } catch (Exception exp) { // NOPMD
                        LOGGER.error(String.format("Error in writing error response for batch id :: %s", bid), exp);
                    }
                    finally {
                    	if(fileOut != null)
                    	IOUtils.closeQuietly(fileOut);
                    }
                }
            });
            service.shutdown();
        } else {
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000099,
                    new Object[] { "Invalid Input. File could not be processed!" });
        }
        return batchId;
    }

    public ExcelData parseExcelSheet(InputStream excelInputStream, String fileName) throws BusinessException {
        return excelReader.parseXLData(excelInputStream, fileName);
    }

    /**
     * Updates the failed batch status as {@link TransactionStatus#ERROR}
     * 
     * @param batchId
     */
    public void updateBatchErrorStatus(String batchId) {
        updateBatchErrorStatus(batchId, null, null);
    }

    public void updateBatchErrorStatus(String batchId, ExcelData excelData, String outputFileName) {
        try {
            batchTransactionBO.updateBatch(batchId, excelData, BusinessConstants.NUMBER_ZERO, BusinessConstants.NUMBER_ZERO,
                    BusinessConstants.NUMBER_ZERO, TransactionStatus.ERROR.getStatus(),
                    outputFileName);
        } catch (SystemException | BusinessException exp) {
            LOGGER.error(String.format("Error updating batch status as ERROR. Batch Id :: %s", batchId), exp);
        }
    }

    @Override
    public void invalidateBatch(String batchId) throws SystemException, BusinessException {
        batchTransactionBO.invalidateBatch(batchId);
    }

    @Override
    public byte[] getBatchInputFileContent(String batchId) throws SystemException, BusinessException {
        return batchTransactionBO.getBatchInputFileContent(batchId);
    }

    @Override
    public byte[] getBatchOutputFileContent(String batchId) throws SystemException, BusinessException {
        return batchTransactionBO.getBatchOutputFileContent(batchId);
    }

    @Override
    public byte[] getBatchFileContent(BatchTransaction batchTran, String fileType) throws SystemException, BusinessException {
        return batchTransactionBO.getBatchFileContent(batchTran, fileType);
    }

    @Override
    @PreAuthorize("hasRole(@accessPrivilege.getDashboardBatchBulkDownloadIO())")
    public BatchTransaction getBatch(String batchId) {
        return batchTransactionBO.getBatch(batchId);
    }

    @Override
    @PreAuthorize("hasRole(@accessPrivilege.getDashboardBatchBulkUpload())")
    public void saveExcelFile(MultipartFile excelFile, String fileName) throws SystemException, BusinessException {
        StringBuffer filePathBfr = new StringBuffer(AdminUtil.getSanBasePath(umgFileProxy.getSanPath(systemParameterProvider
                .getParameter(SystemConstants.SAN_BASE))));
        filePathBfr.append(File.separatorChar).append(BusinessConstants.BATCH_FILE).append(File.separatorChar)
                .append(BusinessConstants.BATCH_TEST).append(File.separatorChar).append(BusinessConstants.ARCHIEVE_FOLDER)
                .append(File.separatorChar).append(fileName);
        File file = new File(filePathBfr.toString());
        try {
            tenantDelegate.writeFileToDirectory(file, excelFile.getInputStream());
        } catch (IOException e) {
            throw new SystemException(BusinessExceptionCodes.BSE000005, new Object[] {}, e);
        }
    }

    @PreAuthorize("hasRole(@accessPrivilege.getDashboardBatchBulk())")
    public BatchTransactionInfoWrapper getPagedBatchData(final BatchDashboardFilter filter) throws SystemException,
            BusinessException {
        checkValidFromToDates(filter);
        final List<BatchTransactionInfo> batchTxns = batchTransactionBO.getPagedBatchData(filter, isEmptySearch(filter)); 

        sortRecordsByStatusCount(batchTxns, filter);

        final BatchTransactionInfoWrapper wrapper = new BatchTransactionInfoWrapper();
        wrapper.setBatchTransactionInfoList(batchTxns);
        wrapper.setPageInfo(createPageInfo(filter));

        wrapper.setSearchResultMessage(formSearchResultMessage(filter, filter.getMatchedTransactionCount(), batchTxns.size()));
        wrapper.setToatlCount(filter.getMatchedTransactionCount());
        wrapper.setTenantConfigsMap(((TenantInfo) cacheRegistry.getMap(FrameworkConstant.TENANT_MAP)
                .get(RequestContext.getRequestContext().getTenantCode())).getTenantConfigsMap());
        return wrapper;
    }

    private void sortRecordsByStatusCount(final List<BatchTransactionInfo> batchTxns, final BatchDashboardFilter filter) {
        final BatchDashboardColumnEnum sortColumn = valueOfByHeaderName(filter.getSortColumn());
        if (isFilterByBatchStatusCount(sortColumn)) {
            Collections.sort(batchTxns, new StatusCountComparator(sortColumn, filter.isDescending()));
        }
    }

    private boolean isFilterByBatchStatusCount(final BatchDashboardColumnEnum sortColumn) {
        if (sortColumn == SUCCESS_COUNT || sortColumn == FAILED_COUNT) {
            return true;
        } else {
            return false;
        }
    }

    private void checkValidFromToDates(final BatchDashboardFilter filter) throws BusinessException {
        filter.setStartTime(AdminUtil.getMillisFromEstToUtc(filter.getFromDate(), null));
        filter.setEndTime(AdminUtil.getMillisFromEstToUtc(filter.getToDate(), null));

        if (filter.getStartTime() != null && filter.getEndTime() != null && filter.getStartTime() > filter.getEndTime()) {
            LOGGER.error("BSE000076 : The Batch Transaction Run Dates Range is invalid");
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000076, new Object[] {});
        } else if (filter.getEndTime() != null && filter.getStartTime() == null) {
            LOGGER.error("BSE000078 : Batch RunDateFrom missing. Please specify the RunDateFrom");
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000078, new Object[] {});
        }
    }
   
    private BatchDashboardPageInfo createPageInfo(final BatchDashboardFilter filter) {
        final BatchDashboardPageInfo pi = new BatchDashboardPageInfo();
        pi.setPageSize(filter.getPageSize());
        pi.setTotalRecords(filter.getMatchedTransactionCount());
        pi.setTotalPages(((Double) Math.ceil(new Double(filter.getMatchedTransactionCount()) / filter.getPageSize())).longValue());
        return pi;
    }

    class StatusCountComparator implements Comparator<BatchTransactionInfo> {

        private final BatchDashboardColumnEnum sortColumn;
        private final boolean sortOrder;

        private StatusCountComparator(final BatchDashboardColumnEnum sortColumn, final boolean sortOrder) {
            this.sortColumn = sortColumn;
            this.sortOrder = sortOrder;
        }

        @Override
        public int compare(BatchTransactionInfo o1, BatchTransactionInfo o2) {
            final Long value1 = getCount(o1);
            final Long value2 = getCount(o2);

            if (sortOrder) {
                return value2.compareTo(value1);
            } else {
                return value1.compareTo(value2);
            }
        }

        private Long getCount(final BatchTransactionInfo bti) {
            Long value;
            if (sortColumn == FAILED_COUNT) {
                value = bti.getFailCount();
            } else {
                value = bti.getSuccessCount();
            }

            return value == null ? valueOf(0l) : value;
        }
    }

    private void updateBatchId(final List<Map<String, Object>> jsonList, final String batchId) {
        if (jsonList != null) {
            for (final Map<String, Object> list : jsonList) {
                if (list.containsKey("header")) {
                    Map<String, Object> headerMap = (Map<String, Object>) list.get("header");
                    headerMap.put("batchId", batchId);
                }
            }
        }
    }

    @Override
    public String formSearchResultMessage(final BatchDashboardFilter filter, final long totalCount, final long returnCount) {
        String searchResultMessage = "";

        if (isEmptySearch(filter)) {
            if (totalCount <= filter.getPageSize()) {
                searchResultMessage = "Showing all " + totalCount + " records";
            } else {
                searchResultMessage = "Showing latest " + filter.getPageSize() + " records";
            }
        } else {
            if (totalCount <= filter.getPageSize()) {
                searchResultMessage = "Showing all " + returnCount + " resulting records";
            } else {
                searchResultMessage = "Showing latest " + filter.getPageSize() + " of " + totalCount + " resulting records";
            }

            if (totalCount > MAX_DISPLAY_RECORDS_SIZE) {
                searchResultMessage = "Search is resulting in more than 50,000 records, please refine your criteria";
            }
        }

        return searchResultMessage;
    }

    @Override
    public boolean isEmptySearch(final BatchDashboardFilter filter) {
        boolean emptySearch = true;

        if (StringUtil.isNullOrEmpty(filter.getBatchId()) && StringUtil.isNullOrEmpty(filter.getInputFileName())
                && StringUtil.isNullOrEmpty(filter.getFromDate()) && StringUtil.isNullOrEmpty(filter.getToDate())) {
            emptySearch = true;
        } else {
            emptySearch = false;
        }

        return emptySearch;
    }

    @Override
    @PreAuthorize("hasRole(@accessPrivilege.getDashboardBatchBulkTerminateBatch())")
    public String terminateBatch(final String batchId) {
        return batchTransactionBO.terminateBatch(batchId);
    }

    @Override
    @PreAuthorize("hasRole(@accessPrivilege.getDashboardBatchBulkUpload())")
    public void saveBulkFile(MultipartFile jsonFile, String fileName) throws SystemException, BusinessException {
        StringBuffer filePathBfr = new StringBuffer(AdminUtil.getSanBasePath(umgFileProxy.getSanPath(systemParameterProvider
                .getParameter(SystemConstants.SAN_BASE))));
        filePathBfr.append(File.separatorChar).append(BusinessConstants.BULK_FILE).append(File.separatorChar)
                .append(BusinessConstants.BULK_INPUT).append(File.separatorChar).append(fileName);
        File file = new File(filePathBfr.toString());
        try {
            tenantDelegate.writeFileToDirectory(file, jsonFile.getInputStream());
        } catch (IOException e) {
            throw new SystemException(BusinessExceptionCodes.BSE000005, new Object[] {}, e);
        }
    }

}