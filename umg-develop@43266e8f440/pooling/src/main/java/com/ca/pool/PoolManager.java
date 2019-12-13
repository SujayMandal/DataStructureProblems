package com.ca.pool;

import java.util.List;
import java.util.Map;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.rmodel.dao.RModelDAO;
import com.ca.framework.core.rmodel.info.SupportPackage;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.modelet.ModeletClientInfo;
import com.ca.pool.manager.ModeletManager;
import com.ca.pool.model.Pool;
import com.ca.pool.model.PoolCriteria;
import com.ca.pool.model.PoolStatusStats;
import com.ca.pool.model.TransactionCriteria;
import com.hazelcast.core.IQueue;

public interface PoolManager {

    public IQueue<Object> getPoolQueue(final String poolName) throws SystemException;

    public List<Pool> getPoolList();

    public List<PoolCriteria> getPoolCriteriaList();

    public Pool getPoolByCriteria(final TransactionCriteria transactionCriteria) throws SystemException;

    public abstract boolean addModeletToPoolQueue(final ModeletClientInfo modeletClientInfo) throws SystemException;

    public abstract boolean removeModeletFromPoolQueue(final ModeletClientInfo modeletClientInfo) throws SystemException;

    public abstract ModeletClientInfo getModeletClientInfo(final TransactionCriteria transactionCriteria, final long timeOut)
            throws SystemException;

    public void updatePoolStatus(final String poolName, final String batchStatus);

    public boolean isPoolModeletsBusy(final String poolName);

    public boolean canPoolBeUsed(final String poolName);

    public PoolStatusStats getPoolStatusStats(final String poolName) throws SystemException;

    public List<PoolStatusStats> getPoolStatusStats(final List<String> poolNames) throws SystemException;

    public void createNewPool(final Pool pool);

    public void deletePool(final Pool pool);

    public RModelDAO getRModelDAO();

    public void setRModelDAO(RModelDAO rModelDAO);

    public Map<String, String> getModelPackageName(final String modelName, final Integer majorVersion, final Integer minorVersion,
            final String tenantCode);

    public List<SupportPackage> getSupportPackageList(final String modelName, final Integer majorVersion,
            final Integer minorVersion, final String tenantCode);

    public CacheRegistry getCacheRegistry();

    public PoolObjectsLoader getPoolObjectsLoader();

    public void setCacheRegistr(final CacheRegistry cr);

    public String getEnvDefaultPoolName(final String execLanguage, String execEnvironment) throws SystemException;

    public KeyValuePair<String, Integer> getModeletPoolAndCount(TransactionCriteria transactionCriteria) throws SystemException;

    public void addModeletToRestartMapStatus(final ModeletClientInfo modeletClientInfo);

    public void removeModeletFromRestartMapStatus(final ModeletClientInfo modeletClientInfo);

    public boolean isModeletInRestartMapStatus(final ModeletClientInfo modeletClientInfo);

    public void addModeletToRestartMap(final ModeletClientInfo modeletClientInfo);

    public ModeletClientInfo getModeletFromRestartMap(final ModeletClientInfo modeletClientInfo);

    public void removeModeletFromRestartMap(final ModeletClientInfo modeletClientInfo);

    public long getRetryCount();

    public ModeletManager getModeletManager();

    public Pool getPoolDetail(String poolName);
    public boolean removeModeletClientFromPoolQueue(final IQueue<Object> pool, final ModeletClientInfo clientInfo);

}
