package com.ca.umg.me2.pool;

import java.util.ArrayList;
import java.util.List;

import com.ca.modelet.ModeletClientInfo;
import com.ca.pool.PoolManager;
import com.ca.pool.model.Pool;
import com.ca.pool.model.PoolCriteriaDefMapping;
import com.ca.pool.model.PoolUsageOrderMapping;


public class PoolManagerCreator {
	
	public static final String CRITERIA = "TENANT = localhost AND ENVIRONMENT = MATLAB AND ENVIRONMENT_VERSION = 7.16 AND TRANSACTION_TYPE = ONLINE";
	public static final String ID = "1";
	public static final String ONLINE_POOL = "ONLINE_POOL";
	
	public static ModeletClientInfo createModeletClientInfo() {
		final ModeletClientInfo modeletClientInfo = new ModeletClientInfo();
		modeletClientInfo.setHost("localhost");
		modeletClientInfo.setPort(1234);
		modeletClientInfo.setPoolName(ONLINE_POOL);
		
		return modeletClientInfo;
	}
	
	public static void createPool(final PoolManager poolManager) {
		final List<Pool> poolList = new ArrayList<>();
		
		final Pool onlinePool = new Pool();
		onlinePool.setId(ID);
		onlinePool.setPoolName(ONLINE_POOL);
        // onlinePool.setBatchPool(false);
		
		poolList.add(onlinePool);
		
        // poolManager.createPoolQueue(poolList);
	}
	
	public static  void createPoolCriteia(final PoolManager poolManager) {
		final List<PoolCriteriaDefMapping> poolCriteriaDefMappingList = new ArrayList<>();
		
		final PoolCriteriaDefMapping poolCriteriaDefMapping = new PoolCriteriaDefMapping();
		poolCriteriaDefMapping.setId(ID);
		poolCriteriaDefMapping.setPoolId(ID);
		poolCriteriaDefMapping.setPoolName(ONLINE_POOL);
		poolCriteriaDefMapping.setPoolCriteriaValue(CRITERIA);
		
		poolCriteriaDefMappingList.add(poolCriteriaDefMapping);
		
        // poolManager.createPoolCriteia(poolCriteriaDefMappingList);
	}
	
	public static void createPoolUsageOrder(final PoolManager poolManager) {
		final List<PoolUsageOrderMapping> poolUsageOrderMappingList = new ArrayList<>();
		
		final PoolUsageOrderMapping usageOder = new PoolUsageOrderMapping();
		usageOder.setId(ID);
		usageOder.setPoolId(ID);
		usageOder.setPoolName(ONLINE_POOL);
		usageOder.setPoolTryOrder(1);
		usageOder.setPoolUsageId(ID);
		usageOder.setPoolUsageName(ONLINE_POOL);
		
		poolUsageOrderMappingList.add(usageOder);		
		
        // poolManager.createPoolUsageOrder(poolUsageOrderMappingList);
	}
	
}
