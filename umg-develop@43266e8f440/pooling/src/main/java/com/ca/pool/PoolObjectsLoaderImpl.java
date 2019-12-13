package com.ca.pool;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.constants.PoolConstants;
import com.ca.framework.core.exception.SystemException;
import com.ca.modelet.ModeletClientInfo;
import com.ca.pool.dao.PoolDAO;
import com.ca.pool.model.ExecutionEnvironment;
import com.ca.pool.model.ExecutionLanguage;
import com.ca.pool.model.Pool;
import com.ca.pool.model.PoolCriteria;
import com.ca.pool.model.PoolCriteriaDefMapping;
import com.ca.pool.model.PoolCriteriaDetails;
import com.ca.pool.model.PoolUsageOrderMapping;
import com.ca.pool.model.TransactionCriteria;
import com.ca.pool.modelet.profiler.info.ModeletProfileParamsInfo;
import com.ca.pool.util.PoolCriteriaUtil;
import com.hazelcast.core.IList;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static com.ca.framework.core.constants.PoolConstants.ALL_MODELET_MAP;
import static com.ca.framework.core.constants.PoolConstants.CRITERA_POOL_MAP;
import static com.ca.framework.core.constants.PoolConstants.MODELET_PROFILER;
import static com.ca.framework.core.constants.PoolConstants.POOL_MAP;
import static com.ca.framework.core.constants.PoolConstants.POOL_USAGE_ORDER_MAP;
import static com.ca.framework.core.constants.PoolConstants.RA_SYSTEM_MODELETS;
import static com.ca.framework.core.exception.SystemException.newSystemException;
import static com.ca.pool.model.Pool.getSystemDefaultPool;
import static com.ca.pool.util.BeanToMapUtil.getModeletClientInfoMap;
import static com.ca.pool.util.PoolCriteriaUtil.getReplacedPoolCriteriaForAny;
import static org.slf4j.LoggerFactory.getLogger;

//import javax.inject.Named;

@SuppressWarnings("PMD")
public class PoolObjectsLoaderImpl implements PoolObjectsLoader {

	private static final Logger LOGGER = getLogger(PoolObjectsLoaderImpl.class);

	private CacheRegistry cacheRegistry;

	private PoolDAO poolDao;

	private ExpressionParser parser = new SpelExpressionParser();

	// TODO : Right now it is static data, if it is changble later, it should be cached
	private List<PoolCriteria> poolCriteriaList;

	@Override
	@PostConstruct
	public void loadPoolObjects() throws SystemException {
		LOGGER.info("************* Loading and Creating ALL POOL objects is started*************");
		final List<Pool> poolList = poolDao.loadAllPool();
		poolList.add(getSystemDefaultPool());
		deletePools(poolList);
		createPoolQueue(poolList);
		createPoolMap(poolList);

		LOGGER.info("*************Loading and Creating POOL CRITERIA object*************");
		poolCriteriaList = poolDao.loadAllPoolCriteria();

		LOGGER.info("*************Loading and Creating POOL CRITERIA MAPPING objects*************");
		final List<PoolCriteriaDefMapping> poolCriteriaDefMappingList = poolDao.loadAllPoolCriteriaDefMapping();
		deletePoolCriteria(poolCriteriaDefMappingList);
		createPoolCriteria(poolCriteriaDefMappingList);

		LOGGER.info("*************Loading and Creating POOL USAGE ORDER objects*************");
		final List<PoolUsageOrderMapping> poolUsageOrderMappingList = poolDao.loadAllPoolUsageOrderMapping();
		deletePoolUsageOrder(poolUsageOrderMappingList);
		createPoolUsageOrder(poolUsageOrderMappingList);

		LOGGER.info("************* Loading and Creating ALL POOL objects is done*************");
	}

	@Override
	public void createPoolQueue(final List<Pool> poolList) {
		for (Pool pool : poolList) {
			createPoolQueue(pool.getPoolName());
		}
	}

	@Override
	public void createPoolQueue(final String poolName) {
		final IQueue<Object> poolQueue = cacheRegistry.getDistributedPoolQueue(poolName);
		if(poolQueue != null) {
			LOGGER.info("{} pool is created", poolName);
			LOGGER.info("{} pool size is now : {}", poolName, poolQueue.size());
		} else {
			LOGGER.error("Queue for {} pool is NOT created", poolName);
		}
	}

	@Override
	public void createPoolMap(final List<Pool> poolList) {
		for (Pool pool : poolList) {
			createPoolMap(pool);
		}
	}

	@Override
	public void createPoolMap(final Pool pool) {
		final IMap<Object, Object> poolMap = cacheRegistry.getMap(POOL_MAP);
		poolMap.put(pool.getPoolName(), pool);
		LOGGER.info("Pool is mapped. Key is {}, and Value is {}", pool.getPoolName(), pool);
	}

	@Override
	public void createPoolCriteria(final List<PoolCriteriaDefMapping> poolCriteriaDefMappingList) throws SystemException {
		final IMap<String, String> criteriaPoolMap = cacheRegistry.getMap(CRITERA_POOL_MAP);
		final List<TransactionCriteria> matlabtempList = new ArrayList<>();
		final List<TransactionCriteria> rTempLinuxList = new ArrayList<>();
		final List<TransactionCriteria> rTempWindowsList = new ArrayList<>();
		final List<TransactionCriteria> excelTempWindowsList = new ArrayList<>();
		final Map<Object, String> defMappingToObjectMap = new HashMap<>();
		criteriaPoolMap.clear();

		List<String> fieldsList = new ArrayList<>();
		for (PoolCriteria criteria : getPoolCriteriaList()) {
			fieldsList.add(criteria.getCriteriaName());
		}
		segregatePoolsByExcLanguage(poolCriteriaDefMappingList, criteriaPoolMap, matlabtempList, rTempLinuxList, rTempWindowsList,
				excelTempWindowsList, defMappingToObjectMap, fieldsList);

		createSortedPoolLists(matlabtempList, rTempLinuxList, rTempWindowsList, excelTempWindowsList, defMappingToObjectMap);
	}

	private void segregatePoolsByExcLanguage(final List<PoolCriteriaDefMapping> poolCriteriaDefMappingList,
			final IMap<String, String> criteriaPoolMap, final List<TransactionCriteria> matlabtempList,
			final List<TransactionCriteria> rTempLinuxList, final List<TransactionCriteria> rTempWindowsList,
			final List<TransactionCriteria> excelTempWindowsList, final Map<Object, String> defMappingToObjectMap, List<String> fieldsList)
			throws SystemException {
		TransactionCriteria transactionCriteria;
		for (PoolCriteriaDefMapping poolCriteriaDefMapping : poolCriteriaDefMappingList) {
			final String criteriaDefMapString = poolCriteriaDefMapping.getPoolCriteriaValue();
			final String poolName = poolCriteriaDefMapping.getPoolName();
			transactionCriteria = PoolCriteriaUtil.getCriteriaObject(criteriaDefMapString);
			transactionCriteria.setClassFieldsList(fieldsList);

			switch (ExecutionLanguage.getEnvironment(transactionCriteria.getExecutionLanguage())) {
			case R:
				if(StringUtils.equalsIgnoreCase(ExecutionEnvironment.LINUX.getEnvironment(), transactionCriteria.getExecutionEnvironment())) {
					rTempLinuxList.add(transactionCriteria);
				} else {
					rTempWindowsList.add(transactionCriteria);
				}

				break;
			case EXCEL:
				excelTempWindowsList.add(transactionCriteria);
				break;

			case MATLAB:
				matlabtempList.add(transactionCriteria);
				break;

			default:
				break;
			}
			defMappingToObjectMap.put(transactionCriteria, criteriaDefMapString);
			criteriaPoolMap.put(criteriaDefMapString, poolName);
			LOGGER.info("CriteriaPoolMap is added, key is {}, value is {}", criteriaDefMapString, poolName);
		}
	}

	/**
	 * sorts the matlab and r pool criteria def mappping
	 *
	 * @param matlabtempList
	 * @param rtempListLinux
	 * @param defMappingToObjectMap
	 */
	private void createSortedPoolLists(List<TransactionCriteria> matlabtempList, List<TransactionCriteria> rtempListLinux,
			List<TransactionCriteria> rtempListWindows, List<TransactionCriteria> excelTempListWindows, Map<Object, String> defMappingToObjectMap) {

		final IList<Object> matlabSortedPoolList = cacheRegistry.getList(PoolConstants.MATLAB_SORTED_POOL_LIST);
		final IList<Object> rSortedPoolListLinux = cacheRegistry.getList(PoolConstants.R_SORTED_POOL_LIST_LINUX);
		final IList<Object> rSortedPoolListWindows = cacheRegistry.getList(PoolConstants.R_SORTED_POOL_LIST_WINDOWS);
		final IList<Object> excelSortedPoolListWindows = cacheRegistry.getList(PoolConstants.EXCEL_SORTED_POOL_LIST_WINDOWS);

		List<String> matlabModelVersionAny = new ArrayList<>();
		List<String> rModelVersionAnyWindows = new ArrayList<>();
		List<String> rModelVersionAnyLinux = new ArrayList<>();
		List<String> excelModelVersionAny = new ArrayList<>();

		matlabSortedPoolList.clear();
		rSortedPoolListLinux.clear();
		rSortedPoolListWindows.clear();
		excelSortedPoolListWindows.clear();

		// sorting both the
		Collections.sort(matlabtempList);
		Collections.sort(rtempListLinux);
		Collections.sort(rtempListWindows);
		Collections.sort(excelTempListWindows);

		// creating sorted list for matlab
		for (TransactionCriteria matlabCriteriaObject : matlabtempList) {
			String criteriaDefMapString = defMappingToObjectMap.get(matlabCriteriaObject);
			if(StringUtils.equalsIgnoreCase(matlabCriteriaObject.getModelVersion(), PoolConstants.ANY)) {
				matlabModelVersionAny.add(criteriaDefMapString);
			} else {
				matlabSortedPoolList.add(criteriaDefMapString);
			}
		}
		// adding the pool with model version any in last
		if(CollectionUtils.isNotEmpty(matlabModelVersionAny)) {
			matlabSortedPoolList.addAll(matlabModelVersionAny);
		}
		// creating sorted list for R - Linux
		for (TransactionCriteria rLinuxCriteriaObject : rtempListLinux) {
			String criteriaDefMapString = defMappingToObjectMap.get(rLinuxCriteriaObject);
			if(StringUtils.equalsIgnoreCase(rLinuxCriteriaObject.getModelVersion(), PoolConstants.ANY)) {
				rModelVersionAnyLinux.add(criteriaDefMapString);
			} else {
				rSortedPoolListLinux.add(criteriaDefMapString);
			}
		}

		// adding the pool with model version any in last
		if(CollectionUtils.isNotEmpty(rModelVersionAnyLinux)) {
			rSortedPoolListLinux.addAll(rModelVersionAnyLinux);
		}

		// creating sorted list for R - Windows
		for (TransactionCriteria rWindowsCriteriaObject : rtempListWindows) {
			String criteriaDefMapString = defMappingToObjectMap.get(rWindowsCriteriaObject);
			if(StringUtils.equalsIgnoreCase(rWindowsCriteriaObject.getModelVersion(), PoolConstants.ANY)) {
				rModelVersionAnyWindows.add(criteriaDefMapString);
			} else {
				rSortedPoolListWindows.add(criteriaDefMapString);
			}
		}

		// adding the pool with model version any in last
		if(CollectionUtils.isNotEmpty(rModelVersionAnyWindows)) {
			rSortedPoolListWindows.addAll(rModelVersionAnyWindows);
		}

		// creating sorted list for R - Excel
		for (TransactionCriteria excelWindowsCriteriaObject : excelTempListWindows) {
			String criteriaDefMapString = defMappingToObjectMap.get(excelWindowsCriteriaObject);
			if(StringUtils.equalsIgnoreCase(excelWindowsCriteriaObject.getModelVersion(), PoolConstants.ANY)) {
				excelModelVersionAny.add(criteriaDefMapString);
			} else {
				excelSortedPoolListWindows.add(criteriaDefMapString);
			}
		}

		// adding the pool with model version any in last
		if(CollectionUtils.isNotEmpty(excelModelVersionAny)) {
			excelSortedPoolListWindows.addAll(excelModelVersionAny);
		}

	}

	public void deletePoolCriteria(final List<PoolCriteriaDefMapping> poolCriteriaDefMappingList) {
		LOGGER.info("Delete pool criteria from cache if any is deleted from database");
		final IMap<String, String> criteriaPoolMap = cacheRegistry.getMap(CRITERA_POOL_MAP);
		final Set<String> existingCriterias = criteriaPoolMap.keySet();
		for (final String existingCriteria : existingCriterias) {
			boolean doesNotExist = true;
			for (final PoolCriteriaDefMapping criteriaDefMappingObject : poolCriteriaDefMappingList) {
				if(existingCriteria.equalsIgnoreCase(criteriaDefMappingObject.getPoolCriteriaValue())) {
					doesNotExist = false;
					break;
				}
			}

			if(doesNotExist) {
				criteriaPoolMap.remove(existingCriteria);
				LOGGER.info("Pool Criteria {} is removed from Map", existingCriteria);
			}
		}
	}

	@Override
	public void createPoolUsageOrder(final List<PoolUsageOrderMapping> poolUsageOrderMappingList) {
		final IMap<Object, Object> poolMapByUsageOrderMap = cacheRegistry.getMap(POOL_USAGE_ORDER_MAP);
		poolMapByUsageOrderMap.clear();

		for (PoolUsageOrderMapping poolUsageOrderMapping : poolUsageOrderMappingList) {
			final String key = poolUsageOrderMapping.getPoolName();

			TreeSet<PoolUsageOrderMapping> poolUsageOrderList;
			if(poolMapByUsageOrderMap.containsKey(key)) {
				poolUsageOrderList = (TreeSet<PoolUsageOrderMapping>) poolMapByUsageOrderMap.get(key);
			} else {
				poolUsageOrderList = new TreeSet<PoolUsageOrderMapping>();
			}

			poolUsageOrderList.add(poolUsageOrderMapping);
			poolMapByUsageOrderMap.put(key, poolUsageOrderList);
			LOGGER.info("PoolUsageOrder map is added, Key is : {}, value is : {}", key, poolUsageOrderList.toString());
		}
	}

	public void deletePoolUsageOrder(final List<PoolUsageOrderMapping> poolUsageOrderMappingList) {
		LOGGER.info("Delete Pool Usage Orders from cache if it any is deleted from database");
		final IMap<Object, Object> poolMapByUsageOrderMap = cacheRegistry.getMap(POOL_USAGE_ORDER_MAP);
		final Set<Object> existingPoolNameSet = poolMapByUsageOrderMap.keySet();
		for (final Object existingPoolName : existingPoolNameSet) {
			boolean doesNotExist = true;
			for (final PoolUsageOrderMapping poolUsageOrderObject : poolUsageOrderMappingList) {
				if(existingPoolName.toString().equalsIgnoreCase(poolUsageOrderObject.getPoolName())) {
					doesNotExist = false;
					break;
				}
			}

			if(doesNotExist) {
				LOGGER.info("Pool Usage Order {} is removed from Map", poolMapByUsageOrderMap.get(existingPoolName.toString()).toString());
				poolMapByUsageOrderMap.remove(existingPoolName.toString());
			} else {
				deletePoolUsageOrder(poolUsageOrderMappingList, existingPoolName.toString());
			}
		}
	}

	private void deletePoolUsageOrder(final List<PoolUsageOrderMapping> poolUsageOrderMappingList, final String existingPoolName) {
		final IMap<Object, Object> poolMapByUsageOrderMap = cacheRegistry.getMap(POOL_USAGE_ORDER_MAP);
		final TreeSet<PoolUsageOrderMapping> poolUsageOrderList = (TreeSet<PoolUsageOrderMapping>) poolMapByUsageOrderMap.get(existingPoolName);
		boolean doesNotExist = true;
		if(CollectionUtils.isNotEmpty(poolUsageOrderList)) {
			for (final PoolUsageOrderMapping existingPoolUsageOrderList : poolUsageOrderList) {
				for (final PoolUsageOrderMapping poolUsageOrderObject : poolUsageOrderMappingList) {
					if(existingPoolUsageOrderList.getPoolName().equalsIgnoreCase(poolUsageOrderObject.getPoolName()) && existingPoolUsageOrderList
							.getPoolUsageName().equalsIgnoreCase(poolUsageOrderObject.getPoolUsageName())) {
						doesNotExist = false;
						break;
					}
				}

				if(doesNotExist) {
					poolUsageOrderList.remove(existingPoolUsageOrderList);
					poolMapByUsageOrderMap.put(existingPoolName.toString(), poolUsageOrderList);
					LOGGER.info("Pool Usage Order {} is removed from Tree Set", existingPoolUsageOrderList.toString());
					break;
				}
			}

			if(doesNotExist) {
				deletePoolUsageOrder(poolUsageOrderMappingList, existingPoolName);
			}

		}
	}

	@Override
	public List<Pool> getPoolList() {
		final List<Pool> poolList = new ArrayList<Pool>();
		final IMap<Object, Object> poolMap = cacheRegistry.getMap(POOL_MAP);
		final Set<Object> keySet = poolMap.keySet();
		for (Object key : keySet) {
			final Object poolFromMap = poolMap.get(key);
			if(poolFromMap != null) {
				poolList.add((Pool) poolFromMap);
			}
		}
		LOGGER.error("Current Pool size is : {} and pool list is : {}", poolList.size(), poolList);
		return poolList;
	}

	@Override
	public IQueue<Object> getPoolQueue(final String poolName) throws SystemException {
		final IQueue<Object> poolQueue = cacheRegistry.getDistributedPoolQueue(poolName);
		if(poolQueue == null) {
			LOGGER.error("Pool Queue is not found for the pool name:", poolName);
			throw newSystemException("MSE0000202", null);
		}
		return poolQueue;
	}

	@Override
	public List<PoolCriteria> getPoolCriteriaList() {
		return poolCriteriaList;
	}

	@Override
	public void updatePoolStatus(final String poolName, final String poolStatus) {
		final IMap<Object, Object> poolMap = cacheRegistry.getMap(POOL_MAP);
		final Object oPool = poolMap.get(poolName);
		if(oPool != null) {
			final Pool pool = (Pool) oPool;
			pool.setPoolStatus(poolStatus);
			poolMap.put(poolName, pool);
			LOGGER.info("Updated batch status of pool {} to status {} ", poolName, poolStatus);
		} else {
			LOGGER.error("Didnot find pool for pool name {}", poolName);
		}
	}

	@Override
	public Pool getPoolByCriteria(final TransactionCriteria transactionCriteria) throws SystemException {
    	/*if(transactionCriteria.getClientID() != null && ! StringUtils.isEmpty(transactionCriteria.getClientID())){
            cacheRegistry.getTopic("MODELET_FOUND").publish(transactionCriteria.getClientID() + "@" + "MODELET_FOUND");
           }*/

		final Pool pool = getMatchedPoolList(transactionCriteria);
		if(pool == null) {
			LOGGER.error("Pool is not found for the criteria :" + transactionCriteria.toString());
			throw newSystemException("MSE0000203", new String[] {transactionCriteria.toString()});
		}

		LOGGER.error("Pool found for the criteria. Pool is : {} and Its critera is : {}", pool.getPoolName(), transactionCriteria.toString());
		return pool;
	}

	@Override
	public void createNewPool(final Pool pool) {
		createPoolQueue(pool.getPoolName());
		createPoolMap(pool);
	}

	public void deletePools(final List<Pool> poolList) {
		LOGGER.info("Delete pools from cache if any pool is deleted from Database");
		final IMap<String, Pool> poolMap = cacheRegistry.getMap(POOL_MAP);
		final Set<String> existingPools = poolMap.keySet();
		for (final String pool : existingPools) {
			boolean doesNotExist = true;
			for (final Pool poolObject : poolList) {
				if(pool.equalsIgnoreCase(poolObject.getPoolName())) {
					doesNotExist = false;
					break;
				}
			}

			if(doesNotExist) {
				poolMap.remove(pool);
				LOGGER.info("Pool {} is removed from Map", pool);
			}
		}
	}

	private Pool getMatchedPoolList(final TransactionCriteria transactionCriteria) throws SystemException {
		final IMap<Object, Object> criteriaPoolMap = cacheRegistry.getMap(CRITERA_POOL_MAP);
		final IMap<Object, Object> poolMap = cacheRegistry.getMap(POOL_MAP);
		final IList<Object> sortedPoolCriteriaList;
		String poolCriteraString = null;
		String replacedPoolCritera = null;
		String matchedCritera = null;
		String poolName = null;
		try {
			sortedPoolCriteriaList = getSortedPoolsByExcEnvironment(transactionCriteria);

			for (Object poolCritera : sortedPoolCriteriaList) {
				poolCriteraString = poolCritera.toString();
				replacedPoolCritera = PoolCriteriaUtil.getReplacedPoolCriteria(poolCriteraString, transactionCriteria);
				final String replacedStr = StringUtils.replace(replacedPoolCritera, "&and", "and");
				LOGGER.info("Pool Criteria after replacement {}: for criteria {} " + "PoolObjectsLoaderImpl::getMatchedPoolList ", replacedStr,
						transactionCriteria.toString());
				boolean trueValue = parser.parseExpression(replacedStr).getValue(Boolean.class);
				if(trueValue) {
					matchedCritera = poolCriteraString;
					break;
				} else {
					replacedPoolCritera = getReplacedPoolCriteriaForAny(replacedPoolCritera);
					trueValue = parser.parseExpression(replacedPoolCritera).getValue(Boolean.class);
					if(trueValue) {
						matchedCritera = poolCriteraString;
						break;
					}
				}
			}
		} catch (ParseException pex) {
			LOGGER.error("Error occured in spel parsing of replaced pool criteria PoolObjectsLoaderImpl::getMatchedPoolList ", pex);
			throw newSystemException("MSE0000402", new Object[] {poolCriteraString, transactionCriteria.toString()});
		}

		if(StringUtils.isNotEmpty(matchedCritera)) {
			poolName = (String) criteriaPoolMap.get(matchedCritera);
		}

		LOGGER.error("MATCHED POOL is:" + poolName);

		Pool matchedPool = null;
		if(StringUtils.isNotBlank(poolName)) {
			matchedPool = (Pool) poolMap.get(poolName);
		}
		return matchedPool;
	}

	private IList<Object> getSortedPoolsByExcEnvironment(final TransactionCriteria transactionCriteria) {
		IList<Object> sortedPoolCriteriaList = null;
		switch (ExecutionLanguage.getEnvironment(transactionCriteria.getExecutionLanguage())) {
		case MATLAB:
			sortedPoolCriteriaList = cacheRegistry.getList(PoolConstants.MATLAB_SORTED_POOL_LIST);
			break;
		case EXCEL:
			sortedPoolCriteriaList = cacheRegistry.getList(PoolConstants.EXCEL_SORTED_POOL_LIST_WINDOWS);
			break;
		case R:
			if(StringUtils.equalsIgnoreCase(ExecutionEnvironment.LINUX.getEnvironment(), transactionCriteria.getExecutionEnvironment())) {
				sortedPoolCriteriaList = cacheRegistry.getList(PoolConstants.R_SORTED_POOL_LIST_LINUX);
			} else {
				sortedPoolCriteriaList = cacheRegistry.getList(PoolConstants.R_SORTED_POOL_LIST_WINDOWS);
			}
			break;
		default:
			break;
		}
		return sortedPoolCriteriaList;
	}

	@Override
	public CacheRegistry getCacheRegistry() {
		return cacheRegistry;
	}

	public void setCacheRegistry(final CacheRegistry cacheRegistry) {
		this.cacheRegistry = cacheRegistry;
	}

	public PoolDAO getPoolDao() {
		return poolDao;
	}

	public void setPoolDao(final PoolDAO poolDao) {
		this.poolDao = poolDao;
	}

	@Override
	public String getPoolCriteria(final String poolName) {
		final IMap<String, String> criteriaPoolMap = cacheRegistry.getMap(CRITERA_POOL_MAP);
		final Set<String> poolCriteriaSet = criteriaPoolMap.keySet();
		for (final String poolCriteria : poolCriteriaSet) {
			if(criteriaPoolMap.get(poolCriteria).equalsIgnoreCase(poolName)) {
				LOGGER.info("Pool Criteria is found for the pool {}, Matched Criteria is : {}", poolName, poolCriteria);
				return poolCriteria;
			}
		}

		LOGGER.error("Pool Criteria is NOT found for the pool {}", poolName);
		return null;
	}

	@Override
	public TreeSet<PoolUsageOrderMapping> getPoolUsageOrderList(final String poolName) throws SystemException {
		final TreeSet<PoolUsageOrderMapping> poolUsageOrderList = (TreeSet<PoolUsageOrderMapping>) cacheRegistry.getMap(POOL_USAGE_ORDER_MAP)
				.get(poolName);
		if(poolUsageOrderList == null) {
			LOGGER.error("Pool Usage Order List is NOT found for the pool name. Pool Name is : {}" + poolName);
			throw newSystemException("MSE0000201", null);
		}
		return poolUsageOrderList;
	}

	@Override
	public PoolCriteriaDetails getPoolCriteriaDetails(final String poolName) {
		return new PoolCriteriaDetails(getPoolCriteria(poolName));
	}

	@Override
	public List<ModeletClientInfo> getModeletClientInfo(final String poolName) {
		final IMap<String, ModeletClientInfo> allModeletMap = cacheRegistry.getMap(ALL_MODELET_MAP);
		final IMap<String, List<ModeletProfileParamsInfo>> modeletProfilerData = cacheRegistry.getMap(MODELET_PROFILER);
		final IMap<String, String> currentModeletProfilerIMap = cacheRegistry.getMap(PoolConstants.CURRENT_MODELET_PROFILER);
		final Set<String> keySet = allModeletMap.keySet();
		final List<ModeletClientInfo> modeletClientInfoList = new ArrayList<>();
		for (final String key : keySet) {
			final ModeletClientInfo clientInfo = allModeletMap.get(key);
			if(clientInfo.getPoolName().equals(poolName)) {
				String keyOnline = StringUtils.join(clientInfo.getHost(), FrameworkConstant.HYPHEN, clientInfo.getPort());
				String currentProfiler = currentModeletProfilerIMap.get(keyOnline);
				if(StringUtils.isNoneBlank(currentProfiler)) {
					clientInfo.setProfiler(currentProfiler);
				} else {
					List<ModeletProfileParamsInfo> modeletProfilerParamInfos = modeletProfilerData.get(keyOnline);
					if(CollectionUtils.isNotEmpty(modeletProfilerParamInfos)) {
						clientInfo.setProfiler(modeletProfilerParamInfos.get(0).getProfileName());
					}
				}
				modeletClientInfoList.add(clientInfo);
			}
		}
		return modeletClientInfoList;
	}

	@Override
	public List<ModeletClientInfo> getAllModeletClientInfo() {
		final IMap<String, ModeletClientInfo> allModeletMap = cacheRegistry.getMap(ALL_MODELET_MAP);
		final Set<String> keySet = allModeletMap.keySet();
		final List<ModeletClientInfo> modeletClientInfoList = new ArrayList<>();
		for (final String key : keySet) {
			final ModeletClientInfo clientInfo = allModeletMap.get(key);
			modeletClientInfoList.add(clientInfo);
		}

		return modeletClientInfoList;
	}

	@Override
	public List<ModeletClientInfo> getInactiveModelets() {
		final IMap<String, ModeletClientInfo> allModeletMap = cacheRegistry.getMap(ALL_MODELET_MAP);
		final IMap<String, ModeletClientInfo> systemModeletsInfo = cacheRegistry.getMap(RA_SYSTEM_MODELETS);
		final List<ModeletClientInfo> inactiveModelets = new ArrayList<>();
		for (Map.Entry<String, ModeletClientInfo> entry : systemModeletsInfo.entrySet()) {
			LOGGER.error("RA_SYSTEM_MODELET key is " + entry.getKey());
			if(!allModeletMap.containsKey(entry.getKey())) {
				LOGGER.error(entry.getKey() + " is InActive");
				inactiveModelets.add(entry.getValue());
			} else {
				LOGGER.error(entry.getKey() + " is Active");
			}
		}
		return inactiveModelets;
	}

	public List<Map<String, Object>> getActiveAndInactiveModeletClients() {
		final IMap<String, ModeletClientInfo> allModeletMap = cacheRegistry.getMap(ALL_MODELET_MAP);
		final Set<String> keySet = allModeletMap.keySet();
		final List<Map<String, Object>> modeletClientInfoList = new ArrayList<>();
		for (final String key : keySet) {
			final ModeletClientInfo clientInfo = allModeletMap.get(key);
			modeletClientInfoList.add((Map) getModeletClientInfoMap(clientInfo));
		}
		final List<ModeletClientInfo> inactiveModelets = getInactiveModelets();
		if(!inactiveModelets.isEmpty()) {
			for (ModeletClientInfo modeletInfo : inactiveModelets) {
				modeletInfo.setPoolName("  ");
				modeletInfo.setModeletStatus(ModeletStatus.UNREGISTERED.getStatus());
				modeletClientInfoList.add((Map) getModeletClientInfoMap(modeletInfo));
			}
		}

		return modeletClientInfoList;
	}
}