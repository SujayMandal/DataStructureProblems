package com.ca.umg.rt.core.flow.dao;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.ca.framework.core.batch.TransactionStatus;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.rt.core.deployment.constants.RuntimeConstants;
import com.ca.umg.rt.core.flow.entity.TransactionLog;

@Named
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class TransactionLogDAOImpl implements TransactionLogDAO {

    private static final String SQL =  "INSERT INTO UMG_RUNTIME_TRANSACTION(ID,TENANT_ID,CLIENT_TRANSACTION_ID,LIBRARY_NAME,VERSION_NAME,MAJOR_VERSION,MINOR_VERSION,STATUS,"+
            "TENANT_INPUT,TENANT_OUTPUT,MODEL_INPUT,MODEL_OUTPUT,RUN_AS_OF_DATE,RUNTIME_CALL_START,RUNTIME_CALL_END,IS_TEST,ERROR_CODE,ERROR_DESCRIPTION,CREATED_BY,CREATED_ON,LAST_UPDATED_BY,LAST_UPDATED_ON, TRANSACTION_MODE )"+
            "VALUES(:ID,:TENANT_ID,:CLIENT_TRANSACTION_ID,:LIBRARY_NAME,:VERSION_NAME,:MAJOR_VERSION,:MINOR_VERSION,:STATUS,"+
            ":TENANT_INPUT,:TENANT_OUTPUT,:MODEL_INPUT,:MODEL_OUTPUT,:RUN_AS_OF_DATE,:RUNTIME_CALL_START,:RUNTIME_CALL_END," +
            ":IS_TEST,:ERROR_CODE,:ERROR_DESCRIPTION,:CREATED_BY,:CREATED_ON,:LAST_UPDATED_BY,:LAST_UPDATED_ON,:TRANSACTION_MODE)";
    
    private static final String INSERT_TXN_FLOW_SQL = "INSERT INTO UMG_RUNTIME_TRANSACTION(ID,TENANT_ID,CLIENT_TRANSACTION_ID,LIBRARY_NAME,VERSION_NAME,MAJOR_VERSION,MINOR_VERSION,STATUS, "
    		+ "TENANT_INPUT,TENANT_OUTPUT,MODEL_INPUT,MODEL_OUTPUT,RUN_AS_OF_DATE,RUNTIME_CALL_START,RUNTIME_CALL_END, "
    		+ "MODEL_CALL_START,MODEL_CALL_END,IS_TEST,CREATED_BY,CREATED_ON,LAST_UPDATED_BY,LAST_UPDATED_ON,ERROR_CODE,ERROR_DESCRIPTION, "
    		+ "MODEL_EXECUTION_TIME, MODELET_EXECUTION_TIME, ME2_WAITING_TIME,OP_VALIDATION,ACCEPTABLEVALUES_VALIDATION) "
    		+ "VALUES(:ID,:TENANT_ID,:CLIENT_TRANSACTION_ID,:LIBRARY_NAME,:VERSION_NAME,:MAJOR_VERSION,:MINOR_VERSION,:STATUS, "
    		+ ":TENANT_INPUT,:TENANT_OUTPUT,:MODEL_INPUT,:MODEL_OUTPUT,:RUN_AS_OF_DATE,:RUNTIME_CALL_START,:RUNTIME_CALL_END, "
    		+ ":MODEL_CALL_START,:MODEL_CALL_END,:IS_TEST,:CREATED_BY,:CREATED_ON,:LAST_UPDATED_BY,:LAST_UPDATED_ON,:ERROR_CODE,:ERROR_DESCRIPTION, "
    		+ ":MODEL_EXECUTION_TIME, :MODELET_EXECUTION_TIME, :ME2_WAITING_TIME, :OP_VALIDATION,:ACCEPTABLEVALUES_VALIDATION)";

    private static final String INSERT_REQUEST = "INSERT INTO UMG_RUNTIME_TRANSACTION(ID,TENANT_ID,CLIENT_TRANSACTION_ID,LIBRARY_NAME,VERSION_NAME,MAJOR_VERSION,MINOR_VERSION,STATUS, "
            + "TENANT_INPUT,MODEL_INPUT,TENANT_OUTPUT,MODEL_OUTPUT,RUN_AS_OF_DATE,RUNTIME_CALL_START,"
            + "IS_TEST,CREATED_BY,CREATED_ON,LAST_UPDATED_BY,LAST_UPDATED_ON,TRANSACTION_MODE,FREE_MEMORY,CPU_USAGE,FREE_MEMORY_AT_START,CPU_USAGE_AT_START,IP_AND_PORT,POOL_NAME,OP_VALIDATION,ACCEPTABLEVALUES_VALIDATION,MODEL_EXEC_ENV_NAME) "
            + "VALUES(:ID,:TENANT_ID,:CLIENT_TRANSACTION_ID,:LIBRARY_NAME,:VERSION_NAME,:MAJOR_VERSION,:MINOR_VERSION,:STATUS, "
            + ":TENANT_INPUT,:MODEL_INPUT,:TENANT_OUTPUT,:MODEL_OUTPUT,:RUN_AS_OF_DATE,:RUNTIME_CALL_START,"
            + ":IS_TEST,:CREATED_BY,:CREATED_ON,:LAST_UPDATED_BY,:LAST_UPDATED_ON,:TRANSACTION_MODE,:FREE_MEMORY,:CPU_USAGE,:FREE_MEMORY_AT_START,:CPU_USAGE_AT_START,:IP_AND_PORT,:POOL_NAME,:OP_VALIDATION,:ACCEPTABLEVALUES_VALIDATION,:MODEL_EXEC_ENV_NAME )";

    private static final String INSERT_REQUEST_ERROR = "INSERT INTO UMG_RUNTIME_TRANSACTION(ID,TENANT_ID,CLIENT_TRANSACTION_ID,LIBRARY_NAME,VERSION_NAME,MAJOR_VERSION,MINOR_VERSION,STATUS, "
            + "TENANT_INPUT,MODEL_INPUT,TENANT_OUTPUT,MODEL_OUTPUT,RUN_AS_OF_DATE,RUNTIME_CALL_START,"
            + "IS_TEST,CREATED_BY,CREATED_ON,LAST_UPDATED_BY,LAST_UPDATED_ON, ERROR_CODE,ERROR_DESCRIPTION, TRANSACTION_MODE ,FREE_MEMORY,CPU_USAGE,FREE_MEMORY_AT_START,CPU_USAGE_AT_START,IP_AND_PORT,POOL_NAME,OP_VALIDATION,ACCEPTABLEVALUES_VALIDATION,MODEL_EXEC_ENV_NAME) "
            + "VALUES(:ID,:TENANT_ID,:CLIENT_TRANSACTION_ID,:LIBRARY_NAME,:VERSION_NAME,:MAJOR_VERSION,:MINOR_VERSION,:STATUS, "
            + ":TENANT_INPUT,:MODEL_INPUT,:TENANT_OUTPUT,:MODEL_OUTPUT,:RUN_AS_OF_DATE,:RUNTIME_CALL_START,"
            + ":IS_TEST,:CREATED_BY,:CREATED_ON,:LAST_UPDATED_BY,:LAST_UPDATED_ON, :ERROR_CODE, :ERROR_DESCRIPTION, :TRANSACTION_MODE ,:FREE_MEMORY,:CPU_USAGE,:FREE_MEMORY_AT_START,:CPU_USAGE_AT_START,:IP_AND_PORT,:POOL_NAME,:OP_VALIDATION,:ACCEPTABLEVALUES_VALIDATION,:MODEL_EXEC_ENV_NAME)";

    private static final String INSERT_RESPONSE = "INSERT INTO UMG_RUNTIME_TRANSACTION(ID,TENANT_ID,CLIENT_TRANSACTION_ID,LIBRARY_NAME,VERSION_NAME,MAJOR_VERSION,MINOR_VERSION,STATUS, "
            + "TENANT_INPUT,MODEL_INPUT,TENANT_OUTPUT,MODEL_OUTPUT,RUN_AS_OF_DATE,RUNTIME_CALL_END, "
            + "MODEL_CALL_START,MODEL_CALL_END,IS_TEST,CREATED_BY,CREATED_ON,LAST_UPDATED_BY,LAST_UPDATED_ON, "
            + "MODEL_EXECUTION_TIME, MODELET_EXECUTION_TIME, ME2_WAITING_TIME, ERROR_CODE, ERROR_DESCRIPTION, TRANSACTION_MODE ,FREE_MEMORY, CPU_USAGE, FREE_MEMORY_AT_START, CPU_USAGE_AT_START, IP_AND_PORT, POOL_NAME, OP_VALIDATION,ACCEPTABLEVALUES_VALIDATION ) "
            + "VALUES(:ID,:TENANT_ID,:CLIENT_TRANSACTION_ID,:LIBRARY_NAME,:VERSION_NAME,:MAJOR_VERSION,:MINOR_VERSION,:STATUS, "
            + ":TENANT_INPUT,:MODEL_INPUT,:TENANT_OUTPUT,:MODEL_OUTPUT,:RUN_AS_OF_DATE,:RUNTIME_CALL_END, "
            + ":MODEL_CALL_START,:MODEL_CALL_END,:IS_TEST,:CREATED_BY,:CREATED_ON,:LAST_UPDATED_BY,:LAST_UPDATED_ON, "
            + ":MODEL_EXECUTION_TIME, :MODELET_EXECUTION_TIME, :ME2_WAITING_TIME, :ERROR_CODE, :ERROR_DESCRIPTION, :TRANSACTION_MODE, :FREE_MEMORY, :CPU_USAGE, :FREE_MEMORY_AT_START, :CPU_USAGE_AT_START, :IP_AND_PORT, :POOL_NAME, :OP_VALIDATION,:ACCEPTABLEVALUES_VALIDATION )";

    private static final String UPDATE_RESPONSE = "UPDATE UMG_RUNTIME_TRANSACTION SET " +
            "MODEL_EXECUTION_TIME = :MODEL_EXECUTION_TIME, " +
            "MODELET_EXECUTION_TIME = :MODELET_EXECUTION_TIME, ME2_WAITING_TIME = :ME2_WAITING_TIME, " +
            "RUNTIME_CALL_END = :RUNTIME_CALL_END, " +
            "MODEL_CALL_START = :MODEL_CALL_START, MODEL_CALL_END = :MODEL_CALL_END, STATUS = :STATUS , TRANSACTION_MODE = :TRANSACTION_MODE, " +
 "CPU_USAGE = :CPU_USAGE, FREE_MEMORY = :FREE_MEMORY, FREE_MEMORY_AT_START = :FREE_MEMORY_AT_START, CPU_USAGE_AT_START = :CPU_USAGE_AT_START ,IP_AND_PORT = :IP_AND_PORT, POOL_NAME = :POOL_NAME ,NO_OF_ATTEMPTS = :NO_OF_ATTEMPTS ,ERROR_CODE = :ERROR_CODE ,ERROR_DESCRIPTION = :ERROR_DESCRIPTION,R_SERVE_PORT= :R_SERVE_PORT "
            +
            "WHERE ID = :ID";
    
    private static final String UPDATE_MODEL_EXEC_ENV_NAME = "UPDATE UMG_RUNTIME_TRANSACTION SET " +
            "MODEL_EXEC_ENV_NAME = :MODEL_EXEC_ENV_NAME WHERE ID = :ID";

    private static final String CHECK_TRANSACTION_ID_EXISTS = "SELECT ID FROM UMG_RUNTIME_TRANSACTION WHERE ID = :ID LIMIT 1";
    
    private static final String DELETE_TXN = "DELETE FROM UMG_RUNTIME_TRANSACTION WHERE ID = :ID";
    
    private NamedParameterJdbcTemplate jdbcTemplate;
    @Inject
    @Named(value = "dataSource")
    private DataSource dataSource;

    /**
     * Initilize {@link JdbcTemplate} with {@link DataSource} instance.
     **/
    @PostConstruct
    public void initializeTemplate()
    {
        setJdbcTemplate(new NamedParameterJdbcTemplate(dataSource));
    }
    
    @Override
    public int remove(String transactionId) {
    	MapSqlParameterSource valueMap = new MapSqlParameterSource();
        valueMap.addValue(Constants.ID,transactionId);
        return jdbcTemplate.update(DELETE_TXN, valueMap);
    }
    
    private final Object lock = new Object();

    @Override
    public int insertTransactionLog(TransactionLog transactionLog) {
        MapSqlParameterSource valueMap = new MapSqlParameterSource();
        valueMap.addValue(Constants.ID, transactionLog.getId());
        valueMap.addValue(Constants.TENANT_ID, RequestContext.getRequestContext().getTenantCode());
        valueMap.addValue(Constants.CLIENT_TRANSACTION_ID, transactionLog.getTransactionId());
        valueMap.addValue(Constants.LIBRARY_NAME, transactionLog.getLibraryName());
        valueMap.addValue(Constants.VERSION_NAME, transactionLog.getModelName());
        valueMap.addValue(Constants.MAJOR_VERSION, transactionLog.getMajorVersion());
        valueMap.addValue(Constants.MINOR_VERSION, transactionLog.getMinorVersion());
        valueMap.addValue(Constants.STATUS, transactionLog.getStatus());
        valueMap.addValue(Constants.TENANT_INPUT, transactionLog.getTenantInput());
        valueMap.addValue(Constants.TENANT_OUTPUT, transactionLog.getTenantOutput());
        valueMap.addValue(Constants.MODEL_INPUT, transactionLog.getModelInput());
        valueMap.addValue(Constants.MODEL_OUTPUT, transactionLog.getModelOutput());
        valueMap.addValue(Constants.RUN_AS_OF_DATE, transactionLog.getRunAsOfDate());
        valueMap.addValue(Constants.RUNTIME_CALL_START, transactionLog.getRuntimeStart());
        valueMap.addValue(Constants.RUNTIME_CALL_END, transactionLog.getRuntimeEnd());
        valueMap.addValue(Constants.IS_TEST, transactionLog.getIsTest());
        valueMap.addValue(Constants.ERROR_CODE, transactionLog.getErrorCode());
        valueMap.addValue(Constants.ERROR_DESCRIPTION, transactionLog.getErrorDescription());
        valueMap.addValue(Constants.CREATED_BY, transactionLog.getCreatedBy());
        valueMap.addValue(Constants.CREATED_ON, System.currentTimeMillis());
        valueMap.addValue(Constants.LAST_UPDATED_BY, RequestContext.getRequestContext().getTenantCode());
        valueMap.addValue(Constants.LAST_UPDATED_ON, System.currentTimeMillis());
        valueMap.addValue(Constants.TRANSACTION_MODE, transactionLog.getTransactionMode());
        
        int update; 
        
        synchronized(lock) {
        	update = jdbcTemplate.update(SQL, valueMap);
        }
                
        return update;
    }
    
    @Override
    public int insertTxnFlowLog(TransactionLog transactionLog) {
    	MapSqlParameterSource valueMap = new MapSqlParameterSource();
        valueMap.addValue(Constants.ID, transactionLog.getId());
        valueMap.addValue(Constants.TENANT_ID, RequestContext.getRequestContext().getTenantCode());
        valueMap.addValue(Constants.CLIENT_TRANSACTION_ID, transactionLog.getTransactionId());
        valueMap.addValue(Constants.LIBRARY_NAME, transactionLog.getLibraryName());
        valueMap.addValue(Constants.VERSION_NAME, transactionLog.getModelName());
        valueMap.addValue(Constants.MAJOR_VERSION, transactionLog.getMajorVersion());
        valueMap.addValue(Constants.MINOR_VERSION, transactionLog.getMinorVersion());
        valueMap.addValue(Constants.STATUS, transactionLog.getStatus());
        valueMap.addValue(Constants.TENANT_INPUT, transactionLog.getTenantInput());
        valueMap.addValue(Constants.TENANT_OUTPUT, transactionLog.getTenantOutput());
        valueMap.addValue(Constants.MODEL_INPUT, transactionLog.getModelInput());
        valueMap.addValue(Constants.MODEL_OUTPUT, transactionLog.getModelOutput());
        valueMap.addValue(Constants.RUN_AS_OF_DATE, transactionLog.getRunAsOfDate());
        valueMap.addValue(Constants.RUNTIME_CALL_START, transactionLog.getRuntimeStart());
        valueMap.addValue(Constants.RUNTIME_CALL_END, transactionLog.getRuntimeEnd());
        valueMap.addValue(Constants.MODEL_CALL_START, transactionLog.getModelCallStart());
        valueMap.addValue(Constants.MODEL_CALL_END, transactionLog.getModelCallEnd());
        valueMap.addValue(Constants.IS_TEST, transactionLog.getIsTest());
        valueMap.addValue(Constants.CREATED_BY, transactionLog.getCreatedBy());
        valueMap.addValue(Constants.CREATED_ON, System.currentTimeMillis());
        valueMap.addValue(Constants.LAST_UPDATED_BY, RequestContext.getRequestContext().getTenantCode());
        valueMap.addValue(Constants.LAST_UPDATED_ON, System.currentTimeMillis());
        valueMap.addValue(Constants.ERROR_CODE, transactionLog.getErrorCode());
        valueMap.addValue(Constants.ERROR_DESCRIPTION, transactionLog.getErrorDescription());
        valueMap.addValue(Constants.MODEL_EXECUTION_TIME, transactionLog.getModelExecutionTime());
        valueMap.addValue(Constants.MODELET_EXECUTION_TIME, transactionLog.getModeletExecutionTime());
        valueMap.addValue(Constants.ME2_WAITING_TIME, transactionLog.getMe2WaitingTime());
        valueMap.addValue(Constants.OP_VALIDATION, transactionLog.isOpValidation());
        valueMap.addValue(Constants.ACCEPTABLEVALUES_VALIDATION, transactionLog.isACceptValuesValidation());
        
        return jdbcTemplate.update(INSERT_TXN_FLOW_SQL, valueMap);
    }

    public int checkTransactionExists(String umgTransactionId) {
        MapSqlParameterSource valueMap = new MapSqlParameterSource();
        valueMap.addValue(Constants.ID, umgTransactionId);
        List transactionList = jdbcTemplate.queryForList(CHECK_TRANSACTION_ID_EXISTS, valueMap);
        return CollectionUtils.size(transactionList);
    }

    @Override
    public int insertTransactionRequest(TransactionLog transactionLog) {
        MapSqlParameterSource valueMap = new MapSqlParameterSource();
        valueMap.addValue(Constants.ID, transactionLog.getId());
        valueMap.addValue(Constants.TENANT_ID, RequestContext.getRequestContext().getTenantCode());
        valueMap.addValue(Constants.CLIENT_TRANSACTION_ID, transactionLog.getTransactionId());
        valueMap.addValue(Constants.LIBRARY_NAME, transactionLog.getLibraryName());
        valueMap.addValue(Constants.VERSION_NAME, transactionLog.getModelName());
        valueMap.addValue(Constants.MAJOR_VERSION, transactionLog.getMajorVersion());
        valueMap.addValue(Constants.MINOR_VERSION, transactionLog.getMinorVersion());
        valueMap.addValue(Constants.STATUS,
                transactionLog.getStatus() == null ? TransactionStatus.QUEUED.getStatus() : transactionLog.getStatus());
        valueMap.addValue(Constants.TENANT_INPUT, RuntimeConstants.STR_EMPTY);
        valueMap.addValue(Constants.MODEL_INPUT, RuntimeConstants.STR_EMPTY);
        valueMap.addValue(Constants.TENANT_OUTPUT, RuntimeConstants.STR_EMPTY);
        valueMap.addValue(Constants.MODEL_OUTPUT, RuntimeConstants.STR_EMPTY);
        valueMap.addValue(Constants.RUN_AS_OF_DATE, transactionLog.getRunAsOfDate());
        valueMap.addValue(Constants.RUNTIME_CALL_START, transactionLog.getRuntimeStart());
        valueMap.addValue(Constants.IS_TEST, transactionLog.getIsTest());
        valueMap.addValue(Constants.CREATED_BY, transactionLog.getCreatedBy());
        valueMap.addValue(Constants.CREATED_ON, System.currentTimeMillis());
        valueMap.addValue(Constants.LAST_UPDATED_BY, RequestContext.getRequestContext().getTenantCode());
        valueMap.addValue(Constants.LAST_UPDATED_ON, System.currentTimeMillis());
        valueMap.addValue(Constants.TRANSACTION_MODE, transactionLog.getTransactionMode());
        valueMap.addValue(Constants.FREE_MEMORY, transactionLog.getFreeMemory());
        valueMap.addValue(Constants.CPU_USAGE, transactionLog.getCpuUsage());
        valueMap.addValue(Constants.FREE_MEMORY_AT_START, transactionLog.getFreeMemoryAtStart());
        valueMap.addValue(Constants.CPU_USAGE_AT_START, transactionLog.getCpuUsageAtStart());
        valueMap.addValue(Constants.POOL_NAME, transactionLog.getPoolName());
        valueMap.addValue(Constants.IP_AND_PORT, transactionLog.getIpAndPort());
        valueMap.addValue(Constants.OP_VALIDATION, transactionLog.isOpValidation());
        valueMap.addValue(Constants.ACCEPTABLEVALUES_VALIDATION, transactionLog.isACceptValuesValidation());
        valueMap.addValue(Constants.MODEL_EXEC_ENV_NAME,transactionLog.getModelExecEnvName());
        
        int update;
        synchronized(lock) {
            if(StringUtils.isNotBlank(transactionLog.getErrorCode())) {
                valueMap.addValue(Constants.ERROR_CODE, transactionLog.getErrorCode());
                valueMap.addValue(Constants.ERROR_DESCRIPTION, transactionLog.getErrorDescription());
                update = jdbcTemplate.update(INSERT_REQUEST_ERROR, valueMap);
            }
            else {
            	update = jdbcTemplate.update(INSERT_REQUEST, valueMap);
            }        	
        }
        
        return update;
    }

    @Override
    public int insertTransactionResponse(TransactionLog transactionLog) {
        MapSqlParameterSource valueMap = new MapSqlParameterSource();
        valueMap.addValue(Constants.ID, transactionLog.getId());
        valueMap.addValue(Constants.TENANT_ID, RequestContext.getRequestContext().getTenantCode());
        valueMap.addValue(Constants.CLIENT_TRANSACTION_ID, transactionLog.getTransactionId());
        valueMap.addValue(Constants.LIBRARY_NAME, transactionLog.getLibraryName());
        valueMap.addValue(Constants.VERSION_NAME, transactionLog.getModelName());
        valueMap.addValue(Constants.MAJOR_VERSION, transactionLog.getMajorVersion());
        valueMap.addValue(Constants.MINOR_VERSION, transactionLog.getMinorVersion());
        valueMap.addValue(Constants.STATUS, transactionLog.getStatus());
        valueMap.addValue(Constants.TENANT_INPUT, RuntimeConstants.STR_EMPTY);
        valueMap.addValue(Constants.MODEL_INPUT, RuntimeConstants.STR_EMPTY);
        valueMap.addValue(Constants.TENANT_OUTPUT, RuntimeConstants.STR_EMPTY);
        valueMap.addValue(Constants.MODEL_OUTPUT, RuntimeConstants.STR_EMPTY);
        valueMap.addValue(Constants.RUN_AS_OF_DATE, transactionLog.getRunAsOfDate());
        valueMap.addValue(Constants.RUNTIME_CALL_START, transactionLog.getRuntimeEnd());
        valueMap.addValue(Constants.RUNTIME_CALL_END, transactionLog.getRuntimeEnd());
        valueMap.addValue(Constants.MODEL_CALL_START, transactionLog.getModelCallStart());
        valueMap.addValue(Constants.MODEL_CALL_END, transactionLog.getModelCallEnd());
        valueMap.addValue(Constants.IS_TEST, transactionLog.getIsTest());
        valueMap.addValue(Constants.CREATED_BY, transactionLog.getCreatedBy());
        valueMap.addValue(Constants.CREATED_ON, System.currentTimeMillis());
        valueMap.addValue(Constants.LAST_UPDATED_BY, RequestContext.getRequestContext().getTenantCode());
        valueMap.addValue(Constants.LAST_UPDATED_ON, System.currentTimeMillis());
        valueMap.addValue(Constants.MODEL_EXECUTION_TIME, transactionLog.getModelExecutionTime());
        valueMap.addValue(Constants.MODELET_EXECUTION_TIME, transactionLog.getModeletExecutionTime());
        valueMap.addValue(Constants.ME2_WAITING_TIME, transactionLog.getMe2WaitingTime());
        valueMap.addValue(Constants.ERROR_CODE, transactionLog.getErrorCode());
        valueMap.addValue(Constants.ERROR_DESCRIPTION, transactionLog.getErrorDescription());
        valueMap.addValue(Constants.TRANSACTION_MODE, transactionLog.getTransactionMode());
        valueMap.addValue(Constants.CPU_USAGE, transactionLog.getCpuUsage());
        valueMap.addValue(Constants.FREE_MEMORY, transactionLog.getFreeMemory());
        valueMap.addValue(Constants.FREE_MEMORY_AT_START, transactionLog.getFreeMemoryAtStart());
        valueMap.addValue(Constants.CPU_USAGE_AT_START, transactionLog.getCpuUsageAtStart());
        valueMap.addValue(Constants.POOL_NAME, transactionLog.getPoolName());
        valueMap.addValue(Constants.IP_AND_PORT, transactionLog.getIpAndPort());
        valueMap.addValue(Constants.OP_VALIDATION, transactionLog.isOpValidation());
        valueMap.addValue(Constants.ACCEPTABLEVALUES_VALIDATION, transactionLog.isACceptValuesValidation());
        
        int update;

        synchronized(lock) {
        	update = jdbcTemplate.update(INSERT_RESPONSE, valueMap);
        }

        return update;         
    }

    @Override
    public int updateTransactionResponse(TransactionLog transactionLog) {
        MapSqlParameterSource valueMap = new MapSqlParameterSource();
        valueMap.addValue(Constants.ID, transactionLog.getId());
        valueMap.addValue(Constants.STATUS, transactionLog.getStatus());
        valueMap.addValue(Constants.RUNTIME_CALL_START, transactionLog.getRuntimeStart());
        valueMap.addValue(Constants.RUNTIME_CALL_END, transactionLog.getRuntimeEnd());
        valueMap.addValue(Constants.MODEL_CALL_START, transactionLog.getModelCallStart());
        valueMap.addValue(Constants.MODEL_CALL_END, transactionLog.getModelCallEnd());
        valueMap.addValue(Constants.MODEL_EXECUTION_TIME, transactionLog.getModelExecutionTime());
        valueMap.addValue(Constants.MODELET_EXECUTION_TIME, transactionLog.getModeletExecutionTime());
        valueMap.addValue(Constants.ME2_WAITING_TIME, transactionLog.getMe2WaitingTime());
        valueMap.addValue(Constants.TRANSACTION_MODE, transactionLog.getTransactionMode());
        valueMap.addValue(Constants.CPU_USAGE, transactionLog.getCpuUsage());
        valueMap.addValue(Constants.FREE_MEMORY, transactionLog.getFreeMemory());
        valueMap.addValue(Constants.FREE_MEMORY_AT_START, transactionLog.getFreeMemoryAtStart());
        valueMap.addValue(Constants.CPU_USAGE_AT_START, transactionLog.getCpuUsageAtStart());
        valueMap.addValue(Constants.IP_AND_PORT, transactionLog.getIpAndPort());
        valueMap.addValue(Constants.POOL_NAME, transactionLog.getPoolName());
        valueMap.addValue(Constants.NO_OF_ATTEMPTS, transactionLog.getNoOfAttempts());
        valueMap.addValue(Constants.ERROR_CODE, transactionLog.getErrorCode());
        valueMap.addValue(Constants.ERROR_DESCRIPTION, transactionLog.getErrorDescription());
        valueMap.addValue(Constants.R_SERVE_PORT, transactionLog.getrServePort());
        return jdbcTemplate.update(UPDATE_RESPONSE, valueMap);
    }

    
    /**
     * Get the instance of {@link JdbcTemplate}
     *
     * @return An instance of {@link JdbcTemplate}
     **/
    public NamedParameterJdbcTemplate getJdbcTemplate()
    {
        return jdbcTemplate;
    }

    /**
     * DOCUMENT ME!
     *
     * @param jdbcTemplate An instance of {@link JdbcTemplate}
     **/
    public void setJdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * DOCUMENT ME!
     *
     * @return the {@link DataSource} associated with {@link FlowDAOImpl} instance.
     **/
    public DataSource getDataSource()
    {
        return dataSource;
    }

    /**
     * DOCUMENT ME!
     *
     * @param dataSource the {@link DataSource} to set
     **/
    public void setDataSource(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    private interface Constants{
        String ID = "ID";
        String TENANT_ID = "TENANT_ID";
        String CLIENT_TRANSACTION_ID = "CLIENT_TRANSACTION_ID";
        String LIBRARY_NAME ="LIBRARY_NAME";
        String VERSION_NAME = "VERSION_NAME";
        String MAJOR_VERSION = "MAJOR_VERSION";
        String MINOR_VERSION = "MINOR_VERSION";
        String STATUS = "STATUS";
        String TENANT_INPUT = "TENANT_INPUT";
        String TENANT_OUTPUT = "TENANT_OUTPUT";
        String MODEL_INPUT = "MODEL_INPUT";
        String MODEL_OUTPUT = "MODEL_OUTPUT";
        String RUN_AS_OF_DATE = "RUN_AS_OF_DATE";
        String RUNTIME_CALL_START = "RUNTIME_CALL_START";
        String RUNTIME_CALL_END = "RUNTIME_CALL_END";
        String IS_TEST = "IS_TEST";
        String ERROR_CODE = "ERROR_CODE";
        String ERROR_DESCRIPTION = "ERROR_DESCRIPTION";
        String CREATED_BY = "CREATED_BY";
        String CREATED_ON = "CREATED_ON";
        String LAST_UPDATED_BY = "LAST_UPDATED_BY";
        String LAST_UPDATED_ON = "LAST_UPDATED_ON";
        String MODEL_CALL_START = "MODEL_CALL_START";
        String MODEL_CALL_END = "MODEL_CALL_END";
        String MODEL_EXECUTION_TIME = "MODEL_EXECUTION_TIME";
        String MODELET_EXECUTION_TIME = "MODELET_EXECUTION_TIME";
        String ME2_WAITING_TIME = "ME2_WAITING_TIME";
        String TRANSACTION_MODE = "TRANSACTION_MODE";
        String CPU_USAGE = "CPU_USAGE";
        String FREE_MEMORY = "FREE_MEMORY";
        String CPU_USAGE_AT_START = "CPU_USAGE_AT_START";
        String FREE_MEMORY_AT_START = "FREE_MEMORY_AT_START";
        String POOL_NAME = "POOL_NAME";
        String IP_AND_PORT = "IP_AND_PORT";
        String NO_OF_ATTEMPTS = "NO_OF_ATTEMPTS";
        String OP_VALIDATION = "OP_VALIDATION";
        String ACCEPTABLEVALUES_VALIDATION = "ACCEPTABLEVALUES_VALIDATION";
        String MODEL_EXEC_ENV_NAME = "MODEL_EXEC_ENV_NAME";
        String R_SERVE_PORT = "R_SERVE_PORT";
    }

	@Override
	public int updateModelExecEnvName(TransactionLog transactionLog) {
		 MapSqlParameterSource valueMap = new MapSqlParameterSource();
	        valueMap.addValue(Constants.ID, transactionLog.getId());	   
	        valueMap.addValue(Constants.MODEL_EXEC_ENV_NAME, transactionLog.getModelExecEnvName());
	        return jdbcTemplate.update(UPDATE_MODEL_EXEC_ENV_NAME, valueMap);
	}
}
