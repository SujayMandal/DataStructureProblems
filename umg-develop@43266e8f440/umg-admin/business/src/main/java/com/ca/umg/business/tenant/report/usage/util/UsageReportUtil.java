package com.ca.umg.business.tenant.report.usage.util;

import static com.ca.umg.business.tenant.report.usage.UsageReportColumnEnum.BATCH_ID;
import static com.ca.umg.business.tenant.report.usage.UsageReportColumnEnum.CREATED_ON;
import static com.ca.umg.business.tenant.report.usage.UsageReportColumnEnum.MAJOR_VERSION;
import static com.ca.umg.business.tenant.report.usage.UsageReportColumnEnum.MINOR_VERSION;
import static com.ca.umg.business.tenant.report.usage.UsageReportColumnEnum.MODEL;
import static com.ca.umg.business.tenant.report.usage.UsageReportColumnEnum.PROCESSING_STATUS;
import static com.ca.umg.business.tenant.report.usage.UsageReportColumnEnum.PROCESSING_TIME;
import static com.ca.umg.business.tenant.report.usage.UsageReportColumnEnum.REASON;
import static com.ca.umg.business.tenant.report.usage.UsageReportColumnEnum.TENANT_ID;
import static com.ca.umg.business.tenant.report.usage.UsageReportColumnEnum.TENANT_TRANSACTION_ID;
import static com.ca.umg.business.tenant.report.usage.UsageReportColumnEnum.TRANSACTION_MODE;
import static com.ca.umg.business.tenant.report.usage.UsageReportColumnEnum.TRANSACTION_TYPE;
import static com.ca.umg.business.tenant.report.usage.UsageReportColumnEnum.UMG_TRANSACTION_ID;
import static com.ca.umg.business.util.AdminUtil.getDateFormatMillisForEst;
import static java.util.Locale.getDefault;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.ca.umg.business.tenant.report.usage.ExecutionReportEnum;
import com.ca.umg.business.tenant.report.usage.UsageTransactionInfo;
import com.ca.umg.business.transaction.info.TransactionStatus;

@SuppressWarnings("PMD")
public final class UsageReportUtil {

	private static final String MS = "ms";

	private static final String VERSION_SAPERATOR = ".";

	//		RUN_AS_OF_DATE_FORMAT.set(new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss", getDefault()));

	public static String getProcessingTime(final long processingTime) {
		String value = " ";
		if(processingTime > 0){
			value = processingTime + " " + MS;
		}
		return value;
	}

	public static String getModelVersion(final SqlRowSet sqlRowSet) {
		final int majorVersion = sqlRowSet.getInt(MAJOR_VERSION.getDbColumnName());
		final int minorVersion = sqlRowSet.getInt(MINOR_VERSION.getDbColumnName());
		return getModelVersion(majorVersion, minorVersion);
	}

	public static String getModelVersion(final ResultSet rs) throws SQLException {
		final int majorVersion = rs.getInt(MAJOR_VERSION.getDbColumnName());
		final int minorVersion = rs.getInt(MINOR_VERSION.getDbColumnName());
		return getModelVersion(majorVersion, minorVersion);
	}

	public static String getModelVersion(final int majorVersion, final int minorVersion) {
		return majorVersion + VERSION_SAPERATOR + minorVersion;
	}

	public static String getFormattedRunAsOfDate(final SqlRowSet sqlRowSet) {
		final long runAsOfDateMilliseconds = sqlRowSet.getLong(CREATED_ON.getDbColumnName());
		return getFormattedRunAsOfDate(runAsOfDateMilliseconds);
	}

	public static String getFormattedRunAsOfDate(final ResultSet rs) throws SQLException {
		final long runAsOfDateMilliseconds = rs.getLong(CREATED_ON.getDbColumnName());
		return getFormattedRunAsOfDate(runAsOfDateMilliseconds);
	}

	public static String getFormattedRunAsOfDate(final long runAsOfDateMilliseconds) {
		return getDateFormatMillisForEst(runAsOfDateMilliseconds, null);
	}

	public static String getProcessingStatus(final SqlRowSet sqlRowSet) {
		final String cellValue = sqlRowSet.getString(PROCESSING_STATUS.getDbColumnName());
		return getProcessingStatus(cellValue);
	}

	public static String getProcessingStatus(final ResultSet sqlRowSet) throws SQLException {
		final String cellValue = sqlRowSet.getString(PROCESSING_STATUS.getDbColumnName());
		return getProcessingStatus(cellValue);
	}

	public static String getProcessingStatus(final String cellValue) {
		final TransactionStatus status = TransactionStatus.valuOf(cellValue);
		return status.getReportStatus();
	}
	public static String getPlatformExecutionTime(final SqlRowSet sqlRowSet) {
		String value = "";
		if(ExecutionReportEnum.RUNTIME_CALL_END.getDbColumnName() != null  && ExecutionReportEnum.RUNTIME_CALL_START.getDbColumnName() != null){
		final long SysExeTime = sqlRowSet.getLong(ExecutionReportEnum.RUNTIME_CALL_END.getDbColumnName())-sqlRowSet.getLong(ExecutionReportEnum.RUNTIME_CALL_START.getDbColumnName());
		if(SysExeTime > 0){
		value = String.valueOf(SysExeTime) + "ms";
		}
		}
		return value;
	}

	public static String getFormattedDate(final Long dateTime) {
		final SimpleDateFormat df = new SimpleDateFormat("yyyy-MMM-dd", getDefault());
		String formattedDate = "";
		if (dateTime != null) {
			formattedDate = df.format(new Date(dateTime));
		}

		return formattedDate;
	}

	public static UsageTransactionInfo valueOf(final SqlRowSet sqlRowSet) {
		final UsageTransactionInfo usageTransactionInfo = new UsageTransactionInfo();
		usageTransactionInfo.setTenantTransactionId(sqlRowSet.getString(TENANT_TRANSACTION_ID.getDbColumnName()));
		usageTransactionInfo.setTenantId(sqlRowSet.getString(TENANT_ID.getDbColumnName()));
		usageTransactionInfo.setTransactionMode(sqlRowSet.getString(TRANSACTION_MODE.getDbColumnName()));
		usageTransactionInfo.setBatchId(sqlRowSet.getString(BATCH_ID.getDbColumnName()));
		usageTransactionInfo.setModel(sqlRowSet.getString(MODEL.getDbColumnName()));
		usageTransactionInfo.setModelVersion(getModelVersion(sqlRowSet));
		usageTransactionInfo.setRunDateTime(getFormattedRunAsOfDate(sqlRowSet));
		usageTransactionInfo.setProcessingStatus(getProcessingStatus(sqlRowSet));
		usageTransactionInfo.setFailureReason(sqlRowSet.getString(REASON.getDbColumnName()));
		usageTransactionInfo.setProcessingTime(getProcessingTime(sqlRowSet.getLong(PROCESSING_TIME.getDbColumnName())));
		usageTransactionInfo.setUMGTransactionId(sqlRowSet.getString(UMG_TRANSACTION_ID.getDbColumnName()));
		usageTransactionInfo.setTransactionType(sqlRowSet.getString(TRANSACTION_TYPE.getDbColumnName()));
		usageTransactionInfo.setSelected(false);
		return usageTransactionInfo;
	}

	public static UsageTransactionInfo valueOf(final ResultSet rs) throws SQLException {
		final UsageTransactionInfo usageTransactionInfo = new UsageTransactionInfo();
		usageTransactionInfo.setTenantTransactionId(rs.getString(TENANT_TRANSACTION_ID.getDbColumnName()));
		usageTransactionInfo.setTenantId(rs.getString(TENANT_ID.getDbColumnName()));
		usageTransactionInfo.setTransactionMode(rs.getString(TRANSACTION_MODE.getDbColumnName()));
		usageTransactionInfo.setBatchId(rs.getString(BATCH_ID.getDbColumnName()));
		usageTransactionInfo.setModel(rs.getString(MODEL.getDbColumnName()));
		usageTransactionInfo.setModelVersion(getModelVersion(rs));
		usageTransactionInfo.setRunDateTime(getFormattedRunAsOfDate(rs));
		usageTransactionInfo.setProcessingStatus(getProcessingStatus(rs));
		usageTransactionInfo.setFailureReason(rs.getString(REASON.getDbColumnName()));
		usageTransactionInfo.setProcessingTime(getProcessingTime(rs.getLong(PROCESSING_TIME.getDbColumnName())));
		usageTransactionInfo.setUMGTransactionId(rs.getString(UMG_TRANSACTION_ID.getDbColumnName()));
		usageTransactionInfo.setTransactionType(rs.getString(TRANSACTION_TYPE.getDbColumnName()));
		usageTransactionInfo.setSelected(false);
		return usageTransactionInfo;
	}

	public static List<UsageTransactionInfo> buildFromRowSet(final SqlRowSet sqlRowSet) {
		final List<UsageTransactionInfo> list = new ArrayList<UsageTransactionInfo>();

		if (sqlRowSet != null) {
			while (sqlRowSet.next()) {
				list.add(valueOf(sqlRowSet));
			}
		}
		return list;
	}
}