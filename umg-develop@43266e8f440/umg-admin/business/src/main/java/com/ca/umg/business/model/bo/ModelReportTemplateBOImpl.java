package com.ca.umg.business.model.bo;

import static com.ca.framework.core.exception.BusinessException.newBusinessException;
import static com.ca.umg.report.ReportExceptionCodes.REPORT_TEMPLATE_NOT_AVL_ERROR;
import static com.ca.umg.report.ReportExceptionCodes.REPORT_TRAN_DATA_NOT_AVAILABLE;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.TransactionSystemException;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.transaction.delegate.TransactionDelegate;
import com.ca.umg.business.transaction.mongo.dao.MongoTransactionDAO;
import com.ca.umg.business.transaction.mongo.entity.TransactionDocument;
import com.ca.umg.business.version.entity.Version;
import com.ca.umg.report.model.ModelReportTemplateDefinition;
import com.ca.umg.report.model.ReportTemplateStatus;
import com.ca.umg.report.service.dao.ModelReportTemplateDAO;

@Named
public class ModelReportTemplateBOImpl implements ModelReportTemplateBO {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelReportTemplateBOImpl.class);

    @Inject
    private ModelReportTemplateDAO modelReportTemplateDAO;

    @Inject
    private MongoTransactionDAO mongoTransactionDAO;

    @Inject
    private TransactionDelegate transactionDelegate;

    @Override
    public ModelReportTemplateDefinition createModelReportTemplate(
            final ModelReportTemplateDefinition modelReportTemplateDefinition) throws SystemException {
        ModelReportTemplateDefinition result = null;
        try {
            result = modelReportTemplateDAO.save(modelReportTemplateDefinition);
        } catch (DataAccessException | TransactionSystemException ex) {
            LOGGER.error("Exception occurred while inserting Model Report Template into DB. Exception is :", ex);
            LOGGER.error("Report Template Defination is:", modelReportTemplateDefinition.toString());
            SystemException.newSystemException(BusinessExceptionCodes.BSE000200,
                    new Object[] { ex.getMessage(), modelReportTemplateDefinition.toString() });
        }

        return result;
    }

    @Override
    public ModelReportTemplateDefinition findModelReportTemplateDefinition(final String modelReportTemplateId)
            throws SystemException, BusinessException {
        return modelReportTemplateDAO.findOne(modelReportTemplateId);
    }

    @Override
    public void deleteModelReportTemplateDefinition(final ModelReportTemplateDefinition modelReportTemplateDefinition)
            throws SystemException, BusinessException {
        modelReportTemplateDAO.delete(modelReportTemplateDefinition);
    }

    @Override
    public ModelReportTemplateDefinition getModelReportTemplate(final String versionId)
            throws SystemException, BusinessException {
        final ModelReportTemplateDefinition template = modelReportTemplateDAO.findByVersionIdAndIsActive(versionId,
                ReportTemplateStatus.ACTIVE.getStatus());
        if (template == null) {
            newBusinessException(REPORT_TEMPLATE_NOT_AVL_ERROR.getErrorCode(),
                    new String[] { REPORT_TEMPLATE_NOT_AVL_ERROR.getErrorDescription() });
        }
        return template;
    }

    @Override
    public ModelReportTemplateDefinition uploadModelReportTemplate(
            final ModelReportTemplateDefinition modelReportTemplateDefinition) throws SystemException, BusinessException {
        ModelReportTemplateDefinition result = null;
        try {
            final ModelReportTemplateDefinition existingOne = getModelReportTemplate(
                    modelReportTemplateDefinition.getVersionId());
            existingOne.setIsActive(ReportTemplateStatus.NOT_ACTIVE.getStatus());
            modelReportTemplateDAO.save(existingOne);

            modelReportTemplateDefinition.setReportVersion(existingOne.getReportVersion() + 1);
            result = modelReportTemplateDAO.save(modelReportTemplateDefinition);
        } catch (DataAccessException | TransactionSystemException ex) {
            LOGGER.error("Exception occurred while inserting Model Report Template into DB. Exception is :", ex);
            LOGGER.error("Report Template Defination is:", modelReportTemplateDefinition.toString());
            SystemException.newSystemException(BusinessExceptionCodes.BSE000200,
                    new Object[] { ex.getMessage(), modelReportTemplateDefinition.toString() });
        }

        return result;
    }

    @Override
    public ModelReportTemplateDefinition getModelReportTemplateByTxnId(final String transactionId)
            throws SystemException, BusinessException {
        final TransactionDocument transactionDocument = getTransactionDocumentByTxnId(transactionId);
        final String fullVersion = transactionDocument.getMajorVersion() + "." + transactionDocument.getMinorVersion();
        LOGGER.info("Version name:" + transactionDocument.getVersionName() + ", Full Version :" + fullVersion + ", TransactionId"
                + transactionId);
        final Version versionInfo = transactionDelegate.getVersionInfo(transactionDocument.getVersionName(), fullVersion);
        return versionInfo != null ? getModelReportTemplate(versionInfo.getId()) : null;
    }

    @Override
    public ModelReportTemplateDefinition getModelReportTemplate(final String versionName, final String fullVersion)
            throws SystemException, BusinessException {
        LOGGER.info("Version name:" + versionName + ", Full Version :" + fullVersion);
        final Version versionInfo = transactionDelegate.getVersionInfo(versionName, fullVersion);
        return versionInfo != null ? getModelReportTemplate(versionInfo.getId()) : null;
    }

    @Override
    public TransactionDocument getTransactionDocumentByTxnId(final String transactionId)
            throws SystemException, BusinessException {
        final TransactionDocument td = mongoTransactionDAO.getTenantAndModelIO(transactionId);
        if (td == null) {
            newBusinessException(REPORT_TRAN_DATA_NOT_AVAILABLE.getErrorCode(),
                    new String[] { REPORT_TRAN_DATA_NOT_AVAILABLE.getErrorDescription() });
        }
        return td;
    }
}