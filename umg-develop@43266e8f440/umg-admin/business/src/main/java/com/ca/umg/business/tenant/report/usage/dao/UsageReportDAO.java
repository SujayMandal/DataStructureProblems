package com.ca.umg.business.tenant.report.usage.dao;

import static com.ca.framework.core.exception.BusinessException.newBusinessException;
import static com.ca.framework.core.exception.BusinessException.raiseBusinessException;
import static com.ca.framework.core.exception.SystemException.newSystemException;
import static com.ca.umg.business.exception.codes.BusinessExceptionCodes.BSE0000503;
import static com.ca.umg.business.exception.codes.BusinessExceptionCodes.BSE0000506;
import static com.ca.umg.business.tenant.report.usage.dao.UsageReportQuery.createGetAllUniqueModelNamesQuery;
import static com.ca.umg.business.tenant.report.usage.dao.UsageReportQuery.createGetDerivedModelNameQuery;
import static com.ca.umg.business.tenant.report.usage.dao.UsageReportQuery.createTransactionCountQuery;
import static com.ca.umg.business.tenant.report.usage.dao.UsageReportQuery.createUsageReportQuery;
import static com.ca.umg.business.tenant.report.usage.dao.UsageReportQuery.creategetAllUniqueModelVersionQuery;
import static com.ca.umg.business.tenant.report.usage.util.UsageReportUtil.valueOf;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.tenant.report.usage.UsageReportFilter;
import com.ca.umg.business.tenant.report.usage.UsageTransactionInfo;
import com.ca.umg.business.tenant.report.usage.delegate.TenantUsageReportDelegate;

@Repository
@SuppressWarnings("PMD")
public class UsageReportDAO {

	private static final Logger LOGGER = LoggerFactory.getLogger(UsageReportDAO.class);

	@Inject
	@Named(value = "dataSource")
	private DataSource dataSource;

	@Inject
	private TenantUsageReportDelegate tenantUsageReportDelegate;

	private JdbcTemplate jdbcTemplate;

	private final Object lock = new Object();

	@PostConstruct
	public void initializeTemplate() {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public Map<String, Object> getTransactionCount(final UsageReportFilter filter) {
		final String query = createTransactionCountQuery(filter);
		LOGGER.debug("Get Transaction Count Query:" + query);
		return jdbcTemplate.queryForMap(query);
	}

	public SqlRowSet loadTransactionsRowSet(final UsageReportFilter filter) throws BusinessException {
		try {
			final String query = createUsageReportQuery(filter);
			LOGGER.debug("Usage Report Query:" + query);
			return jdbcTemplate.queryForRowSet(query);
		} catch (DataAccessException ex) {
			LOGGER.debug("While fecthing records for the Usage Report, got below exception");
			newBusinessException(BSE0000503, new Object[] { "Fetching records for Usage Report Failed", ex.getMessage() }, ex);
		}

		throw raiseBusinessException(BSE0000503, new Object[] { "Transactions are not loaded from database for usage report" });
	}

	public void setDataSource(final DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public List<String> getAllUniqueModelNames(final String tenantId) {
		final String query = createGetAllUniqueModelNamesQuery(tenantId);
		LOGGER.debug("Get All Unique Model Names Query:" + query);
		return jdbcTemplate.queryForList(query, String.class);
	}

	public List<String> getAllUniqueModelVersion(final String tenantId, final String tenantModelName) {
		final String query = creategetAllUniqueModelVersionQuery(tenantId, tenantModelName);
		LOGGER.debug("Get All Unique Model Versions Query:" + query);
		return jdbcTemplate.queryForList(query, String.class);
	}

	public List<UsageTransactionInfo> loadTransactionsRowSet1(final UsageReportFilter filter)
			throws BusinessException {
		try {
			final String query = createUsageReportQuery(filter);
			LOGGER.debug("Usage Report Query:" + query);
			final PreparedStatementCreator preparedStatement = new PreparedStatementCreatorImpl(query);
			return jdbcTemplate.query(preparedStatement, new TransactionRowMapper());
		} catch (DataAccessException ex) {
			LOGGER.debug("While fecthing records for the Usage Report, got below exception");
			newBusinessException(BSE0000503, new Object[] { "Fetching records for Usage Report Failed", ex.getMessage() }, ex);
		}

		throw raiseBusinessException(BSE0000503, new Object[] { "Transactions are not loaded from database for usage report" });
	}

	public List<UsageTransactionInfo> executePreparedStamenent(final PreparedStatementCreator preparedStatement) {

		return jdbcTemplate.query(preparedStatement, new TransactionRowMapper());
	}

	class CancelThread implements Runnable {
		private final PreparedStatementCreatorImpl preparedStatementCreatorImpl;

		public CancelThread(final PreparedStatementCreatorImpl preparedStatementCreatorImpl) {
			this.preparedStatementCreatorImpl = preparedStatementCreatorImpl;
		}

		@Override
		public void run() {
			synchronized (lock) {
				if (preparedStatementCreatorImpl.getPreparedStatement() != null) {
					final boolean cancelStatus = tenantUsageReportDelegate.getUsageSearchRequestCancelStatusFromCache("");
					if (cancelStatus) {
						try {
							preparedStatementCreatorImpl.getPreparedStatement().cancel();
							notifyAll();
						} catch (SQLException sqle) {
							// TODO
							LOGGER.debug(sqle.getMessage());
						}
					}
				}
			}
		}
	}

	class PreparedStatementCreatorImpl implements PreparedStatementCreator {

		private final String sql;
		private PreparedStatement preparedStatement;

		public PreparedStatementCreatorImpl(final String sql) {
			this.sql = sql;
		}

		@Override
		public PreparedStatement createPreparedStatement(final Connection con) throws SQLException {
			preparedStatement = con.prepareStatement(sql);
			return preparedStatement;
		}

		public PreparedStatement getPreparedStatement() {
			return preparedStatement;
		}
	}

	class TransactionRowMapper implements RowMapper<UsageTransactionInfo> {
		@Override
		public UsageTransactionInfo mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			return valueOf(rs);
		}
	}

	public String getDerivedModelName(final String transactionId, final Integer majorVersion, final Integer minorVersion) throws SystemException {
		final String query = createGetDerivedModelNameQuery(transactionId, majorVersion, minorVersion);
		LOGGER.debug("Get Derived Model Name Query:" + query);
		try {
			final Map<String, Object> countMap = jdbcTemplate.queryForMap(query);
			return (String) countMap.get("UMG_NAME");
		} catch (DataAccessException dae) {
			throw newSystemException(BSE0000506, new Object[] { "Derived Model Name is empty for transaction Id :" + transactionId, "" });
		}
	}
}