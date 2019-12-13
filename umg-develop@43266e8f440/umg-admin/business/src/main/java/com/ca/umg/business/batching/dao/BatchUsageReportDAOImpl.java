package com.ca.umg.business.batching.dao;

import static com.ca.framework.core.exception.BusinessException.newBusinessException;
import static com.ca.framework.core.exception.BusinessException.raiseBusinessException;
import static com.ca.umg.business.exception.codes.BusinessExceptionCodes.BSE0000603;
import static com.ca.umg.business.batching.dao.BatchUsageReportQuery.createAllBatchUsageReportQuery;
import static com.ca.umg.business.batching.dao.BatchUsageReportQuery.createBatchUsageReportQuery;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.business.batching.report.usage.BatchUsageReportFilter;
@Named
public class BatchUsageReportDAOImpl implements BatchUsageReportDAO {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BatchUsageReportDAOImpl.class);
	
	@Inject
	@Named(value = "dataSource")
	private DataSource dataSource;
	
	private JdbcTemplate jdbcTemplate;

	@PostConstruct
	public void initializeTemplate() {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	// returns selected transaction
	@Override
	public SqlRowSet loadBatchTransactionsRowSet(final BatchUsageReportFilter filter) throws BusinessException {
		try {
			final String query = createBatchUsageReportQuery(filter);
			LOGGER.debug("Batch Usage Report by batchId Query:" + query);
			return jdbcTemplate.queryForRowSet(query);
		} catch (DataAccessException ex) {
			LOGGER.debug("While fecthing records for the Batch Usage Report by batchId, got below exception");
			newBusinessException(BSE0000603, new Object[] { "Fetching records for Batch Usage Report by batchId Failed", ex.getMessage() }, ex);
		}

		throw raiseBusinessException(BSE0000603, new Object[] { "Transactions are not loaded from database for batch usage report by batchId" });
	}
	
	// returns all transactions based on filter
	@Override
	public SqlRowSet loadAllBatchTransactionsRowSet(final BatchUsageReportFilter filter) throws BusinessException {
		try {
			final String query = createAllBatchUsageReportQuery(filter);
			LOGGER.debug("Batch Usage Report Query:" + query);
			return jdbcTemplate.queryForRowSet(query);
		} catch (DataAccessException ex) {
			LOGGER.debug("While fecthing records for the Batch Usage Report, got below exception");
			newBusinessException(BSE0000603, new Object[] { "Fetching records for Batch Usage Report Failed", ex.getMessage() }, ex);
		}

		throw raiseBusinessException(BSE0000603, new Object[] { "Transactions are not loaded from database for Batch usage report" });
	}

}
