package com.ca.umg.business.transaction.delegate;

import static com.ca.pool.TransactionMode.ANY;
import static com.ca.pool.TransactionType.PROD;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.access.prepost.PreAuthorize;

import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.delegate.AbstractDelegate;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.exception.codes.FrameworkExceptionCodes;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.framework.core.util.ConversionUtil;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.framework.core.util.UmgFileProxy;
import com.ca.umg.business.batching.dao.BatchRuntimeTransactionDAO;
import com.ca.umg.business.common.info.PagingInfo;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.transaction.bo.TransactionBO;
import com.ca.umg.business.transaction.dao.TransactionExcelReportDAO;
import com.ca.umg.business.transaction.entity.Transaction;
import com.ca.umg.business.transaction.info.AdvanceTransactionFilter;
import com.ca.umg.business.transaction.info.BasicSearchCriteria;
import com.ca.umg.business.transaction.info.TransactionFilter;
import com.ca.umg.business.transaction.info.TransactionFilterForApi;
import com.ca.umg.business.transaction.info.TransactionInfo;
import com.ca.umg.business.transaction.info.TransactionVersionInfo;
import com.ca.umg.business.transaction.info.TransactionWrapper;
import com.ca.umg.business.transaction.info.TransactionWrapperForApi;
import com.ca.umg.business.transaction.mongo.bo.TransactionDocBO;
import com.ca.umg.business.transaction.mongo.dao.MongoTransactionDAO;
import com.ca.umg.business.transaction.mongo.dao.MongoTransactionDAOImpl;
import com.ca.umg.business.transaction.mongo.entity.TransactionDocument;
import com.ca.umg.business.transaction.mongo.info.TransactionDocumentForApi;
import com.ca.umg.business.transaction.mongo.info.TransactionDocumentInfo;
import com.ca.umg.business.transaction.query.Operator;
import com.ca.umg.business.transaction.util.TransactionUtil;
import com.ca.umg.business.util.AdminUtil;
import com.ca.umg.business.version.bo.VersionBO;
import com.ca.umg.business.version.entity.Version;
import com.hazelcast.util.StringUtil;

@SuppressWarnings("PMD")
@Named
public class TransactionDelegateImpl extends AbstractDelegate implements TransactionDelegate {

    private static final int HAS_ONLY_MAJOR_VERSION = 0;

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionDelegateImpl.class.getName());

    private static final int HAS_MAJOR_AND_MINOR_VERSION = 2;
    
    private static final String FILE_NOT_AVILABLE_FOR_DOWNLOAD_MSG = "Model output not available for download.";
    
    @Inject
    private TransactionBO transactionBO;

    @Inject
    private VersionBO versionBO;

    @Inject
    private TransactionExcelReportDAO transactionExcelReportDAO;

    @Inject
    private SystemParameterProvider systemParameterProvider;

    @Inject
    private TransactionDocBO transactionDocBO;

    @Inject
    private BatchRuntimeTransactionDAO batchRuntimeTxnDAO;  

    @Inject
    private UmgFileProxy umgFileProxy;
    
    @Inject
    private MongoTransactionDAO mongoTransactionDAO;
    
    @Override
    public TransactionWrapper listAll(TransactionFilter transactionFilter) throws BusinessException, SystemException {
        setTransactionFilter(transactionFilter);
        Page<Transaction> txnList = transactionBO.listAll(transactionFilter);
        List<TransactionInfo> txnInfoList = convertToList(txnList.getContent(), TransactionInfo.class);

        PagingInfo responsePageInfo = new PagingInfo();
        responsePageInfo.setPage(txnList.getNumber() + 1);
        responsePageInfo.setPageSize(txnList.getSize());
        responsePageInfo.setTotalPages(txnList.getTotalPages());
        responsePageInfo.setTotalElements(txnList.getTotalElements());
        TransactionWrapper transactionWrapper = constructTransactionWrapper(txnInfoList);
        transactionWrapper.setPagingInfo(responsePageInfo);
        return transactionWrapper;
    }

    @Override
    public List<String> getOperatorList() throws BusinessException, SystemException {
        List<String> operatorList = new ArrayList<>();
        operatorList.add(Operator.GREATER_THAN.getOperatoreValue());
        operatorList.add(Operator.GREATER_THAN_EQUAL.getOperatoreValue());
        operatorList.add(Operator.LESS_THAN.getOperatoreValue());
        operatorList.add(Operator.LESS_THAN_EQUAL.getOperatoreValue());
        operatorList.add(Operator.EQUAL.getOperatoreValue());
        operatorList.add(Operator.NOT_EQUAL.getOperatoreValue());
        operatorList.add(Operator.LIKE.getOperatoreValue());
        return operatorList;
    }

    /**
     * This method will manually set the paging information base on number of record in the list, and existing pagingInfo
     */
    private PagingInfo setPagingForJDBCQuery(List<TransactionInfo> txnInfoList, PagingInfo currentPageInfo) {
        PagingInfo newPageInfo = new PagingInfo();
        if (txnInfoList != null && !txnInfoList.isEmpty() && currentPageInfo != null) {
            newPageInfo.setPageSize(currentPageInfo.getPageSize());
            double totalRecord = txnInfoList.size();
            Double totalPages = Math.ceil(totalRecord / currentPageInfo.getPageSize());
            newPageInfo.setTotalPages(totalPages.intValue());
            newPageInfo.setPage(currentPageInfo.getPage() > totalPages.intValue() ? 1 : currentPageInfo.getPage());
        }
        return newPageInfo;
    }

    /**
     * This method will trim the list based on the pagingInfo
     * 
     * @param dataList
     * @param pageInfo
     * @return
     */
    private List<TransactionInfo> getTrimedList(List<TransactionInfo> dataList, PagingInfo pageInfo) {
        List<TransactionInfo> subList = null;
        if (dataList != null) {
            int recordSize = dataList.size();
            int fromIndex = pageInfo.getPage() * pageInfo.getPageSize() - pageInfo.getPageSize();
            int toIndex = (fromIndex + pageInfo.getPageSize()) >= recordSize ? recordSize : fromIndex + pageInfo.getPageSize();

            if (recordSize >= fromIndex) {
                subList = dataList.subList(fromIndex, toIndex);
            }
        }
        return subList;
    }

    /**
     * 
     * This method constructs TransactionWrapper(transactionInfoList,libraryNameList and tenantModelNameList) from list
     * Transactions
     * 
     * @param txnInfoList
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    private TransactionWrapper constructTransactionWrapper(List<TransactionInfo> txnInfoList) throws BusinessException,
            SystemException {
        TransactionWrapper transactionWrapper = new TransactionWrapper();
        List<TransactionInfo> txnDateInfoList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(txnInfoList)) {
            for (TransactionInfo transactionInfo : txnInfoList) {
                transactionInfo.setRunAsOfDateTime(AdminUtil.getDateFormatMillisForEst(transactionInfo.getRunAsOfDate()
                        .getMillis(), null));
                txnDateInfoList.add(transactionInfo);
            }
        }
        fillTransactionVersionDetails(txnDateInfoList);
        transactionWrapper.setTransactionInfoList(txnDateInfoList);
        List<String> libraryNameList = transactionBO.findAllLibraries();
        List<String> tenantModelNameList = transactionBO.findAllTenantModelNames();
        transactionWrapper.setLibraryNameList(libraryNameList);
        transactionWrapper.setTenantModelNameList(tenantModelNameList);
        return transactionWrapper;
    }

    private TransactionWrapper constructTransactionWrapper1(List<TransactionDocumentInfo> txnInfoList) throws BusinessException,
            SystemException {
        TransactionWrapper transactionWrapper = new TransactionWrapper();
        List<TransactionDocumentInfo> txnDateInfoList = new ArrayList<TransactionDocumentInfo>();
        if (CollectionUtils.isNotEmpty(txnInfoList)) {
            LOGGER.info("Started constructing transaction wrapper object for {} transactions.", txnInfoList.size());
            for (TransactionDocumentInfo transactionInfo : txnInfoList) {
                transactionInfo.setRunAsOfDateTime(AdminUtil.getDateFormatMillisForEst(transactionInfo.getRunAsOfDate(), null));
                transactionInfo.setCreatedDateTime(AdminUtil.getDateFormatMillisForEst(transactionInfo.getCreatedDate(), null));
                transactionInfo.setModeletHostPortInfo(transactionInfo.getModeletServerHost(), transactionInfo.getModeletServerPort() , transactionInfo.getrServePort() );
                txnDateInfoList.add(transactionInfo);
            }
        }
        // fillTransactionVersionDetails1(txnDateInfoList);
        transactionWrapper.setTransactionDocumentInfos(txnDateInfoList);
        
        List<String> tenantModelNameList = mongoTransactionDAO.fetchDistinctVersionName();
        
/*        List<String> libraryNameList = transactionBO.findAllLibraries();
*/        /*List<String> tenantModelNameList = transactionBO.findAllTenantModelNames();*/
/*        transactionWrapper.setLibraryNameList(libraryNameList);
*/        transactionWrapper.setTenantModelNameList(tenantModelNameList);
        LOGGER.info("Finished constructing transaction wrapper object for transactions.");
        return transactionWrapper;
    }
    
    /**
     * populates the date in "yyyy-MMM-dd HH:mm" format for each transaction of RA Api and updates the tenant output
     * made changes for added for umg-4849   
     * @param txnInfoList
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    private TransactionWrapperForApi constructTranWrapperForApi(List<TransactionDocument> transactionDocumentsList) throws BusinessException,
        SystemException {
        TransactionWrapperForApi baseTransactionWrapper = null;
        List<TransactionDocumentForApi> txnDocForApiList = null;
        try {
            baseTransactionWrapper = new TransactionWrapperForApi();
            txnDocForApiList = new ArrayList<TransactionDocumentForApi>();
            if (CollectionUtils.isNotEmpty(transactionDocumentsList)) {
                LOGGER.info("Started constructing transaction wrapper object for {} transactions.", transactionDocumentsList.size());
                Map<String, Object> tntOutputMap = null;
                Map<String, Object> tntInputMap = null;
                for (TransactionDocument transactionDocument : transactionDocumentsList) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    if (transactionDocument.getTenantOutput() != null) {
                        byte[] jsonByteArray = objectMapper.writeValueAsBytes(transactionDocument.getTenantOutput());
                        tntOutputMap = ConversionUtil.convertJson(jsonByteArray, Map.class);
                    }
                    
                    if (transactionDocument.getTenantInput() != null) {
                        byte[] jsonByteArray = objectMapper.writeValueAsBytes(transactionDocument.getTenantInput());
                        tntInputMap = ConversionUtil.convertJson(jsonByteArray, Map.class);
                    } 
                    
                    TransactionDocumentForApi documentForApi = convert(transactionDocument, TransactionDocumentForApi.class);
                    documentForApi.setRunAsOfDateTime(AdminUtil.getDateFormatMillisForUTC(documentForApi.populateRunAsOfDate(), null));
                    documentForApi.setCreatedDateTime(AdminUtil.getDateFormatMillisForUTC(documentForApi.populateCreatedDate(), null));
                    documentForApi.setTenantOutput(tntOutputMap);
                    documentForApi.setTenantInput(tntInputMap);
                    txnDocForApiList.add(documentForApi);
            }
            
        }
        baseTransactionWrapper.setTransactions(txnDocForApiList);
        } catch (IOException ex) {
            LOGGER.error("An error occurred while converting tenant output json for api",ex);
            SystemException.newSystemException(FrameworkExceptionCodes.BSE000009,
                    new Object[] { String.format("An error occurred while converting tenant output json for api") });
        }
        LOGGER.info("Finished constructing transaction wrapper object for api transactions.");
        return baseTransactionWrapper;
    }

    private void fillTransactionVersionDetails(List<TransactionInfo> txnInfoList) throws BusinessException, SystemException {
        for (TransactionInfo transactionInfo : txnInfoList) {
            Version version = versionBO.findByNameAndVersion(transactionInfo.getTenantModelName(),
                    transactionInfo.getMajorVersion(), transactionInfo.getMinorVersion());
            if (version != null) {
                TransactionVersionInfo transactionVersionInfo = new TransactionVersionInfo();
                transactionVersionInfo.setPublishedBy(version.getPublishedBy());
                transactionVersionInfo.setPublishedOn(version.getPublishedOn());
                transactionVersionInfo.setStatus(version.getStatus());
                transactionVersionInfo.setUmgLibraryName(version.getModelLibrary().getUmgName());
                transactionVersionInfo.setUmgTidName(version.getMapping().getName());
                transactionVersionInfo.setUmgModelName(version.getMapping().getModel().getUmgName());
                transactionInfo.setTransactionVersionInfo(transactionVersionInfo);
                transactionInfo.setEnvironment(version.getModelLibrary().getExecutionLanguage());
                transactionInfo.setTransactionMode(version.getModelType());
            }
        }
    }

    /**
     * This method validate versions,from date, to date and sets the validated data to TransactionFilter
     * 
     * @param transactionFilter
     * @throws BusinessException
     * @throws SystemException
     */
    private void setTransactionFilter(TransactionFilter transactionFilter) throws BusinessException, SystemException {
        fillVersions(transactionFilter);
        transactionFilter.setRunAsOfDateFrom(AdminUtil.getMillisFromEstToUtc(transactionFilter.getRunAsOfDateFromString(), null));
        transactionFilter.setRunAsOfDateTo(AdminUtil.getMillisFromEstToUtc(transactionFilter.getRunAsOfDateToString(), null));
        checkValidFromToDates(transactionFilter);
        fillMaxDateIfRunAsOfToDateIsAbsent(transactionFilter);
    }
    
    /**
     * This method validate versions,from date, to date (in utc fromat) for RaApi and sets the validated data to TransactionFilter
     * 
     * @param transactionFilter
     * @throws BusinessException
     * @throws SystemException
     */
    private void setTransactionFilterForApi(TransactionFilter transactionFilter) throws BusinessException, SystemException {
        fillVersions(transactionFilter);
        transactionFilter.setRunAsOfDateFrom(AdminUtil.getMillisForUtc(transactionFilter.getRunAsOfDateFromString(), null));
        transactionFilter.setRunAsOfDateTo(AdminUtil.getMillisForUtc(transactionFilter.getRunAsOfDateToString(), null));
        checkValidFromToDates(transactionFilter);
        fillMaxDateIfRunAsOfToDateIsAbsent(transactionFilter);
    }

    private void fillMaxDateIfRunAsOfToDateIsAbsent(TransactionFilter transactionFilter) throws BusinessException,
            SystemException {
        if (transactionFilter.getRunAsOfDateTo() == null) {
            transactionFilter.setRunAsOfDateTo(transactionBO.getMaxRunAsOfDate());
        }
    }

    private void checkValidFromToDates(TransactionFilter transactionFilter) throws BusinessException {
        if (transactionFilter.getRunAsOfDateTo() != null && transactionFilter.getRunAsOfDateFrom() != null
                && transactionFilter.getRunAsOfDateFrom() > transactionFilter.getRunAsOfDateTo()) {
            LOGGER.error("BSE000076 : The Transaction Run Dates Range is invalid");
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000076, new Object[] {});
        }
        // UMG-2064
        /*
         * else if(transactionFilter.getRunAsOfDateTo() != null && transactionFilter.getRunAsOfDateFrom() == null){
         * LOGGER.error("BSE000078 : RunDateFrom missing. Please specify the RunDateFrom");
         * BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000078, new Object[]{}); }
         */
    }

    private void fillVersions(TransactionFilter transactionFilter) throws BusinessException {
        if (StringUtils.isNotBlank(transactionFilter.getFullVersion())) {
            try {
                String[] versionArr = transactionFilter.getFullVersion().split("\\.");
                if (versionArr.length > HAS_MAJOR_AND_MINOR_VERSION) {
                    BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000077, new Object[] {});
                } else if (versionArr.length == HAS_ONLY_MAJOR_VERSION) {
                    transactionFilter.setMajorVersion(Integer.valueOf(transactionFilter.getFullVersion()));
                } else {
                    Integer majorVersion = Integer.valueOf(versionArr[0]);
                    if (versionArr.length == HAS_MAJOR_AND_MINOR_VERSION) {
                        transactionFilter.setMajorVersion(majorVersion);
                        transactionFilter.setMinorVersion(Integer.valueOf(versionArr[1]));
                    } else {
                        transactionFilter.setMajorVersion(majorVersion);
                    }

                }
            } catch (NumberFormatException excp) {
                LOGGER.error("BSE000077 : The Transaction Version data format is invalid. Valid Formats are : [Integer], [Integer.Integer]");
                BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000077, new Object[] {});
            }
        }
    }

    @Override
    public SqlRowSet loadTransactionsRowSet(final TransactionFilter filter, final String tenantId) throws SystemException,
            BusinessException {
        fillVersions(filter);
        setRunAsOfDate(filter, tenantId);
        return transactionExcelReportDAO.loadTransactionsRowSet(filter, tenantId);
    }

    private void setRunAsOfDate(final TransactionFilter transactionFilter, final String tenantId) throws BusinessException,
            SystemException {
        transactionFilter.setRunAsOfDateFrom(AdminUtil.getMillisFromEstToUtc(transactionFilter.getRunAsOfDateFromString(), null));
        transactionFilter.setRunAsOfDateTo(AdminUtil.getMillisFromEstToUtc(transactionFilter.getRunAsOfDateToString(), null));
        checkValidFromToDates(transactionFilter);

        final Long runAsOfDateFrom = transactionFilter.getRunAsOfDateFrom();
        final Long runAsOfDateTo = transactionFilter.getRunAsOfDateTo();

        if (runAsOfDateFrom != null && runAsOfDateTo != null) {
            return;
        }

        final Map<String, Object> map = transactionExcelReportDAO.getMinAndMaxRunAsOfDate(transactionFilter, tenantId);

        if (runAsOfDateFrom == null) {
            transactionFilter.setRunAsOfDateFrom((Long) map.get("mindate"));
        }

        if (runAsOfDateTo == null) {
            transactionFilter.setRunAsOfDateTo((Long) map.get("maxdate"));
        }
    }

    /**
     * creates a list of BasicSearchCriteria by checking the transactionfilter object
     * 
     * @param transactionFilter
     * @return list of {@link BasicSearchCriteria}
     * @throws BusinessException
     */
    private KeyValuePair<List<BasicSearchCriteria>, List<String>> createListOfBasicSearchCriteria(
            TransactionFilter transactionFilter) throws BusinessException {

        List<BasicSearchCriteria> basicSearchCriterias = null;

        List<String> fieldCriteras = new ArrayList<String>();
        try {
            basicSearchCriterias = new ArrayList<>();          
            if (StringUtils.isNotBlank(transactionFilter.getTenantModelName()) && !StringUtils.equalsIgnoreCase(transactionFilter.getTenantModelName(),BusinessConstants.ANY) ) {
                basicSearchCriterias.add(getBasicSearchCriteria("versionName", transactionFilter.getTenantModelName(),
                        Operator.EQUAL));
                fieldCriteras.add("versionName");
            }           
            if (transactionFilter.getMajorVersion() != null) {
                basicSearchCriterias.add(getBasicSearchCriteria("majorVersion", transactionFilter.getMajorVersion(),
                        Operator.EQUAL));
            }
            if (transactionFilter.getMinorVersion() != null) {
                basicSearchCriterias.add(getBasicSearchCriteria("minorVersion", transactionFilter.getMinorVersion(),
                        Operator.EQUAL));
            }
            if (StringUtils.isNotBlank(transactionFilter.getErrorType()) && !StringUtils.equalsIgnoreCase(transactionFilter.getErrorType(),BusinessConstants.ANY) ) {
                basicSearchCriterias.add(getBasicSearchCriteria("errorCode",
                        TransactionUtil.getErrorCodePattern(transactionFilter, systemParameterProvider), Operator.LIKE));
            }else{
	            if (StringUtils.isNotBlank(transactionFilter.getTransactionStatus()) && !StringUtils.equalsIgnoreCase(transactionFilter.getTransactionStatus(),BusinessConstants.ANY) ) {
	                List<String> tranStatusList = new ArrayList<String>();
	                if (StringUtils.equalsIgnoreCase(transactionFilter.getTransactionStatus(), BusinessConstants.STATUS_SUCCESS)) {
	                    tranStatusList.add(BusinessConstants.BATCH_STATUS_SUCCESS);
	                    tranStatusList.add(BusinessConstants.STATUS_SUCCESS);
	                } else  if (StringUtils.equalsIgnoreCase(transactionFilter.getTransactionStatus(), BusinessConstants.STATUS_FAILURE)) {
	                    tranStatusList.add(BusinessConstants.BATCH_STATUS_ERROR);
	                    tranStatusList.add(BusinessConstants.STATUS_ERROR);
	                } 
	                
	                if(tranStatusList.size()>0)
	                	basicSearchCriterias.add(getBasicSearchCriteria("status", tranStatusList, Operator.IN));
	                //fieldCriteras.add("libraryName");
	            }
            }
            if (StringUtils.isNotBlank(transactionFilter.getTransactionType()) && !StringUtils.equalsIgnoreCase(transactionFilter.getTransactionType(),BusinessConstants.ANY) ) {
                Boolean isTestTransasction = Boolean.FALSE;
                if (StringUtils.equalsIgnoreCase(transactionFilter.getTransactionType(), "test")) {
                    isTestTransasction = Boolean.TRUE;
                }
                basicSearchCriterias.add(getBasicSearchCriteria("test", isTestTransasction, Operator.EQUAL));
                //fieldCriteras.add("libraryName");
            }
            
         
            if (StringUtils.isNotBlank(transactionFilter.getClientTransactionID()) && !StringUtils.equalsIgnoreCase(transactionFilter.getClientTransactionID(),BusinessConstants.ANY) ) {               
                List<String> txnIds = getListOfIdsFromString(transactionFilter.getClientTransactionID());
                basicSearchCriterias.add(getBasicSearchCriteria("clientTransactionID", txnIds, Operator.IN));              
            }
            if (StringUtils.isNotBlank(transactionFilter.getRaTransactionID()) && !StringUtils.equalsIgnoreCase(transactionFilter.getRaTransactionID(),BusinessConstants.ANY) ) {              
                List<String> txnIds = getListOfIdsFromString(transactionFilter.getRaTransactionID());
                basicSearchCriterias.add(getBasicSearchCriteria("transactionId", txnIds, Operator.IN));             
            }
            if (StringUtils.isNotBlank(transactionFilter.getBatchId()) && !StringUtils.equalsIgnoreCase(transactionFilter.getBatchId(),BusinessConstants.ANY) ) {
                List<String> batchIdList = getListOfIdsFromString(transactionFilter.getBatchId());
                List<String> txnIds = null;
                
                if(StringUtils.equalsIgnoreCase(transactionFilter.getTransactionStatus(), BusinessConstants.STATUS_IN_PROGRESS))
                	txnIds= batchRuntimeTxnDAO.getInProgressTxnIdsFromBatchId(batchIdList);
                else
                	txnIds=batchRuntimeTxnDAO.getTxnIdsFromBatchId(batchIdList);
                if (txnIds.size() > 0) {
                    basicSearchCriterias.add(getBasicSearchCriteria("transactionId", txnIds, Operator.IN));
                } else {
                    basicSearchCriterias.add(getBasicSearchCriteria("transactionId", "NaN", Operator.EQUAL));
                }
                fieldCriteras.add("transactionId");
            }           
            
            if (transactionFilter.getRunAsOfDateFrom() != null) {
                basicSearchCriterias.add(getBasicSearchCriteria("runAsOfDate", transactionFilter.getRunAsOfDateFrom(),
                        Operator.GREATER_THAN_EQUAL));
                // fieldCriteras.add("runAsOfDate");
            }
            if (transactionFilter.getRunAsOfDateTo() != null) {
                basicSearchCriterias.add(getBasicSearchCriteria("runAsOfDate", transactionFilter.getRunAsOfDateTo(),
                        Operator.LESS_THAN_EQUAL));
                // fieldCriteras.add("runAsOfDate");
            }
          /*
         
        
            if (StringUtils.isNotBlank(transactionFilter.getCreatedBy())) {
                basicSearchCriterias
                        .add(getBasicSearchCriteria("createdBy", transactionFilter.getCreatedBy(), Operator.EQUAL));
            }*/
        } catch (Exception e) { // NOPMD
            LOGGER.error("Error occurred while transforming the basic serachcriteria" + e);
            // TODO new error code to be introduced
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE0000111, new Object[] {});
        }
        return new KeyValuePair(basicSearchCriterias, fieldCriteras);
    }

    /**
     * creates a list of BasicSearchCriteria by checking the transactionfilter object
     * 
     * @param transactionFilter
     * @return list of {@link BasicSearchCriteria}
     * @throws BusinessException
     */
    private List<BasicSearchCriteria> createListOfNestedSearchCriteria(TransactionFilter transactionFilter)
            throws BusinessException {
        List<BasicSearchCriteria> basicSearchCriterias = null;
        try {
            basicSearchCriterias = new ArrayList<>();
            if (StringUtils.isNotBlank(transactionFilter.getErrorDescription())) {
                basicSearchCriterias.add(getBasicSearchCriteria("errorDescription", transactionFilter.getErrorDescription(),
                        Operator.LIKE));
                basicSearchCriterias.add(getBasicSearchCriteria("errorCode", transactionFilter.getErrorDescription(),
                        Operator.LIKE));
            }

        } catch (Exception e) { // NOPMD
            LOGGER.error("Error occurred while transforming the nested serachcriteria" + e);
            // TODO new error code to be introduced
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE0000111, new Object[] {});
        }
        return basicSearchCriterias;
    }
    
   
    private BasicSearchCriteria getBasicSearchCriteria(String key, Object value, Operator operator) {
        BasicSearchCriteria basicSrchCriteria = new BasicSearchCriteria();
        basicSrchCriteria.setSearchKey(key);
        basicSrchCriteria.setSearchValue(value);
        basicSrchCriteria.setSearchOperator(operator.getOperatoreValue());
        return basicSrchCriteria;
    }

    @PreAuthorize("hasAnyRole(@accessPrivilege.getDashboardTransaction(),@accessPrivilege.getDashboardTransactionDownloadExcelUsageReport())")
    @Override
    public TransactionWrapper searchTransactions(TransactionFilter transactionFilter,
            AdvanceTransactionFilter advanceTransactionFilter) throws BusinessException, SystemException {
        TransactionWrapper transactionWrapper = null;
        KeyValuePair<String, String> docSearchQuery = null;
        try {
            setTransactionFilter(transactionFilter);
            KeyValuePair<List<BasicSearchCriteria>, List<String>> basicSearchCriterias = createListOfBasicSearchCriteria(transactionFilter);
            List<BasicSearchCriteria> nestedCriterias = createListOfNestedSearchCriteria(transactionFilter);           
            docSearchQuery = transactionDocBO.generateTransactionDocQuery(basicSearchCriterias.getKey(),
                    nestedCriterias, advanceTransactionFilter);
            LOGGER.error("Generated document query : " + docSearchQuery);

            Page<TransactionDocument> transactionDocs = transactionDocBO.getTransactionDocuments(docSearchQuery,
                    transactionFilter.getPage(), transactionFilter.getPageSize(),
                    StringUtils.isNotBlank(transactionFilter.getSortColumn()) ? transactionFilter.getSortColumn() : "createdDate",
                    transactionFilter.isDescending(), basicSearchCriterias.getValue(), 
                    isEmptySearch(transactionFilter, advanceTransactionFilter), Boolean.FALSE, null);

            List<TransactionDocumentInfo> transactionDocumentInfos = convertToList(transactionDocs.getContent(),
                    TransactionDocumentInfo.class);

            PagingInfo responsePageInfo = new PagingInfo();
            responsePageInfo.setPage(transactionDocs.getNumber());
            responsePageInfo.setPageSize(transactionDocs.getSize());
            responsePageInfo.setTotalPages(transactionDocs.getNumber() + 1);
            responsePageInfo.setTotalElements(transactionDocs.getTotalElements());
            transactionWrapper = constructTransactionWrapper1(transactionDocumentInfos);
            transactionWrapper.setPagingInfo(responsePageInfo);
            
            transactionWrapper.setSearchResultMessage(formSearchResultMessage(transactionFilter, advanceTransactionFilter, transactionDocs.getTotalElements(), transactionDocumentInfos.size()));
            transactionWrapper.setTotalCount(transactionDocs.getTotalElements());
        } catch (SystemException e) {
            if (e.getCode().equalsIgnoreCase(BusinessExceptionCodes.BSE000702)) {
                List<TransactionDocumentInfo> transactionDocumentInfos = new ArrayList<>();
                transactionWrapper = constructTransactionWrapper1(transactionDocumentInfos);

                PagingInfo responsePageInfo = new PagingInfo();
                responsePageInfo.setPage(transactionFilter.getPage());
                responsePageInfo.setPageSize(transactionFilter.getPageSize());
                responsePageInfo.setTotalPages(transactionFilter.getPage() + 1);
                responsePageInfo.setTotalElements(new Long(systemParameterProvider.getParameter(BusinessConstants.MAX_DISPLAY_RECORDS_SIZE)));
                
                transactionWrapper.setPagingInfo(responsePageInfo);

                transactionWrapper.setSearchResultMessage("Search is taking longer than " + getMongoTimeout(docSearchQuery) + ", please refine your criteria");
                transactionWrapper.setTotalCount(Integer.valueOf(systemParameterProvider.getParameter(BusinessConstants.MAX_DISPLAY_RECORDS_SIZE)));
            }
        }
        return transactionWrapper;
    }
    
    @PreAuthorize("hasAnyRole(@accessPrivilege.getDashboardTransaction(),@accessPrivilege.getDashboardTransactionDownloadExcelUsageReport())")
    @Override
    public TransactionWrapper searchDefaultTransactions(Integer pageSize) throws BusinessException, SystemException {
        TransactionWrapper transactionWrapper = null;
        KeyValuePair<String, String> docSearchQuery = null;
        TransactionFilter txnFilterData = null;
        AdvanceTransactionFilter advanceTransactionFilter = null;
        long startDelegateTime = System.currentTimeMillis();
        try {
            Page<TransactionDocument> transactionDocs = transactionDocBO.getDefaultTransactionDocuments(pageSize);
            long startTime = System.currentTimeMillis();
            List<TransactionDocumentInfo> transactionDocumentInfos = convertToList(transactionDocs.getContent(),
                    TransactionDocumentInfo.class);
            long endTime = System.currentTimeMillis();
            LOGGER.info("**************** TIME TAKEN TO CONVERT MONGO DATA TO LIST ****************** : " + (endTime-startTime) + " ms");
            PagingInfo responsePageInfo = new PagingInfo();
            responsePageInfo.setPage(transactionDocs.getNumber());
            responsePageInfo.setPageSize(transactionDocs.getSize());
            responsePageInfo.setTotalPages(transactionDocs.getNumber() + 1);
            responsePageInfo.setTotalElements(transactionDocs.getTotalElements());
            transactionWrapper = constructTransactionWrapper1(transactionDocumentInfos);
            transactionWrapper.setPagingInfo(responsePageInfo);
            txnFilterData = ConversionUtil.convertJson("{}", TransactionFilter.class);
            advanceTransactionFilter = ConversionUtil.convertJson("{}", AdvanceTransactionFilter.class);
            txnFilterData.setPage(1);
            txnFilterData.setPageSize(pageSize);
            transactionWrapper.setSearchResultMessage(formSearchResultMessage(txnFilterData, advanceTransactionFilter, transactionDocs.getTotalElements(), transactionDocumentInfos.size()));
            transactionWrapper.setTotalCount(transactionDocs.getTotalElements());
        } catch (SystemException e) {
            if (e.getCode().equalsIgnoreCase(BusinessExceptionCodes.BSE000702)) {
                List<TransactionDocumentInfo> transactionDocumentInfos = new ArrayList<>();
                transactionWrapper = constructTransactionWrapper1(transactionDocumentInfos);

                PagingInfo responsePageInfo = new PagingInfo();
                responsePageInfo.setTotalElements(new Long(BusinessConstants.MAX_DISPLAY_RECORDS_SIZE));
                
                transactionWrapper.setPagingInfo(responsePageInfo);

                transactionWrapper.setSearchResultMessage("Search is taking longer than " + getMongoTimeout(docSearchQuery) + ", please refine your criteria");
                transactionWrapper.setTotalCount(Integer.valueOf(systemParameterProvider.getParameter(BusinessConstants.MAX_DISPLAY_RECORDS_SIZE)));
            }
        }
        long endDelegateTime = System.currentTimeMillis();
        LOGGER.info("#######################TOTAL TIME TAKEN IN DELEGATE LAYER ########################## : "+(endDelegateTime-startDelegateTime)+" ms");
        return transactionWrapper;
    }
    
    @Override
    public TransactionWrapperForApi searchTransactionsForRaApi(TransactionFilter transactionFilter,
            AdvanceTransactionFilter advanceTransactionFilter, TransactionFilterForApi transactionFilterForApi) throws BusinessException, SystemException {
        TransactionWrapperForApi transactionWrapperForApi = null;
        KeyValuePair<String, String> docSearchQuery = null;
        try {
            setTransactionFilterForApi(transactionFilter);
            KeyValuePair<List<BasicSearchCriteria>, List<String>> basicSearchCriterias = createListOfBasicSearchCriteria(transactionFilter);
            List<BasicSearchCriteria> nestedCriterias = createListOfNestedSearchCriteria(transactionFilter);       
            docSearchQuery = transactionDocBO.generateTransactionDocQuery(basicSearchCriterias.getKey(),
                    nestedCriterias, advanceTransactionFilter);
            LOGGER.error("Generated document query : " + docSearchQuery);

            Page<TransactionDocument> transactionDocs = transactionDocBO.getTransactionDocuments(docSearchQuery,
                    transactionFilter.getPage(), transactionFilter.getPageSize(),
                    StringUtils.isNotBlank(transactionFilter.getSortColumn()) ? transactionFilter.getSortColumn() : "runAsOfDate",
                    transactionFilter.isDescending(), basicSearchCriterias.getValue(), 
                    isEmptySearch(transactionFilter, advanceTransactionFilter), Boolean.TRUE, transactionFilterForApi);

            transactionWrapperForApi = constructTranWrapperForApi(transactionDocs.getContent());
            transactionWrapperForApi.setSearchResultMessage(getSearchResultMsg(transactionFilter, 
                    transactionDocs.getTotalElements(), transactionWrapperForApi.getTransactions().size()));
            transactionWrapperForApi.setTotalCount(transactionDocs.getTotalElements());           
        } catch (SystemException e) {
                throw e;
        }
        return transactionWrapperForApi;
    }

    @Override
    public TransactionDocument getTxnDocument(String txnId) throws SystemException {
        return transactionBO.getTxnDocumentByTxnId(txnId);

    }
    
    @PreAuthorize("hasRole(@accessPrivilege.getDashboardTransactionDownloadIOJson())")
    @Override
    public TransactionDocument getTntModelIoDocuments(String txnId) throws SystemException {
        return transactionBO.getTxnDocumentByTxnId(txnId);
    }
    
    @PreAuthorize("hasRole(@accessPrivilege.getDashboardTransactionDownloadTenantIO())")
    @Override
    public TransactionDocument getTntIoDocuments(String txnId) throws SystemException {
        return transactionBO.getTxnDocumentByTxnId(txnId);
    }
    
    @PreAuthorize("hasRole(@accessPrivilege.getDashboardTransactionDownloadModelIO())")
    @Override
    public TransactionDocument getModelIoDocuments(String txnId) throws SystemException {
        return transactionBO.getTxnDocumentByTxnId(txnId);
    }

    @Override
    public TransactionVersionInfo getTransactionVersionInfo(String versionName, String fullVersion) throws BusinessException, SystemException {
        final Version version = getVersionInfo(versionName, fullVersion);
        TransactionVersionInfo transactionVersionInfo = null;
        if (version != null) {
            transactionVersionInfo = new TransactionVersionInfo();
            transactionVersionInfo.setPublishedBy(version.getPublishedBy());
            transactionVersionInfo.setPublishedOn(version.getPublishedOn());
            transactionVersionInfo.setStatus(version.getStatus());
            transactionVersionInfo.setUmgLibraryName(version.getModelLibrary().getUmgName());
            transactionVersionInfo.setUmgTidName(version.getMapping().getName());
            transactionVersionInfo.setUmgModelName(version.getMapping().getModel().getUmgName());
        }

        return transactionVersionInfo;
    }
    
    private static List<String> getListOfIdsFromString(final String value) {
        List<String> tranIdList = new ArrayList<>();
        final String[] transactions = value.split("[,]");
        for (String transaction : transactions) {
            tranIdList.add(org.apache.commons.lang3.StringUtils.trim(transaction));
        }
        return tranIdList;
    }
    
    public String formSearchResultMessage(final TransactionFilter filter,
            final AdvanceTransactionFilter advanceTransactionFilter, final long totalCount, final long returnCount) {
        String searchResultMessage = "";

        if (isEmptySearch(filter, advanceTransactionFilter)) {
            if (totalCount <= filter.getPageSize()) {
                searchResultMessage = "Showing all " + totalCount + " records";
            } else {
                searchResultMessage = "Showing latest " + filter.getPageSize() + " records";
            }
        } else {
            searchResultMessage = getSearchResultMsg(filter, totalCount, returnCount);
            if (totalCount > Integer.valueOf(systemParameterProvider.getParameter(BusinessConstants.MAX_DISPLAY_RECORDS_SIZE))) {
                searchResultMessage = "Search is resulting in more than 50,000 records, please refine your criteria";
            }
        }

        return searchResultMessage;
    }

    public String getSearchResultMsg(final TransactionFilter filter, final long totalCount,
            final long returnCount) {
        String searchResultMessage = "";
        if (totalCount <= filter.getPageSize()) {
            if (returnCount == 0) {
                searchResultMessage = "No records found";
            } else {
                searchResultMessage = "Showing all " + returnCount + " resulting records";
            }
        } else {
            if (returnCount == 0) {
                searchResultMessage = "No records found";
            } else {
                searchResultMessage = "Showing latest " + filter.getPageSize() + " of " + totalCount + " resulting records";
            }
        }
        return searchResultMessage;
    }

    public boolean isEmptySearch(final TransactionFilter transactionFilter,
            final AdvanceTransactionFilter advanceTransactionFilter) {
        return isBasicSearchEmpty(transactionFilter) && isAdvancedSearchEmpty(advanceTransactionFilter);
    }

    public boolean isBasicSearchEmpty(final TransactionFilter transactionFilter) {
        boolean basicSearchEmpty = true;

        if ((StringUtil.isNullOrEmpty(transactionFilter.getTenantModelName()) || ANY.getMode().equalsIgnoreCase(
                transactionFilter.getTenantModelName()))
                && (StringUtil.isNullOrEmpty(transactionFilter.getLibraryName()) || ANY.getMode().equalsIgnoreCase(
                        transactionFilter.getLibraryName()))
                && StringUtil.isNullOrEmpty(transactionFilter.getFullVersion())
                && (StringUtil.isNullOrEmpty(transactionFilter.getTransactionStatus()) || ANY.getMode().equalsIgnoreCase(
                        transactionFilter.getTransactionStatus()))
                && StringUtil.isNullOrEmpty(transactionFilter.getRunAsOfDateFromString())
                && StringUtil.isNullOrEmpty(transactionFilter.getRunAsOfDateToString())
                && StringUtil.isNullOrEmpty(transactionFilter.getClientTransactionID())
                && StringUtil.isNullOrEmpty(transactionFilter.getErrorType())
                && StringUtil.isNullOrEmpty(transactionFilter.getErrorDescription())
                && (StringUtil.isNullOrEmpty(transactionFilter.getTransactionType()) || PROD.getType().equalsIgnoreCase(
                        transactionFilter.getTransactionType()))
                && StringUtil.isNullOrEmpty(transactionFilter.getBatchId())
                && (StringUtil.isNullOrEmpty(transactionFilter.getTransactionMode()) || ANY.getMode().equalsIgnoreCase(
                        transactionFilter.getTransactionMode()))) {
            basicSearchEmpty = true;
        } else {
            basicSearchEmpty = false;
        }

        return basicSearchEmpty;
    }

    public boolean isAdvancedSearchEmpty(final AdvanceTransactionFilter advanceTransactionFilter) {
        return advanceTransactionFilter == null;
    }

    public String getMongoTimeout(KeyValuePair<String, String> query) {
        String sTimeout = "";
        if (query != null && query.getKey() != null && StringUtils.isNotBlank(query.getKey())) {
            Long timeout;
            if (StringUtils.isNotBlank(query.getValue())) {
                timeout = StringUtils.isNotBlank(systemParameterProvider
                        .getParameter(BusinessConstants.MAX_WAIT_TIME_ADVANCED_SEARCH)) ? Long
                        .parseLong(systemParameterProvider.getParameter(BusinessConstants.MAX_WAIT_TIME_ADVANCED_SEARCH)) : MongoTransactionDAOImpl.MAX_TIME_ADVANCED_MS;
        
            } else {
                timeout = StringUtils.isNotBlank(systemParameterProvider
                        .getParameter(BusinessConstants.MAX_WAIT_TIME_PRIMARY_SEARCH)) ? Long
                        .parseLong(systemParameterProvider.getParameter(BusinessConstants.MAX_WAIT_TIME_PRIMARY_SEARCH)) : MongoTransactionDAOImpl.MAX_TIME_PRIMARY_MS;
            }
            sTimeout = (timeout / 1000) + "sec";
        }

        return sTimeout;
    }

    @Override
    public Version getVersionInfo(final String versionName, final String fullVersion) throws BusinessException, SystemException {
        Integer majorVersion = null;
        Integer minorVersion = null;

        if (StringUtils.isNotBlank(fullVersion)) {
            try {
                String[] versionArr = fullVersion.split("\\.");
                if (versionArr.length > HAS_MAJOR_AND_MINOR_VERSION) {
                    BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000077, new Object[] {});
                } else if (versionArr.length == HAS_ONLY_MAJOR_VERSION) {
                    majorVersion = Integer.valueOf(fullVersion);
                } else {
                    majorVersion = Integer.valueOf(versionArr[0]);
                    if (versionArr.length == HAS_MAJOR_AND_MINOR_VERSION) {
                        minorVersion = Integer.valueOf(versionArr[1]);
                    }
                }
            } catch (NumberFormatException excp) {
                LOGGER.error("BSE000077 : The Transaction Version data format is invalid. Valid Formats are : [Integer], [Integer.Integer]");
                BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000077, new Object[] {});
            }
        }

        return versionBO.findByNameAndVersion(versionName, majorVersion, minorVersion);
    }

    @Override
    public byte[] getBulkModelOuput(final String txnId) throws SystemException {
        final String outputFilename = batchRuntimeTxnDAO.getBatchOutputFileName(txnId);

        StringBuffer file = new StringBuffer(AdminUtil.getSanBasePath(umgFileProxy.getSanPath(systemParameterProvider
                .getParameter(SystemConstants.SAN_BASE))));
        file.append(File.separatorChar).append(BusinessConstants.BULK_FILE).append(File.separatorChar)
                .append(BusinessConstants.BULK_OUTPUT).append(File.separatorChar).append(outputFilename);

        LOGGER.info("Reading bytes from bulk output file : " + file.toString());
        byte[] bFile = null;
        FileInputStream fos = null;
        try {
            fos = new FileInputStream(file.toString());
            bFile = IOUtils.toByteArray(fos);
        } catch (IOException ioe) {
            LOGGER.error("Reading bytes from bulk output file is failed: " + file.toString());
            LOGGER.error(ioe.getMessage());
            SystemException.newSystemException(BusinessExceptionCodes.BSE000377, new String[] { ioe.getMessage() });
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ioe) {
                    LOGGER.error("Reading bytes from bulk output file is failed: " + file.toString());
                    LOGGER.error(ioe.getMessage());
                    SystemException.newSystemException(BusinessExceptionCodes.BSE000377, new String[] { ioe.getMessage() });
                }
            }
        }

        LOGGER.info("Reading bytes from bulk output file is success");
        return bFile;
    }

    @Override
    public byte[] getBulkModelErrorOuput(String txnId) throws SystemException {
        final String inputFilename = batchRuntimeTxnDAO.getBulkInputFileName(txnId);
        String outputModelFileName = inputFilename.substring(0, inputFilename.indexOf(BusinessConstants.EXTN_JSON));
        StringBuffer file = new StringBuffer(AdminUtil.getSanBasePath(umgFileProxy.getSanPath(systemParameterProvider
                .getParameter(SystemConstants.SAN_BASE))));
        file.append(File.separatorChar).append(BusinessConstants.BULK_FILE).append(File.separatorChar)
                .append(BusinessConstants.OUTPUT_ARCHIVE_FOLDER).append(File.separatorChar).append(outputModelFileName)
                .append(BusinessConstants.HYPHEN).append(BusinessConstants.ERROR_RESPONSE_MESSAGE)
                .append(BusinessConstants.EXTN_JSON);

        LOGGER.info("Reading bytes from bulk output file : " + file.toString());
        byte[] bFile = null;
        FileInputStream fos = null;
        try {
			File outputArchiveFile = new File(file.toString());
			if (outputArchiveFile.exists()) {
				fos = new FileInputStream(file.toString());
				bFile = IOUtils.toByteArray(fos);
			} else {
				StringBuffer outputFilePath = new StringBuffer(AdminUtil.getSanBasePath(
						umgFileProxy.getSanPath(systemParameterProvider.getParameter(SystemConstants.SAN_BASE))));
				outputFilePath.append(File.separatorChar).append(BusinessConstants.BULK_FILE).append(File.separatorChar)
						.append(BusinessConstants.OUTPUT_FOLDER).append(File.separatorChar).append(outputModelFileName)
						.append(BusinessConstants.HYPHEN).append(BusinessConstants.ERROR_RESPONSE_MESSAGE)
						.append(BusinessConstants.EXTN_JSON);
				File outputFile = new File(outputFilePath.toString());
				if (outputFile.exists()) {
					fos = new FileInputStream(outputFilePath.toString());
					bFile = IOUtils.toByteArray(fos);
				} else {
					bFile = AdminUtil.createTempJson(outputModelFileName.concat(BusinessConstants.HYPHEN).concat(BusinessConstants.ERROR_RESPONSE_MESSAGE)
			                .concat(BusinessConstants.EXTN_JSON));
				}

			}
        } catch (IOException ioe) {
            LOGGER.error("Reading bytes from bulk output file is failed: " + file.toString());
            LOGGER.error(ioe.getMessage());
            if (bFile == null) {
                bFile = FILE_NOT_AVILABLE_FOR_DOWNLOAD_MSG.getBytes();
            }
            // SystemException.newSystemException(BusinessExceptionCodes.BSE000377, new String[] {ioe.getMessage()});
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ioe) {
                    LOGGER.error("Reading bytes from bulk output file is failed: " + file.toString());
                    LOGGER.error(ioe.getMessage());
                    SystemException.newSystemException(BusinessExceptionCodes.BSE000377, new String[] { ioe.getMessage() });
                }
            }

        }
            
        LOGGER.info("Reading bytes from bulk output file is success");
        return bFile;
    }    
  

}