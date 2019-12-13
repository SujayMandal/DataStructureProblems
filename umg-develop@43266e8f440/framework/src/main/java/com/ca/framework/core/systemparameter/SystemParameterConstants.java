/**
 * 
 */
package com.ca.framework.core.systemparameter;

/**
 * @author kamathan
 *
 */
public final class SystemParameterConstants {

    public static final String SSH_KEY = "SSH_KEY";

    /**
     * SSH port to connect
     */
    public static final String SSH_PORT = "SSH_PORT";

    public static final String SSH_USER = "SSH_USER";

    public static final String SSH_PASSWORD = "SSH_PASSWORD";

    public static final String SSH_IDENTITY = "SSH_IDENTITY";
    
    public static final String R_MODELET_STARTUP_SCRIPT = "R_MODELET_STARTUP_SCRIPT";

    public static final String R_MODELET_STARTUP_SCRIPT_RJAVA = "R_MODELET_STARTUP_SCRIPT_RJAVA";
    
    public static final String R_MODELET_STARTUP_SCRIPT_RSERVE = "R_MODELET_STARTUP_SCRIPT_RSERVE";

    public static final String R_MODELET_STARTUP_SCRIPT_WINDOWS = "R_MODELET_STARTUP_SCRIPT_WINDOWS";
    
    public static final String MODEL_PUBLISH_STATUS_UPDATE_URL = "MODEL_PUBLISH_STATUS_UPDATE_URL";

    /**
     * Delay between stopping and starting the modelet automatically.
     */
    public static final String MODELET_RESTART_DELAY = "MODELET_RESTART_DELAY";

    public static final String JMX_MODELET_PORT_MAPPING = "JMX_MODELET_PORT_MAPPING";

    public static final String MODELET_EXEC_TIME_LIMIT = "MODELET_EXEC_TIME_LIMIT";

    public static final String R_MODELET_RESTART_ERROR_CODES = "R_MODELET_RESTART_ERROR_CODES";

    public static  final String ALLOWED_HOSTS = "ALLOWED_HOSTS".intern();

    public static  final String PROFILER_DEFAULT_KEY_PREFIX = "PROFILER_DEF_".intern();

    public  static final  String MODELET_LOG = "MODELET_LOG";

    private SystemParameterConstants() {

    }
}
