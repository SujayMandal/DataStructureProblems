package com.ca.umg.business.pooling.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.PoolConstants;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;

/**
 * 
 * Pooling Helper
 * 
 * @author mandasuj
 *
 */
public final class ModeletPoolingHelper {

    private ModeletPoolingHelper() {
    }

    /**
     * create pooldQueueMap from POOL_MAP
     * 
     * @param CacheRegistry
     * @return Map<Object, Object>
     */
    public static Map<Object, Object> createPooldQueueMap(CacheRegistry cacheRegistry) {
        final IMap<Object, Object> poolMap = cacheRegistry.getMap(PoolConstants.POOL_MAP);
        Map<Object, Object> pooldQueueMap = new HashMap<>();
        if (poolMap != null) {
            final Set<Object> keySet = poolMap.keySet();
            for (Object key : keySet) {
                final IQueue<Object> poolQueue = cacheRegistry.getDistributedPoolQueue((String) key);
                if (CollectionUtils.isNotEmpty(poolQueue)) {
                    Iterator<Object> modeletClients = poolQueue.iterator();
                    List<Object> modeletClientsList = new ArrayList<>();
                    while (modeletClients.hasNext()) {
                        modeletClientsList.add(modeletClients.next());
                    }
                    pooldQueueMap.put(key, modeletClientsList);
                }
            }
        }
        return pooldQueueMap;
    }

}
