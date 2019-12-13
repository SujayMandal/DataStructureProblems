package com.ca.umg.business.transaction.migrate.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.util.ConversionUtil;
import com.ca.umg.business.transaction.mongo.entity.TransactionDocument;

@Named
public class MigrateTransactionDAOImpl implements MigrateTransactionDAO {
	
	private static final String FIND_TRANSACTIONS = "SELECT "
			+ "rt.ID as ID, rt.MODEL_INPUT as MODEL_INPUT, rt.MODEL_OUTPUT as MODEL_OUTPUT, "
			+ "rt.TENANT_INPUT as TENANT_INPUT, rt.TENANT_OUTPUT as TENANT_OUTPUT, "
			+ "rt.CLIENT_TRANSACTION_ID as CLIENT_TRANSACTION_ID, rt.LIBRARY_NAME as LIBRARY_NAME, "
			+ "rt.VERSION_NAME as VERSION_NAME, rt.MAJOR_VERSION as MAJOR_VERSION, "
			+ "rt.MINOR_VERSION as MINOR_VERSION, rt.STATUS as STATUS, "
			+ "rt.RUN_AS_OF_DATE as RUN_AS_OF_DATE, rt.IS_TEST as IS_TEST, "
			+ "rt.CREATED_BY as CREATED_BY, rt.CREATED_ON as CREATED_ON, "
			+ "rt.ERROR_CODE as ERROR_CODE, rt.ERROR_DESCRIPTION as ERROR_DESCRIPTION, "
			+ "rt.RUNTIME_CALL_START as RUNTIME_CALL_START, rt.RUNTIME_CALL_END as RUNTIME_CALL_END, "
			+ "rt.MODEL_CALL_START as MODEL_CALL_START, rt.MODEL_CALL_END as MODEL_CALL_END, "
			+ "rt.MODEL_EXECUTION_TIME as MODEL_EXECUTION_TIME, rt.MODELET_EXECUTION_TIME as MODELET_EXECUTION_TIME, "
			+ "rt.ME2_WAITING_TIME as ME2_WAITING_TIME "
			+ "FROM UMG_RUNTIME_TRANSACTION rt "
			+ "where rt.ID = :TransactionId";
	
	private static final String FIND_ALL_IDS = "SELECT rt.ID FROM UMG_RUNTIME_TRANSACTION rt "
			+ "where rt.TENANT_INPUT <> '' AND rt.TENANT_OUTPUT <> '' AND rt.TENANT_ID = :TENANTCODE";
	
	private static final String UPDATE_BLOB_TO_NULL = "UPDATE UMG_RUNTIME_TRANSACTION "
			+ "SET TENANT_INPUT = NULL, TENANT_OUTPUT = NULL, MODEL_OUTPUT = NULL, MODEL_INPUT = NULL "
			+ "WHERE ID=:TranId";
	
	private static final RuntimeTransactionMapper RTM_TRAN_MAPPER = new RuntimeTransactionMapper();

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
	public TransactionDocument getBlobsOfRuntmTransaction(String tranId) {
		Map<String,String> params = new HashMap<>();
		params.put("TransactionId", tranId);
		return namedJdbcTemplate.queryForObject(FIND_TRANSACTIONS, params,RTM_TRAN_MAPPER);
		
	}
		
	@Override
	public List<String> getAllRtmTranIds () {
		List<String> listRntmTranIds = null;
		Map<String,String> paramMap = new HashMap<String,String>();
		paramMap.put("TENANTCODE", RequestContext.getRequestContext().getTenantCode());
		listRntmTranIds = namedJdbcTemplate.queryForList(FIND_ALL_IDS, paramMap, String.class);
		return listRntmTranIds;
	}
	
	@Override
	public Boolean updateBlobAsNull (String tranId) {
		Integer rowsAffected = null;
		Boolean status = Boolean.FALSE;
		Map<String,String> paramMap = new HashMap<>();
		paramMap.put("TranId", tranId);
		rowsAffected = namedJdbcTemplate.update(UPDATE_BLOB_TO_NULL, paramMap);
		if (rowsAffected != null) {
			status = Boolean.TRUE;
		} 
		return status;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}

class RuntimeTransactionMapper implements RowMapper<TransactionDocument> {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(RuntimeTransactionMapper.class);
	@SuppressWarnings("unchecked")
	public TransactionDocument mapRow(ResultSet rs, int i) throws SQLException {
    	TransactionDocument rtmtranBlbInfo = new TransactionDocument();
		rtmtranBlbInfo.setTransactionId(rs.getString("ID"));
		rtmtranBlbInfo.setClientTransactionID(rs.getString("CLIENT_TRANSACTION_ID"));
		rtmtranBlbInfo.setLibraryName(rs.getString("LIBRARY_NAME"));
		rtmtranBlbInfo.setVersionName(rs.getString("VERSION_NAME"));
		rtmtranBlbInfo.setMajorVersion(rs.getInt("MAJOR_VERSION"));
		rtmtranBlbInfo.setMinorVersion(rs.getInt("MINOR_VERSION"));
		rtmtranBlbInfo.setStatus(rs.getString("STATUS"));
		rtmtranBlbInfo.setRunAsOfDate(rs.getLong("RUN_AS_OF_DATE"));
		rtmtranBlbInfo.setTest(rs.getBoolean("IS_TEST"));
		rtmtranBlbInfo.setCreatedBy(rs.getString("CREATED_BY"));
		rtmtranBlbInfo.setCreatedDate(rs.getLong("CREATED_ON"));
		rtmtranBlbInfo.setRuntimeCallStart(rs.getLong("RUNTIME_CALL_START"));
		rtmtranBlbInfo.setRuntimeCallEnd(rs.getLong("RUNTIME_CALL_END"));
		rtmtranBlbInfo.setModelCallStart(rs.getLong("MODEL_CALL_START"));
		rtmtranBlbInfo.setModelCallEnd(rs.getLong("MODEL_CALL_END"));
		rtmtranBlbInfo.setModelExecutionTime(rs.getLong("MODEL_EXECUTION_TIME"));
		rtmtranBlbInfo.setModeletExecutionTime(rs.getLong("MODELET_EXECUTION_TIME"));
		rtmtranBlbInfo.setMe2WaitingTime(rs.getLong("ME2_WAITING_TIME"));
		
		if (rs.getString("ERROR_CODE") != null) {
			rtmtranBlbInfo.setErrorCode(rs.getString("ERROR_CODE"));
		} 
		
		if (rs.getBytes("ERROR_DESCRIPTION") != null) {
			rtmtranBlbInfo.setErrorDescription(new String(rs.getBytes("ERROR_DESCRIPTION")));
		} 
		
		try {
			if (rs.getBytes("MODEL_INPUT") != null) {
				rtmtranBlbInfo.setModelInput(ConversionUtil.convertJson(rs.getBytes("MODEL_INPUT"), Map.class));
			} 
			
			if (rs.getBytes("MODEL_OUTPUT") != null) {
				rtmtranBlbInfo.setModelOutput(ConversionUtil.convertJson(rs.getBytes("MODEL_OUTPUT"), Map.class));
			} 
			
			if (rs.getBytes("TENANT_INPUT") != null) {
					rtmtranBlbInfo.setTenantInput(ConversionUtil.convertJson(rs.getBytes("TENANT_INPUT"), Map.class));
			}
			
			if (rs.getBytes("TENANT_OUTPUT") != null) {
				rtmtranBlbInfo.setTenantOutput(ConversionUtil.convertJson(rs.getBytes("TENANT_OUTPUT"), Map.class));
			} 
		} catch (SystemException e) {
			LOGGER.error("Error during the mappping of bytes to json object"+e);
			//SystemException.newSystemException(BusinessExceptionCodes.BSE000049, new Object[] {}, e);
		}
		
        return rtmtranBlbInfo;
    }
}
