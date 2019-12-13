package com.ca.umg.business.hazelcaststats.delegate;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.constants.PoolConstants;
import com.ca.framework.core.info.tenant.TenantInfo;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.umg.business.pooling.helper.ModeletPoolingHelper;
import org.apache.commons.collections.MapUtils;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

//import com.ca.umg.business.hazelcaststats.info.IndexInfo;

@Named
public class HazelCastStatusDelegateImpl implements HazelCastStatusDelegate {

	@Inject
	private CacheRegistry cacheRegistry;

	private static final String DISTRIBUTED_POOL_QUEUE = "DISTRIBUTED_POOL_QUEUE";
	private static final String BATCH_LOCK = "BATCH_LOCK";
	private static final String BATCH_LOCK_CACHE_REG = "-batchLock";

	@Override
	@PreAuthorize("hasRole(T(com.ca.umg.business.constants.BusinessConstants).ROLE_SUPER_ADMIN)")
	public Map<Object, Object> getAllHazelCastEntries() {
		Map<Object, Object> systemParameterInfos = new HashMap<>();
		systemParameterInfos.put(SystemParameterProvider.SYSTEM_PARAMETER, cacheRegistry.getMap(SystemParameterProvider.SYSTEM_PARAMETER));
		systemParameterInfos.put(PoolConstants.ALL_MODELET_MAP, cacheRegistry.getMap(PoolConstants.ALL_MODELET_MAP));
		systemParameterInfos.put(PoolConstants.RA_SYSTEM_MODELETS, cacheRegistry.getMap(PoolConstants.RA_SYSTEM_MODELETS));
		systemParameterInfos.put(PoolConstants.HOST_TO_MEMBER, cacheRegistry.getMap(PoolConstants.HOST_TO_MEMBER));
		systemParameterInfos.put(PoolConstants.CRITERA_POOL_MAP, cacheRegistry.getMap(PoolConstants.CRITERA_POOL_MAP));
		systemParameterInfos.put(PoolConstants.POOL_MAP, cacheRegistry.getMap(PoolConstants.POOL_MAP));
		systemParameterInfos.put(PoolConstants.POOL_USAGE_ORDER_MAP, cacheRegistry.getMap(PoolConstants.POOL_USAGE_ORDER_MAP));
		systemParameterInfos.put(PoolConstants.MATLAB_SORTED_POOL_LIST, cacheRegistry.getList(PoolConstants.MATLAB_SORTED_POOL_LIST));
		systemParameterInfos.put(PoolConstants.R_SORTED_POOL_LIST_LINUX, cacheRegistry.getList(PoolConstants.R_SORTED_POOL_LIST_LINUX));
		systemParameterInfos.put(PoolConstants.R_SORTED_POOL_LIST_WINDOWS, cacheRegistry.getList(PoolConstants.R_SORTED_POOL_LIST_WINDOWS));
		systemParameterInfos.put(PoolConstants.EXCEL_SORTED_POOL_LIST_WINDOWS, cacheRegistry.getList(PoolConstants.EXCEL_SORTED_POOL_LIST_WINDOWS));

		systemParameterInfos.put(PoolConstants.ONLINE_MODELET, cacheRegistry.getMap(PoolConstants.ONLINE_MODELET));
		systemParameterInfos.put(PoolConstants.CURRENT_MODELET_PROFILER, cacheRegistry.getMap(PoolConstants.CURRENT_MODELET_PROFILER));
		systemParameterInfos.put(PoolConstants.MODELET_PROFILER_LIST, cacheRegistry.getMap(PoolConstants.MODELET_PROFILER_LIST));
		systemParameterInfos.put(PoolConstants.MODELET_PROFILER, cacheRegistry.getMap(PoolConstants.MODELET_PROFILER));
		systemParameterInfos.put(PoolConstants.STARTING_MODELET_LIST, cacheRegistry.getMap(PoolConstants.STARTING_MODELET_LIST));

		systemParameterInfos.put(FrameworkConstant.TENANT_URL_MAP, cacheRegistry.getMap(FrameworkConstant.TENANT_URL_MAP));
		systemParameterInfos.put(FrameworkConstant.MAJOR_VERSION_ENV_MAP, cacheRegistry.getMap(FrameworkConstant.MAJOR_VERSION_ENV_MAP));
		systemParameterInfos.put(PoolConstants.MODEL_EXECUTION_ENVIRONMENTS, cacheRegistry.getMap(PoolConstants.MODEL_EXECUTION_ENVIRONMENTS));
		systemParameterInfos.put(FrameworkConstant.TENANT_MAP, removeAuthTokens());
		systemParameterInfos.put(FrameworkConstant.RESTART_MODELET_COUNT_MAP, cacheRegistry.getMap(FrameworkConstant.RESTART_MODELET_COUNT_MAP));
		systemParameterInfos.put(FrameworkConstant.BATCH_INPUT_FILES_MAP, cacheRegistry.getMap(FrameworkConstant.BATCH_INPUT_FILES_MAP));
		systemParameterInfos.put(FrameworkConstant.BULK_INPUT_FILES_MAP, cacheRegistry.getMap(FrameworkConstant.BULK_INPUT_FILES_MAP));

		Map<Object, Object> pooldQueueMap = ModeletPoolingHelper.createPooldQueueMap(cacheRegistry);

		systemParameterInfos.put(DISTRIBUTED_POOL_QUEUE, pooldQueueMap);
		systemParameterInfos.put(BATCH_LOCK, cacheRegistry.getMap(RequestContext.getRequestContext().getTenantCode() + BATCH_LOCK_CACHE_REG));

		return systemParameterInfos;
	}

	private Map<String, TenantInfo> removeAuthTokens() {
		Map<String, TenantInfo> dummyMap = cacheRegistry.getMap(FrameworkConstant.TENANT_MAP);
		if(MapUtils.isNotEmpty(dummyMap)) {
			Collection<TenantInfo> tenantInfos = dummyMap.values();
			for (TenantInfo tenantInfo : tenantInfos) {
				tenantInfo.setAuthTokens(null);
				tenantInfo.setActiveAuthToken(null);
			}
		}
		return dummyMap;
	}

}
