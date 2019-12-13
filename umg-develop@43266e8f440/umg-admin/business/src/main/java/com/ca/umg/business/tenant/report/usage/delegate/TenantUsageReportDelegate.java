package com.ca.umg.business.tenant.report.usage.delegate;

import java.util.List;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.tenant.report.usage.UsageReportFilter;
import com.ca.umg.business.tenant.report.usage.UsageSearchRequestCancel;
import com.ca.umg.business.tenant.report.usage.UsageTransactionWrapper;

public interface TenantUsageReportDelegate {

	public List<String> getAllUniqueModels(final String tenantId);

	public List<String> getAllUniqueModelVersion(final String tenantId, final String tenantModelName);

	public long getTransactionCount(final UsageReportFilter filter) throws BusinessException, SystemException;

	public SqlRowSet loadTransactionsRowSetByFilter(final UsageReportFilter filter) throws SystemException, BusinessException;

	public UsageTransactionWrapper filterTransactions(final UsageReportFilter filter) throws BusinessException, SystemException;

	public UsageSearchRequestCancel createUsageSearchRequestCancel() throws BusinessException;

	public UsageSearchRequestCancel updateUsageSearchRequestCancel(final String id, final boolean cancelStatus) throws BusinessException;

	public void deleteUsageSearchRequestCancel(final String id);

	public UsageSearchRequestCancel findUsageSearchRequestCancel(final String id, final boolean cancelStatus);

	public UsageSearchRequestCancel getUsageSearchRequestCancel(final String id);

	public UsageSearchRequestCancel getUsageSearchRequestCancelById(final String id);

	public boolean getUsageSearchRequestCancelStatusFromCache(final String id);

	public String getTransactionIdByFilter(final UsageReportFilter filter) throws BusinessException, SystemException;

	public UsageTransactionWrapper searchTransactions(final UsageReportFilter filter) throws BusinessException, SystemException;

	public String getTransactionIdBySearch(final UsageReportFilter filter) throws BusinessException, SystemException;

	public SqlRowSet loadTransactionsRowSetBySearch(final UsageReportFilter filter) throws SystemException, BusinessException;

	public SqlRowSet loadTransactionsRowSetByTransactionList(final UsageReportFilter filter) throws SystemException, BusinessException;
}