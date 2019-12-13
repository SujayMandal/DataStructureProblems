package com.ca.umg.business.modelet.profiler.dao;

import com.ca.umg.business.modelet.profiler.entity.ModeletProfiler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ModeletProfilerDao extends JpaRepository<ModeletProfiler, String> {

	/**
	 * returns the list of modelet profiler for given environment name and version
	 * 
	 * @param environment
	 * @param version
	 * @param active
	 * @return
	 */
	@Query("select u from ModeletProfiler u where u.modelExecutionEnvironment.executionEnvironment = ?1 and u.modelExecutionEnvironment.environmentVersion = ?2 and u.modelExecutionEnvironment.active = ?3")
	List<ModeletProfiler> findModeletProfilerByExecEnvironment(String environment, String version, String active);

	ModeletProfiler findModeletProfilerByName(String name);

	ModeletProfiler findModeletProfilerById(String id);

	Long countModeletProfilerByName(String name);
}
