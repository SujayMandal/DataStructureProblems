/**
 *
 */
package com.ca.me2.listener;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.constants.PoolConstants;
import com.ca.framework.core.entity.ModeletRestartInfo;
import com.ca.modelet.ModeletClientInfo;
import com.ca.pool.ModeletStatus;
import com.ca.pool.ModeletWiatingForUpThread;
import com.ca.pool.PoolManager;
import com.ca.systemmodelet.SystemModeletConfig;
import com.hazelcast.core.Client;
import com.hazelcast.core.ClientListener;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;

import static com.ca.framework.core.constants.PoolConstants.ALL_MODELET_MAP;
import static com.ca.framework.core.constants.PoolConstants.DEFAULT_ITERATION_SLEEP_TIME;
import static com.ca.framework.core.constants.PoolConstants.DEFAULT_TOTAL_WAIT_TIME;
import static com.ca.modelet.ModeletClientInfo.getMemberKey;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext;

/**
 * Listener class for newly started modelets.
 *
 * this class is responsible forunregistering the modelet from the modelet registry in the case of modelet crash.
 *
 * @author kamathan
 *
 */
public class ClientRegistrationListener implements ClientListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClientRegistrationListener.class);

	private PoolManager poolManager;
	private SystemModeletConfig systemModeletConfig;
	private ExecutorService executorService = Executors.newCachedThreadPool();

	public ClientRegistrationListener(final PoolManager poolManager, final SystemModeletConfig systemModeletConfig) {
		this.poolManager = poolManager;
		this.systemModeletConfig = systemModeletConfig;
	}

	public ClientRegistrationListener() {

	}

	public void setSystemModeletConfig(SystemModeletConfig systemModeletConfig) {
		this.systemModeletConfig = systemModeletConfig;
	}

	public void setPoolManager(PoolManager poolManager) {
		this.poolManager = poolManager;
	}

	public void init() {
		LOGGER.info("Initializing ClientRegistrationListener");
	}

	public static ModeletClientInfo getModeletClientInfo(final String memberHost, final int memberPort, final CacheRegistry cacheRegistry) {
		ModeletClientInfo modeletClientInfo = null;
		final String hostKey = getHostKeyFromMember(memberHost, memberPort, cacheRegistry);
		if(hostKey != null) {
			MDC.put(FrameworkConstant.MDC_MODELET_KEY, hostKey);
			final Map<String, ModeletClientInfo> allModeletsMap = cacheRegistry.getMap(ALL_MODELET_MAP);
			modeletClientInfo = allModeletsMap.get(hostKey);
			LOGGER.info("Modelet Client info is found  for member, Member key is {}, Modelet client info is {}", getMemberKey(memberHost, memberPort),
					modeletClientInfo == null ? null : modeletClientInfo.getLogMessage());
		} else {
			LOGGER.info("Modelet client info is NOT found for member, Member is {}", getMemberKey(memberHost, memberPort));
		}

		return modeletClientInfo;
	}

	public static void removeFromQueue(final PoolManager poolManager, final String memberHost, final int memberPort) {
		ModeletClientInfo modeletClientInfo = getModeletClientInfo(memberHost, memberPort, poolManager.getCacheRegistry());

		if(modeletClientInfo != null) {
			LOGGER.info("Trying to remove found Modelet from Pool. Found Modelet is {}, and Pool is {}", modeletClientInfo.getString(),
					modeletClientInfo.getPoolName());
			boolean status = false;

			LOGGER.info("Removing key {} : {} from ONLINE_MODELET cache.", modeletClientInfo.getHost(), modeletClientInfo.getPort());
			poolManager.getCacheRegistry().getMap(PoolConstants.ONLINE_MODELET).remove(StringUtils
					.join(new String[] {modeletClientInfo.getHost(), String.valueOf(modeletClientInfo.getPort())}, FrameworkConstant.HYPHEN));

			final IQueue<Object> iQueue = poolManager.getCacheRegistry().getDistributedPoolQueue(modeletClientInfo.getPoolName());
			LOGGER.info(" Pool queue name is : {}, and size is : {}", iQueue.getName(), iQueue.size());

			if(isNotEmpty(iQueue)) {
				LOGGER.info(" Pool {} is not empty, so can be removed", modeletClientInfo.getPoolName());
				if(CollectionUtils.isNotEmpty(iQueue)) {
					Iterator<Object> clientInfos = iQueue.iterator();
					while (clientInfos.hasNext()) {
						ModeletClientInfo modeletClient = (ModeletClientInfo) clientInfos.next();
						if(StringUtils.equalsIgnoreCase(modeletClientInfo.getHostKey(), modeletClient.getHostKey())) {
							iQueue.remove(modeletClient);
							LOGGER.debug("Existing modelet is removed successfully from pool {}, Deleted Modelet is : {} ", iQueue.getName(),
									modeletClient.getLogMessage());
							LOGGER.debug("Now {} pool size is {} after deleting", iQueue.getName(), iQueue.size());
							status = true;
							break;
						}
					}
				}
				if(!status) {
					LOGGER.info("Found Modelet is not removed from pool, Modelet is {}, and Pool is {}", modeletClientInfo.getString(),
							modeletClientInfo.getPoolName());
					status = removeByComparing(modeletClientInfo.getPoolName(), modeletClientInfo, poolManager.getCacheRegistry());
				} else {
					LOGGER.info("Found Modelet is removed from pool, Modelet is {}, and Pool is {}", modeletClientInfo.getString(),
							modeletClientInfo.getPoolName());
				}
			} else {
				LOGGER.info(" Pool {} is empty, so can not be removed", modeletClientInfo.getPoolName());
				LOGGER.info(" As pool is empty, it is being created here");
				poolManager.getPoolObjectsLoader().createPoolQueue(modeletClientInfo.getPoolName());
			}

			if(StringUtils.equalsIgnoreCase(ModeletStatus.BUSY.getStatus(), modeletClientInfo.getModeletStatus())) {
				status = true;
			}
			if(status) {
				final IMap<String, ModeletClientInfo> allModeletMap = poolManager.getCacheRegistry().getMap(ALL_MODELET_MAP);
				final Map<String, String> hostToMember = poolManager.getCacheRegistry().getMap(PoolConstants.HOST_TO_MEMBER);
				modeletClientInfo.setModeletStatus(ModeletStatus.UNREGISTERED.getStatus());
				allModeletMap.put(modeletClientInfo.getHostKey(), modeletClientInfo);
				hostToMember.remove(modeletClientInfo.getHostKey());
				LOGGER.info(" Modelet is removed from All Modelet Map, and HostToMember Map. Removed Modelet is {}", modeletClientInfo.getString());
				LOGGER.info(" Modelet unregistration is successfull, Modelet is {}", modeletClientInfo.getString());
				LOGGER.info(" Modelet Pool {} size is {}", iQueue.getName(), iQueue.size());
			} else {
				LOGGER.info("Modelet unregistration is FAILED because modele is not removed from cluster, Modelet is {}",
						modeletClientInfo.getString());
			}

			final IMap<String, List<ModeletRestartInfo>> restartCountMap = poolManager.getCacheRegistry()
					.getMap(FrameworkConstant.RESTART_MODELET_COUNT_MAP);
			Set<String> ketSet = restartCountMap.keySet();
			for (String key : ketSet) {
				List<ModeletRestartInfo> restartInfoList = restartCountMap.get(key);
				for (ModeletRestartInfo modeletRestartInfo : restartInfoList) {
					if(modeletRestartInfo.getModeletHostKey() != null && modeletClientInfo.getHostKey()
							.equals(modeletRestartInfo.getModeletHostKey())) {
						if(modeletRestartInfo.getId() == null) {
							restartInfoList.remove(modeletRestartInfo);
						}
						modeletRestartInfo.setExecCount(PoolConstants.NUMBER_ZERO);
						restartCountMap.put(key, restartInfoList);
						break;
					}

				}

			}
		} else {
			LOGGER.error("ERROR:: Modelet client info is null, it is NOT found, hence not able to remove from cluster");
		}
	}

	private static boolean removeByComparing(final String poolName, final ModeletClientInfo clientInfo, final CacheRegistry cacheRegistry) {
		final IQueue<Object> poolQueue = cacheRegistry.getDistributedPoolQueue(clientInfo.getPoolName());
		if(isNotEmpty(poolQueue)) {
			LOGGER.error(" Trying remove by comparing, but pool is not empty, pool name is {}", clientInfo.getPoolName());
			Iterator<Object> modeletClients = poolQueue.iterator();
			while (modeletClients.hasNext()) {
				ModeletClientInfo modeletClientInfo = (ModeletClientInfo) modeletClients.next();
				if(modeletClientInfo.getPort() == clientInfo.getPort() && modeletClientInfo.getHost().equals(clientInfo.getHost())) {
					poolQueue.remove(modeletClientInfo);
					LOGGER.error("ERROR:: Removed by comparing, pool name is {}, and removed Modelet is {}", clientInfo.getPoolName(),
							modeletClientInfo);
					return true;
				}
			}
		} else {
			LOGGER.error("ERROR:: Trying remove by comparing, but Queue is empty, pool name is {}", clientInfo.getPoolName());
		}

		return false;
	}

	private PoolManager getPoolManager() {
		PoolManager pm = null;
		if(poolManager != null) {
			pm = poolManager;
		} else if(getCurrentWebApplicationContext() != null) {
			final Object object = getCurrentWebApplicationContext().getBean("poolManager");
			if(object != null) {
				pm = (PoolManager) object;
			} else {
				pm = poolManager;
			}
		}

		return pm;
	}

	public static String getHostKeyFromMember(final String memberHost, final int memberPort, final CacheRegistry cacheRegistry) {
		final String passedMemberKey = ModeletClientInfo.getMemberKey(memberHost, memberPort);
		final Map<String, String> hostToMember = cacheRegistry.getMap(PoolConstants.HOST_TO_MEMBER);
		final Set<String> hostKeySet = hostToMember.keySet();
		for (final String hostKey : hostKeySet) {
			final String memberKey = hostToMember.get(hostKey);
			LOGGER.info(" Host key and Member key are from HostToMember. Host key is {}, Member key is {}", hostKey, memberKey);
			if(memberKey != null && memberKey.equalsIgnoreCase(passedMemberKey)) {
				LOGGER.info(" Host key is found for the member key in HOST_TO_MEMBER map, host key is {} for the member key {}", hostKey, memberKey);
				return hostKey;
			}
		}

		LOGGER.info("Host key is NOT found for the member key in HOST_TO_MEMBER map, member key is {}", passedMemberKey);
		return null;
	}

	@Override
	public void clientConnected(final Client client) {
		LOGGER.error(" New Modelet memeber joined the cluster. New member is {}.", client);
		Callable<Void> callable = new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				final String memberHost = client.getSocketAddress().getAddress().getHostAddress();
				final int memberPort = client.getSocketAddress().getPort();
				final ModeletWiatingForUpThread modeletWiatingForUpThread = new ModeletWiatingForUpThread(poolManager, memberHost, memberPort,
						DEFAULT_TOTAL_WAIT_TIME, DEFAULT_ITERATION_SLEEP_TIME, systemModeletConfig);
				LOGGER.info("Started new ModeletWiatingForUpThread for Member added. MemberHost is {}, MemberPort is {}", memberHost, memberPort);
				modeletWiatingForUpThread.run();
				return null;
			}
		};
		executorService.submit(callable);
	}

	@Override
	public void clientDisconnected(final Client client) {
		Callable callable = new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				final Lock lock = poolManager.getCacheRegistry().getDistributedLock(PoolConstants.LOCK_FOR_MODELET_ALLOCATION);
				lock.lock();
				try {
					LOGGER.error(" Memeber is removed from the cluster. Removed Member is {}.", client);
					final PoolManager poolManager = getPoolManager();
					if(poolManager != null) {
						final String memberHost = client.getSocketAddress().getAddress().getHostAddress();
						final int memberPort = client.getSocketAddress().getPort();
						LOGGER.error(" Trying to remove member {} from the queue.", client);
						removeFromQueue(poolManager, memberHost, memberPort);
						LOGGER.error(" Refreshing system Modelet data.");
						systemModeletConfig.refreshCache();
						LOGGER.error(" Refreshed.");
					}
				} finally {
					lock.unlock();
				}
				return null;
			}
		};
		executorService.submit(callable);
	}
}