package com.ca.umg.business.tenant.report.model.delegate;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.security.access.prepost.PreAuthorize;

import com.ca.framework.core.delegate.AbstractDelegate;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.mapping.info.MappingDescriptor;
import com.ca.umg.business.tenant.report.model.TenantModelReport;
import com.ca.umg.business.tenant.report.model.TenantModelReportEnum;
import com.ca.umg.business.tenant.report.model.bo.TenantModelReportBO;

@Named
public class TenantModelReportDelegateImpl extends AbstractDelegate implements TenantModelReportDelegate {

	@Inject
	private TenantModelReportBO tenantModelReportBO;

	@Override
	public TenantModelReport viewTenantModelReport(final String txnId, final boolean tabularViewData) throws SystemException, BusinessException {
		return tenantModelReportBO.viewTenantModelReport(txnId, tabularViewData);
	}

	@Override
	public TenantModelReport exportTenantModelReport(final String txnId, final TenantModelReportEnum report) throws SystemException {
		return tenantModelReportBO.exportTenantModelReport(txnId, report);
	}

	@Override
	public TenantModelReport exportTabularViewReport(final String txnId, final TenantModelReportEnum report) throws SystemException {
		return tenantModelReportBO.exportTabularViewReport(txnId, report);
	}

	@Override
	public MappingDescriptor getMappingDescriptor(final TenantModelReport modelReport) throws SystemException, BusinessException {
		return tenantModelReportBO.getMappingDescriptor(modelReport);
	}

	@PreAuthorize("hasRole(@accessPrivilege.getDashboardTransactionDownloadIOExcel())")
	@Override
	public List<TenantModelReport> viewTenantModelReport(final List<String> txnId) throws SystemException, BusinessException {
		return tenantModelReportBO.viewTenantModelReport(txnId);
	}

	@Override
	public MappingDescriptor getMappingDescriptorForReport(TenantModelReport modelReport)
			throws SystemException, BusinessException {
		return tenantModelReportBO.getMappingDescriptorForReport(modelReport);
	}
}