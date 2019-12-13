package com.ca.umg.business.modelet.profiler.bo;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.modelet.profiler.entity.ModeletProfiler;
import com.ca.umg.business.modelet.profiler.info.ModeletProfilerInfo;
import com.ca.umg.business.modelet.profiler.key.info.ModeletProfilerKeyInfo;
import com.ca.umg.business.modelet.profiler.param.info.ModeletProfilerParamInfo;
import com.ca.umg.business.modelet.profiler.request.info.ModeletProfilerRequest;

import java.util.List;

public interface ModeletProfilerBo {
	/**
	 * Fetch modelet profiler for specific execution environment
	 *
	 * @param environment
	 * @param version
	 * @param active
	 *
	 * @return
	 */
	List<ModeletProfiler> fetchModeletProfilerByExecEnvironment(String environment, String version, String active);

	/**
	 * Create modelet profiler
	 *
	 * @param profilerInfo
	 *
	 * @return
	 *
	 * @throws SystemException
	 */
	ModeletProfilerInfo saveModeletProfiler(ModeletProfilerInfo profilerInfo) throws SystemException;

	/**
	 * find size of profiler by name
	 * @param name
	 *
	 * @return
	 *
	 * @throws SystemException
	 */
	Long modeletProfilerSizeByName(String name) throws SystemException;

	/**
	 * create modelet profiler params
	 *
	 * @param profilerParamInfos
	 *
	 * @return
	 *
	 * @throws SystemException
	 */
	List<ModeletProfilerParamInfo> saveModeletProfilerParams(List<ModeletProfilerParamInfo> profilerParamInfos) throws SystemException;

	/**
	 * fetch list of modelet profiler key info
	 *
	 * @return
	 *
	 * @throws SystemException
	 */
	List<ModeletProfilerKeyInfo> fetchAllModeletProfilerKey() throws SystemException;

	/**
	 * fetch all modelet profiler infos
	 *
	 * @return
	 */
	List<ModeletProfilerInfo> fetchAllModeletProfiler() throws SystemException;

	/**
	 * fetch modelet profiler for given id
	 *
	 * @param id
	 *
	 * @return
	 */
	ModeletProfilerRequest fetchModeletProfiler(String id) throws SystemException;

	/**
	 * returns modelet profiler info for profiler id
	 * @param id
	 *
	 * @return
	 *
	 * @throws SystemException
	 */
	ModeletProfilerInfo fetchModeletProfilerById(String id) throws SystemException;

	/**
	 * fetch list of modelet profiler params for given profiler id.
	 * @param id
	 *
	 * @return
	 *
	 * @throws SystemException
	 */
	List<ModeletProfilerParamInfo> fetchAllModeletProfilerParamsByProfilerId(String id) throws SystemException;

	/**
	 * Remove modelet profiler by id
	 * @param id
	 *
	 * @throws SystemException
	 */
	void removeModeletProfile(String id) throws SystemException;
}
