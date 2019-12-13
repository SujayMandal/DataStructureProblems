package com.ca.umg.business.model.bo;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.transaction.mongo.entity.TransactionDocument;
import com.ca.umg.report.model.ModelReportTemplateDefinition;

public interface ModelReportTemplateBO {
    
    public ModelReportTemplateDefinition createModelReportTemplate(final ModelReportTemplateDefinition modelReportTemplateDefinition) throws SystemException;
    
    public ModelReportTemplateDefinition findModelReportTemplateDefinition(final String modelReportTemplateId) throws SystemException, BusinessException;
    
    public void deleteModelReportTemplateDefinition(final ModelReportTemplateDefinition modelReportTemplateDefinition) throws SystemException, BusinessException;
    
    public ModelReportTemplateDefinition getModelReportTemplate(final String versionId) throws SystemException, BusinessException;
    
    public ModelReportTemplateDefinition uploadModelReportTemplate(final ModelReportTemplateDefinition modelReportTemplateDefinition) throws SystemException, 
		BusinessException;
    
    public ModelReportTemplateDefinition getModelReportTemplateByTxnId(final String transactionId) throws SystemException, BusinessException;
    
    public ModelReportTemplateDefinition getModelReportTemplate(final String versionName, final String fullVersion) throws SystemException, BusinessException;
    
    public TransactionDocument getTransactionDocumentByTxnId(final String transactionId) throws SystemException, BusinessException;
}