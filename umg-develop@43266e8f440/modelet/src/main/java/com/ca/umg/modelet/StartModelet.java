package com.ca.umg.modelet;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.constants.PoolConstants;
import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.modelet.common.ServerType;
import com.ca.pool.ModeletStatus;
import com.ca.pool.modelet.profiler.info.ModeletProfileParamsInfo;
import com.ca.pool.modelet.profiler.key.constant.ModeletProfilerKeyConstant;
import com.ca.umg.modelet.common.SystemInfo;
import com.ca.umg.modelet.config.ModeletConfig;
import com.ca.umg.modelet.constants.ModeletConstants;
import com.hazelcast.core.IMap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import static com.ca.framework.core.constants.PoolConstants.MODELET_PROFILER;
import static java.lang.System.getProperty;
import static org.apache.commons.lang3.math.NumberUtils.isDigits;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

public final class StartModelet {

	private static final Logger LOGGER = LoggerFactory.getLogger(StartModelet.class);

	private StartModelet() {
	}

	public static void main(final String[] args) {
		validateJVMArgs();
		final AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ModeletConfig.class);
		final InitializeModelet initializeModelet = ctx.getBean(InitializeModelet.class);
		try {
			createSystemInfo(ctx);
		} catch (Exception e) {
			LOGGER.error("Modelet shutdown. Reason: {}", e);
			System.exit(-1);
		}
		try {
			initializeModelet.initializeServer();
			initializeModelet.initializeRuntime();
			initializeModelet.registerModelet();
		} catch (SystemException e) {
			try {
				LOGGER.error("Exception occured.", e);
				initializeModelet.destroyRuntime();
				initializeModelet.destroyServer();
			} catch (SystemException e1) {
				LOGGER.error("Exception occured.", e1);
			} finally {
				ctx.close();
				System.exit(-1);
			}
		}
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				LOGGER.error("Started shutting down Modelet");
				LOGGER.info("Started destroying runtime");
				try {
					initializeModelet.destroyRuntime();
					LOGGER.info("Destroyed runtime successfully");
					LOGGER.info("Started destroying server socket");
					// unregistering modelet in shutdown hook is not possible as the hazelcast throws illegal state exception
					// initializeModelet.unregisterModelet();
					initializeModelet.destroyServer();
					LOGGER.info("Destroyed server socket successfully");
				} catch (SystemException e) {
					LOGGER.error("Exception occured in run.", e);
				} finally {
					// shutdown log4j2
					if(LogManager.getContext() instanceof LoggerContext) {
						LOGGER.error("Shutting down log4j2");
						Configurator.shutdown((LoggerContext) LogManager.getContext());
					} else {
						LOGGER.warn("Unable to shutdown log4j2");
					}
				}

				ctx.close();
			}
		});
	}

	private static void validateJVMArgs() {
		notNull(getProperty(ModeletConstants.MODELET_PROFILER));
		notNull(getProperty(ModeletConstants.LOG_PATH));
		notNull(getProperty(ModeletConstants.PORT));
		isTrue(isDigits(System.getProperty(ModeletConstants.PORT)));
		//notNull(getProperty(ModeletConstants.SERVER_TYPE));
		//notNull(getProperty(ModeletConstants.SAN_PATH));
		//notNull(getProperty(ModeletConstants.WORK_SPACE));
		//notNull(getProperty(ModeletConstants.EXECUTION_ENVIRONMENT), ModeletConstants.EXECUTION_ENVIRONMENT + " must not be null");
		//notNull(getProperty(ModeletConstants.EXECUTION_LANGUAGE), ModeletConstants.EXECUTION_LANGUAGE + " must not be null");
		/*isTrue(StringUtils.equals(SystemConstants.WINDOWS_OS, getProperty(ModeletConstants.EXECUTION_ENVIRONMENT)) || StringUtils
						.equals(SystemConstants.LINUX_OS, getProperty(ModeletConstants.EXECUTION_ENVIRONMENT)),
				"executionEnvironment should be either Windows or Linux");
		isTrue(StringUtils.equalsIgnoreCase(ExecutionLanguage.R.getValue(), getProperty(ModeletConstants.EXECUTION_LANGUAGE)) || StringUtils
						.equalsIgnoreCase(ExecutionLanguage.MATLAB.getValue(), getProperty(ModeletConstants.EXECUTION_LANGUAGE)) || StringUtils
						.equalsIgnoreCase(ExecutionLanguage.EXCEL.getValue(), getProperty(ModeletConstants.EXECUTION_LANGUAGE)),
				"executionLanguage should be R, Matlab or Excel");*/

	}

	private static void createSystemInfo(final AnnotationConfigApplicationContext ctx) {

		LOGGER.error("creating system info");
		final CacheRegistry cacheRegistry = ctx.getBean(CacheRegistry.class);
		final SystemInfo systemInfo = ctx.getBean(SystemInfo.class);
		String key = null;
		try {
			key = org.apache.commons.lang.StringUtils
					.join(new String[] {InetAddress.getLocalHost().getHostAddress(), getProperty(ModeletConstants.PORT)}, FrameworkConstant.HYPHEN);
		} catch (UnknownHostException e) {
			LOGGER.error("There is problem in reading local inet address.{}", e);
		}
		final IMap<String, List<ModeletProfileParamsInfo>> modeletProfilerData = cacheRegistry.getMap(MODELET_PROFILER);
		List<ModeletProfileParamsInfo> profileParamList = modeletProfilerData.get(key);

		if(CollectionUtils.isNotEmpty(profileParamList)) {
			systemInfo.setProfiler(profileParamList.get(0).getProfileName());
			LOGGER.error("Profiler place 1 : {}", profileParamList.get(0).toString());
			if(!StringUtils.equalsIgnoreCase(systemInfo.getProfiler(), getProperty(ModeletConstants.MODELET_PROFILER))) {
				LOGGER.error("There is mis-match in profiler name between D argument: {} and original: {}",
						getProperty(ModeletConstants.MODELET_PROFILER), systemInfo.getProfiler());
			}
		} else {
			systemInfo.setProfiler(getProperty(ModeletConstants.MODELET_PROFILER));
			final IMap<String, List<ModeletProfileParamsInfo>> profilerData = cacheRegistry.getMap(PoolConstants.MODELET_PROFILER_LIST);
			profileParamList = profilerData.get(systemInfo.getProfiler());
		}
		LOGGER.error("Profiler : {}", profileParamList != null ? profileParamList.toString() : "NA");
		isTrue(CollectionUtils.isNotEmpty(profileParamList), "Modelet is not associated with any profiler.");

		cacheRegistry.getMap(PoolConstants.CURRENT_MODELET_PROFILER).put(key, systemInfo.getProfiler());

		String workSpace = null;
		String execLang = profileParamList.get(0).getExecutionEnvironment();//R
		String execEnv = null;//Linux
		String logLevel = null;
		for (ModeletProfileParamsInfo profilerParam : profileParamList) {
			if(StringUtils.equals(profilerParam.getCode(), ModeletProfilerKeyConstant.WORKSPACE.getProfilerKey())) {
				workSpace = profilerParam.getParamValue();
			}
			if(StringUtils.equals(profilerParam.getCode(), ModeletProfilerKeyConstant.EXECUTION_ENVIRONMENT.getProfilerKey())) {
				execEnv = profilerParam.getParamValue();
			}
			if(StringUtils.equals(profilerParam.getCode(), ModeletProfilerKeyConstant.LOG_LEVEL.getProfilerKey())) {
				logLevel = profilerParam.getParamValue();
			}
		}

		LOGGER.error("workspace : {}, execution environment : {}, Execution Lang : {}, Log Level : {}", workSpace, execEnv, execLang, logLevel);
		setProfilerLogLevel(logLevel);

		systemInfo.setLogPath(getProperty(ModeletConstants.LOG_PATH));
		systemInfo.setPort(Integer.valueOf(getProperty(ModeletConstants.PORT)));
		systemInfo.setServerType(ServerType.SOCKET.getServerType());
		//extract san path from system parameter
		String sanPath = (String) cacheRegistry.getMap(SystemParameterProvider.SYSTEM_PARAMETER).get(SystemConstants.SAN_BASE);
		systemInfo.setSanPath(sanPath);
		systemInfo.setLocalSanPath(getProperty(ModeletConstants.LOCAL_SAN_PATH));

		systemInfo.setWorkspacePath(workSpace);
		systemInfo.setExecEnvironment(execEnv);
		systemInfo.setExecutionLanguage(execLang);
		systemInfo.setModeletName(getProperty(ModeletConstants.MODELET_NAME));
		systemInfo.setStatus(ModeletStatus.REGISTERED.getStatus());
		systemInfo.setrMode(SystemConstants.R_JAVA);
		if(getProperty(SystemConstants.R_MODE) != null) {
			systemInfo.setrMode(getProperty(SystemConstants.R_MODE));
		}
		if(getProperty(ModeletConstants.R_SERVE_PORT) != null && StringUtils.equalsIgnoreCase(systemInfo.getrMode(), SystemConstants.R_SERVE)) {
			systemInfo.setrServePort(Integer.valueOf(getProperty(ModeletConstants.R_SERVE_PORT)));
		}
		LOGGER.error("System info created. {}", systemInfo != null ? systemInfo.toString() : null);
	}

	private static void setProfilerLogLevel(String logLevel) {
		Level levelNew = null;
		if(StringUtils.equalsIgnoreCase(logLevel, Level.ERROR.name())) {
			levelNew = Level.ERROR;
		} else if(StringUtils.equalsIgnoreCase(logLevel, Level.INFO.name())) {
			levelNew = Level.INFO;
		} else if(StringUtils.equalsIgnoreCase(logLevel, Level.DEBUG.name())) {
			levelNew = Level.DEBUG;
		}
		if(levelNew != null) {
			LOGGER.info("Setting log level to : {}", logLevel);
			final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
			final Configuration config = ctx.getConfiguration();
			for (Map.Entry<String, LoggerConfig> cnf : config.getLoggers().entrySet()) {
				cnf.getValue().setLevel(levelNew);
			}
			ctx.updateLoggers();
		} else {
			LOGGER.error("Log Level did not used by profiler as only ERROR, INFO or DEBUG is allowed. Log Level from profiler: {}", logLevel);
		}
	}
}