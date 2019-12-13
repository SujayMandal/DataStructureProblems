package com.ca.umg.business.modelet.profiler.bo;

import com.ca.framework.core.delegate.AbstractDelegate;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.modelet.profiler.dao.ModeletProfilerDao;
import com.ca.umg.business.modelet.profiler.entity.ModeletProfiler;
import com.ca.umg.business.modelet.profiler.info.ModeletProfilerInfo;
import com.ca.umg.business.modelet.profiler.key.dao.ModeletProfilerKeyDao;
import com.ca.umg.business.modelet.profiler.key.entity.ModeletProfilerKey;
import com.ca.umg.business.modelet.profiler.key.info.ModeletProfilerKeyInfo;
import com.ca.umg.business.modelet.profiler.param.dao.ModeletProfilerParamDao;
import com.ca.umg.business.modelet.profiler.param.entity.ModeletProfilerParam;
import com.ca.umg.business.modelet.profiler.param.info.ModeletProfilerParamInfo;
import com.ca.umg.business.modelet.profiler.request.info.ModeletProfilerRequest;
import com.ca.umg.business.util.AdminUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named
public class ModeletProfilerBoImpl extends AbstractDelegate implements ModeletProfilerBo {

	private static final Logger LOGGER = LoggerFactory.getLogger(ModeletProfilerBoImpl.class);

	@Inject
	private ModeletProfilerDao modeletProfilerDao;

	@Inject
	private ModeletProfilerKeyDao modeletProfilerKeyDao;

	@Inject
	private ModeletProfilerParamDao modeletProfilerParamDao;

	@Override
	public List<ModeletProfiler> fetchModeletProfilerByExecEnvironment(String environment, String version, String active) {
		List<ModeletProfiler> profilerList = null;
		try {
			profilerList = modeletProfilerDao.findModeletProfilerByExecEnvironment(environment, version, active);
		} catch (DataAccessException dae) {
			LOGGER.error("fetchModeletProfilerByExecEnvironment failed to fetch data.");
		}
		return profilerList;
	}

	@Override
	public ModeletProfilerInfo saveModeletProfiler(ModeletProfilerInfo profilerInfo) throws SystemException {
		ModeletProfiler profiler = convert(profilerInfo, ModeletProfiler.class);
		boolean actualAdminAware = AdminUtil.getActualAdminAware();
		AdminUtil.setAdminAwareTrue();
		try {
			profiler = modeletProfilerDao.save(profiler);
		} catch (DataAccessException dae) {
			LOGGER.error("modelet profiling saving failure. {}", dae);
			SystemException.newSystemException(BusinessExceptionCodes.BSE001002, null);
		} finally {
			AdminUtil.setActualAdminAware(actualAdminAware);
		}
		return convert(profiler, ModeletProfilerInfo.class);
	}

	@Override
	public Long modeletProfilerSizeByName(String name) throws SystemException {
		Long countProfiler = 0L;
		boolean actualAdminAware = AdminUtil.getActualAdminAware();
		AdminUtil.setAdminAwareTrue();
		try {
			countProfiler = modeletProfilerDao.countModeletProfilerByName(name);
		} catch (DataAccessException dae) {
			LOGGER.error("modelet profiling saving failure. {}", dae);
			SystemException.newSystemException(BusinessExceptionCodes.BSE001024, null);
		} finally {
			AdminUtil.setActualAdminAware(actualAdminAware);
		}
		return countProfiler;
	}

	@Override
	public List<ModeletProfilerParamInfo> saveModeletProfilerParams(List<ModeletProfilerParamInfo> profilerParamInfos) throws SystemException {
		List<ModeletProfilerParam> profilerParamList = convertToList(profilerParamInfos, ModeletProfilerParam.class);
		boolean actualAdminAware = AdminUtil.getActualAdminAware();
		AdminUtil.setAdminAwareTrue();
		try {
			profilerParamList = modeletProfilerParamDao.save(profilerParamList);
		} catch (DataAccessException dae) {
			LOGGER.error("modelet profiling param saving failure. {}", dae);
			SystemException.newSystemException(BusinessExceptionCodes.BSE001009, null);
		} finally {
			AdminUtil.setActualAdminAware(actualAdminAware);
		}
		return convertToList(profilerParamList, ModeletProfilerParamInfo.class);
	}

	@Override
	public List<ModeletProfilerKeyInfo> fetchAllModeletProfilerKey() throws SystemException {
		boolean actualAdminAware = AdminUtil.getActualAdminAware();
		AdminUtil.setAdminAwareTrue();
		List<ModeletProfilerKeyInfo> profilerKeyInfos = null;
		List<ModeletProfilerKey> profilerKeys = null;
		try {
			profilerKeys = modeletProfilerKeyDao.findAll();
		} catch (DataAccessException dae) {
			LOGGER.error("modelet profiling key retrieval failure. {}", dae);
		} finally {
			AdminUtil.setActualAdminAware(actualAdminAware);
		}
		if(profilerKeys != null) {
			profilerKeyInfos = convertToList(profilerKeys, ModeletProfilerKeyInfo.class);
		}
		return profilerKeyInfos;
	}

	@Override
	public List<ModeletProfilerInfo> fetchAllModeletProfiler() throws SystemException {
		boolean actualAdminAware = AdminUtil.getActualAdminAware();
		AdminUtil.setAdminAwareTrue();
		List<ModeletProfilerInfo> modeletProfilerInfos = null;
		List<ModeletProfiler> profilers = null;
		try {
			profilers = modeletProfilerDao.findAll();
		} catch (DataAccessException dae) {
			LOGGER.error("modelet profiling fetch all failure. {}", dae);
			SystemException.newSystemException(BusinessExceptionCodes.BSE001001, null);
		} finally {
			AdminUtil.setActualAdminAware(actualAdminAware);
		}
		if(profilers != null) {
			modeletProfilerInfos = convertToList(profilers, ModeletProfilerInfo.class);
		}
		return modeletProfilerInfos;
	}

	@Override
	public ModeletProfilerRequest fetchModeletProfiler(String id) throws SystemException {
		boolean actualAdminAware = AdminUtil.getActualAdminAware();
		AdminUtil.setAdminAwareTrue();
		ModeletProfiler profiler = null;
		try {
			profiler = modeletProfilerDao.findModeletProfilerById(id);
		} catch (DataAccessException dae) {
			LOGGER.error("modelet profiling fetch failure. {}", dae);
			SystemException.newSystemException(BusinessExceptionCodes.BSE001003, null);
		} finally {
			AdminUtil.setActualAdminAware(actualAdminAware);
		}
		ModeletProfilerRequest profilerRequest = null;
		if(profiler != null) {
			profilerRequest = new ModeletProfilerRequest();
			profilerRequest.setId(profiler.getId());
			profilerRequest.setName(profiler.getName());
			profilerRequest.setDescription(profiler.getDescription());
			if(profiler.getModelExecutionEnvironment() != null) {
				profilerRequest.setExecutionEnvironmentId(profiler.getModelExecutionEnvironment().getId());
				profilerRequest.setExecutionEnvironment(profiler.getModelExecutionEnvironment().getExecutionEnvironment());
				profilerRequest.setEnvironmentVersion(profiler.getModelExecutionEnvironment().getEnvironmentVersion());
			}
			List<ModeletProfilerParam> profilerParams = null;
			AdminUtil.setAdminAwareTrue();
			try {
				profilerParams = modeletProfilerParamDao.findModeletProfilerParamsByProfilerId(profiler.getId());
			} catch (DataAccessException dae) {
				LOGGER.error("modelet profiling param fetch failure for profiler id. {}", dae);
				SystemException.newSystemException(BusinessExceptionCodes.BSE001004, null);
			} finally {
				AdminUtil.setActualAdminAware(actualAdminAware);
			}
			Map<String, String> profilerParamMap = new HashMap<>();
			for (ModeletProfilerParam param : profilerParams) {
				profilerParamMap.put(param.getModeletProfilerKey().getCode(), param.getParamValue());
			}
			profilerRequest.setParams(profilerParamMap);
		}

		return profilerRequest;
	}

	@Override
	public ModeletProfilerInfo fetchModeletProfilerById(String id) throws SystemException {
		boolean actualAdminAware = AdminUtil.getActualAdminAware();
		AdminUtil.setAdminAwareTrue();
		ModeletProfilerInfo profilerInfo = null;
		ModeletProfiler profiler = null;
		try {
			profiler = modeletProfilerDao.findModeletProfilerById(id);
		} catch (DataAccessException dae) {
			LOGGER.error("modelet profiling fetch failure. {}", dae);
			SystemException.newSystemException(BusinessExceptionCodes.BSE001003, null);
		} finally {
			AdminUtil.setActualAdminAware(actualAdminAware);
		}
		if(profiler != null) {
			profilerInfo = convert(profiler, ModeletProfilerInfo.class);
		}
		return profilerInfo;
	}

	@Override
	public List<ModeletProfilerParamInfo> fetchAllModeletProfilerParamsByProfilerId(String id) throws SystemException {
		List<ModeletProfilerParamInfo> profilerParamInfos = null;
		List<ModeletProfilerParam> profilerParamList = null;
		boolean actualAdminAware = AdminUtil.getActualAdminAware();
		AdminUtil.setAdminAwareTrue();
		try {
			profilerParamList = modeletProfilerParamDao.findModeletProfilerParamsByProfilerId(id);
		} catch (DataAccessException dae) {
			LOGGER.error("modelet profiling param fetch failure. {}", dae);
			SystemException.newSystemException(BusinessExceptionCodes.BSE001018, null);
		} finally {
			AdminUtil.setActualAdminAware(actualAdminAware);
		}
		if(profilerParamList != null) {
			profilerParamInfos = convertToList(profilerParamList, ModeletProfilerParamInfo.class);
		}
		return profilerParamInfos;
	}

	@Override
	@Transactional(rollbackFor = SystemException.class)
	public void removeModeletProfile(String id) throws SystemException {
		boolean actualAdminAware = AdminUtil.getActualAdminAware();
		AdminUtil.setAdminAwareTrue();
		try {
			modeletProfilerParamDao.deleteModeletProfilerParamByProfilerId(id);
			modeletProfilerDao.delete(id);
		} catch (DataAccessException dae) {
			LOGGER.error("modelet profiling param fetch failure. {}", dae);
			SystemException.newSystemException(BusinessExceptionCodes.BSE001019, null);
		} finally {
			AdminUtil.setActualAdminAware(actualAdminAware);
		}
	}

}
