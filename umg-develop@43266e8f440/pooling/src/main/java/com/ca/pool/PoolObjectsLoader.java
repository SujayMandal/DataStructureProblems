package com.ca.pool;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.exception.SystemException;
import com.ca.modelet.ModeletClientInfo;
import com.ca.pool.model.Pool;
import com.ca.pool.model.PoolCriteria;
import com.ca.pool.model.PoolCriteriaDefMapping;
import com.ca.pool.model.PoolCriteriaDetails;
import com.ca.pool.model.PoolUsageOrderMapping;
import com.ca.pool.model.TransactionCriteria;
import com.hazelcast.core.IQueue;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public interface PoolObjectsLoader {

	public void createPoolQueue(List<Pool> poolList);
	
	public void createPoolQueue(final String poolName);

	public void createPoolMap(List<Pool> poolList);
	
	public void createPoolMap(final Pool pool);

	public void createPoolCriteria(List<PoolCriteriaDefMapping> poolCriteriaDefMappingList) throws SystemException;

	public void createPoolUsageOrder(List<PoolUsageOrderMapping> poolUsageOrderMappingList);

	public List<Pool> getPoolList();

	public IQueue<Object> getPoolQueue(String poolName) throws SystemException;

	public List<PoolCriteria> getPoolCriteriaList();

	public void updatePoolStatus(String poolName, String batchStatus);

	public void createNewPool(Pool pool);

	public Pool getPoolByCriteria(final TransactionCriteria transactionCriteria) throws SystemException;
	
	public void loadPoolObjects() throws SystemException;
	
	public String getPoolCriteria(final String poolName);
	
	public TreeSet<PoolUsageOrderMapping> getPoolUsageOrderList(final String poolName) throws SystemException;
	
	public PoolCriteriaDetails getPoolCriteriaDetails(final String poolName);
	
	public List<ModeletClientInfo> getModeletClientInfo(final String poolName);

	List<ModeletClientInfo> getAllModeletClientInfo();
	
	public CacheRegistry getCacheRegistry();

    public List<ModeletClientInfo> getInactiveModelets();

    public List<Map<String, Object>> getActiveAndInactiveModeletClients();
}