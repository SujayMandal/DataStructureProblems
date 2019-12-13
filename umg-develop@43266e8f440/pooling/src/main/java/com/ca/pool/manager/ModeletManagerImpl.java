/**
 *
 */
package com.ca.pool.manager;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.connection.ConnectionAttribute;
import com.ca.framework.core.connection.Connector;
import com.ca.framework.core.connection.ConnectorType;
import com.ca.framework.core.connection.SSHConnector;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.constants.PoolConstants;
import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.entity.ModeletRestartInfo;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.exception.codes.FrameworkExceptionCodes;
import com.ca.framework.core.systemparameter.SystemParameterConstants;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.modelet.ModeletClientInfo;
import com.ca.modelet.client.ModeletClient;
import com.ca.pool.constant.PoolingConstant;
import com.ca.pool.modelet.profiler.info.ModeletProfileParamsInfo;
import com.ca.pool.util.BeanToMapUtil;
import com.ca.umg.notification.notify.NotificationTriggerBO;
import com.hazelcast.core.IMap;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.ca.framework.core.constants.PoolConstants.MODELET_PROFILER;

/**
 * @author kamathan
 *
 */
@Named
public class ModeletManagerImpl implements ModeletManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(ModeletManagerImpl.class);
	public static final String PORT = "PORT";
	public static final String HOST = "HOST";
	public static final String SERVERTYPE = "SERVERTYPE";
	public static final String NAME = "NAME";
	public static final String RSERVEPORT = "RSERVEPORT";
	public static final String RMODE = "RMODE";
	public static final String EXPORT_PARAMS = "EXPORT_PARAMS";
	public static final String X_ARGS = "X_ARGS";
	public static final String D_ARGS = "D_ARGS";
	public static final String JMX_PORT = "#JMX_PORT#";
	public static final String DEFAULT_R_VERSION = "3.2.1";

	@Inject
	private ApplicationContext applicationContext;

	@Inject
	private CacheRegistry cacheRegistry;

	@Inject
	private ModeletHelper modeletHelper;

	@Inject
	private NotificationTriggerBO notificationTriggerBO;

	@PostConstruct
	private void init() {

	}

	@Override
	public void startModelet(ModeletClientInfo modeletClientInfo, String connectionType) throws SystemException, BusinessException {
		LOGGER.info("Received request to start modelet {}, {}. Current Pool is : {}", modeletClientInfo.getHost(), modeletClientInfo.getPort(),
				modeletClientInfo.getPoolName());
		Connector connector = getConnector(connectionType);
		LOGGER.info("Retrieved connector for starting modelet.");
		connector.setConnectionAttributes(getConnectionAttribute(modeletClientInfo));
		String command = getModeletStartupCommand(modeletClientInfo);

		if(StringUtils.isBlank(command)) {
			LOGGER.error("Modelet start up Command is empty, hence throwing error");
			SystemException.newSystemException(FrameworkExceptionCodes.MSE0000503, new Object[] {});
		}

		connector.openConnection();

		connector.executeCommand(command);
		connector.closeConnection();
		cacheRegistry.getMap(PoolConstants.ONLINE_MODELET).put(StringUtils
				.join(new String[] {modeletClientInfo.getHost(), String.valueOf(modeletClientInfo.getPort())}, FrameworkConstant.HYPHEN), true);
		LOGGER.error("Intiated to start modelet {}, {} successfully. Current Pool is : {}", modeletClientInfo.getHost(), modeletClientInfo.getPort(),
				modeletClientInfo.getPoolName());
	}

	private String getModeletStartupCommand(ModeletClientInfo modeletClientInfo) throws BusinessException {
		String command = null;
		// TODO generate commands based on modelet environment type
		String modeletStartupCommand = null;
		LOGGER.debug("getModeletStartupCommand() modeletClientInfo : {}", modeletClientInfo.getString());
		switch (modeletClientInfo.getExecEnvironment()) {
		case SystemConstants.LINUX_OS:
			if(StringUtils.equalsIgnoreCase(SystemConstants.R_JAVA, modeletClientInfo.getrMode())) {
				modeletStartupCommand = (String) cacheRegistry.getMap(SystemParameterProvider.SYSTEM_PARAMETER)
						.get(SystemParameterConstants.R_MODELET_STARTUP_SCRIPT_RJAVA);
			} else {
				modeletStartupCommand = (String) cacheRegistry.getMap(SystemParameterProvider.SYSTEM_PARAMETER)
						.get(SystemParameterConstants.R_MODELET_STARTUP_SCRIPT_RSERVE);
			}

			break;
		case SystemConstants.WINDOWS_OS:
			modeletStartupCommand = (String) cacheRegistry.getMap(SystemParameterProvider.SYSTEM_PARAMETER)
					.get(SystemParameterConstants.R_MODELET_STARTUP_SCRIPT_WINDOWS);
			break;
		default:
			throw new BusinessException(FrameworkExceptionCodes.RSE0000508, new Object[] {});
		}
		LOGGER.error("Modelet strtup command from cache : " + modeletStartupCommand);

		if(StringUtils.isNotBlank(modeletStartupCommand)) {
			command = getActualCommand(modeletClientInfo, modeletStartupCommand);
			LOGGER.error("Actual Complete Modelet strtup command is : {}", command);
		}

		return command;
	}

	private ConnectionAttribute getConnectionAttribute(ModeletClientInfo modeletClientInfo) {
		final ConnectionAttribute connectionAttribute = new ConnectionAttribute();

		connectionAttribute.setHost(modeletClientInfo.getHost());
		connectionAttribute
				.setUsername((String) cacheRegistry.getMap(SystemParameterProvider.SYSTEM_PARAMETER).get(SystemParameterConstants.SSH_USER));
		connectionAttribute
				.setPassword((String) cacheRegistry.getMap(SystemParameterProvider.SYSTEM_PARAMETER).get(SystemParameterConstants.SSH_PASSWORD));
		connectionAttribute
				.setIdentityKey((String) cacheRegistry.getMap(SystemParameterProvider.SYSTEM_PARAMETER).get(SystemParameterConstants.SSH_IDENTITY));

		LOGGER.info("Modelet Connection Details : {} ", connectionAttribute.toString());
		return connectionAttribute;
	}

	private String getActualCommand(ModeletClientInfo modeletClientInfo, String command) throws BusinessException {
		String formattedCommand = command;
		String[] allKeys = StringUtils.substringsBetween(formattedCommand, PoolConstants.NUMBER_SIGN, PoolConstants.NUMBER_SIGN);

		if(allKeys != null) {
			Set<String> keys = new HashSet<>(Arrays.asList(allKeys));
			Map<Object, Object> fieldMap = new BeanMap(modeletClientInfo);

			final IMap<String, List<ModeletProfileParamsInfo>> modeletProfilerData = cacheRegistry.getMap(MODELET_PROFILER);
			List<ModeletProfileParamsInfo> modeletProfilerParamInfos = modeletProfilerData
					.get(StringUtils.join(modeletClientInfo.getHost(), FrameworkConstant.HYPHEN, modeletClientInfo.getPort()));
			if(CollectionUtils.isEmpty(modeletProfilerParamInfos)) {
				BusinessException.newBusinessException(FrameworkExceptionCodes.RSE0000509, new Object[] {});
			}

			LOGGER.info("modeletProfilerParamInfos : {}", modeletProfilerParamInfos.toString());

			Map<String, List<String>> commandMap = new HashMap<>();

			for (ModeletProfileParamsInfo profilerData : modeletProfilerParamInfos) {
				if(!commandMap.containsKey(profilerData.getType())) {
					commandMap.put(profilerData.getType(), new ArrayList<String>());
				}
				commandMap.get(profilerData.getType()).add(ModeletProfilerArgEnum.getArgumentValue(profilerData));
			}
			commandMap.get(PoolConstants.PROFILER_TYPE_D_ARG)
					.add(StringUtils.join("-DmodeletProfiler=\"", modeletProfilerParamInfos.get(0).getProfileName(), "\""));

			LOGGER.info("Command Map : {}", commandMap);

			for (String key : keys) {
				switch (StringUtils.upperCase(key)) {
				case PORT:
					formattedCommand = StringUtils
							.replace(formattedCommand, StringUtils.join(PoolConstants.NUMBER_SIGN, key, PoolConstants.NUMBER_SIGN),
									String.valueOf(fieldMap.get(key)));

					formattedCommand = StringUtils.replace(formattedCommand, JMX_PORT, getJMXPort(String.valueOf(fieldMap.get(key))));
					break;
				case HOST:
					formattedCommand = StringUtils
							.replace(formattedCommand, StringUtils.join(PoolConstants.NUMBER_SIGN, key, PoolConstants.NUMBER_SIGN),
									(String) fieldMap.get(key));
					break;
				case SERVERTYPE:
					formattedCommand = StringUtils
							.replace(formattedCommand, StringUtils.join(PoolConstants.NUMBER_SIGN, key, PoolConstants.NUMBER_SIGN),
									(String) fieldMap.get(key));
					break;
				case NAME:
					formattedCommand = StringUtils
							.replace(formattedCommand, StringUtils.join(PoolConstants.NUMBER_SIGN, key, PoolConstants.NUMBER_SIGN),
									(String) fieldMap.get(key));
					break;
				case RSERVEPORT:
					formattedCommand = StringUtils
							.replace(formattedCommand, StringUtils.join(PoolConstants.NUMBER_SIGN, key, PoolConstants.NUMBER_SIGN),
									String.valueOf(fieldMap.get(key)));
					break;
				case RMODE:
					formattedCommand = StringUtils
							.replace(formattedCommand, StringUtils.join(PoolConstants.NUMBER_SIGN, key, PoolConstants.NUMBER_SIGN),
									(String) fieldMap.get(key));
					break;
				case EXPORT_PARAMS:
					formattedCommand = StringUtils
							.replace(formattedCommand, StringUtils.join(PoolConstants.NUMBER_SIGN, key, PoolConstants.NUMBER_SIGN),
									StringUtils.join(commandMap.get(PoolConstants.PROFILER_TYPE_EXPORT), PoolingConstant.SPACE));
					break;
				case X_ARGS:
					formattedCommand = StringUtils
							.replace(formattedCommand, StringUtils.join(PoolConstants.NUMBER_SIGN, key, PoolConstants.NUMBER_SIGN),
									StringUtils.join(commandMap.get(PoolConstants.PROFILER_TYPE_X_ARG), PoolingConstant.SPACE));
					break;
				case D_ARGS:
					formattedCommand = StringUtils
							.replace(formattedCommand, StringUtils.join(PoolConstants.NUMBER_SIGN, key, PoolConstants.NUMBER_SIGN),
									StringUtils.join(commandMap.get(PoolConstants.PROFILER_TYPE_D_ARG), PoolingConstant.SPACE));
					break;
				default:
					break;
				}
			}
		}
		LOGGER.info("Modelet startup command for modelet {} is {}.", modeletClientInfo, formattedCommand);
		return formattedCommand;
	}

	private String getJMXPort(String modeletPort) {
		String jmxPort = "";
		String jmxInformation = (String) cacheRegistry.getMap(SystemParameterProvider.SYSTEM_PARAMETER)
				.get(SystemParameterConstants.JMX_MODELET_PORT_MAPPING);
		String jmxModeletPortMapping[] = StringUtils.split(jmxInformation, "\\|");
		if(jmxModeletPortMapping != null) {
			for (String jmxModeletPorts : jmxModeletPortMapping) {
				String[] jmxmodeletport = StringUtils.split(jmxModeletPorts, "-");
				if(StringUtils.equalsIgnoreCase(jmxmodeletport[0], modeletPort)) {
					jmxPort = jmxmodeletport[1];
					break;
				}
			}
		}

		LOGGER.debug("JMX Port iss : " + jmxPort);

		if(jmxPort.equals("")) {
			LOGGER.error("JMX Port is empty WHICH IS WRONG, PLEASE LOOK INTO THIS");
		}

		return jmxPort;
	}

	@Override
	public void stopModelet(ModeletClientInfo modeletClientInfo) throws SystemException {
		LOGGER.info("Received request to stop modelet {}, {}.", modeletClientInfo.getHost(), modeletClientInfo.getPort());
		final ModeletClient modeletClient = modeletHelper.buildModeletClient(modeletClientInfo);
		if(modeletClient != null) {
			try {
				modeletClient.createConnection();
				// send shutdown command to modelet
				String input = null;
				// if (modeletClientInfo.getExecutionLanguage().equals(ExecutionLanguage.R.getValue())) {
				// input = "{" + "\"headerInfo\":{\"commandName\":\"STOP_RSERVE\",\"engine\":\"R\"" + " }" + "}";
				// } else {
				input = "{" + "\"headerInfo\":{\"commandName\":\"DESTROY_SERVER\",\"engine\":\"R\"" + " }" + "}";

				// }
				LOGGER.error("Modelet Stop Command is : {}", input);
				modeletClient.sendData(input);
				cacheRegistry.getMap(PoolConstants.ONLINE_MODELET).remove(StringUtils
						.join(new String[] {modeletClientInfo.getHost(), String.valueOf(modeletClientInfo.getPort())}, FrameworkConstant.HYPHEN));
			} catch (BusinessException e) {
				LOGGER.error("An error occurred while stopping modelet {}.", modeletClientInfo);
				SystemException.newSystemException(FrameworkExceptionCodes.MSE0000502, new Object[] {modeletClientInfo, e.getMessage()});
			}
		}

		LOGGER.error("Initiated too stop modelet {}, {} successfully. Current Pool name is : {}", modeletClientInfo.getHost(),
				modeletClientInfo.getPort(), modeletClientInfo.getPoolName());
	}

	private Connector getConnector(String connectionType) throws SystemException {
		Connector connector = null;
		switch (ConnectorType.valueOf(connectionType)) {
		case SSH:
			connector = applicationContext.getBean(SSHConnector.class);
			break;
		default:
			LOGGER.error("Connector Type is wrong, type is : {}, It should be SSH", connectionType);
			SystemException.newSystemException(FrameworkExceptionCodes.MSE0000501, new Object[] {connectionType});
		}
		return connector;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ca.pool.manager.ModeletManager#restartModelet(com.ca.modelet.ModeletClientInfo)
	 */
	@Override
	public void restartModelet(final ModeletClientInfo modeletClientInfo, Map<String, String> info) throws SystemException {
		LOGGER.error("Calling stopModelet from ModeletManagerImpl::restartModelet for modeletClientInfo " + modeletClientInfo);
		stopModelet(modeletClientInfo);
		try {
			if(notificationTriggerBO != null) {
				notificationTriggerBO.notifyModeletRestart(BeanToMapUtil.getModeletClientInfoMap(modeletClientInfo), info, Boolean.TRUE);
			}
		} catch (BusinessException e) {
			LOGGER.error("Exception occured sending notification for restart of modelet", e);
		}

		Thread startModeletThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					try {
						final long delay = getModeletStartDelay();
						LOGGER.error("Sleeping for {} in ModeletManagerImpl::restartModelet before calling start ", delay);
						Thread.sleep(delay);
					} catch (InterruptedException e) {
						LOGGER.error("An error occurred while restarting the modelet.", e);
						SystemException.newSystemException(FrameworkExceptionCodes.MSE0000503, new Object[] {e.getLocalizedMessage()});
					}

					LOGGER.error("Modelet is requested to start as part of rerstart process, Modelet details : {}", modeletClientInfo);
					try {
						// if (StringUtils.equalsIgnoreCase(ExecutionLanguage.R.getValue(),
						// modeletClientInfo.getExecutionLanguage())) {
						// startRServe(modeletClientInfo);
						// } else {
						startModelet(modeletClientInfo, ConnectorType.SSH.getType());
						// }
					} catch (SystemException se) {
						LOGGER.error("Exception occured While restarting the modelet", se);
					} catch (BusinessException e) {
						LOGGER.error("Exception occured While restarting the modelet", e);
					}
					LOGGER.error(
							"Modelet is requested to start as part of rerstart process, this is done, this should be registered with cluster before"
									+ " Modelet allocation is begin Modelet details : {}", modeletClientInfo);
				} catch (SystemException e) {
					LOGGER.error("An error occurred while starting modelet {}.", modeletClientInfo, e);
				}
			}
		});
		startModeletThread.start();
	}

	@Override
	public boolean isModeletRestartReq(ModeletClientInfo info) {
		Boolean restart = Boolean.FALSE;
		Boolean is_Any = Boolean.FALSE;
		if(info != null) {
			final IMap<String, List<ModeletRestartInfo>> restartModeletCountMap = cacheRegistry.getMap("RESTART_MODELET_COUNT_MAP");
			String key = info.getTenantCode() + PoolConstants.MODEL_SEPERATOR + info.getLoadedModel() + PoolConstants.MODEL_SEPERATOR + info
					.getLoadedModelVersion();

			String key_Any = info.getTenantCode() + PoolConstants.MODEL_SEPERATOR + info.getLoadedModel() + PoolConstants.MODEL_SEPERATOR + "Any";

			Integer execCount = 0;
			if(restartModeletCountMap.containsKey(key) || restartModeletCountMap.containsKey(key_Any)) {
				List<ModeletRestartInfo> modeletRestartInfoList = restartModeletCountMap.get(key);
				if(modeletRestartInfoList == null) {
					is_Any = Boolean.TRUE;
					modeletRestartInfoList = restartModeletCountMap.get(key_Any);
					key = key_Any;

				}
				ModeletRestartInfo existModeletRestartInfo = null;
				Boolean isExist = Boolean.FALSE;
				for (ModeletRestartInfo childMdeletRestartInfo : modeletRestartInfoList) {
					if(childMdeletRestartInfo.getModeletHostKey() != null && !StringUtils
							.equals(info.getHostKey(), childMdeletRestartInfo.getModeletHostKey())) {
						continue;
					} else if(childMdeletRestartInfo.getModeletHostKey() == null || info.getHostKey()
							.equals(childMdeletRestartInfo.getModeletHostKey())) {
						existModeletRestartInfo = childMdeletRestartInfo;
						isExist = Boolean.TRUE;
						break;
					}

				}
				if(!isExist) {
					restart = craeteNew(info, restart, restartModeletCountMap, key, execCount, modeletRestartInfoList);

				} else {
					if(existModeletRestartInfo == null) {
						existModeletRestartInfo = new ModeletRestartInfo();
					}
					if(existModeletRestartInfo.getModeletHostKey() == null && is_Any) {
						existModeletRestartInfo.setModeletHostKey("NA");
						craeteNew(info, restart, restartModeletCountMap, key, execCount, modeletRestartInfoList);
					} else {
						execCount = existModeletRestartInfo.getExecCount() + 1;
						existModeletRestartInfo.setModelNameAndVersion(info.getLoadedModel() + "_" + info.getLoadedModelVersion());
						if(execCount >= existModeletRestartInfo.getRestartCount()) {
							restart = Boolean.TRUE;
						}

						existModeletRestartInfo.setModeletHostKey(info.getHostKey());
						existModeletRestartInfo.setExecCount(execCount);
						restartModeletCountMap.put(key, modeletRestartInfoList);
					}

				}

			}
		}

		return restart;
	}

	private Boolean craeteNew(ModeletClientInfo info, Boolean restart, final IMap<String, List<ModeletRestartInfo>> restartModeletCountMap,
			String key, Integer execCount, List<ModeletRestartInfo> modeletRestartInfoList) {
		Boolean restartCopy = restart;
		ModeletRestartInfo newmodeletRestartInfo = new ModeletRestartInfo();
		newmodeletRestartInfo.setExecCount(execCount + 1);
		newmodeletRestartInfo.setRestartCount(modeletRestartInfoList.get(0).getRestartCount());
		newmodeletRestartInfo.setModeletHostKey(info.getHostKey());
		newmodeletRestartInfo.setModelNameAndVersion(info.getLoadedModel() + "_" + info.getLoadedModelVersion());
		newmodeletRestartInfo.setTenantId(modeletRestartInfoList.get(0).getTenantId());
		if(newmodeletRestartInfo.getExecCount() >= modeletRestartInfoList.get(0).getRestartCount()) {
			restartCopy = Boolean.TRUE;
		}
		modeletRestartInfoList.add(newmodeletRestartInfo);
		restartModeletCountMap.put(key, modeletRestartInfoList);
		return restartCopy;
	}

	@Override
	public ModeletRestartInfo getRestartAndExecCount(ModeletClientInfo info) {
		final IMap<String, List<ModeletRestartInfo>> restartModeletCountMap = cacheRegistry.getMap("RESTART_MODELET_COUNT_MAP");
		ModeletRestartInfo resatrtInfo = null;
		if(restartModeletCountMap != null) {
			String key = info.getTenantCode() + PoolConstants.MODEL_SEPERATOR + info.getLoadedModel() + PoolConstants.MODEL_SEPERATOR + info
					.getLoadedModelVersion();
			List<ModeletRestartInfo> infosList = restartModeletCountMap.get(key);
			if(infosList == null) {
				key = info.getTenantCode() + PoolConstants.MODEL_SEPERATOR + info.getLoadedModel() + PoolConstants.MODEL_SEPERATOR + "Any";
				infosList = restartModeletCountMap.get(key);
			}
			if(infosList != null) {
				for (ModeletRestartInfo modeletRestartInfo : infosList) {
					if(StringUtils.equals(modeletRestartInfo.getModeletHostKey(), (info.getHostKey()))) {
						resatrtInfo = modeletRestartInfo;
						break;
					}

				}
			}
		}
		return resatrtInfo;
	}

	@Override
	public void setExecCounttoZero(ModeletClientInfo info) {
		final IMap<String, List<ModeletRestartInfo>> restartModeletCountMap = cacheRegistry.getMap("RESTART_MODELET_COUNT_MAP");
		if(restartModeletCountMap != null) {
			String key = info.getTenantCode() + PoolConstants.MODEL_SEPERATOR + info.getLoadedModel() + PoolConstants.MODEL_SEPERATOR + info
					.getLoadedModelVersion();
			List<ModeletRestartInfo> infosList = (List<ModeletRestartInfo>) restartModeletCountMap.get(key);
			if(infosList == null) {
				key = info.getTenantCode() + PoolConstants.MODEL_SEPERATOR + info.getLoadedModel() + PoolConstants.MODEL_SEPERATOR + "Any";
				infosList = restartModeletCountMap.get(key);
			}
			if(infosList != null) {
				for (ModeletRestartInfo modeletRestartInfo : infosList) {
					if(StringUtils.equals(info.getHostKey(), modeletRestartInfo.getModeletHostKey())) {
						if(modeletRestartInfo.getId() == null) {
							infosList.remove(modeletRestartInfo);
						}
						modeletRestartInfo.setExecCount(PoolConstants.NUMBER_ZERO);
						restartModeletCountMap.put(key, infosList);
						break;
					}
				}
			}
		}

	}

	@Override
	public long getModeletStartDelay() {
		final String delay = (String) cacheRegistry.getMap(SystemParameterProvider.SYSTEM_PARAMETER)
				.get(SystemParameterConstants.MODELET_RESTART_DELAY);
		return StringUtils.isNotBlank(delay) ? Integer.parseInt(delay) : 30000l;
	}

	@Override
	public long getModeletStopDelay() {
		final long startDelay = getModeletStartDelay();
		return startDelay + 60000l;
	}

	@Override
	public void startRServe(ModeletClientInfo modeletClientInfo) throws SystemException {
		LOGGER.info("Received request to start R serve process {}, {}.", modeletClientInfo.getHost(), modeletClientInfo.getPort());
		final ModeletClient modeletClient = modeletHelper.buildModeletClient(modeletClientInfo);
		if(modeletClient != null) {
			try {
				modeletClient.createConnection();
				// send R serve initialize command to modelet
				String input = null;
				input = "{" + "\"headerInfo\":{\"commandName\":\"START_RSERVE\",\"engine\":\"R\"" + " }" + "}";
				modeletClient.sendData(input);
			} catch (BusinessException e) {
				LOGGER.error("An error occurred while stopping modelet {}.", modeletClientInfo);
				SystemException.newSystemException(FrameworkExceptionCodes.MSE0000502, new Object[] {modeletClientInfo, e.getMessage()});
			} finally {
				modeletClient.shutdownConnection();
			}
		}

	}

	@Override
	public String fetchModeletResponse(ModeletClientInfo modeletClientInfo, String connectionType) throws SystemException, BusinessException {
		String result = null;
		Connector connector = getConnector(connectionType);
		LOGGER.info("Retrieved connector for fetching modelet command result.");
		connector.setConnectionAttributes(getConnectionAttribute(modeletClientInfo));
		String command = "ps -ef | grep " + modeletClientInfo.getPort();
		LOGGER.info("command recieved to fetch running process. command is : " + command);

		if(StringUtils.isBlank(command)) {
			LOGGER.error("Modelet Command is empty, hence throwing error");
			SystemException.newSystemException(FrameworkExceptionCodes.MSE0000503, new Object[] {});
		}

		connector.openConnection();

		result = connector.getExecuteCommandResult(command);
		connector.closeConnection();
		LOGGER.error("response fetched from modelet {}", result);
		return result;
	}

	@Override
	public String fetchModeletLogs(ModeletClientInfo modeletClientInfo, String connectionType) throws SystemException {
		String result = null;
		Connector connector = getConnector(connectionType);
		LOGGER.info("Retrieved connector for fetching modelet logs.");
		connector.setConnectionAttributes(getConnectionAttribute(modeletClientInfo));
		String command = (String) cacheRegistry.getMap(SystemParameterProvider.SYSTEM_PARAMETER).get(SystemParameterConstants.MODELET_LOG);
		command = command.replace(PoolingConstant.MODELET_PORT, String.valueOf(modeletClientInfo.getPort()));
		LOGGER.info("Command for fetching modelet logs is : " + command);

		if(StringUtils.isBlank(command)) {
			LOGGER.error("Modelet command is empty, hence throwing error");
			SystemException.newSystemException(FrameworkExceptionCodes.MSE0000507, new Object[] {});
		}
		connector.openConnection();
		result = connector.getExecuteCommandResult(command);
		connector.closeConnection();
		LOGGER.error("Response fetched from modelet {}", result);
		return result;
	}
}
