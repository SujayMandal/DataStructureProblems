package com.ca.umg.business.batching.dao;

import static com.ca.umg.business.tenant.report.usage.UsageReportColumnEnum.CREATED_ON;
import static java.util.Locale.getDefault;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.business.batching.report.usage.BatchUsageReportColumnEnum;
import com.ca.umg.business.batching.report.usage.BatchUsageReportFilter;
import com.ca.umg.business.util.AdminUtil;

@SuppressWarnings("PMD")
public class BatchUsageReportQuery {

	private static final Logger LOGGER = LoggerFactory.getLogger(BatchUsageReportQuery.class);

	private static final String SINGLE_QUOTE = "'";

    private static final String BATCH_REPORT_QUERY_SELECT_CLAUSE = "select * from BATCH_TRANSACTION";

	private BatchUsageReportQuery() {

	}

	// returns selected transaction
	public static String createBatchUsageReportQuery(final BatchUsageReportFilter filter) {
		final StringBuilder sb = new StringBuilder(100);
		sb.append(BATCH_REPORT_QUERY_SELECT_CLAUSE);

		final String whereClause = createBatchWhereClause(filter);
		if (whereClause != null && whereClause.length() > 0) {
			sb.append(whereClause);
		}

		LOGGER.debug("Batch Usage Report : Create Usage Report Query :" + sb.toString());
		return sb.toString();
	}
	
	// returns all transactions based on filter
	public static String createAllBatchUsageReportQuery(final BatchUsageReportFilter filter) {
		final StringBuilder sb = new StringBuilder(100);
		sb.append(BATCH_REPORT_QUERY_SELECT_CLAUSE);

		final String whereClause = createBatchWhereClause(filter);
		if (whereClause != null && whereClause.length() > 0) {
			sb.append(whereClause);
		}

		sb.append(createBatchOrderByClause(filter));
		sb.append(createBatchLimitClause(filter));

		LOGGER.debug("Batch Usage Report : Create Usage Report Query :" + sb.toString());
		return sb.toString();
	}

	public static String createBatchOrderByClause(final BatchUsageReportFilter filter) {
		final StringBuilder orderByClause = new StringBuilder();
				orderByClause.append(" order by ").append(CREATED_ON.getDbColumnName()).append("  desc");
		return orderByClause.toString();
	}

	private static String createBatchWhereClause(final BatchUsageReportFilter filter) {
		final StringBuilder whereConditions = new StringBuilder();
		whereConditions.append(" where ");
		boolean addedFirstCondition = false;
		addedFirstCondition = addWhereCondition(whereConditions, getTntCodeCondition(RequestContext.getRequestContext().getTenantCode()), addedFirstCondition);
		addedFirstCondition = addWhereCondition(whereConditions, getBatchTransactionIdCondition(filter.getSelectedTransactions()), addedFirstCondition);
		addedFirstCondition = addWhereCondition(whereConditions, getBatchTransactionIdLike(filter.getBatchId()), addedFirstCondition);
		addedFirstCondition = addWhereCondition(whereConditions, getBatchFileNameLike(filter.getInputFileName()), addedFirstCondition);
		addedFirstCondition = addWhereCondition(whereConditions,
				getBatchDatesBetweenCondition(filter.getFromDate(), filter.getToDate()), addedFirstCondition);
		return StringUtils.equalsIgnoreCase(StringUtils.trim(whereConditions.toString()), "where") ? null : whereConditions.toString();
	}
	
	private static String getTntCodeCondition(final String tenantCode) {
		String condition = null;

		if (tenantCode != null) {
			condition = BatchUsageReportColumnEnum.TENANT_ID.getDbColumnName() + " = '" + tenantCode + SINGLE_QUOTE;
		}

		return condition;
	}
	
	
	private static boolean addWhereCondition(final StringBuilder otherJoinConditions, final String joinCondition, final boolean addedFirstCondition) {
		boolean addedFirstConditionFlag = addedFirstCondition;
		if (joinCondition != null) {
			if (addedFirstConditionFlag) {
				otherJoinConditions.append(" and ").append(joinCondition);
			} else {
				otherJoinConditions.append(joinCondition);
			}

			addedFirstConditionFlag = true;
		}

		return addedFirstConditionFlag;
	}

	public static String createBatchLimitClause(final BatchUsageReportFilter filter) {
		String limitClause = "";
		StringBuilder sb = new StringBuilder();
		sb.append(" limit ");
		sb.append(filter.getPageSize());
		limitClause = sb.toString();
		return limitClause;
	}

	
	private static String getBatchTransactionIdCondition(final List<String> selectedTransactions) {
		if (selectedTransactions != null && selectedTransactions.size() > 0) {
			final String transactionList = getTransactionListForInOper(selectedTransactions);
			final String inList = " in (" + transactionList + ") ";
			final StringBuffer sb = new StringBuffer();
			sb.append("lower(").append("ID").append(")").append(inList);
			return sb.toString();
		} else {
			return null;
		}
	}
	
	private static String getTransactionListForInOper(final List<String> selectedTransactions) {
		final StringBuffer sb = new StringBuffer();
		for (String transaction : selectedTransactions) {
			if (transaction != null && transaction.trim().length() > 0) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
				sb.append(SINGLE_QUOTE + transaction.trim().toLowerCase(getDefault()) + SINGLE_QUOTE);
			}
		}

		if (sb.length() > 0) {
			return sb.toString();
		} else {
			return "";
		}
	}
	
	private static String getBatchTransactionIdLike(final String value) {
		String condition = null;
		if (isNotBlank(value)) {
			condition = "lower(ID) like '" + AdminUtil.getLikePattern(value) + SINGLE_QUOTE;
		}

		return condition;
	}
	
	private static String getBatchFileNameLike(final String value) {
		String condition = null;
		if (isNotBlank(value)) {
			condition = "lower(BATCH_INPUT_FILE) like '" + AdminUtil.getLikePattern(value) + SINGLE_QUOTE;
		}

		return condition;
	}
	
	private static String getBatchDatesBetweenCondition(final Long startDate, final Long endDate) {
		String condition = null;
		if (startDate != null && endDate != null) {
			condition = BatchUsageReportColumnEnum.START_TIME.getDbColumnName() + " >= " + startDate + " and " + BatchUsageReportColumnEnum.END_TIME.getDbColumnName() +" <= " + endDate;
		} else if (startDate != null) {
			condition = BatchUsageReportColumnEnum.START_TIME.getDbColumnName() + " >= " + startDate;
		} else if (endDate != null) {
			condition = BatchUsageReportColumnEnum.END_TIME.getDbColumnName() + " <= " + endDate;
		}

		return condition;
	}
}