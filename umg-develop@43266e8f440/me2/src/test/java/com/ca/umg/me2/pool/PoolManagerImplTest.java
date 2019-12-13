package com.ca.umg.me2.pool;

//import static com.ca.umg.me2.pool.PoolManagerCreator.CRITERIA;
import static com.ca.umg.me2.pool.PoolManagerCreator.ONLINE_POOL;
import static com.ca.umg.me2.pool.PoolManagerCreator.createModeletClientInfo;
import static com.ca.umg.me2.pool.PoolManagerCreator.createPool;
import static com.ca.umg.me2.pool.PoolManagerCreator.createPoolCriteia;
import static com.ca.umg.me2.pool.PoolManagerCreator.createPoolUsageOrder;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.cache.registry.impl.DefaultCacheRegistry;
import com.ca.framework.core.exception.SystemException;
import com.ca.modelet.ModeletClientInfo;
import com.ca.pool.PoolManager;
import com.ca.pool.PoolManagerImpl;
import com.ca.pool.model.Pool;
import com.hazelcast.core.IQueue;

public class PoolManagerImplTest {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(PoolManagerImplTest.class);

	private PoolManager poolManager;
	private ModeletClientInfo modeletClientInfo;
	
	
	private CacheRegistry cr = new DefaultCacheRegistry("hazelcast-config.xml");
	
	@Before
	public void setup() {
		modeletClientInfo = createModeletClientInfo();
		poolManager = new PoolManagerImpl();
		poolManager.setCacheRegistr(cr);
		cr.getHazelcastInstance();
		createPool(poolManager);
		createPoolCriteia(poolManager);
		createPoolUsageOrder(poolManager);
	}
	
	
	@Test
	public void testGetPoolList() {
		final List<Pool> poolList = poolManager.getPoolList();
		assertTrue(poolList != null);
//		assertTrue(poolList.size() == 1);
//		assertTrue(poolList.get(0).getId().equals(ID));
//		assertTrue(poolList.get(0).getPoolName().equals(ONLINE_POOL));
	}

	@Test
	public void testAddModeletToPoolQueue() {
		try {
	        poolManager.addModeletToPoolQueue(modeletClientInfo);
	        
	        final IQueue<Object> poolQueue = poolManager.getPoolQueue(modeletClientInfo.getPoolName());
	        assertTrue(poolQueue != null);
	        assertTrue(poolQueue.size() == 1);
	        final ModeletClientInfo modeletClientInfofromQueue = (ModeletClientInfo) poolQueue.take();
	        assertTrue(modeletClientInfofromQueue != null);
	        assertTrue(modeletClientInfofromQueue.equals(modeletClientInfo));
        } catch (SystemException e) {
	        e.printStackTrace();
        } catch (InterruptedException ie) {
        	LOGGER.error("InterruptedException: {}",ie);
        }
	}
	
	@Test
	public void testRemoveModeletFromPoolQueue() {
		try {
	        poolManager.removeModeletFromPoolQueue(modeletClientInfo);
	        
	        final IQueue<Object> poolQueue = poolManager.getPoolQueue(modeletClientInfo.getPoolName());
	        assertTrue(poolQueue != null);
	        assertTrue(poolQueue.size() == 0);
	        
        } catch (SystemException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
	}
	
	@Test
	public void testGetPoolQueue() {
		try {
	        final IQueue<Object> onlinePool = poolManager.getPoolQueue(ONLINE_POOL);
	        assertTrue(onlinePool != null);
        } catch (SystemException e) {
	        e.printStackTrace();
        }
	}
}