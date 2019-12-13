package com.ca.umg.business.transaction.dao;

import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.BATCH_ID;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.ERROR_CODE;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.ERROR_DESCRIPTION;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.LIBRARY_NAME;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.MAJOR_VERSION;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.MINOR_VERSION;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.MODEL;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.RUN_DATE_TIME;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.TENANT_ID;
import static com.ca.umg.business.transaction.report.TransactionExcelReportEnum.TENANT_TRANSACTION_ID;
import static com.ca.umg.business.transaction.util.TransactionUtil.getErrorCodePattern;
import static com.ca.umg.business.util.AdminUtil.getLikePattern;
import static java.util.Locale.getDefault;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.umg.business.transaction.info.TransactionFilter;
import com.ca.umg.business.transaction.report.TransactionExcelReportEnum;

@Repository
public class TransactionExcelReportQuery {

	private static final String LEFT_OUTER_JOIN = " left outer join ";

	private static final String ESCAPE_FOR_SINGLE_QUOTE = "\\\\'";

	private static final String SINGLE_QUOTE_REXP = "[']";

	private static final String SINGLE_QUOTE = "'";

	private static final String ESCAPE_FOR_PERCENTAGE = "\\\\%";

	private static final String PERCENTAGE_REXP = "[%]";

	private static final String PERCENTAGE = "%";

	private static final String ESCAPE_FOR_UNDER_SCORE = "\\\\_";

	private static final String UNDER_SCORE_REXP = "[_]";

	private static final String UNDER_SCORE = "_";

	private static final String DOT = ".";

	private static final String UMG_RT_TRAN_TBL_ALIAS = "urt";

	private static final String BT_TXN_RT_TXN_MAP_ALIAS = "mapping";

	private static final String MDL_LIB_TBL_ALIAS = "ml";

	private static final String UMG_VER_ALIAS = "uv";

	private static final String UMG_RT_TRAN_TBL = "UMG_RUNTIME_TRANSACTION";

	private static final String BT_TXN_RT_TXN_MAP_TBL = "BATCH_TXN_RUNTIME_TXN_MAPPING";

	private static final String MDL_LIB_TBL = "MODEL_LIBRARY";

	private static final String UMG_VER_TBL = "UMG_VERSION";

	private static final String REPORT_QUERY_SELECT_CLAUSE = "select urt.ID, "
			+ "urt.TENANT_ID, "
			+ "mapping.BATCH_ID, "
			+ "urt.VERSION_NAME, "
			+ "urt.MAJOR_VERSION, "
			+ "urt.MINOR_VERSION, "
			+ "urt.CLIENT_TRANSACTION_ID, "
			+ "urt.RUN_AS_OF_DATE, "
			+ "urt.STATUS, "
			+ "urt.MAJOR_VERSION, "
			+ "urt.MINOR_VERSION, "
			+ "urt.RUNTIME_CALL_START, "
			+ "urt.RUNTIME_CALL_END, "
			+ "urt.ERROR_CODE, "
			+ "ml.EXECUTION_LANGUAGE ";

	private static final String REPORT_QUERY_FROM_CLAUSE = " from " + UMG_RT_TRAN_TBL + " " + UMG_RT_TRAN_TBL_ALIAS
			+ LEFT_OUTER_JOIN + BT_TXN_RT_TXN_MAP_TBL + " " + BT_TXN_RT_TXN_MAP_ALIAS
			+ " on urt.ID = mapping.TRANSACTION_ID"
			+ " and urt.TENANT_ID = mapping.TENANT_ID"
			+ LEFT_OUTER_JOIN + UMG_VER_TBL + " " + UMG_VER_ALIAS
			+ " on urt.version_name = uv.NAME"
			+ " and urt.major_version = uv.major_version"
			+ " and urt.minor_version = uv.minor_version"
			+ LEFT_OUTER_JOIN + MDL_LIB_TBL + " " + MDL_LIB_TBL_ALIAS
			+ " on uv.MODEL_LIBRARY_ID = ml.id";

	private static final String MIN_MAX_QRY_FRM_WITH_BTCH_ID = " from " + UMG_RT_TRAN_TBL + " " + UMG_RT_TRAN_TBL_ALIAS
			+ LEFT_OUTER_JOIN + BT_TXN_RT_TXN_MAP_TBL + " " + BT_TXN_RT_TXN_MAP_ALIAS
			+ " on urt.ID = mapping.TRANSACTION_ID"
			+ " and urt.TENANT_ID = mapping.TENANT_ID ";

	public static final String MIN_MAX_QUERY_SELECT_CLAUSE = "select max(urt.RUN_AS_OF_DATE) as maxdate, "
			+ "min(urt.RUN_AS_OF_DATE) mindate ";

	private static final String REPORT_QUERY_ORDER_BY = " order by urt.created_on desc";

	@Inject
	private SystemParameterProvider systemParameterProvider;

	private boolean addedFirstCondition;

	public String createGetMaxMinRunAsOfDateQuery(final TransactionFilter filter, final String tenantId) {
		final StringBuilder sb  = new StringBuilder();
		sb.append(MIN_MAX_QUERY_SELECT_CLAUSE);
		sb.append(MIN_MAX_QRY_FRM_WITH_BTCH_ID);

		final String whereClause = createWhereClause(filter, tenantId);
		if (whereClause != null && whereClause.length() > 0) {
			sb.append(whereClause);
		}

		return sb.toString();
	}

	public String createLoadReportQuery(final TransactionFilter filter, final String tenantId) {
		final StringBuilder sb  = new StringBuilder();
		sb.append(REPORT_QUERY_SELECT_CLAUSE);
		sb.append(REPORT_QUERY_FROM_CLAUSE);


		final String whereClause = createWhereClause(filter, tenantId);
		if (whereClause != null && whereClause.length() > 0) {
			sb.append(whereClause);
		}

		sb.append(REPORT_QUERY_ORDER_BY);

		return sb.toString();
	}

	private String getTableAlias(final TransactionExcelReportEnum reportEnum) {
		String alias;
		if (reportEnum == BATCH_ID) {
			alias = BT_TXN_RT_TXN_MAP_ALIAS;
		} else {
			alias = UMG_RT_TRAN_TBL_ALIAS;
		}

		return alias;
	}

	private String createWhereClause(final TransactionFilter filter, final String tenantId) {
		final StringBuilder whereConditions = new StringBuilder();
		whereConditions.append(" where ");
		addedFirstCondition = false;

		addWhereCondition(whereConditions, getStringEqualCondition(tenantId, TENANT_ID));
		addWhereCondition(whereConditions, getStringEqualCondition(filter.getLibraryName(), LIBRARY_NAME));

		addWhereCondition(whereConditions, getDatesBetweenCondition(filter.getRunAsOfDateFrom(), filter.getRunAsOfDateTo(), RUN_DATE_TIME));
		addWhereCondition(whereConditions, getStringEqualCondition(filter.getTenantModelName(), MODEL));

		addWhereCondition(whereConditions, getStringLikeCondition(filter.getClientTransactionID(), TENANT_TRANSACTION_ID));

		addWhereCondition(whereConditions, getIntegerEqualCondition(filter.getMajorVersion(), MAJOR_VERSION));
		addWhereCondition(whereConditions, getIntegerEqualCondition(filter.getMinorVersion(), MINOR_VERSION));
		//TODO commented this for umg-4200
				//need to change according to new filter object as the method is used for usage report
		//addWhereCondition(whereConditions, getBooleanEqualCondition(filter.isTestTxn(), IS_TEST));

		addWhereCondition(whereConditions, getStringLikeCondition(getErrorCodePattern(filter, systemParameterProvider), ERROR_CODE));

		addWhereCondition(whereConditions,getErrorDescCondition(filter));

		addWhereCondition(whereConditions, getStringEqualCondition(filter.getBatchId(), BATCH_ID));

		return whereConditions.toString();
	}

	private String getErrorDescCondition(final TransactionFilter filter) {
		String errorCondition = null;
		final String errorDescCondition = getStringLikeCondition(filter.getErrorDescription(), ERROR_DESCRIPTION);
		final String errorCodeCondition = getStringLikeCondition(filter.getErrorDescription(), ERROR_CODE);

		if (errorDescCondition != null && errorCodeCondition != null) {
			final StringBuilder sb = new StringBuilder();
			sb.append(" ( ").append(errorDescCondition);
			sb.append(" or ");
			sb.append(errorCodeCondition).append(" ) ");
			errorCondition = sb.toString();
		}

		return errorCondition;
	}

	private void addWhereCondition(final StringBuilder otherJoinConditions, final String joinCondition) {
		if (joinCondition != null) {
			if (addedFirstCondition) {
				otherJoinConditions.append(" and ").append(joinCondition);
			} else {
				otherJoinConditions.append(joinCondition);
			}

			addedFirstCondition = true;
		}
	}

	private boolean isValueNotBlankAndNotAny(final String value) {
		return StringUtils.isNotBlank(value) && !"Any".equalsIgnoreCase(value);
	}

	private String getStringEqualCondition(final String value, final TransactionExcelReportEnum column) {
		String condition = null;
		if (isValueNotBlankAndNotAny(value)) {
			condition = "lower(" + getTableAlias(column) + DOT + column.getDbColumnName() + ") = '" + value.toLowerCase(getDefault()) + SINGLE_QUOTE;
		}

		return condition;
	}

	private String getStringLikeCondition(final String value, final TransactionExcelReportEnum column) {
		String condition = null;
		if (StringUtils.isNotBlank(value)) {
			condition = "lower(" + getTableAlias(column) + DOT + column.getDbColumnName() + ") like '" + getLikePattern(getFormatedString(value)) + SINGLE_QUOTE;
		}

		return condition;
	}

	private String getIntegerEqualCondition(final Integer value, final TransactionExcelReportEnum column) {
		String condition = null;
		if (value != null) {
			condition = getTableAlias(column) + DOT + column.getDbColumnName() + " = " + value;
		}

		return condition;
	}

	/*private String getBooleanEqualCondition(final boolean value, final TransactionExcelReportEnum column) {
		String condition;
		if (value) {
			condition = getIntegerEqualCondition(1, column);
		} else {
			condition = getIntegerEqualCondition(0, column);
		}

		return condition;
	}
*/
	private String getDatesBetweenCondition(final Long startDate, final Long endDate, final TransactionExcelReportEnum column) {
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

	private String getFormatedString(final String value) {
		String formattedValue = value;

		if (value.indexOf(SINGLE_QUOTE) > -1) {
			formattedValue = value.replaceAll(SINGLE_QUOTE_REXP,  ESCAPE_FOR_SINGLE_QUOTE);
		}

		if (value.indexOf(PERCENTAGE) > -1) {
			formattedValue = value.replaceAll(PERCENTAGE_REXP,  ESCAPE_FOR_PERCENTAGE);
		}

		if (value.indexOf(UNDER_SCORE) > -1) {
			formattedValue = value.replaceAll(UNDER_SCORE_REXP,  ESCAPE_FOR_UNDER_SCORE);
		}

		return formattedValue.toLowerCase(getDefault());
	}

	public void setSystemParameterProvider(final SystemParameterProvider systemParameterProvider) {
		this.systemParameterProvider = systemParameterProvider;
	}
}