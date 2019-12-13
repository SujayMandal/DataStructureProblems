package com.ca.umg.business.tenant.report.usage.bo;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.tenant.report.usage.UsageReportFilter;

public interface TenantUsageReportBO {

	public List<String> getAllUniqueModel(final String tenantId);

	public List<String> getAllUniqueModelVersion(final String tenantId, final String tenantModelName);

	public Map<String, Object> getTransactionCount(final UsageReportFilter filter) throws SystemException, BusinessException;

	public SqlRowSet loadTransactionsRowSet(final UsageReportFilter filter) throws SystemException, BusinessException;
}