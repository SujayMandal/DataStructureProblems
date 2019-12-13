package com.ca.pool.modelet.profiler.key.constant;

public enum ModeletProfilerKeyConstant {

	LD_LIBRARY_PATH("LD_LIBRARY_PATH"),
	R_TEMP_PATH("rTempPath"),
	JAVA_LIBRARY_PATH("java.library.path"),
	JAVA_HOME("JAVA_HOME"),
	R_HOME("R_HOME"),
	MAX_PERM_SIZE("X:MaxPermSize"),
	MIN_HEAP_FREE_RATIO("X:MinHeapFreeRatio"),
	MAX_HEAP_FREE_RATIO("X:MaxHeapFreeRatio"),
	MAX_MEMORY("mx"),
	WORKSPACE("workspace"),
	EXECUTION_ENVIRONMENT("executionEnvironment"),
	LOG_LEVEL("loglevel"),
	LOG4J_CONFIG_FILE("log4j.configurationFile"),
	HAZELCAST_CONFIG_FILE("hazelcast.config"),
	HTTP_CONNECTION_POOLING_CONFIG_FILE("httpConnectionPooling.properties"),
	X_CONC_MARK_SWEEP_GC("X:+UseConcMarkSweepGC"),
	X_PAR_NEW_GC("X:+UseParNewGC"),
	MIN_MEMORY("ms"),
	X_CMS_INIT_OCCUP_ONLY("X:+UseCMSInitiatingOccupancyOnly"),
	X_CMS_INIT_OCCUP_FRAC("X:CMSInitiatingOccupancyFraction");

	private String key;

	ModeletProfilerKeyConstant(final String key) {
		this.key = key;
	}

	public String getProfilerKey() {
		return this.key;
	}

}
