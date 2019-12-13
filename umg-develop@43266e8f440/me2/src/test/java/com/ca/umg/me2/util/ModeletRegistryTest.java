package com.ca.umg.me2.util;

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

public class ModeletRegistryTest {

    private ModeletRegistry modeletRegistry;
    
    private PoolManager poolManager;

    private CacheRegistry cr = new DefaultCacheRegistry("hazelcast-config.xml");

	@Before
	public void setup() {
		modeletRegistry = new ModeletRegistry();
    	poolManager = new PoolManagerImpl();
    	modeletRegistry.setPoolManager(poolManager);
    	cr.getHazelcastInstance();
    	poolManager.setCacheRegistr(cr);
		createPool(poolManager);
		createPoolCriteia(poolManager);
		createPoolUsageOrder(poolManager);
	}

	
    @Test
    public void testGet() throws Exception {
        ModeletClientInfo modeletClientInfo = new ModeletClientInfo();
        modeletClientInfo.setHost("localhost");
        modeletClientInfo.setPort(8000);
        modeletClientInfo.setContextPath("");
        modeletClientInfo.setServerType(ServerType.HTTP.getServerType());
        
        modeletClientInfo.setPoolName("temp");

        modeletRegistry.addModeletPoolManager(modeletClientInfo);
        // assertNotNull(modeletRegistry.getModeletClient(CRITERIA));
    }

    @Test
    public void testAdd() throws Exception {
        ModeletClientInfo modeletClientInfo = new ModeletClientInfo();
        modeletClientInfo.setHost("localhost");
        modeletClientInfo.setPort(8000);
        modeletClientInfo.setContextPath("");
        modeletClientInfo.setServerType(ServerType.HTTP.getServerType());
        modeletClientInfo.setPoolName("temp");

        modeletRegistry.addModeletPoolManager(modeletClientInfo);
        // assertNotNull(modeletRegistry.getModeletClient(CRITERIA));
    }

    @Test
    public void testRemove() throws Exception {
        ModeletClientInfo modeletClientInfo = new ModeletClientInfo();
        modeletClientInfo.setHost("localhost");
        modeletClientInfo.setPort(8000);
        modeletClientInfo.setContextPath("");
        modeletClientInfo.setServerType(ServerType.HTTP.getServerType());
        modeletClientInfo.setPoolName("temp");
        modeletRegistry.addModeletPoolManager(modeletClientInfo);
        modeletRegistry.removeModeletFromPoolManager(modeletClientInfo);
    }
}