package com.ca.umg.rt.modelet.sync.processor;

import static com.ca.framework.core.constants.PoolConstants.POOL_MAP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.util.KeyValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.connection.ConnectorType;
import com.ca.framework.core.constants.PoolConstants;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.modelet.ModeletClientInfo;
import com.ca.modelet.client.ModeletClient;
import com.ca.modelet.client.SocketModeletClient;
import com.ca.pool.ModeletPoolingStatus;
import com.ca.pool.ModeletStatus;
import com.ca.pool.PoolManager;
import com.ca.pool.PoolObjectsLoader;
import com.ca.pool.manager.ModeletManager;
import com.ca.pool.model.Pool;
import com.ca.systemmodelet.SystemModeletConfig;
import com.ca.umg.notification.notify.NotificationTriggerDelegate;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.IList;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.hazelcast.util.CollectionUtil;

/**
 * @author yogeshku
 *
 */
@SuppressWarnings("PMD")
public class ModeletPoolSyncProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(ModeletPoolSyncProcessor.class);

	@Inject
	private CacheRegistry cacheRegistry;

	@Inject
	private ModeletManager modeletManager;

	@Inject
	private PoolObjectsLoader poolObjectsLoader;

	@Inject
	private SystemModeletConfig systemModeletConfig;

	@Inject
	private PoolManager poolManager;

	@Inject
	private NotificationTriggerDelegate notificationTriggerDelegate;

	private final ObjectMapper mapper = new ObjectMapper();

	@SuppressWarnings("unchecked")
	public void synchronizeModelets() {

		final IMap<Object, Pool> poolMap = cacheRegistry.getMap(POOL_MAP);

		final IMap<String, ModeletClientInfo> allModeletsMap = cacheRegistry.getMap(PoolConstants.ALL_MODELET_MAP);
		final IMap<String, String> map = poolObjectsLoader.getCacheRegistry()
				.getMap(PoolConstants.MODELET_ALLOCATE_STATUS_MAP);
		boolean poolUpdateInprogress = false;
		List<Map<String, Object>> modeletInfo = new ArrayList<Map<String, Object>>();
		if (!ModeletPoolingStatus.isInprogress(map.get(PoolConstants.MODELET_ALLOCATE_STATUS_KEY))) {// NO PMD
			try {
				poolUpdateInprogress = true;
				map.put(PoolConstants.MODELET_ALLOCATE_STATUS_KEY, ModeletPoolingStatus.IN_PROGRESS.getStatus());
				for (ModeletClientInfo modelet : allModeletsMap.values()) {
					LOGGER.debug(
							"Member Key : {} --Host Key : {} --Host : {} --Port Number : {} --Pool Name : {} --Context Path : {} --Model Name : {} --Model Status : {}",
							modelet.getMemberKey(), modelet.getHostKey(), modelet.getHost(), modelet.getPort(),
							modelet.getPoolName(), modelet.getContextPath(), modelet.getModeletName(),
							modelet.getModeletStatus());

					// Testing if modelet is Busy or not.
					if (StringUtils.equalsIgnoreCase(ModeletStatus.BUSY.getStatus(), modelet.getModeletStatus())) {
						LOGGER.info("Modelet pool name : {}, Host name : {}, Port number : {} is busy.",
								modelet.getPoolName(), modelet.getHost(), modelet.getPort());
						// Check if modelet is down.
						boolean modeletRunningtatus = checkModeletConnectionStatus(modelet);
						if (!modeletRunningtatus) {
							// In case modelet is down update modelet status UNREGISTERED.
							modelet.setModeletStatus(ModeletStatus.UNREGISTERED.getStatus());
							updateCacheRegistryMaps(modelet);
							isModeletRemovedFromDistributedPool(modelet, poolMap);
						}
						continue;
					}
					IList<Object> startingModeletList = cacheRegistry.getList(PoolConstants.STARTING_MODELET_LIST);
					if (CollectionUtil.isNotEmpty(startingModeletList) && startingModeletList
							.contains(ModeletClientInfo.getMemberKey(modelet.getMemberHost(), modelet.getPort()))) {
						startingModeletList
								.remove(ModeletClientInfo.getMemberKey(modelet.getMemberHost(), modelet.getPort()));
						LOGGER.info("Skipping synchronization for modelet {}.It just started.", modelet.getMemberKey());
						continue;
					}

					// Search and replace modelet in DISTRIBUTED_POOL_QUEUE.
					// Pool name has been taken from ALL_MODELET_MAP
					boolean modeletRemoved = modeletReplaceInDistributedPool(modelet);

					// If modelet does not exist in DISTRIBUTED_POOL_QUEUE for given pool name.
					// Search and replace modelet in each pool of DISTRIBUTED_POOL_QUEUE.
					if (!modeletRemoved) {
						modeletRemoved = isModeletRemovedFromDistributedPool(modelet, poolMap);
					}

					// double check for modelet is removed from distributed pool or not if not then
					// it is busy
					if (!modeletRemoved && StringUtils.equalsIgnoreCase(ModeletStatus.BUSY.getStatus(),modelet.getModeletStatus())) {
						LOGGER.info("Skipping synchronization for modelet {} It is in {} State",
								modelet.getHostKey(),modelet.getModeletStatus());
						continue;
					}
					boolean actualRunningStatus = checkRunningStatus(modelet);

					if (actualRunningStatus) {
						if (StringUtils.equalsIgnoreCase(ModeletStatus.FAILED.getStatus(), modelet.getModeletStatus())
								|| StringUtils.equalsIgnoreCase(ModeletStatus.UNREGISTERED.getStatus(),
										modelet.getModeletStatus())) {
							LOGGER.info("Stopping Modelet. Modelet Name : {}, Host : {}, Port : {}" + modelet.getPort(),
									modelet.getModeletName(), modelet.getHost(), modelet.getPort());
							try {
								modeletManager.stopModelet(modelet);
								checkModeletPoolStatus(modeletInfo, modelet,
										new KeyValuePair(modelet.getPoolName(), ModeletStatus.STARTED.getStatus()));
							} catch (SystemException e) {
								LOGGER.error(e.getLocalizedMessage(), e);
							}
						}
					} else {
						if (StringUtils.equalsIgnoreCase(ModeletStatus.REGISTERED.getStatus(),
								modelet.getModeletStatus())) {
							LOGGER.info("Starting Modelet. Modelet Name : {}, Host : {}, Port : {} ",
									modelet.getModeletName(), modelet.getHost(), modelet.getPort());
							try {
								modeletManager.startModelet(modelet, ConnectorType.SSH.getType());
								checkModeletPoolStatus(modeletInfo, modelet,
										new KeyValuePair(modelet.getPoolName(), ModeletStatus.STOPPED.getStatus()));
							} catch (SystemException | BusinessException e) {
								LOGGER.error(e.getLocalizedMessage(), e);
							}
						}
					}

					if (!StringUtils.equalsIgnoreCase(ModeletStatus.BUSY.getStatus(), modelet.getModeletStatus())) {
						KeyValuePair originalPool = fetchPoolNameForModelet(poolMap, modelet);

						if (originalPool != null
								&& !StringUtils.equalsIgnoreCase(modelet.getPoolName(), originalPool.getKey())) {
							checkModeletPoolStatus(modeletInfo, modelet, originalPool);
							systemModeletConfig.updateModeletConfig(modelet, modelet.getPoolName());
							updateCacheRegistryMaps(modelet);
						}

						// distributedPoolQueueOperation(modelet, poolMap);
						addModeletToDistributedPool(modelet);
					} else {
						LOGGER.info("Modelet pool name : {}, Host name : {}, Port number : {} is busy.",
								modelet.getPoolName(), modelet.getHost(), modelet.getPort());
					}

				}
			} finally {
				if (poolUpdateInprogress) {
					map.put(PoolConstants.MODELET_ALLOCATE_STATUS_KEY, ModeletPoolingStatus.DONE.getStatus());
				}
			}
			sendNotification(modeletInfo);
		} else {
			LOGGER.error("Skipping hazelcast sync proccess as modelet pool allocation algo is in progress");
		}

	}

	private boolean checkModeletConnectionStatus(ModeletClientInfo modelet) {
		boolean modeletRunningStatus = false;
		ModeletClient modeletClient = new SocketModeletClient(modelet.getHost(), modelet.getPort());

		try {
			modeletClient.createConnection();
			modeletRunningStatus = true;
		} catch (SystemException e) {
			LOGGER.debug("Error in checking connection for host {} and port {}.", modelet.getHost(), modelet.getPort());
		} finally {
			try {
				modeletClient.shutdownConnection();
			} catch (SystemException e) {
				LOGGER.debug("Error in closing connection for host {} and port {}.", modelet.getHost(),
						modelet.getPort());
			}
		}

		return modeletRunningStatus;
	}

	private boolean checkRunningStatus(ModeletClientInfo modelet) {
		boolean actualRunningStatus = false;
		ModeletClient modeletClient = new SocketModeletClient(modelet.getHost(), modelet.getPort());

		String result = null;
		try {
			modeletClient.createConnection();
			result = modeletClient.sendData("{" + "\"headerInfo\":{" + "\"commandName\":\"GET_STATUS\"" + "}" + "}");
		} catch (SystemException | BusinessException e1) {
			LOGGER.debug("Error in creating connection for host {} and port {}.", modelet.getHost(), modelet.getPort());
		} finally {
			try {
				modeletClient.shutdownConnection();
			} catch (SystemException e) {
				LOGGER.debug("Error in closing connection for host {} and port {}.", modelet.getHost(),
						modelet.getPort());
			}
		}

		if (null != result) {
			LOGGER.debug(result);

			Map<String, Object> data = null;
			try {
				data = mapper.readValue(result, new TypeReference<HashMap<String, Object>>() {
				});
			} catch (JsonParseException | JsonMappingException e) {
				LOGGER.error("Error in parsing search result.", e);
			} catch (IOException ioe) {
				LOGGER.error("Error in parsing search result.", ioe);
			}

			if (!StringUtils.contains(result, "error") || StringUtils.equals("false",
					(String) ((Map<String, Object>) data.get("responseHeaderInfo")).get("error"))) {
				actualRunningStatus = true;
			}
		}
		return actualRunningStatus;
	}

	private void sendNotification(List<Map<String, Object>> modeletInfo) {
		if (!modeletInfo.isEmpty()) {
			Map<String, Object> notificationMap = new HashMap<String, Object>();
			notificationMap.put("modeletList", modeletInfo);
			try {
				notificationTriggerDelegate.notifyModeletStatusfinal(notificationMap, false);
			} catch (SystemException | BusinessException e) {
				LOGGER.info("Issue With Notify Modelet Pool Change Info " + e.getLocalizedMessage());
			}
		}
	}

	/*
	 * private void distributedPoolQueueOperation(ModeletClientInfo modelet,
	 * IMap<Object, Pool> poolMap) { boolean modeletFound =
	 * modeletExistInDistributedPool(modelet);
	 * 
	 * LOGGER.
	 * info("Distributed pool queue, pool name : {}, modelclient host {}, port {} search result : {}"
	 * , modelet.getPoolName(), modelet.getHost(), modelet.getPort(), modeletFound);
	 * 
	 * if (!modeletFound) { removeModeletFromDistributedPool(modelet, poolMap);
	 * addModeletToDistributedPool(modelet); } }
	 */

	private void addModeletToDistributedPool(ModeletClientInfo modelet) {
		try {
			LOGGER.info("Adding modelet to distributed pool queue = {}. host = {} , port = {} and Modelet Status = {}", modelet.getPoolName(),
					modelet.getHost(), modelet.getPort() ,modelet.getModeletStatus());
			if (StringUtils.equalsIgnoreCase(ModeletStatus.REGISTERED.getStatus(), modelet.getModeletStatus())) {
			poolManager.addModeletToPoolQueue(modelet);
			}
			// checkModeletPoolStatus(modeletInfo, modelet, new
			// KeyValuePair(modelet.getPoolName(), modelet.getModeletStatus()));
		} catch (SystemException e) {
			LOGGER.info("Pool is not available. " + e.getLocalizedMessage());
		}
	}

	/**
	 * Search and replace modelet in each pool of DISTRIBUTED_POOL_QUEUE.
	 * 
	 * @param modelet
	 * @param poolMap
	 */
	private Boolean isModeletRemovedFromDistributedPool(ModeletClientInfo modelet, IMap<Object, Pool> poolMap) {
		Boolean removed = false;
		for (Pool pool : poolMap.values()) {
			IQueue<Object> distributedPoolQueue = cacheRegistry.getDistributedPoolQueue(pool.getPoolName());

			removed = poolManager.removeModeletClientFromPoolQueue(distributedPoolQueue, modelet);
			if (removed) {
				break;
			}
		}

		return removed;
	}

	/*
	 * private boolean modeletExistInDistributedPool(ModeletClientInfo modelet) {
	 * final IQueue<Object> poolQueue =
	 * cacheRegistry.getDistributedPoolQueue(modelet.getPoolName()); boolean
	 * modeletFound = false; if (CollectionUtils.isNotEmpty(poolQueue)) {
	 * Iterator<Object> modeletClients = poolQueue.iterator(); while
	 * (modeletClients.hasNext()) { ModeletClientInfo modelClientInfo =
	 * (ModeletClientInfo) modeletClients.next(); if
	 * (StringUtils.equalsIgnoreCase(modelet.getHostKey(),
	 * modelClientInfo.getHostKey())) { modeletFound = true; break; } } } return
	 * modeletFound; }
	 */

	/**
	 * Search and replace modelet from DISTRIBUTED_POOL_QUEUE for specific modelet
	 * pool name.
	 * 
	 * @param modelet
	 * @return
	 */
	private boolean modeletReplaceInDistributedPool(ModeletClientInfo modelet) {
		final IQueue<Object> poolQueue = cacheRegistry.getDistributedPoolQueue(modelet.getPoolName());
		return poolManager.removeModeletClientFromPoolQueue(poolQueue, modelet);
	}

	private void checkModeletPoolStatus(List<Map<String, Object>> modeletInfo, ModeletClientInfo modelet,
			KeyValuePair originalPool) {
		// Change pool details
		Map<String, Object> poolDetails = new HashMap<String, Object>();
		poolDetails.put("oldPoolname", originalPool.getKey());
		poolDetails.put("newPoolname", modelet.getPoolName());
		poolDetails.put("modeletName", modelet.getHostKey());
		poolDetails.put("newStatus", modelet.getModeletStatus());
		poolDetails.put("oldStatus", originalPool.getValue());
		modeletInfo.add(poolDetails);
		LOGGER.info("Updating Modelet pool details.");
		LOGGER.info("Old Pool Name : " + originalPool.getKey());
		LOGGER.info("New Pool Name : " + modelet.getPoolName());
		LOGGER.info("Modelet Name : " + modelet.getModeletName() + ", Host : " + modelet.getHost() + ", Port : "
				+ modelet.getPort());
	}

	private void updateCacheRegistryMaps(ModeletClientInfo modelet) {
		LOGGER.debug("New pool Name" + modelet.getPoolName());
		// LOGGER.debug("Old pool name is : " + oldPoolName);

		final IMap<String, ModeletClientInfo> systemModeletsMap = cacheRegistry
				.getMap(PoolConstants.RA_SYSTEM_MODELETS);

		final IMap<String, ModeletClientInfo> allModeletsMap = cacheRegistry.getMap(PoolConstants.ALL_MODELET_MAP);

		// final IMap<Object, Pool> poolMap = cacheRegistry.getMap(POOL_MAP);

		for (ModeletClientInfo modeletInfo : systemModeletsMap.values()) {
			if (StringUtils.equals(modeletInfo.getHost(), modelet.getHost())
					&& modeletInfo.getPort() == modelet.getPort()) {
				modeletInfo.setPoolName(modelet.getPoolName());
				cacheRegistry.getMap(PoolConstants.RA_SYSTEM_MODELETS).put(modelet.getHostKey(), modelet);
				break;
			}
		}

		for (ModeletClientInfo modeletInfo : allModeletsMap.values()) {
			if (StringUtils.equals(modeletInfo.getHost(), modelet.getHost())
					&& modeletInfo.getPort() == modelet.getPort()) {
				modeletInfo.setPoolName(modelet.getPoolName());
				cacheRegistry.getMap(PoolConstants.ALL_MODELET_MAP).put(modelet.getHostKey(), modelet);
				break;
			}
		}

		/*
		 * if (StringUtils.equalsIgnoreCase(ModeletStatus.REGISTERED.getStatus(),
		 * modelet.getModeletStatus())) { IQueue<Object> oldDistributedPool =
		 * cacheRegistry.getDistributedPoolQueue(oldPoolName);
		 * 
		 * poolManager.removeModeletClientFromPoolQueue(oldDistributedPool, modelet);
		 * 
		 * try { poolManager.addModeletToPoolQueue(modelet); } catch (SystemException e)
		 * { LOGGER.info("Pool is not available. " + e.getLocalizedMessage()); } }
		 */

	}

	private KeyValuePair fetchPoolNameForModelet(IMap<Object, Pool> poolMap, ModeletClientInfo modelet) {

		KeyValuePair poolDetail = null;

		for (Pool pool : poolMap.values()) {

			List<ModeletClientInfo> modeleteList = poolObjectsLoader.getModeletClientInfo(pool.getPoolName());
			for (ModeletClientInfo modeletInfo : modeleteList) {
				if (StringUtils.equalsIgnoreCase(modelet.getHost(), modeletInfo.getHost())
						&& modelet.getPort() == modeletInfo.getPort()) {
					poolDetail = new KeyValuePair(modeletInfo.getPoolName(), modeletInfo.getModeletStatus());
					break;
				}
			}
			if (null != poolDetail) {
				break;
			}
		}

		return poolDetail;
	}

}
