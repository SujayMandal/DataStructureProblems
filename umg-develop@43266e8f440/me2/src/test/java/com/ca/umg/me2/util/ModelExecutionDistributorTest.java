package com.ca.umg.me2.util;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.cache.registry.impl.DefaultCacheRegistry;
import com.ca.modelet.ModeletClientInfo;
import com.ca.modelet.common.ServerType;
import com.ca.pool.PoolManager;
import com.ca.pool.PoolManagerImpl;
import com.ca.umg.me2.bo.ModelExecutorBOImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.ca.umg.me2.pool.PoolManagerCreator.*;

public class ModelExecutionDistributorTest {

	@InjectMocks
	private ModelExecutorBOImpl modelExecutorBO;
	
	@Mock
	private ModelExecutionDistributor modeletExecutionDistributor;
	
    private ModeletRegistry modeletRegistry = new ModeletRegistry();
    
    private PoolManager poolManager;
    
    private CacheRegistry cr = new DefaultCacheRegistry("hazelcast-config.xml");

    @Before
    public void setup() throws Exception {
    	MockitoAnnotations.initMocks(this);
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
    public final void testAddModelet() throws Exception {
    	ModeletClientInfo modeletClientInfo = new ModeletClientInfo();
        modeletClientInfo.setHost("localhost");
        modeletClientInfo.setPort(8000);
        modeletClientInfo.setContextPath("");
        modeletClientInfo.setServerType(ServerType.HTTP.getServerType());
        
        modeletClientInfo.setPoolName("temp");
        modeletExecutionDistributor.addModeletBackToRegistry(modeletClientInfo);
        modeletClientInfo = new ModeletClientInfo();
        modeletExecutionDistributor.addModeletBackToRegistry(modeletClientInfo);
    }
}