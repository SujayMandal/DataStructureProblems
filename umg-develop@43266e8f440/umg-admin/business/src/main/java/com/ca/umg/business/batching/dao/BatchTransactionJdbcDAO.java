
package com.ca.umg.business.batching.dao;

import static com.ca.framework.core.exception.BusinessException.newBusinessException;
import static com.ca.framework.core.exception.BusinessException.raiseBusinessException;
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
import static com.ca.umg.business.batching.dao.BatchDashboardColumnEnum.TOTAL_RECORDS;
import static com.ca.umg.business.batching.dao.BatchDashboardColumnEnum.TRANSACTION_MODE;
import static com.ca.umg.business.batching.dao.BatchDashboardQuery.getBatchTransactionCountQuery;
import static com.ca.umg.business.exception.codes.BusinessExceptionCodes.BSE000098;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.batching.info.BatchTransactionInfo;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.util.AdminUtil;

@Repository
@SuppressWarnings("PMD")
public class BatchTransactionJdbcDAO {

	private static final Logger LOGGER = LoggerFactory.getLogger(BatchTransactionJdbcDAO.class);

	@Inject
	@Named(value = "dataSource")
	private DataSource dataSource;

	private JdbcTemplate jdbcTemplate;

	@PostConstruct
	public void initializeTemplate() {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public long getBatchTransactionCount(final BatchDashboardFilter filter) {
		final String query = getBatchTransactionCountQuery(filter);
		LOGGER.info("Get Batch Transaction Count Query:" + query);
		final Map<String, Object> countMap = jdbcTemplate.queryForMap(query);
		return ((Long) countMap.get("TOTAL_COUNT")).longValue();
	}

	public List<BatchTransactionInfo> getPagedBatchData(final BatchDashboardFilter filter) throws BusinessException {
		try {
			final String query = BatchDashboardQuery.getSelectQuery(filter);
			LOGGER.info("Batch Dashboard Query:" + query);
			return jdbcTemplate.query(query, new BatchTransactionRowMapper());
		} catch (DataAccessException ex) {
			LOGGER.debug("While fecthing records for the Usage Report, got below exception");
			newBusinessException(BSE000098, new Object[] { "Fetching records for Usage Report Failed", ex.getMessage() }, ex);
		}

		throw raiseBusinessException(BSE000098, new Object[] { "Transactions are not loaded from database for usage report" });
	}

	public void setDataSource(final DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	private class BatchTransactionRowMapper implements RowMapper<BatchTransactionInfo> {
		@Override
		public BatchTransactionInfo mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			final BatchTransactionInfo info = new BatchTransactionInfo();
			info.setId(rs.getString(ID.getDbColumnName()));
			info.setBatchInputFile(rs.getString(BATCH_INPUT_FILE.getDbColumnName()));
			info.setBatchOutputFile(rs.getString(BATCH_OUTPUT_FILE.getDbColumnName()));
			info.setTest(rs.getBoolean(IS_TEST.getDbColumnName()));
			info.setStatus(rs.getString(STATUS.getDbColumnName()));
			info.setTotalRecords(rs.getLong(TOTAL_RECORDS.getDbColumnName()));
			info.setCreatedDate(rs.getLong(CREATED_ON.getDbColumnName()));

            if (info.getCreatedDate() != null) {
                info.setCreatedDateTime(AdminUtil.getDateFormatMillisForEst(info.getCreatedDate(), null));
            }
						
			final Long startTime = rs.getLong(START_TIME.getDbColumnName());
			if (startTime != null) {
				info.setFromDate(AdminUtil.getDateFormatMillisForEst(startTime, null));
			}
			
			final Long endTime = rs.getLong(END_TIME.getDbColumnName());
			if (endTime != -1) {
				info.setToDate(AdminUtil.getDateFormatMillisForEst(endTime, null));
			} else {
				info.setToDate("");
			}
			
			long batchExecTime = rs.getLong(BATCH_EXEC_TIME.getDbColumnName());
			if (batchExecTime == -1) {
				info.setBatchExecTime("");
			} else {
				info.setBatchExecTime(batchExecTime + " ms");
			}
			
			info.setTransactionMode(rs.getString(TRANSACTION_MODE.getDbColumnName()));
			if (StringUtils.equalsIgnoreCase(info.getTransactionMode(),BusinessConstants.BATCH) || StringUtils.equalsIgnoreCase(info.getTransactionMode(),BusinessConstants.BULK)) {
			    info.setSuccessCount(rs.getLong(SUCCESS_COUNT.getDbColumnName()));
			    info.setFailCount(rs.getLong(FAILED_COUNT.getDbColumnName()));
			    info.setNotPickedCount(rs.getLong(NOT_PICKED_COUNT.getDbColumnName()));
			    info.setTxnInProgressCount(info.getTotalRecords()-(info.getSuccessCount()+info.getFailCount()+info.getNotPickedCount()));
			}
			info.setModellingEnv(rs.getString(MODELLING_ENVIRONMENT.getDbColumnName()));
			info.setExecEnv(rs.getString(EXECUTION_ENVIRONMENT.getDbColumnName()));
			return info;
		}
	}
	
    public Map<String, Long> getTxnIdsFromBatchId(final String batchIds, final String status) throws SystemException {
    	final Map<String, Long> map = new HashMap<String, Long>();
        try {
        	String sql = BatchDashboardQuery.getBatchTransCountQuery(batchIds, status);
        	LOGGER.info("Get batch status from mapping query :" + sql);
        	createBatchIdMapByStatusCount(jdbcTemplate.queryForRowSet(sql),  map);
        } catch (EmptyResultDataAccessException ex) {
            LOGGER.info("No Records found with the batch id :" + batchIds);
        } catch (DataAccessException ex) {
            SystemException.newSystemException(BSE000098, new Object[] { batchIds, ex.getMessage() });
        }
        
        return map;
    }
    
    private void createBatchIdMapByStatusCount(final SqlRowSet rowSet, final Map<String, Long> map) {
    	while(rowSet.next()) {
    		map.put(rowSet.getString("BATCH_ID"), rowSet.getLong("STATUS_COUNT"));
    	}
    }
}