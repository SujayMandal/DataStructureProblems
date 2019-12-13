package com.ca.umg.business.batching.dao;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;


@SuppressWarnings("pmd")
@Repository
public class BatchRuntimeTransactionMappingDAO2 {
 
	private static final Logger LOGGER = LoggerFactory.getLogger(BatchRuntimeTransactionMappingDAO2.class);

    private static final String BATCH_TXN_STATUS_QUERY = "select BATCH_ID, count(STATUS) AS STATUS_COUNT "
    		+ "from BATCH_TXN_RUNTIME_TXN_MAPPING where BATCH_ID IN (BTCH_IDS) AND STATUS = 'STS' group by BATCH_ID";
    
    @Inject
    @Named(value = "dataSource")
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void initializeTemplate() {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }
    
    public Map<String, Long> getTxnIdsFromBatchId(final String batchIds, final String status) throws SystemException {
    	final Map<String, Long> map = new HashMap<String, Long>();
        try {
        	String sql = BATCH_TXN_STATUS_QUERY.replace("BTCH_IDS", batchIds);
        	sql = sql.replace("STS", status);
        	createBatchIdMapByStatusCount(jdbcTemplate.queryForRowSet(sql),  map);
        } catch (EmptyResultDataAccessException ex) {
            LOGGER.info("No Records found with the batch id :" + batchIds);
        } catch (DataAccessException ex) {
            SystemException.newSystemException(BusinessExceptionCodes.BSE000098, new Object[] { batchIds, ex.getMessage() });
        }
        
        return map;
    }
    
    private void createBatchIdMapByStatusCount(final SqlRowSet rowSet, final Map<String, Long> map) {
    	while(rowSet.next()) {
    		map.put(rowSet.getString("BATCH_ID"), rowSet.getLong("STATUS_COUNT"));
    	}
    }
}