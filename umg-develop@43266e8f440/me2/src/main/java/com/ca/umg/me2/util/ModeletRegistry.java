/**
 * 
 */
package com.ca.umg.me2.util;

import static com.ca.framework.core.constants.PoolConstants.ALL_MODELET_MAP;
import static com.ca.framework.core.constants.PoolConstants.CRITERA_POOL_MAP;
import static com.ca.framework.core.constants.PoolConstants.HOST_TO_MEMBER;
import static com.ca.framework.core.constants.PoolConstants.POOL_USAGE_ORDER_MAP;
import static com.ca.umg.me2.util.ModelExecConstants.TIME_OUT;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.modelet.ModeletClientInfo;
import com.ca.modelet.client.ModeletClient;
import com.ca.pool.PoolManager;
import com.ca.pool.manager.ModeletHelper;
import com.ca.pool.model.Pool;
import com.ca.pool.model.PoolStatusStats;
import com.ca.pool.model.PoolUsageOrderMapping;
import com.ca.pool.model.TransactionCriteria;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;

/**
 * Holds all the modelets available for servicing.
 * 
 * @author kamathan
 *
 */
@SuppressWarnings("PMD")
public class ModeletRegistry {

    private static final int SECONDS_FOR_MINITUE = 60;

    private static final double MILLISECONDS_FOR_SECOND = 1000.0;

    private static final Logger LOGGER = LoggerFactory.getLogger(ModeletRegistry.class);

    @Inject
    private SystemParameterProvider systemParameterProvider;

    @Inject
    private ModeletHelper modeletHelper;

    /**
     * time to wait for the request to fetch the next available modelet client from registry
     */

    private long timeout = 3600;

    private PoolManager poolManager;

    public void addModeletPoolManager(final ModeletClientInfo modeletClientInfo) throws SystemException {
        LOGGER.error("Adding modelet {} back into the pool manager.", modeletClientInfo);
        LOGGER.error("Details of the re-added modelet :" + modeletClientInfo.toString());
        poolManager.addModeletToPoolQueue(modeletClientInfo);
    }

    public void removeModeletFromPoolManager(final ModeletClientInfo modeletClientInfo) throws SystemException {
        boolean removed = poolManager.removeModeletFromPoolQueue(modeletClientInfo);
        LOGGER.debug("Removed modelet details : " + modeletClientInfo.toString());
        LOGGER.debug("Removed modelet status :" + removed);
    }

    public double getModeletAvaiableTimeoutInSec() {
        return getTimeOut() / MILLISECONDS_FOR_SECOND;
    }

    private long getTimeOut() {
        if (systemParameterProvider.getParameter(TIME_OUT) != null) {
            timeout = Integer.parseInt(systemParameterProvider.getParameter(TIME_OUT));
        }
        return timeout;
    }

    public KeyValuePair<ModeletClientInfo, ModeletClient> getModeletClient(final TransactionCriteria transactionCriteria)
            throws SystemException {
        final ModeletClientInfo modeletClientInfo = poolManager.getModeletClientInfo(transactionCriteria, getTimeOut());
        final ModeletClient modeletClient = modeletClientInfo != null ? modeletHelper.buildModeletClient(modeletClientInfo)
                : null;
        return new KeyValuePair<ModeletClientInfo, ModeletClient>(modeletClientInfo, modeletClient);
    }

    public void getAllModeletInfo() throws SystemException {
        poolManager.getPoolObjectsLoader().loadPoolObjects();
        List<Pool> pools = poolManager.getPoolList();
        Collections.sort(pools);

        LOGGER.error("INFO:: ***** ALL MODELET MAP DETAILS *****");
        final IMap<String, ModeletClientInfo> allModeletMap = poolManager.getCacheRegistry().getMap(ALL_MODELET_MAP);
        final Set<String> hostKeySet = allModeletMap.keySet();
        if (!hostKeySet.isEmpty()) {
            for (final String hostKey : hostKeySet) {
                LOGGER.error("INFO:: Host Key is : {}, value is {}", hostKey, allModeletMap.get(hostKey).getString());
            }
        }

        LOGGER.error("INFO:: ***** HOST TO MEMBER MAP DETAILS *****");
        final IMap<String, String> hostToMemberMap = poolManager.getCacheRegistry().getMap(HOST_TO_MEMBER);
        final Set<String> hostKeySet1 = hostToMemberMap.keySet();
        if (!hostKeySet1.isEmpty()) {
            for (final String hostKey : hostKeySet1) {
                LOGGER.error("INFO:: Host Key is : {}, Member value is {}", hostKey, hostToMemberMap.get(hostKey));
            }
        }

        LOGGER.error("INFO:: ***** POOL Details *****");
        final List<Pool> poolList = poolManager.getPoolList();
        for (final Pool pool : poolList) {
            LOGGER.error("INFO:: " + pool.toString());
        }

        LOGGER.error("INFO:: ***** POOL CRITERIA Details *****");
        final IMap<String, String> criteriaPoolMap = poolManager.getCacheRegistry().getMap(CRITERA_POOL_MAP);
        for (final String criteria : criteriaPoolMap.keySet()) {
            LOGGER.error("INFO:: Pool {} = Criteria {}", criteriaPoolMap.get(criteria), criteria);
        }

        LOGGER.error("INFO:: ***** POOL USAGE ORDER Details *****");
        final IMap<Object, Object> poolMapByUsageOrderMap = poolManager.getCacheRegistry().getMap(POOL_USAGE_ORDER_MAP);
        for (final Object poolName : poolMapByUsageOrderMap.keySet()) {
            final TreeSet<PoolUsageOrderMapping> poolUsageOrderList = (TreeSet<PoolUsageOrderMapping>) poolMapByUsageOrderMap
                    .get(poolName.toString());
            for (final PoolUsageOrderMapping poolUsageOrderMapping : poolUsageOrderList) {
                LOGGER.error("INFO:: " + poolUsageOrderMapping.toString());
            }
        }

        LOGGER.error("INFO:: **** POOL, QUEUE, SIZE AND Modelet Client Details Details ***** ");
        if (CollectionUtils.isNotEmpty(pools)) {
            for (Pool pool : pools) {
                IQueue<Object> poolQueue = poolManager.getPoolQueue(pool.getPoolName());
                if (CollectionUtils.isNotEmpty(poolQueue)) {
                    Iterator<Object> modeletClients = poolQueue.iterator();
                    LOGGER.error("INFO:: Pool {} current size is : {}", pool.getPoolName(), poolQueue.size());
                    while (modeletClients.hasNext()) {
                        ModeletClientInfo modeletClientInfo = (ModeletClientInfo) modeletClients.next();
                        LOGGER.error("INFO:: #Modelet Info#. is {}", modeletClientInfo.getString());
                    }
                } else {
                    LOGGER.error("INFO:: Pool {} current size is : {}", pool.getPoolName(), 0);
                }
            }
        }

    }

    public List<PoolStatusStats> getPoolStatusStats(final List<String> poolNames) throws SystemException {
        return poolManager.getPoolStatusStats(poolNames);
    }

    public void setPoolManager(final PoolManager poolManager) {
        this.poolManager = poolManager;
    }

    public PoolManager getPoolManager() {
        return poolManager;
    }

}