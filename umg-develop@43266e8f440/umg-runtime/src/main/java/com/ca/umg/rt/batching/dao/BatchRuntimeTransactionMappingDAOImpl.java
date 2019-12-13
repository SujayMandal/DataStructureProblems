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

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.ca.umg.rt.batching.entity.BatchRuntimeTransactionMapping;
import com.ca.umg.rt.core.deployment.constants.RuntimeConstants;

@Named
public class BatchRuntimeTransactionMappingDAOImpl implements BatchRuntimeTransactionMappingDAO {

    private NamedParameterJdbcTemplate jdbcTemplate;

    @Inject
    @Named(value = "dataSource")
    private DataSource dataSource;
    private static final String SQL = "INSERT INTO BATCH_TXN_RUNTIME_TXN_MAPPING (ID,BATCH_ID,TENANT_ID,TRANSACTION_ID,STATUS,ERROR,"
            + "CREATED_BY,CREATED_ON,LAST_UPDATED_BY,LAST_UPDATED_ON) values (:ID,:BATCH_ID,:TENANT_ID,:TRANSACTION_ID,"
            + ":STATUS,:ERROR,:CREATED_BY,:CREATED_ON,:LAST_UPDATED_BY,:LAST_UPDATED_ON)";
    
    private static final String UPDATE_SQL = "UPDATE BATCH_TXN_RUNTIME_TXN_MAPPING SET STATUS=:STATUS , ERROR=:ERROR , LAST_UPDATED_ON=:LAST_UPDATED_ON "
    		+ "WHERE BATCH_ID=:BATCH_ID AND TENANT_ID=:TENANT_ID AND TRANSACTION_ID=:TRANSACTION_ID";

    private static final String FIND_ONE_BATCH_TXN_MAPPING = "SELECT * FROM BATCH_TXN_RUNTIME_TXN_MAPPING WHERE BATCH_ID=:BATCH_ID";

    /**
     * Initialize {@link JdbcTemplate} with {@link DataSource} instance.
     **/
    @PostConstruct
    public void initializeTemplate() {
        setJdbcTemplate(new NamedParameterJdbcTemplate(dataSource));
    }

    public void setJdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(BatchRuntimeTransactionMapping trnsMapping) {
        MapSqlParameterSource valueMap = new MapSqlParameterSource();
        valueMap.addValue("ID", trnsMapping.getId());
        valueMap.addValue("BATCH_ID", trnsMapping.getBatchId());
        valueMap.addValue("TENANT_ID", trnsMapping.getTenantId());
        valueMap.addValue("TRANSACTION_ID", trnsMapping.getTransactionId());
        valueMap.addValue("CREATED_BY", trnsMapping.getCreatedBy());
        valueMap.addValue("CREATED_ON", trnsMapping.getCreatedDate());
        valueMap.addValue("LAST_UPDATED_BY", trnsMapping.getCreatedBy());
        valueMap.addValue("LAST_UPDATED_ON", trnsMapping.getCreatedDate());
        valueMap.addValue("STATUS", trnsMapping.getStatus());
        valueMap.addValue("ERROR", trnsMapping.getError());
        jdbcTemplate.update(SQL, valueMap);
    }
    
    @Override
    public void update(BatchRuntimeTransactionMapping trnsMapping) {
        MapSqlParameterSource valueMap = new MapSqlParameterSource();
        valueMap.addValue("BATCH_ID", trnsMapping.getBatchId());
        valueMap.addValue("TENANT_ID", trnsMapping.getTenantId());
        valueMap.addValue("TRANSACTION_ID", trnsMapping.getTransactionId());
        valueMap.addValue("LAST_UPDATED_ON", trnsMapping.getLastModifiedDate());
        valueMap.addValue("STATUS", trnsMapping.getStatus());
        valueMap.addValue("ERROR", trnsMapping.getError());
        jdbcTemplate.update(UPDATE_SQL, valueMap);
    }

    @Override
    public List<BatchRuntimeTransactionMapping> findAllByBatchId(String batchId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(RuntimeConstants.BATCH_ID, batchId);
        return jdbcTemplate.query(FIND_ONE_BATCH_TXN_MAPPING, params, new BatchTransactionMappingMapper());
    }
}

class BatchTransactionMappingMapper implements RowMapper<BatchRuntimeTransactionMapping> {
    public BatchRuntimeTransactionMapping mapRow(ResultSet rs, int i) throws SQLException {
        BatchRuntimeTransactionMapping btm = new BatchRuntimeTransactionMapping();
        btm.setId(rs.getString(RuntimeConstants.ID));
        btm.setStatus(rs.getString("STATUS"));
        btm.setCreatedBy(rs.getString("CREATED_BY"));
        btm.setCreatedDate(rs.getLong("CREATED_ON"));
        btm.setLastModifiedBy(rs.getString("LAST_UPDATED_BY"));
        btm.setLastModifiedDate(rs.getLong("LAST_UPDATED_ON"));
        btm.setTransactionId(rs.getString("TRANSACTION_ID"));
        return btm;
    }
}