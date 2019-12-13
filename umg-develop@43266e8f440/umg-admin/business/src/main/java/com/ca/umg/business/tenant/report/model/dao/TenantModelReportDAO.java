package com.ca.umg.business.tenant.report.model.dao;

import java.util.List;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.tenant.report.model.TenantModelReport;
import com.ca.umg.business.tenant.report.model.TenantModelReportEnum;

public interface TenantModelReportDAO {

	public TenantModelReport viewTransactionInputAndOutputs(final String txnId) throws SystemException;

	public TenantModelReport exportTransactionInputAndOutputs(final String txnId, final TenantModelReportEnum report) throws SystemException;
	
	public List<TenantModelReport> viewTransactionInputAndOutputs(final List<String> txnId) throws SystemException;
}