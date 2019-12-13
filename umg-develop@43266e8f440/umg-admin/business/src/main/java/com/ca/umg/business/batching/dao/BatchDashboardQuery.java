package com.ca.umg.business.batching.dao;

import static com.ca.umg.business.batching.dao.BatchDashboardColumnEnum.BATCH_EXEC_TIME;
import static com.ca.umg.business.batching.dao.BatchDashboardColumnEnum.BATCH_INPUT_FILE;
import static com.ca.umg.business.batching.dao.BatchDashboardColumnEnum.BATCH_OUTPUT_FILE;
import static com.ca.umg.business.batching.dao.BatchDashboardColumnEnum.CREATED_ON;
import static com.ca.umg.business.batching.dao.BatchDashboardColumnEnum.END_TIME;
import static com.ca.umg.business.batching.dao.BatchDashboardColumnEnum.EXECUTION_ENVIRONMENT;
import static com.ca.umg.business.batching.dao.BatchDashboardColumnEnum.FAILED_COUNT;
import static com.ca.umg.business.batching.dao.BatchDashboardColumnEnum.ID;
import static com.ca.umg.business.batching.dao.BatchDashboardColumnEnum.IS_TEST;
import static com.ca.umg.business.batching.dao.BatchDashboardColumnEnum.MODELLING_ENVIRONMENT;
import static com.ca.umg.business.batching.dao.BatchDashboardColumnEnum.NOT_PICKED_COUNT;
import static com.ca.umg.business.batching.dao.BatchDashboardColumnEnum.START_TIME;
import static com.ca.umg.business.batching.dao.BatchDashboardColumnEnum.STATUS;
import static com.ca.umg.business.batching.dao.BatchDashboardColumnEnum.SUCCESS_COUNT;
import static com.ca.umg.business.batching.dao.BatchDashboardColumnEnum.TENANT_ID;
import static com.ca.umg.business.batching.dao.BatchDashboardColumnEnum.TOTAL_RECORDS;
import static com.ca.umg.business.batching.dao.BatchDashboardColumnEnum.TRANSACTION_MODE;
import static com.ca.umg.business.batching.dao.BatchDashboardColumnEnum.valueOfByHeaderName;
import static java.lang.Double.valueOf;
import static java.lang.Math.ceil;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.business.util.AdminUtil;

@SuppressWarnings("PMD")
public class BatchDashboardQuery {

	private static final String BATCH_TRANS_TBL = "BATCH_TRANSACTION";
	
	private static final String FROM_CLAUSE = " FROM " + BATCH_TRANS_TBL + " ";
	
	private static final String SINGLE_QUOTE = "'";
	
	private static final String SELECT_CLAUSE = "SELECT " + ID.getDbColumnName() + ", "
			+ BATCH_INPUT_FILE.getDbColumnName() + ", "
			+ BATCH_OUTPUT_FILE.getDbColumnName() + ", "
			+ IS_TEST.getDbColumnName() + ", "
			+ STATUS.getDbColumnName() + ", "
			+ TOTAL_RECORDS.getDbColumnName() + ", "
			+ SUCCESS_COUNT.getDbColumnName() + ", "
			+ NOT_PICKED_COUNT.getDbColumnName() + ", "
			+ FAILED_COUNT.getDbColumnName() + ", "
			+ TRANSACTION_MODE.getDbColumnName() + ", "
			+ EXECUTION_ENVIRONMENT.getDbColumnName() + ", "
			+ MODELLING_ENVIRONMENT.getDbColumnName() + ", "
			+ START_TIME.getDbColumnName() + ", "
			+ CREATED_ON.getDbColumnName() + ", "
			+ "IFNULL(" + END_TIME.getDbColumnName() +", -1) as "+ END_TIME.getDbColumnName() + ", "
			+ "CASE WHEN IFNULL(" + END_TIME.getDbColumnName() +", 0) = 0 THEN -1 ELSE " + END_TIME.getDbColumnName() +"-"+ START_TIME.getDbColumnName() + " END as " + BATCH_EXEC_TIME.getDbColumnName();
			//+ "IFNULL(" + END_TIME.getDbColumnName() +", 0) - IFNULL(" + START_TIME.getDbColumnName() + ", 0) as " + BATCH_EXEC_TIME.getDbColumnName();
			
	private static final String DEFAULT_ORDER_BY = " order by created_on desc";
	
	private static final String BATCH_TXN_STATUS_QUERY = "select BATCH_ID, count(STATUS) AS STATUS_COUNT "
	    		+ "from BATCH_TXN_RUNTIME_TXN_MAPPING where BATCH_ID IN (BTCH_IDS) AND STATUS = 'STS' group by BATCH_ID";
	
	public static String getBatchTransCountQuery(final String batchIds, final String status) {
		String query = BATCH_TXN_STATUS_QUERY.replace("BTCH_IDS", batchIds);
		query = query.replace("STS", status);
		return query;
	}
	
	public static String getBatchTransactionCountQuery(final BatchDashboardFilter filter) {
		final StringBuilder sb = new StringBuilder();
		sb.append("SELECT COUNT(*) AS TOTAL_COUNT");
		sb.append(FROM_CLAUSE);
		sb.append(createWhereClause(filter));
		return sb.toString();
	}
	
	public static String getSelectQuery(final BatchDashboardFilter filter) {
		final StringBuilder sb = new StringBuilder();
		sb.append(SELECT_CLAUSE);
		sb.append(FROM_CLAUSE);
		sb.append(createWhereClause(filter));
		sb.append(createOrderByClause(filter));
		sb.append(createLimitClause(filter));
		return sb.toString();
	}
	
	private static String createWhereClause(final BatchDashboardFilter filter) {
		final StringBuilder whereConditions = new StringBuilder();
		whereConditions.append(" where ");
		boolean addedFirstCondition = false;

		addedFirstCondition = addWhereCondition(whereConditions, getStringLikeCondition(filter.getBatchId(), ID), addedFirstCondition);
		addedFirstCondition = addWhereCondition(whereConditions, getStringLikeCondition(filter.getInputFileName(), BATCH_INPUT_FILE), addedFirstCondition);
		addedFirstCondition = addWhereCondition(whereConditions, getStartTimeCondition(filter.getStartTime()), addedFirstCondition);
		addedFirstCondition = addWhereCondition(whereConditions, getEndTimeCondition(filter.getEndTime()), addedFirstCondition);
		addedFirstCondition = addWhereCondition(whereConditions, getTntCodeCondition(RequestContext.getRequestContext().getTenantCode()), addedFirstCondition);
		if (addedFirstCondition) {
			return whereConditions.toString();
		} else {
			return " ";
		}
	}
	
	private static String getStringLikeCondition(final String value, final BatchDashboardColumnEnum column) {
		String condition = null;
		if (isNotBlank(value)) {
			condition = "lower(" + column.getDbColumnName() + ") like '" + AdminUtil.getLikePattern(value) + SINGLE_QUOTE;
		}

		return condition;
	}
	
	private static String getStartTimeCondition(final Long startDate) {
		String condition = null;

		if (startDate != null) {
			condition = START_TIME.getDbColumnName() + " >= " + startDate;
		}

		return condition;
	}
	
	private static String getEndTimeCondition(final Long endDate) {
		String condition = null;

		if (endDate != null) {
			condition = END_TIME.getDbColumnName() + " <= " + endDate;
		}

		return condition;
	}
	
	private static String getTntCodeCondition(final String tenantCode) {
		String condition = null;

		if (tenantCode != null) {
			condition = TENANT_ID.getDbColumnName() + " = '" + tenantCode + SINGLE_QUOTE;
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
	
	private static String createOrderByClause(final BatchDashboardFilter filter) {
		final StringBuilder orderByClause = new StringBuilder();
		if (isBlank(filter.getSortColumn())) {
			orderByClause.append(DEFAULT_ORDER_BY);
		} else {
			final BatchDashboardColumnEnum column = valueOfByHeaderName(filter.getSortColumn());
			orderByClause.append(" order by ");

			switch (column) {
				case ID:
				case BATCH_INPUT_FILE:
				case BATCH_OUTPUT_FILE:
				case IS_TEST:
				case STATUS:
				case TOTAL_RECORDS:
				case START_TIME:
				case END_TIME:	
				case BATCH_EXEC_TIME:	
				case SUCCESS_COUNT:
				case FAILED_COUNT:
				case BATCH_ID:
					orderByClause.append(ID.getDbColumnName());
					break;	
				default:
					orderByClause.append(DEFAULT_ORDER_BY);
			}

			if (filter.isDescending()) {
				orderByClause.append(" desc");
			} else {
				orderByClause.append(" asc");
			}
		}
		
		return orderByClause.toString();
	}
	
	private static String createLimitClause(final BatchDashboardFilter filter) {
		String limitClause = " ";
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
	
	private static boolean isPagingRequired(final BatchDashboardFilter filter) {
		return filter.getPage() > 0 && filter.getPageSize() > 0 && filter.getMatchedTransactionCount() > 0;
	}
	
	private static int getLimitOffset(final BatchDashboardFilter filter) {
		int offset = -1;
		if (isPagingRequired(filter)) {
			offset = filter.getPageSize() * filter.getPage() - filter.getPageSize();
		}

		return offset;
	}
	
	private static int getLimitRowCount(final BatchDashboardFilter filter) {
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
	
	private static boolean isLastPage(final BatchDashboardFilter filter) {
		boolean lastPage = false;
		if (isPagingRequired(filter) && getPageCount(filter) == filter.getPage()) {
			lastPage = true;
		}

		return lastPage;
	}
	
	private static int getPageCount(final BatchDashboardFilter filter) {
		int pageCount = -1;
		if (isPagingRequired(filter)) {
			pageCount = valueOf(ceil(valueOf(filter.getMatchedTransactionCount()) / filter.getPageSize())).intValue();
		}

		return pageCount;
	}
}