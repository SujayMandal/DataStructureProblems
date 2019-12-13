package com.ca.framework.core.constants;

@SuppressWarnings("PMD")
public class PoolConstants {

    private PoolConstants() {

    }

    public static final String MATLAB = "MATLAB";

    public static final String R = "R";

    public static final String BOTH_ENV = "BOTH";

    // Below three default pools must be same as default pools defined in pool table.

    public static final String DEFAULT_POOL = "SYSTEM_TEMP_POOL";

    // Below pool to hold the inactive or killed modelets for modelet pooling UI

    public static final String INACTIVE_MODELETS_POOL = "INACTIVE_MODELETS";

    // Here are object's name created in Hazelcast

    public static final String ALL_MODELET_MAP = "ALL_MODELET_MAP";

    public static final String RA_SYSTEM_MODELETS = "RA_SYSTEM_MODELETS";

    public static final String MODELET_PROFILER = "MODELET_PROFILER";

    public static final String MODELET_PROFILER_LIST = "MODELET_PROFILER_LIST";

    public static final String CURRENT_MODELET_PROFILER = "CURRENT_MODELET_PROFILER";

    public static final String ONLINE_MODELET = "ONLINE_MODELET";

    public static final String HOST_TO_MEMBER = "HOST_TO_MEMBER";

    public static final String POOL_MAP = "POOL_MAP";

    public static final String CRITERA_POOL_MAP = "CRITERA_POOL_MAP";

    public static final String POOL_USAGE_ORDER_MAP = "POOL_USAGE_ORDER_MAP";

    public static final String MAP_ENTRY_LISTENER = "MAP_ENTRY_LISTENER";

    public static final String MATLAB_SORTED_POOL_LIST = "MATLAB_SORTED_POOL_LIST";

    public static final String R_SORTED_POOL_LIST_LINUX = "R_SORTED_POOL_LIST_LINUX";

    public static final String R_SORTED_POOL_LIST_WINDOWS = "R_SORTED_POOL_LIST_WINDOWS";

    public static final String EXCEL_SORTED_POOL_LIST_WINDOWS = "EXCEL_SORTED_POOL_LIST_WINDOWS";

    public static final String STARTING_MODELET_LIST = "STARTING_MODELET_LIST";

    public static final int NUMBER_ZERO = 0;

    public static final int NUMBER_ONE = 1;

    public static final int NUMBER_TEN = 10;

    public static final String LOCK_FOR_MODELET_ALLOCATION = "LOCK_FOR_MODELET_ALLOCATION";

    public static final String MODELET_ALLOCATE_STATUS_MAP = "MODELET_ALLOCATE_STATUS_MAP";

    public static final String MODELET_ALLOCATE_STATUS_KEY = "MODELET_ALLOCATE_STATUS_KEY";

    public static final String MATLAB_MAX_MODELET_COUNT = "MATLAB_MAX_MODELET_COUNT";

    public static final String R_MAX_MODELET_COUNT = "R_MAX_MODELET_COUNT";

    public static final String MATLAB_MIN_MODELET_COUNT = "MATLAB_MIN_MODELET_COUNT";

    public static final String R_MIN_MODELET_COUNT = "R_MIN_MODELET_COUNT";

    public static final String ANY = "Any";

    public static final int MODELET_COUNT_FOR_DELETED_POOL = -1;

    public static final long DEFAULT_TOTAL_WAIT_TIME = 3 * 60 * 1000L;

    public static final long DEFAULT_ITERATION_SLEEP_TIME = 5 * 1000L;

    public static final String MODEL = "MODEL";

    public static final String MODEL_VERSION = "MODEL_VERSION";

    public static final String TENANT = "TENANT";

    public static final String EXECUTION_LANGUAGE = "EXECUTION_LANGUAGE";

    public static final String EXECUTION_LANGUAGE_VERSION = "EXECUTION_LANGUAGE_VERSION";

    public static final String EXECUTION_ENVIRONMENT = "EXECUTION_ENVIRONMENT";

    public static final String TRANSACTION_TYPE = "TRANSACTION_TYPE";

    public static final String TRANSACTION_MODE = "TRANSACTION_MODE";

    public static final String CHANNEL = "CHANNEL";

    public static final String R_MODELET_CAPACITY = "4GB - Linux 64 bit";

    public static final String MATLAB_MODELET_CAPACITY = "1GB - Linux 64 bit";

    public static final String NUMBER_SIGN = "#";

    public static final String ENV_SEPERATOR = "-";

    public static final String MODEL_SEPERATOR = "_";

    public static final String MODEL_VERION_SEPERATOR = ".";

    public static final String MODELET_RESTART_MAP_STATUS = "MODELET_RESTART_MAP_STATUS";

    public static final String MODELET_RESTART_MAP = "MODELET_RESTART_MAP";

    public static final String RETRY_COUNT = "retryCount";

    public static final String BREARCH_LIMIT = "BREARCH_LIMIT";

    public static final String ID = "ID";
    
    public static final String EXCEL = "EXCEL";   
    
    public static final String ACTIVE_EXECUTION_ENVIRONMENTS="ACTIVE_EXECUTION_ENVIRONMENTS_MAP";

    public static final String MODEL_EXECUTION_ENVIRONMENTS="MODEL_EXECUTION_ENVIRONMENTS_MAP";

    public static final String PROFILER_TYPE_EXPORT = "EXPORT";

    public static final String PROFILER_TYPE_X_ARG = "X_ARG";

    public static final String PROFILER_TYPE_D_ARG = "D_ARG";
   
    
}
