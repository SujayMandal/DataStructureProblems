package com.ca.umg.rt.batching.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.ca.framework.core.batch.TransactionStatus;
import com.ca.umg.rt.batching.entity.BatchTransaction;
import com.ca.umg.rt.core.deployment.constants.RuntimeConstants;

@Named
public class BatchTransactionDAOImpl implements BatchTransactionDAO {
	 private static final Logger LOGGER = LoggerFactory.getLogger(BatchTransactionDAOImpl.class);
    private static final String FIND_ONE_BATCH_TRANSACTION = "SELECT * FROM BATCH_TRANSACTION WHERE ID=:BATCH_ID";
    private static final String GET_ALL_BATCH_TRANSACTIONS = "SELECT * FROM BATCH_TRANSACTION";
    private static final String FIND_BY_BATCH_FILE = "SELECT * FROM BATCH_TRANSACTION WHERE BATCH_INPUT_FILE=:BATCH_INPUT_FILE";
    private static final String UPDATE_BATCH_COUNT = "UPDATE BATCH_TRANSACTION SET TOTAL_RECORDS=:TOTAL_RECORDS WHERE ID=:BATCH_ID";
    private static final String UPDATE_NOT_PICKED_COUNT = "UPDATE BATCH_TRANSACTION SET NOT_PICKED_COUNT=:NOT_PICKED_COUNT WHERE ID=:BATCH_ID";
    private static final String UPDATE_BATCH_OUTPUT_FILE = "UPDATE BATCH_TRANSACTION SET BATCH_OUTPUT_FILE=:BATCH_OUTPUT_FILE WHERE ID=:BATCH_ID";
    private static final String UPDATE_BATCH_STATUS = "UPDATE BATCH_TRANSACTION SET TOTAL_RECORDS=:TOTAL_RECORDS, STATUS=:STATUS, "
            + "END_TIME=:END_TIME, SUCCESS_COUNT=:SUCCESS_COUNT, FAIL_COUNT=:FAIL_COUNT WHERE ID=:BATCH_ID";
    private static final String UPDATE_BATCH_STATUS_ONLY = "UPDATE BATCH_TRANSACTION SET STATUS=:STATUS WHERE ID=:BATCH_ID";
    private static final String UPDATE_SUCCESS_FAIL_COUNT = "UPDATE BATCH_TRANSACTION SET SUCCESS_COUNT=:SUCCESS_COUNT, FAIL_COUNT=:FAIL_COUNT WHERE ID=:BATCH_ID";
    private static final String SAVE_BATCH_TRANSACTION = "INSERT INTO BATCH_TRANSACTION(ID,TENANT_ID,BATCH_INPUT_FILE,STATUS,"
            + "START_TIME,END_TIME,TOTAL_RECORDS,SUCCESS_COUNT,FAIL_COUNT,BATCH_OUTPUT_FILE,IS_TEST,CREATED_BY,CREATED_ON,LAST_UPDATED_BY,LAST_UPDATED_ON, TRANSACTION_MODE, USER, MODEL_NAME, MODEL_VERSION,EXECUTION_ENVIRONMENT, MODELLING_ENVIRONMENT,STORE_RLOGS) "
            + "values (:ID,:TENANT_ID,:BATCH_INPUT_FILE,:STATUS,:START_TIME,:END_TIME,:TOTAL_RECORDS,"
            + ":SUCCESS_COUNT,:FAIL_COUNT,:BATCH_OUTPUT_FILE,:IS_TEST,:CREATED_BY,:CREATED_ON,:LAST_UPDATED_BY,:LAST_UPDATED_ON,:TRANSACTION_MODE,:USER,:MODEL_NAME,:MODEL_VERSION,:EXECUTION_ENVIRONMENT,:MODELLING_ENVIRONMENT,:STORE_RLOGS)";
    private static final String FIND_BY_FILE_AND_STARTTIME = "SELECT * FROM BATCH_TRANSACTION WHERE BATCH_INPUT_FILE like :BATCH_INPUT_FILE AND START_TIME>=:START_TIME";
    
    private static final String UPDATE_BATCH_TRANSACTION = "UPDATE BATCH_TRANSACTION SET STATUS=:STATUS, END_TIME=:END_TIME, TOTAL_RECORDS=:TOTAL_RECORDS, SUCCESS_COUNT=:SUCCESS_COUNT, FAIL_COUNT=:FAIL_COUNT, BATCH_OUTPUT_FILE=:BATCH_OUTPUT_FILE"
    		+ ", LAST_UPDATED_BY=:LAST_UPDATED_BY, LAST_UPDATED_ON=:LAST_UPDATED_ON, USER=:USER, MODEL_NAME=:MODEL_NAME, MODEL_VERSION=:MODEL_VERSION, EXECUTION_ENVIRONMENT=:EXECUTION_ENVIRONMENT, MODELLING_ENVIRONMENT=:MODELLING_ENVIRONMENT, STORE_RLOGS=:STORE_RLOGS"
            + " WHERE ID=:ID";
    
    private static final String FIND_BY_INPUT_FILE_FOR_BULK = "SELECT * FROM BATCH_TRANSACTION WHERE BATCH_INPUT_FILE=:BATCH_INPUT_FILE AND STATUS=:STATUS AND TRANSACTION_MODE=:TRANSACTION_MODE ORDER BY CREATED_ON DESC";
    
    private static final String BATCH_INPUT_FILE = "BATCH_INPUT_FILE";
    private static final String STATUS = "STATUS";
    
    private static final String UPDATE_BULK_ENV=" UPDATE BATCH_TRANSACTION SET EXECUTION_ENVIRONMENT=:EXECUTION_ENVIRONMENT, MODELLING_ENVIRONMENT=:MODELLING_ENVIRONMENT WHERE ID=:BATCH_ID";
    private static final BatchTransactionMapper BTM = new BatchTransactionMapper();

    private NamedParameterJdbcTemplate namedJdbcTemplate;

    @Inject
    @Named(value = "dataSource")
    private DataSource dataSource;

    /**
     * Initialize {@link JdbcTemplate} with {@link DataSource} instance.
     **/
    @PostConstruct
    public void initializeTemplate() {
        setJdbcTemplate(new NamedParameterJdbcTemplate(dataSource));
    }

    public void setJdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
        this.namedJdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<BatchTransaction> findByBatchFileName(String batchFileName) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(BATCH_INPUT_FILE, batchFileName);
        return namedJdbcTemplate.query(FIND_BY_BATCH_FILE, params, BTM);
    }
    
    @Override
    public BatchTransaction findByFileNameForBulk(String batchFileName) {
        BatchTransaction batchTransaction = null;
        Map<String, String> params = new HashMap<String, String>();
        params.put(BATCH_INPUT_FILE, batchFileName);
        params.put(STATUS, TransactionStatus.QUEUED.getStatus());
        params.put("TRANSACTION_MODE", "Bulk");
        List<BatchTransaction> transactions = namedJdbcTemplate.query(FIND_BY_INPUT_FILE_FOR_BULK, params, BTM);
        if (!transactions.isEmpty()) {
            batchTransaction = transactions.get(0);
        }
        return batchTransaction;
        //return namedJdbcTemplate.queryForObject(FIND_BY_INPUT_FILE_FOR_BULK, params, BTM);
    }

    @Override
    public BatchTransaction save(BatchTransaction batchTransaction, final int test, final boolean newEntry) {
    	Double modelVersion = null;
    	if(!StringUtils.isBlank(batchTransaction.getModelVersion())){
    		modelVersion = Double.parseDouble(batchTransaction.getModelVersion());
    	}
        MapSqlParameterSource valueMap = new MapSqlParameterSource();
        valueMap.addValue(RuntimeConstants.ID, batchTransaction.getId());
        valueMap.addValue(RuntimeConstants.TENANT_ID, batchTransaction.getTenantId());
        valueMap.addValue(BATCH_INPUT_FILE, batchTransaction.getBatchInputFileName());
        valueMap.addValue(STATUS, batchTransaction.getStatus());
        valueMap.addValue("START_TIME", batchTransaction.getStartTime());
        valueMap.addValue("END_TIME", batchTransaction.getEndTime());
        valueMap.addValue(RuntimeConstants.TOTAL_RECORDS, batchTransaction.getTotalRecords());
        valueMap.addValue(RuntimeConstants.SUCCESS_COUNT, batchTransaction.getSuccessCount());
        valueMap.addValue(RuntimeConstants.FAIL_COUNT, batchTransaction.getFailCount());
        valueMap.addValue("CREATED_BY", batchTransaction.getCreatedBy());
        valueMap.addValue("CREATED_ON", batchTransaction.getCreatedDate());
        valueMap.addValue("LAST_UPDATED_BY", batchTransaction.getLastModifiedBy());
        valueMap.addValue("LAST_UPDATED_ON", batchTransaction.getLastModifiedDate());
        valueMap.addValue("BATCH_OUTPUT_FILE", batchTransaction.getBatchOutputFileName());
        valueMap.addValue("IS_TEST", test); // RuntimeConstants.INT_ZERO);// Runtime Executions are never test.
        valueMap.addValue("TRANSACTION_MODE", batchTransaction.getTransactionMode());
        valueMap.addValue("USER", batchTransaction.getUser());
        valueMap.addValue("MODEL_NAME", batchTransaction.getModelName());
        valueMap.addValue("MODEL_VERSION", modelVersion);
        valueMap.addValue("EXECUTION_ENVIRONMENT", batchTransaction.getExecEnv());
        valueMap.addValue("MODELLING_ENVIRONMENT", batchTransaction.getModellingEnv());
        valueMap.addValue("STORE_RLOGS", batchTransaction.getStoreRlogs());
        if(newEntry){
        	LOGGER.error("Creating batch entry saveRLog value is  " + batchTransaction.getStoreRlogs());
        	namedJdbcTemplate.update(SAVE_BATCH_TRANSACTION, valueMap);
        } else {
        	LOGGER.error("Updating batch entry saveRLog value is  " + batchTransaction.getStoreRlogs());
        	namedJdbcTemplate.update(UPDATE_BATCH_TRANSACTION, valueMap);
        }
        return findOne(batchTransaction.getId());
    }

    @Override
    public BatchTransaction updateStatus(String batchId, int batchCount, int successCount, int failureCount, String status) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(RuntimeConstants.BATCH_ID, batchId);
        params.put(RuntimeConstants.TOTAL_RECORDS, batchCount);
        params.put(STATUS, status);
        params.put(RuntimeConstants.SUCCESS_COUNT, successCount);
        params.put(RuntimeConstants.FAIL_COUNT, failureCount);
        params.put("END_TIME", System.currentTimeMillis());
        namedJdbcTemplate.update(UPDATE_BATCH_STATUS, params);
        return findOne(batchId);

    }
    
    @Override
    public BatchTransaction updateStatusOnly(String batchId, String status) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(RuntimeConstants.BATCH_ID, batchId);
        params.put(STATUS, status);
        namedJdbcTemplate.update(UPDATE_BATCH_STATUS_ONLY, params);
        return findOne(batchId);

    }
    
    @Override
    public BatchTransaction updateNotPickedCount(String batchId, int notPickedCount) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(RuntimeConstants.BATCH_ID, batchId);
        params.put("NOT_PICKED_COUNT", notPickedCount);
        namedJdbcTemplate.update(UPDATE_NOT_PICKED_COUNT, params);
        return findOne(batchId);

    }
    
    @Override
    public BatchTransaction updateSuccessFailCount(String batchId, int successCount, int failCount) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(RuntimeConstants.BATCH_ID, batchId);
        params.put(RuntimeConstants.SUCCESS_COUNT, successCount);
        params.put(RuntimeConstants.FAIL_COUNT, failCount);
        namedJdbcTemplate.update(UPDATE_SUCCESS_FAIL_COUNT, params);
        return findOne(batchId);

    }

    @Override
    public List<BatchTransaction> findAll() {
        return namedJdbcTemplate.query(GET_ALL_BATCH_TRANSACTIONS, BTM);
    }

    @Override
    public BatchTransaction findOne(String batchId) {
        Map<String, String> params = new HashMap<String, String>();
        BatchTransaction batchTransaction = null;
        params.put(RuntimeConstants.BATCH_ID, batchId);
        List<BatchTransaction> batchTransactions =  namedJdbcTemplate.query(FIND_ONE_BATCH_TRANSACTION, params, BTM);
        if (CollectionUtils.isNotEmpty(batchTransactions)) {
            batchTransaction = batchTransactions.get(0);
        }
        return batchTransaction;	
    }

    @Override
    public void updateBatch(String batchId, int totalCount) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(RuntimeConstants.BATCH_ID, batchId);
        params.put(RuntimeConstants.TOTAL_RECORDS, totalCount);
        namedJdbcTemplate.update(UPDATE_BATCH_COUNT, params);
    }

    @Override
    public void updateBatchOutputFile(String batchId, String outputFileName) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(RuntimeConstants.BATCH_ID, batchId);
        params.put("BATCH_OUTPUT_FILE", outputFileName);
        namedJdbcTemplate.update(UPDATE_BATCH_OUTPUT_FILE, params);
    }

    @Override
    public List<BatchTransaction> findByBatchFileNameAndStartTime(String batchFileName, long startTime) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(BATCH_INPUT_FILE, batchFileName);
        params.put("START_TIME", startTime);
        return namedJdbcTemplate.query(FIND_BY_FILE_AND_STARTTIME, params, BTM);
    }

	@Override
	public void updateEnvAndModelEnvs(String batchId, String execEnv, String modellingEnv) {	 
	        Map<String, Object> params = new HashMap<String, Object>();
	        params.put(RuntimeConstants.BATCH_ID, batchId);	        
	        params.put(RuntimeConstants.EXECUTION_ENVIRONMENT, execEnv);
	        params.put(RuntimeConstants.MODELLING_ENVIRONMENT, modellingEnv);
	        namedJdbcTemplate.update(UPDATE_BULK_ENV, params); 
	}
	
}


class BatchTransactionMapper implements RowMapper<BatchTransaction> {
    public BatchTransaction mapRow(ResultSet rs, int i) throws SQLException {
        BatchTransaction bt = new BatchTransaction();
        bt.setId(rs.getString(RuntimeConstants.ID));
        bt.setTenantId(rs.getString(RuntimeConstants.TENANT_ID));
        bt.setBatchInputFileName(rs.getString("BATCH_INPUT_FILE"));
        bt.setBatchOutputFileName(rs.getString("BATCH_OUTPUT_FILE"));
        bt.setStatus(rs.getString("STATUS"));
        bt.setStartTime(rs.getLong("START_TIME"));
        bt.setEndTime(rs.getLong("END_TIME"));
        bt.setTotalRecords(rs.getLong(RuntimeConstants.TOTAL_RECORDS));
        bt.setSuccessCount(rs.getLong(RuntimeConstants.SUCCESS_COUNT));
        bt.setFailCount(rs.getLong(RuntimeConstants.FAIL_COUNT));
        bt.setNotPickedCount(rs.getLong("NOT_PICKED_COUNT"));
        bt.setCreatedBy(rs.getString("CREATED_BY"));
        bt.setCreatedDate(rs.getLong("CREATED_ON"));
        bt.setLastModifiedBy(rs.getString("LAST_UPDATED_BY"));
        bt.setLastModifiedDate(rs.getLong("LAST_UPDATED_ON"));
        bt.setTransactionMode(rs.getString("TRANSACTION_MODE"));
        bt.setStoreRlogs(rs.getString("STORE_RLOGS"));
        return bt;
    }
    

}
