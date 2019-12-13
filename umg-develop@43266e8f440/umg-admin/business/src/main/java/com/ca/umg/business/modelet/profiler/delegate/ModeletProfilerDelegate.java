package com.ca.umg.business.modelet.profiler.delegate;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.modelet.ModeletClientInfo;
import com.ca.umg.business.model.info.ModelExecutionEnvironmentInfo;
import com.ca.umg.business.modelet.profiler.info.ModeletProfilerInfo;
import com.ca.umg.business.modelet.profiler.key.info.ModeletProfilerKeyInfo;
import com.ca.umg.business.modelet.profiler.param.info.ModeletProfilerParamInfo;
import com.ca.umg.business.modelet.profiler.request.info.ModeletProfilerRequest;
import com.ca.umg.business.modelet.system.info.SystemModeletInfo;

import java.util.List;

public interface ModeletProfilerDelegate {
	/**
	 * Fetch modelet profiler for specific execution environment
	 *
	 * @param environment
	 * @param version
	 *
	 * @return
	 */
	List<ModeletProfilerInfo> fetchModeletProfilerByExecEnvironment(String environment, String version);

	/**
	 * Prepare and return modelet profiler information for modelet
	 * @param host
	 * @param port
	 *
	 * @return
	 *
	 * @throws BusinessException
	 */
	List<ModeletProfilerParamInfo> fetchModeletProfilerMap(String host, int port) throws BusinessException;

	/**
	 * Create modelet profiler
	 * @param modeletProfiler
	 *
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void createModeletProfile(ModeletProfilerRequest modeletProfiler) throws BusinessException, SystemException;

	/**
	 * fetch all modelet profiler
	 *
	 * @return
	 *
	 * @throws BusinessException
	 * @throws SystemException
	 */
	List<ModeletProfilerRequest> fetchAllModeletProfiler() throws BusinessException, SystemException;

	/**
	 * fetch modelet profiler for given id
	 *
	 * @param id
	 *
	 * @return
	 *
	 * @throws BusinessException
	 * @throws SystemException
	 */
	ModeletProfilerRequest fetchModeletProfiler(String id) throws BusinessException, SystemException;

	/**
	 * Fetch default modelet profiler data
	 * @return
	 *
	 * @throws BusinessException
	 * @throws SystemException
	 */
	ModeletProfilerRequest fetchDefaultModeletProfilerData() throws BusinessException, SystemException;

	/**
	 * fetch all modelet profiler keys
	 *
	 * @return
	 *
	 * @throws BusinessException
	 */
	List<ModeletProfilerKeyInfo> fetchModeletProfilerKeys() throws BusinessException;

	/**
	 * fetch all system modelet with profiler
	 * @return
	 *
	 * @throws BusinessException
	 * @throws SystemException
	 */
	List<SystemModeletInfo> fetchSystemModeletList() throws BusinessException, SystemException;

	/**
	 * update modelet profiler for system modelet
	 * @param hostName
	 * @param port
	 * @param profilerId
	 *
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void updateSytemModeletProfiler(String hostName, String port, String profilerId) throws BusinessException, SystemException;

	/**
	 * Remove modelet profile link with system modelet
	 * @param modeletId
	 *
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void removeModeletProfileMap(String modeletId) throws BusinessException, SystemException;

	/**
	 * update modelet profiler
	 * @param modeletProfiler
	 *
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void updateModeletProfile(ModeletProfilerRequest modeletProfiler) throws BusinessException, SystemException;

	/**
	 * Remove modelet profiler
	 * @param id
	 *
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void removeModeletProfile(String id) throws BusinessException, SystemException;

	/**
	 * Fetch active model execution environment list
	 * @return
	 *
	 * @throws BusinessException
	 * @throws SystemException
	 */
	List<ModelExecutionEnvironmentInfo> getActiveModelExecutionEnvList() throws BusinessException, SystemException;

	public void restartModelets(List<ModeletClientInfo> modeletClientInfoList) throws SystemException, BusinessException;

	public String downloadModeletLogs(ModeletClientInfo modeletClientInfo) throws SystemException, BusinessException;
}
