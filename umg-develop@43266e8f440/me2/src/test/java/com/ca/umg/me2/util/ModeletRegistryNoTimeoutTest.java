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

public class ModeletRegistryNoTimeoutTest {

    private CacheRegistry cr = new DefaultCacheRegistry("hazelcast-config.xml");
    
    private static final String SYSTEM_PARAMETER = "SYSTEM_PARAMETER_MAP";

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
    public void testGet() throws Exception {

        ModeletClientInfo modeletClientInfo = new ModeletClientInfo();
        modeletClientInfo.setHost("localhost");
        modeletClientInfo.setPort(8000);
        modeletClientInfo.setContextPath("");
        modeletClientInfo.setServerType(ServerType.SOCKET.getServerType());
        cr.getMap(SYSTEM_PARAMETER).put(ModelExecConstants.TIME_OUT,"0");
    }
}