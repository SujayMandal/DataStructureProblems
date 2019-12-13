/**
 * 
 */
package com.ca.umg.me2.listener;

import static com.ca.umg.me2.pool.PoolManagerCreator.createPool;
import static com.ca.umg.me2.pool.PoolManagerCreator.createPoolCriteia;
import static com.ca.umg.me2.pool.PoolManagerCreator.createPoolUsageOrder;

import org.junit.Before;
import org.junit.Test;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.cache.registry.impl.DefaultCacheRegistry;
import com.ca.modelet.ModeletClientInfo;
import com.ca.modelet.common.ServerType;
import com.ca.pool.PoolManager;
import com.ca.pool.PoolManagerImpl;
import com.hazelcast.core.IQueue;

public class ModeletRegistrationListenerTest {

    private CacheRegistry cr = new DefaultCacheRegistry("hazelcast-config.xml");

    /**
     * Test method for
     * {@link com.ca.umg.me2.util.ModeletRegistrationListener#entryAdded(com.hazelcast.core.EntryEvent)}
     * .
     * 
     * @throws Exception
     */
    
    private PoolManager poolManager;

	@Before
	public void setup() {
    	poolManager = new PoolManagerImpl();
    	poolManager.setCacheRegistr(cr);
		cr.getHazelcastInstance();
		createPool(poolManager);
		createPoolCriteia(poolManager);
		createPoolUsageOrder(poolManager);
	}
    
    @Test
    public final void testEntryAdded() throws Exception {
        IQueue<Object> cache = cr.getDistributedQueue();
        ModeletClientInfo modeletClientInfo = new ModeletClientInfo();
        modeletClientInfo.setHost("localhost");
        modeletClientInfo.setPort(8000);
        modeletClientInfo.setContextPath("");
        modeletClientInfo.setServerType(ServerType.HTTP.getServerType());
        cache.add(modeletClientInfo);
        // add one second delay to allow listener to update the registry
        Thread.sleep(1000);
        // KeyValuePair<ModeletClientInfo, ModeletClient> modeletFromQueue = modeleRegistry.getModeletClient(CRITERIA);
        /*
         * assertNotNull(modeletFromQueue); assertNotNull(modeletFromQueue.getKey()); assertNotNull(modeletFromQueue.getValue());
         * assertEquals(modeletClientInfo, modeletFromQueue.getKey());
         */
    }
}