package com.ca.umg.modelet.transport.cache;

import static com.ca.framework.core.constants.PoolConstants.ALL_MODELET_MAP;
import static com.ca.framework.core.constants.PoolConstants.HOST_TO_MEMBER;
import static com.ca.framework.core.constants.PoolConstants.MODELET_RESTART_MAP;
import static com.ca.framework.core.constants.PoolConstants.MODELET_RESTART_MAP_STATUS;
import static com.ca.framework.core.constants.PoolConstants.RA_SYSTEM_MODELETS;
import static java.lang.Boolean.FALSE;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.PoolConstants;
import com.ca.framework.core.exception.SystemException;
import com.ca.modelet.ModeletClientInfo;
import com.ca.pool.ModeletStatus;
import com.ca.pool.model.ExecutionLanguage;
import com.ca.pool.model.Pool;
import com.ca.umg.modelet.InitializeModelet;
import com.ca.umg.modelet.common.SystemInfo;
import com.ca.umg.modelet.listener.ClientRegistrationListener;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;

@SuppressWarnings("PMD")
@Named
public class Registry {

    private final static Logger LOGGER = LoggerFactory.getLogger(Registry.class);

    private boolean listenerAdded = Boolean.FALSE;

    @Inject
    @Named("modeletCacheRegistry")
    private CacheRegistry cacheRegistry;

    @Inject
    private AnnotationConfigApplicationContext context;

    @Inject
    private SystemInfo systemInfo;

    public void register() throws SystemException {
        LOGGER.debug("Initiated modelet registration.");
        String hostKey = null;
        String memberKey = null;
        int rServePort = 0;
        String rMode = null; 
        ModeletClientInfo clientInfo = null;
        boolean registerModelet = false;
        String poolName = null;
        try {
            clientInfo = createModeletClientInfo();
            hostKey = clientInfo.getHostKey();
            memberKey = clientInfo.getMemberKey();
            rServePort = systemInfo.getrServePort();

            LOGGER.error("New Modelet Started, About to register with cluster, Modelet Info is {}", clientInfo.getLogMessage());

            poolName = getCurentPoolForModelet(hostKey);
            removeModeletFromCache(clientInfo);
            //check when creating a new Modlet for one language with same stop port number which is already
            //register for another Modlet on second language
            if(StringUtils.isNotBlank(poolName) && isCurrentModletLanguage(hostKey)) {
            	poolName = null;
            }
            clientInfo.setrServePort(rServePort);
            clientInfo.setModeletStatus(ModeletStatus.REGISTERED.getStatus());
            registerModelet = true;
        } catch (UnknownHostException e) {
            LOGGER.error("An error occurred while registering the modelet {}.", clientInfo);
            if (clientInfo != null) {
                clientInfo.setModeletStatus(ModeletStatus.FAILED.getStatus());
            }
            throw new SystemException("", new String[] {}, e);
        } finally {
            final IMap<String, ModeletClientInfo> allModeletMap = cacheRegistry.getMap(ALL_MODELET_MAP);
            final IMap<String, String> hostToMember = cacheRegistry.getMap(HOST_TO_MEMBER);
            IMap<String, Pool> poolMap = cacheRegistry.getMap(PoolConstants.POOL_MAP);
            if (registerModelet) {
                if (clientInfo != null && hostKey != null) {
                    if (poolMap != null && clientInfo != null) {

                        if (isModelExecutionWaitingFormodelet(clientInfo)) {
                            clientInfo.setPoolName(poolName);
                            final IMap<String, ModeletClientInfo> modeletRestartMap = cacheRegistry.getMap(MODELET_RESTART_MAP);
                            modeletRestartMap.put(clientInfo.getHostKey(), clientInfo);
                        } else {

                            if (poolName == null && StringUtils.isBlank(poolName)) {
                                poolName = getDefaultPoolForModelet(poolMap);
                            }

                            IQueue<Object> pool = cacheRegistry.getDistributedPoolQueue(poolName);
                            clientInfo.setPoolName(poolName);
                            if (pool.add(clientInfo)) {
                                allModeletMap.put(hostKey, clientInfo);
                                hostToMember.put(hostKey, memberKey);
                                LOGGER.error("Modelet is added to All Modelet Map. HostKey : {}, value {}", hostKey,
                                        clientInfo.getLogMessage());

                                LOGGER.error("HostToMember is added, HostKey : {}, value {}", hostKey, memberKey);
                                LOGGER.error("Modelet {} registered with {} pool.", clientInfo.getLogMessage(), poolName);
                                LOGGER.error("{} pool size is {} ", poolName, pool.size());
                            } else {
                                LOGGER.error("Modelet {} registration with pool {} failed.", clientInfo.getLogMessage(),
                                        poolName);
                            }
                        }
                    }
                    LOGGER.error("Modelet Registration - Completed");
                }
                if (!listenerAdded) {
                    listenerAdded = Boolean.TRUE;
                    cacheRegistry.getHazelcastInstance().getLifecycleService()
                            .addLifecycleListener(new ClientRegistrationListener(this, context.getBean(InitializeModelet.class)));
                }
            } else {
                if (poolMap != null && clientInfo != null) {
                    LOGGER.error(
                            "New Modelet registration is failed, hence removing it from Pool. Pool is {} and Failed Modelet is ",
                            clientInfo.getPoolName(), clientInfo.getString());
                    removeModeletFromCache(clientInfo);
                }

                if (hostKey != null) {
                    allModeletMap.remove(hostKey);
                    LOGGER.error("Entry in All Modelet Map is removed, HostKey is : {}", hostKey);
                    hostToMember.remove(hostKey);
                    LOGGER.error("Entry in HostToMember is removed, Hostkey is : {}", hostKey);
                }

                LOGGER.error("Modelet Registration initiated - failed");
            }
        }
    }

    /**
     * checks if the model execution request is waiting for the modelet which has come up
     * 
     */
    private boolean isModelExecutionWaitingFormodelet(final ModeletClientInfo modeletClientInfo) {
        final IMap<String, Boolean> modeletRestartMap = cacheRegistry.getMap(MODELET_RESTART_MAP_STATUS);
        return modeletRestartMap.containsKey(modeletClientInfo.getHostKey())
                ? modeletRestartMap.get(modeletClientInfo.getHostKey()) : FALSE;

    }

    /**
     * returns the default pool name for the current modelet based on execution environment and execution language.
     * 
     * @param poolMap
     * @return
     */
    private String getDefaultPoolForModelet(Map<String, Pool> poolMap) {
        String poolName = null;
        for (Entry<String, Pool> poolEntry : poolMap.entrySet()) {
            Pool pool = poolEntry.getValue();
            if (pool.getDefaultPool() == 1
                    && StringUtils.equalsIgnoreCase(pool.getExecutionEnvironment(), systemInfo.getExecEnvironment())
                    && StringUtils.equalsIgnoreCase(pool.getExecutionLanguage(), systemInfo.getExecutionLanguage())) {
                poolName = pool.getPoolName();
            }
        }
        return poolName;
    }

    private String getCurentPoolForModelet(String hostKey) {
        final IMap<String, ModeletClientInfo> systemModeletsMap = cacheRegistry.getMap(RA_SYSTEM_MODELETS);
        ModeletClientInfo modeletClientInfo = systemModeletsMap.get(hostKey);
        return modeletClientInfo != null ? modeletClientInfo.getPoolName() : null;
    }
    private Boolean isCurrentModletLanguage(String hostKey) {
        final IMap<String, ModeletClientInfo> systemModeletsMap = cacheRegistry.getMap(RA_SYSTEM_MODELETS);
        ModeletClientInfo modeletClientInfo = systemModeletsMap.get(hostKey);
        return modeletClientInfo != null ? !StringUtils.equals(modeletClientInfo.getExecutionLanguage(), systemInfo.getExecutionLanguage()) : false;
    }

    public void reRegister() throws SystemException, UnknownHostException {
        LOGGER.error("Modelet re registration initiated");
        final IMap<String, ModeletClientInfo> allModeletMap = cacheRegistry.getMap(ALL_MODELET_MAP);
        ModeletClientInfo clientInfo = allModeletMap.get(systemInfo.getMemberHost() + "-" + systemInfo.getPort());
        removeModeletFromCache(clientInfo);
        register();
        LOGGER.error("Modelet re registration completed");
    }

    private ModeletClientInfo createModeletClientInfo() throws UnknownHostException {
        systemInfo.setMemberHost(cacheRegistry.getMemberAddress());
        ModeletClientInfo clientInfo = new ModeletClientInfo();
        clientInfo.setPort(systemInfo.getPort());
        clientInfo.setHost(InetAddress.getLocalHost().getHostAddress());
        clientInfo.setContextPath("");
        clientInfo.setServerType(systemInfo.getServerType());
        clientInfo.setMemberPort(cacheRegistry.getMemberPort());
        clientInfo.setMemberHost(systemInfo.getMemberHost());
        clientInfo.setExecutionLanguage(getEvnironment());
        clientInfo.setExecEnvironment(systemInfo.getExecEnvironment());
        clientInfo.setModeletName(systemInfo.getModeletName());
        clientInfo.setrServePort(systemInfo.getrServePort());
        clientInfo.setrMode(systemInfo.getrMode());
        clientInfo.setProfiler(systemInfo.getProfiler());
        return clientInfo;
    }

    private void removeModeletFromCache(final ModeletClientInfo clientInfo) {
        String poolName = getCurentPoolForModelet(clientInfo.getHostKey());
        boolean presenceFlag = false;
        // Remove from Pool
        IQueue<Object> pool = cacheRegistry.getDistributedPoolQueue(poolName);
        LOGGER.debug(
                "Trying to remove modelet from any existing pool if it is avaiable , Pool is : {}, Deleeting modeletg is : {}",
                pool.getName(), clientInfo.getLogMessage());
        if (CollectionUtils.isNotEmpty(pool)) {
            Iterator<Object> clientInfos = pool.iterator();
            while (clientInfos.hasNext()) {
                ModeletClientInfo modeletClientInfo = (ModeletClientInfo) clientInfos.next();
                if (StringUtils.equalsIgnoreCase(modeletClientInfo.getHostKey(), clientInfo.getHostKey())) {
                    pool.remove(modeletClientInfo);
                    LOGGER.debug("Existing modelet is removed successfully from pool {}, Deleted Modelet is : {} ",
                            pool.getName(), clientInfo.getLogMessage());
                    LOGGER.debug("Now {} pool size is {} after deleting", pool.getName(), pool.size());
                    presenceFlag = true;
                    break;
                }
            }
        }
        if (presenceFlag == false) {
            LOGGER.debug(
                    "Modelet is not avaiable in Pool to remove. hecnce not able to removed. Pool is : {}, Tried to delete Modelet is : {}",
                    pool.getName(), clientInfo.getLogMessage());
        }
    }

    public void unregister() throws SystemException {
        ModeletClientInfo clientInfo = null;
        final Map<String, ModeletClientInfo> allModeletMap = cacheRegistry.getMap(ALL_MODELET_MAP);
        final IMap<String, String> hostToMember = cacheRegistry.getMap(HOST_TO_MEMBER);
        IQueue<Object> cache = null;
        clientInfo = allModeletMap.get(systemInfo.getHostKey());
        clientInfo.setModeletStatus(ModeletStatus.UNREGISTERED.getStatus());
        cache = cacheRegistry.getDistributedPoolQueue(clientInfo.getPoolName());
        if (clientInfo != null && cache != null) {
            removeModeletFromCache(clientInfo);
            LOGGER.error("Modelet un-registration is successful. Unregistered Modelet is {}", clientInfo.getString());
            LOGGER.error("Now {} pool size is {} after unregistration", cache.getName(), cache.size());
            allModeletMap.put(clientInfo.getHostKey(), clientInfo);
            LOGGER.error("Entry in All Modelet Map is removed, Hostkey is : {}", clientInfo.getHostKey());
            hostToMember.remove(clientInfo.getHostKey());
            LOGGER.error("Entry in HostToMember is removed, Hostkey is : {}", clientInfo.getHostKey());
        } else {
            LOGGER.error("Modelet un-registration is Failed, because Modele is null or queue is null");
        }
    }

    public void shutdownHazelcastClient() {
        cacheRegistry.getHazelcastInstance().shutdown();
    }

    private String getEvnironment() {
        LOGGER.error("Modelet initialized with environment {}", systemInfo.getExecutionLanguage());
        return ExecutionLanguage.getEnvironment(systemInfo.getExecutionLanguage()).getValue();
    }
}