package com.ca.umg.business.modelet.system.bo;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.modelet.profiler.param.info.ModeletProfilerParamInfo;
import com.ca.umg.business.modelet.system.info.SystemModeletInfo;

import java.util.List;

public interface SystemModeletBo {
	/**
	 * Save modelet operation
	 * @param modeletInfo
	 *
	 * @return
	 *
	 * @throws SystemException
	 */
	SystemModeletInfo saveModelet(SystemModeletInfo modeletInfo) throws SystemException;

	/**
	 * Update modelet data
	 * @param modeletInfo
	 *
	 * @throws SystemException
	 */
	void updateModelet(SystemModeletInfo modeletInfo) throws SystemException;

	/**
	 * fetch all modelet data
	 * @return
	 *
	 * @throws SystemException
	 */
	List<SystemModeletInfo> fetchAllSystemModelets() throws SystemException;

	/**
	 * fetch system modelet for given host name and port number
	 * @param hostName
	 * @param portNumber
	 *
	 * @return
	 *
	 * @throws SystemException
	 */
	SystemModeletInfo fetchSystemModelet(String hostName, String portNumber) throws SystemException;

	/**
	 * Fetch system modelet by id
	 * @param modeletId
	 *
	 * @return
	 *
	 * @throws SystemException
	 */
	SystemModeletInfo fetchSystemModeletById(String modeletId) throws SystemException;

	/**
	 * count number of modelets exist for given port number and host name
	 * @param hostName
	 * @param portNumber
	 *
	 * @return
	 *
	 * @throws SystemException
	 */
	Long countModelets(String hostName, String portNumber) throws SystemException;

	/**
	 * Fetch modele profiler params data for given profiler is
	 * @param profilerId
	 *
	 * @return
	 *
	 * @throws SystemException
	 */
	List<ModeletProfilerParamInfo> fetchModeProfileParams(String profilerId) throws SystemException;

	/**
	 * fetch system mmodelet list with modelet profiler
	 * @return
	 *
	 * @throws SystemException
	 */
	List<SystemModeletInfo> fetchSystemModeletList() throws SystemException;

	/**
	 * update modelet profiler for system modelet
	 * @param hostName
	 * @param port
	 * @param profilerId
	 *
	 * @return
	 *
	 * @throws SystemException
	 */
	SystemModeletInfo updateSytemModeletProfiler(String hostName, String port, String profilerId) throws SystemException;

	/**
	 * remove mapping of system modelet and profiler
	 * @param modeletId
	 *
	 * @throws SystemException
	 */
	void unlinkSytemModeletProfiler(String modeletId) throws SystemException;

	/**
	 * fetch list of system modelets for given profiler
	 * @param id
	 *
	 * @return
	 *
	 * @throws SystemException
	 */
	List<SystemModeletInfo> fetchSystemModeletsByProfiler(String id) throws SystemException;
}
