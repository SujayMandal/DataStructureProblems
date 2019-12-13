package com.ca.umg.business.tenant.report.model.bo;

import java.util.List;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.mapping.info.MappingDescriptor;
import com.ca.umg.business.tenant.report.model.TenantModelReport;
import com.ca.umg.business.tenant.report.model.TenantModelReportEnum;

public interface TenantModelReportBO {

	public TenantModelReport viewTenantModelReport(final String txnId, final boolean tabularViewData) throws SystemException, BusinessException;

	public TenantModelReport exportTenantModelReport(final String txnId, final TenantModelReportEnum report) throws SystemException;

	public TenantModelReport exportTabularViewReport(final String txnId, final TenantModelReportEnum report) throws SystemException;

	public MappingDescriptor getMappingDescriptor(final TenantModelReport modelReport) throws SystemException, BusinessException;
	
	public MappingDescriptor getMappingDescriptorForReport(final TenantModelReport modelReport) throws SystemException, BusinessException;
	
	public List<TenantModelReport> viewTenantModelReport(final List<String> txnId) throws SystemException, BusinessException;
}