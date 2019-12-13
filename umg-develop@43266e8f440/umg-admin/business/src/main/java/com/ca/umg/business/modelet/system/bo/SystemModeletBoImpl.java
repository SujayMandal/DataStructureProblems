package com.ca.umg.business.modelet.system.bo;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.constants.PoolConstants;
import com.ca.framework.core.delegate.AbstractDelegate;
import com.ca.framework.core.exception.SystemException;
import com.ca.modelet.ModeletClientInfo;
import com.ca.pool.ModeletStatus;
import com.ca.pool.PoolObjectsLoader;
import com.ca.pool.model.Pool;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.modelet.profiler.dao.ModeletProfilerDao;
import com.ca.umg.business.modelet.profiler.dao.SystemModeletProfilerMapDao;
import com.ca.umg.business.modelet.profiler.entity.ModeletProfiler;
import com.ca.umg.business.modelet.profiler.entity.SystemModeletProfilerMap;
import com.ca.umg.business.modelet.profiler.info.ModeletProfilerInfo;
import com.ca.umg.business.modelet.profiler.param.dao.ModeletProfilerParamDao;
import com.ca.umg.business.modelet.profiler.param.entity.ModeletProfilerParam;
import com.ca.umg.business.modelet.profiler.param.info.ModeletProfilerParamInfo;
import com.ca.umg.business.modelet.system.dao.SystemModeletDao;
import com.ca.umg.business.modelet.system.entity.SystemModelet;
import com.ca.umg.business.modelet.system.info.SystemModeletInfo;
import com.ca.umg.business.util.AdminUtil;
import com.hazelcast.core.IMap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ca.framework.core.constants.PoolConstants.MODELET_PROFILER;

@Named
public class SystemModeletBoImpl extends AbstractDelegate implements SystemModeletBo {

	private static final Logger LOGGER = LoggerFactory.getLogger(SystemModeletBoImpl.class);
	private static final int MAX_MODELET = 1;

	@Inject
	private SystemModeletDao systemModeletDao;

	@Inject
	private ModeletProfilerParamDao modeletProfilerParamDao;

	@Inject
	private ModeletProfilerDao modeletProfilerDao;

	@Inject
	private CacheRegistry cacheRegistry;

	@Inject
	private SystemModeletProfilerMapDao systemModeletProfilerMapDao;

	@Inject
	private PoolObjectsLoader poolObjectsLoader;

	@Override
	public SystemModeletInfo saveModelet(SystemModeletInfo modeletInfo) throws SystemException {
		SystemModelet modelet = convert(modeletInfo, SystemModelet.class);
		boolean actualAdminAware = AdminUtil.getActualAdminAware();
		AdminUtil.setAdminAwareTrue();
		try {
			modelet = systemModeletDao.save(modelet);
		} catch (DataAccessException dae) {
			LOGGER.error("system modelet save failed. {}", dae);
		} finally {
			AdminUtil.setActualAdminAware(actualAdminAware);
		}
		return convert(modelet, SystemModeletInfo.class);
	}

	@Override
	public void updateModelet(SystemModeletInfo modeletInfo) throws SystemException {
		SystemModelet modelet = convert(modeletInfo, SystemModelet.class);
		systemModeletDao.save(modelet);
	}

	@Override
	public List<SystemModeletInfo> fetchAllSystemModelets() throws SystemException {
		List<SystemModelet> modeletList = systemModeletDao.findAll();
		return convertToList(modeletList, SystemModeletInfo.class);
	}

	@Override
	public SystemModeletInfo fetchSystemModelet(String hostName, String portNumber) throws SystemException {
		boolean actualAdminAware = AdminUtil.getActualAdminAware();
		AdminUtil.setAdminAwareTrue();
		List<SystemModelet> modelets = new ArrayList<>();
		try {
			modelets = systemModeletDao.findByHostNameAndPort(hostName, Integer.parseInt(portNumber));
		} catch (DataAccessException dae) {
			LOGGER.error("system modele find by host name and port failed, {}", dae);
		} finally {
			AdminUtil.setActualAdminAware(actualAdminAware);
		}
		if(modelets.size() > MAX_MODELET) {
			SystemException.newSystemException(BusinessExceptionCodes.BSE001010, new Object[] {hostName, portNumber});
		}
		if(modelets.isEmpty()) {
			SystemException.newSystemException(BusinessExceptionCodes.BSE001011, new Object[] {hostName, portNumber});
		}
		return convert(modelets.get(0), SystemModeletInfo.class);
	}

	@Override
	public SystemModeletInfo fetchSystemModeletById(String modeletId) throws SystemException {
		SystemModelet modelet = null;
		boolean actualAdminAware = AdminUtil.getActualAdminAware();
		AdminUtil.setAdminAwareTrue();
		try {
			modelet = systemModeletDao.findById(modeletId);
		} catch (DataAccessException dae) {
			LOGGER.error("system modele find by host name and port failed, {}", dae);
		} finally {
			AdminUtil.setActualAdminAware(actualAdminAware);
		}
		return convert(modelet, SystemModeletInfo.class);
	}

	@Override
	public Long countModelets(String hostName, String portNumber) throws SystemException {
		return systemModeletDao.countByHostNameAndPort(hostName, Integer.parseInt(portNumber));
	}

	@Override
	public List<ModeletProfilerParamInfo> fetchModeProfileParams(String profilerId) throws SystemException {
		boolean actualAdminAware = AdminUtil.getActualAdminAware();
		AdminUtil.setAdminAwareTrue();
		List<ModeletProfilerParam> profilerParams = null;
		try {
			profilerParams = modeletProfilerParamDao.findModeletProfilerParamsByProfilerId(profilerId);
		} catch (DataAccessException dae) {
			LOGGER.error("fetch modelet profile params failure. {}", dae);
			SystemException.newSystemException(BusinessExceptionCodes.BSE001008, null);
		} finally {
			AdminUtil.setActualAdminAware(actualAdminAware);
		}
		List<ModeletProfilerParamInfo> modeletProfilerParamInfos = null;
		if(profilerParams != null) {
			modeletProfilerParamInfos = convertToList(profilerParams, ModeletProfilerParamInfo.class);
		}
		return modeletProfilerParamInfos;
	}

	@Override
	public List<SystemModeletInfo> fetchSystemModeletList() throws SystemException {
		List<SystemModeletInfo> systemModeletInfos = null;
		boolean actualAdminAware = AdminUtil.getActualAdminAware();
		AdminUtil.setAdminAwareTrue();

		List<SystemModelet> systemModelets = null;
		try {
			systemModelets = systemModeletDao.findAll();
		} catch (DataAccessException dae) {
			LOGGER.error("system modelet fetch failed. {}", dae);
			SystemException.newSystemException(BusinessExceptionCodes.BSE001006, null);
		} finally {
			AdminUtil.setActualAdminAware(actualAdminAware);
		}
		if(systemModelets != null) {
			systemModeletInfos = convertToList(systemModelets, SystemModeletInfo.class);
			final IMap<String, String> currentModeletProfilerIMap = cacheRegistry.getMap(PoolConstants.CURRENT_MODELET_PROFILER);
			Map<String, String> modeletClientStatus = new HashMap<>();
			final List<Pool> poolList = poolObjectsLoader.getPoolList();
			if(CollectionUtils.isNotEmpty(poolList)) {
				for (Pool pool : poolList) {
					String poolName = pool.getPoolName();
					List<ModeletClientInfo> modeletClientInfoList = poolObjectsLoader.getModeletClientInfo(poolName);
					for (ModeletClientInfo modelet : modeletClientInfoList) {
						modeletClientStatus
								.put(StringUtils.join(modelet.getHost(), FrameworkConstant.HYPHEN, modelet.getPort()), modelet.getModeletStatus());
					}
				}
			}
			IMap<String, Boolean> onlineModelet = cacheRegistry.getMap(PoolConstants.ONLINE_MODELET);
			LOGGER.info("ONLINE_MODELET data : {}", onlineModelet);
			LOGGER.info("Modelet Client Status data : {}", modeletClientStatus);
			for (SystemModeletInfo systemModelet : systemModeletInfos) {
				String key = StringUtils.join(systemModelet.getHostName(), FrameworkConstant.HYPHEN, systemModelet.getPort());
				systemModelet.setCurrentProfiler(currentModeletProfilerIMap.get(key));
				systemModelet.setModeletStatus(modeletClientStatus.get(key));
				if(StringUtils.equalsAny(modeletClientStatus.get(key), ModeletStatus.REGISTERED.getStatus(),
						ModeletStatus.REGISTRATION_INPROGRESS.getStatus(), ModeletStatus.BUSY.getStatus(), ModeletStatus.STARTED.getStatus(),
						ModeletStatus.REGISTERED_WITH_SYSTEM_DEFAULT_POOL.getStatus())) {
					Boolean uiStart = onlineModelet.get(key);
					systemModelet.setUiStart(Boolean.TRUE.equals(uiStart));
				}
			}
		}
		return systemModeletInfos;
	}

	@Override
	public SystemModeletInfo updateSytemModeletProfiler(String hostName, String port, String profilerId) throws SystemException {
		SystemModeletInfo modeletInfo = this.fetchSystemModelet(hostName, port);
		ModeletProfiler modeletProfiler = null;
		boolean actualAdminAware = AdminUtil.getActualAdminAware();
		AdminUtil.setAdminAwareTrue();
		try {
			SystemModeletProfilerMap profilerMap = systemModeletProfilerMapDao.findBySystemModelet(modeletInfo.getId());
			modeletProfiler = modeletProfilerDao.findModeletProfilerById(profilerId);
			if(profilerMap != null) {
				profilerMap.setModeletProfiler(modeletProfiler);
				//systemModeletProfilerMapDao.updateProfiler(modeletInfo.getId(), profilerId);
				systemModeletProfilerMapDao.save(profilerMap);
			} else {
				profilerMap = new SystemModeletProfilerMap();
				profilerMap.setModeletProfiler(modeletProfiler);
				profilerMap.setSystemModelet(convert(modeletInfo, SystemModelet.class));
				systemModeletProfilerMapDao.save(profilerMap);
			}
			modeletInfo.setModeletProfiler(convert(modeletProfiler, ModeletProfilerInfo.class));
		} catch (DataAccessException dae) {
			LOGGER.error("modelet profiler linking failed. {}", dae);
			SystemException.newSystemException(BusinessExceptionCodes.BSE001007, null);
		} finally {
			AdminUtil.setActualAdminAware(actualAdminAware);
		}
		return modeletInfo;
	}

	@Override
	@Transactional(rollbackFor = SystemException.class)
	public void unlinkSytemModeletProfiler(String modeletId) throws SystemException {
		SystemModelet modelet = null;
		boolean actualAdminAware = AdminUtil.getActualAdminAware();
		AdminUtil.setAdminAwareTrue();
		try {
			systemModeletProfilerMapDao.deleteModeletProfilerParamByModeletId(modeletId);
		} catch (DataAccessException dae) {
			LOGGER.error("modelet profiler fetch by id failed. {}", dae);
			SystemException.newSystemException(BusinessExceptionCodes.BSE001025, new Object[] {});
		} finally {
			AdminUtil.setActualAdminAware(actualAdminAware);
		}
		if(modelet != null) {
			cacheRegistry.getMap(MODELET_PROFILER).remove(StringUtils.join(modelet.getHostName(), FrameworkConstant.HYPHEN, modelet.getPort()));
		}
	}

	@Override
	public List<SystemModeletInfo> fetchSystemModeletsByProfiler(String id) throws SystemException {
		List<SystemModeletInfo> systemModeletInfos = null;
		boolean actualAdminAware = AdminUtil.getActualAdminAware();
		AdminUtil.setAdminAwareTrue();

		List<SystemModelet> systemModelets = null;
		try {
			systemModelets = systemModeletDao.fetchSystemModeletsByProfiler(id);
		} catch (DataAccessException dae) {
			LOGGER.error("system modelet fetch failed for profiler. {}", dae);
			SystemException.newSystemException(BusinessExceptionCodes.BSE001021, null);
		} finally {
			AdminUtil.setActualAdminAware(actualAdminAware);
		}
		if(systemModelets != null) {
			systemModeletInfos = convertToList(systemModelets, SystemModeletInfo.class);
		}
		return systemModeletInfos;
	}

}
