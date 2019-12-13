package com.ca.umg.business.transaction.dao;

import static com.ca.umg.business.exception.codes.BusinessExceptionCodes.BSE0000111;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.transaction.info.TransactionFilter;

@Repository
public class TransactionExcelReportDAO {

	private static final Logger LOGGER = LoggerFactory.getLogger(TransactionExcelReportDAO.class);
    
    @Inject
    private TransactionExcelReportQuery transactionExcelReportQuery;
    
    @Inject
    @Named(value = "dataSource")
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void initializeTemplate() {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }
    
    public Map<String, Object> getMinAndMaxRunAsOfDate(final TransactionFilter filter, final String tenantId) {
    	final String query = transactionExcelReportQuery.createGetMaxMinRunAsOfDateQuery(filter, tenantId);
    	LOGGER.info("Get Min and Max Run As Of Date Query:" + query);
    	return jdbcTemplate.queryForMap(query);
    }
    
    public SqlRowSet loadTransactionsRowSet(final TransactionFilter filter, final String tenantId) 
    		throws SystemException, BusinessException {
        try {
        	final String query = transactionExcelReportQuery.createLoadReportQuery(filter, tenantId);
            LOGGER.info("Transaction Excel Report Query:" + query);
            return jdbcTemplate.queryForRowSet(query);
        } catch (DataAccessException ex) {
            SystemException.newSystemException(BSE0000111, new Object[] {ex.getMessage() });
        }
        
        throw BusinessException.raiseBusinessException(BSE0000111, new Object[] { "Transactions are not loaded from database for excel report" });
    }
    
	public void setDataSource(final DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public void setTransactionExcelReportQuery(final TransactionExcelReportQuery transactionExcelReportQuery) {
		this.transactionExcelReportQuery = transactionExcelReportQuery;
	}
}