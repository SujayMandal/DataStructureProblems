package com.ca.umg.me2.dao;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.pool.TransactionMode;
import com.ca.pool.model.RequestMode;

/**
 * @author basanaga
 * 
 *         This Class used to updated the Transaction or batch status from
 *         Queued to in Execution
 *
 */
@Named
public class TransactionLogDAO {
	@Inject
	@Named(value = "dataSource")
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;

	@Inject
	private CacheRegistry cacheRegistry;

	private static final String SQL = "UPDATE UMG_RUNTIME_TRANSACTION SET STATUS=? WHERE ID=?";
	private static final String BATCH_SQL = "UPDATE BATCH_TRANSACTION SET STATUS=? WHERE ID=?";
	private static final String BATCH_STATUS_UPDTAE_MAP = "BATCH_STATUS_UPDTAE_MAP";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TransactionLogDAO.class);

	/**
	 * This method used to update the transaction status to In Execution
	 * 
	 * @param status
	 * @param txnId
	 * @return
	 */
	public int updateTransactionStatus(String status, String txnId) {
		return jdbcTemplate.update(SQL, new Object[] { status, txnId });
	}

	/**
	 * This method used to update the batch/bulk status to In Execution
	 * 
	 * @param status
	 * @param batchId
	 * @param txnReqType
	 */
	public void updateBatchStatus(String status, String batchId, String txnReqType) {
		if (cacheRegistry.getMap(BATCH_STATUS_UPDTAE_MAP).get(batchId) == null
				&& StringUtils.equalsIgnoreCase(txnReqType, RequestMode.BATCH.getMode())) {
			cacheRegistry.getMap(BATCH_STATUS_UPDTAE_MAP).put(batchId, Boolean.TRUE);
			jdbcTemplate.update(BATCH_SQL, new Object[] { status, batchId });
		} else if(StringUtils.equalsIgnoreCase(txnReqType, TransactionMode.BULK.getMode())){
			LOGGER.error("Updating status of bulk");
			jdbcTemplate.update(BATCH_SQL, new Object[] { status, batchId });			
		}
	}

	/**
	 * Initilize {@link JdbcTemplate} with {@link DataSource} instance.
	 **/
	@PostConstruct
	public void initializeTemplate() {
		setJdbcTemplate(new JdbcTemplate(dataSource));
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

}
