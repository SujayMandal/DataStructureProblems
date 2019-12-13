package com.ca.framework.core.constants;

public final class SystemConstants {

	private SystemConstants() {

	}

	public static final String MESSAGE_SOURCE_FILE_PATH = "MESSAGE_SOURCE_FILE_PATH".intern();

	public static final String SYSTEM_KEY_TYPE_DATABASE = "DATABASE";

	public static final String SYSTEM_KEY_TYPE_TENANT = "TENANT";

	public static final String SYSTEM_KEY_TYPE_PLUGIN = "PLUGIN";

	/**
	 * Represents system key database driver.
	 */
	public static final String SYSTEM_KEY_DB_DRIVER = "DRIVER";

	/**
	 * Represents system key database url
	 */
	public static final String SYSTEM_KEY_DB_URL = "URL";

	/**
	 * Represents system key database schema
	 */
	public static final String SYSTEM_KEY_DB_SCHEMA = "SCHEMA";

	/**
	 * Represents system key database username
	 */
	public static final String SYSTEM_KEY_DB_USER = "USER";

	/**
	 * Represents system key database password
	 */
	public static final String SYSTEM_KEY_DB_PASSWORD = "PASSWORD";

	/**
	 * Represents system key tenant url
	 */
	public static final String SYSTEM_KEY_TENANT_URL = "RUNTIME_BASE_URL";

	/**
	 * Represents system key batch_enabled
	 */
	public static final String SYSTEM_KEY_BATCH_ENABLED = "BATCH_ENABLED";

	/**
	 * Represents system key ftp_enabled
	 */

	public static final String SYSTEM_KEY_FTP = "FTP";

	/**
	 * Represents system key ftp_enabled
	 */
	public static final String SYSTEM_KEY_TENANT_WRAPPER_TYPE = "TENANT_WRAPPER";

	public static final String SAN_BASE = "sanBase".intern();

	public static final String RECORD_LIMIT = "RECORD_LIMIT".intern();

	public static final String SYSTEM_KEY_CONNECTION_TIMEOUT = "connectionTimeout";

	public static final String SYSTEM_KEY_MAX_CONNECTION_AGE = "maxConnectionAge";

	public static final String SYSTEM_KEY_DEFAULT_AUTO_COMMIT = "defaultAutoCommit";

	public static final String MIN_POOL_SIZE = "minPoolSize";

	public static final String MAX_POOL_SIZE = "maxPoolSize";

	public static final String SYSTEM_KEY_MAX_IDLE_TIME = "maxIdleTime";

	/**
	 * Error code related
	 */
	public static final String VALIDATION_ERROR_CODE_PATTERN = "validation-error-code-pattern";
	public static final String SYSTEM_EXCEPTION_ERROR_CODE_PATTERN = "system-exception-error-code-pattern";
	public static final String MODEL_EXCEPTION_ERROR_CODE_PATTERN = "model-exception-error-code-pattern";

	public static final String BULK_TEST_MODE = "bulk-test-exec".intern();

	/**
	 * Represents umg_admin schema
	 */
	public static final String UMG_ADMIN_SCHEMA = "umg-admin-schema";

	/**
	 * Represents OBJECT_SIZE_FLAG system parameter to control object heap size
	 * calculator utility
	 */
	public static final String OBJECT_SIZE_FLAG = "OBJECT_SIZE_FLAG";

	/**
	 * fileupload path
	 */
	public static final String FILE_UPLOAD_TEMP_PATH = "uploadFileTempPath";

	/**
	 * added the constant for story umg-4020 for version reset
	 */
	public static final String VERSN_PUBLISH_CLIENT_TRAN_ID = "Publishing-Test";

	public static final String WINDOWS_OS = "Windows";

	public static final String VERSION_KEY = "VERSION_KEY";

	public static final String MAJOR_VERSION_KEY = "MAJOR_VERSION_KEY";

	public static final String LINUX_OS = "Linux";

	public static final String EXECUTION_ENVIRONMENT = "EXECUTION_ENVIRONMENT";

	public static final String ENVIRONMENT_VERSION = "ENVIRONMENT_VERSION";

	public static final String EXECUTION_LANGUAGE = "EXECUTION_LANGUAGE";

	public static final String R_MODE = "rMode";

	public static final String R_SERVE = "rServe";

	public static final String R_JAVA = "rJava";

	public static final String TEMP_PATH = "tempPath";

	public static final String INDEX_FILE_TEMP_PATH = "IndexFileTempPath";

}
