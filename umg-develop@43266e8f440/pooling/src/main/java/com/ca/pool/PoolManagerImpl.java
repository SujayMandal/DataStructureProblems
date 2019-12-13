package com.ca.pool;

import static com.ca.framework.core.batch.TransactionStatus.IN_PROGRESS;
import static com.ca.framework.core.constants.PoolConstants.ALL_MODELET_MAP;
import static com.ca.framework.core.constants.PoolConstants.MODELET_RESTART_MAP;
import static com.ca.framework.core.constants.PoolConstants.MODELET_RESTART_MAP_STATUS;
import static com.ca.framework.core.constants.PoolConstants.NUMBER_ZERO;
import static com.ca.framework.core.constants.PoolConstants.POOL_MAP;
import static com.ca.framework.core.constants.PoolConstants.RETRY_COUNT;
import static com.ca.framework.core.exception.SystemException.newSystemException;
import static com.ca.framework.core.exception.codes.FrameworkExceptionCodes.RSE0000507;
import static com.ca.framework.core.systemparameter.SystemParameterConstants.MODELET_EXEC_TIME_LIMIT;
import static com.ca.framework.core.systemparameter.SystemParameterProvider.SYSTEM_PARAMETER;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.constants.PoolConstants;
import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.entity.ModeletRestartInfo;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.publishing.status.constants.PublishingStatus;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.rmodel.dao.RModelDAO;
import com.ca.framework.core.rmodel.info.SupportPackage;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.me2.listener.ClientRegistrationListener;
import com.ca.modelet.ModeletClientInfo;
import com.ca.pool.manager.ModeletManager;
import com.ca.pool.model.DefaultPool;
import com.ca.pool.model.ExecutionEnvironment;
import com.ca.pool.model.ExecutionLanguage;
import com.ca.pool.model.Pool;
import com.ca.pool.model.PoolCriteria;
import com.ca.pool.model.PoolStatusStats;
import com.ca.pool.model.PoolUsageOrderMapping;
import com.ca.pool.model.TransactionCriteria;
import com.ca.pool.util.ModeletRegistrationEvent;
import com.ca.systemmodelet.SystemModeletConfig;
import com.ca.umg.notification.model.NotificationHeaderEnum;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;

@SuppressWarnings("PMD")
public class PoolManagerImpl implements PoolManager {

    private static final Logger LOGGER = getLogger(PoolManagerImpl.class);

    private static final String R_LINUX_DEFAULT = "R-Linux-Default";

    private static final String R_WINDOWS_DEFAULT = "R-Windows-Default";

    private static final String MATLAB_LINUX_DEFAULT = "Matlab-Linux-Default";

    private static final String EXCEL_WINDOWS_DEFAULT = "Excel-Windows-Default";

    private RModelDAO rModelDAO;

    @Autowired
    private CacheRegistry cacheRegistry;

    @Autowired
    private PoolObjectsLoader poolObjectsLoader;

    @Inject
    private ModeletManager modeletManager;

    @Inject
    private SystemParameterProvider systemParameterProvider;

    private SystemModeletConfig systemModeletConfig;

    private int retryCount = 2;

    public SystemModeletConfig getSystemModeletConfig() {
        return systemModeletConfig;
    }

    public void setSystemModeletConfig(SystemModeletConfig systemModeletConfig) {
        this.systemModeletConfig = systemModeletConfig;
    }

    @PostConstruct
    private void startPoolManager() throws SystemException {
        LOGGER.info("Allocate Modelets Process started, this is initiated from ME2 start up");

        // TODO : Do we need to remove already registered Membership Listeners, if yes, then, we need to keep track of
        // registration Id in Hazel cast and remove then before adding new one
        /*
         * cacheRegistry.getHazelcastInstance().getCluster() .addMembershipListener(new MemberRegistrationListener(this,
         * systemModeletConfig));
         */
        cacheRegistry.getHazelcastInstance().getClientService()
                .addClientListener(new ClientRegistrationListener(this, systemModeletConfig));

        // ModeletAllocationAlgorithm.allocateModelets(this, modeletManager);
        // createSystemModeletConfig();

        LOGGER.info("Allocate Modelets Process is done");

    }

    @Override
    public List<Pool> getPoolList() {
        final List<Pool> poolList = new ArrayList<Pool>();
        final IMap<Object, Object> poolMap = cacheRegistry.getMap(POOL_MAP);
        final Set<Object> keySet = poolMap.keySet();

        for (Object key : keySet) {
            final Object poolFromMap = poolMap.get(key);
            if (poolFromMap != null) {
                poolList.add((Pool) poolFromMap);
            }
        }
        return poolList;
    }

    @Override
    public IQueue<Object> getPoolQueue(final String poolName) throws SystemException {
        final IQueue<Object> poolQueue = cacheRegistry.getDistributedPoolQueue(poolName);
        if (poolQueue == null) {
            LOGGER.error("ERROR:: Pool Queue is not found for the pool name. Pool Name is {}", poolName);
            throw newSystemException("MSE0000003", null);
        }
        return poolQueue;
    }

    @Override
    public List<PoolCriteria> getPoolCriteriaList() {
        return poolObjectsLoader.getPoolCriteriaList();
    }

    @Override
    public boolean addModeletToPoolQueue(final ModeletClientInfo modeletClientInfo) throws SystemException {
        boolean status = false;
        if (modeletClientInfo != null) {
            final IQueue<Object> poolQueue = getPoolQueue(modeletClientInfo.getPoolName());
            LOGGER.error("{} pool size before adding is {}", modeletClientInfo.getPoolName(), poolQueue.size());
            status = poolQueue.add(modeletClientInfo);
            if (status) {
                if (StringUtils.equalsIgnoreCase(modeletClientInfo.getRequestMode(), TransactionMode.BULK.getMode())) {
                    LOGGER.error("Raise Modelet Registration Event for Scheduler, so that scheduler can send another request");
                    // raise an event to cluster on modelet availability
                    ModeletRegistrationEvent modeletRegistrationEvent = new ModeletRegistrationEvent();
                    modeletRegistrationEvent.setModelName(modeletClientInfo.getLoadedModel());
                    modeletRegistrationEvent
                            .setMajorVersion(StringUtils.substringBefore(modeletClientInfo.getLoadedModelVersion(), "\\."));
                    modeletRegistrationEvent.setTenantCode(modeletClientInfo.getTenantCode());
                    cacheRegistry.getTopic(FrameworkConstant.MODELET_REG_LISTENER_EVENT).publish(modeletRegistrationEvent);
                }
                LOGGER.info("{} pool is added with another modelet {}", modeletClientInfo.getPoolName(),
                        modeletClientInfo.getString());
            } else {
                LOGGER.error("ERROR:: {} pool is NOT added with another modelet {}", modeletClientInfo.getPoolName(),
                        modeletClientInfo.getString());
            }
            LOGGER.info("{} pool size after adding is {}", modeletClientInfo.getPoolName(), poolQueue.size());
        } else {
            LOGGER.error("ERROR:: Modellet Client Info is null, may be Modelet is not started properly");
        }
        return status;
    }

    // TODO: do we really need to remove Modelet from cache? I think it is not required
    @Override
    public boolean removeModeletFromPoolQueue(final ModeletClientInfo modeletClientInfo) throws SystemException {
        boolean status = false;
        if (modeletClientInfo != null) {
            final IQueue<Object> poolQueue = getPoolQueue(modeletClientInfo.getPoolName());
            LOGGER.info("{} pool size before removing is {}", modeletClientInfo.getPoolName(), poolQueue.size());
        	status = removeModeletClientFromPoolQueue(poolQueue, modeletClientInfo);
        	LOGGER.info("{} pool size after removing is {}", modeletClientInfo.getPoolName(), poolQueue.size());
            final IMap<String, ModeletClientInfo> allModeletMap = cacheRegistry.getMap(ALL_MODELET_MAP);
            final Map<String, String> hostToMember = cacheRegistry.getMap(PoolConstants.HOST_TO_MEMBER);
            modeletClientInfo.setModeletStatus(ModeletStatus.UNREGISTERED.getStatus());

            allModeletMap.remove(modeletClientInfo.getHostKey());
            hostToMember.remove(modeletClientInfo.getHostKey());

            LOGGER.info("Modelet is removed from All Modelet Map, and HostToMember Map. Remvoed Modelet is {}",
                    modeletClientInfo.getString());
            LOGGER.info("Modelet unregistration is successfull, Modelet is {}", modeletClientInfo.getString());
        } else {
            LOGGER.error("ERROR:: Modellet Client Info is null, may be Modelet is not started properly");
        }
        return status;
    }
    @Override
    public boolean removeModeletClientFromPoolQueue(final IQueue<Object> pool, final ModeletClientInfo clientInfo) {
    	boolean presenceFlag = false; 
    	if (CollectionUtils.isNotEmpty(pool)) {
            Iterator<Object> clientInfos = pool.iterator();
            while (clientInfos.hasNext()) {
                ModeletClientInfo modeletClientInfo = (ModeletClientInfo) clientInfos.next();
                if(StringUtils.equalsIgnoreCase(modeletClientInfo.getHostKey(), clientInfo.getHostKey())) {
                     pool.remove(modeletClientInfo);
                     presenceFlag = true;
                     break;
                }
            }
        }
    	if (presenceFlag) {
    		LOGGER.debug("Existing modelet has been removed successfully from pool {}, Deleted Modelet is : {} ", pool.getName(),
                    clientInfo.getLogMessage());
            LOGGER.debug("Now {} pool size is {} after deleting", pool.getName(), pool.size());
        } else {
            LOGGER.debug(
                    "Modelet is not avaiable in Pool to remove. hecnce not able to removed. Pool is : {}, Tried to delete Modelet is : {}",
                    pool.getName(), clientInfo.getLogMessage());
        }
    	return presenceFlag;
    }

    @Override
    public ModeletClientInfo getModeletClientInfo(final TransactionCriteria transactionCriteria, final long timeOut)
            throws SystemException {
        final Pool pool = getPoolByCriteria(transactionCriteria);
        final String poolName = pool.getPoolName();
        final Integer poolWaitTimeout = pool.getWaitTimeout();
        ModeletClientInfo modeletClientInfo = null;
        final TreeSet<PoolUsageOrderMapping> poolMapByUsageOrderist = poolObjectsLoader.getPoolUsageOrderList(poolName);
        final long startTime = currentTimeMillis();
        try {
            for (final PoolUsageOrderMapping usagePoolOrder : poolMapByUsageOrderist) {
                final String queueName = usagePoolOrder.getPoolUsageName();
                if (isItSamePool(usagePoolOrder) || canPoolBeUsed(queueName)) {
                    final IQueue<Object> poolQueue = getPoolQueue(queueName);
                    LOGGER.info("Current Size of the pool {} is before taking a modelet {}.", queueName, poolQueue.size());

                    if (poolQueue.size() > 0) {
                        modeletClientInfo = (ModeletClientInfo) poolQueue.poll(NUMBER_ZERO, MILLISECONDS);
                    }

                    if (modeletClientInfo != null) {
                        LOGGER.error("Taken Modelelt from {} pool is {}", queueName, modeletClientInfo.getString());
                        if(transactionCriteria.getClientID() != null && ! StringUtils.isEmpty(transactionCriteria.getClientID())){
                            cacheRegistry.getTopic(PublishingStatus.MODELET_FOUND).publish(transactionCriteria.getClientID() + "@" + PublishingStatus.MODELET_FOUND);
                           }
                    } else {
                        LOGGER.error("Modelet is not available in {} pool, and trying in another pool", queueName);
                    }

                    if (modeletClientInfo != null) {
                        LOGGER.info("Current Size of the pool {} is after taking a modelet {}.", queueName, poolQueue.size());
                        break;
                    }
                }
            }

            if (modeletClientInfo != null) {
                LOGGER.info("Modelet Info : " + modeletClientInfo);
            } else {
                LOGGER.error(
                        "Modelet Info is null, may be traing to get Modelet from other pool based on usage order, or trying again after some delay, "
                                + "based on retry count set for the system");
            }

            // getting Modelet from actual pool with wait time once all tried on all pool usage list
            if (modeletClientInfo == null) {
                final IQueue<Object> poolQueue = getPoolQueue(poolName);
                final long endTime = currentTimeMillis();
                LOGGER.error("Current Size of the pool {} is before taking a modelet {}.", poolName, poolQueue.size());
                if (poolWaitTimeout != null && poolWaitTimeout > NUMBER_ZERO) {
                    modeletClientInfo = (ModeletClientInfo) poolQueue.poll(actualME2TimeOut(startTime, endTime, poolWaitTimeout),
                            MILLISECONDS);
                } else if (timeOut > NUMBER_ZERO) {
                    modeletClientInfo = (ModeletClientInfo) poolQueue.poll(actualME2TimeOut(startTime, endTime, timeOut),
                            MILLISECONDS);
                } else {
                    modeletClientInfo = (ModeletClientInfo) poolQueue.take();
                }
            }

            // verify if the model execution request is same as loaded in the modelet else restart the current modelet and try to
            // get new modelet from the pool
            // also added the or condition (transactionCriteria.getIsVersionCreationTest) at last for umg-4020 and umg-4251
            // version reset to restart
            // modelet if the request is for model publish test
            if ((modeletClientInfo != null
                    && StringUtils.equalsIgnoreCase(modeletClientInfo.getExecutionLanguage(), ExecutionLanguage.R.getValue())
                    && StringUtils.isNotBlank(modeletClientInfo.getLoadedModel())
                    && (!StringUtils.equalsIgnoreCase(modeletClientInfo.getLoadedModel(), transactionCriteria.getModelName())
                            || !StringUtils.equalsIgnoreCase(modeletClientInfo.getLoadedModelVersion(),
                                    transactionCriteria.getModelVersion())))
                    || (modeletClientInfo != null && transactionCriteria.getIsVersionCreationTest()
                            && StringUtils.isNotBlank(modeletClientInfo.getLoadedModel()) && StringUtils.equalsIgnoreCase(
                                    modeletClientInfo.getExecutionLanguage(), ExecutionLanguage.R.getValue()))) {
                LOGGER.error(
                        "Modelet will be restarting because, Modelet is loaded with some model which is not same as it's allocated pool criteria model, "
                                + "this is happening during execution of transaction");
                LOGGER.error("Modelet which is going to restarted is : {}", modeletClientInfo);
                LOGGER.error("Loaded Model is {}, and version is {}", modeletClientInfo.getLoadedModel(),
                        modeletClientInfo.getLoadedModelVersion());
                LOGGER.error("Pool Criteria Model is {}, and version is {}", transactionCriteria.getModelName(),
                        transactionCriteria.getModelVersion());
                Map<String, String> modeletRestartInfoMap = createInfo(transactionCriteria, modeletClientInfo.getLoadedModel(),
                        modeletClientInfo.getLoadedModelVersion());

                ModeletRestartInfo modeletRestartInfo = modeletManager.getRestartAndExecCount(modeletClientInfo);
                if (modeletRestartInfo != null) {
                    modeletRestartInfoMap.put(NotificationHeaderEnum.EXEC_COUNT.getHeaderName(),
                            String.valueOf(modeletRestartInfo.getExecCount()));
                    modeletRestartInfoMap.put(NotificationHeaderEnum.RESTART_COUNT.getHeaderName(),
                            String.valueOf(modeletRestartInfo.getRestartCount()));
                } else {
                    modeletRestartInfoMap.put(NotificationHeaderEnum.EXEC_COUNT.getHeaderName(),
                            ModeletManager.EXEC_RESTART_COUNT_NOT_PRESENT);
                    modeletRestartInfoMap.put(NotificationHeaderEnum.RESTART_COUNT.getHeaderName(),
                            ModeletManager.EXEC_RESTART_COUNT_NOT_PRESENT);
                }
                modeletManager.setExecCounttoZero(modeletClientInfo);
                modeletClientInfo.setLoadedModel(null);
                modeletClientInfo.setLoadedModelVersion(null);
                addModeletToRestartMapStatus(modeletClientInfo);
                modeletManager.restartModelet(modeletClientInfo, modeletRestartInfoMap);

                final String currentPoolName = modeletClientInfo.getPoolName();

                // final long transactionWaitTimeTimeForModeletToComeUp =
                // getTransactionWaitTimeForModeletToComeUp(currentPoolName);

                final long transactionWaitTimeTimeForModeletToComeUp = Long
                        .valueOf((String) cacheRegistry.getMap(SYSTEM_PARAMETER).get(MODELET_EXEC_TIME_LIMIT)) * 1000;

                final long startWaitTime = currentTimeMillis();

                final ModeletClientInfo oldModeletClientInfo = modeletClientInfo;

                modeletClientInfo = null;

                while (true) {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        LOGGER.error("An error occurred while waiting for restarting the modelet.", e);
                        removeModeletFromRestartMapStatus(oldModeletClientInfo);
                        SystemException.newSystemException(RSE0000507, new Object[] { e.getLocalizedMessage() });
                    }

                    modeletClientInfo = getModeletFromRestartMap(oldModeletClientInfo);
                    if (modeletClientInfo != null) {
                        modeletClientInfo.setPoolName(currentPoolName);

                        final IMap<String, ModeletClientInfo> allModeletMap = getCacheRegistry().getMap(ALL_MODELET_MAP);
                        allModeletMap.put(modeletClientInfo.getHostKey(), modeletClientInfo);

                        removeModeletFromRestartMapStatus(modeletClientInfo);
                        removeModeletFromRestartMap(modeletClientInfo);
                        break;
                    }

                    final long endWaitTime = currentTimeMillis();
                    if (transactionWaitTimeTimeForModeletToComeUp < (endWaitTime - startWaitTime)) {
                        removeModeletFromRestartMapStatus(oldModeletClientInfo);
                        newSystemException(RSE0000507, new Object[] { "Modelet timeout after restart." });
                    }
                }

                addUnUsedModeletsToDefaultPool();

                // try to get new modelet from the pool
                // modeletClientInfo = getModeletClientInfo(transactionCriteria, timeOut);

            }
        } catch (InterruptedException ie) {
            LOGGER.error("ERROR:: An error occurred while retrieving modelet client.", ie);
            newSystemException("MSE0000003", new Object[] { ie.getLocalizedMessage() });
        }

        return modeletClientInfo;
    }

    private Map<String, String> createInfo(TransactionCriteria transactionCriteria, String loadedModel,
            String loadedModelVersion) {
        Map<String, String> info = new HashMap<>();
        info.put(NotificationHeaderEnum.MODEL_TO_LOAD.getHeaderName(), transactionCriteria.getModelName());
        info.put(NotificationHeaderEnum.MODEL_VERSION_TO_LOAD.getHeaderName(), transactionCriteria.getModelVersion());
        info.put(NotificationHeaderEnum.REASON.getHeaderName(), ModeletManager.RESTART_REASON_3);
        info.put(NotificationHeaderEnum.TRANSACTION_ID.getHeaderName(), transactionCriteria.getUmgTransactionId());
        info.put(NotificationHeaderEnum.LOADED_MODEL.getHeaderName(), loadedModel);
        info.put(NotificationHeaderEnum.LOADED_MODEL_VERSION.getHeaderName(), loadedModelVersion);
        info.put(NotificationHeaderEnum.TRANSACTION_RUN_DATE.getHeaderName(), transactionCriteria.getRunAsData());
        return info;
    }

    private long actualME2TimeOut(final long startTime, final long endTime, final long actualTimeOut) {
        final long diff = endTime - startTime;
        if (actualTimeOut > diff) {
            return actualTimeOut - diff;
        } else {
            return NUMBER_ZERO;
        }
    }

    private boolean isItSamePool(final PoolUsageOrderMapping usagePoolOrder) {
        return usagePoolOrder.getPoolId().equals(usagePoolOrder.getPoolUsageId())
                && usagePoolOrder.getPoolName().equals(usagePoolOrder.getPoolUsageName());
    }

    @Override
    public boolean canPoolBeUsed(final String poolName) {
        boolean value = true;

        final IMap<Object, Object> poolMap = cacheRegistry.getMap(POOL_MAP);
        final Object oPool = poolMap.get(poolName);
        if (oPool != null) {
            final Pool pool = (Pool) oPool;
            value = arePoolModeletsBusy(pool);
            if (value) {
                LOGGER.info("Pool is busy (in progress), so pool {} can not be used", poolName);
            } else {
                LOGGER.info("Pool is not busy (in progress), so pool {} can be used", poolName);
            }

            value = !value;

        }

        return value;
    }

    @Override
    public void updatePoolStatus(final String poolName, final String poolStatus) {
        poolObjectsLoader.updatePoolStatus(poolName, poolStatus);
    }

    @Override
    public boolean isPoolModeletsBusy(final String poolName) {
        final IMap<Object, Object> poolMap = cacheRegistry.getMap(POOL_MAP);
        final Object oPool = poolMap.get(poolName);
        boolean flag = false;
        if (oPool != null) {
            final Pool pool = (Pool) oPool;
            flag = arePoolModeletsBusy(pool);
        }

        return flag;
    }

    private boolean arePoolModeletsBusy(final Pool pool) {
        if (pool != null && IN_PROGRESS.toString().equals(pool.getPoolStatus())) {
            return true;
        }

        return false;
    }

    @Override
    public PoolStatusStats getPoolStatusStats(final String poolName) throws SystemException {
        return null;
    }

    @Override
    public List<PoolStatusStats> getPoolStatusStats(final List<String> poolNames) throws SystemException {
        return null;
    }

    @Override
    public void createNewPool(final Pool pool) {
        poolObjectsLoader.createPoolQueue(pool.getPoolName());
        poolObjectsLoader.createPoolMap(pool);
    }

    @Override
    public void deletePool(final Pool pool) {

        // TODO: Implementation is pending
    }

    @Override
    public Pool getPoolByCriteria(final TransactionCriteria transactionCriteria) throws SystemException {
        return poolObjectsLoader.getPoolByCriteria(transactionCriteria);
    }

    public RModelDAO getRModelDAO() {
        return rModelDAO;
    }

    public void setRModelDAO(RModelDAO rModelDAO) {
        this.rModelDAO = rModelDAO;
    }

    @Override
    public Map<String, String> getModelPackageName(final String modelName, final Integer majorVersion, final Integer minorVersion,
            final String tenantCode) {
        createRequestContext(tenantCode);
        return rModelDAO.getModelPackageName(modelName, majorVersion, minorVersion, tenantCode);
    }

    @Override
    public List<SupportPackage> getSupportPackageList(final String modelName, final Integer majorVersion,
            final Integer minorVersion, final String tenantCode) {
        createRequestContext(tenantCode);
        return rModelDAO.getSupportPackageList(modelName, majorVersion, minorVersion, tenantCode);
    }

    private void createRequestContext(final String tenantCode) {
        final Properties properties = new Properties();
        properties.put(RequestContext.TENANT_CODE, tenantCode);
        new RequestContext(properties);
    }

    @Override
    public CacheRegistry getCacheRegistry() {
        return cacheRegistry;
    }

    @Override
    public PoolObjectsLoader getPoolObjectsLoader() {
        return poolObjectsLoader;
    }

    @Override
    public void setCacheRegistr(final CacheRegistry cr) {
        this.cacheRegistry = cr;
    }

    @Override
    public String getEnvDefaultPoolName(final String execLanguage, final String execEnvironment) throws SystemException {
        final List<Pool> poolList = getPoolList();
        String envDefaultPoolName = null;
        for (final Pool pool : poolList) {
            if (DefaultPool.isDefaultPool(pool)) {
                if (ExecutionLanguage.getEnvironment(execLanguage)
                        .equals(ExecutionLanguage.getEnvironment(pool.getExecutionLanguage()))
                        && ExecutionEnvironment.getEnvironment(execEnvironment)
                                .equals(ExecutionEnvironment.getEnvironment(pool.getExecutionEnvironment()))) {
                    envDefaultPoolName = pool.getPoolName();
                }
            }
        }

        return envDefaultPoolName;
    }

    @Override
    public KeyValuePair<String, Integer> getModeletPoolAndCount(TransactionCriteria transactionCriteria) throws SystemException {
        KeyValuePair<String, Integer> probablePool = new KeyValuePair<String, Integer>();
        Pool pool = getPoolByCriteria(transactionCriteria);
        if (pool != null) {
            final String poolName = pool.getPoolName();
            final TreeSet<PoolUsageOrderMapping> poolMapByUsageOrderist = poolObjectsLoader.getPoolUsageOrderList(poolName);
            for (final PoolUsageOrderMapping usagePoolOrder : poolMapByUsageOrderist) {
                final String queueName = usagePoolOrder.getPoolUsageName();
                if (isItSamePool(usagePoolOrder) || canPoolBeUsed(queueName)) {
                    final IQueue<Object> poolQueue = getPoolQueue(queueName);
                    if (poolQueue.size() > 0) {
                        probablePool.setKey(poolQueue.getName());
                        probablePool.setValue(poolQueue.size());
                        break;
                    }
                }
            }
        }
        return probablePool;
    }

    @Override
    public void addModeletToRestartMapStatus(final ModeletClientInfo modeletClientInfo) {
        LOGGER.error("Modelet Restart status is updated to true, modelet is {}", modeletClientInfo.getLogMessage());
        final IMap<String, Boolean> modeletRestartMap = cacheRegistry.getMap(MODELET_RESTART_MAP_STATUS);
        modeletRestartMap.put(modeletClientInfo.getHostKey(), TRUE);
    }

    @Override
    public void removeModeletFromRestartMapStatus(final ModeletClientInfo modeletClientInfo) {
        LOGGER.error("Modelet Restart status is removed from map, modelet is {}", modeletClientInfo.getLogMessage());
        final IMap<String, Boolean> modeletRestartMap = cacheRegistry.getMap(MODELET_RESTART_MAP_STATUS);
        modeletRestartMap.remove(modeletClientInfo.getHostKey());
    }

    @Override
    public boolean isModeletInRestartMapStatus(final ModeletClientInfo modeletClientInfo) {
        LOGGER.error("Checking whether this Modelet is restarted or not, Modelet is {}", modeletClientInfo.getLogMessage());
        final IMap<String, Boolean> modeletRestartMap = cacheRegistry.getMap(MODELET_RESTART_MAP_STATUS);
        return modeletRestartMap.containsKey(modeletClientInfo.getHostKey())
                ? modeletRestartMap.get(modeletClientInfo.getHostKey()) : FALSE;
    }

    @Override
    public void addModeletToRestartMap(final ModeletClientInfo modeletClientInfo) {
        LOGGER.error("Modelet Restarted, adding this to map so that transaction will execcute using this modelet, Modelet is {}",
                modeletClientInfo.getLogMessage());
        final IMap<String, ModeletClientInfo> modeletRestartMap = cacheRegistry.getMap(MODELET_RESTART_MAP);
        modeletRestartMap.put(modeletClientInfo.getHostKey(), modeletClientInfo);
    }

    @Override
    public ModeletClientInfo getModeletFromRestartMap(final ModeletClientInfo modeletClientInfo) {
        LOGGER.error("Modelet is remvoed from map as this will executes transaction, Modelet is {}",
                modeletClientInfo.getLogMessage());
        final IMap<String, ModeletClientInfo> modeletRestartMap = cacheRegistry.getMap(MODELET_RESTART_MAP);
        return modeletRestartMap.get(modeletClientInfo.getHostKey());
    }

    @Override
    public void removeModeletFromRestartMap(final ModeletClientInfo modeletClientInfo) {
        LOGGER.error("Modelet is remvoed from map as this will executes transaction, Modelet is {}",
                modeletClientInfo.getLogMessage());
        final IMap<String, ModeletClientInfo> modeletRestartMap = cacheRegistry.getMap(MODELET_RESTART_MAP);
        modeletRestartMap.remove(modeletClientInfo.getHostKey());
    }

    private long getTransactionWaitTimeForModeletToComeUp(final String poolName) {
        final IMap<Object, Object> poolMap = cacheRegistry.getMap(POOL_MAP);
        final Object oPool = poolMap.get(poolName);
        final Pool pool = (Pool) oPool;
        final int poolWaitTimeout = pool.getWaitTimeout();
        return poolWaitTimeout + poolWaitTimeout * getRetryCount();
    }

    @Override
    public long getRetryCount() {
        String retryCountStr = systemParameterProvider.getParameter(RETRY_COUNT);
        if (retryCountStr != null) {
            retryCount = Integer.parseInt(retryCountStr);
        }
        return retryCount;
    }

    private void addUnUsedModeletsToDefaultPool() throws SystemException {
        final IMap<String, ModeletClientInfo> modeletRestartMap = cacheRegistry.getMap(MODELET_RESTART_MAP);
        final Set<String> keySet = modeletRestartMap.keySet();

        final List<ModeletClientInfo> addedModeletList = new ArrayList<>();
        for (String key : keySet) {
            final ModeletClientInfo modeletClientInfo = modeletRestartMap.get(key);
            if (modeletClientInfo!= null && !isModeletInRestartMapStatus(modeletClientInfo)) {
                if (modeletClientInfo.getExecutionLanguage().equals(ExecutionLanguage.R.getValue())
                        && modeletClientInfo.getExecEnvironment().equalsIgnoreCase(SystemConstants.LINUX_OS)) {
                    modeletClientInfo.setPoolName(R_LINUX_DEFAULT);
                } else if (modeletClientInfo.getExecutionLanguage().equals(ExecutionLanguage.R.getValue())
                        && modeletClientInfo.getExecEnvironment().equalsIgnoreCase(SystemConstants.WINDOWS_OS)) {
                    modeletClientInfo.setPoolName(R_WINDOWS_DEFAULT);
                } else if (modeletClientInfo.getExecutionLanguage().equals(ExecutionLanguage.MATLAB.getValue())) {
                    modeletClientInfo.setPoolName(MATLAB_LINUX_DEFAULT);
                } else if (modeletClientInfo.getExecutionLanguage().equals(ExecutionLanguage.EXCEL.getValue())) {
                    modeletClientInfo.setPoolName(EXCEL_WINDOWS_DEFAULT);
                }
                addedModeletList.add(modeletClientInfo);
            } else {
            	LOGGER.error("Modelet Key received is {}", key);
            	LOGGER.error("ModeletClientInfo received from map is {}", modeletClientInfo);
            }
        }

        for (final ModeletClientInfo modeletClientInfo : addedModeletList) {
            addModeletToPoolQueue(modeletClientInfo);
        }

    }

    @Override
    public ModeletManager getModeletManager() {
        return modeletManager;
    }

    @Override
    public Pool getPoolDetail(String poolName) {
        Pool pool = null;
        final IMap<Object, Object> poolMap = cacheRegistry.getMap(POOL_MAP);
        final Set<Object> poolNameSet = poolMap.keySet();

        for (Object obj : poolNameSet) {
            final Object poolFromMap = poolMap.get(obj);
            if (poolFromMap != null) {
                Pool poolFromCache = (Pool) poolFromMap;
                if (StringUtils.equalsIgnoreCase(poolName, poolFromCache.getPoolName())) {
                    pool = poolFromCache;
                    break;
                }
            }
        }
        return pool;
    }

}