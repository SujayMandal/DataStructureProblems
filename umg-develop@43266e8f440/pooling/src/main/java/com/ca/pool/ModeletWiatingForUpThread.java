package com.ca.pool;

import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.constants.PoolConstants;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.modelet.ModeletClientInfo;
import com.ca.pool.model.ExecutionLanguage;
import com.ca.pool.modelet.profiler.info.ModeletProfileParamsInfo;
import com.ca.systemmodelet.SystemModeletConfig;
import com.hazelcast.core.IList;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.List;

import static com.ca.framework.core.constants.PoolConstants.*;
import static com.ca.me2.listener.ClientRegistrationListener.getHostKeyFromMember;
import static com.ca.modelet.ModeletClientInfo.getMemberKey;
import static java.lang.System.currentTimeMillis;

public class ModeletWiatingForUpThread implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(ModeletWiatingForUpThread.class);

	private final long totalWaitTime;
	private final long iterationSleepTime;
	private final PoolManager poolManager;
	private final String memberHost;
	private final int memberPort;
	private final SystemModeletConfig systemModeletConfig;

	public ModeletWiatingForUpThread(final PoolManager poolManager, final String memberHost, final int memberPort, final long totalWaitTime,
			final long iterationSleepTime, final SystemModeletConfig systemModeletConfig) {
		this.totalWaitTime = totalWaitTime;
		this.iterationSleepTime = iterationSleepTime;
		this.poolManager = poolManager;
		this.memberHost = memberHost;
		this.memberPort = memberPort;
		this.systemModeletConfig = systemModeletConfig;
	}

	@Override
	public void run() {
		final long startTime = currentTimeMillis();
		int i = 1;
		while (true) {
			LOGGER.debug("{} iteration count : {}", Thread.currentThread().getName(), i);
			i++;

			KeyValuePair<Boolean, ModeletClientInfo> modeletRegistrationStatus = isModeletRegisteredWithAnyPool();
			if(modeletRegistrationStatus.getKey()) {

				ModeletClientInfo modeletClientInfo = modeletRegistrationStatus.getValue();

				IList<Object> starttingModeletList = poolManager.getCacheRegistry().getList(PoolConstants.STARTING_MODELET_LIST);
				if(!starttingModeletList.contains(ModeletClientInfo.getMemberKey(modeletClientInfo.getMemberHost(), modeletClientInfo.getPort()))) {
					starttingModeletList.add(ModeletClientInfo.getMemberKey(modeletClientInfo.getMemberHost(), modeletClientInfo.getPort()));
				}

				setSystemModeletConfig();
				LOGGER.error(
						"Modelet is registered with Pool {}, now initiating Modelet Allocation Process, Member host is : {}, Member port is : {}",
						memberHost, memberPort);

				if(StringUtils.equalsIgnoreCase(ExecutionLanguage.R.getValue(), modeletClientInfo.getExecutionLanguage())) {
					boolean removed = false;
					IQueue<Object> pool = poolManager.getCacheRegistry().getDistributedPoolQueue(modeletClientInfo.getPoolName());
					removed = poolManager.removeModeletClientFromPoolQueue(pool, modeletClientInfo);
					loadModeltoModelet(modeletClientInfo);
					poolManager.getCacheRegistry().getMap(ALL_MODELET_MAP).put(modeletClientInfo.getHostKey(), modeletClientInfo);
					if(removed) {
						poolManager.getCacheRegistry().getDistributedPoolQueue(modeletClientInfo.getPoolName()).add(modeletClientInfo);
					}
					poolManager.getCacheRegistry().getMap(RA_SYSTEM_MODELETS).put(modeletClientInfo.getHostKey(), modeletClientInfo);
				}
				break;
			}

			try {
				LOGGER.debug("{} is sleeped for {}", Thread.currentThread().getName(), iterationSleepTime);
				Thread.sleep(iterationSleepTime);
			} catch (InterruptedException e) {
				LOGGER.error("Thread is Interrupted", e);
				LOGGER.error("Modelet Allocation is failed");
			}

			final long endTime = currentTimeMillis();

			if((endTime - startTime) > totalWaitTime) {
				LOGGER.error(
						"{} is waited total time {}, and skiping Modelelt Allocation as it is execeed maximum wait time, total no of iterations tried is {}",
						Thread.currentThread().getName(), totalWaitTime, i);
				LOGGER.error("Member Host is : {}, Member Port is : {}", memberHost, memberPort);
				LOGGER.error(
						"Modelet Allocation runs only when Modelet is up, but this thread will be called for even Runtime or Admin or ME2. if this thread is for "
								+ "Rutime or Admin or ME2, we can ignore, but if this is for Modelet, then Please run refreshModeletAllocation API to run modelet allocation. Please"
								+ "look at Member port and Member Hosts, Member Host is : {}, Member port is : {}", memberHost, memberPort);
				break;
			}
		}
	}

	private void setSystemModeletConfig() {
		final String hostKey = getHostKeyFromMember(memberHost, memberPort, poolManager.getCacheRegistry());
		final IMap<String, ModeletClientInfo> allModeletMap = poolManager.getCacheRegistry().getMap(ALL_MODELET_MAP);
		final IMap<String, ModeletClientInfo> systemModeletsMap = poolManager.getCacheRegistry().getMap(RA_SYSTEM_MODELETS);
		final IMap<String, List<ModeletProfileParamsInfo>> profilerData = poolManager.getCacheRegistry().getMap(PoolConstants.MODELET_PROFILER_LIST);
		//final IMap<String, List<ModeletProfileParamsInfo>> modeletProfilerData = poolManager.getCacheRegistry().getMap(MODELET_PROFILER);
		ModeletClientInfo modelClientInfo = allModeletMap.get(hostKey);
		if(!systemModeletsMap.containsKey(hostKey)) {
			String modeletId = systemModeletConfig.createModeletConfig(modelClientInfo);
			systemModeletConfig.createModeletProfilerLink(modelClientInfo, modeletId);
			LOGGER.debug("Host key " + hostKey + " added to RA_SYSTEM_MODELETS.");
			systemModeletsMap.put(hostKey, modelClientInfo);
			String key = org.apache.commons.lang.StringUtils
					.join(new Object[] {modelClientInfo.getHost(), FrameworkConstant.HYPHEN, String.valueOf(modelClientInfo.getPort())});
			List<ModeletProfileParamsInfo> profileParamList = profilerData.get(modelClientInfo.getProfiler());
			poolManager.getCacheRegistry().getMap(MODELET_PROFILER).put(key, profileParamList);
		} else {
			systemModeletConfig.updateModeletConfig(modelClientInfo, modelClientInfo.getPoolName());
			LOGGER.debug("Host key is present in RA_SYSTEM_MODELETS" + hostKey);
		}
	}

	private KeyValuePair<Boolean, ModeletClientInfo> isModeletRegisteredWithAnyPool() {
		boolean registered = false;
		final IMap<String, ModeletClientInfo> allModeletMap = poolManager.getCacheRegistry().getMap(ALL_MODELET_MAP);
		final String memberKey = getMemberKey(memberHost, memberPort);
		final String hostKey = getHostKeyFromMember(memberHost, memberPort, poolManager.getCacheRegistry());
		ModeletClientInfo modeletClientInfo = null;
		MDC.put(FrameworkConstant.MDC_MODELET_KEY, hostKey);
		if(hostKey != null) {
			modeletClientInfo = allModeletMap.get(hostKey);
			if(modeletClientInfo != null && modeletClientInfo.getModeletStatus().equals(ModeletStatus.REGISTERED.getStatus()) && !modeletClientInfo
					.getPoolName().equalsIgnoreCase(PoolConstants.DEFAULT_POOL)) {
				registered = true;
				LOGGER.error("Modelet is registered with pool. Modelet is : {}, Pool Name is : {}", modeletClientInfo.getLogMessage(),
						modeletClientInfo.getPoolName());
			}
		}

		if(!registered) {
			LOGGER.debug("Modelet is not registered with any pool yet, Member key is : {}", memberKey);
		}

		return new KeyValuePair<Boolean, ModeletClientInfo>(registered, modeletClientInfo);
	}

	private void loadModeltoModelet(ModeletClientInfo modeletClientInfo) {
		try {
			ModeletAllocationAlgorithm.loadModel(modeletClientInfo, poolManager);
		} catch (SystemException e) {
			modeletClientInfo.setModeletStatus(ModeletStatus.FAILED.getStatus());
		}
	}

}