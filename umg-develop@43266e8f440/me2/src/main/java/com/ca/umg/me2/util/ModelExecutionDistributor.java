/**
 * 
 */
package com.ca.umg.me2.util;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.PoolConstants;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.modelet.ModeletClientInfo;
import com.ca.modelet.client.ModeletClient;
import com.ca.pool.model.PoolStatusStats;
import com.ca.pool.model.TransactionCriteria;

/**
 * @author kamathan
 *
 */
@Named
public class ModelExecutionDistributor {

    @Inject
    private ModeletRegistry modeletRegistry;
    
    @Inject
    private CacheRegistry cacheRegistry;

    public KeyValuePair<ModeletClientInfo, ModeletClient> getAvailableFreeModelet(final TransactionCriteria transactionCriteria)
            throws SystemException {
        return modeletRegistry.getModeletClient(transactionCriteria);
    }

    public void addModeletBackToRegistry(final ModeletClientInfo modeletClientInfo) throws SystemException {
        if (modeletClientInfo != null) {
            modeletRegistry.addModeletPoolManager(modeletClientInfo);
            // Updating RA_SYSTEM_MODELETS & ALL_MODELET_MAP
            cacheRegistry.getMap(PoolConstants.ALL_MODELET_MAP).put(modeletClientInfo.getHostKey(), modeletClientInfo);
            cacheRegistry.getMap(PoolConstants.RA_SYSTEM_MODELETS).put(modeletClientInfo.getHostKey(), modeletClientInfo);
        }
    }

    public void removeModeletFromPoolManager(final ModeletClientInfo modeletClientInfo) throws SystemException {
        if (modeletClientInfo != null) {
            modeletRegistry.removeModeletFromPoolManager(modeletClientInfo);
        }
    }

    public double getModeletAvaiableTimeoutInSec() {
        return modeletRegistry.getModeletAvaiableTimeoutInSec();
    }

    public void getAllModeletinfo() throws SystemException {
        modeletRegistry.getAllModeletInfo();
    }

    public List<PoolStatusStats> getPoolStatusStats(final List<String> poolNames) throws SystemException {
        return modeletRegistry.getPoolStatusStats(poolNames);
    }
}