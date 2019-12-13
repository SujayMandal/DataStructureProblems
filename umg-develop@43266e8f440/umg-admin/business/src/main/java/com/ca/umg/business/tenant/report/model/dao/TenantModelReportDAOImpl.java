package com.ca.umg.business.tenant.report.model.dao;

import static com.ca.framework.core.exception.SystemException.newSystemException;
import static com.ca.framework.core.requestcontext.RequestContext.getRequestContext;
import static com.ca.umg.business.exception.codes.BusinessExceptionCodes.BSE0000506;
import static com.ca.umg.business.tenant.report.model.TenantModelReportEnum.CLIENT_TRANSACTION_ID;
import static com.ca.umg.business.tenant.report.model.TenantModelReportEnum.CREATED_DATE;
import static com.ca.umg.business.tenant.report.model.TenantModelReportEnum.TRANSACTION_ID;
import static com.ca.umg.business.tenant.report.model.TenantModelReportEnum.VERSION_NAME;
import static com.ca.umg.business.tenant.report.model.TenantModelReportEnum.values;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Field;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.constants.PoolConstants;
import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.exception.codes.FrameworkExceptionCodes;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.framework.core.util.ConversionUtil;
import com.ca.pool.TransactionType;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.tenant.report.model.TenantModelReport;
import com.ca.umg.business.tenant.report.model.TenantModelReportEnum;

@SuppressWarnings("PMD")
@Repository
public class TenantModelReportDAOImpl implements TenantModelReportDAO {

    private static final Logger LOGGER = getLogger(TenantModelReportDAOImpl.class);

    @Inject
    private MongoTemplate mongoTemplate;

    @Inject
    private SystemParameterProvider systemParameterProvider;

    public void setMongoTemplate(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public TenantModelReport viewTransactionInputAndOutputs(final String txnId) throws SystemException {
        TenantModelReport modelReport = null;

        if (txnId == null) {
            LOGGER.debug("Mongo Query for fetching Input and outputs of transaction: Transaction Id is null");
            newSystemException(BSE0000506, new Object[] { "", "Transaction Id is null" });
        }

        try {
            final Query query = new Query(where(TRANSACTION_ID.getColumn()).is(txnId));
            addFields(query, values());
            LOGGER.debug("Mongo Query for fetching Input and outputs of transaction:" + query.toString());
            modelReport = mongoTemplate.findOne(query, TenantModelReport.class, getRequestContext().getTenantCode()
                    + FrameworkConstant.DOCUMENTS);
        } catch (Exception ex) {// NOPMD
            LOGGER.debug("Mongo Query for fetching Input and outputs of transaction: Failed");
            newSystemException(BSE0000506, new Object[] { txnId, ex.getMessage() });
        }
        LOGGER.debug("Mongo Query for fetching Input and outputs of transaction: Success");
        return modelReport;
    }

    @Override
    public TenantModelReport exportTransactionInputAndOutputs(final String txnId, final TenantModelReportEnum report)
            throws SystemException {
        TenantModelReport modelReport = null;
        try {
            final Query query = new Query(where(TRANSACTION_ID.getColumn()).is(txnId));
            addFields(query, TRANSACTION_ID, CLIENT_TRANSACTION_ID, CREATED_DATE, VERSION_NAME, report);
            LOGGER.debug("Mongo Query for exporting report:" + query.toString());
            modelReport = mongoTemplate.findOne(query, TenantModelReport.class, getRequestContext().getTenantCode()
                    + FrameworkConstant.DOCUMENTS);
        } catch (Exception ex) {// NOPMD
            LOGGER.debug("Mongo Query for exporting report: Failed");
            newSystemException(BSE0000506, new Object[] { txnId, ex.getMessage() });
        }
        LOGGER.debug("Mongo Query for exporting report: Success");
        return modelReport;
    }

    private void addFields(final Query query, final TenantModelReportEnum... columns) {
        if (columns != null) {
            final Field fields = query.fields();
            for (TenantModelReportEnum column : columns) {
                if (column.getColumn() != null) {
                    fields.include(column.getColumn());
                }
            }
        }
    }

    @Override
    public List<TenantModelReport> viewTransactionInputAndOutputs(final List<String> txnId) throws SystemException {
        List<TenantModelReport> modelReport = null;

        if (txnId == null || txnId.isEmpty()) {
            LOGGER.debug("Mongo Query for fetching Input and outputs of transaction: Transaction Id is null");
            newSystemException(BSE0000506, new Object[] { "", "Transaction Id is null" });
        }

        try {
            final Query query = new Query(where(TRANSACTION_ID.getColumn()).in(txnId));
            addFields(query, values());
            LOGGER.debug("Mongo Query for fetching Input and outputs of transaction:" + query.toString());

            modelReport = mongoTemplate.find(query, TenantModelReport.class, getRequestContext().getTenantCode()
                    + FrameworkConstant.DOCUMENTS);
            for (TenantModelReport tenantModelReport : modelReport) {
                if (tenantModelReport.getModeletPoolCriteria() != null) {
                    // below log should remove once we will come to know the issue
                    LOGGER.error("ModeletPoolCriteria is ================" + tenantModelReport.getModeletPoolCriteria());
                    String addOnValidation = tenantModelReport.getModeletPoolCriteria().substring(
                            tenantModelReport.getModeletPoolCriteria().indexOf(FrameworkConstant.ADD_ON_VALIDATION));
                    if (StringUtils.isNotEmpty(addOnValidation)
                            && !addOnValidation.substring(addOnValidation.indexOf('=') + 1).startsWith("null")) {
                        tenantModelReport.setAddOnValidation(addOnValidation.substring(addOnValidation.indexOf('=') + 1,
                                addOnValidation.indexOf(']') + 1));
                    }

                }
                if (tenantModelReport.isTest()) {
                    tenantModelReport.setTransactionType(StringUtils.lowerCase(TransactionType.TEST.getType()));
                } else {
                    tenantModelReport.setTransactionType(StringUtils.lowerCase(TransactionType.PROD.getType()));
                }
                setTxnIO(tenantModelReport.getTransactionId(), tenantModelReport);
            }
        } catch (Exception ex) {// NOPMD
            LOGGER.debug("Mongo Query for fetching Input and outputs of transaction: Failed");
            newSystemException(BSE0000506, new Object[] { txnId, ex.getMessage() });
        }
        LOGGER.debug("Mongo Query for fetching Input and outputs of transaction: Success");
        return modelReport;
    }

    private void setTxnIO(final String txnId, TenantModelReport tenantModelReport) throws SystemException {

        String sanBase = systemParameterProvider.getParameter(SystemConstants.SAN_BASE);
        final Query queryForTI = new Query(where(TRANSACTION_ID.getColumn()).in(txnId));

        Map<String, Object> txnTIPayload = mongoTemplate.findOne(queryForTI, Map.class, getRequestContext().getTenantCode()
                + FrameworkConstant.UNDERSCORE + FrameworkConstant.TENANTINPUT + FrameworkConstant.DOCUMENTS);

        try {
            if (txnTIPayload != null && txnTIPayload.get("tenantInput") != null) {
                removeDate((Map<String, Object>) txnTIPayload.get("tenantInput"));
                tenantModelReport.setTenantInput((Map<String, Object>) txnTIPayload.get("tenantInput"));
            } else if (txnTIPayload != null && txnTIPayload.get(BusinessConstants.ALTERNATE_STORAGE) != null
                    && Boolean.valueOf((boolean) txnTIPayload.get(BusinessConstants.ALTERNATE_STORAGE))) {
                byte[] tenantInputFileBytes = getFile(sanBase, txnTIPayload, BusinessConstants.TENANT_INPUT);
                if (tenantInputFileBytes != null && tenantInputFileBytes.length > 0) {
                    Map<String, Object> tenantInput = ConversionUtil.convertJson(tenantInputFileBytes, Map.class);
                    removeDate(tenantInput);
                    tenantModelReport.setTenantInput(tenantInput);
                }
            }
            Map<String, Object> txnTOPayload = mongoTemplate.findOne(queryForTI, Map.class, getRequestContext().getTenantCode()
                    + FrameworkConstant.UNDERSCORE + FrameworkConstant.TENANTOUTPUT + FrameworkConstant.DOCUMENTS);
            if (txnTOPayload != null && txnTOPayload.get("tenantOutput") != null) {
                Map<String, Object> tenantOutput = (Map<String, Object>) txnTOPayload.get("tenantOutput");
                setTxnId(tenantOutput);
                tenantModelReport.setTenantOutput(tenantOutput);
            } else if (txnTOPayload != null && txnTOPayload.get(BusinessConstants.ALTERNATE_STORAGE) != null
                    && Boolean.valueOf((boolean) txnTOPayload.get(BusinessConstants.ALTERNATE_STORAGE))) {
                byte[] tenantOutputFileBytes = getFile(sanBase, txnTOPayload, BusinessConstants.TENANT_OUTPUT);
                if (tenantOutputFileBytes != null && tenantOutputFileBytes.length > 0) {
                    Map<String, Object> tenantOutput = ConversionUtil.convertJson(tenantOutputFileBytes, Map.class);
                    setTxnId(tenantOutput);
                    tenantModelReport.setTenantOutput(tenantOutput);
                }
            }

            Map<String, Object> txnMIPayload = mongoTemplate.findOne(queryForTI, Map.class, getRequestContext().getTenantCode()
                    + FrameworkConstant.UNDERSCORE + FrameworkConstant.MODELINPUT + FrameworkConstant.DOCUMENTS);
            if (txnMIPayload != null && txnMIPayload.get("modelInput") != null) {
                tenantModelReport.setModelInput((Map<String, Object>) txnMIPayload.get("modelInput"));
            } else if (txnMIPayload != null && txnMIPayload.get(BusinessConstants.ALTERNATE_STORAGE) != null
                    && Boolean.valueOf((boolean) txnMIPayload.get(BusinessConstants.ALTERNATE_STORAGE))) {
                byte[] modelInputFileBytes = getFile(sanBase, txnMIPayload, BusinessConstants.MODEL_INPUT);
                if (modelInputFileBytes != null && modelInputFileBytes.length > 0) {
                    tenantModelReport.setModelInput(ConversionUtil.convertJson(modelInputFileBytes, Map.class));
                }
            }
            Map<String, Object> txnMOPayload = mongoTemplate.findOne(queryForTI, Map.class, getRequestContext().getTenantCode()
                    + FrameworkConstant.UNDERSCORE + FrameworkConstant.MODELOUTPUT + FrameworkConstant.DOCUMENTS);
            if (txnMOPayload != null && txnMOPayload.get("modelOutput") != null) {
                tenantModelReport.setModelOutput((Map<String, Object>) txnMOPayload.get("modelOutput"));
            } else if (txnMOPayload != null && txnMOPayload.get(BusinessConstants.ALTERNATE_STORAGE) != null
                    && Boolean.valueOf((boolean) txnMOPayload.get(BusinessConstants.ALTERNATE_STORAGE))) {
                byte[] modelOutputFileBytes = getFile(sanBase, txnMOPayload, BusinessConstants.MODEL_OUTPUT);
                if (modelOutputFileBytes != null && modelOutputFileBytes.length > 0) {
                    tenantModelReport.setModelOutput(ConversionUtil.convertJson(modelOutputFileBytes, Map.class));
                }
            }
        } catch (SystemException | IOException e) {
            newSystemException(FrameworkExceptionCodes.BSE000125, new Object[] { txnId, e.getMessage() });
        }
    }

    private void setTxnId(Map<String, Object> tenantOutput) {
        Map<String, Object> headerObj = (Map<String, Object>) tenantOutput.get("header");
        Map<String, Object> dataObj = (Map<String, Object>) tenantOutput.get("data");

        if (headerObj != null && dataObj != null) {
            if (headerObj.get("transactionId") != null) {
                dataObj.put("transactionId", headerObj.get("transactionId"));
            }
            if (headerObj.get("umgTransactionId") != null) {
                dataObj.put("umgTransactionId", headerObj.get("umgTransactionId"));
            }
            if (headerObj.get("success") != null) {
                dataObj.put("success", headerObj.get("success"));
            }
        }
    }

    private void removeDate(Map<String, Object> tenantInput) {
        Map<String, Object> headerObj = (Map<String, Object>) tenantInput.get("header");
        if (headerObj != null && headerObj.get("date") != null) {
            headerObj.put("date", StringUtils.EMPTY);
        }
    }

    private static byte[] getFile(String sanBase, Map<String, Object> txnPayload, String fileType) throws IOException {
        String fileName = txnPayload.get(BusinessConstants.TRANSACTION_ID) + PoolConstants.ENV_SEPERATOR + fileType
                + BusinessConstants.JSON_EXTENSION;
        byte[] fileBytes = null;
        File file = null;
        if (StringUtils.isNotEmpty(sanBase)) {
            StringBuilder buffer = new StringBuilder(sanBase);
            buffer.append(File.separatorChar).append(getRequestContext().getTenantCode()).append(File.separatorChar)
                    .append(BusinessConstants.DOCUMENTS_FOLDER).append(File.separatorChar).append(fileName);
            String absoluteFileName = buffer.toString();
            file = new File(absoluteFileName);
        } else {
            LOGGER.error("San base not available");
        }
        if(file!=null) {
        fileBytes = Files.readAllBytes(file.toPath());
        }
        return fileBytes;
    }
}