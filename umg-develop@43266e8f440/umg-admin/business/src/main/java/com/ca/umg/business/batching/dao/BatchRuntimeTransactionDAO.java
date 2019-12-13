package com.ca.umg.business.batching.dao;
 
import java.util.Collections;
import java.util.List;
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
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;

/**
 * @author basanaga
 * 
 */
@Repository
public class BatchRuntimeTransactionDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchRuntimeTransactionDAO.class);

    private static final String RUNTIME_TXN_SELECT_QUERY = "SELECT TXN.ID from UMG_RUNTIME_TRANSACTION TXN "
    		+ "INNER JOIN BATCH_TXN_RUNTIME_TXN_MAPPING BAT_TXN_MAP ON BAT_TXN_MAP.TRANSACTION_ID=TXN.ID "
    		+ "WHERE BAT_TXN_MAP.BATCH_ID IN (:batchId)";
    
    private static final String OUTPUT_FILE_QRY = "select BATCH_OUTPUT_FILE from BATCH_TRANSACTION where ID in (select BATCH_ID from "
    		+ "BATCH_TXN_RUNTIME_TXN_MAPPING where TRANSACTION_ID IN ('TXN_ID'))";
    
    private static final String INPUT_FILE_QRY = "select BATCH_INPUT_FILE from BATCH_TRANSACTION where ID in (select BATCH_ID from "
    		+ "BATCH_TXN_RUNTIME_TXN_MAPPING where TRANSACTION_ID IN ('TXN_ID'))";
    
    private static final String RUNTIME_IN_PROG_TXN_SELECT_QRY = "SELECT TXN.ID from UMG_RUNTIME_TRANSACTION TXN "
    		+ "INNER JOIN BATCH_TXN_RUNTIME_TXN_MAPPING BAT_TXN_MAP ON BAT_TXN_MAP.TRANSACTION_ID=TXN.ID "
    		+ "WHERE BAT_TXN_MAP.BATCH_ID IN (:batchId) AND BAT_TXN_MAP.STATUS='IN-PROGRESS'";


    @Inject
    @Named(value = "dataSource")
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    /**
     * Initializes JDBC template with data source.
     */
    @PostConstruct
    public void initializeTemplate() {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }
    
    
    /**
     * This method used to get the Transactions for the particular batch id
     * 
     * @param searchCriteria
     * @param batchId
     * @return
     * @throws SystemException
     */
    public List<String> getTxnIdsFromBatchId(List<String> batchIdList) throws SystemException {
        List<String> transactionList = null;
        try {
        	Map<String, List<String>> param = Collections.singletonMap("batchId",batchIdList); 
        	NamedParameterJdbcTemplate  namedParameterJdbcTemplate = new  
        			NamedParameterJdbcTemplate(jdbcTemplate.getDataSource());
        	transactionList = namedParameterJdbcTemplate.queryForList(RUNTIME_TXN_SELECT_QUERY, param, String.class);
        	
            if (transactionList != null) {
                LOGGER.info("Retrieved  {} records", transactionList.size());
            }
        } catch (EmptyResultDataAccessException ex) {
            transactionList = Collections.emptyList();
            LOGGER.info("No Records found with the batch id :" + batchIdList);
        } catch (DataAccessException ex) {
            SystemException.newSystemException(BusinessExceptionCodes.BSE000098, new Object[] { batchIdList, ex.getMessage() });
        }

        return transactionList;

    }
    public List<String> getInProgressTxnIdsFromBatchId(List<String> batchIdList) throws SystemException {
        List<String> transactionList = null;
        try {
        	Map<String, List<String>> param = Collections.singletonMap("batchId",batchIdList);  
        	NamedParameterJdbcTemplate  namedParameterJdbcTemplate = new  
        			NamedParameterJdbcTemplate(jdbcTemplate.getDataSource());
        	transactionList = namedParameterJdbcTemplate.queryForList(RUNTIME_IN_PROG_TXN_SELECT_QRY, param, String.class);
        	
            if (transactionList != null) {
                LOGGER.info("Retrieved  {} records", transactionList.size());
            }
        } catch (EmptyResultDataAccessException ex) {
            transactionList = Collections.emptyList();
            LOGGER.info("No Records found with the batch id :" + batchIdList);
        } catch (DataAccessException ex) {
            SystemException.newSystemException(BusinessExceptionCodes.BSE000098, new Object[] { batchIdList, ex.getMessage() });
        }

        return transactionList;

    }
    public String getBatchOutputFileName(final String transactionId) throws SystemException {
    	String outputFile = null;
    	final String sql = OUTPUT_FILE_QRY.replace("TXN_ID", transactionId);
    	final Map<String, Object> result = jdbcTemplate.queryForMap(sql);
    	final Object value = result.get("BATCH_OUTPUT_FILE");
    	if (value == null) {
        	LOGGER.info("Output file is empty, check in database, transaction id is : " + transactionId);        		
            SystemException.newSystemException(BusinessExceptionCodes.BSE000098, new Object[] { transactionId, "No output file found"});
    	} else {
    		outputFile = result.get("BATCH_OUTPUT_FILE").toString();
        	LOGGER.info("Output file name is : " + outputFile);
    	}
        return outputFile;
    }
    
    public String getBulkInputFileName(final String transactionId) throws SystemException {
    	String outputFile = null;
    	final String sql = INPUT_FILE_QRY.replace("TXN_ID", transactionId);
    	final Map<String, Object> result = jdbcTemplate.queryForMap(sql);
    	final Object value = result.get("BATCH_INPUT_FILE");
    	if (value == null) {
        	LOGGER.info("Output file is empty, check in database, transaction id is : " + transactionId);        		
            SystemException.newSystemException(BusinessExceptionCodes.BSE000098, new Object[] { transactionId, "No output file found"});
    	} else {
    		outputFile = result.get("BATCH_INPUT_FILE").toString();
        	LOGGER.info("Output file name is : " + outputFile);
    	}
        return outputFile;
    }
}
