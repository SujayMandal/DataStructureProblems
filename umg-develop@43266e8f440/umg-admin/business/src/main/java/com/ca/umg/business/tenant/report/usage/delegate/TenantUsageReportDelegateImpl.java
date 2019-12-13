
package com.ca.umg.business.tenant.report.usage.delegate;

import static com.ca.framework.core.exception.BusinessException.raiseBusinessException;
import static com.ca.umg.business.exception.codes.BusinessExceptionCodes.BSE0000505;
import static com.ca.umg.business.tenant.report.usage.UsageReportColumnEnum.UMG_TRANSACTION_ID;
import static com.ca.umg.business.tenant.report.usage.util.UsageReportDateUtil.END_DATE_GREATER_THAN_CURRENT_DATE;
import static com.ca.umg.business.tenant.report.usage.util.UsageReportDateUtil.NOT_IN_RANGE;
import static com.ca.umg.business.tenant.report.usage.util.UsageReportDateUtil.START_DATE_GREATER_THAN_CURRENT_DATE;
import static com.ca.umg.business.tenant.report.usage.util.UsageReportDateUtil.START_DATE_GREATER_THAN_END_DATE;
import static com.ca.umg.business.tenant.report.usage.util.UsageReportDateUtil.getDateOnly;
import static com.ca.umg.business.tenant.report.usage.util.UsageReportDateUtil.getDateWithMaxTime;
import static com.ca.umg.business.tenant.report.usage.util.UsageReportDateUtil.getDateWithMinTime;
import static com.ca.umg.business.tenant.report.usage.util.UsageReportDateUtil.getEndDate;
import static com.ca.umg.business.tenant.report.usage.util.UsageReportDateUtil.getStartDate;
import static com.ca.umg.business.tenant.report.usage.util.UsageReportDateUtil.isInMaxDateRange;
import static com.ca.umg.business.tenant.report.usage.util.UsageReportDateUtil.isStartDateMoreThanCurrentDate;
import static com.ca.umg.business.tenant.report.usage.util.UsageReportDateUtil.isStartDateMoreThanEndDate;
import static com.ca.umg.business.tenant.report.usage.util.UsageReportDateUtil.setDatesAtUI;
import static com.ca.umg.business.tenant.report.usage.util.UsageReportUtil.buildFromRowSet;
import static com.ca.umg.business.util.AdminUtil.getReportMillisFromEstToUtc;
import static java.lang.Double.valueOf;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.access.prepost.PreAuthorize;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.tenant.report.usage.UsageReportFilter;
import com.ca.umg.business.tenant.report.usage.UsageReportPageInfo;
import com.ca.umg.business.tenant.report.usage.UsageSearchRequestCancel;
import com.ca.umg.business.tenant.report.usage.UsageTransactionInfo;
import com.ca.umg.business.tenant.report.usage.UsageTransactionWrapper;
import com.ca.umg.business.tenant.report.usage.bo.TenantUsageReportBO;
import com.ca.umg.business.tenant.report.usage.bo.UsageSearchRequestCancelBO;
import com.ca.umg.business.tenant.report.usage.util.UsageReportDateUtil;

@SuppressWarnings("PMD")
@Named
public class TenantUsageReportDelegateImpl implements TenantUsageReportDelegate {

	private static final Logger LOGGER = getLogger(TenantUsageReportDelegateImpl.class.getName());

	private static final int HAS_ONLY_MAJOR_VERSION = 0;
	private static final int NON_EXISTANT_VERSION = 0;
	private static final int HAS_MAJOR_AND_MINOR_VERSION = 2;

	@Inject
	private TenantUsageReportBO tenantUsageReportBO;

	@Inject
	private UsageSearchRequestCancelBO usageSearchRequestCancelBO;

	@Override
	public List<String> getAllUniqueModels(final String tenantId) {
		return tenantUsageReportBO.getAllUniqueModel(tenantId);
	}

	@Override
	public List<String> getAllUniqueModelVersion(final String tenantId, final String tenantModelName) {
		return tenantUsageReportBO.getAllUniqueModelVersion(tenantId, tenantModelName);
	}

	@Override
	public long getTransactionCount(final UsageReportFilter filter) throws BusinessException, SystemException {
		final Map<String, Object> countMap = tenantUsageReportBO.getTransactionCount(filter);
		return ((Long) countMap.get("totalcount")).longValue();
	}

	@Override
	public UsageTransactionWrapper filterTransactions(final UsageReportFilter filter) throws BusinessException, SystemException {
		final boolean setDatesATUI = setDatesAtUI(filter);

		fillVersions(filter);
		setRunAsOfDate(filter);

		setTransactionCount(filter);

		final SqlRowSet sqlRowSet = tenantUsageReportBO.loadTransactionsRowSet(filter);
		final List<UsageTransactionInfo> transactionInfoList = buildFromRowSet(sqlRowSet);

		final UsageReportPageInfo responsePageInfo = createPagingInfo(filter, transactionInfoList);
		responsePageInfo.setResetDatesAtUI(setDatesATUI);

		final UsageTransactionWrapper usageTransactionWrapper = new UsageTransactionWrapper();
		usageTransactionWrapper.setTransactionInfoList(transactionInfoList);
		usageTransactionWrapper.setPagingInfo(responsePageInfo);

		return usageTransactionWrapper;
	}

	private UsageReportPageInfo createPagingInfo(final UsageReportFilter filter, final List<UsageTransactionInfo> transactionInfoList) {
		final UsageReportPageInfo responsePageInfo = new UsageReportPageInfo();
		responsePageInfo.setPageSet(filter.getPageSet());
		responsePageInfo.setPage(filter.getPage());
		responsePageInfo.setPageSize(filter.getPageSize());
		responsePageInfo.setTotalPages(filter.getTotalPages());
		responsePageInfo.setTotalElements(Long.valueOf(filter.getMatchedTransactionCount()));
		responsePageInfo.setStartDate(getDateOnly(filter.getRunAsOfDateFromString()));
		responsePageInfo.setEndDate(getDateOnly(filter.getRunAsOfDateToString()));
		return responsePageInfo;
	}

	private void setTransactionCount(final UsageReportFilter filter) throws BusinessException, SystemException {
		final long transactionCount = getTransactionCount(filter);

		filter.setMatchedTransactionCount(transactionCount);
		final int totalPages = ((Double) Math.ceil(valueOf(transactionCount) / filter.getPageSize())).intValue();
		filter.setTotalPages(totalPages);
	}

	@Override
	public SqlRowSet loadTransactionsRowSetByFilter(final UsageReportFilter filter) throws SystemException, BusinessException {
		fillVersions(filter);
		setRunAsOfDate(filter);
		return tenantUsageReportBO.loadTransactionsRowSet(filter);
	}

	@Override
	public SqlRowSet loadTransactionsRowSetBySearch(final UsageReportFilter filter) throws SystemException, BusinessException {
		return tenantUsageReportBO.loadTransactionsRowSet(filter);
	}

	@PreAuthorize("hasRole(@accessPrivilege.getDashboardTransactionDownloadExcelUsageReport())")
	@Override
	public SqlRowSet loadTransactionsRowSetByTransactionList(final UsageReportFilter filter) throws SystemException, BusinessException {
		return tenantUsageReportBO.loadTransactionsRowSet(filter);
	}

	@Override
	public UsageSearchRequestCancel createUsageSearchRequestCancel() throws BusinessException {
		return usageSearchRequestCancelBO.createUsageSearchRequestCancel();
	}

	@Override
	public void deleteUsageSearchRequestCancel(final String id) {
		usageSearchRequestCancelBO.deleteUsageSearchRequestCancel(id);
	}

	@Override
	public UsageSearchRequestCancel findUsageSearchRequestCancel(final String id, final boolean cancelStatus) {
		return usageSearchRequestCancelBO.updateUsageSearchRequestCancel(id, cancelStatus);
	}

	@Override
	public UsageSearchRequestCancel getUsageSearchRequestCancel(final String id) {
		return usageSearchRequestCancelBO.getUsageSearchRequestCancel(id);
	}

	@Override
	public UsageSearchRequestCancel getUsageSearchRequestCancelById(final String id) {
		return usageSearchRequestCancelBO.getUsageSearchRequestCancelById(id);
	}

	@Override
	public UsageSearchRequestCancel updateUsageSearchRequestCancel(final String id, final boolean cancelStatus) throws BusinessException {
		return usageSearchRequestCancelBO.updateUsageSearchRequestCancel(id, cancelStatus);
	}

	@Override
	public boolean getUsageSearchRequestCancelStatusFromCache(final String id) {
		return usageSearchRequestCancelBO.getUsageSearchRequestCancelStatusFromCache(id);
	}

	@Override
	public String getTransactionIdByFilter(final UsageReportFilter filter) throws BusinessException, SystemException {
		fillVersions(filter);
		setRunAsOfDate(filter);

		setTransactionCount(filter);
		final SqlRowSet sqlRowSet = tenantUsageReportBO.loadTransactionsRowSet(filter);

		String transactionId = null;
		if (sqlRowSet != null) {
			while (sqlRowSet.next()) {
				transactionId = sqlRowSet.getString(UMG_TRANSACTION_ID.getDbColumnName());
				break;
			}
		}

		return transactionId;
	}

	private void fillVersions(final UsageReportFilter filter) throws BusinessException {
		if (isNotBlank(filter.getFullVersion()) && !filter.getFullVersion().equals("All")) {
			String[] versionArr = filter.getFullVersion().split("\\.");
			if (versionArr.length == HAS_ONLY_MAJOR_VERSION) {
				filter.setMajorVersion(Integer.valueOf(filter.getFullVersion()));
			} else {
				try {
					Integer majorVersion = Integer.valueOf(versionArr[0]);
					if (majorVersion > NON_EXISTANT_VERSION) {
						if (versionArr.length == HAS_MAJOR_AND_MINOR_VERSION) {
							filter.setMajorVersion(majorVersion);
							filter.setMinorVersion(Integer.valueOf(versionArr[1]));
						} else {
							filter.setMajorVersion(majorVersion);
						}
					}
				} catch (NumberFormatException excp) {
					LOGGER.error("BSE0000505 : The Transaction Version data format is invalid. Valid Formats are ");
					BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE0000505, new Object[] { "Model Version is wrong " });
				}
			}
		}
	}

	private void setRunAsOfDate(final UsageReportFilter filter) throws BusinessException, SystemException {
		validateStartDate(filter);
		validateEndDate(filter);
		validFromToDates(filter);
		final boolean startDateSelectedForMonthReport = isStartDateSelectedForMonthReport(filter);
		filter.setRunAsOfDateFromString(getStartDate(filter));
		filter.setRunAsOfDateToString(getEndDate(filter, startDateSelectedForMonthReport));

		filter.setRunAsOfDateFrom(getReportMillisFromEstToUtc(filter.getRunAsOfDateFromString(), null));
		filter.setRunAsOfDateTo(getReportMillisFromEstToUtc(filter.getRunAsOfDateToString(), null));

	}

	private boolean isStartDateSelectedForMonthReport(final UsageReportFilter filter) {
		return !filter.isCustomDate() && filter.getRunAsOfDateFromString() != null;
	}

	private void validateStartDate(final UsageReportFilter filter) throws BusinessException {
		if (isStartDateMoreThanCurrentDate(filter.getRunAsOfDateFromString())) {
			throw raiseBusinessException(BSE0000505, new String[] { START_DATE_GREATER_THAN_CURRENT_DATE });
		}
	}

	private void validateEndDate(final UsageReportFilter filter) throws BusinessException {
		if (UsageReportDateUtil.isEndDateMoreThanCurrentDate(filter.getRunAsOfDateToString())) {
			throw raiseBusinessException(BSE0000505, new String[] { END_DATE_GREATER_THAN_CURRENT_DATE });
		}
	}

	private void validFromToDates(final UsageReportFilter filter) throws BusinessException {
		final DateTime startDateTime = getDateWithMinTime(filter.getRunAsOfDateFromString());
		final DateTime endDateTime = getDateWithMaxTime(filter.getRunAsOfDateToString());
		if (isStartDateMoreThanEndDate(startDateTime, endDateTime)) {
			throw raiseBusinessException(BSE0000505, new String[] { START_DATE_GREATER_THAN_END_DATE });
		}

		if (!isInMaxDateRange(startDateTime, endDateTime)) {
			throw raiseBusinessException(BSE0000505, new String[] { NOT_IN_RANGE });
		}
	}

	@Override
	public UsageTransactionWrapper searchTransactions(final UsageReportFilter filter) throws BusinessException, SystemException {
		setTransactionCount(filter);
		final SqlRowSet sqlRowSet = tenantUsageReportBO.loadTransactionsRowSet(filter);
		final List<UsageTransactionInfo> transactionInfoList = buildFromRowSet(sqlRowSet);
		final UsageReportPageInfo responsePageInfo = createPagingInfo(filter, transactionInfoList);
		final UsageTransactionWrapper usageTransactionWrapper = new UsageTransactionWrapper();
		usageTransactionWrapper.setTransactionInfoList(transactionInfoList);
		usageTransactionWrapper.setPagingInfo(responsePageInfo);
		return usageTransactionWrapper;
	}

	@Override
	public String getTransactionIdBySearch(final UsageReportFilter filter) throws BusinessException, SystemException {
		setTransactionCount(filter);
		final SqlRowSet sqlRowSet = tenantUsageReportBO.loadTransactionsRowSet(filter);

		String transactionId = null;
		if (sqlRowSet != null) {
			while (sqlRowSet.next()) {
				transactionId = sqlRowSet.getString(UMG_TRANSACTION_ID.getDbColumnName());
				break;
			}
		}

		return transactionId;
	}
}