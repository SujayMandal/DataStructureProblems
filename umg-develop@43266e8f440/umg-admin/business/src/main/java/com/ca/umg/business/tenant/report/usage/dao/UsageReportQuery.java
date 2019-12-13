package com.ca.umg.business.tenant.report.usage.dao;

import static com.ca.umg.business.tenant.report.usage.UsageReportColumnEnum.BATCH_ID;
import static com.ca.umg.business.tenant.report.usage.UsageReportColumnEnum.CREATED_ON;
import static com.ca.umg.business.tenant.report.usage.UsageReportColumnEnum.IS_TEST;
import static com.ca.umg.business.tenant.report.usage.UsageReportColumnEnum.MAJOR_VERSION;
import static com.ca.umg.business.tenant.report.usage.UsageReportColumnEnum.MINOR_VERSION;
import static com.ca.umg.business.tenant.report.usage.UsageReportColumnEnum.MODEL;
import static com.ca.umg.business.tenant.report.usage.UsageReportColumnEnum.PROCESSING_STATUS;
import static com.ca.umg.business.tenant.report.usage.UsageReportColumnEnum.TENANT_ID;
import static com.ca.umg.business.tenant.report.usage.UsageReportColumnEnum.TENANT_TRANSACTION_ID;
import static com.ca.umg.business.tenant.report.usage.UsageReportColumnEnum.UMG_TRANSACTION_ID;
import static com.ca.umg.business.tenant.report.usage.UsageReportColumnEnum.valueOfByHeaderName;
import static com.ca.umg.business.transaction.info.TransactionStatus.FAILURE;
import static com.ca.umg.business.transaction.info.TransactionStatus.SUCCESS;
import static java.lang.Double.valueOf;
import static java.lang.Math.ceil;
import static java.util.Locale.getDefault;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.business.tenant.report.usage.UsageReportColumnEnum;
import com.ca.umg.business.tenant.report.usage.UsageReportFilter;
import com.ca.umg.business.transaction.info.TransactionStatus;

@SuppressWarnings("PMD")
public class UsageReportQuery {

	private static final Logger LOGGER = LoggerFactory.getLogger(UsageReportQuery.class);

	private static final String LEFT_OUTER_JOIN = " left outer join ";

	private static final String SINGLE_QUOTE = "'";

	private static final String DOT = ".";

	private static final String UMG_RT_TRAN_TBL_ALIAS = "urt";

	private static final String BT_TXN_RT_TXN_MAP_ALIAS = "mapping";

	private static final String UMG_VER_ALIAS = "uv";

    private static final String UMG_RT_TRAN_TBL = "UMG_RUNTIME_TRANSACTION";

    private static final String BT_TXN_RT_TXN_MAP_TBL = "BATCH_TXN_RUNTIME_TXN_MAPPING";

    private static final String UMG_VER_TBL = "UMG_VERSION";

	private static final String REPORT_QUERY_SELECT_CLAUSE = "select urt.ID, "
			+ "urt.TENANT_ID, "
			+ "mapping.BATCH_ID, "
			+ "urt.VERSION_NAME, "
			+ "urt.MAJOR_VERSION, "
			+ "urt.MINOR_VERSION, "
			+ "urt.CLIENT_TRANSACTION_ID, "
			+ "urt.RUN_AS_OF_DATE, "
			+ "urt.CREATED_ON, "
			+ "urt.STATUS, "
			+ "urt.MAJOR_VERSION, "
			+ "urt.MINOR_VERSION, "
			+ "urt.RUNTIME_CALL_START, "
			+ "urt.RUNTIME_CALL_END, "
			+ "urt.ERROR_CODE, "
			+ "urt.CPU_USAGE, "
			+ "urt.FREE_MEMORY, "
			+ "urt.CPU_USAGE_AT_START, "
			+ "urt.FREE_MEMORY_AT_START, "
			+ "urt.IP_AND_PORT, "
			+ "urt.POOL_NAME, "
			+ "urt.ME2_WAITING_TIME, "
			+ "urt.NO_OF_ATTEMPTS, "
			+ "urt.ERROR_DESCRIPTION, "
			+ "urt.MODEL_EXECUTION_TIME, "
			+ "urt.ERROR_CODE, "
			+ "IFNULL(urt.MODEL_CALL_END, 0) - IFNULL(urt.MODEL_CALL_START, 0) as PROCESSING_TIME, " //
			+ "CASE WHEN mapping.BATCH_ID is not NULL THEN urt.TRANSACTION_MODE ELSE 'Online' end as TRANSACTION_MODE, " //
			+ "CASE WHEN ERROR_CODE is null THEN ''" //
			+ "WHEN trim(ERROR_CODE) = '' THEN ''" //
			+ "WHEN upper(trim(ERROR_CODE)) like 'RSE%' THEN 'System Exception'" //
			+ "WHEN upper(trim(ERROR_CODE)) like 'ME%' THEN 'System Exception'" //
			+ "WHEN upper(trim(ERROR_CODE)) like 'RMV%' THEN 'System Exception'" //
			+ "WHEN upper(trim(ERROR_CODE)) like 'RVE%' THEN 'Validation Exception'" //
			+ "ELSE '' END as ERROR_REASON, " //
			+ "CASE WHEN IS_TEST = 0 THEN 'Prod'" //
			+ "ELSE 'Test' END as TRANSACTION_TYPE,"
			+ "ml.EXECUTION_ENVIRONMENT,"
			+ "urt.MODEL_EXEC_ENV_NAME,"
			+ "urt.R_SERVE_PORT";

	private static final String REPORT_QUERY_FROM_CLAUSE = " from " + UMG_RT_TRAN_TBL + " " + UMG_RT_TRAN_TBL_ALIAS
			+ LEFT_OUTER_JOIN + BT_TXN_RT_TXN_MAP_TBL + " " + BT_TXN_RT_TXN_MAP_ALIAS
			+ " on urt.ID = mapping.TRANSACTION_ID"
			+ " and urt.TENANT_ID = mapping.TENANT_ID"
			+ LEFT_OUTER_JOIN + UMG_VER_TBL + " " + UMG_VER_ALIAS
			+ " on urt.version_name = uv.NAME"
			+ " join MODEL_LIBRARY ml on ml.ID=uv.MODEL_LIBRARY_ID"
			+ " and urt.major_version = uv.major_version"
			+ " and urt.minor_version = uv.minor_version";

    private static final String DERIVED_MODEL_NAME_QUERY = "select m.UMG_NAME from UMG_RUNTIME_TRANSACTION urt "
            + "	join UMG_VERSION uv on urt.VERSION_NAME = uv.NAME and urt.MAJOR_VERSION = uv.MAJOR_VERSION and urt.MINOR_VERSION = uv.MINOR_VERSION "
            + "	join MAPPING map on uv.MAPPING_ID = map.ID " + " join MODEL m on map.MODEL_ID = m.ID ";

	private static final String COUNT_SELECT_CLAUSE = "select count(*) as totalcount ";
	private static final String COUNT_QRY_FROM = "from " + UMG_RT_TRAN_TBL + " " + UMG_RT_TRAN_TBL_ALIAS;

	private static final String DEFAULT_ORDER_BY = " order by urt.created_on desc";

	private UsageReportQuery() {

	}

	public static String createTransactionCountQuery(final UsageReportFilter filter) {
		final StringBuilder sb = new StringBuilder(100);
		sb.append(COUNT_SELECT_CLAUSE);
		sb.append(COUNT_QRY_FROM);

		final String whereClause = createWhereClause(filter);
		if (whereClause != null && whereClause.length() > 0) {
			sb.append(whereClause);
		}

		LOGGER.debug("Usage Report : Create Transaction Count Query :" + sb.toString());
		return sb.toString();
	}

	public static String createUsageReportQuery(final UsageReportFilter filter) {
		final StringBuilder sb = new StringBuilder(100);
		sb.append(REPORT_QUERY_SELECT_CLAUSE);
		sb.append(REPORT_QUERY_FROM_CLAUSE);

		final String whereClause = createWhereClause(filter);
		if (whereClause != null && whereClause.length() > 0) {
			sb.append(whereClause);
		}

		sb.append(createOrderByClause(filter));
		sb.append(createLimitClause(filter));

		LOGGER.debug("Usage Report : Create Usage Report Query :" + sb.toString());
		return sb.toString();
	}

	public static String createGetAllUniqueModelNamesQuery(final String tenantId) {
		final StringBuilder sb = new StringBuilder(100);
		sb.append("select distinct NAME from UMG_VERSION where lower(TENANT_ID) = '");
		sb.append(tenantId.toLowerCase(getDefault()));
		sb.append("' order by NAME");

		LOGGER.debug("Usage Report : Create Transaction Count Query :" + sb.toString());
		return sb.toString();
	}

	public static String creategetAllUniqueModelVersionQuery(final String tenantId, final String tenantModelName) {
		final StringBuilder sb = new StringBuilder(200);
		sb.append("select distinct concat(MAJOR_VERSION, '.', MINOR_VERSION) as umg_version from UMG_VERSION where lower(TENANT_ID) = '");
		sb.append(tenantId.toLowerCase(getDefault()));
		sb.append("' and lower(name) = '");
		sb.append(tenantModelName.toLowerCase(getDefault()));
		sb.append("' order by MAJOR_VERSION, MINOR_VERSION");

		LOGGER.debug("Usage Report : Create Transaction Count Query :" + sb.toString());
		return sb.toString();
	}

	public static String getTableAlias(final UsageReportColumnEnum reportEnum) {
		String alias;
		if (reportEnum == BATCH_ID) {
			alias = BT_TXN_RT_TXN_MAP_ALIAS;
		} else {
			alias = UMG_RT_TRAN_TBL_ALIAS;
		}

		return alias;
	}

	public static String createOrderByClause(final UsageReportFilter filter) {
		final StringBuilder orderByClause = new StringBuilder();
		if (filter.getSortColumn() == null) {
			orderByClause.append(DEFAULT_ORDER_BY);
		} else {
			final UsageReportColumnEnum column = valueOfByHeaderName(filter.getSortColumn());
			final String tableAlias = getTableAlias(column);
			orderByClause.append(" order by ");

			switch (column) {
				case MODEL_VERSION:
					orderByClause.append(tableAlias).append(DOT);
					orderByClause.append(MAJOR_VERSION.getDbColumnName());
					orderByClause.append(", ");
					orderByClause.append(tableAlias).append(DOT);
					orderByClause.append(MINOR_VERSION.getDbColumnName());
					break;
				case REASON:
				case PROCESSING_TIME:
				case TRANSACTION_MODE:
				case TRANSACTION_TYPE:
					orderByClause.append(column.getDbColumnName());
					break;
				default:
					orderByClause.append(tableAlias).append(DOT);
					orderByClause.append(column.getDbColumnName());
					break;
			}

			if (filter.isDescending()) {
				orderByClause.append(" desc");
			} else {
				orderByClause.append(" asc");
			}
		}

		return orderByClause.toString();
	}

	private static String createWhereClause(final UsageReportFilter filter) {
		final StringBuilder whereConditions = new StringBuilder();
		whereConditions.append(" where ");
		boolean addedFirstCondition = false;

		addedFirstCondition = addWhereCondition(whereConditions,
				getDatesBetweenCondition(filter.getRunAsOfDateFrom(), filter.getRunAsOfDateTo(), CREATED_ON), addedFirstCondition);

		addedFirstCondition = addWhereCondition(whereConditions, getStringEqualCondition(filter.getTenantModelName(), MODEL), addedFirstCondition);
		addedFirstCondition = addWhereCondition(whereConditions, getStringEqualCondition(filter.getTenantCode(), TENANT_ID), addedFirstCondition);

		addedFirstCondition = addWhereCondition(whereConditions, getIntegerEqualCondition(filter.getMajorVersion(), MAJOR_VERSION), addedFirstCondition);
		addedFirstCondition = addWhereCondition(whereConditions, getIntegerEqualCondition(filter.getMinorVersion(), MINOR_VERSION), addedFirstCondition);

		addedFirstCondition = addWhereCondition(whereConditions, getStatusCondition(filter.getTransactionStatus(), PROCESSING_STATUS), addedFirstCondition);

		addedFirstCondition = addWhereCondition(whereConditions, getTransactionIdCondition(filter.getSearchString()), addedFirstCondition);
		
		addedFirstCondition = addWhereCondition(whereConditions, getTransactionIdCondition(filter.getSelectedTransactions()), addedFirstCondition);

		if (filter.getTenantModelName() != null) { // for filter transactions,
			// please include this condition
			if (!filter.isIncludeTest()) {
				addedFirstCondition = addWhereCondition(whereConditions, getBooleanEqualCondition(filter.isIncludeTest(), IS_TEST), addedFirstCondition);
			}
		}

		return whereConditions.toString();
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

	public static boolean isValueNotBlankAndNotAnyOrNotAll(final String value) {
		return isNotBlank(value) && !"Any".equalsIgnoreCase(value) && !"All".equalsIgnoreCase(value);
	}

	private static String getStatusCondition(final String value, final UsageReportColumnEnum column) {
		String condition = null;
		if (isValueNotBlankAndNotAnyOrNotAll(value)) {
			if (TransactionStatus.valuOf(value) == SUCCESS) {
				condition = "lower(" + getTableAlias(column) + DOT + column.getDbColumnName() + ") = '" + SUCCESS.getStatus().toLowerCase(getDefault())
						+ SINGLE_QUOTE;
			} else if (TransactionStatus.valuOf(value) == FAILURE) {
				condition = "lower(" + getTableAlias(column) + DOT + column.getDbColumnName() + ") != '" + SUCCESS.getStatus().toLowerCase(getDefault())
						+ SINGLE_QUOTE;
			}
		}

		return condition;
	}

	private static String getStringEqualCondition(final String value, final UsageReportColumnEnum column) {
		String condition = null;
		if (isValueNotBlankAndNotAnyOrNotAll(value)) {
			condition = "lower(" + getTableAlias(column) + DOT + column.getDbColumnName() + ") = '" + value.toLowerCase(getDefault()) + SINGLE_QUOTE;
		}

		return condition;
	}

	private static String getIntegerEqualCondition(final Integer value, final UsageReportColumnEnum column) {
		String condition = null;
		if (value != null) {
			condition = getTableAlias(column) + DOT + column.getDbColumnName() + " = " + value;
		}

		return condition;
	}

	private static String getDatesBetweenCondition(final Long startDate, final Long endDate, final UsageReportColumnEnum column) {
		String condition = null;

		if (startDate != null && endDate != null) {
			condition = getTableAlias(column) + DOT + column.getDbColumnName() + " between " + startDate + " and " + endDate;
		} else if (startDate != null) {
			condition = getTableAlias(column) + DOT + column.getDbColumnName() + " >= " + startDate;
		} else if (endDate != null) {
			condition = getTableAlias(column) + DOT + column.getDbColumnName() + " <= " + endDate;
		}

		return condition;
	}

	public static String createLimitClause(final UsageReportFilter filter) {
		String limitClause = "";
		if (isPagingRequired(filter)) {
			StringBuilder sb = new StringBuilder();
			sb.append(" limit ");
			sb.append(getLimitOffset(filter));
			sb.append(", ");
			sb.append(getLimitRowCount(filter));
			limitClause = sb.toString();
		}
		return limitClause;
	}

	public static boolean isLastPage(final UsageReportFilter filter) {
		boolean lastPage = false;
		if (isPagingRequired(filter) && getPageCount(filter) == filter.getPage()) {
			lastPage = true;
		}

		return lastPage;
	}

	public static int getPageCount(final UsageReportFilter filter) {
		int pageCount = -1;
		if (isPagingRequired(filter)) {
			pageCount = valueOf(ceil(valueOf(filter.getMatchedTransactionCount()) / filter.getPageSize())).intValue();
		}

		return pageCount;
	}

	public static boolean isPagingRequired(final UsageReportFilter filter) {
		return filter.getPage() > 0 && filter.getPageSize() > 0 && filter.getMatchedTransactionCount() > 0;
	}

	public static int getLimitRowCount(final UsageReportFilter filter) {
		int rowCount = -1;
		if (isPagingRequired(filter)) {
			if (isLastPage(filter)) {
				final int diff = filter.getPageSize() * filter.getPage() - (int)filter.getMatchedTransactionCount();
				rowCount = filter.getPageSize() - diff;
			} else {
				rowCount = filter.getPageSize();
			}
		}

		return rowCount;
	}

	public static int getLimitOffset(final UsageReportFilter filter) {
		int offset = -1;
		if (isPagingRequired(filter)) {
			offset = filter.getPageSize() * filter.getPage() - filter.getPageSize();
		}

		return offset;
	}

	private static String getBooleanEqualCondition(final boolean value, final UsageReportColumnEnum column) {
		String condition;
		if (value) {
			condition = getIntegerEqualCondition(1, column);
		} else {
			condition = getIntegerEqualCondition(0, column);
		}

		return condition;
	}

	public static String createGetDerivedModelNameQuery(final String transactionId, final Integer majorVersion, final Integer minorVersion) {
		final StringBuilder sb = new StringBuilder();
		sb.append(DERIVED_MODEL_NAME_QUERY);
		sb.append(" where urt.ID = '").append(transactionId).append(SINGLE_QUOTE);
		sb.append(" and urt.MAJOR_VERSION = ").append(majorVersion);
		sb.append(" and urt.MINOR_VERSION = ").append(minorVersion);
		sb.append(" and uv.TENANT_ID = '").append(RequestContext.getRequestContext().getTenantCode()).append(SINGLE_QUOTE);
		return sb.toString();
	}

	private static String getTransactionListForInOper(final String value) {
		final StringBuffer sb = new StringBuffer();
		final String[] transactions = value.split("[,]");
		for (String transaction : transactions) {
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

	private static String getTransactionIdCondition(final String value) {
		if (value != null) {
			final String transactionList = getTransactionListForInOper(value);
			final String inList = " in (" + transactionList + ")";
			final StringBuffer sb = new StringBuffer();
			sb.append("(");
			sb.append("lower(").append(getTableAlias(UMG_TRANSACTION_ID) + DOT + UMG_TRANSACTION_ID.getDbColumnName()).append(")").append(inList);
			sb.append(" or ");
			sb.append("lower(").append(getTableAlias(TENANT_TRANSACTION_ID) + DOT + TENANT_TRANSACTION_ID.getDbColumnName()).append(")").append(inList);
			sb.append(")");
			return sb.toString();
		} else {
			return null;
		}
	}
	
	private static String getTransactionIdCondition(final List<String> selectedTransactions) {
		if (selectedTransactions != null && selectedTransactions.size() > 0) {
			final String transactionList = getTransactionListForInOper(selectedTransactions);
			final String inList = " in (" + transactionList + ") ";
			final StringBuffer sb = new StringBuffer();
			sb.append("lower(").append(getTableAlias(UMG_TRANSACTION_ID) + DOT + UMG_TRANSACTION_ID.getDbColumnName()).append(")").append(inList);
			return sb.toString();
		} else {
			return null;
		}
	}
}