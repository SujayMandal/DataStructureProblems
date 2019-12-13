package com.ca.umg.business.tenant.report.usage.bo;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.tenant.report.usage.UsageReportFilter;
import com.ca.umg.business.tenant.report.usage.dao.UsageReportDAO;

@SuppressWarnings("PMD")
@Named
public class TenantUsageReportBOImpl implements TenantUsageReportBO {

	@Inject
	private UsageReportDAO usageReportDAO;

	@Override
	public List<String> getAllUniqueModel(final String tenantId) {
		return usageReportDAO.getAllUniqueModelNames(tenantId);
	}

	@Override
	public List<String> getAllUniqueModelVersion(final String tenantId, final String tenantModelName) {
		return usageReportDAO.getAllUniqueModelVersion(tenantId, tenantModelName);
	}

	@Override
	public Map<String, Object> getTransactionCount(final UsageReportFilter filter) throws SystemException, BusinessException {
		return usageReportDAO.getTransactionCount(filter);
	}

	@Override
	public SqlRowSet loadTransactionsRowSet(final UsageReportFilter filter) throws SystemException, BusinessException {
		return usageReportDAO.loadTransactionsRowSet(filter);
	}
}